package com.example.newsfocus;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class MainActivity
        extends AppCompatActivity
        implements View.OnClickListener,HotActivity.OnFragmentInteractionListener, StoreActivity.OnFragmentInteractionListener, MyActivity.OnFragmentInteractionListener{
    private TextView text_hot, text_store, text_my;
    private ViewPager vp;
    private HotActivity hot_fragment;
    private StoreActivity store_fragment;
    private MyActivity my_fragment;
    private List<Fragment> mFragmentList = new ArrayList<Fragment>();
    private FragmentAdapter mFragmentAdapter;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(intent != null) {
            String username = intent.getStringExtra("username");
            EventBus.getDefault().post(username);
            Log.i("new", "iiiiiiiiiiiiiiii" + username);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        init();
        mFragmentAdapter = new FragmentAdapter(this.getSupportFragmentManager(), mFragmentList);
        vp.setOffscreenPageLimit(3);
        vp.setAdapter(mFragmentAdapter);
        vp.setCurrentItem(0);
        changeTextColor(0);

        //ViewPager监听
        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                changeTextColor(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.item_p1:
                vp.setCurrentItem(0, true);
                break;
            case R.id.item_p2:
                vp.setCurrentItem(1, true);
                break;
            case R.id.item_p3:
                vp.setCurrentItem(2, true);
                break;
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    void init() {
        text_hot = findViewById(R.id.item_p1);
        text_store = findViewById(R.id.item_p2);
        text_my = findViewById(R.id.item_p3);

        text_hot.setOnClickListener(this);
        text_store.setOnClickListener(this);
        text_my.setOnClickListener(this);

        vp = findViewById(R.id.vp);

        hot_fragment = new HotActivity();
        store_fragment = new StoreActivity();
        my_fragment = new MyActivity();

        mFragmentList.add(hot_fragment);
        mFragmentList.add(store_fragment);
        mFragmentList.add(my_fragment);
    }

    private void changeTextColor(int position) {
        if (position == 0) {
            text_hot.setTextColor(Color.parseColor("#66CDAA"));
            text_store.setTextColor(Color.parseColor("#000000"));
            text_my.setTextColor(Color.parseColor("#000000"));
        } else if (position == 1) {
            text_store.setTextColor(Color.parseColor("#66CDAA"));
            text_hot.setTextColor(Color.parseColor("#000000"));
            text_my.setTextColor(Color.parseColor("#000000"));
        } else if (position == 2) {
            text_my.setTextColor(Color.parseColor("#66CDAA"));
            text_hot.setTextColor(Color.parseColor("#000000"));
            text_store.setTextColor(Color.parseColor("#000000"));
        }
    }
}
