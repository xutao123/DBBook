package com.xt.dbbook.activity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.florent37.glidepalette.GlidePalette;
import com.xt.dbbook.Config.IntentExtraConfig;
import com.xt.dbbook.R;
import com.xt.dbbook.adapter.HorizontalAnnotationAdapter;
import com.xt.dbbook.app.DBBookManager;
import com.xt.dbbook.bean.net.BookDetailInfo;
import com.xt.dbbook.bean.net.BookAnnoInfo;
import com.xt.dbbook.bean.net.CollectionStateInfo;
import com.xt.dbbook.network.OkHttpUtil;
import com.xt.dbbook.network.ResponseCallback;
import com.xt.dbbook.network.URLUtil;
import com.xt.dbbook.tools.HorizontalAnnoItemDecoration;
import com.xt.dbbook.tools.DialogManager;
import com.xt.dbbook.tools.DipPixelUtil;
import com.xt.dbbook.view.AppBarOffsetChangeListener;
import com.xt.dbbook.view.FlowLabelLayout;
import com.xt.dbbook.view.LabelView;
import com.xt.dbbook.view.PointStarView;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

import static com.xt.dbbook.Config.IntentExtraConfig.AUTHOR_TYPE;
import static com.xt.dbbook.Config.IntentExtraConfig.CATALOG_TYPE;
import static com.xt.dbbook.Config.IntentExtraConfig.COMEIN_WITH_DATA;
import static com.xt.dbbook.Config.IntentExtraConfig.COME_INTO_SEARCH_ACTIVITY_STATE;
import static com.xt.dbbook.Config.IntentExtraConfig.COMMIT_DATA_TO_SEARCH_ACTIVITY;
import static com.xt.dbbook.Config.IntentExtraConfig.COMMIT_ID_TO_ALLANNOACTIVITY;
import static com.xt.dbbook.Config.IntentExtraConfig.COMMIT_ID_TO_DETAILANNOACTIVITY;
import static com.xt.dbbook.Config.IntentExtraConfig.DETAIL_TYPE;
import static com.xt.dbbook.Config.IntentExtraConfig.GET_DATA_KEY;
import static com.xt.dbbook.Config.IntentExtraConfig.SUMMARY_TYPE;

/**
 * Created by xt on 2018/01/27.
 */

