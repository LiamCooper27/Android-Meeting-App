package com.example.liamcooper.meetingapp.Models;

/**
 * model to help database Colleague
 */
public class Colleague {
    public int userID;
    public int colleagueID;

    public static final String TABLE = "Colleagues";
    public static final String KEY_userID = "userID";
    public static final String KEY_colleagueID = "colleagueID";
}
