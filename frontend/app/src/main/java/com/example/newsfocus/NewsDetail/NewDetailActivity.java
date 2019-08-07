package com.example.newsfocus.NewsDetail;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.newsfocus.R;
import com.example.newsfocus.MyPage.SampleClass;
import com.example.newsfocus.MyPage.SampleListAdapter;
import com.example.newsfocus.Service.ServiceInstance;
import com.example.newsfocus.tools.StringUtils;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class NewDetailActivity extends AppCompatActivity {
    private String group_id;
    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    private WebView contentWebView;
    private ListView listView;
    private List<SampleClass> list;
    private SampleListAdapter sampleListAdapter;
    private ScrollView mScrollView;
    private TextView titleView;
    private TextView timeView;
    private TextView authorView;

    private String author;
    private String time;
    private String title;
    private String comments;
    private TextView commentTextView;

    @Override
    public boolean onSupportNavigateUp()
    {
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_detail);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            // actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        contentWebView = findViewById(R.id.web_content);
        listView = findViewById(R.id.listView);
        mScrollView = findViewById(R.id.scrollView2);
        titleView = findViewById(R.id.title);
        timeView = findViewById(R.id.time);
        authorView = findViewById(R.id.author);
        commentTextView = findViewById(R.id.comment);

        list = new ArrayList<SampleClass>();
        sampleListAdapter = new SampleListAdapter(list, getApplicationContext());
        listView.setAdapter(sampleListAdapter);

        // myScrollView.setOnTouchListener(new TouchListenerImpl());

        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            group_id = bundle.getString("group_id");
            author = bundle.getString("author");
            time = bundle.getString("time");
            title = bundle.getString("title");
            comments = bundle.getString("comments");
            getDetail(group_id);
        }

        commentTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCommentDialog();
            }
        });
    }

    class JavascriptImgInterface{
        private ArrayList<String> imageUrls;

        public JavascriptImgInterface(ArrayList<String> imageUrls) {
            this.imageUrls = imageUrls;
        }
        /**
         * 注意： 在Android4.2极其以上系统需要给提供js调用的方法前加入一个注释：@JavaScriptInterface;
         * 如果方法被标识@JavaScriptInterface则Js可以成功调用这个Java方法，否则调用不成功。
         * @param img
         */
        @JavascriptInterface
        public void showImg(String img){
            //利用js传过来的参数得到图片的地址
            Log.d("HERE", "showImg: "+ img);
            Intent intent = new Intent(NewDetailActivity.this, BigImageActivity.class);
            intent.putExtra("imageUrl", img);
            intent.putStringArrayListExtra("imageUrls", imageUrls);
            // intent.putExtra("imageUrls", this.imageUrls);
            startActivity(intent);
        }
    }

    public void getDetail(String group_id) {
        DisposableObserver<JsonObject> disposableObserver_login = new DisposableObserver<JsonObject>() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onNext(JsonObject r) {
                String html = r.getAsJsonObject("data").get("content").getAsString().replace("\\", "");

                String javascript ="function findImg(){"+
                        "var objs = document.getElementsByTagName('img');"+
                        "for(var i=0;i<objs.length;i++){"+
                        "objs[i].onclick=function(){" +
                        "window.connect.showImg(this.src)" +
                        "}"+
                        "}}"+
                        "function setWidth() {" +
                        "var objs = document.getElementsByTagName('img');" +
                        "for(var i=0;i<objs.length;i++)" +
                        "{" +
                        "var img = objs[i];" +
                        "img.style.width = '100%';img.style.height = 'auto';" +
                        "}}";
                //拼接成一个完成的 HTML，
                html = html.substring(0, 12) + "<script> " + javascript + " </script>" + html.substring(12);

                Log.i("html", html);

                ArrayList<String> urls = StringUtils.returnImageUrlsFromHtml(html);
                contentWebView.loadDataWithBaseURL(null,html, "text/html",  "utf-8", null);
                WebSettings webSettings = contentWebView.getSettings();
                webSettings.setJavaScriptEnabled(true);// 启动js脚本
                contentWebView.addJavascriptInterface(new JavascriptImgInterface(urls), "connect");

                contentWebView.setWebViewClient(new WebViewClient(){
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);
                        initHeader();
                        String onClickFunction = "javascript:findImg()";
                        String setWidthFunction = "javascript:setWidth()";
                        contentWebView.loadUrl(onClickFunction);
                        contentWebView.loadUrl(setWidthFunction);

                        list.add(new SampleClass("上传头像", R.drawable.ic_action_upload_image));
                        list.add(new SampleClass("退出登录",R.drawable.ic_action_logout));
                        list.add(new SampleClass("设置", R.drawable.ic_action_setting));
                        list.add(new SampleClass("设置", R.drawable.ic_action_setting));
                    }
                });
                /*
                listView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionevent) {
                        if (motionevent.getAction() == MotionEvent.ACTION_DOWN) {
                            listView.getParent().requestDisallowInterceptTouchEvent(true);
                        }
                        return false;
                    }
                });
                */
                /*
                int totalHeight = 0;
                for (int i = 0; i < sampleListAdapter.getCount(); i++) {
                     View listItem = sampleListAdapter.getView(i, null, listView);
                     listItem.measure(0, 0);
                     totalHeight += listItem.getMeasuredHeight();
                }
                ViewGroup.LayoutParams params = listView.getLayoutParams();
                 params.height = totalHeight + (listView.getDividerHeight() * (sampleListAdapter.getCount() - 1));
                 listView.setLayoutParams(params);
                 */
            }
            @Override
            public void onError(Throwable e) {
                Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
                //Toast.makeText(GithubApi.this, R.string.network_error, Toast.LENGTH_LONG).show();
            }
        };
        ServiceInstance.getInstance().getDetail(group_id).subscribeOn(Schedulers.newThread()).
                observeOn(AndroidSchedulers.mainThread()).subscribe(disposableObserver_login);
        mCompositeDisposable.add(disposableObserver_login);
    }

    private void initHeader() {
        titleView.setText(title);
        timeView.setText("时间" + time);
        authorView.setText(author);
    }

    private void showCommentDialog() {
        new CommentDialog("优质评论将会被优先展示", new CommentDialog.SendListener() {
            @Override
            public void sendComment(String inputText) {
                Toast.makeText(getApplicationContext(),inputText,Toast.LENGTH_SHORT).show();
            }
        }).show(getSupportFragmentManager(), "comment");
    }
}
