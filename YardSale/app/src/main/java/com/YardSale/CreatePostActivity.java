package com.YardSale;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class CreatePostActivity extends AppCompatActivity {

    // Global variables
    EditText PostTitle, PostPrice, PostZipcode, PostDescription;
    Button CreateButton;

    @Override
    public void onCreate(Bundle saved) {
        super.onCreate(saved);
        setContentView(R.layout.activity_create_post);

        // Initialize activity layout
        PostTitle = findViewById(R.id.PostTitle);
        PostPrice = findViewById(R.id.PostPrice);
        PostZipcode = findViewById(R.id.PostZipcode);
        PostDescription = findViewById(R.id.PostDescription);
        CreateButton = findViewById(R.id.CreateButton);
    }

    public void onClickCreatePost(View v) {
        // Get data entry
        String title = PostTitle.getText().toString();
        String price = PostPrice.getText().toString();
        String zipcode = PostZipcode.getText().toString();
        String description = PostDescription.getText().toString();

        // send data values to Firebase
    }
}