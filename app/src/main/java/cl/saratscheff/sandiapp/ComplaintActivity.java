package cl.saratscheff.sandiapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class ComplaintActivity extends AppCompatActivity {

    ListView listViewDiscussion;
    private Firebase nRef;
    private String complaintID;
    private Firebase messagesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint);
        listViewDiscussion=(ListView)findViewById(R.id.listViewDiscussion);

        Firebase.setAndroidContext(this);
        nRef = new Firebase("https://sizzling-heat-8397.firebaseio.com/");
        complaintID = "edsgeac2BPLpgqtFd6zTnQsnqhiqeH3L";
        messagesRef = nRef.child("markers").child(complaintID).child("messages");

        messagesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                System.out.println(snapshot.getValue());  //prints "Do you have data? You'll love Firebase."
            }
            @Override public void onCancelled(FirebaseError error) { }
        });
    }

    public void sendMessage(View view) {
        EditText editTextMessage = (EditText) findViewById(R.id.editTextMessage);
        String msg = editTextMessage.getText().toString();
        editTextMessage.getText().clear();

        Firebase messagePost = messagesRef.push();
        Map<String, String> post1 = new HashMap<String, String>();
        post1.put("author", "USUARIO");
        post1.put("content", msg);
        messagePost.setValue(post1);
    }


}
