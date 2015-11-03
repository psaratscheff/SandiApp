package cl.saratscheff.sandiapp;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ComplaintActivity extends AppCompatActivity {

    ListView listViewDiscussion;
    private Firebase nRef;
    private Firebase messagesRef;
    private Firebase usersRef;

    private String complaintID;
    private String userID;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint);
        final ImageButton imageButton = (ImageButton) findViewById(R.id.imageButton);
        listViewDiscussion = (ListView)findViewById(R.id.listViewDiscussion);
        listViewDiscussion.setAdapter(new MessageRowAdapter(this));

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                final Dialog nagDialog = new Dialog(ComplaintActivity.this,android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
                nagDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                nagDialog.setCancelable(false);
                nagDialog.setContentView(R.layout.full_screen_image);
                Button btnClose = (Button)nagDialog.findViewById(R.id.btnIvClose);
                ImageView ivPreview = (ImageView)nagDialog.findViewById(R.id.imageViewPreview);
                btnClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {

                        nagDialog.dismiss();
                    }
                });

                /* EJEMPLO DE IMAGEN EN BITMAP DESDE EL DRAWABLE DE PLACEHOLDER */
                Bitmap bitMapImage = BitmapFactory.decodeResource(ComplaintActivity.this.getResources(),
                        R.drawable.dead_watermelon);
                ivPreview.setImageBitmap(bitMapImage);
                nagDialog.show();
            }
        });





        userName = "USUARIO_EJEMPLO"; /* LoginActivity.userName */
        complaintID = "edsgeac2BPLpgqtFd6zTnQsnqhiqeH3L"; /* RECIBIR DESDE EL MAIN */


        Firebase.setAndroidContext(this);
        nRef = new Firebase("https://sizzling-heat-8397.firebaseio.com/");
        messagesRef = nRef.child("markers").child(complaintID).child("messages");
        usersRef = nRef.child("users");

        // onChildAdded event is triggered once for each existing child and then again every time a new child is added
        messagesRef.addChildEventListener(new ChildEventListener() {
            // Retrieve new posts as they are added to the database
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChildKey) {
                MessageClass newMessage = snapshot.getValue(MessageClass.class);
                ((MessageRowAdapter)listViewDiscussion.getAdapter()).add(newMessage);
                System.out.println("Author: " + newMessage.getAuthor());
                System.out.println("Message: " + newMessage.getContent());
            }
            // Get the data on a post that has changed
            @Override
            public void onChildChanged(DataSnapshot snapshot, String previousChildKey) {
                String title = (String) snapshot.child("title").getValue();
                System.out.println("The updated post title is " + title);
            }
            // Get the data on a post that has been removed
            @Override
            public void onChildRemoved(DataSnapshot snapshot) {
                String title = (String) snapshot.child("title").getValue();
                System.out.println("The blog post titled " + title + " has been deleted");
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                // NO me preocupo por cambios de orden (Jamás deberían ocurrir)
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                // ERRR, que hacer?
            }
        });
    }

    public void sendMessage(View view) {
        EditText editTextMessage = (EditText) findViewById(R.id.editTextMessage);
        String msg = editTextMessage.getText().toString();
        editTextMessage.getText().clear();

        Firebase messagePost = messagesRef.push();
        Map<String, String> post1 = new HashMap<String, String>();
        post1.put("author", userName);
        post1.put("content", msg);
        messagePost.setValue(post1);
    }
}
