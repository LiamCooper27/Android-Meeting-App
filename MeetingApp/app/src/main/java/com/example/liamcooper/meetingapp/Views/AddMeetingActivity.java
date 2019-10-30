package com.example.liamcooper.meetingapp.Views;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.liamcooper.meetingapp.Controllers.AttendeeRepo;
import com.example.liamcooper.meetingapp.Controllers.ColleagueRepo;
import com.example.liamcooper.meetingapp.Controllers.MeetingRepo;
import com.example.liamcooper.meetingapp.Controllers.MessageRepo;
import com.example.liamcooper.meetingapp.Controllers.SettingRepo;
import com.example.liamcooper.meetingapp.Controllers.UserRepo;
import com.example.liamcooper.meetingapp.Models.Attendee;
import com.example.liamcooper.meetingapp.Models.Colleague;
import com.example.liamcooper.meetingapp.Models.Meeting;
import com.example.liamcooper.meetingapp.Models.Message;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * this class takes care of adding a meeting
 * it extends fragment activity which is what the map requires
 */
public class AddMeetingActivity extends FragmentActivity implements View.OnClickListener, OnMapReadyCallback {

    MapView mapView;
    private GoogleMap mMap;

    //interface objects
    TextView tvDate, tvTime, tvTitle, tvEmail, title1;
    EditText title, notes, date, time, attendeeEmail;
    Button btnCreateMeeting, btnDatePick, btnTimePick, btnAddAttendee, btnCancel;
    CheckBox isAttending;
    DatePickerDialog datePicker;
    TimePickerDialog timePicker;
    ListAdapter adapter;
    ListView coll_list;
    ListView invited_list;
    Calendar c;

    private int _meetingID = 0;
    private int loggedInUser =0;
    private int fontSize = 0;
    private String fontColour = "";
    private double longitude = 3.878634;
    private double latitude = 51.619543;
    private boolean isCollValid = true;
    private boolean isValid = true;

    //lists for the listview
    ArrayList<HashMap<String, String>> userColleagues = new ArrayList<>();
    ArrayList<HashMap<String, String>> invitedColleagues = new ArrayList<>();

    /**
     * constructor
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_meeting);

        coll_list = findViewById(R.id.coll_list);
        invited_list = findViewById(R.id.invited_list);

        btnCreateMeeting = findViewById(R.id.btnCreateMeeting);
        btnDatePick = findViewById(R.id.btnDatePick);
        btnTimePick = findViewById(R.id.btnTimePick);
        btnAddAttendee = findViewById(R.id.btnInviteAttendee);
        btnCancel = findViewById(R.id.btnCancel);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        Intent e = getIntent();
        loggedInUser = e.getIntExtra("uID",0);

        btnDatePick.setOnClickListener(this);
        btnTimePick.setOnClickListener(this);
        btnAddAttendee.setOnClickListener(this);
        btnCreateMeeting.setOnClickListener(this);
       // btnCancel.setOnClickListener(this);

        getAllColleagues();
        showColleagues();
        showInvitedColleagues();

        getFontSettings();

    }

    /**
     * takes care of the button actions
     * @param v
     */
    @Override
    public void onClick(View v) {
        // add the meeting
        if(v == findViewById(R.id.btnCreateMeeting)) {

            MeetingRepo meetingRepo = new MeetingRepo(this);
            AttendeeRepo attendeeRepo = new AttendeeRepo(this);
            MessageRepo messageRepo = new MessageRepo(this);

            Message message = new Message();
            Meeting meeting = new Meeting();
            Attendee attendee = new Attendee();

            //meeitng variables to add to db
            meeting.meetingID = _meetingID;
            meeting.title = tvTitle.getText().toString();
            meeting.notes = notes.getText().toString();
            meeting.date = tvDate.getText().toString();
            meeting.time = tvTime.getText().toString();
            meeting.latitude = latitude;
            meeting.longitude = longitude;
            meeting.userID = loggedInUser;

            isMeetingValid();

            if (_meetingID == 0 && isValid){

                int mID = meetingRepo.insertMeeting(meeting);

                attendee.meetingID = mID;
                attendee.date = meeting.date;
                attendee.time = meeting.time;
                attendee.userID = loggedInUser;


                //loop over list to find invited attendees then send them messages
                for(int i = 0; i < invitedColleagues.size(); i++) {
                    HashMap<String, String> invites = invitedColleagues.get(i);
                    String uID = invites.get("colID");

                    message.userID = Integer.parseInt(uID);
                    message.meetingID = mID;
                    messageRepo.insertMessage(message);

                }

                attendeeRepo.insertAttendee(attendee);//add creator as an attendee

                Toast.makeText(this,"New Meeting Insert" ,Toast.LENGTH_SHORT).show();

                finish();
                Intent intent = new Intent(this,MeetingsActivity.class);
                intent.putExtra("uID", loggedInUser);
                startActivity(intent);
            }
        //find a user to potentially invite
        } else if(v == findViewById(R.id.btnInviteAttendee)) {
            //MessageRepo inviteRepo = new MessageRepo(this);
            UserRepo uRepo = new UserRepo(this);
            ColleagueRepo cRepo = new ColleagueRepo(this);
            Colleague c = new Colleague();

            int colleagueID = uRepo.getUserID(attendeeEmail.getText().toString().toLowerCase());

            c.colleagueID = colleagueID;
            c.userID = loggedInUser;

            checkColleague(colleagueID);

            if(isCollValid) {
                cRepo.insertColleague(c);
                HashMap<String, String> colleagues = new HashMap<>();
                //colleagues.put("userID",String.valueOf(c.userID));
                colleagues.put("colID",String.valueOf(c.colleagueID));
                colleagues.put("email",attendeeEmail.getText().toString());
                userColleagues.add(colleagues);
                showColleagues();
                showInvitedColleagues();
            }

        //cancel go back
        } else if(v == findViewById(R.id.btnCancel)) {
            finish();
            Intent intent = new Intent(this,MeetingsActivity.class);
            intent.putExtra("uID", loggedInUser);
            startActivity(intent);

            //pick date
        } else if(v == findViewById(R.id.btnDatePick)) {
            //Toast.makeText(AddMeetingActivity.this, latitude + " " + longitude, Toast.LENGTH_LONG).show();
            //Toast.makeText(this,"New Meeting Insert" + invites.size(),Toast.LENGTH_SHORT).show();
            c = Calendar.getInstance();
            int day = c.get(Calendar.DAY_OF_MONTH);
            int month = c.get(Calendar.MONTH);
            int year = c.get(Calendar.YEAR);

            datePicker = new DatePickerDialog(AddMeetingActivity.this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int mYear, int mMonth, int mDay) {
                    String d = String.format("%02d",mDay);
                    String m = String.format("%02d",mMonth+1);
                    tvDate.setText(mYear + "-" + m + "-" + d);
                }
            }, year, month, day);
            datePicker.show();

