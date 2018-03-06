package com.xt.dbbook.app;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.xt.dbbook.Config.EnvInfo;
import com.xt.dbbook.bean.file.HistroySearchData;
import com.xt.dbbook.bean.net.UserInfo;
import com.xt.dbbook.tools.DialogManager;
import com.xt.dbbook.tools.FileUtil;
import com.xt.dbbook.tools.GsonUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Stack;

/**
 * Created by xt on 2018/01/25.
 */

public class DBBookManager {
    private static Stack<Activity> activityStack;
    private volatile static DBBookManager mInstance;
    private volatile static UserInfo mUserInfo;

    private DBBookManager() {
        activityStack = new Stack<>();
    }

    public static DBBookManager getAppManager() {
        if (mInstance == null) {
            synchronized (DBBookManager.class) {
                if (mInstance == null) {
                    mInstance = new DBBookManager();
                }
            }
        }
        return mInstance;
    }

    /**
     * 登录用户信息
     */
    public static UserInfo getUserInfo() {
        return mUserInfo;
    }

    /**
     * 解析本地保存的UserInfo
     */
    public static void initUserInfo() {
        String jsonPath = EnvInfo.getAppJsonDirPath() + FileUtil.USER_INFO_JSON;

        mUserInfo = convertJsonArrayUserInfo(jsonPath);
    }

    private static UserInfo convertJsonArrayUserInfo(String path) {
        if (TextUtils.isEmpty(path))
            return null;

        String jsonStr = FileUtil.readFile2String(path);
        return GsonUtil.gsonBuild().fromJson(jsonStr, UserInfo.class);
    }

    /**
     * 保存登录用户记录
     *
     * @param userInfo
     */
    public static void saveUserInfo(UserInfo userInfo) {
        if (userInfo == null)
            return;

        try {
            JSONObject jo = new JSONObject();
            jo.put("name", userInfo.getName());
            jo.put("uid", userInfo.getUid());
            jo.put("large_avatar", userInfo.getLarge_avatar());
            jo.put("created", userInfo.getCreated());

            FileUtil.saveJsonToFile(jo.toString(), EnvInfo.getAppJsonDirPath(), FileUtil.USER_INFO_JSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 退出删除登录用户信息
     */
    public static void deleteUserInfo() {
        FileUtil.deleteFile(EnvInfo.getAppJsonDirPath(), FileUtil.USER_INFO_JSON);
    }

    /**
     * 添加Activity到堆栈
     */
    public void addActivity(Activity activity) {
        if (activityStack == null) {
            activityStack = new Stack<>();
        }
        activityStack.add(activity);
        ((DBBookApp) (DBBookApp.getAppContext())).setCurrentActivity(activity);
    }

    /**
     * 从activity stack中移除掉指定的Activity
     *
     * @param activity
     */
    public void removeActivity(Activity activity) {

        if (activity != null) {
            if (activityStack != null)
                activityStack.remove(activity);
        }
        releaseCurrentActivity();
    }

    public void releaseCurrentActivity() {
        if (DBBookApp.getAppContext() != null) {
            ((DBBookApp) (DBBookApp.getAppContext())).setCurrentActivity(null);
        }
    }

    /**
     * 结束指定的Activity
     */
    public void finishActivity(Activity activity) {
        if (activity != null) {
            activity.finish();
        }
    }

    /**
     * 结束所有Activity
     */
    public void finishAllActivity() {
        if (null == activityStack || activityStack.size() == 0) {
            return;
        }
        int size = activityStack.size();
        for (int i = 0; i < size; i++) {
            if (null != activityStack.get(i))
                finishActivity(activityStack.get(i));
        }
        activityStack.clear();
    }

    /**
     * 退出应用程序
     */
    public void exitApp(boolean isKillApp) {
        try {
            finishAllActivity();
            releaseCurrentActivity();
            // 杀死该应用进程
            if (isKillApp) {
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
