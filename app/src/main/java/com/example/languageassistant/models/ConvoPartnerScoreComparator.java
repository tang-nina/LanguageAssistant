package com.example.languageassistant.models;

import java.util.Comparator;

//comparator for sorting ConvoPartnerScore objects
//scores from greatest to least
public class ConvoPartnerScoreComparator implements Comparator<ConvoPartnerScore> {
    @Override
    public int compare(ConvoPartnerScore score1, ConvoPartnerScore score2) {
        if(score1.getScore() > score2.getScore()){
            return -1;
        }else if(score1.getScore() == score2.getScore()){
            return 0;
        }else{
            return 1;
        }
    }
}
