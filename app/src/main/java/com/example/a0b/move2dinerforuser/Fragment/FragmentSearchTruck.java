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
import android.widget.LinearLayout;

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

    Button BtnFindBesides, BtnFindLoaction, btnViewAllTruck, btnmap, btnlist;
    LinearLayout btngroup;
    AutoCompleteTextView autoView_listAll;
    ArrayList<ItemTruckList> itemsTruckList = new ArrayList<>();
    ArrayList<String> truckNames = new ArrayList<>();
    FirebaseDatabase database = FirebaseDatabase.getInstance();

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
        btngroup = rootView.findViewById(R.id.btngroup);
        btnlist = (Button) rootView.findViewById(R.id.btnlist);
        btnmap = (Button) rootView.findViewById(R.id.btnmap);
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


        BtnFindBesides.setOnClickListener(this);
        BtnFindLoaction.setOnClickListener(this);
        btnViewAllTruck.setOnClickListener(this);
        btnlist.setOnClickListener(this);
        btnmap.setOnClickListener(this);

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

                if (btngroup.getVisibility() == View.GONE)
                    btngroup.setVisibility(View.VISIBLE);
                else {
                    btngroup.setVisibility(View.GONE);
                }

                break;

            case R.id.btnFindByLocation:
                startActivity(new Intent(getContext(), ActivitySelectLoc.class));
                break;

            case R.id.btnViewAllTruck:
                startActivity(new Intent(getContext(), ActivityTruckDes.class));
                break;

            case R.id.btnlist:
                startActivity(new Intent(getContext(), ActivityOnSaleTruck.class));
                break;

            case R.id.btnmap:
                Intent i = new Intent(getContext(), ActivityMaps.class);
                i.putExtra("IsFromSelLoc", false);
                startActivity(i);
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
