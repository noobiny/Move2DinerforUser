package com.example.a0b.move2dinerforuser;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
    private ImageView img_thumbnail, truckinfo_iv3, iv_truck_favorite, tv_truck_share, tv_truck_writereview, truckinfo_offbusi_img, truckinfo_onbusi_img;
    private String primaryKey;
    private Boolean isHeart;
    private ViewPager img_slider;
    private TextView btn_truck_review, sliderIndicator, tv_truck_favcount, truckinfo_recent_location, tv_truck_tags, tv_truck_des,
            truckinfo_onbusi;
    private RelativeLayout truckinfo_card;

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
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private RecyclerViewEmptySupport recycler_review, recycle_menuinfo;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_truck_info);

        primaryKey = getIntent().getStringExtra("PrimaryKey");

        if (primaryKey == null) { //카카오톡 링크를 타고 온경우
            Uri uri = getIntent().getData();
            primaryKey = uri.getQueryParameter("PrimaryKey");

        }
        if (primaryKey == null) {
            Toast.makeText(
                    this, "경로가 잘못되었습니다", Toast.LENGTH_SHORT).show();
            return;
        }

        BaseApplication.getInstance().progressON(ActivityTruckInfo.this, "데이터 로딩중...");

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
                        truckinfo_onbusi.setText("영업중");
                        truckinfo_offbusi_img.setVisibility(View.INVISIBLE);
                        truckinfo_onbusi_img.setVisibility(View.VISIBLE);

                    } else {
                        truckinfo_onbusi.setText("영업종료");
                        truckinfo_offbusi_img.setVisibility(View.VISIBLE);
                        truckinfo_onbusi_img.setVisibility(View.INVISIBLE);

                    }
                }

                //카드체크
                if (itemTruckDes.getPayCard() == true) {
                    truckinfo_card.setVisibility(View.VISIBLE);
                } else {
                    truckinfo_card.setVisibility(View.GONE);
                }


                android.support.v7.widget.Toolbar mToolbar = findViewById(R.id.toolbar);
                setSupportActionBar(mToolbar);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
                collapsingToolbarLayout.setTitle(itemTruckDes.getTruckName());
                collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.CollapsedAppBar);
                collapsingToolbarLayout.setExpandedTitleMargin(0, 2, 0, 8);

                loadImageSlide(); //이미지 슬라이드 가져오기
                settingData(); //초기 세팅
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void initView() {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), R.drawable.credit_card, options);
        BitmapFactory.decodeResource(getResources(),R.drawable.kakaotalk_icon,options);
        BitmapFactory.decodeResource(getResources(),R.drawable.write_review,options);
        BitmapFactory.decodeResource(getResources(),R.drawable.ic_favorite_border_black_24dp,options);


        truckinfo_offbusi_img = findViewById(R.id.truckinfo_offbusi_img);
        truckinfo_onbusi_img = findViewById(R.id.truckinfo_onbusi_img);

        truckinfo_card = findViewById(R.id.truckinfo_card);
        truckinfo_onbusi = findViewById(R.id.truckinfo_onbusi);
        truckinfo_iv3 = findViewById(R.id.truckinfo_iv3);
        tv_truck_writereview = (ImageView) findViewById(R.id.tv_truck_writereview);
        tv_truck_share = (ImageView) findViewById(R.id.tv_truck_share);
        truckinfo_recent_location = (TextView) findViewById(R.id.truckinfo_recent_location);
        tv_truck_favcount = (TextView) findViewById(R.id.tv_truck_favcount);
        img_thumbnail = (ImageView) findViewById(R.id.img_thumbnail);
        sliderIndicator = (TextView) findViewById(R.id.sliderIndicator);
        img_slider = (ViewPager) findViewById(R.id.img_slider);
        btn_truck_review = (TextView) findViewById(R.id.btn_truck_review);
        recycler_review = (RecyclerViewEmptySupport) findViewById(R.id.recycler_review);
        recycle_menuinfo = (RecyclerViewEmptySupport) findViewById(R.id.recycle_menuinfo);
        iv_truck_favorite = (ImageView) findViewById(R.id.iv_truck_favorite);
        tv_truck_des = (TextView) findViewById(R.id.tv_truck_des);
        tv_truck_tags = (TextView) findViewById(R.id.tv_truck_tags);
        lottieAnimationView = (LottieAnimationView) findViewById(R.id.animation_view);

        truckinfo_iv3.setImageBitmap(decodeSampledBitmapFromResource(getResources(), R.drawable.credit_card, 25, 25));
        tv_truck_share.setImageBitmap(decodeSampledBitmapFromResource(getResources(), R.drawable.kakaotalk_icon, 35, 35));
        tv_truck_writereview.setImageBitmap(decodeSampledBitmapFromResource(getResources(), R.drawable.write_review, 34, 34));
        iv_truck_favorite.setImageBitmap(decodeSampledBitmapFromResource(getResources(),R.drawable.ic_favorite_border_black_24dp,38,38));

        reviewAdapter = new AdapterTruckReview(reviewListItems, reviewKeys, this);
        recycler_review.setLayoutManager(new LinearLayoutManager(this));
        recycler_review.setAdapter(reviewAdapter);
        recycler_review.setEmptyView(findViewById(R.id.empty_review));

        menuAdapter = new MenuListAdapter(this, menuListItems);
        recycle_menuinfo.setLayoutManager(new LinearLayoutManager(this));
        recycle_menuinfo.setAdapter(menuAdapter);
        recycle_menuinfo.setEmptyView(findViewById(R.id.empty_menu));


    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
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

        tv_truck_des.setText(itemTruckDes.getTruckDes());

        favcount = itemTruckDes.getStarCount();
        tv_truck_favcount.setText(String.valueOf(favcount));

        StringBuilder sb = new StringBuilder();
        if (itemTruckDes.getTags() != null) {
            for (int i = 0; i < itemTruckDes.getTags().size(); i++) {
                sb.append("#" + itemTruckDes.getTags().get(i) + "  ");
            }
            tv_truck_tags.setText(sb);

        } else {
            tv_truck_tags.setText("등록된 태그가 없습니다");
        }

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

        Query menuQuery = truckdatabase.getReference().child("trucks/menu/" + primaryKey);
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
        Query imageQuery = truckdatabase.getReference().child("trucks/pictures/" + primaryKey);
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

                ImageSliderAdapter imageSliderAdapter = new ImageSliderAdapter(ActivityTruckInfo.this, slideImages, Glide.with(ActivityTruckInfo.this));
                sliderIndicator.setText((img_slider.getCurrentItem() + 1) + " / " + slideImages.size());
                img_slider.setAdapter(imageSliderAdapter);
                img_slider.setPageTransformer(false, new ViewPager.PageTransformer() {
                    @Override
                    public void transformPage(View page, float position) {
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
                BaseApplication.getInstance().progressON(ActivityTruckInfo.this, "저장중...");
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

                database.getReference().child("recentreview/11111").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String truckKey = dataSnapshot.child("truckKey").getValue().toString();
                        String userKey = dataSnapshot.child("userKey").getValue().toString();
                        database.getReference().child("recentreview/22222/truckKey").setValue(truckKey);
                        database.getReference().child("recentreview/22222/userKey").setValue(userKey);

                        database.getReference().child("recentreview/11111/truckKey").setValue(primaryKey);
                        database.getReference().child("recentreview/11111/userKey").setValue(auth.getCurrentUser().getUid());
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
                        database.getReference().child("recentreview/33333/truckKey").setValue(truckKey);
                        database.getReference().child("recentreview/33333/userKey").setValue(userKey);


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


                //DB에 저장하고 바로 아이템 추가해서 보여줘야지
                reviewListItems.add(newitem);
                reviewAdapter.notifyDataSetChanged();
                BaseApplication.getInstance().progressOFF();
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


    @Override
    protected void onStop() {
        super.onStop();
    }
}
