package com.example.newsfocus.NewsDetail;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.newsfocus.Classes.Comments;
import com.example.newsfocus.Classes.News;
import com.example.newsfocus.R;
import com.example.newsfocus.Service.ServiceInstanceWithToken;
import com.example.newsfocus.tools.HttpUtils;
import com.google.gson.JsonObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class CommentListAdapter extends BaseAdapter {
    private Context context;
    private List<Comments> list;
    private LruCache<String, Bitmap> mMemoryCaches;

    private int maxMemory = (int)Runtime.getRuntime().maxMemory();
    private int cacheSizes = maxMemory/5;

    private String baseUrl = "http://47.102.84.27:3000/image/avatar/";

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

    public void setImageID(int position, int id) {
        list.get(position).setImage_id(id);
        this.notifyDataSetChanged();
    }

    public void updateItemPost(int position, int count) {
        list.get(position).setStars(count);
        list.get(position).setImage_id(R.drawable.star_after);
        this.notifyDataSetChanged();
    }

    public void updateItemDelete(int position, int count) {
        list.get(position).setStars(count);
        list.get(position).setImage_id(R.drawable.star_before);
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
    public View getView(final int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if(view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.comment_listview_item,null);
            viewHolder = new ViewHolder();
            viewHolder.head_image = view.findViewById(R.id.head_image);
            viewHolder.username = view.findViewById(R.id.username);
            viewHolder.comment = view.findViewById(R.id.comment);
            viewHolder.star = view.findViewById(R.id.star);
            viewHolder.star_count = view.findViewById(R.id.star_count);
            viewHolder.time = view.findViewById(R.id.time);
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
        viewHolder.time.setText(timeStrip2String(list.get(i).getTime()));
        viewHolder.star.setImageResource(list.get(i).getImage_id());
        viewHolder.star.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(list.get(i).getImage_id() == R.drawable.star_before) {
                    postStar(i);
                } else {
                    // Log.i("delete", "rrr");
                    // deleteStar(i);
                }
            }
        });
        if(list.get(i).getUserID().equals(viewHolder.head_image.getTag())) {
            return view;
        }
        viewHolder.head_image.setTag(list.get(i).getUserID());
        try {
            imageLoader.showImageByThead(viewHolder.head_image, list.get(i).getUserID());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return view;
    }
    private class ViewHolder{
        public ImageView head_image;
        public TextView username;
        public TextView comment;
        public ImageView star;
        public TextView star_count;
        public TextView time;
    }

    //---------------------------
    //load images
    public class ImageLoader {
        private ImageView mImageView;
        private String mUrl;
        Bitmap bitmap = null;
        String url;

        public void showImageByThead(ImageView iv, final String url) throws IOException {
            mImageView = iv;
            mUrl = url + ".png";
            this.url = url;

            final URL longUrl = new URL(baseUrl + mUrl);

            bitmap = getBitmapFromLrucache(url);

            if(bitmap != null) {
                mImageView.setImageBitmap(bitmap);
            } else {
                new Thread() {
                    public void run() {
                        bitmap = HttpUtils.getBitmapFromUrl(longUrl.toString());
                        if(bitmap == null) {
                            bitmap = HttpUtils.getBitmapFromUrl("http://47.102.84.27:3000/image/avatar/MTc2MjI0NjU3MTIwMDE4MDIxMDU=.png");
                        }
                        if(bitmap != null) {
                            addBitmapToLrucaches(url, bitmap);
                        }
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
                if(mImageView.getTag().equals(url)) {
                    mImageView.setImageBitmap((Bitmap) msg.obj);
                }
            }
        };
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

    public String timeStrip2String(String s) {
        long l = Long.parseLong(s);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(l);
        return simpleDateFormat.format(date);
    }

    public void postStar(final int position) {
        CompositeDisposable mCompositeDisposable = new CompositeDisposable();
        DisposableObserver<JsonObject> disposableObserver_sendComment = new DisposableObserver<JsonObject>() {
            @Override
            public void onNext(JsonObject r) {
                Log.i("ssss2", r.toString());
                int count = r.get("count").getAsInt();
                updateItemPost(position, count);

            }
            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
                //Toast.makeText(GithubApi.this, R.string.network_error, Toast.LENGTH_LONG).show();
            }
        };
        SharedPreferences sp = context.getSharedPreferences("token", Context.MODE_PRIVATE);
        String token = null;
        String hostUsername = null;
        if(sp.contains("token")) {
            token = sp.getString("token", null);
            hostUsername = sp.getString("username", null);
            ServiceInstanceWithToken.getInstanceWithToken(token).postStar(hostUsername, list.get(position).getCommentId()+"").subscribeOn(Schedulers.newThread()).
                    observeOn(AndroidSchedulers.mainThread()).subscribe(disposableObserver_sendComment);
            mCompositeDisposable.add(disposableObserver_sendComment);
            Log.i("post", "post");
        } else {
            Toast.makeText(context, R.string.login_first, Toast.LENGTH_LONG).show();
        }
    }

    public void deleteStar(final int position) {
        CompositeDisposable mCompositeDisposable = new CompositeDisposable();
        DisposableObserver<JsonObject> disposableObserver_sendComment = new DisposableObserver<JsonObject>() {
            @Override
            public void onNext(JsonObject r) {
                Log.i("ssss2", r.toString());
                int count = r.get("count").getAsInt();
                updateItemDelete(position, count);
            }
            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
                //Toast.makeText(GithubApi.this, R.string.network_error, Toast.LENGTH_LONG).show();
            }
        };
        SharedPreferences sp = context.getSharedPreferences("token", Context.MODE_PRIVATE);
        String token = null;
        String hostUsername = null;
        if(sp.contains("token")) {
            token = sp.getString("token", null);
            hostUsername = sp.getString("username", null);
            ServiceInstanceWithToken.getInstanceWithToken(token).deleteStar(hostUsername, list.get(position).getCommentId() + "").subscribeOn(Schedulers.newThread()).
                    observeOn(AndroidSchedulers.mainThread()).subscribe(disposableObserver_sendComment);
            mCompositeDisposable.add(disposableObserver_sendComment);
        } else {
            Toast.makeText(context, "请重新登录", Toast.LENGTH_LONG).show();
        }
    }
}
