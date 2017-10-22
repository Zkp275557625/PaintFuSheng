package com.kaipingzhou.paintfusheng.utils;

/**
 * 创建人：周开平
 * 创建时间：2017/4/20 18:52
 * 作用：
 */

public class Point {
    private int x;
    private int y;
    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }
    public int getRow() {
        return this.y;
    }
    public int getCol() {
        return this.x;
    }
}
