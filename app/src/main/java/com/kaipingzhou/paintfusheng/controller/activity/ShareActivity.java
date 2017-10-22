package com.kaipingzhou.paintfusheng.controller.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.format.Time;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.kaipingzhou.paintfusheng.R;
import com.kaipingzhou.paintfusheng.controller.view.imageview.ImageViewTouch;
import com.kaipingzhou.paintfusheng.model.Model;
import com.kaipingzhou.paintfusheng.utils.Constants;
import com.kaipingzhou.paintfusheng.utils.ImageHelper;

import java.io.File;
import java.io.FileOutputStream;

/**
 * 创建人：周开平
 * 创建时间：2017/4/18 11:37
 * 作用：分享照片界面
 */

public class ShareActivity extends Activity {

    private ImageButton btn_back;
    private ImageButton btn_save_share;
    private LinearLayout ll_loading;

    private ImageViewTouch iv_img;

    private Bitmap bitmap;

    private Intent intent;
    private String picsaveurl;
    private String drawingCache;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTransluteWindow();

        getStatusBarHeight(this);


        setContentView(R.layout.activity_share);

        initUI();

        initData();

        initListener();
    }



    private void initData() {
        intent = getIntent();
        if (intent != null) {
            drawingCache = intent.getStringExtra("drawingCache");
            if (drawingCache.equals("ok")) {
                bitmap = EditImgActivity.getMainBitmap();

                Bitmap waterMask = BitmapFactory.decodeResource(getResources(), R.drawable.watermask);

                bitmap = ImageHelper.WaterMask(bitmap, waterMask);

                iv_img.setImageBitmap(bitmap);
            }
        }
    }

    /**
     * 获取系统当前时间
     */
    private String getSystemTime() {
        Time t = new Time();
        t.setToNow(); // 取得系统时间。
        int year = t.year;
        int month = t.month + 1;
        int date = t.monthDay;
        int hour = t.hour; // 0-23
        int minute = t.minute;
        int second = t.second;

        String monthStr = month + "";
        if (monthStr.length() == 1) {
            monthStr = "0" + monthStr;
        }

        String dateStr = date + "";
        if (dateStr.length() == 1) {
            dateStr = "0" + dateStr;
        }

        String hourStr = hour + "";
        if (hourStr.length() == 1) {
            hourStr = "0" + hourStr;
        }

        String minuteStr = minute + "";
        if (minuteStr.length() == 1) {
            minuteStr = "0" + minuteStr;
        }

        String secondStr = second + "";
        if (secondStr.length() == 1) {
            secondStr = "0" + secondStr;
        }

        String sysTime = year + "" + monthStr + "" + dateStr + "-" + hourStr + "" + minuteStr + "" + secondStr;

        return sysTime;
    }

    private void initListener() {
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_save_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Model.getInstance().getGloblThreadPool().execute(new Runnable() {
                    @Override
                    public void run() {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ll_loading.setVisibility(View.VISIBLE);
                            }
                        });

                        String currenttime = getSystemTime();

                        String SavePath = Constants.APP_PATH + "FSH" + currenttime + ".jpg";

                        //保存Bitmap
                        try {
                            File file = new File(SavePath);

                            if (!file.getParentFile().exists())
                                file.getParentFile().mkdirs();

                            FileOutputStream fos = new FileOutputStream(file);
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);

                            fos.flush();
                            fos.close();

                            //保存图片后发送广播通知更新数据库
                            Uri uri = Uri.fromFile(file);
                            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ll_loading.setVisibility(View.INVISIBLE);
                                }
                            });

                            Intent intent = new Intent(ShareActivity.this, ShareDetailActivity.class);
                            intent.putExtra("path", SavePath);
                            startActivity(intent);

                        } catch (Exception e) {
                        }
                    }
                });
            }
        });
    }

    private void initUI() {
        btn_back = (ImageButton) findViewById(R.id.btn_back);
        btn_save_share = (ImageButton) findViewById(R.id.btn_save_share);
        iv_img = (ImageViewTouch) findViewById(R.id.iv_img);

        ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
        ll_loading.setVisibility(View.INVISIBLE);
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
