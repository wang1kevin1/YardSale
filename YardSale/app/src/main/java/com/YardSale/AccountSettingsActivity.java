package com.YardSale;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.YardSale.models.User;
import com.YardSale.utils.AccountUtil;
import com.YardSale.utils.PostUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A login screen that offers login via email/password.
 */
public class AccountSettingsActivity extends AppCompatActivity {

    private static final String TAG = "Sign In";

    /**
     * Firebase Connection
     */
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */

    // UI references.
    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private EditText mConfirmPasswordEditText;
    private EditText mPostalCodeEditText;
    private Button mUpdateSettings;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Text Inputs
        mEmailEditText = findViewById(R.id.editTextEmail);
        mPasswordEditText = findViewById(R.id.editTextPassword);
        mConfirmPasswordEditText = findViewById(R.id.editTextConfirmPassword);
        mPostalCodeEditText = findViewById((R.id.editTextPostalCode));

        mDatabase.child("users").child(AccountUtil.getCurrentUser().getUid()).child("zipcode").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String zipcode = dataSnapshot.getValue(String.class);
                mPostalCodeEditText.setText(zipcode);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mEmailEditText.setText(AccountUtil.getCurrentUser().getEmail());
        mPasswordEditText.setText("*************");

        // Buttons
        mUpdateSettings = findViewById(R.id.buttonUpdateSettings);

        mUpdateSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Set errors to null
                mPasswordEditText.setError(null);
                mEmailEditText.setError(null);

                if(!AccountUtil.isEmailValid(mEmailEditText.getText().toString()))
                    mEmailEditText.setError("Enter a valid email");

                if(AccountUtil.isValidPostalCode(mPostalCodeEditText.getText().toString().trim(), mPostalCodeEditText) && AccountUtil.isPasswordValid(mPasswordEditText.getText().toString().trim(),mPasswordEditText))
                    updateUserSettings(mEmailEditText.getText().toString().trim(), mPasswordEditText.getText().toString(), mPostalCodeEditText.getText().toString().trim());

            }
        });


    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }
    public  boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = { 0, 0 };
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }


    /**
     * Updates the user's settings to the values specified and updates the database accordingly.
     *
     * @param email new user email
     * @param password new user password
     * @param postalCode new user postalCode
     */
    private void updateUserSettings(String email, String password, String postalCode){
        FirebaseUser user = AccountUtil.getCurrentUser();

        if(email != user.getEmail()) {
            user.updateEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                        Toast.makeText(AccountSettingsActivity.this, "Settings Updated",
                                Toast.LENGTH_SHORT).show();

                    else
                        Toast.makeText(AccountSettingsActivity.this, "Update Failed",
                                Toast.LENGTH_SHORT).show();
                }
            });;
        }

        user.updatePassword(password).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                    Toast.makeText(AccountSettingsActivity.this, "Settings Updated",
                            Toast.LENGTH_SHORT).show();

                else
                    Toast.makeText(AccountSettingsActivity.this, "Update Failed",
                            Toast.LENGTH_SHORT).show();
            }
        });

        AccountUtil.writeUser(user.getUid(), email, postalCode);
    }

    private void deleteAccount(){

        // Get values before deleting them
        FirebaseUser currentUser = AccountUtil.getCurrentUser();
        final String userID = currentUser.getUid();
        final DatabaseReference database = AccountUtil.getDatabaseReference();

        // Delete the current user. On successful deletion, purge the database of all user data.
        currentUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                // If we successfully delete the user account, we must now erase all user data.
                if(task.isSuccessful()){
                    // Erase private user data
                    database.child(AccountUtil.USERS).child(userID).removeValue();

                    // To be filled with all postIDs associated with the user
                    final ArrayList<String> userPostIDs = new ArrayList<>();

                    // Reference to all of the user's posts
                    DatabaseReference tempRef = database.child(PostUtil.USER_POSTS).child(userID);

                    // Iterate through each child 'user-post' and retrieve the postID's
                    tempRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                                userPostIDs.add(snapshot.getKey());
                            }
                        }

                        // Handle the case the read is canceled for some reason.
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.d("Reading Posts", "Read Canceled", databaseError.toException());
                        }
                    });

                    // Delete all of the user specific posts
                    tempRef.removeValue();

                    // Delete all of the user's posts from the global posts listings
                    for(String postID : userPostIDs)
                        database.child(PostUtil.POSTS).child(postID).removeValue();

                    Log.d(TAG, "Successful");
                    startActivity(new Intent(com.YardSale.AccountSettingsActivity.this, LoginActivity.class));
                    finish();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "There was a problem deleting the account.", e);
            }
        });
    }


}
