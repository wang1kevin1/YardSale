package com.YardSale;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class CreatePostActivity extends AppCompatActivity {
    // Initialize Buttons
    ImageButton imageBtn;
    Button postBtn;
    // Initialize gallery items
    static final int GALLERY_REQUEST_CODE = 2;
    Uri uri = null;
    //Initialize EditText input items
    EditText postTitle, postPrice, postZipcode, postDescription;
    // Initialize Firebase References
    StorageReference storageRef;
    DatabaseReference databaseRef;
    FirebaseDatabase database;
    DatabaseReference mDatabaseUsers;
    FirebaseAuth mAuth;
    FirebaseUser mCurrentUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        //Get buttons
        postBtn = findViewById(R.id.CreateButton);
        imageBtn = findViewById(R.id.AttachButton);
        //Get text
        postTitle = findViewById(R.id.PostTitle);
        postPrice = findViewById(R.id.PostPrice);
        postZipcode = findViewById(R.id.PostZipcode);
        postDescription = findViewById(R.id.PostDescription);
        //Get Firebase Refs
        storageRef = FirebaseStorage.getInstance().getReference();
        databaseRef = database.getInstance().getReference().child("posts");
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("uri");

        //Choose gallery image
        imageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
            }
        });
        //Upload to Firebase
        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(CreatePostActivity.this, "Posting…", Toast.LENGTH_LONG).show();
                final String PostTitle = postTitle.getText().toString().trim();
                final String PostPrice = postPrice.getText().toString().trim();
                final String PostZipcode = postZipcode.getText().toString().trim();
                final String PostDesc = postDescription.getText().toString();
                // do a check for empty fields
                if (!TextUtils.isEmpty(PostDesc) && !TextUtils.isEmpty(PostTitle)){
                    StorageReference filepath = storageRef.child("post_images").child(uri.getLastPathSegment());
                    filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            @SuppressWarnings("VisibleForTests")
                            //getting the post image download url
                            final Task<Uri> downloadUrl = taskSnapshot.getStorage().getDownloadUrl();
                            Toast.makeText(getApplicationContext(), "Successfully Posted!", Toast.LENGTH_SHORT).show();
                            final DatabaseReference newPost = databaseRef.push();
                            //adding post contents to database reference
                            mDatabaseUsers.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    newPost.child("title").setValue(PostTitle);
                                    newPost.child("price").setValue(PostPrice);
                                    newPost.child("zipcode").setValue(PostZipcode);
                                    newPost.child("description").setValue(PostDesc);
                                    newPost.child("imageUrl").setValue(downloadUrl.toString());
                                    newPost.child("username").setValue(dataSnapshot.child("name").getValue())
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    // When processes done, return to My Posts Activity
                                                    Intent myIntent = new Intent(CreatePostActivity.this,
                                                            MyPostsActivity.class);
                                                    startActivity(myIntent);
                                                }
                                            });
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }});
                        }});
                }}});
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Image from gallery result
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK){
            uri = data.getData();
            imageBtn.setImageURI(uri);
        }
    }
}