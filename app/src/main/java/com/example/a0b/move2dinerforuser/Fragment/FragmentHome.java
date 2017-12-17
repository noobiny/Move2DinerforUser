package com.example.a0b.move2dinerforuser.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SnapHelper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.example.a0b.move2dinerforuser.ActivityTruckDes;
import com.example.a0b.move2dinerforuser.Adapter.AdapterRecentReview;
import com.example.a0b.move2dinerforuser.Adapter.AdapterTruckIntro;
import com.example.a0b.move2dinerforuser.Adapter.ImageSliderAdapter;
import com.example.a0b.move2dinerforuser.BaseApplication;
import com.example.a0b.move2dinerforuser.DTO.ItemTruckDes;
import com.example.a0b.move2dinerforuser.DTO.ReviewListItem;
import com.example.a0b.move2dinerforuser.R;
import com.example.a0b.move2dinerforuser.RecyclerViewEmptySupport;
import com.github.rubensousa.gravitysnaphelper.GravitySnapHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import cn.trinea.android.view.autoscrollviewpager.AutoScrollViewPager;


public class FragmentHome extends Fragment implements View.OnClickListener {

    private Button btn_alltruck;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();

    private ArrayList<String> images = new ArrayList<>();


    private ArrayList<ItemTruckDes> bestTrucks = new ArrayList<>();
    private ArrayList<String> bestTruckKeys = new ArrayList<>();
    private RecyclerViewEmptySupport bestRecycler;
    private AdapterTruckIntro bestAdapter;
    private LinearLayoutManager bestManager;


    private RecyclerViewEmptySupport recycler_recentreview;
    private AdapterRecentReview reviewAdapter;
    private LinearLayoutManager reviewManager;
    private ArrayList<ReviewListItem> reviewListItems = new ArrayList<>();
    private ArrayList<String> truckKeys = new ArrayList<>();
    private ArrayList<String> userKeys = new ArrayList<>();


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

        btn_alltruck = rootView.findViewById(R.id.btn_alltruck);
        homeslider = (AutoScrollViewPager) rootView.findViewById(R.id.homeslider);
        bestRecycler = (RecyclerViewEmptySupport) rootView.findViewById(R.id.recycler_TruckInfo);
        pager_indicator = (LinearLayout) rootView.findViewById(R.id.viewPagerCountDots);
        recycler_recentreview = rootView.findViewById(R.id.recycler_recentreview);

        reviewAdapter = new AdapterRecentReview(reviewListItems, getContext());
        reviewManager = new LinearLayoutManager(getContext());
        recycler_recentreview.setLayoutManager(reviewManager);
        recycler_recentreview.setEmptyView(rootView.findViewById(R.id.empty_recentreview));
        recycler_recentreview.setAdapter(reviewAdapter);


        bestAdapter = new AdapterTruckIntro(bestTrucks, bestTruckKeys, getContext());
        bestManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        bestManager.setAutoMeasureEnabled(true);

        //파베에서 내림차순 지원안해서 매니저 거꾸로
        bestManager.setReverseLayout(true);
        bestManager.setStackFromEnd(true);

        bestRecycler.setLayoutManager(bestManager);
        SnapHelper snapHelper = new GravitySnapHelper(Gravity.START);
        snapHelper.attachToRecyclerView(bestRecycler);
        bestRecycler.setEmptyView(rootView.findViewById(R.id.empty_TruckInfo));
        bestRecycler.setAdapter(bestAdapter);

        imageSliderAdapter = new ImageSliderAdapter(getContext(), images, Glide.with(getContext()));

        setItemsList();

        setRecentReview();

        setHomeSlider();


        btn_alltruck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), ActivityTruckDes.class));

            }
        });

        return rootView;
    }

    private void setRecentReview() {

        reviewListItems.clear();
        database.getReference().child("recentreview/11111").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String truckKey = dataSnapshot.child("truckKey").getValue().toString();
                String userKey = dataSnapshot.child("userKey").getValue().toString();

                getReviewData(truckKey, userKey);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        database.getReference().child("recentreview/22222").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String truckKey = dataSnapshot.child("truckKey").getValue().toString();
                String userKey = dataSnapshot.child("userKey").getValue().toString();
                getReviewData(truckKey, userKey);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        database.getReference().child("recentreview/33333").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String truckKey = dataSnapshot.child("truckKey").getValue().toString();
                String userKey = dataSnapshot.child("userKey").getValue().toString();

                getReviewData(truckKey, userKey);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void getReviewData(final String truckKey, final String userKey) {

        database.getReference().child("reviews/").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(userKey)) {
                    if (dataSnapshot.child(userKey).hasChild(truckKey)) {
                        reviewListItems.add(dataSnapshot.child(userKey + "/" + truckKey + "/").getValue(ReviewListItem.class));
                        reviewAdapter.notifyDataSetChanged();
                    } else {
                        return;
                    }
                } else {
                    return;
                }
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
