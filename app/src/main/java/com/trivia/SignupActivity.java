package com.trivia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

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

import static com.trivia.MainActivity.ref;

public class SignupActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText password;
    private ProgressBar progressBar;
    private EditText email;
    private EditText username;
    private TextView msg;

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
        Button signupButton = findViewById(R.id.signupBtn);
        msg = findViewById(R.id.signupMsg);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (email.getText().toString().trim().isEmpty() || username.getText().toString().trim().isEmpty() || password.getText().toString().trim().isEmpty()) {
                    msg.setText("נא למלא את כל השדות!");
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                MainActivity.username = username.getText().toString().trim();
                MainActivity.email = email.getText().toString().trim();
                ref.child("users").child(MainActivity.username).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.getValue() != null) {
                            msg.setText("שם המשתמש קיים!");
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
                            msg.setText("נשלח אליך מייל לאימות החשבון");
                            Map<String, Object> map = new HashMap<>();
                            map.put(MainActivity.username, MainActivity.email);
                            ref.updateChildren(map);
                        } else {
                            try {
                                throw task.getException();
                            } catch(FirebaseAuthWeakPasswordException e) {
                                msg.setText("הסיסמה קצרה מידי!");
                            } catch(FirebaseAuthInvalidCredentialsException e) {
                                msg.setText("המייל שלך אינו תקין!");
                            } catch(FirebaseAuthUserCollisionException e) {
                                msg.setText("המייל שלך בשימוש!");
                            } catch(Exception e) {
                                msg.setText(e.getMessage());
                            }
                        }
                    }
                });
    }
}