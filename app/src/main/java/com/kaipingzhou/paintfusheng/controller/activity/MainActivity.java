package com.kaipingzhou.paintfusheng.controller.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kaipingzhou.paintfusheng.R;
import com.kaipingzhou.paintfusheng.controller.view.horizontalscrollviewpager.HorizontalScrollViewPager;
import com.kaipingzhou.paintfusheng.utils.DensityUtils;
import com.kaipingzhou.paintfusheng.utils.LogUtils;
import com.kaipingzhou.paintfusheng.utils.ToastUtils;

import static android.view.KeyEvent.KEYCODE_BACK;

/**
 * 创建人：周开平
 * 创建时间：2017/4/17 20:51
 * 作用：主界面
 */

public class MainActivity extends Activity {

    /**
     * 从相册选择照片
     */
    private static final int SELECT_IMAGE_CODE = 1;

    private Button btn_back;
    private ImageView iv_title;

    private ImageButton btn_camera;
    private ImageButton btn_album;

    private RelativeLayout rl_photo;
    private RelativeLayout rl_function;

    private ImageButton btn_classic_hand;
    private ImageButton btn_dream_life;
    private ImageButton btn_color_world;
    private ImageButton btn_paint_life;
    private ImageButton btn_classic_filter;

    private boolean rl_photo_visible = true;
    private boolean rl_function_visible = false;

    private HorizontalScrollViewPager viewpager;
    private TextView tv_desc;
    private LinearLayout ll_point_group;

    private static int[] images;
    private static String[] descs;

    private Bitmap bitmap;

    /**
     * 照片来源 默认是从相册选择
     */
    private String PHOTO = "album";
    /**
     * 滤镜分组类型 默认为经典手绘
     */
    private String FILTER = "classic_hand";

    /**
     * 是否已经退出
     */
    private boolean isExit = false;

    private MyInternalHandler myInternalHandler;

    /**
     * 之前高亮显示的点的位置
     */
    private int prePosition;

    class MyInternalHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //切换viewPager的下一个页面
            int item = (viewpager.getCurrentItem() + 1) % images.length;
            viewpager.setCurrentItem(item);

