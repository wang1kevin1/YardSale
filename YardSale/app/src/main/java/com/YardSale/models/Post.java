package com.YardSale.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Post {
    public String UID;
    public String TITLE;
    public String DESCRIPTION;
    public int PRICE;
    public int POSTAL_CODE;


    public Post() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Post(String uid, String title, String description, int price, int postalCode) {
        this.UID = uid;
        this.TITLE = title;
        this.DESCRIPTION = description;
        this.PRICE = price;
        this.POSTAL_CODE = postalCode;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", UID);
        result.put("title", TITLE);
        result.put("description", DESCRIPTION);
        result.put("price", PRICE);
        result.put("zipcode", POSTAL_CODE);

        return result;
    }
}
