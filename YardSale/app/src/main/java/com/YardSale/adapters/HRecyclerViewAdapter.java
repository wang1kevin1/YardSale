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

public class HRecyclerViewAdapter extends RecyclerView.Adapter<HRecyclerViewAdapter.MyViewHolder> {


    ArrayList<Uri> horizontalList;
    Context context;


    public HRecyclerViewAdapter(ArrayList<Uri> horizontalList, Context context) {
        this.horizontalList = horizontalList;
        this.context = context;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        public MyViewHolder(View view) {
            super(view);
            imageView=(ImageView) view.findViewById(R.id.imageView);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.post_image_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        holder.imageView.setImageURI(horizontalList.get(position));
    }


    @Override
    public int getItemCount()
    {
        return horizontalList.size();
    }
}