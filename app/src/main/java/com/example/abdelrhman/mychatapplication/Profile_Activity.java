package com.example.abdelrhman.mychatapplication;

import android.icu.util.Calendar;
import android.icu.util.Currency;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.HashMap;

public class Profile_Activity extends AppCompatActivity {

    public Button SendFriendRequestButton, DeclineFriendRequestButton;
    public TextView ProfileName, ProfileStatus;
    public ImageView ProfileImage;
    public String receiver_User_id;

    public DatabaseReference UsersReference;

    private String Current_State;
    private DatabaseReference FirendRequestReference;
    public FirebaseAuth mAuth;
    private String Sender_User_Id;

    private DatabaseReference FriendsReference;
    private DatabaseReference NotificationsReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_);

        receiver_User_id = getIntent().getExtras().get("visit_user_id").toString();

        // Toast.makeText(Profile_Activity.this , visit_User_id , Toast.LENGTH_LONG).show();

        UsersReference = FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth = FirebaseAuth.getInstance();
        Sender_User_Id = mAuth.getCurrentUser().getUid();

        SendFriendRequestButton = findViewById(R.id.profile_user_send_req_btn);
        DeclineFriendRequestButton = findViewById(R.id.profile_visit_user_DeclineReq_btn);

        ProfileName = findViewById(R.id.profile_visit_userName);
        ProfileStatus = findViewById(R.id.profile_user_visit_UserStatus);
        ProfileImage = findViewById(R.id.profile_visit_user_image);

        Current_State = "not_friends";

        FirendRequestReference = FirebaseDatabase.getInstance().getReference().child("Friend_Requests");
        FirendRequestReference.keepSynced(true);


        FriendsReference = FirebaseDatabase.getInstance().getReference().child("Friends");
        FriendsReference.keepSynced(true);

        NotificationsReference = FirebaseDatabase.getInstance().getReference().child("Notifications");
        NotificationsReference.keepSynced(true );

        UsersReference.child(receiver_User_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("user_name").getValue().toString();
                String status = dataSnapshot.child("user_status").getValue().toString();
                String image = dataSnapshot.child("user_image").getValue().toString();

                ProfileName.setText(name);
                ProfileStatus.setText(status);
                Picasso.with(Profile_Activity.this).load(image).placeholder(R.drawable.defulat_profile).into(ProfileImage);


                FirendRequestReference.child(Sender_User_Id)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                if (dataSnapshot.exists()) {

                                    if (dataSnapshot.hasChild(receiver_User_id)) {

                                        String req_type = dataSnapshot.child(receiver_User_id)
                                                .child("request_type").getValue().toString();

                                        if (req_type.equals("sent")) {

                                            Current_State = "request_sent";

                                            SendFriendRequestButton.setText(R.string.cancel_friend_request);

                                            DeclineFriendRequestButton.setVisibility(View.INVISIBLE);
                                            DeclineFriendRequestButton.setEnabled(false);

                                        }
                                        else if (req_type.equals("received")) {

                                            Current_State = "request_received";
                                            SendFriendRequestButton.setText(R.string.accept_friend_request);

                                            DeclineFriendRequestButton.setVisibility(View.VISIBLE);
                                            DeclineFriendRequestButton.setEnabled(true);

                                            DeclineFriendRequestButton.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {

                                                    DeclineFriendRequest();
                                                }
                                            });
                                        }

                                    }

                                }
                                else {

                                    FriendsReference.child(Sender_User_Id)
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {

                                                    if (dataSnapshot.hasChild(receiver_User_id)){

                                                        Current_State = "friends";
                                                        SendFriendRequestButton.setText(R.string.unfriend_this_person);

                                                        DeclineFriendRequestButton.setVisibility(View.INVISIBLE);
                                                        DeclineFriendRequestButton.setEnabled(false);

                                                    }

                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DeclineFriendRequestButton.setVisibility(View.INVISIBLE);
        DeclineFriendRequestButton.setEnabled(false);



        if (!Sender_User_Id.equals(receiver_User_id)){
        SendFriendRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SendFriendRequestButton.setEnabled(false);


                if (Current_State.equals("not_friends")) {

                    SendFriendRequestToPerson();
                }

                if (Current_State.equals("request_sent")) {

                    CancelFriendRequest();
                }

                if (Current_State.equals("request_received")) {

                    AcceptFrinendRequest();
                }
                if (Current_State.equals("friends")) {

                    UnFriendaFriend();
                }
            }
        });

    }else {

            DeclineFriendRequestButton.setVisibility(View.INVISIBLE);
            SendFriendRequestButton.setVisibility(View.INVISIBLE);
        }
    }

    private void DeclineFriendRequest() {

        FirendRequestReference.child(Sender_User_Id).child(receiver_User_id).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {

                            FirendRequestReference.child(receiver_User_id).child(Sender_User_Id).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()) {

                                                SendFriendRequestButton.setEnabled(true);
                                                Current_State = "not_friends";
                                                SendFriendRequestButton.setText(R.string.send_friend_request);

                                                DeclineFriendRequestButton.setVisibility(View.INVISIBLE);
                                                DeclineFriendRequestButton.setEnabled(false);
                                            }

                                        }
                                    });
                        }
                    }
                });


    }


    private void UnFriendaFriend() {

        FriendsReference.child(Sender_User_Id).child(receiver_User_id).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            FriendsReference.child(receiver_User_id).child(Sender_User_Id).removeValue()
                                         .addOnCompleteListener(new OnCompleteListener<Void>() {
                                             @Override
                                             public void onComplete(@NonNull Task<Void> task) {

                                                 if (task.isSuccessful()){

                                                     SendFriendRequestButton.setEnabled(true);
                                                     Current_State = "not_friends";
                                                     SendFriendRequestButton.setText(R.string.send_friend_request);
                                                    // Toast.makeText(Profile_Activity.this , "Your request has Been Sent" , Toast.LENGTH_LONG).show();
                                                     DeclineFriendRequestButton.setVisibility(View.INVISIBLE);
                                                     DeclineFriendRequestButton.setEnabled(false);
                                                 }
                                             }
                                         });


                        }
                    }
                });

    }


    private void AcceptFrinendRequest() {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {

            Calendar calForATE = Calendar.getInstance();
            SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-YYYY");
            final String saveCurrentDate = currentDate.format(calForATE.getTime());


            FriendsReference.child(Sender_User_Id).child(receiver_User_id).child("date").setValue(saveCurrentDate)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            FriendsReference.child(receiver_User_id).child(Sender_User_Id).child("date").setValue(saveCurrentDate)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {


                                            FirendRequestReference.child(Sender_User_Id).child(receiver_User_id).removeValue()
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {

                                                            if (task.isSuccessful()) {

                                                                FirendRequestReference.child(receiver_User_id).child(Sender_User_Id).removeValue()
                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                                if (task.isSuccessful()) {

                                                                                    SendFriendRequestButton.setEnabled(true);
                                                                                    Current_State = "friends";
                                                                                    SendFriendRequestButton.setText(R.string.unfriend_this_person);

                                                                                    DeclineFriendRequestButton.setVisibility(View.INVISIBLE);
                                                                                    DeclineFriendRequestButton.setEnabled(false);
                                                                                }

                                                                            }
                                                                        });
                                                            }
                                                        }
                                                    });

                                        }
                                    });
                        }
                    });
        }
    }


    private void CancelFriendRequest() {

        FirendRequestReference.child(Sender_User_Id).child(receiver_User_id).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {

                            FirendRequestReference.child(receiver_User_id).child(Sender_User_Id).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()) {

                                                SendFriendRequestButton.setEnabled(true);
                                                Current_State = "not_friends";
                                                SendFriendRequestButton.setText(R.string.send_friend_request);

                                                DeclineFriendRequestButton.setVisibility(View.INVISIBLE);
                                                DeclineFriendRequestButton.setEnabled(false);

                                                Toast.makeText(Profile_Activity.this , "Your request has Been Canceled" , Toast.LENGTH_LONG).show();
                                            }

                                        }
                                    });
                        }
                    }
                });
    }


    private void SendFriendRequestToPerson() {

        FirendRequestReference.child(Sender_User_Id).child(receiver_User_id)
                .child("request_type").setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {


                        if (task.isSuccessful()) {

                            FirendRequestReference.child(receiver_User_id).child(Sender_User_Id)
                                    .child("request_type").setValue("received")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {


                                            if (task.isSuccessful()) {

                                                HashMap<String , String> notificationsData = new HashMap<String, String>();
                                                notificationsData.put("from" , Sender_User_Id);
                                                notificationsData.put("type" , "request");

                                                NotificationsReference.child(receiver_User_id).push().setValue(notificationsData)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                if (task.isSuccessful()){

                                                                    SendFriendRequestButton.setEnabled(true);
                                                                    Current_State = "request_sent";
                                                                    SendFriendRequestButton.setText(R.string.cancel_friend_request);

                                                                    DeclineFriendRequestButton.setVisibility(View.INVISIBLE);
                                                                    DeclineFriendRequestButton.setEnabled(false);
                                                                    //Toast.makeText(Profile_Activity.this , "Your request has Been Sent" , Toast.LENGTH_LONG).show();


                                                                }
                                                            }
                                                        });




                                            }
                                        }
                                    });
                        }
                    }
                });


    }
}
