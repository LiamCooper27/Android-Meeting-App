package com.example.liamcooper.meetingapp.Controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.liamcooper.meetingapp.Models.Message;

/**
 * this class takes care of the CRUD operations for the message table
 */
public class MessageRepo {
    private DbHelper dbHelper;

    /**
     * constructor
     * @param context
     */
    public MessageRepo(Context context) {
        dbHelper = new DbHelper(context);
    }

    /**
     * inserts a message into the database
     * @param m
     * @return
     */
    public int insertMessage(Message m) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Message.KEY_meetingID, m.meetingID);
        values.put(Message.KEY_userID, m.userID);

        long messageID = db.insert(Message.TABLE, null, values);

        db.close();

        return (int) messageID;
    }

    /**
     * gets all messages for a user
     * @param userID
     * @return
     */
    public Cursor getUserMessages(int userID) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String selectQuery =  "SELECT * " +
                " FROM " + Message.TABLE +
                " WHERE Receiver =?";

        Cursor cursor = db.rawQuery(selectQuery, new String[] { String.valueOf(userID) });
        return cursor;
    }

    /**
     * deletes a specific message
     * @param messageID
     */
    public void deleteMessage(int messageID) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("DELETE FROM " + Message.TABLE + " WHERE " + Message.KEY_messageID + " = " + String.valueOf(messageID));
        db.close();
    }
}
