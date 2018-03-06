package com.xt.dbbook.tools;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by xt on 2018/01/20.
 */

public class ThreadPoolUtil {
    private ExecutorService mExecutorService;
    private volatile static ThreadPoolUtil mThreadPoolUtil;

    private ThreadPoolUtil() {
        mExecutorService = Executors.newCachedThreadPool();
    }

    public static ThreadPoolUtil getInstance() {
        if (mThreadPoolUtil == null)
            synchronized (ThreadPoolUtil.class) {
                if (mThreadPoolUtil == null)
                    mThreadPoolUtil = new ThreadPoolUtil();
            }
        return mThreadPoolUtil;
    }

    public void execute(Runnable runnable) {
        mExecutorService.execute(runnable);
    }
}
