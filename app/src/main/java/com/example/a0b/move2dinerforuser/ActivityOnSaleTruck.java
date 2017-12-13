package com.example.a0b.move2dinerforuser;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.a0b.move2dinerforuser.Adapter.AdapterTruckRecycle;
import com.example.a0b.move2dinerforuser.DTO.ItemTruckDes;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class ActivityOnSaleTruck extends AppCompatActivity {

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    public double myLatitude, myLongitude;
    private AdapterTruckRecycle onSaleAdapter;
    private LinearLayoutManager onSaleManager;
    private RecyclerViewEmptySupport recycler_onSaleTruck;
    private ArrayList<ItemTruckDes> onSaleTrucks = new ArrayList<>();
    private ArrayList<String> onSaleTruckKeys = new ArrayList<>();
    private Spinner onsale_spinnersort;
    private AdView adView;
    private MyPermissionListener mPermissionListener;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private boolean isGPSEnabled, isNetworkEnabled;
    private Location currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_sale_truck);
        new CustomTitlebar(this, "현재 영업중인 트럭");
        BaseApplication.getInstance().progressON(this, "로딩중");
        initView();

        onsale_spinnersort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                printOnSaleList(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void initView() {
        mPermissionListener = new MyPermissionListener();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        onSaleManager = new LinearLayoutManager(this);
        onSaleAdapter = new AdapterTruckRecycle(onSaleTruckKeys, this, onSaleTrucks);
        recycler_onSaleTruck = (RecyclerViewEmptySupport) findViewById(R.id.recycler_onSaleTruck);
        recycler_onSaleTruck.setLayoutManager(onSaleManager);
        recycler_onSaleTruck.setEmptyView(findViewById(R.id.empty_onsale));
        recycler_onSaleTruck.setAdapter(onSaleAdapter);
        onsale_spinnersort = (Spinner) findViewById(R.id.onsale_spinnersort);
        adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    private void printOnSaleList(final int flag) {

        //영업중 트럭 찾아와서 아이템 추가
        Query query = database.getReference().child("trucks").child("info");
        query.orderByKey().addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                onSaleTrucks.clear();
                onSaleTruckKeys.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ItemTruckDes itemTruckDes = snapshot.getValue(ItemTruckDes.class);
                    Boolean business = itemTruckDes.getOnBusiness();

                    if (business == true) {
                        onSaleTrucks.add(itemTruckDes);
                        onSaleTruckKeys.add(snapshot.getKey());
                    }
                }

                if (flag == 0) {//거리순 정렬일때는 내위치 가져와야해서 퍼미션 필요해
                    if (currentLocation == null) {
                        new TedPermission(ActivityOnSaleTruck.this)
                                .setPermissionListener(mPermissionListener)
                                .setDeniedMessage("위치 권한이 필요합니다\n1.설정을 누르세요\n2.권한을 누르세요\n3.위치를 켜주세요")
                                .setPermissions(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                                .setGotoSettingButton(true)
                                .setGotoSettingButtonText("설정")
                                .check();
                    } else {
                        sortByDistance();
                    }

                } else if (flag == 1) {//평점순 정렬

                    // 키값을 객체에 넣어서 정렬후 다시 빼내기 ->
                    for (int i = 0; i < onSaleTrucks.size(); i++) {
                        onSaleTrucks.get(i).setKeys(onSaleTruckKeys.get(i));
                    }

                    Collections.sort(onSaleTrucks, new SortByStarCount());
                    onSaleTruckKeys.clear();
                    for (int i = 0; i < onSaleTrucks.size(); i++) {
                        onSaleTruckKeys.add(onSaleTrucks.get(i).getKeys());
                    }
                }

                onSaleAdapter.notifyDataSetChanged();
                BaseApplication.getInstance().progressOFF();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                BaseApplication.getInstance().progressOFF();

            }
        });
    }

    private class SortByStarCount implements Comparator<ItemTruckDes> {
        @Override
        public int compare(ItemTruckDes o1, ItemTruckDes o2) {
            return o1.starCount < o2.starCount ? 1 : o1.starCount > o2.starCount ? -1 : 0;
        }
    }

    private void sortByDistance() {
        for (int i = 0; i < onSaleTrucks.size(); i++) {
            Integer distance = calDistance(onSaleTrucks.get(i).getRecentLat(), onSaleTrucks.get(i).getRecentLon());
            onSaleTrucks.get(i).setDistance(distance);
            onSaleTrucks.get(i).setKeys(onSaleTruckKeys.get(i));
        }

        Collections.sort(onSaleTrucks, new SortByDistance());
        onSaleTruckKeys.clear();
        for (int i = 0; i < onSaleTrucks.size(); i++) {
            onSaleTruckKeys.add(onSaleTrucks.get(i).getKeys());

        }
        onSaleAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                return false;
            case android.R.id.home:
                finish();
                break;
        }
        return false;
    }

    public Integer calDistance(String lat, String lng) {

        double _lat = Double.parseDouble(lat);
        double _lng = Double.parseDouble(lng);

        return (int) LocationDistance.distance(myLatitude, myLongitude, _lat, _lng, "meter");
    }

    private class MyPermissionListener implements PermissionListener {
        private LocationManager locationManager = (LocationManager) ActivityOnSaleTruck.this.getSystemService(Context.LOCATION_SERVICE);

        @SuppressLint("MissingPermission")
        @Override
        public void onPermissionGranted() {
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isNetworkEnabled || !isGPSEnabled) {

                onsale_spinnersort.setSelection(0);

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(ActivityOnSaleTruck.this);

                alertDialog.setTitle("GPS 사용유무셋팅");
                alertDialog.setMessage("GPS 셋팅이 되지 않았을수도 있습니다.\n 설정창으로 가시겠습니까?\n위치설정On한 후 GPS를 찾는데 시간이 걸릴수 있습니다");
                // OK 를 누르게 되면 설정창으로 이동합니다.
                alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                });
                // Cancle 하면 종료 합니다.
                alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(ActivityOnSaleTruck.this, "위치서비스 기능을 사용하도록 설정해주세요.", Toast.LENGTH_SHORT).show();
                        dialog.cancel();
                    }
                });

                alertDialog.show();

            } else {
                OnCompleteListener<Location> completeListener = new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            currentLocation = task.getResult();
                            myLatitude = currentLocation.getLatitude();
                            myLongitude = currentLocation.getLongitude();
                            sortByDistance();
                        } else {
                        }
                    }
                };
                fusedLocationProviderClient.getLastLocation().addOnCompleteListener(ActivityOnSaleTruck.this, completeListener);
            }
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            onsale_spinnersort.setSelection(0);
            Toast.makeText(ActivityOnSaleTruck.this, "권한 필요 : " + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private class SortByDistance implements Comparator<ItemTruckDes> {

        @Override
        public int compare(ItemTruckDes o1, ItemTruckDes o2) {
            return o1.getDistance() > o2.getDistance() ? 1 : o1.getDistance() < o2.getDistance() ? -1 : 0;
        }
    }

}

