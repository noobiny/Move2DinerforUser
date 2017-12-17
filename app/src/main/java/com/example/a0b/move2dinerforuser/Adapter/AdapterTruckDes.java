package com.example.a0b.move2dinerforuser.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
    private FirebaseDatabase database = FirebaseDatabase.getInstance();


    public AdapterTruckDes(ArrayList<ItemTruckDes> items, ArrayList<String> primaryKeys, Context context) {
        this.items = items;
        this.context = context;
        this.primaryKeys = primaryKeys;
        auth = FirebaseAuth.getInstance();
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_truck_des_re, parent, false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(final ItemHolder holder, final int position) {


        holder.cardtruck_name.setText(items.get(position).getTruckName());
        holder.cardtruck_favcount.setText(String.valueOf(items.get(position).getStarCount()));

        if (items.get(position).getOnBusiness() == true) {
            holder.cardtruck_onbusi.setText("영업중");
            holder.cardtruck_onbusi_img.setVisibility(View.VISIBLE);
            holder.cardtruck_offbusi_img.setVisibility(View.INVISIBLE);
        } else {
            holder.cardtruck_onbusi.setText("영업종료");
            holder.cardtruck_onbusi_img.setVisibility(View.INVISIBLE);
            holder.cardtruck_offbusi_img.setVisibility(View.VISIBLE);
        }

        if (items.get(position).getPayCard()) {
            holder.cardtruck_cardpay.setVisibility(View.VISIBLE);
        } else {
            holder.cardtruck_cardpay.setVisibility(View.GONE);
        }

        Glide.with(context)
                .load(items.get(position).getThumbnail())
                .placeholder(R.drawable.loadingimage)
                .error(R.drawable.loadingimage)
                .fitCenter().into(holder.cardtruck_image);

        holder.cardtruck_heart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onStarClicked(database.getReference().child("trucks").child("info").child(primaryKeys.get(position)));
            }
        });

        holder.cardtruck_cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, ActivityTruckInfo.class);
                i.putExtra("PrimaryKey", primaryKeys.get(position));
                context.startActivity(i);
            }
        });

        StringBuilder sb = new StringBuilder();
        if (items.get(position).getTags() != null) {
            for (int i = 0; i < items.get(position).getTags().size(); i++) {
                sb.append("#" + items.get(position).getTags().get(i) + "  ");
            }

//            if (sb.toString().trim().equals("") && items.get(position).getRecentAddress() != null) {
//                String[] splited = items.get(position).getRecentAddress().split(" ");
//                sb.append("#" + splited[2]);
//            }
            holder.cardtruck_tags.setText(sb.toString());
        }


        if (items.get(position).stars.containsKey(auth.getCurrentUser().getUid())) {
            holder.cardtruck_heart.setImageResource(R.drawable.ic_favorite_border_mintfull1_24dp);
        } else {
            holder.cardtruck_heart.setImageResource(R.drawable.ic_favorite_border_mint2_24dp);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ItemHolder extends RecyclerView.ViewHolder {
        CardView cardtruck_cardview;
        ImageView cardtruck_image, cardtruck_offbusi_img, cardtruck_onbusi_img, cardtruck_heart;
        TextView cardtruck_name, cardtruck_favcount, cardtruck_onbusi, cardtruck_tags;
        RelativeLayout cardtruck_cardpay;


        public ItemHolder(View itemView) {
            super(itemView);
            cardtruck_cardview = itemView.findViewById(R.id.cardtruck_cardview);
            cardtruck_image = itemView.findViewById(R.id.cardtruck_image);
            cardtruck_offbusi_img = itemView.findViewById(R.id.cardtruck_offbusi_img);
            cardtruck_onbusi_img = itemView.findViewById(R.id.cardtruck_onbusi_img);
            cardtruck_heart = itemView.findViewById(R.id.cardtruck_heart);
            cardtruck_tags = itemView.findViewById(R.id.cardtruck_tags);

            cardtruck_name = itemView.findViewById(R.id.cardtruck_name);
            cardtruck_favcount = itemView.findViewById(R.id.cardtruck_favcount);
            cardtruck_onbusi = itemView.findViewById(R.id.cardtruck_onbusi);

            cardtruck_cardpay = itemView.findViewById(R.id.cardtruck_cardpay);

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
