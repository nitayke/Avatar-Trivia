package com.trivia;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Calendar;

import static com.trivia.MainActivity.ref;

public class ResultActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        Calendar c = Calendar.getInstance();
        TextView scoreTxt = findViewById(R.id.resultScore);
        Button backBtn = findViewById(R.id.resultBack);

        scoreTxt.setText(getString(R.string.score, getIntent().getIntExtra("SCORE", 0)));
        if (MainActivity.username == null)
            return;
        Score score = new Score(MainActivity.username, getIntent().getIntExtra("SCORE", 0),
                ""+c.get(Calendar.YEAR)+c.get(Calendar.WEEK_OF_YEAR));
        ref.child("scores").push().setValue(score);

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