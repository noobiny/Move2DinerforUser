package com.example.a0b.move2dinerforuser;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.example.a0b.move2dinerforuser.Adapter.AdapterTruckReview;
import com.example.a0b.move2dinerforuser.Adapter.ImageSliderAdapter;
import com.example.a0b.move2dinerforuser.Adapter.MenuListAdapter;
import com.example.a0b.move2dinerforuser.DTO.ItemTruckDes;
import com.example.a0b.move2dinerforuser.DTO.MenuListItem;
import com.example.a0b.move2dinerforuser.DTO.ReviewListItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.kakao.kakaolink.v2.KakaoLinkResponse;
import com.kakao.kakaolink.v2.KakaoLinkService;
import com.kakao.message.template.ButtonObject;
import com.kakao.message.template.ContentObject;
import com.kakao.message.template.FeedTemplate;
import com.kakao.message.template.LinkObject;
import com.kakao.message.template.SocialObject;
import com.kakao.network.ErrorResult;
import com.kakao.network.callback.ResponseCallback;
import com.kakao.util.helper.log.Logger;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import static com.example.a0b.move2dinerforuser.ActivityIntro.truckdatabase;
import static com.kakao.message.template.LinkObject.newBuilder;


public class ActivityTruckInfo extends AppCompatActivity {
    private int favcount;
    private ImageView img_thumbnail, iv_truck_favorite, tv_truck_share, tv_truck_writereview, busi_on, busi_off;
    private String primaryKey;
    private Boolean isHeart;
    private ViewPager img_slider;
    private TextView btn_truck_review, sliderIndicator, tv_truck_favcount, truckinfo_recent_location, tv_truck_busiHours, tv_truck_des,
            tv_truck_cardoff, tv_truck_cardon;

    private ArrayList<String> slideImages = new ArrayList<>();

    private ArrayList<MenuListItem> menuListItems = new ArrayList<>();
    private MenuListAdapter menuAdapter;

    private ArrayList<ReviewListItem> reviewListItems = new ArrayList<>();
    private AdapterTruckReview reviewAdapter;
    private ArrayList<String> reviewKeys = new ArrayList<>();

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    private ItemTruckDes itemTruckDes;

    private LottieAnimationView lottieAnimationView;

    private RecyclerViewEmptySupport recycler_review, recycle_menuinfo;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.gc();

        primaryKey = getIntent().getStringExtra("PrimaryKey");

        if (primaryKey == null) { //카카오톡 링크를 타고 온경우

            Uri uri = getIntent().getData();
            primaryKey = uri.getQueryParameter("PrimaryKey");

        }
        System.out.println("체크 : " + primaryKey);
        if (primaryKey == null) {
            Toast.makeText(
                    this, "경로가 잘못되었습니다", Toast.LENGTH_SHORT).show();
            return;
        }

        setContentView(R.layout.activity_truck_info);


        BaseApplication.getInstance().progressON(ActivityTruckInfo.this, "트럭 정보 로딩중");

        initView();

        //메뉴정보 가져오기
        loadMenuInfo();

        //한 트럭에 대한 리뷰 리스트 가져오기
        loadReviewList();

        checkOnBusiness();

