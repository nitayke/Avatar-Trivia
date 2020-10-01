package com.example.avatar_trivia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.trivia.R;

import org.w3c.dom.Text;

import java.util.HashMap;

public class SignupActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();

        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");

        final EditText email = findViewById(R.id.signupEmail);
        final EditText username = findViewById(R.id.signupUsername);
        final EditText password = findViewById(R.id.signupPassword);
        final ProgressBar progressBar = findViewById(R.id.signupLoading);
        Button signupButton = findViewById(R.id.signupBtn);
        final TextView msg = findViewById(R.id.signupMsg);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (email.getText().toString().isEmpty() || username.getText().toString().isEmpty() || password.getText().toString().isEmpty()) {
                    msg.setText("נא למלא את כל השדות!");
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                MainActivity.username = username.getText().toString();
                MainActivity.email = email.getText().toString();
                mAuth.createUserWithEmailAndPassword(MainActivity.email, password.getText().toString())
                        .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d("", "createUserWithEmail:success");
                                    Toast.makeText(SignupActivity.this, "נשלח אליך מייל לאימות החשבון", Toast.LENGTH_SHORT).show();
                                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            HashMap<String, Object> users = dataSnapshot.getValue(HashMap.class);
                                            users.put(MainActivity.username, MainActivity.email);
                                            ref.setValue(users);
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {}
                                    });
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w("", "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(SignupActivity.this, task.getException().getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}