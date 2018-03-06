package com.xt.dbbook.Config;

import android.os.Environment;
import android.text.TextUtils;

import com.xt.dbbook.tools.FileUtil;

import java.io.File;

/**
 * Created by xt on 2018/01/29.
 */

public class EnvInfo {
    private static String sdcardPath = "";
    private static String sdcardState;
    private static String appPath = "";

    public static void checkEnvInfo() {
        sdcardPath = Environment
                .getExternalStorageDirectory().getAbsolutePath();
        sdcardState = Environment.getExternalStorageState();
        sdcardPath = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM).toString() + "/xt";
        FileUtil.mkDirs(sdcardPath);
    }

    public static String getAppJsonDirPath() {
        return sdcardPath + FileUtil.JSON_FILE_DIR;
    }

}
