package com.example.a0b.move2dinerforuser;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.example.a0b.move2dinerforuser.Adapter.AdapterTruckDes;
import com.example.a0b.move2dinerforuser.DTO.ItemTruckDes;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ActivityTruckDes extends AppCompatActivity {
    private RecyclerViewEmptySupport recyclerView;
    private ArrayList<ItemTruckDes> itemTruckDes = new ArrayList<>();
    private AdapterTruckDes adapter;
    private ArrayList<String> primaryKeys = new ArrayList<>();
    private Spinner spinnersort;
    private AdView adView2;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_truck_des);
        new CustomTitlebar(this, "트럭 모두 보기");

        initView();

        spinnersort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    //등록순 정렬
                    setTruckIntro(0);

                } else if (position == 1) {
                    //별점순 정렬
                    setTruckIntro(1);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        setTruckIntro(0);
    }

    private void initView() {

        adView2 = (AdView) findViewById(R.id.adView2);
        spinnersort = (Spinner) findViewById(R.id.truckdes_spinnersort);
        recyclerView = (RecyclerViewEmptySupport) findViewById(R.id.recycler_TruckDes);
        adapter = new AdapterTruckDes(itemTruckDes,primaryKeys,this);

        GridLayoutManager lmanager = new GridLayoutManager(this,2);
        lmanager.setAutoMeasureEnabled(true);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2,0,false,0));
        recyclerView.setEmptyView(findViewById(R.id.empty_TruckInfo));
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(lmanager);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView2.loadAd(adRequest);
    }


    private void setTruckIntro(final int sorting) {
        //데이터베이스에서 객체형태로 가져와서 아이템(어댑터)에 추가 - 옵저버 패턴
        database.getReference().child("trucks").child("info").orderByKey().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                itemTruckDes.clear();
                primaryKeys.clear();  //primaryKeys 는 데이터 한꺼번에 관리 할때 필요해(push할때 자동생성되는 키)
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    itemTruckDes.add(snapshot.getValue(ItemTruckDes.class));
                    primaryKeys.add(snapshot.getKey());
                }

                if (sorting == 1) { //별점순 정렬일때 플래그
                        // 그냥 키값을 객체에 넣어서 정렬후 다시 빼내기 ->
                        for (int i = 0; i < itemTruckDes.size(); i++) {
                            itemTruckDes.get(i).setKeys(primaryKeys.get(i));
                        }

                        Collections.sort(itemTruckDes, new SortByStarCount());
                    primaryKeys.clear();
                    for (int i = 0; i < itemTruckDes.size(); i++) {
                        primaryKeys.add(itemTruckDes.get(i).getKeys());
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return false;
        }
        return false;
    }

    private class SortByStarCount implements Comparator<ItemTruckDes> {
        @Override
        public int compare(ItemTruckDes o1, ItemTruckDes o2) {
            return o1.starCount < o2.starCount ? 1 : o1.starCount > o2.starCount ? -1 : 0;
        }
    }

}
