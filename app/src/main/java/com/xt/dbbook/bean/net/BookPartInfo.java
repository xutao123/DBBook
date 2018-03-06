package com.xt.dbbook.bean.net;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by xt on 2018/01/19.
 */

public class BookPartInfo {
    /**
     * "count":20
     * "total":200
     * "start":0
     * list{
     * "author":["郭敬明"]
     * "image":"https://img1.doubanio.com\/mpic\/s1513378.jpg"
     * "id":"1016300"
     * "publisher":"春风文艺出版社"
     * "title":"梦里花落知多少"
     * "pubdate":"2003-11"
     * "average":"7.1"  //评分
     * }
     */

    /**
     * 变量名称一定要与返回的值的变量名称完全一样
     * [... , ... , ...]用List
     * {... , ... , ...}用内部类
     */

    public BookPartInfo() {
        books = new ArrayList<>();
    }

    private int start;
    private int total;
    private int count;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    private List<BookInfoData> books;

    public List<BookInfoData> getBooks() {
        return books;
    }

    public void setBooks(List<BookInfoData> books) {
        this.books = books;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getStart() {
        return start;
    }

    public int getTotal() {
        return total;
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
            private int max;
            private long numRaters;
            private String average;
            private int min;

            public void setMax(int max) {
                this.max = max;
            }

            public void setNumRaters(long numRaters) {
                this.numRaters = numRaters;
            }

            public void setAverage(String average) {
                this.average = average;
            }

            public void setMin(int min) {
                this.min = min;
            }

            public int getMax() {
                return max;
            }

            public long getNumRaters() {
                return numRaters;
            }

            public int getMin() {
                return min;
            }

            public String getAverage() {
                return average;
            }
        }
    }

    // //String url = "https://api.douban.com/v2/book/search?tag=畅销&fields=author,image,id,publisher,title,pubdate,rating";
    /*
    {"count":20,
    "start":0,
    "total":200,
    "books":[
        {
        "rating":{"max":10,"numRaters":148284,"average":"7.1","min":0},
        "pubdate":"2003-11",
        "publisher":"春风文艺出版社",
        "image":"https://img1.doubanio.com\/mpic\/s1513378.jpg",
        "author":["郭敬明"],
        "title":"梦里花落知多少",
        "id":"1016300"
        },

        {"rating":{"max":10,"numRaters":8570,"average":"8.6","min":0},
        "pubdate":"2009-4",
        "publisher":"人民文学出版社",
        "image":"https://img1.doubanio.com\/mpic\/s3709449.jpg",
        "author":["[美] 丹·布朗"],
        "title":"达·芬奇密码",
        "id":"3649782"},
        ...
    */

    //所有信息
    /*
    {"count":20,
    "start":0,
    "total":200,
    "books":[{
        "rating":{"max":10,"numRaters":148282,"average":"7.1","min":0},
        "subtitle":"",
        "author":["郭敬明"],
        "pubdate":"2003-11",
        "tags":[{"count":27245,"name":"郭敬明","title":"郭敬明"},
                {"count":17299,"name":"梦里花落知多少","title":"梦里花落知多少"},
                {"count":15425,"name":"小说","title":"小说"},
                {"count":14658,"name":"青春","title":"青春"},
                {"count":6972,"name":"80后","title":"80后"},
                {"count":6564,"name":"爱情","title":"爱情"},
                {"count":4090,"name":"小四","title":"小四"},
                {"count":4087,"name":"中国","title":"中国"}],
        "origin_title":"",
        "image":"https://img1.doubanio.com\/mpic\/s1513378.jpg",
        "binding":"平装",
        "translator":[],
        "catalog":"\n      ",
        "pages":"252",
        "images":{"small":"https://img1.doubanio.com\/spic\/s1513378.jpg",
                    "large":"https://img1.doubanio.com\/lpic\/s1513378.jpg",
                    "medium":"https://img1.doubanio.com\/mpic\/s1513378.jpg"},
        "alt":"https:\/\/book.douban.com\/subject\/1016300\/",
        "id":"1016300",
        "publisher":"春风文艺出版社",
        "isbn10":"7531325098",
        "isbn13":"9787531325093",
        "title":"梦里花落知多少",
        "url":"https:\/\/api.douban.com\/v2\/book\/1016300",
        "alt_title":"",
        "author_intro":"郭敬明（1983- ），网名：第四维；别名：小四。2001年以作品《剧本》获得第三届全国新概念作文大赛一等奖，2002年以作品《我们最后的校园民谣》获得第四届全国新概念作文大赛一等奖。\n著有小说《爱与痛的边缘》《幻城》《左手倒影右手年华》《梦里花落知多少》《猜火车》《1995－2005夏至未至》， 音乐小说《迷藏》，主编《岛》系列、《无极》、《悲伤逆流成河》、《最小说》、《N世界》等。",
        "summary":"《梦里花落知多少》是郭敬明出版第二部小说，此作一改《幻城》的奇幻风格，从天上回到人间。小说以北京、上海等大都市为背景，讲述了几个年青人的爱情故事，情节曲折，语言幽默生动。主人公是一些即将走出校门的大学生，在成长的过程中，友情、爱情都在经历着蜕变，那种成长的快乐和忧伤很能引起年轻读者的共鸣。",
        "price":"20.00元"},
        ...
    */

}
