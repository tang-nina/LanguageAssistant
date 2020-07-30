package com.example.languageassistant.models;

import android.util.Log;

import com.example.languageassistant.Keys;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Grading")
public class Grading extends ParseObject {
    private static final String TAG = "Grading";

    public Grading(){}

    public void addGraded(){
        try {
            int total = Integer.parseInt(fetchIfNeeded().getString(Keys.KEY_TOTAL_GRADED)) + 1;
            put(Keys.KEY_TOTAL_GRADED, Integer.toString(total));
        } catch (ParseException e) {
            Log.e(TAG, "Something has gone terribly wrong with Parse", e);
        }
    }

    public void subtractLeftToGrade(){
        try {
            int total = Integer.parseInt(fetchIfNeeded().getString(Keys.KEY_LEFT_TO_GRADE)) - 1;
            put(Keys.KEY_LEFT_TO_GRADE, Integer.toString(total));
        } catch (ParseException e) {
            Log.e(TAG, "Something has gone terribly wrong with Parse", e);
        }
    }

    public void addLeftToGrade(){
        try {
            int total = Integer.parseInt(fetchIfNeeded().getString(Keys.KEY_LEFT_TO_GRADE)) + 1;
            put(Keys.KEY_LEFT_TO_GRADE, Integer.toString(total));
        } catch (ParseException e) {
            Log.e(TAG, "Something has gone terribly wrong with Parse", e);
        }
    }

    public ParseUser getUser(){
       return getParseUser(Keys.KEY_USER);
    }

    public void setUser(ParseUser user){
        put(Keys.KEY_USER, user);
    }

    public int getTotalGraded(){
        try {
            return Integer.parseInt(fetchIfNeeded().getString(Keys.KEY_TOTAL_GRADED));
        } catch (ParseException e) {
            Log.e(TAG, "Something has gone terribly wrong with Parse", e);
            return 0;
        }
    }

    public int getLeftToGrade(){
        try {
            return Integer.parseInt(fetchIfNeeded().getString(Keys.KEY_LEFT_TO_GRADE));
        } catch (ParseException e) {
            Log.e(TAG, "Something has gone terribly wrong with Parse", e);
            return 0;
        }
    }
}
