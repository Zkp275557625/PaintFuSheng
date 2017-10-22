package com.hqu.cst.sketcher;

/**
 * 创建人：周开平
 * 创建时间：2017/4/19 0:50
 * 作用：
 */

public class OpenCVCPP {
    static {
        System.loadLibrary("OpenCV");
    }

    //铅笔画 buf 和buf2分别为需要处理的图像和纹理图
    public static native int[] Pencil(int[] buf, int w, int h, int[] buf2, int w2, int h2);

    public static native int[] ColorPencil(int[] buf, int w, int h, int[] buf2, int w2, int h2);

    //抽象画
    public static native int[] CoherenceFilter(int[] buf, int w, int h);

    //LowPoly风格 index可选0和1 两种风格
    public static native int[] Lowpoly(int[] buf, int w, int h, int index);

    //水彩画
    public static native int[] Watercolor(int[] buf, int w, int h, int size);

    //浮雕
    public static native int[] Carving(int[] buf, int w, int h);

    //卡通
    public static native int[] Cartoon(int[] buf, int w, int h, int[] buf2, int w2, int h2);

    //毛玻璃
    public static native int[] Maoboli(int[] buf, int w, int h);

    //马赛克
    public static native int[] Mosaic(int[] buf, int w, int h, int size2);

    //ascii马赛克
    public static native int[] ascii(int[] buf, int w, int h);

    //下面三个为opencv自带的效果
    public static native int[] edgePreservingFilter(int[] buf, int w, int h);

    public static native int[] stylization(int[] buf, int w, int h);

    public static native int[] detailEnhance(int[] buf, int w, int h);

    //粉笔画
    public static native int[] Fenbi(int[] buf, int w, int h);

    //线条画
    public static native int[] Line(int[] buf, int w, int h);

    //多色调  div可选2的倍数 越大颜色越少
    public static native int[] ColorReduce(int[] buf, int w, int h, int div);

    //图片融合
    public static native int[] MultiStyle(int[] buf, int w, int h,
                                          int[] buf2, int w2, int h2, int[] Points, int PointsSize);
}
