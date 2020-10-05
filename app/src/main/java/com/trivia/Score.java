package com.trivia;

public class Score implements Comparable<Score>{
    public int score;
    public String user;
    public String week;

    public Score(String _user, int _score, String _week)
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
        return Integer.compare(o.score, this.score);
    }
}
