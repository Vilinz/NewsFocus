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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.newsfocus.R;
import com.example.newsfocus.RegisterPage.RegisterActivity;
import com.example.newsfocus.Service.ServiceInstance;
import com.example.newsfocus.Service.ServiceInstanceWithToken;
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


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MyActivity.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MyActivity#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyActivity extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private ListView listView;
    private List<SampleClass> list;
    private SampleListAdapter sampleListAdapter;

    private Button selectButton;
    private ImageView headImage;
    private TextView usernameView;
    private TextView starCountView;
    private TextView commentCountView;

    private boolean isLogin = false;
    private String username = null;
    private String telephone;
    private String avatar;

    public String baseUrl = "http://47.102.84.27:3000/image/avatar/";

    private String token;

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
        Log.i("ooooooooooo", "creat");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        Log.i("ooooooooooo", "creatview");
        EventBus.getDefault().register(this);
        final View view = inflater.inflate(R.layout.fragment_my, container, false);
        listView = view.findViewById(R.id.listView);
        list = new ArrayList<SampleClass>();
        list.add(new SampleClass("设置", R.drawable.ic_action_setting));
        list.add(new SampleClass("上传头像", R.drawable.ic_action_upload_image));
        list.add(new SampleClass("退出登录",R.drawable.ic_action_logout));
        sampleListAdapter = new SampleListAdapter(list, getActivity());
        listView.setAdapter(sampleListAdapter);

        initView(view);

        headImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_PICK);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, 0);
            }
        });
        try {
            SharedPreferences sp = getActivity().getSharedPreferences("token", Context.MODE_PRIVATE);
            if(sp.contains("token")) {
                token = sp.getString("token", null);
                username = sp.getString("username", null);
                avatar = sp.getString("avatar", null);
                Log.i("token2", token);
                CompositeDisposable mCompositeDisposable = new CompositeDisposable();
                DisposableObserver<JsonObject> disposableObserver_login = new DisposableObserver<JsonObject>() {
                    @Override
                    public void onNext(JsonObject r) {
                        Log.i("veri", r.toString());
                        username = r.getAsJsonObject("username").get("username").getAsString();
                        telephone = r.getAsJsonObject("username").get("telephone").getAsString();
                        avatar = r.getAsJsonObject("username").get("avatar").getAsString();
                        isLogin = true;
                        setLogin();
                    }
                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(getContext(), R.string.login_again, Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        //Toast.makeText(GithubApi.this, R.string.network_error, Toast.LENGTH_LONG).show();
                    }
                };
                ServiceInstanceWithToken.getInstanceWithToken(token).getUserInfo(username).subscribeOn(Schedulers.newThread()).
                        observeOn(AndroidSchedulers.mainThread()).subscribe(disposableObserver_login);
                mCompositeDisposable.add(disposableObserver_login);
            }
        } catch (Exception e) {
            Log.i("pppppppppppppp", "ppppppppppppp");
        }

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

    public void setLogin() {
        selectButton.setVisibility(View.GONE);
        headImage.setVisibility(View.VISIBLE);
        usernameView.setVisibility(View.VISIBLE);
        starCountView.setVisibility(View.VISIBLE);
        commentCountView.setVisibility(View.VISIBLE);
        usernameView.setText(username);
        new Thread() {
            public void run() {
                Bitmap bitmap = getBitmapFromUrl(baseUrl + avatar + ".png");
                Message message = Message.obtain();
                message.obj = bitmap;
                mHandler.sendMessage(message);
            }
        }.start();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void LoginChange(String str) {
        selectButton.setVisibility(View.GONE);
        headImage.setVisibility(View.VISIBLE);
        usernameView.setVisibility(View.VISIBLE);
        starCountView.setVisibility(View.VISIBLE);
        commentCountView.setVisibility(View.VISIBLE);
        usernameView.setText(str);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // https://blog.csdn.net/ABC__D/article/details/51790806
        ContentResolver resolver = getActivity().getContentResolver();
        if (requestCode == 0) {
            try {
                Uri uri = data.getData();

                File file = uri2File(uri);

                upload(file);

                Bitmap bitmap = MediaStore.Images.Media.getBitmap(resolver, uri);
                Bitmap newBitmap = setImgSize(bitmap, 200, 200);
                headImage.setImageBitmap(newBitmap);
            }
            catch (Exception e) {

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public Bitmap setImgSize(Bitmap bm, int newWidth ,int newHeight){
        // https://blog.csdn.net/gxl_1899/article/details/77449908
        // 获得图片的宽高.
        int width = bm.getWidth();
        int height = bm.getHeight();
        // 计算缩放比例.
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数.
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片.
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        return newbm;
    }

    private File uri2File(Uri uri) {
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
        File file = new File(img_path);
        return file;
    }

    private void upload(File file) {
        //多张图片
        RequestBody imageBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)//表单类型
                .addFormDataPart("file", "headImage.png", imageBody)
                .addFormDataPart("username", username);
        List<MultipartBody.Part> parts = builder.build().parts();

        CompositeDisposable mCompositeDisposable = new CompositeDisposable();
        DisposableObserver<JsonObject> disposableObserver_login = new DisposableObserver<JsonObject>() {
            @Override
            public void onNext(JsonObject r) {
                Log.i("veri", r.toString());
            }
            @Override
            public void onError(Throwable e) {
                Toast.makeText(getContext(), R.string.login_again, Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
                //Toast.makeText(GithubApi.this, R.string.network_error, Toast.LENGTH_LONG).show();
            }
        };
        ServiceInstanceWithToken.getInstanceWithToken(token).uploadImages(parts).subscribeOn(Schedulers.newThread()).
                observeOn(AndroidSchedulers.mainThread()).subscribe(disposableObserver_login);
        mCompositeDisposable.add(disposableObserver_login);
    }

    private Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            headImage.setImageBitmap((Bitmap) msg.obj);
        }
    };

    public Bitmap getBitmapFromUrl(String urlString){
        Bitmap bitmap;
        InputStream is = null;
        try {
            URL mUrl= new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) mUrl.openConnection();
            is = new BufferedInputStream(connection.getInputStream());
            bitmap= BitmapFactory.decodeStream(is);
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
