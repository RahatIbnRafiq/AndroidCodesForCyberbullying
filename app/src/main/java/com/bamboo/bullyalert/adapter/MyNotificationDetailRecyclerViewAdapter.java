package com.bamboo.bullyalert.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bamboo.bullyalert.fragment.NotificationDetailFragment.OnListFragmentInteractionListener;
import com.bamboo.bullyalert.R;
import com.bamboo.bullyalert.model.Comment;
import com.bamboo.bullyalert.model.Notification;
import com.bamboo.bullyalert.model.NotificationDetail;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link NotificationDetail.Item} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyNotificationDetailRecyclerViewAdapter extends RecyclerView.Adapter<MyNotificationDetailRecyclerViewAdapter.ViewHolder> {

    //private final Notification mNotification;
    private final OnListFragmentInteractionListener mListener;

    ArrayList<Comment> mCommentList;

    public MyNotificationDetailRecyclerViewAdapter(ArrayList<Comment> commentList, OnListFragmentInteractionListener listener)
    {
        mCommentList = commentList;
        mListener = listener;

    }
    /*public void addCommentToTheList(ArrayList<Comment> oldComments) {
        mCommentList.addAll(oldComments);
        notifyItemInserted(mCommentList.size() - 1);
    }*/


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_notification_detail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mPersonView.setText(mCommentList.get(position).getmCommenterName());
        holder.mCommentView.setText(mCommentList.get(position).getmCommentText());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    //mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mCommentList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mPersonView;
        public final TextView mCommentView;
        //public NotificationDetail.Item mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mPersonView = (TextView) view.findViewById(R.id.person);
            mCommentView = (TextView) view.findViewById(R.id.comment);
        }
    }
}
