package com.bamboo.bullyalert.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bamboo.bullyalert.Database.UserDAO;
import com.bamboo.bullyalert.R;
import com.bamboo.bullyalert.UtilityPackage.UtilityVariables;
import com.bamboo.bullyalert.model.ProfileDetail;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
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
        void onListFragmentInteraction(String username);
    }


    private class JsoupAsyncTask extends AsyncTask<Void, Void, Boolean> {
        String usernameToSearch;
        ProgressDialog dialog;
        Context context;
        ProfileDetail userDetails;
        public JsoupAsyncTask(String usernameToSearch,ProgressDialog dialog, Context context) {
            super();
            this.usernameToSearch = usernameToSearch;
            this.dialog = dialog;
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                Document doc;
                String htmlPageUrl = "https://www.instagram.com/"+this.usernameToSearch+"/";
                doc = Jsoup.connect(htmlPageUrl).get();
                Elements scriptElements = doc.getElementsByTag("script");
                for (Element element :scriptElements ){
                    for (DataNode node : element.dataNodes())
                    {
                        if(node.getWholeData().toString().contains("window._sharedData"))
                        {
                            String data = node.getWholeData().toString();
                            data = data.substring(data.indexOf("{"));
                            JSONObject obj = new JSONObject(data);
                            JSONArray objArray = obj.getJSONObject("entry_data").getJSONArray("ProfilePage");
                            JSONObject jsonObject = objArray.getJSONObject(0).getJSONObject("graphql").getJSONObject("user");
                            String username = jsonObject.optString("username");
                            String user_id = jsonObject.optString("id");
                            String urlProfilePicture = jsonObject.optString("profile_pic_url");
                            String website = "";
                            String full_name = jsonObject.optString("full_name");
                            String bio = jsonObject .optString("biography");
                            int mediaCount= jsonObject.getJSONObject("edge_owner_to_timeline_media").optInt("count");
                            int followsCount= jsonObject.getJSONObject("edge_follow").optInt("count");
                            int followedByCount= jsonObject.getJSONObject("edge_followed_by").optInt("count");
                            userDetails = new ProfileDetail(user_id,username,full_name,urlProfilePicture,bio,website,mediaCount,followsCount,followedByCount);
                            return true;
                        }
                    }
                }

            } catch (Exception e) {
                Log.e(UtilityVariables.tag, "Exception while parsing: "+e.toString()+"class: "+this.getClass().getName());
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result)
        {
            this.dialog.dismiss();
            if(result.booleanValue() == true)
            {
                if(this.userDetails != null)
                {
                    mUserNameView.setText(this.userDetails.getmUserName()+" is the username for this user");
                    mMediaCountView.setText(this.userDetails.getmMediaCount()+" Total Media");
                    mFollowsCountView.setText(this.userDetails.getmFollowsCount()+" people is followed by this user");
                    mFollowedByCountView.setText(this.userDetails.getmFollowedByCount()+" users follow this user");
                    Picasso.with(context)
                            .load(this.userDetails.getmUrlProfilePicture())
                            .into(mProfilePictureView);
                }
            }
            else
            {
                Toast.makeText(this.context,"No user details found.",Toast.LENGTH_SHORT).show();
                this.userDetails = null;
            }
        }
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
            Log.i(UtilityVariables.tag,"Something went wrong in function:showUserDetails class:" +this.getClass().getName()+" "+e.toString());
        }
    }

    private void loadUserDetails()
    {
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage("Getting the user details...");
        progressDialog.show();
        JsoupAsyncTask jsoupAsyncTask = new JsoupAsyncTask(mUserName,progressDialog,this.mContext);
        jsoupAsyncTask.execute();
    }

}
