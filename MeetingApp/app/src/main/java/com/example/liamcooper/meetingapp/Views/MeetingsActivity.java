package com.example.liamcooper.meetingapp.Views;

import android.app.AlertDialog;
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

import com.example.liamcooper.meetingapp.Controllers.AttendeeRepo;
import com.example.liamcooper.meetingapp.Controllers.MeetingRepo;
import com.example.liamcooper.meetingapp.Controllers.SettingRepo;
import com.example.liamcooper.meetingapp.Models.Meeting;
import com.example.liamcooper.meetingapp.Models.Setting;
import com.example.liamcooper.meetingapp.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * this class displays all the upcoming meetings
 */
public class MeetingsActivity extends AppCompatActivity implements View.OnClickListener {

    //interface objects
    TextView title1, meetingTitle, meetingDate, meetingTime, meetingID, creatorID;
    Button btnAddMeeting, btnLogout, btnRemove, btnAccount, btnMessages, btnViewMap, btnHome;
    ListView meetingList;

    private int loggedInUser = 0;
    private int fontSize = 0;
    private String fontColour = "";
    Calendar c;
    private int navHelper = 1;

    //shows users meetings for listview to get
    ArrayList<HashMap<String, String>> userMeetings = new ArrayList<>();

    /**
     * constructor
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meetings);

        btnAddMeeting = findViewById(R.id.btnAddMeeting);
        btnLogout = findViewById(R.id.btnLogout);
        btnAccount = findViewById(R.id.btnAccount);
        btnMessages = findViewById(R.id.btnMessages);
        btnViewMap = findViewById(R.id.btnViewMap);
        btnHome = findViewById(R.id.btnHome);
        meetingList = findViewById(R.id.meetingList);

        btnAccount.setOnClickListener(this);
        btnMessages.setOnClickListener(this);
        btnHome.setOnClickListener(this);
        btnAddMeeting.setOnClickListener(this);

        Intent e = getIntent();
        loggedInUser = e.getIntExtra("uID",0);
        navHelper = e.getIntExtra("navHelper", 0);

        showAllMeetings();

        getFontSettings();

    }

    /**
     * handles buttons on lick events
     * @param view
     */
    @Override
    public void onClick(View view) {
        if(view == findViewById(R.id.btnAddMeeting)) {
            Intent intent = new Intent(this,AddMeetingActivity.class);
            intent.putExtra("uID", loggedInUser);
            startActivity(intent);
            finish();
            showAllMeetings();

        } else if(view == findViewById(R.id.btnMessages)) {
            Intent intent = new Intent(this,MessagesActivity.class);
            intent.putExtra("uID", loggedInUser);
            intent.putExtra("navHelper", 1);
            startActivity(intent);
            finish();

        } else if (view == findViewById(R.id.btnAccount)) {
            Intent intent = new Intent(this,ProfileActivity.class);
            intent.putExtra("uID", loggedInUser);
            intent.putExtra("navHelper", 1);
            startActivity(intent);
            finish();
        } else if(view == findViewById(R.id.btnViewMap)) {
            Intent intent = new Intent(this,ViewMapActivity.class);
            intent.putExtra("uID", loggedInUser);
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(this,MeetingsActivity.class);
            intent.putExtra("uID", loggedInUser);
            intent.putExtra("navHelper", 1);
            startActivity(intent);
            finish();
        }
    }

    /**
     * gets all of the users upcoming meetings then add to list
     */
    public void getAllMeetings(){
        userMeetings.clear();
        MeetingRepo mRepo = new MeetingRepo(this);
        AttendeeRepo aRepo = new AttendeeRepo(this);

        c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH);
        int year = c.get(Calendar.YEAR);

        String cDate = year + "-" + String.format("%02d",month+1) + "-" + String.format("%02d",day);

        Cursor c3 = aRepo.getUserUpcomingMeetings(loggedInUser, cDate);

        while(c3.moveToNext()) {

            Cursor c4 = mRepo.getMeeting(c3.getInt(c3.getColumnIndex(Meeting.KEY_meetingID)));

            while(c4.moveToNext()) {
                HashMap<String, String> attendingMeetings = new HashMap<>();
                attendingMeetings.put("ID",c4.getString(c4.getColumnIndex(Meeting.KEY_meetingID)));
                attendingMeetings.put("title",c4.getString(c4.getColumnIndex(Meeting.KEY_title)));
                attendingMeetings.put("date",c4.getString(c4.getColumnIndex(Meeting.KEY_date)));
                attendingMeetings.put("time",c4.getString(c4.getColumnIndex(Meeting.KEY_time)));
                attendingMeetings.put("creator",c4.getString(c4.getColumnIndex(Meeting.KEY_userID)));
                userMeetings.add(attendingMeetings);

            }
        }
        //c1.close();
        c3.close();
    }

    /**
     * show all of the meetings from list in listview
     */
    public void showAllMeetings(){
        getAllMeetings();

        if(userMeetings.size()!=0) {
            meetingList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    meetingTitle = view.findViewById(R.id.meetingTitle);
                    meetingID = view.findViewById(R.id.meetingID);
                    meetingDate = view.findViewById(R.id.meetingDate);
                    meetingTime = view.findViewById(R.id.meetingTime);

                    SettingRepo sRepo = new SettingRepo(getBaseContext());
                    Setting s = sRepo.getUserSettings(loggedInUser);

                    fontColour = s.fontColour;
                    fontSize = s.fontSize;

                    meetingTitle.setTextSize(fontSize);
                    meetingTitle.setTextColor(Color.parseColor(fontColour));
                    meetingID.setTextSize(fontSize);
                    meetingID.setTextColor(Color.parseColor(fontColour));
                    meetingDate.setTextSize(fontSize);
                    meetingDate.setTextColor(Color.parseColor(fontColour));
                    meetingTime.setTextSize(fontSize);
                    meetingTime.setTextColor(Color.parseColor(fontColour));

                    String meetID = meetingID.getText().toString();

                    Intent objIndent = new Intent(getApplicationContext(),MeetingView.class);
                    objIndent.putExtra("mID", meetID);
                    objIndent.putExtra("uID", loggedInUser);
                    objIndent.putExtra("navHelper", 1);
                    startActivity(objIndent);
                    finish();
                }
            });
            ListAdapter adapter = new SimpleAdapter(MeetingsActivity.this, userMeetings, R.layout.list_item,
                    new String[]{"title", "ID", "date", "time"}, new int[]{R.id.meetingTitle, R.id.meetingID, R.id.meetingDate, R.id.meetingTime});
            meetingList.setAdapter(adapter);
            //adapter.meetingTitle.setTextColor(Color.RED);
        }
    }

    /**
     * get users font settings
     */
    public void getFontSettings(){
        title1 = findViewById(R.id.title1);

        SettingRepo sRepo = new SettingRepo(this);
        Setting s = sRepo.getUserSettings(loggedInUser);

        fontColour = s.fontColour;
        fontSize = s.fontSize;

        title1.setTextSize(fontSize);
        title1.setTextColor(Color.parseColor(fontColour));

    }

    /**
     * handle back press event
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
