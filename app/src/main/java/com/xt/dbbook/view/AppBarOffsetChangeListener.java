package com.xt.dbbook.view;

import android.support.design.widget.AppBarLayout;

/**
 * Created by xt on 2018/01/28.
 */

public abstract class AppBarOffsetChangeListener implements AppBarLayout.OnOffsetChangedListener {
    private State m_curState = State.EXPENDED;

    public enum State {
        COLLAPSED,//折叠状态
        EXPENDED,//展开
        IDEL;//滑动
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if (verticalOffset == 0) { //展开
            if (m_curState != State.EXPENDED) {
                m_curState = State.EXPENDED;
                onOffsetIsChanged(appBarLayout, m_curState);
            }
        } else if (Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()) { //折叠
            if (m_curState != State.COLLAPSED) {
                m_curState = State.COLLAPSED;
                onOffsetIsChanged(appBarLayout, m_curState);
            }
        } else {//滑动
            m_curState = State.IDEL;
            onOffsetIsChanged(appBarLayout, m_curState);
        }
    }

    public abstract void onOffsetIsChanged(AppBarLayout appBarLayout, State state);

}
