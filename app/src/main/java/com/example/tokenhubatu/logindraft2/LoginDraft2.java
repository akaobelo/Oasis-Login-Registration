package com.example.tokenhubatu.logindraft2;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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


public class LoginDraft2 extends AppCompatActivity implements View.OnClickListener {
   private Button btn,btnLogin,btnForgotPassword;
   private EditText txtPhone,txtPassword;

   private EditText txtCode1,txtCode2,txtCode3,txtCode4,txtCode5,txtCode6;

   private DatabaseReference dbRef;
   private FirebaseAuth mAuth;
   private PhoneAuthProvider.ForceResendingToken mResendToken;
   private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
   private String verificationID;
   private    EditText txtVerificatioCode;
    private boolean autoVerify=false;

     private AlertDialog.Builder alert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_draft2);

         btn =  findViewById(R.id.btnSignup);
         btnLogin= findViewById(R.id.btnLogin);
        btnForgotPassword=findViewById(R.id.btnForgot);
         txtPhone=findViewById(R.id.txtPhone);
         txtPassword=findViewById(R.id.txtPassword);




        dbRef= FirebaseDatabase.getInstance().getReference().child("Users");

        btn.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        btnForgotPassword.setOnClickListener(this);

        mAuth=FirebaseAuth.getInstance();

        mCallbacks= new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

                signInWithPhoneAuthCredential(phoneAuthCredential);

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                    if (e instanceof FirebaseAuthInvalidCredentialsException){
                        messageBox("Invalid Credential");
                    }else if (e instanceof FirebaseTooManyRequestsException){
                        messageBox(e+"");
                    }
            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
             //   super.onCodeSent(s, forceResendingToken);

                verificationID=s;
                mResendToken=forceResendingToken;
            }
        };


    }
    //Firebase Authentication


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser= mAuth.getCurrentUser();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    private void startPhoneNumberVerification(String phoneNumber){
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    phoneNumber,
                    60,
                    TimeUnit.SECONDS,
                    LoginDraft2.this,
                    mCallbacks
            );
    }

    private void verifyPhoneNumberWithCode(String verificationId,String code){
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId,code);
        signInWithPhoneAuthCredential(credential);
    }

    private void resendVerificationCode(String phoneNumber, PhoneAuthProvider.ForceResendingToken token){
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                60,
                TimeUnit.SECONDS,
                LoginDraft2.this,
                mCallbacks,
                token);
    }

    private void signInWithPhoneAuthCredential(final PhoneAuthCredential credential){
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(LoginDraft2.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){

//                                FirebaseUser user= task.getResult().getUser();
                               txtVerificatioCode.setText(credential.getSmsCode());
                               txtVerificatioCode.setEnabled(false);
                               autoVerify=true;
                                String OTPcode=credential.getSmsCode()+"";


                                getCode(OTPcode);
                            }else{
                                if (task.getException() instanceof FirebaseAuthInvalidCredentialsException){
                                    txtVerificatioCode.setError("Invalid Code");
                                }
                            }
                    }
                });
    }




    ///////////////////////////
    ///////////////////////////
    ///////////////////////////
    ///////////////////////////
    //Login
    public void openReg(){
        Intent intent = new Intent(this,Registration.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {

        if (view == btn){
            openReg();
        }
        if (view == btnLogin){
          final  String phoneNum=txtPhone.getText().toString().trim();
          final String password=txtPassword.getText().toString().trim();
            dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (TextUtils.isEmpty(txtPhone.getText().toString().trim())){
                        txtPhone.setError("Please fill up phone number field");

                    }else if(TextUtils.isEmpty(txtPassword.getText().toString().trim())) {
                        txtPassword.setError("Please fill up password field");
                    }else{

                        boolean exists = false;
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            Map<String, Object> model = (Map<String, Object>) child.getValue();

                            if(model.get("password").equals(password) && model.get("phoneNumber").equals(phoneNum)) {
                                exists = true;
                                break;
                            }
                        }

                        if(exists) {
                            startPhoneNumberVerification(txtPhone.getText().toString().trim());
                            LayoutInflater factory= LayoutInflater.from(LoginDraft2.this);
                            final View OTPverification= factory.inflate(R.layout.activity_otp_verification,null);

                            txtVerificatioCode= OTPverification.findViewById(R.id.txtVerificationCode);

                            txtCode1=OTPverification.findViewById(R.id.txtCode1);
                            txtCode2=OTPverification.findViewById(R.id.txtCode2);
                            txtCode3=OTPverification.findViewById(R.id.txtCode3);
                            txtCode4=OTPverification.findViewById(R.id.txtCode4);
                            txtCode5=OTPverification.findViewById(R.id.txtCode5);
                            txtCode6=OTPverification.findViewById(R.id.txtCode6);


                            addEditTextBorder();

                            final TextView lblVerificationInfo =OTPverification.findViewById(R.id.lblVerification);

                            lblVerificationInfo.setText("Please enter the One-Time Password(OTP) we sent to "+ phoneNum);

                        alert =new AlertDialog.Builder(LoginDraft2.this);

                            alert.setTitle("One-Time Password Verification").setView(OTPverification)
                                    .setPositiveButton("Resend Code", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            }).setNegativeButton("Verify Code", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                                if (autoVerify){
                                                    dialogInterface.dismiss();
                                                    messageBox("Welcome");
                                                }
                                }
                            });

                        alert.show();

                        }
                        else {
                            messageBox("Invalid Phone number/Password");
                        }
                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }else if(view == btnForgotPassword){
            signOut();
        }

    }

    public void messageBox(String s){
        Toast.makeText(this,s,Toast.LENGTH_LONG).show();
    }

    public void signOut(){
        mAuth.signOut();
        messageBox("Logged out");
    }

    public void addEditTextBorder(){
        ShapeDrawable shape= new ShapeDrawable(new RectShape());
        shape.getPaint().setColor(Color.BLACK);
        shape.getPaint().setStyle(Paint.Style.STROKE);
        shape.getPaint().setStrokeWidth(3);

        txtCode1.setBackground(shape);
        txtCode2.setBackground(shape);
        txtCode3.setBackground(shape);
        txtCode4.setBackground(shape);
        txtCode5.setBackground(shape);
        txtCode6.setBackground(shape);
    }


    public void getCode(String OTPcode){
        char one=OTPcode.charAt(0);
        char two=OTPcode.charAt(1);
        char three=OTPcode.charAt(2);
        char four=OTPcode.charAt(3);
        char five=OTPcode.charAt(4);
        char six=OTPcode.charAt(5);



        txtCode1.setText(one+"");
        txtCode2.setText(two+"");
        txtCode3.setText(three+"");
        txtCode4.setText(four+"");
        txtCode5.setText(five+"");
        txtCode6.setText(six+"");
    }
}
