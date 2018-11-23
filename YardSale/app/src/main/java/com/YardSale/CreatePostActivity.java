package com.YardSale;


import android.content.ClipData;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.YardSale.adapters.CreatePostRecyclerAdapter;
import com.YardSale.models.Post;
import com.YardSale.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreatePostActivity extends BaseActivity {

    private static final int GALLERY_REQUEST_CODE = 2;
    private static final String REQUIRED = "Required";
    private static final String TAG = "CreatePostActivity";

    // Initialize Buttons
    Button mImageBtn;
    Button mPostBtn;
    //Image selection
    String imageEncoded;
    List<String> imagesEncodedList;
    ArrayList<Uri> mArrayUri;
    Uri mImageUri;


    //Image display
    CreatePostRecyclerAdapter imageAdapter;
    RecyclerView imageRecyclerView;

    //Initialize EditText input items
    EditText mPostTitle, mPostPrice, mPostZipcode, mPostDesc;
    // Initialize Firebase References
    StorageReference mStorage;
    DatabaseReference mDatabase;
    // Server time stamp
    String mTimestamp;
    String key;

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

        //get image recycler view stuff
        imageRecyclerView = (RecyclerView) findViewById(R.id.horizontal_recycler_view);

        //Get Firebase Refs
        mStorage = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        //Get timestamp
        mTimestamp = ServerValue.TIMESTAMP.toString();

        //Choose gallery image
        mImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"),
                        GALLERY_REQUEST_CODE);
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

    // Select images to add to post
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try{
            if(requestCode == GALLERY_REQUEST_CODE &&
                    resultCode == RESULT_OK &&
                    data != null ) {
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                imagesEncodedList = new ArrayList<>();
                mArrayUri = new ArrayList<>();
                mArrayUri.clear();

                if (data.getData() != null) { //on Single image selected
                    mImageUri = data.getData();
                    // Get the cursor
                    Cursor cursor = getContentResolver().query(mImageUri, filePathColumn, null, null, null);
                    // Move to first row
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    imageEncoded = cursor.getString(columnIndex);
                    cursor.close();

                    mArrayUri.add(mImageUri);
                    Log.v("CreatePostActivity", "Selected Images: 1");
                } else { //on Multiple image selected
                    if(data.getClipData() != null) {
                        ClipData mClipData = data.getClipData();

                        for (int i = 0; i < mClipData.getItemCount(); i++) {

                            ClipData.Item item = mClipData.getItemAt(i);
                            Uri uri = item.getUri();
                            mArrayUri.add(uri);
                            // Get the cursor
                            Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
                            // Move to first row
                            cursor.moveToFirst();

                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                            imageEncoded = cursor.getString(columnIndex);
                            imagesEncodedList.add(imageEncoded);
                            cursor.close();

                        }
                        Log.v("CreatePostActivity", "Selected Images " + mArrayUri.size());

                    }
                }
                // fill in recycler view with images
                imageAdapter = new CreatePostRecyclerAdapter(mArrayUri, getApplication());

                LinearLayoutManager hLayoutManager =
                        new LinearLayoutManager(CreatePostActivity.this,
                                LinearLayoutManager.HORIZONTAL, false);
                imageRecyclerView.setLayoutManager(hLayoutManager);
                imageRecyclerView.setAdapter(imageAdapter);
            } else {
                Toast.makeText(this, "You haven't picked Image", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    // grabs post data and create a new post
    public void NewPost() {
        final String title = mPostTitle.getText().toString().trim();
        final String price = mPostPrice.getText().toString();
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

        // get post identification key
        key = mDatabase.child("posts").push().getKey();

        // adds post to firebase database
        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        User user = dataSnapshot.getValue(User.class);

                        if (user == null) {
                            // User is null, error out
                            Log.e(TAG, "User " + userId + " is unexpectedly null");
                            Toast.makeText(CreatePostActivity.this,
                                    "Error: could not fetch user.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // sends new post to firebase
                            storePost(userId,
                                    title,
                                    description,
                                    price,
                                    zipcode);
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

        StorageReference imageRef = mStorage.child("post-images").child(key);
        int upload = 0;

        while (upload < mArrayUri.size()) {
            if(mArrayUri.get(upload) != null) {
                imageRef.child(Integer.toString(upload)).putFile(mArrayUri.get(upload));
                upload++;
            }
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

    // Stores new post into Firebase Database
    private void storePost(String userId, String title, String description,
                           String price, String zipcode) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        Post post = new Post(userId, title, description, price, zipcode);
        Map<String, Object> postValues = post.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/posts/" + key, postValues);
        childUpdates.put("/user-posts/" + userId + "/" + key, postValues);

        mDatabase.updateChildren(childUpdates);
    }


}