package com.YardSale.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.YardSale.R;
import com.YardSale.models.Post;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MyPostRecyclerAdapter extends RecyclerView.Adapter<MyPostRecyclerAdapter.PostCardViewHolder> {

    private ArrayList<Post> postList;
    Context context;
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    public MyPostRecyclerAdapter(ArrayList<Post> postList, Context context) {
        this.postList = postList;
        this.context = context;
    }

    public class PostCardViewHolder extends RecyclerView.ViewHolder {
        TextView vTitle;
        ImageView vImage;
        TextView vPrice;
        TextView vLocation;
        TextView vDescription;
        Button vContactSeller;

        public PostCardViewHolder(View view) {
            super(view);
            vTitle = (TextView) view.findViewById(R.id.txtTitle);
            vImage =  (ImageView) view.findViewById(R.id.cardImage);
            vPrice = (TextView)  view.findViewById(R.id.txtPrice);
            vLocation = (TextView)  view.findViewById(R.id.txtLocation);
            vDescription = (TextView) view.findViewById(R.id.txtDescription);
            vContactSeller = view.findViewById(R.id.ContactButton);
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
        final String title = mypost.getTITLE();
        final String uid = mypost.getUID();
        holder.vTitle.setText(mypost.getTITLE());
        holder.vPrice.setText(mypost.getPRICE());
        holder.vLocation.setText(mypost.getZIPCODE());
        holder.vDescription.setText(mypost.getDESCRIPTION());
        Glide.with(context).load(mypost.getURL()).into(holder.vImage);

        holder.vContactSeller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri data = Uri.parse("mailto:" + "sellerEmail" +
                        "?subject=" + "Interested in your YardSale Post" +
                        "&body=" + "I'm interested in your listing for: " +
                        title + ".");
                intent.setData(data);
                context.startActivity(intent);
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
