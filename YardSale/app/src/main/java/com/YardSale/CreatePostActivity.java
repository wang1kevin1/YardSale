package com.YardSale;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

public class CreatePostActivity extends AppCompatActivity {

    // Text variables for input
    EditText PostTitle, PostPrice, PostZipcode, PostDescription;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        // Locate text input in layout
        PostTitle = findViewById(R.id.PostTitle);
        PostPrice = findViewById(R.id.PostPrice);
        PostZipcode = findViewById(R.id.PostZipcode);
        PostDescription = findViewById(R.id.PostDescription);
    }

    // On button click, grabs form input and stores in database.
    // Then returns to MyPostsActivity
    public void onClickCreatePost(View v) {
        // Get data entry
        String title = PostTitle.getText().toString();
        String price = PostPrice.getText().toString();
        String zipcode = PostZipcode.getText().toString();
        String description = PostDescription.getText().toString();

        // send data values to Firebase

        // When processes done, return to My Posts Activity
        Intent myIntent = new Intent(CreatePostActivity.this,
                MyPostsActivity.class);
        startActivity(myIntent);
    }
}