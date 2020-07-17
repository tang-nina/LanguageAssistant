package com.example.languageassistant.models;

import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Grading")
public class Grading extends ParseObject {
    public static final String KEY_USER = "user";
    public static final String KEY_TOTAL_GRADED = "totalGraded";
    public static final String KEY_LEFT_TO_GRADE = "leftToGrade";
    private static final String TAG = "Grading";

    public Grading(){}

    public void addGraded(){
        try {
            int total = Integer.parseInt(fetchIfNeeded().getString(KEY_TOTAL_GRADED)) + 1;
            put(KEY_TOTAL_GRADED, Integer.toString(total));
        } catch (ParseException e) {
            Log.e(TAG, "Something has gone terribly wrong with Parse", e);
        }
    }

    public void subtractLeftToGrade(){
        try {
            int total = Integer.parseInt(fetchIfNeeded().getString(KEY_LEFT_TO_GRADE)) - 1;
            put(KEY_LEFT_TO_GRADE, Integer.toString(total));
        } catch (ParseException e) {
            Log.e(TAG, "Something has gone terribly wrong with Parse", e);
        }
    }

    public void addLeftToGrade(){
        try {
            int total = Integer.parseInt(fetchIfNeeded().getString(KEY_LEFT_TO_GRADE)) + 1;
            put(KEY_LEFT_TO_GRADE, Integer.toString(total));
        } catch (ParseException e) {
            Log.e(TAG, "Something has gone terribly wrong with Parse", e);
        }
    }

    public ParseUser getUser(){
       return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user){
        put(KEY_USER, user);
    }

}
