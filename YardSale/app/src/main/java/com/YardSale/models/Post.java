package com.YardSale.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Post {
    public String uid;
    public String title;
    public String description;
    public int price;
    public int zipcode;
    public String url;

    public Post() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Post(String uid, String title, String description, int price, int zipcode, String url) {
        this.uid = uid;
        this.title = title;
        this.description = description;
        this.price = price;
        this.zipcode = zipcode;
        this.url = url;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("title", title);
        result.put("description", description);
        result.put("price", price);
        result.put("zipcode", zipcode);
        result.put("imageURL", url);

        return result;
    }
}
