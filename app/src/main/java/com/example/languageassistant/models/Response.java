package com.example.languageassistant.models;

import android.text.format.DateUtils;

import com.example.languageassistant.Keys;
import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@ParseClassName("Response")
public class Response extends ParseObject {

    public Response(){}

    public ParseFile getRecordedAnswer(){
        return getParseFile(Keys.KEY_ANSWER_RECORDED);
    }

    public void setRecordedAnswer(File file){
        put(Keys.KEY_ANSWER_RECORDED, new ParseFile(file));
    }

    public void setResponder(ParseUser user ){
        put(Keys.KEY_RESPONDER, user);
    }

    public ParseUser getResponder(){
        return getParseUser(Keys.KEY_RESPONDER);
    }

    public void setGrader(ParseUser user ){
        put(Keys.KEY_GRADING_USER, user);
    }

    //format: "Mmm DD YYYY"
    public void setDateAnswered(String formattedDate){
        put(Keys.KEY_DATE, formattedDate);
    }

    //time response was submitted, not the time of grading.
    public Date getTimestamp(){
        return getCreatedAt();
    }

    public String getPrompt(){
        return (getString(Keys.KEY_PROMPT));
    }

    public void setPrompt(String prompt){
        put(Keys.KEY_PROMPT, prompt);
    }

    public String getWrittenAnswer(){
        return getString(Keys.KEY_ANSWER_WRITTEN);
    }

    public void setWrittenAnswer(String answer){
        put(Keys.KEY_ANSWER_WRITTEN, answer);
    }

    public boolean getGraded(){
        return getBoolean(Keys.KEY_GRADED);
    }

    public void setGraded(boolean graded){
        put(Keys.KEY_GRADED, graded);
    }

    public Number getGrade(){
        return getNumber(Keys.KEY_GRADE);
    }

    public void setGrade(int grade){
        put(Keys.KEY_GRADE, grade);
    }

    public String getComments(){
        return getString(Keys.KEY_ADD_COMMENTS);
    }

    public void setComments(String comments){
        put(Keys.KEY_ADD_COMMENTS, comments);
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
