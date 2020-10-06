package com.avatar_trivia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import static com.avatar_trivia.MainActivity.ref;

public class AddQuestionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_question);

        final EditText question = findViewById(R.id.addQuestionQuestion);
        final EditText correct = findViewById(R.id.addQuestionCorrect);
        final EditText false1 = findViewById(R.id.addQuestionFalse1);
        final EditText false2 = findViewById(R.id.addQuestionFalse2);
        final EditText false3 = findViewById(R.id.addQuestionFalse3);
        final TextView msg = findViewById(R.id.addQuestionMsg);
        final int[] number = new int[1];
        final ProgressBar progressBar = findViewById(R.id.addQuestionProgressBar);
        final Map<String, Object> values = new HashMap<>();
        Button addQuestionButton = findViewById(R.id.addQuestionBtn);

        addQuestionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (question.getText().toString().isEmpty() || correct.getText().toString().isEmpty() ||
                        false1.getText().toString().isEmpty() || false2.getText().toString().isEmpty() || false3.getText().toString().isEmpty())
                {
                    msg.setText("נא למלא את כל השדות");
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                ref.child("questions").child("number").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        number[0] = dataSnapshot.getValue(Integer.class);
                        ref.child("questions").child("number").setValue(++number[0]);
                        Map<String, Object> questionMap = new HashMap<>();
                        questionMap.put("1", correct.getText().toString());
                        questionMap.put("2", false1.getText().toString());
                        questionMap.put("3", false2.getText().toString());
                        questionMap.put("4", false3.getText().toString());
                        questionMap.put("question", question.getText().toString());
                        values.put(String.valueOf(number[0]), questionMap);
                        ref.child("questions").updateChildren(values);
                        progressBar.setVisibility(View.GONE);
                        msg.setText("השאלה נוספה בהצלחה!");
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {}
                });

            }
        });
    }
}