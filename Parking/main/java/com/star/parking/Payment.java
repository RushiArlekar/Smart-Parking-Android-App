package com.star.parking;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.text.SymbolTable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Payment extends AppCompatActivity {

    private TextView textViewAmount, textviewMessage, textViewTime;
    private Button paymentButon,paymentButtonLater;
    private String value;
    private DatabaseReference referenceRFID, referenceRegisteredRFID;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference, reference;
    private FirebaseAuth firebaseAuth;
    private double amount;
    private Integer count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        getSupportActionBar().setTitle("Payment");
        getSupportActionBar().setBackgroundDrawable(getDrawable(R.drawable.grad_toolbar));

        paymentButon = (Button) findViewById(R.id.buttonPayment);
        paymentButtonLater = (Button) findViewById(R.id.buttonPaymentLater);
        textViewAmount = (TextView) findViewById(R.id.textviewAmount);
        textviewMessage = (TextView) findViewById(R.id.textviewMessage);
        textViewTime = (TextView) findViewById(R.id.textviewTime);

        referenceRegisteredRFID = FirebaseDatabase.getInstance().getReference("reg_rfid")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        //referenceRFID = FirebaseDatabase.getInstance().getReference("rfid_tags_scanned");
        //reference = FirebaseDatabase.getInstance().getReference("count");

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading, please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        referenceRegisteredRFID.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //progressDialog.hide();
                String key = dataSnapshot.getKey();
                final String regRFID = dataSnapshot.getValue(String.class);
                //textView.setText(String.valueOf(regRFID));
                //Toast.makeText(Payment.this, key + " Payment Successful " + String.valueOf(regRFID), Toast.LENGTH_LONG).show();

                paymentButon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(Payment.this,MainActivity.class));
                        finish();
                    }
                });

                referenceRFID = FirebaseDatabase.getInstance().getReference(regRFID != null ? regRFID : null);

                referenceRFID.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        progressDialog.hide();

                        String keyRFID = dataSnapshot.getKey();
                        final double time = dataSnapshot.getValue(double.class);
                        //final double time = getTime /60;
                        //Toast.makeText(Payment.this, time + " Payment Successful " +regRFID, Toast.LENGTH_LONG).show();
                        //textviewMessage.setText(time);

                        if(keyRFID.equals(regRFID)){

                            databaseReference = FirebaseDatabase.getInstance().getReference("user_payment")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

                            databaseReference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    final String paid = dataSnapshot.child("paid").getValue(String.class);
                                    final double paidAmount = dataSnapshot.child("paymentAmount").getValue(double.class);
                                    final double paidTime = dataSnapshot.child("time").getValue(double.class);
                                    final double toBePaid = dataSnapshot.child("toBePaid").getValue(double.class);
                                    final double pendingTime = dataSnapshot.child("pendingTime").getValue(double.class);

                                    if(time == 0){

                                        amount = 0;

                                    }
                                    else if(time <= 500){

                                        amount = 5;

                                    }
                                    else if(time > 500){

                                        amount = 5 + (time-8)*0.05;

                                    }
                                    if(amount == 0){

                                        final double newPaidAmount, newPaidTime, dispAmount, dispTime;
                                        newPaidAmount = paidAmount + toBePaid;
                                        newPaidTime = paidTime + pendingTime;

                                        if(toBePaid > 0){

                                            dispAmount = toBePaid;
                                            dispTime = pendingTime;

                                            textViewTime.setText(String.valueOf(dispTime)+" seconds");
                                            textViewAmount.setText("Rs. "+String.valueOf(dispAmount));
                                            textviewMessage.setText("Click to pay, Pending payment included");
                                            paymentButon.setText("Pay Now");
                                            paymentButtonLater.setText("Pay Later");

                                            paymentButon.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    doPayment(newPaidAmount,newPaidTime);
                                                }
                                            });

                                            paymentButtonLater.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    Toast.makeText(Payment.this, "Cannot Proceed, Please pay the pending payment first", Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                        }
                                        else {

                                            textViewTime.setText("0 seconds");
                                            textViewAmount.setText("Rs. 0.0");
                                            textviewMessage.setText("No pending payments");
                                            paymentButon.setText("Main Menu");
                                            paymentButtonLater.setText("View Usage");

                                            paymentButon.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    startActivity(new Intent(Payment.this,MainActivity.class));
                                                    finish();
                                                }
                                            });

                                            paymentButtonLater.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(Payment.this);
                                                    alertDialog.setTitle("Total Usage");
                                                    alertDialog.setMessage("Time :"+String.valueOf(paidTime)+" seconds\nAmount : Rs. "+String.valueOf(paidAmount));
                                                    alertDialog.setCancelable(true);
                                                    alertDialog.show();
                                                }
                                            });
                                        }
                                    }
                                    if(amount > 0){

                                        if(paid.equals("no")) {      //paid="no"

                                            final double newPaidAmount, newPaidTime, dispAmount, dispTime;

                                            if(toBePaid == 0) {

                                                newPaidAmount = amount;
                                                newPaidTime = time;

                                                textviewMessage.setText("Click to pay");
                                                paymentButtonLater.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Payment.this);
                                                        alertDialog.setTitle("Payment Postpone");
                                                        alertDialog.setMessage("Payment can be postponed only once, proceed?");
                                                        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                final UserPayment userPayment = new UserPayment(0, paid, 0, amount, time);
                                                                databaseReference.setValue(userPayment);
                                                                referenceRFID.setValue(0);
                                                                Toast.makeText(Payment.this, "Payment Pending", Toast.LENGTH_SHORT).show();
                                                                startActivity(new Intent(Payment.this, MainActivity.class));
                                                                finish();
                                                            }
                                                        });
                                                        alertDialog.setCancelable(true);
                                                        alertDialog.show();
                                                    }
                                                });
                                            }
                                            else {

                                                newPaidAmount = amount + toBePaid;
                                                newPaidTime = time + pendingTime;

                                                textviewMessage.setText("Click to pay, Pending payment inclueded");
                                                paymentButtonLater.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        Toast.makeText(Payment.this, "Cannot Proceed, Please pay the pending payment first", Toast.LENGTH_SHORT).show();
                                                    }
                                                });

                                            }

                                            textViewTime.setText(String.valueOf(newPaidTime+" seconds"));
                                            textViewAmount.setText("Rs. "+String.valueOf(newPaidAmount));
                                                paymentButon.setText("Pay Now");
                                                paymentButtonLater.setText("Pay Later");

                                                paymentButon.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {

                                                        doPayment(newPaidAmount, newPaidTime);
                                                    }
                                                });

                                        }
                                        else {      //paid=="yes"

                                            final double newPaidAmount, newPaidTime, dispAmount, dispTime;
                                            newPaidAmount = amount + paidAmount + toBePaid;
                                            newPaidTime = time + paidTime + pendingTime;
                                            dispAmount = amount + toBePaid;
                                            dispTime = time + pendingTime;

                                            if(toBePaid == 0){

                                                //Toast.makeText(Payment.this, "1  "+newPaidAmount, Toast.LENGTH_SHORT).show();

                                                textviewMessage.setText("Click to pay");
                                                paymentButtonLater.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Payment.this);
                                                        alertDialog.setTitle("Payment Postpone");
                                                        alertDialog.setMessage("Payment can be postponed only once, proceed?");
                                                        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                final UserPayment userPayment = new UserPayment(paidAmount,paid,paidTime,amount,time);
                                                                databaseReference.setValue(userPayment);
                                                                referenceRFID.setValue(0);
                                                                Toast.makeText(Payment.this, "Payment Pending", Toast.LENGTH_SHORT).show();
                                                                startActivity(new Intent(Payment.this,MainActivity.class));
                                                                finish();
                                                            }
                                                        });
                                                        alertDialog.setCancelable(true);
                                                        alertDialog.show();
                                                    }
                                                });
                                            }
                                            else{

                                                //Toast.makeText(Payment.this, "2  "+paidAmount, Toast.LENGTH_SHORT).show();
                                                textviewMessage.setText("Click to pay, Pending payment inclueded");
                                                paymentButtonLater.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        Toast.makeText(Payment.this, "Cannot Proceed, Please pay the pending payment first", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }

                                            textViewTime.setText(String.valueOf(dispTime)+" seconds");
                                            textViewAmount.setText("Rs. "+String.valueOf(dispAmount));
                                            paymentButtonLater.setText("Pay Later");
                                            paymentButon.setText("Pay Now");

                                            paymentButon.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {

                                                    doPayment(newPaidAmount,newPaidTime);
                                                }
                                            });

                                        }

                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    protected void doPayment(final double paidAmount, final double time){

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Payment.this);
        alertDialog.setTitle("Confirm Payment");
        alertDialog.setMessage("Click yes to proceed and pay");
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                final UserPayment userPayment = new UserPayment(paidAmount,"yes",time,0,0);

                FirebaseDatabase.getInstance().getReference("user_payment")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(userPayment)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    referenceRFID.setValue(0);
                                    //Toast.makeText(Payment.this, "Payment Successfull", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(Payment.this,MainActivity.class));
                                    finish();
                                }
                            }
                        });
            }
        });
        alertDialog.setCancelable(true);
        alertDialog.show();
    }

}
