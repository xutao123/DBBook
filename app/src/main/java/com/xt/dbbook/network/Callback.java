package com.xt.dbbook.network;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by xt on 2018/01/20.
 */

public interface Callback<T> {
    T onParseResponseBody(Response response) throws IOException;

    void onResponse(T t);

    void onFailure(Call call, IOException e);

    void onNetWorkUnavaliable();
}
