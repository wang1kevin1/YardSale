package com.YardSale.utils;

import com.YardSale.models.Post;
import com.YardSale.models.PostReadException;
import com.google.firebase.database.DatabaseReference;

public class PostUtil {


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
        String url = database.child("posts").child(postID).child("url").getKey();

        // Must be non null strings to convert to int
        String sPrice = (database.child("posts").child(postID).child("price").getKey());
        String sPostalCode = (database.child("posts").child(postID).child("zipcode").getKey());

        int price = -1, postalCode = -1;

        if(sPrice != null) {
            price = Integer.parseInt(sPrice);
            throw new PostReadException(postID + " is not a valid post ID");
        }

        if(sPostalCode != null) {
            postalCode = Integer.parseInt(sPostalCode);
            throw new PostReadException(postID + " is not a valid post ID");
        }

        return new Post(uid, title, description, price, postalCode);
    }

}
