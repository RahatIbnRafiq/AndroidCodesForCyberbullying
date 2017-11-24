package com.bamboo.bullyalert.UtilityPackage;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Rahat Ibn Rafiq on 10/17/2017.
 */

public class UtilityFunctions {

    public static JSONObject getJsonStringFromPostRequestUrlString(String urlString, JSONObject data)
    {
        try {

            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());

            writer.write(data.toString());
            writer.flush();
            String response = streamToString(urlConnection.getInputStream());

            JSONObject resultjson = new JSONObject(response);
            return resultjson;

        }
        catch (Exception e)
        {
            Log.i(UtilityVariables.tag,"Exception in getJsonStringFromPostRequestUrlString function: "+e.toString());
            return null;
        }
    }

    public static boolean isPhoneValid(String phone) {
        String regex = "\\d+";
        return phone.matches(regex);
    }

    public static boolean isEmailValid(String email) {
        if(!email.contains("@"))
        {
            return false;
        }
        else
        {
            try
            {
                String[] result = email.split("@");
                if(result.length != 2)
                    return false;
                else if (result[0].length() < 1 || result[1].length() < 1)
                    return false;
            }catch (Exception e)
            {
                Log.i(UtilityVariables.tag,"email is wrong! "+e.toString());
                return false;
            }

        }
        return true;
    }

    public static boolean isPasswordValid(String password) {
        return password.length() >= UtilityVariables.MIN_PASSWORD_LENGTH;
    }

    public static JSONObject getJsonStringFromGetRequestUrlString(String urlString)
    {
        try {
            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();


            urlConnection.setRequestMethod(UtilityVariables.GET_REQUEST_METHOD);
            urlConnection.setReadTimeout(UtilityVariables.READ_TIMEOUT);
            urlConnection.setConnectTimeout(UtilityVariables.CONNECTION_TIMEOUT);
            if(urlConnection.getResponseCode() != 200)
                return null;
            InputStream inputStream =null;
            inputStream = new BufferedInputStream(urlConnection.getInputStream());

            String response = UtilityFunctions.streamToString(inputStream);
            JSONObject resultjson = new JSONObject(response);
            return resultjson;
        } catch (Exception e)
        {
            Log.i(UtilityVariables.tag,"Exception in getJsonStringFromGetRequestUrlString function: "+e.toString());
            return null;
        }

    }

    public static boolean isNetworkAvailable(Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }



    public static String streamToString(InputStream is) {

        try {
            String str = "";

            if (is != null) {
                StringBuilder sb = new StringBuilder();
                String line;

                try {
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(is));

                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }

                    reader.close();
                }
                finally {
                    try{
                        is.close();
                    }catch (IOException ex)
                    {
                        Log.i(UtilityVariables.tag,"IO Exception in streamToString function: "+ex.toString());
                    }

                }

                str = sb.toString();
            }
            //Log.i(UtilityVariables.tag,"return string: "+str);
            return str;

        }catch (Exception e)
        {
            Log.i(UtilityVariables.tag,"Exception in streamToString "+e.toString());
        }

        return null;

    }
}
