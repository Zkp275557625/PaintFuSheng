package com.kaipingzhou.paintfusheng.controller.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Window;
import android.view.WindowManager;

import com.kaipingzhou.paintfusheng.R;
import com.kaipingzhou.paintfusheng.controller.fragment.BucketsFragment;
import com.kaipingzhou.paintfusheng.controller.fragment.ImagesFragment;

/**
 * 创建人：周开平
 * 创建时间：2017/4/18 1:11
 * 作用：选择图片界面
 */

public class AlbumActivity extends FragmentActivity {

    private Intent intent;
    private String filter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTransluteWindow();

        getStatusBarHeight(this);

        setResult(RESULT_CANCELED);

        intent = getIntent();
        filter = intent.getStringExtra("filter");

        Fragment newFragment = new BucketsFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(android.R.id.content, newFragment);

        transaction.commit();
    }

    /**
     * 设置状态栏透明
     */
    public void setTransluteWindow() {
        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    }

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public void showBucket(final int bucketId) {
        Bundle b = new Bundle();
        b.putInt("bucket", bucketId);
        Fragment f = new ImagesFragment();
        f.setArguments(b);
        getSupportFragmentManager().beginTransaction().replace(android.R.id.content, f).addToBackStack(null).commit();
    }

    public void imageSelected(final String imgPath, final String imgTaken, final long imageSize) {
        Intent result = new Intent();
        result.putExtra("imgPath", imgPath);
        result.putExtra("dateTaken", imgTaken);
        result.putExtra("imageSize", imageSize);
        result.putExtra("filter", filter);
        setResult(RESULT_OK, result);
        finish();
    }
}
