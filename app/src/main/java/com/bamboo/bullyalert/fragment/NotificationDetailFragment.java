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
import android.widget.Button;
import com.bamboo.bullyalert.Database.NotificationFeedback;
import com.bamboo.bullyalert.Database.NotificationFeedbackDAO;
import com.bamboo.bullyalert.R;
import com.bamboo.bullyalert.UtilityPackage.UtilityVariables;
import com.bamboo.bullyalert.adapter.MyNotificationDetailRecyclerViewAdapter;
import com.bamboo.bullyalert.model.Comment;
import com.bamboo.bullyalert.model.Notification;
import com.bamboo.bullyalert.model.NotificationDetail;
import java.util.ArrayList;

public class NotificationDetailFragment extends Fragment {
    private static final String ARG_NAME = "NOTIFICATION";
    private Notification mNotification = null;
    private OnListFragmentInteractionListener mListener;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mNotificationDetailAdapter;
    private Context mContext;
    public ArrayList<Comment> commentList;
    private Button mButtonSeeContext;
    private Button mButtonRight;
    private Button mButtonWrong;
    private NotificationFeedbackDAO mNotificationFeedbackDao;

    public NotificationDetailFragment() {
    }

    public static NotificationDetailFragment newInstance(Notification notification) {
        NotificationDetailFragment fragment = new NotificationDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_NAME, notification);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mNotification= (Notification)getArguments().getSerializable(ARG_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification_detail_page, container, false);
        this.mContext = view.getContext();
        this.mNotificationFeedbackDao = new NotificationFeedbackDAO(mContext);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.list);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(), LinearLayoutManager.VERTICAL);
        mRecyclerView.addItemDecoration(dividerItemDecoration);
        populateNotificationDetails();
        mButtonSeeContext = (Button) view.findViewById(R.id.button_see_context);
        mButtonRight = (Button) view.findViewById(R.id.right);
        mButtonWrong = (Button) view.findViewById(R.id.wrong);
        mButtonSeeContext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(commentList.size() < (mNotification.getmNewComments().size()+mNotification.getmOldComments().size()))
                {
                    commentList.addAll(mNotification.getmOldComments());
                    mNotificationDetailAdapter.notifyItemInserted(commentList.size()-1);
                }
            }
        });
        if(mNotification.getmFeedBack() != -1)
        {
            mButtonWrong.setEnabled(false);
            mButtonRight.setEnabled(false);
        }
        else
        {
            mButtonRight.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if(mNotification.getmFeedBack() == -1)
                        getFeedback(1);
                }
            });
            mButtonWrong.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if(mNotification.getmFeedBack() == -1)
                        getFeedback(0);
                }
            });
        }
        return view;
    }

    private void getFeedback(int feedbackValue)
    {
        mNotification.mFeedBack = feedbackValue;
        String comments = "";
        for(int i=0; i< mNotification.getmNewComments().size();i++)
        {
            comments = comments + mNotification.getmNewComments().get(i)+"--->";
        }
        String feedback = feedbackValue+"";
        String predicted = mNotification.getmLevel()+"";
        String notificationId = mNotification.getmNotificationId();
        NotificationFeedback notificationFeedback = new NotificationFeedback(UtilityVariables.USER_EMAIL, notificationId,comments,predicted,feedback);
        long s = mNotificationFeedbackDao.insertUser(notificationFeedback);
    }

    private void populateNotificationDetails()
    {
        if(mNotification != null)
        {
            commentList = mNotification.getmNewComments();
            mNotificationDetailAdapter = new MyNotificationDetailRecyclerViewAdapter(commentList,mListener);
            mRecyclerView.setAdapter(mNotificationDetailAdapter);
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
        void onListFragmentInteraction(NotificationDetail.Item item);
    }
}
