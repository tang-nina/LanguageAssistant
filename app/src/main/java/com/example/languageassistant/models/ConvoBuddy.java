package com.example.languageassistant.models;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("ConvoBuddy")
public class ConvoBuddy extends ParseObject {
    public ConvoBuddy(){}

    public void putUser(ParseUser user){
        put("user", user);
    }

    public void putBuddy(ParseUser user){
        put("convoBuddy", user);
    }

    public ParseUser getBuddy(){
        try {
            return fetchIfNeeded().getParseUser("convoBuddy");
        } catch (ParseException e) {
            System.out.println("HEREEEEE");
            e.printStackTrace();
            return null; //????
        }
    }



}