            myInternalHandler.postDelayed(new MyRunnable(), 4000);
        }
    }

    class MyRunnable implements Runnable {

        @Override
        public void run() {
            myInternalHandler.sendEmptyMessage(0);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTransluteWindow();

        getStatusBarHeight(this);

        setContentView(R.layout.activity_main);

        initUI();

        initData();

        initListener();
    }


    private void initData() {
        images = new int[]{R.drawable.index_img_1, R.drawable.index_img_2, R.drawable.index_img_3, R.drawable.index_img_4};
        descs = new String[]{"铅笔画", "彩色铅笔画", "噪点特效", "油画"};
        viewpager.setAdapter(new SlideShowAdapter());
        //添加点
        addPoint();

        prePosition = 0;

        viewpager.addOnPageChangeListener(new MyOnPageChangeListener());

        //发消息，每隔5秒切换一次图片
        if (myInternalHandler == null) {
            myInternalHandler = new MyInternalHandler();
        }

        //把消息队列所有的消息和回调移除
        myInternalHandler.removeCallbacksAndMessages(null);
        myInternalHandler.postDelayed(new MyRunnable(), 4000);
    }

    private void addPoint() {
        ll_point_group.removeAllViews();

        for (int i = 0; i < descs.length; i++) {
            ImageView imageView = new ImageView(getApplicationContext());

            imageView.setBackgroundResource(R.drawable.point_selector);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    DensityUtils.dip2px(getApplicationContext(), 5), DensityUtils.dip2px(getApplicationContext(), 5));

            if (i == 0) {
                imageView.setEnabled(true);
            } else {
                imageView.setEnabled(false);
                params.leftMargin = DensityUtils.dip2px(getApplicationContext(), 5);
            }

            imageView.setLayoutParams(params);

            ll_point_group.addView(imageView);
        }
    }

    class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            tv_desc.setText(descs[position]);
            //把之前的变为灰色
            ll_point_group.getChildAt(prePosition).setEnabled(false);
            //对应页面的点为红色
            ll_point_group.getChildAt(position).setEnabled(true);

            prePosition = position;
        }

        private boolean isDragging = false;

        @Override
        public void onPageScrollStateChanged(int state) {
            if (state == ViewPager.SCROLL_STATE_DRAGGING) {
                LogUtils.e("拖拽状态");
                //拖拽状态
                isDragging = true;
                //移除之前所有的消息和回调
                myInternalHandler.removeCallbacksAndMessages(null);

            } else if (state == ViewPager.SCROLL_STATE_SETTLING && isDragging) {
                LogUtils.e("惯性滚动状态");
                //惯性滚动状态
                isDragging = false;
                //移除之前所有的消息和回调
                myInternalHandler.removeCallbacksAndMessages(null);
                myInternalHandler.postDelayed(new MyRunnable(), 4000);

            } else if (state == ViewPager.SCROLL_STATE_IDLE) {
                LogUtils.e("静止状态");
                //静止状态
                isDragging = false;
                //移除之前所有的消息和回调
                myInternalHandler.removeCallbacksAndMessages(null);
                myInternalHandler.postDelayed(new MyRunnable(), 4000);
            }
        }
    }

    private void initListener() {

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rl_photo_visible) {
                    //拍照可见
                    ToastUtils.showToast(getApplicationContext(), "已经是首页了");
                } else if (rl_function_visible) {
                    //功能选择可见
                    rl_photo.setVisibility(View.VISIBLE);
                    rl_photo_visible = true;

                    rl_function.setVisibility(View.GONE);
                    rl_function_visible = false;

                    bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.index_title_appname);
                    iv_title.setImageBitmap(bitmap);
                }
            }
        });


        btn_classic_hand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        LogUtils.e("经典手绘");

                        FILTER = "classic_hand";

                        if (PHOTO.equals("camera")) {
                            //拍照
                            Intent intent = new Intent(MainActivity.this, CameraActivity.class);
                            intent.putExtra("filter", FILTER);
                            startActivity(intent);

                        } else if (PHOTO.equals("album")) {
                            //从相册选
                            Intent intent = new Intent(MainActivity.this, AlbumActivity.class);
                            intent.putExtra("filter", FILTER);
                            startActivityForResult(intent, SELECT_IMAGE_CODE);
                        }
                    }
                });
            }
        });

        btn_dream_life.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        LogUtils.e("梦幻浮生");

                        FILTER = "dream_life";

                        if (PHOTO.equals("camera")) {
                            //拍照
                            Intent intent = new Intent(MainActivity.this, CameraActivity.class);
                            intent.putExtra("filter", FILTER);
                            startActivity(intent);

                        } else if (PHOTO.equals("album")) {
                            //从相册选
                            Intent intent = new Intent(MainActivity.this, AlbumActivity.class);
                            intent.putExtra("filter", FILTER);
                            startActivityForResult(intent, SELECT_IMAGE_CODE);
                        }
                    }
                });
            }
        });

        btn_color_world.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        LogUtils.e("色彩世界");

                        FILTER = "color_world";

                        if (PHOTO.equals("camera")) {
                            //拍照
                            Intent intent = new Intent(MainActivity.this, CameraActivity.class);
                            intent.putExtra("filter", FILTER);
                            startActivity(intent);

                        } else if (PHOTO.equals("album")) {
                            //从相册选
                            Intent intent = new Intent(MainActivity.this, AlbumActivity.class);
                            intent.putExtra("filter", FILTER);
                            startActivityForResult(intent, SELECT_IMAGE_CODE);
                        }
                    }
                });
            }
        });

        btn_paint_life.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        LogUtils.e("浮绘生活");

                        FILTER = "paint_life";

                        if (PHOTO.equals("camera")) {
                            //拍照
                            Intent intent = new Intent(MainActivity.this, CameraActivity.class);
                            intent.putExtra("filter", FILTER);
                            startActivity(intent);

                        } else if (PHOTO.equals("album")) {
                            //从相册选
                            Intent intent = new Intent(MainActivity.this, AlbumActivity.class);
                            intent.putExtra("filter", FILTER);
                            startActivityForResult(intent, SELECT_IMAGE_CODE);
                        }
                    }
                });
            }
        });

        btn_classic_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.e("经典滤镜");

                FILTER = "classic_filter";

                if (PHOTO.equals("camera")) {
                    //拍照
                    Intent intent = new Intent(MainActivity.this, CameraActivity.class);
                    intent.putExtra("filter", FILTER);
                    startActivity(intent);

                } else if (PHOTO.equals("album")) {
                    //从相册选
                    Intent intent = new Intent(MainActivity.this, AlbumActivity.class);
                    intent.putExtra("filter", FILTER);
                    startActivityForResult(intent, SELECT_IMAGE_CODE);
                }
            }
        });

        btn_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LogUtils.e("拍照");

                rl_function.setVisibility(View.VISIBLE);
                rl_function_visible = true;
                rl_photo.setVisibility(View.GONE);
                rl_photo_visible = false;

                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.index_title_function);
                iv_title.setImageBitmap(bitmap);

                PHOTO = "camera";

