package com.example.a0b.move2dinerforuser;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a0b.move2dinerforuser.Adapter.MapPagerAdapter;
import com.example.a0b.move2dinerforuser.DTO.ItemTruckDes;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import static com.example.a0b.move2dinerforuser.R.id.map;

public class ActivityMaps extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnMyLocationButtonClickListener {

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    public LatLng myLocationLatLng;
    public ArrayList<Marker> markers = new ArrayList<>();
    public Marker selectedMarker;  // 현재 선택 돼 있는 마커를 지정
    public TextView spinnerdistance;
    BottomBehaviorSeekbar dt;
    private TextView tv_currentlocation;
    private ViewPager mapindicator;
    private boolean isGPSEnabled, isNetworkEnabled, isFromSelLoc;
    private double myLatitude, myLongitude;
    private GoogleMap googleMap;
    private MyPermissionListener mPermissionListener;
    private Geocoder geocoder;
    private Intent i;
    private LatLng startingPoint = new LatLng(37.521504, 126.954152);
    private Location currentLocation;
    private FusedLocationProviderClient fusedLocationProviderClient; // 11버전에서 추가된 새롭게 gps값 가져오는 객체 (심플해짐 로케이션리스너 유물됨)
    private ArrayList<ItemTruckDes> itemTruckDes = new ArrayList<>();
    private ArrayList<Integer> distances = new ArrayList<>();
    private ArrayList<String> priKeys = new ArrayList<>();
    private MapPagerAdapter mapPagerAdapter;
    private int[] marker_basic_Images = {R.drawable.marker_like_unselect_1, R.drawable.marker_like_unselect_2, R.drawable.marker_like_unselect_3, R.drawable.marker_like_unselect_4,
            R.drawable.marker_like_unselect_5, R.drawable.marker_like_unselect_6, R.drawable.marker_like_unselect_7, R.drawable.marker_like_unselect_8,
            R.drawable.marker_like_unselect_9, R.drawable.marker_like_unselect_10, R.drawable.marker_like_unselect_11, R.drawable.marker_like_unselect_12,
            R.drawable.marker_like_unselect_13, R.drawable.marker_like_unselect_14, R.drawable.marker_like_unselect_15, R.drawable.marker_like_unselect_16,
            R.drawable.marker_like_unselect_17, R.drawable.marker_like_unselect_18, R.drawable.marker_like_unselect_19, R.drawable.marker_like_unselect_20
    };

    private int[] marker_selected_Images = {R.drawable.marker_like_select_1, R.drawable.marker_like_select_2, R.drawable.marker_like_select_3, R.drawable.marker_like_select_4,
            R.drawable.marker_like_select_5, R.drawable.marker_like_select_6, R.drawable.marker_like_select_7, R.drawable.marker_like_select_8,
            R.drawable.marker_like_select_9, R.drawable.marker_like_select_10, R.drawable.marker_like_select_11, R.drawable.marker_like_select_12,
            R.drawable.marker_like_select_13, R.drawable.marker_like_select_14, R.drawable.marker_like_select_15, R.drawable.marker_like_select_16,
            R.drawable.marker_like_select_17, R.drawable.marker_like_select_18, R.drawable.marker_like_select_19, R.drawable.marker_like_select_20
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        i = getIntent();
        isFromSelLoc = i.getBooleanExtra("IsFromSelLoc", false);

        initView();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        dt = new BottomBehaviorSeekbar(ActivityMaps.this);

        new TedPermission(ActivityMaps.this)
                .setPermissionListener(mPermissionListener)
                .setDeniedMessage("위치 권한이 필요합니다\n1.설정을 누르세요\n2.권한을 누르세요\n3.위치를 켜주세요")
                .setPermissions(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                .setGotoSettingButton(true)
                .setGotoSettingButtonText("설정")
                .check();

        //슬라이드 할때 마커 이동,카메라 이동
        mapindicator.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                //카메라 해당 가게로 이동
                LatLng loc = new LatLng(Double.parseDouble(itemTruckDes.get(position).getRecentLat()), Double.parseDouble(itemTruckDes.get(position).getRecentLon()));
                CameraUpdate center = CameraUpdateFactory.newLatLng(loc);
                googleMap.animateCamera(center);

                // 마커 이미지 변경  ->  스니펫으로 마커들마다 인덱스 지정한 후 가져오는데 사실은 스니펫 이렇게 사용하는건 아님/ 플래그 넣은 효과
                if (selectedMarker != null) {
                    int index = Integer.parseInt(selectedMarker.getSnippet());
                    selectedMarker.setIcon(BitmapDescriptorFactory.fromResource(marker_basic_Images[index]));
                    selectedMarker.setZIndex(0);
                }

                if (markers.size() != 0) {
                    selectedMarker = markers.get(position);
                    selectedMarker.setZIndex(99);
                    selectedMarker.setIcon(BitmapDescriptorFactory.fromResource(marker_selected_Images[position]));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private void initView() {
        tv_currentlocation = (TextView) findViewById(R.id.tv_currentlocation);
        mapindicator = (ViewPager) findViewById(R.id.mapindicator);
        spinnerdistance = (TextView) findViewById(R.id.spinnerdistance);
        mPermissionListener = new MyPermissionListener();
        geocoder = new Geocoder(this, Locale.KOREA);

        mapPagerAdapter = new MapPagerAdapter(this, itemTruckDes);
        mapindicator.setAdapter(mapPagerAdapter);

    }


    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

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
                Toast.makeText(ActivityMaps.this, "위치서비스 기능을 사용하도록 설정해주세요.", Toast.LENGTH_SHORT).show();
                dialog.cancel();
            }
        });

