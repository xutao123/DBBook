package com.xt.dbbook.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.xt.dbbook.R;

import java.util.ArrayList;

/**
 * Created by xt on 2018/01/24.
 * 最近查找标签显示,先在一行上不断增加，当行不够显示时，在列上增加
 */

public class FlowLabelLayout extends ViewGroup {

    private final int LABEL_MARGIN = 20;//pixel:label之间的间隔
    private final int LABEL_MARGIN_TOP = 20;//pixel:label之间的间隔

    private ArrayList<ArrayList<View>> m_allViews = new ArrayList();
    private Context m_context;
    private boolean m_isReverse = false;

    public FlowLabelLayout(Context context) {
        super(context);
        m_context = context;
        m_allViews = new ArrayList();
    }

    public FlowLabelLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.FlowLabelLayout);
        for (int i = 0; i < ta.getIndexCount(); i++) {
            int index = ta.getIndex(i);
            switch (index) {
                case R.styleable.FlowLabelLayout_reverse:
                    m_isReverse = ta.getBoolean(index, false);
                    break;
                default:
                    break;
            }
        }
        ta.recycle();
    }

    public FlowLabelLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LinearLayout.LayoutParams(getContext(), attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        /**
         * 其实width和height以及计算好了，直接用即可
         * 有种情况，就是mode是AT_MOST时，此时ViewGroup设置的是wrap_content,但实际使用的是父ViewGroup的大小
         * 此时需要计算子View的宽高来设置
         */
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        //父ViewGroup测量的宽高
        int measuredWidth = MeasureSpec.getSize(widthMeasureSpec);
        int measuredHeight = MeasureSpec.getSize(heightMeasureSpec);

        int measure_children_width = 0;//通过计算所有子View来计算得到的FlowLabelLayout宽度
        int measure_children_height = 0;//计算得到的FlowLabelLayout高度

        /**
         * 得到所有子View的宽高，加上间隔即可得到确定的ViewGroup宽高
         */
        int cal_width = 0, cal_height = 0;//计算得到的ViewGroup宽高

        int child_count = getChildCount();
        int i, critical;
        if (m_isReverse) {
            i = child_count - 1;
            critical = 0;
        } else {
            i = 0;
            critical = child_count - 1;
        }

        for (; getJudgeStatement(m_isReverse, i, critical); ) {
            View c_view = getChildAt(i);
            if (c_view.getVisibility() == GONE)
                continue;
            measureChild(c_view, widthMeasureSpec, heightMeasureSpec);
            /**
             * 默认margin left,right不起作用，使用LABEL_MARGIN代替
             */
            int cur_label_width = c_view.getMeasuredWidth() + LABEL_MARGIN * 2;
            /**
             * 默认margin top,bottom不起作用，使用LABEL_MARGIN_TOP代替margin_top
             * 这里不考虑一个label的宽度超过measuredWidth
             * 设置label的最大宽度，不够显示时使用省略号
             */
            if (cur_label_width + cal_width >= measuredWidth) {
                measure_children_width = measure_children_width > cal_width ? measure_children_width : cal_width;
                //另起一行
                measure_children_height += cal_height;
                cal_height = c_view.getMeasuredHeight() + LABEL_MARGIN_TOP;
                cal_width = cur_label_width;
            } else {
                cal_width += cur_label_width;
                cal_height = cal_height > (c_view.getMeasuredHeight() + LABEL_MARGIN_TOP) ?
                        cal_height : (c_view.getMeasuredHeight() + LABEL_MARGIN_TOP);
            }

            if (i == child_count - 1) {
                measure_children_width = measure_children_width > cal_width ? measure_children_width : cal_width;
                measure_children_height += cal_height + LABEL_MARGIN_TOP;
            }
            if (m_isReverse) {
                i--;
            } else {
                i++;
            }
        }

        setMeasuredDimension(widthMode == MeasureSpec.EXACTLY ? measuredWidth : measure_children_width,
                heightMode == MeasureSpec.EXACTLY ? measuredHeight : measure_children_height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        m_allViews.clear();
        //在onMeasure之后可通过getWidth()获得FlowLabelLayout的width
        int flow_label_width = getWidth();
        int used_width = 0;//当前计算行已经使用的width
        int cur_width;//当前View需要的width

        //存储一行view
        ArrayList<View> line_views = new ArrayList();
        int child_count = getChildCount();

        int i, critical;
        if (m_isReverse) {
            i = child_count - 1;
            critical = 0;
        } else {
            i = 0;
            critical = child_count - 1;
        }

        for (; getJudgeStatement(m_isReverse, i, critical); ) {
            View c_view = getChildAt(i);
            if (c_view.getVisibility() == GONE)
                continue;
            cur_width = c_view.getMeasuredWidth() + LABEL_MARGIN * 2;
            if (cur_width + used_width >= flow_label_width) {//换行
                m_allViews.add(line_views);
                used_width = cur_width;
                line_views = new ArrayList<>();
                line_views.add(c_view);
            } else {
                line_views.add(c_view);
                used_width += cur_width;
            }

            if (i == critical) {
                m_allViews.add(line_views);
            }

            if (m_isReverse) {
                i--;
            } else {
                i++;
            }

        }

        int layout_height = 0;//已经排布的高度
        int h_bottom = 0;

        //这边是对上面保存的View进行排布，不用倒置
        for (int k = 0; k < m_allViews.size(); k++) {
            ArrayList<View> cal_line_views = m_allViews.get(k);
            int layout_width = 0;//当前行已排布的宽度
            for (int j = 0; j < cal_line_views.size(); j++) {
                View c_view = cal_line_views.get(j);
                if (c_view.getVisibility() == GONE)
                    continue;
                //计算view排布left,top,right,bottom
                int left = layout_width + LABEL_MARGIN;
                int top = layout_height + LABEL_MARGIN_TOP;
                int right = left + c_view.getMeasuredWidth();
                int bottom = top + c_view.getMeasuredHeight();
                c_view.layout(left, top, right, bottom);
                layout_width = right + LABEL_MARGIN;
                h_bottom = bottom;
            }
            layout_height = h_bottom;
        }
    }

    public interface FlowItemClickListener {
        void onFlowItemClick(View view, int position);

    }

    private FlowItemClickListener m_listener;

    public void setOnFlowItemClickListener(FlowItemClickListener l) {
        m_listener = l;
    }

    float firstPosX = 0f;
    float firstPosY = 0f;
    private boolean m_clicked = true;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                firstPosX = event.getX();
                firstPosY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float curPosX = event.getX();
                float curPosY = event.getY();
                if (calculateMoveAway(firstPosX, firstPosY, curPosX, curPosY))
                    m_clicked = false;
                break;
            case MotionEvent.ACTION_UP:
                if (m_clicked) {
                    float x = event.getX();
                    float y = event.getY();
                    Rect rect = new Rect();
                    for (int i = 0; i < getChildCount(); i++) {
                        getChildAt(i).getHitRect(rect);
                        if (rect.contains((int) x, (int) y)) {
                            if (m_listener != null) {
                                m_listener.onFlowItemClick(getChildAt(i), i);
                                getChildAt(i).performClick();
                            }
                        }
                    }
                }
                m_clicked = true;
                break;
        }

        return true;
