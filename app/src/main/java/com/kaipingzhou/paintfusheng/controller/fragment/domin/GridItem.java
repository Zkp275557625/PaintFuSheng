package com.kaipingzhou.paintfusheng.controller.fragment.domin;

/**
 * 创建人：周开平
 * 创建时间：2017/4/18 23:28
 * 作用：
 */

public class GridItem {
    public final String name;
    public final String path;
    public final String imageTaken;
    public final long imageSize;

    public GridItem(final String n, final String p, final String imageTaken, final long imageSize) {
        name = n;
        path = p;
        this.imageTaken = imageTaken;
        this.imageSize = imageSize;
    }
}
