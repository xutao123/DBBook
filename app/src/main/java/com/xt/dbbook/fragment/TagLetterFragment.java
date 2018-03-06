package com.xt.dbbook.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xt.dbbook.R;
import com.xt.dbbook.adapter.BookListRecyclerAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by xt on 2018/01/25.
 */

public class TagLetterFragment extends TagBaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.recycler)
    RecyclerView v_recyclerView;
//    @BindView(R.id.refresh_layout)
//    SwipeRefreshLayout v_refreshLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tag_letter, container, false);
        m_unbinder = ButterKnife.bind(this, view);

        m_tag = "文学";
        initData();
        return view;
    }

    @Override
    protected void initData() {
        super.initData();
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
//        v_refreshLayout.setColorSchemeResources(R.color.colorPrimaryDark);
//        v_refreshLayout.setOnRefreshListener(this);

        if (m_adapter == null) {
            m_adapter = new BookListRecyclerAdapter(getActivity(), m_bookInfo, m_layoutManager);
            m_adapter.setBookListListener(this);
            v_recyclerView.setAdapter(m_adapter);
        }
        v_recyclerView.setLayoutManager(m_layoutManager);
        v_recyclerView.addOnScrollListener(m_adapter.getRecyclerViewScrollListener());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onRefresh() {
        onFragmentRefresh(v_refreshLayout);
    }
}