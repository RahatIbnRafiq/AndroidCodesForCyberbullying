package com.bamboo.bullyalert.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bamboo.bullyalert.Database.MonitoringPost;
import com.bamboo.bullyalert.R;
import com.bamboo.bullyalert.UtilityPackage.UtilityVariables;
import com.bamboo.bullyalert.fragment.NotificationFragment.OnListFragmentInteractionListener;
import com.bamboo.bullyalert.model.Notification;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Notification.Item} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */

class   CustomComparator implements Comparator<Notification> {
    @Override
    public int compare(Notification o1, Notification o2) {
        if(o1.getmLevel() > o2.getmLevel())
                return -1;
        return 1;
    }
}

public class MyNotificationRecyclerViewAdapter extends RecyclerView.Adapter<MyNotificationRecyclerViewAdapter.ViewHolder> {
    private final OnListFragmentInteractionListener mListener;

    //private final HashMap<String,Notification> mNotifications;
    private final ArrayList<Notification> mNotificationList;

    public MyNotificationRecyclerViewAdapter(ArrayList<Notification> notifications, OnListFragmentInteractionListener listener)
    {
        //this.mNotificationList = new ArrayList<>();
        this.mListener = listener;
        this.mNotificationList = notifications;
        /*Log.i(UtilityVariables.tag,"inside MyNotificationRecyclerViewAdapter constructor: size of notifications: "+mNotifications.size());
        for (Map.Entry<String, Notification> entry : mNotifications.entrySet())
        {
            String postid = entry.getKey();
            Notification n = entry.getValue();
            mNotificationList.add(n);
        }*/
        Collections.sort(mNotificationList, new CustomComparator());

    }

    public void deleteItem(int position)
    {
        this.mNotificationList.remove(position);
        this.notifyItemRemoved(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position)
    {
        try {
            final Notification notif = mNotificationList.get(position);
            holder.mUserName.setText(mNotificationList.get(position).getmUserName());
            holder.mSocialNetwork.setText(mNotificationList.get(position).getmSocialNetwork());
            String textLevel = "low";
            if(mNotificationList.get(position).getmLevel() > 0.7)
            {
                textLevel = "high";
            }
            else if(mNotificationList.get(position).getmLevel() > 0.4 &&  mNotificationList.get(position).getmLevel() < 0.7)
            {
                textLevel = "medium";
            }
            holder.mLevel.setText(textLevel);


            holder.mView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Log.i(UtilityVariables.tag,"clicked username: "+notif.getmPostId());
                    if (null != mListener)
                    {
                        mListener.onListFragmentInteraction(notif);

                    }
                }
            });

            holder.mView.setOnGenericMotionListener(new View.OnGenericMotionListener() {
                @Override
                public boolean onGenericMotion(View v, MotionEvent event) {
                    return false;
                }
            });


        }catch (Exception e)
        {
            Log.i(UtilityVariables.tag,"Exception in onBindViewHolder: "+e.toString());
        }
    }

    @Override
    public int getItemCount()
    {
        return mNotificationList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public final View mView;
        public TextView mLevel;
        public TextView mUserName;
        public TextView mSocialNetwork;


        public ViewHolder(View view)
        {
            super(view);
            mView = view;
            mLevel = (TextView)itemView.findViewById(R.id.probability);
            mUserName = (TextView)itemView.findViewById(R.id.username);
            mSocialNetwork = (TextView)itemView.findViewById(R.id.socialNetwork);

        }
    }
}
