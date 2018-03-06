package com.xt.dbbook.fragment;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.xt.dbbook.Config.IntentExtraConfig;
import com.xt.dbbook.R;
import com.xt.dbbook.activity.BookDetailActivity;
import com.xt.dbbook.activity.CollectionActivity;
import com.xt.dbbook.activity.SearchActivity;
import com.xt.dbbook.app.DBBookManager;
import com.xt.dbbook.bean.net.UserInfo;
import com.xt.dbbook.network.OkHttpUtil;
import com.xt.dbbook.network.ResponseCallback;
import com.xt.dbbook.network.URLUtil;
import com.xt.dbbook.tools.DialogManager;
import com.xt.dbbook.tools.TimeUtil;
import com.xt.dbbook.zxing.CaptureActivity;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

/**
 * Created by xt on 2018/01/25.
 */

public class MeFragment extends BaseFragment implements View.OnClickListener {
    @BindView(R.id.head_image)
    ImageView v_headImage;
    @BindView(R.id.name)
    TextView v_name;
    @BindView(R.id.login)
    TextView v_login;
    @BindView(R.id.login_note_layout)
    LinearLayout v_loginNoteLayout;
    @BindView(R.id.search_layout)
    LinearLayout v_searchLayout;
    @BindView(R.id.scan_layout)
    LinearLayout v_scanLayout;
    @BindView(R.id.login_out)
    TextView v_loginOut;
    @BindView(R.id.time)
    TextView v_time;
    @BindView(R.id.name_layout)
    LinearLayout v_nameLayout;
    @BindView(R.id.collect_layout)
    LinearLayout v_collectLayout;

    private View m_searchViewLayout;
    private IntentIntegrator m_intentIntegrator;
    private SendInfoHandler m_handler;
    private UserInfo m_userInfo;
    private MaterialDialog m_loadingDialog;
    private MaterialDialog m_editDialog;
    private MaterialDialog m_alertDialog;
    private MaterialDialog m_buttonDialog;
    private static final int GET_USERINFO_SUCCESS = 0x100;
    private static final int GET_USERINFO_FAILED = GET_USERINFO_SUCCESS + 1;
    private static final int NETWORK_UNAVAILABLE = GET_USERINFO_SUCCESS + 2;

    public static MeFragment newInstance() {
        MeFragment fragment = new MeFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_me, container, false);
        m_unbinder = ButterKnife.bind(this, view);

