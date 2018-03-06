package com.xt.dbbook.fragment;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.xt.dbbook.R;
import com.xt.dbbook.activity.SearchActivity;
import com.xt.dbbook.adapter.FragmentTabAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by xt on 2018/01/25.
 */

public class HomeFragment extends BaseFragment implements TabLayout.OnTabSelectedListener,
        View.OnClickListener {

    @BindView(R.id.tab_layout)
    TabLayout v_tabLayout;
    @BindView(R.id.view_pager)
    ViewPager v_viewPager;
    @BindView(R.id.search_view)
    LinearLayout v_searchView;

    private View m_searchViewLayout;

    private FragmentTabAdapter m_tabAdapter;
    private ArrayList<String> tags = new ArrayList<>();
    private Class[] m_classArr;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        m_unbinder = ButterKnife.bind(this, view);

        //共享元素动画，必须是View
        m_searchViewLayout = view.findViewById(R.id.search_view);

        initData();
        initView(view);
        return view;
    }

    protected void initData() {
        tags.add("畅销");
        tags.add("文学");
        tags.add("生活");
        m_classArr = new Class[]{TagHotFragment.class,
                TagLetterFragment.class, TagLifeFragment.class};
    }

    protected void initView(View view) {
        v_searchView.setOnClickListener(this);

        /**
         * 使用getFragmentManager() HomeFragment被隐藏再显示时ViewPager中的Fragment不显示
         */
//        m_tabAdapter = new FragmentTabAdapter(getFragmentManager(), m_classArr, tags);
        m_tabAdapter = new FragmentTabAdapter(getChildFragmentManager(), m_classArr, tags);
        v_viewPager.setAdapter(m_tabAdapter);
        v_viewPager.setOffscreenPageLimit(tags.size() - 1); //缓存3页
        v_tabLayout.setupWithViewPager(v_viewPager);
        v_tabLayout.setTabMode(TabLayout.MODE_FIXED);
        //设置下划线高度，宽度跟随tab的宽度
        v_tabLayout.setSelectedTabIndicatorHeight(6);
        v_tabLayout.setOnTabSelectedListener(this);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        v_viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_view:
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                    startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(
                            getActivity(), android.util.Pair.create(m_searchViewLayout, "SearchViewLayout")
                    ).toBundle());
                else startActivity(intent);
                break;
        }
    }
}
