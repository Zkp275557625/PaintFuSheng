package com.kaipingzhou.paintfusheng.controller.view.horizontalscrollviewpager;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 创建人：周开平
 * 创建时间：2017/4/21 17:02
 * 作用：水平方向滑动的viewPager
 */

public class HorizontalScrollViewPager extends ViewPager {

    public HorizontalScrollViewPager(Context context) {
        super(context);
    }

    public HorizontalScrollViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private float startX;
    private float startY;


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //请求父层视图不拦截当前控件的事件
                getParent().requestDisallowInterceptTouchEvent(true);
                //记录起始坐标
                startX = ev.getX();
                startY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                //新的坐标
                float endX = ev.getX();
                float endY = ev.getY();
                //计算偏移量
                float disX = endX - startX;
                float disY = endY - startY;
                //判断滑动方向
                if (Math.abs(disX) > Math.abs(disY)) {
                    //水平方向滑动
                    if (getCurrentItem() == 0 && disX > 0) {
                        //请求父层视图拦截当前控件的事件
                        getParent().requestDisallowInterceptTouchEvent(false);

                    } else if ((getCurrentItem() == (getAdapter().getCount() - 1)) && disX < 0) {
                        //请求父层视图拦截当前控件的事件
                        getParent().requestDisallowInterceptTouchEvent(false);
                    } else {
                        //请求父层视图不拦截当前控件的事件
                        getParent().requestDisallowInterceptTouchEvent(true);
                    }
                } else {
                    //竖直方向滑动
                    //请求父层视图拦截当前控件的事件
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }
}

