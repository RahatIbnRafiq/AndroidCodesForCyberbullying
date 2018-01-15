package com.bamboo.bullyalert.UtilityPackage;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bamboo.bullyalert.model.ProfileDetail;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rahat Ibn Rafiq on 10/25/2017.
 */

public class UtilityInstagramAPI
{


    public static void getAllRecentPostIDs(String userid,Context mContext)
    {

        final List<String> postIds = new ArrayList<>();
        String mAuthToken;
        try {
            String urlString = "https://api.instagram.com/v1/users/"+userid+"/media/recent/?access_token="+UtilityVariables.INSTAGRAM_AUTHENTICATION_TOKEN;
            StringRequest stringRequest = new StringRequest(Request.Method.GET,
                    urlString,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //Log.i(UtilityVariables.tag,"onResponse function: "+response+this.getClass().getName());
                            try
                            {
                                JSONObject jsonObject = new JSONObject(response);
                                JSONObject metaJSon = jsonObject.getJSONObject("meta");
                                String code = metaJSon.getString("code");
                                if(code.equals("200"))
                                {
                                    JSONArray userdata = jsonObject.optJSONArray("data");
                                    for(int i=0;i<userdata.length();i++)
                                    {
                                        JSONObject record = userdata.optJSONObject(i);
                                        String postid = record.optString("id");
                                        postIds.add(postid);
                                    }


                                }

                            }catch (Exception e)
                            {
                                Log.i(UtilityVariables.tag,"Error while getting the users json result. UtilityInstagramAPI class"+e.toString()+ this.getClass().getName());
                            }


                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.i(UtilityVariables.tag,"volley Error while getting the users json result. class UtilityInstagramAPI"+error.toString()+ this.getClass().getName());

                        }
                    });
            RequestQueue requestQueue = Volley.newRequestQueue(mContext);
            requestQueue.add(stringRequest);

        } catch (Exception e)
        {
            Log.i(UtilityVariables.tag,"Exception in loadUserDetails in UtilityInstagramAPI class "+" exception: "+e.toString());
        }
    }
}
