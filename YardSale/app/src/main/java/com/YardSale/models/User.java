package com.YardSale.models;

import com.google.firebase.database.IgnoreExtraProperties;


@IgnoreExtraProperties
public class User {

    public String email;
    public int zipcode;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String email, int zipcode) {
        this.email = email;
        this.zipcode = zipcode;
    }
}
