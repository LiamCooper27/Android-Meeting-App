package com.example.liamcooper.meetingapp.Controllers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.liamcooper.meetingapp.Models.Attendee;
import com.example.liamcooper.meetingapp.Models.Colleague;
import com.example.liamcooper.meetingapp.Models.Meeting;
import com.example.liamcooper.meetingapp.Models.Message;
import com.example.liamcooper.meetingapp.Models.Setting;
import com.example.liamcooper.meetingapp.Models.User;

/**
 * this class is the database helper which provides it with the tables and columns
 */
public class DbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 3;
    private static final String DATABASE_NAME = "crud.db";

    /**
     * constructor
     * @param context
     */
    public DbHelper(Context context ) {
        super(context, DATABASE_NAME,null, DATABASE_VERSION);
    }

    /**
     * creates the tables for the database
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USER_TABLE = "CREATE TABLE " + User.TABLE  + "("
                + User.KEY_ID  + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
                + User.KEY_firstname + " TEXT, "
                + User.KEY_lastname + " TEXT, "
                + User.KEY_email + " TEXT, "
                + User.KEY_password + " TEXT )";

        String CREATE_MEETING_TABLE = "CREATE TABLE " + Meeting.TABLE  + "("
                + Meeting.KEY_meetingID  + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
                + Meeting.KEY_title + " TEXT, "
                + Meeting.KEY_notes + " TEXT, "
                + Meeting.KEY_date + " TEXT, "
                + Meeting.KEY_time + " TEXT, "
                + Meeting.KEY_longitude + " TEXT, "
                + Meeting.KEY_latitude + " TEXT, "
                + Meeting.KEY_userID + " TEXT )";

        String CREATE_ATTENDEE_TABLE = "CREATE TABLE " + Attendee.TABLE + "("
                + Attendee.KEY_meetingID + " TEXT, "
                + Attendee.KEY_date + " TEXT, "
                + Attendee.KEY_time + " TEXT, "
                + Attendee.KEY_userID + " TEXT )";

        String CREATE_COLLEAGUE_TABLE = "CREATE TABLE " + Colleague.TABLE + "("
                + Colleague.KEY_userID + " TEXT, "
                + Colleague.KEY_colleagueID + " TEXT )";

        String CREATE_MESSAGE_TABLE = "CREATE TABLE " + Message.TABLE + "("
                + Message.KEY_messageID + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
                + Message.KEY_meetingID + " TEXT, "
                + Message.KEY_userID + " TEXT )";

        String CREATE_SETTINGS_TABLE = "CREATE TABLE " + Setting.TABLE + "("
                + Setting.KEY_userID + " TEXT, "
                + Setting.KEY_fontSize + " TEXT, "
                + Setting.KEY_fontColour + " TEXT )";

        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_MEETING_TABLE);
        db.execSQL(CREATE_ATTENDEE_TABLE);
        db.execSQL(CREATE_COLLEAGUE_TABLE);
        db.execSQL(CREATE_MESSAGE_TABLE);
        db.execSQL(CREATE_SETTINGS_TABLE);

    }

    /**
     * changes the versions of the database, used when databse has been changed
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + User.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + Meeting.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + Attendee.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + Colleague.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + Message.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + Setting.TABLE);

        // Create tables again
        onCreate(db);
    }
}
