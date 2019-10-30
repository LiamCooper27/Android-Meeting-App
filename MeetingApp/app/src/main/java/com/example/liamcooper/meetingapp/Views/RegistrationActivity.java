package com.example.liamcooper.meetingapp.Views;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.liamcooper.meetingapp.Controllers.UserRepo;
import com.example.liamcooper.meetingapp.Models.User;
import com.example.liamcooper.meetingapp.R;

/**
 * this class allows users to register, create account
 */
public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener {

    //interface objects
    EditText fName, lName, email, password, passwordCopy;
    Button btnRegister;

    private int _userID = 0;
    private boolean valid = true;

    /**
     * constructor
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        fName = findViewById(R.id.fName);
        lName = findViewById(R.id.lName);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        passwordCopy = findViewById(R.id.passwordCopy);
        btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(this);

    }

    /**
     * handles buttons on click events
     * @param view
     */
    public void onClick(View view) {
        if(view == findViewById(R.id.btnRegister)) {
            UserRepo repo = new UserRepo(this);

            User user = new User();
            user.userID = _userID;
            user.firstName = fName.getText().toString();
            user.lastName = lName.getText().toString();
            user.email = email.getText().toString().toLowerCase();
            user.password = password.getText().toString();

            isValid();

            if (_userID == 0 && valid){
                repo.insertUser(user);

                Toast.makeText(this,"Account Created",Toast.LENGTH_SHORT).show();

                int id = repo.getUserID(user.email);
                //Bundle extras = new Bundle();
                Intent intent = new Intent(this,MeetingsActivity.class);
                intent.putExtra("name", user.firstName);
                intent.putExtra("uID", id);
                intent.putExtra("navHelper", 1);

                startActivity(intent);
                finish();
            }

        }
    }

    /*
    checks if users filled out form correctly
     */
    public void isValid() {
        boolean valid = false;
        if(fName == null) {
            valid = false;
            Toast.makeText(this,"Fill in first name",Toast.LENGTH_SHORT).show();
        } else if(lName == null) {
            valid = false;
            Toast.makeText(this,"Fill in first name",Toast.LENGTH_SHORT).show();
        } else if(email == null || (!(email.getText().toString().contains("@")) && !(email.getText().toString().contains(".com")))) {
            valid = false;
            Toast.makeText(this,"Type correct email",Toast.LENGTH_SHORT).show();
        } else if(!(password.getText().toString().equals(passwordCopy.getText().toString()))) {
            valid = false;
            Toast.makeText(this,"Passwords do not match",Toast.LENGTH_SHORT).show();
        } else {
            valid = true;
        }
    }

    /**
     * handles back button press event
     */
    @Override
    public void onBackPressed() {
        finish();
    }
}
