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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.trivia.MainActivity.ref;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    public static final String PREFS_NAME = "MyPrefsFile";
    private static final String PREF_USERNAME = "username";
    private static final String PREF_PASSWORD = "password";
    private CheckBox checkBox;
    private ProgressBar progressBar;
    private TextView msg;
    private EditText usernameET;
    private EditText passwordET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();

        checkBox = findViewById(R.id.loginCheckBox);
        progressBar = findViewById(R.id.loginLoading);
        msg = findViewById(R.id.loginMsg);
        usernameET = findViewById(R.id.loginUsername);
        passwordET = findViewById(R.id.loginPassword);
        Button loginBtn = findViewById(R.id.loginBtn);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (usernameET.getText().toString().isEmpty() || passwordET.getText().toString().isEmpty()) {
                    msg.setText("נא למלא את כל השדות!");
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                MainActivity.username = usernameET.getText().toString().trim();
                ref.child("users").child(MainActivity.username).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        MainActivity.email = dataSnapshot.getValue(String.class);
                        if (MainActivity.email == null)
                        {
                            msg.setText("שם המשתמש אינו קיים!");
                            return;
                        }
                        signIn(MainActivity.email, passwordET.getText().toString().trim());
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
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            if (!task.getResult().getUser().isEmailVerified())
                                msg.setText("המייל שלך אינו מאומת! (חה חה מאומת לא מצחיק)");
                            if (checkBox.isChecked())
                            {
                                getSharedPreferences(PREFS_NAME,MODE_PRIVATE)
                                        .edit()
                                        .putString(PREF_USERNAME, MainActivity.username)
                                        .putString(PREF_PASSWORD, passwordET.getText().toString())
                                        .apply();
                            }
                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                        } else {
                            String errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();
                            switch (errorCode) {
                                case "ERROR_WRONG_PASSWORD":
                                    msg.setText("הסיסמה שגויה!");
                                    break;
                                case "ERROR_WEAK_PASSWORD":
                                    msg.setText("הסיסמה שלך קצרה מידי!");
                                    break;
                                default:
                                    msg.setText(task.getException().toString());
                                    break;
                            }
                        }
                    }
                });
    }
}