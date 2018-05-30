package com.bamboo.bullyalert.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import com.bamboo.bullyalert.R;
import com.bamboo.bullyalert.fragment.SettingFragment.OnListFragmentInteractionListener;
import com.bamboo.bullyalert.model.Setting;
import java.util.List;

public class MySettingRecyclerViewAdapter extends RecyclerView.Adapter<MySettingRecyclerViewAdapter.ViewHolder> {

    private final List<Setting.Item> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MySettingRecyclerViewAdapter(List<Setting.Item> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_setting, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mNameView.setText(mValues.get(position).name);
        if (mValues.get(position).info == null) {
            holder.mOptionOneView.setText(mValues.get(position).optionOneTitle);
            holder.mOptionOneView.setChecked(mValues.get(position).optionOne);
            holder.mOptionTwoView.setText(mValues.get(position).optionTwoTitle);
            holder.mOptionTwoView.setChecked(mValues.get(position).optionTwo);
            holder.mInfoView.setVisibility(View.GONE);
        } else {
            holder.mOptionOneView.setVisibility(View.GONE);
            holder.mOptionTwoView.setVisibility(View.GONE);
            holder.mInfoView.setText(mValues.get(position).info);
        }
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onListFragmentInteraction(holder.mItem);
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
        public final TextView mNameView;
        public final CheckBox mOptionOneView;
        public final CheckBox mOptionTwoView;
        public final EditText mInfoView;
        public Setting.Item mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mNameView = (TextView) view.findViewById(R.id.name);
            mOptionOneView = (CheckBox) view.findViewById(R.id.option_one);
            mOptionTwoView = (CheckBox) view.findViewById(R.id.option_two);
            mInfoView = (EditText) view.findViewById(R.id.info);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mNameView.getText() + "'";
        }
    }
}
