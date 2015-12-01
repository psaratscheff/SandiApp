package cl.saratscheff.sandiapp;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.media.ExifInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.view.ActionProvider;
import android.view.ContextMenu;
import android.view.SubMenu;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Environment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.firebase.geofire.GeoFire;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.firebase.client.Firebase;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.security.acl.Group;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,
        NavigationView.OnNavigationItemSelectedListener, PostFragment.OnFragmentInteractionListener {

    private GoogleMap mMap;
    private Firebase mFire;
    private GeoFire mGeo;
    private Context context;
    private boolean placingMarker = true;
    private Marker lastMarker = null;
    private Marker lastMarkerClicked = null;
    private FloatingActionButton fab;
    private TextView navUsername;
    private TextView navEmail;
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private Menu menuCategory;
    private SupportMapFragment mapFragment;
    private static LatLng currentLocation = new LatLng(-33.478905, -70.657607);
    public PopUpMapMenu editNameDialog;
    private int currentNavSel = 0;
    private int oldNavSel = 0;

    private HashMap<Marker,String> currentMarkers = new HashMap<Marker,String>();

    private Fragment myPostsFragment = null;
    private Fragment myChartFragment = null;
    private MenuItem[] categoriesMenuItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //pedimos permisos necesarios Android6
        PermisionCheck();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);
        currentNavSel = navigationView.getMenu().getItem(0).getItemId();


        context = this;
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (lastMarkerClicked != null && lastMarkerClicked.isInfoWindowShown()){
            lastMarkerClicked.hideInfoWindow();
        } else if (currentNavSel == R.id.nav_myposts) {
            if(myPostsFragment != null){
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.remove(myPostsFragment);
                transaction.commit();
                myPostsFragment = null;
                currentNavSel = R.id.nav_map;
                navigationView.getMenu().getItem(0).setChecked(true);
                setTitle(R.string.title_activity_maps);
            }
        } else if (currentNavSel == R.id.nav_charts) {
            if(myChartFragment != null){
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.remove(myChartFragment);
                transaction.commit();
                myChartFragment = null;
                currentNavSel = R.id.nav_map;
                navigationView.getMenu().getItem(0).setChecked(true);
                setTitle(R.string.title_activity_maps);
                fab.show();
            }
        } else {
            // Ir al Inicio del SO, en vez de volver al login screen.
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            // super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.nav_drawer, menu);
        menuCategory = menu;
        navUsername = (TextView) findViewById(R.id.navUsername);
        navUsername.setText(LoginActivity.userName);
        navEmail = (TextView) findViewById(R.id.navEmail);
        navEmail.setText(LoginActivity.userEmail);

        String[] categories = getResources().getStringArray(R.array.posts_categories);

        for(String cat: categories){
            menu.add(cat).setCheckable(true);
        }

        categoriesMenuItems = new MenuItem[menu.size()];
        for(int i=0; i<menu.size(); i++){
            categoriesMenuItems[i] = menu.getItem(i);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        boolean reloadMap = true;

        if(item.getTitle().equals("Todos") && !item.isChecked()){
            for(MenuItem mi: categoriesMenuItems){
                if(mi != item){
                    mi.setChecked(false);
                }
            }
        }

        else if(!item.getTitle().equals("Todos") && !item.isChecked()){
            for(MenuItem mi: categoriesMenuItems){
                if(mi.getTitle().equals("Todos")){
                    mi.setChecked(false);
                    break;
                }
            }
        }

        if (item.isChecked()) {
            MenuItem aux = null;
            boolean isAnyChecked = false;
            for(MenuItem mi: categoriesMenuItems){
                if(mi.isChecked() && mi != item && !mi.getTitle().equals("Todos")){
                    isAnyChecked = true;
                    break;
                }
                if(mi.getTitle().equals("Todos")){
                    aux = mi;
                }
            }
            if(!isAnyChecked && !item.getTitle().equals("Todos")) {
                aux.setChecked(true);
                item.setChecked(false);
            }
            else if(!isAnyChecked && item.getTitle().equals("Todos")){
                reloadMap = false;
            }
            else{
                item.setChecked(false);
            }

        }
        else {
            item.setChecked(true);
        }

        if(reloadMap) {
            mMap.clear();
            loadPins();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if(id != currentNavSel){
            oldNavSel = currentNavSel;
            currentNavSel = id;
        }

        if (id == R.id.nav_map) {

            fab.show();

            if(myPostsFragment != null){
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.remove(myPostsFragment);
                transaction.commit();
                myPostsFragment = null;
                setTitle(R.string.title_activity_maps);
            } else if (myChartFragment != null){
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.remove(myChartFragment);
                transaction.commit();
                myChartFragment = null;
                setTitle(R.string.title_activity_maps);
            }

        } else if (id == R.id.nav_myposts) {

            fab.show();

            myPostsFragment = new PostFragment().setContext(context);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack if needed
            transaction.replace(R.id.map, myPostsFragment);
            transaction.addToBackStack(null);

            // Commit the transaction
            transaction.commit();
            setTitle(R.string.title_activity_myposts);

        } else if (id == R.id.nav_charts) {

            fab.hide();

            myChartFragment = new ChartFragment().setContext(context);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack if needed
            transaction.replace(R.id.map, myChartFragment);
            transaction.addToBackStack(null);

            // Commit the transaction
            transaction.commit();
            setTitle(R.string.title_activity_chart);

            // IMPLEMENTACIÓN VIEJA (Activity Completo)
            /*Intent chartIntent = new Intent(MapsActivity.this, ChartActivity.class);
            MapsActivity.this.startActivity(chartIntent);*/

        } else if (id == R.id.nav_logout) {

            fab.show();

            LoginManager.getInstance().logOut();
            //startActivity(new Intent(MapsActivity.this, LoginActivity.class));
        }

        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {


            if (requestCode == 1) {
                if (resultCode == Activity.RESULT_OK) {
                    String titulo = data.getStringExtra("titulo");
                    String descripcion = data.getStringExtra("descripcion");
                    String categoria = data.getStringExtra("categoria");
                    String imgpath = data.getStringExtra("img");


                    File mediaStorageDir = new File(
                            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "SandiApp/" + imgpath);
                    String path = mediaStorageDir.getAbsolutePath();
                    /* GIRAR FOTO */
                    //Obtener rotación
                    ExifInterface exif = null;
                    try {
                        exif = new ExifInterface(path);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                            ExifInterface.ORIENTATION_UNDEFINED);
                    // Obtener bmp rotado
                    Bitmap bitmap = Formulario.rotateBitmap(BitmapFactory.decodeFile(path), orientation);
                    /* ---FIN--- */
                    // La linea siguiente funciona en caso de evitar la rotación: (OLD)
                    // Bitmap bitmap = BitmapFactory.decodeFile(path);
                    String img64 = code(bitmap);
                    String imgHD64 = codeHD(bitmap);

                    addPinToCurrentLoc(titulo, descripcion, categoria, img64, imgHD64);

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
        //Definimos que hacer cuando se aprieta el floating action button
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LatLng loc = getCurrentLocation();
                if (loc.latitude != -33.478905 && loc.longitude != -70.657607) {
                    Intent Form = new Intent(MapsActivity.this, Formulario.class);
                    startActivityForResult(Form, 1);
                } else {
                    AlertDialog alertDialog = new AlertDialog.Builder(MapsActivity.this).create();
                    alertDialog.setTitle("No se pudo encontrar su ubicación");
                    alertDialog.setMessage("Para mostrar su ubicación debe encender el GPS en su dispositivo.");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }
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
                    } catch (Exception e) {
                        AlertDialog alertDialog = new AlertDialog.Builder(MapsActivity.this).create();
                        alertDialog.setTitle("No se pudo encontrar su ubicación");
                        alertDialog.setMessage("Para mostrar su ubicación debe encender el GPS en su dispositivo.");
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();
                    }

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

        /* Para saber cuál fue el último marker presionado (Y saber si su infoWindow sigue abierto) */
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener()
        {
            @Override
            public boolean onMarkerClick(com.google.android.gms.maps.model.Marker marker)
            {
                marker.showInfoWindow();
                lastMarkerClicked = marker;
                return true;
            }
        });

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

                        String creatorID = "";
                        try {
                            editNameDialog.setDate(snap.child("date").getValue().toString());
                            editNameDialog.setMarkerID(markerID);
                            creatorID = snap.child("creator").getValue().toString();
                        } catch (NullPointerException e) {
                        }

                        mFire.child("users").child(creatorID).child("name").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    editNameDialog.setCreator(dataSnapshot.getValue().toString());
                                }
                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {

                            }
                        });

                        mFire.child("images").child(markerID).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    editNameDialog.setImage(dataSnapshot.getValue().toString(), markerID);
                                }
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
        final Firebase mFireMarkers = mFire.child("markers");

        mFireMarkers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if(snapshot.hasChildren()){
                    for (DataSnapshot child : snapshot.getChildren()) {

                        boolean[] shouldCreateMark = new boolean[]{false, false, false, false};

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

                        if(child.child("category").exists()){

                            /* TODO arreglar esto! Si se hace login con facebook, luego logout y
                            despues login de nuevo, entonces aparece un error y no se pueden cargar
                            los pins.
                             */
                            try{
                                String category = child.child("category").getValue().toString();
                                boolean isCategorySelected = false;
                                for(int i=0; i<categoriesMenuItems.length; i++){
                                    if(categoriesMenuItems[i].isChecked() && (categoriesMenuItems[i].getTitle().equals("Todos")
                                            || categoriesMenuItems[i].getTitle().equals(category))){
                                        shouldCreateMark[3] = true;
                                        break;
                                    }
                                }
                            }catch (NullPointerException e){}
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
                            if(child.child("creator").exists()){
                                if(child.child("creator").getValue().toString().equals(LoginActivity.userID)){
                                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                                }
                            }
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

        mFireMarkers.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                mMap.clear();
                loadPins();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

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
    public void addPinToCurrentLoc(String titulo, String descripcion, String category, String image, String imageHD){

        focusCamera(currentLocation, 13);

        String id = getRandomString(32);
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();

        saveMarkerToFirebase(id, dateFormat.format(date).toString(), titulo, descripcion, category, image, imageHD, currentLocation);

    }

    /* Retorna la ubicacion actual. La ubicacion actual se calcula cuando
    esta actividad esta corriendo, es decir que si se pide la ubicacion actual
    antes de haber llamado a esta actividad al menos una vez, se retornara una
    ubicacion default.
     */
    public static LatLng getCurrentLocation(){
        return currentLocation;
    }

    private void saveMarkerToFirebase(String id, String date, String title, String description, String category, String image, String imageHD, LatLng location){

        mFire.child("markers").child(id).child("creator").setValue(LoginActivity.userID);
        mFire.child("markers").child(id).child("date").setValue(date);
        mFire.child("markers").child(id).child("title").setValue(title);
        mFire.child("markers").child(id).child("description").setValue(description);
        mFire.child("markers").child(id).child("category").setValue(category);
        mFire.child("images").child(id).setValue(image);
        //mFire.child("markers").child(id).child("image").setValue(image);
        mFire.child("markers").child(id).child("latitude").setValue(location.latitude);
        mFire.child("markers").child(id).child("longitude").setValue(location.longitude);

        final String mId = id;
        final String hdImg = imageHD;
        // La foto HD la cargamos después de haber cargado el resto... (Tarda un rato)
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                mFire.child("hd-images").child(mId).setValue(hdImg);
            }
        }, 3000);
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

    private String codeHD(Bitmap imgOriginalSize) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Bitmap img = ScaleImage(imgOriginalSize, 1920, 1920); // Ajustar tamaño, maximo 1920x1920 (FullHD=1920x1080p)
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

    private void PermisionCheck() {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                //explicacion de los permisos
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        0);
            }


            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    //explicacion de los permisos
                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            0);
                }

            }
        }
    }

    @Override
    public void onFragmentInteraction(String str){

    }

    public NavigationView getNavigationView(){
        return navigationView;
    }

    public void focusCamera(LatLng loc, int zoom){
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, zoom));
    }
}
