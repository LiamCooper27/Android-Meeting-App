package com.example.liamcooper.meetingapp.Controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.liamcooper.meetingapp.Models.Meeting;

/**
 * this class takes care of the CRUD operations for the meetings
 */
public class MeetingRepo {
    private DbHelper dbHelper;

    /**
     * constructor
     * @param context
     */
    public MeetingRepo(Context context) {
        dbHelper = new DbHelper(context);
    }

    /**
     * insert a meeting into database
     * @param m
     * @return
     */
    public int insertMeeting(Meeting m) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(Meeting.KEY_title, m.title);
        values.put(Meeting.KEY_notes, m.notes);
        values.put(Meeting.KEY_date, m.date);
        values.put(Meeting.KEY_time, m.time);
        values.put(Meeting.KEY_longitude, m.longitude);
        values.put(Meeting.KEY_latitude, m.latitude);
        values.put(Meeting.KEY_userID, m.userID);

        long meetingID = db.insert(Meeting.TABLE, null, values);

        db.close();

        return (int) meetingID;
    }

    /**
     * get all the meeting data in the table
     * @return cursor of all meeting data
     */
    public Cursor getAllMeetingData() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+Meeting.TABLE,null);
        return res;
    }

    /**
     * get a specific meeting
     * @param meetingID
     * @return
     */
    public Cursor getMeeting(int meetingID) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String selectQuery =  "SELECT * " +
                " FROM " + Meeting.TABLE +
                " WHERE meetingID =?";

        Cursor cursor = db.rawQuery(selectQuery, new String[] { String.valueOf(meetingID) });
        return cursor;
    }

}
