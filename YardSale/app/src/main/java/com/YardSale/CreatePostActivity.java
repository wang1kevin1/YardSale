package com.YardSale;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class CreatePostActivity extends BaseActivity{
    private static final int GALLERY_REQUEST_CODE = 2;
    private static final String REQUIRED = "Required";

    // Initialize Buttons
    ImageButton mImageBtn;
    Button mPostBtn;
    // Initialize gallery items
    Uri uri = null;
    //Initialize EditText input items
    EditText mPostTitle, mPostPrice, mPostZipcode, mPostDesc;
    // Initialize Firebase References
    StorageReference mStorage;
    DatabaseReference mDatabase;
    DatabaseReference mDatabaseUsers;
    FirebaseAuth mAuth;
    FirebaseUser mCurrentUser;
    // Server time stamp
    String mTimestamp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        //Get buttons
        mPostBtn = findViewById(R.id.CreateButton);
        mImageBtn = findViewById(R.id.AttachButton);
        //Get text
        mPostTitle = findViewById(R.id.PostTitle);
        mPostPrice = findViewById(R.id.PostPrice);
        mPostZipcode = findViewById(R.id.PostZipcode);
        mPostDesc = findViewById(R.id.PostDescription);
        //Get Firebase Refs
        mStorage = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("posts");
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("uri");
        //Get timestamp
        mTimestamp = ServerValue.TIMESTAMP.toString();
        //Choose gallery image
        mImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
            }
        });
        //Upload to Firebase
        mPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NewPost();
            }
        });
    }

    public void NewPost() {
        final String title = mPostTitle.getText().toString().trim();
        final int price = Integer.parseInt(mPostPrice.getText().toString().substring(1));
        final int zipcode = Integer.parseInt(mPostZipcode.getText().toString());
        final String description = mPostDesc.getText().toString();

        // final String userId = getUid();

        // Title is required
        if (TextUtils.isEmpty(title)) {
            mPostTitle.setError(REQUIRED);
            return;
        }
        // Description is required
        if (TextUtils.isEmpty(description)) {
            mPostDesc.setError(REQUIRED);
            return;
        }

        // Disable button so there are no multi-posts
        setEditingEnabled(false);
        Toast.makeText(this, "Posting...", Toast.LENGTH_SHORT).show();



        // do a check for empty fields
        if (!TextUtils.isEmpty(description) && !TextUtils.isEmpty(title)){
            StorageReference filepath = mStorage.child("post_images").child(uri.getLastPathSegment());
            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    @SuppressWarnings("VisibleForTests")
                    //getting the post image download url
                    final Task<Uri> downloadUrl = taskSnapshot.getStorage().getDownloadUrl();
                    Toast.makeText(getApplicationContext(), "Successfully Posted!", Toast.LENGTH_SHORT).show();
                    final DatabaseReference newPost = mDatabase.push();
                    //adding post contents to database reference
                    mDatabaseUsers.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            newPost.child("timestamp").setValue(mTimestamp);
                            newPost.child("title").setValue(title);
                            newPost.child("price").setValue(price);
                            newPost.child("zipcode").setValue(zipcode);
                            newPost.child("description").setValue(description);
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
        }
    }

    private void setEditingEnabled(boolean enabled) {
        mPostTitle.setEnabled(enabled);
        mPostPrice.setEnabled(enabled);
        mPostZipcode.setEnabled(enabled);
        mPostDesc.setEnabled(enabled);
        mImageBtn.setEnabled(enabled);
        mPostBtn.setEnabled(enabled);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Image from gallery result
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK){
            uri = data.getData();
            mImageBtn.setImageURI(uri);
        }
    }
}