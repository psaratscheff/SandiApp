package cl.saratscheff.sandiapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class ComplaintActivity extends AppCompatActivity {

    ListView listViewDiscussion;
    private Firebase nRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint);
        Firebase.setAndroidContext(this);

        listViewDiscussion=(ListView)findViewById(R.id.listViewDiscussion);

        nRef = new Firebase("https://sizzling-heat-8397.firebaseio.com/");

        nRef.child("message").addValueEventListener(new ValueEventListener() {
            int counter = 0;

            @Override
            public void onDataChange(DataSnapshot snapshot) {
                System.out.println("Contador: " + counter + "//" + snapshot.getValue());  //prints "Do you have data? You'll love Firebase."
            }
            @Override public void onCancelled(FirebaseError error) { }
        });

        nRef.child("message").child("message2").child("message3").setValue("Do you have data? You'll love Firebase.");
    }
}
