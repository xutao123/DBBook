package com.xt.dbbook.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.xt.dbbook.Config.IntentExtraConfig;
import com.xt.dbbook.activity.BookDetailActivity;
import com.xt.dbbook.adapter.BookListRecyclerAdapter;
import com.xt.dbbook.bean.net.BookPartInfo;
import com.xt.dbbook.network.OkHttpUtil;
import com.xt.dbbook.network.ResponseCallback;
import com.xt.dbbook.network.URLUtil;
import com.xt.dbbook.tools.DialogManager;

import java.io.IOException;
import java.lang.ref.WeakReference;

import okhttp3.Call;

import static com.xt.dbbook.adapter.BookListRecyclerAdapter.CurShowType.SHOW_LOADING;
import static com.xt.dbbook.adapter.BookListRecyclerAdapter.CurShowType.SHOW_NORMAL;

/**
 * Created by xt on 2018/02/02.
 */

public class TagBaseFragment extends BaseFragment implements BookListRecyclerAdapter.OnBookListListener {

    protected BookPartInfo m_bookInfo;
    protected BookListRecyclerAdapter m_adapter;
    protected SendInfoHandler m_handler;
    protected static MaterialDialog m_dialog;
    protected static final int GET_BOOKINFO_SUCCESS = 0x100;
    protected static final int SHOW_BOOKINFO_ERROR = GET_BOOKINFO_SUCCESS + 1;
    protected static final int UPDATA_BOOKINFO_SUCCESS = GET_BOOKINFO_SUCCESS + 2;
    protected static final int UPDATA_BOOKINFO_ERROR = GET_BOOKINFO_SUCCESS + 3;
    protected static final int SHOW_LOADING_ITEM = GET_BOOKINFO_SUCCESS + 4;
    private static final int NETWORK_UNAVAILABLE = GET_BOOKINFO_SUCCESS + 5;
    protected Intent m_intent;
    protected LinearLayoutManager m_layoutManager;

    protected int m_start = 0;//此次搜索开始数目
    protected int m_total;//所有条目数目
    protected int m_count = 20;//一次返回的数目
    protected int m_returnTotal = 0;//已经返回的数目

    protected String m_tag = "";
    protected boolean m_refreshTag = false;
    protected SwipeRefreshLayout v_refreshLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    protected void initData() {
        if (m_handler == null)
            m_handler = new SendInfoHandler(this);
        if (m_dialog == null)
            m_dialog = DialogManager.createLoadingDialog(getActivity(), "Search", "请稍后...", true);
        if (!m_refreshTag)
            DialogManager.showDialog(m_dialog);

        resetParam();

        //获得tag书籍
        final String url = URLUtil.getInstance().addPathNode("book").addPathNode("search")
                .addFirstStringParams("tag", m_tag).addIntParams("start", m_start)
                .addIntParams("count", m_count)
                .addFields("start", "total", "count", "author", "image",
                        "id", "publisher", "title", "pubdate", "rating").getUrl();

        OkHttpUtil.getInstance(getActivity()).get(url).execute(new ResponseCallback<BookPartInfo>() {
            @Override
            public void onResponse(BookPartInfo info) {
                m_bookInfo = info;
                if (m_count < info.getTotal())
                    m_start = m_count;
                else m_start = info.getTotal();
                m_total = info.getTotal();
                m_returnTotal = info.getCount();
                m_handler.sendEmptyMessage(GET_BOOKINFO_SUCCESS);
            }

            @Override
            public void onFailure(Call call, IOException e) {
                m_handler.sendEmptyMessage(SHOW_BOOKINFO_ERROR);
            }

            @Override
            public void onNetWorkUnavaliable() {
                m_handler.sendEmptyMessage(NETWORK_UNAVAILABLE);
            }
        });
    }

    /**
     * 刷新时将所有数据重置
     */
    public void resetParam() {
        m_start = 0;//此次搜索开始数目
        m_total = 0;//所有条目数目
        m_count = 20;//一次返回的数目
        m_returnTotal = 0;//已经返回的数目
    }

    public void onFragmentRefresh(SwipeRefreshLayout refreshLayout) {
        m_refreshTag = true;
        v_refreshLayout = refreshLayout;
        initData();
    }

    @Override
    protected void initView(View view) {
        m_layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
    }

    public void updataView() {
        m_adapter.updataData(m_bookInfo);
    }

    public void updataCurShowType() {
        m_adapter.updataCurShowType(SHOW_LOADING);
    }

    @Override
    public void onChooseBook(int position, String bookId) {
        if (m_intent == null)
            m_intent = new Intent(getActivity(), BookDetailActivity.class);
        m_intent.putExtra(IntentExtraConfig.BOOK_ID, bookId);
        startActivity(m_intent);
    }

