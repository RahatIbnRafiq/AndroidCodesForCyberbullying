package com.bamboo.bullyalert.Database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Rahat Ibn Rafiq on 11/8/2017.
 */

public class NotificationFeedbackDBDAO
{


    protected SQLiteDatabase database;
    private DataBaseHelper dbHelper;
    private Context mContext;

    public NotificationFeedbackDBDAO(Context context) {
        this.mContext = context;
        dbHelper = DataBaseHelper.getHelper(mContext);
        open();

    }
    public void open() throws SQLException
    {
        if(dbHelper == null)
            dbHelper = DataBaseHelper.getHelper(mContext);
        database = dbHelper.getWritableDatabase();
    }

    public void close()
    {
        dbHelper.close();
        database = null;
    }


}
