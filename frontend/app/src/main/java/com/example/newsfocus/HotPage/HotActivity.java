package com.example.newsfocus.HotPage;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.example.newsfocus.NewsDetail.NewDetailActivity;
import com.example.newsfocus.Classes.News;
import com.example.newsfocus.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HotActivity.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HotActivity#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HotActivity extends Fragment implements IHotView{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private View view;

    private OnFragmentInteractionListener mListener;
    private MyListView listView;
    private HotPresenter hp;

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

        listView = (MyListView)view.findViewById(R.id.listView);

        hp = new HotPresenter(this);
        hp.initAdapter(getContext(), listView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(), NewDetailActivity.class);
                Bundle bundle = new Bundle();
                News mNew = hp.getNewByPosition(position);
                bundle.putString("group_id", mNew.getGroup_id());
                bundle.putString("author", mNew.getAuthor());
                bundle.putString("time", mNew.getTime());
                bundle.putString("title", mNew.getTitle());
                bundle.putString("comments", mNew.getComments());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        listView.setONLoadMoreListener(new MyListView.OnLoadMoreListener() {
            @Override
            public void onloadMore() {
                hp.loadMoreData();
            }
        });

        hp.initData();
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
    public void setListView() {
        listView.setLoadCompleted();
    }

    @Override
    public void showMsg(int s) {
        Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
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
