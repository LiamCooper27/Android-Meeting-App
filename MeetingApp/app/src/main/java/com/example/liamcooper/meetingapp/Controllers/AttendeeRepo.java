package com.example.liamcooper.meetingapp.Controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.liamcooper.meetingapp.Models.Attendee;

/**
 * this class takes care of the CRUD operations for attendees table
 */
public class AttendeeRepo {
    private DbHelper dbHelper;

    /**
     * constructor
     * @param context
     */
    public AttendeeRepo(Context context) {
        dbHelper = new DbHelper(context);
    }

    /**
     * inserts an attendee into the table
     * @param a = attendee
     */
    public void insertAttendee(Attendee a) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(Attendee.KEY_meetingID, a.meetingID);
        values.put(Attendee.KEY_date, a.date);
        values.put(Attendee.KEY_time, a.time);
        values.put(Attendee.KEY_userID, a.userID);

        db.insert(Attendee.TABLE, null, values);

        db.close();

    }

    /**
     * gets the attendees of a specific meeting
     * @param meetingID
     * @return a cursor of meeting attendees
     */
    public Cursor getMeetingAttendees(String meetingID) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String attendees = "";
        String selectQuery =  "SELECT * " +
                " FROM " + Attendee.TABLE +
                " WHERE meetingID =?";

        Cursor cursor = db.rawQuery(selectQuery, new String[] {String.valueOf(meetingID)});
        return cursor;
    }

    /**
     * gets all of the attendess
     * @return
     */
    public Cursor getAllAttendeeData() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+Attendee.TABLE,null);
        return res;
    }

    /**
     * gets all of a users upcoming meetings
     * @param userID
     * @param cDate
     * @return a cursor of all meetings
     */
    public Cursor getUserUpcomingMeetings(int userID, String cDate) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String selectQuery =  "SELECT * " +
                " FROM " + Attendee.TABLE +
                " WHERE userID =? AND date >= ?";

        Cursor cursor = db.rawQuery(selectQuery, new String[] { String.valueOf(userID), cDate});
        return cursor;
    }

    /**
     * gets all of a users previous meetings for their history
     * @param userID
     * @param cDate
     * @return a cursor of all previous meetings
     */
    public Cursor getUserPreviousMeetings(int userID, String cDate) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String selectQuery =  "SELECT * " +
                " FROM " + Attendee.TABLE +
                " WHERE userID =? AND date < ?";

        Cursor cursor = db.rawQuery(selectQuery, new String[] { String.valueOf(userID), cDate});
        return cursor;
    }
}
