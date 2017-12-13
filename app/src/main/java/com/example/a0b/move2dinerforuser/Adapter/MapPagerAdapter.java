package com.example.a0b.move2dinerforuser.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.a0b.move2dinerforuser.ActivityTruckInfo;
import com.example.a0b.move2dinerforuser.DTO.ItemTruckDes;
import com.example.a0b.move2dinerforuser.DTO.SalesInfoListItem;
import com.example.a0b.move2dinerforuser.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class MapPagerAdapter extends PagerAdapter {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private ArrayList<ItemTruckDes> mResources;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();


    public MapPagerAdapter(Context mContext, ArrayList<ItemTruckDes> mResources) {
        this.mContext = mContext;
        this.mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mResources = mResources;
    }


    //뷰페이저를 리프레쉬하려면 이 메소드 써야한대요
    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return mResources.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == (object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View itemView = mLayoutInflater.inflate(R.layout.viewpager_mapinfo, container, false);

        ImageView mapinfo_thumbnail = (ImageView) itemView.findViewById(R.id.mapinfo_thumbnail);
        TextView mapinfo_index = (TextView) itemView.findViewById(R.id.mapinfo_index);
        TextView mapinfo_truckname = (TextView) itemView.findViewById(R.id.mapinfo_truckname);
        TextView mapinfo_distance = (TextView) itemView.findViewById(R.id.mapinfo_distance);

        Glide.with(mContext).load(mResources.get(position).getThumbnail()).into(mapinfo_thumbnail);
        mapinfo_index.setText(String.valueOf(position + 1) + ". ");
        mapinfo_truckname.setText(mResources.get(position).getTruckName());

        Integer distance = mResources.get(position).getDistance();
        if (distance >= 1000) {
            mapinfo_distance.setText(String.valueOf(distance/1000) + "km " + String.valueOf(distance%1000) + "m");

        } else {
            mapinfo_distance.setText(String.valueOf(distance) + "m");
        }
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String primaryKey = mResources.get(position).getKeys();

                Query truckInfoQuery = database.getReference().child("trucks").child("info").child(primaryKey);
                truckInfoQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Intent i = new Intent(mContext, ActivityTruckInfo.class);
                        ItemTruckDes itemTruckDes = dataSnapshot.getValue(ItemTruckDes.class);
                        i.putExtra("PrimaryKey", primaryKey);
                        mContext.startActivity(i);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        container.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }


}
