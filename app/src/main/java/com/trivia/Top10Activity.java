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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Top10Activity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top10);
        ListView listView = findViewById(R.id.top10List);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("scores");
        final ArrayList<DataSnapshot>[] scores = new ArrayList[]{new ArrayList<>()};
        ArrayList<Score> scores1 = new ArrayList<>();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                scores[0] = (ArrayList<DataSnapshot>) dataSnapshot.getChildren();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
        for (DataSnapshot i : scores[0]) {
            scores1.add(new Score(i.child("user").getValue(String.class), i.child("score").getValue(String.class)));
        }
        Collections.sort(scores1);
        ArrayAdapter<Score> adapter = new ArrayAdapter<>(this, R.layout.activity_listview, scores1);
        listView.setAdapter(adapter);
    }
}