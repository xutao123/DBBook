package com.xt.dbbook.network;

/**
 * Created by xt on 2018/01/21.
 */

public class URLUtil {
    //GET  https://api.douban.com/v2/book/:id/annotations
    //GET "https://api.douban.com/v2/book/:id

    //GET String url = "https://api.douban.com/v2/book/search?tag=畅销
    // &fields=author,image,id,publisher,title,pubdate,rating"

    private static final String ROOTURL = "https://api.douban.com/v2";
    private static final String BOOK_ROOTURL = "https://api.douban.com/v2/book";
    private static final String USER_ROOTURL = "https://api.douban.com/v2/user";

    private static StringBuilder m_builder = new StringBuilder(ROOTURL);

    public static URLUtil getInstance() {
        return new URLUtil().resetURL();
    }

    public URLUtil addPathNode(String str) {
        if (m_builder != null)
            m_builder.append("/").append(str);
        return this;
    }

    public URLUtil addFirstStringParams(String name, String param) {
        if (m_builder != null)
            m_builder.append("?").append(name).append("=").append(param);
        return this;
    }

    public URLUtil addStringParams(String name, String param) {
        if (m_builder != null)
            m_builder.append("&").append(name).append("=").append(param);
        return this;
    }

    public URLUtil addFirstIntParams(String name, int param) {
        if (m_builder != null)
            m_builder.append("?").append(name).append("=").append(param);
        return this;
    }

    public URLUtil addIntParams(String name, int param) {
        if (m_builder != null)
            m_builder.append("&").append(name).append("=").append(param);
        return this;
    }

    public URLUtil addFirstLongParams(String name, long param) {
        if (m_builder != null)
            m_builder.append("?").append(name).append("=").append(param);
        return this;
    }

    public URLUtil addLongParams(String name, long param) {
        if (m_builder != null)
            m_builder.append("&").append(name).append("=").append(param);
        return this;
    }

    public URLUtil addFields(String... fields) {
        if (m_builder != null)
            m_builder.append("&fields=");
        for (String str : fields) {
            m_builder.append(str).append(",");
        }
        m_builder.deleteCharAt(m_builder.length() - 1);
        return this;
    }

    public URLUtil addFieldsWithoutParam(String... fields) {
        if (m_builder != null)
            m_builder.append("?fields=");
        for (String str : fields) {
            m_builder.append(str).append(",");
        }
        m_builder.deleteCharAt(m_builder.length() - 1);
        return this;
    }

    public String getUrl() {
        if (m_builder != null)
            return m_builder.toString();
        return "";
    }

    public URLUtil resetURL() {
        m_builder = new StringBuilder(ROOTURL);
        return this;
    }

}
