package com.example.a0b.move2dinerforuser.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.a0b.move2dinerforuser.ActivityOnSaleTruck;
import com.example.a0b.move2dinerforuser.ActivityTruckDes;
import com.example.a0b.move2dinerforuser.Adapter.AdapterTruckIntro;
import com.example.a0b.move2dinerforuser.Adapter.AdapterTruckRecycle;
import com.example.a0b.move2dinerforuser.Adapter.ImageSliderAdapter;
import com.example.a0b.move2dinerforuser.BaseApplication;
import com.example.a0b.move2dinerforuser.DTO.ItemTruckDes;
import com.example.a0b.move2dinerforuser.R;
import com.example.a0b.move2dinerforuser.RecyclerViewEmptySupport;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import cn.trinea.android.view.autoscrollviewpager.AutoScrollViewPager;


public class FragmentHome extends Fragment implements View.OnClickListener {

    private FirebaseDatabase database = FirebaseDatabase.getInstance();

    private ArrayList<String> images = new ArrayList<>();

    private Button btn_viewonsaletruck;

    private ArrayList<ItemTruckDes> bestTrucks = new ArrayList<>();
    private ArrayList<String> bestTruckKeys = new ArrayList<>();
    private RecyclerViewEmptySupport bestRecycler;
    private AdapterTruckRecycle bestAdapter;
    private LinearLayoutManager bestManager;

    private AdapterTruckIntro onSaleAdapter;
    private LinearLayoutManager onSaleManager;
    private RecyclerViewEmptySupport recycler_onSaleTruck;
    private ArrayList<ItemTruckDes> onSaleTrucks = new ArrayList<>();
    private ArrayList<String> onSaleTruckKeys = new ArrayList<>();

    private AutoScrollViewPager homeslider;
    private LinearLayout pager_indicator;
    private int dotsCount;
    private ImageView[] dots;
    private ImageSliderAdapter imageSliderAdapter;
    private int currentPosition;

    public FragmentHome() {
    }

    public static FragmentHome newInstance() {
        return new FragmentHome();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        homeslider = (AutoScrollViewPager) rootView.findViewById(R.id.homeslider);
        bestRecycler = (RecyclerViewEmptySupport) rootView.findViewById(R.id.recycler_TruckInfo);
        recycler_onSaleTruck = (RecyclerViewEmptySupport) rootView.findViewById(R.id.recycler_onSaleTruck);
        pager_indicator = (LinearLayout) rootView.findViewById(R.id.viewPagerCountDots);
        btn_viewonsaletruck = (Button) rootView.findViewById(R.id.btn_viewonsaletruck);


        //startTime 역순으로 정렬렬
        onSaleAdapter = new AdapterTruckIntro(onSaleTrucks, onSaleTruckKeys, getContext());
        onSaleManager = new LinearLayoutManager(getContext());
        onSaleManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recycler_onSaleTruck.setLayoutManager(onSaleManager);
        recycler_onSaleTruck.setEmptyView(rootView.findViewById(R.id.empty_onsale));
        recycler_onSaleTruck.setAdapter(onSaleAdapter);

        bestAdapter = new AdapterTruckRecycle(bestTruckKeys, getContext(), bestTrucks);
        bestManager = new LinearLayoutManager(getContext());
        bestManager.setAutoMeasureEnabled(true);

        //파베에서 내림차순 지원안해서 매니저 거꾸로
        bestManager.setReverseLayout(true);
        bestManager.setStackFromEnd(true);

        bestRecycler.setLayoutManager(bestManager);
        bestRecycler.setEmptyView(rootView.findViewById(R.id.empty_TruckInfo));
        bestRecycler.setAdapter(bestAdapter);

        btn_viewonsaletruck.setOnClickListener(this);

        imageSliderAdapter = new ImageSliderAdapter(getContext(), images);

        setItemsList();

        setOnSaleTrucks();

        setHomeSlider();

        return rootView;
    }

    private void setOnSaleTrucks() {
        Query query = database.getReference().child("trucks").child("info");
        query.orderByKey().addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int count = 0;
                onSaleTrucks.clear();
                onSaleTruckKeys.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ItemTruckDes itemTruckDes = snapshot.getValue(ItemTruckDes.class);

                    if (itemTruckDes.getOnBusiness() == false)
                        continue;

                    onSaleTrucks.add(itemTruckDes);
                    onSaleTruckKeys.add(snapshot.getKey());
                    if (count++ == 3) {
                        break;
                    }
                }
                onSaleAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setHomeSlider() {
        Query sliderimages = database.getReference().child("homesliderimages");
        sliderimages.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                images.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    images.add(snapshot.getValue().toString());
                }

                homeslider.setAdapter(imageSliderAdapter);
                setUiPageViewController();
                BaseApplication.getInstance().progressOFF();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        homeslider.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                currentPosition = position;
                for (int i = 0; i < dotsCount; i++) {
                    dots[i].setImageDrawable(getResources().getDrawable(R.drawable.nonselecteditem_dot));

                }
                if (dots != null) {
                    dots[currentPosition % 5].setImageDrawable(getResources().getDrawable(R.drawable.selecteditem_dot));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    private void setUiPageViewController() {

        dotsCount = images.size();
        dots = new ImageView[dotsCount];

        for (int i = 0; i < dotsCount; i++) {
            dots[i] = new ImageView(getActivity());
            dots[i].setImageDrawable(getResources().getDrawable(R.drawable.nonselecteditem_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(4, 0, 4, 0);

            pager_indicator.addView(dots[i], params);
        }

        dots[0].setImageDrawable(getResources().getDrawable(R.drawable.selecteditem_dot));
    }

    @Override
    public void onResume() {
        super.onResume();

        imageSliderAdapter.notifyDataSetChanged();
        homeslider.setInterval(5000);
        homeslider.startAutoScroll(0);
    }

    @Override
    public void onPause() {
        super.onPause();
        homeslider.stopAutoScroll();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_viewonsaletruck:
                startActivity(new Intent(getContext(), ActivityOnSaleTruck.class));
                break;
        }
    }


    void setItemsList() {
        //좋아요 상위 랭킹 5개 아이템 추가
        Query mQuery = database.getReference().child("trucks").child("info").orderByChild("starCount").limitToLast(5);
        mQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                bestTrucks.clear();
                bestTruckKeys.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ItemTruckDes itemTruckDes = snapshot.getValue(ItemTruckDes.class);
                    bestTrucks.add(itemTruckDes);
                    bestTruckKeys.add(snapshot.getKey());
                }
                bestAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();

    }


}
