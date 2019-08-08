package com.example.newsfocus;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.newsfocus.HotPage.HotActivity;
import com.example.newsfocus.MyPage.MyActivity;
import com.example.newsfocus.StorePage.StoreActivity;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity
        extends AppCompatActivity
        implements View.OnClickListener, HotActivity.OnFragmentInteractionListener, StoreActivity.OnFragmentInteractionListener, MyActivity.OnFragmentInteractionListener{
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

        if (!checkPermission()) {
            openActivity();
        } else {
            if (checkPermission()) {
                requestPermissionAndContinue();
            } else {
                openActivity();
            }
        }

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

    private static final int PERMISSION_REQUEST_CODE = 200;
    private boolean checkPermission() {

        return ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                ;
    }

    private void requestPermissionAndContinue() {
        if (ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, WRITE_EXTERNAL_STORAGE)
                    && ActivityCompat.shouldShowRequestPermissionRationale(this, READ_EXTERNAL_STORAGE)) {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
                alertBuilder.setCancelable(true);
                alertBuilder.setTitle(getString(R.string.app_name));
                alertBuilder.setMessage(R.string.app_name);
                alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{WRITE_EXTERNAL_STORAGE
                                , READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
                    }
                });
                AlertDialog alert = alertBuilder.create();
                alert.show();
                Log.e("", "permission denied, show dialog");
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{WRITE_EXTERNAL_STORAGE,
                        READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            }
        } else {
            openActivity();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (permissions.length > 0 && grantResults.length > 0) {

                boolean flag = true;
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        flag = false;
                    }
                }
                if (flag) {
                    openActivity();
                } else {
                    finish();
                }

            } else {
                finish();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void openActivity() {
        //add your further process after giving permission or to download images from remote server.
    }
}
