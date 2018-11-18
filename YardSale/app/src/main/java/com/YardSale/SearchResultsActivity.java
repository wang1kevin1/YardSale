package com.YardSale;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

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



public class SearchResultsActivity extends Activity {
    TextView tSResult = (TextView) findViewById(R.id.text_view_id);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.search); need to add a layout to run search activity
        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            DatabaseReference mDatabase;
            //use the query to search your data somehow
            mDatabase = FirebaseDatabase.getInstance().getReference("posts");
            Query query2 = mDatabase.orderByChild("zipcode").equalTo(query);
            query2.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.getChildrenCount() > 0){
                        System.out.println("count : "+ dataSnapshot.getChildrenCount());
                        for(DataSnapshot child : dataSnapshot.getChildren()){
                            String value = child.getValue(String.class);
                            tSResult.setText(value);
                        }
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
        });
     }
    }
}