package com.kaipingzhou.paintfusheng.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;

/**
 * 创建人：周开平
 * 创建时间：2017/4/18 16:32
 * 作用：照片处理工具类
 */

public class ImageHelper {
    /***
     * 图片压缩
     * @param bgimage 源图片资源
     * @return 宽高比例不变，且都在1080以内的图片
     */
    public static Bitmap zoomImage(Bitmap bgimage) {
        // 获取这个图片的宽和高
        float width = bgimage.getWidth();
        float height = bgimage.getHeight();

        double newWidth = width;
        double newHeight = height;

        int Max = (int) Math.max(newWidth, newHeight);
        int flag = 0;//默认 h > w

        if (newWidth > newHeight) {
            flag = 1;
        }

        if (Max > 1080 && flag == 0) {//height > width
            newHeight = 1080;
            newWidth = 1080 * 1.0f / height * width;
        } else if (Max > 1080 && flag == 1) {//width > height
            newWidth = 1080;
            newHeight = 1080 * 1.0f / width * height;
        }

        // 创建操作图片用的matrix对象
        Matrix matrix = new Matrix();
        // 计算宽高缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 缩放图片动作
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width,
                (int) height, matrix, true);
        return bitmap;
    }

    /**
     * 压缩图片至指定大小
     *
     * @param bgimage
     * @param newWidth  新的宽度
     * @param newHeight 新的高度
     * @return
     */
    public static Bitmap zoomImageToFixedSize(Bitmap bgimage, double newWidth, double newHeight) {
        // 获取这个图片的宽和高
        float width = bgimage.getWidth();
        float height = bgimage.getHeight();

        // 创建操作图片用的matrix对象
        Matrix matrix = new Matrix();
        // 计算宽高缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 缩放图片动作
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width,
                (int) height, matrix, true);
        return bitmap;
    }

    /**
     * 将bitmap压缩成w = h的新图片（截取）
     *
     * @param bitmap
     * @return result w = h的图片
     */
    public static Bitmap toSquare(Bitmap bitmap) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        Bitmap result = null;

        if (w <= h) {//h > w
            int s = (int) ((h - w) * 1.0 / 2);
            result = Bitmap.createBitmap(bitmap, 0, s, w, w);
        } else {//h < w
            int s = (int) ((w - h) * 1.0 / 2);
            result = Bitmap.createBitmap(bitmap, s, 0, h, h);
        }
        return result;
    }

    /**
     * 给图片加水印
     *
     * @param src       原图
     * @param watermark 水印
     * @return 加水印的原图
     */
    public static Bitmap WaterMask(Bitmap src, Bitmap watermark) {
        int w = src.getWidth();
        int h = src.getHeight();

        //根据bitmap缩放水印图片
        float w1 = w / 5;
        float h1 = (float) (w1 / 2.782);
        //获取原始水印图片的宽、高
        int w2 = watermark.getWidth();
        int h2 = watermark.getHeight();

        //计算缩放的比例
        float scalewidth = ((float) w1) / w2;
        float scaleheight = ((float) h1) / h2;

        Matrix matrix = new Matrix();
        matrix.postScale(scalewidth, scaleheight);

        watermark = Bitmap.createBitmap(watermark, 0, 0, w2, h2, matrix, true);
        //获取新的水印图片的宽、高
        w2 = watermark.getWidth();
        h2 = watermark.getHeight();

        Bitmap result = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);// 创建一个新的和SRC长度宽度一样的位图
        Canvas cv = new Canvas(result);
        //在canvas上绘制原图和新的水印图
        cv.drawBitmap(src, 0, 0, null);
        //水印图绘制在画布的右下角
        cv.drawBitmap(watermark, w - w2, h - h2, null);
        cv.save(Canvas.ALL_SAVE_FLAG);
        cv.restore();

        return result;
    }

    /**
     * 冰冻效果
     *
     * @param bmp
     * @return
     */
    public static Bitmap IceImage(Bitmap bmp) {
        int width = bmp.getWidth();
        int height = bmp.getHeight();

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        int pixColor;
        int r, g, b, r1, g1, b1;
        int[] pixels = new int[width * height];
        bmp.getPixels(pixels, 0, width, 0, 0, width, height);

        for (int i = 0; i < height; i++) {
            for (int k = 0; k < width; k++) {
                //获取前一个像素颜色
                pixColor = pixels[width * i + k];
                r = Color.red(pixColor);
                g = Color.green(pixColor);
                b = Color.blue(pixColor);
                //红色
                r1 = r - g - b;
                r1 = r1 * 3 / 2;

                //绿色
                g1 = g - b - r;
                g1 = g1 * 3 / 2;

                //蓝色
                b1 = b - g - r;
                b1 = b1 * 3 / 2;

                //检查各点像素值是否超出范围
                if (r1 < 0) {
                    r1 = 0;
                } else if (r1 > 255) {
                    r1 = 255;
                }

                if (g1 < 0) {
                    g1 = 0;
                } else if (g1 > 255) {
                    g1 = 255;
                }

                if (b1 < 0) {
                    b1 = 0;
                } else if (b1 > 255) {
                    b1 = 255;
                }

                pixels[width * i + k] = Color.argb(255, r1, g1, b1);
            }
        }
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    /**
     * 熔铸（反冰冻）
     *
     * @param bitmap
     * @return
     */
    public static Bitmap FireImage(Bitmap bitmap) {

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int[] oldpixels = new int[w * h];
        int[] newpixels = new int[w * h];

        int color;
        int pixR = 0, pixG = 0, pixB = 0;

        bitmap.getPixels(oldpixels, 0, w, 0, 0, w, h);
        Bitmap result = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);

        for (int i = 0; i < h; i++) {
            for (int k = 0; k < w; k++) {
                //获取前一个像素颜色
                color = oldpixels[w * i + k];
                pixR = Color.red(color);
                pixG = Color.green(color);
                pixB = Color.blue(color);

                //R 分量
                pixR = pixR * 128 / (pixG + pixB + 1);
                pixR = Math.min(255, Math.max(0, pixR));
                //G 分量
                pixG = pixG * 128 / (pixB + pixR + 1);
                pixG = Math.min(255, Math.max(0, pixG));
                //B 分量
                pixB = pixB * 128 / (pixR + pixG + 1);
                pixB = Math.min(255, Math.max(0, pixB));

                newpixels[w * i + k] = Color.rgb(pixR, pixG, pixB);
            }
        }
        result.setPixels(newpixels, 0, w, 0, 0, w, h);

        return result;
    }

    /**
     * 幻觉
     *
     * @param bitmap
     * @return
     */
    public static Bitmap Delusion(Bitmap bitmap) {

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        Bitmap result = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);

        int r, g, b, color;
        int r1, g1, b1, color1;

        int[] oldPx = new int[w * h];
        int[] newPx = new int[w * h];

        bitmap.getPixels(oldPx, 0, w, 0, 0, w, h);

        for (int x = 0; x < (w - 1); x++) {
            for (int y = 0; y < (h - 1); y++) {
                //得到当前点的r,g,b值
                color = oldPx[x * h + y];
                r = Color.red(color);
                g = Color.green(color);
                b = Color.blue(color);

                double _scale = Math.sqrt(w * w + h * h) / 2;
                double _offset = (int) (_scale / 2);
                double cx = (x - w / 2.0) / _scale;
                double cy = (y - h / 2.0) / _scale;
                double _amount = Math.PI / 3;
                double angle = Math.floor(Math.atan2(cy, cx) / 2.0 / _amount) * 2.0 * _amount + _amount;
                double radius = Math.sqrt(cx * cx + cy * cy);
                int xx = (int) (x - _offset * Math.cos(angle));
                int yy = (int) (y - _offset * Math.sin(angle));

                xx = Function.FClamp(xx, 0, (int) (w - 1));
                yy = Function.FClamp(yy, 0, (int) (h - 1));

                //得到当前点的r,g,b值
                color1 = oldPx[xx * h + yy];
                r1 = Color.red(color1);
                g1 = Color.green(color1);
                b1 = Color.blue(color1);

                r = Function.FClamp0255(r + radius * (r1 - r));
                g = Function.FClamp0255(g + radius * (g1 - g));
                b = Function.FClamp0255(b + radius * (b1 - b));

                newPx[x * h + y] = Color.rgb(r, g, b);
            }
        }
        result.setPixels(newPx, 0, w, 0, 0, w, h);
        return result;
    }

    /**
     * 急速奔驰
     *
     * @param bitmap
     * @return
     */
    public static Bitmap Brushing(Bitmap bitmap) {

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Bitmap result = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);

        int m_fcx = (int) (w * 32768);
        int m_fcy = (int) (h * 32768);

        final int ta = 255;
        int r, g, b, color;

        int[] oldPx = new int[w * h];
        int[] newPx = new int[w * h];

        bitmap.getPixels(oldPx, 0, w, 0, 0, w, h);
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                //得到当前点的r,g,b值
                int sa = 0;
                color = oldPx[x * h + y];
                r = Color.red(color) * ta;
                g = Color.green(color) * ta;
                b = Color.blue(color) * ta;
                sa += ta;

                int fx = (x * 65536) - m_fcx;
                int fy = (y * 65536) - m_fcy;
                final int RADIUS_LENGTH = 64;

                for (int i = 0; i < RADIUS_LENGTH; i++) {
                    fx = fx - (fx / 16) * 30 / 1024;
                    fy = fy - (fy / 16) * 30 / 1024;

                    int u = (fx + m_fcx + 32768) / 65536;
                    int v = (fy + m_fcy + 32768) / 65536;
                    if (u >= 0 && u < w && v >= 0 && v < h) {
                        color = oldPx[u * h + v];
                        r += Color.red(color) * ta;
                        g += Color.green(color) * ta;
                        b += Color.blue(color) * ta;
                        sa += ta;
                    }
                }

                r = r / sa;
                g = g / sa;
                b = b / sa;

                //检查颜色值是否超出范围
                if (r > 255) {
                    r = 255;
                } else if (r < 0) {
                    r = 0;
                }

                if (g > 255) {
                    g = 255;
                } else if (g < 0) {
                    g = 0;
                }

                if (b > 255) {
                    b = 255;
                } else if (b < 0) {
                    b = 0;
                }
                newPx[x * h + y] = Color.rgb(r, g, b);
            }
        }
        result.setPixels(newPx, 0, w, 0, 0, w, h);
        return result;
    }

    public static Bitmap Shutter(Bitmap bitmap) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Bitmap result = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);

        //垂直方向上的百叶窗
        boolean _direct = false;//horizontal: true,  vertical: false

        int _width = w / 10; //10个
        int _opacity = 100;
        int _color = 0x000000;

        int r, g, b, a, color;
        int[] oldPx = new int[w * h];
        int[] newPx = new int[w * h];

        bitmap.getPixels(oldPx, 0, w, 0, 0, w, h);

        for (int x = 0; x < (w - 1); x++) {
            for (int y = 0; y < (h - 1); y++) {
                color = oldPx[x * h + y];
                r = Color.red(color);
                g = Color.green(color);
                b = Color.blue(color);

                int nMod = 0;
                if (_direct) // 水平方向
                    nMod = y % _width;
                else if (_direct == false) // 垂直方向
                    nMod = x % _width;

                double fDelta = 255.0 * (_opacity / 100.0) / (_width - 1.0);
                a = Function.FClamp0255(nMod * fDelta);
                int colorR = _color & 0xFF0000 >> 16;
                int colorG = _color & 0x00FF00 >> 8;
                int colorB = _color & 0x0000FF;
                if (_color == 0xFF) {
                    newPx[x * h + y] = Color.rgb(colorR, colorG, colorB);
                    continue;
                }
                if (a == 0)
                    continue;

                int t = 0xFF - a;
                newPx[x * h + y] = Color.rgb((colorR * a + r * t) / 0xFF,
                        (colorG * a + g * t) / 0xFF, (colorB * a + b * t) / 0xFF);
            }
        }
        result.setPixels(newPx, 0, w, 0, 0, w, h);
        return result;
    }

    /**
     * 水纹
     *
     * @param bitmap
     * @return
     */
    public static Bitmap WaterStreak(Bitmap bitmap) {
        double wave = 20.0;
        double period = 128;

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

        int[] inPixels = new int[width * height];
        int[] outPixels = new int[width * height];
        Point[][] ssPixels = new Point[height][width];
        bitmap.getPixels(inPixels, 0, width, 0, 0, width, height);
        int index = 0, index2 = 0;
        int xoffset = 0, yoffset = 0;

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                xoffset = (int) ((double) wave * Math.sin(2.0 * Math.PI * (float) row / period));
                yoffset = (int) ((double) wave * Math.cos(2.0 * Math.PI * (float) col / period));
                xoffset = xoffset + col;
                yoffset = yoffset + row;
                if (xoffset < 0 || xoffset >= width) {
                    xoffset = 0;
                }
                if (yoffset < 0 || yoffset >= height) {
                    yoffset = 0;
                }

                // save the 2D coordinate newX, and newY
                ssPixels[row][col] = new Point(xoffset, yoffset);
            }
        }

        // coordinate 2D result and fill the pixels data.
        for (int row = 0; row < height; row++) {
            int ta = 0, tr = 0, tg = 0, tb = 0;
            for (int col = 0; col < width; col++) {
                index = row * width + col;
                index2 = ssPixels[row][col].getRow() * width + ssPixels[row][col].getCol();
                ta = (inPixels[index2] >> 24) & 0xff;
                tr = (inPixels[index2] >> 16) & 0xff;
                tg = (inPixels[index2] >> 8) & 0xff;
                tb = inPixels[index2] & 0xff;
                outPixels[index] = (ta << 24) | (tr << 16) | (tg << 8) | tb;
            }
        }
        result.setPixels(outPixels, 0, width, 0, 0, width, height);
        return result;
    }

    /**
     * 画笔风格
     *
     * @param bitmap
     * @return
     */
    public static Bitmap PaintBrush(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        int[] inPixels = new int[width * height];
        int[] outPixels = new int[width * height];
        bitmap.getPixels(inPixels, 0, width, 0, 0, width, height);
        int index = 0, index2 = 0;
        int size = 13;
        int semiRow = (int) (size / 2);
        int semiCol = (int) (size / 2);
        int newX, newY;

        // initialize the color RGB array with zero...
        int[] rgb = new int[3];
        int[] rgb2 = new int[3];
        for (int i = 0; i < rgb.length; i++) {
            rgb[i] = rgb2[i] = 0;
        }

        // start the algorithm process here!!
        for (int row = 0; row < height; row++) {
            int ta = 0;
            for (int col = 0; col < width; col++) {
                index = row * width + col;
                ta = (inPixels[index] >> 24) & 0xff;
                rgb[0] = (inPixels[index] >> 16) & 0xff;
                rgb[1] = (inPixels[index] >> 8) & 0xff;
                rgb[2] = inPixels[index] & 0xff;

				/* adjust region to fit in source image */
                // color difference and moment Image
                double moment = 0.0d;
                for (int subRow = -semiRow; subRow <= semiRow; subRow++) {
                    for (int subCol = -semiCol; subCol <= semiCol; subCol++) {
                        newY = row + subRow;
                        newX = col + subCol;
                        if (newY < 0) {
                            newY = 0;
                        }
                        if (newX < 0) {
                            newX = 0;
                        }
                        if (newY >= height) {
                            newY = height - 1;
                        }
                        if (newX >= width) {
                            newX = width - 1;
                        }
                        index2 = newY * width + newX;
                        rgb2[0] = (inPixels[index2] >> 16) & 0xff; // red
                        rgb2[1] = (inPixels[index2] >> 8) & 0xff; // green
                        rgb2[2] = inPixels[index2] & 0xff; // blue
                        moment += Function.colorDiff(rgb, rgb2);
                    }
                }
                // calculate the output pixel value.
                int outPixelValue = Function.clamp((int) (255.0d * moment / (size * size)));
                outPixels[index] = (ta << 24) | (outPixelValue << 16) | (outPixelValue << 8) | outPixelValue;
            }
        }
        result.setPixels(outPixels, 0, width, 0, 0, width, height);
        return result;
    }

    /**
     * 边缘退化
     *
     * @param bitmap
     * @return
     */
    public static Bitmap EdgeDegradation(Bitmap bitmap) {
        int vignetteWidth = 50;
        int fade = 35;
        //边缘退化为黑色
        int vignetteColor = Color.BLACK;
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        int[] inPixels = new int[width * height];
        int[] outPixels = new int[width * height];
        bitmap.getPixels(inPixels, 0, width, 0, 0, width, height);
        int index = 0;
        for (int row = 0; row < height; row++) {
            int ta = 0, tr = 0, tg = 0, tb = 0;
            for (int col = 0; col < width; col++) {

                int dX = Math.min(col, width - col);
                int dY = Math.min(row, height - row);
                index = row * width + col;
                ta = (inPixels[index] >> 24) & 0xff;
                tr = (inPixels[index] >> 16) & 0xff;
                tg = (inPixels[index] >> 8) & 0xff;
                tb = inPixels[index] & 0xff;
                if ((dY <= vignetteWidth) & (dX <= vignetteWidth)) {
                    double k = 1 - (double) (Math.min(dY, dX) - vignetteWidth + fade) / (double) fade;
                    outPixels[index] = Function.superpositionColor(ta, tr, tg, tb, k);
                    continue;
                }

                if ((dX < (vignetteWidth - fade)) | (dY < (vignetteWidth - fade))) {
                    outPixels[index] = (ta << 24) | (Color.red(vignetteColor) << 16) | (Color.green(vignetteColor) << 8) | Color.blue(vignetteColor);
                } else {
                    if ((dX < vignetteWidth) & (dY > vignetteWidth)) {
                        double k = 1 - (double) (dX - vignetteWidth + fade) / (double) fade;
                        outPixels[index] = Function.superpositionColor(ta, tr, tg, tb, k);
                    } else {
                        if ((dY < vignetteWidth) & (dX > vignetteWidth)) {
                            double k = 1 - (double) (dY - vignetteWidth + fade) / (double) fade;
                            outPixels[index] = Function.superpositionColor(ta, tr, tg, tb, k);
                        } else {
                            outPixels[index] = (ta << 24) | (tr << 16) | (tg << 8) | tb;
                        }
                    }
                }
            }
        }
        result.setPixels(outPixels, 0, width, 0, 0, width, height);
        return result;
    }

    /**
     * 噪点特效
     *
     * @param bitmap
     * @return
     */
    public static Bitmap Noise(Bitmap bitmap) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Bitmap result = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);

        int r, g, b, color;
        int[] oldPx = new int[w * h];
        int[] newPx = new int[w * h];
        int num = (int) (0.2f * 32768f);
        bitmap.getPixels(oldPx, 0, w, 0, 0, w, h);

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {

                color = oldPx[x * h + y];
                r = Color.red(color);
                g = Color.green(color);
                b = Color.blue(color);

                if (num != 0) {
                    int rr = Function.getRandomInt(-255, 0xff) * num;
                    int gg = Function.getRandomInt(-255, 0xff) * num;
                    int bb = Function.getRandomInt(-255, 0xff) * num;
                    int rrr = r + (rr >> 15);
                    int ggg = g + (gg >> 15);
                    int bbb = b + (bb >> 15);
                    r = (rrr > 0xff) ? ((byte) 0xff) : ((rrr < 0) ? ((byte) 0) : ((byte) rrr));
                    g = (ggg > 0xff) ? ((byte) 0xff) : ((ggg < 0) ? ((byte) 0) : ((byte) ggg));
                    b = (bbb > 0xff) ? ((byte) 0xff) : ((bbb < 0) ? ((byte) 0) : ((byte) bbb));
                }
                newPx[x * h + y] = Color.rgb(r, g, b);
            }
        }
        result.setPixels(newPx, 0, w, 0, 0, w, h);
        return result;
    }

    /**
     * 宝丽来色
     *
     * @param bitmap
     * @return
     */
    public static Bitmap Polaroid(Bitmap bitmap) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        Bitmap resultBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);
        int color = 0;
        int r, g, b, a1, r1, g1, b1;

        int[] oldPx = new int[w * h];
        int[] newPx = new int[w * h];

        bitmap.getPixels(oldPx, 0, w, 0, 0, w, h);
        for (int i = 0; i < w * h; i++) {
            color = oldPx[i];

            r = Color.red(color);
            g = Color.green(color);
            b = Color.blue(color);

            //宝丽来色数组
            r1 = (int) (1.438 * r + (-0.062) * g + (-0.062) * b);
            g1 = (int) ((-0.122) * r + 1.378 * g + (-0.122) * b);
            b1 = (int) ((-0.016) * r + (-0.016) * g + 1.483 * b);
            a1 = (int) ((-0.03) * r + 0.05 * g + (-0.02) * b);

            if (r1 > 255) {
                r1 = 255;
            }

            if (g1 > 255) {
                g1 = 255;
            }

            if (b1 > 255) {
                b1 = 255;
            }

            if (a1 > 255) {
                a1 = 255;
            }

            newPx[i] = Color.argb(a1, r1, g1, b1);
        }
        resultBitmap.setPixels(newPx, 0, w, 0, 0, w, h);

        return resultBitmap;
    }

    /**
     * 泛红
     *
     * @param bitmap
     * @return
     */
    public static Bitmap Red(Bitmap bitmap) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        Bitmap resultBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);
        int color = 0;
        int a, r, g, b, r1;

        int[] oldPx = new int[w * h];
        int[] newPx = new int[w * h];

        bitmap.getPixels(oldPx, 0, w, 0, 0, w, h);
        for (int i = 0; i < w * h; i++) {
            color = oldPx[i];

            r = Color.red(color);
            g = Color.green(color);
            b = Color.blue(color);
            a = Color.alpha(color);

            //红色通道的值变为原来的两倍
            r1 = r * 2;

            if (r1 > 255) {
                r1 = 255;
            }

            newPx[i] = Color.argb(a, r1, g, b);
        }
        resultBitmap.setPixels(newPx, 0, w, 0, 0, w, h);

        return resultBitmap;
    }

    /**
     * 荧光绿
     *
     * @param bitmap
     * @return
     */
    public static Bitmap Green(Bitmap bitmap) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        Bitmap resultBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);
        int color = 0;
        int r, g, b, g1;

        int[] oldPx = new int[w * h];
        int[] newPx = new int[w * h];

        bitmap.getPixels(oldPx, 0, w, 0, 0, w, h);
        for (int i = 0; i < w * h; i++) {
            color = oldPx[i];

            r = Color.red(color);
            g = Color.green(color);
            b = Color.blue(color);

            //绿色通道得值变为原来的1.4倍
            g1 = (int) (0 * r + 1.4 * g + 0 * b);

            if (g1 > 255) {
                g1 = 255;
            }
            newPx[i] = Color.argb(1, r, g1, b);
        }
        resultBitmap.setPixels(newPx, 0, w, 0, 0, w, h);

        return resultBitmap;
    }

    /**
     * 宝石蓝
     *
     * @param bitmap
     * @return
     */
    public static Bitmap Blue(Bitmap bitmap) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        Bitmap resultBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);
        int color = 0;
        int r, g, b, b1;

        int[] oldPx = new int[w * h];
        int[] newPx = new int[w * h];

        bitmap.getPixels(oldPx, 0, w, 0, 0, w, h);
        for (int i = 0; i < w * h; i++) {
            color = oldPx[i];

            r = Color.red(color);
            g = Color.green(color);
            b = Color.blue(color);

            //蓝色通道的值变为原来的1.6倍
            b1 = (int) (0 * r + 0 * g + 1.6 * b);

            if (b1 > 255) {
                b1 = 255;
            }
            newPx[i] = Color.argb(1, r, g, b1);
        }
        resultBitmap.setPixels(newPx, 0, w, 0, 0, w, h);

        return resultBitmap;
    }

    /**
     * 泛黄
     *
     * @param bitmap
     * @return
     */
    public static Bitmap Yellow(Bitmap bitmap) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        Bitmap resultBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);
        int color = 0;
        int r, g, b, r1, g1;

        int[] oldPx = new int[w * h];
        int[] newPx = new int[w * h];

        bitmap.getPixels(oldPx, 0, w, 0, 0, w, h);
        for (int i = 0; i < w * h; i++) {
            color = oldPx[i];

            r = Color.red(color);
            g = Color.green(color);
            b = Color.blue(color);

            //红色通道的值和绿色通道的值增加50（红色+绿色 =黄色）
            r1 = r + 50;
            g1 = g + 50;

            if (r1 > 255) {
                r1 = 255;
            }

            if (g1 > 255) {
                g1 = 255;
            }

            newPx[i] = Color.argb(1, r1, g1, b);
        }
        resultBitmap.setPixels(newPx, 0, w, 0, 0, w, h);

        return resultBitmap;
    }

    /**
     * lomo
     *
     * @param bitmap
     * @return
     */
    public static Bitmap Lomo(Bitmap bitmap) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        Bitmap resultBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);
        int color = 0;
        int a, r, g, b, r1, g1, b1;

        int[] oldPx = new int[w * h];
        int[] newPx = new int[w * h];

        bitmap.getPixels(oldPx, 0, w, 0, 0, w, h);
        for (int i = 0; i < w * h; i++) {
            color = oldPx[i];

            r = Color.red(color);
            g = Color.green(color);
            b = Color.blue(color);
            a = Color.alpha(color);

            r1 = (int) (1.7 * r + 0.1 * g + 0.1 * b - 73.1);
            g1 = (int) (0 * r + 1.7 * g + 0.1 * b - 73.1);
            b1 = (int) (0 * r + 0.1 * g + 1.6 * b - 73.1);

            if (r1 > 255) {
                r1 = 255;
            }

            if (g1 > 255) {
                g1 = 255;
            }

            if (b1 > 255) {
                b1 = 255;
            }

            newPx[i] = Color.argb(a, r1, g1, b1);
        }
        resultBitmap.setPixels(newPx, 0, w, 0, 0, w, h);

        return resultBitmap;
    }

    /**
     * 霓虹
     *
     * @param bitmap
     * @return
     */
    public static Bitmap Neon(Bitmap bitmap) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        Bitmap resultBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);
        int color = 0;
        int r, g, b, r1, g1, b1, r2, g2, b2;

        int[] oldPx = new int[w * h];
        int[] newPx = new int[w * h];

        bitmap.getPixels(oldPx, 0, w, 0, 0, w, h);
        for (int y = 0; y < h - 1; y++) {
            for (int x = 0; x < w - 1; x++) {
                color = oldPx[x + y * w];

                r = (color >> 16) & 0xFF;
                g = (color >> 8) & 0xFF;
                b = (color >> 0) & 0xFF;

                int newcolor = oldPx[x + 1 + y * w];

                r1 = (newcolor >> 16) & 0xFF;
                g1 = (newcolor >> 8) & 0xFF;
                b1 = (newcolor >> 0) & 0xFF;

                int newcolor2 = oldPx[x + (y + 1) * w];

                r2 = (newcolor2 >> 16) & 0xFF;
                g2 = (newcolor2 >> 8) & 0xFF;
                b2 = (newcolor2 >> 0) & 0xFF;

                int tr = (int) (2 * Math.sqrt(((r - r1) * (r - r1) + (r - r2) * (r - r2))));
                int tg = (int) (2 * Math.sqrt(((g - g1) * (g - g1) + (g - g2) * (g - g2))));
                int tb = (int) (2 * Math.sqrt(((b - b1) * (b - b1) + (b - b2) * (b - b2))));

                newPx[x + y * w] = (255 << 24) | (tr << 16) | (tg << 8) | (tb);
            }
        }
        resultBitmap.setPixels(newPx, 0, w, 0, 0, w, h);

        return resultBitmap;
    }

    /**
     * 黑白
     *
     * @param bitmap
     * @return
     */
    public static Bitmap BlackWhite(Bitmap bitmap) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        Bitmap resultBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);
        int color = 0;
        int a, r, g, b, r1, g1, b1;
        int[] oldPx = new int[w * h];
        int[] newPx = new int[w * h];

        bitmap.getPixels(oldPx, 0, w, 0, 0, w, h);
        for (int i = 0; i < w * h; i++) {
            color = oldPx[i];

            r = Color.red(color);
            g = Color.green(color);
            b = Color.blue(color);
            a = Color.alpha(color);

            //黑白矩阵
            r1 = (int) (0.33 * r + 0.59 * g + 0.11 * b);
            g1 = (int) (0.33 * r + 0.59 * g + 0.11 * b);
            b1 = (int) (0.33 * r + 0.59 * g + 0.11 * b);

            //检查各像素值是否超出范围
            if (r1 > 255) {
                r1 = 255;
            }

            if (g1 > 255) {
                g1 = 255;
            }

            if (b1 > 255) {
                b1 = 255;
            }

            newPx[i] = Color.argb(a, r1, g1, b1);
        }
        resultBitmap.setPixels(newPx, 0, w, 0, 0, w, h);
        return resultBitmap;
    }

    /**
     * 老照片
     *
     * @param bm
     * @return
     */
    public static Bitmap OldPhoto(Bitmap bm) {
        int Width = bm.getWidth();
        int Height = bm.getHeight();

        Bitmap bitmap = Bitmap.createBitmap(Width, Height, Bitmap.Config.ARGB_8888);

        int color = 0;
        int r, g, b, a, r1, g1, b1;

        int[] oldPx = new int[Width * Height];
        int[] newPx = new int[Width * Height];
        bm.getPixels(oldPx, 0, Width, 0, 0, Width, Height);

        for (int i = 0; i < Width * Height; i++) {
            color = oldPx[i];

            r = Color.red(color);
            g = Color.green(color);
            b = Color.blue(color);
            a = Color.alpha(color);

            //老照片矩阵
            r1 = (int) (0.393 * r + 0.769 * b + 0.189 * b);
            g1 = (int) (0.349 * r + 0.686 * g + 0.168 * b);
            b1 = (int) (0.272 * r + 0.534 * g + 0.131 * b);

            //检查各像素值是否超出范围
            if (r1 > 255) {
                r1 = 255;
            }

            if (g1 > 255) {
                g1 = 255;
            }

            if (b1 == 255) {
                b1 = 255;
            }
            newPx[i] = Color.argb(a, r1, g1, b1);
        }
        bitmap.setPixels(newPx, 0, Width, 0, 0, Width, Height);
        return bitmap;
    }

    /**
     * 倒影
     *
     * @param bitmap
     * @return
     */
    public static Bitmap InvertedImg(Bitmap bitmap) {
        //原图和镜面图的间距
        final int reflectionGap = 2;

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        Matrix matrix = new Matrix();
        matrix.preScale(1, -1);

        Bitmap reflectionImage = Bitmap.createBitmap(bitmap, 0, height / 2,
                width, height / 2, matrix, false);

        Bitmap bitmapWithReflection = Bitmap.createBitmap(width,
                (height + height / 2), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmapWithReflection);
        canvas.drawBitmap(bitmap, 0, 0, null);
        Paint deafalutPaint = new Paint();
        canvas.drawRect(0, height, width, height + reflectionGap, deafalutPaint);

        canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);

        Paint paint = new Paint();
        LinearGradient shader = new LinearGradient(0, bitmap.getHeight(), 0,
                bitmapWithReflection.getHeight() + reflectionGap, 0x70ffffff,
                0x00ffffff, Shader.TileMode.CLAMP);
        paint.setShader(shader);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        canvas.drawRect(0, height, width, bitmapWithReflection.getHeight()
                + reflectionGap, paint);

        return bitmapWithReflection;
    }

    /**
     * 光照
     *
     * @param bmp
     * @return
     */
    public static Bitmap SunshineImage(Bitmap bmp) {
        int width = bmp.getWidth();
        int height = bmp.getHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        int pixColor = 0;
        int pixR = 0;
        int pixG = 0;
        int pixB = 0;
        int newR = 0;
        int newG = 0;
        int newB = 0;

        int centerX = width / 2;
        int centerY = height / 2;
        int radius = Math.min(centerX, centerY);
        float strength = 150F;
        int[] pixels = new int[width * height];
        bmp.getPixels(pixels, 0, width, 0, 0, width, height);
        for (int i = 1; i < height - 1; i++) {
            for (int k = 1; k < width - 1; k++) {
                pixColor = pixels[width * i + k];
                pixR = Color.red(pixColor);
                pixG = Color.green(pixColor);
                pixB = Color.blue(pixColor);
                newR = pixR;
                newG = pixG;
                newB = pixB;
                int distance = (int) (Math.pow((centerY - i), 2) + Math.pow((centerX - k), 2));
                if (distance < radius * radius) {
                    int result = (int) (strength * (1.0 - Math.sqrt(distance) / radius));
                    newR = pixR + result;
                    newG = newG + result;
                    newB = pixB + result;
                }
                newR = Math.min(255, Math.max(0, newR));
                newG = Math.min(255, Math.max(0, newG));
                newB = Math.min(255, Math.max(0, newB));
                pixels[width * i + k] = Color.argb(255, newR, newG, newB);
            }
        }
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    /**
     * 羽化
     *
     * @param bitmap
     * @return
     */
    public static Bitmap Feather(Bitmap bitmap) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Bitmap result = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);

        int ratio = w > h ? h * 32768 / w : w * 32768 / h;
        int r, g, b, color;

        int[] oldPx = new int[w * h];
        int[] newPx = new int[w * h];

        float Size = 0.5f;
        int cx = w >> 1;
        int cy = h >> 1;
        int max = cx * cx + cy * cy;
        int min = (int) (max * (1 - Size));
        int diff = max - min;

        bitmap.getPixels(oldPx, 0, w, 0, 0, w, h);

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {

                color = oldPx[x * h + y];
                r = Color.red(color);
                g = Color.green(color);
                b = Color.blue(color);

                int dx = cx - x;
                int dy = cy - y;
                if (w > h) {
                    dx = (dx * ratio) >> 15;
                } else {
                    dy = (dy * ratio) >> 15;
                }
                int distSq = dx * dx + dy * dy;
                float v = ((float) distSq / diff) * 255;
                r = (int) (r + (v));
                g = (int) (g + (v));
                b = (int) (b + (v));
                r = (r > 255 ? 255 : (r < 0 ? 0 : r));
                g = (g > 255 ? 255 : (g < 0 ? 0 : g));
                b = (b > 255 ? 255 : (b < 0 ? 0 : b));
                newPx[x * h + y] = Color.rgb(r, g, b);
            }
        }
        result.setPixels(newPx, 0, w, 0, 0, w, h);
        return result;
    }

    /**
     * 放大镜
     *
     * @param bitmap
     * @param radius
     * @return
     */
    public static Bitmap Magnifier(Bitmap bitmap, int radius) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        Bitmap result = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);

        int[] oldPx = new int[w * h];
        int[] newPx = new int[w * h];
        int r, g, b, a, color;
        float xishu = 2;

        bitmap.getPixels(oldPx, 0, w, 1, 1, w - 1, h - 1);

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                color = oldPx[j * w + i];
                r = Color.red(color);
                g = Color.green(color);
                b = Color.blue(color);
                a = Color.alpha(color);

                int newR = r;
                int newG = g;
                int newB = b;
                int newA = a;

                int centerX = w / 2;
                int centerY = h / 2;

                int distance = (int) ((centerX - i) * (centerX - i) + (centerY - j) * (centerY - j));
                //放大镜区域内的图像放大
                if (distance < radius * radius) {
                    // 图像放大效果
                    int src_x = (int) ((float) (i - centerX) / xishu + centerX);
                    int src_y = (int) ((float) (j - centerY) / xishu + centerY);

                    color = oldPx[src_y * w + src_x];
                    newR = Color.red(color);
                    newG = Color.green(color);
                    newB = Color.blue(color);
                    newA = Color.alpha(color);
                }

                //检查像素值是否超出0~255的范围
                newR = Math.min(255, Math.max(0, newR));
                newG = Math.min(255, Math.max(0, newG));
                newB = Math.min(255, Math.max(0, newB));
                newA = Math.min(255, Math.max(0, newA));

                newPx[j * w + i] = Color.argb(newA, newR, newG, newB);
            }
        }
        result.setPixels(newPx, 0, w, 0, 0, w, h);
        return result;
    }

    /**
     * 哈哈镜(修改自放大镜)
     *
     * @param bitmap
     * @param radius
     * @return
     */
    public static Bitmap MagicMirror(Bitmap bitmap, int radius) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Bitmap result = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);

        int[] oldPx = new int[w * h];
        int[] newPx = new int[w * h];
        int r, g, b, a, color;
        float xishu = 2;
        int real_radius = (int) (radius / xishu);

        bitmap.getPixels(oldPx, 0, w, 1, 1, w - 1, h - 1);

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                color = oldPx[j * w + i];
                r = Color.red(color);
                g = Color.green(color);
                b = Color.blue(color);
                a = Color.alpha(color);

                int newR = r;
                int newG = g;
                int newB = b;
                int newA = a;

                int centerX = w / 2;
                int centerY = h / 2;

                int distance = (int) ((centerX - i) * (centerX - i) + (centerY - j) * (centerY - j));
                //对半径范围内的图像进行处理，其余部分的图像不做处理
                if (distance < radius * radius) {
                    // 放大镜的凹凸效果
                    int src_x = (int) ((float) (i - centerX) / xishu);
                    int src_y = (int) ((float) (j - centerY) / xishu);
                    src_x = (int) (src_x * (Math.sqrt(distance) / real_radius));
                    src_y = (int) (src_y * (Math.sqrt(distance) / real_radius));
                    src_x = src_x + centerX;
                    src_y = src_y + centerY;

                    color = oldPx[src_y * w + src_x];
                    newR = Color.red(color);
                    newG = Color.green(color);
                    newB = Color.blue(color);
                    newA = Color.alpha(color);
                }
                //检查像素值是否超出0~255的范围
                newR = Math.min(255, Math.max(0, newR));
                newG = Math.min(255, Math.max(0, newG));
                newB = Math.min(255, Math.max(0, newB));
                newA = Math.min(255, Math.max(0, newA));

                newPx[j * w + i] = Color.argb(newA, newR, newG, newB);
            }
        }
        result.setPixels(newPx, 0, w, 0, 0, w, h);
        return result;
    }

    /**
     * 马赛克
     *
     * @param bitmap
     * @return
     */
    public static Bitmap Masic(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        int[] inPixels = new int[width * height];
        int[] outPixels = new int[width * height];

        bitmap.getPixels(inPixels, 0, width, 0, 0, width, height);
        int index = 0;

        int offsetX = 0, offsetY = 0;
        int newX = 0, newY = 0;
        int size = 10;
        double total = size * size;
        double sumred = 0, sumgreen = 0, sumblue = 0;
        for (int row = 0; row < height; row++) {
            int ta = 0, tr = 0, tg = 0, tb = 0;
            for (int col = 0; col < width; col++) {
                newY = (row / size) * size;
                newX = (col / size) * size;
                offsetX = newX + size;
                offsetY = newY + size;
                for (int subRow = newY; subRow < offsetY; subRow++) {
                    for (int subCol = newX; subCol < offsetX; subCol++) {
                        if (subRow < 0 || subRow >= height) {
                            continue;
                        }
                        if (subCol < 0 || subCol >= width) {
                            continue;
                        }
                        index = subRow * width + subCol;
                        ta = (inPixels[index] >> 24) & 0xff;
                        sumred += (inPixels[index] >> 16) & 0xff;
                        sumgreen += (inPixels[index] >> 8) & 0xff;
                        sumblue += inPixels[index] & 0xff;
                    }
                }
                index = row * width + col;
                tr = (int) (sumred / total);
                tg = (int) (sumgreen / total);
                tb = (int) (sumblue / total);
                outPixels[index] = (ta << 24) | (tr << 16) | (tg << 8) | tb;

                sumred = sumgreen = sumblue = 0; // reset them...
            }
        }
        result.setPixels(outPixels, 0, width, 0, 0, width, height);
        return result;
    }

    /**
     * 漫画
     *
     * @param bitmap
     * @return
     */
    public static Bitmap Cartoon(Bitmap bitmap) {
        int mBitmapWidth = 0;
        int mBitmapHeight = 0;

        mBitmapWidth = bitmap.getWidth();
        mBitmapHeight = bitmap.getHeight();

        Bitmap bmpReturn = Bitmap.createBitmap(mBitmapWidth, mBitmapHeight,
                Bitmap.Config.ARGB_8888);
        int iPixel = 0;
        for (int i = 0; i < mBitmapWidth; i++) {
            for (int j = 0; j < mBitmapHeight; j++) {
                int curr_color = bitmap.getPixel(i, j);

                int avg = (Color.red(curr_color) + Color.green(curr_color) + Color.blue(curr_color)) / 3;
                if (avg >= 100) {
                    iPixel = 255;
                } else {
                    iPixel = 0;
                }
                int modif_color = Color.argb(255, iPixel, iPixel, iPixel);

                bmpReturn.setPixel(i, j, modif_color);
            }
        }
        return bmpReturn;
    }

    /**
     * 扭曲
     *
     * @param bitmap
     * @return
     */
    public static Bitmap RadialDistortion(Bitmap bitmap) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Bitmap result = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);

        float Radius = 0.5f;
        float Distortion = 1.5f;
        PointRD Center = new PointRD(0.5f, 0.5f);

        int r, g, b, color;

        int[] oldPx = new int[w * h];
        int[] newPx = new int[w * h];

        bitmap.getPixels(oldPx, 0, w, 0, 0, w, h);
        int realxpos = (int) (w * Center.X);
        int realypos = (int) (h * Center.Y);
        float realradius = Math.min(w, h) * Radius;
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                float pos = 1f - ((float) Math.sqrt((double) (((x - realxpos) * (x - realxpos)) + (y - realypos) * (y - realypos))) / realradius);
                if (pos > 0f) {
                    pos = 1f - (Distortion * pos * pos);
                    float pos1 = (x - realxpos) * pos + realxpos;
                    float pos2 = (y - realypos) * pos + realypos;
                    int x1 = (int) pos1;
                    float pos3 = pos1 - x1;
                    int x2 = (pos3 > 0f) ? (x1 + 1) : x1;
                    int y1 = (int) pos2;
                    float pos4 = pos2 - y1;
                    int y2 = (pos4 > 0f) ? (y1 + 1) : y1;
                    if (x1 < 0) {
                        x1 = 0;
                    } else if (x1 >= w) {
                        x1 = w - 1;
                    }
                    if (x2 < 0) {
                        x2 = 0;
                    } else if (x2 >= w) {
                        x2 = w - 1;
                    }
                    if (y1 < 0) {
                        y1 = 0;
                    } else if (y1 >= h) {
                        y1 = h - 1;
                    }
                    if (y2 < 0) {
                        y2 = 0;
                    } else if (y2 >= h) {
                        y2 = h - 1;
                    }

                    color = oldPx[x1 * h + y1];
                    r = Color.red(color);
                    g = Color.green(color);
                    b = Color.blue(color);

                    color = oldPx[x2 * h + y1];
                    int r2 = Color.red(color);
                    int g2 = Color.green(color);
                    int b2 = Color.blue(color);

                    color = oldPx[x2 * h + y2];
                    int r3 = Color.red(color);
                    int g3 = Color.green(color);
                    int b3 = Color.blue(color);

                    color = oldPx[x1 * h + y2];
                    int r4 = Color.red(color);
                    int g4 = Color.green(color);
                    int b4 = Color.blue(color);

                    r = (int) ((r * (1f - pos4) * (1f - pos3) + r2 * (1f - pos4) * pos3 + r3 * pos4 * pos3) + r4 * pos4 * (1f - pos3));
                    g = (int) ((g * (1f - pos4) * (1f - pos3) + g2 * (1f - pos4) * pos3 + g3 * pos4 * pos3) + g4 * pos4 * (1f - pos3));
                    b = (int) ((b * (1f - pos4) * (1f - pos3) + b2 * (1f - pos4) * pos3 + b3 * pos4 * pos3) + b4 * pos4 * (1f - pos3));
                } else {
                    color = oldPx[x * h + y];
                    r = Color.red(color);
                    g = Color.green(color);
                    b = Color.blue(color);
                }
                newPx[x * h + y] = Color.rgb(r, g, b);
            }
        }
        result.setPixels(newPx, 0, w, 0, 0, w, h);
        return result;
    }

    /**
     * 图片变亮
     *
     * @param bitmap
     * @return
     */
    public static Bitmap Bright(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        int[] inPixels = new int[width * height];
        int[] outPixels = new int[width * height];
        bitmap.getPixels(inPixels, 0, width, 0, 0, width, height);
        // calculate RED, GREEN, BLUE means of pixel
        int index = 0;
        int[] rgbmeans = new int[3];
        double redSum = 0, greenSum = 0, blueSum = 0;
        double total = height * width;
        for (int row = 0; row < height; row++) {
            int tr = 0, tg = 0, tb = 0;
            for (int col = 0; col < width; col++) {
                index = row * width + col;
                tr = (inPixels[index] >> 16) & 0xff;
                tg = (inPixels[index] >> 8) & 0xff;
                tb = inPixels[index] & 0xff;
                redSum += tr;
                greenSum += tg;
                blueSum += tb;
            }
        }

        // get means
        rgbmeans[0] = (int) (redSum / total);
        rgbmeans[1] = (int) (greenSum / total);
        rgbmeans[2] = (int) (blueSum / total);

        // adjust brightness algorithm, here
        for (int row = 0; row < height; row++) {
            int ta = 0, tr = 0, tg = 0, tb = 0;
            for (int col = 0; col < width; col++) {
                index = row * width + col;
                ta = (inPixels[index] >> 24) & 0xff;
                tr = (inPixels[index] >> 16) & 0xff;
                tg = (inPixels[index] >> 8) & 0xff;
                tb = inPixels[index] & 0xff;

                // remove means
                tr -= rgbmeans[0];
                tg -= rgbmeans[1];
                tb -= rgbmeans[2];

                // adjust brightness
                tr += (int) (rgbmeans[0] * 1.2f);
                tg += (int) (rgbmeans[1] * 1.2f);
                tb += (int) (rgbmeans[2] * 1.2f);
                outPixels[index] = (ta << 24) | (Function.clamp(tr) << 16)
                        | (Function.clamp(tg) << 8) | Function.clamp(tb);
            }
        }
        result.setPixels(outPixels, 0, width, 0, 0, width, height);
        return result;
    }

    /**
     * 油画
     *
     * @param bitmap
     * @return
     */
    public static Bitmap OilPaint(Bitmap bitmap) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Bitmap result = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);

        int[] oldPx = new int[w * h];
        int[] newPx = new int[w * h];

        bitmap.getPixels(oldPx, 0, w, 0, 0, w, h);
        int index = 0;
        int subradius = 4;
        int intensity = 20;
        int[] intensityCount = new int[intensity + 1];
        int[] ravg = new int[intensity + 1];
        int[] gavg = new int[intensity + 1];
        int[] bavg = new int[intensity + 1];
        //初始化
        for (int i = 0; i <= intensity; i++) {
            intensityCount[i] = 0;
            ravg[i] = 0;
            gavg[i] = 0;
            bavg[i] = 0;
        }

        for (int row = 0; row < h; row++) {
            int ta = 0, tr = 0, tg = 0, tb = 0;
            for (int col = 0; col < w; col++) {
                for (int subRow = -subradius; subRow <= subradius; subRow++) {
                    for (int subCol = -subradius; subCol <= subradius; subCol++) {
                        int nrow = row + subRow;
                        int ncol = col + subCol;
                        if (nrow >= h || nrow < 0) {
                            nrow = 0;
                        }
                        if (ncol >= w || ncol < 0) {
                            ncol = 0;
                        }

                        index = nrow * w + ncol;
                        tr = (oldPx[index] >> 16) & 0xff;
                        tg = (oldPx[index] >> 8) & 0xff;
                        tb = oldPx[index] & 0xff;

                        int curIntensity = (int) (((double) ((tr + tg + tb) / 3) * intensity) / 255.0f);
                        intensityCount[curIntensity]++;
                        ravg[curIntensity] += tr;
                        gavg[curIntensity] += tg;
                        bavg[curIntensity] += tb;
                    }
                }

                int maxCount = 0, maxIndex = 0;
                for (int m = 0; m < intensityCount.length; m++) {
                    if (intensityCount[m] > maxCount) {
                        maxCount = intensityCount[m];
                        maxIndex = m;
                    }
                }

                //计算平均值
                int nr = ravg[maxIndex] / maxCount;
                int ng = gavg[maxIndex] / maxCount;
                int nb = bavg[maxIndex] / maxCount;
                index = row * w + col;
                newPx[index] = (ta << 24) | (nr << 16) | (ng << 8) | nb;

                //重新初始化
                for (int i = 0; i <= intensity; i++) {
                    intensityCount[i] = 0;
                    ravg[i] = 0;
                    gavg[i] = 0;
                    bavg[i] = 0;
                }
            }
        }
        result.setPixels(newPx, 0, w, 0, 0, w, h);
        return result;
    }

    /**
     * FastBuklr算法
     *
     * @param bitmap 原图
     * @param radius 半径
     * @return 模糊的图片
     */
    public static Bitmap FastBuklr(Bitmap bitmap, int radius) {

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int[] pix = new int[w * h];

        Bitmap result = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);

        bitmap.getPixels(pix, 0, w, 0, 0, w, h);

        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;

        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int vmin[] = new int[Math.max(w, h)];

        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int dv[] = new int[256 * divsum];
        for (i = 0; i < 256 * divsum; i++) {
            dv[i] = (i / divsum);
        }

        yw = yi = 0;

        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;

        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;

            for (x = 0; x < w; x++) {

                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vmin[x]];

                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;

                sir = stack[i + radius];

                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];

                rbs = r1 - Math.abs(i);

                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;

                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }

                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16) | (dv[gsum] << 8) | dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi += w;
            }
        }
        result.setPixels(pix, 0, w, 0, 0, w, h);
        return result;
    }
}
