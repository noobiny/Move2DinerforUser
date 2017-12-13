package com.example.a0b.move2dinerforuser;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.a0b.move2dinerforuser.Adapter.AdapterPlaceInfo;
import com.example.a0b.move2dinerforuser.DTO.ItemPlaceInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class ActivitySelectLoc extends AppCompatActivity {
    ArrayList<ItemPlaceInfo> items, filteredItems;
    SearchView searchView;
    RecyclerView recyclerView;
    AdapterPlaceInfo adapter;
    FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_loc);
        CustomTitlebar ct = new CustomTitlebar(this, "푸드트럭 Zone");
        ct.iv_arrow_back.setVisibility(View.VISIBLE);
        initView();
        setPlaceList();
    }

    private void initView() {
        items = new ArrayList<>();
        filteredItems = new ArrayList<>();

        recyclerView = (RecyclerView) findViewById(R.id.recycler_LocaList);
        adapter = new AdapterPlaceInfo(items, filteredItems, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setQueryHint("위치 검색!");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                adapter.getFilter().filter(s);
                return false;
            }
        });
        return true;
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

    void setPlaceList() {


        Query query = firebaseDatabase.getReference().child("placeinfo");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ItemPlaceInfo itemPlaceInfo = snapshot.getValue(ItemPlaceInfo.class);
                    items.add(new ItemPlaceInfo(itemPlaceInfo.getPlaceName(), itemPlaceInfo.getLatitude(), itemPlaceInfo.getLongitude()));

                }
                adapter.invalidate();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
}
