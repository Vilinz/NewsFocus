package com.example.newsfocus;

import android.content.Context;
import android.net.Uri;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.net.URL;
import java.util.List;

public class ListAdapter extends BaseAdapter {
    private Context context;
    private List<News> list;
    final int VIEW_TYPE = 3;
    final int TYPE_1 = 0; //无图
    final int TYPE_2 = 1; //一张图
    final int TYPE_3 = 2; //两张图
    final int TYPE_4 = 3; //三张以上

    public ListAdapter(Context context,List<News> list) {
        this.context=context;
        this.list=list;
    }
    public void addItem(News g) {
        list.add(g);
        this.notifyDataSetChanged();
    }
    public void removeItem(int i) {
        if(i > 0) list.remove(i);
        this.notifyDataSetChanged();
    }

    public void refresh() {
        this.notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        if(list == null) {
            return 0;
        }
        return list.size();
    }
    @Override
    public Object getItem(int i) {
        if(list == null) return null;
        return list.get(i);
    }

    @Override
    public int getViewTypeCount() {
        return 4;
    }

    @Override
    public int getItemViewType(int position) {
        int count = list.get(position).getImage_info().size();
        if(count == 0) {
            return TYPE_1;
        } else if(count == 1) {
            return TYPE_2;
        } else if(count == 2) {
            return TYPE_3;
        } else {
            return TYPE_4;
        }
    }
    @Override
    public long getItemId(int i) {
        return i;
    }
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder1 viewHolder1 = null;
        ViewHolder2 viewHolder2 = null;
        ViewHolder3 viewHolder3 = null;
        ViewHolder4 viewHolder4 = null;
        int type = getItemViewType(i);
        if(view == null) {
            switch (type) {
                case TYPE_1:
                    view = LayoutInflater.from(context).inflate(R.layout.new_item_type_1,null);
                    viewHolder1 = new ViewHolder1();
                    viewHolder1.title = view.findViewById(R.id.title);
                    viewHolder1.author = view.findViewById(R.id.author);
                    view.setTag(viewHolder1);
                    break;
                case TYPE_2:
                    view = LayoutInflater.from(context).inflate(R.layout.new_item_type_2,null);
                    viewHolder2 = new ViewHolder2();
                    viewHolder2.title = view.findViewById(R.id.title);
                    viewHolder2.author = view.findViewById(R.id.author);
                    viewHolder2.imageView = view.findViewById(R.id.head_image);
                    view.setTag(viewHolder2);
                    break;
                case TYPE_3:
                    view = LayoutInflater.from(context).inflate(R.layout.new_item_type_3,null);
                    viewHolder3 = new ViewHolder3();
                    viewHolder3.title = view.findViewById(R.id.title);
                    viewHolder3.author = view.findViewById(R.id.author);
                    viewHolder3.imageView = view.findViewById(R.id.head_image);
                    view.setTag(viewHolder3);
                    break;
                case TYPE_4:
                    view = LayoutInflater.from(context).inflate(R.layout.new_item_type_4,null);
                    viewHolder4 = new ViewHolder4();
                    viewHolder4.title = view.findViewById(R.id.title);
                    viewHolder4.author = view.findViewById(R.id.author);
                    viewHolder4.imageView1 = view.findViewById(R.id.imageView1);
                    viewHolder4.imageView2 = view.findViewById(R.id.imageView2);
                    viewHolder4.imageView3 = view.findViewById(R.id.imageView3);
                    view.setTag(viewHolder4);
                    break;
            }
        }else{
            switch (type) {
                case TYPE_1:
                    viewHolder1=(ViewHolder1) view.getTag();
                    break;
                case TYPE_2:
                    viewHolder2=(ViewHolder2) view.getTag();
                    break;
                case TYPE_3:
                    viewHolder3=(ViewHolder3) view.getTag();
                    break;
                case TYPE_4:
                    viewHolder4=(ViewHolder4) view.getTag();
                    break;

            }
        }
        switch (type) {
            case TYPE_1:
                viewHolder1.title.setText(list.get(i).getTitle());
                viewHolder1.author.setText(list.get(i).getAuthor());
                break;
            case TYPE_2:
                viewHolder2.title.setText(list.get(i).getTitle());
                viewHolder2.author.setText(list.get(i).getAuthor());
                viewHolder2.imageView.setImageURI(Uri.parse(list.get(i).getImage_info().get(0)));
                break;
            case TYPE_3:
                viewHolder3.title.setText(list.get(i).getTitle());
                viewHolder3.author.setText(list.get(i).getAuthor());
                viewHolder3.imageView.setImageURI(Uri.parse(list.get(i).getImage_info().get(0)));
                break;
            case TYPE_4:
                viewHolder4.title.setText(list.get(i).getTitle());
                viewHolder4.author.setText(list.get(i).getAuthor());
                viewHolder4.imageView1.setImageURI(Uri.parse(list.get(i).getImage_info().get(0)));
                viewHolder4.imageView2.setImageURI(Uri.parse(list.get(i).getImage_info().get(1)));
                viewHolder4.imageView3.setImageURI(Uri.parse(list.get(i).getImage_info().get(2)));
                break;
        }

        return view;
    }
    private class ViewHolder1{
        public TextView title;
        public TextView author;
    }

    private class ViewHolder2{
        public TextView title;
        public TextView author;
        public ImageView imageView;
    }

    private class ViewHolder3{
        public TextView title;
        public TextView author;
        public ImageView imageView;
    }

    private class ViewHolder4{
        public TextView title;
        public TextView author;
        public ImageView imageView1;
        public ImageView imageView2;
        public ImageView imageView3;
    }

}
