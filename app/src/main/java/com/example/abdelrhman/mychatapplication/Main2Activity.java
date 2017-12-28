package com.example.abdelrhman.mychatapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TableLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Main2Activity extends AppCompatActivity {

    private  FirebaseAuth mAuth;
    private Toolbar mToolbar;
    private ViewPager myViewerPager;
    private TabLayout myTablayout;

    private TabsPagerAdapter myTabsPagerAdapter;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        mToolbar = findViewById(R.id.main_page_toolbar);
       setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("MyChatApp");

        mAuth = FirebaseAuth.getInstance();

        //Tabs For MAin Activity
        myViewerPager  = findViewById(R.id.main_tabs_pager);
        myTabsPagerAdapter = new TabsPagerAdapter(getSupportFragmentManager());
        myViewerPager.setAdapter(myTabsPagerAdapter);
        myTablayout = findViewById(R.id.main_tabs);
        myTablayout.setupWithViewPager(myViewerPager);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null){

            LogOutUser();

        }
    }

    private void LogOutUser() {


            Intent startPafeIntent = new Intent(Main2Activity.this , StartPage_Activity.class);
            startPafeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(startPafeIntent);
            finish();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
         super.onCreateOptionsMenu(menu);
         getMenuInflater().inflate(R.menu.main_menu , menu);
         return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         super.onOptionsItemSelected(item);
         if (item.getItemId() == R.id.main_logOut_Button){

             mAuth.signOut();
             LogOutUser();
         }

        if (item.getItemId() == R.id.main_all_users_Button){
            Intent allUsersIntent = new Intent(Main2Activity.this , AllUsersActivity.class);
            startActivity(allUsersIntent);
        }

        if (item.getItemId() == R.id.main_account_setting_Button){

             Intent settingIntent = new Intent(Main2Activity.this , SettingActivity.class);
             startActivity(settingIntent);
         }


         return true;
    }
}
