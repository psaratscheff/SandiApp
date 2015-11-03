package cl.saratscheff.sandiapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.firebase.client.Firebase;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Firebase mFire;
    private GeoFire mGeo;
    private Context context;
    private boolean placingMarker = true;
    private Marker lastMarker = null;
    private FloatingActionButton fab;
    private static LatLng currentLocation = new LatLng(-33.478905, -70.657607);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        context = this;
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(final GoogleMap googleMap) {

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LatLng loc = MapsActivity.getCurrentLocation();
                double Lat = loc.latitude;
                double Lon = loc.longitude;
                Intent Form = new Intent(MapsActivity.this, Formulario.class);
                Form.putExtra("Lat", Lat);
                Form.putExtra("Lon", Lon);
                startActivity(Form);
            }
        });

        mMap = googleMap;
        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
            }
        });
        mFire = new Firebase("https://sizzling-heat-8397.firebaseio.com");
        //mGeo = new GeoFire(mFire.child("markers"));

        loadPins();

        /* Creamos el boton para detectar nuestra ubicacion si es que se tiene permisos */
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.getUiSettings().setCompassEnabled(true);

            //add location button click listener

            mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                @Override
                public boolean onMyLocationButtonClick() {

                    try {
                        LatLng location = new LatLng(mMap.getMyLocation().getLatitude(), mMap.getMyLocation().getLongitude());
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 13));
                    } catch (Exception e) { }

                    return true;
                }
            });
        }

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (placingMarker && lastMarker != null) {
                    lastMarker.remove();
                    lastMarker = null;
                }
            }
        });

        /* Cuando se hace un long click se agrega un marcador nuevo */
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                if (placingMarker) {
                    addTouchedPinToMap(latLng, "Click para crear post!", "");
                }
            }
        });

        /* Decimos que hacer cuando se apriete el titulo de un marcador */
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {

                FragmentManager fm = getSupportFragmentManager();
                PopUpMapMenu editNameDialog = new PopUpMapMenu();
                editNameDialog.setTitle(marker.getTitle());
                editNameDialog.setDescription(marker.getSnippet());
                editNameDialog.show(fm, "fragment_pop_up_map_menu");
            }
        });

        /* Definimos la posicion y vista inicial */
        LatLng santiago = new LatLng(-33.478905, -70.657607);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(santiago, 11));
    }

    /* Esto es para agregar un marcador haciendo touch */
    public void addTouchedPinToMap(LatLng position, String title, String description) {

        if(placingMarker){

            if(lastMarker != null)
                lastMarker.remove();

            // Creating a marker
            MarkerOptions markerOptions = new MarkerOptions();

            // Settings
            markerOptions.position(position);
            markerOptions.title(title);
            markerOptions.snippet(description);

            // Animating to the touched position
            mMap.animateCamera(CameraUpdateFactory.newLatLng(position));

            // Placing a marker on the touched position
            lastMarker = mMap.addMarker(markerOptions);
        }
    }

    private void loadPins(){
        Firebase mFireMarkers = mFire.child("markers");

        mFireMarkers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    LatLng loc = new LatLng(Double.parseDouble(child.child("latitude").getValue().toString()), Double.parseDouble(child.child("longitude").getValue().toString()));

                    MarkerOptions markerOptions = new MarkerOptions();

                    markerOptions.position(loc);
                    markerOptions.title(child.child("title").getValue().toString());
                    markerOptions.snippet(child.child("description").getValue().toString());

                    mMap.addMarker(markerOptions);
                }
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });
    }

    private String getRandomString(int length) {
        Random rnd = new Random();
        String[] abc123 = new String[]{"a","b","c","d","e","f","g","h","i","j","k","l",
                "m","n","o","p","q","r","s","t","u","v","w","x","y","z","A","B","C","D",
                "E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V",
                "W","X","Y","Z","0","1","2","3","4","5","6","7","8","9"};
        String output = "";
        for(int i=0; i<length; i++){
            output = output + abc123[rnd.nextInt(abc123.length)];
        }
        return output;
    }

    /* Agrega un pin al mapa en la ubicacion del usuario. Ademas este pin se guarda en
     * la BDD de Firebase. */
    public void addPinToCurrentLoc(String titulo, String descripcion){
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 13));

        MarkerOptions markerOptions = new MarkerOptions();

        markerOptions.position(currentLocation);
        markerOptions.title(titulo);
        markerOptions.snippet(descripcion);

        saveMarkerToFirebase(titulo, descripcion, currentLocation);
        mMap.addMarker(markerOptions);

    }

    /* Retorna la ubicacion actual. La ubicacion actual se calcula cuando
    esta actividad esta corriendo, es decir que si se pide la ubicacion actual
    antes de haber llamado a esta actividad al menos una vez, se retornara una
    ubicacion default.
     */
    public static LatLng getCurrentLocation(){
        return currentLocation;
    }


    private void saveMarkerToFirebase(String title, String description, LatLng location){
        String id = getRandomString(32);
        mFire.child("markers").child(id).child("title").setValue(title);
        mFire.child("markers").child(id).child("description").setValue(description);
        mFire.child("markers").child(id).child("latitude").setValue(location.latitude);
        mFire.child("markers").child(id).child("longitude").setValue(location.longitude);
    }
}
