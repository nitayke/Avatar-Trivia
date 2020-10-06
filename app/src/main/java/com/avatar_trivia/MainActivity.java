package com.avatar_trivia;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class MainActivity extends AppCompatActivity {
    public static String email = null;
    public static String username = null;
    public static DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
    public static final String PREFS_NAME = "MyPrefsFile";
    public static final String PREF_USERNAME = "username";
    public static final String PREF_EMAIL = "email";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button login = findViewById(R.id.mainLogin);
        Button signup = findViewById(R.id.mainSignup);
        Button guest = findViewById(R.id.mainGuest);
        SharedPreferences pref = getSharedPreferences(PREFS_NAME,MODE_PRIVATE);
        String username1 = pref.getString(PREF_USERNAME, null);
        String email1 = pref.getString(PREF_EMAIL, null);

        if (username1 != null && email1 != null) {
            MainActivity.username = username1;
            MainActivity.email = email1;
            finish();
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
        }
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SignupActivity.class));
            }
        });

        guest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
            }
        });
    }
}