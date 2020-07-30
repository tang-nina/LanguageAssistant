package com.example.languageassistant.models;

import com.example.languageassistant.Keys;
import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.io.Serializable;

@ParseClassName("Prompt")
public class Prompt extends ParseObject implements Serializable {
    public Prompt(){}

    public String getPrompt(){
        return getString(Keys.KEY_PROMPT_TEXT);
    }
}



