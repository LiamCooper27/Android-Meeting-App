package com.example.liamcooper.meetingapp.Views;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
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
import com.example.liamcooper.meetingapp.Controllers.UserRepo;
import com.example.liamcooper.meetingapp.Models.Attendee;
import com.example.liamcooper.meetingapp.Models.Meeting;
import com.example.liamcooper.meetingapp.Models.Setting;
import com.example.liamcooper.meetingapp.Models.User;
import com.example.liamcooper.meetingapp.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * this class handle the view for specific meetings
 */
public class MeetingView extends FragmentActivity implements OnMapReadyCallback {

    //interface objects
    MapView mapView;
    private GoogleMap mMap;
    Button btnBack;
    TextView meetingID, meetingNotes, meetingDate, meetingTime, creatorID, title, title2, title3;
    ListView attendee_list;

    String mID ="";
    private double longitude;
    private double latitude;
    private int loggedInUser;
    private int navHelper = 1;
    private int fontSize = 0;
    private String fontColour = "";

    //list of attendees
    ArrayList<HashMap<String, String>> meetingAttendees = new ArrayList<>();

    /**
     * constructor
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_view);

        getFontSettings();

        attendee_list = findViewById(R.id.attendee_list);
        btnBack = findViewById(R.id.btnBack);

        Intent e = getIntent();
        mID = e.getStringExtra("mID");
        meetingID.setText("Meeting " + mID);
        loggedInUser = e.getIntExtra("uID",0);
        navHelper = e.getIntExtra("navHelper",0);

        //handles back button event click
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v==findViewById(R.id.btnBack)) {
                    if(navHelper == 1) {
                        finish();
                        Intent intent = new Intent(getBaseContext(),MeetingsActivity.class);
                        intent.putExtra("uID", loggedInUser);
                        intent.putExtra("navHelper", 1);
                        startActivity(intent);
                    } else if (navHelper == 2){
                        finish();
                        Intent intent = new Intent(getBaseContext(),ProfileActivity.class);
                        intent.putExtra("uID", loggedInUser);
                        intent.putExtra("navHelper", 2);
                        startActivity(intent);
                    }
                }
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        displayMeeting();
        getAttendees();
        showAllAttendees();
    }

    /**
     * displays meeting
     */
    public void displayMeeting() {
        MeetingRepo mRepo = new MeetingRepo(this);
        Meeting m = new Meeting();

        Cursor c = mRepo.getMeeting(Integer.parseInt(mID));
        while(c.moveToNext()) {
            meetingNotes.setText(c.getString(c.getColumnIndex(Meeting.KEY_notes)));
            meetingDate.setText(c.getString(c.getColumnIndex(Meeting.KEY_date)));
            meetingTime.setText(c.getString(c.getColumnIndex(Meeting.KEY_time)));
            creatorID.setText(c.getString(c.getColumnIndex(Meeting.KEY_userID)));
            longitude = Double.parseDouble(c.getString(c.getColumnIndex(Meeting.KEY_longitude)));
            latitude = Double.parseDouble(c.getString(c.getColumnIndex(Meeting.KEY_latitude)));

            getFontSettings();
        }

    }

    /**
     * gets the meeting attendees and adds to list
     */
    public void getAttendees() {
        AttendeeRepo aRepo = new AttendeeRepo(this);
        UserRepo uRepo = new UserRepo(this);
        Cursor c1 = aRepo.getMeetingAttendees(mID);

        while(c1.moveToNext()) {
            Cursor c2 = uRepo.getSpecificUser(c1.getInt(c1.getColumnIndex(Attendee.KEY_userID)));

            while(c2.moveToNext()) {
                HashMap<String, String> attendee = new HashMap<>();
                attendee.put("fName",c2.getString(c2.getColumnIndex(User.KEY_firstname)));
                attendee.put("lName",c2.getString(c2.getColumnIndex(User.KEY_lastname)));
                attendee.put("email",c2.getString(c2.getColumnIndex(User.KEY_email)));
                meetingAttendees.add(attendee);
            }
        }
    }

    /**
     * show all meeting attendees in listview
     */
    public void showAllAttendees(){

        if(meetingAttendees.size()!=0) {
            attendee_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                }
            });
            ListAdapter adapter = new SimpleAdapter(MeetingView.this, meetingAttendees, R.layout.meeting_attendee_list_item,
                    new String[]{"fName", "lName", "email"}, new int[]{R.id.tvFName, R.id.tvLName, R.id.tvAEmail});
            attendee_list.setAdapter(adapter);
        }
    }

    /**
     * display map with marker of meeting location
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);

        LatLng meetingLocation = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(meetingLocation).title("Meeting Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(meetingLocation));

    }

    /**
     * get users font settings
     */
    public void getFontSettings(){
        meetingID = findViewById(R.id.meetingID);
        meetingNotes = findViewById(R.id.meetingNotes);
        meetingDate = findViewById(R.id.meetingDate);
        meetingTime = findViewById(R.id.meetingTime);
        creatorID = findViewById(R.id.creatorID);
        title = findViewById(R.id.title);
        title2 = findViewById(R.id.title2);
        title3 = findViewById(R.id.title3);

        SettingRepo sRepo = new SettingRepo(this);
        Setting s = sRepo.getUserSettings(loggedInUser);

        fontColour = s.fontColour;
        fontSize = s.fontSize;
        meetingID.setTextSize(fontSize);
        meetingID.setTextColor(Color.parseColor(fontColour));
        meetingNotes.setTextSize(fontSize);
        meetingNotes.setTextColor(Color.parseColor(fontColour));
        meetingDate.setTextSize(fontSize);
        meetingDate.setTextColor(Color.parseColor(fontColour));
        meetingTime.setTextSize(fontSize);
        meetingTime.setTextColor(Color.parseColor(fontColour));
        creatorID.setTextSize(fontSize);
        creatorID.setTextColor(Color.parseColor(fontColour));
        title.setTextSize(fontSize);
        title.setTextColor(Color.parseColor(fontColour));
        title2.setTextSize(fontSize);
        title2.setTextColor(Color.parseColor(fontColour));
        title3.setTextSize(fontSize);
        title3.setTextColor(Color.parseColor(fontColour));

    }

    /**
     * handles back press event
     */
    @Override
    public void onBackPressed() {
        if(navHelper == 1) {
            finish();
            Intent intent = new Intent(this,MeetingsActivity.class);
            intent.putExtra("uID", loggedInUser);
            intent.putExtra("navHelper", 1);
            startActivity(intent);
        } else {
            finish();
            Intent intent = new Intent(this,ProfileActivity.class);
            intent.putExtra("uID", loggedInUser);
            intent.putExtra("navHelper", 2);
            startActivity(intent);
        }
    }
}
