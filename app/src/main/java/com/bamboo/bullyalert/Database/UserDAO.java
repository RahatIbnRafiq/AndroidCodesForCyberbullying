package com.bamboo.bullyalert.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

/**
 * Created by Rahat Ibn Rafiq on 10/21/2017.
 */

public class UserDAO extends UserDBDAO
{
    public UserDAO(Context context)
    {
        super(context);
    }

    public long insertUser(User user)
    {
        ContentValues values = new ContentValues();
        values.put(DataBaseHelper.USER_TABLE_COLUMN_EMAIL, user.email);
        values.put(DataBaseHelper.USER_TABLE_INSTAGRAM_TOKEN, user.instagramToken);
        return database.insert(DataBaseHelper.USER_TABLE, null, values);
    }


    public long deleteUser(String email)
    {

        String table = DataBaseHelper.USER_TABLE;
        String whereClause = DataBaseHelper.USER_TABLE_COLUMN_EMAIL+"=?";
        String[] whereArgs = new String[] { email };
        return database.delete(table, whereClause, whereArgs);
    }

    public User fetchUser(String email)
    {
        User user = null;
        String sql = "SELECT * FROM " + DataBaseHelper.USER_TABLE
                + " WHERE " + DataBaseHelper.USER_TABLE_COLUMN_EMAIL + " = ?";

        Cursor cursor = database.rawQuery(sql, new String[] { email + "" });
        if (cursor.moveToNext())
        {
            user = new User();
            user.email = cursor.getString(0);
            user.instagramToken = cursor.getString(1);
        }
        return user;
    }
}