        alertDialog.show();
    }

    //내위치를 받아오는 콜백을 받은후에 맵을 보여주게 하기 ★  // 순서 중요
    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        OnCompleteListener<Location> completeListener = new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    currentLocation = task.getResult();
                    myLatitude = currentLocation.getLatitude();
                    myLongitude = currentLocation.getLongitude();
                    myLocationLatLng = new LatLng(myLatitude, myLongitude);
                    mapSync();
                } else {
                }
            }
        };
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(ActivityMaps.this, completeListener);
    }

    private void mapSync() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(map);
        mapFragment.getMapAsync(ActivityMaps.this);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {

        this.googleMap = googleMap;
        this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startingPoint, 10));
        this.googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        if (isFromSelLoc) {
            //지역별 조회에서 가져오는것. 만약 지역별 조회에서 데이터 없으면 기본으로 학교로 자동 리턴
            myLatitude = i.getDoubleExtra("Latitude", 37.5841486f);
            myLongitude = i.getDoubleExtra("Longitude", 126.9244591f);
            myLocationLatLng = new LatLng(myLatitude, myLongitude);
        } else {
            this.googleMap.setMyLocationEnabled(true);
            this.googleMap.setOnMyLocationButtonClickListener(this);

        }

        clearView();
        settingMap(300);
        retrieveWithDistance(300);

        this.googleMap.setOnMarkerClickListener(this);

        try {
            List<Address> myAddress = geocoder.getFromLocation(myLatitude, myLongitude, 1);
            String city = myAddress.get(0).getSubLocality();
            String street = myAddress.get(0).getThoroughfare();
            if (city == null) {
                city = myAddress.get(0).getLocality();
            }
            if (street == null) {
                street = "";
            }
            tv_currentlocation.setText(city + " " + street);
        } catch (IOException e) {
            e.printStackTrace();
        }


        spinnerdistance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dt.show();
            }
        });
    }

    public void clearView() {
        mapindicator.setVisibility(View.VISIBLE);
        selectedMarker = null;
        googleMap.clear();
        priKeys.clear();
        itemTruckDes.clear();
        markers.clear();
        distances.clear();
        mapPagerAdapter.notifyDataSetChanged();
    }

    public void settingMap(int rad) {

        MarkerOptions myMarker = new MarkerOptions().position(myLocationLatLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location_on)).title("MyLocation");
        CircleOptions circle = new CircleOptions().center(myLocationLatLng).radius(rad).strokeWidth(0f).fillColor(Color.parseColor("#33ff0000"));
        googleMap.addCircle(circle);
        googleMap.addMarker(myMarker);
        CameraUpdate zoom = null;

        //각각 위치마다 줌 크기를 다르게
        switch (rad) {
            case 300:
                zoom = CameraUpdateFactory.newLatLngZoom(myLocationLatLng, 16);
                break;
            case 500:
                zoom = CameraUpdateFactory.newLatLngZoom(myLocationLatLng, 15);
                break;
            case 1000:
                zoom = CameraUpdateFactory.newLatLngZoom(myLocationLatLng, 14);
                break;
            case 3000:
                zoom = CameraUpdateFactory.newLatLngZoom(myLocationLatLng, 13);
                break;
            case 5000:
                zoom = CameraUpdateFactory.newLatLngZoom(myLocationLatLng, 12);
        }
        googleMap.animateCamera(zoom);
    }

    public void retrieveWithDistance(final int dist) {

        Query query = database.getReference().child("trucks").child("info");
        query.orderByKey().addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ItemTruckDes item = snapshot.getValue(ItemTruckDes.class);

                    if (item.getOnBusiness() == false || item.getRecentLat() == null)
                        continue;
                    Integer distance = calDistance(item.getRecentLat(), item.getRecentLon());
                    if (distance <= dist) {
                        //거리도 계산한후 추가해주고 어댑터에 넘기는 방법
                        distances.add(distance);
                        itemTruckDes.add(item);
                        priKeys.add(snapshot.getKey());
                    }

                }

                //거리순 오름차순 정렬
                for (int i = 0; i < itemTruckDes.size(); i++) {
                    itemTruckDes.get(i).setDistance(distances.get(i));
                    itemTruckDes.get(i).setKeys(priKeys.get(i));
                }

                priKeys.clear();
                distances.clear();
                Collections.sort(itemTruckDes, new SortByDistance());

                mapPagerAdapter.notifyDataSetChanged();

                //파베에서 데이터를 가져온 후 addMarker
                if (itemTruckDes.size() != 0) {

                    for (int i = 0; i < itemTruckDes.size(); i++) {
                        markers.add(addMarker(itemTruckDes.get(i), i));
                    }
                    mapPagerAdapter.notifyDataSetChanged();
                    int index = mapindicator.getCurrentItem();
                    selectedMarker = markers.get(index);
                    selectedMarker.setZIndex(999);
                    selectedMarker.setIcon(BitmapDescriptorFactory.fromResource(marker_selected_Images[index]));
                } else {
                    mapindicator.setVisibility(View.INVISIBLE);
                    Toast.makeText(ActivityMaps.this, "현재 위치에 영업중인 트럭이 없습니다", Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public Integer calDistance(String lat, String lng) {

        double _lat = Double.parseDouble(lat);
        double _lng = Double.parseDouble(lng);

        return (int) LocationDistance.distance(myLatitude, myLongitude, _lat, _lng, "meter");
    }

    private Marker addMarker(ItemTruckDes itemTruckDes, int index) {

        LatLng position = new LatLng(Double.parseDouble(itemTruckDes.getRecentLat()), Double.parseDouble(itemTruckDes.getRecentLon()));

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.title("");
        markerOptions.position(position);
        markerOptions.snippet(String.valueOf(index));
        markerOptions.icon(BitmapDescriptorFactory.fromResource(marker_basic_Images[index]));

        return googleMap.addMarker(markerOptions);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        //내 위치를 나타내는 것도 마커인데 그 마커클릭시 아무 반응 안하게,
        if (marker.getTitle().equals("MyLocation"))
            return true;

        //해당 뷰페이지 이동
        mapindicator.setCurrentItem(Integer.parseInt(marker.getSnippet()));
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    //테드에 연결된 퍼미션
    private class MyPermissionListener implements PermissionListener {
        private LocationManager locationManager = (LocationManager) ActivityMaps.this.getSystemService(Context.LOCATION_SERVICE);

        @Override
        public void onPermissionGranted() {
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isNetworkEnabled || !isGPSEnabled) {
                if (isFromSelLoc) {
                    mapSync();
                } else {
                    showSettingsAlert();
                }
            } else {
                //권한 제대로 받은 경우
                if (!isFromSelLoc) {
                    getCurrentLocation();
                } else
                    mapSync();
            }
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            Toast.makeText(ActivityMaps.this, "권한 필요 : " + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private class SortByDistance implements Comparator<ItemTruckDes> {

        @Override
        public int compare(ItemTruckDes o1, ItemTruckDes o2) {
            return o1.getDistance() > o2.getDistance() ? 1 : o1.getDistance() < o2.getDistance() ? -1 : 0;
        }
    }

}