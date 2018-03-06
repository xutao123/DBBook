package com.xt.dbbook.tools;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;

import com.afollestad.materialdialogs.MaterialDialog;
import com.xt.dbbook.R;

/**
 * Created by xt on 2018/01/28.
 */

public class DialogManager {

    public static MaterialDialog createLoadingDialog(Context context, String title, String msgContent, boolean
            isCancelable) {
        if (context == null) {
            return null;
        }
        MaterialDialog dialog = new MaterialDialog.Builder(context)
                .content(msgContent)
                .progress(true, 0)
                .cancelable(isCancelable)
                .canceledOnTouchOutside(false)
                .progressIndeterminateStyle(false).build();
        if (!TextUtils.isEmpty(title))
            dialog.setTitle(title);
        return dialog;
    }

    /**
     * 创建一个支持带有输入框Material 样式 dialog,参数inputCallback不能为null,否则EditText不显示
     *
     * @param context
     * @param title                   dialog 标题
     * @param content                 显示的msg
     * @param hint                    默认输入框中的提示语
     * @param prefill                 默认输入框中的显示内容
     * @param limitInput              字数限制，填<=0的数则无限制
     * @param isAutoDissmiss          是否自动关闭对话框
     * @param alwaysCallInputCallback
     * @param inputCallback           输入框的callback,
     * @param singleBtnCallback       按钮的点击响应Callback
     * @return
     */
    public static MaterialDialog createEditDialog(Context context, String title, String content, String hint,
                                                  String prefill, int limitInput, boolean isAutoDissmiss,
                                                  boolean alwaysCallInputCallback, MaterialDialog
                                                          .InputCallback inputCallback,
                                                  MaterialDialog.SingleButtonCallback singleBtnCallback) {

        MaterialDialog.Builder builder = new MaterialDialog.Builder(context)
                .positiveText("确定")
                .positiveColorRes(R.color.button_back_color)
                .negativeText("取消")
                .negativeColorRes(R.color.color_black)
                .autoDismiss(isAutoDissmiss)
                .cancelable(false)
                .widgetColorRes(R.color.button_back_color)
                .input(hint, prefill, false, inputCallback);
        if (alwaysCallInputCallback) {
            builder.alwaysCallInputCallback();
        }
        if (!TextUtils.isEmpty(title)) {
            builder.title(title);
        }
        if (!TextUtils.isEmpty(content)) {
            builder.content(content);
        }
        if (limitInput > 0) {
            builder.inputRange(0, limitInput);
        }
        if (singleBtnCallback != null) {
            builder.onAny(singleBtnCallback);
        }
        return builder.build();
    }

    /**
     * 用于提示
     *
     * @param context
     * @param title
     * @param msgContent
     * @return
     */
    public static MaterialDialog createAlertDialog(Context context, String title, String msgContent) {
        if (context == null) {
            return null;
        }
        MaterialDialog dialog = new MaterialDialog.Builder(context)
                .positiveText("确定")
                .positiveColorRes(R.color.button_back_color)
                .content(msgContent).build();

        if (!TextUtils.isEmpty(title))
            dialog.setTitle(title);
        return dialog;
    }

    /**
     * 创建确定和取消按钮的提示Dialog
     *
     * @param context
     * @param title             显示的title
     * @param msgContent        显示的msg
     * @param positiveText
     * @param negativeText
     * @param isCancelable      是否可以cancel
     * @param singleBtnCallback 按钮的点击响应Callback
     * @return
     */
    public static MaterialDialog createButtonDialog(Context context, String title, String msgContent, String
            positiveText, String negativeText, boolean isCancelable,
                                                    MaterialDialog.SingleButtonCallback singleBtnCallback) {

        MaterialDialog.Builder builder = new MaterialDialog.Builder(context)
                .positiveColorRes(R.color.button_back_color)
                .negativeColorRes(R.color.color_black)
                .cancelable(isCancelable);
        if (!TextUtils.isEmpty(title)) {
            builder.title(title);
        }
        if (!TextUtils.isEmpty(msgContent)) {
            builder.content(msgContent);
        }

        if (!TextUtils.isEmpty(positiveText)) {
            builder.positiveText(positiveText);
        }

        if (!TextUtils.isEmpty(negativeText)) {
            builder.negativeText(negativeText);
        }

        if (singleBtnCallback != null) {
            builder.onAny(singleBtnCallback);
        }
        return builder.build();
    }

    public static void showDialog(Dialog dialog) {
        if (dialog != null) {
            try {
                dialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void dismissDialog(Dialog dialog) {
        if (dialog != null && dialog.isShowing()) {
            try {
                dialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
