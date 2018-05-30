package com.bamboo.bullyalert.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Rahat Ibn Rafiq on 10/25/2017.
 */

public class MonitoringPostDAO extends MonitoringPostDBDAO
{
    public MonitoringPostDAO(Context context)
    {
        super(context);
    }

    public long insertMonitoringPost(MonitoringPost post)
    {
        ContentValues values = new ContentValues();
        values.put(DataBaseHelper.MONITORING_POSTS_TABLE_COLUMN_EMAIL, post.email);
        values.put(DataBaseHelper.MONITORING_POSTS_TABLE_COLUMN_USERNAME, post.username);
        values.put(DataBaseHelper.MONITORING_POSTS_TABLE_COLUMN_USERID, post.userid);
        values.put(DataBaseHelper.MONITORING_POSTS_TABLE_COLUMN_POSTID, post.postid);
        values.put(DataBaseHelper.MONITORING_POSTS_TABLE_COLUMN_LAST_TIME_CHECKED, post.lastTimeChecked);
        values.put(DataBaseHelper.MONITORING_POSTS_TABLE_COLUMN_SOCIAL_NETWORK, post.socialNetwork);
        values.put(DataBaseHelper.MONITORING_POSTS_TABLE_COLUMN_PSOT_CODE, post.postCode);
        return database.insert(DataBaseHelper.MONITORING_POSTS_TABLE, null, values);
    }

    public void updateLastTimeCheckedForPosts(HashMap<String,MonitoringPost> mPostsFromDB)
    {
        for (Map.Entry<String, MonitoringPost> entry : mPostsFromDB.entrySet())
        {
            String postid = entry.getKey();
            MonitoringPost post = entry.getValue();
            if(!post.lastTimeChecked.equals("0"))
            {
                String email = post.email;
                String lastTimeChecked = post.lastTimeChecked;
                ContentValues values = new ContentValues();
                values.put(DataBaseHelper.MONITORING_POSTS_TABLE_COLUMN_LAST_TIME_CHECKED, lastTimeChecked);
                int result = database.update(DataBaseHelper.MONITORING_POSTS_TABLE,
                        values,
                        DataBaseHelper.MONITORING_POSTS_TABLE_COLUMN_POSTID + " = ? AND " + DataBaseHelper.MONITORING_POSTS_TABLE_COLUMN_EMAIL + " = ?",
                        new String[]{postid, email});
            }
        }
    }

    public HashMap<String,MonitoringPost> fetchAllMonitoringPostsByEmail(String email)
    {
        HashMap<String,MonitoringPost> postMap = new HashMap<>();
        String sql = "SELECT * FROM " + DataBaseHelper.MONITORING_POSTS_TABLE + " WHERE " + DataBaseHelper.MONITORING_POSTS_TABLE_COLUMN_EMAIL + " = ?";
        Cursor cursor = database.rawQuery(sql, new String[] { email + "" });
        if(cursor != null)
        {
            if(cursor.moveToFirst())
            {
                do {
                    MonitoringPost post = new MonitoringPost();
                    post.email = cursor.getString(cursor.getColumnIndex(DataBaseHelper.MONITORING_POSTS_TABLE_COLUMN_EMAIL));
                    post.username = cursor.getString(cursor.getColumnIndex(DataBaseHelper.MONITORING_POSTS_TABLE_COLUMN_USERNAME));
                    post.userid = cursor.getString(cursor.getColumnIndex(DataBaseHelper.MONITORING_POSTS_TABLE_COLUMN_USERID));
                    post.postid = cursor.getString(cursor.getColumnIndex(DataBaseHelper.MONITORING_POSTS_TABLE_COLUMN_POSTID));
                    post.lastTimeChecked = cursor.getString(cursor.getColumnIndex(DataBaseHelper.MONITORING_POSTS_TABLE_COLUMN_LAST_TIME_CHECKED));
                    post.socialNetwork = cursor.getString(cursor.getColumnIndex(DataBaseHelper.MONITORING_POSTS_TABLE_COLUMN_SOCIAL_NETWORK));
                    post.postCode = cursor.getString(cursor.getColumnIndex(DataBaseHelper.MONITORING_POSTS_TABLE_COLUMN_PSOT_CODE));
                    postMap.put(post.postid,post);
                }while(cursor.moveToNext());
            }
        }
        return postMap;
    }
}
