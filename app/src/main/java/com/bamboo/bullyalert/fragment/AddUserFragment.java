package com.bamboo.bullyalert.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bamboo.bullyalert.Database.User;
import com.bamboo.bullyalert.Database.UserDAO;
import com.bamboo.bullyalert.NavigationActivity;
import com.bamboo.bullyalert.R;
import com.bamboo.bullyalert.UtilityPackage.UtilityFunctions;
import com.bamboo.bullyalert.UtilityPackage.UtilityVariables;
import com.bamboo.bullyalert.adapter.MyAddUserRecyclerViewAdapter;
import com.bamboo.bullyalert.model.AddUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class AddUserFragment extends Fragment implements LoginDialogFragment.LoginDialogFragmentListener,AdapterView.OnItemSelectedListener{
    /*
    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    */
    private int mColumnCount = 1;
    private static final String ARG_COLUMN_COUNT = "column-count";
    private OnListFragmentInteractionListener mListListener;


    private String mSocialNetworkName;
    private EditText mEditTextUserToBeSearched;
    //private RecyclerView mRecyclerViewUserSearchList;
    private Button mMonitorButton;
    private Button mUserSearchButton;


    private RecyclerView mRecyclerView;
    private List<AddUser>mListUserSearch;
    private RecyclerView.Adapter mUserSearchResultAdapter;



    private GetAuthenticationToken getAuthToken = null;
    private PerformMonitoringRequest performMonitoringRequest = null;
    private ProgressDialog mProgressDialog ;
    //private UserSearchByUserName userSearchByUserName = null;


    //private String mAuthenticationToken;


    private UserDAO mUserDao;

    private Context mContext;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public AddUserFragment()
    {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static AddUserFragment newInstance(int columnCount) {
        AddUserFragment fragment = new AddUserFragment();
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
        View view = inflater.inflate(R.layout.fragment_add_user_page, container, false);
        this.mContext = view.getContext();

        this.mUserDao = new UserDAO(mContext);

        mProgressDialog = new ProgressDialog(mContext);


        mUserSearchButton = (Button)view.findViewById(R.id.searchButton);
        mMonitorButton = (Button)view.findViewById(R.id.monitorButton);
        mEditTextUserToBeSearched = (EditText)view.findViewById(R.id.edittext_username);


        mUserSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLoginDialog();
            }
        });

        mMonitorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performMonitoringRequestFunction();
            }
        });

        Spinner spinner = (Spinner) view.findViewById(R.id.spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        // add the list of social networks here when you add new OSNs like fb and others
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(mContext,
                R.array.social_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);


        // Set the adapter
        mRecyclerView = (RecyclerView) view.findViewById(R.id.list);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                LinearLayoutManager.VERTICAL);
        mRecyclerView.addItemDecoration(dividerItemDecoration);


        //at first the button and the recyclerview of user list search will be invisible
        //mRecyclerViewUserSearchList = (RecyclerView) view.findViewById(R.id.list);
        mMonitorButton = (Button)view.findViewById(R.id.monitorButton);
        mRecyclerView.setVisibility(View.GONE);
        mMonitorButton.setVisibility(View.GONE);



        return view;
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
        mListListener = null;
    }



    private void performMonitoringRequestFunction()
    {

        performMonitoringRequest = new PerformMonitoringRequest();
        performMonitoringRequest.execute((Void) null);
    }

    private String getTokenFromDatabase()
    {
        User user = mUserDao.fetchUser(UtilityVariables.USER_EMAIL);
        if(user == null)
        {
            //not in the database.
            return null;
        }
        else if(this.mSocialNetworkName.equals("Instagram") && user.instagramToken != null)
        {
            //instagram token exists
            UtilityVariables.INSTAGRAM_AUTHENTICATION_TOKEN = user.instagramToken;
            return user.instagramToken;
        }
        else if(this.mSocialNetworkName.equals("Instagram") && user.instagramToken == null)
        {
            //instagram token does not exist
            return null;
        }
        return null;

    }




    private void showLoginDialog()
    {
        if(UtilityVariables.INSTAGRAM_AUTHENTICATION_TOKEN != null)
        {
            UserSearch(UtilityVariables.INSTAGRAM_AUTHENTICATION_TOKEN);
        }
        else
            {
            String token = getTokenFromDatabase();
            if (token == null) {
                FragmentManager fm = getFragmentManager();
                LoginDialogFragment loginDialogFragment = LoginDialogFragment.newInstance(this.mSocialNetworkName + " credentials");
                loginDialogFragment.setTargetFragment(AddUserFragment.this, 300);
                loginDialogFragment.show(fm, "fragment_login_dialog");
            } else
            {
                Log.i(UtilityVariables.tag, "token found in the database!!");
                UserSearch(token);
            }
        }


    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        this.mSocialNetworkName = parent.getItemAtPosition(position).toString();
        Log.i(UtilityVariables.tag,"social network selected: "+this.mSocialNetworkName);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onFinishLoginDialog(String code) {

        Log.i(UtilityVariables.tag,"web view finished. got the code: "+code);
        getAuthToken = new GetAuthenticationToken(code);
        getAuthToken.execute((Void) null);

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
        //void onListFragmentInteraction(AddUser.Item item);
    }

    private void UserSearch(String authToken)
    {
        String username = mEditTextUserToBeSearched.getText().toString().trim();
        String authenticationToken = authToken;
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
        getUserSearchResultByUserName(username,authenticationToken);

    }

    private void showUserSearchResult(JSONArray jsonArray)
    {
        try {
            for(int i=0;i<jsonArray.length();i++)
            {
                JSONObject userObject = jsonArray.getJSONObject(i);
                Log.i(UtilityVariables.tag,"parsing json, userobject: "+userObject.toString());
                String username = userObject.optString("username");
                String userid = userObject.optString("id");
                String urlProfilePicture = userObject.optString("profile_picture");
                String website = userObject.optString("website");
                String fullname = userObject.optString("full_name");
                String bio = userObject.optString("bio");
                AddUser user = new AddUser(userid,username,urlProfilePicture,false,fullname,bio,website);
                mListUserSearch.add(user);

            }

            mUserSearchResultAdapter = new MyAddUserRecyclerViewAdapter(mListUserSearch,mListListener,this.mContext);
            mRecyclerView.setAdapter(mUserSearchResultAdapter);
            mRecyclerView.setVisibility(View.VISIBLE);
            mMonitorButton.setVisibility(View.VISIBLE);

        }catch (Exception e)
        {
            Log.i(UtilityVariables.tag,"exception while parsing json array in showUserSearchResult: "+e.toString());
        }

    }

    private void getUserSearchResultByUserName(String username, String authenticationToken)
    {
        final ProgressDialog progressDialog = new ProgressDialog(this.mContext);
        progressDialog.setMessage("Getting the users...");
        progressDialog.show();
        if(this.mSocialNetworkName.equals("Instagram"))
        {
            String urlString = UtilityVariables.INSTAGRAM_API_USER_SEARCH+username+"&access_token="+authenticationToken;
            StringRequest stringRequest = new StringRequest(Request.Method.GET,
                    urlString,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            progressDialog.dismiss();
                            Log.i(UtilityVariables.tag,"onResponse function: "+response);
                            try {
                                mListUserSearch = new ArrayList<>();
                                JSONObject jsonObject = new JSONObject(response);
                                JSONObject metaJSon = jsonObject.getJSONObject("meta");
                                String code = metaJSon.getString("code");
                                if(code.equals("200"))
                                {
                                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                                    if(jsonArray.length() == 0)
                                    {
                                        Toast.makeText(mContext,
                                                "No users found. Try with another username please",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                    else
                                    {
                                        showUserSearchResult(jsonArray);
                                    }
                                }
                                else
                                {
                                    Log.i(UtilityVariables.tag,"Error while getting the users json result."+code);
                                }
                            }catch (JSONException e)
                            {
                                Log.i(UtilityVariables.tag,"Error while getting the users json result."+e.toString());
                            }


                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.i(UtilityVariables.tag,error.toString());

                        }
                    });
            RequestQueue requestQueue = Volley.newRequestQueue(mContext);
            requestQueue.add(stringRequest);
        }
    }


    private class PerformMonitoringRequest extends AsyncTask<Void, Void, Boolean>
    {

        String mMessage;



        PerformMonitoringRequest() {

        }

        protected Boolean doInBackground(Void... params)
        {
            try {
                mProgressDialog.setMessage("Processing your monitoring request...");
                JSONObject data = new JSONObject();
                data.put("email",UtilityVariables.USER_EMAIL);
                JSONArray array = new JSONArray();
                int j = 0;
                for(int i =0;i<mListUserSearch.size();i++)
                {
                    if(mListUserSearch.get(i).ismChecked())
                    {
                        JSONObject temp = new JSONObject();
                        temp.put("email",UtilityVariables.USER_EMAIL);
                        temp.put("userid",mListUserSearch.get(i).getmUserId());
                        temp.put("username",mListUserSearch.get(i).getmUserName());
                        array.put(j,temp);
                        j+=1;
                    }

                }
                data.put("data",array);
                Log.i(UtilityVariables.tag,"debugging error in PerformMonitoringRequest inner class: "+data.toString());

                String urlString = UtilityVariables.INSTAGRAM_MONITOR_USER_SERVER;
                JSONObject resultjson = UtilityFunctions.getJsonStringFromPostRequestUrlString(urlString,data);
                mMessage = resultjson.optString("message");
                String success = resultjson.optString("success");
                if(success.equals("succeess"))
                    return true;
                return false;

            }catch (Exception e)
            {
                Log.i(UtilityVariables.tag,e.toString());
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mProgressDialog.dismiss();
            if(mMessage != null)
            {
                Toast.makeText(mContext, mMessage, Toast.LENGTH_SHORT).show();
            }
            else
            {
                if(UtilityFunctions.isNetworkAvailable(mContext))
                {
                    Toast.makeText(mContext, "Something went wrong. Please try again.", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(mContext, "Please make sure the internet is on", Toast.LENGTH_SHORT).show();
                }

            }

            if (success)
            {
                Intent intent = new Intent(mContext, NavigationActivity.class);
                startActivity(intent);
            }

        }

        @Override
        protected void onCancelled()
        {
            Log.i(UtilityVariables.tag,"Process was cancelled");
        }
    }


    private class GetAuthenticationToken extends AsyncTask<Void, Void, Boolean>
    {
        private final String mCode;
        String authToken = null;

        GetAuthenticationToken(String code) {
            mCode = code;
        }

        protected Boolean doInBackground(Void... params) {

            try {

                String urlString = UtilityVariables.INSTAGRAM_GET_ACCESS_TOKEN+"?code="+mCode;
                JSONObject resultjson = UtilityFunctions.getJsonStringFromGetRequestUrlString(urlString);

                if(resultjson.optString("success") != null && resultjson.optString("success").equals("success"))
                {
                    resultjson = new JSONObject(resultjson.optString("token"));
                    Log.i(UtilityVariables.tag,"Trying to parse the token from server response. "+resultjson.toString());
                    authToken = resultjson.optString("access_token");
                    Log.i(UtilityVariables.tag,"got the auth token: "+authToken);
                    return true;
                }
                else
                {
                    return false;
                }

            } catch (Exception e) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success)
            {
                if(authToken != null)
                {
                    if(mUserDao == null)
                        mUserDao = new UserDAO(mContext);
                    mUserDao.deleteUser(UtilityVariables.USER_EMAIL);
                    User user = new User(UtilityVariables.USER_EMAIL,authToken);
                    UtilityVariables.INSTAGRAM_AUTHENTICATION_TOKEN = authToken;
                    mUserDao.insertUser(user);
                    Log.i(UtilityVariables.tag,"User has been inserted into the database!");
                    UserSearch(authToken);
                }

            }
            else
            {
                Log.i(UtilityVariables.tag,"Error happened while getting authentication token");
            }
        }

        @Override
        protected void onCancelled()
        {
            Log.i(UtilityVariables.tag,"Process was cancelled");
        }
    }
}
