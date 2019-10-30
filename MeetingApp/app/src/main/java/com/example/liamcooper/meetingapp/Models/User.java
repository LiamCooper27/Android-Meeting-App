package com.example.liamcooper.meetingapp.Models;

/**
 * model to help database User
 */
public class User {
    public int userID;
    public String firstName;
    public String lastName;
    public String email;
    public String password;

    public static final String TABLE = "User";
    public static final String KEY_ID = "userID";
    public static final String KEY_firstname = "firstname";
    public static final String KEY_lastname = "lastname";
    public static final String KEY_email = "email";
    public static final String KEY_password = "password";
}
