package com.kaipingzhou.paintfusheng.controller.fragment.utils;

import android.graphics.Bitmap;

import java.lang.ref.SoftReference;
import java.util.HashMap;

/**
 * 创建人：周开平
 * 创建时间：2017/4/18 23:31
 * 作用：
 */

public class MemoryCache {
    private static final HashMap<String, SoftReference<Bitmap>> cache = new HashMap<String, SoftReference<Bitmap>>();

    Bitmap get(final String id) {
        if (!cache.containsKey(id)) return null;
        SoftReference<Bitmap> ref = cache.get(id);
        return ref.get();
    }

    void put(final String id, final Bitmap bitmap) {
        cache.put(id, new SoftReference<Bitmap>(bitmap));
    }
}
