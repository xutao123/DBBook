package com.xt.dbbook.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

/**
 * Created by xt on 2018/01/25.
 */

public class FragmentTabAdapter extends FragmentStatePagerAdapter {
    private Class<?>[] m_fragmentClass;
    private ArrayList<String> m_tags;
    private ArrayList<Fragment> m_fragmentList;

    public FragmentTabAdapter(FragmentManager fm) {
        super(fm);
    }

    public FragmentTabAdapter(FragmentManager fm, Class<?>[] fragmentClass, ArrayList<String> tags) {
        super(fm);
        m_fragmentClass = fragmentClass;
        m_tags = tags;
        initFragment();
    }

    private void initFragment() {
        if (m_fragmentClass == null || m_fragmentClass.length <= 0)
            return;

        if (m_fragmentList == null)
            m_fragmentList = new ArrayList<>();
        else m_fragmentList.clear();

        for (int i = 0; i < m_fragmentClass.length; i++) {
            Class c = m_fragmentClass[i];
            Fragment fragment = null;
            try {
                fragment = (Fragment) c.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            if (fragment == null)
                continue;
            m_fragmentList.add(fragment);
        }
    }

    @Override
    public Fragment getItem(int position) {
        if (m_fragmentList != null) {
            return m_fragmentList.get(position);
        }
        return null;
    }

    @Override
    public int getCount() {
        return m_fragmentClass.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (m_tags == null)
            return "";
        return m_tags.get(position);
    }
}
