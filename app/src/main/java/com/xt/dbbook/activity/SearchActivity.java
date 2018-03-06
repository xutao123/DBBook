package com.xt.dbbook.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.xt.dbbook.Config.EnvInfo;
import com.xt.dbbook.Config.IntentExtraConfig;
import com.xt.dbbook.R;
import com.xt.dbbook.adapter.BookListRecyclerAdapter;
import com.xt.dbbook.bean.file.HistroySearchData;
import com.xt.dbbook.bean.net.BookPartInfo;
import com.xt.dbbook.bean.net.ISBNSearchData;
import com.xt.dbbook.network.OkHttpUtil;
import com.xt.dbbook.network.ResponseCallback;
import com.xt.dbbook.network.URLUtil;
import com.xt.dbbook.tools.DialogManager;
import com.xt.dbbook.tools.FileUtil;
import com.xt.dbbook.tools.GsonUtil;
import com.xt.dbbook.view.FlowLabelLayout;
import com.xt.dbbook.view.LabelView;
import com.xt.dbbook.view.SearchView;
import com.xt.dbbook.zxing.CaptureActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

import static com.xt.dbbook.Config.IntentExtraConfig.COMEIN_WITHOUT_DATA;
import static com.xt.dbbook.Config.IntentExtraConfig.COME_INTO_SEARCH_ACTIVITY_STATE;
import static com.xt.dbbook.Config.IntentExtraConfig.COMMIT_DATA_TO_SEARCH_ACTIVITY;
import static com.xt.dbbook.adapter.BookListRecyclerAdapter.CurShowType.SHOW_NORMAL;

/**
 * Created by xt on 2018/01/27.
 */

