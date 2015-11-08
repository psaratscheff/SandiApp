package cl.saratscheff.sandiapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class InitActivity extends AppCompatActivity {

    private Firebase mRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);

        Firebase.setAndroidContext(this);
        mRef = new Firebase("https://sizzling-heat-8397.firebaseio.com");


        /* Si el usuario ya se ha registrado en el telefono, entonces se inicia sesion automaticamente */
        mRef.addAuthStateListener(new Firebase.AuthStateListener() {
            @Override
            public void onAuthStateChanged(AuthData authData) {
                if (authData != null) {
                    LoginActivity.userID = mRef.getAuth().getUid();
                    LoginActivity.userEmail = mRef.getAuth().getProviderData().get("email").toString();

                    mRef.child("users").child(LoginActivity.userID).child("name").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            LoginActivity.userName = dataSnapshot.getValue().toString();
                            startActivity(new Intent(InitActivity.this, MapsActivity.class));
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });
                }

                else {
                    startActivity(new Intent(InitActivity.this, LoginActivity.class));
                }
            }
        });
    }
}
