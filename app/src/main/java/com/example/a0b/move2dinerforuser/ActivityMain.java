package com.example.a0b.move2dinerforuser;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.example.a0b.move2dinerforuser.Adapter.BottomNavigationViewHelper;
import com.example.a0b.move2dinerforuser.Adapter.ViewPagerAdapter;
import com.example.a0b.move2dinerforuser.Fragment.FragmentHome;
import com.example.a0b.move2dinerforuser.Fragment.FragmentOthers;
import com.example.a0b.move2dinerforuser.Fragment.FragmentSearchTruck;


public class ActivityMain extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, BottomNavigationView.OnNavigationItemSelectedListener {

    private SwipeViewPager viewPager;
    private BottomNavigationView bottomNavigationView;
    private MenuItem prevMenuItem;
    private ViewPagerAdapter adapter;
    private BackPressCloseHandler backPressCloseHandler;

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BaseApplication.getInstance().progressON(ActivityMain.this, "데이터 로딩중입니다");
        viewPager = (SwipeViewPager) findViewById(R.id.viewPager);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavi);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        setupViewPager(viewPager);
        backPressCloseHandler = new BackPressCloseHandler(this);
    }

    private void setupViewPager(SwipeViewPager viewPager) {

        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(FragmentHome.newInstance());
        adapter.addFragment(FragmentSearchTruck.newInstance());
        adapter.addFragment(FragmentOthers.newInstance());

        viewPager.setPagingEnabled(false);
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (prevMenuItem != null) {
                    prevMenuItem.setChecked(false);
                } else {
                    bottomNavigationView.getMenu().getItem(0).setChecked(false);
                }
                bottomNavigationView.getMenu().getItem(position).setChecked(true);
                prevMenuItem = bottomNavigationView.getMenu().getItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        backPressCloseHandler.onBackPressed();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.btm_home) {
            viewPager.setCurrentItem(0);
        } else if (id == R.id.btm_search) {
            viewPager.setCurrentItem(1);
        } else if (id == R.id.btm_others) {
            viewPager.setCurrentItem(2);
        } else {

        }
        return false;
    }

}