        initData();
        initView(view);
        return view;
    }

    @Override
    protected void initView(View view) {
        v_login.setOnClickListener(this);
        v_loginNoteLayout.setOnClickListener(this);
        v_searchLayout.setOnClickListener(this);
        v_scanLayout.setOnClickListener(this);
        v_collectLayout.setOnClickListener(this);
        v_loginOut.setOnClickListener(this);

        //共享元素动画，必须是View
        m_searchViewLayout = view.findViewById(R.id.search_layout);
    }

    @Override
    protected void initData() {
        m_handler = new SendInfoHandler(this);

        m_userInfo = DBBookManager.getUserInfo();

        if (m_userInfo != null)
            updataUserInfoView();
    }

    private void updataUserInfoView() {
        Glide.with(getActivity()).load(m_userInfo.getLarge_avatar())
                .apply(new RequestOptions().circleCrop().error(R.drawable.me_user))
                .into(v_headImage);

        v_name.setText(m_userInfo.getName());
        v_time.setText(String.valueOf("这是与您相遇的第" +
                TimeUtil.daysBetween(m_userInfo.getCreated(), System.currentTimeMillis()) + "天"));

        v_nameLayout.setVisibility(View.VISIBLE);
        v_login.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login:
                if (m_editDialog == null)
                    m_editDialog = DialogManager.createEditDialog(getActivity(), "登录",
                            "输入豆瓣用户id进行登录，详见登录说明", "请输入豆瓣id", "", 9, true, false,
                            new MaterialDialog.InputCallback() {
                                @Override
                                public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                                }
                            }, new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    if (which == DialogAction.NEGATIVE) {
                                        return;
                                    }
                                    String uid = m_editDialog.getInputEditText().getText().toString().trim();
                                    login(uid);
                                }
                            });
                DialogManager.showDialog(m_editDialog);
                break;
            case R.id.login_note_layout:
                if (m_alertDialog == null)
                    m_alertDialog = DialogManager.createAlertDialog(getActivity(), "登录说明",
                            "登录需要的用户豆瓣id来源：可从浏览器中登录进入豆瓣，进入个人主页，此时http地址如下，" +
                                    "https://www.douban.com/people/111111111/，其中最后的111111111就是登录所需的豆瓣id");
                DialogManager.showDialog(m_alertDialog);
                break;
            case R.id.search_layout:
                search();
                break;
            case R.id.scan_layout:
                scan();
                break;
            case R.id.collect_layout:
                if (m_userInfo == null) {
                    Toast.makeText(getActivity(), "请先登录", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(getActivity(), CollectionActivity.class);
                startActivity(intent);
                break;
            case R.id.login_out:
                if (m_userInfo == null) {
                    Toast.makeText(getActivity(), "未登录", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (m_buttonDialog == null)
                    m_buttonDialog = DialogManager.createButtonDialog(getActivity(), "退出登录",
                            "是否确定退出", "确定", "取消", true, new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    if (which == DialogAction.NEGATIVE)
                                        return;
                                    m_userInfo = null;
                                    DBBookManager.deleteUserInfo();
                                    Glide.with(getActivity()).load(R.drawable.me_user).into(v_headImage);
                                    v_nameLayout.setVisibility(View.GONE);
                                    v_login.setVisibility(View.VISIBLE);
                                }
                            });
                DialogManager.showDialog(m_buttonDialog);
                break;
        }
    }

    //搜一搜
    private void search() {
        Intent intent = new Intent(getActivity(), SearchActivity.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(
                    getActivity(), Pair.create(m_searchViewLayout, "SearchViewLayout")
            ).toBundle());
        else startActivity(intent);
    }

    //扫一扫
    private void scan() {
        // 创建IntentIntegrator对象
        if (m_intentIntegrator == null) {
            m_intentIntegrator = IntentIntegrator.forSupportFragment(this);
            m_intentIntegrator.setCaptureActivity(CaptureActivity.class);
        }
        // 开始扫描
        m_intentIntegrator.initiateScan();
    }

    //登录
    private void login(String uid) {
        //GET https://api.douban.com/v2/user/:name
        String url = URLUtil.getInstance().addPathNode("user")
                .addPathNode(uid).getUrl();
        if (m_loadingDialog == null)
            m_loadingDialog = DialogManager.createLoadingDialog(getActivity(), null, "请稍后...", true);
        DialogManager.showDialog(m_loadingDialog);
        OkHttpUtil.getInstance(getActivity()).get(url).execute(new ResponseCallback<UserInfo>() {
            @Override
            public void onResponse(UserInfo info) {
                m_userInfo = info;
                m_handler.sendEmptyMessage(GET_USERINFO_SUCCESS);
            }

            @Override
            public void onFailure(Call call, IOException e) {
                m_handler.sendEmptyMessage(GET_USERINFO_FAILED);
            }

            @Override
            public void onNetWorkUnavaliable() {
                m_handler.sendEmptyMessage(NETWORK_UNAVAILABLE);
            }
        });
    }

    /**
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() != null) {
                String resultStr = result.getContents();
                if (resultStr.length() != 10 && resultStr.length() != 13)
                    return;
                Pattern pattern = Pattern.compile("[0-9]*");
                Matcher isNum = pattern.matcher(resultStr);
                if (!isNum.matches()) {
                    return;
                }

                Intent intent = new Intent(getActivity(), BookDetailActivity.class);
                intent.putExtra(IntentExtraConfig.BOOK_ISBN, resultStr);

                startActivity(intent);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void handleFragmentMessage(Message msg, BaseFragment fragment) {
        super.handleFragmentMessage(msg, fragment);

        switch (msg.what) {
            case GET_USERINFO_SUCCESS:
                if (m_userInfo != null) {
                    updataUserInfoView();
                    DBBookManager.getAppManager().saveUserInfo(m_userInfo);
                } else {
                    Toast.makeText(getActivity(), "用户不存在", Toast.LENGTH_SHORT).show();
                }
                DialogManager.dismissDialog(m_loadingDialog);
                break;
            case GET_USERINFO_FAILED:
                DialogManager.dismissDialog(m_loadingDialog);
                Toast.makeText(getActivity(), "获取用户信息失败,请检查id", Toast.LENGTH_SHORT).show();
                break;
            case NETWORK_UNAVAILABLE:
                DialogManager.dismissDialog(m_loadingDialog);
                Toast.makeText(getActivity(), "请先连接网络", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
