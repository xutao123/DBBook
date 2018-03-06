package com.xt.dbbook.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.xt.dbbook.R;
import com.xt.dbbook.bean.net.DetailAnnoInfo;
import com.xt.dbbook.network.OkHttpUtil;
import com.xt.dbbook.network.ResponseCallback;
import com.xt.dbbook.network.URLUtil;
import com.xt.dbbook.tools.DialogManager;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

import static com.xt.dbbook.Config.IntentExtraConfig.COMMIT_ID_TO_DETAILANNOACTIVITY;

/**
 * Created by xt on 2018/02/10.
 */

public class DetailAnnoActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar v_toolbar;
    @BindView(R.id.page_text)
    TextView v_pageText;
    @BindView(R.id.cut_off_line)
    ImageView v_cutOffLine;
    @BindView(R.id.chapter_text)
    TextView v_chapterText;
    @BindView(R.id.user_img)
    ImageView v_userImg;
    @BindView(R.id.name)
    TextView v_name;
    @BindView(R.id.time)
    TextView v_time;
    @BindView(R.id.content)
    TextView v_content;

    private DetailAnnoInfo m_detailAnno;

    private static final int GET_ANNO_SUCCESS = 0x100;
    private static final int GET_ANNO_FAILURE = GET_ANNO_SUCCESS + 1;
    private static final int NET_WORK_UNAVAILABLE = GET_ANNO_SUCCESS + 2;
    private static final int ANNO_NULL = GET_ANNO_SUCCESS + 3;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        setContentView(R.layout.activity_detail_anno);
        ButterKnife.bind(this);

        initToolbar();

        Intent intent = getIntent();
        if (intent != null) {
            String id = intent.getStringExtra(COMMIT_ID_TO_DETAILANNOACTIVITY);
            if (!TextUtils.isEmpty(id))
                initData(id);
        }
    }

    private void initToolbar() {
        v_toolbar.setTitle("读书笔记");
        setSupportActionBar(v_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        v_toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }


    private void initView() {
        if (m_detailAnno == null)
            return;

        //设置标题
        if (m_detailAnno.getPage_no() > 0) {
            v_pageText.setText("第" + m_detailAnno.getPage_no() + "页");

            if (!m_detailAnno.getChapter().equals("")) {
                v_cutOffLine.setVisibility(View.VISIBLE);
                v_chapterText.setText(m_detailAnno.getChapter());
            } else {
                v_cutOffLine.setVisibility(View.GONE);
                v_chapterText.setVisibility(View.GONE);
            }
        } else if (!m_detailAnno.getChapter().equals("")) {
            v_pageText.setVisibility(View.GONE);
            v_cutOffLine.setVisibility(View.GONE);
            v_chapterText.setText(m_detailAnno.getChapter());
        } else {
            v_chapterText.setText("全文");
        }

        if (m_detailAnno.getAuthor_user() != null) {
            Glide.with(this).load(m_detailAnno.getAuthor_user().getAvatar())
                    .apply(new RequestOptions().circleCrop().error(R.drawable.me_user))
                    .into(v_userImg);
            v_name.setText(m_detailAnno.getAuthor_user().getName());
        }
        v_time.setText(m_detailAnno.getTime());
        v_content.setText(m_detailAnno.getContent());
    }

    private void initData(String id) {
        if (m_handler == null)
            m_handler = new CommonHandler(this);

        if (m_dialog == null)
            m_dialog = DialogManager.createLoadingDialog(this, null, "请稍后...", true);

        //https://api.douban.com/v2/book/annotation/21816786
        String url = URLUtil.getInstance().addPathNode("book").addPathNode("annotation")
                .addPathNode(id).addFieldsWithoutParam("chapter", "author_user", "content",
                        "page_no", "time", "id", "name", "url", "avatar", "uid").getUrl();

        DialogManager.showDialog(m_dialog);
        OkHttpUtil.getInstance(this).get(url).execute(new ResponseCallback<DetailAnnoInfo>() {
            @Override
            public void onResponse(DetailAnnoInfo info) {
                m_detailAnno = info;
                if (m_detailAnno != null)
                    m_handler.sendEmptyMessage(GET_ANNO_SUCCESS);
                else m_handler.sendEmptyMessage(ANNO_NULL);
            }

            @Override
            public void onFailure(Call call, IOException e) {
                m_handler.sendEmptyMessage(GET_ANNO_FAILURE);
            }

            @Override
            public void onNetWorkUnavaliable() {
                m_handler.sendEmptyMessage(NET_WORK_UNAVAILABLE);
            }
        });
    }

    @Override
    public void handlerActivityMessage(Message msg) {
        super.handlerActivityMessage(msg);
        DialogManager.dismissDialog(m_dialog);
        switch (msg.what) {
            case GET_ANNO_SUCCESS:
                initView();
                break;
            case GET_ANNO_FAILURE:
                Toast.makeText(this, "网络或者服务器错误", Toast.LENGTH_SHORT).show();
                break;
            case NET_WORK_UNAVAILABLE:
                Toast.makeText(this, "请先连接网络", Toast.LENGTH_SHORT).show();
                break;
            case ANNO_NULL:
                Toast.makeText(this, "获取数据为null", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
