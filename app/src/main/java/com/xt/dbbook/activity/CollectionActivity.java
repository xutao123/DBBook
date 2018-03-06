package com.xt.dbbook.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.xt.dbbook.Config.IntentExtraConfig;
import com.xt.dbbook.R;
import com.xt.dbbook.adapter.RefreshListAdapter;
import com.xt.dbbook.app.DBBookManager;
import com.xt.dbbook.bean.net.CollectionInfo;
import com.xt.dbbook.network.OkHttpUtil;
import com.xt.dbbook.network.ResponseCallback;
import com.xt.dbbook.network.URLUtil;
import com.xt.dbbook.tools.DialogManager;
import com.xt.dbbook.view.LabelView;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

import static com.xt.dbbook.adapter.RefreshListAdapter.CurShowType.SHOW_NORMAL;
import static com.xt.dbbook.adapter.RefreshListAdapter.ViewLayoutType.COLLECTION_LAYOUT;

/**
 * Created by xt on 2018/02/06.
 */

public class CollectionActivity extends BaseActivity implements RefreshListAdapter.OnRefreshListListener,
        View.OnClickListener {

    @BindView(R.id.toolbar)
    Toolbar v_toolbar;
    @BindView(R.id.total_num)
    LabelView v_totalNum;
    @BindView(R.id.collection_list)
    RecyclerView v_collectionList;
    @BindView(R.id.illustrate)
    LabelView v_illustrate;

    private CollectionInfo m_collectionInfo;
    private RefreshListAdapter m_adapter;
    private LinearLayoutManager m_layoutManager;
    private Intent m_intent;

    private static final int NET_WORK_SUCCESS = 0x100;
    private static final int NET_WORK_ERROR = NET_WORK_SUCCESS + 1;
    private static final int NET_WORK_UNAVAILABLE = NET_WORK_SUCCESS + 2;
    private static final int COLLECTION_UPDATE = NET_WORK_SUCCESS + 3;

    protected long m_start = 0;//此次搜索开始数目
    protected long m_total;//所有条目数目
    protected long m_count = 20;//一次返回的数目
    protected long m_returnTotal = 0;//已经返回的数目

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);
        ButterKnife.bind(this);

        initView();
        initData();
    }

    private void initData() {
        if (m_handler == null)
            m_handler = new CommonHandler(this);

        if (m_dialog == null)
            m_dialog = DialogManager.createLoadingDialog(this, null, "请稍后...", true);

        //GET  https://api.douban.com/v2/book/user/:name/collections
        String url = URLUtil.getInstance().addPathNode("book").addPathNode("user")
                .addPathNode(DBBookManager.getUserInfo().getUid())
                .addPathNode("collections")
                .addFirstLongParams("start", m_start)
                .addLongParams("count", m_count)
                .addFields("start", "count", "total",
                        "collections", "book", "updated", "user_id", "author", "image", "id",
                        "publisher", "title", "pubdate", "rating", "average").getUrl();

        DialogManager.showDialog(m_dialog);
        OkHttpUtil.getInstance(this).get(url).execute(new ResponseCallback<CollectionInfo>() {
            @Override
            public void onResponse(CollectionInfo info) {
                m_collectionInfo = info;
                if (m_count < info.getTotal())
                    m_start = m_count;
                else m_start = info.getTotal();
                m_total = info.getTotal();
                m_returnTotal = info.getCount();
                m_handler.sendEmptyMessage(NET_WORK_SUCCESS);
            }

            @Override
            public void onFailure(Call call, IOException e) {
                m_handler.sendEmptyMessage(NET_WORK_ERROR);
            }

            @Override
            public void onNetWorkUnavaliable() {
                m_handler.sendEmptyMessage(NET_WORK_UNAVAILABLE);
            }
        });

    }

    private void initView() {
        //设置ToolBar
        v_toolbar.setTitle("我的收藏");
        setSupportActionBar(v_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//设置回退图标
        v_toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        v_illustrate.setOnClickListener(this);
    }

    private void initCollectionView() {
        if (m_collectionInfo == null)
            return;

        if (m_layoutManager == null)
            m_layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        v_collectionList.setLayoutManager(m_layoutManager);

        if (m_adapter == null) {
            m_adapter = new RefreshListAdapter(this, m_collectionInfo, m_layoutManager, COLLECTION_LAYOUT);
            m_adapter.setRefreshListListener(this);
            v_collectionList.setAdapter(m_adapter);
        }

        v_collectionList.addOnScrollListener(m_adapter.getRecyclerViewScrollListener());

        v_totalNum.setText(String.valueOf(m_collectionInfo.getTotal()) + "本");
    }

    @Override
    public void handlerActivityMessage(Message msg) {
        super.handlerActivityMessage(msg);

        DialogManager.dismissDialog(m_dialog);

        switch (msg.what) {
            case NET_WORK_SUCCESS:
                if (m_collectionInfo.getCollections() == null ||
                        m_collectionInfo.getCollections().size() <= 0)
                    Toast.makeText(this, "您还收藏任何图书", Toast.LENGTH_SHORT).show();
                else
                    initCollectionView();
                break;
            case NET_WORK_ERROR:
                Toast.makeText(this, "网络或者服务器错误", Toast.LENGTH_SHORT).show();
                break;
            case NET_WORK_UNAVAILABLE:
                Toast.makeText(this, "请先连接网络", Toast.LENGTH_SHORT).show();
                break;
            case COLLECTION_UPDATE:
                m_adapter.updataData(m_collectionInfo);
                break;
        }
    }

    /**
     * 选择RefreshListAdapter中的item
     *
     * @param position
     * @param bookId
     */
    @Override
    public void onChooseItem(int position, String bookId) {
        if (m_intent == null)
            m_intent = new Intent(this, BookDetailActivity.class);
        m_intent.putExtra(IntentExtraConfig.BOOK_ID, bookId);
        startActivity(m_intent);
    }

    @Override
    public void getNewData() {
        if (m_returnTotal >= m_total) {
            m_adapter.updataCurShowType(SHOW_NORMAL);
            Toast.makeText(this, "所有收藏都在上面了", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = URLUtil.getInstance().addPathNode("book")
                .addPathNode(DBBookManager.getUserInfo().getUid())
                .addPathNode("collections").addFieldsWithoutParam("start", "count", "total",
                        "collections", "book", "updated", "user_id", "author", "image", "id",
                        "publisher", "title", "pubdate", "rating", "average").getUrl();

        OkHttpUtil.getInstance(this).get(url).execute(new ResponseCallback<CollectionInfo>() {
            @Override
            public void onResponse(CollectionInfo info) {
                m_collectionInfo = info;
                if (m_start + m_count < info.getTotal())
                    m_returnTotal += m_count;
                else m_returnTotal = info.getTotal();
                m_start = m_returnTotal;
                m_total = info.getTotal();
                m_handler.sendEmptyMessage(COLLECTION_UPDATE);
            }

            @Override
            public void onFailure(Call call, IOException e) {
                m_adapter.updataCurShowType(SHOW_NORMAL);
                DialogManager.dismissDialog(m_dialog);
                m_handler.sendEmptyMessage(NET_WORK_ERROR);
            }

            @Override
            public void onNetWorkUnavaliable() {
                m_handler.sendEmptyMessage(NET_WORK_UNAVAILABLE);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (m_adapter != null)
            m_adapter.recycler();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.illustrate:
                MaterialDialog m_alertDialog = DialogManager.createAlertDialog(this, "收藏说明",
                        "因豆瓣未提供收藏权限和取消收藏权限，因此，需要去原豆瓣APP中进行收藏，才能再此显示收藏结果");
                DialogManager.showDialog(m_alertDialog);
                break;
        }
    }
}
