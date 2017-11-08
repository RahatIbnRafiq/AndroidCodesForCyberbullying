package com.bamboo.bullyalert.fragment;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bamboo.bullyalert.Database.User;
import com.bamboo.bullyalert.Database.UserDAO;
import com.bamboo.bullyalert.R;
import com.bamboo.bullyalert.UtilityPackage.UtilityFunctions;
import com.bamboo.bullyalert.UtilityPackage.UtilityVariables;
import com.bamboo.bullyalert.model.ProfileDetail;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;


/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ProfileDetailFragment extends Fragment implements LoginDialogFragment.LoginDialogFragmentListener{

    private static final String ARG_NAME = "userid";
    private OnListFragmentInteractionListener mListener;

    String mUserId;

    ImageView mProfilePictureView;
    TextView mUserName;
    TextView mMediaCount;
    TextView mFollowsCount;
    TextView mFollowedByCount;

    private Context mContext;

    private UserDAO mUserDao;
    private ProfileDetail mUserDetails;
    private GetAuthenticationToken getAuthToken = null;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ProfileDetailFragment()
    {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ProfileDetailFragment newInstance(String userid) {
        ProfileDetailFragment fragment = new ProfileDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_NAME, userid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            mUserId = getArguments().getString(ARG_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_profile_detail_page, container, false);

        this.mContext = view.getContext();

        mUserName = (TextView)view.findViewById(R.id.username);
        mMediaCount = (TextView)view.findViewById(R.id.media_count);
        mFollowedByCount = (TextView)view.findViewById(R.id.followedby_count);
        mFollowsCount = (TextView)view.findViewById(R.id.follows_count);
        mProfilePictureView = (ImageView)view.findViewById(R.id.profile_picture);
        this.mUserDao = new UserDAO(mContext);

        loadUserDetails();
        //showUserDetails();


        // Set the adapter
        /*RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.list);
        if (recyclerView != null) {
            Context context = view.getContext();
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(new MyProfileDetailRecyclerViewAdapter(ProfileDetail.ITEMS, mListener));
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                    LinearLayoutManager.VERTICAL);
            recyclerView.addItemDecoration(dividerItemDecoration);
        }*/
        return view;
    }


    private void showUserDetails()
    {
        try
        {
            if(this.mUserDetails != null)
            {
                this.mUserName.setText(mUserDetails.getmUserName()+" is the username for this user");
                this.mMediaCount.setText(mUserDetails.getmMediaCount()+" Total Media");
                this.mFollowsCount.setText(mUserDetails.getmFollowsCount()+" people is followed by this user");
                this.mFollowedByCount.setText(mUserDetails.getmFollowedByCount()+" users follow this user");
                Picasso.with(this.mContext)
                        .load(mUserDetails.getmUrlProfilePicture())
                        .into(this.mProfilePictureView);
            }
            else
            {
                Toast.makeText(this.mContext,"Sorry no details were found for this user",Toast.LENGTH_SHORT).show();
            }

        }catch (Exception e)
        {
            Log.i(UtilityVariables.tag,"Something went wrong in function:showUserDetails class:"
                    +this.getClass().getName()+" "+e.toString());
        }

    }


    private void getAccessToken()
    {
        FragmentManager fm = getFragmentManager();
        LoginDialogFragment loginDialogFragment = LoginDialogFragment.newInstance("instagram credentials");
        loginDialogFragment.setTargetFragment(ProfileDetailFragment.this, 300);
        loginDialogFragment.show(fm, "fragment_login_dialog");
    }



    private void loadUserDetails()
    {
        String mAuthToken;
        try {
            if(UtilityVariables.INSTAGRAM_AUTHENTICATION_TOKEN != null)
                mAuthToken = UtilityVariables.INSTAGRAM_AUTHENTICATION_TOKEN;
            else
                mAuthToken = getTokenFromDatabase();

            String urlString = UtilityVariables.INSTAGRAM_API_USER_INFORMATION+mUserId+"/?access_token="+mAuthToken;
            StringRequest stringRequest = new StringRequest(Request.Method.GET,
                    urlString,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if(response.contains("OAuthAccessTokenError"))
                            {
                                getAccessToken();
                            }
                            else {
                                Log.i(UtilityVariables.tag, "onResponse function: " + response + this.getClass().getName());
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    JSONObject metaJSon = jsonObject.getJSONObject("meta");
                                    String code = metaJSon.getString("code");
                                    if (code.equals("200")) {
                                        JSONObject data = jsonObject.getJSONObject("data");
                                        String username = data.optString("username");
                                        String userid = data.optString("id");
                                        String bio = data.optString("bio");
                                        String website = data.optString("website");
                                        String profile_picture = data.optString("profile_picture");
                                        String fullname = data.optString("full_name");
                                        int media_count = data.getJSONObject("counts").optInt("media");
                                        int follows_count = data.getJSONObject("counts").optInt("follows");
                                        int followedby_count = data.getJSONObject("counts").optInt("followed_by");

                                        mUserDetails = new ProfileDetail(userid, username, fullname, profile_picture,
                                                bio, website, media_count, follows_count, followedby_count);
                                        showUserDetails();


                                    }

                                } catch (Exception e) {
                                    Log.i(UtilityVariables.tag, "Error while getting the users json result." + e.toString() + this.getClass().getName());
                                }
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

        } catch (Exception e)
        {
            Log.i(UtilityVariables.tag,"Exception in loadUserDetails in "+this.getClass().getName()+" exception: "+e.toString());
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

    @Override
    public void onFinishLoginDialog(String code)
    {
        getAuthToken = new GetAuthenticationToken(code);
        getAuthToken.execute((Void) null);

    }

    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(String username);
    }


    private String getTokenFromDatabase()
    {
        User user = mUserDao.fetchUser(UtilityVariables.USER_EMAIL);
        if(user == null)
        {
            //not in the database.
            return null;
        }
        else if(user.instagramToken != null)
        {
            //instagram token exists
            UtilityVariables.INSTAGRAM_AUTHENTICATION_TOKEN = user.instagramToken;
            return user.instagramToken;
        }
        else if(user.instagramToken == null)
        {
            //instagram token does not exist
            return null;
        }
        return null;

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
                    loadUserDetails();
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
