package com.xt.dbbook.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.bumptech.glide.request.RequestOptions;
import com.xt.dbbook.R;
import com.xt.dbbook.bean.net.BaseInfo;
import com.xt.dbbook.bean.net.BookAnnoInfo;
import com.xt.dbbook.bean.net.CollectionInfo;
import com.xt.dbbook.view.PointStarView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.xt.dbbook.adapter.RefreshListAdapter.CurShowType.SHOW_LOADING;
import static com.xt.dbbook.adapter.RefreshListAdapter.CurShowType.SHOW_NORMAL;
import static com.xt.dbbook.adapter.RefreshListAdapter.ViewLayoutType.ANNOTATION_LAYOUT;
import static com.xt.dbbook.adapter.RefreshListAdapter.ViewLayoutType.COLLECTION_LAYOUT;

/**
 * Created by xt on 2018/02/06.
 * <p>
 * CollectionActivity使用
 * <p>
 * AllAnnoActivity使用
 */

public class RefreshListAdapter extends RecyclerView.Adapter {

    private Context m_context;
    private CollectionInfo m_collectionInfo;
    private BookAnnoInfo m_annoInfo;

    private Animation m_rotate;
    protected RecyclerView.LayoutManager m_layoutManager;
    private OnRefreshListListener m_listener;

    private Unbinder m_loadingHolderUnbinder;
    private Unbinder m_bookListHolderUnbinder;
    private Unbinder m_annoHolderUnbinder;

    private final int BODY_TYPE = 0;//显示文本内容的Item
    private final int LOADING_TYPE = BODY_TYPE + 1; //正在加载中的Item
    private CurShowType m_curShowType = SHOW_NORMAL;

    //为了方便，默认使用collection，item_book_list.xml
    private ViewLayoutType m_viewLayoutType = COLLECTION_LAYOUT;

    /**
     * 规定主体item显示样式，用于协助计算Item的ViewType
     * 做到多个RecyclerView使用同一个Adapter文件
     */
    public enum ViewLayoutType {
        COLLECTION_LAYOUT,//使用显示collection收藏item:item_book_list.xml
        ANNOTATION_LAYOUT//使用显示笔记item布局:item_anno_list.xml
    }

    /**
     * 分两类
     * 一是 显示主体的ItemView
     * <p>
     * 二是 显示上拉刷新时最后需要显示和隐藏的dialog item
     */
    public enum CurShowType {
        SHOW_NORMAL,//正常展示
        SHOW_LOADING //正在上拉刷新
    }

    public RefreshListAdapter(Context context
            , BaseInfo baseInfo, ViewLayoutType itemLayoutType) {
        m_context = context;
        m_viewLayoutType = itemLayoutType;
        if (m_viewLayoutType == ANNOTATION_LAYOUT) {
            m_annoInfo = (BookAnnoInfo) baseInfo;
        } else {
            m_collectionInfo = (CollectionInfo) baseInfo;
        }
    }

    public RefreshListAdapter(Context context, BaseInfo baseInfo,
                              RecyclerView.LayoutManager layoutManager, ViewLayoutType itemLayoutType) {
        this(context, baseInfo, itemLayoutType);
        m_layoutManager = layoutManager;
    }

    public interface OnRefreshListListener {
        //点击item回调
        void onChooseItem(int position, String id);

        //上拉刷新回调
        void getNewData();
    }

