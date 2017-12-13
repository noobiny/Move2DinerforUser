package com.example.a0b.move2dinerforuser;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.a0b.move2dinerforuser.Adapter.AdapterMyReview;
import com.example.a0b.move2dinerforuser.Adapter.AdapterTruckIntro;
import com.example.a0b.move2dinerforuser.DTO.ItemTruckDes;
import com.example.a0b.move2dinerforuser.DTO.ReviewListItem;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;

import gun0912.tedbottompicker.TedBottomPicker;

import static com.example.a0b.move2dinerforuser.ActivitySelectAuth.mGoogleApiClient;

public class ActivityUserInfo extends AppCompatActivity implements View.OnClickListener {
    private static final int ACTIVITY_USER_INFO = 300;

    private ArrayList<ItemTruckDes> favlist = new ArrayList<>();
    private ArrayList<String> primaryKeys = new ArrayList<>();
    private AdapterTruckIntro favAdapter;
    private LinearLayoutManager favManager;

    private ArrayList<ReviewListItem> reviewListItems = new ArrayList<>();
    private ArrayList<String> revPriKeys = new ArrayList<>();
    private AdapterMyReview revAdapter;
    private LinearLayoutManager revManager;
    private MyPermissionListener mPermissionListener;
    private FirebaseStorage storage;
    private FirebaseAuth auth;
    private FirebaseDatabase database;

    private Button btn_user_remove, btn_showfavAll, btn_showrevAll;
    private ImageView iv_user_photo;
    private TextView tv_user_nick;
    private Uri imagePath;

    private RecyclerViewEmptySupport recycler_review;
    private RecyclerViewEmptySupport recycler_fav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        CustomTitlebar ct = new CustomTitlebar(this, "나의 프로필", ACTIVITY_USER_INFO);
        ct.action_save_user_info.setOnClickListener(this); //타이틀바 저장버튼

        mPermissionListener = new MyPermissionListener();
        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        initView();

        //데이터 가져오기
        getDataFromFirebase();

