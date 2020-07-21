package com.example.languageassistant;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.languageassistant.models.Grading;
import com.google.android.material.button.MaterialButton;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

public class NewAccountActivity extends AppCompatActivity {
    private static final String TAG = "NewAccountActivity";
    EditText etName;
    EditText etUsername;
    EditText etPassword;
    EditText etEmail;
    EditText etNativeLang;
    EditText etTargetLang;
    MaterialButton btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_account);

        etName = findViewById(R.id.etName);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etEmail = findViewById(R.id.etEmail);
        etNativeLang  = findViewById(R.id.etNative);
        etTargetLang = findViewById(R.id.etTarget);

        btnSubmit = findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(etName.getText().toString().trim().length() ==0 || etUsername.getText().toString().trim().length()==0 || etPassword.getText().toString().trim().length()==0 ||
                        etNativeLang.getText().toString().trim().length()==0 || etTargetLang.getText().toString().trim().length()==0){ //should I trim these for whitespace too?
                    //||etEmail.getText().toString().trim().length()==0
                    //alert user if missing info
                    AlertDialog.Builder builder = new AlertDialog.Builder(NewAccountActivity.this);
                    builder.setCancelable(true);
                    builder.setMessage("Please fill in all fields.");
                    builder.setPositiveButton(NewAccountActivity.this.getString(R.string.ok),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }else{
                    //if all info is filled out, create new user

                    ParseUser user = new ParseUser();
                    user.setUsername(etUsername.getText().toString());
                    user.setPassword(etPassword.getText().toString());
                    //user.setEmail(etEmail.getText().toString());
                    user.put("nativeLanguage", etNativeLang.getText().toString());
                    user.put("targetLanguage", etTargetLang.getText().toString());
                    user.put("name", etName.getText().toString());

                    user.signUpInBackground(new SignUpCallback() {
                        public void done(ParseException e) {
                            if (e == null) {

                                final Grading grading = new Grading();
                                grading.setUser(ParseUser.getCurrentUser());
                                grading.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if(e==null){
                                            ParseUser curUser = ParseUser.getCurrentUser();
                                            curUser.put("grading", grading);
                                            curUser.saveInBackground(new SaveCallback() {
                                                @Override
                                                public void done(ParseException e) {
                                                    if(e==null){
                                                        goToMainActivity();
                                                    }
                                                }
                                            });

                                        }

                                    }
                                });

                            } else {
                                Toast.makeText(NewAccountActivity.this, "Login unsuccessful.", Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "Issue with login.", e);
                                return;
                            }
                        }
                    });
                }
            }
        });
    }

    //to main activity
    private void goToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}