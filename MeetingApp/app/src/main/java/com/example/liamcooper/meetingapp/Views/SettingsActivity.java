package com.example.liamcooper.meetingapp.Views;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.liamcooper.meetingapp.Controllers.SettingRepo;
import com.example.liamcooper.meetingapp.Models.Setting;
import com.example.liamcooper.meetingapp.R;

/**
 * this class allows the user to pick preffered font sizes and colours
 */
public class SettingsActivity extends AppCompatActivity implements View.OnClickListener{

    //interface objects
    TextView title1, title2;
    RadioGroup sizePickers, colourPickers;
    RadioButton blue, red, black, small, medium, large;
    Button btnSave, btnCancel;

    Boolean isColourPicked = false;
    Boolean isFontPicked = false;
    private int loggedInUser = 0;
    int fontSize = 0;
    String fontColour = "";

    /**
     * constructor
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        sizePickers = findViewById(R.id.sizePickers);
        colourPickers = findViewById(R.id.colourPickers);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);

        blue = findViewById(R.id.blue);
        black = findViewById(R.id.black);
        red = findViewById(R.id.red);
        small = findViewById(R.id.small);
        medium = findViewById(R.id.medium);
        large = findViewById(R.id.large);

        // handles the options when a specific radio button is picked
        sizePickers.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(small.isChecked()) {
                    fontSize = 15;
                    isFontPicked = true;
                    //Toast.makeText(getBaseContext(), "checked small", Toast.LENGTH_SHORT).show();
                } else if(medium.isChecked()) {
                    fontSize = 20;
                    isFontPicked = true;
                } else {
                    fontSize = 25;
                    isFontPicked = true;
                }
            }
        });

        //handles the options when a specific radio button is picked
        colourPickers.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(blue.isChecked()) {
                    fontColour = "#0000FF";
                    isColourPicked = true;
                } else if(black.isChecked()) {
                    fontColour = "#000000";
                    isColourPicked = true;
                } else {
                    fontColour = "#FF0000";
                    isColourPicked = true;
                }
            }
        });

        btnSave.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

        Intent e = getIntent();
        loggedInUser = e.getIntExtra("uID",0);

        getFontSettings();

    }

    /**
     * handles buttons onclick events
     * @param v
     */
    @Override
    public void onClick(View v) {
        if(v == findViewById(R.id.btnSave)) {
            //Toast.makeText(this, "checked small" + fontSize, Toast.LENGTH_SHORT).show();
            Setting s = new Setting();
            SettingRepo sRepo = new SettingRepo(this);

            s.userID = loggedInUser;
            s.fontColour = fontColour;
            s.fontSize = fontSize;

            if(isColourPicked && isFontPicked) {
                sRepo.deleteSetting(loggedInUser);
                sRepo.insertSetting(s);
                Intent intent = new Intent(this,ProfileActivity.class);
                intent.putExtra("uID", loggedInUser);
                intent.putExtra("navHelper", 2);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Please choose a value for colours and size", Toast.LENGTH_SHORT).show();
            }

        } else {
            Intent intent = new Intent(this,ProfileActivity.class);
            intent.putExtra("uID", loggedInUser);
            intent.putExtra("navHelper", 2);
            startActivity(intent);
            finish();
        }
    }

    /**
     * gets users preffered font settings
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
     * handles back button press event
     */
    @Override
    public void onBackPressed() {
        finish();
        Intent intent = new Intent(this,ProfileActivity.class);
        intent.putExtra("uID", loggedInUser);
        intent.putExtra("navHelper", 2);
        startActivity(intent);
    }

}
