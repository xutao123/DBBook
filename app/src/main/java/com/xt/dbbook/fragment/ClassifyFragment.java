package com.xt.dbbook.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xt.dbbook.R;

import butterknife.ButterKnife;

/**
 * Created by xt on 2018/01/25.
 */

public class ClassifyFragment extends BaseFragment {
    public static ClassifyFragment newInstance() {
        ClassifyFragment fragment = new ClassifyFragment();
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_classify, container, false);
        m_unbinder = ButterKnife.bind(this, view);

        initData();
        initView(view);
        return view;
    }

    @Override
    protected void initView(View view) {
    }

    @Override
    protected void initData() {

    }
}
