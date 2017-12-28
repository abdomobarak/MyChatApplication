package com.example.abdelrhman.mychatapplication;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment {

    private RecyclerView myFriendList;
    private DatabaseReference FriendsReference;
    private DatabaseReference usersReference;
    private FirebaseAuth mAuth;
    String online_user_id;

    public FirebaseRecyclerOptions options;


    private View myMainView;
    FirebaseRecyclerAdapter<Friends ,FriendsViewHolder > firebaseRecyclerAdapter;


    public FriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myMainView = inflater.inflate(R.layout.fragment_friends, container, false);

        myFriendList = myMainView.findViewById(R.id.friends_list);
        mAuth = FirebaseAuth.getInstance();
        online_user_id = mAuth.getCurrentUser().getUid();

        FriendsReference = FirebaseDatabase.getInstance().getReference().child("Friends").child(online_user_id);
        usersReference = FirebaseDatabase.getInstance().getReference().child("Users");
        myFriendList.setLayoutManager( new LinearLayoutManager(getContext()));

        options = new FirebaseRecyclerOptions.Builder<AllUsers>()
                .setQuery(FriendsReference ,AllUsers.class).build();



         firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final FriendsViewHolder holder, int position, @NonNull Friends model) {

                holder.setDate(model.getDate());

                String list_user_id = getRef(position).getKey();
                usersReference.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        String userName = dataSnapshot.child("user_name").getValue().toString();
                        String thumb_Image = dataSnapshot.child("user_thumb_image").getValue().toString();

                        FriendsViewHolder.setUserName(userName);
                        FriendsViewHolder.setThumbImage(getContext(),thumb_Image);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }

            @Override
            public FriendsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.all_users_display_layout , parent , false);


                return new FriendsViewHolder(v);
            }
        };

        myFriendList.setAdapter(firebaseRecyclerAdapter);





        return myMainView;


    }




    public static class FriendsViewHolder extends RecyclerView.ViewHolder{


      static  View mView;

        public FriendsViewHolder(View itemView) {
            super(itemView);

            mView =itemView;
        }


        public void setDate (String date){

            TextView sincefriendDate =mView.findViewById(R.id.all_Users_Display_Status);
            sincefriendDate.setText(date);
        }

        public static void setUserName(String UserName){

            TextView userNameDisplay = mView.findViewById(R.id.all_users_Display_username);
            userNameDisplay.setText(UserName);
        }

        public static void setThumbImage(final Context ctx, final String thumb_image) {


            final CircleImageView thumb_images = mView.findViewById(R.id.all_users_Display_profile_Image);
            Picasso.with(ctx).load(thumb_image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.defulat_profile)
                    .into(thumb_images, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {


                            Picasso.with(ctx).load(thumb_image).placeholder(R.drawable.defulat_profile).into(thumb_images);
                        }
                    });
        }
    }
}
