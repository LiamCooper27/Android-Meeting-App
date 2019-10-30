package com.example.liamcooper.meetingapp.Views;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.liamcooper.meetingapp.Controllers.AttendeeRepo;
import com.example.liamcooper.meetingapp.Controllers.MeetingRepo;
import com.example.liamcooper.meetingapp.Controllers.UserRepo;
import com.example.liamcooper.meetingapp.Models.Attendee;
import com.example.liamcooper.meetingapp.Models.Meeting;
import com.example.liamcooper.meetingapp.Models.User;
import com.example.liamcooper.meetingapp.R;

/**
 * this class handles logging in
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    EditText email, password;
    Button btnLogin, btnRegister;

    /**
     * constructor
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        btnLogin.setOnClickListener(this);
        btnRegister.setOnClickListener(this);

    }

    /**
     * handles butto click events
     * @param v
     */
    @Override
    public void onClick(View v) {
        if(v == findViewById(R.id.btnRegister)) {
            Intent intent = new Intent(this,RegistrationActivity.class);
            startActivity(intent);
        } else {
            String credentialOne = email.getText().toString().toLowerCase();
            String credentialTwo = password.getText().toString();

            UserRepo repo = new UserRepo(this);

            User u = repo.login(credentialOne, credentialTwo); // check if user exists

            if(u != null) {
                Intent intent = new Intent(this,MeetingsActivity.class);
                int id = repo.getUserID(u.email);
                intent.putExtra("name", u.firstName);
                intent.putExtra("uID", id);
                intent.putExtra("navHelper",1);
                startActivity(intent);
            } else {
                Toast.makeText(this,"Email or Password are incorrect",Toast.LENGTH_SHORT).show();
            }
        }
    }

}
