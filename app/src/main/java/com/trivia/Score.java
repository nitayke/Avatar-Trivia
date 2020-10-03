package com.trivia;

public class Score implements Comparable<Score>{
    public String score;
    public String user;
    public String date;

    public Score(String _user, String _score, String _date)
    {
        this.user = _user;
        this.score = _score;
        this.date = _date;
    }
    @Override
    public int compareTo(Score other)
    {
        return score.compareTo(other.score);
    }

    @Override
    public String toString() {
        return user + " - " + score;
    }
}
