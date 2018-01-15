package com.bamboo.bullyalert.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bamboo.bullyalert.R;
import com.bamboo.bullyalert.fragment.HistoryDetailFragment.OnListFragmentInteractionListener;
import com.bamboo.bullyalert.model.HistoryDetail;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link HistoryDetail.Item} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class MyHistoryDetailRecyclerViewAdapter extends RecyclerView.Adapter<MyHistoryDetailRecyclerViewAdapter.ViewHolder> {

    private final List<HistoryDetail.Item> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyHistoryDetailRecyclerViewAdapter(List<HistoryDetail.Item> items, OnListFragmentInteractionListener listener)
    {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_notification_detail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mPersonView.setText(mValues.get(position).person);
        holder.mCommentView.setText(mValues.get(position).comment);

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
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final View mView;
        private final TextView mPersonView;
        private final TextView mCommentView;
        private HistoryDetail.Item mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mPersonView = (TextView) view.findViewById(R.id.person);
            mCommentView = (TextView) view.findViewById(R.id.comment);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mCommentView.getText() + "'";
        }
    }
}
