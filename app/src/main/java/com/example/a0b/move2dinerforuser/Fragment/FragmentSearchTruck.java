package com.example.a0b.move2dinerforuser.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.example.a0b.move2dinerforuser.ActivityMaps;
import com.example.a0b.move2dinerforuser.ActivityOnSaleTruck;
import com.example.a0b.move2dinerforuser.ActivitySelectLoc;
import com.example.a0b.move2dinerforuser.ActivityTruckDes;
import com.example.a0b.move2dinerforuser.ActivityTruckInfo;
import com.example.a0b.move2dinerforuser.DTO.ItemTruckList;
import com.example.a0b.move2dinerforuser.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class FragmentSearchTruck extends Fragment implements View.OnClickListener {

    Button BtnFindBesides, BtnFindLoaction, btnViewAllTruck, btnViewOnSaleTruck;
    AutoCompleteTextView autoView_listAll;
    ArrayList<ItemTruckList> itemsTruckList = new ArrayList<>();
    ArrayList<String> truckNames = new ArrayList<>();
    FirebaseDatabase database=FirebaseDatabase.getInstance();

    public FragmentSearchTruck() {
    }

    public static FragmentSearchTruck newInstance() {
        return new FragmentSearchTruck();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search_truck, container, false);
        BtnFindBesides = (Button) rootView.findViewById(R.id.btnFindByMyBesides);
        btnViewOnSaleTruck = (Button) rootView.findViewById(R.id.btnViewOnSaleTruck);
        BtnFindLoaction = (Button) rootView.findViewById(R.id.btnFindByLocation);
        btnViewAllTruck = (Button) rootView.findViewById(R.id.btnViewAllTruck);
        autoView_listAll = (AutoCompleteTextView) rootView.findViewById(R.id.autoview_listAll);
        autoView_listAll.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selected = (String) adapterView.getItemAtPosition(i);
                for (ItemTruckList item : itemsTruckList) {
                    if (item.getTruckName().equals(selected)) {
                        Intent intent = new Intent(getActivity(), ActivityTruckInfo.class);
                        intent.putExtra("PrimaryKey", item.getKeys());
                        getActivity().startActivity(intent);
                    }
                }
                autoView_listAll.setText("");
            }
        });

        autoView_listAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                autoView_listAll.showDropDown();
            }
        });

        btnViewOnSaleTruck.setOnClickListener(this);
        BtnFindBesides.setOnClickListener(this);
        BtnFindLoaction.setOnClickListener(this);
        btnViewAllTruck.setOnClickListener(this);

        loadTruckList();
        return rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnFindByMyBesides:
                Intent i = new Intent(getContext(), ActivityMaps.class);
                i.putExtra("IsFromSelLoc", false);
                startActivity(i);
                break;

            case R.id.btnFindByLocation:
                startActivity(new Intent(getContext(), ActivitySelectLoc.class));
                break;

            case R.id.btnViewAllTruck:
                startActivity(new Intent(getContext(), ActivityTruckDes.class));
                break;
            case R.id.btnViewOnSaleTruck:
                startActivity(new Intent(getContext(), ActivityOnSaleTruck.class));
                break;
        }
    }

    private void loadTruckList() {
        Query favQuery = database.getReference().child("trucks").child("info");
        favQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                itemsTruckList.clear();
                truckNames.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String key = snapshot.getKey();
                    String truckName = snapshot.child("truckName").getValue().toString();
                    itemsTruckList.add(new ItemTruckList(key, truckName));
                    truckNames.add(truckName);
                }
                autoView_listAll.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, truckNames));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
