package com.xt.dbbook.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.afollestad.materialdialogs.MaterialDialog;
import com.xt.dbbook.app.DBBookManager;
import com.xt.dbbook.tools.DialogManager;

import java.lang.ref.WeakReference;

import butterknife.Unbinder;

/**
 * Created by xt on 2018/01/25.
 */

public abstract class BaseActivity extends AppCompatActivity {
    CommonHandler m_handler;
    Unbinder m_unbinder;
    MaterialDialog m_dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //将当前的Activity实例加入到DBBookManager的ActivityStack中
        DBBookManager.getAppManager().addActivity(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (m_unbinder != null)
            m_unbinder.unbind();
        if (m_handler != null)
            m_handler.removeCallbacksAndMessages(null);

        hideInputKeyBroad();

        DBBookManager.getAppManager().removeActivity(this);

        if (m_dialog != null)
            DialogManager.dismissDialog(m_dialog);
    }

    public static class CommonHandler extends Handler {
        WeakReference<BaseActivity> wReferences = null;

        public CommonHandler(BaseActivity activity) {
            wReferences = new WeakReference(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            BaseActivity activity = wReferences.get();
            if (activity != null && !activity.isFinishing())
                activity.handlerActivityMessage(msg);
        }
    }

    public void handlerActivityMessage(Message msg) {
    }

    /**
     * 隐藏软键盘
     * 注意：Activity的anroid:windowSoftInputMode属性不要设置
     * stateVisible、stateAlwaysVisible 、 stateUnchanged 、stateHiddn 、 stateAlwaysHidden
     * 否则无法隐藏
     */
    public void hideInputKeyBroad() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        // 得到InputMethodManager的实例
        if (imm.isActive()) {
            // 如果开启
            try {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
