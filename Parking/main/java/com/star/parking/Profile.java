package com.star.parking;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;

public class Profile extends AppCompatActivity {

    private ListView listView;
    private Button VerifyButton;
    private TextView textViewVerified;
    private Integer MaxAttempt;
    private DatabaseReference databaseReference;
    private static final String TAG = "Profile";
    private ArrayList<String> arrayList = new ArrayList<>();
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    //private static final String TAG = "Profile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        getSupportActionBar().setTitle("Profile");
        getSupportActionBar().setBackgroundDrawable(getDrawable(R.drawable.grad_toolbar));

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        textViewVerified = (TextView) findViewById(R.id.textviewVerified);
        VerifyButton = (Button) findViewById(R.id.buttonVerified);
        MaxAttempt = 4;

        loadProfile();

        if(!user.isEmailVerified()) {
            textViewVerified.setTextColor(Color.RED);
            textViewVerified.setText("EMAIL NOT VERIFIED. Click on the button to verify");
            VerifyButton.setText("Send Verification Email");
            VerifyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(Profile.this, "Verification Email sent. " + MaxAttempt + " attempts left", Toast.LENGTH_SHORT).show();
                                textViewVerified.setText("Please click on the link sent to your Mailbox to verify the account and sign in again(Long press on the button to sign in again)");
                                MaxAttempt--;
                                if (MaxAttempt == 0) {
                                    textViewVerified.setText("Cannot Verify Email? Click here to register as a new user");
                                    VerifyButton.setText("Delete User");
                                    textViewVerified.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            DeleteRegister();
                                        }
                                    });
                                }
                                else {
                                    VerifyButton.setOnLongClickListener(new View.OnLongClickListener() {
                                        @Override
                                        public boolean onLongClick(View v) {
                                            FirebaseAuth.getInstance().signOut();
                                            startActivity(new Intent(Profile.this, Login.class));
                                            finish();
                                            return false;
                                        }
                                    });
                                }
                            }
                            else{
                                Toast.makeText(Profile.this, "Error sending verification Email. PLease try again ", Toast.LENGTH_SHORT).show();
                                MaxAttempt--;

                                if (MaxAttempt == 0) {
                                    textViewVerified.setText("Cannot Verify Email? Click here to register as a new user");
                                    VerifyButton.setText("Register");
                                    VerifyButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            DeleteRegister();
                                        }
                                    });
                                }
                            }
                        }
                    });
                }
            });
        }
        else {
            textViewVerified.setText("Email Verified");
            textViewVerified.setTextColor(Color.GREEN);
            VerifyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(Profile.this, "Logged out", Toast.LENGTH_SHORT).show();
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(Profile.this,Login.class));
                    finish();
                }
            });
        }

    }

    protected void DeleteRegister(){
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        AlertDialog.Builder dialog = new AlertDialog.Builder(Profile.this);
        dialog.setTitle("Register new User");
        dialog.setMessage("The current account data will be deleted and new user registration will have to be done using a valid Email \n" +
                "Are you sure?");
        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                final String Name=null;
                final String Address=null;
                final String CarName=null;
                final String CarNumber=null;
                final String RFID=null;
                final String UserID=null;
                final String Contact=null;

                final DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("user_payment")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                //UserPayment userPayment = new UserPayment(0,null,0,0,0);
                databaseReference1.removeValue();

                //Toast.makeText(Profile.this, "inside", Toast.LENGTH_SHORT).show();
                DatabaseReference userReferance = FirebaseDatabase.getInstance().getReference("reg_users")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

                final UserInformation userInformation1 = new UserInformation(Name,Address,CarName,CarNumber,RFID,UserID,Contact);

                userReferance.setValue(userInformation1);

                Toast.makeText(Profile.this, "Data Deleted", Toast.LENGTH_SHORT).show();

                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(Profile.this,Registration.class));
                finish();
            }
        });

        dialog.setCancelable(true);
        dialog.show();
    }

    protected void loadProfile(){

        databaseReference = FirebaseDatabase.getInstance().getReference("reg_users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        Toast.makeText(Profile.this,FirebaseAuth.getInstance().getCurrentUser().getEmail() ,Toast.LENGTH_LONG).show();

        listView = (ListView) findViewById(R.id.profileList);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,arrayList);
        listView.setAdapter(arrayAdapter);

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fetching data");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);

        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                progressDialog.hide();

                String key = dataSnapshot.getKey();
                String value = dataSnapshot.getValue(String.class);
                arrayList.add(key+ ": " +value);
                arrayAdapter.notifyDataSetChanged();

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Profile.this,"Error loading data",Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(Profile.this,Login.class));
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()){
           textViewVerified.setText("Email Varified");
           VerifyButton.setText("Log Out");
        }

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if(FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()) {
            textViewVerified.setText("Email Varified");
        }

    }
}
