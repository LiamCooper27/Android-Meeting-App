package com.example.liamcooper.meetingapp.Controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.liamcooper.meetingapp.Models.Colleague;

/**
 * this class takes care of the CRUD capabilites for colleague table
 */
public class ColleagueRepo {
    private DbHelper dbHelper;

    /**
     * constructor
     * @param context
     */
    public ColleagueRepo(Context context) {
        dbHelper = new DbHelper(context);
    }

    /**
     * inserts a colleague into the table
     * @param c
     */
    public void insertColleague(Colleague c) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(Colleague.KEY_userID, c.userID);
        values.put(Colleague.KEY_colleagueID, c.colleagueID);

        db.insert(Colleague.TABLE, null, values);

        db.close();

    }

    /**
     * gets the colleagues of a specific user
     * @param userID
     * @return a cursor of all colleagues
     */
    public Cursor getUsersColleagues(int userID) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String selectQuery =  "SELECT * " +
                " FROM " + Colleague.TABLE +
                " WHERE userID =?";

        Cursor cursor = db.rawQuery(selectQuery, new String[] { String.valueOf(userID) });

        return cursor;
    }

    /**
     * checks to see if user and others user are colleagues
     * @param userID
     * @param collID
     * @return a boolean if it is true or not
     */
    public Boolean isColleague(int userID, int collID) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String selectQuery =  "SELECT * " +
                " FROM " + Colleague.TABLE +
                " WHERE userID =? AND colleagueID =?";

        Cursor cursor = db.rawQuery(selectQuery, new String[] { String.valueOf(userID), String.valueOf(collID) });

        if(cursor == null) {
            return false;
        } else {
            return true;
        }
    }
}
