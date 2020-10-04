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

        ref.child("scores").push().setValue(new Score(MainActivity.username,
                scoreTxt.getText().toString(), "" + c.get(Calendar.YEAR)+c.get(Calendar.WEEK_OF_YEAR)));

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }
}