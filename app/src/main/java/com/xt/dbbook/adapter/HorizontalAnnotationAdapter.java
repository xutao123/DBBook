package com.xt.dbbook.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xt.dbbook.R;
import com.xt.dbbook.bean.net.BookAnnoInfo;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by xt on 2018/01/29.
 */

public class HorizontalAnnotationAdapter extends RecyclerView.Adapter {

    private Context m_context;
    private BookAnnoInfo m_bookAnnoInfo;

    private static final int NORMAL_ITEM_TYPE = 0;
    private static final int SHOW_ALL_ITEM_TYPE = 1;

    private OnAnnoItemClickListener m_listener;

    public HorizontalAnnotationAdapter(Context context, BookAnnoInfo bookAnnoInfo) {
        m_context = context;
        m_bookAnnoInfo = bookAnnoInfo;
    }

    public void setAnnoItemClickListener(OnAnnoItemClickListener l) {
        m_listener = l;
    }

    public interface OnAnnoItemClickListener {
        void onAnnoItemClick(View annoView, int position);

        void onShowAllItemClick(View annoView, int position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == NORMAL_ITEM_TYPE) { //Normal
            View v = LayoutInflater.from(m_context).inflate(R.layout.item_annotation, parent, false);
            return new NormalAdapterHolder(v);
        } else if (viewType == SHOW_ALL_ITEM_TYPE) { //Show_All
            View v = LayoutInflater.from(m_context).inflate(R.layout.item_show_all_anno, parent, false);
            return new ShowAllAdapterHolder(v);
        } else {
            return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof NormalAdapterHolder) {
            NormalAdapterHolder normalHolder = (NormalAdapterHolder) holder;
            BookAnnoInfo.AnnoData data = m_bookAnnoInfo.getAnnotations().get(position);
            normalHolder.v_annoText.setText(data.getSummary());
            normalHolder.v_userName.setText(data.getAuthor_user().getName());
            normalHolder.v_time.setText(data.getTime());
            if (data.getPage_no() > 0) {
                normalHolder.v_pageText.setVisibility(View.VISIBLE);
                normalHolder.v_pageText.setText("第" + data.getPage_no() + "页");

                if (!data.getChapter().equals("")) {
                    normalHolder.v_splitLine.setVisibility(View.VISIBLE);
                    normalHolder.v_chapterText.setVisibility(View.VISIBLE);
                    normalHolder.v_chapterText.setText(data.getChapter());
                } else {
                    normalHolder.v_splitLine.setVisibility(View.GONE);
                    normalHolder.v_chapterText.setVisibility(View.GONE);
                }
            } else if (!data.getChapter().equals("")) {
                normalHolder.v_pageText.setVisibility(View.GONE);
                normalHolder.v_splitLine.setVisibility(View.GONE);
                normalHolder.v_chapterText.setVisibility(View.VISIBLE);
                normalHolder.v_chapterText.setText(data.getChapter());
            } else {
                normalHolder.v_chapterText.setVisibility(View.VISIBLE);
                normalHolder.v_chapterText.setText("全文");
            }

            normalHolder.v_annoItemLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (m_listener != null)
                        m_listener.onAnnoItemClick(v, position);
                }
            });

        } else if (holder instanceof ShowAllAdapterHolder) {
            ShowAllAdapterHolder showAllHolder = (ShowAllAdapterHolder) holder;
            showAllHolder.v_annoNum.setText(m_bookAnnoInfo.getTotal() + "篇");
            showAllHolder.v_showAllLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (m_listener != null)
                        m_listener.onShowAllItemClick(v, position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (m_bookAnnoInfo != null)
            if (m_bookAnnoInfo.getAnnotations().size() > 3)
                return 4;
            else return m_bookAnnoInfo.getAnnotations().size();
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 3)
            return SHOW_ALL_ITEM_TYPE;
        return NORMAL_ITEM_TYPE;
    }

    public void recycler() {
        if (m_showAllUnbinder != null)
            m_showAllUnbinder.unbind();
        if (m_NormalUnbinder != null)
            m_NormalUnbinder.unbind();
    }

    private Unbinder m_showAllUnbinder;
    private Unbinder m_NormalUnbinder;

    class ShowAllAdapterHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.show_all_layout)
        CardView v_showAllLayout;
        @BindView(R.id.anno_num)
        TextView v_annoNum;

        public ShowAllAdapterHolder(View itemView) {
            super(itemView);
            m_showAllUnbinder = ButterKnife.bind(this, itemView);
        }
    }

    class NormalAdapterHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.anno_layout)
        CardView v_annoItemLayout;
        @BindView(R.id.page_text)
        TextView v_pageText;
        @BindView(R.id.split_line)
        ImageView v_splitLine;
        @BindView(R.id.chapter_text)
        TextView v_chapterText;
        @BindView(R.id.anno_text)
        TextView v_annoText;
        @BindView(R.id.user_name)
        TextView v_userName;
        @BindView(R.id.time)
        TextView v_time;

        public NormalAdapterHolder(View itemView) {
            super(itemView);
            m_NormalUnbinder = ButterKnife.bind(this, itemView);
        }
    }
}
