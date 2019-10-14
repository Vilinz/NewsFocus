package com.example.newsfocus.MyPage;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.newsfocus.Classes.News;
import com.example.newsfocus.R;
import com.example.newsfocus.RegisterPage.RegisterActivity;
import com.example.newsfocus.Service.ServiceInstance;
import com.example.newsfocus.Service.ServiceInstanceWithToken;
import com.example.newsfocus.tools.BitmapUtils;
import com.google.gson.JsonObject;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import android.content.ContentResolver;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MyActivity.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MyActivity#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyActivity extends Fragment implements IMyView {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private boolean isLogin;

    private OnFragmentInteractionListener mListener;

    private Button selectButton;
    private ImageView headImage;
    private TextView usernameView;
    private TextView starCountView;
    private TextView commentCountView;
    private ListView listView;

    private MyPresenter mp;
    public String baseUrl = "http://47.102.84.27:3000/image/avatar/";

    public MyActivity() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyActivity.
     */
    // TODO: Rename and change types and number of parameters
    public static MyActivity newInstance(String param1, String param2) {
        MyActivity fragment = new MyActivity();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_my, container, false);
        EventBus.getDefault().register(this);

        mp = new MyPresenter(this);
        listView = view.findViewById(R.id.listView);
        mp.initAdapter(listView, getContext());

        initView(view);
        mp.autoLogin(getContext());

        headImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_PICK);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, 0);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0) {

                } else if(position == 1) {
                    SharedPreferences sp = getActivity().getSharedPreferences("token",MODE_PRIVATE);
                    if(sp!=null) {
                        sp.edit().clear().commit();
                    }
                    setLogout();
                }
                Toast.makeText(getContext(),"你单击的是第"+(position+1)+"条数据",Toast.LENGTH_SHORT).show();
            }
        });

        if(!isLogin) {
            selectButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), RegisterActivity.class);
                    startActivity(intent);
                }
            });
        }
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void autoLogin(JsonObject r) {
        selectButton.setVisibility(View.GONE);
        headImage.setVisibility(View.VISIBLE);
        usernameView.setVisibility(View.VISIBLE);
        starCountView.setVisibility(View.VISIBLE);
        commentCountView.setVisibility(View.VISIBLE);
        String username = r.getAsJsonObject("username").get("username").getAsString();
        String avatar = null;
        if(!(r.getAsJsonObject("username").get("avatar") + "").equals("null")) {
            avatar = r.getAsJsonObject("username").get("avatar").getAsString();
        }
        usernameView.setText(username);
        isLogin = true;
        final String finalAvatar = avatar;
        mp.downImageFromURL(baseUrl + finalAvatar + ".png");
    }

    @Override
    public void updateImage(Bitmap bitmap) {
        headImage.setImageBitmap(bitmap);
        Toast.makeText(getContext(), R.string.upload_success, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setImage(Bitmap bitmap) {
        headImage.setImageBitmap(bitmap);
    }

    @Override
    public void showMsg(int i) {
        Toast.makeText(getContext(), i, Toast.LENGTH_SHORT).show();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void initView(View view) {
        selectButton = view.findViewById(R.id.button);
        headImage = view.findViewById(R.id.head_image);
        usernameView = view.findViewById(R.id.username);
        starCountView = view.findViewById(R.id.star_count);
        commentCountView = view.findViewById(R.id.comment_count);
    }

    public void setLogout() {
        selectButton.setVisibility(View.VISIBLE);
        headImage.setVisibility(View.GONE);
        usernameView.setVisibility(View.GONE);
        starCountView.setVisibility(View.GONE);
        commentCountView.setVisibility(View.GONE);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void LoginChange(String str) {
        selectButton.setVisibility(View.GONE);
        headImage.setVisibility(View.VISIBLE);
        usernameView.setVisibility(View.VISIBLE);
        starCountView.setVisibility(View.VISIBLE);
        commentCountView.setVisibility(View.VISIBLE);
        usernameView.setText(str);
        mp.autoLogin(getContext());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // https://blog.csdn.net/ABC__D/article/details/51790806
        ContentResolver resolver = getActivity().getContentResolver();
        if (requestCode == 0) {
            try {
                Uri uri = data.getData();
                String img_path;
                String[] proj = {MediaStore.Images.Media.DATA};
                Cursor actualimagecursor = getActivity().managedQuery(uri, proj, null,
                        null, null);
                if (actualimagecursor == null) {
                    img_path = uri.getPath();
                } else {
                    int actual_image_column_index = actualimagecursor
                            .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    actualimagecursor.moveToFirst();
                    img_path = actualimagecursor
                            .getString(actual_image_column_index);
                }
                img_path = BitmapUtils.compressImage(img_path);
                File file = new File(img_path);
                Log.i("sizee", file.length()+"");
                mp.uploadImage(file, img_path);
            }
            catch (Exception e) {
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
