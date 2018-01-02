package com.bamboo.bullyalert.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
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
import com.bamboo.bullyalert.NavigationActivity;
import com.bamboo.bullyalert.R;
import com.bamboo.bullyalert.UtilityPackage.UtilityFunctions;
import com.bamboo.bullyalert.UtilityPackage.UtilityVariables;
import com.bamboo.bullyalert.adapter.MyAddUserRecyclerViewAdapter;
import com.bamboo.bullyalert.model.AddUser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AddUserFragment extends Fragment implements AdapterView.OnItemSelectedListener{

    private static final String ARG_COLUMN_COUNT = "column-count";
    private OnListFragmentInteractionListener mListListener;


    private String mSocialNetworkName;
    private EditText mEditTextUserToBeSearched;
    private Button mMonitorButton;


    private RecyclerView mRecyclerView;
    private List<AddUser>mListUserSearch;
    private RecyclerView.Adapter mUserSearchResultAdapter;


    private ProgressDialog mProgressDialog ;



    private Context mContext;

    public AddUserFragment()
    {
    }

    //@SuppressWarnings("unused")
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
        /*
        if (getArguments() != null) {
            int mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_user_page, container, false);
        this.mContext = view.getContext();


        mProgressDialog = new ProgressDialog(mContext);


        Button mUserSearchButton = (Button) view.findViewById(R.id.searchButton);
        mMonitorButton = (Button)view.findViewById(R.id.monitorButton);
        mEditTextUserToBeSearched = (EditText)view.findViewById(R.id.edittext_username);


        mUserSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserSearch();
            }
        });

        mMonitorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performMonitoringRequestFunction();
            }
        });

        Spinner spinner = (Spinner) view.findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(mContext,
                R.array.social_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);


        mRecyclerView = (RecyclerView) view.findViewById(R.id.list);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                LinearLayoutManager.VERTICAL);
        mRecyclerView.addItemDecoration(dividerItemDecoration);


        mMonitorButton = (Button)view.findViewById(R.id.monitorButton);
        mRecyclerView.setVisibility(View.GONE);
        mMonitorButton.setVisibility(View.GONE);



        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListListener = null;
    }



    private void performMonitoringRequestFunction()
    {

        PerformMonitoringRequest performMonitoringRequest = new PerformMonitoringRequest();
        performMonitoringRequest.execute((Void) null);
    }



    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        this.mSocialNetworkName = parent.getItemAtPosition(position).toString();
        Log.i(UtilityVariables.tag,"social network selected: "+this.mSocialNetworkName);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public interface OnListFragmentInteractionListener
    {
        //void onListFragmentInteraction(AddUser.Item item);
    }

    private void UserSearch()
    {
        String username = mEditTextUserToBeSearched.getText().toString().trim();
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
        getUserSearchResultByUserName(username);

    }


    private void getUserSearchResultByUserName(String username)
    {
        final ProgressDialog progressDialog = new ProgressDialog(this.mContext);
        progressDialog.setMessage("Getting the users...");
        progressDialog.show();
        if(this.mSocialNetworkName.equals("Instagram"))
        {
            String urlString = UtilityVariables.INSTAGRAM_API_USER_SEARCH+username+"/?__a=1";
            Log.i(UtilityVariables.tag,urlString);
            StringRequest stringRequest = new StringRequest(Request.Method.GET,
                    urlString,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            progressDialog.dismiss();
                            mListUserSearch = new ArrayList<>();
                            Log.i(UtilityVariables.tag,"onResponse function add user fragment: "+response);
                            try
                            {
                                JSONObject jsonObject = new JSONObject(response);
                                jsonObject = jsonObject.getJSONObject("user");
                                String username = jsonObject.optString("username");
                                String user_id = jsonObject.optString("id");
                                String urlProfilePicture = jsonObject.optString("profile_pic_url");
                                String website = "";
                                String full_name = jsonObject.optString("full_name");
                                String bio = jsonObject .optString("biography");
                                AddUser user = new AddUser(user_id,username,urlProfilePicture,false,full_name,bio,website);
                                mListUserSearch.add(user);

                                mUserSearchResultAdapter = new MyAddUserRecyclerViewAdapter(mListUserSearch,mListListener,mContext);
                                mRecyclerView.setAdapter(mUserSearchResultAdapter);
                                mRecyclerView.setVisibility(View.VISIBLE);
                                mMonitorButton.setVisibility(View.VISIBLE);


                            }
                            catch (Exception e)
                            {
                                Log.i(UtilityVariables.tag,"exception while parsing json array in showUserSearchResult: "+e.toString());
                                progressDialog.dismiss();
                                mListUserSearch.clear();
                                mUserSearchResultAdapter.notifyDataSetChanged();
                                Toast.makeText(mContext,"No user found. Try with a different username please.",Toast.LENGTH_SHORT).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error)
                        {
                            progressDialog.dismiss();
                            mListUserSearch.clear();
                            mUserSearchResultAdapter.notifyDataSetChanged();
                            Log.i(UtilityVariables.tag,error.toString());
                            Toast.makeText(mContext,"No user found. Try with a different username please.",Toast.LENGTH_SHORT).show();

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
                if(resultjson != null && resultjson.optString("message") != null && resultjson.optString("success") != null)
                {
                    mMessage = resultjson.optString("message");
                    String success = resultjson.optString("success");
                    return success.equals("success");
                }
                else
                {
                    return false;
                }



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
                Log.i(UtilityVariables.tag,"hello inside add user fragment, user was inserted into the db in the server.");
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



}
