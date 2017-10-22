package com.kaipingzhou.paintfusheng.utils;

import android.graphics.Color;

public class Function {

    public static int FClamp(final int t, final int tLow, final int tHigh) {
        if (t < tHigh) {
            return ((t > tLow) ? t : tLow);
        }
        return tHigh;
    }

    public static double FClampDouble(final double t, final double tLow, final double tHigh) {
        if (t < tHigh) {
            return ((t > tLow) ? t : tLow);
        }
        return tHigh;
    }

    public static int FClamp0255(final double d) {
        return (int) (FClampDouble(d, 0.0, 255.0) + 0.5);
    }

    public static double colorDiff(int[] rgb1, int[] rgb2) {
        // (1-(d/d0)^2)^2
        double d2, r2;
        d2 = colorDistance(rgb1, rgb2);

        if (d2 >= 22500)
            return 0.0;

        r2 = d2 / 22500;

        return ((1.0d - r2) * (1.0d - r2));
    }

    public static double colorDistance(int[] rgb1, int[] rgb2) {
        int dr, dg, db;
        dr = rgb1[0] - rgb2[0];
        dg = rgb1[1] - rgb2[1];
        db = rgb1[2] - rgb2[2];
        return dr * dr + dg * dg + db * db;
    }

    public static int clamp(int value) {
        return value > 255 ? 255 : ((value < 0) ? 0 : value);
    }

    public static int superpositionColor(int ta, int red, int green, int blue, double k) {
        int vignetteColor = Color.BLACK;
        red = (int) (Color.red(vignetteColor) * k + red * (1.0 - k));
        green = (int) (Color.green(vignetteColor) * k + green * (1.0 - k));
        blue = (int) (Color.blue(vignetteColor) * k + blue * (1.0 - k));
        int color = (ta << 24) | (clamp(red) << 16) | (clamp(green) << 8) | clamp(blue);
        return color;
    }

    static int getRandomInt(int a, int b) {
        int min = Math.min(a, b);
        int max = Math.max(a, b);
        return min + (int) (Math.random() * (max - min + 1));
    }
}
