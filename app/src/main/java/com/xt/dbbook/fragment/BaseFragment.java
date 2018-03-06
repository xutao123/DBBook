package com.xt.dbbook.fragment;

import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.xt.dbbook.activity.BaseActivity;
import com.xt.dbbook.tools.DialogManager;

import java.lang.ref.WeakReference;

import butterknife.Unbinder;

/**
 * Created by xt on 2018/01/25.
 */

public abstract class BaseFragment extends Fragment {
    Unbinder m_unbinder;

    protected abstract void initView(View view);

    protected abstract void initData();

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (m_unbinder != null)
            m_unbinder.unbind();
    }

    protected static class SendInfoHandler extends Handler {
        private WeakReference<BaseFragment> reference;

        public SendInfoHandler(BaseFragment fragment) {
            reference = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            BaseFragment fragment = reference.get();
            if (fragment != null && !fragment.isDetached())
                fragment.handleFragmentMessage(msg,fragment);
        }
    }

    public void handleFragmentMessage(Message msg,BaseFragment fragment) {
    }

}
