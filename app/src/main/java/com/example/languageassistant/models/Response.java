package com.example.languageassistant.models;

import android.text.format.DateUtils;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@ParseClassName("Response")
public class Response extends ParseObject {
    public static final String KEY_PROMPT = "prompt";
    public static final String KEY_RESPONDER= "respondingUser";
    public static final String KEY_ANSWER_WRITTEN = "answerWritten";
    public static final String KEY_ANSWER_RECORDED = "answerRecorded";
    public static final String KEY_GRADER = "gradingUser";
    public static final String KEY_GRADED = "graded";
    public static final String KEY_GRADE = "grade";
    public static final String KEY_COMMENTS = "additionalComments";

    public Response(){}

    //time response was submitted, not the time of grading.
    public Date getTimestamp(){
        return getCreatedAt();
    }

    public Prompt getPrompt(){
        return (Prompt) getParseObject(KEY_PROMPT);
    }

    public void setPrompt(Prompt prompt){
        put(KEY_PROMPT, prompt);
    }

    public String getWrittenAnswer(){
        return getString(KEY_ANSWER_WRITTEN);
    }

    public void setWrittenAnswer(String answer){
        put(KEY_ANSWER_WRITTEN, answer);
    }

    //recording?

    public boolean getGraded(){
        return getBoolean(KEY_GRADED);
    }

    public void setGraded(boolean graded){
        put(KEY_GRADED, graded);
    }

    public int getGrade(){
        return (int) getNumber(KEY_GRADE);
    }

    public void setGrade(int grade){
        put(KEY_GRADE, grade);
    }

    public String getComments(){
        return getString(KEY_COMMENTS);
    }

    public void setComments(String comments){
        put(KEY_COMMENTS, comments);
    }

    public static String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss zzz yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS, DateUtils.FORMAT_ABBREV_RELATIVE).toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return relativeDate;
    }
}
