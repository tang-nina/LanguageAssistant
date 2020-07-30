package com.example.languageassistant.models;

import com.parse.ParseUser;

public class ConvoPartnerScore {
    private ParseUser user;
    private double score;

    public ConvoPartnerScore(int score, ParseUser user){
        this.user = user;
        this.score = score;
    }

    //gets score of this partner
    public double getScore(){
        return score;
    }

    //gets this partner
    public ParseUser getUser(){
        return user;
    }

}
