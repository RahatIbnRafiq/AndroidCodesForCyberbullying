package com.bamboo.bullyalert.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bamboo.bullyalert.Database.UserDAO;
import com.bamboo.bullyalert.R;
import com.bamboo.bullyalert.UtilityPackage.UtilityVariables;
import com.bamboo.bullyalert.adapter.MyProfileRecyclerViewAdapter;
import com.bamboo.bullyalert.model.Profile;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ProfileFragment extends Fragment
{

    private int mColumnCount = 1;
    private static final String ARG_COLUMN_COUNT = "column-count";
    private OnListFragmentInteractionListener mListener;

    private Context mContext;
    private RecyclerView mRecyclerView;
    private List<Profile> mListMonitoringUserProfiles;
    private RecyclerView.Adapter mMonitoringUserProfilesAdapter;





    private PopulateMonitoringUserProfiles populateMonitoringUserProfiles = null;
    private UserDAO mUserDao;



    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ProfileFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ProfileFragment newInstance(int columnCount) {
        ProfileFragment fragment = new ProfileFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_page, container, false);

        this.mContext = view.getContext();
        //mProgressDialog = new ProgressDialog(mContext);
        this.mUserDao = new UserDAO(mContext);


        mRecyclerView = (RecyclerView) view.findViewById(R.id.list);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                LinearLayoutManager.VERTICAL);
        mRecyclerView.addItemDecoration(dividerItemDecoration);
        mListMonitoringUserProfiles = new ArrayList<>();

        populateMonitoringUserProfilesFunction();


        return view;
    }



    private void populateMonitoringUserProfilesFunction()
    {
        populateMonitoringUserProfiles = new PopulateMonitoringUserProfiles();
        populateMonitoringUserProfiles.execute((Void) null);
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
    public interface OnListFragmentInteractionListener
    {
        // TODO: Update argument type and name
        void onListFragmentInteraction(Profile userProfile);
    }


    private void showMonitoringUserProfiles(JSONArray jsonArray)
    {
        try {
            for(int i=0;i<jsonArray.length();i++)
            {
                JSONObject userObject = jsonArray.getJSONObject(i);
                String username = userObject.optString("username");
                String userid = userObject.optString("userid");
                String urlProfilePicture = "";
                String socialNetwork = "Instagram";

                Profile monitoringUserProfile = new Profile(userid,username,urlProfilePicture,socialNetwork);
                mListMonitoringUserProfiles.add(monitoringUserProfile);

            }
            mMonitoringUserProfilesAdapter = new MyProfileRecyclerViewAdapter(mListMonitoringUserProfiles,mListener,mContext);
            mRecyclerView.setAdapter(mMonitoringUserProfilesAdapter);

        }catch (Exception e)
        {
            Log.i(UtilityVariables.tag,"exception while parsing json array in showUserSearchResult: "+e.toString());
        }

    }

    private void getMonitoringUsers()
    {

        //// TODO: 10/23/2017  just doing instagram. add other social networks later

        String urlString = UtilityVariables.INSTAGRAM_GET_MONITORING_USERS+"?email="+UtilityVariables.USER_EMAIL;
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                urlString,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try
                        {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.optString("success");
                            if(success.equals("success"))
                            {
                                JSONArray jsonArray = jsonObject.getJSONArray("users");
                                if(jsonArray.length() == 0)
                                {
                                    Toast.makeText(mContext, "Currently you are not monitoring any users", Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    showMonitoringUserProfiles(jsonArray);
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






    private class PopulateMonitoringUserProfiles extends AsyncTask<Void, Void, Boolean>
    {
        PopulateMonitoringUserProfiles()
        {
        }

        protected Boolean doInBackground(Void... params) {

            try
            {
                getMonitoringUsers();
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
