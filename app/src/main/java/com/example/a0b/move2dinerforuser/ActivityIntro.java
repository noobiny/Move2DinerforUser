package com.example.a0b.move2dinerforuser;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

public class ActivityIntro extends AppCompatActivity {

    private FirebaseRemoteConfig firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
    public static FirebaseDatabase truckdatabase;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBackPressed() {
        finishAffinity();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        firebaseRemoteConfig.setConfigSettings(configSettings);
        firebaseRemoteConfig.setDefaults(R.xml.default_config);


        firebaseRemoteConfig.fetch(3600)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            firebaseRemoteConfig.activateFetched();
                        } else {
                        }
                        serverInspection();
                    }
                });
    }

    //원격으로 서버 점검하기
    private void serverInspection() {
        boolean inspection = firebaseRemoteConfig.getBoolean("splash_message_inspection");
        String splash_message = firebaseRemoteConfig.getString("splash_message");
        if (inspection) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(splash_message).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            builder.create().show();
        } else {

            if (truckdatabase == null) {
                FirebaseOptions options = new FirebaseOptions.Builder()
                        .setApiKey("AIzaSyBB14lXk-lNAVPTp7vBH7YKqJgQ3q_js4o")
                        .setApplicationId("com.example.a0b.move2dinerforuser")
                        .setDatabaseUrl("https://move2diner.firebaseio.com/")
                        .build();
                truckdatabase = FirebaseDatabase.getInstance(FirebaseApp.initializeApp(getApplicationContext(), options, "second app"));

            }

            MobileAds.initialize(this,
                    "ca-app-pub-8609705965365501~1080265776");

            startActivity(new Intent(ActivityIntro.this, ActivitySelectAuth.class));
            finish();

        }

    }
}
