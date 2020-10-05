package com.trivia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.trivia.MainActivity.ref;
import static java.lang.Thread.sleep;

public class GameActivity extends AppCompatActivity {
    private int life = 3;
    private int score = 0;
    private ArrayList<Integer> not_used_questions = new ArrayList<>();
    private List<String> numbers = new ArrayList<>();
    private ProgressBar progressBar;
    private TextView scoreTxt;
    private TextView question;
    private TextView lifes;
    private Button[] buttons = new Button[4];
    private Map<String, Object> questionMap = new HashMap<>();
    private Random rand = new Random();
    private int[] correctAnswer = new int[1];
    private long[] milliseconds = new long[1];
    private ProgressBar timeProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        setViews();
        for (int i = 1; i <= 4; i++)
            numbers.add(String.valueOf(i));
        ref.child("questions").child("number").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int size = snapshot.getValue(Integer.class);
                for (int i = 1; i <= size; i++)
                {
                    not_used_questions.add(i);
                }
                // TODO: fix the threading in the game
                game();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    void game() {
        for (Button i : buttons)
            i.getBackground().clearColorFilter();
        lifes.setText(getString(R.string.lifes, life));
        scoreTxt.setText(getString(R.string.score, score));
        final CountDownTimer timer = new CountDownTimer(10000, 200) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeProgressBar.setProgress((int) (timeProgressBar.getMax() - millisUntilFinished/200));
                milliseconds[0] = millisUntilFinished;
            }
            @Override
            public void onFinish() {
                life--;
                game();
            }
        };
        timer.start();
        if (not_used_questions.size() == 1) {
            endGame();
        }
        int index = not_used_questions.get(rand.nextInt(not_used_questions.size() - 1) + 1);
        not_used_questions.remove((Integer) index);
        ref.child("questions").child(String.valueOf(index)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    questionMap.put(ds.getKey(), ds.getValue());
                }
                Collections.shuffle(numbers);
                question.setText(questionMap.get("question").toString());
                for (int i = 0; i < 4; i++)
                    buttons[i].setText(questionMap.get(numbers.get(i)).toString());
                correctAnswer[0] = numbers.indexOf("1");
                progressBar.setVisibility(View.GONE);
                for (int i = 0; i < 4; i++)
                {
                    final int finalI = i;
                    buttons[i].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            buttons[correctAnswer[0]].getBackground().setColorFilter(Color.GREEN, PorterDuff.Mode.MULTIPLY);
                            if (finalI != correctAnswer[0])
                            {
                                buttons[finalI].getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_OVER);
                                life--;
                            }
                            else
                                score += (Math.pow(((float)milliseconds[0])/100, 2)/100);
                            try {
                                sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            timer.cancel();
                            if (life > 0)
                                game();
                            else
                                endGame();
                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    void endGame()
    {
        finish();
        Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
        intent.putExtra("SCORE", score);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    void setViews()
    {
        progressBar = findViewById(R.id.gameProgressBar);
        progressBar.setVisibility(View.VISIBLE);
        scoreTxt = findViewById(R.id.gameScore);
        question = findViewById(R.id.gameQuestion);
        lifes = findViewById(R.id.gameLifes);
        timeProgressBar = findViewById(R.id.gameTimeProgressBar);
        buttons[0] = findViewById(R.id.gameBtn1);
        buttons[1] = findViewById(R.id.gameBtn2);
        buttons[2] = findViewById(R.id.gameBtn3);
        buttons[3] = findViewById(R.id.gameBtn4);
        timeProgressBar.bringToFront();
    }
}