        connectEvent();

    }


    private void initView() {
        recycler_review = (RecyclerViewEmptySupport) findViewById(R.id.recycler_reviews);
        recycler_fav = (RecyclerViewEmptySupport) findViewById(R.id.list_favlist);
        btn_showfavAll = (Button) findViewById(R.id.btn_showfavAll);
        btn_showrevAll = (Button) findViewById(R.id.btn_showrevAll);
        btn_user_remove = (Button) findViewById(R.id.btn_user_remove);
        iv_user_photo = (ImageView) findViewById(R.id.iv_user_photo);
        tv_user_nick = (TextView) findViewById(R.id.tv_user_nick);

        //홈화면에 있는 리사이클러어댑터 재활용
        favAdapter = new AdapterTruckIntro(favlist, primaryKeys, ActivityUserInfo.this);
        favManager = new LinearLayoutManager(this);
        favManager.setOrientation(LinearLayout.HORIZONTAL);
        favManager.setAutoMeasureEnabled(true);
        recycler_fav.setLayoutManager(favManager);
        recycler_fav.setAdapter(favAdapter);
        recycler_fav.setEmptyView(findViewById(R.id.empty_fav));

        revManager = new LinearLayoutManager(this);
        revAdapter = new AdapterMyReview(reviewListItems, revPriKeys, this);
        recycler_review.setLayoutManager(revManager);
        recycler_review.setAdapter(revAdapter);
        recycler_review.setEmptyView(findViewById(R.id.empty_review));
    }

    private void logOut() {
        LoginManager.getInstance().logOut(); //페이스북로그아웃
        FirebaseAuth.getInstance().signOut();
        mGoogleApiClient.connect();
        mGoogleApiClient.registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(@Nullable Bundle bundle) {
                auth.getInstance().signOut();
                if (mGoogleApiClient.isConnected()) {
                    Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull Status status) {
                            if (status.isSuccess()) {
                                startActivity(new Intent(ActivityUserInfo.this, ActivitySelectAuth.class));
                                finish();
                            }
                        }
                    });
                }
            }

            @Override
            public void onConnectionSuspended(int i) {
            }
        });

    }

    private void getDataFromFirebase() {

        //데이터 가져오기
        tv_user_nick.setText(auth.getCurrentUser().getDisplayName());

        BaseApplication.getInstance().progressON(ActivityUserInfo.this, "데이터 로딩중");
        //사진 데이터 가져올때 Glide 사용해야해
        if (auth.getCurrentUser().getPhotoUrl() != null) {
            Glide.with(this).load(auth.getCurrentUser().getPhotoUrl()).into(iv_user_photo);
        }

        //리뷰 데이터 가져오기  -> 최근 3개 가져오기
        Query revQuery = database.getReference().child("reviews").child(auth.getCurrentUser().getUid()).limitToLast(3);
        revQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                reviewListItems.clear();
                revPriKeys.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    reviewListItems.add(snapshot.getValue(ReviewListItem.class));
                    revPriKeys.add(snapshot.getKey());
                }
                revAdapter.notifyDataSetChanged();
                BaseApplication.getInstance().progressOFF();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        //최근 좋아요 데이터 가져오기
        Query favQuery = database.getReference().child("trucks").child("info");
        favQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                favlist.clear();
                primaryKeys.clear();
                int count = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.child("stars").getValue() != null && snapshot.child("stars").getValue().toString().contains(auth.getCurrentUser().getUid())) {
                        primaryKeys.add(snapshot.getKey());
                        favlist.add(snapshot.getValue(ItemTruckDes.class));
                        count++;
                        if (count > 2) break;
                    }
                }
                favAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void connectEvent() {
        tv_user_nick.setOnClickListener(this);

        //이벤트연결
        btn_showfavAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ActivityUserInfo.this, ActivityMyTruck.class));
            }
        });

        btn_showrevAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ActivityUserInfo.this, ActivityReviewList.class));
            }
        });


        //사진 변경하기 (찍는 건 스킵, 원래 있는것 선택만 할수있음)
        iv_user_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TedPermission(ActivityUserInfo.this)
                        .setPermissionListener(mPermissionListener)
                        .setDeniedMessage("사진 접근과 카메라 접근을 허용해 주셔야 합니다")
                        .setPermissions(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                        .setGotoSettingButton(true)
                        .setGotoSettingButtonText("설정")
                        .check();
            }
        });

        btn_user_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //회원탈퇴
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(ActivityUserInfo.this)
                        .setMessage("탈퇴를 하시면 모든 정보가 삭제됩니다\n계속 하시겠습니까?")
                        .setPositiveButton("탈퇴", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                auth.getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            logOut();
                                            Toast.makeText(ActivityUserInfo.this, "탈퇴가 완료되었습니다", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(ActivityUserInfo.this, "탈퇴 실패", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                return;
                            }
                        });
                AlertDialog dialog = dialogBuilder.create();
                dialog.show();

            }
        });
    }

    public void uploadImage(Uri uri) {

        StorageReference storageRef = storage.getReferenceFromUrl("gs://movetodiner.appspot.com");

        Uri file = uri;
        StorageReference riversRef = storageRef.child("images/user/" + file.getLastPathSegment());
        UploadTask uploadTask = riversRef.putFile(file);

        //지금까지 스토리지에는 사진을 저장했고, 이제 프로필에 연결(저장)해야해 (아래의 콜백으로)
        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                @SuppressWarnings("VisibleForTests") //BUG , 적어줘야 에러안남
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();

                FirebaseUser user = auth.getCurrentUser();
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setPhotoUri(Uri.parse(downloadUrl.toString()))
                        .build();

                user.updateProfile(profileUpdates)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    BaseApplication.getInstance().progressOFF();
                                    Toast.makeText(ActivityUserInfo.this, "성공적으로 저장 됐습니다", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                            }
                        });
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_user_nick:
                AlertDialog.Builder alert = new AlertDialog.Builder(ActivityUserInfo.this);
                alert.setTitle("닉네임 변경");
                final EditText editText = new EditText(ActivityUserInfo.this);
                editText.setSingleLine(true);
                alert.setView(editText);
                alert.setPositiveButton("변경", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(editText.getText().toString()).build();
                        FirebaseUser user = auth.getCurrentUser();
                        user.updateProfile(profileUpdates)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            tv_user_nick.setText(auth.getCurrentUser().getDisplayName());
                                            Toast.makeText(ActivityUserInfo.this, "닉네임 변경이 완료되었습니다", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                });
                alert.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                alert.show();
                break;
            case R.id.action_save_user_info:
                if (imagePath != null) {
                    BaseApplication.getInstance().progressON(ActivityUserInfo.this, "사진을 저장하고 있습니다");
                    uploadImage(imagePath);
                }
                break;
        }
    }

    private class MyPermissionListener implements PermissionListener {
        @Override
        public void onPermissionGranted() {
            TedBottomPicker bottomSheetDialogFragment = new TedBottomPicker.Builder(ActivityUserInfo.this)
                    .setOnImageSelectedListener(new TedBottomPicker.OnImageSelectedListener() {
                        @Override
                        public void onImageSelected(final Uri uri) {
                            imagePath = uri;
                            Glide.with(ActivityUserInfo.this).load(imagePath).into(iv_user_photo);
                        }
                    })
                    .create();
            bottomSheetDialogFragment.show(getSupportFragmentManager());
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            Toast.makeText(ActivityUserInfo.this, "권한 필요 : " + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
        }
    }
}
