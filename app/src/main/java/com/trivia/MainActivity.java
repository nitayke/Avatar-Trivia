package com.trivia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MainActivity extends AppCompatActivity {
    public static String email;
    public static String username;
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
    }
}