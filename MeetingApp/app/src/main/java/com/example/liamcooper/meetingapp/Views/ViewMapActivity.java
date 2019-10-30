package com.example.liamcooper.meetingapp.Views;

import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.liamcooper.meetingapp.Controllers.AttendeeRepo;
import com.example.liamcooper.meetingapp.Controllers.MeetingRepo;
import com.example.liamcooper.meetingapp.Models.Meeting;
import com.example.liamcooper.meetingapp.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * this class allows users to look at the locations of all their upcoming meetings
 */
public class ViewMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private int loggedInUser;
    Calendar c;

    //interface object
    Button btnBack;

    private double longitude;
    private double latitude;

    ArrayList<HashMap<String, String>> meetingLocations = new ArrayList<>();

    /**
     * constructor
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Intent e = getIntent();
        loggedInUser = e.getIntExtra("uID",0);

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v==findViewById(R.id.btnBack)) {
                    Intent intent = new Intent(getBaseContext(),MeetingsActivity.class);
                    intent.putExtra("uID", loggedInUser);
                    startActivity(intent);
                    finish();
                }
            }
        });

        getAllMeetings();
    }

    /**
     * get all the meetings to find locations
     */
    public void getAllMeetings(){

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

            if(c4.getCount() == 0) {
                //buffer.append("not attending\n\n");
            }

            while(c4.moveToNext()) {
                HashMap<String, String> location = new HashMap<>();
                location.put("ID",c4.getString(c4.getColumnIndex(Meeting.KEY_meetingID)));
                location.put("long",c4.getString(c4.getColumnIndex(Meeting.KEY_longitude)));
                location.put("lat",c4.getString(c4.getColumnIndex(Meeting.KEY_latitude)));
                meetingLocations.add(location);

            }
        }
        //c1.close();
        c3.close();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        LatLng place;

        //loop over all the meetings and add their locations as a marker on the map
        for(int i =0; i < meetingLocations.size();i++) {
            HashMap<String, String> locations = meetingLocations.get(i);
            String meeting = locations.get("ID");
            longitude = Double.parseDouble(locations.get("long"));
            latitude = Double.parseDouble(locations.get("lat"));

            place = new LatLng(latitude, longitude);
            mMap.addMarker(new MarkerOptions().position(place).title("Location " + meeting));
            //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        }
    }

    /**
     * handle back press event
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
