package com.example.languageassistant.models;

import java.util.Comparator;

//comparator for sorting Grading objects
public class GradingComparator implements Comparator<GradingScore> {
    @Override
    public int compare(GradingScore gradingScore1, GradingScore gradingScore2) {
        //for sorting scores from least to greatest
        if( gradingScore1.getScore() < gradingScore2.getScore()){
            return -1;
        }else if(gradingScore1.getScore() == gradingScore2.getScore()){
            return 0;
        }else{
            return 1;
        }
    }
}
