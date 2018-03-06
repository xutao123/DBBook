package com.xt.dbbook.network;

import android.app.Application;
import android.content.Context;

import com.xt.dbbook.tools.GsonUtil;
import com.xt.dbbook.tools.NetWorkUtil;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by xt on 2018/01/20.
 */

public class OkHttpUtil {
    private OkHttpClient mOkHttpClient = new OkHttpClient();
    private volatile static OkHttpUtil mInstance;
    private Call mCall;
    //key-value
    private HashMap<String, String> mParamMap;
    private ArrayList<UploadFile> mFileList;
    private Context m_context;

    private OkHttpUtil() {
    }

    private OkHttpUtil(Context context) {
        m_context = context.getApplicationContext();
    }

    public static OkHttpUtil getInstance(Context context) {
        if (mInstance == null)
            synchronized (OkHttpUtil.class) {
                if (mInstance == null)
                    mInstance = new OkHttpUtil(context);
            }
        return mInstance;
    }

    //同步访问
    public Response execute() {
        if (mCall == null) {
            return null;
        }

        try {
            return mCall.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    //异步访问
    public void execute(final Callback callback) {
        if (mCall == null || m_context == null) {
            return;
        }

        if (!NetWorkUtil.isNetworkAvailable(m_context)) {
            callback.onNetWorkUnavaliable();
            return;
        }

        mCall.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(call, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                System.out.println("currentThread:" + Thread.currentThread().getName());
                if (call.isCanceled())
                    callback.onFailure(call, new IOException("call is cancled"));
                else if (!response.isSuccessful())
                    callback.onFailure(call, new IOException("request is failed response code:" + response.code()));
                else {
                    Object obj = callback.onParseResponseBody(response);
                    callback.onResponse(obj);
                }
            }
        });
    }

    /**
     * 访问delete请求
     * @param url
     * @return
     */
    public OkHttpUtil delete(String url) {
        Request request = new Request.Builder().url(url).delete().build();
        mCall = null;
        mCall = mOkHttpClient.newCall(request);
        return this;
    }

    /**
     * 获得get request
     * @param url
     * @return
     */
    public OkHttpUtil get(String url) {
        Request request = new Request.Builder().url(url).build();
        mCall = null;
        mCall = mOkHttpClient.newCall(request);
        return this;
    }

    /**
     * 获得post request
     * @param url
     * @return
     */
    public OkHttpUtil post(String url) {
        Request request;
        if ((mParamMap != null && mParamMap.size() > 0)
                || (mFileList != null && mFileList.size() > 0)) {
            MultipartBody.Builder builder = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM);
            if (mParamMap != null && mParamMap.size() > 0) {
                Iterator iter = mParamMap.keySet().iterator();
                while (iter.hasNext()) {
                    String key = (String) iter.next();
                    builder.addFormDataPart(key, mParamMap.get(key));
                }
                mParamMap.clear();
            }
            if (mFileList != null && mFileList.size() > 0) {
                for (UploadFile uploadFile : mFileList) {
                    builder.addFormDataPart(uploadFile.tag, uploadFile.fileName,
                            RequestBody.create(MediaType.parse(getMIMEType(uploadFile.fileName)),
                                    uploadFile.loadFile));
                }
                mFileList.clear();
            }
            request = new Request.Builder().url(url).post(builder.build()).build();
        } else {
            request = new Request.Builder().url(url).build();
        }
        mCall = null;
        mCall = mOkHttpClient.newCall(request);
        return this;
    }

    public OkHttpUtil add(String key, String value) {
        if (mParamMap == null)
            mParamMap = new HashMap<>();
        mParamMap.put(key, value);
        return this;
    }

    public OkHttpUtil addFile(String tag, String filename, File file) {
        if (mFileList == null)
            mFileList = new ArrayList<>();
        mFileList.add(new UploadFile(tag, filename, file));
        return this;
    }

    /**
     * 通过文件名获得MIMEType
     *
     * @param fileName
     * @return
     */
    private String getMIMEType(String fileName) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String MIMEType = null;
        try {
            MIMEType = fileNameMap.getContentTypeFor(URLEncoder.encode(fileName, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return MIMEType;
    }

    //对OkHttpClient进行设置，缓存，超时时间


    private class UploadFile {
        public String tag;
        public String fileName;
        public File loadFile;

        public UploadFile(String tag, String fileName, File loadFile) {
            this.tag = tag;
            this.fileName = fileName;
            this.loadFile = loadFile;
        }
    }

    public static Object parseResponse(Response response, Class cla) {
        String responseStr = null;
        try {
            responseStr = response.body().string();
            String urlString = responseStr == null ? "" : URLDecoder.decode(responseStr, "UTF-8");
            return GsonUtil.gsonBuild().fromJson(urlString, cla);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
