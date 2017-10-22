package com.kaipingzhou.paintfusheng.controller.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.format.Time;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.kaipingzhou.paintfusheng.R;
import com.kaipingzhou.paintfusheng.controller.view.imageview.ImageViewTouch;
import com.kaipingzhou.paintfusheng.model.Model;
import com.kaipingzhou.paintfusheng.utils.CacheUtils;
import com.kaipingzhou.paintfusheng.utils.Constants;
import com.kaipingzhou.paintfusheng.utils.ImageHelper;
import com.kaipingzhou.paintfusheng.utils.LogUtils;
import com.kaipingzhou.paintfusheng.utils.PermissionUtils;
import com.kaipingzhou.paintfusheng.utils.ToastUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

/**
 * 创建人：周开平
 * 创建时间：2017/4/18 0:32
 * 作用：拍照界面
 */

public class CameraActivity extends Activity {

    private ImageButton btn_back;
    private ImageButton btn_take;
    private ImageButton btn_take_done;
    private ImageButton btn_retake;

    private ImageView iv_img;

    private SurfaceView surfaceview;

    private LinearLayout dialog_view;

    private Camera camera;
    private Camera.Parameters parameters;

    private String picsaveurl;

    private Intent intent;
    private String filter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTransluteWindow();

        getStatusBarHeight(this);

        setContentView(R.layout.activity_camera);

        intent = getIntent();

        filter = intent.getStringExtra("filter");

        initUI();

        initListener();
    }

    private void initListener() {
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //关闭当前界面
                finish();
            }
        });

        btn_take.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        iv_img.setVisibility(View.VISIBLE);

                        if (PermissionUtils.isGrantExternalRW(CameraActivity.this, 1)) {
                            takePhotoAndSave();
                        }
