package com.bamboo.bullyalert.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bamboo.bullyalert.Database.User;
import com.bamboo.bullyalert.Database.UserDAO;
import com.bamboo.bullyalert.NavigationActivity;
import com.bamboo.bullyalert.R;
import com.bamboo.bullyalert.UtilityPackage.UtilityFunctions;
import com.bamboo.bullyalert.UtilityPackage.UtilityVariables;
import com.bamboo.bullyalert.adapter.MyNotificationRecyclerViewAdapter;
import com.bamboo.bullyalert.model.Notification;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NotificationFragment extends Fragment
{
    private static final String ARG_NOTIFICATION = "NOTIFICATION";

    private OnListFragmentInteractionListener mListener;

    private Context mContext;

    private HashMap<String,Notification>mNotifications;
    public ArrayList<Notification>mNotificationList;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mNotificationsAdapter;
    private Button mAppStatusButton;


    private UserDAO mUserDao;

    public NotificationFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static NotificationFragment newInstance(HashMap<String,Notification> notification) {
        NotificationFragment fragment = new NotificationFragment();
        if(notification != null)
        {
            Log.i(UtilityVariables.tag, "inside notification fragment. size of the notification list: " + notification.size());
        }
        else
        {
            Log.i(UtilityVariables.tag, "inside notification fragment. size of the notification list: null");
        }
        Bundle args = new Bundle();
        args.putSerializable(ARG_NOTIFICATION,notification);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null)
        {
            mNotifications = (HashMap<String, Notification>) getArguments().getSerializable(ARG_NOTIFICATION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification_page, container, false);
        this.mContext = view.getContext();
        this.mUserDao = new UserDAO(mContext);

        mAppStatusButton = (Button) view.findViewById(R.id.appStatusButton);

        mAppStatusButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(mContext,UtilityVariables.APP_STATUS,Toast.LENGTH_LONG).show();
            }
        });




        // Set the adapter
        mRecyclerView = (RecyclerView) view.findViewById(R.id.list);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        this.mNotificationList = new ArrayList<>();

        populateNotification();

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target)
            {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir)
            {
                Log.i(UtilityVariables.tag,"swiped item: "+viewHolder.getAdapterPosition());
                Notification n = mNotificationList.get(viewHolder.getAdapterPosition());
                n.setmFeedBack(-2);
                mNotificationList.remove(viewHolder.getAdapterPosition());
                mNotificationsAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                LinearLayoutManager.VERTICAL);
        mRecyclerView.addItemDecoration(dividerItemDecoration);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);


        return view;
    }

    private void populateNotification()
    {
        if(mNotifications != null)
        {
            Log.i(UtilityVariables.tag, "inside populateNotification function. size of the notification map: " + mNotifications.size());
            for (Map.Entry<String, Notification> entry : mNotifications.entrySet())
            {
                if(entry.getValue().getmFeedBack() == -1)
                    mNotificationList.add(entry.getValue());
            }

            mNotificationsAdapter = new MyNotificationRecyclerViewAdapter(mNotificationList,mListener);
            mRecyclerView.setAdapter(mNotificationsAdapter);
        }


    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(Notification item);
    }


}
