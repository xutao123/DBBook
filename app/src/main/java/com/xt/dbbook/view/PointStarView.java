package com.xt.dbbook.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.xt.dbbook.R;

import java.text.DecimalFormat;

/**
 * Created by xt on 2018/01/26.
 */

public class PointStarView extends View {
    private int m_starSize = (int) TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics());
    private int m_starNum = 5;
    private int m_starSpace = (int) TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics());
    private Paint m_paint;
    private float m_totalPoint = 10; //总分
    private float m_point;//分数
    private int m_textColor = Color.rgb(0xcc, 0xcc, 0xcc);
    private String m_pointText;
    private Rect m_textBound = new Rect();
    private Bitmap m_emptyStar;
    private Bitmap m_fullStar;
    private boolean m_needDrawText = true;

    public PointStarView(Context context) {
        super(context);
    }

    public PointStarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.PointStarView);
        for (int i = 0; i < ta.getIndexCount(); i++) {
            int index = ta.getIndex(i);
            switch (index) {
                case R.styleable.PointStarView_star_num:
                    m_starNum = ta.getInteger(index, 5);
                    break;
                case R.styleable.PointStarView_star_size:
                    m_starSize = ta.getDimensionPixelSize(index, (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics()));
                    break;
                case R.styleable.PointStarView_star_space:
                    m_starSpace = ta.getDimensionPixelSize(index, (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()));
                    break;
                case R.styleable.PointStarView_point:
                    m_point = ta.getFloat(index, 0f);
                    break;
                case R.styleable.PointStarView_total_point:
                    m_totalPoint = ta.getFloat(index, 10f);
                    break;
                case R.styleable.PointStarView_draw_text:
                    m_needDrawText = ta.getBoolean(index, true);
                    break;
            }
        }
        if (m_point > m_totalPoint)
            m_point = m_totalPoint;
        ta.recycle();

        m_paint = initPaint();
        m_emptyStar = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.star_empty);
        m_fullStar = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.star_full);

        if (m_needDrawText) {
            m_pointText = String.valueOf(m_point);
            m_paint.getTextBounds(m_pointText, 0, m_pointText.length(), m_textBound);
        }
    }

    public PointStarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private Paint initPaint() {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTextSize(m_starSize);
        paint.setColor(m_textColor);
        paint.setStrokeWidth(2);
        paint.setTextAlign(Paint.Align.LEFT);
        return paint;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int measuredWidth = MeasureSpec.getSize(widthMeasureSpec);
        int measuredHeight = MeasureSpec.getSize(heightMeasureSpec);

        int width, height;
        /**
         * MeasureSpec.UNSPECIFIED会出现的
         */
        if (widthMode == MeasureSpec.AT_MOST || widthMode == MeasureSpec.UNSPECIFIED) {
            width = (m_starSize + m_starSpace) * m_starNum;
            if (m_needDrawText)
                width += m_textBound.width() + m_starSpace;
        } else {
            width = measuredWidth;
        }
        if (heightMode == MeasureSpec.AT_MOST || heightMode == MeasureSpec.UNSPECIFIED) {
            height = m_starSize;
        } else {
            height = measuredHeight;
        }
        setMeasuredDimension(width, height);
//        setMeasuredDimension(widthMode == MeasureSpec.AT_MOST ?
//                        (m_starSize + m_starSpace) * m_starNum + m_textBound.width() + m_starSpace : measuredWidth,
//                heightMode == MeasureSpec.AT_MOST ? m_starSize : measuredHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //先将所有空星星画出来
        for (int i = 0; i < m_starNum; i++)
            //null使用整个图片，RectF dst：是图片在Canvas画布中显示的区域
            canvas.drawBitmap(m_emptyStar, null, new RectF(i * (m_starSize + m_starSpace), 0,
                    (i + 1) * m_starSize + i * m_starSpace, m_starSize), m_paint);
        //按照分数画出指定个full_star
        float needStarNum = m_point / (m_totalPoint / m_starNum);

        //先画出整数个full star
        int j = 0;
        for (; j < (int) Math.floor(needStarNum); j++) {
            canvas.drawBitmap(m_fullStar, null, new RectF(j * (m_starSize + m_starSpace), 0,
                    (j + 1) * m_starSize + j * m_starSpace, m_starSize), m_paint);
        }
        //再画部分星星
        DecimalFormat df = new DecimalFormat("##0.0");
        float partialNum = Float.parseFloat(df.format(needStarNum - Math.floor(needStarNum)));
        canvas.drawBitmap(m_fullStar, new Rect(0, 0, (int) (m_fullStar.getWidth() * partialNum), m_fullStar.getHeight()),
                new Rect(j * (m_starSize + m_starSpace), 0,
                        j * (m_starSize + m_starSpace) + (int) (m_starSize * partialNum), m_starSize), m_paint);

        //分数
        if (m_needDrawText)
            canvas.drawText(m_pointText, m_starNum * (m_starSize + m_starSpace)
                    , (m_starSize + m_textBound.height()) / 2, m_paint);
    }

    public void setPoint(float point, float totalPoint) {
        m_point = point;
        m_totalPoint = totalPoint;
        m_pointText = String.valueOf(m_point);
        if (m_needDrawText)
            m_paint.getTextBounds(m_pointText, 0, m_pointText.length(), m_textBound);
        refresh();
    }

    public void refresh() {
        invalidate();
    }

    public void destroy() {
        m_emptyStar.recycle();
        m_fullStar.recycle();
    }
}
