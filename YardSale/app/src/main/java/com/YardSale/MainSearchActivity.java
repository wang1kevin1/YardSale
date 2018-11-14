package com.YardSale;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.content.Intent;


public class MainSearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_search);
    }

    // On button click goes to search activity
    public void goToBrowse(View v) {
        Intent myIntent = new Intent(MainSearchActivity.this,
                SearchResultsActivity.class);
        startActivity(myIntent);
    }

    // On button click goes to MyPostsActivity
    public void goToPosts(View v) {
        Intent myIntent = new Intent(MainSearchActivity.this,
                MyPostsActivity.class);
        startActivity(myIntent);
    }

    // On button click goes to account settings activity
    public void goToAccount(View v) {
        //Add intent
    }
}