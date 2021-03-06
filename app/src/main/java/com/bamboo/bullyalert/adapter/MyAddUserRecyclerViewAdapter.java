package com.bamboo.bullyalert.adapter;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import com.bamboo.bullyalert.R;
import com.bamboo.bullyalert.UtilityPackage.UtilityVariables;
import com.bamboo.bullyalert.fragment.AddUserFragment.OnListFragmentInteractionListener;
import com.bamboo.bullyalert.model.AddUser;
import com.squareup.picasso.Picasso;
import java.util.HashMap;
import java.util.List;

public class MyAddUserRecyclerViewAdapter extends RecyclerView.Adapter<MyAddUserRecyclerViewAdapter.ViewHolder>
{
    private List<AddUser>mListUserSearch;
    private OnListFragmentInteractionListener mListener;
    private Context mContext;
    private HashMap<String,AddUser> mToBeMonitoredUsersMap = new HashMap<>();

    public MyAddUserRecyclerViewAdapter(List<AddUser> listUserSearch, OnListFragmentInteractionListener listener,Context context)
    {
        try {
            this.mListUserSearch = listUserSearch;
            this.mListener = listener;
            this.mContext = context;
            for(int i=0;i<this.mListUserSearch.size();i++)
            {
                this.mToBeMonitoredUsersMap.put(listUserSearch.get(i).getmUserName(),listUserSearch.get(i));
            }

        }catch (Exception e)
        {
            Log.i(UtilityVariables.tag, "Exception in "+this.getClass().getName()+" constructor "+e.toString());
        }

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        try
        {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_add_user,parent,false);
            return new ViewHolder(v);
        }catch (Exception e)
        {
            Log.i(UtilityVariables.tag, "Exception in onCreateViewHolder function in "+this.getClass().getName()+e.toString());
            return null;
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        AddUser addUser = this.mListUserSearch.get(position);
        try
        {
            holder.mTextViewUserId.setText("User ID: "+addUser.getmUserId());
            holder.mTextViewUserName.setText("User Name: "+addUser.getmUserName());
            holder.mCheckedView.setChecked(addUser.ismChecked());
            holder.mTextViewFullName.setText("Full Name: "+addUser.getFullName());
            holder.mTextViewBio.setText("Bio: "+addUser.getBio());
            Picasso.with(mContext)
            .load(addUser.getmProfilePictureUrl())
            .into(holder.mImageViewProfilePicture);
        }catch (Exception e)
        {
            Log.i(UtilityVariables.tag,"Exception in onBindViewHolder function in : "+this.getClass().getName()+e.toString());
        }
    }

    @Override
    public int getItemCount() {
        return this.mListUserSearch.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private int originalHeight = 0;
        private boolean isViewExpanded = false;
        private TextView mTextViewUserName;
        private TextView mTextViewUserId;
        private CheckBox mCheckedView;
        private ImageView mImageViewProfilePicture;
        private TextView mTextViewFullName;
        private TextView mTextViewBio;
        private CardView mVisibleCardView;
        private CardView mInVisibleCardView;

        public ViewHolder(View itemView)
        {
            super(itemView);
            mTextViewUserId = (TextView)itemView.findViewById(R.id.userid);
            mTextViewUserName = (TextView)itemView.findViewById(R.id.username);
            mCheckedView = (CheckBox) itemView.findViewById(R.id.checkbox);
            mImageViewProfilePicture = (ImageView)itemView.findViewById(R.id.profile_picture);
            mTextViewFullName = (TextView)itemView.findViewById(R.id.fullname);
            mTextViewBio = (TextView)itemView.findViewById(R.id.bio);
            mVisibleCardView = (CardView)itemView.findViewById(R.id.visibleCardView);
            mInVisibleCardView = (CardView)itemView.findViewById(R.id.invisibleCardView);
            itemView.setOnClickListener(this);
            mCheckedView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    ViewGroup vGroup = (ViewGroup) v.getParent();
                    TextView textView = (TextView)vGroup.getChildAt(2);
                    String username = textView.getText().toString();
                    username = username.substring(username.indexOf(":")+1).trim();
                    mToBeMonitoredUsersMap.get(username).setmChecked(mCheckedView.isChecked());
                }
            });
            if (!isViewExpanded) {
                mInVisibleCardView.setVisibility(View.GONE);
                mInVisibleCardView.setEnabled(false);
            }
        }

        @Override
        public void onClick(final View view) {
            if (originalHeight == 0) {
                originalHeight = view.getHeight();
            }

            ValueAnimator valueAnimator;
            if (!isViewExpanded)
            {
                mInVisibleCardView.setVisibility(View.VISIBLE);
                mInVisibleCardView.setEnabled(true);
                isViewExpanded = true;
                valueAnimator = ValueAnimator.ofInt(originalHeight, originalHeight + (int) (originalHeight * 2.0)); // These values in this method can be changed to expand however much you like
            }
            else
            {
                isViewExpanded = false;
                valueAnimator = ValueAnimator.ofInt(originalHeight + (int) (originalHeight * 2.0), originalHeight);

                Animation a = new AlphaAnimation(1.00f, 0.00f); // Fade out

                a.setDuration(200);
                a.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation)
                    {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        mInVisibleCardView.setVisibility(View.INVISIBLE);
                        mInVisibleCardView.setEnabled(false);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation)
                    {
                    }
                });
                mInVisibleCardView.startAnimation(a);
            }
            valueAnimator.setDuration(200);
            valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator animation) {
                    view.getLayoutParams().height = (Integer) animation.getAnimatedValue();
                    view.requestLayout();
                }
            });
            valueAnimator.start();
        }
    }
}
