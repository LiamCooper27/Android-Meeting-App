package com.example.liamcooper.meetingapp.Models;

/**
 * model to help database Setting
 */
public class Setting {
    public int userID;
    public int fontSize;
    public String fontColour;

    public static final String TABLE = "Settings";
    public static final String KEY_userID = "userID";
    public static final String KEY_fontSize = "title";
    public static final String KEY_fontColour = "colour";


}
