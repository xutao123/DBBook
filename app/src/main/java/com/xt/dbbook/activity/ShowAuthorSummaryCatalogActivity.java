package com.xt.dbbook.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.xt.dbbook.R;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.xt.dbbook.Config.IntentExtraConfig.AUTHOR_TYPE;
import static com.xt.dbbook.Config.IntentExtraConfig.CATALOG_TYPE;
import static com.xt.dbbook.Config.IntentExtraConfig.DETAIL_TYPE;
import static com.xt.dbbook.Config.IntentExtraConfig.GET_DATA_KEY;
import static com.xt.dbbook.Config.IntentExtraConfig.SUMMARY_TYPE;

/**
 * Created by xt on 2018/02/02.
 */

public class ShowAuthorSummaryCatalogActivity extends BaseActivity {
    @BindView(R.id.toolbar)
    Toolbar v_toolbar;
    @BindView(R.id.text)
    TextView v_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        setContentView(R.layout.activity_author_summary_catalog);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        String type = intent.getStringExtra(DETAIL_TYPE);
        Bundle bundle = intent.getExtras();

        initView(type, bundle);
    }

    private void initView(String type, Bundle bundle) {
        switch (type) {
            case SUMMARY_TYPE:
                v_toolbar.setTitle("内容简介");
                break;
            case AUTHOR_TYPE:
                v_toolbar.setTitle("作者简介");
                break;
            case CATALOG_TYPE:
                v_toolbar.setTitle("目录");
                break;
        }
        setSupportActionBar(v_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//设置回退图标
        v_toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        v_text.setText(bundle.getString(GET_DATA_KEY));

    }
}
