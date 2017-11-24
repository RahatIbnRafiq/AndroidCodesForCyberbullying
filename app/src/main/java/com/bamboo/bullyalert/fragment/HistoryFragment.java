package com.bamboo.bullyalert.fragment;

import android.content.Context;
import android.os.AsyncTask;
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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bamboo.bullyalert.R;
import com.bamboo.bullyalert.UtilityPackage.UtilityVariables;
import com.bamboo.bullyalert.adapter.MyHistoryRecyclerViewAdapter;
import com.bamboo.bullyalert.model.History;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class HistoryFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 3;
    private OnListFragmentInteractionListener mListener;

    private PopulateHistory populateHistory;
    private Context mContext;
    private List<History> mHistoryList;
    private RecyclerView.Adapter mHistoryAdapter;
    private RecyclerView mRecyclerView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public HistoryFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static HistoryFragment newInstance(int columnCount) {
        HistoryFragment fragment = new HistoryFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history_page, container, false);
        mContext = view.getContext();
        mHistoryList = new ArrayList<>();

        // Set the adapter
        mRecyclerView = (RecyclerView) view.findViewById(R.id.list);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                LinearLayoutManager.VERTICAL);
        mRecyclerView.addItemDecoration(dividerItemDecoration);
        populateHistoryFunction();
        return view;
    }


    private void populateHistoryFunction()
    {
        populateHistory = new PopulateHistory();
        populateHistory.execute((Void) null);
    }

    private void showHistory(JSONArray jsonArray)
    {
        Log.i(UtilityVariables.tag,"inside show history function");
        HashMap<String,History> historyByUserMap = new HashMap<>();
        try {
            for(int i=0;i<jsonArray.length();i++)
            {
                JSONObject historyObject = jsonArray.getJSONObject(i);
                String username = historyObject.optString("username");
                String osn_name = historyObject.optString("osn_name");
                String appNotificationResult = historyObject.optString("appNotificationResult");
                History h;
                if(historyByUserMap.containsKey(username))
                {
                    h = historyByUserMap.get(username);
                    if(Double.parseDouble(appNotificationResult) > 0.5)
                        h.mBullyingCount+=1;
                }
                else
                {
                    h = new History(username,osn_name,0);
                    if(Double.parseDouble(appNotificationResult) > 0.5)
                        h.mBullyingCount+=1;
                    historyByUserMap.put(username,h);
                }


            }

            for (Map.Entry<String, History> entry : historyByUserMap.entrySet()) {
                History history = entry.getValue();
                mHistoryList.add(history);
            }

            mHistoryAdapter = new MyHistoryRecyclerViewAdapter(mHistoryList,mListener);
            mRecyclerView.setAdapter(mHistoryAdapter);


        }catch (Exception e)
        {
            Log.i(UtilityVariables.tag,"exception while parsing json array in showHistory: "+e.toString());
        }
    }

    private void getHistory()
    {
        String urlString = UtilityVariables.GET_NOTIFICATIONS+"?email="+UtilityVariables.USER_EMAIL;
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                urlString,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try
                        {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.optString("success");
                            Log.i(UtilityVariables.tag,"response in the history: getting notifications: "+jsonObject.toString());
                            if(success.equals("success"))
                            {
                                JSONArray jsonArray = jsonObject.getJSONArray("notifications");
                                if(jsonArray.length() == 0)
                                {
                                    Toast.makeText(mContext, "Currently no history", Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    showHistory(jsonArray);
                                }
                            }
                            else
                            {

                            }


                        }
                        catch (Exception e)
                        {
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        Log.i(UtilityVariables.tag,"volley error"+error.toString());
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.add(stringRequest);
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
        void onListFragmentInteraction(History item);
    }


    private class PopulateHistory extends AsyncTask<Void, Void, Boolean>
    {
        PopulateHistory()
        {
        }

        protected Boolean doInBackground(Void... params) {

            try
            {
                getHistory();
                return true;

            } catch (Exception e) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success)
            {

            }
            else
            {
            }
        }

        @Override
        protected void onCancelled()
        {

        }
    }
}
