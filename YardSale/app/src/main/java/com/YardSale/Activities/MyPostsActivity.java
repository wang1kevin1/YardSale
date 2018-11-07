package com.YardSale;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MyPostsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_posts);

        FloatingActionButton FormButton = findViewById(R.id.FormButton);
        FormButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MyPostsActivity.this,
                        CreatePostActivity.class);
                startActivity(myIntent);
            }
        });
    }

}
