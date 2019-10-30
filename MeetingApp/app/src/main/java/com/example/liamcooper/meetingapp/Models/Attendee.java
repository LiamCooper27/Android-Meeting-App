package com.example.liamcooper.meetingapp.Models;

/**
 * model to help database Attendee
 */
public class Attendee {
    public int meetingID;
    public String date;
    public String time;
    public int userID;

    public static final String TABLE = "Attendees";
    public static final String KEY_meetingID = "meetingID";
    public static final String KEY_date = "date";
    public static final String KEY_time = "time";
    public static final String KEY_userID = "userID";
}
