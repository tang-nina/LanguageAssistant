package com.example.languageassistant.models;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Email")
public class Email extends ParseObject{
    ParseUser user;
    String email;

    public Email(){}

    public String getEmail(){
        try{
            return fetchIfNeeded().getString("email");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }
    public ParseUser getUser(){
        return getParseUser("user");
    }

    public void putEmail(String putEmail){ put("email", putEmail);}
    public void putUser(ParseUser putUser){ put("user", putUser);}

}
