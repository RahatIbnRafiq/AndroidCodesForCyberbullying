package com.bamboo.bullyalert.UtilityPackage;

import com.bamboo.bullyalert.model.Notification;

import java.util.ArrayList;

/**
 * Created by Rahat Ibn Rafiq on 10/17/2017.
 */

public class UtilityVariables {

    public static final String tag = "BullyAlert";

    //public static final String ROOT_URL = "http://192.168.0.13:3000";
    //public static final String ROOT_URL = "http://192.168.5.179:3000";
    public static final String ROOT_URL = "http://128.138.232.94:3000";
    public static final String LOGIN_GUARDIAN = ROOT_URL+"/api/guardian/login";
    public static final String REGISTER_GUARDIAN = ROOT_URL+"/api/guardian/register";
    public static final String ADD_NOTIFICATION = ROOT_URL+"/api/notification/add";
    public static final String ADD_NOTIFICATION_FEEDBACK = ROOT_URL+"/api/notificationfeedback/add";

    public static final String GET_REQUEST_METHOD = "GET";
    public static final int READ_TIMEOUT = 60*1000;
    public static final int CONNECTION_TIMEOUT = 60*1000;
    public static final long ALARM_INTERVAL = 60*1000;


    public static final String INSTAGRAM_CLIENT_ID = "362741ea25924668af07edfb3873e3a2";
    public static final String INSTAGRAM_MONITOR_USER_SERVER  = ROOT_URL+"/api/guardian/instagram/useraddRequest";
    public static final String INSTAGRAM_CALLBACK_URL = "http://localhost";
    public static final String INSTAGRAM_AUTH_URL = "https://api.instagram.com/oauth/authorize/";
    public static final String INSTAGRAM_GET_ACCESS_TOKEN  = ROOT_URL+"/api/guardian/instagramAuthToken";
    public static final String INSTAGRAM_GET_MONITORING_USERS = ROOT_URL+"/api/guardian/instagram/getMonitoringUsers";


    public static final String GET_NOTIFICATIONS = ROOT_URL+"/api/guardian/getNotifications";
    public static final String GET_FEEDBACKS = ROOT_URL+"/api/guardian/getNotificationFeedbacks";

    public static final String INSTAGRAM_API_USER_SEARCH  = "https://api.instagram.com/v1/users/search/?q=";

    public static final String INSTAGRAM_API_USER_INFORMATION  = "https://api.instagram.com/v1/users/";


    public static final int MIN_PASSWORD_LENGTH = 4;



    public static String USER_EMAIL="email";

    public static String INSTAGRAM_AUTHENTICATION_TOKEN = null;

    public static boolean IS_ALARM_ON =  false;



    public static String INTENT_VARIABLE_NOTIFICATIONS = "NOTIFICATIONS";
    public static String INTENT_VARIABLE_NOTIFICATIONS_AUTHENTICATION = "NOTIFICATIONS_TO_GET_TOKEN";


    public static ArrayList<Notification> notificationFeedback = null;


    public static String APP_STATUS = "";
    public static String APP_STATUS_WAITING = "THE APP IS NOW WAITING FOR A WHILE. IT WILL POLL THE SOCIAL NETWORK AT: ";
    public static String APP_STATUS_GETTING_POSTS = "THE APP IS NOW COLLECTING SOCIAL NETWORK POSTINGS OF THE USERS YOU ARE MONITORING";
    public static String APP_STATUS_GETTING_COMMENTS = "THE APP IS NOW COLLECTING COMMENTS FOR THE USERS' SOCIAL NETWORKS YOU ARE MONITORING";
    public static String APP_STATUS_CLASSIFYING = "THE APP IS NOW CLASSIFYING THE POSTS. YOU WILL GET A NOTIFICATION IF THERE IS A POTENTIAL BULLYING INSTANCE";

}
