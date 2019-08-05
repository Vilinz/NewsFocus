package com.example.newsfocus;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class SampleListAdapter extends BaseAdapter {
    private List<SampleClass> mData;
    private Context mContext;

    public SampleListAdapter(List<SampleClass> m, Context c) {
        mData = m;
        mContext = c;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(mContext).inflate(R.layout.sample_item,parent,false);
        ImageView img_icon = (ImageView) convertView.findViewById(R.id.imageView);
        TextView text = (TextView) convertView.findViewById(R.id.text);
        img_icon.setImageResource(mData.get(position).getId());
        text.setText(mData.get(position).getText());
        return convertView;
    }
}
