package com.example.a0b.move2dinerforuser;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.MenuItem;

import com.example.a0b.move2dinerforuser.Adapter.AdapterTruckRecycle;
import com.example.a0b.move2dinerforuser.DTO.ItemTruckDes;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class ActivityMyTruck extends AppCompatActivity {

    private RecyclerViewEmptySupport recycler_myfavAll;
    private ArrayList<ItemTruckDes> favlist = new ArrayList<>();
    private ArrayList<String> primaryKeys = new ArrayList<>();
    private AdapterTruckRecycle favAdapter;

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_truck);
        new CustomTitlebar(this, "내가 찜한 트럭");

        initView();
        BaseApplication.getInstance().progressON(ActivityMyTruck.this, "로딩중");
        printFavList();

    }

    private void initView() {
        recycler_myfavAll = (RecyclerViewEmptySupport) findViewById(R.id.recycler_myfavAll);
        recycler_myfavAll.setLayoutManager(new LinearLayoutManager(this));
        favAdapter = new AdapterTruckRecycle(primaryKeys, this, favlist);
        recycler_myfavAll.setAdapter(favAdapter);
        recycler_myfavAll.setEmptyView(findViewById(R.id.empty_fav));

    }

    //좋아요 데이터 가져오기
    private void printFavList() {
        Query favQuery = database.getReference().child("trucks").child("info").orderByChild("stars");
        favQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                favlist.clear();
                primaryKeys.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.child("stars").getValue() != null && snapshot.child("stars").getValue().toString().contains(auth.getCurrentUser().getUid())) {
                        primaryKeys.add(snapshot.getKey());
                        favlist.add(snapshot.getValue(ItemTruckDes.class));
                    }
                }
                BaseApplication.getInstance().progressOFF();
                favAdapter.notifyDataSetChanged();
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
}
