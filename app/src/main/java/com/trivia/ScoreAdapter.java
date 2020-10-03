package com.trivia;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ScoreAdapter extends ArrayAdapter<Score> {
    public ScoreAdapter(Context context, ArrayList<Score> scores) {
        super(context, 0, scores);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Score score = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_textview, parent, false);
        }
        // Lookup view for data population
        TextView tvScore = (TextView) convertView.findViewById(R.id.resultScore);
        // Populate the data into the template view using the data object
        String txt = score.user + " - " + score.score;
        tvScore.setText(txt);
        // Return the completed view to render on screen
        return convertView;
    }
}