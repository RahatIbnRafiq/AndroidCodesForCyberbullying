package com.bamboo.bullyalert.UtilityPackage;

import com.bamboo.bullyalert.model.Notification;

import java.util.ArrayList;

/**
 * Created by Rahat Ibn Rafiq on 10/17/2017.
 */

public class UtilityVariables {

    public static final String tag = "BullyAlert";

    public static final String ROOT_URL = "http://192.168.0.13:3000";
    //public static final String ROOT_URL = "http://192.168.5.180:3000";
    public static final String LOGIN_GUARDIAN = ROOT_URL+"/api/guardian/login";
    public static final String REGISTER_GUARDIAN = ROOT_URL+"/api/guardian/register";
    public static final String ADD_NOTIFICATION = ROOT_URL+"/api/notification/add";

    public static final String GET_REQUEST_METHOD = "GET";
    public static final int READ_TIMEOUT = 15000;
    public static final int CONNECTION_TIMEOUT = 15000;
    public static final long ALARM_INTERVAL = 60*1000;


    public static final String INSTAGRAM_CLIENT_ID = "362741ea25924668af07edfb3873e3a2";
    public static final String INSTAGRAM_MONITOR_USER_SERVER  = ROOT_URL+"/api/guardian/instagram/useraddRequest";
    public static final String INSTAGRAM_CALLBACK_URL = "http://localhost";
    public static final String INSTAGRAM_AUTH_URL = "https://api.instagram.com/oauth/authorize/";
    public static final String INSTAGRAM_GET_ACCESS_TOKEN  = ROOT_URL+"/api/guardian/instagramAuthToken";
    public static final String INSTAGRAM_GET_MONITORING_USERS = ROOT_URL+"/api/guardian/instagram/getMonitoringUsers";

    public static final String INSTAGRAM_API_USER_SEARCH  = "https://api.instagram.com/v1/users/search/?q=";

    public static final String INSTAGRAM_API_USER_INFORMATION  = "https://api.instagram.com/v1/users/";


    public static final int MIN_PASSWORD_LENGTH = 4;



    public static String USER_EMAIL="email";

    public static String INSTAGRAM_AUTHENTICATION_TOKEN = null;

    public static boolean IS_ALARM_ON =  false;



    public static String INTENT_VARIABLE_NOTIFICATIONS = "NOTIFICATIONS";
    public static String INTENT_VARIABLE_NOTIFICATIONS_AUTHENTICATION = "NOTIFICATIONS_TO_GET_TOKEN";


    public static ArrayList<Notification> notificationFeedback = null;
}
