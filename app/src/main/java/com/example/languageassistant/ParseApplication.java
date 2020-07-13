package com.example.languageassistant;

import android.app.Application;

import com.example.languageassistant.models.Prompt;
import com.parse.Parse;
import com.parse.ParseObject;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class ParseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Use for monitoring Parse network traffic
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        // Can be Level.BASIC, Level.HEADERS, or Level.BODY
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.networkInterceptors().add(httpLoggingInterceptor);

        // set applicationId and server based on the values in the Heroku settings.
        // any network interceptors must be added with the Configuration Builder given this syntax

        ParseObject.registerSubclass(Prompt.class);
        //ParseObject.registerSubclass(Comment.class);


        // set applicationId, and server server based on the values in the Heroku settings.
        // clientKey is not needed unless explicitly configured
        // any network interceptors must be added with the Configuration Builder given this syntax
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("nina-language-assistant") // should correspond to APP_ID env variable
                .clientKey("nina-language-assistant")
                .clientBuilder(builder)// set explicitly unless clientKey is explicitly configured on Parse server
                .server("https://nina-language-assistant.herokuapp.com/parse/").build());
    }
}
