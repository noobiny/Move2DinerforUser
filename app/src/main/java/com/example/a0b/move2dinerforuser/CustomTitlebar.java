package com.example.a0b.move2dinerforuser;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


public class CustomTitlebar {
    TextView tv_titlebar, action_save_user_info;
    ImageView iv_arrow_back;
    public Activity activity;

    public CustomTitlebar(Activity _activity, String title) {
        activity = _activity;

        tv_titlebar = (TextView) activity.findViewById(R.id.tv_titlebar);
        iv_arrow_back = (ImageView) activity.findViewById(R.id.iv_arrow_back);

        tv_titlebar.setText(title);
        iv_arrow_back.setVisibility(View.VISIBLE);
        iv_arrow_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
            }
        });
    }

    public CustomTitlebar(Activity _activity, String title, int code) {
        activity = _activity;

        tv_titlebar = (TextView) activity.findViewById(R.id.tv_titlebar);
        iv_arrow_back = (ImageView) activity.findViewById(R.id.iv_arrow_back);
        action_save_user_info = (TextView) activity.findViewById(R.id.action_save_user_info);

        tv_titlebar.setText(title);
        iv_arrow_back.setVisibility(View.VISIBLE);
        action_save_user_info.setVisibility(View.VISIBLE);
        iv_arrow_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
            }
        });
    }
}