    @Override
    public void getNewData() {
        if (m_returnTotal >= m_total) {
            m_adapter.updataCurShowType(SHOW_NORMAL);
            Toast.makeText(getActivity(), "已经获得全部数据", Toast.LENGTH_SHORT).show();
            return;
        }

        //获得数据
        final String url = URLUtil.getInstance().addPathNode("book").addPathNode("search")
                .addFirstStringParams("tag", m_tag).addIntParams("start", m_start)
                .addIntParams("count", m_count)
                .addFields("start", "total", "count", "author", "image",
                        "id", "publisher", "title", "pubdate", "rating").getUrl();

        OkHttpUtil.getInstance(getActivity()).get(url).execute(new ResponseCallback<BookPartInfo>() {
            @Override
            public void onResponse(BookPartInfo info) {
                m_bookInfo = info;
                if (m_start + m_count < info.getTotal())
                    m_returnTotal += m_count;
                else m_returnTotal = info.getTotal();
                m_start = m_returnTotal;
                m_total = info.getTotal();
                m_handler.sendEmptyMessage(UPDATA_BOOKINFO_SUCCESS);
            }

            @Override
            public void onFailure(Call call, IOException e) {
                m_handler.sendEmptyMessage(UPDATA_BOOKINFO_ERROR);
            }

            @Override
            public void onNetWorkUnavaliable() {
                m_handler.sendEmptyMessage(NETWORK_UNAVAILABLE);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (m_adapter != null)
            m_adapter.recycler();
    }

    @Override
    public void handleFragmentMessage(Message msg, BaseFragment fragment) {
        DialogManager.dismissDialog(m_dialog);
        switch (msg.what) {
            case GET_BOOKINFO_SUCCESS:
                if (fragment != null)
                    fragment.initView(fragment.getView());

                if (m_refreshTag) {
                    m_adapter.refreshView(m_bookInfo);
                    m_refreshTag = false;
                    v_refreshLayout.setRefreshing(false);
                }
                break;
            case SHOW_BOOKINFO_ERROR:
                if (m_adapter != null)
                    m_adapter.updataCurShowType(SHOW_NORMAL);
                if (fragment != null)
                    Toast.makeText(fragment.getActivity(), "网络或者服务器异常", Toast.LENGTH_SHORT).show();
                break;
            case UPDATA_BOOKINFO_ERROR:
                if (m_refreshTag) {
                    m_refreshTag = false;
                    v_refreshLayout.setRefreshing(false);
                }
                break;
            case UPDATA_BOOKINFO_SUCCESS:
                if (fragment != null) {
                    if (fragment instanceof TagHotFragment)
                        ((TagHotFragment) fragment).updataView();
                    else if (fragment instanceof TagLetterFragment)
                        ((TagLetterFragment) fragment).updataView();
                    else ((TagLifeFragment) fragment).updataView();
                }
                if (m_refreshTag) {
                    m_refreshTag = false;
                    v_refreshLayout.setRefreshing(false);
                }
                break;
            case SHOW_LOADING_ITEM:
                if (fragment != null) {
                    if (fragment instanceof TagHotFragment)
                        ((TagHotFragment) fragment).updataCurShowType();
                    else if (fragment instanceof TagLetterFragment)
                        ((TagLetterFragment) fragment).updataCurShowType();
                    else ((TagLifeFragment) fragment).updataCurShowType();
                }
                if (m_refreshTag) {
                    m_refreshTag = false;
                    v_refreshLayout.setRefreshing(false);
                }
                break;
            case NETWORK_UNAVAILABLE:
                Toast.makeText(getActivity(), "请先连接网络", Toast.LENGTH_SHORT).show();
                if (m_refreshTag) {
                    m_refreshTag = false;
                    v_refreshLayout.setRefreshing(false);
                }
                break;
        }
    }

    /*
    protected static class SendInfoHandler extends Handler {
        private WeakReference<Fragment> reference;

        public SendInfoHandler(Fragment fragment) {
            reference = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            Fragment fragment = reference.get();

            switch (msg.what) {
                case SHOW_BOOKINFO:
                    if (fragment != null) {
                        if (fragment instanceof TagHotFragment)
                            ((TagHotFragment) fragment).initView(fragment.getView());
                        else if (fragment instanceof TagLetterFragment)
                            ((TagLetterFragment) fragment).initView(fragment.getView());
                        else ((TagLifeFragment) fragment).initView(fragment.getView());
                    }

                    DialogManager.dismissDialog(m_dialog);
                    break;
                case NETWORK_ERROR:
                    if (fragment != null)
                        Toast.makeText(fragment.getActivity(), "网络异常", Toast.LENGTH_SHORT).show();
                    break;

                case UPDATA_BOOKINFO:
                    if (fragment != null) {
                        if (fragment instanceof TagHotFragment)
                            ((TagHotFragment) fragment).updataView();
                        else if (fragment instanceof TagLetterFragment)
                            ((TagLetterFragment) fragment).updataView();
                        else ((TagLifeFragment) fragment).updataView();
                    }
                    DialogManager.dismissDialog(m_dialog);
                    break;
                case SHOW_LOADING_ITEM:
                    if (fragment != null) {
                        if (fragment instanceof TagHotFragment)
                            ((TagHotFragment) fragment).updataCurShowType();
                        else if (fragment instanceof TagLetterFragment)
                            ((TagLetterFragment) fragment).updataCurShowType();
                        else ((TagLifeFragment) fragment).updataCurShowType();
                    }
                    break;
            }
        }
    }
    */
}
