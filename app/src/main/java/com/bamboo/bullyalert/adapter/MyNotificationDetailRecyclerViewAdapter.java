package com.bamboo.bullyalert.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bamboo.bullyalert.UtilityPackage.UtilityVariables;
import com.bamboo.bullyalert.fragment.NotificationDetailFragment.OnListFragmentInteractionListener;
import com.bamboo.bullyalert.R;
import com.bamboo.bullyalert.model.Comment;
import com.bamboo.bullyalert.model.NotificationDetail;
import java.util.ArrayList;

public class MyNotificationDetailRecyclerViewAdapter extends RecyclerView.Adapter<MyNotificationDetailRecyclerViewAdapter.ViewHolder>
{
    private final OnListFragmentInteractionListener mListener;
    private ArrayList<Comment> mCommentList;

    public MyNotificationDetailRecyclerViewAdapter(ArrayList<Comment> commentList, OnListFragmentInteractionListener listener)
    {
        mCommentList = commentList;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        try
        {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_notification_detail, parent, false);
            return new ViewHolder(view);
        }catch (Exception e)
        {
            Log.i(UtilityVariables.tag,"Exception in "+this.getClass().getName()+" onCreateViewHolder fucntion: "+e.toString());
            return null;
        }
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        try {
            holder.mPersonView.setText(mCommentList.get(position).getmCommenterName());
            holder.mCommentView.setText(mCommentList.get(position).getmCommentText());
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mListener)
                    {
                    }
                }
            });
        }catch (Exception e)
        {
            Log.i(UtilityVariables.tag,"Exception in "+this.getClass().getName()+" onBindViewHolder function: "+e.toString());
        }
    }

    @Override
    public int getItemCount() {
        return mCommentList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final View mView;
        private final TextView mPersonView;
        private final TextView mCommentView;

        public ViewHolder(View view)
        {
            super(view);
            mView = view;
            mPersonView = (TextView) view.findViewById(R.id.person);
            mCommentView = (TextView) view.findViewById(R.id.comment);
        }
    }
}
