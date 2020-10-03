package com.trivia;

public class Score {
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
    public String toString() {
        return user + " - " + score;
    }
}
