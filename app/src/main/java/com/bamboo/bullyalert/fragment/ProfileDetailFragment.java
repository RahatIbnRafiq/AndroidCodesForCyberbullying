package com.bamboo.bullyalert.fragment;

import android.app.ProgressDialog;
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
import com.bamboo.bullyalert.UtilityPackage.UtilityInstagramWebsite;
import com.bamboo.bullyalert.UtilityPackage.UtilityVariables;
import com.bamboo.bullyalert.model.ProfileDetail;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

public class ProfileDetailFragment extends Fragment
{

    private static final String ARG_NAME = "username";
    private OnListFragmentInteractionListener mListener;

    String mUserId;
    String mUserName;

    ImageView mProfilePictureView;
    TextView mUserNameView;
    TextView mMediaCountView;
    TextView mFollowsCountView;
    TextView mFollowedByCountView;

    private Context mContext;

    private UserDAO mUserDao;
    private ProfileDetail mUserDetails;


    public ProfileDetailFragment()
    {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ProfileDetailFragment newInstance(String username) {
        ProfileDetailFragment fragment = new ProfileDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_NAME, username);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            mUserName = getArguments().getString(ARG_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_profile_detail_page, container, false);

        this.mContext = view.getContext();

        mUserNameView = (TextView)view.findViewById(R.id.username);
        mMediaCountView = (TextView)view.findViewById(R.id.media_count);
        mFollowedByCountView = (TextView)view.findViewById(R.id.followedby_count);
        mFollowsCountView = (TextView)view.findViewById(R.id.follows_count);
        mProfilePictureView = (ImageView)view.findViewById(R.id.profile_picture);
        this.mUserDao = new UserDAO(mContext);

        loadUserDetails();

        return view;
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
        // TODO: Update argument type and name
        void onListFragmentInteraction(String username);
    }

    private void showUserDetails()
    {
        try
        {
            if(this.mUserDetails != null)
            {
                this.mUserNameView.setText(mUserDetails.getmUserName()+" is the username for this user");
                this.mMediaCountView.setText(mUserDetails.getmMediaCount()+" Total Media");
                this.mFollowsCountView.setText(mUserDetails.getmFollowsCount()+" people is followed by this user");
                this.mFollowedByCountView.setText(mUserDetails.getmFollowedByCount()+" users follow this user");
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

    private void loadUserDetails()
    {
        Log.i(UtilityVariables.tag,"inside load user details function");
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage("Getting the user details...");
        progressDialog.show();
        try
        {
            String urlString = UtilityVariables.URL_ROOT_INSTAGRAM_WEBSITE+mUserName+"/?__a=1";
            StringRequest stringRequest = new StringRequest(Request.Method.GET, urlString,
                    new Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String response)
                        {
                            progressDialog.dismiss();
                            try
                            {
                                JSONObject jsonObject = new JSONObject(response);
                                jsonObject = jsonObject.getJSONObject("user");
                                String mUserId = jsonObject.optString("id");
                                String mUserName= jsonObject.optString("username");
                                String mFullName= jsonObject.optString("full_name");
                                String mUrlProfilePicture= jsonObject.optString("profile_pic_url");
                                String mBio= jsonObject.optString("biography");
                                String mWebsite= "";
                                int mMediaCount= Integer.parseInt(jsonObject.getJSONObject("media").optString("count"));

                                int mFollowsCount= jsonObject.getJSONObject("follows").optInt("count");
                                int mFollowedByCount= jsonObject.getJSONObject("followed_by").optInt("count");
                                mUserDetails = null;
                                mUserDetails = new ProfileDetail(mUserId, mUserName,  mFullName,  mUrlProfilePicture,
                                        mBio,  mWebsite,  mMediaCount,  mFollowsCount,  mFollowedByCount);

                                showUserDetails();



                            }
                            catch (JSONException e)
                            {

                            }
                        }
                    },
                    new Response.ErrorListener()
                    {
                        @Override
                        public void onErrorResponse(VolleyError error)
                        {
                            progressDialog.dismiss();
                            Log.i(UtilityVariables.tag,"getInstagramUserProfileDetail response: "+error.toString());
                        }
                    }
            );

            RequestQueue requestQueue = Volley.newRequestQueue(mContext);
            requestQueue.add(stringRequest);


        }catch (Exception e)
        {

        }
    }

}
