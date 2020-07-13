package com.example.languageassistant.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Prompt")
public class Prompt extends ParseObject{
    public static final String KEY_PROMPT = "text";
    public Prompt(){}

    public String getPrompt(){
        return getString(KEY_PROMPT);
    }

}