            //pick time
        } else {
            c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int min = c.get(Calendar.MINUTE);

            timePicker = new TimePickerDialog(AddMeetingActivity.this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                    String h = String.format("%02d",hourOfDay);
                    String m = String.format("%02d",minute);
                    tvTime.setText(h + ":" + m);
                }
            }, hour, min, true);
            timePicker.show();
        }
    }

    /**
     * check is colleague is valid when adding
     * @param collID
     */
    public void checkColleague(int collID) {
        ColleagueRepo cRepo = new ColleagueRepo(this);
        UserRepo uRepo = new UserRepo(this);
        uRepo.getSpecificUser(collID);
        if(collID == loggedInUser) {
            isCollValid = false;
            Toast.makeText(this, "Cannot add yourself", Toast.LENGTH_SHORT).show();
        } else if(uRepo.getSpecificUser(collID) == null) {
            isCollValid = false;
            Toast.makeText(this,"User does not exist",Toast.LENGTH_SHORT).show();
        }else {
            isCollValid = true;
        }
    }

    /**
     * check if meeting is okay to add
     */
    public void isMeetingValid() {
        if(tvDate.getText().toString().contains("Select Date")) {
            isValid = false;
            Toast.makeText(this,"Pick a date",Toast.LENGTH_SHORT).show();
        } else if (tvTime.getText().toString().contains("Select Time")) {
            isValid = false;
            Toast.makeText(this,"Pick a time",Toast.LENGTH_SHORT).show();
        } else if (invitedColleagues.size() == 0) {
            isValid = false;
            Toast.makeText(this,"Invite someone to attend",Toast.LENGTH_SHORT).show();
        } else {
            isValid = true;
        }
    }

    /**
     * get all the users colleagues then add to list
     */
    public void getAllColleagues() {
        //userColleagues.clear();

        ColleagueRepo cRepo = new ColleagueRepo(this);
        UserRepo uRepo = new UserRepo(this);

        Cursor c1 = cRepo.getUsersColleagues(loggedInUser);

        if(c1.getCount() == 0) {
            // show message
            Toast.makeText(this,"Nothing here",Toast.LENGTH_SHORT).show();
        }

        while (c1.moveToNext()) {
            HashMap<String, String> colleagues = new HashMap<>();
            //colleagues.put("userID",c1.getString(c1.getColumnIndex(Colleague.KEY_userID)));
            colleagues.put("colID",c1.getString(c1.getColumnIndex(Colleague.KEY_colleagueID)));

            Cursor c2 = uRepo.getSpecificUser(c1.getInt(c1.getColumnIndex(Colleague.KEY_colleagueID)));

            while (c2.moveToNext()) {
                colleagues.put("email",c2.getString(c2.getColumnIndex(User.KEY_email)));

                userColleagues.add(colleagues);
            }
            //userColleagues.add(colleagues);

        }
        c1.close();
    }

    /**
     * show all the users colleagues, each item in list is clickable to add to invited arraylist
     */
    public void showColleagues(){
        //getAllColleagues();
        final UserRepo uRepo = new UserRepo(this);
        final MessageRepo mRepo = new MessageRepo(this);
        final Message m = new Message();

        if(userColleagues.size()!=0) {

            coll_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    String attendeeID = ((TextView)view.findViewById(R.id.tvAttendeeID)).getText().toString();
                    String email = ((TextView)view.findViewById(R.id.tvColEmail)).getText().toString();

                    HashMap<String, String> invites = new HashMap<>();
                    invites.put("colID",attendeeID);
                    invites.put("email",email);

                    userColleagues.remove(invites);
                    invitedColleagues.add(invites);

                    showColleagues();

                    Toast.makeText(getBaseContext(), "checked " + attendeeID, Toast.LENGTH_SHORT).show();

                    showInvitedColleagues();

                }

            });

            adapter = new SimpleAdapter(AddMeetingActivity.this, userColleagues, R.layout.attendee_list_item,
                    new String[]{"colID", "email"}, new int[]{R.id.tvAttendeeID, R.id.tvColEmail});
            coll_list.setAdapter(adapter);
        }

    }

    /**
     * shows which users have been chosen to get invited, display in list view
     */
    public void showInvitedColleagues(){

        final UserRepo uRepo = new UserRepo(this);
        final MessageRepo mRepo = new MessageRepo(this);
        final Message m = new Message();

        if(invitedColleagues.size()!=0) {
            //final ListView colleagueList = getListView();
            invited_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    String attendeeID = ((TextView)view.findViewById(R.id.tvAttendeeID)).getText().toString();
                    String email = ((TextView)view.findViewById(R.id.tvColEmail)).getText().toString();

                    HashMap<String, String> colleague = new HashMap<>();
                    colleague.put("colID",attendeeID);
                    colleague.put("email",email);

                    invitedColleagues.remove(colleague);
                    userColleagues.add(colleague);

                    showInvitedColleagues();

                    Toast.makeText(getBaseContext(), "checked " + attendeeID, Toast.LENGTH_SHORT).show();

                    showColleagues();

                }

            });

            adapter = new SimpleAdapter(AddMeetingActivity.this, invitedColleagues, R.layout.attendee_list_item,
                    new String[]{"colID", "email"}, new int[]{R.id.tvAttendeeID, R.id.tvColEmail});
            invited_list.setAdapter(adapter);
        }

    }

    /**
     * displays the map and adds the markers
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);

        LatLng defaultLocation = new LatLng(latitude, -longitude);
        mMap.addMarker(new MarkerOptions().position(defaultLocation).title("Meeting location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(defaultLocation));

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick (LatLng point){
                mMap.clear();
                MarkerOptions marker = new MarkerOptions().position(point).title("Meeting Location");
                mMap.addMarker(marker);
                //location = Integer.toString(point);
                latitude = point.latitude;
                longitude = point.longitude;

                Toast.makeText(AddMeetingActivity.this, point.latitude + " " + point.longitude, Toast.LENGTH_LONG).show();
            }

        });
    }

    /**
     * changes the font sizes to the users preferred sizes
     */
    public void getFontSettings(){
        tvTitle = findViewById(R.id.tvMeeting);
        title1 = findViewById(R.id.title1);
        notes = findViewById(R.id.notes);
        attendeeEmail = findViewById(R.id.attendeeEmail);
        tvDate = findViewById(R.id.tvDate);
        tvTime = findViewById(R.id.tvTime);

        SettingRepo sRepo = new SettingRepo(this);
        Setting s = sRepo.getUserSettings(loggedInUser);
        fontColour = s.fontColour;
        fontSize = s.fontSize;

        tvTitle.setTextSize(fontSize);
        tvTitle.setTextColor(Color.parseColor(fontColour));
        title1.setTextSize(fontSize);
        title1.setTextColor(Color.parseColor(fontColour));
        notes.setTextSize(fontSize);
        notes.setTextColor(Color.parseColor(fontColour));
        attendeeEmail.setTextSize(fontSize);
        attendeeEmail.setTextColor(Color.parseColor(fontColour));
        tvDate.setTextSize(fontSize);
        tvDate.setTextColor(Color.parseColor(fontColour));
        tvTime.setTextSize(fontSize);
        tvTime.setTextColor(Color.parseColor(fontColour));

    }

    /**
     * handles back press event
     */
    @Override
    public void onBackPressed() {
        finish();
        Intent intent = new Intent(this,MeetingsActivity.class);
        intent.putExtra("uID", loggedInUser);
        intent.putExtra("navHelper", 1);
        startActivity(intent);
    }
}
