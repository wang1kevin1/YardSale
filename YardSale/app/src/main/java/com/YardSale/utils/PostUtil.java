package com.YardSale.utils;

import com.YardSale.models.Post;
import com.YardSale.models.PostReadException;
import com.google.firebase.database.DatabaseReference;

public class PostUtil {

    public static String POSTS = "posts";
    public static String USER_POSTS = "user-posts";

    /**
     *
     * @param postID lookup post's ID
     * @return a post reference to the specified post.
     * @throws PostReadException If the post is invalid
     */
    public static Post getPostReference(String postID) throws PostReadException {

        DatabaseReference database = AccountUtil.getDatabaseReference();

        String uid = database.child("posts").child(postID).child("uid").getKey();
        String title = database.child("posts").child(postID).child("title").getKey();
        String description = database.child("posts").child(postID).child("description").getKey();

        // Must be non null strings to convert to int
        String sPrice = (database.child("posts").child(postID).child("price").getKey());
        String sPostalCode = (database.child("posts").child(postID).child("zipcode").getKey());

        String price = "-1", postalCode = "-1", url = "", postkey = "";

        if(sPrice != null) {
            price = sPrice;
            throw new PostReadException(postID + " is not a valid post ID");
        }

        if(sPostalCode != null) {
            postalCode = sPostalCode;
            throw new PostReadException(postID + " is not a valid post ID");
        }

        return new Post(uid, title, description, price, postalCode, url, postkey);
    }

}
