package com.example.rk.photoblog;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Page_login extends AppCompatActivity {

//    Declare all thing her

    private EditText username;
    private EditText pass;
    private Button login;

    private FirebaseAuth mAuth;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_login);

//        Initiliazing here

        mAuth = FirebaseAuth.getInstance();

        username = findViewById(R.id.username);
        pass = findViewById(R.id.password);
        login = findViewById(R.id.login_btn);
        progressDialog = new ProgressDialog(this);

//        setOnclick on loging button

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//      when i pess loging buttton it show progresss Dailog

//                creat startlogint method

                Startloging();

                progressDialog.setMessage("Please wait we are checking your account");
                progressDialog.setTitle("Loging");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();


            }
        });
    }

//    here is my startloggng method

    private void Startloging() {

// get The text what is writen in email field and pass

    String email = username.getText().toString();
    final String password = pass.getText().toString();

//        her i check text field is not empty than execute code

        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {


                if (task.isSuccessful()){

                    SendToMain();

                }
                else {
                    String errormsg = task.getException().getMessage();
                    Toast.makeText(Page_login.this,"Error"+errormsg,Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }

            }
        });
    }


}

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser current_user = mAuth.getCurrentUser();
        if (current_user != null){

            SendToMain();

        }
    }

    private void SendToMain() {

        Intent main = new Intent(Page_login.this,MainActivity.class);
        startActivity(main);

    }

    public void signUp(View view) {

        Intent Reg = new Intent(Page_login.this, RegisterAct.class);
        startActivity(Reg);
    }
}
