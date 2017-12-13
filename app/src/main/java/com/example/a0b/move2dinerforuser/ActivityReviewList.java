package com.example.a0b.move2dinerforuser;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.MenuItem;

import com.example.a0b.move2dinerforuser.Adapter.AdapterMyReview;
import com.example.a0b.move2dinerforuser.DTO.ReviewListItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class ActivityReviewList extends AppCompatActivity {

    private RecyclerViewEmptySupport recyclerView;
    private AdapterMyReview adapterMyReview;
    private ArrayList<ReviewListItem> reviewListItems = new ArrayList<>();
    private ArrayList<String> revPriKeys = new ArrayList<>();

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_list);
        new CustomTitlebar(this, "내가 작성한 리뷰");

        BaseApplication.getInstance().progressON(ActivityReviewList.this, "리뷰정보 로딩중");

        initView();
        printReviewList();

    }

    private void initView() {
        recyclerView = (RecyclerViewEmptySupport) findViewById(R.id.recycler_Review);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapterMyReview = new AdapterMyReview(reviewListItems, revPriKeys, this);
        recyclerView.setAdapter(adapterMyReview);
        recyclerView.setEmptyView(findViewById(R.id.empty_review));

    }

    private void printReviewList() {
        //전체 리뷰보여주기
        Query revQuery = database.getReference().child("reviews").child(auth.getCurrentUser().getUid());
        revQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                reviewListItems.clear();
                revPriKeys.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    reviewListItems.add(snapshot.getValue(ReviewListItem.class));
                    revPriKeys.add(snapshot.getKey());
                }

                //시간순서로 정렬
                for (int i = 0; i < reviewListItems.size(); i++) {
                    reviewListItems.get(i).setKey(revPriKeys.get(i));
                }

                Collections.sort(reviewListItems);
                revPriKeys.clear();
                for (int i = 0; i < reviewListItems.size(); i++) {
                    revPriKeys.add(reviewListItems.get(i).getKey());
                }

                adapterMyReview.notifyDataSetChanged();
                BaseApplication.getInstance().progressOFF();
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
