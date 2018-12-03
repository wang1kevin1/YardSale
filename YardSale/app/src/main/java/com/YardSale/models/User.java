package com.YardSale.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;


@IgnoreExtraProperties
public class User {

    private String EMAIL;
    private String ZIPCODE;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String email, String zipcode) {
        this.EMAIL = email;
        this.ZIPCODE = zipcode;
    }

    public String getEMAIL() { return EMAIL; }
    public void setEMAIL(String email) {this.EMAIL = email; }
    public String getZIPCODE() { return ZIPCODE; }
    public void setZIPCODE(String zipcode) {this.ZIPCODE = zipcode; }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("email", EMAIL);
        result.put("zipcode", ZIPCODE);

        return result;
    }
}
