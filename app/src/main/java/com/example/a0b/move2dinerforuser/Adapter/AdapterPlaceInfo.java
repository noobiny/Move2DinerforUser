package com.example.a0b.move2dinerforuser.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;

import com.example.a0b.move2dinerforuser.ActivityMaps;
import com.example.a0b.move2dinerforuser.DTO.ItemPlaceInfo;
import com.example.a0b.move2dinerforuser.R;

import java.util.ArrayList;


public class AdapterPlaceInfo extends RecyclerView.Adapter<AdapterPlaceInfo.ItemHolder> implements Filterable {

    private ArrayList<ItemPlaceInfo> items, filteredItems;
    private Context context;
    private PlaceFilter filter;

    public AdapterPlaceInfo(ArrayList<ItemPlaceInfo> items, ArrayList<ItemPlaceInfo> filteredItems, Context context) {
        this.items = items;
        this.filteredItems = filteredItems;
        this.context = context;
        filter = new PlaceFilter(this);
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_place_info, parent, false);
        return new ItemHolder(view);
    }


    @Override
    public void onBindViewHolder(final ItemHolder holder, final int position) {

        holder.BtnPlaceName.setText(filteredItems.get(position).getPlaceName());
        holder.BtnPlaceName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, ActivityMaps.class);
                i.putExtra("IsFromSelLoc", true);
                i.putExtra("Latitude", items.get(position).getLatitude());
                i.putExtra("Longitude", items.get(position).getLongitude());
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return filteredItems.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    class ItemHolder extends RecyclerView.ViewHolder {
        Button BtnPlaceName;

        public ItemHolder(View itemView) {
            super(itemView);
            BtnPlaceName = (Button) itemView.findViewById(R.id.btnPlaceInfo);
        }
    }

    public void invalidate() {
        filteredItems.clear();
        filteredItems.addAll(items);
        notifyDataSetChanged();
    }

    private class PlaceFilter extends Filter {
        AdapterPlaceInfo adapter;

        PlaceFilter(AdapterPlaceInfo adapter) {
            super();
            this.adapter = adapter;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            filteredItems.clear();
            FilterResults results = new FilterResults();
            if (constraint == null || constraint.toString().length() == 0) {
                filteredItems.addAll(items);
            } else {
                for (ItemPlaceInfo item : items) {
                    if (item.getPlaceName().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        filteredItems.add(item);
                    }
                }

            }
            results.values = filteredItems;
            results.count = filteredItems.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            adapter.notifyDataSetChanged();
        }
    }
}
