package com.kaipingzhou.paintfusheng.controller.view.imageview;

/**
 * 创建人：周开平
 * 创建时间：2017/4/21 0:17
 * 作用：
 */

public interface Easing {
    double easeOut( double time, double start, double end, double duration );

    double easeIn( double time, double start, double end, double duration );

    double easeInOut( double time, double start, double end, double duration );
}
