package com.YardSale.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.YardSale.R;

import java.util.ArrayList;

public class CreatePostRecyclerAdapter extends RecyclerView.Adapter<CreatePostRecyclerAdapter.CreatePostViewHolder> {


    ArrayList<Uri> horizontalList;
    Context context;


    public CreatePostRecyclerAdapter(ArrayList<Uri> horizontalList, Context context) {
        this.horizontalList = horizontalList;
        this.context = context;
    }


    public class CreatePostViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;

        public CreatePostViewHolder(View view) {
            super(view);
            imageView=(ImageView) view.findViewById(R.id.imageView);
        }
    }

    @Override
    public CreatePostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.post_image_item, parent, false);

        return new CreatePostViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final CreatePostViewHolder holder, final int position) {

        holder.imageView.setImageURI(horizontalList.get(position));
    }


    @Override
    public int getItemCount()
    {
        return horizontalList.size();
    }
}