//        return super.onTouchEvent(event);
    }

    /**
     * 用户触碰其中的子View之后，手指有滑动
     * 如果滑动距离超过100，则认为用户不是点击这个子View
     *
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return
     */
    private boolean calculateMoveAway(float x1, float y1, float x2, float y2) {
        if (Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1)) > 100) {
            return true;
        }
        return false;
    }

    public void addLabelView(View view) {
        addView(view);
        requestLayout();
        invalidate();
    }

    public void addLabelViewList(ArrayList<View> list) {
        for (View view : list)
            addView(view);
        requestLayout();
        invalidate();
    }

    //倒置
    public void setReverse(boolean reverse) {
        m_isReverse = reverse;
        requestLayout();
        invalidate();
    }

    /**
     * 按照View中的文字找到view后删除
     *
     * @param data
     */
    public void removeViewByText(String data) {
        int child_count = getChildCount();
        for (int i = 0; i < child_count; i++) {
            View c_view = getChildAt(i);
            if (c_view instanceof LabelView)
                if (((LabelView) c_view).getText().equals(data))
                    removeView(c_view);
        }
        requestLayout();
        invalidate();
    }

    /**
     * 获得循环判断条件
     *
     * @param reverse  true：需要倒序排列所有View
     * @param i        当前计算的是第几个View
     * @param critical 计算的临界条件
     * @return
     */
    public boolean getJudgeStatement(boolean reverse, int i, int critical) {
        if (reverse) {
            if (i >= critical)
                return true;
            else return false;
        } else {
            if (i <= critical)
                return true;
            else return false;
        }
    }

    /*

    public class FlowLabelAdapter<T> {
        T m_view;

        ArrayList<T> m_view_list;

        public FlowLabelAdapter(T v) {
            m_view = v;
            m_view_list = new ArrayList<>();
        }


        public void addLabelView(T view) {
            m_view_list.add(view);
        }


        public void addLabelViewList(ArrayList<T> list) {
            for (T view : list)
                m_view_list.add(view);
        }

        public void refresh(ArrayList<View> list) {
            for (View view : list)
                addView(view);
            requestLayout();
            invalidate();
            m_view_list.clear();
        }
    } */

}
