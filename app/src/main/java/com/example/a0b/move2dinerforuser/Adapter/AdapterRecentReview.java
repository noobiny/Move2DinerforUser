package com.example.a0b.move2dinerforuser.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.a0b.move2dinerforuser.ActivityTruckInfo;
import com.example.a0b.move2dinerforuser.CustomBitmapPool;
import com.example.a0b.move2dinerforuser.DTO.ReviewListItem;
import com.example.a0b.move2dinerforuser.R;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class AdapterRecentReview extends RecyclerView.Adapter<AdapterRecentReview.ItemHolder> {
    private ArrayList<ReviewListItem> dataList;
    private Context context;


    public AdapterRecentReview(ArrayList<ReviewListItem> dataList, Context context) {
        this.dataList = dataList;
        this.context = context;
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recent_review_row, parent, false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemHolder holder, final int position) {

        Glide.with(context).load(dataList.get(position).getThumbnail()).placeholder(R.drawable.ic_account_circle_black_24dp).error(R.drawable.ic_account_circle_black_24dp)
                .bitmapTransform(new CropCircleTransformation(new CustomBitmapPool())).into(holder.recentreview_row_userthumbnail);
        Glide.with(context).load(dataList.get(position).getTruckThumbnail()).placeholder(R.drawable.loadingimage).error(R.drawable.loadingimage)
                .into(holder.recentreview_row_truckpic);
        holder.recentreview_row_username.setText(dataList.get(position).getUserNick());
        holder.recentreview_row_time.setText(dataList.get(position).getReviewTime());
        holder.recentreview_row_truckname.setText(dataList.get(position).getTruckName());
        holder.recentreview_row_content.setText(dataList.get(position).getContent());

        holder.recentreview_row_entire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, ActivityTruckInfo.class);
                i.putExtra("PrimaryKey", dataList.get(position).getTruckUid());
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class ItemHolder extends RecyclerView.ViewHolder {
        LinearLayout recentreview_row_entire;
        ImageView recentreview_row_userthumbnail, recentreview_row_truckpic;
        TextView recentreview_row_username, recentreview_row_time, recentreview_row_truckname, recentreview_row_content;

        public ItemHolder(View itemView) {
            super(itemView);
            recentreview_row_entire = itemView.findViewById(R.id.recentreview_row_entire);
            recentreview_row_userthumbnail = itemView.findViewById(R.id.recentreview_row_userthumbnail);
            recentreview_row_truckpic = itemView.findViewById(R.id.recentreview_row_truckpic);
            recentreview_row_username = itemView.findViewById(R.id.recentreview_row_username);
            recentreview_row_time = itemView.findViewById(R.id.recentreview_row_time);
            recentreview_row_truckname = itemView.findViewById(R.id.recentreview_row_truckname);
            recentreview_row_content = itemView.findViewById(R.id.recentreview_row_content);
        }
    }
}
