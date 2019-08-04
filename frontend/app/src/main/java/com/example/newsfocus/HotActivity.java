package com.example.newsfocus;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HotActivity.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HotActivity#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HotActivity extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private View view;

    private OnFragmentInteractionListener mListener;

    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    private List<News> mData = new ArrayList<News>();
    private ListAdapter listAdapter;
    private ListView listView;

    public HotActivity() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HotActivity.
     */
    // TODO: Rename and change types and number of parameters
    public static HotActivity newInstance(String param1, String param2) {
        HotActivity fragment = new HotActivity();
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
        view = inflater.inflate(R.layout.fragment_hot, container, false);

        listView = (ListView)view.findViewById(R.id.listView);
        listAdapter = new ListAdapter(getActivity(), mData);
        listView.setAdapter(listAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(), NewDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("group_id", mData.get(position).getGroup_id());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        DisposableObserver<JsonObject> disposableObserver_login = new DisposableObserver<JsonObject>() {
            @Override
            public void onNext(JsonObject r) {
                int count = r.getAsJsonArray("data").size();
                Log.i("count", count + "");
                for(int i = 0; i < count; i++) {
                    JsonObject ob = r.getAsJsonArray("data").get(i).getAsJsonObject();
                    String image_infos = ob.get("image_infos").getAsString();
                    JsonParser jsonParser = new JsonParser();
                    JsonArray array = (JsonArray) jsonParser.parse(image_infos);
                    List<String> url = new ArrayList<String>();
                    for(int j = 0; j < array.size(); j++) {
                        JsonObject temp = array.get(j).getAsJsonObject();
                        String url_temp = temp.get("url_prefix").getAsString() + temp.get("web_uri").getAsString();
                        url.add(url_temp);
                    }
                    News n = new News(ob.get("group_id").getAsString(),
                                        ob.get("title").getAsString(),
                                        ob.get("author").getAsString(),
                                        ob.get("time").getAsString(),
                                        url,
                                        ob.get("comments").getAsString());
                    mData.add(n);
                }
                listAdapter.refresh();
            }
            @Override
            public void onError(Throwable e) {
                Toast.makeText(getContext(), "error", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
                //Toast.makeText(GithubApi.this, R.string.network_error, Toast.LENGTH_LONG).show();
            }
        };
        ServiceInstance.getInstance().getNewHead(0, 10).subscribeOn(Schedulers.newThread()).
                observeOn(AndroidSchedulers.mainThread()).subscribe(disposableObserver_login);
        mCompositeDisposable.add(disposableObserver_login);

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

}
