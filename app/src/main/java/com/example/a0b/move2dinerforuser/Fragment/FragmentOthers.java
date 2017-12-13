package com.example.a0b.move2dinerforuser.Fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.a0b.move2dinerforuser.ActivityMyTruck;
import com.example.a0b.move2dinerforuser.ActivityReviewList;
import com.example.a0b.move2dinerforuser.ActivitySelectAuth;
import com.example.a0b.move2dinerforuser.ActivityUserInfo;
import com.example.a0b.move2dinerforuser.R;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;

import static com.example.a0b.move2dinerforuser.ActivitySelectAuth.mGoogleApiClient;


public class FragmentOthers extends Fragment implements View.OnClickListener {
    Button BtnMyInfo, BtnMyTruck, BtnMyReview;
    private FirebaseAuth auth;

    public FragmentOthers() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_others, container, false);
        BtnMyInfo = (Button) rootView.findViewById(R.id.btnMyInfo);
        BtnMyTruck = (Button) rootView.findViewById(R.id.btnMyTruck);
        BtnMyReview = (Button) rootView.findViewById(R.id.btnMyReview);

        auth = FirebaseAuth.getInstance();

        BtnMyInfo.setOnClickListener(this);
        BtnMyTruck.setOnClickListener(this);
        BtnMyReview.setOnClickListener(this);
        rootView.findViewById(R.id.btnChangeAuth).setOnClickListener(this);
        return rootView;
    }

    public static FragmentOthers newInstance() {
        return new FragmentOthers();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnMyInfo:
                startActivity(new Intent(getActivity(), ActivityUserInfo.class));
                break;

            case R.id.btnMyReview:
                startActivity(new Intent(getActivity(), ActivityReviewList.class));
                break;

            case R.id.btnMyTruck:
                startActivity(new Intent(getActivity(), ActivityMyTruck.class));
                break;

            case R.id.btnChangeAuth:
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext())
                        .setMessage("로그 아웃 하시겠습니까?")
                        .setPositiveButton("로그 아웃", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                logOut();

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
                break;
        }
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
                                                        startActivity(new Intent(getActivity(), ActivitySelectAuth.class));
                                getActivity().finish();
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
}