//                Intent intent = new Intent(MainActivity.this, CameraActivity.class);
//                startActivity(intent);
            }
        });


        btn_album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LogUtils.e("从相册选");

                rl_function.setVisibility(View.VISIBLE);
                rl_function_visible = true;
                rl_photo.setVisibility(View.GONE);
                rl_photo_visible = false;

                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.index_title_function);
                iv_title.setImageBitmap(bitmap);

                PHOTO = "album";
//                Intent intent = new Intent(MainActivity.this, AlbumActivity.class);
//                startActivityForResult(intent, SELECT_IMAGE_CODE);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == SELECT_IMAGE_CODE) {
            //从手机相册选择图片
            if (data != null) {
                String imgPath = data.getStringExtra("imgPath");
                String filter = data.getStringExtra("filter");
                Intent intent = new Intent(this, EditImgActivity.class);
                intent.putExtra("picsaveurl", imgPath);
                intent.putExtra("filter", filter);
                startActivity(intent);
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KEYCODE_BACK) {
            if (!isExit) {
                isExit = true;
                ToastUtils.showToast(getApplicationContext(), "再按一次退出浮生绘");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isExit = false;
                    }
                }, 2000);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void initUI() {
        btn_back = (Button) findViewById(R.id.btn_back);
        iv_title = (ImageView) findViewById(R.id.iv_title);

        btn_camera = (ImageButton) findViewById(R.id.btn_camera);
        btn_album = (ImageButton) findViewById(R.id.btn_album);

        rl_photo = (RelativeLayout) findViewById(R.id.rl_photo);
        rl_photo.setVisibility(View.VISIBLE);

        rl_function = (RelativeLayout) findViewById(R.id.rl_function);
        rl_function.setVisibility(View.GONE);

        btn_classic_hand = (ImageButton) findViewById(R.id.btn_classic_hand);
        btn_dream_life = (ImageButton) findViewById(R.id.btn_dream_life);
        btn_color_world = (ImageButton) findViewById(R.id.btn_color_world);
        btn_paint_life = (ImageButton) findViewById(R.id.btn_paint_life);
        btn_classic_filter = (ImageButton) findViewById(R.id.btn_classic_filter);

        viewpager = (HorizontalScrollViewPager) findViewById(R.id.viewpager);
        tv_desc = (TextView) findViewById(R.id.tv_desc);
        ll_point_group = (LinearLayout) findViewById(R.id.ll_point_group);
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

    private class SlideShowAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return images.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView imageView = new ImageView(getApplicationContext());
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setBackgroundResource(R.mipmap.logo);

            container.addView(imageView);

            int image = images[position];
//            String desc = descs[position];

            imageView.setBackgroundResource(image);
//            tv_desc.setText(desc);

            imageView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            //按下
                            LogUtils.e("按下");
                            //把之前的所有消息和回调移除
                            myInternalHandler.removeCallbacksAndMessages(null);
                            break;
                        case MotionEvent.ACTION_UP:
                            //离开
                            LogUtils.e("离开");
                            //把之前的所有消息和回调移除
                            myInternalHandler.removeCallbacksAndMessages(null);
                            myInternalHandler.postDelayed(new MyRunnable(), 4000);
                            break;
                        case MotionEvent.ACTION_CANCEL:
//                            //取消
                            LogUtils.e("取消");
                            //把之前的所有消息和回调移除
                            myInternalHandler.removeCallbacksAndMessages(null);
                            myInternalHandler.postDelayed(new MyRunnable(), 4000);
                            break;
                    }
                    return true;
                }
            });

            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
