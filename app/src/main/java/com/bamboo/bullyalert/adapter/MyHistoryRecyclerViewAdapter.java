package com.bamboo.bullyalert.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bamboo.bullyalert.R;
import com.bamboo.bullyalert.fragment.HistoryFragment.OnListFragmentInteractionListener;
import com.bamboo.bullyalert.model.History;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {History} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class MyHistoryRecyclerViewAdapter extends RecyclerView.Adapter<MyHistoryRecyclerViewAdapter.ViewHolder> {

    private final List<History> mHistory;
    private final OnListFragmentInteractionListener mListener;

    public MyHistoryRecyclerViewAdapter(List<History> history, OnListFragmentInteractionListener listener) {
        mHistory = history;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mTextViewUserName.setText(mHistory.get(position).getmUsername());
        holder.mTextViewSocialNetwork.setText(mHistory.get(position).getmOsnName());
        holder.mTextViewBullyingCount.setText(mHistory.get(position).getmBullyingCount()+"");

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener)
                {
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mHistory.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mTextViewUserName;
        private TextView mTextViewSocialNetwork;
        private TextView mTextViewBullyingCount;
        private final View mView;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mTextViewUserName = (TextView) view.findViewById(R.id.history_name);
            mTextViewSocialNetwork = (TextView) view.findViewById(R.id.history_type);
            mTextViewBullyingCount = (TextView) view.findViewById(R.id.history_count);
        }
    }
}
