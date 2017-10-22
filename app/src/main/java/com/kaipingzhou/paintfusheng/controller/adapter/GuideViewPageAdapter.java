package com.kaipingzhou.paintfusheng.controller.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * 创建人：周开平
 * 创建时间：2017/4/17 21:17
 * 作用：
 */

public class GuideViewPageAdapter extends PagerAdapter {

    private final Context mContext;
    private final ArrayList<ImageView> mImageViews;

    public GuideViewPageAdapter(Context context, ArrayList<ImageView> imageViews) {
        this.mContext = context;
        this.mImageViews = imageViews;
    }

    /**
     * 返回数据的总个数
     *
     * @return
     */
    @Override
    public int getCount() {
        return mImageViews == null ? 0 : mImageViews.size();
    }

    /**
     * 作用getView
     *
     * @param container viewpager
     * @param position  要创建页面的位置
     * @return 返回和当前页面有关系的值
     */
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView imageView = mImageViews.get(position);
        //添加到容器中
        container.addView(imageView);
        return imageView;
    }

    /**
     * 判断
     *
     * @param view   当前创建的视图
     * @param object 上面instantiateItem返回的值
     * @return
     */
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    /**
     * 销毁页面
     *
     * @param container
     * @param position
     * @param object
     */
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
