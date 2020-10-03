package com.trivia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

import static com.trivia.MainActivity.ref;

public class Top10Activity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top10);
        ListView listView = findViewById(R.id.top10List);
        final ArrayList<Score> scores = new ArrayList<>();
        ref.child("scores").orderByChild("score").limitToFirst(10).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot i : snapshot.getChildren())
                {
                    scores.add(new Score(i.child("user").getValue(String.class),
                            i.child("score").getValue(String.class), i.child("date").getValue(String.class)));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
        Collections.sort(scores);
        ArrayAdapter<Score> adapter = new ArrayAdapter<>(this, R.layout.activity_listview, scores);
        listView.setAdapter(adapter);
    }
}