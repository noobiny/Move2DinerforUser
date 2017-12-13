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


        holder.TxtTruckName.setText(items.get(position).getTruckName());
        Glide.with(context)
                .load(items.get(position).getThumbnail())
                .placeholder(R.drawable.loadingimage)
                .error(R.drawable.loadingimage)
                .fitCenter().into(holder.ImgTruck);

        //좋아요 한 순서로 아이템 저장되게하고 클릭하면 이동할수있게..
        holder.layout.setOnClickListener(new View.OnClickListener() {
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

        CardView layout;
        ImageView ImgTruck;
        TextView TxtTruckName;

        //가독성이 떨어지는데 어댑터를 처음에 동시에 공유해서 해서 이렇게 돼버렸음.; 사실 어댑터 분리하는게 맞아요

        public ItemHolder(View itemView) {
            super(itemView);

            layout = (CardView) itemView.findViewById(R.id.lay_parent);
            ImgTruck = (ImageView) itemView.findViewById(R.id.imgTruck);
            TxtTruckName = (TextView) itemView.findViewById(R.id.txtTruckName);
        }

    }
}
