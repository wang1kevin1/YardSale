package com.YardSale;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.YardSale.adapters.PostRecyclerAdapter;
import com.YardSale.models.Post;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;


public class MainSearchActivity extends BaseActivity {

    //Cardview displays
    PostRecyclerAdapter cardAdapter;
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
        setContentView(R.layout.activity_main_search);

        //get userId
        userId = getUid();
        //Get Firebase Refs
        mStorage = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.keepSynced(true);

        //Recycler view cardview list
        postRecyclerView = (RecyclerView) findViewById(R.id.vertical_recycler_view);

        mDatabase.child("posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mPostData = new ArrayList<>();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    Post aPost = postSnapshot.getValue(Post.class);
                    mPostData.add(aPost);

                    String postKey = postSnapshot.getKey();

                    Log.v("myPost", "post image:" +
                            mStorage.child("post-images").child(postKey));
                }
                cardAdapter = new PostRecyclerAdapter(mPostData, getApplication());

                LinearLayoutManager layoutmanager = new LinearLayoutManager(MainSearchActivity.this,
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