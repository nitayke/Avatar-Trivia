package com.trivia;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

import static com.trivia.MainActivity.ref;


public class AllTimeFragment extends Fragment {

    public AllTimeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_all_time, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ListView listView = getView().findViewById(R.id.allTimeListView);
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
        ArrayAdapter<Score> adapter = new ScoreAdapter(getActivity().getApplicationContext(), scores);
        listView.setAdapter(adapter);
    }
}