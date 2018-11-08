package com.YardSale;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.YardSale.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Pattern;

/**
 * A login screen that offers login via email/password.
 */
public class CreateAccountActivity extends AppCompatActivity {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

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
    private Button mCreateAccountButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        // Text Inputs
        mEmailEditText = findViewById(R.id.editTextEmail);
        mPasswordEditText = findViewById(R.id.editTextPassword);
        mConfirmPasswordEditText = findViewById(R.id.editTextConfirmPassword);
        mPostalCodeEditText = findViewById((R.id.editTextPostalCode));

        // Buttons
        mCreateAccountButton = findViewById(R.id.buttonCreateAccount);

        mCreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Set errors to null
                mPasswordEditText.setError(null);
                mEmailEditText.setError(null);

                if(mPasswordEditText.getText().toString().equals(mConfirmPasswordEditText.getText().toString()))
                    createAccount(mEmailEditText.getText().toString().trim(), mPasswordEditText.getText().toString(), mPostalCodeEditText.getText().toString().trim());

                else {
                    mPasswordEditText.setError("Passwords do not match");
                    mConfirmPasswordEditText.setError("Passwords do not match");
                }

            }
        });

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    private void createAccount(String email, String password, final String postalCode){

        if(email.isEmpty())
            mEmailEditText.setError("Email field is required.");

        else if(!isEmailValid(email))
            mEmailEditText.setError("Please enter a valid email");


        else if(isPasswordValid(password) && isValidPostalCode(postalCode)){

            //Attempt to create an account
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success");
                                //FirebaseUser user = mAuth.getCurrentUser();
                                //writeNewUser(user.getUid(), user.getEmail(), postalCode);
                                Intent myIntent = new Intent(CreateAccountActivity.this,
                                        MainSearchActivity.class);
                                startActivity(myIntent);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(CreateAccountActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private boolean isEmailValid(String email) {
        //Basic form for email
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;

        //If email doesn't match form, it will return false, otherwise true.
        //Does not check for validity of actual email or domain.
        return pat.matcher(email).matches();
    }

    private boolean isPasswordValid(String password) {
        //Makes sure password contains an uppercase letter
        if (!password.matches(".*[A-Z].*")) {
            mPasswordEditText.setError("Password must contain at least one uppercase letter");
            return false;
        }

        //Makes sure password contains a lowercase letter
        else if (!password.matches(".*[a-z].*")) {
            mPasswordEditText.setError("Password must contain at least one lowercase letter");
            return false;
        }

        //Makes sure password contains a number
        else if (!password.matches(".*[0-9].*")) {
            mPasswordEditText.setError("Password must contain at least one number");
            return false;
        }

        //Makes sure password contains a special character
        else if (!password.matches(".*[!@#$%^&*()_+=;:'.,<>/?`~].*")) {
            mPasswordEditText.setError("Password must contain at least one of these special characters: !@#$%^&*()_+=;:'.,<>/?`~");
            return false;
        }

        //Makes sure password is at least 4 characters long
        else if(password.length() <= 4) {
            mPasswordEditText.setError("Password must be at least 5 characters long");
            return false;
        }

        return true;
    }

    private boolean isValidPostalCode(String postalCode){
        if(postalCode.isEmpty())
            mPostalCodeEditText.setError("Postal Code field is required");

        if(postalCode.length()!=5)
            mPostalCodeEditText.setError("Please enter a valid postal code");

        return true;
    }

    private void writeNewUser(String userId, String email, String postalCode) {
        User user = new User(email, postalCode);
        mDatabase.child("users").child(userId).setValue(user);
    }
}