public class SearchActivity extends BaseActivity implements FlowLabelLayout.FlowItemClickListener,
        TextView.OnEditorActionListener, SearchView.TextChangedListener, SearchView.ClickListener,
        View.OnClickListener, BookListRecyclerAdapter.OnBookListListener {

    @BindView(R.id.flow_layout)
    FlowLabelLayout v_flowLayout;
    @BindView(R.id.search_view)
    SearchView v_searchView;
    @BindView(R.id.histroy_layout)
    LinearLayout v_histroyLayout;
    @BindView(R.id.cancel)
    TextView v_cancel;
    @BindView(R.id.recycler_view)
    RecyclerView v_resultView;
    @BindView(R.id.clear_histroy)
    TextView v_clearHistroy;

    private MaterialDialog m_analyzeDialog;
    private HistroySearchData m_searchHistroyData;
    private static final int NETWORK_SUCCESS = 0x100;
    private static final int UPDATA_BOOKINFO = NETWORK_SUCCESS + 1;
    private static final int NETWORK_ERROR = NETWORK_SUCCESS + 2;
    private static final int NETWORK_UNAVAILABLE = NETWORK_SUCCESS + 3;
    private BookPartInfo m_bookInfoList; //根据关键字得到的图书信息列表
    private BookListRecyclerAdapter m_adapter;
    private LinearLayoutManager m_layoutManager;
    private Intent m_intent;
    private IntentIntegrator m_intentIntegrator;

    private int m_start = 0;//此次搜索开始数目
    private int m_total;//所有条目数目
    private int m_count = 20;//一次返回的数目
    private int m_returnTotal = 0;//已经返回的数目

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        setContentView(R.layout.activity_search);

        m_unbinder = ButterKnife.bind(this);
        v_flowLayout.setOnFlowItemClickListener(this);

        Intent intent = getIntent();

        initData();
        initView(intent);
    }

    /**
     * 由于设置这个Activity的LaunchMode为SingleTask，所以如果之前有该Activity实例，
     * onNewIntent
     *
     * @param intent
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        initData();
        initView(intent);
    }

    private void initData() {
        if (m_handler == null)
            m_handler = new CommonHandler(this);
        //找出所有用户的搜索记录
        analyzeHistoryJson();
    }

    private void initView(Intent intent) {
        v_cancel.setOnClickListener(this);
        v_clearHistroy.setOnClickListener(this);

        v_searchView.setEditorActionListener(this);
        v_searchView.setTextChangedListener(this);
        v_searchView.setScanClickListener(this);

        //默认没有携带关键字
        if (intent.getIntExtra(COME_INTO_SEARCH_ACTIVITY_STATE, COMEIN_WITHOUT_DATA) ==
                COMEIN_WITHOUT_DATA) {
            //没有携带搜索关键字时，显示搜索历史
            initLabelLayout();
        } else {
            //说明携带了搜索用的关键字
            if (TextUtils.isEmpty(intent.getStringExtra(COMMIT_DATA_TO_SEARCH_ACTIVITY)))
                initLabelLayout();
            else {
                initFlowLabelLayoutWithData(intent.getStringExtra(COMMIT_DATA_TO_SEARCH_ACTIVITY));
                //按照data搜索
                if (m_analyzeDialog == null)
                    m_analyzeDialog = DialogManager.createLoadingDialog(this, null, "请稍后...", true);
                DialogManager.showDialog(m_analyzeDialog);
                searchBookList(intent.getStringExtra(COMMIT_DATA_TO_SEARCH_ACTIVITY));
            }
        }

    }

    /**
     * 搜索图书
     * 得到数据之后，隐藏HistroyView：v_histroyLayout
     * 显示v_stubView即RecyclerView
     *
     * @param bookName
     */
    private void searchBookList(String bookName) {
        //重置
        m_returnTotal = 0;
        m_start = 0;
        m_total = 0;

        final String url = URLUtil.getInstance().addPathNode("book").addPathNode("search")
                .addFirstStringParams("q", bookName).addIntParams("start", m_start)
                .addIntParams("count", m_count)
                .addFields("start", "total", "count", "author", "image",
                        "id", "publisher", "title", "pubdate", "rating").getUrl();

        OkHttpUtil.getInstance(this).get(url).execute(new ResponseCallback<BookPartInfo>() {
            @Override
            public void onResponse(BookPartInfo info) {
                m_bookInfoList = info;
                if (m_count < info.getTotal())
                    m_start = m_count;
                else m_start = info.getTotal();
                m_returnTotal = m_start;
                m_total = info.getTotal();
                m_handler.sendEmptyMessage(NETWORK_SUCCESS);
            }

            @Override
            public void onFailure(Call call, IOException e) {
                m_handler.sendEmptyMessage(NETWORK_ERROR);
            }

            @Override
            public void onNetWorkUnavaliable() {
                m_handler.sendEmptyMessage(NETWORK_UNAVAILABLE);
            }
        });
    }

    /**
     * 解析本地存储的搜索记录
     */
    private void analyzeHistoryJson() {
        DialogManager.showDialog(m_analyzeDialog);
        String jsonPath = EnvInfo.getAppJsonDirPath() + FileUtil.SEARCH_HISTROY_JSON;

        m_searchHistroyData = convertJsonArray2HistroySearchData(jsonPath);
    }

    private HistroySearchData convertJsonArray2HistroySearchData(String path) {
        if (TextUtils.isEmpty(path))
            return null;

        String jsonStr = FileUtil.readFile2String(path);
        return GsonUtil.gsonBuild().fromJson(jsonStr, HistroySearchData.class);
    }

    //保存修改历史记录
    private void saveSearchData() {
        if (m_searchHistroyData == null)
            return;

        JSONArray ja = new JSONArray();
        for (String str : m_searchHistroyData.getDataList()) {
            ja.put(str);
        }

        if (!TextUtils.isEmpty(ja.toString())) {
            JSONObject jo = new JSONObject();
            try {
                jo.put("dataList", ja);
                FileUtil.saveJsonToFile(jo.toString(), EnvInfo.getAppJsonDirPath(), FileUtil.SEARCH_HISTROY_JSON);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    //展示搜索结果
    private void showSearchResult() {
        if (m_layoutManager == null)
            m_layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        if (m_adapter == null) {
            m_adapter = new BookListRecyclerAdapter(this, m_bookInfoList, m_layoutManager,
                    true, v_searchView.getEditTextString(),
                    new ForegroundColorSpan(Color.parseColor("#CD661D")),
                    new StyleSpan(Typeface.BOLD));
            m_adapter.setBookListListener(this);
        } else {
            m_adapter.replaceDataAndStyle(m_bookInfoList,
                    v_searchView.getEditTextString(), null, null);
        }
        v_resultView.setAdapter(m_adapter);
        v_resultView.addOnScrollListener(m_adapter.getRecyclerViewScrollListener());
        v_resultView.setLayoutManager(m_layoutManager);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //保存修改本地记录
        saveSearchData();
    }

    private LabelView createLabelView(String label) {
        LabelView lv = (LabelView) LayoutInflater.from(this).inflate(R.layout.view_label, null);
        lv.setText(label);
        return lv;
    }

    /**
     * 先将这个view从v_flowLayout中删除
     * 将HistroySearchData中的Set集合更新
     * 在将这个view加入到v_flowLayout中
     * 最后搜索
     *
     * @param view
     * @param position
     */
    @Override
    public void onFlowItemClick(View view, int position) {
        //通过((LabelView) view).getText()来搜索
        //更新搜索记录顺序
        String data = ((LabelView) view).getText().trim();
        updateFlowLabelLayout(data);
        //EditText设置内容
        v_searchView.setEditText(data);
        //按照data搜索
        if (m_analyzeDialog == null)
            m_analyzeDialog = DialogManager.createLoadingDialog(this, null, "请稍后...", true);
        DialogManager.showDialog(m_analyzeDialog);
        searchBookList(data);
    }

    /**
     * 带有搜索关键字进入页面时,
     * <p>
     * 设置EditText中的text
     *
     * @param searchData
     */
    private void initFlowLabelLayoutWithData(String searchData) {
        initLabelLayout(); //init
        updateFlowLabelLayout(searchData); //update

        v_searchView.setEditText(searchData);
    }

    /**
     * 第一次创建，所有view都是第一次加入
     */
    public void initLabelLayout() {
        v_flowLayout.removeAllViews();
        if (m_searchHistroyData != null && m_searchHistroyData.getDataList() != null) {
            Set<String> data = m_searchHistroyData.getDataList();
            for (String str : data)
                v_flowLayout.addLabelView(createLabelView(str));
        }
    }

    /**
     * 点击search按钮，更新搜索数据
     * <p>
     * 当searchData已经在历史记录中时，需要先删除HistroySearchData中的data数据
     * 删除FlowLabelLayout中的这个data对应的View
     * 最后再将这个View重新添加进FlowLabelLayout中
     * <p>
     * 否则直接添加
     *
     * @param searchData
     */
    private void updateFlowLabelLayout(String searchData) {
        //保存记录
        if (m_searchHistroyData == null)
            m_searchHistroyData = new HistroySearchData();
        if (!TextUtils.isEmpty(searchData)) {
            if (m_searchHistroyData.hasData(searchData))
                v_flowLayout.removeViewByText(searchData);

            m_searchHistroyData.updataSearchData(searchData);
            v_flowLayout.addLabelView(createLabelView(searchData));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel:
                this.finish();
                break;
            case R.id.clear_histroy:
                if (m_searchHistroyData != null) {
                    if (m_searchHistroyData.getDataList() != null
                            && m_searchHistroyData.getDataList().size() > 0)
                        v_flowLayout.removeAllViews();
                    m_searchHistroyData.clearAll();
                }
                break;
        }
    }

    /**
     * 点击RecyclerView Item回调
     *
     * @param position
     * @param bookId
     */
    @Override
    public void onChooseBook(int position, String bookId) {
        if (m_intent == null)
            m_intent = new Intent(this, BookDetailActivity.class);
        m_intent.putExtra(IntentExtraConfig.BOOK_ID, bookId);
        startActivity(m_intent);
    }

    /**
     * 上拉刷新
     */
    @Override
    public void getNewData() {
        if (m_returnTotal == m_total) {
            m_adapter.updataCurShowType(SHOW_NORMAL);
            return;
        }

        //获得数据
        String bookName = v_searchView.getEditTextString();
        final String url = URLUtil.getInstance().addPathNode("book").addPathNode("search")
                .addFirstStringParams("q", bookName).addIntParams("start", m_start)
                .addIntParams("count", m_count)
                .addFields("start", "total", "count", "author", "image",
                        "id", "publisher", "title", "pubdate", "rating").getUrl();

        OkHttpUtil.getInstance(this).get(url).execute(new ResponseCallback<BookPartInfo>() {
            @Override
            public void onResponse(BookPartInfo info) {
                m_bookInfoList = info;
                if (m_start + m_count < info.getTotal())
                    m_returnTotal += info.getCount();
                else m_returnTotal = info.getTotal();
                m_start = m_returnTotal;
                m_total = info.getTotal();
                m_handler.sendEmptyMessage(UPDATA_BOOKINFO);
            }

            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onNetWorkUnavaliable() {
                m_handler.sendEmptyMessage(NETWORK_UNAVAILABLE);
            }
        });
    }

    /**
     * 按下搜索按钮时的回调
     *
     * @param v
     * @param actionId
     * @param event
     * @return
     */
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) { //点击键盘上的搜索按钮
            hideInputKeyBroad();
            String bookName = v_searchView.getEditTextString();
            if (TextUtils.isEmpty(bookName)) {
                Toast.makeText(this, "搜索名称不能为空", Toast.LENGTH_SHORT).show();
                return true;
            }

            if (m_analyzeDialog == null)
                m_analyzeDialog = DialogManager.createLoadingDialog(this, null, "请稍后...", true);
            DialogManager.showDialog(m_analyzeDialog);

            //搜索
            searchBookList(bookName);
            //更新搜索历史
            updateFlowLabelLayout(bookName);
            return true;
        }
        return false;
    }

    /**
     * EditText中的内容改变时回调
     */
    @Override
    public void onTextChanged() {
        String bookName = v_searchView.getEditTextString();
        if (!TextUtils.isEmpty(bookName))
            searchBookList(bookName);
    }

    public void visibleRecyclerView() {
        v_histroyLayout.setVisibility(View.GONE);
        v_resultView.setVisibility(View.VISIBLE);
    }

    public void visibleHistroyView() {
        v_resultView.setVisibility(View.GONE);
        v_histroyLayout.setVisibility(View.VISIBLE);
    }

    /**
     * 点击删除按钮时的回调
     */
    @Override
    public void onDeleteClick() {
        visibleHistroyView();
    }

    /**
     * 点击扫描按钮时的回调
     */
    @Override
    public void onScanClick() {
        // 创建IntentIntegrator对象
        if (m_intentIntegrator == null) {
            m_intentIntegrator = new IntentIntegrator(SearchActivity.this);
            m_intentIntegrator.setCaptureActivity(CaptureActivity.class);
        }
        // 开始扫描
        m_intentIntegrator.initiateScan();
    }

    @Override
    public void handlerActivityMessage(Message msg) {
        super.handlerActivityMessage(msg);
        switch (msg.what) {
            case NETWORK_SUCCESS: //查找成功
                visibleRecyclerView();
                showSearchResult();
                DialogManager.dismissDialog(m_analyzeDialog);
                break;
            case NETWORK_ERROR:
                Toast.makeText(this, "网络错误", Toast.LENGTH_SHORT).show();
                DialogManager.dismissDialog(m_analyzeDialog);
                break;
            case UPDATA_BOOKINFO: //获得更新数据成功
                m_adapter.updataData(m_bookInfoList);
                break;
            case NETWORK_UNAVAILABLE:
                DialogManager.dismissDialog(m_analyzeDialog);
                Toast.makeText(this, "请先连接网络", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    /**
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() != null) {
                String resultStr = result.getContents();
                if (resultStr.length() != 10 && resultStr.length() != 13)
                    return;
                Pattern pattern = Pattern.compile("[0-9]*");
                Matcher isNum = pattern.matcher(resultStr);
                if (!isNum.matches()) {
                    return;
                }

                if (m_intent == null)
                    m_intent = new Intent(this, BookDetailActivity.class);
                m_intent.putExtra(IntentExtraConfig.BOOK_ISBN, resultStr);
                startActivity(m_intent);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}
