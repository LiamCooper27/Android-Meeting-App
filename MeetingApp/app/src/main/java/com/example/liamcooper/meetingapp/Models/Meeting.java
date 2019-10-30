package com.example.liamcooper.meetingapp.Models;

/**
 * model to help database Meeting
 */
public class Meeting {
    public int meetingID;
    public String title;
    public String notes;
    public String date;
    public String time;
    public double longitude;
    public double latitude;
    public int userID;

    public static final String TABLE = "Meeting";
    public static final String KEY_meetingID = "meetingID";
    public static final String KEY_title = "title";
    public static final String KEY_notes = "notes";
    public static final String KEY_date = "date";
    public static final String KEY_time = "time";
    public static final String KEY_longitude = "longitude";
    public static final String KEY_latitude = "latitude";
    public static final String KEY_userID = "userID";
}
