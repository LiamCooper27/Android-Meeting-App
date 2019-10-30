package com.example.liamcooper.meetingapp.Models;

/**
 * model to help database Message
 */
public class Message {
    public int messageID;
    public int meetingID;
    public int userID;

    public static final String TABLE = "Message";
    public static final String KEY_userID = "Receiver";
    public static final String KEY_meetingID = "meetingID";
    public static final String KEY_messageID = "messageID";
}
