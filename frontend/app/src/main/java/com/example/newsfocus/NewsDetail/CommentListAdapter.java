package com.example.newsfocus.NewsDetail;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.newsfocus.Classes.Comments;
import com.example.newsfocus.Classes.News;
import com.example.newsfocus.R;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class CommentListAdapter extends BaseAdapter {
    private Context context;
    private List<Comments> list;
    private LruCache<String, Bitmap> mMemoryCaches;

    private int maxMemory = (int)Runtime.getRuntime().maxMemory();
    private int cacheSizes = maxMemory/5;

    public CommentListAdapter(Context context,List<Comments> list) {
        this.context=context;
        this.list=list;
        mMemoryCaches = new LruCache<String, Bitmap>(cacheSizes) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };
    }
    public void addItem(Comments g) {
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
    public long getItemId(int i) {
        return i;
    }
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if(view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.comment_listview_item,null);
            viewHolder = new ViewHolder();
            viewHolder.head_image = view.findViewById(R.id.head_image);
            viewHolder.username = view.findViewById(R.id.username);
            viewHolder.comment = view.findViewById(R.id.comment);
            viewHolder.star = view.findViewById(R.id.star);
            viewHolder.star_count = view.findViewById(R.id.star_count);
            view.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)view.getTag();
        }
        /*
        switch (type) {
            case TYPE_1:
                break;
            case TYPE_2:
                viewHolder2.imageView.setImageResource(R.drawable.ic_action_name);
                break;
            case TYPE_3:
                viewHolder3.imageView.setImageResource(R.drawable.ic_action_name);
                break;
            case TYPE_4:
                viewHolder4.imageView1.setImageResource(R.drawable.ic_action_name);
                viewHolder4.imageView2.setImageResource(R.drawable.ic_action_name);
                viewHolder4.imageView3.setImageResource(R.drawable.ic_action_name);
                break;
        }
        */
        ImageLoader imageLoader = new ImageLoader();
        viewHolder.username.setText(list.get(i).getUserID());
        viewHolder.comment.setText(list.get(i).getComment());
        viewHolder.star_count.setText(list.get(i).getStars()+"");

        return view;
    }
    private class ViewHolder{
        public ImageView head_image;
        public TextView username;
        public TextView comment;
        public ImageView star;
        public TextView star_count;
    }

    //---------------------------
    //load images
    public class ImageLoader {
        private ImageView mImageView;
        private String mUrl;

        public void showImageByThead(ImageView iv, final String url) {
            mImageView = iv;
            mUrl = url;

            Bitmap bitmap = getBitmapFromLrucache(mUrl);
            if(bitmap != null) {
                mImageView.setImageBitmap(bitmap);
            } else {
                new Thread() {
                    public void run() {
                        Bitmap bitmap = getBitmapFromUrl(url);
                        Message message = Message.obtain();
                        message.obj = bitmap;
                        mHandler.sendMessage(message);
                    }
                }.start();
            }
        }

        private Handler mHandler = new Handler(){
            public void handleMessage(android.os.Message msg) {
                super.handleMessage(msg);
                if(mImageView.getTag().equals(mUrl)) {
                    mImageView.setImageBitmap((Bitmap) msg.obj);
                }
            }
        };


        public Bitmap getBitmapFromUrl(String urlString){
            Bitmap bitmap;
            InputStream is = null;
            try {
                URL mUrl= new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) mUrl.openConnection();
                is = new BufferedInputStream(connection.getInputStream());
                bitmap=BitmapFactory.decodeStream(is);
                connection.disconnect();
                return bitmap;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }finally{
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }

    //缓存
    public Bitmap getBitmapFromLrucache(String url) {
        return mMemoryCaches.get(url);
    }

    public void addBitmapToLrucaches(String url, Bitmap bitmap) {
        if(getBitmapFromLrucache(url) == null) {
            mMemoryCaches.put(url, bitmap);
        }
    }
}
