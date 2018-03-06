package com.xt.dbbook.bean.net;

/**
 * Created by xt on 2018/02/10.
 */

public class DetailAnnoInfo {
    private String chapter;
    private BookAnnoInfo.AnnoData.AuthorUser author_user;
    private String content;
    private int page_no;
    private String time;
    private String id; //annotation id

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getChapter() {
        return chapter;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setChapter(String chapter) {
        this.chapter = chapter;
    }

    public BookAnnoInfo.AnnoData.AuthorUser getAuthor_user() {
        return author_user;
    }

    public void setAuthor_user(BookAnnoInfo.AnnoData.AuthorUser author_user) {
        this.author_user = author_user;
    }

    public int getPage_no() {
        return page_no;
    }

    public void setPage_no(int page_no) {
        this.page_no = page_no;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public class AuthorUser {
        private String name;
        private String url;
        private String avatar;
        private String uid;

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }
    }
}
