package com.kaipingzhou.paintfusheng.controller.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.jlmd.animatedcircleloadingview.AnimatedCircleLoadingView;
import com.kaipingzhou.paintfusheng.R;
import com.kaipingzhou.paintfusheng.model.Model;
import com.kaipingzhou.paintfusheng.utils.CacheUtils;
import com.kaipingzhou.paintfusheng.utils.PermissionUtils;
import com.kaipingzhou.paintfusheng.utils.ToastUtils;

import static com.kaipingzhou.paintfusheng.R.id.iv_splash;

public class SplashActivity extends Activity {

    public static final String START_GUIDE = "start_guide";

    private AnimatedCircleLoadingView animatedCircleLoadingView;
    private ImageView iv_logo;
    private TextView tv_splash;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (PermissionUtils.isGrantExternalRW(SplashActivity.this, 1)) {
                ToMainOrGuide();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTransluteWindow();

        getStatusBarHeight(this);

        setContentView(R.layout.activity_splash);

        initUI();

        startLoading();

        startPercentMockThread();

        animatedCircleLoadingView.setAnimationListener(new AnimatedCircleLoadingView.AnimationListener() {
            @Override
            public void onAnimationEnd(boolean success) {

                animatedCircleLoadingView.setVisibility(View.GONE);
                iv_logo.setVisibility(View.VISIBLE);
                tv_splash.setVisibility(View.VISIBLE);

                handler.sendEmptyMessageDelayed(1, 1500);
            }
        });
    }

    private void startLoading() {
        animatedCircleLoadingView.startDeterminate();
    }

    private void startPercentMockThread() {
        Model.getInstance().getGloblThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    for (int i = 0; i <= 100; i++) {
                        Thread.sleep(50);
                        changePercent(i);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void changePercent(final int percent) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                animatedCircleLoadingView.setPercent(percent);
            }
        });
    }

    private void ToMainOrGuide() {

        boolean isStartGuide = CacheUtils.getBoolean(SplashActivity.this, START_GUIDE);

        if (isStartGuide) {
            //进入过引导页，直接进入主界面
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
        } else {
            //没有进入过引导页，进入引导页
            Intent intent = new Intent(SplashActivity.this, GuideActivity.class);
            startActivity(intent);
        }
        //关闭当前页面
        finish();
    }

    /**
     * 请求权限回调
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ToMainOrGuide();
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtils.showToast(getApplicationContext(), "您的手机暂不适配哦~");
                        }
                    });
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void initUI() {
        animatedCircleLoadingView = (AnimatedCircleLoadingView) findViewById(iv_splash);
        iv_logo = (ImageView) findViewById(R.id.iv_logo);
        tv_splash = (TextView) findViewById(R.id.tv_splash);
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
}
