package com.kaipingzhou.paintfusheng.controller.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hqu.cst.sketcher.ImageHelperJNI;
import com.kaipingzhou.paintfusheng.R;
import com.kaipingzhou.paintfusheng.controller.view.imageview.ImageViewTouch;
import com.kaipingzhou.paintfusheng.model.Model;
import com.kaipingzhou.paintfusheng.utils.DensityUtils;
import com.kaipingzhou.paintfusheng.utils.ImageHelper;
import com.kaipingzhou.paintfusheng.utils.LogUtils;
import com.kaipingzhou.paintfusheng.utils.PermissionUtils;
import com.kaipingzhou.paintfusheng.utils.ToastUtils;
import com.xinlan.imageedit.editimage.filter.PhotoProcessing;

/**
 * 创建人：周开平
 * 创建时间：2017/4/18 10:39
 * 作用：图片编辑界面
 */

public class EditImgActivity extends FragmentActivity {

    private ImageButton btn_back;
    private ImageButton btn_ok;

    private ImageViewTouch iv_img;

    private TextView tv_filter_title;

    /**
     * 经典手绘
     */
    private HorizontalScrollView hsv_classic_hand;
    private ImageView iv_original;
    private ImageView iv_pencil;
    private ImageView iv_colorpencil;
    private ImageView iv_watercolor;
    private ImageView iv_carving;
    private ImageView iv_frostedglass;
    private ImageView iv_chalkpaint;
    private ImageView iv_lowpoly;
    private ImageView iv_coherencefilter;
    private ImageView iv_edgepreserving;

    /**
     * 梦幻浮生
     */
    private HorizontalScrollView hsv_dream_life;
    private ImageView iv_original_dream_life;
    private ImageView iv_iceimg;
    private ImageView iv_fireimg;
    private ImageView iv_delusion;
    private ImageView iv_brushing;
    private ImageView iv_shutter;
    private ImageView iv_waterstreak;
    private ImageView iv_paintbrush;
    private ImageView iv_edge_degradation;
    private ImageView iv_noise;

    /**
     * 色彩世界
     */
    private HorizontalScrollView hsv_color_world;
    private ImageView iv_original_color_world;
    private ImageView iv_polaroid;
    private ImageView iv_red;
    private ImageView iv_green;
    private ImageView iv_blue;
    private ImageView iv_yellow;
    private ImageView iv_lomo;
    private ImageView iv_neon;
    private ImageView iv_black_white;
    private ImageView iv_old_photo;

    /**
     * 浮绘生活
     */
    private HorizontalScrollView hsv_paint_life;
    private ImageView iv_original_paint_life;
    private ImageView iv_inverted_img;
    private ImageView iv_sunshineimage;
    private ImageView iv_feather;
    private ImageView iv_magnifier;
    private ImageView iv_magicmirror;
    private ImageView iv_bright;
    private ImageView iv_masic;
    private ImageView iv_cartoon;
    private ImageView iv_radial_distortion;
    private ImageView iv_oilpaint;

    /**
     * 经典滤镜
     */
    private HorizontalScrollView hsv_classic_filter;
    private ImageView iv_original_classic_filter;
    private ImageView iv_soft;
    private ImageView iv_classic;
    private ImageView iv_florid;
    private ImageView iv_retro;
    private ImageView iv_genteel;
    private ImageView iv_film;
    private ImageView iv_recall;
    private ImageView iv_yogurt;
    private ImageView iv_fleeting_time;
    private ImageView iv_luscious;


    private Intent intent;
    private String picsaveurl;

    private LinearLayout ll_loading;

    private Bitmap bitmap;

    private Bitmap imageToFixedSize;
    private Bitmap preview_selected;
    private Bitmap watercolor_texture;
    private Bitmap pencil_texture;
    private Bitmap tempBitmap;

    private static Bitmap mainBitmap;

    private String filter;

    public static Bitmap getMainBitmap() {
        return mainBitmap;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTransluteWindow();

        getStatusBarHeight(this);

        //隐藏虚拟按键
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE;
        window.setAttributes(params);

        setContentView(R.layout.activity_edit_img);

        //加载资源文件    纹理图的加载
        preview_selected = BitmapFactory.decodeResource(getResources(), R.drawable.preview_selected);

        pencil_texture = BitmapFactory.decodeResource(getResources(), R.drawable.pencil_texture);

        watercolor_texture = BitmapFactory.decodeResource(getResources(), R.drawable.watercolor_texture);

        initUI();

        initData();

        initListener();
    }

