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
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    private EditText user_id, pass_word;
    private Button login, signup;
    private FirebaseAuth firebaseAuth;;
    private  FirebaseAuth.AuthStateListener authStateListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().setTitle("Login");
        getSupportActionBar().setBackgroundDrawable(getDrawable(R.drawable.grad_toolbar));

        user_id = (EditText) findViewById(R.id.user_id);
        pass_word = (EditText) findViewById(R.id.password);
        login = (Button) findViewById(R.id.login_submit);
        signup = (Button) findViewById(R.id.signup);

        firebaseAuth = FirebaseAuth.getInstance();



        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = firebaseAuth.getCurrentUser();

                if(user != null){

                    //Intent that goes to homepage of the app
                    startActivity(new Intent(Login.this,MainActivity.class));
                    finish();

                }
                /*else{

                    Toast.makeText(Login.this,"User not found. Please enter valid user ID and password",Toast.LENGTH_LONG).show();

                }*/

            }
        };


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startLogin();
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                startActivity(new Intent(Login.this,Registration.class));
                finish();

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onResume(){
        super.onResume();

        firebaseAuth.addAuthStateListener(authStateListener);

    }

    @Override
    protected void onPause(){
        super.onPause();

        firebaseAuth.removeAuthStateListener(authStateListener);

    }

    public void startLogin(){

        String userID = user_id.getText().toString();
        String password = pass_word.getText().toString();

        if(TextUtils.isEmpty(userID) || TextUtils.isEmpty(password)){
            Toast.makeText(Login.this,"Empty fields",Toast.LENGTH_LONG).show();
        }
        else {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Logging in. Please wait...");
            progressDialog.show();
            progressDialog.setCanceledOnTouchOutside(false);

            firebaseAuth.signInWithEmailAndPassword(userID,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(!task.isSuccessful()){
                        progressDialog.dismiss();
                        Toast.makeText(Login.this,"Login Failed. Try again",Toast.LENGTH_LONG).show();
                    }
                }
            });

        }

    }
}

