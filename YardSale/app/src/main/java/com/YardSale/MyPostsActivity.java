package com.YardSale;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.YardSale.adapters.MyPostRecyclerAdapter;
import com.YardSale.models.Post;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class MyPostsActivity extends BaseActivity {

    //Cardview displays
    MyPostRecyclerAdapter cardAdapter;
    RecyclerView postRecyclerView;
    ArrayList<Post> mPostData;
    ArrayList<Uri> mArrayUri;
    // Initialize Firebase References
    StorageReference mStorage;
    DatabaseReference mDatabase;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_posts);

        //get userId
        userId = getUid();
        //Get Firebase Refs
        mStorage = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.keepSynced(true);

        //Recycler view cardview list
        postRecyclerView = (RecyclerView) findViewById(R.id.vertical_recycler_view);
        mArrayUri = new ArrayList<>();
        mPostData = new ArrayList<>();

        //Create post button
        FloatingActionButton FormButton = findViewById(R.id.FormButton);
        FormButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MyPostsActivity.this,
                        CreatePostActivity.class);
                startActivity(myIntent);
            }
        });

        mDatabase.child("user-posts").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    Post aPost = postSnapshot.getValue(Post.class);
                    mPostData.add(aPost);
                    Log.v("posttitle", "post title:");
                }
                cardAdapter = new MyPostRecyclerAdapter(mPostData, getApplication());

                LinearLayoutManager layoutmanager = new LinearLayoutManager(MyPostsActivity.this,
                        LinearLayoutManager.VERTICAL, false);
                postRecyclerView.setLayoutManager(layoutmanager);
                postRecyclerView.setAdapter(cardAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.v("read error", databaseError.getMessage());
            }
        });
    }

}