    /**
     * 初始化数据
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void initData() {
        intent = getIntent();
        if (intent != null) {
            ll_loading.setVisibility(View.VISIBLE);
            picsaveurl = intent.getStringExtra("picsaveurl");
            filter = intent.getStringExtra("filter");

            LogUtils.e("picsaveurl" + picsaveurl);

            if (PermissionUtils.isGrantExternalRW(EditImgActivity.this, 1)) {
                decodeImgFrpmSD(picsaveurl, filter);
            }
        }
    }

    /**
     * 请求权限回调
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    decodeImgFrpmSD(picsaveurl, filter);
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

    /**
     * 初始化预览效果
     *
     * @param picsaveurl
     * @param filter
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void decodeImgFrpmSD(String picsaveurl, String filter) {

        bitmap = BitmapFactory.decodeFile(picsaveurl);
        iv_img.setImageBitmap(bitmap);
        ll_loading.setVisibility(View.GONE);


        imageToFixedSize = ImageHelper.zoomImageToFixedSize(this.bitmap,
                DensityUtils.dip2px(getApplicationContext(), 112), DensityUtils.dip2px(getApplicationContext(), 112));

        Drawable drawable = new BitmapDrawable(imageToFixedSize);

        iv_original.setBackground(drawable);
        iv_original_dream_life.setBackground(drawable);
        iv_original_classic_filter.setBackground(drawable);
        iv_original_color_world.setBackground(drawable);
        iv_original_paint_life.setBackground(drawable);

        if (filter.equals("classic_hand")) {
            tv_filter_title.setText("经典手绘");
            hsv_classic_hand.setVisibility(View.VISIBLE);
            hsv_dream_life.setVisibility(View.GONE);
            hsv_color_world.setVisibility(View.GONE);
            hsv_paint_life.setVisibility(View.GONE);
            hsv_classic_filter.setVisibility(View.GONE);
            Model.getInstance().getGloblThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ll_loading.setVisibility(View.VISIBLE);
                        }
                    });

                    //铅笔画预览
                    final Bitmap pencil = ImageHelperJNI.Pencil(imageToFixedSize, pencil_texture);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Drawable drawable = new BitmapDrawable(pencil);
                            iv_pencil.setBackground(drawable);
                        }
                    });

                    //彩色铅笔画预览
                    final Bitmap colorPencil = ImageHelperJNI.ColorPencil(imageToFixedSize, pencil_texture);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Drawable drawable = new BitmapDrawable(colorPencil);
                            iv_colorpencil.setBackground(drawable);
                        }
                    });

                    //水彩画预览
                    final Bitmap waterColor = ImageHelperJNI.WaterColor(imageToFixedSize, watercolor_texture);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Drawable drawable = new BitmapDrawable(waterColor);
                            iv_watercolor.setBackground(drawable);
                        }
                    });

                    //浮雕预览
                    final Bitmap carving = ImageHelperJNI.Carving(imageToFixedSize);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Drawable drawable = new BitmapDrawable(carving);
                            iv_carving.setBackground(drawable);
                        }
                    });

                    //毛玻璃预览
                    final Bitmap frostedglass = ImageHelperJNI.FrostedGlass(imageToFixedSize);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Drawable drawable = new BitmapDrawable(frostedglass);
                            iv_frostedglass.setBackground(drawable);
                        }
                    });

                    //粉笔画预览
                    final Bitmap chalkPaint = ImageHelperJNI.ChalkPaint(imageToFixedSize);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Drawable drawable = new BitmapDrawable(chalkPaint);
                            iv_chalkpaint.setBackground(drawable);
                        }
                    });

                    //低面多边形预览
                    final Bitmap lowpoly = ImageHelperJNI.Lowpoly(imageToFixedSize);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Drawable drawable = new BitmapDrawable(lowpoly);
                            iv_lowpoly.setBackground(drawable);
                        }
                    });

                    //抽象画预览
                    final Bitmap coherenceFilter = ImageHelperJNI.CoherenceFilter(imageToFixedSize);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Drawable drawable = new BitmapDrawable(coherenceFilter);
                            iv_coherencefilter.setBackground(drawable);
                        }
                    });

                    //边缘保持（磨皮）预览
                    final Bitmap edgePreserving = ImageHelperJNI.edgePreserving(imageToFixedSize);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Drawable drawable = new BitmapDrawable(edgePreserving);
                            iv_edgepreserving.setBackground(drawable);
                        }
                    });


                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ll_loading.setVisibility(View.GONE);
                        }
                    });
                }
            });
        } else if (filter.equals("dream_life")) {
            tv_filter_title.setText("梦幻浮生");
            hsv_classic_hand.setVisibility(View.GONE);
            hsv_dream_life.setVisibility(View.VISIBLE);
            hsv_color_world.setVisibility(View.GONE);
            hsv_paint_life.setVisibility(View.GONE);
            hsv_classic_filter.setVisibility(View.GONE);
            Model.getInstance().getGloblThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ll_loading.setVisibility(View.VISIBLE);
                        }
                    });

                    //冰冻预览
                    final Bitmap iceImage = ImageHelper.IceImage(imageToFixedSize);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Drawable drawable = new BitmapDrawable(iceImage);
                            iv_iceimg.setBackground(drawable);
                        }
                    });


                    //熔铸预览
                    final Bitmap fireImage = ImageHelper.FireImage(imageToFixedSize);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Drawable drawable = new BitmapDrawable(fireImage);
                            iv_fireimg.setBackground(drawable);
                        }
                    });

                    //幻觉预览
                    final Bitmap delusion = ImageHelper.Delusion(imageToFixedSize);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Drawable drawable = new BitmapDrawable(delusion);
                            iv_delusion.setBackground(drawable);
                        }
                    });

                    //急速奔驰预览
                    final Bitmap brushing = ImageHelper.Brushing(imageToFixedSize);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Drawable drawable = new BitmapDrawable(brushing);
                            iv_brushing.setBackground(drawable);
                        }
                    });

                    //百叶窗预览
                    final Bitmap shutter = ImageHelper.Shutter(imageToFixedSize);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Drawable drawable = new BitmapDrawable(shutter);
                            iv_shutter.setBackground(drawable);
                        }
                    });

                    //水纹预览
                    final Bitmap waterStreak = ImageHelper.WaterStreak(imageToFixedSize);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Drawable drawable = new BitmapDrawable(waterStreak);
                            iv_waterstreak.setBackground(drawable);
                        }
                    });

                    //画笔风格预览
                    final Bitmap paintBrush = ImageHelper.PaintBrush(imageToFixedSize);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Drawable drawable = new BitmapDrawable(paintBrush);
                            iv_paintbrush.setBackground(drawable);
                        }
                    });

                    //边缘退化预览
                    final Bitmap edgeDegradation = ImageHelper.EdgeDegradation(imageToFixedSize);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Drawable drawable = new BitmapDrawable(edgeDegradation);
                            iv_edge_degradation.setBackground(drawable);
                        }
                    });

                    final Bitmap noise = ImageHelper.Noise(imageToFixedSize);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Drawable drawable = new BitmapDrawable(noise);
                            iv_noise.setBackground(drawable);
                        }
                    });


                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ll_loading.setVisibility(View.GONE);
                        }
                    });
                }
            });
        } else if (filter.equals("paint_life")) {
            tv_filter_title.setText("浮绘生活");
            hsv_classic_hand.setVisibility(View.GONE);
            hsv_dream_life.setVisibility(View.GONE);
            hsv_color_world.setVisibility(View.GONE);
            hsv_paint_life.setVisibility(View.VISIBLE);
            hsv_classic_filter.setVisibility(View.GONE);
            Model.getInstance().getGloblThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ll_loading.setVisibility(View.VISIBLE);
                        }
                    });

                    //倒影预览
                    final Bitmap invertedImg = ImageHelper.InvertedImg(imageToFixedSize);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Drawable drawable = new BitmapDrawable(invertedImg);
                            iv_inverted_img.setBackground(drawable);
                        }
                    });


                    //光照预览
                    final Bitmap sunshineImage = ImageHelper.SunshineImage(imageToFixedSize);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Drawable drawable = new BitmapDrawable(sunshineImage);
                            iv_sunshineimage.setBackground(drawable);
                        }
                    });

                    //羽化预览
                    final Bitmap feather = ImageHelper.Feather(imageToFixedSize);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Drawable drawable = new BitmapDrawable(feather);
                            iv_feather.setBackground(drawable);
                        }
                    });

                    //放大镜预览
                    final Bitmap magnifier = ImageHelper.Magnifier(imageToFixedSize, DensityUtils.dip2px(getApplicationContext(), 51));

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Drawable drawable = new BitmapDrawable(magnifier);
                            iv_magnifier.setBackground(drawable);
                        }
                    });

                    //哈哈镜预览
                    final Bitmap magicMirror = ImageHelper.MagicMirror(imageToFixedSize, DensityUtils.dip2px(getApplicationContext(), 51));

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Drawable drawable = new BitmapDrawable(magicMirror);
                            iv_magicmirror.setBackground(drawable);
                        }
                    });

                    //明亮预览
                    final Bitmap bright = ImageHelper.Bright(imageToFixedSize);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Drawable drawable = new BitmapDrawable(bright);
                            iv_bright.setBackground(drawable);
                        }
                    });

                    //马赛克预览
                    final Bitmap masic = ImageHelper.Masic(imageToFixedSize);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Drawable drawable = new BitmapDrawable(masic);
                            iv_masic.setBackground(drawable);
                        }
                    });

                    //霓虹预览
                    final Bitmap neon = ImageHelper.Neon(imageToFixedSize);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Drawable drawable = new BitmapDrawable(neon);
                            iv_neon.setBackground(drawable);
                        }
                    });

                    //漫画预览
                    final Bitmap cartoon = ImageHelper.Cartoon(imageToFixedSize);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Drawable drawable = new BitmapDrawable(cartoon);
                            iv_cartoon.setBackground(drawable);
                        }
                    });

                    //扭曲预览
                    final Bitmap radialDistortion = ImageHelper.RadialDistortion(imageToFixedSize);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Drawable drawable = new BitmapDrawable(radialDistortion);
                            iv_radial_distortion.setBackground(drawable);
                        }
                    });

                    //油画预览
                    final Bitmap oilPaint = ImageHelper.OilPaint(imageToFixedSize);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Drawable drawable = new BitmapDrawable(oilPaint);
                            iv_oilpaint.setBackground(drawable);
                        }
                    });

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ll_loading.setVisibility(View.GONE);
                        }
                    });
                }
            });
        } else if (filter.equals("color_world")) {
            tv_filter_title.setText("色彩世界");
            hsv_classic_hand.setVisibility(View.GONE);
            hsv_dream_life.setVisibility(View.GONE);
            hsv_color_world.setVisibility(View.VISIBLE);
            hsv_paint_life.setVisibility(View.GONE);
            hsv_classic_filter.setVisibility(View.GONE);
            Model.getInstance().getGloblThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ll_loading.setVisibility(View.VISIBLE);
                        }
                    });

                    //宝丽来色
                    final Bitmap polaroid = ImageHelper.Polaroid(imageToFixedSize);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Drawable drawable = new BitmapDrawable(polaroid);
                            iv_polaroid.setBackground(drawable);
                        }
                    });


                    //泛红预览
                    final Bitmap red = ImageHelper.Red(imageToFixedSize);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Drawable drawable = new BitmapDrawable(red);
                            iv_red.setBackground(drawable);
                        }
                    });

                    //荧光绿预览
                    final Bitmap green = ImageHelper.Green(imageToFixedSize);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Drawable drawable = new BitmapDrawable(green);
                            iv_green.setBackground(drawable);
                        }
                    });

                    //宝石蓝预览
                    final Bitmap blue = ImageHelper.Blue(imageToFixedSize);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Drawable drawable = new BitmapDrawable(blue);
                            iv_blue.setBackground(drawable);
                        }
                    });

                    //泛黄预览
                    final Bitmap yellow = ImageHelper.Yellow(imageToFixedSize);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Drawable drawable = new BitmapDrawable(yellow);
                            iv_yellow.setBackground(drawable);
                        }
                    });

                    //lomo预览
                    final Bitmap lomo = ImageHelper.Lomo(imageToFixedSize);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Drawable drawable = new BitmapDrawable(lomo);
                            iv_lomo.setBackground(drawable);
                        }
                    });

                    //霓虹预览
                    final Bitmap neon = ImageHelper.Neon(imageToFixedSize);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Drawable drawable = new BitmapDrawable(neon);
                            iv_neon.setBackground(drawable);
                        }
                    });

                    //黑白预览
                    final Bitmap blackWhite = ImageHelper.BlackWhite(imageToFixedSize);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Drawable drawable = new BitmapDrawable(blackWhite);
                            iv_black_white.setBackground(drawable);
                        }
                    });

                    //老照片预览
                    final Bitmap oldPhoto = ImageHelper.OldPhoto(imageToFixedSize);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Drawable drawable = new BitmapDrawable(oldPhoto);
                            iv_old_photo.setBackground(drawable);
                        }
                    });


                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ll_loading.setVisibility(View.GONE);
                        }
                    });
                }
            });
        } else if (filter.equals("classic_filter")) {
            tv_filter_title.setText("经典滤镜");
            hsv_classic_hand.setVisibility(View.GONE);
            hsv_dream_life.setVisibility(View.GONE);
            hsv_color_world.setVisibility(View.GONE);
            hsv_paint_life.setVisibility(View.GONE);
            hsv_classic_filter.setVisibility(View.VISIBLE);
            Model.getInstance().getGloblThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ll_loading.setVisibility(View.VISIBLE);
                        }
                    });

                    Bitmap tmp = Bitmap.createBitmap(imageToFixedSize);

                    //轻柔预览
                    final Bitmap soft = PhotoProcessing.Soft(tmp);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Drawable drawable = new BitmapDrawable(soft);
                            iv_soft.setBackground(drawable);
                        }
                    });

                    tmp = Bitmap.createBitmap(imageToFixedSize);

                    //经典预览
                    final Bitmap classic = PhotoProcessing.Classic(tmp);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Drawable drawable = new BitmapDrawable(classic);
                            iv_classic.setBackground(drawable);
                        }
                    });

                    tmp = Bitmap.createBitmap(imageToFixedSize);

                    //绚丽预览
                    final Bitmap florid = PhotoProcessing.Florid(tmp);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Drawable drawable = new BitmapDrawable(florid);
                            iv_florid.setBackground(drawable);
                        }
                    });

                    tmp = Bitmap.createBitmap(imageToFixedSize);

                    //复古预览
                    final Bitmap retro = PhotoProcessing.Retro(tmp);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Drawable drawable = new BitmapDrawable(retro);
                            iv_retro.setBackground(drawable);
                        }
                    });

                    tmp = Bitmap.createBitmap(imageToFixedSize);

                    //优雅预览
                    final Bitmap genteel = PhotoProcessing.Genteel(tmp);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Drawable drawable = new BitmapDrawable(genteel);
                            iv_genteel.setBackground(drawable);
                        }
                    });

                    tmp = Bitmap.createBitmap(imageToFixedSize);

                    //胶片预览
                    final Bitmap film = PhotoProcessing.Film(tmp);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Drawable drawable = new BitmapDrawable(film);
                            iv_film.setBackground(drawable);
                        }
                    });

                    tmp = Bitmap.createBitmap(imageToFixedSize);

                    //回忆预览
                    final Bitmap recall = PhotoProcessing.Recall(tmp);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Drawable drawable = new BitmapDrawable(recall);
                            iv_recall.setBackground(drawable);
                        }
                    });

                    tmp = Bitmap.createBitmap(imageToFixedSize);

                    //优格预览
                    final Bitmap yogurt = PhotoProcessing.Yogurt(tmp);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Drawable drawable = new BitmapDrawable(yogurt);
                            iv_yogurt.setBackground(drawable);
                        }
                    });

                    tmp = Bitmap.createBitmap(imageToFixedSize);

                    //流年预览
                    final Bitmap fleeting_time = PhotoProcessing.FleetingTime(tmp);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Drawable drawable = new BitmapDrawable(fleeting_time);
                            iv_fleeting_time.setBackground(drawable);
                        }
                    });

                    tmp = Bitmap.createBitmap(imageToFixedSize);

                    //光绚预览
                    final Bitmap luscious = PhotoProcessing.Luscious(tmp);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Drawable drawable = new BitmapDrawable(luscious);
                            iv_luscious.setBackground(drawable);
                        }
                    });

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ll_loading.setVisibility(View.GONE);
                        }
                    });
                }
            });
        }
    }

    public final DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case AlertDialog.BUTTON_POSITIVE:// "确认"按钮退出程序
                    finish();
                    break;
                case AlertDialog.BUTTON_NEGATIVE:// "取消"第二个按钮取消对话框
                    break;
            }
        }
    };

    /**
     * 设置各个效果的点击事件
     */
    private void initListener() {
        btn_back.setOnClickListener(new View.OnClickListener() {
            @SuppressWarnings("deprecation")
            public void onClick(View v) {
                //返回提示对话框
                AlertDialog isExit = new AlertDialog.Builder(EditImgActivity.this).create();
                isExit.setTitle("系统提示");
                isExit.setMessage("确定要放弃当前正在编辑的图片并返回吗？");
                //设置事件侦听器
                isExit.setButton("确定", listener);
                isExit.setButton2("取消", listener);
                //弹出对话框
                isExit.show();
            }
        });

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Model.getInstance().getGloblThreadPool().execute(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(EditImgActivity.this, ShareActivity.class);

//                        iv_img.setDrawingCacheEnabled(true);
//
//                        int width = bitmap.getWidth();
//                        int height = bitmap.getHeight();
//
//                        Bitmap drawingCache = iv_img.getDrawingCache();
//
//                        int W = drawingCache.getWidth();
//                        int H = drawingCache.getHeight();
//
//                        int x = (int) ((W - width)*1.0 / 2);
//                        int y = (int) ((H - height) *1.0 / 2);

//                        mainBitmap = Bitmap.createBitmap(drawingCache, x, y, width, height);
                        intent.putExtra("drawingCache", "ok");
                        startActivity(intent);
//                        iv_img.setDrawingCacheEnabled(false);
                    }
                });
            }
        });

        if (filter.equals("classic_hand")) {
            //经典手绘分组
            iv_original.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updatePreview(1);

                    Model.getInstance().getGloblThreadPool().execute(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ll_loading.setVisibility(View.VISIBLE);
                                }
                            });

                            bitmap = BitmapFactory.decodeFile(picsaveurl);
                            mainBitmap = bitmap;

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    iv_img.setImageBitmap(bitmap);
                                    ll_loading.setVisibility(View.GONE);
                                }
                            });
                        }
                    });
                }
            });

            iv_pencil.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updatePreview(2);

                    Model.getInstance().getGloblThreadPool().execute(new Runnable() {
                        @Override
                        public void run() {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ll_loading.setVisibility(View.VISIBLE);
                                }
                            });

                            bitmap = BitmapFactory.decodeFile(picsaveurl);
                            Bitmap zoomImage = ImageHelper.zoomImage(bitmap);
                            tempBitmap = ImageHelperJNI.Pencil(zoomImage, pencil_texture);
                            mainBitmap = tempBitmap;

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    iv_img.setImageBitmap(tempBitmap);
                                    ll_loading.setVisibility(View.GONE);
                                }
                            });
                        }
                    });
                }
            });

            iv_colorpencil.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updatePreview(3);

                    Model.getInstance().getGloblThreadPool().execute(new Runnable() {
                        @Override
                        public void run() {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ll_loading.setVisibility(View.VISIBLE);
                                }
                            });

                            bitmap = BitmapFactory.decodeFile(picsaveurl);
                            Bitmap zoomImage = ImageHelper.zoomImage(bitmap);
                            tempBitmap = ImageHelperJNI.ColorPencil(zoomImage, pencil_texture);
                            mainBitmap = tempBitmap;

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    iv_img.setImageBitmap(tempBitmap);
                                    ll_loading.setVisibility(View.GONE);
                                }
                            });
                        }
                    });
                }
            });

            iv_watercolor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updatePreview(4);

                    Model.getInstance().getGloblThreadPool().execute(new Runnable() {
                        @Override
                        public void run() {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ll_loading.setVisibility(View.VISIBLE);
                                }
                            });

                            bitmap = BitmapFactory.decodeFile(picsaveurl);
                            Bitmap zoomImage = ImageHelper.zoomImage(bitmap);
                            tempBitmap = ImageHelperJNI.WaterColor(zoomImage, watercolor_texture);
                            mainBitmap = tempBitmap;

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    iv_img.setImageBitmap(tempBitmap);
                                    ll_loading.setVisibility(View.GONE);
                                }
                            });
                        }
                    });
                }
            });

            iv_carving.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updatePreview(5);

                    Model.getInstance().getGloblThreadPool().execute(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ll_loading.setVisibility(View.VISIBLE);
                                }
                            });

                            bitmap = BitmapFactory.decodeFile(picsaveurl);
                            Bitmap zoomImage = ImageHelper.zoomImage(bitmap);
                            tempBitmap = ImageHelperJNI.Carving(zoomImage);
                            mainBitmap = tempBitmap;

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    iv_img.setImageBitmap(tempBitmap);
                                    ll_loading.setVisibility(View.GONE);
                                }
                            });
                        }
                    });
                }
            });

            iv_frostedglass.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updatePreview(6);

                    Model.getInstance().getGloblThreadPool().execute(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ll_loading.setVisibility(View.VISIBLE);
                                }
                            });

                            bitmap = BitmapFactory.decodeFile(picsaveurl);
                            Bitmap zoomImage = ImageHelper.zoomImage(bitmap);
                            tempBitmap = ImageHelperJNI.FrostedGlass(zoomImage);
                            mainBitmap = tempBitmap;

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    iv_img.setImageBitmap(tempBitmap);
                                    ll_loading.setVisibility(View.GONE);
                                }
                            });
                        }
                    });
                }
            });

            iv_chalkpaint.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updatePreview(7);

                    Model.getInstance().getGloblThreadPool().execute(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ll_loading.setVisibility(View.VISIBLE);
                                }
                            });

                            bitmap = BitmapFactory.decodeFile(picsaveurl);
                            Bitmap zoomImage = ImageHelper.zoomImage(bitmap);
                            tempBitmap = ImageHelperJNI.ChalkPaint(zoomImage);
                            mainBitmap = tempBitmap;

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    iv_img.setImageBitmap(tempBitmap);
                                    ll_loading.setVisibility(View.GONE);
                                }
                            });
                        }
                    });
                }
            });

            iv_lowpoly.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updatePreview(8);

                    Model.getInstance().getGloblThreadPool().execute(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ll_loading.setVisibility(View.VISIBLE);
                                }
                            });

                            bitmap = BitmapFactory.decodeFile(picsaveurl);
                            Bitmap zoomImage = ImageHelper.zoomImage(bitmap);
                            tempBitmap = ImageHelperJNI.Lowpoly(zoomImage);
                            mainBitmap = tempBitmap;

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    iv_img.setImageBitmap(tempBitmap);
                                    ll_loading.setVisibility(View.GONE);
                                }
                            });
                        }
                    });
                }
            });

            iv_coherencefilter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updatePreview(9);

                    Model.getInstance().getGloblThreadPool().execute(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ll_loading.setVisibility(View.VISIBLE);
                                }
                            });

                            bitmap = BitmapFactory.decodeFile(picsaveurl);
                            Bitmap zoomImage = ImageHelper.zoomImage(bitmap);
                            tempBitmap = ImageHelperJNI.CoherenceFilter(zoomImage);
                            mainBitmap = tempBitmap;

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    iv_img.setImageBitmap(tempBitmap);
                                    ll_loading.setVisibility(View.GONE);
                                }
                            });
                        }
                    });
                }
            });

            iv_edgepreserving.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updatePreview(10);

                    Model.getInstance().getGloblThreadPool().execute(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ll_loading.setVisibility(View.VISIBLE);
                                }
                            });

                            bitmap = BitmapFactory.decodeFile(picsaveurl);
                            Bitmap zoomImage = ImageHelper.zoomImage(bitmap);
                            tempBitmap = ImageHelperJNI.edgePreserving(zoomImage);
                            mainBitmap = tempBitmap;

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    iv_img.setImageBitmap(tempBitmap);
                                    ll_loading.setVisibility(View.GONE);
                                }
                            });
                        }
                    });
                }
            });
        } else if (filter.equals("dream_life")) {
            //梦幻浮生分组

            iv_original_dream_life.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iv_original_dream_life.setImageBitmap(preview_selected);
                    iv_iceimg.setImageBitmap(null);
                    iv_fireimg.setImageBitmap(null);
                    iv_delusion.setImageBitmap(null);
                    iv_brushing.setImageBitmap(null);
                    iv_shutter.setImageBitmap(null);
                    iv_waterstreak.setImageBitmap(null);
                    iv_paintbrush.setImageBitmap(null);
                    iv_edge_degradation.setImageBitmap(null);
                    iv_noise.setImageBitmap(null);

                    Model.getInstance().getGloblThreadPool().execute(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ll_loading.setVisibility(View.VISIBLE);
                                }
                            });

                            bitmap = BitmapFactory.decodeFile(picsaveurl);
                            mainBitmap = bitmap;

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    iv_img.setImageBitmap(bitmap);
                                    ll_loading.setVisibility(View.GONE);
                                }
                            });
                        }
                    });
                }
            });

            iv_iceimg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iv_original_dream_life.setImageBitmap(null);
                    iv_iceimg.setImageBitmap(preview_selected);
                    iv_fireimg.setImageBitmap(null);
                    iv_delusion.setImageBitmap(null);
                    iv_brushing.setImageBitmap(null);
                    iv_shutter.setImageBitmap(null);
                    iv_waterstreak.setImageBitmap(null);
                    iv_paintbrush.setImageBitmap(null);
                    iv_edge_degradation.setImageBitmap(null);
                    iv_noise.setImageBitmap(null);

                    Model.getInstance().getGloblThreadPool().execute(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ll_loading.setVisibility(View.VISIBLE);
                                }
                            });

                            bitmap = BitmapFactory.decodeFile(picsaveurl);
                            Bitmap zoomImage = ImageHelper.zoomImage(bitmap);
                            tempBitmap = ImageHelper.IceImage(zoomImage);
                            mainBitmap = tempBitmap;

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    iv_img.setImageBitmap(tempBitmap);
                                    ll_loading.setVisibility(View.GONE);
                                }
                            });
                        }
                    });
                }
            });

            iv_fireimg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iv_original_dream_life.setImageBitmap(null);
                    iv_iceimg.setImageBitmap(null);
                    iv_fireimg.setImageBitmap(preview_selected);
                    iv_delusion.setImageBitmap(null);
                    iv_brushing.setImageBitmap(null);
                    iv_shutter.setImageBitmap(null);
                    iv_waterstreak.setImageBitmap(null);
                    iv_paintbrush.setImageBitmap(null);
                    iv_edge_degradation.setImageBitmap(null);
                    iv_noise.setImageBitmap(null);

                    Model.getInstance().getGloblThreadPool().execute(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ll_loading.setVisibility(View.VISIBLE);
                                }
                            });

                            bitmap = BitmapFactory.decodeFile(picsaveurl);
                            Bitmap zoomImage = ImageHelper.zoomImage(bitmap);
                            tempBitmap = ImageHelper.FireImage(zoomImage);
                            mainBitmap = tempBitmap;

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    iv_img.setImageBitmap(tempBitmap);
                                    ll_loading.setVisibility(View.GONE);
                                }
                            });
                        }
                    });
                }
            });

            iv_delusion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iv_original_dream_life.setImageBitmap(null);
                    iv_iceimg.setImageBitmap(null);
                    iv_fireimg.setImageBitmap(null);
                    iv_delusion.setImageBitmap(preview_selected);
                    iv_brushing.setImageBitmap(null);
                    iv_shutter.setImageBitmap(null);
                    iv_waterstreak.setImageBitmap(null);
                    iv_paintbrush.setImageBitmap(null);
                    iv_edge_degradation.setImageBitmap(null);
                    iv_noise.setImageBitmap(null);

                    Model.getInstance().getGloblThreadPool().execute(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ll_loading.setVisibility(View.VISIBLE);
                                }
                            });

                            bitmap = BitmapFactory.decodeFile(picsaveurl);
                            Bitmap zoomImage = ImageHelper.toSquare(ImageHelper.zoomImage(bitmap));
                            tempBitmap = ImageHelper.Delusion(zoomImage);
                            mainBitmap = tempBitmap;

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    iv_img.setImageBitmap(tempBitmap);
                                    ll_loading.setVisibility(View.GONE);
                                }
                            });
                        }
                    });
                }
            });

            iv_brushing.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iv_original_dream_life.setImageBitmap(null);
                    iv_iceimg.setImageBitmap(null);
                    iv_fireimg.setImageBitmap(null);
                    iv_delusion.setImageBitmap(null);
                    iv_brushing.setImageBitmap(preview_selected);
                    iv_shutter.setImageBitmap(null);
                    iv_waterstreak.setImageBitmap(null);
                    iv_paintbrush.setImageBitmap(null);
                    iv_edge_degradation.setImageBitmap(null);
                    iv_noise.setImageBitmap(null);

                    Model.getInstance().getGloblThreadPool().execute(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ll_loading.setVisibility(View.VISIBLE);
                                }
                            });

                            bitmap = BitmapFactory.decodeFile(picsaveurl);
                            Bitmap zoomImage = ImageHelper.toSquare(ImageHelper.zoomImage(bitmap));
                            tempBitmap = ImageHelper.Brushing(zoomImage);
                            mainBitmap = tempBitmap;

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    iv_img.setImageBitmap(tempBitmap);
                                    ll_loading.setVisibility(View.GONE);
                                }
                            });
                        }
                    });
                }
            });

            iv_shutter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iv_original_dream_life.setImageBitmap(null);
                    iv_iceimg.setImageBitmap(null);
                    iv_fireimg.setImageBitmap(null);
                    iv_delusion.setImageBitmap(null);
                    iv_brushing.setImageBitmap(null);
                    iv_shutter.setImageBitmap(preview_selected);
                    iv_waterstreak.setImageBitmap(null);
                    iv_paintbrush.setImageBitmap(null);
                    iv_edge_degradation.setImageBitmap(null);
                    iv_noise.setImageBitmap(null);

                    Model.getInstance().getGloblThreadPool().execute(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ll_loading.setVisibility(View.VISIBLE);
                                }
                            });

                            bitmap = BitmapFactory.decodeFile(picsaveurl);
                            Bitmap zoomImage = ImageHelper.zoomImage(bitmap);
                            tempBitmap = ImageHelper.Shutter(zoomImage);
                            mainBitmap = tempBitmap;

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    iv_img.setImageBitmap(tempBitmap);
                                    ll_loading.setVisibility(View.GONE);
                                }
                            });
                        }
                    });
                }
            });

            iv_waterstreak.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iv_original_dream_life.setImageBitmap(null);
                    iv_iceimg.setImageBitmap(null);
                    iv_fireimg.setImageBitmap(null);
                    iv_delusion.setImageBitmap(null);
                    iv_brushing.setImageBitmap(null);
                    iv_shutter.setImageBitmap(null);
                    iv_waterstreak.setImageBitmap(preview_selected);
                    iv_paintbrush.setImageBitmap(null);
                    iv_edge_degradation.setImageBitmap(null);
                    iv_noise.setImageBitmap(null);

                    Model.getInstance().getGloblThreadPool().execute(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ll_loading.setVisibility(View.VISIBLE);
                                }
                            });

                            bitmap = BitmapFactory.decodeFile(picsaveurl);
                            Bitmap zoomImage = ImageHelper.zoomImage(bitmap);
                            tempBitmap = ImageHelper.WaterStreak(zoomImage);
                            mainBitmap = tempBitmap;

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    iv_img.setImageBitmap(tempBitmap);
                                    ll_loading.setVisibility(View.GONE);
                                }
                            });
                        }
                    });
                }
            });

            iv_paintbrush.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iv_original_dream_life.setImageBitmap(null);
                    iv_iceimg.setImageBitmap(null);
                    iv_fireimg.setImageBitmap(null);
                    iv_delusion.setImageBitmap(null);
                    iv_brushing.setImageBitmap(null);
                    iv_shutter.setImageBitmap(null);
                    iv_waterstreak.setImageBitmap(null);
                    iv_paintbrush.setImageBitmap(preview_selected);
                    iv_edge_degradation.setImageBitmap(null);
                    iv_noise.setImageBitmap(null);

                    Model.getInstance().getGloblThreadPool().execute(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ll_loading.setVisibility(View.VISIBLE);
                                }
                            });

                            bitmap = BitmapFactory.decodeFile(picsaveurl);
                            Bitmap zoomImage = ImageHelper.zoomImage(bitmap);
                            tempBitmap = ImageHelper.PaintBrush(zoomImage);
                            mainBitmap = tempBitmap;

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    iv_img.setImageBitmap(tempBitmap);
                                    ll_loading.setVisibility(View.GONE);
                                }
                            });
                        }
                    });
                }
            });

            iv_edge_degradation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iv_original_dream_life.setImageBitmap(null);
                    iv_iceimg.setImageBitmap(null);
                    iv_fireimg.setImageBitmap(null);
                    iv_delusion.setImageBitmap(null);
                    iv_brushing.setImageBitmap(null);
                    iv_shutter.setImageBitmap(null);
                    iv_waterstreak.setImageBitmap(null);
                    iv_paintbrush.setImageBitmap(null);
                    iv_edge_degradation.setImageBitmap(preview_selected);
                    iv_noise.setImageBitmap(null);

                    Model.getInstance().getGloblThreadPool().execute(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ll_loading.setVisibility(View.VISIBLE);
                                }
                            });

                            bitmap = BitmapFactory.decodeFile(picsaveurl);
                            Bitmap zoomImage = ImageHelper.zoomImage(bitmap);
                            tempBitmap = ImageHelper.EdgeDegradation(zoomImage);
                            mainBitmap = tempBitmap;

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    iv_img.setImageBitmap(tempBitmap);
                                    ll_loading.setVisibility(View.GONE);
                                }
                            });
                        }
                    });
                }
            });

            iv_noise.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iv_original_dream_life.setImageBitmap(null);
                    iv_iceimg.setImageBitmap(null);
                    iv_fireimg.setImageBitmap(null);
                    iv_delusion.setImageBitmap(null);
                    iv_brushing.setImageBitmap(null);
                    iv_shutter.setImageBitmap(null);
                    iv_waterstreak.setImageBitmap(null);
                    iv_paintbrush.setImageBitmap(null);
                    iv_edge_degradation.setImageBitmap(null);
                    iv_noise.setImageBitmap(preview_selected);

                    Model.getInstance().getGloblThreadPool().execute(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ll_loading.setVisibility(View.VISIBLE);
                                }
                            });

                            bitmap = BitmapFactory.decodeFile(picsaveurl);
                            Bitmap zoomImage = ImageHelper.zoomImage(bitmap);
                            tempBitmap = ImageHelper.Noise(zoomImage);
                            mainBitmap = tempBitmap;

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    iv_img.setImageBitmap(tempBitmap);
                                    ll_loading.setVisibility(View.GONE);
                                }
                            });
                        }
                    });
                }
            });

        } else if (filter.equals("color_world")) {
            //色彩世界分组

            iv_original_color_world.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iv_original_color_world.setImageBitmap(preview_selected);
                    iv_polaroid.setImageBitmap(null);
                    iv_red.setImageBitmap(null);
                    iv_green.setImageBitmap(null);
                    iv_blue.setImageBitmap(null);
                    iv_yellow.setImageBitmap(null);
                    iv_lomo.setImageBitmap(null);
                    iv_neon.setImageBitmap(null);
                    iv_black_white.setImageBitmap(null);
                    iv_old_photo.setImageBitmap(null);

                    Model.getInstance().getGloblThreadPool().execute(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ll_loading.setVisibility(View.VISIBLE);
                                }
                            });

                            bitmap = BitmapFactory.decodeFile(picsaveurl);
                            mainBitmap = bitmap;

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    iv_img.setImageBitmap(bitmap);
                                    ll_loading.setVisibility(View.GONE);
                                }
                            });
                        }
                    });
                }
            });

            iv_polaroid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iv_original_color_world.setImageBitmap(null);
                    iv_polaroid.setImageBitmap(preview_selected);
                    iv_red.setImageBitmap(null);
                    iv_green.setImageBitmap(null);
                    iv_blue.setImageBitmap(null);
                    iv_yellow.setImageBitmap(null);
                    iv_lomo.setImageBitmap(null);
                    iv_neon.setImageBitmap(null);
                    iv_black_white.setImageBitmap(null);
                    iv_old_photo.setImageBitmap(null);

                    Model.getInstance().getGloblThreadPool().execute(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ll_loading.setVisibility(View.VISIBLE);
                                }
                            });

                            bitmap = BitmapFactory.decodeFile(picsaveurl);
                            Bitmap zoomImage = ImageHelper.zoomImage(bitmap);
                            tempBitmap = ImageHelper.Polaroid(zoomImage);
                            mainBitmap = tempBitmap;

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    iv_img.setImageBitmap(tempBitmap);
                                    ll_loading.setVisibility(View.GONE);
                                }
                            });
                        }
                    });
                }
            });

            iv_red.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iv_original_color_world.setImageBitmap(null);
                    iv_polaroid.setImageBitmap(null);
                    iv_red.setImageBitmap(preview_selected);
                    iv_green.setImageBitmap(null);
                    iv_blue.setImageBitmap(null);
                    iv_yellow.setImageBitmap(null);
                    iv_lomo.setImageBitmap(null);
                    iv_neon.setImageBitmap(null);
                    iv_black_white.setImageBitmap(null);
                    iv_old_photo.setImageBitmap(null);

                    Model.getInstance().getGloblThreadPool().execute(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ll_loading.setVisibility(View.VISIBLE);
                                }
                            });

                            bitmap = BitmapFactory.decodeFile(picsaveurl);
                            Bitmap zoomImage = ImageHelper.zoomImage(bitmap);
                            tempBitmap = ImageHelper.Red(zoomImage);
                            mainBitmap = tempBitmap;

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    iv_img.setImageBitmap(tempBitmap);
                                    ll_loading.setVisibility(View.GONE);
                                }
                            });
                        }
                    });
                }
            });

            iv_green.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iv_original_color_world.setImageBitmap(null);
                    iv_polaroid.setImageBitmap(null);
                    iv_red.setImageBitmap(null);
                    iv_green.setImageBitmap(preview_selected);
                    iv_blue.setImageBitmap(null);
                    iv_yellow.setImageBitmap(null);
                    iv_lomo.setImageBitmap(null);
                    iv_neon.setImageBitmap(null);
                    iv_black_white.setImageBitmap(null);
                    iv_old_photo.setImageBitmap(null);

                    Model.getInstance().getGloblThreadPool().execute(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ll_loading.setVisibility(View.VISIBLE);
                                }
                            });

                            bitmap = BitmapFactory.decodeFile(picsaveurl);
                            Bitmap zoomImage = ImageHelper.toSquare(ImageHelper.zoomImage(bitmap));
                            tempBitmap = ImageHelper.Green(zoomImage);
                            mainBitmap = tempBitmap;

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    iv_img.setImageBitmap(tempBitmap);
                                    ll_loading.setVisibility(View.GONE);
                                }
                            });
                        }
                    });
                }
            });

            iv_blue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iv_original_color_world.setImageBitmap(null);
                    iv_polaroid.setImageBitmap(null);
                    iv_red.setImageBitmap(null);
                    iv_green.setImageBitmap(null);
                    iv_blue.setImageBitmap(preview_selected);
                    iv_yellow.setImageBitmap(null);
                    iv_lomo.setImageBitmap(null);
                    iv_neon.setImageBitmap(null);
                    iv_black_white.setImageBitmap(null);
                    iv_old_photo.setImageBitmap(null);

                    Model.getInstance().getGloblThreadPool().execute(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ll_loading.setVisibility(View.VISIBLE);
                                }
                            });

                            bitmap = BitmapFactory.decodeFile(picsaveurl);
                            Bitmap zoomImage = ImageHelper.zoomImage(bitmap);
                            tempBitmap = ImageHelper.Blue(zoomImage);
                            mainBitmap = tempBitmap;

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    iv_img.setImageBitmap(tempBitmap);
                                    ll_loading.setVisibility(View.GONE);
                                }
                            });
                        }
                    });
                }
            });

            iv_yellow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iv_original_color_world.setImageBitmap(null);
                    iv_polaroid.setImageBitmap(null);
                    iv_red.setImageBitmap(null);
                    iv_green.setImageBitmap(null);
                    iv_blue.setImageBitmap(null);
                    iv_yellow.setImageBitmap(preview_selected);
                    iv_lomo.setImageBitmap(null);
                    iv_neon.setImageBitmap(null);
                    iv_black_white.setImageBitmap(null);
                    iv_old_photo.setImageBitmap(null);

                    Model.getInstance().getGloblThreadPool().execute(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ll_loading.setVisibility(View.VISIBLE);
                                }
                            });

                            bitmap = BitmapFactory.decodeFile(picsaveurl);
                            Bitmap zoomImage = ImageHelper.zoomImage(bitmap);
                            tempBitmap = ImageHelper.Yellow(zoomImage);
                            mainBitmap = tempBitmap;

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    iv_img.setImageBitmap(tempBitmap);
                                    ll_loading.setVisibility(View.GONE);
                                }
                            });
                        }
                    });
                }
            });

            iv_lomo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iv_original_color_world.setImageBitmap(null);
                    iv_polaroid.setImageBitmap(null);
                    iv_red.setImageBitmap(null);
                    iv_green.setImageBitmap(null);
                    iv_blue.setImageBitmap(null);
                    iv_yellow.setImageBitmap(null);
                    iv_lomo.setImageBitmap(preview_selected);
                    iv_neon.setImageBitmap(null);
                    iv_black_white.setImageBitmap(null);
                    iv_old_photo.setImageBitmap(null);

                    Model.getInstance().getGloblThreadPool().execute(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ll_loading.setVisibility(View.VISIBLE);
                                }
                            });

                            bitmap = BitmapFactory.decodeFile(picsaveurl);
                            Bitmap zoomImage = ImageHelper.zoomImage(bitmap);
                            tempBitmap = ImageHelper.Lomo(zoomImage);
                            mainBitmap = tempBitmap;

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    iv_img.setImageBitmap(tempBitmap);
                                    ll_loading.setVisibility(View.GONE);
                                }
                            });
                        }
                    });
                }
            });

            iv_neon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iv_original_color_world.setImageBitmap(null);
                    iv_polaroid.setImageBitmap(null);
                    iv_red.setImageBitmap(null);
                    iv_green.setImageBitmap(null);
                    iv_blue.setImageBitmap(null);
                    iv_yellow.setImageBitmap(null);
                    iv_lomo.setImageBitmap(null);
                    iv_neon.setImageBitmap(preview_selected);
                    iv_black_white.setImageBitmap(null);
                    iv_old_photo.setImageBitmap(null);

                    Model.getInstance().getGloblThreadPool().execute(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ll_loading.setVisibility(View.VISIBLE);
                                }
                            });

                            bitmap = BitmapFactory.decodeFile(picsaveurl);
                            Bitmap zoomImage = ImageHelper.zoomImage(bitmap);
                            tempBitmap = ImageHelper.Neon(zoomImage);
                            mainBitmap = tempBitmap;

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    iv_img.setImageBitmap(tempBitmap);
                                    ll_loading.setVisibility(View.GONE);
                                }
                            });
                        }
                    });
                }
            });

            iv_black_white.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iv_original_color_world.setImageBitmap(null);
                    iv_polaroid.setImageBitmap(null);
                    iv_red.setImageBitmap(null);
                    iv_green.setImageBitmap(null);
                    iv_blue.setImageBitmap(null);
                    iv_yellow.setImageBitmap(null);
                    iv_lomo.setImageBitmap(null);
                    iv_neon.setImageBitmap(null);
                    iv_black_white.setImageBitmap(preview_selected);
                    iv_old_photo.setImageBitmap(null);

                    Model.getInstance().getGloblThreadPool().execute(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ll_loading.setVisibility(View.VISIBLE);
                                }
                            });

                            bitmap = BitmapFactory.decodeFile(picsaveurl);
                            Bitmap zoomImage = ImageHelper.zoomImage(bitmap);
                            tempBitmap = ImageHelper.BlackWhite(zoomImage);
                            mainBitmap = tempBitmap;

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    iv_img.setImageBitmap(tempBitmap);
                                    ll_loading.setVisibility(View.GONE);
                                }
                            });
                        }
                    });
                }
            });

            iv_old_photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iv_original_color_world.setImageBitmap(null);
                    iv_polaroid.setImageBitmap(null);
                    iv_red.setImageBitmap(null);
                    iv_green.setImageBitmap(null);
                    iv_blue.setImageBitmap(null);
                    iv_yellow.setImageBitmap(null);
                    iv_lomo.setImageBitmap(null);
                    iv_neon.setImageBitmap(null);
                    iv_black_white.setImageBitmap(null);
                    iv_old_photo.setImageBitmap(preview_selected);

                    Model.getInstance().getGloblThreadPool().execute(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ll_loading.setVisibility(View.VISIBLE);
                                }
                            });

                            bitmap = BitmapFactory.decodeFile(picsaveurl);
                            Bitmap zoomImage = ImageHelper.toSquare(ImageHelper.zoomImage(bitmap));
                            tempBitmap = ImageHelper.OldPhoto(zoomImage);
                            mainBitmap = tempBitmap;

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    iv_img.setImageBitmap(tempBitmap);
                                    ll_loading.setVisibility(View.GONE);
                                }
                            });
                        }
                    });
                }
            });
        } else if (filter.equals("paint_life")) {
            //浮绘生活分组

            iv_original_paint_life.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iv_original_paint_life.setImageBitmap(preview_selected);
                    iv_inverted_img.setImageBitmap(null);
                    iv_sunshineimage.setImageBitmap(null);
                    iv_feather.setImageBitmap(null);
                    iv_magnifier.setImageBitmap(null);
                    iv_magicmirror.setImageBitmap(null);
                    iv_bright.setImageBitmap(null);
                    iv_masic.setImageBitmap(null);
                    iv_cartoon.setImageBitmap(null);
                    iv_radial_distortion.setImageBitmap(null);
                    iv_oilpaint.setImageBitmap(null);

                    Model.getInstance().getGloblThreadPool().execute(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ll_loading.setVisibility(View.VISIBLE);
                                }
                            });

                            bitmap = BitmapFactory.decodeFile(picsaveurl);
                            mainBitmap = bitmap;

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    iv_img.setImageBitmap(bitmap);
                                    ll_loading.setVisibility(View.GONE);
                                }
                            });
                        }
                    });
                }
            });

            iv_inverted_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iv_original_paint_life.setImageBitmap(null);
                    iv_inverted_img.setImageBitmap(preview_selected);
                    iv_sunshineimage.setImageBitmap(null);
                    iv_feather.setImageBitmap(null);
                    iv_magnifier.setImageBitmap(null);
                    iv_magicmirror.setImageBitmap(null);
                    iv_bright.setImageBitmap(null);
                    iv_masic.setImageBitmap(null);
                    iv_cartoon.setImageBitmap(null);
                    iv_radial_distortion.setImageBitmap(null);
                    iv_oilpaint.setImageBitmap(null);

                    Model.getInstance().getGloblThreadPool().execute(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ll_loading.setVisibility(View.VISIBLE);
                                }
                            });

                            bitmap = BitmapFactory.decodeFile(picsaveurl);
                            Bitmap zoomImage = ImageHelper.zoomImage(bitmap);
                            tempBitmap = ImageHelper.InvertedImg(zoomImage);
                            mainBitmap = tempBitmap;

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    iv_img.setImageBitmap(tempBitmap);
                                    ll_loading.setVisibility(View.GONE);
                                }
                            });
                        }
                    });
                }
            });

            iv_sunshineimage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iv_original_paint_life.setImageBitmap(null);
                    iv_inverted_img.setImageBitmap(null);
                    iv_sunshineimage.setImageBitmap(preview_selected);
                    iv_feather.setImageBitmap(null);
                    iv_magnifier.setImageBitmap(null);
                    iv_magicmirror.setImageBitmap(null);
                    iv_bright.setImageBitmap(null);
                    iv_masic.setImageBitmap(null);
                    iv_cartoon.setImageBitmap(null);
                    iv_radial_distortion.setImageBitmap(null);
                    iv_oilpaint.setImageBitmap(null);

                    Model.getInstance().getGloblThreadPool().execute(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ll_loading.setVisibility(View.VISIBLE);
                                }
                            });

                            bitmap = BitmapFactory.decodeFile(picsaveurl);
                            Bitmap zoomImage = ImageHelper.zoomImage(bitmap);
                            tempBitmap = ImageHelper.SunshineImage(zoomImage);
                            mainBitmap = tempBitmap;

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    iv_img.setImageBitmap(tempBitmap);
                                    ll_loading.setVisibility(View.GONE);
                                }
                            });
                        }
                    });
                }
            });

            iv_feather.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iv_original_paint_life.setImageBitmap(null);
                    iv_inverted_img.setImageBitmap(null);
                    iv_sunshineimage.setImageBitmap(null);
                    iv_feather.setImageBitmap(preview_selected);
                    iv_magnifier.setImageBitmap(null);
                    iv_magicmirror.setImageBitmap(null);
                    iv_bright.setImageBitmap(null);
                    iv_masic.setImageBitmap(null);
                    iv_cartoon.setImageBitmap(null);
                    iv_radial_distortion.setImageBitmap(null);
                    iv_oilpaint.setImageBitmap(null);

                    Model.getInstance().getGloblThreadPool().execute(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ll_loading.setVisibility(View.VISIBLE);
                                }
                            });

                            bitmap = BitmapFactory.decodeFile(picsaveurl);
                            Bitmap zoomImage = ImageHelper.toSquare(ImageHelper.zoomImage(bitmap));
                            tempBitmap = ImageHelper.Feather(zoomImage);
                            mainBitmap = tempBitmap;

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    iv_img.setImageBitmap(tempBitmap);
                                    ll_loading.setVisibility(View.GONE);
                                }
                            });
                        }
                    });
                }
            });

            iv_magnifier.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iv_original_paint_life.setImageBitmap(null);
                    iv_inverted_img.setImageBitmap(null);
                    iv_sunshineimage.setImageBitmap(null);
                    iv_feather.setImageBitmap(null);
                    iv_magnifier.setImageBitmap(preview_selected);
                    iv_magicmirror.setImageBitmap(null);
                    iv_bright.setImageBitmap(null);
                    iv_masic.setImageBitmap(null);
                    iv_cartoon.setImageBitmap(null);
                    iv_radial_distortion.setImageBitmap(null);
                    iv_oilpaint.setImageBitmap(null);

                    Model.getInstance().getGloblThreadPool().execute(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ll_loading.setVisibility(View.VISIBLE);
                                }
                            });

                            bitmap = BitmapFactory.decodeFile(picsaveurl);
                            Bitmap zoomImage = ImageHelper.zoomImage(bitmap);
                            tempBitmap = ImageHelper.Magnifier(zoomImage, zoomImage.getWidth() / 2);
                            mainBitmap = tempBitmap;

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    iv_img.setImageBitmap(tempBitmap);
                                    ll_loading.setVisibility(View.GONE);
                                }
                            });
                        }
                    });
                }
            });

            iv_magicmirror.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iv_original_paint_life.setImageBitmap(null);
                    iv_inverted_img.setImageBitmap(null);
                    iv_sunshineimage.setImageBitmap(null);
                    iv_feather.setImageBitmap(null);
                    iv_magnifier.setImageBitmap(null);
                    iv_magicmirror.setImageBitmap(preview_selected);
                    iv_bright.setImageBitmap(null);
                    iv_masic.setImageBitmap(null);
                    iv_cartoon.setImageBitmap(null);
                    iv_radial_distortion.setImageBitmap(null);
                    iv_oilpaint.setImageBitmap(null);

                    Model.getInstance().getGloblThreadPool().execute(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ll_loading.setVisibility(View.VISIBLE);
                                }
                            });

                            bitmap = BitmapFactory.decodeFile(picsaveurl);
                            Bitmap zoomImage = ImageHelper.zoomImage(bitmap);
                            tempBitmap = ImageHelper.MagicMirror(zoomImage, zoomImage.getWidth() / 2);
                            mainBitmap = tempBitmap;

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    iv_img.setImageBitmap(tempBitmap);
                                    ll_loading.setVisibility(View.GONE);
                                }
                            });
                        }
                    });
                }
            });

            iv_bright.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iv_original_paint_life.setImageBitmap(null);
                    iv_inverted_img.setImageBitmap(null);
                    iv_sunshineimage.setImageBitmap(null);
                    iv_feather.setImageBitmap(null);
                    iv_magnifier.setImageBitmap(null);
                    iv_magicmirror.setImageBitmap(null);
                    iv_bright.setImageBitmap(preview_selected);
                    iv_masic.setImageBitmap(null);
                    iv_cartoon.setImageBitmap(null);
                    iv_radial_distortion.setImageBitmap(null);
                    iv_oilpaint.setImageBitmap(null);

                    Model.getInstance().getGloblThreadPool().execute(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ll_loading.setVisibility(View.VISIBLE);
                                }
                            });

                            bitmap = BitmapFactory.decodeFile(picsaveurl);
                            Bitmap zoomImage = ImageHelper.zoomImage(bitmap);
                            tempBitmap = ImageHelper.Bright(zoomImage);
                            mainBitmap = tempBitmap;

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    iv_img.setImageBitmap(tempBitmap);
                                    ll_loading.setVisibility(View.GONE);
                                }
                            });
                        }
                    });
                }
            });

            iv_masic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iv_original_paint_life.setImageBitmap(null);
                    iv_inverted_img.setImageBitmap(null);
                    iv_sunshineimage.setImageBitmap(null);
                    iv_feather.setImageBitmap(null);
                    iv_magnifier.setImageBitmap(null);
                    iv_magicmirror.setImageBitmap(null);
                    iv_bright.setImageBitmap(null);
                    iv_masic.setImageBitmap(preview_selected);
                    iv_cartoon.setImageBitmap(null);
                    iv_radial_distortion.setImageBitmap(null);
                    iv_oilpaint.setImageBitmap(null);

                    Model.getInstance().getGloblThreadPool().execute(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ll_loading.setVisibility(View.VISIBLE);
                                }
                            });

                            bitmap = BitmapFactory.decodeFile(picsaveurl);
                            Bitmap zoomImage = ImageHelper.zoomImage(bitmap);
                            tempBitmap = ImageHelper.Masic(zoomImage);
                            mainBitmap = tempBitmap;

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    iv_img.setImageBitmap(tempBitmap);
                                    ll_loading.setVisibility(View.GONE);
                                }
                            });
                        }
                    });
                }
            });

            iv_cartoon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iv_original_paint_life.setImageBitmap(null);
                    iv_inverted_img.setImageBitmap(null);
                    iv_sunshineimage.setImageBitmap(null);
                    iv_feather.setImageBitmap(null);
                    iv_magnifier.setImageBitmap(null);
                    iv_magicmirror.setImageBitmap(null);
                    iv_bright.setImageBitmap(null);
                    iv_masic.setImageBitmap(null);
                    iv_cartoon.setImageBitmap(preview_selected);
                    iv_radial_distortion.setImageBitmap(null);
                    iv_oilpaint.setImageBitmap(null);

                    Model.getInstance().getGloblThreadPool().execute(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ll_loading.setVisibility(View.VISIBLE);
                                }
                            });

                            bitmap = BitmapFactory.decodeFile(picsaveurl);
                            Bitmap zoomImage = ImageHelper.zoomImage(bitmap);
                            tempBitmap = ImageHelper.Cartoon(zoomImage);
                            mainBitmap = tempBitmap;

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    iv_img.setImageBitmap(tempBitmap);
                                    ll_loading.setVisibility(View.GONE);
                                }
                            });
                        }
                    });
                }
            });

            iv_radial_distortion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iv_original_paint_life.setImageBitmap(null);
                    iv_inverted_img.setImageBitmap(null);
                    iv_sunshineimage.setImageBitmap(null);
                    iv_feather.setImageBitmap(null);
                    iv_magnifier.setImageBitmap(null);
                    iv_magicmirror.setImageBitmap(null);
                    iv_bright.setImageBitmap(null);
                    iv_masic.setImageBitmap(null);
                    iv_cartoon.setImageBitmap(null);
                    iv_radial_distortion.setImageBitmap(preview_selected);
                    iv_oilpaint.setImageBitmap(null);

                    Model.getInstance().getGloblThreadPool().execute(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ll_loading.setVisibility(View.VISIBLE);
                                }
                            });

                            bitmap = BitmapFactory.decodeFile(picsaveurl);
                            Bitmap zoomImage = ImageHelper.toSquare(ImageHelper.zoomImage(bitmap));
                            tempBitmap = ImageHelper.RadialDistortion(zoomImage);
                            mainBitmap = tempBitmap;

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    iv_img.setImageBitmap(tempBitmap);
                                    ll_loading.setVisibility(View.GONE);
                                }
                            });
                        }
                    });
                }
            });

            iv_oilpaint.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iv_original_paint_life.setImageBitmap(null);
                    iv_inverted_img.setImageBitmap(null);
                    iv_sunshineimage.setImageBitmap(null);
                    iv_feather.setImageBitmap(null);
                    iv_magnifier.setImageBitmap(null);
                    iv_magicmirror.setImageBitmap(null);
                    iv_bright.setImageBitmap(null);
                    iv_masic.setImageBitmap(null);
                    iv_cartoon.setImageBitmap(null);
                    iv_radial_distortion.setImageBitmap(null);
                    iv_oilpaint.setImageBitmap(preview_selected);

                    Model.getInstance().getGloblThreadPool().execute(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ll_loading.setVisibility(View.VISIBLE);
                                }
                            });

                            bitmap = BitmapFactory.decodeFile(picsaveurl);
                            Bitmap zoomImage = ImageHelper.toSquare(ImageHelper.zoomImage(bitmap));
                            tempBitmap = ImageHelper.OilPaint(zoomImage);
                            mainBitmap = tempBitmap;

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    iv_img.setImageBitmap(tempBitmap);
                                    ll_loading.setVisibility(View.GONE);
                                }
                            });
                        }
                    });
                }
            });
        } else if (filter.equals("classic_filter")) {
            //经典滤镜分组
            iv_original_classic_filter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iv_original_classic_filter.setImageBitmap(preview_selected);
                    iv_soft.setImageBitmap(null);
                    iv_classic.setImageBitmap(null);
                    iv_florid.setImageBitmap(null);
                    iv_retro.setImageBitmap(null);
                    iv_genteel.setImageBitmap(null);
                    iv_film.setImageBitmap(null);
                    iv_recall.setImageBitmap(null);
                    iv_yogurt.setImageBitmap(null);
                    iv_fleeting_time.setImageBitmap(null);
                    iv_luscious.setImageBitmap(null);

                    Model.getInstance().getGloblThreadPool().execute(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ll_loading.setVisibility(View.VISIBLE);
                                }
                            });

                            bitmap = BitmapFactory.decodeFile(picsaveurl);
                            mainBitmap = bitmap;

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    iv_img.setImageBitmap(bitmap);
                                    ll_loading.setVisibility(View.GONE);
                                }
                            });
                        }
                    });
                }
            });

            iv_soft.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iv_original_classic_filter.setImageBitmap(null);
                    iv_soft.setImageBitmap(preview_selected);
                    iv_classic.setImageBitmap(null);
                    iv_florid.setImageBitmap(null);
                    iv_retro.setImageBitmap(null);
                    iv_genteel.setImageBitmap(null);
                    iv_film.setImageBitmap(null);
                    iv_recall.setImageBitmap(null);
                    iv_yogurt.setImageBitmap(null);
                    iv_fleeting_time.setImageBitmap(null);
                    iv_luscious.setImageBitmap(null);

                    Model.getInstance().getGloblThreadPool().execute(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ll_loading.setVisibility(View.VISIBLE);
                                }
                            });

                            bitmap = BitmapFactory.decodeFile(picsaveurl);
                            Bitmap zoomImage = ImageHelper.zoomImage(bitmap);
                            tempBitmap = PhotoProcessing.Soft(zoomImage);
                            mainBitmap = tempBitmap;

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    iv_img.setImageBitmap(tempBitmap);
                                    ll_loading.setVisibility(View.GONE);
                                }
                            });
                        }
                    });
                }
            });

            iv_classic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iv_original_classic_filter.setImageBitmap(null);
                    iv_soft.setImageBitmap(null);
                    iv_classic.setImageBitmap(preview_selected);
                    iv_florid.setImageBitmap(null);
                    iv_retro.setImageBitmap(null);
                    iv_genteel.setImageBitmap(null);
                    iv_film.setImageBitmap(null);
                    iv_recall.setImageBitmap(null);
                    iv_yogurt.setImageBitmap(null);
                    iv_fleeting_time.setImageBitmap(null);
                    iv_luscious.setImageBitmap(null);

                    Model.getInstance().getGloblThreadPool().execute(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ll_loading.setVisibility(View.VISIBLE);
                                }
                            });

                            bitmap = BitmapFactory.decodeFile(picsaveurl);
                            Bitmap zoomImage = ImageHelper.zoomImage(bitmap);
                            tempBitmap = PhotoProcessing.Classic(zoomImage);
                            mainBitmap = tempBitmap;

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    iv_img.setImageBitmap(tempBitmap);
                                    ll_loading.setVisibility(View.GONE);
                                }
                            });
                        }
                    });
                }
            });

            iv_florid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iv_original_classic_filter.setImageBitmap(null);
                    iv_soft.setImageBitmap(null);
                    iv_classic.setImageBitmap(null);
                    iv_florid.setImageBitmap(preview_selected);
                    iv_retro.setImageBitmap(null);
                    iv_genteel.setImageBitmap(null);
                    iv_film.setImageBitmap(null);
                    iv_recall.setImageBitmap(null);
                    iv_yogurt.setImageBitmap(null);
                    iv_fleeting_time.setImageBitmap(null);
                    iv_luscious.setImageBitmap(null);

                    Model.getInstance().getGloblThreadPool().execute(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ll_loading.setVisibility(View.VISIBLE);
                                }
                            });

                            bitmap = BitmapFactory.decodeFile(picsaveurl);
                            Bitmap zoomImage = ImageHelper.toSquare(ImageHelper.zoomImage(bitmap));
                            tempBitmap = PhotoProcessing.Florid(zoomImage);
                            mainBitmap = tempBitmap;

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    iv_img.setImageBitmap(tempBitmap);
                                    ll_loading.setVisibility(View.GONE);
                                }
                            });
                        }
                    });
                }
            });

            iv_retro.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iv_original_classic_filter.setImageBitmap(null);
                    iv_soft.setImageBitmap(null);
                    iv_classic.setImageBitmap(null);
                    iv_florid.setImageBitmap(null);
                    iv_retro.setImageBitmap(preview_selected);
                    iv_genteel.setImageBitmap(null);
                    iv_film.setImageBitmap(null);
                    iv_recall.setImageBitmap(null);
                    iv_yogurt.setImageBitmap(null);
                    iv_fleeting_time.setImageBitmap(null);
                    iv_luscious.setImageBitmap(null);

                    Model.getInstance().getGloblThreadPool().execute(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ll_loading.setVisibility(View.VISIBLE);
                                }
                            });

                            bitmap = BitmapFactory.decodeFile(picsaveurl);
                            Bitmap zoomImage = ImageHelper.zoomImage(bitmap);
                            tempBitmap = PhotoProcessing.Retro(zoomImage);
                            mainBitmap = tempBitmap;

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    iv_img.setImageBitmap(tempBitmap);
                                    ll_loading.setVisibility(View.GONE);
                                }
                            });
                        }
                    });
                }
            });

            iv_genteel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iv_original_classic_filter.setImageBitmap(null);
                    iv_soft.setImageBitmap(null);
                    iv_classic.setImageBitmap(null);
                    iv_florid.setImageBitmap(null);
                    iv_retro.setImageBitmap(null);
                    iv_genteel.setImageBitmap(preview_selected);
                    iv_film.setImageBitmap(null);
                    iv_recall.setImageBitmap(null);
                    iv_yogurt.setImageBitmap(null);
                    iv_fleeting_time.setImageBitmap(null);
                    iv_luscious.setImageBitmap(null);

                    Model.getInstance().getGloblThreadPool().execute(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ll_loading.setVisibility(View.VISIBLE);
                                }
                            });

                            bitmap = BitmapFactory.decodeFile(picsaveurl);
                            Bitmap zoomImage = ImageHelper.zoomImage(bitmap);
                            tempBitmap = PhotoProcessing.Genteel(zoomImage);
                            mainBitmap = tempBitmap;

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    iv_img.setImageBitmap(tempBitmap);
                                    ll_loading.setVisibility(View.GONE);
                                }
                            });
                        }
                    });
                }
            });

            iv_film.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iv_original_classic_filter.setImageBitmap(null);
                    iv_soft.setImageBitmap(null);
                    iv_classic.setImageBitmap(null);
                    iv_florid.setImageBitmap(null);
                    iv_retro.setImageBitmap(null);
                    iv_genteel.setImageBitmap(null);
                    iv_film.setImageBitmap(preview_selected);
                    iv_recall.setImageBitmap(null);
                    iv_yogurt.setImageBitmap(null);
                    iv_fleeting_time.setImageBitmap(null);
                    iv_luscious.setImageBitmap(null);

                    Model.getInstance().getGloblThreadPool().execute(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ll_loading.setVisibility(View.VISIBLE);
                                }
                            });

                            bitmap = BitmapFactory.decodeFile(picsaveurl);
                            Bitmap zoomImage = ImageHelper.zoomImage(bitmap);
                            tempBitmap = PhotoProcessing.Film(zoomImage);
                            mainBitmap = tempBitmap;

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    iv_img.setImageBitmap(tempBitmap);
                                    ll_loading.setVisibility(View.GONE);
                                }
                            });
                        }
                    });
                }
            });

            iv_recall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iv_original_classic_filter.setImageBitmap(null);
                    iv_soft.setImageBitmap(null);
                    iv_classic.setImageBitmap(null);
                    iv_florid.setImageBitmap(null);
                    iv_retro.setImageBitmap(null);
                    iv_genteel.setImageBitmap(null);
                    iv_film.setImageBitmap(null);
                    iv_recall.setImageBitmap(preview_selected);
                    iv_yogurt.setImageBitmap(null);
                    iv_fleeting_time.setImageBitmap(null);
                    iv_luscious.setImageBitmap(null);

                    Model.getInstance().getGloblThreadPool().execute(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ll_loading.setVisibility(View.VISIBLE);
                                }
                            });

                            bitmap = BitmapFactory.decodeFile(picsaveurl);
                            Bitmap zoomImage = ImageHelper.zoomImage(bitmap);
                            tempBitmap = PhotoProcessing.Recall(zoomImage);
                            mainBitmap = tempBitmap;

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    iv_img.setImageBitmap(tempBitmap);
                                    ll_loading.setVisibility(View.GONE);
                                }
                            });
                        }
                    });
                }
            });

            iv_yogurt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iv_original_classic_filter.setImageBitmap(null);
                    iv_soft.setImageBitmap(null);
                    iv_classic.setImageBitmap(null);
                    iv_florid.setImageBitmap(null);
                    iv_retro.setImageBitmap(null);
                    iv_genteel.setImageBitmap(null);
                    iv_film.setImageBitmap(null);
                    iv_recall.setImageBitmap(null);
                    iv_yogurt.setImageBitmap(preview_selected);
                    iv_fleeting_time.setImageBitmap(null);
                    iv_luscious.setImageBitmap(null);

                    Model.getInstance().getGloblThreadPool().execute(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ll_loading.setVisibility(View.VISIBLE);
                                }
                            });

                            bitmap = BitmapFactory.decodeFile(picsaveurl);
                            Bitmap zoomImage = ImageHelper.zoomImage(bitmap);
                            tempBitmap = PhotoProcessing.Yogurt(zoomImage);
                            mainBitmap = tempBitmap;

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    iv_img.setImageBitmap(tempBitmap);
                                    ll_loading.setVisibility(View.GONE);
                                }
                            });
                        }
                    });
                }
            });

            iv_fleeting_time.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iv_original_classic_filter.setImageBitmap(null);
                    iv_soft.setImageBitmap(null);
                    iv_classic.setImageBitmap(null);
                    iv_florid.setImageBitmap(null);
                    iv_retro.setImageBitmap(null);
                    iv_genteel.setImageBitmap(null);
                    iv_film.setImageBitmap(null);
                    iv_recall.setImageBitmap(null);
                    iv_yogurt.setImageBitmap(null);
                    iv_fleeting_time.setImageBitmap(preview_selected);
                    iv_luscious.setImageBitmap(null);

                    Model.getInstance().getGloblThreadPool().execute(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ll_loading.setVisibility(View.VISIBLE);
                                }
                            });

                            bitmap = BitmapFactory.decodeFile(picsaveurl);
                            Bitmap zoomImage = ImageHelper.toSquare(ImageHelper.zoomImage(bitmap));
                            tempBitmap = PhotoProcessing.FleetingTime(zoomImage);
                            mainBitmap = tempBitmap;

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    iv_img.setImageBitmap(tempBitmap);
                                    ll_loading.setVisibility(View.GONE);
                                }
                            });
                        }
                    });
                }
            });

            iv_luscious.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iv_original_classic_filter.setImageBitmap(null);
                    iv_soft.setImageBitmap(null);
                    iv_classic.setImageBitmap(null);
                    iv_florid.setImageBitmap(null);
                    iv_retro.setImageBitmap(null);
                    iv_genteel.setImageBitmap(null);
                    iv_film.setImageBitmap(null);
                    iv_recall.setImageBitmap(null);
                    iv_yogurt.setImageBitmap(null);
                    iv_fleeting_time.setImageBitmap(null);
                    iv_luscious.setImageBitmap(preview_selected);

                    Model.getInstance().getGloblThreadPool().execute(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ll_loading.setVisibility(View.VISIBLE);
                                }
                            });

                            bitmap = BitmapFactory.decodeFile(picsaveurl);
                            Bitmap zoomImage = ImageHelper.toSquare(ImageHelper.zoomImage(bitmap));
                            tempBitmap = PhotoProcessing.Luscious(zoomImage);
                            mainBitmap = tempBitmap;

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    iv_img.setImageBitmap(tempBitmap);
                                    ll_loading.setVisibility(View.GONE);
                                }
                            });
                        }
                    });
                }
            });
        }
    }

    /**
     * 根据位置设置效果预览图
     *
     * @param position
     */
    private void updatePreview(int position) {
        switch (position) {
            case 1:
                iv_original.setImageBitmap(preview_selected);
                iv_pencil.setImageBitmap(null);
                iv_colorpencil.setImageBitmap(null);
                iv_watercolor.setImageBitmap(null);
                iv_carving.setImageBitmap(null);
                iv_frostedglass.setImageBitmap(null);
                iv_chalkpaint.setImageBitmap(null);
                iv_lowpoly.setImageBitmap(null);
                iv_coherencefilter.setImageBitmap(null);
                iv_edgepreserving.setImageBitmap(null);
                break;
            case 2:
                iv_original.setImageBitmap(null);
                iv_pencil.setImageBitmap(preview_selected);
                iv_colorpencil.setImageBitmap(null);
                iv_watercolor.setImageBitmap(null);
                iv_carving.setImageBitmap(null);
                iv_frostedglass.setImageBitmap(null);
                iv_chalkpaint.setImageBitmap(null);
                iv_lowpoly.setImageBitmap(null);
                iv_coherencefilter.setImageBitmap(null);
                iv_edgepreserving.setImageBitmap(null);
                break;
            case 3:
                iv_original.setImageBitmap(null);
                iv_pencil.setImageBitmap(null);
                iv_colorpencil.setImageBitmap(preview_selected);
                iv_watercolor.setImageBitmap(null);
                iv_carving.setImageBitmap(null);
                iv_frostedglass.setImageBitmap(null);
                iv_chalkpaint.setImageBitmap(null);
                iv_lowpoly.setImageBitmap(null);
                iv_coherencefilter.setImageBitmap(null);
                iv_edgepreserving.setImageBitmap(null);
                break;
            case 4:
                iv_original.setImageBitmap(null);
                iv_pencil.setImageBitmap(null);
                iv_colorpencil.setImageBitmap(null);
                iv_watercolor.setImageBitmap(preview_selected);
                iv_carving.setImageBitmap(null);
                iv_frostedglass.setImageBitmap(null);
                iv_chalkpaint.setImageBitmap(null);
                iv_lowpoly.setImageBitmap(null);
                iv_coherencefilter.setImageBitmap(null);
                iv_edgepreserving.setImageBitmap(null);
                break;
            case 5:
                iv_original.setImageBitmap(null);
                iv_pencil.setImageBitmap(null);
                iv_colorpencil.setImageBitmap(null);
                iv_watercolor.setImageBitmap(null);
                iv_carving.setImageBitmap(preview_selected);
                iv_frostedglass.setImageBitmap(null);
                iv_chalkpaint.setImageBitmap(null);
                iv_lowpoly.setImageBitmap(null);
                iv_coherencefilter.setImageBitmap(null);
                iv_edgepreserving.setImageBitmap(null);
                break;
            case 6:
                iv_original.setImageBitmap(null);
                iv_pencil.setImageBitmap(null);
                iv_colorpencil.setImageBitmap(null);
                iv_watercolor.setImageBitmap(null);
                iv_carving.setImageBitmap(null);
                iv_frostedglass.setImageBitmap(preview_selected);
                iv_chalkpaint.setImageBitmap(null);
                iv_lowpoly.setImageBitmap(null);
                iv_coherencefilter.setImageBitmap(null);
                iv_edgepreserving.setImageBitmap(null);
                break;
            case 7:
                iv_original.setImageBitmap(null);
                iv_pencil.setImageBitmap(null);
                iv_colorpencil.setImageBitmap(null);
                iv_watercolor.setImageBitmap(null);
                iv_carving.setImageBitmap(null);
                iv_frostedglass.setImageBitmap(null);
                iv_chalkpaint.setImageBitmap(preview_selected);
                iv_lowpoly.setImageBitmap(null);
                iv_coherencefilter.setImageBitmap(null);
                iv_edgepreserving.setImageBitmap(null);
                break;
            case 8:
                iv_original.setImageBitmap(null);
                iv_pencil.setImageBitmap(null);
                iv_colorpencil.setImageBitmap(null);
                iv_watercolor.setImageBitmap(null);
                iv_carving.setImageBitmap(null);
                iv_frostedglass.setImageBitmap(null);
                iv_chalkpaint.setImageBitmap(null);
                iv_lowpoly.setImageBitmap(preview_selected);
                iv_coherencefilter.setImageBitmap(null);
                iv_edgepreserving.setImageBitmap(null);
                break;
            case 9:
                iv_original.setImageBitmap(null);
                iv_pencil.setImageBitmap(null);
                iv_colorpencil.setImageBitmap(null);
                iv_watercolor.setImageBitmap(null);
                iv_carving.setImageBitmap(null);
                iv_frostedglass.setImageBitmap(null);
                iv_chalkpaint.setImageBitmap(null);
                iv_lowpoly.setImageBitmap(null);
                iv_coherencefilter.setImageBitmap(preview_selected);
                iv_edgepreserving.setImageBitmap(null);
                break;
            case 10:
                iv_original.setImageBitmap(null);
                iv_pencil.setImageBitmap(null);
                iv_colorpencil.setImageBitmap(null);
                iv_watercolor.setImageBitmap(null);
                iv_carving.setImageBitmap(null);
                iv_frostedglass.setImageBitmap(null);
                iv_chalkpaint.setImageBitmap(null);
                iv_lowpoly.setImageBitmap(null);
                iv_coherencefilter.setImageBitmap(null);
                iv_edgepreserving.setImageBitmap(preview_selected);
                break;
        }
    }

    /**
     * 初始化UI
     */
    private void initUI() {
        ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
        ll_loading.setVisibility(View.GONE);

        btn_back = (ImageButton) findViewById(R.id.btn_back);
        btn_ok = (ImageButton) findViewById(R.id.btn_ok);

        iv_img = (ImageViewTouch) findViewById(R.id.iv_img);
        tv_filter_title = (TextView) findViewById(R.id.tv_filter_title);

        /**
         * 经典手绘
         */
        hsv_classic_hand = (HorizontalScrollView) findViewById(R.id.hsv_classic_hand);
        hsv_classic_hand.setVisibility(View.VISIBLE);
        iv_original = (ImageView) findViewById(R.id.iv_original);
        iv_pencil = (ImageView) findViewById(R.id.iv_pencil);
        iv_colorpencil = (ImageView) findViewById(R.id.iv_colorpencil);
        iv_watercolor = (ImageView) findViewById(R.id.iv_watercolor);
        iv_carving = (ImageView) findViewById(R.id.iv_carving);
        iv_frostedglass = (ImageView) findViewById(R.id.iv_frostedglass);
        iv_chalkpaint = (ImageView) findViewById(R.id.iv_chalkpaint);
        iv_lowpoly = (ImageView) findViewById(R.id.iv_lowpoly);
        iv_coherencefilter = (ImageView) findViewById(R.id.iv_coherencefilter);
        iv_edgepreserving = (ImageView) findViewById(R.id.iv_edgepreserving);

        /**
         * 梦幻浮生
         */
        hsv_dream_life = (HorizontalScrollView) findViewById(R.id.hsv_dream_life);
        hsv_dream_life.setVisibility(View.GONE);
        iv_original_dream_life = (ImageView) findViewById(R.id.iv_original_dream_life);
        iv_iceimg = (ImageView) findViewById(R.id.iv_iceimg);
        iv_fireimg = (ImageView) findViewById(R.id.iv_fireimg);
        iv_delusion = (ImageView) findViewById(R.id.iv_delusion);
        iv_brushing = (ImageView) findViewById(R.id.iv_brushing);
        iv_shutter = (ImageView) findViewById(R.id.iv_shutter);
        iv_waterstreak = (ImageView) findViewById(R.id.iv_waterstreak);
        iv_paintbrush = (ImageView) findViewById(R.id.iv_paintbrush);
        iv_edge_degradation = (ImageView) findViewById(R.id.iv_edge_degradation);
        iv_noise = (ImageView) findViewById(R.id.iv_noise);

        /**
         * 色彩世界
         */
        hsv_color_world = (HorizontalScrollView) findViewById(R.id.hsv_color_world);
        hsv_color_world.setVisibility(View.GONE);
        iv_original_color_world = (ImageView) findViewById(R.id.iv_original_color_world);
        iv_polaroid = (ImageView) findViewById(R.id.iv_polaroid);
        iv_red = (ImageView) findViewById(R.id.iv_red);
        iv_green = (ImageView) findViewById(R.id.iv_green);
        iv_blue = (ImageView) findViewById(R.id.iv_blue);
        iv_yellow = (ImageView) findViewById(R.id.iv_yellow);
        iv_lomo = (ImageView) findViewById(R.id.iv_lomo);
        iv_neon = (ImageView) findViewById(R.id.iv_neon);
        iv_black_white = (ImageView) findViewById(R.id.iv_black_white);
        iv_old_photo = (ImageView) findViewById(R.id.iv_old_photo);

        /**
         * 浮绘生活
         */
        hsv_paint_life = (HorizontalScrollView) findViewById(R.id.hsv_paint_life);
        iv_original_paint_life = (ImageView) findViewById(R.id.iv_original_paint_life);
        iv_inverted_img = (ImageView) findViewById(R.id.iv_inverted_img);
        iv_sunshineimage = (ImageView) findViewById(R.id.iv_sunshineimage);
        iv_feather = (ImageView) findViewById(R.id.iv_feather);
        iv_magnifier = (ImageView) findViewById(R.id.iv_magnifier);
        iv_magicmirror = (ImageView) findViewById(R.id.iv_magicmirror);
        iv_bright = (ImageView) findViewById(R.id.iv_bright);
        iv_masic = (ImageView) findViewById(R.id.iv_masic);
        iv_cartoon = (ImageView) findViewById(R.id.iv_cartoon);
        iv_radial_distortion = (ImageView) findViewById(R.id.iv_radial_distortion);
        iv_oilpaint = (ImageView) findViewById(R.id.iv_oilpaint);

        /**
         * 经典滤镜
         */
        hsv_classic_filter = (HorizontalScrollView) findViewById(R.id.hsv_classic_filter);
        iv_original_classic_filter = (ImageView) findViewById(R.id.iv_original_classic_filter);
        iv_soft = (ImageView) findViewById(R.id.iv_soft);
        iv_classic = (ImageView) findViewById(R.id.iv_classic);
        iv_florid = (ImageView) findViewById(R.id.iv_florid);
        iv_retro = (ImageView) findViewById(R.id.iv_retro);
        iv_genteel = (ImageView) findViewById(R.id.iv_genteel);
        iv_film = (ImageView) findViewById(R.id.iv_film);
        iv_recall = (ImageView) findViewById(R.id.iv_recall);
        iv_yogurt = (ImageView) findViewById(R.id.iv_yogurt);
        iv_fleeting_time = (ImageView) findViewById(R.id.iv_fleeting_time);
        iv_luscious = (ImageView) findViewById(R.id.iv_luscious);
    }

    /**
     * 设置状态栏透明
     */
    public void setTransluteWindow() {
        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    }

    public int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
