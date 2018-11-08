package com.YardSale.models;

import com.google.firebase.database.IgnoreExtraProperties;


@IgnoreExtraProperties
public class User {

    private String mEmail;
    private String mPostalCode;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String email, String postalCode) {
        mEmail = email;
        mPostalCode = postalCode;
    }
}
