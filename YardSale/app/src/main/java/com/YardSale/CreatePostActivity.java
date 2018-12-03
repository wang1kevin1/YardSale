package com.YardSale;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CreatePostActivity extends BaseActivity {

    private static final int REQUEST_TAKE_PHOTO = 1;
    private static final String REQUIRED = "Required";
    private static final String TAG = "CreatePostActivity";

    // Initialize Buttons
    Button mImageBtn;
    Button mPostBtn;
    //Image selection
    ArrayList<Uri> mArrayUri;
    ArrayList<String> mImageIndex;
    Uri mImageUri;
    String mCurrentPhotoPath;


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
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // Ensure that there's a camera activity to handle the intent
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                        // Error occurred while creating the File...
                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        mImageUri = FileProvider.getUriForFile(CreatePostActivity.this,
                                "com.YardSale.android.fileprovider",
                                photoFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
                        startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                    }
                }
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

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    // Select images to add to post
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_TAKE_PHOTO &&
                resultCode == RESULT_OK) {
            mArrayUri = new ArrayList<>();
            mArrayUri.add(mImageUri);
            // fill in recycler view with images
            imageAdapter = new CreatePostRecyclerAdapter(mArrayUri, getApplication());

            LinearLayoutManager hLayoutManager =
                    new LinearLayoutManager(CreatePostActivity.this,
                            LinearLayoutManager.HORIZONTAL, false);
            imageRecyclerView.setLayoutManager(hLayoutManager);
            imageRecyclerView.setAdapter(imageAdapter);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    // grabs post data and create a new post
    public void NewPost() {
        final String title = mPostTitle.getText().toString().trim();
        final String price = mPostPrice.getText().toString();
        final String zipcode = mPostZipcode.getText().toString();
        final String description = mPostDesc.getText().toString().trim();

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
        // Image is required
        if (mArrayUri == null) {
            Toast.makeText(this, "Image Required", Toast.LENGTH_LONG).show();
            return;
        }

        // Disable button so there are no multi-posts
        setEditingEnabled(false);

        Toast.makeText(this, "Posting...", Toast.LENGTH_SHORT).show();

        // get post identification key
        key = mDatabase.child("posts").push().getKey();

        // upload post images to firebase storage
        int upload = 0;
        mImageIndex = new ArrayList<>();

        while (upload < mArrayUri.size()) {
            if(mArrayUri.get(upload) != null) {
                mImageIndex.add(Integer.toString(upload));
                upload++;
            }
        }

        final StorageReference imageRef =
                mStorage.child("post-images")
                        .child(key);
        //first image added only
        imageRef.putFile(mArrayUri.get(0)).
                addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        // Download file From Firebase Storage
                        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri downloadPhotoUrl) {
                                Log.v(TAG, "downloadPhotoUrl: " + downloadPhotoUrl);

                                // adds post to firebase database
                                storePost(userId,
                                        title,
                                        description,
                                        price,
                                        zipcode,
                                        downloadPhotoUrl.toString(),
                                        key);
                                mDatabase.child("post-images").child(key).setValue(downloadPhotoUrl.toString());
                                Toast.makeText(getApplicationContext(),
                                        "Successfully Posted!",
                                        Toast.LENGTH_SHORT).show();
                                setEditingEnabled(true);
                                finish();
                            }
                        });
                    }
                });

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
                           String price, String zipcode, String url, String postkey) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        Post post = new Post(userId, title, description, price, zipcode, url, postkey);
        Map<String, Object> postValues = post.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/posts/" + key, postValues);
        childUpdates.put("/user-posts/" + userId + "/" + key, postValues);
        childUpdates.put("/zipcode-posts/" + zipcode + "/" + key, postValues);

        mDatabase.updateChildren(childUpdates);
    }


}