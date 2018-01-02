package com.bamboo.bullyalert.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bamboo.bullyalert.UtilityPackage.UtilityVariables;
import com.bamboo.bullyalert.fragment.ProfileFragment.OnListFragmentInteractionListener;
import com.bamboo.bullyalert.R;
import com.bamboo.bullyalert.model.Profile;

import java.util.HashMap;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Profile} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyProfileRecyclerViewAdapter extends RecyclerView.Adapter<MyProfileRecyclerViewAdapter.ViewHolder> {

    private final List<Profile> mListMonitoringUserProfiles;
    private final OnListFragmentInteractionListener mListener;
    private Context mContext;


    public HashMap<String,Profile> mMonitoringUserProfilesHashmap = new HashMap<>();

    public MyProfileRecyclerViewAdapter(List<Profile> items, OnListFragmentInteractionListener listener,Context context) {
        mListMonitoringUserProfiles = items;
        mListener = listener;
        this.mContext = context;
        for(int i=0;i<this.mListMonitoringUserProfiles.size();i++)
        {
            this.mMonitoringUserProfilesHashmap.put(mListMonitoringUserProfiles.get(i).getmUserName(),mListMonitoringUserProfiles.get(i));
        }
    }

    public interface OnClickProfileCardListener
    {
        void onClickProfileCard(Profile userProfile);
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_profile, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        Profile monitoringUserProfile = mListMonitoringUserProfiles.get(position);
        try {
            holder.mTextViewUserName.setText("User: "+monitoringUserProfile.getmUserName());
            holder.mTextViewSocialNetwork.setText("on Instagram");

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    Log.i(UtilityVariables.tag,"clicked username: "+mListMonitoringUserProfiles.get(position).getmUserName());
                    if (null != mListener)
                    {
                        mListener.onListFragmentInteraction(mListMonitoringUserProfiles.get(position));

                    }
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
        return mListMonitoringUserProfiles.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {

        public TextView mTextViewUserName;
        public TextView mTextViewSocialNetwork;
        public final View mView;


        public ViewHolder(View itemView)
        {
            super(itemView);
            mView = itemView;
            mTextViewUserName = (TextView)itemView.findViewById(R.id.username);
            mTextViewSocialNetwork = (TextView)itemView.findViewById(R.id.socialNetwork);
            //itemView.setOnClickListener(this);
        }

        /*@Override
        public void onClick(View v)
        {
            // TODO: 10/23/2017 go to profile details fragment
            Log.i(UtilityVariables.tag,"Clicked profile username: "+mTextViewUserName.getText().toString());
            mListener.onListFragmentInteraction(holder.mItem);
        }*/
    }
}
