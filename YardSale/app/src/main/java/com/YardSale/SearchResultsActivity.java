package com.YardSale;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import com.YardSale.adapters.MyPostRecyclerAdapter;
import com.YardSale.models.Post;
import com.YardSale.models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;


public class SearchResultsActivity extends Activity {
    TextView tSResult = (TextView) findViewById(R.id.text_view_id);
    ArrayList<Post> mPostData;
    MyPostRecyclerAdapter cardAdapter;
    RecyclerView postRecyclerView;
    StorageReference mStorage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_main_search);
        Log.v("myPost","\n\n\n\n\n\n\n\n\n\n1\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        //IGNORE
        /*if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            DatabaseReference mDatabase;
            *mDatabase = FirebaseDatabase.getInstance().getReference();
            System.out.println(query);
            mDatabase.child("posts").orderByChild("zipcode").equalTo(query).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Post post = dataSnapshot.getChildren().iterator().next().getValue(Post.class);
                    tSResult.setText(post.title);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });*/

        // If user enters query into search bar
        Log.v("myPost","\n\n\n\n\n\n\n\n\n\n1\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Log.v("myPost", "1");
            DatabaseReference mDatabase;
            //get reference to posts in database
            mDatabase = FirebaseDatabase.getInstance().getReference();
            mStorage = FirebaseStorage.getInstance().getReference();
            /* Query sorts and filters the data at database location so only a subset of the child
            data is included. Orders the data by zipcode anpd checks if its equal to the users
            query.*/
            Query query2 = mDatabase.child("posts").orderByChild("zipcode").equalTo(query);
            // Attach a listener to read the data at posts reference
            query2.addValueEventListener(new ValueEventListener() {
                @Override
                //Iterate through data snapshot that match query
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mPostData = new ArrayList<>();
                    if(dataSnapshot.getChildrenCount() > 0){
                        System.out.println("count : "+ dataSnapshot.getChildrenCount());
                        for(DataSnapshot child : dataSnapshot.getChildren()){ //child
                            Post post = child.getValue(Post.class); //child
                            mPostData.add(post);

                            //String postKey = postSnapshot.getKey();

                            //Log.v("myPost", "post image:" +
                              //      mStorage.child("post-images").child(postKey));
                            //Display result in textView (temporary)
                            //tSResult.setText(post);4
                        }
                    }
                    cardAdapter = new MyPostRecyclerAdapter(mPostData, getApplication());

                    LinearLayoutManager layoutmanager = new LinearLayoutManager(SearchResultsActivity.this,
                            LinearLayoutManager.VERTICAL, false);
                    postRecyclerView.setLayoutManager(layoutmanager);
                    postRecyclerView.setAdapter(cardAdapter);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w("myPost", "loadPost:onCancelled", databaseError.toException());

                }
        });
     }
    }
}