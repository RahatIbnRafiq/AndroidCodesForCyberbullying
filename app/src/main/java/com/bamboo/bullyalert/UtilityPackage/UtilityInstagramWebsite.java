package com.bamboo.bullyalert.UtilityPackage;


import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bamboo.bullyalert.model.ProfileDetail;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Rahat Ibn Rafiq on 12/2/2017.
 */

public class UtilityInstagramWebsite
{
    private static UtilityInstagramWebsite instance = null;
    public String URL_ROOT = "https://www.instagram.com/";
    ProfileDetail profileDetail;


    public static UtilityInstagramWebsite getInstance()
    {
        if(instance == null)
        {
            instance = new UtilityInstagramWebsite();
        }
        return instance;
    }




    public  ProfileDetail getInstagramUserProfileDetail(String username,Context context)
    {
        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Getting the user details...");
        progressDialog.show();
        try
        {
            String urlString = this.URL_ROOT+username+"/?__a=1";
            StringRequest stringRequest = new StringRequest(Request.Method.GET, urlString,
                    new Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String response)
                        {
                            progressDialog.dismiss();
                            //Log.i(UtilityVariables.tag,"getInstagramUserProfileDetail response: "+response);
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
                                int mFollowsCount= Integer.parseInt(jsonObject.optString("follows"));
                                int mFollowedByCount= Integer.parseInt(jsonObject.optString("followed_by"));
                                profileDetail = null;
                                profileDetail = new ProfileDetail(mUserId, mUserName,  mFullName,  mUrlProfilePicture,
                                         mBio,  mWebsite,  mMediaCount,  mFollowsCount,  mFollowedByCount);



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

            RequestQueue requestQueue = Volley.newRequestQueue(context);
            requestQueue.add(stringRequest);


        }catch (Exception e)
        {
            return null;
        }

        return null;
    }
}
