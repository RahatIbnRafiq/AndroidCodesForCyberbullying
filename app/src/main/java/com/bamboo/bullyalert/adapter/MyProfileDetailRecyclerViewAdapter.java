package com.bamboo.bullyalert.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bamboo.bullyalert.R;
import com.bamboo.bullyalert.fragment.ProfileDetailFragment.OnListFragmentInteractionListener;
import com.bamboo.bullyalert.model.ProfileDetail;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link ProfileDetail.Item} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyProfileDetailRecyclerViewAdapter //extends RecyclerView.Adapter<MyProfileDetailRecyclerViewAdapter.ViewHolder>
{

    /*private final List<ProfileDetail.Item> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyProfileDetailRecyclerViewAdapter(List<ProfileDetail.Item> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_profile_detail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mAttributeView.setText(mValues.get(position).attribute);
        holder.mValueView.setText(mValues.get(position).value);

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
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mAttributeView;
        public final TextView mValueView;
        public ProfileDetail.Item mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mAttributeView = (TextView) view.findViewById(R.id.attribute);
            mValueView = (TextView) view.findViewById(R.id.value);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mValueView.getText() + "'";
        }

    }*/
}
