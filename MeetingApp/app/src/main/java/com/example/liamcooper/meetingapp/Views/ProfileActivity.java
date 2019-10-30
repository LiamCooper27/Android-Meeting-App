package com.example.liamcooper.meetingapp.Views;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.liamcooper.meetingapp.Controllers.AttendeeRepo;
import com.example.liamcooper.meetingapp.Controllers.MeetingRepo;
import com.example.liamcooper.meetingapp.Controllers.SettingRepo;
import com.example.liamcooper.meetingapp.Controllers.UserRepo;
import com.example.liamcooper.meetingapp.Models.Meeting;
import com.example.liamcooper.meetingapp.Models.Setting;
import com.example.liamcooper.meetingapp.Models.User;
import com.example.liamcooper.meetingapp.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * this class displays users details
 */
public class ProfileActivity extends AppCompatActivity implements View.OnClickListener{

    //interface objects
    TextView tvFirstName, tvLastName, tvEmail, meetingTitle, meetingID, meetingDate, meetingTime, tvHistoryTitle;
    Button btnSettings, btnHome, btnMessages, btnAccount, btnLogout;
    ListView meetingHistoryList;
    ListAdapter adapter;

    ArrayList<HashMap<String, String>> userMeetingHistory = new ArrayList<>();
    Calendar c;

    private int fontSize = 0;
    private String fontColour = "";
    private int navHelper = 2;
    private int loggedInUser;

    /**
     * constructor
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Intent e = getIntent();
        loggedInUser = e.getIntExtra("uID",0);
        navHelper = e.getIntExtra("navHelper",0);

        getFontSettings();

        meetingHistoryList = findViewById(R.id.meetingHistoryList);

        btnSettings = findViewById(R.id.btnSettings);
        btnAccount = findViewById(R.id.btnAccount);
        btnLogout = findViewById(R.id.btnLogout);
        btnMessages = findViewById(R.id.btnMessages);
        btnHome = findViewById(R.id.btnHome);

        btnSettings.setOnClickListener(this);
        btnLogout.setOnClickListener(this);
        btnMessages.setOnClickListener(this);
        btnHome.setOnClickListener(this);

        displayProfile();
        showAllMeetings();
    }

    /**
     * handles buttons on click events
     * @param v
     */
    @Override
    public void onClick(View v) {
        if (v == findViewById(R.id.btnSettings)) {
            Intent intent = new Intent(this,SettingsActivity.class);
            intent.putExtra("uID", loggedInUser);
            startActivity(intent);
            finish();

        } else if(v == findViewById(R.id.btnAccount)) {
            Intent intent = new Intent(this,ProfileActivity.class);
            intent.putExtra("uID", loggedInUser);
            intent.putExtra("navHelper", 2);
            startActivity(intent);
            finish();
        } else if(v == findViewById(R.id.btnHome)) {
            Intent intent = new Intent(this,MeetingsActivity.class);
            intent.putExtra("uID", loggedInUser);
            intent.putExtra("navHelper", 2);
            startActivity(intent);
            finish();
        } else if(v == findViewById(R.id.btnMessages)) {
            Intent intent = new Intent(this,MessagesActivity.class);
            intent.putExtra("uID", loggedInUser);
            intent.putExtra("navHelper", 2);
            startActivity(intent);
            finish();
        } else {
            finish();
        }

    }

    /**
     * displays the profile
     */
    public void displayProfile() {

        UserRepo userRepo = new UserRepo(this);
        User user = new User();

        Cursor c = userRepo.getSpecificUser(loggedInUser);
        while(c.moveToNext()) {
            tvFirstName.setText(c.getString(c.getColumnIndex(User.KEY_firstname)));
            tvLastName.setText(c.getString(c.getColumnIndex(User.KEY_lastname)));
            tvEmail.setText(c.getString(c.getColumnIndex(User.KEY_email)));
        }

    }

    //gets the meeting history of the user, previous meetings before todays date
    public void getMeetingHistory() {
        //userMeetingHistory.clear();

        MeetingRepo mRepo = new MeetingRepo(this);
        AttendeeRepo aRepo = new AttendeeRepo(this);

        c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH);
        int year = c.get(Calendar.YEAR);

        String cDate = year + "-" + String.format("%02d",month+1) + "-" + String.format("%02d",day);

