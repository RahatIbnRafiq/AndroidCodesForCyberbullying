package com.bamboo.bullyalert.Database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Rahat Ibn Rafiq on 10/21/2017.
 */

public class UserDBDAO
{
    protected SQLiteDatabase database;
    private DataBaseHelper dbHelper;
    private Context mContext;

    public UserDBDAO(Context context) {
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
