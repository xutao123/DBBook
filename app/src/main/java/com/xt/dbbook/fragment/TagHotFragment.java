package com.xt.dbbook.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.xt.dbbook.R;
import com.xt.dbbook.adapter.BookListRecyclerAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bingoogolapple.bgabanner.BGABanner;

/**
 * Created by xt on 2018/01/25.
 */

public class TagHotFragment extends TagBaseFragment implements BGABanner.Delegate,
        SwipeRefreshLayout.OnRefreshListener, NestedScrollView.OnScrollChangeListener {

    @BindView(R.id.banner)
    BGABanner v_banner;
    @BindView(R.id.recycler)
    RecyclerView v_recyclerView;
    @BindView(R.id.nested_scroll_view)
    NestedScrollView v_nestedScrollView;
//    @BindView(R.id.refresh_layout)
//    SwipeRefreshLayout v_refreshLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tag_hot, container, false);
        m_unbinder = ButterKnife.bind(this, view);

        m_tag = "畅销";
        initData();
        return view;
    }

    @Override
    protected void initData() {
        super.initData();

        v_banner.setDelegate(this);
        v_banner.setAutoPlayAble(true);
        v_banner.setData(R.drawable.banner1, R.drawable.banner2, R.drawable.banner3);
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
//        v_refreshLayout.setColorSchemeResources(R.color.colorPrimaryDark);
//        v_refreshLayout.setOnRefreshListener(this);

        if (m_adapter == null) {
            m_adapter = new BookListRecyclerAdapter(getActivity(), m_bookInfo);
            m_adapter.setBookListListener(this);
            v_recyclerView.setAdapter(m_adapter);
        }
        v_recyclerView.setLayoutManager(m_layoutManager);

        //设置NestedScrollView滑动监听
        v_nestedScrollView.setOnScrollChangeListener(this);
    }

    @Override
    public void onBannerItemClick(BGABanner banner, View itemView, Object model, int position) {
        Toast.makeText(getActivity(), "哈哈:" + position, Toast.LENGTH_SHORT).show();
    }

    /**
     * 下拉刷新
     */
    @Override
    public void onRefresh() {
        onFragmentRefresh(v_refreshLayout);
    }

    /**
     * v.getChildAt(0).getMeasuredHeight()内部子View的高度（只能有一个子View），包括不显示在屏幕上的高度
     * v.getMeasuredHeight():NestedScrollView显示在屏幕上的高度
     *
     * @param v
     * @param scrollX    滑动之后可视视图左上角在滑动x轴上的位置
     * @param scrollY    滑动之后可视视图左上角在滑动y轴上的位置
     * @param oldScrollX 滑动前x
     * @param oldScrollY 滑动前y
     */
    @Override
    public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
            // 上拉加载更多
            m_handler.sendEmptyMessage(SHOW_LOADING_ITEM);
            getNewData();
        }
    }
}