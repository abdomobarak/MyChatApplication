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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class Login_Activity extends AppCompatActivity {

    private Toolbar mToolbar;

    private Button LoginButton;
    private EditText Login_Email , Login_Password;
    private FirebaseAuth mAuth;
    private ProgressDialog dialog;

    private DatabaseReference usersReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_);

        mAuth = FirebaseAuth.getInstance();
        usersReference = FirebaseDatabase.getInstance().getReference().child("Users");

        mToolbar = findViewById(R.id.login_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Sign in");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Login_Email = findViewById(R.id.LoginEmailEditText);
        Login_Password = findViewById(R.id.LoginPasswordEditText);
        LoginButton = findViewById(R.id.Login_btn);
        dialog = new ProgressDialog(this);

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = Login_Email.getText().toString();
                String password = Login_Password.getText().toString();

                LoginUserAccount(email , password);
            }
        });

    }

    private void LoginUserAccount(String email, String password) {

        if (TextUtils.isEmpty(email)){

            Toast.makeText(Login_Activity.this, "Please Write Your Email"  ,Toast.LENGTH_LONG).show();
        }
        else if (TextUtils.isEmpty(password)){
            Toast.makeText(Login_Activity.this, "Please Write Your password"  ,Toast.LENGTH_LONG).show();
        }
        else {

            dialog.setTitle("Login Account");
            dialog.setMessage("Please Wait , While We Verfying Your Email ");
            dialog.show();

            mAuth.signInWithEmailAndPassword(email , password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()){

                                String online_user_id = mAuth.getCurrentUser().getUid();
                                String DeviceToken = FirebaseInstanceId.getInstance().getToken();

                                usersReference.child(online_user_id).child("device_token").setValue(DeviceToken)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                                Intent mainIntent = new Intent(Login_Activity.this , Main2Activity.class);
                                                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(mainIntent);
                                                finish();

                                            }
                                        });





                            }else {

                                Toast.makeText(Login_Activity.this,
                                        "Wrong Email and Password, Try again.."  ,Toast.LENGTH_LONG).show();
                            }

                            dialog.dismiss();
                        }
                    });
        }
    }
}
