package cl.saratscheff.sandiapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
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

    private ArrayList<MessageClass> mensajes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint);
        listViewDiscussion = (ListView)findViewById(R.id.listViewDiscussion);


        // String[] codeLearnChapters = new String[] { "Android Introduction","Android Setup/Installation","Android Hello World","Android Layouts/Viewgroups","Android Activity & Lifecycle","Intents in Android"};
        //ArrayAdapter<String> codeLearnArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, codeLearnChapters);
        // listViewDiscussion.setAdapter(codeLearnArrayAdapter);
        mensajes = new ArrayList<MessageClass>();
        listViewDiscussion.setAdapter(new MessageRowAdapter(this));


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
