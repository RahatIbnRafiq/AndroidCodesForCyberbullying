package com.bamboo.bullyalert.IntentServices;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.bamboo.bullyalert.Classifier.Classifier;
import com.bamboo.bullyalert.Database.MonitoringPost;
import com.bamboo.bullyalert.Database.MonitoringPostDAO;
import com.bamboo.bullyalert.Database.NotificationFeedback;
import com.bamboo.bullyalert.Database.NotificationFeedbackDAO;
import com.bamboo.bullyalert.Database.UserDAO;
import com.bamboo.bullyalert.NavigationActivity;
import com.bamboo.bullyalert.R;
import com.bamboo.bullyalert.UtilityPackage.UtilityFunctions;
import com.bamboo.bullyalert.UtilityPackage.UtilityVariables;
import com.bamboo.bullyalert.model.Comment;
import com.bamboo.bullyalert.model.Notification;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class IntentServiceNotification extends IntentService
{
    private List<String> mMonitoringUserNames;
    HashMap<String,MonitoringPost> mPostsFromDB;
    HashMap<String,MonitoringPost> mPostsFromAPI;
    HashMap<String,ArrayList<Comment>> mNewCommentsForPost;
    HashMap<String,ArrayList<Comment>> mOldCommentsForPost;
    HashMap<String,Notification> mNotifications;
    private Classifier mClassifier;
    private int mInstagramBullyingInstance = 0;
    private MonitoringPostDAO mMonitoringPostDao;
    private UserDAO mUserDao;
    private NotificationFeedbackDAO mNotificationFeedbackDao;
    private boolean isClassifierUpdated = false;

    public IntentServiceNotification()
    {
        super("IntentServiceNotification");
    }


    @Override
    protected void onHandleIntent(Intent intent)
    {
        if(UtilityVariables.IS_ALARM_ON == false)
        {
            return;
        }
        try
        {
            this.mMonitoringUserNames = new ArrayList<>();
            mPostsFromDB = new HashMap<>();
            mPostsFromAPI = new HashMap<>();
            mMonitoringPostDao = new MonitoringPostDAO(getApplicationContext());
            mNotificationFeedbackDao = new NotificationFeedbackDAO(getApplicationContext());
            mUserDao = new UserDAO(getApplicationContext());
            mNewCommentsForPost = new HashMap<>();
            mOldCommentsForPost = new HashMap<>();
            mNotifications = new HashMap<>();
            this.mClassifier = Classifier.getInstance(this);
            this.mInstagramBullyingInstance = 0;
            checkInstagramNotification();

        }catch (Exception e)
        {
            Log.i(UtilityVariables.tag,this.getClass().getName()+" exception in function:onHandleIntent: "+e.toString());
        }

    }


    public void getAllMonitoringUsers()
    {
        try
        {
            String urlString = UtilityVariables.INSTAGRAM_GET_MONITORING_USERS+"?email="+UtilityVariables.USER_EMAIL;
            JSONObject jsonObject = UtilityFunctions.getJsonStringFromGetRequestUrlString(urlString);
            String success = jsonObject.optString("success");
            if(success.equals("success"))
            {
                JSONArray jsonArray = jsonObject.getJSONArray("users");
                if(jsonArray.length() == 0)
                {
                }
                else
                {
                    for(int i=0;i<jsonArray.length();i++)
                    {
                        JSONObject userObject = jsonArray.getJSONObject(i);
                        mMonitoringUserNames.add(userObject.optString("username"));

                    }
                }
            }
            else
            {
                Log.i(UtilityVariables.tag,this.getClass().getName()+ "Something went wrong when trying to get monitoring users from the server: "+jsonObject.toString());
            }

        }catch (Exception e)
        {
            Log.i(UtilityVariables.tag,this.getClass().getName()+"Exception getAllMonitoringUsers function: "+e.toString()+" class: "+this.getClass().getName());
        }


    }


    public void getAllPostsFromDatabase()
    {
        UtilityVariables.APP_STATUS = UtilityVariables.APP_STATUS_GETTING_POSTS;
        mPostsFromDB = mMonitoringPostDao.fetchAllMonitoringPostsByEmail(UtilityVariables.USER_EMAIL);
    }


    public void getRecentNewPostsForUsers() throws Exception
    {
        for(int i=0;i<mMonitoringUserNames.size();i++)
        {
            try {
                Document doc;
                String htmlPageUrl = "https://www.instagram.com/"+mMonitoringUserNames.get(i)+"/";
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
                            String userid = jsonObject.optString("id");
                            JSONArray postDataArray = jsonObject.getJSONObject("edge_owner_to_timeline_media").getJSONArray("edges");

                            for(int k=0;k<postDataArray.length();k++)
                            {
                                JSONObject postjson = postDataArray.getJSONObject(k).getJSONObject("node");
                                String postid = postjson.optString("id");
                                if(mPostsFromDB.containsKey(postid))
                                {
                                }
                                else
                                {
                                    MonitoringPost post = new MonitoringPost();
                                    post.postid = postid;
                                    post.userid = userid;
                                    post.username = mMonitoringUserNames.get(i);
                                    post.email = UtilityVariables.USER_EMAIL;
                                    post.lastTimeChecked = 0+"";
                                    post.socialNetwork = "Instagram";
                                    post.postCode = postjson.optString("shortcode");
                                    mPostsFromAPI.put(postid, post);
                                    Log.i(UtilityVariables.tag,"postid: "+postid+" for user: "+post.username+" postcode: "+post.postCode);
                                }

                            }

                        }
                    }
                }
            } catch (Exception e) {
                Log.e(UtilityVariables.tag, "Exception while parsing: "+e.toString()+"class: "+this.getClass().getName());
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
            if(!mPostsFromDB.containsKey(postid))
            {
                long s = mMonitoringPostDao.insertMonitoringPost(post);
                newPostMap.put(postid,post);
            }
        }
        mPostsFromDB.putAll(newPostMap);
    }


    public void getCommentsForPosts()
    {
        UtilityVariables.APP_STATUS = UtilityVariables.APP_STATUS_GETTING_COMMENTS;
        for (Map.Entry<String, MonitoringPost> entry : mPostsFromDB.entrySet())
        {
            ArrayList<Comment> newComments = new ArrayList<>();
            ArrayList<Comment> oldComments = new ArrayList<>();
            MonitoringPost post = entry.getValue();
            String postid = entry.getKey();
            String postCode = post.postCode;
            String urlString = UtilityVariables.INSTAGRAM_API_GET_COMMENTS+postCode;
            try
            {
                Document doc;
                doc = Jsoup.connect(urlString).get();
                Elements scriptElements = doc.getElementsByTag("script");
                for (Element element :scriptElements ){
                    for (DataNode node : element.dataNodes())
                    {
                        if(node.getWholeData().toString().contains("window._sharedData"))
                        {
                            String data = node.getWholeData().toString();
                            int start_index  = data.indexOf("{");
                            if (start_index > 0)
                            {
                                data = data.substring(data.indexOf("{"));
                                JSONObject obj = new JSONObject(data);
                                JSONArray objArray = obj.getJSONObject("entry_data").getJSONArray("PostPage");
                                JSONObject jsonObject = objArray.getJSONObject(0).getJSONObject("graphql").getJSONObject("shortcode_media");
                                JSONArray commentArray = jsonObject.getJSONObject("edge_media_to_comment").getJSONArray("edges");
                                long newLastTimeChecked = 0;
                                for (int k = 0; k < commentArray.length(); k++) {
                                    JSONObject record = commentArray.optJSONObject(k).optJSONObject("node");
                                    String createdTimeString = record.optString("created_at");
                                    String commentText = record.optString("text");
                                    String commenterName = record.optJSONObject("owner").optString("username");
                                    if (Long.parseLong(post.lastTimeChecked) == 0 || Long.parseLong(createdTimeString) > Long.parseLong(post.lastTimeChecked)) {
                                        newComments.add(new Comment(commentText, commenterName, Long.parseLong(createdTimeString)));
                                        newLastTimeChecked = Math.max(newLastTimeChecked, Long.parseLong(createdTimeString));
                                    } else {
                                        oldComments.add(new Comment(commentText, commenterName, Long.parseLong(createdTimeString)));
                                    }
                                }
                                post.lastTimeChecked = String.valueOf(newLastTimeChecked); //setting the new last time checked
                                if (newComments.size() > 0)
                                {
                                    Collections.reverse(oldComments);
                                    Collections.reverse(newComments);
                                    this.mNewCommentsForPost.put(postid, newComments);
                                    this.mOldCommentsForPost.put(postid, oldComments);
                                }
                            }

                        }
                    }
                }
            }catch (Exception e)
            {
                Log.i(UtilityVariables.tag,"Exception, Intent service, getCommentsForPosts function: "+e.toString()+" class "+this.getClass().getName());
            }
        }
    }


    public void classifyPosts()
    {
        UtilityVariables.APP_STATUS = UtilityVariables.APP_STATUS_CLASSIFYING;
        for (Map.Entry<String, ArrayList<Comment>> entry : mNewCommentsForPost.entrySet())
        {
            String postid = entry.getKey();
            ArrayList<Comment> newComments = entry.getValue();
            ArrayList<Comment> oldComments = this.mOldCommentsForPost.get(postid);
            ArrayList<Comment> allComments = this.mOldCommentsForPost.get(postid);
            allComments.addAll(newComments);
            double[]featureValues = this.mClassifier.getFeatureValues(allComments);
            double prediction = this.mClassifier.predict(featureValues);
            if(Math.round(prediction) == 1)
            {
                this.mInstagramBullyingInstance += 1;
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
        }
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


    private void sendNotificationDataToServer()
    {
        for (Map.Entry<String, Notification> entry : mNotifications.entrySet())
        {
            String postid = entry.getKey();
            Notification notification = entry.getValue();
            ArrayList<Comment> newComments = notification.getmNewComments();
            long lastCommentTime = 0;
            SimpleDateFormat sdf = new SimpleDateFormat();
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            JSONArray commentArray = new JSONArray();
            for(int i = 0; i<newComments.size();i++)
            {
                if(newComments.get(i).mCreatedTime > lastCommentTime)
                    lastCommentTime = newComments.get(i).mCreatedTime;
                JSONObject commentObject = new JSONObject();
                try
                {
                    commentObject.put("commentText",newComments.get(i).getmCommentText());
                    commentObject.put("commenterName",newComments.get(i).getmCommenterName());
                    commentObject.put("createdAt",sdf.format(new Date(newComments.get(i).mCreatedTime*1000)).toString());
                    commentArray.put(i,commentObject);

                }catch (Exception e)
                {
                    Log.i(UtilityVariables.tag,"exception while forming json object from comments. in function sendNotificationDataToServer: "+e.toString());
                }
            }
            String notificationId = UtilityVariables.USER_EMAIL+ ","+postid+","+lastCommentTime+"";
            long timeAtNotification = System.currentTimeMillis();
            notification.setmNotificationId(notificationId);
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
                if (resultjson.optString("success").toString().equals("success"))
                {
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


    private void updateClassifier()
    {
        try
        {
            ArrayList<NotificationFeedback> feedbacks = mNotificationFeedbackDao.fetchAllFeedbackByEmail(UtilityVariables.USER_EMAIL);
            if(feedbacks.size() > 0)
            {
                this.mClassifier.updateClassifier(feedbacks);
                this.isClassifierUpdated = true;
                for(int i=0; i< feedbacks.size();i++)
                {
                    try {

                        JSONObject data = new JSONObject();
                        data.put("notificationid",feedbacks.get(i).getmNotificationId());
                        data.put("email",UtilityVariables.USER_EMAIL);
                        data.put("predicted",feedbacks.get(i).getmPredicted());
                        data.put("feedback",feedbacks.get(i).getmFeedback());
                        String urlString = UtilityVariables.ADD_NOTIFICATION_FEEDBACK;
                        JSONObject resultjson = UtilityFunctions.getJsonStringFromPostRequestUrlString(urlString,data);
                        if (resultjson.optString("success").toString().equals("success"))
                        {
                            long s = mNotificationFeedbackDao.deleteFeedback(UtilityVariables.USER_EMAIL,feedbacks.get(i).getmNotificationId());
                        }
                        else
                        {
                            Log.e(UtilityVariables.tag,"adding feedback failed in updateClassifier function : "+this.getClass().getName());
                        }

                    }catch (Exception e)
                    {
                        Log.e(UtilityVariables.tag,"Exception in updateClassifier function sending data to server : "+e.toString()+" class: "+this.getClass().getName());
                    }
                }

            }
            else
            {
                this.isClassifierUpdated = false;
            }
        }catch (Exception e)
        {
            Log.e(UtilityVariables.tag, "Exception in updating classifier function in IntentServiceNotification: "+e.toString());
        }

    }


    private void universalClassifierUpdate()
    {
        int notificationCount = 0;
        int feedbackCount = 0;
        try
        {
            String urlString = UtilityVariables.INSTAGRAM_GET_NOTIFICATION_COUNT+"?email="+UtilityVariables.USER_EMAIL;
            JSONObject jsonObject = UtilityFunctions.getJsonStringFromGetRequestUrlString(urlString);
            String success = jsonObject.optString("success");
            if(success.equals("success"))
            {
                notificationCount = jsonObject.optInt("message");
            }
            urlString = UtilityVariables.INSTAGRAM_GET_FEEDBACK_COUNT+"?email="+UtilityVariables.USER_EMAIL;
            jsonObject = UtilityFunctions.getJsonStringFromGetRequestUrlString(urlString);
            success = jsonObject.optString("success");
            if(success.equals("success"))
            {
                feedbackCount = jsonObject.optInt("message");
            }
            else
            {
                Log.e(UtilityVariables.tag,this.getClass().getName()+ "Something went wrong when trying to get feedback count from the server: "+jsonObject.toString());
            }

            float condition = 0;

            if (notificationCount != 0)
                condition = (float)feedbackCount / (float) notificationCount;

            if(condition < 0.05)
            {
                Log.i(UtilityVariables.tag,"not enough feedback. general classifier will be applied");
                urlString = UtilityVariables.INSTAGRAM_GET_CLAFFISIER;
                jsonObject = UtilityFunctions.getJsonStringFromGetRequestUrlString(urlString);
                success = jsonObject.optString("success");
                if(success.equals("success"))
                {
                    JSONArray weightArray = jsonObject.optJSONArray("weights");
                    if(weightArray.length() > 0)
                    {
                        Log.i(UtilityVariables.tag,weightArray.toString());
                        JSONObject weights = weightArray.getJSONObject(0);
                        double[] coefficients = new double[4];
                        try
                        {
                            coefficients[0] = Double.parseDouble(weights.optString("interceptWeight"));
                            coefficients[1] = Double.parseDouble(weights.optString("negativeCommentCountWeight"));
                            coefficients[2] = Double.parseDouble(weights.optString("negativeCommentPercentageWeight"));
                            coefficients[3] = Double.parseDouble(weights.optString("negativeWordPerNegativeCommentWeight"));
                            this.mClassifier.setCoefficients(coefficients);
                            Log.i(UtilityVariables.tag, "classifier is updated to the general classifier.");
                        }
                        catch(Exception e)
                        {
                            Log.e(UtilityVariables.tag, "No general classifier yet. The app will retain the device's resident classifier. "+e.toString());
                        }

                    }
                    else
                    {
                        Log.e(UtilityVariables.tag," No general classifier on the server. Resident classifier will be retained.");
                    }
                }
            }
            else
            {
                if(this.isClassifierUpdated)
                {
                    Log.i(UtilityVariables.tag, " enough feed backs. sending this classifier to the server.");
                    JSONObject data = new JSONObject();
                    data.put("interceptWeight", this.mClassifier.getCoefficients()[0]);
                    data.put("negativeCommentCountWeight", this.mClassifier.getCoefficients()[1]);
                    data.put("negativeCommentPercentageWeight", this.mClassifier.getCoefficients()[2]);
                    data.put("negativeWordPerNegativeCommentWeight", this.mClassifier.getCoefficients()[3]);
                    this.isClassifierUpdated = false;
                    urlString = UtilityVariables.INSTAGRAM_ADD_CLAFFISIER;
                    JSONObject resultjson = UtilityFunctions.getJsonStringFromPostRequestUrlString(urlString,data);

                    if (resultjson.optString("success").toString().equals("success"))
                    {
                    }
                    else
                    {
                        Log.i(UtilityVariables.tag,"adding classifier failed: "+this.getClass().getName()+" json: "+resultjson.toString());
                    }
                }
                else
                {
                }
            }

        }catch (Exception e)
        {
            Log.e(UtilityVariables.tag,this.getClass().getName()+"Exception universalClassifierUpdate function "+e.toString());
        }
    }


    public void checkInstagramNotification()
    {
        try
        {
            Log.i(UtilityVariables.tag,"*********************************************************************");
            updateClassifier();
            getAllMonitoringUsers();
            getAllPostsFromDatabase();
            getRecentNewPostsForUsers();
            insertNewPostsInDatabase();
            getCommentsForPosts();
            classifyPosts();
            sendNotificationDataToServer();
            createNotification(getApplicationContext(),"Possible Bullying!!","On Instagram!!"+
                    this.mInstagramBullyingInstance+" instances","Possible Bullying!");
            updateMonitoringPostTable();
            universalClassifierUpdate();
            UtilityVariables.APP_STATUS = UtilityVariables.APP_STATUS_WAITING;
            Log.i(UtilityVariables.tag,"*********************************************************************");
        }catch (Exception e)
        {
            Log.i(UtilityVariables.tag,"Exception in checkInstagramNotification function: "+e.toString());
        }
    }
}
