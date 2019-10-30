package com.example.liamcooper.meetingapp.Views;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.provider.ContactsContract;
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
import com.example.liamcooper.meetingapp.Controllers.MessageRepo;
import com.example.liamcooper.meetingapp.Controllers.SettingRepo;
import com.example.liamcooper.meetingapp.Controllers.UserRepo;
import com.example.liamcooper.meetingapp.Models.Attendee;
import com.example.liamcooper.meetingapp.Models.Meeting;
import com.example.liamcooper.meetingapp.Models.Message;
import com.example.liamcooper.meetingapp.Models.Setting;
import com.example.liamcooper.meetingapp.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * this class handles the messages for meetings so user can accept or decline
 */
public class MessagesActivity extends AppCompatActivity implements View.OnClickListener {

    // interface objects
    TextView tvMessageID, tvMeetingID, tvDate, tvTime, title1, title2;
    ListView invitationList;
    ListView acceptedList;
    Button btnReply, btnHome, btnAccount;
    ListAdapter adapter1;
    ListAdapter adapter2;

    private int fontSize = 0;
    private String fontColour = "";
    private int loggedInUser;
    final ArrayList<HashMap<String, String>> userPendingInvitations = new ArrayList<>();
    final ArrayList<HashMap<String, String>> userAcceptedInvitations = new ArrayList<>();
    private int navHelper = 0;

    /**
     * constructor
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        btnReply = findViewById(R.id.btnReply);
        btnHome = findViewById(R.id.btnHome);
        btnAccount = findViewById(R.id.btnAccount);
        invitationList = findViewById(R.id.invitationList);
        acceptedList = findViewById(R.id.acceptedList);

        btnReply.setOnClickListener(this);
        btnHome.setOnClickListener(this);
        btnAccount.setOnClickListener(this);

        Intent e = getIntent();
        loggedInUser = e.getIntExtra("uID",0);
        navHelper = e.getIntExtra("navHelper",0);

        getInvitations();
        showInvites();
        showAcceptedInvites();

        getFontSettings();

    }

    /**
     * handles button on click events
     * @param view
     */
    @Override
    public void onClick(View view) {
        // when pressed delete pending and add accepted as attendee
        if (view == findViewById(R.id.btnReply)) {
            AttendeeRepo attendeeRepo = new AttendeeRepo(this);
            MessageRepo messageRepo = new MessageRepo(this);

            Attendee attendee = new Attendee();
            Message message = new Message();

            //loop over all of users accepted invites then add them as attendee
            for(int i = 0; i < userAcceptedInvitations.size(); i++) {
                HashMap<String, String> details = userAcceptedInvitations.get(i);
                int meetingID = Integer.parseInt(details.get("meetingID"));
                int messageID = Integer.parseInt(details.get("messageID"));
                String date = details.get("date");
                String time = details.get("time");

                attendee.userID = loggedInUser;
                attendee.meetingID = meetingID;
                attendee.date = date;
                attendee.time = time;

                attendeeRepo.insertAttendee(attendee);
                messageRepo.deleteMessage(messageID);

            }

            // delete all of the pending invitations
            for(int i = 0; i < userPendingInvitations.size(); i++) {
                HashMap<String, String> details = userPendingInvitations.get(i);
                int messageID = Integer.parseInt(details.get("messageID"));
                messageRepo.deleteMessage(messageID);
            }
            Intent intent = new Intent(this,MessagesActivity.class);
            intent.putExtra("uID", loggedInUser);
            intent.putExtra("navHelper", 3);
            startActivity(intent);
            finish();
        } else if(view == findViewById(R.id.btnHome)) {
            Intent intent = new Intent(this,MeetingsActivity.class);
            intent.putExtra("uID", loggedInUser);
            intent.putExtra("navHelper", 3);
            startActivity(intent);
            finish();
        } else if(view == findViewById(R.id.btnAccount)) {
            Intent intent = new Intent(this,ProfileActivity.class);
            intent.putExtra("uID", loggedInUser);
            intent.putExtra("navHelper", 3);
            startActivity(intent);
            finish();
        }
    }

    /**
     * get all the inviations for the user
     */
    public void getInvitations() {
        MessageRepo messageRepo = new MessageRepo(this);
        MeetingRepo meetingRepo = new MeetingRepo(this);

        Cursor c1 = messageRepo.getUserMessages(loggedInUser);

        if(c1.getCount() == 0) {
            // show message
            Toast.makeText(this,"User not attending anybodys meeting",Toast.LENGTH_SHORT).show();
        }
        while(c1.moveToNext()) {

            //Cursor c4 = mRepo.getMeeting(Integer.parseInt(c3.getString(0)));
            HashMap<String, String> pendingInvites = new HashMap<>();
            pendingInvites.put("messageID",c1.getString(c1.getColumnIndex(Message.KEY_messageID)));
            pendingInvites.put("meetingID",c1.getString(c1.getColumnIndex(Message.KEY_meetingID)));
            //userPendingInvitations.add(pendingInvites);
            Cursor c2 = meetingRepo.getMeeting(c1.getInt(c1.getColumnIndex(Message.KEY_meetingID)));

            while(c2.moveToNext()){
                pendingInvites.put("date",c2.getString(c2.getColumnIndex(Meeting.KEY_date)));
                pendingInvites.put("time",c2.getString(c2.getColumnIndex(Meeting.KEY_time)));
                userPendingInvitations.add(pendingInvites);
            }
        }
        c1.close();
    }