public class BookDetailActivity extends BaseActivity implements View.OnClickListener,
        FlowLabelLayout.FlowItemClickListener, HorizontalAnnotationAdapter.OnAnnoItemClickListener {
    @BindView(R.id.toolbar)
    Toolbar v_toolbar;
    @BindView(R.id.collapsing_toolbar_layout)
    CollapsingToolbarLayout v_collapsingToolbarLayout;
    @BindView(R.id.app_bar)
    AppBarLayout v_appBar;
    @BindView(R.id.book_img)
    ImageView v_bookImg;
    @BindView(R.id.app_bar_title)
    TextView v_appBarTitle;
    @BindView(R.id.point_num)
    TextView v_pointNum;
    @BindView(R.id.point_view)
    PointStarView v_pointView;
    @BindView(R.id.book_title)
    TextView v_bookTitle;
    @BindView(R.id.sub_title)
    TextView v_subTitle;
    @BindView(R.id.author)
    TextView v_author;
    @BindView(R.id.publisher)
    TextView v_publisher;
    @BindView(R.id.pub_time)
    TextView v_pubTime;
    @BindView(R.id.comment_num)
    TextView v_commentNum;
    @BindView(R.id.label_layout)
    FlowLabelLayout v_labelLayout;
    @BindView(R.id.point_layout)
    CardView pointLayout;
    @BindView(R.id.summary)
    TextView v_summary;
    @BindView(R.id.catalog)
    TextView v_catalog;
    @BindView(R.id.author_summary)
    TextView v_authorSummary;
    @BindView(R.id.annotation_recycler)
    RecyclerView v_annotationRecycler;
    @BindView(R.id.scroll_view)
    NestedScrollView v_scrollView;
    @BindView(R.id.summary_arrow)
    ImageView v_summaryArrow;
    @BindView(R.id.author_arrow)
    ImageView v_authorArrow;
    @BindView(R.id.catalog_arrow)
    ImageView v_catalogArrow;
    @BindView(R.id.summary_layout)
    RelativeLayout v_summaryLayout;
    @BindView(R.id.author_summary_layout)
    RelativeLayout v_authorSummaryLayout;
    @BindView(R.id.catalog_layout)
    RelativeLayout v_catalogLayout;

    //用于Activity之间的动画
    private View m_summaryTextView;
    private View m_authorTextView;
    private View m_catalogTextView;

    private BookDetailInfo m_detailInfo;
    private BookAnnoInfo m_noteInfo;
    private HorizontalAnnotationAdapter m_annoAdapter;
    private Intent m_annoIntent;
    private Menu m_menu;
    private boolean m_isCollected = false;

    private static final int GET_BOOK_INFO_SUCCESS = 0x101;
    private static final int GET_BOOK_INFO_FAILURE = GET_BOOK_INFO_SUCCESS + 1;
    private static final int GET_BOOK_NOTE_SUCCESS = GET_BOOK_INFO_SUCCESS + 2;
    private static final int GET_BOOK_NOTE_FAILURE = GET_BOOK_INFO_SUCCESS + 3;
    private static final int NETWORK_UNAVAILABLE = GET_BOOK_INFO_SUCCESS + 4;
    private static final int GET_BOOK_COLLECTION_SUCCESS = GET_BOOK_INFO_SUCCESS + 5;
    private static final int GET_BOOK_COLLECTION_FAILURE = GET_BOOK_INFO_SUCCESS + 6;
    private static final int BOOK_COLLECTION_SUCCESS = GET_BOOK_INFO_SUCCESS + 7;
    private static final int BOOK_COLLECTION_FAILURE = GET_BOOK_INFO_SUCCESS + 8;
    private static final int DELETE_COLLECTION_SUCCESS = GET_BOOK_INFO_SUCCESS + 9;
    private static final int DELETE_COLLECTION_FAILURE = GET_BOOK_INFO_SUCCESS + 10;

    private int m_curSearchType = SEARCH_BY_ID;
    private static final int SEARCH_BY_ID = 0;
    private static final int SEARCH_BY_ISBN = SEARCH_BY_ID + 1;

    //最多返回多少条笔记
    private static final int m_getAnnoNum = 4;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**
         * 设置状态栏透明
         * Activity根View要设置android:fitsSystemWindows="true"
         * 否则状态栏和Activity中的View重合
         */
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        setContentView(R.layout.activity_detail);
        m_unbinder = ButterKnife.bind(this);

        initData();
    }

    /**
     * 扫描ISBN进入时使用isbn号来搜索图书详细内容
     * 选择显示的书籍列表进入时使用book id搜索
     */
    private void initData() {
        m_handler = new CommonHandler(this);
        Intent intent = getIntent();
        String book_id = "";
        String book_isbn = "";
        if (intent != null) {
            book_id = intent.getStringExtra(IntentExtraConfig.BOOK_ID);
            book_isbn = intent.getStringExtra(IntentExtraConfig.BOOK_ISBN);

            if (!TextUtils.isEmpty(book_id))
                m_curSearchType = SEARCH_BY_ID;
            else m_curSearchType = SEARCH_BY_ISBN;

        }
        m_dialog = DialogManager.createLoadingDialog(this, "Search", "请稍后...", true);
        DialogManager.showDialog(m_dialog);

        String detailUrl = null;
        switch (m_curSearchType) {
            case SEARCH_BY_ID:
                detailUrl = URLUtil.getInstance().addPathNode("book").addPathNode(book_id).getUrl();
                break;
            case SEARCH_BY_ISBN:
                detailUrl = URLUtil.getInstance().addPathNode("book").addPathNode("isbn").
                        addPathNode(book_isbn).getUrl();
                break;
        }

        //获得详细信息
        getDetailInfo(detailUrl);
    }

    /**
     * 获得当前书籍是否被收藏
     */
    private void getCollectionStated(String book_id) {
        //GET  https://api.douban.com/v2/book/:id/collection?user_id=""

        if (DBBookManager.getUserInfo() == null ||
                TextUtils.isEmpty(DBBookManager.getUserInfo().getUid())) {
            m_isCollected = false;
            return;
        }

        String url = URLUtil.getInstance().addPathNode("book")
                .addPathNode(book_id).addPathNode("collection")
                .addFirstStringParams("user_id", DBBookManager.getUserInfo().getUid())
                .getUrl();

        OkHttpUtil.getInstance(this).get(url).execute(new ResponseCallback<CollectionStateInfo>() {
            @Override
            public void onResponse(CollectionStateInfo info) {
                Message msg = Message.obtain();
                msg.arg1 = info.getCode();
                msg.what = GET_BOOK_COLLECTION_SUCCESS;
                m_handler.sendMessage(msg);
            }

            @Override
            public void onFailure(Call call, IOException e) {
                m_handler.sendEmptyMessage(GET_BOOK_COLLECTION_FAILURE);
            }

            @Override
            public void onNetWorkUnavaliable() {
                m_handler.sendEmptyMessage(NETWORK_UNAVAILABLE);
            }
        });

    }

    /**
     * 获得详细信息
     *
     * @param detailUrl
     */
    private void getDetailInfo(String detailUrl) {
        OkHttpUtil.getInstance(this).get(detailUrl).execute(new ResponseCallback<BookDetailInfo>() {
            @Override
            public void onResponse(BookDetailInfo info) {
                getCollectionStated(info.getId());
                m_detailInfo = info;
                Message msg = Message.obtain();
                msg.obj = info.getId();
                msg.what = GET_BOOK_INFO_SUCCESS;
                m_handler.sendMessage(msg);
            }

            @Override
            public void onFailure(Call call, IOException e) {
                m_handler.sendEmptyMessage(GET_BOOK_INFO_FAILURE);
            }

            @Override
            public void onNetWorkUnavaliable() {
                m_handler.sendEmptyMessage(NETWORK_UNAVAILABLE);
            }
        });
    }

    /**
     * 获得书评
     */
    private void getNote(String book_id) {
        //获得书评
        String noteUrl = URLUtil.getInstance().addPathNode("book").addPathNode(book_id)
                .addPathNode("annotations").addFirstIntParams("start", 0).addIntParams("count", m_getAnnoNum)
                .addFields("count", "start", "total", "annotations", "chapter", "author_user",
                        "summary", "page_no", "time", "name", "url", "avatar", "uid", "id")
                .getUrl();
        OkHttpUtil.getInstance(this).get(noteUrl).execute(new ResponseCallback<BookAnnoInfo>() {
            @Override
            public void onResponse(BookAnnoInfo info) {
                m_noteInfo = info;
                m_handler.sendEmptyMessage(GET_BOOK_NOTE_SUCCESS);
            }

            @Override
            public void onFailure(Call call, IOException e) {
                m_handler.sendEmptyMessage(GET_BOOK_NOTE_FAILURE);
            }

            @Override
            public void onNetWorkUnavaliable() {
                m_handler.sendEmptyMessage(NETWORK_UNAVAILABLE);
            }
        });
    }

    private void initView() {
        if (m_detailInfo == null || m_detailInfo.getImages() == null ||
                m_detailInfo.getRating() == null)
            return;
        //获得主色调
        Glide.with(this).load(m_detailInfo.getImages().getLarge())
                .listener(
                        GlidePalette.with(m_detailInfo.getImages().getLarge())
                                .use(GlidePalette.Profile.VIBRANT).intoBackground(v_appBar)
                                .use(GlidePalette.Profile.MUTED_LIGHT).intoBackground(v_scrollView)
                )
                .into(v_bookImg);

        setSupportActionBar(v_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        v_toolbar.setTitle("");
        //点击回退
        v_toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        v_appBar.addOnOffsetChangedListener(new DetailAppBarOffsetChangeListener());

        //将值赋给控件
        v_pointNum.setText(m_detailInfo.getRating().getAverage());
        v_pointView.setPoint(Float.parseFloat(m_detailInfo.getRating().getAverage()), 10.0f);
        v_commentNum.setText(String.valueOf(m_detailInfo.getRating().getNumRaters()));
        v_bookTitle.setText(m_detailInfo.getTitle());
        v_subTitle.setText(m_detailInfo.getSubtitle());
        v_author.setText("作者: " + m_detailInfo.getAuthor());
        v_publisher.setText("出版社: " + m_detailInfo.getPublisher());
        v_pubTime.setText("出版时间: " + m_detailInfo.getPubdate());

        //显示标签
        for (BookDetailInfo.TagData tag : m_detailInfo.getTags())
            v_labelLayout.addLabelView(createLabelView(tag.getName()));
        //设置每个标签按钮的点击事件
        v_labelLayout.setOnFlowItemClickListener(this);


        if (!TextUtils.isEmpty(m_detailInfo.getSummary().trim())) {
            v_summary.setText(m_detailInfo.getSummary());
            v_summaryLayout.setOnClickListener(this);
        } else {
            v_summary.setText("暂无简介");
            v_summaryArrow.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(m_detailInfo.getAuthor_intro().trim())) {
            v_authorSummary.setText(m_detailInfo.getAuthor_intro());
            v_authorSummaryLayout.setOnClickListener(this);
        } else {
            v_authorSummary.setText("暂无作者简介");
            v_authorArrow.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(m_detailInfo.getCatalog().trim())) {
            v_catalog.setText(m_detailInfo.getCatalog());
            v_catalogLayout.setOnClickListener(this);
        } else {
            v_catalog.setText("暂无目录");
            v_catalogArrow.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.summary_layout:
                if (m_summaryTextView == null)
                    m_summaryTextView = findViewById(R.id.summary_layout);
                Intent intent1 = new Intent(this, ShowAuthorSummaryCatalogActivity.class);
                Bundle bundle1 = new Bundle();
                bundle1.putString(GET_DATA_KEY, m_detailInfo.getSummary());
                intent1.putExtras(bundle1);
                intent1.putExtra(DETAIL_TYPE, SUMMARY_TYPE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                    startActivity(intent1,
                            ActivityOptions.makeSceneTransitionAnimation(this,
                                    m_summaryTextView, "summary_author_catalog").toBundle());
                else startActivity(intent1);
                break;
            case R.id.author_summary_layout:
                if (m_authorTextView == null)
                    m_authorTextView = findViewById(R.id.author_summary_layout);
                Intent intent2 = new Intent(this, ShowAuthorSummaryCatalogActivity.class);
                Bundle bundle2 = new Bundle();
                bundle2.putString(GET_DATA_KEY, m_detailInfo.getAuthor_intro());
                intent2.putExtras(bundle2);
                intent2.putExtra(DETAIL_TYPE, AUTHOR_TYPE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                    startActivity(intent2,
                            ActivityOptions.makeSceneTransitionAnimation(this,
                                    m_authorTextView, "summary_author_catalog").toBundle());
                else startActivity(intent2);
                break;
            case R.id.catalog_layout:
                if (m_catalogTextView == null)
                    m_catalogTextView = findViewById(R.id.catalog_layout);
                Intent intent3 = new Intent(this, ShowAuthorSummaryCatalogActivity.class);
                Bundle bundle3 = new Bundle();
                bundle3.putString(GET_DATA_KEY, m_detailInfo.getCatalog());
                intent3.putExtras(bundle3);
                intent3.putExtra(DETAIL_TYPE, CATALOG_TYPE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                    startActivity(intent3,
                            ActivityOptions.makeSceneTransitionAnimation(this,
                                    m_catalogTextView, "summary_author_catalog").toBundle());
                else startActivity(intent3);
                break;
        }
    }

    /**
     * 显示书评View
     */
    private void initNoteView() {
        v_annotationRecycler.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        v_annotationRecycler.addItemDecoration(
                new HorizontalAnnoItemDecoration(DipPixelUtil.dip2Pixel(this, 10.0f)));
        if (m_annoAdapter == null)
            m_annoAdapter = new HorizontalAnnotationAdapter(this, m_noteInfo);
        m_annoAdapter.setAnnoItemClickListener(this);
        v_annotationRecycler.setAdapter(m_annoAdapter);
    }

    public LabelView createLabelView(String label) {
        LabelView lv = (LabelView) LayoutInflater.from(this).inflate(R.layout.view_label, null);
        lv.setText(label);
        return lv;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.collection_menu, menu);
        m_menu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.collection: //收藏
                //权限问题，不可收藏
                collectBook();
                return true;
            case R.id.cancel_collection://取消收藏
                //权限问题，不可取消收藏
                cancelCollectBook();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 取消收藏
     * 权限问题，不可取消
     */
    private void cancelCollectBook() {
        Toast.makeText(this, "权限不支持", Toast.LENGTH_SHORT).show();
        if (m_detailInfo == null)
            return;

        if (DBBookManager.getUserInfo() == null ||
                TextUtils.isEmpty(DBBookManager.getUserInfo().getUid())) {
            m_isCollected = false;
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
            return;
        }

        //        DELETE  https://api.douban.com/v2/book/:id/collection
        //        返回: status = 204 无返回信息
        String url = URLUtil.getInstance().addPathNode("book")
                .addPathNode(m_detailInfo.getId()).addPathNode("collection")
                .addFirstStringParams("user_id", DBBookManager.getUserInfo().getUid())
                .getUrl();

        OkHttpUtil.getInstance(this).delete(url).execute(new ResponseCallback() {
            @Override
            public void onResponse(Object o) {
                m_handler.sendEmptyMessage(DELETE_COLLECTION_SUCCESS);
            }

            @Override
            public void onFailure(Call call, IOException e) {
                m_handler.sendEmptyMessage(DELETE_COLLECTION_FAILURE);
            }

            @Override
            public void onNetWorkUnavaliable() {
                m_handler.sendEmptyMessage(NETWORK_UNAVAILABLE);
            }
        });
    }

    /**
     * 收藏书籍
     * 权限问题不可收藏
     */
    private void collectBook() {
        Toast.makeText(this, "权限不支持", Toast.LENGTH_SHORT).show();

        if (m_detailInfo == null)
            return;
        if (DBBookManager.getUserInfo() == null ||
                TextUtils.isEmpty(DBBookManager.getUserInfo().getUid())) {
            m_isCollected = false;
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
            return;
        }
        //POST  https://api.douban.com/v2/book/:id/collection?user_id=""
        //status	收藏状态	必填（想读：wish 在读：reading 或 doing 读过：read 或 done）
        //status = "wish"
        String url = URLUtil.getInstance().addPathNode("book")
                .addPathNode(m_detailInfo.getId()).addPathNode("collection").getUrl();

        OkHttpUtil.getInstance(this).post(url)
                .add("user_id", DBBookManager.getUserInfo().getUid())
                .add("status", "wish").execute(new ResponseCallback() {
            @Override
            public void onResponse(Object o) {
                m_handler.sendEmptyMessage(BOOK_COLLECTION_SUCCESS);
            }

            @Override
            public void onFailure(Call call, IOException e) {
                m_handler.sendEmptyMessage(BOOK_COLLECTION_FAILURE);
            }

            @Override
            public void onNetWorkUnavaliable() {
                m_handler.sendEmptyMessage(NETWORK_UNAVAILABLE);
            }
        });
    }

    /**
     * 调用invalidateOptionsMenu()回调这个方法用于动态修改Toolbar中的menu
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (m_isCollected) {
            m_menu.findItem(R.id.collection).setVisible(false);
            m_menu.findItem(R.id.cancel_collection).setVisible(true);
        } else {
            m_menu.findItem(R.id.collection).setVisible(true);
            m_menu.findItem(R.id.cancel_collection).setVisible(false);
        }
        v_toolbar.setTitle("");
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void handlerActivityMessage(Message msg) {
        switch (msg.what) {
            case GET_BOOK_INFO_SUCCESS:
                initView();
                getNote(msg.obj.toString());
                DialogManager.dismissDialog(m_dialog);
                break;
            case GET_BOOK_INFO_FAILURE:
                DialogManager.dismissDialog(m_dialog);
                Toast.makeText(this, "网络或者服务器错误", Toast.LENGTH_SHORT).show();
                break;
            case GET_BOOK_NOTE_SUCCESS:
                initNoteView();
                DialogManager.dismissDialog(m_dialog);
                break;
            case GET_BOOK_NOTE_FAILURE:
                DialogManager.dismissDialog(m_dialog);
                Toast.makeText(this, "网络或者服务器错误", Toast.LENGTH_SHORT).show();
                break;
            case NETWORK_UNAVAILABLE:
                DialogManager.dismissDialog(m_dialog);
                Toast.makeText(this, "请先连接网络", Toast.LENGTH_SHORT).show();
                break;
            case GET_BOOK_COLLECTION_FAILURE: //获得收藏状态
                m_isCollected = false;
                invalidateOptionsMenu();
                break;
            case GET_BOOK_COLLECTION_SUCCESS:
                if (msg.arg1 == 0)
                    m_isCollected = true;
                else m_isCollected = false;
                invalidateOptionsMenu();
                break;
            case BOOK_COLLECTION_FAILURE://收藏失败
                Toast.makeText(this, "收藏失败", Toast.LENGTH_SHORT).show();
                break;
            case BOOK_COLLECTION_SUCCESS://收藏成功
                Toast.makeText(this, "收藏成功", Toast.LENGTH_SHORT).show();
                m_isCollected = true;
                invalidateOptionsMenu();
                break;
            case DELETE_COLLECTION_FAILURE:
                Toast.makeText(this, "取消收藏失败", Toast.LENGTH_SHORT).show();
                m_isCollected = false;
                invalidateOptionsMenu();
                break;
            case DELETE_COLLECTION_SUCCESS:
                Toast.makeText(this, "已取消收藏", Toast.LENGTH_SHORT).show();
                m_isCollected = true;
                invalidateOptionsMenu();
                break;
        }
    }

    /**
     * 点击FlowLabelLayout中的Item回调
     * 进入搜索界面使用LabelView中的标签进行关键字搜索
     *
     * @param view
     * @param position
     */
    @Override
    public void onFlowItemClick(View view, int position) {
        Intent labelSearchIntent = new Intent(this, SearchActivity.class);
        labelSearchIntent.putExtra(COME_INTO_SEARCH_ACTIVITY_STATE, COMEIN_WITH_DATA);

        labelSearchIntent.putExtra(COMMIT_DATA_TO_SEARCH_ACTIVITY, ((LabelView) view).getText());
        startActivity(labelSearchIntent);
    }

    /**
     * 点击normal annotation Item回调
     *
     * @param annoView
     * @param position
     */
    @Override
    public void onAnnoItemClick(View annoView, int position) {
        if (m_annoIntent == null)
            m_annoIntent = new Intent(this, DetailAnnoActivity.class);
        m_annoIntent.putExtra(COMMIT_ID_TO_DETAILANNOACTIVITY,
                m_noteInfo.getAnnotations().get(position).getId());
        startActivity(m_annoIntent);
    }

    /**
     * 点击show all item回调
     *
     * @param annoView
     * @param position
     */
    @Override
    public void onShowAllItemClick(View annoView, int position) {
        Intent intent = new Intent(this, AllAnnoActivity.class);
        intent.putExtra(COMMIT_ID_TO_ALLANNOACTIVITY, m_detailInfo.getId());
        startActivity(intent);
    }

    class DetailAppBarOffsetChangeListener extends AppBarOffsetChangeListener {
        @Override
        public void onOffsetIsChanged(AppBarLayout appBarLayout, State state) {
            switch (state) {
                case COLLAPSED:
                    if (m_detailInfo != null)
                        v_appBarTitle.setText(m_detailInfo.getTitle());
                    break;
                case EXPENDED:
                    v_appBarTitle.setText("图书");
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (m_annoAdapter != null)
            m_annoAdapter.recycler();
    }
}
