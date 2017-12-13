package com.example.a0b.move2dinerforuser.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.a0b.move2dinerforuser.CustomBitmapPool;
import com.example.a0b.move2dinerforuser.DTO.ReviewListItem;
import com.example.a0b.move2dinerforuser.R;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class AdapterTruckReview extends RecyclerView.Adapter<AdapterTruckReview.ItemHolder> {
    private ArrayList<ReviewListItem> dataList = new ArrayList<>();
    private ArrayList<String> reviewKeys = new ArrayList<>();
    private Context context;

    public AdapterTruckReview(ArrayList<ReviewListItem> dataList, ArrayList<String> reviewKeys, Context context) {
        this.dataList = dataList;
        this.reviewKeys = reviewKeys;
        this.context = context;
    }

    @Override
    public AdapterTruckReview.ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_row, parent, false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(AdapterTruckReview.ItemHolder holder, int position) {
        holder.review_row_nick.setText(dataList.get(position).getUserNick());
        holder.review_row_content.setText(dataList.get(position).getContent());
        holder.review_row_time.setText(dataList.get(position).getReviewTime());

        Glide.with(context).load(dataList.get(position).getThumbnail()).placeholder(R.drawable.ic_account_circle_black_24dp).error(R.drawable.ic_account_circle_black_24dp)
                .bitmapTransform(new CropCircleTransformation(new CustomBitmapPool())).into(holder.review_row_thumbnail);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class ItemHolder extends RecyclerView.ViewHolder {
        TextView review_row_nick;
        TextView review_row_content;
        TextView review_row_time;
        ImageView review_row_thumbnail;


        public ItemHolder(View itemView) {
            super(itemView);
            review_row_thumbnail = (ImageView) itemView.findViewById(R.id.review_row_thumbnail);
            review_row_nick = (TextView) itemView.findViewById(R.id.review_row_nick);
            review_row_content = (TextView) itemView.findViewById(R.id.review_row_content);
            review_row_time = (TextView) itemView.findViewById(R.id.review_row_time);
        }
    }
}