    /**
     * show all the invites in list view
     * when pressed item is removed from listview
     */
    public void showInvites(){

        final UserRepo uRepo = new UserRepo(this);
        final MessageRepo mRepo = new MessageRepo(this);
        final Message m = new Message();

        if(userPendingInvitations.size()!=0) {
            //final ListView colleagueList = getListView();
            invitationList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    String messageID = ((TextView)view.findViewById(R.id.tvMessageID)).getText().toString();
                    String meetingID = ((TextView)view.findViewById(R.id.tvMeetingID)).getText().toString();
                    String date = ((TextView)view.findViewById(R.id.tvDate)).getText().toString();
                    String time = ((TextView)view.findViewById(R.id.tvTime)).getText().toString();

                    //getFontSettings();

                    HashMap<String, String> pendingInvites = new HashMap<>();
                    pendingInvites.put("messageID",messageID);
                    pendingInvites.put("meetingID",meetingID);
                    pendingInvites.put("date", date);
                    pendingInvites.put("time", time);

                    userPendingInvitations.remove(pendingInvites);
                    userAcceptedInvitations.add(pendingInvites);
                    //invitationList.getItemAtPosition(position);
                    view.setVisibility(View.GONE);

                    //adapter1.notifyDataSetChanged();
                    showInvites();

                    Toast.makeText(getBaseContext(), "Added " + meetingID + " to accepted list", Toast.LENGTH_SHORT).show();

                    showAcceptedInvites();

                }

            });

            adapter1 = new SimpleAdapter(MessagesActivity.this, userPendingInvitations, R.layout.message_list_item,
                    new String[]{"messageID", "meetingID", "date", "time"}, new int[]{R.id.tvMessageID, R.id.tvMeetingID, R.id.tvDate, R.id.tvTime});
            invitationList.setAdapter(adapter1);
            //adapter1.notifyDataSetChanged();
        }

    }

    /**
     * show all accepted invites in listview
     * when pressed item is removed
     */
    public void showAcceptedInvites(){

        final UserRepo uRepo = new UserRepo(this);
        final MessageRepo mRepo = new MessageRepo(this);
        final Message m = new Message();

        if(userAcceptedInvitations.size()!=0) {

            acceptedList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    String messageID = ((TextView)view.findViewById(R.id.tvMessageID)).getText().toString();
                    String meetingID = ((TextView)view.findViewById(R.id.tvMeetingID)).getText().toString();
                    String date = ((TextView)view.findViewById(R.id.tvDate)).getText().toString();
                    String time = ((TextView)view.findViewById(R.id.tvTime)).getText().toString();

                    HashMap<String, String> acceptedInvites = new HashMap<>();

                    acceptedInvites.put("messageID",messageID);
                    acceptedInvites.put("meetingID",meetingID);
                    acceptedInvites.put("date", date);
                    acceptedInvites.put("time", time);

                    userAcceptedInvitations.remove(acceptedInvites);
                    userPendingInvitations.add(acceptedInvites);
                    view.setVisibility(View.GONE);

                   // adapter1.notifyDataSetChanged();
                    showAcceptedInvites();

                    Toast.makeText(getBaseContext(), "Removed " + meetingID + " from accepted list" , Toast.LENGTH_SHORT).show();

                    showInvites();
                    //adapter1.notifyDataSetChanged();

                }

            });

            adapter1 = new SimpleAdapter(MessagesActivity.this, userAcceptedInvitations, R.layout.message_list_item,
                    new String[]{"messageID", "meetingID", "date", "time"}, new int[]{R.id.tvMessageID, R.id.tvMeetingID, R.id.tvDate, R.id.tvTime});
            acceptedList.setAdapter(adapter1);
            //adapter1.notifyDataSetChanged();
        }

    }

    /**
     * get users font settings
     */
    public void getFontSettings(){

        title1 = findViewById(R.id.title1);
        title2 = findViewById(R.id.title2);

        SettingRepo sRepo = new SettingRepo(this);
        Setting s = sRepo.getUserSettings(loggedInUser);
        fontColour = s.fontColour;
        fontSize = s.fontSize;

        title1.setTextSize(fontSize);
        title1.setTextColor(Color.parseColor(fontColour));
        title2.setTextSize(fontSize);
        title2.setTextColor(Color.parseColor(fontColour));
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
