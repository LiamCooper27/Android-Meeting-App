package com.example.liamcooper.meetingapp.Controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.liamcooper.meetingapp.Models.Colleague;
import com.example.liamcooper.meetingapp.Models.Message;
import com.example.liamcooper.meetingapp.Models.Setting;

/**
 * this class takes care of the CRUD operations for settings
 * users font settings
 */
public class SettingRepo {

    private DbHelper dbHelper;

    /**
     * constructor
     * @param context
     */
    public SettingRepo(Context context) {
        dbHelper = new DbHelper(context);
    }

    /**
     * inserts a setting into a database
     * @param s
     */
    public void insertSetting(Setting s) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(Setting.KEY_userID, s.userID);
        values.put(Setting.KEY_fontSize, s.fontSize);
        values.put(Setting.KEY_fontColour, s.fontColour);

        db.insert(Setting.TABLE, null, values);

        db.close();

    }

    /**
     * gets all users preferred settings
     * @param userID
     * @return
     */
    public Setting getUserSettings(int userID) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Setting s = new Setting();

        String selectQuery =  "SELECT * " +
                " FROM " + Setting.TABLE +
                " WHERE userID =?";

        Cursor cursor = db.rawQuery(selectQuery, new String[] { String.valueOf(userID) });

        if(cursor.getCount() == 0) {
            s.userID = userID;
            s.fontSize = 15;
            s.fontColour = "#000000";
        }

        while(cursor.moveToNext()) {
            s.userID = cursor.getInt(cursor.getColumnIndex(Setting.KEY_userID));
            s.fontSize = cursor.getInt(cursor.getColumnIndex(Setting.KEY_fontSize));
            s.fontColour = cursor.getString(cursor.getColumnIndex(Setting.KEY_fontColour));
        }

        db.close();
        return s;
    }

    /**
     * deletes users settings
     * @param userID
     */
    public void deleteSetting(int userID) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("DELETE FROM " + Setting.TABLE + " WHERE " + Setting.KEY_userID + " = " + String.valueOf(userID));
        db.close();
    }
}
