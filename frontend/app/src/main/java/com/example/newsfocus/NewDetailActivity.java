package com.example.newsfocus;

import android.app.ActionBar;
import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.gson.JsonObject;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class NewDetailActivity extends AppCompatActivity {
    private String group_id;
    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    private WebView webContent;

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

        webContent = findViewById(R.id.web_content);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            group_id = bundle.getString("group_id");
            Log.i("group_id", group_id);
            getDetail(group_id);
        }
    }

    public void getDetail(String group_id) {
        DisposableObserver<JsonObject> disposableObserver_login = new DisposableObserver<JsonObject>() {
            @Override
            public void onNext(JsonObject r) {
                String html = r.getAsJsonObject("data").get("content").getAsString().replace("\\", "");
                Log.i("html", html);
                webContent.loadDataWithBaseURL(null, getNewCleanContent(html), "text/html", "utf-8",
                        null);
                webContent.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
                webContent.getSettings().setJavaScriptEnabled(true); //设置支持Javascript
                webContent.requestFocus();
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