        Cursor c1 = aRepo.getUserPreviousMeetings(loggedInUser, cDate);

        if(c1.getCount() == 0) {
            // show message
            Toast.makeText(this,"Nothing here",Toast.LENGTH_SHORT).show();
        }
        while(c1.moveToNext()) {
            //Cursor cursor = mRepo.getMeeting(c1.getColumnIndex(Attendee.KEY_meetingID));

            Cursor cursor = mRepo.getMeeting(c1.getInt(c1.getColumnIndex(Meeting.KEY_meetingID)));
            while (cursor.moveToNext()) {
                HashMap<String, String> pastMeetings = new HashMap<>();
                pastMeetings.put("ID", cursor.getString(cursor.getColumnIndex(Meeting.KEY_meetingID)));
                pastMeetings.put("title", cursor.getString(cursor.getColumnIndex(Meeting.KEY_title)));
                pastMeetings.put("date", cursor.getString(cursor.getColumnIndex(Meeting.KEY_date)));
                pastMeetings.put("time", cursor.getString(cursor.getColumnIndex(Meeting.KEY_time)));
                pastMeetings.put("creator", cursor.getString(cursor.getColumnIndex(Meeting.KEY_userID)));
                userMeetingHistory.add(pastMeetings);
            }
        }

    }

    /**
     * show all of the meeting history in listview
     *
     */
    public void showAllMeetings(){
        getMeetingHistory();

        if(userMeetingHistory.size()!=0) {
            meetingHistoryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    meetingTitle = view.findViewById(R.id.meetingTitle);
                    meetingID = view.findViewById(R.id.meetingID);
                    meetingDate = findViewById(R.id.meetingDate);
                    meetingTime = findViewById(R.id.meetingTime);
                    //creatorID = findViewById(R.id.creatorID);

                    String meetID = meetingID.getText().toString();

                    Intent objIndent = new Intent(getApplicationContext(),MeetingView.class);
                    objIndent.putExtra("mID", meetID);
                    objIndent.putExtra("uID", loggedInUser);
                    objIndent.putExtra("navHelper", 2);
                    startActivity(objIndent);
                    finish();
                }
            });
            adapter = new SimpleAdapter(ProfileActivity.this, userMeetingHistory, R.layout.list_item,
                    new String[]{"title", "ID", "date", "time"}, new int[]{R.id.meetingTitle, R.id.meetingID, R.id.meetingDate, R.id.meetingTime});
            meetingHistoryList.setAdapter(adapter);
        }
    }

    /**
     * get users font settings
     */
    public void getFontSettings(){
        tvFirstName = findViewById(R.id.tvFirstName);
        tvLastName = findViewById(R.id.tvLastName);
        tvEmail = findViewById(R.id.tvEmail);
        tvHistoryTitle = findViewById(R.id.tvHistoryTitle);

        SettingRepo sRepo = new SettingRepo(this);
        Setting s = sRepo.getUserSettings(loggedInUser);
        fontColour = s.fontColour;
        fontSize = s.fontSize;

        tvFirstName.setTextSize(fontSize);
        tvFirstName.setTextColor(Color.parseColor(fontColour));
        tvLastName.setTextSize(fontSize);
        tvLastName.setTextColor(Color.parseColor(fontColour));
        tvEmail.setTextSize(fontSize);
        tvEmail.setTextColor(Color.parseColor(fontColour));
        tvHistoryTitle.setTextSize(fontSize);
        tvHistoryTitle.setTextColor(Color.parseColor(fontColour));

    }

    /**
     * handles back pressed event
     */
    @Override
    public void onBackPressed() {
        if(navHelper == 1) {
            finish();
            Intent intent = new Intent(this,MeetingsActivity.class);
            intent.putExtra("uID", loggedInUser);
            intent.putExtra("navHelper", 1);
            startActivity(intent);
        } else if(navHelper == 2) {
            finish();
            Intent intent = new Intent(this,ProfileActivity.class);
            intent.putExtra("uID", loggedInUser);
            intent.putExtra("navHelper", 2);
            startActivity(intent);
        } else {
            finish();
            Intent intent = new Intent(this,MessagesActivity.class);
            intent.putExtra("uID", loggedInUser);
            intent.putExtra("navHelper", 3);
            startActivity(intent);
        }

    }

}