//                        surfaceview.setVisibility(View.GONE);
                        btn_take.setVisibility(View.INVISIBLE);
                        btn_take_done.setVisibility(View.VISIBLE);
                        btn_retake.setVisibility(View.VISIBLE);
                    }
                });
            }
        });

        btn_take_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PermissionUtils.isGrantExternalRW(CameraActivity.this, 2)) {
                    gotoEditImgActivity(filter);
                }
            }
        });

        btn_retake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //重新拍照
                dialog_view.setVisibility(View.VISIBLE);
                Model.getInstance().getGloblThreadPool().execute(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(CameraActivity.this, CameraActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
//                surfaceview.setVisibility(View.VISIBLE);
//                iv_img.setVisibility(View.GONE);
                dialog_view.setVisibility(View.GONE);
            }
        });
    }

    private void gotoEditImgActivity(String filter) {
        dialog_view.setVisibility(View.VISIBLE);
        Intent intent = new Intent(CameraActivity.this, EditImgActivity.class);
        String picsaveurl = CacheUtils.getString(getApplicationContext(), "picsaveurl", 0);
        LogUtils.e(picsaveurl);
        intent.putExtra("picsaveurl", picsaveurl);
        intent.putExtra("filter", filter);
        dialog_view.setVisibility(View.INVISIBLE);
        startActivity(intent);
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
                    takePhotoAndSave();
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtils.showToast(getApplicationContext(), "您的手机暂不适配哦~");
                        }
                    });
                }
                break;

            case 2:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    gotoEditImgActivity(filter);
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

    private void takePhotoAndSave() {
        dialog_view.setVisibility(View.VISIBLE);
        camera.takePicture(mShutter, null, mJpeg);
        dialog_view.setVisibility(View.GONE);
    }

    private void initUI() {

        dialog_view = (LinearLayout) findViewById(R.id.dialog_view);
        dialog_view.setVisibility(View.INVISIBLE);

        surfaceview = (SurfaceView) findViewById(R.id.surfaceview);
        surfaceview.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
//        surfaceview.getHolder().setFixedSize(1080, 1920); // 设置Surface分辨率
        surfaceview.getHolder().setKeepScreenOn(true);// 屏幕常亮
        surfaceview.getHolder().addCallback(new SurfaceCallback());// 为SurfaceView的句柄添加一个回调函数

        iv_img = (ImageView) findViewById(R.id.iv_img);
        iv_img.setScaleType(ImageView.ScaleType.FIT_XY);

        btn_back = (ImageButton) findViewById(R.id.btn_back);

        btn_take = (ImageButton) findViewById(R.id.btn_take);
        btn_take.setVisibility(View.VISIBLE);

        btn_take_done = (ImageButton) findViewById(R.id.btn_take_done);

        btn_retake = (ImageButton) findViewById(R.id.btn_retake);
        btn_retake.setVisibility(View.INVISIBLE);
    }

    //重构相机照相回调类
    private final class SurfaceCallback implements SurfaceHolder.Callback {
        @SuppressWarnings("deprecation")
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            //实景设计模块需要 相机与读写手机存储 两个敏感权限，需要动态获取
            parameters = camera.getParameters(); // 获取各项参数
            parameters.setPictureFormat(PixelFormat.JPEG); // 设置图片格式
            Camera.Size bestSize;

            List<Camera.Size> sizeList = camera.getParameters().getSupportedPreviewSizes();
            bestSize = sizeList.get(0);

            for (int i = 1; i < sizeList.size(); i++) {
                if ((sizeList.get(i).width * sizeList.get(i).height) >
                        (bestSize.width * bestSize.height)) {
                    bestSize = sizeList.get(i);
                }
            }

            parameters.setPreviewSize(bestSize.width, bestSize.height); // 设置预览大小
//            parameters.setPreviewSize(1080, 1920); // 设置预览大小
            parameters.setJpegQuality(100); // 设置照片质量
            parameters.setPictureSize(bestSize.width, bestSize.height);
//            parameters.setPictureSize(1080, 1920);
            //前面参数的设置不起效 为默认模式 如果要设置 取消注释即可
            /*camera.setParameters(parameters);*/
            camera.startPreview(); // 开始预览
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            try {
                camera = Camera.open(); // 打开摄像头
                camera.setPreviewDisplay(holder); // 设置用于显示拍照影像的SurfaceHolder对象
                camera.setDisplayOrientation(getPreviewDegree(CameraActivity.this));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            if (camera != null) {
                camera.stopPreview();
                camera.release(); // 释放照相机
                camera = null;
            }
        }
    }

    // 提供一个静态方法，用于根据手机方向获得相机预览画面旋转的角度
    public static int getPreviewDegree(Activity activity) {
        // 获得手机的方向
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int degree = 0;
        // 根据手机的方向计算相机预览画面应该选择的角度
        switch (rotation) {
            case Surface.ROTATION_0:
                degree = 90;
                break;
            case Surface.ROTATION_90:
                degree = 0;
                break;
            case Surface.ROTATION_180:
                degree = 270;
                break;
            case Surface.ROTATION_270:
                degree = 180;
                break;
        }
        return degree;
    }

    /**
     * 图像数据处理还未完成时的回调函数
     */
    private Camera.ShutterCallback mShutter = new Camera.ShutterCallback() {
        @Override
        public void onShutter() {
            // 一般显示进度条
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    dialog_view.setVisibility(View.VISIBLE);
//                }
//            });
        }
    };

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


    /**
     * 图像数据处理完成后的回调函数
     */
    private Camera.PictureCallback mJpeg = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(final byte[] data, Camera camera) {
            //在子线程中完成耗时操作
            Model.getInstance().getGloblThreadPool().execute(new Runnable() {
                @Override
                public void run() {

                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                    Matrix matrixs = new Matrix();
                    matrixs.setRotate(getPreviewDegree(CameraActivity.this));
                    bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrixs, true);

                    //压缩图片至1080
                    bitmap = ImageHelper.zoomImage(bitmap);

                    final Bitmap finalBitmap = bitmap;

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            iv_img.setImageBitmap(finalBitmap);
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

//                        picsaveurl = SavePath;

                        CacheUtils.putString(getApplicationContext(), "picsaveurl", SavePath);

                    } catch (Exception e) {
                    }
                }
            });
        }
    };

    /**
     * 获取和保存当前屏幕的截图
     */
    private void GetandSaveCurrentImage(Activity activity) {
        // 获取windows中最顶层的view
        View view = activity.getWindow().getDecorView();
        view.buildDrawingCache();
        // 获取状态栏高度
        Rect rect = new Rect();
        view.getWindowVisibleDisplayFrame(rect);
        int statusBarHeights = rect.top;
        Display display = activity.getWindowManager().getDefaultDisplay();
        // 获取屏幕宽和高
        int widths = display.getWidth();
        int heights = display.getHeight();
        // 允许当前窗口保存缓存信息
        view.setDrawingCacheEnabled(true);
        // 去掉状态栏
        Bitmap Bmp = Bitmap.createBitmap(view.getDrawingCache(), 0,
                statusBarHeights, widths, heights - statusBarHeights);
        // 销毁缓存信息
        view.destroyDrawingCache();
        String currenttime = System.currentTimeMillis() + "";
        String SavePath = Constants.APP_PATH + currenttime + ".jpg";

        // 3.保存Bitmap
        try {
            File file = new File(SavePath);

            if (!file.getParentFile().exists())
                file.getParentFile().mkdirs();

            FileOutputStream fos = new FileOutputStream(file);
            Bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);

            fos.flush();
            fos.close();

            //保存图片后发送广播通知更新数据库
            Uri uri = Uri.fromFile(file);
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));

        } catch (Exception e) {
        }
        picsaveurl = SavePath + currenttime + ".jpg";
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
