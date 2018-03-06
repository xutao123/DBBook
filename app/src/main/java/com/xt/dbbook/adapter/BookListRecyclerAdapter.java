package com.xt.dbbook.adapter;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xt.dbbook.R;
import com.xt.dbbook.bean.net.BookPartInfo;
import com.xt.dbbook.view.PointStarView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.xt.dbbook.adapter.BookListRecyclerAdapter.CurShowType.SHOW_LOADING;
import static com.xt.dbbook.adapter.BookListRecyclerAdapter.CurShowType.SHOW_NORMAL;

/**
 * Created by xt on 2018/01/25.
 */

public class BookListRecyclerAdapter extends RecyclerView.Adapter {

    private Context m_context;
    private BookPartInfo m_bookInfo;
    private SpannableStringBuilder m_titleBuildString;
    private SpannableStringBuilder m_authorBuilderString;
    private boolean m_needSpan = false;
    private String m_needSpanStr = null;
    private ForegroundColorSpan m_colorSpan = null;
    private StyleSpan m_styleSpan = null;

    private Animation m_rotate;

    protected RecyclerView.LayoutManager m_layoutManager;
    private OnBookListListener m_listener;


    private final int BODY_TYPE = 0;//显示文本内容的Item
    private final int LOADING_TYPE = BODY_TYPE + 1; //正在加载中的Item
    private CurShowType m_curShowType = SHOW_NORMAL;

    public enum CurShowType {
        SHOW_NORMAL,//正常展示
        SHOW_LOADING //正在上拉刷新
    }

    public BookListRecyclerAdapter(Context context, BookPartInfo bookInfoList) {
        m_context = context;
        m_bookInfo = bookInfoList;
    }

    public BookListRecyclerAdapter(Context context, BookPartInfo bookInfoList,
                                   RecyclerView.LayoutManager layoutManager) {
        this(context, bookInfoList);
        m_layoutManager = layoutManager;
    }

    /**
     * @param context
     * @param bookInfoList
     * @param needSpan     是否需要对部分字段另外设置颜色
     * @param needSpanStr  另外需要设置颜色的字段
     * @param colorSpan    颜色
     * @param styleSpan    样式
     */
    public BookListRecyclerAdapter(Context context, BookPartInfo bookInfoList,
                                   RecyclerView.LayoutManager layoutManager, boolean needSpan, String needSpanStr,
                                   ForegroundColorSpan colorSpan, StyleSpan styleSpan) {
        this(context, bookInfoList, layoutManager);
        m_needSpan = needSpan;
        m_needSpanStr = needSpanStr;
        m_colorSpan = colorSpan;
        m_styleSpan = styleSpan;
        if (m_needSpan) {
            m_titleBuildString = new SpannableStringBuilder();
            m_authorBuilderString = new SpannableStringBuilder();
        }
    }

    public interface OnBookListListener {
        //点击item回调
        void onChooseBook(int position, String bookId);

        //上拉刷新回调
        void getNewData();
    }

    public void setBookListListener(OnBookListListener l) {
        m_listener = l;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == BODY_TYPE) {
            View v = LayoutInflater.from(m_context).inflate(R.layout.item_book_list, parent, false);
            return new BodyHolder(v);
        } else {
            View v = LayoutInflater.from(m_context).inflate(R.layout.item_loading, parent, false);
            return new LoadingHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof BodyHolder) {
            BodyHolder bodyHolder = (BodyHolder) holder;
            final BookPartInfo.BookInfoData bookData = m_bookInfo.getBooks().get(position);
            Glide.with(m_context).load(bookData.getImage())
                    .into(bodyHolder.v_bookImg);
            String title = bookData.getTitle();
            String authorPublisher = bookData.getAuthorPublisher();

            if (m_needSpan && !TextUtils.isEmpty(m_needSpanStr)) {
                int titleBegin = title.indexOf(m_needSpanStr);
                int authorBegin = authorPublisher.indexOf(m_needSpanStr);
                //设置Title中和搜索关键字匹配的部分样式
                if (titleBegin >= 0) {
                    int titleEnd = titleBegin + m_needSpanStr.length();
                    m_titleBuildString.clear();
                    m_titleBuildString.append(title);
                    if (m_colorSpan != null)
                        m_titleBuildString.setSpan(m_colorSpan, titleBegin, titleEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    if (m_styleSpan != null)
                        m_titleBuildString.setSpan(m_styleSpan, titleBegin, titleEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    bodyHolder.v_bookName.setText(m_titleBuildString);
                } else {
                    bodyHolder.v_bookName.setText(title);
                }

                //设置作者名和出版商中与关键字匹配的部分样式
                if (authorBegin >= 0) {
                    int authorEnd = authorBegin + m_needSpanStr.length();
                    m_authorBuilderString.clear();
                    m_authorBuilderString.append(authorPublisher);
                    if (m_colorSpan != null)
                        m_authorBuilderString.setSpan(m_colorSpan, authorBegin, authorEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    if (m_styleSpan != null)
                        m_authorBuilderString.setSpan(m_styleSpan, authorBegin, authorEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    bodyHolder.v_authorPublish.setText(m_authorBuilderString);
                } else {
                    bodyHolder.v_authorPublish.setText(authorPublisher);
                }
            } else {
                //正常显示
                bodyHolder.v_bookName.setText(title);
                bodyHolder.v_authorPublish.setText(authorPublisher);
            }
            bodyHolder.v_starView.setPoint(Float.parseFloat(bookData.getRating().getAverage()), 10.0f);
            bodyHolder.v_rL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (m_listener != null)
                        m_listener.onChooseBook(position, bookData.getId());
                }
            });
        } else {
            LoadingHolder loadingHolder = (LoadingHolder) holder;
            if (m_curShowType.equals(SHOW_NORMAL)) {
                loadingHolder.v_loadImg.clearAnimation();
                loadingHolder.v_loadLayout.setVisibility(View.GONE);
            } else {
                loadingHolder.v_loadLayout.setVisibility(View.VISIBLE);
                if (m_rotate == null)
                    m_rotate = AnimationUtils.loadAnimation(m_context, R.anim.rotate_anim);
                loadingHolder.v_loadImg.setAnimation(m_rotate);
            }
        }
    }

