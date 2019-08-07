package com.example.newsfocus.NewsDetail;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import com.example.newsfocus.R;

public class MyListView extends ListView implements AbsListView.OnScrollListener {
    private Context mContext;
    private View mFootView;
    private int mTotalItemCount;//item总数
    private OnLoadMoreListener mLoadMoreListener;
    private boolean mIsLoading=false;//是否正在加载

    public MyListView(Context context) {
        super(context);
        init(context);
    }

    public MyListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public MyListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context){
        this.mContext=context;
        mFootView= LayoutInflater.from(context).inflate(R.layout.foot_view, null);
        setOnScrollListener(this);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        // 滑到底部后自动加载，判断listview已经停止滚动并且最后可视的条目等于adapter的条目
        int lastVisibleIndex=view.getLastVisiblePosition();
        if (!mIsLoading&&scrollState == OnScrollListener.SCROLL_STATE_IDLE
                && lastVisibleIndex ==mTotalItemCount-1) {
            mIsLoading=true;
            addFooterView(mFootView);
            if (mLoadMoreListener!=null) {
                mLoadMoreListener.onloadMore();
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        mTotalItemCount=totalItemCount;
    }

    public void setONLoadMoreListener(OnLoadMoreListener listener){
        mLoadMoreListener=listener;
    }

    public interface OnLoadMoreListener{
        void onloadMore();
    }

    public void setLoadCompleted(){
        mIsLoading=false;
        removeFooterView(mFootView);
    }
}
