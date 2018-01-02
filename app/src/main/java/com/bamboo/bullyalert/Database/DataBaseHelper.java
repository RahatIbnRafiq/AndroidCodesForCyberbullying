package com.bamboo.bullyalert.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.bamboo.bullyalert.UtilityPackage.UtilityVariables;

/**
 * Created by Rahat Ibn Rafiq on 10/21/2017.
 */


public class DataBaseHelper extends SQLiteOpenHelper
{

    private static final String DATABASE_NAME = "CyberSafetyDatabase";
    private static final int DATABASE_VERSION = 2;

    //User table

    public static final String USER_TABLE = "USERS";
    public static final String USER_TABLE_COLUMN_EMAIL = "EMAIL";
    public static final String USER_TABLE_INSTAGRAM_TOKEN = "INSTAGRAM_TOKEN";
    public static final String CREATE_USER_TABLE = "CREATE TABLE IF NOT EXISTS "
            + USER_TABLE + "(" + USER_TABLE_COLUMN_EMAIL + " TEXT PRIMARY KEY, "
            + USER_TABLE_INSTAGRAM_TOKEN + " TEXT " + ")";



    //user feedback table

    public static final String FEEDBACK_TABLE = "FEEDBACKS";
    public static final String FEEDBACK_TABLE_COLUMN_EMAIL = "EMAIL";
    public static final String FEEDBACK_TABLE_COLUMN_NOTIFICATION_ID = "NOTIFICATION_ID";
    public static final String FEEDBACK_TABLE_COLUMN_COMMENTS = "COMMENTS";
    public static final String FEEDBACK_TABLE_COLUMN_PREDICTED = "PREDICTED";
    public static final String FEEDBACK_TABLE_COLUMN_FEEDBACK = "FEEDBACK";
    public static final String CREATE_FEEDBACK_TABLE = "CREATE TABLE IF NOT EXISTS "
            + FEEDBACK_TABLE
            + "("
            + FEEDBACK_TABLE_COLUMN_NOTIFICATION_ID + " TEXT PRIMARY KEY, "
            + FEEDBACK_TABLE_COLUMN_COMMENTS + " TEXT NOT NULL, "
            + FEEDBACK_TABLE_COLUMN_PREDICTED + " TEXT NOT NULL, "
            + FEEDBACK_TABLE_COLUMN_FEEDBACK + " TEXT NOT NULL, "
            + FEEDBACK_TABLE_COLUMN_EMAIL + " TEXT NOT NULL"
            + ")";



    //MonitoringPosts Table

    public static final String MONITORING_POSTS_TABLE = "MONITORING_POSTS";


    public static final String MONITORING_POSTS_TABLE_COLUMN_EMAIL = "EMAIL";
    public static final String MONITORING_POSTS_TABLE_COLUMN_USERNAME = "USERNAME";
    public static final String MONITORING_POSTS_TABLE_COLUMN_USERID = "USERID";
    public static final String MONITORING_POSTS_TABLE_COLUMN_POSTID = "POSTID";
    public static final String MONITORING_POSTS_TABLE_COLUMN_LAST_TIME_CHECKED = "LAST_TIME_CHECKED";
    public static final String MONITORING_POSTS_TABLE_COLUMN_SOCIAL_NETWORK = "SOCIAL_NETWORK";
    public static final String MONITORING_POSTS_TABLE_COLUMN_PSOT_CODE = "POST_CODE";

    public static final String CREATE_MONITORING_POSTS_TABLE = "CREATE TABLE IF NOT EXISTS "
            + MONITORING_POSTS_TABLE + "(" +
            "ID INTEGER PRIMARY KEY AUTOINCREMENT, "+
            MONITORING_POSTS_TABLE_COLUMN_EMAIL + " TEXT NOT NULL, " +
            MONITORING_POSTS_TABLE_COLUMN_POSTID + " TEXT NOT NULL, " +
            MONITORING_POSTS_TABLE_COLUMN_USERNAME + " TEXT, " +
            MONITORING_POSTS_TABLE_COLUMN_USERID + " TEXT, " +
            MONITORING_POSTS_TABLE_COLUMN_LAST_TIME_CHECKED + " TEXT, " +
            MONITORING_POSTS_TABLE_COLUMN_SOCIAL_NETWORK + " TEXT," +
            MONITORING_POSTS_TABLE_COLUMN_PSOT_CODE + " TEXT" +
            ")";




    private static DataBaseHelper instance;

    public static synchronized DataBaseHelper getHelper(Context context) {
        if (instance == null)
            instance = new DataBaseHelper(context);
        return instance;
    }

    private DataBaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onOpen(SQLiteDatabase db)
    {
        super.onOpen(db);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_MONITORING_POSTS_TABLE);
        db.execSQL(CREATE_FEEDBACK_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS "+USER_TABLE);
        db.execSQL("DROP TABLE IF EXISTS "+MONITORING_POSTS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS "+FEEDBACK_TABLE);
        onCreate(db);
    }
}
