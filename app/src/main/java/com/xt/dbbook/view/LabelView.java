package com.xt.dbbook.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.xt.dbbook.R;

/**
 * Created by xt on 2018/01/25.
 */

public class LabelView extends View {
    private final int DEFAULT_WIDTH = 600;
    private String m_text = "";
    private int m_back_color = Color.argb(0xff, 0xea, 0xea, 0xea);
    private int m_radius = 0; //目前默认全弧，该参数暂时无效
    private int m_text_color = Color.argb(0xc0, 0x00, 0x00, 0x00);
    private int m_text_size = (int) TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics());
    private Paint m_paint;
    private Rect m_text_bounds = new Rect();
    private final int Default_PADDING_TOP = 10;
    private final int Default_PADDING_BOTTOM = 12;

    public LabelView(Context context) {
        super(context);
    }

    public LabelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.LabelView);
        for (int i = 0; i < ta.getIndexCount(); i++) {
            int index = ta.getIndex(i);
            switch (index) {
                case R.styleable.LabelView_back_color:
                    m_back_color = ta.getColor(index, Color.argb(0xff, 0xcc, 0xcc, 0xcc));
                    break;
                case R.styleable.LabelView_label_text:
                    m_text = ta.getString(index).trim();
                    break;
                case R.styleable.LabelView_border_radius:
                    m_radius = ta.getDimensionPixelSize(index, 0);
                    break;
                case R.styleable.LabelView_text_color:
                    m_text_color = ta.getColor(index, Color.argb(0xc0, 0x00, 0x00, 0x00));
                    break;
                case R.styleable.LabelView_text_size:
                    m_text_size = ta.getDimensionPixelSize(index, (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_SP, 14, getResources().getDisplayMetrics()));
                    break;
                default:
                    break;
            }
        }
        ta.recycle();
        m_paint = new Paint();
        m_paint.setTextSize(m_text_size);
    }

    public LabelView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int measuredWidth = MeasureSpec.getSize(widthMeasureSpec);
        int measuredHeight = MeasureSpec.getSize(heightMeasureSpec);

        m_paint.getTextBounds(m_text, 0, m_text.length(), m_text_bounds);
        int text_width = m_text_bounds.width();
        int text_height = m_text_bounds.height();
        if (text_width > DEFAULT_WIDTH)
            text_width = DEFAULT_WIDTH;

        int calculateHeight;
        if (getPaddingTop() == 0 && getPaddingBottom() == 0)
            calculateHeight = text_height + Default_PADDING_TOP + Default_PADDING_BOTTOM;
        else
            calculateHeight = text_height + getPaddingTop() + getPaddingBottom();

        if (heightMode == MeasureSpec.AT_MOST || heightMode == MeasureSpec.UNSPECIFIED)
            m_radius = calculateHeight / 2;
        else
            m_radius = measuredHeight / 2;

        int width;
        if (widthMode == MeasureSpec.AT_MOST || widthMode == MeasureSpec.UNSPECIFIED)
            width = text_width + getPaddingLeft() + getPaddingRight() + m_radius * 2;
        else
            width = measuredWidth;

        int height;
        if (heightMode == MeasureSpec.AT_MOST || heightMode == MeasureSpec.UNSPECIFIED)
            height = calculateHeight;
        else
            height = measuredHeight;

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //绘制背景
        m_paint.setColor(m_back_color);
//        m_paint.setStrokeWidth(2);
        m_paint.setStyle(Paint.Style.FILL_AND_STROKE);//空心效果
        m_paint.setAntiAlias(true);
        RectF rect = new RectF(0, 0, getWidth(), getHeight());
        canvas.drawRoundRect(rect, m_radius, m_radius, m_paint);

        //绘制文字
        m_paint.setColor(m_text_color);
        m_paint.setStrokeWidth(1);
        m_paint.setStyle(Paint.Style.FILL);
        m_paint.setTextSize(m_text_size);
        m_paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText(m_text, (getWidth() - m_text_bounds.width()) / 2 + getPaddingLeft() - getPaddingRight(),
                (getHeight() + m_text_bounds.height()) / 2 + getPaddingTop() - getPaddingBottom(), m_paint);

    }

    public void setBackColor(int color) {
        m_back_color = color;
        invalidate();
    }

    public void setTextColor(int color) {
        m_text_color = color;
        invalidate();
    }

    public void setText(String text) {
        m_text = text.trim();
        requestLayout();
        invalidate();
    }

    public String getText() {
        return m_text;
    }

}
