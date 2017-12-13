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
import com.example.a0b.move2dinerforuser.DTO.ItemTruckDes;
import com.example.a0b.move2dinerforuser.R;

import java.util.ArrayList;

public class AdapterTruckRecycle extends RecyclerView.Adapter<AdapterTruckRecycle.ItemHolder> {

    ArrayList<String> primaryKeys;
    Context context;
    ArrayList<ItemTruckDes> itemTruckDes;

    public AdapterTruckRecycle(ArrayList<String> primaryKeys, Context context, ArrayList<ItemTruckDes> itemTruckDes) {
        this.primaryKeys = primaryKeys;
        this.context = context;
        this.itemTruckDes = itemTruckDes;

    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_truck_saleinfo, parent, false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemHolder holder, final int position) {
        holder.truck_row_name.setText(itemTruckDes.get(position).getTruckName());



        Integer starCount = itemTruckDes.get(position).getStarCount();

        if (starCount != null)
            holder.fav_count.setText(String.valueOf(starCount));
        else {
            holder.fav_count.setText("0");
        }
        String recentAddress = itemTruckDes.get(position).getRecentAddress();
        if (recentAddress != null)
            holder.truckrow_place.setText(recentAddress);
        else
            holder.truckrow_place.setText("영업 정보가 없습니다");

        Integer distance = itemTruckDes.get(position).getDistance();

        if (distance != null) {
            if (distance >= 1000) {
                holder.truckrow_distance.setText(distance / 1000 + "km ");
            } else {
                holder.truckrow_distance.setText(distance + "m");
            }
        } else {
            holder.truckrow_distance.setText("");
        }

        Glide.with(context)
                .load(itemTruckDes.get(position).getThumbnail())
                .placeholder(R.drawable.loadingimage)
                .error(R.drawable.loadingimage)
                .fitCenter().into(holder.truckrow_thumbnail);

        if(itemTruckDes.get(position).getPayCard()){
            holder.truckrow_paycard_off.setVisibility(View.INVISIBLE);
            holder.truckrow_paycard_on.setVisibility(View.VISIBLE);
        }
        else{
            holder.truckrow_paycard_off.setVisibility(View.VISIBLE);
            holder.truckrow_paycard_on.setVisibility(View.INVISIBLE);
        }
        if (itemTruckDes.get(position).getOnBusiness()) {
            holder.truckrow_onbusi_off.setVisibility(View.INVISIBLE);
            holder.truckrow_onbusi_on.setVisibility(View.VISIBLE);
        } else {
            holder.truckrow_onbusi_off.setVisibility(View.VISIBLE);
            holder.truckrow_onbusi_on.setVisibility(View.INVISIBLE);
        }

        holder.truckrow_parent.setOnClickListener(new View.OnClickListener() {
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
        return itemTruckDes.size();

    }

    public class ItemHolder extends RecyclerView.ViewHolder {
        ImageView truckrow_thumbnail;
        TextView truck_row_name;
        LinearLayout truckrow_parent;
        TextView truckrow_place, fav_count;
        TextView truckrow_distance, truckrow_onbusi_off, truckrow_onbusi_on, truckrow_paycard_off, truckrow_paycard_on;

        public ItemHolder(View itemView) {
            super(itemView);
            truckrow_parent = (LinearLayout) itemView.findViewById(R.id.truckrow_parent);
            truckrow_place = (TextView) itemView.findViewById(R.id.truckrow_place);
            truckrow_distance = (TextView) itemView.findViewById(R.id.truckrow_distance);
            truckrow_onbusi_off = (TextView) itemView.findViewById(R.id.truckrow_onbusi_off);
            truckrow_onbusi_on = (TextView) itemView.findViewById(R.id.truckrow_onbusi_on);
            truckrow_paycard_off = (TextView) itemView.findViewById(R.id.truckrow_paycard_off);
            truckrow_paycard_on = (TextView) itemView.findViewById(R.id.truckrow_paycard_on);
            fav_count = (TextView) itemView.findViewById(R.id.fav_count);
            truckrow_thumbnail = (ImageView) itemView.findViewById(R.id.truckrow_thumbnail);
            truck_row_name = (TextView) itemView.findViewById(R.id.truck_row_name);
        }
    }
}
