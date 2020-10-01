package com.example.avatar_trivia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.trivia.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class GameActivity extends AppCompatActivity {
    private int life = 3;
    private int score = 0;
    private DatabaseReference ref = FirebaseDatabase.getInstance().getReference("questions");
    private ArrayList<Integer> used_questions = new ArrayList<>();
    private List<String> numbers = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        for (int i = 1; i <= 4; i++)
        {
            numbers.add(String.valueOf(i));
        }
        while (life > 0)
        {
            try {
                game();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    void game() throws InterruptedException {
        final boolean[] timeUp = {false};
        TextView scoreTxt = findViewById(R.id.gameScore);
        TextView question = findViewById(R.id.gameQuestion);
        TextView lifes = findViewById(R.id.gameLifes);
        final Button[] buttons = {findViewById(R.id.gameBtn1), findViewById(R.id.gameBtn2), findViewById(R.id.gameBtn3), findViewById(R.id.gameBtn4)};
        ProgressBar progressBar = findViewById(R.id.gameProgressBar);
        final TextView timerTxt = findViewById(R.id.gameTimerTxt);
        final Integer[] questionsNumber = new Integer[1];
        final Map<String, Object> questionMap = new HashMap<>();
        Random rand = new Random();
        final int correctAnswer;
        final long[] milliseconds = new long[1];

        for (Button i : buttons)
            i.setBackgroundColor(Color.parseColor("@android:color/holo_blue_light"));
        lifes.setText("יש לך עוד " + life + " חיים");
        scoreTxt.setText("הניקוד שלך הוא " + score);
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
        ref.child("number").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                questionsNumber[0] = dataSnapshot.getValue(Integer.class);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
        int index = rand.nextInt(questionsNumber[0] - 1) + 1;
        while (used_questions.contains(index))
            index = rand.nextInt(questionsNumber[0] - 1) + 1;
        if (used_questions.contains(index))
            // end game
        used_questions.add(index);
        ref.child(String.valueOf(index)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
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
                    {
                        score += ((1/milliseconds[0]) * 1000000);
                    }
                }
            });
        }
        if (!timeUp[0])
            timer.cancel();
    }
}