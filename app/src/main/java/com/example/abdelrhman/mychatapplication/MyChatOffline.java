package com.example.abdelrhman.mychatapplication;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

/**
 * Created by Abdelrhman on 12/27/2017.
 */

public class MyChatOffline extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);


        //load picture offlinepicasso
        Picasso.Builder  builder = new Picasso.Builder(this);
        builder.downloader(new OkHttpDownloader(this , Integer.MAX_VALUE));
        Picasso built = builder.build();
        built.setIndicatorsEnabled(true);
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);
    }
}
