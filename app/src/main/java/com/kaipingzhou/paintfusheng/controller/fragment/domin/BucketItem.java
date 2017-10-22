package com.kaipingzhou.paintfusheng.controller.fragment.domin;

/**
 * 创建人：周开平
 * 创建时间：2017/4/18 23:29
 * 作用：
 */

public class BucketItem extends GridItem {

    public final int id;
    public int images = 1;

    /**
     * Creates a new BucketItem
     *
     * @param n the name of the bucket
     * @param p the absolute path to the bucket
     * @param i the bucket ID
     */
    public BucketItem(final String n, final String p, final String taken, int i) {
        super(n, p, taken, 0);
        id = i;
    }
}
