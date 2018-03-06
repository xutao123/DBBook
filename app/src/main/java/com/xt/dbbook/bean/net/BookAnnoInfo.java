package com.xt.dbbook.bean.net;

import java.util.List;

/**
 * Created by xt on 2018/01/28.
 */

public class BookAnnoInfo extends BaseInfo {
    private int count;
    private int start;
    private int total;

    private List<AnnoData> annotations;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<AnnoData> getAnnotations() {
        return annotations;
    }

    public void setAnnotations(List<AnnoData> annotations) {
        this.annotations = annotations;
    }

    public class AnnoData {
        private String chapter;
        private AuthorUser author_user;
        private String summary;
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

        public void setChapter(String chapter) {
            this.chapter = chapter;
        }

        public AuthorUser getAuthor_user() {
            return author_user;
        }

        public void setAuthor_user(AuthorUser author_user) {
            this.author_user = author_user;
        }

        public String getSummary() {
            return summary;
        }

        public void setSummary(String summary) {
            this.summary = summary;
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


    /*
    {"count":20,
    "start":0,
    "total":116,
    "annotations":[
            {"chapter":"",
            "book":{...}
                "author_user":{
                    "name":"默默的",
                    "is_banned":false,
                    "is_suicide":false,
                    "url":"https:\/\/api.douban.com\/v2\/user\/43252893",
                    "avatar":"https://img1.doubanio.com\/icon\/u43252893-8.jpg",
                    "uid":"43252893",
                    "alt":"https:\/\/www.douban.com\/people\/43252893\/",
                    "type":"user",
                    "id":"43252893",
                    "large_avatar":"https://img1.doubanio.com\/icon\/up43252893-8.jpg"},
                "privacy":2,
                "abstract_photo":"",
                "abstract":"从春秋五霸升级衍化到战国七雄，可以看出春秋的时候，中原的主要矛盾是南北矛盾，体现在晋楚两国的争霸当中，晋在北边，楚在南边，一直是南北对峙。到了战国的时候，主要矛盾就是东西矛盾了，具体表现就是秦国跟...",
                "summary":"从春秋五霸升级衍化到战国七雄，可以看出春秋的时候，中原的主要矛盾是南北矛盾，体现在晋楚两国的争霸当中，晋在北边，楚在南边，一直是南北对峙。到了战国的时候，主要矛盾就是东西矛盾了，具体表现就是秦国跟...","
                content":"从春秋五霸升级衍化到战国七雄，可以看出春秋的时候，中原的主要矛盾是南北矛盾，体现在晋楚两国的争霸当中，晋在北边，楚在南边，一直是南北对峙。到了战国的时候，主要矛盾就是东西矛盾了，具体表现就是秦国跟关东六国的矛盾。因为关东六国位居崤山函谷关以东，对秦国形成一定威胁。尤其到了战国末期，秦朝想统一六国，进一步激化了他们之间的矛盾。",
                "photos":{},
                "last_photo":0,
                "comments_count":0,
                "hasmath":false,
                "book_id":"3901238",
                "time":"2012-10-18 09:00:38",
                "author_id":"43252893",
                "id":"21816786",
                "page_no":1}
                }
                ]}
     */


//    获取某本图书的所有笔记
//    GET  https://api.douban.com/v2/book/:id/annotations
//    参数	意义	备注
//    format	返回content字段格式	选填（编辑伪标签格式：text, HTML格式：html），默认为text
//    order	排序	选填（最新笔记：collect, 按有用程度：rank, 按页码先后：page），默认为rank
//    page	按页码过滤	选填
//            此数据仅显示公开笔记
//
//    返回: status = 200,
//
//    {
//        "start": 0,
//            "count": 20,
//            "total": 23,
//            "annotations" : [Annotation, ]
//    }
//    获取某篇笔记的信息
//    GET  https://api.douban.com/v2/book/annotation/:id
//    参数	意义	备注
//    format	返回content字段格式	选填（编辑伪标签格式：text, HTML格式：html），默认为text
//    返回: status = 200, 笔记Annotation信息
//
//            获取丛书书目信息
//    GET  https://api.douban.com/v2/book/series/:id/books
//    返回: status = 200, 图书信息
}
