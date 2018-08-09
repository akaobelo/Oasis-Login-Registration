package com.example.tokenhubatu.logindraft2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LoginDraft2 extends AppCompatActivity {
   private Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_draft2);

         btn = (Button) findViewById(R.id.btnSignup);

        btn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
               openReg();
            }
        });
    }
    public void openReg(){
        Intent intent = new Intent(this,Registration.class);
        startActivity(intent);
    }
}
