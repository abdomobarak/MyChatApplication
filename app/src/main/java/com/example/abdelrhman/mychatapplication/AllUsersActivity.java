package com.example.abdelrhman.mychatapplication;


import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;


import android.view.LayoutInflater;
import android.view.View;


import android.view.ViewGroup;
import android.widget.TextView;


import com.firebase.ui.database.FirebaseRecyclerAdapter;

import com.firebase.ui.database.FirebaseRecyclerOptions;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;



import de.hdodenhof.circleimageview.CircleImageView;


public class AllUsersActivity extends AppCompatActivity {

    public Toolbar mToolbar;
    public RecyclerView allUsersListRecycleView;
    public DatabaseReference allDatabaseUsersReference;
    public FirebaseRecyclerOptions options;

    public  FirebaseRecyclerAdapter<AllUsers , AllUsersViewHolder> firebaseRecyclerAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users);

        mToolbar = findViewById(R.id.all_Users_app_Bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("All Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        allUsersListRecycleView = findViewById(R.id.All_Users_List_recycleView);
        allUsersListRecycleView.setHasFixedSize(true);
        allUsersListRecycleView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        allDatabaseUsersReference = FirebaseDatabase.getInstance().getReference().child("Users");
        allDatabaseUsersReference.keepSynced(true);




         options = new FirebaseRecyclerOptions.Builder<AllUsers>()
                .setQuery(allDatabaseUsersReference ,AllUsers.class).build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<AllUsers, AllUsersViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull AllUsersViewHolder holder, final int position, @NonNull final AllUsers model) {


                holder.setUser_Name(model.getUser_Name());
                holder.setUser_Status(model.getUser_Status());
                holder.setUser_thumb_Image(getApplicationContext(),model.getUser_thumb_Image());

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

//                        String visit_user_id = getRef(position).getKey();
//                        Intent profileIntent = new Intent(AllUsersActivity.this , Profile_Activity.class);
//                        profileIntent.putExtra("visit_user_id" , visit_user_id);
//                        startActivity(profileIntent);

                    }
                });

            }

            @Override
            public AllUsersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


                View v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.all_users_display_layout , parent , false);


                return new AllUsersViewHolder(v);
            }
        };

                allUsersListRecycleView.setAdapter(firebaseRecyclerAdapter);


    }


         @Override
      protected void onStart() {
         super.onStart();

          firebaseRecyclerAdapter.startListening();

       }



      @Override
     protected void onStop() {
         super.onStop();
         firebaseRecyclerAdapter.stopListening();

         }








    public static class AllUsersViewHolder extends RecyclerView.ViewHolder {


        View mView;


        private AllUsersViewHolder(View itemView) {
            super(itemView);
            mView = itemView;


        }

        private void setUser_Name(String user_Name) {
           final TextView name = mView.findViewById(R.id.all_users_Display_username);
            name.setText(user_Name);

        }

        private void setUser_Status(String user_status) {
           final TextView status = mView.findViewById(R.id.all_Users_Display_Status);
            status.setText(user_status);
        }

        private void setUser_thumb_Image(final Context ctx, final String user_thumb_image) {

           final CircleImageView  thumb_image = mView.findViewById(R.id.all_users_Display_profile_Image);
            Picasso.with(ctx).load(user_thumb_image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.defulat_profile)
                    .into(thumb_image, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {


                            Picasso.with(ctx).load(user_thumb_image).placeholder(R.drawable.defulat_profile).into(thumb_image);
                        }
                    });

        }

    }
}
