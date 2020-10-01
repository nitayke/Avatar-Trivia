package com.trivia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
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
    private final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("/users");
    private CheckBox checkBox;
    private ProgressBar progressBar;
    private TextView msg;
    private EditText usernameET;
    private EditText passwordET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        checkBox = findViewById(R.id.loginCheckBox);
        progressBar = findViewById(R.id.loginLoading);
        msg = findViewById(R.id.loginMsg);
        usernameET = findViewById(R.id.loginUsername);
        passwordET = findViewById(R.id.loginPassword);
        mAuth = FirebaseAuth.getInstance();
        SharedPreferences pref = getSharedPreferences(PREFS_NAME,MODE_PRIVATE);
        String username = pref.getString(PREF_USERNAME, null);
        String password = pref.getString(PREF_PASSWORD, null);

        if (username != null && password != null) {
            MainActivity.username = username;
            ref.child(MainActivity.username).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String txt = dataSnapshot.getKey() + ": " + dataSnapshot.getValue();
                    msg.setText(txt);
                    MainActivity.email = dataSnapshot.getValue(String.class);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {}
            });
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
        }
        Button loginBtn = findViewById(R.id.loginBtn);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                if (usernameET.getText().toString().isEmpty() || passwordET.getText().toString().isEmpty()) {
                    msg.setText("נא למלא את כל השדות!");
                    return;
                }
                MainActivity.username = usernameET.getText().toString();
                ref.child(MainActivity.username).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        MainActivity.email = dataSnapshot.getValue(String.class);
                        if (MainActivity.email == null)
                        {
                            msg.setText("שם המשתמש אינו קיים!");
                            return;
                        }
                        signIn(MainActivity.email, passwordET.getText().toString());
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {}
                });
            }
        });
    }

    void signIn(String email, String password)
    {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            if (checkBox.isChecked())
                            {
                                getSharedPreferences(PREFS_NAME,MODE_PRIVATE)
                                        .edit()
                                        .putString(PREF_USERNAME, MainActivity.username)
                                        .putString(PREF_PASSWORD, passwordET.getText().toString())
                                        .apply();
                            }
                            progressBar.setVisibility(View.GONE);
                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                        } else {
                            msg.setText("הכניסה נכשלה!");
                        }
                    }
                });
    }
}