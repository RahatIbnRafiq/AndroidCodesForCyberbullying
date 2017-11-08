package com.bamboo.bullyalert.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bamboo.bullyalert.R;
import com.bamboo.bullyalert.UtilityPackage.UtilityVariables;
import com.bamboo.bullyalert.adapter.MyNotificationDetailRecyclerViewAdapter;
import com.bamboo.bullyalert.model.Comment;
import com.bamboo.bullyalert.model.Notification;
import com.bamboo.bullyalert.model.NotificationDetail;

import java.util.ArrayList;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class NotificationDetailFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_NAME = "NOTIFICATION";
    // TODO: Customize parameters
    private Notification mNotification = null;
    private OnListFragmentInteractionListener mListener;


    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mNotificationDetailAdapter;
    private Context mContext;

    public ArrayList<Comment> commentList;

    private Button mButtonSeeContext;
    private Button mButtonRight;
    private Button mButtonWrong;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public NotificationDetailFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static NotificationDetailFragment newInstance(Notification notification) {
        NotificationDetailFragment fragment = new NotificationDetailFragment();
        Log.i(UtilityVariables.tag,"Inside notification detail fragment. Initializing bundle");
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
            Log.i(UtilityVariables.tag,"bundle arguments found inside notification detail fragment");
            Log.i(UtilityVariables.tag," notification post id"+ mNotification.getmPostId());
            Log.i(UtilityVariables.tag," notification username"+ mNotification.getmUserName());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification_detail_page, container, false);

        //TextView nameTextView = (TextView) view.findViewById(R.id.name);
        //nameTextView.setText(mName);

        this.mContext = view.getContext();

        // Set the adapter
        mRecyclerView = (RecyclerView) view.findViewById(R.id.list);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                LinearLayoutManager.VERTICAL);
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

        mButtonRight.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

            }
        });



        return view;
    }


    private void getFeedback(int feedback)
    {
        mNotification.mFeedBack = feedback;
    }
    private void populateNotificationDetails()
    {
        if(mNotification != null)
        {
            commentList = mNotification.getmNewComments();
            Log.i(UtilityVariables.tag,"inside populateNotificationDetails function in notificationDetailFragment.");
            mNotificationDetailAdapter = new MyNotificationDetailRecyclerViewAdapter(commentList,mListener);
            mRecyclerView.setAdapter(mNotificationDetailAdapter);
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnListFragmentInteractionListener) {
//            mListener = (OnListFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnListFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(NotificationDetail.Item item);
    }
}
