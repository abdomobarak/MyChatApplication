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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class StatusActivity extends AppCompatActivity {

    private Toolbar mToolBar;
    private Button SaveChanesButton;
    private EditText StatusInputEditText;
    private DatabaseReference ChangeStatusRef;
    FirebaseAuth mAuth;
   private ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        mAuth = FirebaseAuth.getInstance();
        String User_id = mAuth.getCurrentUser().getUid();
        ChangeStatusRef = FirebaseDatabase.getInstance().getReference().child("Users").child(User_id);

        mToolBar = findViewById(R.id.statusAppBar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle("Change Title");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SaveChanesButton =findViewById(R.id.status_Button);
        StatusInputEditText = findViewById(R.id.status_Input);
        dialog = new ProgressDialog(this);

        String old_Status = getIntent().getExtras().get("user_status").toString();
        StatusInputEditText.setText(old_Status);

        SaveChanesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String new_Status = StatusInputEditText.getText().toString();
                ChangeProfileStatus(new_Status);


            }
        });
    }

    private void ChangeProfileStatus(String new_status) {

        if (TextUtils.isEmpty(new_status)){


            Toast.makeText(StatusActivity.this , "Please Write Your Status" , Toast.LENGTH_LONG).show();
        }else {

            dialog.setTitle("Change Profile Status");
            dialog.setMessage("Please Wait , While Your Status Updating....");
            dialog.show();

            ChangeStatusRef.child("user_status").setValue(new_status)
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isSuccessful()){
                        dialog.dismiss();
                        Intent settingsIntent = new Intent(StatusActivity.this , SettingActivity.class);

                        startActivity(settingsIntent);
                        Toast.makeText(StatusActivity.this , "Profile Status Updated Sucessfully..." , Toast.LENGTH_LONG).show();
                        finish();


                    }else {

                        Toast.makeText(StatusActivity.this , "Error Occurred..." , Toast.LENGTH_LONG).show();

                    }
                }
            });

        }
    }


}
