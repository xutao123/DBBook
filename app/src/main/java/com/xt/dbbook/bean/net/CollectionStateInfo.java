package com.xt.dbbook.bean.net;

/**
 * Created by xt on 2018/02/09.
 */

public class CollectionStateInfo {

    private int code;
    private String msg;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
    /*
{
  "code": 1001,
  "msg": "uri_not_found",
  "request": "GET /v2/book/<请求图书的id>/collection"
}
     */
}
