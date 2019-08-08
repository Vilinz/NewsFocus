package com.example.newsfocus.MyPage;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
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
import com.google.gson.JsonObject;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;


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
        try {
            SharedPreferences sp = getActivity().getSharedPreferences("token", Context.MODE_PRIVATE);
            if(sp.contains("token")) {
                String token = sp.getString("token", null);
                Log.i("token2", token);
                CompositeDisposable mCompositeDisposable = new CompositeDisposable();
                DisposableObserver<JsonObject> disposableObserver_login = new DisposableObserver<JsonObject>() {
                    @Override
                    public void onNext(JsonObject r) {
                        Log.i("veri", r.toString());
                        username = r.get("username").getAsString();
                        isLogin = true;
                        setLogin(username);
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
                ServiceInstance.getInstance().verification(token).subscribeOn(Schedulers.newThread()).
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

    public void setLogin(String u) {
        selectButton.setVisibility(View.GONE);
        headImage.setVisibility(View.VISIBLE);
        usernameView.setVisibility(View.VISIBLE);
        starCountView.setVisibility(View.VISIBLE);
        commentCountView.setVisibility(View.VISIBLE);
        usernameView.setText(u);
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
}
