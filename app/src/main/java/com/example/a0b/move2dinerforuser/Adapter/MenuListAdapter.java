package com.example.a0b.move2dinerforuser.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.a0b.move2dinerforuser.DTO.MenuListItem;
import com.example.a0b.move2dinerforuser.R;

import java.text.NumberFormat;
import java.util.ArrayList;


public class MenuListAdapter extends RecyclerView.Adapter<MenuListAdapter.MyViewHolder> {
    Context context;
    ArrayList<MenuListItem> properties;

    public MenuListAdapter(Context context, ArrayList<MenuListItem> properties) {
        this.context = context;
        this.properties = properties;
    }

    @Override
    public MenuListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_menu_info, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.tvRecycleFDName.setText(properties.get(position).getFoodName());
        holder.tvRecycleFDPrice.setText(String.valueOf(NumberFormat.getCurrencyInstance().format(properties.get(position).getFoodPrice())));
        holder.tvRecycleFDDescribe.setText(properties.get(position).getFoodDescribe());
    }

    @Override
    public int getItemCount() {
        return properties.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvRecycleFDName, tvRecycleFDPrice, tvRecycleFDDescribe;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvRecycleFDName = (TextView) itemView.findViewById(R.id.tvRecycleFDName);
            tvRecycleFDPrice = (TextView) itemView.findViewById(R.id.tvRecycleFDPrice);
            tvRecycleFDDescribe = (TextView) itemView.findViewById(R.id.tvRecycleFDDescribe);
        }
    }

}
