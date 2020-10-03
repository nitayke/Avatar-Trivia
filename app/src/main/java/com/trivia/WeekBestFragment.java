package com.trivia;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.trivia.MainActivity.ref;

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
        ListView listView = getView().findViewById(R.id.weekListView);
        final ArrayList<Score> scores = new ArrayList<>();
        Calendar c = Calendar.getInstance();
        // TODO: order by 2 children (current week and top scores)
        ref.child("scores").orderByChild("date").
                equalTo("" + c.get(Calendar.YEAR)+c.get(Calendar.WEEK_OF_YEAR)).
                addListenerForSingleValueEvent(new ValueEventListener() {
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