    public void setRefreshListListener(OnRefreshListListener l) {
        m_listener = l;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == BODY_TYPE) {
            if (m_viewLayoutType == COLLECTION_LAYOUT) {
                View v = LayoutInflater.from(m_context).inflate(R.layout.item_book_list, parent, false);
                return new CollectionHolder(v);
            } else if (m_viewLayoutType == ANNOTATION_LAYOUT) {
                View v = LayoutInflater.from(m_context).inflate(R.layout.item_anno_list, parent, false);
                return new AnnotationHolder(v);
            } else {
                return null;
            }
        } else {
            View v = LayoutInflater.from(m_context).inflate(R.layout.item_loading, parent, false);
            return new LoadingHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof CollectionHolder) {
            CollectionHolder collectionHolder = (CollectionHolder) holder;
            final CollectionInfo.BookCollections collectionData = m_collectionInfo.getCollections().get(position);
            Glide.with(m_context).load(collectionData.getBook().getImage())
                    .into(collectionHolder.v_bookImg);
            collectionHolder.v_bookName.setText(collectionData.getBook().getTitle());
            collectionHolder.v_collectionTime.setText("收藏于:" + collectionData.getUpdated());
            collectionHolder.v_starView.setPoint(Float.parseFloat(collectionData.getBook().getRating().getAverage()), 10.0f);
            collectionHolder.v_authorPublish.setText(collectionData.getBook().getAuthorPublisher());

            collectionHolder.v_rL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (m_listener != null)
                        m_listener.onChooseItem(position, collectionData.getBook().getId());
                }
            });
        } else if (holder instanceof AnnotationHolder) {
            AnnotationHolder annoHolder = (AnnotationHolder) holder;
            final BookAnnoInfo.AnnoData annoData = m_annoInfo.getAnnotations().get(position);
            //设置标题
            if (annoData.getPage_no() > 0) {
                annoHolder.v_pageText.setVisibility(View.VISIBLE);
                annoHolder.v_pageText.setText("第" + annoData.getPage_no() + "页");

                if (!annoData.getChapter().equals("")) {
                    annoHolder.v_cutOffLine.setVisibility(View.VISIBLE);
                    annoHolder.v_chapterText.setVisibility(View.VISIBLE);
                    annoHolder.v_chapterText.setText(annoData.getChapter());
                } else {
                    annoHolder.v_cutOffLine.setVisibility(View.GONE);
                    annoHolder.v_chapterText.setVisibility(View.GONE);
                }
            } else if (!annoData.getChapter().equals("")) {
                annoHolder.v_pageText.setVisibility(View.GONE);
                annoHolder.v_cutOffLine.setVisibility(View.GONE);
                annoHolder.v_chapterText.setVisibility(View.VISIBLE);
                annoHolder.v_chapterText.setText(annoData.getChapter());
            } else {
                annoHolder.v_chapterText.setVisibility(View.VISIBLE);
                annoHolder.v_chapterText.setText("全文");
            }

            if (annoData.getAuthor_user() != null) {
                Glide.with(m_context).load(annoData.getAuthor_user().getAvatar())
                        .apply(new RequestOptions().circleCrop().error(R.drawable.me_user))
                        .into(annoHolder.v_userImg);
                annoHolder.v_name.setText(annoData.getAuthor_user().getName());
            }
            annoHolder.v_time.setText(annoData.getTime());
            annoHolder.v_content.setText(annoData.getSummary());

            annoHolder.v_annoItemLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (m_listener != null)
                        m_listener.onChooseItem(position, annoData.getId());
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
        if (m_collectionInfo != null && m_collectionInfo.getCollections() != null &&
                m_collectionInfo.getCollections().size() > 0) {
            return m_collectionInfo.getCollections().size() + 1;
        } else if (m_annoInfo != null && m_annoInfo.getAnnotations() != null &&
                m_annoInfo.getAnnotations().size() > 0) {
            return m_annoInfo.getAnnotations().size() + 1;
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

    public void updataData(BaseInfo info) {
        if (info == null)
            return;
        if (m_viewLayoutType == COLLECTION_LAYOUT) {
            List<CollectionInfo.BookCollections> newAddList =
                    ((CollectionInfo) info).getCollections();
            if (m_collectionInfo == null)
                m_collectionInfo = new CollectionInfo();
            m_collectionInfo.getCollections().addAll(newAddList);
        } else {
            List<BookAnnoInfo.AnnoData> newAddList = ((BookAnnoInfo) info).getAnnotations();
            if (m_annoInfo == null)
                m_annoInfo = new BookAnnoInfo();
            m_annoInfo.getAnnotations().addAll(newAddList);
        }
        //隐藏加载Item
        m_curShowType = SHOW_NORMAL;
        notifyDataSetChanged();
    }

    public void updataCurShowType(CurShowType type) {
        m_curShowType = type;
        notifyItemChanged(getItemCount() - 1);
//        notifyDataSetChanged();
    }

    /**
     * 下拉刷新
     */
    public void refreshView(BaseInfo info) {
        if (m_viewLayoutType == COLLECTION_LAYOUT) {
            m_collectionInfo = (CollectionInfo) info;
        } else {
            m_annoInfo = (BookAnnoInfo) info;
        }
        notifyDataSetChanged();
    }

    public CurShowType getCurShowType() {
        return m_curShowType;
    }

    public void recycler() {
        if (m_bookListHolderUnbinder != null)
            m_bookListHolderUnbinder.unbind();
        if (m_loadingHolderUnbinder != null)
            m_loadingHolderUnbinder.unbind();
        if (m_annoHolderUnbinder != null)
            m_annoHolderUnbinder.unbind();
    }

    /**
     * item_book_list.xml
     */
    public class CollectionHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.book_img)
        ImageView v_bookImg;
        @BindView(R.id.book_name)
        TextView v_bookName;
        @BindView(R.id.star_view)
        PointStarView v_starView;
        @BindView(R.id.author_publish)
        TextView v_authorPublish;
        @BindView(R.id.collection_time)
        TextView v_collectionTime;
        @BindView(R.id.item_layout)
        RelativeLayout v_rL;

        public CollectionHolder(View itemView) {
            super(itemView);
            m_bookListHolderUnbinder = ButterKnife.bind(this, itemView);
            v_collectionTime.setVisibility(View.VISIBLE);
        }
    }

    public class AnnotationHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.anno_item_layout)
        LinearLayout v_annoItemLayout;
        @BindView(R.id.page_text)
        TextView v_pageText;
        @BindView(R.id.cut_off_line)
        ImageView v_cutOffLine;
        @BindView(R.id.chapter_text)
        TextView v_chapterText;
        @BindView(R.id.content)
        TextView v_content;
        @BindView(R.id.user_img)
        ImageView v_userImg;
        @BindView(R.id.name)
        TextView v_name;
        @BindView(R.id.time)
        TextView v_time;

        public AnnotationHolder(View itemView) {
            super(itemView);
            m_annoHolderUnbinder = ButterKnife.bind(this, itemView);
        }
    }

    /**
     * item_loading.xml
     */
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
                    && lastVisibleItem + 1 == getItemCount()) {
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
