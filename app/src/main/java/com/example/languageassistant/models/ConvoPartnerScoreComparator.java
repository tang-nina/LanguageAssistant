package com.example.languageassistant.models;

import java.util.Comparator;

public class ConvoPartnerScoreComparator implements Comparator<ConvoPartnerScore> {

    //sorts greatest to least
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
