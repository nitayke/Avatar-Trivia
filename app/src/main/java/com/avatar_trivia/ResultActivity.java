package com.avatar_trivia;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import static com.avatar_trivia.MainActivity.ref;

public class ResultActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        final Calendar c = Calendar.getInstance();
        final EditText guestName = findViewById(R.id.resultGuestName);
        Button saveBtn = findViewById(R.id.resultSave);
        TextView scoreTxt = findViewById(R.id.resultScore);
        Button backBtn = findViewById(R.id.resultBack);

        scoreTxt.setText(getString(R.string.score, getIntent().getIntExtra("SCORE", 0)));
        if (MainActivity.username != null) {
            Score score = new Score(MainActivity.username, getIntent().getIntExtra("SCORE", 0),
                    "" + c.get(Calendar.YEAR) + c.get(Calendar.WEEK_OF_YEAR));
            ref.child("scores").push().setValue(score);
        }
        else
        {
            saveBtn.setVisibility(View.VISIBLE);
            guestName.setVisibility(View.VISIBLE);
            saveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (guestName.getText().toString().isEmpty())
                        return;
                    Score score = new Score(guestName.getText().toString(), getIntent().getIntExtra("SCORE", 0),
                            "" + c.get(Calendar.YEAR) + c.get(Calendar.WEEK_OF_YEAR));
                    ref.child("scores").push().setValue(score);
                    finish();
                    Toast.makeText(getApplicationContext(), "השיא נשמר בהצלחה!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                }
            });
        }

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
            }
        });
    }
}