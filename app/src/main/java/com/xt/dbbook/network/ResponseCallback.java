package com.xt.dbbook.network;

import com.xt.dbbook.tools.GsonUtil;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.net.URLDecoder;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by xt on 2018/01/20.
 */

public abstract class ResponseCallback<T> implements Callback<T> {
    @Override
    public T onParseResponseBody(Response response) throws IOException {
        String responseStr = response.body().string();
        //Invalid % sequence
        responseStr = responseStr.replaceAll("%(?![0-9a-fA-F]{2})", "%25");
        responseStr = responseStr.replaceAll("\\+", "%2B");
        String urlDecodeStr = URLDecoder.decode(responseStr, "UTF-8");
        String urlString = responseStr == null ? "" : urlDecodeStr;
        //获得超类的泛型参数的实际类型
        Class<T> entityClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass())
                .getActualTypeArguments()[0];
        if (entityClass == String.class) {
            return (T) responseStr;
        }

        return GsonUtil.gsonBuild().fromJson(urlString, entityClass);
    }

    @Override
    public abstract void onResponse(T t);

    @Override
    public abstract void onFailure(Call call, IOException e);

    @Override
    public abstract void onNetWorkUnavaliable();
}
