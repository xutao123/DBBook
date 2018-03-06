package com.xt.dbbook.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.xt.dbbook.R;
import com.xt.dbbook.app.DBBookManager;
import com.xt.dbbook.fragment.ClassifyFragment;
import com.xt.dbbook.fragment.HomeFragment;
import com.xt.dbbook.fragment.MeFragment;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.ashokvarma.bottomnavigation.BottomNavigationBar.BACKGROUND_STYLE_STATIC;
import static com.ashokvarma.bottomnavigation.BottomNavigationBar.MODE_FIXED;

public class MainActivity extends BaseActivity implements BottomNavigationBar.OnTabSelectedListener {

    @BindView(R.id.bottom_navigation_bar)
    BottomNavigationBar v_bottomNavigationBar;

    private BottomNavigationItem m_homeItem;
    private BottomNavigationItem m_classifyItem;
    private BottomNavigationItem m_meItem;
    public static final int HOME_TAB_HOME = 0;//home
    public static final int HOME_TAB_CLASSIFY = 1;//classify
    public static final int HOME_TAB_ME = 2;//me
    private int m_curTab;
    private FragmentManager m_fragMana;
    private FragmentTransaction m_fragTran;
    private Fragment m_curFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        m_unbinder = ButterKnife.bind(this);
        initNavigationBar();
        initData();
        initView();
    }

    private void initData() {
        m_fragMana = this.getSupportFragmentManager();
    }

    private void initView() {
        onTabSelected(HOME_TAB_HOME);
    }

    private void initNavigationBar() {
        m_homeItem = new BottomNavigationItem(R.drawable.ic_home, getString(R.string.home_bottom_home)).
                setActiveColorResource(R.color.button_back_color);
        m_classifyItem = new BottomNavigationItem(R.drawable.icon_classify, getString(R.string.home_bottom_classify)).
                setActiveColorResource(R.color.button_back_color);
        m_meItem = new BottomNavigationItem(R.drawable.icon_me, getString(R.string.home_bottom_me)).
                setActiveColorResource(R.color.button_back_color);
        v_bottomNavigationBar.clearAll();
        v_bottomNavigationBar.setTabSelectedListener(this);
        v_bottomNavigationBar.setMode(MODE_FIXED);
        v_bottomNavigationBar.setBackgroundStyle(BACKGROUND_STYLE_STATIC);
        v_bottomNavigationBar.setInActiveColor(R.color.inactive_text_color);
        v_bottomNavigationBar.setActiveColor(R.color.button_back_color);
        v_bottomNavigationBar.addItem(m_homeItem)
                .addItem(m_classifyItem).addItem(m_meItem)
                .setFirstSelectedPosition(HOME_TAB_HOME)
                .initialise();
        m_curTab = HOME_TAB_HOME;
    }

    /**
     * 每次replace会把生命周期全部执行一遍
     *
     * @param position
     */
    @Override
    public void onTabSelected(int position) {
        m_curTab = position;
        m_fragTran = m_fragMana.beginTransaction();
        hideFragments(m_fragTran);
        Fragment fragment = m_fragMana.findFragmentByTag(String.valueOf(position));

        if (fragment == null) {
            switch (m_curTab) {
                case HOME_TAB_HOME:
                    m_curFragment = HomeFragment.newInstance();
                    break;
                case HOME_TAB_CLASSIFY:
                    m_curFragment = ClassifyFragment.newInstance();
                    break;
                case HOME_TAB_ME:
                    m_curFragment = MeFragment.newInstance();
                    break;
                default:
                    break;
            }
            //调用replace会销毁前一个显示的Fragment的生命周期
            m_fragTran.add(R.id.frame, m_curFragment, String.valueOf(m_curTab));
        } else {
            m_curFragment = fragment;
            m_fragTran.show(m_curFragment);
        }
        try {
            m_fragTran.commitAllowingStateLoss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTabUnselected(int position) {
    }

    @Override
    public void onTabReselected(int position) {
    }

    private void hideFragments(FragmentTransaction transaction) {
        List<Fragment> fragmentList = m_fragMana.getFragments();
        if (fragmentList != null) {
            for (int i = 0; i < fragmentList.size(); i++) {
                Fragment fragment = fragmentList.get(i);
                if (fragment != null) {
                    transaction.hide(fragment);
                }
            }
        }
    }

    //连按两次退出程序设置
    private static Boolean mIsQuit = false;
    Timer mTimer = new Timer();

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == event.KEYCODE_BACK) {
            if (mIsQuit == false) {
                mIsQuit = true;
                Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        mIsQuit = false;
                    }
                };
                mTimer.schedule(task, 1000);
            } else {
                //退出应用
                DBBookManager.getAppManager().exitApp(true);
            }
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
