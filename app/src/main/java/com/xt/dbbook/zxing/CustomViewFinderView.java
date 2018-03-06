package com.xt.dbbook.zxing;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.TypedValue;

import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.ViewfinderView;

import java.util.ArrayList;
import java.util.List;

import static android.graphics.Shader.TileMode.CLAMP;
import static android.util.TypedValue.COMPLEX_UNIT_DIP;

/**
 * Created by xt on 2018/02/02.
 */

public class CustomViewFinderView extends ViewfinderView {
    public CustomViewFinderView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    //定义边角线的宽度为2dp，转为pixel
    private int m_cornerLineWidth = (int) TypedValue.applyDimension(
            COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics());
    //定义边角线的长度占总长度的比例
    private float m_cornerLineRate = 0.1f;

    //使用LinearGradient线性渐变给paint设置渐变色
    private LinearGradient m_linearGradient;

    //设置扫描线上各个点位置及其颜色，用来创建LinearGradient
    private int[] m_scanLineColors = new int[]{0x00FFFFFF, Color.WHITE, 0x00FFFFFF};
    private float[] m_colorPos = new float[]{0.0f, 0.5f, 1.0f};

    //扫描线每次重绘的移动距离
    private float m_scanLineMoveLen = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, getResources().getDisplayMetrics());

    //定义扫描线位置（垂直方向）
    private float m_scanLineVerPos = 0;

    //重绘时间间隔
    public static final long CUSTOME_ANIMATION_DELAY = 20;

    @Override
    public void onDraw(Canvas canvas) {
        //不用super,完全替换之前的绘制方式
        refreshSizes();
        if (framingRect == null || previewFramingRect == null) {
            return;
        }

        Rect frame = framingRect; //中间扫描框所在矩阵
        Rect previewFrame = previewFramingRect;

        int width = canvas.getWidth();
        int height = canvas.getHeight();

        //绘制边角
        paint.setColor(Color.WHITE);//边角颜色
        paint.setStrokeWidth(m_cornerLineWidth);

        //上边两条线
        canvas.drawLine(frame.left, frame.top + m_cornerLineWidth / 2,
                frame.left + frame.width() * m_cornerLineRate, frame.top + m_cornerLineWidth / 2, paint);
        canvas.drawLine(frame.right - frame.width() * m_cornerLineRate, frame.top + m_cornerLineWidth / 2,
                frame.right, frame.top + m_cornerLineWidth / 2, paint);
        //上边左右两条线
        canvas.drawLine(frame.left + m_cornerLineWidth / 2, frame.top,
                frame.left + m_cornerLineWidth / 2, frame.top + frame.height() * m_cornerLineRate, paint);
        canvas.drawLine(frame.right - m_cornerLineWidth / 2, frame.top,
                frame.right - m_cornerLineWidth / 2, frame.top + frame.height() * m_cornerLineRate, paint);
        //下边左右两条线
        canvas.drawLine(frame.left + m_cornerLineWidth / 2, frame.bottom - frame.height() * m_cornerLineRate,
                frame.left + m_cornerLineWidth / 2, frame.bottom, paint);
        canvas.drawLine(frame.right - m_cornerLineWidth / 2, frame.bottom - frame.height() * m_cornerLineRate,
                frame.right - m_cornerLineWidth / 2, frame.bottom, paint);
        //下边两条线
        canvas.drawLine(frame.left, frame.bottom - m_cornerLineWidth / 2,
                frame.left + frame.width() * m_cornerLineRate, frame.bottom - m_cornerLineWidth / 2, paint);
        canvas.drawLine(frame.right - frame.width() * m_cornerLineRate, frame.bottom - m_cornerLineWidth / 2,
                frame.right, frame.bottom - m_cornerLineWidth / 2, paint);

        // Draw the exterior (i.e. outside the framing rect) darkened
        paint.setColor(resultBitmap != null ? resultColor : maskColor);
        canvas.drawRect(0, 0, width, frame.top, paint);
        canvas.drawRect(0, frame.top, frame.left, frame.bottom + 1, paint);
        canvas.drawRect(frame.right + 1, frame.top, width, frame.bottom + 1, paint);
        canvas.drawRect(0, frame.bottom + 1, width, height, paint);

        if (resultBitmap != null) {
            // Draw the opaque result bitmap over the scanning rectangle
            paint.setAlpha(CURRENT_POINT_OPACITY);
            canvas.drawBitmap(resultBitmap, null, frame, paint);
        } else {
            //绘制扫描线
            //得到最新扫描线的垂直绘制位置
            m_scanLineVerPos += m_scanLineMoveLen;
            if (m_scanLineVerPos > frame.height())
                m_scanLineVerPos = 0;
            //创建渐变扫描线
            m_linearGradient = new LinearGradient(frame.left, frame.top + m_scanLineVerPos,
                    frame.right, frame.top + m_scanLineVerPos,
                    m_scanLineColors, m_colorPos, CLAMP);
            paint.setShader(m_linearGradient);
            canvas.drawLine(frame.left, frame.top + m_scanLineVerPos, frame.right,
                    frame.top + m_scanLineVerPos, paint);
            paint.setShader(null);


            //将Camera得到的预览图像按照比例绘制到扫描框中,来自原ViewFinderView
            float scaleX = frame.width() / (float) previewFrame.width();
            float scaleY = frame.height() / (float) previewFrame.height();

            List<ResultPoint> currentPossible = possibleResultPoints;
            List<ResultPoint> currentLast = lastPossibleResultPoints;
            int frameLeft = frame.left;
            int frameTop = frame.top;
            if (currentPossible.isEmpty()) {
                lastPossibleResultPoints = null;
            } else {
                possibleResultPoints = new ArrayList<>(5);
                lastPossibleResultPoints = currentPossible;
                paint.setAlpha(CURRENT_POINT_OPACITY);
                paint.setColor(resultPointColor);
                for (ResultPoint point : currentPossible) {
                    canvas.drawCircle(frameLeft + (int) (point.getX() * scaleX),
                            frameTop + (int) (point.getY() * scaleY),
                            POINT_SIZE, paint);
                }
            }
            if (currentLast != null) {
                paint.setAlpha(CURRENT_POINT_OPACITY / 2);
                paint.setColor(resultPointColor);
                float radius = POINT_SIZE / 2.0f;
                for (ResultPoint point : currentLast) {
                    canvas.drawCircle(frameLeft + (int) (point.getX() * scaleX),
                            frameTop + (int) (point.getY() * scaleY),
                            radius, paint);
                }
            }
        }

        //在更新动画的间隔，仅仅重绘激光线，这边可以修改位置，绘制扫描线
        //来自原ViewFinderView
        // Request another update at the animation interval, but only repaint the laser line,
        // not the entire viewfinder mask.
        postInvalidateDelayed(CUSTOME_ANIMATION_DELAY,
                frame.left,
                frame.top,
                frame.right,
                frame.bottom);
    }
}
