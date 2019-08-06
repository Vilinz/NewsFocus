package com.example.newsfocus;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.newsfocus.tools.StringUtils;
import com.google.gson.JsonObject;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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

        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            group_id = bundle.getString("group_id");
            Log.i("group_id", group_id);
            getDetail(group_id);
        }
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
                        String onClickFunction = "javascript:findImg()";
                        String setWidthFunction = "javascript:setWidth()";
                        contentWebView.loadUrl(onClickFunction);
                        contentWebView.loadUrl(setWidthFunction);
                        super.onPageFinished(view, url);
                    }
                });
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

    /**
     * 将html文本内容中包含img标签的图片，宽度变为屏幕宽度，高度根据宽度比例自适应
     **/
    public String getNewCleanContent(String htmltext){
        try {
            Document doc= Jsoup.parse(htmltext);
            Elements elements=doc.getElementsByTag("img");
            for (Element element : elements) {
                element.attr("width","100%").attr("height","auto");
            }

            return doc.toString();
        } catch (Exception e) {
            return htmltext;
        }
    }
}
