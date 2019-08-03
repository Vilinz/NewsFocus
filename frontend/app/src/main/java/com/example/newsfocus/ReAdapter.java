package com.example.newsfocus;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import java.util.Map;


public class ReAdapter extends RecyclerView.Adapter<ViewHolder> {
    private List<News> mdatas;
    private Context mContext;
    private int mLayoutId;
    public ReAdapter(Context context,int layoutId,List datas) {
        mContext=context;
        mLayoutId=layoutId;
        mdatas=datas;
    }
    public void removeItem(int i) {
        if(i>0) {
            mdatas.remove(i);
            notifyItemRemoved(i);
        }
    }

    public void refresh() {
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        return mdatas.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        ViewHolder viewHolder=ViewHolder.get(mContext,parent,mLayoutId);
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(final ViewHolder holder,int position) {
        TextView v = holder.getView(R.id.title);
        v.setText(mdatas.get(position).getTitle());
        // convert(holder,mdatas.get(position));

    }
}
