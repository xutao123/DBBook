package com.xt.dbbook.tools;

import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by xt on 2018/01/29.
 */

public class FileUtil {
    //JSON文件存储目录
    public static final String JSON_FILE_DIR = "/json/";
    //保存搜索记录json文件名
    public static final String SEARCH_HISTROY_JSON = "search_histroy.json";
    //保存用户登录信息json文件名
    public static final String USER_INFO_JSON = "user_info.json";

    public static boolean isExistFile(String filePath) {
        return !TextUtils.isEmpty(filePath) && isExistFile(new File(filePath));
    }

    public static boolean isExistFile(File srcFile) {
        return srcFile.exists();
    }

    public static void mkDir(String path) {
        File srcDir = new File(path);
        if (srcDir.exists()) {
            return;
        }
        srcDir.mkdir();
    }

    public static boolean mkDirs(String strFolder) {
        if (TextUtils.isEmpty(strFolder)) {
            return false;
        }
        File file = new File(strFolder);
        return file.exists() || file.mkdirs();
    }

    public static File createFile(String dirPath, String fileName) {
        File file = null;
        try {
            file = new File(dirPath, fileName);
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    public static void saveJsonToFile(String jsonStr, String fileDir, String fileName) {
        if (!mkDirs(fileDir)) {
            return;
        }
        FileUtil.deleteFile(fileDir, fileName);
        try {
            FileUtil.saveStr2File(fileDir,fileName, jsonStr);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void deleteFile(String dirPath, String fileName) {
        if (dirPath == null || fileName == null) {
            return;
        }
        File file = new File(dirPath, fileName);
        deleteFile(file);
    }

    public static void deleteFile(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return;
        }
        File file = new File(filePath);
        deleteFile(file);
    }

    public static void deleteFile(File file) {
        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
            } else {
                if (file.isDirectory()) {
                    File[] childFile = file.listFiles();
                    if ((childFile != null) && (childFile.length != 0)) {
                        for (File f : childFile) {
                            deleteFile(f);
                        }
                    }
                    file.delete();
                }
            }
        }
    }

    public static void saveStr2File(String fileDir,String filePath, String resString)
            throws IOException {
        if (TextUtils.isEmpty(resString))
            return;

        File resfile = new File(fileDir,filePath);
        if (resfile.exists()) {
            resfile.delete();
        }
        // 判断文件的目录是否存在
        String parent = resfile.getParent();
        if (!isExistFile(parent)) {
            if (!mkDirs(parent)) {
                return;
            }
        }
        resfile.createNewFile();
        try {
            FileOutputStream fis = new FileOutputStream(resfile);
            byte[] buf = resString.getBytes();
            fis.write(buf, 0, buf.length);
            fis.flush();
            fis.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String readFile2String(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return null;
        }
        String fileContent = null;
        File file = new File(filePath);
        FileInputStream inStream = null;
        if (file.exists()) {
            try {
                inStream = new FileInputStream(file);
                fileContent = streamToString(inStream);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                if (inStream != null) {
                    try {
                        inStream.close();
                        inStream = null;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return fileContent;
        } else {
            return null;
        }
    }

    public static String streamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

}
