package com.kaipingzhou.paintfusheng.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.MessageDigest;

/**
 * 创建人：周开平
 * 创建时间：2017/4/17 20:41
 * 作用：缓存数据
 */

public class CacheUtils {

    /**
     * md5加密算法
     *
     * @param string
     * @return
     * @throws Exception
     */
    public static String encode(String string) throws Exception {
        byte[] hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10) {
                hex.append("0");
            }
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }

    /**
     * 得到缓存值
     *
     * @param context
     * @param key
     * @return
     */
    public static boolean getBoolean(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences("HQUZkP", Context.MODE_PRIVATE);
        return sp.getBoolean(key, false);
    }

    /**
     * @param context
     * @param key
     * @param value
     */
    public static void putBoolean(Context context, String key, boolean value) {
        SharedPreferences sp = context.getSharedPreferences("HQUZkP", Context.MODE_PRIVATE);
        sp.edit().putBoolean(key, value).commit();
    }

    /**
     * 缓存文本数据
     *
     * @param context
     * @param key
     * @param value
     */
    public static void putString(Context context, String key, String value) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            try {
                String fileName = encode(key);

                File file = new File(Constants.APP_CACHE, fileName);

                File parentFile = file.getParentFile();
                if (!parentFile.exists()) {
                    //创建目录
                    parentFile.mkdirs();
                }

                if (!file.exists()) {
                    file.createNewFile();
                }
                //保存文本数据
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                fileOutputStream.write(value.getBytes());
                fileOutputStream.close();

            } catch (Exception e) {
                e.printStackTrace();
                LogUtils.e("文本数据缓存失败");
            }
        } else {
            SharedPreferences sp = context.getSharedPreferences("HQUZkP", Context.MODE_PRIVATE);
            sp.edit().putString(key, value).commit();
        }
    }

    /**
     * 获取缓存的文本信息
     *
     * @param context
     * @param key
     * @return
     */
    public static String getString(Context context, String key, int defVal) {
        String result = "";
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            try {
                String fileName = encode(key);

                File file = new File(Constants.APP_CACHE, fileName);

                if (file.exists()) {

                    FileInputStream is = new FileInputStream(file);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();

                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = is.read(buffer)) != -1) {
                        stream.write(buffer, 0, length);
                    }

                    is.close();
                    stream.close();

                    result = stream.toString();
                } else {
                    result = defVal + "";
                }

            } catch (Exception e) {
                e.printStackTrace();
                LogUtils.e("图片获取失败");
            }
        } else {
            SharedPreferences sp = context.getSharedPreferences("HQUZkP", Context.MODE_PRIVATE);
            result = sp.getString(key, "");
        }
        return result;
    }

    public static void clearSp(Context context, String[] keys) {
        //开始清除SharedPreferences中保存的内容
        for (String key : keys) {
            String fileName = null;
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                try {
                    fileName = encode(key);
                    File file = new File(Constants.APP_CACHE, fileName);

                    if (file.exists()) {
                        file.delete();
                        LogUtils.e(key + "删除成功");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