        connectEvent();


    }

    private void checkOnBusiness() {
        database.getReference().child("trucks").child("info").child(primaryKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                itemTruckDes = dataSnapshot.getValue(ItemTruckDes.class);

//                //영업 중인지 체크 & 최근 장소 표시
                if (itemTruckDes.getRecentAddress() != null) {
                    truckinfo_recent_location.setText(itemTruckDes.getRecentAddress());

                    if (itemTruckDes.getOnBusiness()) {

                        busi_on.setVisibility(View.VISIBLE);
                        busi_off.setVisibility(View.INVISIBLE);
                    } else {
                        busi_on.setVisibility(View.INVISIBLE);
                        busi_off.setVisibility(View.VISIBLE);
                    }
                }

                //카드체크
                if(itemTruckDes.getPayCard()==true){
                    tv_truck_cardoff.setVisibility(View.INVISIBLE);
                    tv_truck_cardon.setVisibility(View.VISIBLE);

                }else{
                    tv_truck_cardoff.setVisibility(View.VISIBLE);
                    tv_truck_cardon.setVisibility(View.INVISIBLE);
                }

                android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
                setSupportActionBar(toolbar);
                toolbar.setTitle(itemTruckDes.getTruckName());
                toolbar.setBackgroundColor(getResources().getColor(R.color.appColor));
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);

                loadImageSlide(); //이미지 슬라이드 가져오기
                settingData(); //초기 세팅
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void initView() {

        tv_truck_cardoff=findViewById(R.id.tv_truck_cardoff);
        tv_truck_cardon=findViewById(R.id.tv_truck_cardon);

        busi_on = (ImageView) findViewById(R.id.busi_on);
        busi_off = (ImageView) findViewById(R.id.busi_off);

        tv_truck_writereview = (ImageView) findViewById(R.id.tv_truck_writereview);
        tv_truck_share = (ImageView) findViewById(R.id.tv_truck_share);
//        checkonbusiness = (TextView) findViewById(R.id.checkonbusiness);
        truckinfo_recent_location = (TextView) findViewById(R.id.truckinfo_recent_location);
        tv_truck_favcount = (TextView) findViewById(R.id.tv_truck_favcount);
        img_thumbnail = (ImageView) findViewById(R.id.img_thumbnail);
        sliderIndicator = (TextView) findViewById(R.id.sliderIndicator);
        img_slider = (ViewPager) findViewById(R.id.img_slider);
        btn_truck_review = (TextView) findViewById(R.id.btn_truck_review);
//        tv_truck_name = (TextView) findViewById(R.id.tv_truck_name);
        recycler_review = (RecyclerViewEmptySupport) findViewById(R.id.recycler_review);
        recycle_menuinfo = (RecyclerViewEmptySupport) findViewById(R.id.recycle_menuinfo);
        iv_truck_favorite = (ImageView) findViewById(R.id.iv_truck_favorite);
        tv_truck_des = (TextView) findViewById(R.id.tv_truck_des);
        tv_truck_busiHours = (TextView) findViewById(R.id.tv_truck_busiHours);
        lottieAnimationView = (LottieAnimationView) findViewById(R.id.animation_view);

        reviewAdapter = new AdapterTruckReview(reviewListItems, reviewKeys, this);
        recycler_review.setLayoutManager(new LinearLayoutManager(this));
        recycler_review.setAdapter(reviewAdapter);
        recycler_review.setEmptyView(findViewById(R.id.empty_review));

        menuAdapter = new MenuListAdapter(this, menuListItems);
        recycle_menuinfo.setLayoutManager(new LinearLayoutManager(this));
        recycle_menuinfo.setAdapter(menuAdapter);
        recycle_menuinfo.setEmptyView(findViewById(R.id.empty_menu));


    }

    private void connectEvent() {

        tv_truck_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (appInstalledOrNot("com.kakao.talk") == false) {
                    Toast.makeText(ActivityTruckInfo.this, "카카오톡이 설치되어 있는지 확인하여 주세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                shareKakao();
            }
        });

        img_slider.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                sliderIndicator.setText((position % slideImages.size() + 1) + " / " + slideImages.size());
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        //하트 버튼 클릭이벤트 ->  번갈아가면서 좋아요 싫어요할수있게 이미지 변경
        iv_truck_favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isHeart == true) {
                    lottieAnimationView.setVisibility(View.INVISIBLE);
                    iv_truck_favorite.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                    tv_truck_favcount.setText(String.valueOf(--favcount));
                    isHeart = false;
                } else {
                    lottieAnimationView.setVisibility(View.VISIBLE);
                    lottieAnimationView.playAnimation();
                    tv_truck_favcount.setText(String.valueOf(++favcount));
                    isHeart = true;
                }
                //트랜잭션 저장
                onStarClicked(database.getReference().child("trucks").child("info").child(primaryKey));
            }
        });


        btn_truck_review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //한 사람당 한 트럭에 한번만 리뷰쓰기 가능
                //기존 리뷰 있으면 수정하실래요? 물어본 후 리뷰 수정 하기
                Query findReviewQuery = database.getReference().child("reviews");
                findReviewQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(auth.getCurrentUser().getUid()).child(primaryKey).getValue() != null) {
                            //작성한 기록이 있음
                            AlertDialog.Builder alert = new AlertDialog.Builder(ActivityTruckInfo.this);
                            alert.setTitle("확인");
                            alert.setMessage("이미 작성하신 평가가 있습니다,\n 수정 하시겠습니까?");
                            alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    writeReview();
                                }
                            });

                            alert.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                }
                            });
                            alert.show();
                        } else {
                            //처음 작성하기
                            writeReview();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        });

        tv_truck_writereview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Query findReviewQuery = database.getReference().child("reviews");
                findReviewQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(auth.getCurrentUser().getUid()).child(primaryKey).getValue() != null) {
                            //작성한 기록이 있음
                            AlertDialog.Builder alert = new AlertDialog.Builder(ActivityTruckInfo.this);
                            alert.setTitle("확인");
                            alert.setMessage("이미 작성하신 평가가 있습니다,\n 수정 하시겠습니까?");
                            alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    writeReview();
                                }
                            });

                            alert.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                }
                            });
                            alert.show();
                        } else {
                            //처음 작성하기
                            writeReview();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        });


    }

    private void settingData() {

//        CustomTitlebar ct = new CustomTitlebar(this, itemTruckDes.getTruckName());
//        ct.iv_arrow_back.setVisibility(View.VISIBLE);
//        tv_truck_name.setText(itemTruckDes.getTruckName());
        tv_truck_des.setText(itemTruckDes.getTruckDes());
        tv_truck_busiHours.setText(itemTruckDes.getBusiInfo());

        favcount = itemTruckDes.getStarCount();
        tv_truck_favcount.setText(String.valueOf(favcount));

        //처음에 좋아요 인지 아닌지 검사
        if (itemTruckDes.stars.containsKey(auth.getCurrentUser().getUid())) {
            iv_truck_favorite.setImageResource(R.drawable.ic_favorite_border_red_24dp);
            lottieAnimationView.setVisibility(View.VISIBLE);
            isHeart = true;
        } else {
            lottieAnimationView.setVisibility(View.INVISIBLE);
            iv_truck_favorite.setImageResource(R.drawable.ic_favorite_border_black_24dp);
            isHeart = false;
        }
    }

    private void loadMenuInfo() {

        Query menuQuery = truckdatabase.getReference().child("trucks").child("menu").child(primaryKey);
        menuQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                menuListItems.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    MenuListItem menuListItem = snapshot.getValue(MenuListItem.class);
                    menuListItems.add(menuListItem);
                }
                menuAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void loadImageSlide() {

        //슬라이드 가져오기
        Query imageQuery = truckdatabase.getReference().child("trucks").child("pictures").child(primaryKey);
        imageQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    slideImages.add(snapshot.getValue().toString());
                }

                //이미지들을 등록 안한경우 => 슬라이드 대신 썸네일 보여주게함
                if (slideImages.size() == 0) {
                    img_thumbnail.setVisibility(View.VISIBLE);
                    sliderIndicator.setVisibility(View.INVISIBLE);
                    if (auth.getCurrentUser().getPhotoUrl() != null) {
                        Glide.with(ActivityTruckInfo.this).load(itemTruckDes.getThumbnail()).into(img_thumbnail);
                    }
                    BaseApplication.getInstance().progressOFF();
                    return;
                }

                ImageSliderAdapter imageSliderAdapter = new ImageSliderAdapter(ActivityTruckInfo.this, slideImages);
                sliderIndicator.setText((img_slider.getCurrentItem() + 1) + " / " + slideImages.size());
                img_slider.setAdapter(imageSliderAdapter);
                img_slider.setPageTransformer(false, new ViewPager.PageTransformer() {
                    @Override
                    public void transformPage(View page, float position) {
                        //position 값이 왼쪽, 오른쪽 이동방향에 따라 음수와 양수가 나오므로 절대값 Math.abs()으로 계산
                        //position의 변동폭이 (-2.0 ~ +2.0) 사이이기에 부호 상관없이 (0.0~1.0)으로 변경폭 조절
                        //주석으로 수학적 연산을 설명하기에는 한계가 있으니 코드를 보고 잘 생각해 보시기 바랍니다.
                        float normalizedposition = Math.abs(1 - Math.abs(position));
                        page.setAlpha(normalizedposition);  //View의 투명도 조절
                        page.setScaleX(normalizedposition / 2 + 0.5f); //View의 x축 크기조절
                        page.setScaleY(normalizedposition / 2 + 0.5f); //View의 y축 크기조절
                        page.setRotationY(position * 80);   //View의 Y축(세로축) 회전 각도
                    }
                });

                BaseApplication.getInstance().progressOFF();


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void loadReviewList() {
        Query revQuery = database.getReference().child("reviews");
        revQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                reviewListItems.clear();
                reviewKeys.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.child(primaryKey).getValue() == null)
                        continue;
                    reviewListItems.add(snapshot.child(primaryKey).getValue(ReviewListItem.class));
                    reviewKeys.add(snapshot.child(primaryKey).getKey());
                }

                for (int i = 0; i < reviewListItems.size(); i++) {
                    reviewListItems.get(i).setKey(reviewKeys.get(i));
                }

                Collections.sort(reviewListItems);

                reviewKeys.clear();
                for (int i = 0; i < reviewListItems.size(); i++) {
                    reviewKeys.add(reviewListItems.get(i).getKey());
                }

                reviewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void writeReview() {

        AlertDialog.Builder alert = new AlertDialog.Builder(ActivityTruckInfo.this);

        alert.setTitle("리뷰");
        alert.setMessage("리뷰 작성");

        final EditText editText = new EditText(ActivityTruckInfo.this);
        alert.setView(editText);

        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                long curr = System.currentTimeMillis();
                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd hh:mm");
                String datetime2 = sdf2.format(new Date(curr));

                String _photoUrl;//photoUrl 기본지정
                if (auth.getCurrentUser().getPhotoUrl() == null) {
                    _photoUrl = "https://firebasestorage.googleapis.com/v0/b/movetodiner.appspot.com/o/images%2Fuser%2FbaseImage.png?alt=media&token=101db517-cbc1-40a8-ba4c-2659cca98caf";
                } else {
                    _photoUrl = auth.getCurrentUser().getPhotoUrl().toString();
                }

                ReviewListItem newitem = new ReviewListItem(datetime2, editText.getText().toString(), auth.getCurrentUser().getDisplayName(),
                        auth.getCurrentUser().getUid(), _photoUrl,
                        itemTruckDes.getTruckName(), primaryKey, itemTruckDes.getTruckDes(), itemTruckDes.getThumbnail());
                //리뷰를 작성할때 현재시간, 내용, 사용자닉네임, 사용자uid,  사용자 썸네일, 트럭이름,트럭Uid, 트럭정보, 트럭썸네일을 넣는다

                database.getReference().child("reviews").child(auth.getCurrentUser().getUid()).child(primaryKey)
                        .setValue(newitem);

                //DB에 저장하고 바로 아이템 추가해서 보여줘야지
                reviewListItems.add(newitem);
                reviewAdapter.notifyDataSetChanged();

                Toast.makeText(ActivityTruckInfo.this, "리뷰가 저장됐습니다", Toast.LENGTH_SHORT).show();
            }
        });
        alert.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        alert.show();
    }


    private void shareKakao() {

        String scheme = getResources().getString(R.string.kakao_scheme);
        String host = getResources().getString(R.string.kakaolink_host);

//        FeedTemplate params = FeedTemplate
//                .newBuilder(ContentObject.newBuilder("디저트 사진",
//                        "http://mud-kage.kakao.co.kr/dn/NTmhS/btqfEUdFAUf/FjKzkZsnoeE4o19klTOVI1/openlink_640x640s.jpg",
//                        LinkObject.newBuilder().setWebUrl("https://developers.kakao.com")
//                                .setMobileWebUrl("https://developers.kakao.com").build())
//                        .setDescrption("아메리카노, 빵, 케익")
//                        .build())
//                .setSocial(SocialObject.newBuilder().setLikeCount(10).setCommentCount(20)
//                        .setSharedCount(30).setViewCount(40).build())
//                .addButton(new ButtonObject("웹에서 보기", LinkObject.newBuilder().setWebUrl("https://developers.kakao.com").setMobileWebUrl("https://developers.kakao.com").build()))
//                .addButton(new ButtonObject("앱에서 보기", LinkObject.newBuilder()
//                        .setWebUrl("https://developers.kakao.com")
//                        .setMobileWebUrl("https://developers.kakao.com")
//                        .build()))
//                .build();
//
//        KakaoLinkService.getInstance().sendDefault(this, params, new ResponseCallback<KakaoLinkResponse>() {
//            @Override
//            public void onFailure(ErrorResult errorResult) {
//                Logger.e(errorResult.toString());
//            }
//
//            @Override
//            public void onSuccess(KakaoLinkResponse result) {
//
//            }
//        });

//
        FeedTemplate params = FeedTemplate
                .newBuilder(ContentObject.newBuilder("\"" + itemTruckDes.getTruckName() + "\"",
                        itemTruckDes.getThumbnail(),
                        newBuilder().setWebUrl("https://developers.kakao.com")
                                .setMobileWebUrl("https://developers.kakao.com").build())
                        .setDescrption("최근장소 : " + itemTruckDes.getRecentAddress())
                        .build())
                .setSocial(SocialObject.newBuilder().setLikeCount(itemTruckDes.getStarCount()).build())
                .addButton(new ButtonObject("앱에서 보기", newBuilder()
                        .setMobileWebUrl(scheme + "://" + host)
                        .setAndroidExecutionParams("PrimaryKey=" + primaryKey)
                        .build()))
                .build();

        KakaoLinkService.getInstance().sendDefault(this, params, new ResponseCallback<KakaoLinkResponse>() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                Logger.e(errorResult.toString());
            }

            @Override
            public void onSuccess(KakaoLinkResponse result) {
                System.out.println("카카오톡 공유 성공");
            }
        });

    }

    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
        boolean app_installed = false;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }

    //내 아이디 없으면 값 추가, 있으면 값 삭제
    private void onStarClicked(DatabaseReference postRef) {
        postRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                ItemTruckDes itemTruckDes = mutableData.getValue(ItemTruckDes.class);
                if (itemTruckDes == null) {
                    return Transaction.success(mutableData);
                }
                if (itemTruckDes.stars.containsKey(auth.getCurrentUser().getUid())) {
                    // Unstar the post and remove self from stars
                    itemTruckDes.starCount = itemTruckDes.starCount - 1;

                    itemTruckDes.stars.remove(auth.getCurrentUser().getUid());
                } else {
                    // Star the post and add self to stars
                    itemTruckDes.starCount = itemTruckDes.starCount + 1;
                    itemTruckDes.stars.put(auth.getCurrentUser().getUid(), true);
                }

                // Set value and report transaction success
                mutableData.setValue(itemTruckDes);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {

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

    @Override
    protected void onResume() {
        super.onResume();


    }
}
