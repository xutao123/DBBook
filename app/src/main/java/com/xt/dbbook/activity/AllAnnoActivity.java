package com.xt.dbbook.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.xt.dbbook.Config.IntentExtraConfig;
import com.xt.dbbook.R;
import com.xt.dbbook.adapter.RefreshListAdapter;
import com.xt.dbbook.bean.net.BookAnnoInfo;
import com.xt.dbbook.network.OkHttpUtil;
import com.xt.dbbook.network.ResponseCallback;
import com.xt.dbbook.network.URLUtil;
import com.xt.dbbook.tools.DialogManager;
import com.xt.dbbook.tools.DipPixelUtil;
import com.xt.dbbook.tools.VerticalAnnoItemDecoration;
import com.xt.dbbook.view.LabelView;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

import static com.xt.dbbook.Config.IntentExtraConfig.COMMIT_ID_TO_ALLANNOACTIVITY;
import static com.xt.dbbook.adapter.RefreshListAdapter.CurShowType.SHOW_NORMAL;
import static com.xt.dbbook.adapter.RefreshListAdapter.ViewLayoutType.ANNOTATION_LAYOUT;

/**
 * Created by xt on 2018/02/11.
 */

public class AllAnnoActivity extends BaseActivity implements
        RefreshListAdapter.OnRefreshListListener, SwipeRefreshLayout.OnRefreshListener {
    @BindView(R.id.toolbar)
    Toolbar v_toolbar;
    @BindView(R.id.total_num)
    LabelView v_totalNum;
    @BindView(R.id.anno_recycler)
    RecyclerView v_annoRecycler;
    @BindView(R.id.refresh_layout)
    SwipeRefreshLayout v_refreshLayout;

    protected int m_start = 0;//此次搜索开始数目
    protected int m_total;//所有条目数目
    protected int m_count = 20;//一次返回的数目
    protected int m_returnTotal = 0;//已经返回的数目
    private RefreshListAdapter m_adapter;
    private LinearLayoutManager m_layoutManager;

    private BookAnnoInfo m_noteInfo;
    private String m_bookId;
    private Intent m_intent;

    private static final int NETWORK_UNAVAILABLE = 0x100;
    private static final int GET_BOOK_ANNO_FAILURE = NETWORK_UNAVAILABLE + 1;
    private static final int GET_BOOK_ANNO_SUCCESS = NETWORK_UNAVAILABLE + 2;
    private static final int UPDATA_BOOK_NOTE_FAILURE = NETWORK_UNAVAILABLE + 3;
    private static final int UPDATA_BOOK_NOTE_SUCCESS = NETWORK_UNAVAILABLE + 4;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        setContentView(R.layout.activity_all_anno);
        ButterKnife.bind(this);

        initToolbar();

        Intent intent = getIntent();
        if (intent != null) {
            String bookId = intent.getStringExtra(COMMIT_ID_TO_ALLANNOACTIVITY);
            if (!TextUtils.isEmpty(bookId)) {
                m_bookId = bookId;
                initData();
            }
        }
    }

    private void initToolbar() {
        v_toolbar.setTitle("所有笔记");
        setSupportActionBar(v_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        v_toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }


    private void initData() {
        if (m_dialog == null)
            m_dialog = DialogManager.createLoadingDialog(this, null, "请稍后...", false);

        if (m_handler == null)
            m_handler = new CommonHandler(this);

        getBookAnno(false);
    }

    private void getBookAnno(boolean isRefresh) {
        if (!isRefresh) //下拉刷新不用显示LoadingDialog
            DialogManager.showDialog(m_dialog);
        //获得书评
        String noteUrl = URLUtil.getInstance().addPathNode("book").addPathNode(m_bookId)
                .addPathNode("annotations").addFirstIntParams("start", 0).addIntParams("count", m_count)
                .addFields("count", "start", "total", "annotations", "chapter", "author_user",
                        "summary", "page_no", "time", "name", "url", "avatar", "uid", "id")
                .getUrl();
        OkHttpUtil.getInstance(this).get(noteUrl).execute(new ResponseCallback<BookAnnoInfo>() {
            @Override
            public void onResponse(BookAnnoInfo info) {
                m_noteInfo = info;
                if (m_count < info.getTotal())
                    m_start = m_count;
                else m_start = info.getTotal();
                m_total = info.getTotal();
                m_returnTotal = info.getCount();

                m_handler.sendEmptyMessage(GET_BOOK_ANNO_SUCCESS);
            }

            @Override
            public void onFailure(Call call, IOException e) {
                m_handler.sendEmptyMessage(GET_BOOK_ANNO_FAILURE);
            }

            @Override
            public void onNetWorkUnavaliable() {
                m_handler.sendEmptyMessage(NETWORK_UNAVAILABLE);
            }
        });
    }

    private void initAnnoView() {
        if (m_layoutManager == null) {
            m_layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            v_annoRecycler.setLayoutManager(m_layoutManager);
            v_annoRecycler.addItemDecoration(
                    new VerticalAnnoItemDecoration(DipPixelUtil.dip2Pixel(this, 20.0f)));
        }

        if (m_adapter == null) {
            m_adapter = new RefreshListAdapter(this, m_noteInfo, m_layoutManager, ANNOTATION_LAYOUT);
            //设置Item点击监听
            m_adapter.setRefreshListListener(this);
            v_annoRecycler.setAdapter(m_adapter);
            //监听RecyclerView滑动
            v_annoRecycler.addOnScrollListener(m_adapter.getRecyclerViewScrollListener());
            //设置下拉刷新监听
            v_refreshLayout.setOnRefreshListener(this);
        } else {
            //下拉刷新
            m_adapter.refreshView(m_noteInfo);
        }

        v_totalNum.setText("共" + String.valueOf(m_noteInfo.getTotal()) + "篇");
    }

    @Override
    public void handlerActivityMessage(Message msg) {
        super.handlerActivityMessage(msg);
        DialogManager.dismissDialog(m_dialog);
        v_refreshLayout.setRefreshing(false);
        switch (msg.what) {
            case GET_BOOK_ANNO_SUCCESS:
                initAnnoView();
                break;
            case UPDATA_BOOK_NOTE_SUCCESS:
                m_adapter.updataData(m_noteInfo);
                break;
            case GET_BOOK_ANNO_FAILURE:
                Toast.makeText(this, "网络或者服务器错误", Toast.LENGTH_SHORT).show();
                break;
            case UPDATA_BOOK_NOTE_FAILURE:
                Toast.makeText(this, "网络或者服务器错误", Toast.LENGTH_SHORT).show();
                m_adapter.updataCurShowType(SHOW_NORMAL);
                break;
            case NETWORK_UNAVAILABLE:
                Toast.makeText(this, "请先连接网络", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onChooseItem(int position, String annoId) {
        if (m_intent == null)
            m_intent = new Intent(this, DetailAnnoActivity.class);
        m_intent.putExtra(IntentExtraConfig.COMMIT_ID_TO_DETAILANNOACTIVITY, annoId);
        startActivity(m_intent);
    }

    @Override
    public void getNewData() {
        if (m_returnTotal >= m_total) {
            m_adapter.updataCurShowType(SHOW_NORMAL);
            Toast.makeText(this, "所有笔记都在上面了", Toast.LENGTH_SHORT).show();
            return;
        }

        String noteUrl = URLUtil.getInstance().addPathNode("book").addPathNode(m_bookId)
                .addPathNode("annotations").addFirstIntParams("start", m_start).addIntParams("count", m_count)
                .addFields("count", "start", "total", "annotations", "chapter", "author_user",
                        "summary", "page_no", "time", "name", "url", "avatar", "uid", "id")
                .getUrl();
        OkHttpUtil.getInstance(this).get(noteUrl).execute(new ResponseCallback<BookAnnoInfo>() {
            @Override
            public void onResponse(BookAnnoInfo info) {
                m_noteInfo = info;
                if (m_start + m_count < info.getTotal())
                    m_returnTotal += m_count;
                else m_returnTotal = info.getTotal();
                m_start = m_returnTotal;
                m_total = info.getTotal();
                m_handler.sendEmptyMessage(UPDATA_BOOK_NOTE_SUCCESS);
            }

            @Override
            public void onFailure(Call call, IOException e) {
                m_handler.sendEmptyMessage(UPDATA_BOOK_NOTE_FAILURE);
            }

            @Override
            public void onNetWorkUnavaliable() {
                m_handler.sendEmptyMessage(NETWORK_UNAVAILABLE);
            }
        });
    }

    /**
     * 下拉刷新
     */
    @Override
    public void onRefresh() {
        getBookAnno(true); //上拉刷新，重新获取数据
    }
}
