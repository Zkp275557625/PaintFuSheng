package com.kaipingzhou.paintfusheng.controller.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;

import com.kaipingzhou.paintfusheng.R;
import com.kaipingzhou.paintfusheng.utils.PermissionUtils;
import com.kaipingzhou.paintfusheng.utils.ToastUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.ShareContentCustomizeCallback;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;

/**
 * 创建人：周开平
 * 创建时间：2017/4/19 23:01
 * 作用：
 */

public class ShareDetailActivity extends Activity {

    private ImageButton btn_back;
    private ImageButton btn_share;

    private Intent intent;
    private String path;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTransluteWindow();

        getStatusBarHeight(this);

        ShareSDK.initSDK(getApplicationContext(), "7d42489a27a670e4a7eb4937945ef303");

        setContentView(R.layout.activity_share_detail);

        initUI();

        initData();

        initListener();
    }

    private void initData() {
        intent = getIntent();
        path = intent.getStringExtra("path");
    }

    private void initListener() {
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //启动分享
//                ToastUtils.showToast(getApplicationContext(), "分享启动中...");
                if (PermissionUtils.isGrantExternalRW(ShareDetailActivity.this, 1)){
                    showShare();
                }
            }
        });
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
                    showShare();
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

    private void showShare() {
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        // title标题，印象笔记、邮箱、信息、微信、人人网、QQ和QQ空间使用
        oks.setTitle("快来体验浮生绘吧~");
        // titleUrl是标题的网络链接，仅在Linked-in,QQ和QQ空间使用
        oks.setTitleUrl("http://120.25.101.171:8080/app-release.apk");
        // text是分享文本，所有平台都需要这个字段
        oks.setText("浮生绘2.0上线啦~");
        //分享网络图片，新浪微博分享网络图片需要通过审核后申请高级写入接口，否则请注释掉测试新浪微博
//        oks.setImageUrl("http://f1.sharesdk.cn/imgs/2014/02/26/owWpLZo_638x960.jpg");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        final File file = new File(path);

        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        if (file.exists()) {
            oks.setImagePath(file.getAbsolutePath());
        } else {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.logo);
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 60, fos);

                fos.flush();
                fos.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
            oks.setImagePath(file.getAbsolutePath());
        }
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setShareContentCustomizeCallback(new ShareContentCustomizeCallback() {
            @Override
            public void onShare(Platform platform, Platform.ShareParams paramsToShare) {
                if (platform.getName().equalsIgnoreCase(QQ.NAME)) {
                    paramsToShare.setText(null);
                    paramsToShare.setTitle(null);
                    paramsToShare.setTitleUrl(null);
                    if (file.exists()) {
                        paramsToShare.setImagePath(file.getAbsolutePath());
                    } else {
                        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.logo);
                        FileOutputStream fos = null;
                        try {
                            fos = new FileOutputStream(file);
                            bitmap.compress(Bitmap.CompressFormat.PNG, 60, fos);

                            fos.flush();
                            fos.close();

                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        paramsToShare.setImagePath(file.getAbsolutePath());
                    }
                } else if (platform.getName().equalsIgnoreCase(QZone.NAME)) {
                    paramsToShare.setText(null);
                    paramsToShare.setTitle(null);
                    paramsToShare.setTitleUrl(null);
                    if (file.exists()) {
                        paramsToShare.setImagePath(file.getAbsolutePath());
                    } else {
                        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.logo);
                        FileOutputStream fos = null;
                        try {
                            fos = new FileOutputStream(file);
                            bitmap.compress(Bitmap.CompressFormat.PNG, 60, fos);

                            fos.flush();
                            fos.close();

                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        paramsToShare.setImagePath(file.getAbsolutePath());
                    }
                }

            }
        });
        oks.setUrl("http://120.25.101.171:8080/app-release.apk");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("快来体验浮生绘吧~");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite("浮生绘");
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://120.25.101.171:8080/app-release.apk");

        // 启动分享GUI
        oks.show(this);
    }

    private void initUI() {
        btn_back = (ImageButton) findViewById(R.id.btn_back);
        btn_share = (ImageButton) findViewById(R.id.btn_share);

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
