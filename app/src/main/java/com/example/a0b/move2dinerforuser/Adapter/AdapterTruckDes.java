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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import java.util.ArrayList;


public class AdapterTruckDes extends RecyclerView.Adapter<AdapterTruckDes.ItemHolder> {
    private ArrayList<ItemTruckDes> items = new ArrayList<>();
    private ArrayList<String> primaryKeys = new ArrayList<>();
    private Context context;
    private FirebaseAuth auth;
    private FirebaseDatabase database=FirebaseDatabase.getInstance();


    public AdapterTruckDes(ArrayList<ItemTruckDes> items, ArrayList<String> primaryKeys, Context context) {
        this.items = items;
        this.context = context;
        this.primaryKeys = primaryKeys;
        auth = FirebaseAuth.getInstance();
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_truck_des, parent, false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(final ItemHolder holder, final int position) {
        holder.TxtTruckName.setText(items.get(position).getTruckName());
        holder.tv_card_favcount.setText(String.valueOf(items.get(position).getStarCount()));

        if(items.get(position).getOnBusiness()==true){
            holder.truckrow_onbusi_off.setVisibility(View.INVISIBLE);
            holder.truckrow_onbusi_on.setVisibility(View.VISIBLE);
        }else{
            holder.truckrow_onbusi_off.setVisibility(View.VISIBLE);
            holder.truckrow_onbusi_on.setVisibility(View.INVISIBLE);
        }

        if(items.get(position).getPayCard()){
            holder.truckrow_paycard_off.setVisibility(View.INVISIBLE);
            holder.truckrow_paycard_on.setVisibility(View.VISIBLE);
        }
        else{
            holder.truckrow_paycard_off.setVisibility(View.VISIBLE);
            holder.truckrow_paycard_on.setVisibility(View.INVISIBLE);
        }

        Glide.with(context)
                .load(items.get(position).getThumbnail())
                .placeholder(R.drawable.loadingimage)
                .error(R.drawable.loadingimage)
                .fitCenter().into(holder.ImgTruck);

        holder.iv_card_favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onStarClicked(database.getReference().child("trucks").child("info").child(primaryKeys.get(position)));
            }
        });

        holder.layParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, ActivityTruckInfo.class);
                i.putExtra("PrimaryKey", primaryKeys.get(position));
                context.startActivity(i);
            }
        });

        if (items.get(position).stars.containsKey(auth.getCurrentUser().getUid())) {
            holder.iv_card_favorite.setImageResource(R.drawable.ic_favorite_border_mintfull1_24dp);
        } else {
            holder.iv_card_favorite.setImageResource(R.drawable.ic_favorite_border_mint2_24dp);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ItemHolder extends RecyclerView.ViewHolder {
        CardView layParent;
        ImageView ImgTruck, iv_card_favorite,n;
        TextView TxtTruckName, tv_card_favcount,truckrow_onbusi_off,truckrow_onbusi_on,truckrow_paycard_off,truckrow_paycard_on;
        public ItemHolder(View itemView) {
            super(itemView);
            layParent = (CardView) itemView.findViewById(R.id.lay_parent);
            ImgTruck = (ImageView) itemView.findViewById(R.id.imgTruck);
            TxtTruckName = (TextView) itemView.findViewById(R.id.txtTruckName);
            truckrow_onbusi_off = (TextView) itemView.findViewById(R.id.truckrow_onbusi_off);
            truckrow_onbusi_on = (TextView) itemView.findViewById(R.id.truckrow_onbusi_on);
            truckrow_paycard_off = (TextView) itemView.findViewById(R.id.truckrow_paycard_off);
            truckrow_paycard_on = (TextView) itemView.findViewById(R.id.truckrow_paycard_on);
            iv_card_favorite = (ImageView) itemView.findViewById(R.id.iv_card_favorite);
            tv_card_favcount = (TextView) itemView.findViewById(R.id.tv_card_favcount);
        }
    }

    public void onStarClicked(DatabaseReference postRef) {
        postRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                ItemTruckDes itemTruckDes = mutableData.getValue(ItemTruckDes.class);
                if (itemTruckDes == null) {
                    return Transaction.success(mutableData);
                }
                if (itemTruckDes.stars.containsKey(auth.getCurrentUser().getUid())) {
                    // Unstar the post and remove self from stars
                    itemTruckDes.starCount = itemTruckDes.starCount - 1;
                    itemTruckDes.stars.remove(auth.getCurrentUser().getUid());
                } else {
                    // Star the post and add self to stars
                    itemTruckDes.starCount = itemTruckDes.starCount + 1;
                    itemTruckDes.stars.put(auth.getCurrentUser().getUid(), true);
                }

                // Set value and report transaction success
                mutableData.setValue(itemTruckDes);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
            }
        });
    }
}
