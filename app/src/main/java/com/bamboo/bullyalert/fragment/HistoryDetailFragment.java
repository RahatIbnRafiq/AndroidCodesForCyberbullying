package com.bamboo.bullyalert.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bamboo.bullyalert.R;

import com.bamboo.bullyalert.model.History;
import com.bamboo.bullyalert.model.HistoryDetail;

public class HistoryDetailFragment extends Fragment {

    private static final String ARG_NAME = "name";
    private String mName = "";
    private OnListFragmentInteractionListener mListener;

    private History mHistory = null;
    private Context mContext;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mNotificationDetailAdapter;

    public HistoryDetailFragment() {
    }

    public static HistoryDetailFragment newInstance(History history) {
        HistoryDetailFragment fragment = new HistoryDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_NAME, history);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mHistory = (History) getArguments().getSerializable(ARG_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history_detail_page, container, false);
        this.mContext = view.getContext();

        mRecyclerView = (RecyclerView) view.findViewById(R.id.list);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                LinearLayoutManager.VERTICAL);
        mRecyclerView.addItemDecoration(dividerItemDecoration);
        return view;
    }



    private void populateHistoryDetails()
    {
        if(mHistory != null)
        {

        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(HistoryDetail item);
    }


}
