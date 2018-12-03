package com.YardSale.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Post {
    private String UID;
    private String TITLE;
    private String DESCRIPTION;
    private String PRICE;
    private String ZIPCODE;
    private String URL;

    public Post() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Post(String uid, String title, String description,
                String price, String zipcode, String url) {
        this.UID = uid;
        this.TITLE = title;
        this.DESCRIPTION = description;
        this.PRICE = price;
        this.ZIPCODE = zipcode;
        this.URL = url;
    }

    public String getUID() {
        return UID;
    }
    public void setUID(String uid) {this.UID = uid; }
    public String getTITLE() {
        return TITLE;
    }
    public void setTITLE(String title) {this.TITLE = title; }
    public String getDESCRIPTION() {
        return DESCRIPTION;
    }
    public void setDESCRIPTION(String desc) {this.DESCRIPTION = desc; }
    public String getPRICE() {
        return PRICE;
    }
    public void setPRICE(String price) {this.PRICE = price; }
    public String getZIPCODE() {
        return ZIPCODE;
    }
    public void setZIPCODE(String zipcode) {this.ZIPCODE = zipcode; }
    public String getURL() {
        return URL;
    }
    public void setURL(String url) {this.URL = url; }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", UID);
        result.put("title", TITLE);
        result.put("description", DESCRIPTION);
        result.put("price", PRICE);
        result.put("zipcode", ZIPCODE);
        result.put("url", URL);

        return result;
    }
}
