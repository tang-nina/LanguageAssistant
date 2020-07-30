package com.example.languageassistant.models;

import com.example.languageassistant.Keys;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("ConvoBuddy")
public class ConvoBuddy extends ParseObject {

    public ConvoBuddy(){}

    //put user
    public void putUser(ParseUser user){
        put(Keys.KEY_USER, user);
    }

    //put convo buddy
    public void putBuddy(ParseUser user){
        put(Keys.KEY_CONVO_BUDDY, user);
    }

    //remove convo buddy
    public void removeBuddy() {
        try {
            fetchIfNeeded().remove(Keys.KEY_CONVO_BUDDY);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    //get convo buddy
    public ParseUser getBuddy(){
        try {
            return fetchIfNeeded().getParseUser(Keys.KEY_CONVO_BUDDY);
        } catch (ParseException e) {
            return null;
        }
    }
}
