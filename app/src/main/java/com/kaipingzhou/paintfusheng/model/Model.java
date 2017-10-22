package com.kaipingzhou.paintfusheng.model;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 创建人：周开平
 * 创建时间：2017/4/17 20:34
 * 作用：数据模型层
 */

public class Model {

    private ExecutorService executorService = Executors.newCachedThreadPool();

    private static Model model = new Model();

    private Model() {

    }

    public static Model getInstance() {
        return model;
    }

    public ExecutorService getGloblThreadPool() {
        return executorService;
    }
}
