package com.xt.dbbook.bean.net;

/**
 * Created by xt on 2018/02/04.
 */

public class UserInfo {

    private String name;
    private String uid;
    private String large_avatar;
    private String created;

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getLarge_avatar() {
        return large_avatar;
    }

    public void setLarge_avatar(String large_avatar) {
        this.large_avatar = large_avatar;
    }
    //GET https://api.douban.com/v2/user/:name
        /*
        {"loc_id":"118163",
        "name":"冰河判官",
        "created":"2015-09-05 09:22:50",
        "is_banned":false,
        "is_suicide":false,
        "loc_name":"江苏苏州",
        "avatar":"https://img1.doubanio.com\/icon\/user_normal.jpg",
        "signature":"",
        "uid":"",
        "alt":"https:\/\/www.douban.com\/people\/134124130\/",
        "desc":"",
        "type":"user",
        "id":"",
        "large_avatar":"https://img3.doubanio.com\/icon\/user_large.jpg"}
         */

}
