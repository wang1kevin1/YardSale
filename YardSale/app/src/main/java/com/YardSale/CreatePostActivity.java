package com.YardSale;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.YardSale.models.Post;
import com.YardSale.models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class CreatePostActivity extends BaseActivity{
    private static final int GALLERY_REQUEST_CODE = 2;
    private static final String REQUIRED = "Required";
    private static final String TAG = "CreatePostActivity";

    // Initialize Buttons
    ImageButton mImageBtn;
    Button mPostBtn;
    // Initialize gallery items
    Uri filepath;
    //Initialize EditText input items
    EditText mPostTitle, mPostPrice, mPostZipcode, mPostDesc;
    // Initialize Firebase References
    StorageReference mStorage;
    DatabaseReference mDatabase;
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
        mDatabase = FirebaseDatabase.getInstance().getReference();
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
                UploadImage();
            }
        });
    }

    public void NewPost() {
        final String title = mPostTitle.getText().toString().trim();
        final String price = mPostPrice.getText().toString().substring(1);
        final String zipcode = mPostZipcode.getText().toString();
        final String description = mPostDesc.getText().toString();

        final String userId = getUid();



        // Title is required
        if (TextUtils.isEmpty(title)) {
            mPostTitle.setError(REQUIRED);
            return;
        }
        // Price is required
        if (TextUtils.isEmpty(price)) {
            mPostPrice.setError(REQUIRED);
            return;
        }
        // Zipcode is required
        if (TextUtils.isEmpty(zipcode)) {
            mPostZipcode.setError(REQUIRED);
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

        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        User user = dataSnapshot.getValue(User.class);

                        // [START_EXCLUDE]
                        if (user == null) {
                            // User is null, error out
                            Log.e(TAG, "User " + userId + " is unexpectedly null");
                            Toast.makeText(CreatePostActivity.this,
                                    "Error: could not fetch user.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // Create new post
                            createNewPost(userId, title, description,
                                    Integer.parseInt(price), Integer.parseInt(zipcode));
                            Toast.makeText(getApplicationContext(), "Successfully Posted!",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // Finish this Activity, back to the stream
                        setEditingEnabled(true);
                        finish();
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                        setEditingEnabled(true);
                    }
                });
    }

    private void UploadImage() {
        if(filepath != null)
        {
            StorageReference imageRef = mStorage.child("images/" + filepath.getLastPathSegment());
            imageRef.putFile(filepath)
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(CreatePostActivity.this, "Failed " +
                                    e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                            // ...
                        }
                    });
        }
    }

    // disables editing to prevent multiple posts on button spam
    private void setEditingEnabled(boolean enabled) {
        mPostTitle.setEnabled(enabled);
        mPostPrice.setEnabled(enabled);
        mPostZipcode.setEnabled(enabled);
        mPostDesc.setEnabled(enabled);
        mImageBtn.setEnabled(enabled);
        mPostBtn.setEnabled(enabled);
    }

    private void createNewPost(String userId, String title, String description, int price, int zipcode) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        String key = mDatabase.child("posts").push().getKey();
        Post post = new Post(userId, title, description, price, zipcode);
        Map<String, Object> postValues = post.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/posts/" + key, postValues);
        childUpdates.put("/user-posts/" + userId + "/" + key, postValues);

        mDatabase.updateChildren(childUpdates);
    }

    // shows image on button space after selection
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Image from gallery result
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK
                && data != null && data.getData() != null ) {
            filepath = data.getData();
            mImageBtn.setImageURI(filepath);
        }
    }
}