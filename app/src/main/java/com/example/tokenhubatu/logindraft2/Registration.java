package com.example.tokenhubatu.logindraft2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;
import java.util.concurrent.TimeUnit;


public class Registration extends AppCompatActivity implements View.OnClickListener {
    EditText etPhone,etPass1,etpass2;
    Button btnreg, btnsignUp;

    private ProgressDialog progressDialog;

    private static final String TAG = "Registration";
    private String mVerificationId;

    private DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);


        progressDialog = new ProgressDialog(this);

        etPhone = findViewById(R.id.etPhoneNum);
        etPass1 = findViewById(R.id.etPass1);
        etpass2 = findViewById(R.id.etPass2);
        btnreg = findViewById(R.id.btnreg);
        btnsignUp = findViewById(R.id.btnsignUp);

        btnreg.setOnClickListener(this);
        btnsignUp.setOnClickListener(this);

        dbRef=FirebaseDatabase.getInstance().getReference().child("Users");

    }
    public void displayToast(View v) {
        Toast.makeText(this, "Wrong Password", Toast.LENGTH_SHORT).show();
    }

    public void messageBox(String s){
        Toast.makeText(this,s,Toast.LENGTH_LONG).show();
    }

    private void RegUser(){
        String Pnumber = etPhone.getText().toString().trim();
        String confirmPass = etpass2.getText().toString().trim();
        String Fpass= etPass1.getText().toString().trim();
        if (TextUtils.isEmpty(Pnumber)){
        etPhone.setError("Cannot be empty!");

            return ;
        }
        if (TextUtils.isEmpty(Fpass)){
           etPass1.setError("Cannot be empty!");
            return;
        }

        if (TextUtils.isEmpty(confirmPass)){
          etpass2.setError("Cannot be empty");
            return;
        }
        if (confirmPass.equals(Fpass)){

                if (Pnumber.length()  == 13){
                    Log.d("number checking",getAreaPhoneCode(Pnumber));
                        if (getAreaPhoneCode(Pnumber).equals("+639")){



                            User user= new User(Pnumber,Fpass);
                            dbRef.push().setValue(user, new DatabaseReference.CompletionListener(){


                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {


                                    dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                boolean exists=false;

                                            for(DataSnapshot  child : dataSnapshot.getChildren()){
                                                Map<String, Object> model = (Map<String, Object>) child.getValue();
                                                if(model.get("phoneNumber").equals(etPhone.getText().toString().trim())) {
                                                    exists = true;
                                                    break;
                                                }
                                            }


                                            if(exists) {
                                                etPhone.requestFocus();
                                                etPhone.setError("Phonenumber has already been registered.");
                                            }
                                            else {
                                                progressDialog.setMessage("Registering User..");
                                                progressDialog.show();
                                                progressDialog.dismiss();
                                                messageBox("User Registered");
                                                Intent intent = new Intent(Registration.this,LoginDraft2.class);
                                                startActivity(intent);
                                                finish();
                                            }

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }
                            });
                        }else{
                            etPhone.requestFocus();
                            etPhone.setError("Invalid Phone Numbers");
                        }
                }else{
                    etPhone.requestFocus();
                    etPhone.setError("Invalid Phone Number");
                }

        }else{
            etPass1.setError("Password not matched!");
            etpass2.setError("Password not matched!");
        }



    }


    @Override
    public void onClick(View view) {
        if (view == btnreg){
            RegUser();
        }
    }

    public String getAreaPhoneCode(String s){
        String check;

        char one=s.charAt(0);
        char two=s.charAt(1);
        char three=s.charAt(2);
        char four=s.charAt(3);
        check=one+""+two+""+three+""+four+"";

        return check;
    }


}
