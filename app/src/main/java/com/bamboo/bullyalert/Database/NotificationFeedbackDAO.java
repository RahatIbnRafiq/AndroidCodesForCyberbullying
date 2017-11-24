package com.bamboo.bullyalert.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Rahat Ibn Rafiq on 11/8/2017.
 */

public class NotificationFeedbackDAO extends NotificationFeedbackDBDAO
{

    public NotificationFeedbackDAO(Context context)
    {
        super(context);
    }


    public long insertUser(NotificationFeedback notificationFeedback)
    {
        ContentValues values = new ContentValues();
        values.put(DataBaseHelper.FEEDBACK_TABLE_COLUMN_EMAIL, notificationFeedback.getmEmail());
        values.put(DataBaseHelper.FEEDBACK_TABLE_COLUMN_FEEDBACK, notificationFeedback.getmFeedback());
        values.put(DataBaseHelper.FEEDBACK_TABLE_COLUMN_NOTIFICATION_ID, notificationFeedback.getmNotificationId());
        values.put(DataBaseHelper.FEEDBACK_TABLE_COLUMN_COMMENTS, notificationFeedback.getmComments());
        values.put(DataBaseHelper.FEEDBACK_TABLE_COLUMN_PREDICTED, notificationFeedback.getmPredicted());
        return database.insert(DataBaseHelper.FEEDBACK_TABLE, null, values);
    }

    public long deleteFeedback(String email,String notificationid)
    {

        String table = DataBaseHelper.FEEDBACK_TABLE;
        String whereClause = DataBaseHelper.FEEDBACK_TABLE_COLUMN_EMAIL+"=? AND "+DataBaseHelper.FEEDBACK_TABLE_COLUMN_NOTIFICATION_ID+"=?";
        String[] whereArgs = new String[] { email,notificationid};
        return database.delete(table, whereClause, whereArgs);
    }

    public ArrayList<NotificationFeedback> fetchAllFeedbackByEmail(String email)
    {
        ArrayList<NotificationFeedback> notificationFeedbackList = new ArrayList<>();
        String sql = "SELECT * FROM " + DataBaseHelper.FEEDBACK_TABLE
                + " WHERE " + DataBaseHelper.FEEDBACK_TABLE_COLUMN_EMAIL + " = ?";

        Cursor cursor = database.rawQuery(sql, new String[] { email + "" });
        if(cursor != null)
        {
            if(cursor.moveToFirst())
            {
                do {
                    String userEmail = cursor.getString(cursor.getColumnIndex(DataBaseHelper.FEEDBACK_TABLE_COLUMN_EMAIL));
                    String notificationId = cursor.getString(cursor.getColumnIndex(DataBaseHelper.FEEDBACK_TABLE_COLUMN_NOTIFICATION_ID));
                    String predicted = cursor.getString(cursor.getColumnIndex(DataBaseHelper.FEEDBACK_TABLE_COLUMN_PREDICTED));
                    String feedback = cursor.getString(cursor.getColumnIndex(DataBaseHelper.FEEDBACK_TABLE_COLUMN_FEEDBACK));
                    String comments = cursor.getString(cursor.getColumnIndex(DataBaseHelper.FEEDBACK_TABLE_COLUMN_COMMENTS));
                    NotificationFeedback notificationFeedback = new NotificationFeedback(userEmail,
                            notificationId,comments,predicted,feedback);

                    notificationFeedbackList.add(notificationFeedback);

                }while(cursor.moveToNext());
            }
        }
        return notificationFeedbackList;

    }

}
