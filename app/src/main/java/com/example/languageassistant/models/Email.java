package com.example.languageassistant.models;

import com.example.languageassistant.Keys;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Email")
public class Email extends ParseObject{

    public Email(){}

    //gets email
    public String getEmail(){
        try{
            return fetchIfNeeded().getString(Keys.KEY_EMAIL);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    //get user
    public ParseUser getUser(){
        return getParseUser(Keys.KEY_USER);
    }

    //updates email
    public void putEmail(String putEmail){ put(Keys.KEY_EMAIL, putEmail);}

    //updates user
    public void putUser(ParseUser putUser){ put(Keys.KEY_USER, putUser);}

}
