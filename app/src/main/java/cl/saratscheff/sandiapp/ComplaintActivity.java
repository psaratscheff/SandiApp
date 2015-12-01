package cl.saratscheff.sandiapp;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ShareActionProvider;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ServerValue;
import com.firebase.client.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class ComplaintActivity extends AppCompatActivity {

    ListView listViewDiscussion;
    private Firebase nRef;
    private Firebase messagesRef;

    private String title;
    private String markerID;
    private String userName;
    private Bitmap image;
    private Bitmap HDimage;

    private Dialog hdImageDialog;
    private ProgressDialog pgDialog;
    private Boolean loadingHDimage;

    private ShareActionProvider mShareActionProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint);
        final ImageButton imageButton = (ImageButton) findViewById(R.id.imageButton);
        listViewDiscussion = (ListView)findViewById(R.id.listViewDiscussion);
        listViewDiscussion.setAdapter(new MessageRowAdapter(this));

        userName = LoginActivity.userName;
        Intent intent = getIntent();
        markerID = intent.getStringExtra("markerID");
        title = intent.getStringExtra("title");
        this.setTitle(title); // Seteo el title del activity como el del complaint.

        Firebase.setAndroidContext(this);
        nRef = new Firebase("https://sizzling-heat-8397.firebaseio.com/");
        messagesRef = nRef.child("markers").child(markerID).child("messages");

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "SandiApp/" + markerID + ".jpg");
        String path = mediaStorageDir.getAbsolutePath();
        image = BitmapFactory.decodeFile(path);
        imageButton.setImageBitmap(image);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pgDialog = new ProgressDialog(ComplaintActivity.this);
                pgDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                pgDialog.setMessage("Cargando imagen. Por favor espere...");
                pgDialog.setIndeterminate(true);
                pgDialog.setCanceledOnTouchOutside(false);
                pgDialog.setOnCancelListener(
                        new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                loadingHDimage = false;
                            }
                        });
                loadingHDimage = true;
                pgDialog.show();

                nRef.child("hd-images").child(markerID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (loadingHDimage) { // En caso de no haber cancelado el cargar la imagen
                            pgDialog.hide();
                            HDimage = decode(dataSnapshot.getValue().toString());
                            hdImageDialog = new Dialog(ComplaintActivity.this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
                            hdImageDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            hdImageDialog.setCancelable(true); // Por defecto es true... se podria sacar, pero por si acaso (No hay otra forma de retroceder que con el back button)
                            hdImageDialog.setContentView(R.layout.full_screen_image);
                            final ImageView ivPreview = (ImageView) hdImageDialog.findViewById(R.id.imageViewPreview);
                            ivPreview.setImageBitmap(HDimage);
                            loadingHDimage = false;
                            hdImageDialog.show();
                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });
            }
        });

        // onChildAdded event is triggered once for each existing child and then again every time a new child is added
        messagesRef.addChildEventListener(new ChildEventListener() {
            // Retrieve new posts as they are added to the database
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChildKey) {
                // Cargo el mensaje solo una vez que ya contenga fecha
                snapshot.getRef().addValueEventListener(new ValueEventListener() {
                    boolean printed = false; // Evito cargar duplicadamente el mensaje

                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (!printed) {
                            MessageClass newMessage = snapshot.getValue(MessageClass.class);
                            ((MessageRowAdapter) listViewDiscussion.getAdapter()).add(newMessage);
                            printed = true; // Marco mensaje ya impreso
                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError error) {
                        System.out.println("The read failed: " + error.getMessage());
                    }
                });
            }

            // Get the data on a post that has changed
            @Override
            public void onChildChanged(DataSnapshot snapshot, String previousChildKey) {
                // nada
            }

            // Get the data on a post that has been removed
            @Override
            public void onChildRemoved(DataSnapshot snapshot) {
                // nada
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                // nada
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                // nada
            }
        });
    }

    @Override
    public void onResume(){

        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.chat_menu, menu);
        // Locate MenuItem with ShareActionProvider
        MenuItem item = menu.findItem(R.id.menu_item_share);

        // Fetch and store ShareActionProvider
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "SandiApp - " + title);
        sendIntent.setType("text/plain");


        mShareActionProvider.setShareIntent(sendIntent);
        //startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.facebook_app_id)));


        // Return true to display menu
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

    // Call to update the share intent
    private void setShareIntent(Intent shareIntent) {
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed(); // Los dialogos se manejan solos (canelables).
    }

    public void sendMessage(View view) {
        EditText editTextMessage = (EditText) findViewById(R.id.editTextMessage);
        String msg = editTextMessage.getText().toString();
        if (msg.isEmpty()){ return; }
        editTextMessage.getText().clear();

        Firebase messagePost = messagesRef.push();
        Map<String, String> post1 = new HashMap<String, String>();
        post1.put("author", userName);
        post1.put("content", msg);
        messagePost.setValue(post1);
        messagePost.child("createdAt").setValue(ServerValue.TIMESTAMP);
    }

    private Bitmap decode(String imageFile)
    {
        try {
            byte[] imageAsBytes = Base64.decode(imageFile, Base64.DEFAULT);
            Bitmap bmp = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
            return bmp;
        }catch (Exception e)
        {
            return null;
        }
    }
}
