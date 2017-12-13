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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.a0b.move2dinerforuser.ActivityTruckInfo;
import com.example.a0b.move2dinerforuser.DTO.ReviewListItem;
import com.example.a0b.move2dinerforuser.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class AdapterMyReview extends RecyclerView.Adapter<AdapterMyReview.ItemHolder> {
    private ArrayList<ReviewListItem> dataList;
    private ArrayList<String> revPriKeys;
    private Context context;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    public AdapterMyReview(ArrayList<ReviewListItem> dataList, ArrayList<String> revPriKeys, Context context) {
        this.dataList = dataList;
        this.revPriKeys = revPriKeys;
        this.context = context;
    }

    @Override
    public AdapterMyReview.ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.myreview_row, parent, false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(AdapterMyReview.ItemHolder holder, final int position) {

        Glide.with(context).load(dataList.get(position).getTruckThumbnail()).placeholder(R.drawable.loadingimage)
                .error(R.drawable.loadingimage).into(holder.myreview_row_thumbnail);
        holder.myreview_row_truckName.setText(dataList.get(position).getTruckName());
        holder.myreview_row_content.setText(dataList.get(position).getContent());
        holder.myreview_row_time.setText(dataList.get(position).getReviewTime());

        holder.myreview_row_entire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, ActivityTruckInfo.class);
                i.putExtra("PrimaryKey", dataList.get(position).getTruckUid());
                context.startActivity(i);
            }
        });

        holder.myreview_row_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteContent(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    private void deleteContent(final int position) {

        database.getReference().child("reviews").child(auth.getCurrentUser().getUid()).child(revPriKeys.get(position)).removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                notifyDataSetChanged();
                Toast.makeText(context, "삭제가 완료되었습니다", Toast.LENGTH_SHORT).show();
            }
        });
    }

    class ItemHolder extends RecyclerView.ViewHolder {
        TextView myreview_row_truckName;
        TextView myreview_row_content;
        TextView myreview_row_time;
        LinearLayout myreview_row_del;
        ImageView myreview_row_thumbnail;
        LinearLayout myreview_row_entire;

        public ItemHolder(View itemView) {
            super(itemView);
            myreview_row_entire=(LinearLayout)itemView.findViewById(R.id.myreview_row_entire);
            myreview_row_thumbnail = (ImageView) itemView.findViewById(R.id.myreview_row_thumbnail);
            myreview_row_truckName = (TextView) itemView.findViewById(R.id.myreview_row_truckName);
            myreview_row_content = (TextView) itemView.findViewById(R.id.myreview_row_content);
            myreview_row_time = (TextView) itemView.findViewById(R.id.myreview_row_time);
            myreview_row_del = (LinearLayout) itemView.findViewById(R.id.myreview_row_del);
        }
    }
}