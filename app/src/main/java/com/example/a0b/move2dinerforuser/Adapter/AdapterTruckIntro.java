package com.example.a0b.move2dinerforuser.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.a0b.move2dinerforuser.ActivityTruckInfo;
import com.example.a0b.move2dinerforuser.DTO.ItemTruckDes;
import com.example.a0b.move2dinerforuser.R;

import java.util.ArrayList;


public class AdapterTruckIntro extends RecyclerView.Adapter<AdapterTruckIntro.ItemHolder> {
    ArrayList<ItemTruckDes> items;
    ArrayList<String> primaryKeys;
    Context context;

    public AdapterTruckIntro(ArrayList<ItemTruckDes> items, ArrayList<String> primaryKeys, Context context) {
        this.items = items;
        this.context = context;
        this.primaryKeys = primaryKeys;
    }


    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_truck_info_re, parent, false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(final ItemHolder holder, final int position) {


        holder.item_truck_info_truckname.setText(items.get(position).getTruckName());
        Glide.with(context)
                .load(items.get(position).getThumbnail())
                .placeholder(R.drawable.loadingimage)
                .error(R.drawable.loadingimage)
                .fitCenter().into(holder.item_truck_info_thumbnail);

        holder.item_truck_favcount.setText(String.valueOf(items.get(position).getStarCount()));

        StringBuilder sb = new StringBuilder();
        if (items.get(position).getTags() != null) {
            for (int i = 0; i < items.get(position).getTags().size(); i++) {
                sb.append("#" + items.get(position).getTags().get(i) + "  ");
            }

//            if (sb.toString().trim().equals("") && items.get(position).getRecentAddress() != null) {
//                String[] splited = items.get(position).getRecentAddress().split(" ");
//                sb.append("#" + splited[2]);
//            }
            holder.item_truck_info_tag.setText(sb.toString());
        }

        holder.item_truck_info_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, ActivityTruckInfo.class);
                i.putExtra("PrimaryKey", primaryKeys.get(position));
                context.startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ItemHolder extends RecyclerView.ViewHolder {

        CardView item_truck_info_layout;
        ImageView item_truck_info_thumbnail;
        TextView item_truck_info_truckname, item_truck_info_tag, item_truck_favcount;


        public ItemHolder(View itemView) {
            super(itemView);

            item_truck_info_layout = (CardView) itemView.findViewById(R.id.item_truck_info_layout);
            item_truck_info_thumbnail = (ImageView) itemView.findViewById(R.id.item_truck_info_thumbnail);
            item_truck_info_truckname = (TextView) itemView.findViewById(R.id.item_truck_info_truckname);
            item_truck_favcount = (TextView) itemView.findViewById(R.id.item_truck_favcount);
            item_truck_info_tag = (TextView) itemView.findViewById(R.id.item_truck_info_tag);
        }

    }
}
