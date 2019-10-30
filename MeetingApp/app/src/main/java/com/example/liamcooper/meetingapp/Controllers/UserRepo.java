package com.example.liamcooper.meetingapp.Controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.liamcooper.meetingapp.Models.User;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * this class is for CRUD operations for users table
 */
public class UserRepo {
    private DbHelper dbHelper;

    /**
     * constructor
     * @param context
     */
    public UserRepo(Context context) {
        dbHelper = new DbHelper(context);
    }

    /**
     * inserts user into database
     * @param u
     * @return
     */
    public int insertUser(User u) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(User.KEY_firstname, u.firstName);
        values.put(User.KEY_lastname, u.lastName);
        values.put(User.KEY_email, u.email);
        values.put(User.KEY_password, u.password);

        long userID = db.insert(User.TABLE, null, values);

        db.close();

        return (int) userID;
    }

    /**
     * checks if user can login
     * @param e = email
     * @param p = password
     * @return returns the user if credentials are correct
     */
    public User login(String e, String p) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery =  "SELECT *" +
                " FROM " + User.TABLE +
                " WHERE email =? AND password =?";

        Cursor cursor = db.rawQuery(selectQuery, new String[] { String.valueOf(e), String.valueOf(p) });

        if(cursor.getCount() > 0) {
            User u = new User();
            cursor.moveToFirst();

            u.userID = cursor.getInt(0);
            u.firstName = cursor.getString(1);
            u.lastName = cursor.getString(2);
            u.email = cursor.getString(3);
            u.password = cursor.getString(4);

            return u;
        } else {
            return null;
        }
    }

    /**
     * gets all user data
     * @return
     */
    public Cursor getAllUserData() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+User.TABLE,null);
        return res;
    }

    /**
     * gets user id which matches email that has been passed in
     * @param email
     * @return
     */
    public int getUserID(String email) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        int userID = 0;

        String selectQuery =  "SELECT " +
                User.KEY_ID +
                " FROM " + User.TABLE +
                " WHERE email =?";

        Cursor cursor = db.rawQuery(selectQuery, new String[] { String.valueOf(email) });

        if (cursor.moveToFirst()) {
            do {
                userID = cursor.getInt(cursor.getColumnIndex(User.KEY_ID));

            } while (cursor.moveToNext());
        }

        cursor.close();
        return userID;
    }

    /**
     * gets a specific user
     * @param userID
     * @return
     */
    public Cursor getSpecificUser(int userID) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String selectQuery =  "SELECT * " +
                " FROM " + User.TABLE +
                " WHERE userID =?";

        Cursor cursor = db.rawQuery(selectQuery, new String[] { String.valueOf(userID) });
        return cursor;
    }
}
