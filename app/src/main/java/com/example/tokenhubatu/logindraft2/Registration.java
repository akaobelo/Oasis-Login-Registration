package com.example.tokenhubatu.logindraft2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;


public class Registration extends AppCompatActivity implements View.OnClickListener {
    EditText etPhone,etPass1,etpass2;
    Button btnreg, btnsignUp;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        mAuth = FirebaseAuth.getInstance();



        progressDialog = new ProgressDialog(this);

        etPhone = (EditText) findViewById(R.id.etPhoneNum);
        etPass1 = (EditText) findViewById(R.id.etPass1);
        etpass2 = (EditText) findViewById(R.id.etPass2);
        btnreg = (Button) findViewById(R.id.btnreg);
        btnsignUp = (Button)findViewById(R.id.btnsignUp);

//        btnreg.setOnClickListener(new View.OnClickListener()
        btnreg.setOnClickListener(this);
        btnsignUp.setOnClickListener(this);


    }
    public void displayToast(View v) {
        Toast.makeText(this, "Wrong Password", Toast.LENGTH_SHORT).show();
    }


    private void RegUser(){
        String Pnumber = etPhone.getText().toString().trim();
        String Fpass = etpass2.getText().toString().trim();

        if (TextUtils.isEmpty(Pnumber)){
            Toast.makeText(this,"Enter Phone Number", Toast.LENGTH_SHORT).show();
            return ;
        }
        if (TextUtils.isEmpty(Fpass)){
            Toast.makeText(this,"Enter Password", Toast.LENGTH_SHORT).show();
            return;
        }
        progressDialog.setMessage("Registering User..");
        progressDialog.show();

    }
    public void onClick(View v){
        if(v == btnreg){
            RegUser();
        }
        if (v == btnsignUp){

        }
    }

}
