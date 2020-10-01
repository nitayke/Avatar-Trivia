package com.example.avatar_trivia;

public class Score implements Comparable<Score>{
    public String score;
    public String user;

    public Score(String _user, String _score)
    {
        this.user = _user;
        this.score = _score;
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
