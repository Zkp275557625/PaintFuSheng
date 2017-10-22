package com.xinlan.imageedit.editimage.filter;

import android.graphics.Bitmap;

/**
 * 创建人：周开平
 * 创建时间：2017/4/20 22:51
 * 作用：
 */

public class PhotoProcessing {
    static {
        System.loadLibrary("photoprocessing");
    }

    public static native int nativeInitBitmap(int width, int height);

    public static native void nativeGetBitmapRow(int y, int[] pixels);

    public static native void nativeSetBitmapRow(int y, int[] pixels);

    public static native int nativeGetBitmapWidth();

    public static native int nativeGetBitmapHeight();

    public static native void nativeDeleteBitmap();

    public static native void nativeApplyInstafix();

    public static native void nativeApplyAnsel();

    public static native void nativeApplyTestino();

    public static native void nativeApplyXPro();

    public static native void nativeApplyRetro();

    public static native void nativeApplyBW();

    public static native void nativeApplySepia();

    public static native void nativeApplyCyano();

    public static native void nativeApplyGeorgia();

    public static native void nativeApplySahara();

    public static native void nativeApplyHDR();

    public static Bitmap getBitmapFromNative(Bitmap bitmap) {
        int width = nativeGetBitmapWidth();
        int height = nativeGetBitmapHeight();

        if (bitmap == null || width != bitmap.getWidth() || height != bitmap.getHeight() || !bitmap.isMutable()) { // in
            Bitmap.Config config = Bitmap.Config.ARGB_8888;
            if (bitmap != null) {
                config = bitmap.getConfig();
                bitmap.recycle();
            }
            bitmap = Bitmap.createBitmap(width, height, config);
        }

        int[] pixels = new int[width];
        for (int y = 0; y < height; y++) {
            nativeGetBitmapRow(y, pixels);
            bitmap.setPixels(pixels, 0, width, 0, y, width, 1);
        }

        return bitmap;
    }

    public static void sendBitmapToNative(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        nativeInitBitmap(width, height);
        int[] pixels = new int[width];
        for (int y = 0; y < height; y++) {
            bitmap.getPixels(pixels, 0, width, 0, y, width, 1);
            nativeSetBitmapRow(y, pixels);
        }
    }

    /**
     * 轻柔
     *
     * @param bitmap
     * @return
     */
    public static Bitmap Soft(Bitmap bitmap) {
        sendBitmapToNative(bitmap);
        nativeApplyInstafix();
        Bitmap soft = getBitmapFromNative(bitmap);
        nativeDeleteBitmap();

        return soft;
    }

    /**
     * 经典
     *
     * @param bitmap
     * @return
     */
    public static Bitmap Classic(Bitmap bitmap) {
        sendBitmapToNative(bitmap);
        nativeApplyTestino();
        Bitmap classic = getBitmapFromNative(bitmap);
        nativeDeleteBitmap();

        return classic;
    }

    /**
     * 绚丽
     *
     * @param bitmap
     * @return
     */
    public static Bitmap Florid(Bitmap bitmap) {
        sendBitmapToNative(bitmap);
        nativeApplyXPro();
        Bitmap florid = getBitmapFromNative(bitmap);
        nativeDeleteBitmap();

        return florid;
    }

    /**
     * 复古
     *
     * @param bitmap
     * @return
     */
    public static Bitmap Retro(Bitmap bitmap) {
        sendBitmapToNative(bitmap);
        nativeApplyRetro();
        Bitmap retro = getBitmapFromNative(bitmap);
        nativeDeleteBitmap();

        return retro;
    }

    /**
     * 优雅
     *
     * @param bitmap
     * @return
     */
    public static Bitmap Genteel(Bitmap bitmap) {
        sendBitmapToNative(bitmap);
        nativeApplyBW();
        Bitmap genteel = getBitmapFromNative(bitmap);
        nativeDeleteBitmap();

        return genteel;
    }

    /**
     * 胶片
     *
     * @param bitmap
     * @return
     */
    public static Bitmap Film(Bitmap bitmap) {
        sendBitmapToNative(bitmap);
        nativeApplySepia();
        Bitmap film = getBitmapFromNative(bitmap);
        nativeDeleteBitmap();

        return film;
    }

    /**
     * 回忆
     *
     * @param bitmap
     * @return
     */
    public static Bitmap Recall(Bitmap bitmap) {
        sendBitmapToNative(bitmap);
        nativeApplyCyano();
        Bitmap recall = getBitmapFromNative(bitmap);
        nativeDeleteBitmap();

        return recall;
    }

    /**
     * 优格
     *
     * @param bitmap
     * @return
     */
    public static Bitmap Yogurt(Bitmap bitmap) {
        sendBitmapToNative(bitmap);
        nativeApplyGeorgia();
        Bitmap yogurt = getBitmapFromNative(bitmap);
        nativeDeleteBitmap();

        return yogurt;
    }

    /**
     * 流年
     *
     * @param bitmap
     * @return
     */
    public static Bitmap FleetingTime(Bitmap bitmap) {
        sendBitmapToNative(bitmap);
        nativeApplySahara();
        Bitmap fleeting_time = getBitmapFromNative(bitmap);
        nativeDeleteBitmap();

        return fleeting_time;
    }

    /**
     * 光绚
     *
     * @param bitmap
     * @return
     */
    public static Bitmap Luscious(Bitmap bitmap) {
        sendBitmapToNative(bitmap);
        nativeApplyHDR();
        Bitmap luscious = getBitmapFromNative(bitmap);
        nativeDeleteBitmap();

        return luscious;
    }
}
