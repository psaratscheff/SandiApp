package cl.saratscheff.sandiapp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.firebase.client.Firebase;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
    public PopUpMapMenu editNameDialog;

    private HashMap<Marker,String> currentMarkers = new HashMap<Marker,String>();


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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    try {


    if (requestCode == 1) {
        if (resultCode == Activity.RESULT_OK) {
            String titulo = data.getStringExtra("titulo");
            String descripcion = data.getStringExtra("descripcion");
            String imgpath = data.getStringExtra("img");


            File mediaStorageDir = new File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "SandiApp/" + imgpath);
            String path = mediaStorageDir.getAbsolutePath();
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            String img64 = code(bitmap);

            addPinToCurrentLoc(titulo, descripcion, img64);

            String done = "";
        }
        if (resultCode == Activity.RESULT_CANCELED) {
            //Write your code if there's no result
        }
    }

    }catch (Exception e)
    {}
    }//onActivityResult

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
                Intent Form = new Intent(MapsActivity.this, Formulario.class);
                startActivityForResult(Form, 1);
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
        /* mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                if (placingMarker) {
                    addTouchedPinToMap(latLng, "Click para crear post!", "");
                }
            }
        }); */

        /* Decimos que hacer cuando se apriete el titulo de un marcador */
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {

                final String markerID = currentMarkers.get(marker);

                FragmentManager fm = getSupportFragmentManager();
                editNameDialog = new PopUpMapMenu();
                editNameDialog.setTitle(marker.getTitle());
                editNameDialog.setDescription(marker.getSnippet());

                mFire.child("markers").child(markerID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snap) {
                        editNameDialog.setDate(snap.child("date").getValue().toString());
                        editNameDialog.setMarkerID(markerID);
                        String creatorID = snap.child("creator").getValue().toString();

                        mFire.child("users").child(creatorID).child("name").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                editNameDialog.setCreator(dataSnapshot.getValue().toString());
                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {

                            }
                        });

                        mFire.child("images").child(markerID).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                editNameDialog.setImage(dataSnapshot.getValue().toString(), markerID);
                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {

                            }
                        });
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                        System.out.println("The read failed: " + firebaseError.getMessage());
                    }
                });

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
                if(snapshot.hasChildren()){
                    for (DataSnapshot child : snapshot.getChildren()) {

                        boolean[] shouldCreateMark = new boolean[]{false, false, false};

                        LatLng loc = null;
                        if(child.child("latitude").exists() && child.child("longitude").exists()){
                            loc = new LatLng(Double.parseDouble(child.child("latitude").getValue().toString()),
                                    Double.parseDouble(child.child("longitude").getValue().toString()));
                            shouldCreateMark[0] = true;
                        }

                        String title = "";
                        String description = "";

                        if(child.child("title").exists()){
                            title = child.child("title").getValue().toString();
                            shouldCreateMark[1] = true;
                        }

                        if(child.child("description").exists()){
                            description = child.child("description").getValue().toString();
                            shouldCreateMark[2] = true;
                        }

                        boolean create = true;

                        for(int i = 0; i<shouldCreateMark.length; i++){
                            if(shouldCreateMark[i] == false)
                                create = false;
                        }

                        if(create){
                            MarkerOptions markerOptions = new MarkerOptions();

                            markerOptions.position(loc);
                            markerOptions.title(child.child("title").getValue().toString());
                            markerOptions.snippet(child.child("description").getValue().toString());

                            Marker mark = mMap.addMarker(markerOptions);
                            currentMarkers.put(mark, child.getKey());
                        }
                    }
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
    public void addPinToCurrentLoc(String titulo, String descripcion, String image){

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 13));

        String id = getRandomString(32);
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();

        saveMarkerToFirebase(id, dateFormat.format(date).toString(), titulo, descripcion, image, currentLocation);

    }

    /* Retorna la ubicacion actual. La ubicacion actual se calcula cuando
    esta actividad esta corriendo, es decir que si se pide la ubicacion actual
    antes de haber llamado a esta actividad al menos una vez, se retornara una
    ubicacion default.
     */
    public static LatLng getCurrentLocation(){
        return currentLocation;
    }


    private void saveMarkerToFirebase(String id, String date, String title, String description, String image, LatLng location){

        mFire.child("markers").child(id).child("creator").setValue(LoginActivity.userID);
        mFire.child("markers").child(id).child("date").setValue(date);
        mFire.child("markers").child(id).child("title").setValue(title);
        mFire.child("markers").child(id).child("description").setValue(description);
        mFire.child("images").child(id).setValue(image);
        //mFire.child("markers").child(id).child("image").setValue(image);
        mFire.child("markers").child(id).child("latitude").setValue(location.latitude);
        mFire.child("markers").child(id).child("longitude").setValue(location.longitude);
    }

    private String code(Bitmap imgOriginalSize) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Bitmap img = ScaleImage(imgOriginalSize, 300, 300); // Ajustar tamaño, maximo 300x300px
        img.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String temp = null;
        try {
            System.gc();
            temp = Base64.encodeToString(b, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            baos = new ByteArrayOutputStream();
            img.compress(Bitmap.CompressFormat.JPEG, 50, baos);
            b = baos.toByteArray();
            temp = Base64.encodeToString(b, Base64.DEFAULT);
            Log.e("EWN", "Out of memory error catched");
        }
        return temp;
    }

    public static Bitmap ScaleImage(Bitmap image, int maxHeight, int maxWidth)
    {
        int height = image.getHeight();
        int width = image.getWidth();
        if (width<maxWidth && height>maxHeight){
            return image;
        };

        double ratioH = (double)maxHeight / image.getHeight();
        double ratioW = (double)maxWidth / image.getWidth();

        double ratio = Math.min(ratioH, ratioW);
        int newWidth = (int)(image.getHeight() * ratio);
        int newHeight = (int)(image.getWidth() * ratio);

        Bitmap newImage = Bitmap.createScaledBitmap(image, newHeight, newWidth, true);
        return newImage;
    }
}
