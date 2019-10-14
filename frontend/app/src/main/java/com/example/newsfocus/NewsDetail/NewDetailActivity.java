package com.example.newsfocus.NewsDetail;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.newsfocus.R;
import com.example.newsfocus.tools.TimeUtils;

import java.util.ArrayList;

public class NewDetailActivity extends AppCompatActivity implements INewDetailView{
    private WebView contentWebView;
    private CommentListView listView;
    private ScrollView mScrollView;
    private TextView titleView;
    private TextView timeView;
    private TextView authorView;
    private EditText commentTextView;

    private NewDetailPresenter np;

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

        np = new NewDetailPresenter(this);

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
        commentTextView = findViewById(R.id.commentView);

        np.initUsername(this, listView);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            Log.i("pppp", bundle.getString("group_id"));
            np.setNewContent(bundle.getString("group_id"), bundle.getString("author"), bundle.getString("time"),
                    bundle.getString("title"), bundle.getString("comments"));

            np.getDetail();
        }

        commentTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCommentDialog();
            }
        });
    }

    @Override
    public void setHtml(String html, ArrayList<String> urls) {
        contentWebView.loadDataWithBaseURL(null,html, "text/html",  "utf-8", null);
        final WebSettings webSettings = contentWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);// 启动js脚本
        webSettings.setBlockNetworkImage(true);
        contentWebView.addJavascriptInterface(new NewDetailActivity.JavascriptImgInterface(urls), "connect");

        contentWebView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                np.setHeader();
                String onClickFunction = "javascript:findImg()";
                String setWidthFunction = "javascript:setWidth()";
                contentWebView.loadUrl(onClickFunction);
                contentWebView.loadUrl(setWidthFunction);
                webSettings.setBlockNetworkImage(false);
                //判断webview是否加载了，图片资源
                if (!webSettings.getLoadsImagesAutomatically()) {
                    //设置wenView加载图片资源
                    webSettings.setLoadsImagesAutomatically(true);
                }
                np.getCommentByNewId();
            }
        });
    }

    @Override
    public void updateHeader(String title, String time, String author) {
        titleView.setText(title);
        timeView.setText(TimeUtils.timeStrip2String(time));
        authorView.setText(author);
    }

    @Override
    public void showMsg(int i) {
        Toast.makeText(getApplicationContext(), i, Toast.LENGTH_SHORT).show();
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

    private void showCommentDialog() {
        new CommentDialog("优质评论将会被优先展示", new CommentDialog.SendListener() {
            @Override
            public void sendComment(final String inputText) {
                np.sendComment(inputText);
            }
        }).show(getSupportFragmentManager(), "comment");
    }
}
