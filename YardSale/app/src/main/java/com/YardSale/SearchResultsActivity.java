package com.YardSale;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;

public class SearchResultsActivity extends Activity {

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
            //use the query to search your data somehow
        }
    }

    mDatabase = FirebaseDatabase.getInstance().getReference("user-posts");
    Query  query = mDatabase.orderByChild("zipcode").equalTo(zipcode);
            query .addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if(dataSnapshot.getChildrenCount() > 0){
                System.out.println("count : "+ dataSnapshot.getChildrenCount());
                for(DataSnapshot child : dataSnapshot.getChildren()){
                    value = child.getValue(String.class);
                    tSResult.setText(value);
                }
            }

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    });

}