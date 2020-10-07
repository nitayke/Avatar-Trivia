package com.avatar_trivia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.avatar_trivia.MainActivity.ref;

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
    private TextView points;
    private ImageButton imageButton;
    private ImageView imageView;
    private Button resume;
    private Button quit;
    CountDownTimer timer = getTimer(10000);

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
                setButtonsListener();
                game();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    void game() {
        timer = getTimer(10000);
        lifes.setText(getString(R.string.lifes, life));
        scoreTxt.setText(getString(R.string.score, score));
        timer.start();
        if (not_used_questions.size() == 1) {
            endGame();
        }
        int index = not_used_questions.get(rand.nextInt(not_used_questions.size() - 1) + 1);
        not_used_questions.remove((Integer) index);
        ref.child("questions").child(String.valueOf(index)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                setQuestion(dataSnapshot);
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
        points = findViewById(R.id.gamePoints);
        imageButton = findViewById(R.id.gameImageButton);
        imageView = findViewById(R.id.gamePauseView);
        resume = findViewById(R.id.gameResume);
        quit = findViewById(R.id.gameQuit);

        resume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView.setVisibility(View.GONE);
                resume.setVisibility(View.GONE);
                quit.setVisibility(View.GONE);

                timer = getTimer(milliseconds[0]);
                timer.start();
            }
        });

        quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
            }
        });
    }

    void setColors(int i)
    {
        buttons[correctAnswer[0]].getBackground().setColorFilter(Color.GREEN, PorterDuff.Mode.MULTIPLY);
        if (i != correctAnswer[0])
        {
            buttons[i].getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_OVER);
            life--;
        }
        else {
            int pointsInt = (int) (Math.pow(((double) milliseconds[0]) / 100, 2) / 50);
            score += pointsInt;
            String pointsTxt = "+" + pointsInt;
            points.setText(pointsTxt);
        }
    }

    void setQuestion(@NonNull DataSnapshot dataSnapshot)
    {
        for (Button i : buttons)
            i.getBackground().clearColorFilter();
        points.setText("");
        for(DataSnapshot ds : dataSnapshot.getChildren()) {
            questionMap.put(ds.getKey(), ds.getValue());
        }
        Collections.shuffle(numbers);
        question.setText(questionMap.get("question").toString());
        for (int i = 0; i < 4; i++)
            buttons[i].setText(questionMap.get(numbers.get(i)).toString());
        correctAnswer[0] = numbers.indexOf("1");
        progressBar.setVisibility(View.GONE);
    }

    void setButtonsListener()
    {
        for (int i = 0; i < 4; i++)
        {
            final int finalI = i;
            buttons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setColors(finalI);
                    timer.cancel();
                    if (life > 0)
                        game();
                    else
                        endGame();
                }
            });
        }
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView imageView = findViewById(R.id.gamePauseView);
                Button resume = findViewById(R.id.gameResume);
                Button quit = findViewById(R.id.gameQuit);

                imageView.setVisibility(View.VISIBLE);
                resume.setVisibility(View.VISIBLE);
                quit.setVisibility(View.VISIBLE);

                timer.cancel();
            }
        });
    }

    CountDownTimer getTimer(long millisecondsLong)
    {
        return new CountDownTimer(millisecondsLong, 200) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeProgressBar.setProgress((int) (timeProgressBar.getMax() - millisUntilFinished/200));
                milliseconds[0] = millisUntilFinished;
            }
            @Override
            public void onFinish() {
                life--;
                if (life > 0)
                    game();
                else
                    endGame();
            }
        };
    }
}