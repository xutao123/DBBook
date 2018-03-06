package com.xt.dbbook.Config;

/**
 * Created by xt on 2018/01/28.
 */

public class IntentExtraConfig {
    public static final String BOOK_ID = "book_id";
    public static final String BOOK_ISBN = "book_isbn";
    public static final String BOOK_NAME = "book_name";

    //需要ShowAutorActivity显示何种类型的详细信息
    public static final String DETAIL_TYPE = "detail_type";
    public static final String SUMMARY_TYPE = "summary_type";
    public static final String AUTHOR_TYPE = "author_type";
    public static final String CATALOG_TYPE = "catalog_type";
    public static final String GET_DATA_KEY = "get_detail_data";

    //进入SearchActivity时是否携带搜索数据标记
    public static final String COME_INTO_SEARCH_ACTIVITY_STATE = "with_or_without_data";
    public static final String COMMIT_DATA_TO_SEARCH_ACTIVITY = "get_search_data";
    public static final int COMEIN_WITHOUT_DATA = 0;
    public static final int COMEIN_WITH_DATA = COMEIN_WITHOUT_DATA + 1;

    //获得笔记 Annotation Id
    public static final String COMMIT_ID_TO_DETAILANNOACTIVITY = "get_anno_id";

    //获得全部笔记需要的bookId AllAnnoActivity
    public static final String COMMIT_ID_TO_ALLANNOACTIVITY = "get_book_id";
}
