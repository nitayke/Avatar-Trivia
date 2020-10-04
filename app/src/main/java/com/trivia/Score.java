package com.trivia;

public class Score implements Comparable<Score>{
    public String score;
    public String user;
    public String week;

    public Score(String _user, String _score, String _week)
    {
        this.user = _user;
        this.score = _score;
        this.week = _week;
    }

    @Override
    public String toString() {
        return user + " - " + score;
    }

    @Override
    public int compareTo(Score o) {
        return o.score.compareTo(this.score);
    }
}
