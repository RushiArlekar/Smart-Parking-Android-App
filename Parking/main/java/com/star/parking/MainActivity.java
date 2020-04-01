package com.star.parking;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {


    private Button profileButton, paymentButton, freespaceButton, logoutButton;
    private TextView textviewVerified;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    //private static final String TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setBackgroundDrawable(getDrawable(R.drawable.grad_toolbar));

        textviewVerified = (TextView) findViewById(R.id.textviewVerified);
        profileButton = (Button) findViewById(R.id.profile_button);
        paymentButton = (Button) findViewById(R.id.payment_button);
        freespaceButton = (Button) findViewById(R.id.free_space_button);
        logoutButton = (Button) findViewById(R.id.logout_button);
        firebaseAuth = FirebaseAuth.getInstance();

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null) {
                    //Log.d(TAG, "onAuthStateChanged: signed_in:" + user.getUid());
                    //Toast.makeText(MainActivity.this,"Welcome " +user.getEmail(),Toast.LENGTH_LONG).show();
                }
                else{
                    //Log.d(TAG, "onAuthStateChanged: signed_out:" + user.getUid());
                    Toast.makeText(MainActivity.this,"Signed out",Toast.LENGTH_LONG).show();
                    startActivity(new Intent(MainActivity.this,Login.class));
                    finish();
                }
            }
        };

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,Profile.class));
            }
        });

        paymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,Payment.class));
            }
        });

        freespaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,AvailableParkingSpace.class));
                finish();
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.d(TAG, "onClick: attempting to sign out the user");
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this,Login.class));
                finish();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(authStateListener);
        if(FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()){
            textviewVerified.setText("");
            RegisterRFID();
        }
        else{
            textviewVerified.setText("Please verify your Email to complete the registration process (Open profile to verify Email)");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(authStateListener != null){
            FirebaseAuth.getInstance().removeAuthStateListener(authStateListener);

        }
    }


    protected void RegisterRFID(){

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("reg_users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String key= dataSnapshot.child("RFID").getKey();
                String value = dataSnapshot.child("RFID").getValue(String.class);
                //Toast.makeText(MainActivity.this, key, Toast.LENGTH_SHORT).show();

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("reg_rfid")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

                databaseReference.setValue(value);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
