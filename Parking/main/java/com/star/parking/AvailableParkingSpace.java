package com.star.parking;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AvailableParkingSpace extends AppCompatActivity {

    private TextView textSpace1,textSpace2,textSpace3,textSpace4;
    private Button getSpaceButton;
    private Integer totalSpace,total;
    private DatabaseReference databaseReference, referenceSpace1, referenceSpace2, referenceSpace3, referenceSpace4;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_available_parking_space);

        getSupportActionBar().setTitle("Available Spaces");
        getSupportActionBar().setBackgroundDrawable(getDrawable(R.drawable.grad_toolbar));

        textSpace1 = (TextView) findViewById(R.id.space1);
        textSpace2 = (TextView) findViewById(R.id.space2);
        textSpace3 = (TextView) findViewById(R.id.space3);
        textSpace4 = (TextView) findViewById(R.id.space4);

        getSpaceButton = (Button) findViewById(R.id.buttonGetSpace);
        totalSpace = 0;

        GetParkingSpace1();
        GetParkingSpace2();
        GetParkingSpace3();
        GetParkingSpace4();

        getSpaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetParkingSpace1();
                GetParkingSpace2();
                GetParkingSpace3();
                GetParkingSpace4();

                Toast.makeText(AvailableParkingSpace.this, "Refreshed", Toast.LENGTH_SHORT).show();
            }
        });

    }

    protected void GetParkingSpace1(){

        //databaseReference = FirebaseDatabase.getInstance().getReference("Empty rfid");
        referenceSpace1 = FirebaseDatabase.getInstance().getReference("P1");

        referenceSpace1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int space1 = dataSnapshot.getValue(int.class);
                if (space1 == 0) {
                    textSpace1.setText("Space 1: Available");
                    textSpace1.setBackgroundColor(Color.GREEN);
                }
                else{
                    textSpace1.setText("Space 1: Not Available");
                    textSpace1.setBackgroundColor(Color.RED);
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    protected void GetParkingSpace2(){

        //databaseReference = FirebaseDatabase.getInstance().getReference("Empty rfid");

        referenceSpace2 = FirebaseDatabase.getInstance().getReference("P2");

        referenceSpace2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int space2 = dataSnapshot.getValue(int.class);
                if (space2 == 0) {
                    textSpace2.setText("Space 2: Available");
                    textSpace2.setBackgroundColor(Color.GREEN);
                }
                else{
                    textSpace2.setText("Space 2: Not Available");
                    textSpace2.setBackgroundColor(Color.RED);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    protected void GetParkingSpace3(){

        //databaseReference = FirebaseDatabase.getInstance().getReference("Empty rfid");
        referenceSpace3 = FirebaseDatabase.getInstance().getReference("P3");

        referenceSpace3.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int space3 = dataSnapshot.getValue(int.class);
                if (space3 == 0) {
                    textSpace3.setText("Space 3: Available");
                    textSpace3.setBackgroundColor(Color.GREEN);
                }
                else{
                    textSpace3.setText("Space 3: Not Available");
                    textSpace3.setBackgroundColor(Color.RED);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    protected void GetParkingSpace4(){

        //databaseReference = FirebaseDatabase.getInstance().getReference("Empty rfid");
        referenceSpace4 = FirebaseDatabase.getInstance().getReference("P4");

        referenceSpace4.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int space4 = dataSnapshot.getValue(int.class);
                if (space4 == 0) {
                    textSpace4.setText("Space 4: Available");
                    textSpace4.setBackgroundColor(Color.GREEN);
                }
                else{
                    textSpace4.setText("Space 4: Not Available");
                    textSpace4.setBackgroundColor(Color.RED);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        //String key = databaseReference.getKey();

        //Toast.makeText(AvailableParkingSpace.this, "Spaces ", Toast.LENGTH_SHORT).show();

    }
}
