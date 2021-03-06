package com.example.a0b.move2dinerforuser.Adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.RequestManager;
import com.example.a0b.move2dinerforuser.R;

import java.util.ArrayList;

public class ImageSliderAdapter extends PagerAdapter {

    Context mContext;
    LayoutInflater mLayoutInflater;
    ArrayList<String> mResources;
    private final RequestManager glide;

    public ImageSliderAdapter(Context context, ArrayList<String> mResources, RequestManager glide) {
        this.glide = glide;
        mContext = context;
        this.mResources = mResources;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        View itemView = mLayoutInflater.inflate(R.layout.viewpager_image, container, false);

        int realPos = position % mResources.size();
        ImageView imageView = (ImageView) itemView.findViewById(R.id.img_viewpager_childimage);
        glide.load(mResources.get(realPos)).placeholder(R.drawable.loadingimage).error(R.drawable.loadingimage).into(imageView);

        container.addView(itemView, 0);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }

}
