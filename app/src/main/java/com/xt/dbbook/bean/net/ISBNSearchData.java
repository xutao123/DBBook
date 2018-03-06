package com.xt.dbbook.bean.net;

import java.util.List;

/**
 * Created by xt on 2018/02/01.
 */

public class ISBNSearchData {

    private List<String> author;
    private String image;
    private String id;
    private String publisher;
    private String title;
    private String pubdate;
    private BookPartInfo.BookInfoData.Rate rating;

    public BookPartInfo.BookInfoData.Rate getRating() {
        return rating;
    }

    public void setRating(BookPartInfo.BookInfoData.Rate rating) {
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

    /*
     *{"rating":
     *  {"max":10,"numRaters":58,"average":"6.9","min":0},
     *"pubdate":"2011-6",
     * "publisher":"机械工业出版社",
     * "image":"https://img1.doubanio.com\/mpic\/s6379378.jpg",
     * "author":["杨丰盛"],
     * "title":"Android技术内幕",
     * "id":"6047744"}
     */
}
