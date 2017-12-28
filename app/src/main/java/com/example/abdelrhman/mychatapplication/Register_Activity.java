package com.example.abdelrhman.mychatapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register_Activity extends AppCompatActivity {

    Toolbar mToolbar;
    private EditText registerUsername , registerUserEmail , registerUserpassword;
    private Button createAccountButton;
    private FirebaseAuth mAuth;
    private ProgressDialog dialog;

    private DatabaseReference storeUserDefaultDataReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_);

        mAuth= FirebaseAuth.getInstance();

        mToolbar = findViewById(R.id.register_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Sign  up");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        registerUsername = findViewById(R.id.registerNameEditText);
        registerUserEmail = findViewById(R.id.registerEmailEditText);
        registerUserpassword = findViewById(R.id.registerPasswordEditText);
        dialog = new ProgressDialog(this);



        createAccountButton = findViewById(R.id.registerCreateAccount_btn);
        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = registerUsername.getText().toString();
                String email = registerUserEmail.getText().toString();
                String password = registerUserpassword.getText().toString();

                RegisterMYAccount(name , email , password);
            }
        });




    }



    private void RegisterMYAccount(final String name, String email, String password) {


        if (TextUtils.isEmpty(name)){

            Toast.makeText(Register_Activity.this , "Please Write Your Name" , Toast.LENGTH_LONG).show();

        }

       else if (TextUtils.isEmpty(email)){

            Toast.makeText(Register_Activity.this , "Please Write Your Email" , Toast.LENGTH_LONG).show();

        }

       else if (TextUtils.isEmpty(password)){

            Toast.makeText(Register_Activity.this , "Please Write Your Password" , Toast.LENGTH_LONG).show();

        }else {



            dialog.setTitle("Creating New Account");
            dialog.setMessage("Please Wait , While we creating Your Account");
            dialog.show();

            mAuth.createUserWithEmailAndPassword(email , password)
                    .addOnCompleteListener(Register_Activity.this , new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()){

                                String Device_Token = mAuth.getCurrentUser().getUid();

                                String Current_User_Id = mAuth.getCurrentUser().getUid();


                                storeUserDefaultDataReference = FirebaseDatabase.getInstance()
                                        .getReference()
                                        .child("Users")
                                         .child(Current_User_Id);
                                storeUserDefaultDataReference.child("user_name").setValue(name);
                                storeUserDefaultDataReference.child("user_status").setValue("Hey There ,iam Available," +
                                        " iam using ChatApp Developed By ShortCuts Software Comapany");
                                storeUserDefaultDataReference.child("user_image").setValue("default_profile");
                                storeUserDefaultDataReference.child("device_token").setValue(Device_Token);

                                storeUserDefaultDataReference.child("user_thumb_image").setValue("default_image")
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful()){
                                            Intent mainIntent = new Intent(Register_Activity.this , Main2Activity.class);
                                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(mainIntent);
                                            finish();

                                        }

                                    }
                                });






                            }
                            else {
                                Toast.makeText(Register_Activity.this , "Please Check Your Data , try Again..." , Toast.LENGTH_LONG).show();

                            }

                            dialog.dismiss();

                        }
                    });

        }
    }
}
