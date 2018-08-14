package com.example.tokenhubatu.logindraft2;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class LoginDraft2 extends AppCompatActivity implements View.OnClickListener {
   private Button btn,btnLogin;
   private EditText txtPhone,txtPassword;

   private DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_draft2);

         btn =  findViewById(R.id.btnSignup);
         btnLogin= findViewById(R.id.btnLogin);

         txtPhone=findViewById(R.id.txtPhone);
         txtPassword=findViewById(R.id.txtPassword);


        dbRef= FirebaseDatabase.getInstance().getReference().child("Users");

        btn.setOnClickListener(this);
        btnLogin.setOnClickListener(this);


    }
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
                          messageBox("found");

                        }
                        else {
                            txtPassword.setError("Not found!");
                            txtPhone.setError("Not found!");
                        }
                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

    }

    public void messageBox(String s){
        Toast.makeText(this,s,Toast.LENGTH_LONG).show();
    }
}
