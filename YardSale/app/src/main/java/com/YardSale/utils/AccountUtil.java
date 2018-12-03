package com.YardSale.utils;

import android.widget.EditText;

import com.YardSale.models.User;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Pattern;

public class AccountUtil {

    /**
     * Checks the validity of an email address.
     * @param email
     * @return
     * true if valid
     * false if invalid
     */
    public static boolean isEmailValid(String email) {
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

    /**
     * Checks the validity of a password and sets errors accordingly.
     * @param password
     * @param passwordEditText
     * @return
     * true if valid
     * false if invalid
     */
    public static boolean isPasswordValid(String password, EditText passwordEditText) {
        //Makes sure password contains an uppercase letter
        if (!password.matches(".*[A-Z].*")) {
            passwordEditText.setError("Password must contain at least one uppercase letter");
            return false;
        }

        //Makes sure password contains a lowercase letter
        else if (!password.matches(".*[a-z].*")) {
            passwordEditText.setError("Password must contain at least one lowercase letter");
            return false;
        }

        //Makes sure password contains a number
        else if (!password.matches(".*[0-9].*")) {
            passwordEditText.setError("Password must contain at least one number");
            return false;
        }

        //Makes sure password contains a special character
        else if (!password.matches(".*[!@#$%^&*()_+=;:'.,<>/?`~].*")) {
            passwordEditText.setError("Password must contain at least one of these special characters: !@#$%^&*()_+=;:'.,<>/?`~");
            return false;
        }

        //Makes sure password is at least 4 characters long
        else if(password.length() <= 4) {
            passwordEditText.setError("Password must be at least 5 characters long");
            return false;
        }

        return true;
    }

    /**
     * Checks the validity of a postal code and sets errors accordingly.
     * @param postalCode
     * @param postalCodeEditText
     * @return
     * true if valid
     * false if invalid
     */
    public static boolean isValidPostalCode(String postalCode, EditText postalCodeEditText){
        if(postalCode.isEmpty())
            postalCodeEditText.setError("Postal Code field is required");

        if(postalCode.length()!=5)
            postalCodeEditText.setError("Please enter a valid postal code");

        return true;
    }

    /**
     *
     * @return the email of the current user.
     */
    public static String getCurrentUserEmail(){
        return getCurrentUser().getEmail();
    }

    /**
     *
     * @return a reference to the current authenticated user
     */
    public static FirebaseUser getCurrentUser(){
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    /**
     *
     * @return a reference to the database
     */
    public static DatabaseReference getDatabaseReference(){
        return FirebaseDatabase.getInstance().getReference();
    }

    /**
     * Updates the user's settings to the values specified and updates the database accordingly.
     *
     * @param email new user email
     * @param password new user password
     * @param zipcode new user postalCode
     */
    public static void updateUserSettings(String email, String password, String zipcode){
        FirebaseUser firebaseUser = getCurrentUser();
        firebaseUser.reauthenticate(EmailAuthProvider.getCredential(email, password));
        writeUser(firebaseUser.getUid(), email, zipcode);
    }

    /**
     * Writes user information to the database when a new user is created or a user updates their settings.
     *
     * @param userId ID for the user
     * @param email Email for the user
     * @param zipcode Zipcode for the user
     */
    public static void writeUser(String userId, String email, String zipcode) {
        User user = new User(email, zipcode);
        getDatabaseReference().child("users").child(userId).setValue(user);
    }
}
