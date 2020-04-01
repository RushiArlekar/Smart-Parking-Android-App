package com.star.parking;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ValueEventListener;


public class Registration extends AppCompatActivity {

    private Button reg_button;
    private EditText editName, editAddress, editCarName, editCarNumber, editRFID, editEmailId, editPassword, editContact;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration);

        getSupportActionBar().setTitle("Registration");
        getSupportActionBar().setBackgroundDrawable(getDrawable(R.drawable.grad_toolbar));

        firebaseAuth = FirebaseAuth.getInstance();

        editName = (EditText) findViewById(R.id.editName);
        editAddress = (EditText) findViewById(R.id.editAddress);
        editCarName = (EditText) findViewById(R.id.editCarName);
        editCarNumber = (EditText) findViewById(R.id.editCarNumber);
        editRFID = (EditText) findViewById(R.id.editRFID);
        editEmailId = (EditText) findViewById(R.id.editEmailId);
        editPassword = (EditText) findViewById(R.id.editPassword);
        editContact = (EditText) findViewById(R.id.editContact);

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user!=null){

                    Toast.makeText(Registration.this,"Loading...",Toast.LENGTH_LONG).show();
                    //startActivity(new Intent(Registration.this,MainActivity.class));
                    //finish();
                }
            }
        };

        reg_button = (Button) findViewById(R.id.button1);

        reg_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startRegister();
            }
        });
    }

    @Override
    protected  void onStart(){
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onPause(){
        super.onPause();
        firebaseAuth.removeAuthStateListener(authStateListener);
    }

    private void startRegister()
    {


        final String user_name = editName.getText().toString().trim();
        final String user_address = editAddress.getText().toString().trim();
        final String user_car_name = editCarName.getText().toString().trim();
        final String user_car_number = editCarNumber.getText().toString().trim();
        final String user_RFID = editRFID.getText().toString().trim();
        final String email_id = editEmailId.getText().toString().trim();
        final String user_contact = editContact.getText().toString().trim();
        String user_password  = editPassword.getText().toString().trim();

        if(TextUtils.isEmpty(user_name) || TextUtils.isEmpty(user_address) || TextUtils.isEmpty(user_car_name)
                || TextUtils.isEmpty(user_car_number) || TextUtils.isEmpty(user_RFID) || TextUtils.isEmpty(email_id)
                || TextUtils.isEmpty(user_password) || TextUtils.isEmpty(user_contact)){
            Toast.makeText(Registration.this,"Fields are Emty",Toast.LENGTH_LONG).show();
        }
        else{

            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Registering, Please wait...");
            progressDialog.show();
            progressDialog.setCanceledOnTouchOutside(false);


            firebaseAuth.createUserWithEmailAndPassword(email_id,user_password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isSuccessful()){

                                final DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("user_payment")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                UserPayment userPayment = new UserPayment(0,"no",0,0,0);
                                databaseReference1.setValue(userPayment);

                                final UserInformation userInformation = new UserInformation(user_name, user_address, user_car_name, user_car_number, user_RFID, email_id, user_contact);

                                FirebaseDatabase.getInstance().getReference("reg_users")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .setValue(userInformation).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            progressDialog.hide();
                                            Toast.makeText(Registration.this,"Registration Successful. Welcome "+user_name,Toast.LENGTH_LONG).show();
                                            startActivity(new Intent(Registration.this,MainActivity.class));
                                            finish();

                                        }
                                        else {
                                            Toast.makeText(Registration.this, "Registration Failed, Please try again", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                            else
                            {
                                Toast.makeText(Registration.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

        }
    }
}


