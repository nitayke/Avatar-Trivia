package com.example.avatar_trivia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    public static final String PREFS_NAME = "MyPrefsFile";
    private static final String PREF_USERNAME = "username";
    private static final String PREF_PASSWORD = "password";
    private final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        SharedPreferences pref = getSharedPreferences(PREFS_NAME,MODE_PRIVATE);
        String username = pref.getString(PREF_USERNAME, null);
        String password = pref.getString(PREF_PASSWORD, null);

        if (username != null && password != null) {
            MainActivity.username = username;
            getEmail(username);
        }
        final EditText usernameET = findViewById(R.id.loginUsername);
        final EditText passwordET = findViewById(R.id.loginPassword);
        final Button login = findViewById(R.id.loginBtn);
        final CheckBox checkBox = findViewById(R.id.loginCheckBox);
        final ProgressBar progressBar = findViewById(R.id.loginLoading);
        final TextView msg = findViewById(R.id.loginMsg);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                if (usernameET.getText().toString().isEmpty() || passwordET.getText().toString().isEmpty()) {
                    msg.setText("נא למלא את כל השדות!");
                    return;
                }
                MainActivity.username = usernameET.getText().toString();
                getEmail(MainActivity.username);
                mAuth.signInWithEmailAndPassword(MainActivity.email, passwordET.getText().toString())
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Log.d("", "signInWithEmail:success");
                                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                                } else {
                                    Log.w("", "signInWithEmail:failure", task.getException());
                                    Toast.makeText(LoginActivity.this, task.getException().getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                if (checkBox.isChecked())
                {
                    getSharedPreferences(PREFS_NAME,MODE_PRIVATE)
                            .edit()
                            .putString(PREF_USERNAME, MainActivity.username)
                            .putString(PREF_PASSWORD, passwordET.getText().toString())
                            .apply();
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void getEmail(String username)
    {
        ref.child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                MainActivity.email = dataSnapshot.getValue(String.class);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}