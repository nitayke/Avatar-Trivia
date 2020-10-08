package com.avatar_trivia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

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

        final EditText[] inputs = {findViewById(R.id.addQuestionQuestion),
        findViewById(R.id.addQuestionCorrect), findViewById(R.id.addQuestionFalse1),
                        findViewById(R.id.addQuestionFalse2), findViewById(R.id.addQuestionFalse3)};
        final int[] number = new int[1];
        final ProgressBar progressBar = findViewById(R.id.addQuestionProgressBar);
        final Map<String, Object> values = new HashMap<>();
        Button addQuestionButton = findViewById(R.id.addQuestionBtn);

        addQuestionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (EditText i : inputs)
                {
                    if (i.getText().toString().isEmpty()) {
                        Toast.makeText(getApplicationContext(), "נא למלא את כל השדות!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (i.getText().toString().length() > 30) {
                        Toast.makeText(getApplicationContext(), "אורך השדות צריך להיות עד 30 תווים!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                progressBar.setVisibility(View.VISIBLE);
                ref.child("questions").child("number").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        number[0] = dataSnapshot.getValue(Integer.class);
                        ref.child("questions").child("number").setValue(++number[0]);
                        Map<String, Object> questionMap = new HashMap<>();
                        for (int i = 1; i <= 4; i++)
                        {
                            questionMap.put(Integer.toString(i), inputs[i].getText().toString());
                        }
                        questionMap.put("question", inputs[0].getText().toString());
                        values.put(String.valueOf(number[0]), questionMap);
                        ref.child("questions").updateChildren(values);
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), "השאלה נוספה בהצלחה!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {}
                });

            }
        });
    }
}