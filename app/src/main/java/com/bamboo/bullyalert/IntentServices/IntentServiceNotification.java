package com.bamboo.bullyalert.IntentServices;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.provider.Settings;
import android.service.notification.StatusBarNotification;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bamboo.bullyalert.Classifier.Classifier;
import com.bamboo.bullyalert.Database.MonitoringPost;
import com.bamboo.bullyalert.Database.MonitoringPostDAO;
import com.bamboo.bullyalert.Database.User;
import com.bamboo.bullyalert.Database.UserDAO;
import com.bamboo.bullyalert.NavigationActivity;
import com.bamboo.bullyalert.R;
import com.bamboo.bullyalert.UtilityPackage.UtilityFunctions;
import com.bamboo.bullyalert.UtilityPackage.UtilityVariables;
import com.bamboo.bullyalert.fragment.NotificationFragment;
import com.bamboo.bullyalert.model.Comment;
import com.bamboo.bullyalert.model.Notification;
import com.bamboo.bullyalert.model.Profile;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class IntentServiceNotification extends IntentService
{
    private List<String> mMonitoringUserIds;
    HashMap<String,MonitoringPost> mPostsFromDB;
    HashMap<String,MonitoringPost> mPostsFromAPI;
    HashMap<String,ArrayList<Comment>> mNewCommentsForPost;
    HashMap<String,ArrayList<Comment>> mOldCommentsForPost;

    HashMap<String,Notification> mNotifications;

    private Classifier mClassifier;

    private int mInstagramBullyingInstance = 0;


    private MonitoringPostDAO mMonitoringPostDao;
    private UserDAO mUserDao;

    public IntentServiceNotification()
    {
        super("IntentServiceNotification");
    }



    @Override
    protected void onHandleIntent(Intent intent)
    {
        Log.i(UtilityVariables.tag,"inside intent service now.");
        try
        {
            this.mMonitoringUserIds = new ArrayList<>();
            mPostsFromDB = new HashMap<>();
            mPostsFromAPI = new HashMap<>();
            mMonitoringPostDao = new MonitoringPostDAO(getApplicationContext());
            mUserDao = new UserDAO(getApplicationContext());
            mNewCommentsForPost = new HashMap<>();
            mOldCommentsForPost = new HashMap<>();
            mNotifications = new HashMap<>();
            this.mClassifier = Classifier.getInstance(this);
            this.mInstagramBullyingInstance = 0;
            checkInstagramNotification();

        }catch (Exception e)
        {
            Log.i(UtilityVariables.tag,"Exception in IntentServiceNotification- onHandleIntent :"+e.toString());
        }

    }

    public void getAllMonitoringUsers()
    {
        try
        {
            //Log.i(UtilityVariables.tag,"Inside Notification Intent Service");
            //Log.i(UtilityVariables.tag,"getAllMonitoringUsers function");
            String urlString = UtilityVariables.INSTAGRAM_GET_MONITORING_USERS+"?email="+UtilityVariables.USER_EMAIL;
            JSONObject jsonObject = UtilityFunctions.getJsonStringFromGetRequestUrlString(urlString);
            String success = jsonObject.optString("success");
            if(success.equals("success"))
            {
                JSONArray jsonArray = jsonObject.getJSONArray("users");
                if(jsonArray.length() == 0)
                {
                    Log.i(UtilityVariables.tag,"not monitoring any users");
                }
                else
                {

                    //Log.i(UtilityVariables.tag,"Got the monitoring user ids");
                    for(int i=0;i<jsonArray.length();i++)
                    {
                        JSONObject userObject = jsonArray.getJSONObject(i);
                        mMonitoringUserIds.add(userObject.optString("userid"));
                        //Log.i(UtilityVariables.tag,"user: "+userObject.optString("userid"));

                    }
                }
            }
            else
            {
                Log.i(UtilityVariables.tag,"Something went wrong: "+jsonObject.toString());
            }

        }catch (Exception e)
        {
            Log.i(UtilityVariables.tag,"Exception getAllMonitoringUsers function IntentServiceNotification: "+e.toString());
        }


    }

    public void getAllPostsFromDatabase()
    {
        mPostsFromDB = mMonitoringPostDao.fetchAllMonitoringPostsByEmail(UtilityVariables.USER_EMAIL);
        for (Map.Entry<String, MonitoringPost> entry : mPostsFromDB.entrySet())
        {
            String postid = entry.getKey();
            MonitoringPost post = entry.getValue();
            //Log.i(UtilityVariables.tag,"new last time checked: "+post.lastTimeChecked);
        }
    }

    public void getRecentNewPostsForUsers() throws Exception
    {

        for(int i=0;i<mMonitoringUserIds.size();i++)
        {
            String urlString = "https://api.instagram.com/v1/users/"+mMonitoringUserIds.get(i)+
                    "/media/recent/?access_token="+UtilityVariables.INSTAGRAM_AUTHENTICATION_TOKEN;

            JSONObject jsonObject = UtilityFunctions.getJsonStringFromGetRequestUrlString(urlString);
            //Log.i(UtilityVariables.tag,"json string after post get request:"+jsonObject.toString());
            JSONArray userdata = jsonObject.optJSONArray("data");
            for(int j=0;j<userdata.length();j++)
            {
                MonitoringPost post = new MonitoringPost();
                JSONObject record = userdata.optJSONObject(j);
                post.email = UtilityVariables.USER_EMAIL;
                post.username = record.getJSONObject("user").optString("username");
                post.userid = mMonitoringUserIds.get(i);
                post.postid = record.optString("id");
                post.lastTimeChecked = 0+"";
                post.socialNetwork = "Instagram";
                mPostsFromAPI.put(post.postid,post);
            }

        }
    }

    public void insertNewPostsInDatabase()
    {

        HashMap<String,MonitoringPost>newPostMap = new HashMap<>();

        for (Map.Entry<String, MonitoringPost> entry : mPostsFromAPI.entrySet())
        {
            String postid = entry.getKey();
            MonitoringPost post = entry.getValue();
            //Log.i(UtilityVariables.tag,"posts from api: "+postid);
            if(!mPostsFromDB.containsKey(postid))
            {
                long s = mMonitoringPostDao.insertMonitoringPost(post);
                //Log.i(UtilityVariables.tag,"inserted into database new postid: "+postid+", insert:"+s);
                newPostMap.put(postid,post);
            }
        }

        mPostsFromDB.putAll(newPostMap);
        //Log.i(UtilityVariables.tag,"total post before new posts in database: "+mPostsFromDB.size());
        //Log.i(UtilityVariables.tag,"total new posts inserted into database this time: "+newPostMap.size());
        //Log.i(UtilityVariables.tag,"total post after new posts in database: "+mPostsFromDB.size());
    }

    public void getCommentsForPosts()
    {
        for (Map.Entry<String, MonitoringPost> entry : mPostsFromDB.entrySet())
        {

            String postid = entry.getKey();
            MonitoringPost post = entry.getValue();
            //Log.i(UtilityVariables.tag,"post id: "+postid);
            try
            {
                ArrayList<Comment> newComments = new ArrayList<>();
                ArrayList<Comment> oldComments = new ArrayList<>();
                String urlString = "https://api.instagram.com/v1/media/"+postid+"/comments?access_token="+UtilityVariables.INSTAGRAM_AUTHENTICATION_TOKEN;

                JSONObject jsonObject = UtilityFunctions.getJsonStringFromGetRequestUrlString(urlString);
                //Log.i(UtilityVariables.tag,"json string for comments: "+jsonObject.toString());
                JSONObject metaJSon = jsonObject.getJSONObject("meta");
                String code = metaJSon.getString("code");

                if(code.equals("200"))
                {
                    JSONArray data = jsonObject.optJSONArray("data");
                    long newLastTimeChecked = 0;
                    for(int i=0;i<data.length();i++)
                    {
                        JSONObject record = data.optJSONObject(i);
                        String createdTimeString = record.optString("created_time");
                        String commentText = record.optString("text");
                        JSONObject fromJson = record.optJSONObject("from");
                        String commenterName = fromJson.optString("username");
                        if(Long.parseLong(post.lastTimeChecked) == 0 ||
                                Long.parseLong(createdTimeString) > Long.parseLong(post.lastTimeChecked))
                        {
                            // this comment came after this post's last time checked comment by the app
                            //first time this post's comments have been checked. Add all of them and keep track of the latest comment time.
                            newComments.add(new Comment(commentText,commenterName,Long.parseLong(createdTimeString)));
                            newLastTimeChecked = Math.max(newLastTimeChecked,Long.parseLong(createdTimeString));
                            //Log.i(UtilityVariables.tag,"last time checked: "+Long.parseLong(createdTimeString));
                        }
                        else
                        {
                            oldComments.add(new Comment(commentText,commenterName,Long.parseLong(createdTimeString)));
                        }

                    }
                    //Log.i(UtilityVariables.tag,"previous last time checked for post id: "+postid+"  value: "+post.lastTimeChecked);
                    post.lastTimeChecked = String.valueOf(newLastTimeChecked); //setting the new last time checked
                    //Log.i(UtilityVariables.tag,"new last time checked for post id: "+postid+"  value: "+post.lastTimeChecked);
                }

                if(newComments.size() > 0)
                {
                    Collections.reverse(oldComments);
                    Collections.reverse(newComments);
                    Log.i(UtilityVariables.tag,"new comments found!:"+newComments.size());
                    this.mNewCommentsForPost.put(postid,newComments);
                    this.mOldCommentsForPost.put(postid,oldComments);
                }


            }catch (Exception e)
            {
                Log.i(UtilityVariables.tag,"Exception in getCommentsForPosts function, IntentServiceNotification: "+e.toString());
            }
            //Log.i(UtilityVariables.tag,"__________________________________________________");
        }


        }


    public void classifyPosts()
    {
        Log.i(UtilityVariables.tag," inside the classify posts function. total posts for whcih new comments were found:"+mNewCommentsForPost.size());
        Log.i(UtilityVariables.tag,"___________________________________");
        for (Map.Entry<String, ArrayList<Comment>> entry : mNewCommentsForPost.entrySet())
        {
            String postid = entry.getKey();
            ArrayList<Comment> newComments = entry.getValue();
            ArrayList<Comment> oldComments = this.mOldCommentsForPost.get(postid);

            double[]featureValues = this.mClassifier.getFeatureValues(newComments);
            double prediction = this.mClassifier.predict(featureValues);
            //Log.i(UtilityVariables.tag,"prediction value : "+ prediction);
            if(Math.round(prediction) == 1)
            {
                this.mInstagramBullyingInstance += 1;
                //mNotificationPosts.put(postid,newComments);
            }

            DecimalFormat df = new DecimalFormat("#.##");

            Notification notification = new Notification();
            notification.setmLevel(Double.valueOf(df.format(prediction)));
            notification.setmNewComments(newComments);
            notification.setmOldComments(oldComments);
            notification.setmPostId(postid);
            notification.setmSocialNetwork(this.mPostsFromDB.get(postid).socialNetwork);
            notification.setmUserId(this.mPostsFromDB.get(postid).userid);
            notification.setmUserName(this.mPostsFromDB.get(postid).username);

            mNotifications.put(postid,notification);

            //Log.i(UtilityVariables.tag,"___________________________________");
        }
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


    public void updateMonitoringPostTable()
    {
        mMonitoringPostDao.updateLastTimeCheckedForPosts(mPostsFromDB);
    }

    public void createNotification(Context context, String msg, String msgText, String msgAlert)
    {
        if(this.mInstagramBullyingInstance > 0)
        {
            Intent intent = new Intent(context,NavigationActivity.class);
            intent.putExtra(UtilityVariables.INTENT_VARIABLE_NOTIFICATIONS,this.mNotifications);
            PendingIntent notificationIntent = PendingIntent.getActivity(context,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle(msg)
                            .setContentText(msgAlert).setContentText(msgText);

            mBuilder.setContentIntent(notificationIntent);
            mBuilder.setDefaults(NotificationCompat.DEFAULT_SOUND);
            mBuilder.setAutoCancel(true);

            NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(1,mBuilder.build());
        }
    }


    public void createNotificationAuthentication(Context context, String msg, String msgText, String msgAlert)
    {
        Intent intent = new Intent(context,NavigationActivity.class);
        intent.putExtra(UtilityVariables.INTENT_VARIABLE_NOTIFICATIONS_AUTHENTICATION,UtilityVariables.INTENT_VARIABLE_NOTIFICATIONS_AUTHENTICATION);
        PendingIntent notificationIntent = PendingIntent.getActivity(context,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(msg)
                        .setContentText(msgAlert).setContentText(msgText);

        mBuilder.setContentIntent(notificationIntent);
        mBuilder.setDefaults(NotificationCompat.DEFAULT_SOUND);
        mBuilder.setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(2,mBuilder.build());
    }


    private void sendNotificationDataToServer()
    {
        for (Map.Entry<String, Notification> entry : mNotifications.entrySet())
        {
            String postid = entry.getKey();
            Notification notification = entry.getValue();
            ArrayList<Comment> newComments = notification.getmNewComments();
            long lastCommentTime = 0;
            //String commentString = "";
            SimpleDateFormat sdf = new SimpleDateFormat();
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            JSONArray commentArray = new JSONArray();
            for(int i = 0; i<newComments.size();i++)
            {
                if(newComments.get(i).mCreatedTime > lastCommentTime)
                    lastCommentTime = newComments.get(i).mCreatedTime;
                //commentString  = commentString + newComments.get(i).getmCommentText()+" ::TIME::";
                //String timeString = sdf.format(new Date(newComments.get(i).mCreatedTime*1000)).toString()+"-->";
                //commentString  = commentString + newComments.get(i).getmCommentText()+" -->";
                JSONObject commentObject = new JSONObject();
                try
                {
                    commentObject.put("commentText",newComments.get(i).getmCommentText());
                    commentObject.put("commenterName",newComments.get(i).getmCommenterName());
                    commentObject.put("createdAt",sdf.format(new Date(newComments.get(i).mCreatedTime*1000)).toString());
                    commentArray.put(i,commentObject);

                }catch (Exception e)
                {
                    Log.i(UtilityVariables.tag,"exception while forming json object from comments. "+e.toString());
                }

            }
            Log.i(UtilityVariables.tag,"last comment time api instagram"+lastCommentTime);
            String notificationId = UtilityVariables.USER_EMAIL+
                    ","+postid+","+lastCommentTime+"";
            long timeAtNotification = System.currentTimeMillis();


            //System.out.println(sdf.format(new Date(lastCommentTime)).toString());
            Date date = new Date(lastCommentTime);

            try
            {
                JSONObject data = new JSONObject();
                data.put("notificationid",notificationId);
                data.put("email",UtilityVariables.USER_EMAIL);
                data.put("username",notification.getmUserName());
                data.put("osn_name","instagram");
                data.put("postid",postid);
                data.put("newComments",commentArray.toString());
                data.put("appNotificationResult",notification.getmLevel()+"");
                data.put("lastCommentCreatedAt",sdf.format(new Date(lastCommentTime*1000)).toString());
                data.put("timeAtNotification",sdf.format(new Date(timeAtNotification)).toString());

                String urlString = UtilityVariables.ADD_NOTIFICATION;
                JSONObject resultjson = UtilityFunctions.getJsonStringFromPostRequestUrlString(urlString,data);
                String message = resultjson.optString("message").toString();

                if (resultjson.optString("success").toString().equals("success"))
                {
                    Log.i(UtilityVariables.tag,"adding notification success: "+this.getClass().getName());
                }
                else
                {
                    Log.i(UtilityVariables.tag,"adding notification failed: "+this.getClass().getName());
                }

            }catch (Exception e)
            {
                Log.i(UtilityVariables.tag, "Exception in sendNotificationDataToServer: "+e.toString());
            }


        }
    }




    public void checkInstagramNotification()
    {
        try
        {
            Log.i(UtilityVariables.tag, "checkInstagramNotification function ");
            if (UtilityVariables.INSTAGRAM_AUTHENTICATION_TOKEN != null || getTokenFromDatabase() != null)
            {
                getAllMonitoringUsers();
                getAllPostsFromDatabase();
                getRecentNewPostsForUsers();
                insertNewPostsInDatabase();
                getCommentsForPosts();
                classifyPosts();
                sendNotificationDataToServer();
                createNotification(getApplicationContext(),
                        "Possible Bullying!!",
                        "On Instagram!!"+this.mInstagramBullyingInstance+" instances",
                        "Possible Bullying!");
                updateMonitoringPostTable();

            }
            else
            {
                createNotificationAuthentication(getApplicationContext(),
                        "authentication needed",
                        "authentication needed",
                        "authentication needed");

            }
        }catch (Exception e)
        {
            Log.i(UtilityVariables.tag,"Exception in checkInstagramNotification function: "+e.toString());
        }
    }
}
