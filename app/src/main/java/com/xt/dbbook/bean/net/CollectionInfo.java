package com.xt.dbbook.bean.net;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xt on 2018/02/06.
 */

public class CollectionInfo extends BaseInfo{

    private long start;
    private long count;
    private long total;
    private List<BookCollections> collections;

    public CollectionInfo() {
        start = 0;
        count = 0;
        total = 0;
        collections = new ArrayList<>();
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<BookCollections> getCollections() {
        return collections;
    }

    public void setCollections(List<BookCollections> collections) {
        this.collections = collections;
    }

    public class BookCollections {
        private BookInfoData book;
        private String updated;
        private String user_id;//用户id

        public BookInfoData getBook() {
            return book;
        }

        public void setBook(BookInfoData book) {
            this.book = book;
        }

        public String getUpdated() {
            return updated;
        }

        public void setUpdated(String updated) {
            this.updated = updated;
        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public class BookInfoData {
            private List<String> author;
            private String image;
            private String id;
            private String publisher;
            private String title;
            private String pubdate;
            private Rate rating;

            public Rate getRating() {
                return rating;
            }

            public void setRating(Rate rating) {
                this.rating = rating;
            }

            public void setAuthor(List<String> author) {
                this.author = author;
            }

            public void setImage(String image) {
                this.image = image;
            }

            public void setId(String id) {
                this.id = id;
            }

            public void setPublisher(String publisher) {
                this.publisher = publisher;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public void setPubdate(String pubdate) {
                this.pubdate = pubdate;
            }

            public List<String> getAuthor() {
                return author;
            }

            public String getImage() {
                return image;
            }

            public String getId() {
                return id;
            }

            public String getPublisher() {
                return publisher;
            }

            public String getTitle() {
                return title;
            }

            public String getPubdate() {
                return pubdate;
            }

            public String getAuthorPublisher() {
                return author + " / " + pubdate + " / " + publisher;
            }

            public class Rate {
                private String average;

                public void setAverage(String average) {
                    this.average = average;
                }

                public String getAverage() {
                    return average;
                }
            }
        }
    }

/*
    "start": 0,
    "count": 20,
    "total": 23,
    "collections" : [
        {
          "book":  {    "id":"7056972",
                        "isbn10":"7505715666",
                        "isbn13":"9787505715660",
                        "title":"小王子",
                        "origin_title":"",
                        "alt_title":"",
                        "subtitle":"",
                        "url":"https:\/\/api.douban.com\/v2\/book\/1003078",
                        "alt":"https:\/\/book.douban.com\/subject\/1003078\/",
                        "image":"https://img3.doubanio.com\/mpic\/s1001902.jpg",
                        "images":{
                            "small":"https://img3.doubanio.com\/spic\/s1001902.jpg",
                            "large":"https://img3.doubanio.com\/lpic\/s1001902.jpg",
                            "medium":"https://img3.doubanio.com\/mpic\/s1001902.jpg"
                            },...,}
          "book_id": "7056972",
          "comment": "各种成长的喜悦与痛苦，讲故事的功力比起过往短篇经典有过之而无不及，依旧是巅峰之作！",
          "id": 593151296,
          "rating": {
                    "max": 5,
                    "min": 0,
                    "value": "5"
        },
        "status": "read",
        "tags": [
                 "吴淼",
                 "奇幻",
                 "中国",
                 "塔希里亚"
                  ],
        "updated": "2012-10-19 15:29:41",
        "user_id": "33388491"
        },
        ...
     ]
*/
}
