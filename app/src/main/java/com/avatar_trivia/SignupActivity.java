package com.avatar_trivia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import static com.avatar_trivia.MainActivity.ref;

public class SignupActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText password;
    private ProgressBar progressBar;
    private EditText email;
    private EditText username;
    Button signupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        password = findViewById(R.id.signupPassword);
        progressBar = findViewById(R.id.signupLoading);
        email = findViewById(R.id.signupEmail);
        username = findViewById(R.id.signupUsername);
        signupButton = findViewById(R.id.signupBtn);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (email.getText().toString().trim().isEmpty() || username.getText().toString().trim().isEmpty() || password.getText().toString().trim().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "נא למלא את כל השדות!", Toast.LENGTH_SHORT).show();
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                MainActivity.username = username.getText().toString().trim();
                MainActivity.email = email.getText().toString().trim();
                ref.child("users").child(MainActivity.username).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.getValue() != null) {
                            Toast.makeText(getApplicationContext(), "שם המשתמש קיים!", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                        else
                            signup();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });

            }
        });
    }

    void signup()
    {
        mAuth.createUserWithEmailAndPassword(MainActivity.email, password.getText().toString().trim())
                .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            task.getResult().getUser().sendEmailVerification();
                            Toast.makeText(getApplicationContext(), "נשלח אליך מייל לאימות החשבון.", Toast.LENGTH_SHORT).show();
                            Map<String, Object> map = new HashMap<>();
                            map.put(MainActivity.username, MainActivity.email);
                            ref.child("users").updateChildren(map);
                            signupButton.setText("פתיחת Gmail");
                            signupButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    finish();
                                    Intent intent = getPackageManager().getLaunchIntentForPackage("com.google.android.gm");
                                    startActivity(intent);
                                }
                            });
                        } else {
                            try {
                                throw task.getException();
                            } catch(FirebaseAuthWeakPasswordException e) {
                                Toast.makeText(getApplicationContext(), "הסיסמה שלך קצרה מידי!", Toast.LENGTH_SHORT).show();
                            } catch(FirebaseAuthInvalidCredentialsException e) {
                                Toast.makeText(getApplicationContext(), "המייל שלך אינו תקין!", Toast.LENGTH_SHORT).show();
                            } catch(FirebaseAuthUserCollisionException e) {
                                Toast.makeText(getApplicationContext(), "המייל שלך בשימוש!", Toast.LENGTH_SHORT).show();
                            } catch(Exception e) {
                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }
}