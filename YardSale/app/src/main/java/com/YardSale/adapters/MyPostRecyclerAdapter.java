package com.YardSale.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.YardSale.R;
import com.YardSale.models.Post;
import com.YardSale.utils.AccountUtil;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class MyPostRecyclerAdapter extends RecyclerView.Adapter<MyPostRecyclerAdapter.PostCardViewHolder> {

    private ArrayList<Post> postList;
    Context context;
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    StorageReference mStorage = FirebaseStorage.getInstance().getReference();

    public MyPostRecyclerAdapter(ArrayList<Post> postList, Context context) {
        this.postList = postList;
        this.context = context;
    }

    public class PostCardViewHolder extends RecyclerView.ViewHolder {
        TextView vTitle;
        ImageButton vImage;
        TextView vPrice;
        TextView vLocation;
        TextView vDescription;
        Button vDeletePost;

        public PostCardViewHolder(View view) {
            super(view);
            vTitle = (TextView) view.findViewById(R.id.txtTitle);
            vImage =  (ImageButton) view.findViewById(R.id.cardImage);
            vPrice = (TextView)  view.findViewById(R.id.txtPrice);
            vLocation = (TextView)  view.findViewById(R.id.txtLocation);
            vDescription = (TextView) view.findViewById(R.id.txtDescription);
            vDeletePost = view.findViewById(R.id.CardViewButton);
        }
    }

    @Override
    public PostCardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.post_cardview_item, parent, false);

        return new PostCardViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PostCardViewHolder holder, final int position) {
        Post mypost = postList.get(position);
        final String postkey = mypost.getPOSTKEY();
        final String myUID = AccountUtil.getCurrentUser().getUid();
        final String url = mypost.getURL();
        final String zipcode = mypost.getZIPCODE();
        holder.vTitle.setText(mypost.getTITLE());
        holder.vPrice.setText(mypost.getPRICE());
        holder.vLocation.setText(mypost.getZIPCODE());
        holder.vDescription.setText(mypost.getDESCRIPTION());
        Glide.with(context).load(mypost.getURL()).into(holder.vImage);
        holder.vImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(url), "image/*");
                context.startActivity(intent);
            }
        });
        holder.vDeletePost.setText("Delete Post");
        holder.vDeletePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), "Hold to Delete", Toast.LENGTH_LONG).show();
            }
        });
        holder.vDeletePost.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
               mDatabase.child("post-images").child(postkey).removeValue();
               mDatabase.child("posts").child(postkey).removeValue();
               mDatabase.child("user-posts").child(myUID).child(postkey).removeValue();
               mDatabase.child("zipcode-posts").child(zipcode).child(postkey).removeValue();
               mStorage.child("post-images").child(postkey).delete();
               postList.remove(position);

               return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        if (postList == null) {
            return 0;
        } else {
            return postList.size();
        }
    }
}

