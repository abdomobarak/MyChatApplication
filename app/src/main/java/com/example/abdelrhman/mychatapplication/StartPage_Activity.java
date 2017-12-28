package com.example.abdelrhman.mychatapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartPage_Activity extends AppCompatActivity {


   private Button NeednewAccountButton , AlreadyHaveAccountButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_page_);

        NeednewAccountButton = findViewById(R.id.need_account_btn);
        AlreadyHaveAccountButton = findViewById(R.id.have_account_btn);


        NeednewAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent RegisterIntent = new Intent(StartPage_Activity.this , Register_Activity.class);
                startActivity(RegisterIntent);

            }
        });

        AlreadyHaveAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent LoginIntent = new Intent(StartPage_Activity.this , Login_Activity.class);
                startActivity(LoginIntent);

            }
        });
    }
}