    @Override
    public int getItemCount() {
        if (m_bookInfo != null && m_bookInfo.getBooks() != null &&
                m_bookInfo.getBooks().size() > 0) {
            return m_bookInfo.getBooks().size() + 1;
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (position < getItemCount() - 1) {
            return BODY_TYPE;
        } else {
            return LOADING_TYPE;
        }
    }

    /**
     * 替换原来显示的数据
     *
     * @param bookInfo
     */
    public void replaceData(BookPartInfo bookInfo) {
        if (bookInfo == null)
            return;
        m_bookInfo = bookInfo;
        notifyDataSetChanged();
    }

    /**
     * 替换原来显示的数据,同时替换原来的匹配文字Style
     *
     * @param bookInfo
     */
    public void replaceDataAndStyle(BookPartInfo bookInfo, String needSpanStr,
                                    ForegroundColorSpan colorSpan,
                                    StyleSpan styleSpan) {
        if (bookInfo == null)
            return;
        m_bookInfo = bookInfo;

        if (!TextUtils.isEmpty(needSpanStr))
            m_needSpanStr = needSpanStr;
        if (colorSpan != null)
            m_colorSpan = colorSpan;
        if (styleSpan != null)
            m_styleSpan = styleSpan;
        notifyDataSetChanged();
    }

    /**
     * 添加新的数据
     *
     * @param bookInfo
     */
    public void updataData(BookPartInfo bookInfo) {
        if (bookInfo == null)
            return;
        List<BookPartInfo.BookInfoData> newAddList = bookInfo.getBooks();
        if (m_bookInfo == null)
            m_bookInfo = new BookPartInfo();
        m_bookInfo.getBooks().addAll(newAddList);

        //隐藏加载Item
        m_curShowType = SHOW_NORMAL;
        notifyDataSetChanged();
    }

    /**
     * 下拉刷新
     *
     * @param bookInfo
     */
    public void refreshView(BookPartInfo bookInfo) {
        m_bookInfo = bookInfo;
        notifyDataSetChanged();
    }

    public void updataCurShowType(CurShowType type) {
        m_curShowType = type;
        notifyItemChanged(getItemCount());
//        notifyDataSetChanged();
    }

    public CurShowType getCurShowType() {
        return m_curShowType;
    }

    public void recycler() {
        if (m_bodyHolderUnbinder != null)
            m_bodyHolderUnbinder.unbind();
        if (m_loadingHolderUnbinder != null)
            m_loadingHolderUnbinder.unbind();
    }

    private Unbinder m_bodyHolderUnbinder;

    public class BodyHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.book_img)
        ImageView v_bookImg;
        @BindView(R.id.book_name)
        TextView v_bookName;
        @BindView(R.id.star_view)
        PointStarView v_starView;
        @BindView(R.id.author_publish)
        TextView v_authorPublish;
        @BindView(R.id.item_layout)
        RelativeLayout v_rL;

        public BodyHolder(View itemView) {
            super(itemView);
            m_bodyHolderUnbinder = ButterKnife.bind(this, itemView);
        }
    }

    private Unbinder m_loadingHolderUnbinder;

    public class LoadingHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.load_img)
        ImageView v_loadImg;
        @BindView(R.id.load_layout)
        LinearLayout v_loadLayout;

        public LoadingHolder(View itemView) {
            super(itemView);
            m_loadingHolderUnbinder = ButterKnife.bind(this, itemView);
        }
    }


    /**
     * 监听RecyclerView滑动
     * 上拉刷新，需要在创建RecyclerView时，传入这个接口
     *
     * @return
     */
    public RecyclerViewScrollListener getRecyclerViewScrollListener() {
        return new RecyclerViewScrollListener();
    }

    public class RecyclerViewScrollListener extends RecyclerView.OnScrollListener {
        private int lastVisibleItem = -1;

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);

            if (newState == RecyclerView.SCROLL_STATE_IDLE
                    && lastVisibleItem + 1 == getItemCount()) { //最后一个
                updataCurShowType(SHOW_LOADING);
                if (m_listener != null)
                    m_listener.getNewData();
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            if (m_layoutManager instanceof LinearLayoutManager)
                lastVisibleItem = ((LinearLayoutManager) m_layoutManager).findLastVisibleItemPosition();
            else if (m_layoutManager instanceof GridLayoutManager)
                lastVisibleItem = ((GridLayoutManager) m_layoutManager).findLastVisibleItemPosition();
            else
                lastVisibleItem = 0;
        }
    }

}
