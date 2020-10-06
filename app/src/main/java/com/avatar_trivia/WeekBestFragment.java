package com.avatar_trivia;

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
import java.util.Calendar;
import java.util.Collections;

import static com.avatar_trivia.MainActivity.ref;

public class WeekBestFragment extends Fragment {

    public WeekBestFragment() {
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
        return inflater.inflate(R.layout.fragment_week_best, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final ListView listView = getView().findViewById(R.id.weekListView);
        ArrayList<Score> scores = new ArrayList<>();
        Calendar c = Calendar.getInstance();
        final ArrayList<Score>[] finalScores = new ArrayList[]{scores};
        String week = "" + c.get(Calendar.YEAR) + c.get(Calendar.WEEK_OF_YEAR);
        ref.child("scores").orderByChild("week").equalTo(week).
                addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot i : snapshot.getChildren())
                {
                    finalScores[0].add(new Score(i.child("user").getValue(String.class),
                            i.child("score").getValue(Integer.class),
                            i.child("week").getValue(String.class)));
                }
                Collections.sort(finalScores[0]);
                if (finalScores[0].size() < 10) {
                    ArrayAdapter<Score> adapter = new ArrayAdapter<>(getActivity().getApplicationContext(),
                            R.layout.list_textview, finalScores[0]);
                    listView.setAdapter(adapter);
                }
                else
                {
                    ArrayAdapter<Score> adapter = new ArrayAdapter<>(getActivity().getApplicationContext(),
                            R.layout.list_textview, finalScores[0].subList(0, 9));
                    listView.setAdapter(adapter);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}