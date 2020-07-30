package com.example.languageassistant.models;

import com.parse.ParseUser;

public class GradingScore {
    private ParseUser user;
    private Grading grading;
    private double score;

    public GradingScore(Grading grading, ParseUser user){
        this.user = user;
        this.grading = grading;
        score = grading.getTotalGraded()*0.2 + grading.getLeftToGrade()*0.8;
    }

    //gets calculated score for this grader
    public double getScore(){
        return score;
    }

    public ParseUser getUser(){
        return user;
    }
}
