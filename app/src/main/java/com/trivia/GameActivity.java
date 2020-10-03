package com.trivia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.UiAutomation;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.trivia.MainActivity.ref;

public class GameActivity extends AppCompatActivity {
    private int life = 3;
    private int score = 0;
    private ArrayList<Integer> not_used_questions = new ArrayList<>();
    private List<String> numbers = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        for (int i = 1; i <= 4; i++)
        {
            numbers.add(String.valueOf(i));
        }
        ref.child("questions").child("number").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int size = snapshot.getValue(Integer.class);
                for (int i = 1; i <= size; i++)
                {
                    not_used_questions.add(i);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
        while (life > 0)
        {
            game();
        }
        Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
        intent.putExtra("SCORE", score);
        startActivity(intent);
    }

    void game() {
        final boolean[] timeUp = {false};
        TextView scoreTxt = findViewById(R.id.gameScore);
        TextView question = findViewById(R.id.gameQuestion);
        TextView lifes = findViewById(R.id.gameLifes);
        final Button[] buttons = {findViewById(R.id.gameBtn1), findViewById(R.id.gameBtn2), findViewById(R.id.gameBtn3), findViewById(R.id.gameBtn4)};
        ProgressBar progressBar = findViewById(R.id.gameProgressBar);
        final TextView timerTxt = findViewById(R.id.gameTimerTxt);
        final Map<String, Object> questionMap = new HashMap<>();
        Random rand = new Random();
        final int correctAnswer;
        final long[] milliseconds = new long[1];

        for (Button i : buttons)
            i.setBackgroundColor(Color.parseColor("@android:color/holo_blue_light"));
        lifes.setText(getString(R.string.lifes, life));
        scoreTxt.setText(getString(R.string.score, score));
        CountDownTimer timer = new CountDownTimer(10000, 100) {
            @Override
            public void onTick(long millisUntilFinished) {
                timerTxt.setText(new DecimalFormat("##.#").format(millisUntilFinished/1000));
                milliseconds[0] = millisUntilFinished;
            }
            @Override
            public void onFinish() {
                timeUp[0] = true;
            }
        };
        timer.start();
        progressBar.setVisibility(View.VISIBLE);
        if (not_used_questions.isEmpty()) {
            Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
            intent.putExtra("SCORE", score);
            startActivity(intent);
        }
        int index = not_used_questions.get(rand.nextInt(not_used_questions.size() - 1) + 1);
        not_used_questions.remove((Integer) index);
        ref.child("questions").child(String.valueOf(index)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    questionMap.put(ds.getKey(), ds.getValue());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
        Collections.shuffle(numbers);
        question.setText(questionMap.get("question").toString());
        for (int i = 0; i < 4; i++)
            buttons[i].setText(questionMap.get(numbers.get(i)).toString());
        correctAnswer = numbers.indexOf("1");

        for (int i = 0; i < 4; i++)
        {
            final int finalI = i;
            buttons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    buttons[correctAnswer].setBackgroundColor(Color.parseColor("#00FF00"));
                    if (finalI != correctAnswer)
                    {
                        buttons[finalI].setBackgroundColor(Color.parseColor("#FF0000"));
                        life--;
                    }
                    else
                        score += (milliseconds[0]/100);
                }
            });
        }
        if (!timeUp[0])
            timer.cancel();
    }
}