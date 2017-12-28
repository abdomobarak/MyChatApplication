package com.example.abdelrhman.mychatapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class SettingActivity extends AppCompatActivity {

    private CircleImageView setting_UserDisplayImage;
    private TextView settingDisplayName , settingDisplayStatus ;
    private Button settingChangeprofileImage_btn , settingChangestatus_btn;

    private DatabaseReference getUserDataReference;
    private FirebaseAuth mAuth;

    private StorageReference storeprofileImageStorageRef;

    private final static int gallery_Pick =1;

    Bitmap thumpBitmap;
    private StorageReference thumbImageRef;
    private ProgressDialog dialog;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        mAuth = FirebaseAuth.getInstance();
        String online_User_Id  = mAuth.getCurrentUser().getUid();

        thumbImageRef =FirebaseStorage.getInstance().getReference().child("Thumb_Images");

        getUserDataReference  = FirebaseDatabase.getInstance().getReference()
        .child("Users").child(online_User_Id);
        getUserDataReference.keepSynced(true);

        storeprofileImageStorageRef = FirebaseStorage.getInstance().getReference()
        .child("Profile_Images");

        setting_UserDisplayImage = findViewById(R.id.circleImageView);
        settingDisplayName = findViewById(R.id.setting_UserAnme);
        settingDisplayStatus = findViewById(R.id.setting_User_Status);
        settingChangeprofileImage_btn = findViewById(R.id.setting_Change_Image_Button);
        settingChangestatus_btn = findViewById(R.id.setting_Change_status_Button);

        dialog =new ProgressDialog(this);



        getUserDataReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name  = dataSnapshot.child("user_name").getValue().toString();
                String status = dataSnapshot.child("user_status").getValue().toString();
                final String image = dataSnapshot.child("user_image").getValue().toString();
                String thumb_image = dataSnapshot.child("user_thumb_image").getValue().toString();

                settingDisplayName.setText(name);
                settingDisplayStatus.setText(status);


                if (!image.equals("default_profile")){



                 // Picasso.with(SettingActivity.this).load(image).placeholder(R.drawable.defulat_profile).into(setting_UserDisplayImage);
                    Picasso.with(SettingActivity.this).load(image).networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.defulat_profile)
                            .into(setting_UserDisplayImage, new Callback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError() {
                                    Picasso.with(SettingActivity.this).load(image)
                                            .placeholder(R.drawable.defulat_profile)
                                            .into(setting_UserDisplayImage);


                                }
                            });
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        settingChangeprofileImage_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent , gallery_Pick);
            }
        });

        settingChangestatus_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String old_Status = settingDisplayStatus.getText().toString();

                Intent statusIntent = new Intent(SettingActivity.this , StatusActivity.class);
                statusIntent.putExtra("user_status" ,old_Status );
                startActivity(statusIntent);
                finish();


            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == gallery_Pick && resultCode == RESULT_OK && data!= null){

            Uri imageUri = data.getData();

            // start picker to get image for cropping and then use the image in cropping activity
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1 )
                    .start(this);

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                dialog.setTitle("Updating Profile Image");
                dialog.setMessage("Please Wait , While updating Your Image......");
                dialog.show();


                Uri resultUri = result.getUri();

                File thumbFileUri = new File(resultUri.getPath());
                String User_Id = mAuth.getCurrentUser().getUid();

                try {

                    thumpBitmap = new Compressor(this)
                            .setMaxWidth(200)
                            .setMaxHeight(200)
                            .setQuality(50)
                            .compressToBitmap(thumbFileUri);
                }
                catch (IOException e){

                    e.printStackTrace();
                }
                ByteArrayOutputStream byteArrayOnputStream = new ByteArrayOutputStream();
                thumpBitmap.compress(Bitmap.CompressFormat.JPEG , 50 , byteArrayOnputStream);
                final  byte[] thumb_byte= byteArrayOnputStream.toByteArray();



                StorageReference filepathStorageRef =storeprofileImageStorageRef.child(User_Id + ".jpg");
                final StorageReference thumb_filePath = thumbImageRef.child(User_Id + ".jpg");





                filepathStorageRef.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if (task.isSuccessful()){

                            Toast.makeText(SettingActivity.this , "Saving Your Profile Image..." , Toast.LENGTH_LONG).show();
                            final String downlaodUri = task.getResult().getDownloadUrl().toString();
                            UploadTask uploadTask = thumb_filePath.putBytes(thumb_byte);
                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                    String thumb_downloadUri = task.getResult().getDownloadUrl().toString();
                                    if (task.isSuccessful()){

                                        Map update_user_data = new HashMap();
                                        update_user_data.put("user_image" , downlaodUri);
                                        update_user_data.put("user_thumb_image" , thumb_downloadUri);

                                        getUserDataReference.updateChildren(update_user_data)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {

                                                        //image loaded sucessful
                                                        Toast.makeText(SettingActivity.this , "Your Image updated Sucessfully.." ,
                                                                Toast.LENGTH_LONG).show();

                                                        dialog.dismiss();
                                                    }
                                                });

                                    }

                                }
                            });


                        }
                        else {

                            Toast.makeText(SettingActivity.this ,
                                    "Error Occured , While Saving Image." , Toast.LENGTH_LONG).show();
                             dialog.dismiss();
                        }
                    }
                });

            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();
            }
        }
    }
}
