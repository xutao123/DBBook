package com.xt.dbbook.zxing;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;

import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.xt.dbbook.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by xt on 2018/02/02.
 * 自定义新CaptureActivity替换原来库中的CaptureActivity
 */

public class CaptureActivity extends AppCompatActivity {
    @BindView(R.id.toolbar)
    Toolbar v_toolbar;
    @BindView(R.id.zxing_barcode_scanner)
    DecoratedBarcodeView v_zxingBarcodeScanner;
    private CaptureManager m_captureManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //将原CaptureActivity的布局文件com.google.zxing.client.android.R.layout.zxing_capture
        // 用新布局文件activity_capture替换,可将原文件中的主要布局控件com.journeyapps.barcodescanner.DecoratedBarcodeView
        //copy到activity_capture中，并可加入其它控件
//        setContentView(com.google.zxing.client.android.R.layout.zxing_capture);
        setContentView(R.layout.scaner_activity_capture);
        ButterKnife.bind(this);

        initView();

        //结果解析和返回都是通过这个CaptureManager类
        m_captureManager = new CaptureManager(this, v_zxingBarcodeScanner);
        m_captureManager.initializeFromIntent(getIntent(), savedInstanceState);
        m_captureManager.decode();
    }

    private void initView() {
        v_toolbar.setTitle("扫一扫");
        setSupportActionBar(v_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//设置回退图标
        v_toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        //定义扫描布局
        /**
         * 扫描界面主布局就是通过DecoratedBarcodeView来完成的
         * 其中DecoratedBarcodeView的initialize(AttributeSet attrs)中scannerLayout默认是
         * 通过布局R.layout.zxing_barcode_scanner来完成
         *
         * 可以通过自定义一个zxing_barcode_scanner布局通过"zxing_scanner_layout"布局参数传入代替默认的
         * R.layout.zxing_barcode_scanner布局
         *
         * 其中自定义的布局要包含原布局中的如下三个节点（可以重新设置布局样式）：
         * com.journeyapps.barcodescanner.BarcodeView，
         * com.journeyapps.barcodescanner.ViewfinderView以及一个TextView共同组成
         *
         * 其中com.journeyapps.barcodescanner.ViewfinderView就是中间的扫描框
         * 可以自定义View来对ViewfinderView进行替换，来替换原布局文件中的中的com.journeyapps.barcodescanner.ViewfinderView节点
         *
         *
         * DecoratedBarcodeView本质是一个FrameLayout，在设置布局时要注意，本身使用merge节点
         *
         * 在com.journeyapps.barcodescanner.DecoratedBarcodeView中设置的
         * app:zxing_framing_rect_height="200dp"
         * app:zxing_framing_rect_width="200dp"最终都会传给ViewfinderView，来设置其最终大小
         * 但不能在com.journeyapps.barcodescanner.ViewfinderView节点下对其设置指定大小
         *
         */


    }


    @Override
    protected void onResume() {
        super.onResume();
        m_captureManager.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        m_captureManager.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        m_captureManager.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        m_captureManager.onSaveInstanceState(outState);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        m_captureManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return v_zxingBarcodeScanner.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }
}