package com.xt.dbbook.view;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xt.dbbook.R;

/**
 * Created by xt on 2018/01/24.
 */

public class SearchView extends RelativeLayout implements TextWatcher, View.OnClickListener {
    public EditText v_editText;
    /**
     * scan和delete只有一个能可见
     */
    public ImageView v_delete;
    public ImageView v_scan;
    private TextView.OnEditorActionListener m_editorActionListener;
    private TextChangedListener m_textChangedListener;
    private ClickListener m_clickListener;

    public SearchView(Context context) {
        super(context);
    }

    public SearchView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        //加载组合控件布局
        LayoutInflater.from(context).inflate(R.layout.view_search, this);
        v_editText = (EditText) findViewById(R.id.search_edit);
        v_delete = (ImageView) findViewById(R.id.search_delete);
        v_scan = (ImageView) findViewById(R.id.search_scan);
        //监听EditText中的内容变化
        v_editText.addTextChangedListener(this);
        v_delete.setOnClickListener(this);
        v_scan.setOnClickListener(this);
    }

    public SearchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    public void setEditorActionListener(TextView.OnEditorActionListener l) {
        m_editorActionListener = l;
        v_editText.setOnEditorActionListener(m_editorActionListener);
    }

    //获得EditText中的内容
    public String getEditTextString() {
        return v_editText.getText().toString().trim();
    }

    public void destroySelf() {
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.length() <= 0)
            visibleScan();
        else {
            visibleDelete();
            if (m_textChangedListener != null)
                m_textChangedListener.onTextChanged();
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    //仅仅响应delete Image onclick事件
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_delete:
                v_editText.setText("");
                if (m_clickListener != null)
                    m_clickListener.onDeleteClick();
                break;
            case R.id.search_scan:
                if (m_clickListener != null)
                    m_clickListener.onScanClick();
                break;
        }
    }


    //可见delete按钮
    public void visibleDelete() {
        v_delete.setVisibility(View.VISIBLE);
        v_scan.setVisibility(View.GONE);
    }

    //可见Scan按钮
    public void visibleScan() {
        v_scan.setVisibility(View.VISIBLE);
        v_delete.setVisibility(View.GONE);
    }

    public void setEditText(String data) {
        if (!TextUtils.isEmpty(data)) {
            v_editText.setText(data);
            //定位光标
            v_editText.setSelection(data.length());
        }
    }

    public interface TextChangedListener {
        void onTextChanged();
    }

    public void setTextChangedListener(TextChangedListener l) {
        m_textChangedListener = l;
    }

    public interface ClickListener {
        void onScanClick();

        void onDeleteClick();
    }

    public void setScanClickListener(ClickListener l) {
        m_clickListener = l;
    }

}
