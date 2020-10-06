package com.avatar_trivia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.database.ValueEventListener;

import static com.avatar_trivia.MainActivity.PREFS_NAME;
import static com.avatar_trivia.MainActivity.PREF_EMAIL;
import static com.avatar_trivia.MainActivity.PREF_USERNAME;
import static com.avatar_trivia.MainActivity.ref;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private CheckBox checkBox;
    private ProgressBar progressBar;
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
        usernameET = findViewById(R.id.loginUsername);
        passwordET = findViewById(R.id.loginPassword);
        Button loginBtn = findViewById(R.id.loginBtn);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (usernameET.getText().toString().isEmpty() || passwordET.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "נא למלא את כל השדות!", Toast.LENGTH_SHORT).show();
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
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), "שם המשתמש אינו קיים!", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(getApplicationContext(), "המייל שלך אינו מאומת!", Toast.LENGTH_SHORT).show();
                            else {
                                if (checkBox.isChecked())
                                {
                                    getSharedPreferences(PREFS_NAME,MODE_PRIVATE)
                                            .edit()
                                            .putString(PREF_USERNAME, MainActivity.username)
                                            .putString(PREF_EMAIL, MainActivity.email)
                                            .apply();
                                }
                                finish();
                                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                            }
                        } else {
                            String errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();
                            switch (errorCode) {
                                case "ERROR_WRONG_PASSWORD":
                                    Toast.makeText(getApplicationContext(), "הסיסמה שגויה!", Toast.LENGTH_SHORT).show();
                                    break;
                                case "ERROR_WEAK_PASSWORD":
                                    Toast.makeText(getApplicationContext(), "הסיסמה שלך קצרה מידי!", Toast.LENGTH_SHORT).show();
                                    break;
                                default:
                                    Toast.makeText(getApplicationContext(), task.getException().toString(), Toast.LENGTH_SHORT).show();
                                    break;
                            }
                        }
                    }
                });
    }
}