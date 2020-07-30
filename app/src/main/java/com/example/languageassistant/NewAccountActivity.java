package com.example.languageassistant;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.languageassistant.models.ConvoBuddy;
import com.example.languageassistant.models.Email;
import com.example.languageassistant.models.Grading;
import com.example.languageassistant.models.Response;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.material.button.MaterialButton;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

public class NewAccountActivity extends AppCompatActivity {

    EditText etName;
    EditText etUsername;
    EditText etPassword;
    EditText etEmail;
    EditText etNativeLang;
    EditText etTargetLang;
    MaterialButton btnSubmit;

    //FB login + SDK
    private CallbackManager callbackManager;
    LoginButton btnLogin;
    String location = "";
    String birthday = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_account);

        etName = findViewById(R.id.etName);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etEmail = findViewById(R.id.etEmail);
        etNativeLang = findViewById(R.id.etNative);
        etTargetLang = findViewById(R.id.etTarget);

        btnSubmit = findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (etName.getText().toString().trim().length() == 0 || etUsername.getText().toString().trim().length() == 0 || etPassword.getText().toString().trim().length() == 0 ||
                        etNativeLang.getText().toString().trim().length() == 0 || etTargetLang.getText().toString().trim().length() == 0 || etEmail.getText().toString().trim().length() == 0) {

                    //alert user if missing info
                    AlertDialog.Builder builder = new AlertDialog.Builder(NewAccountActivity.this);
                    builder.setCancelable(true);
                    builder.setMessage(getString(R.string.fill_all));
                    builder.setPositiveButton(NewAccountActivity.this.getString(R.string.ok),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();

                } else if (!isEmailValid(etEmail.getText().toString())) {

                    //alert user of wrong email format
                    AlertDialog.Builder builder = new AlertDialog.Builder(NewAccountActivity.this);
                    builder.setCancelable(true);
                    builder.setMessage(getString(R.string.valid_email));
                    builder.setPositiveButton(NewAccountActivity.this.getString(R.string.ok),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();

                } else {
                    ParseQuery<ParseUser> query = ParseQuery.getQuery(ParseUser.class);
                    query.whereEqualTo(Keys.KEY_USERNAME, etUsername.getText().toString());
                    query.findInBackground(new FindCallback<ParseUser>() {
                        public void done(List<ParseUser> objects, ParseException e) {
                            if (e == null) {
                                int size = objects.size();
                                if (size != 0) {
                                    //username already in use alert
                                    AlertDialog.Builder builder = new AlertDialog.Builder(NewAccountActivity.this);
                                    builder.setCancelable(true);
                                    builder.setMessage(getString(R.string.username_repeat));
                                    builder.setPositiveButton(NewAccountActivity.this.getString(R.string.ok),
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                }
                                            });
                                    AlertDialog dialog = builder.create();
                                    dialog.show();
                                } else {
                                    //if all info is filled out, create new user

                                    ParseUser user = new ParseUser();
                                    user.setUsername(etUsername.getText().toString());
                                    user.setPassword(etPassword.getText().toString());

                                    user.put(Keys.KEY_NATIVE_LANG, etNativeLang.getText().toString());
                                    user.put(Keys.KEY_TARGET_LANG, etTargetLang.getText().toString());
                                    user.put(Keys.KEY_NAME, etName.getText().toString());

                                    if (AccessToken.getCurrentAccessToken() != null) {
                                        //only update if the user stays logged in to FB account
                                        user.put(Keys.KEY_LOC, location);
                                        user.put(Keys.KEY_BDAY, birthday);
                                    } else {
                                        user.put(Keys.KEY_LOC, "");
                                        user.put(Keys.KEY_BDAY, "");
                                    }

                                    user.signUpInBackground(new SignUpCallback() {
                                        public void done(ParseException e) {
                                            if (e == null) {
                                                //create matching grading object
                                                final Grading grading = new Grading();
                                                grading.setUser(ParseUser.getCurrentUser());
                                                grading.saveInBackground(new SaveCallback() {
                                                    @Override
                                                    public void done(ParseException e) {
                                                        if (e == null) {

                                                            //create matching convo object
                                                            final ConvoBuddy convo = new ConvoBuddy();
                                                            convo.putUser(ParseUser.getCurrentUser());

                                                            convo.saveInBackground(new SaveCallback() {
                                                                @Override
                                                                public void done(ParseException e) {
                                                                    if (e == null) {

                                                                        //create matching email object
                                                                        final Email email = new Email();
                                                                        email.putUser(ParseUser.getCurrentUser());
                                                                        email.putEmail(etEmail.getText().toString());

                                                                        email.saveInBackground(new SaveCallback() {
                                                                            @Override
                                                                            public void done(ParseException e) {
                                                                                if (e == null) {
                                                                                    ParseUser curUser = ParseUser.getCurrentUser();
                                                                                    curUser.put(Keys.KEY_GRADING, grading);
                                                                                    curUser.put(Keys.KEY_CONVO, convo);
                                                                                    curUser.put(Keys.KEY_EMAIL_OB, email);

                                                                                    curUser.saveInBackground(new SaveCallback() {
                                                                                        @Override
                                                                                        public void done(ParseException e) {
                                                                                            if (e == null) {

                                                                                                //check for any responses whose grader went unassigned
                                                                                                ParseQuery<ParseUser> query = new ParseQuery<ParseUser>(ParseUser.class);
                                                                                                query.whereEqualTo(Keys.KEY_ID, "JSYDmAKach");
                                                                                                query.findInBackground(new FindCallback<ParseUser>() {
                                                                                                    @Override
                                                                                                    public void done(List<ParseUser> objects, ParseException e) {
                                                                                                        if (e == null) {

                                                                                                            //check if default user has any responses attached to it (responses that don't have graders)
                                                                                                            ParseQuery<Response> queryResponse = ParseQuery.getQuery(Response.class);
                                                                                                            queryResponse.whereEqualTo(Keys.KEY_GRADING_USER, objects.get(0));
                                                                                                            queryResponse.findInBackground(new FindCallback<Response>() {
                                                                                                                @Override
                                                                                                                public void done(final List<Response> responses, ParseException e) {
                                                                                                                    if (e == null) {
                                                                                                                        if (responses.size() != 0) { //if there are (responses is the list that DEFAULT user must grade)

                                                                                                                            ParseQuery<ParseUser> queryUser = ParseQuery.getQuery(ParseUser.class);
                                                                                                                            queryUser.whereEqualTo(Keys.KEY_TARGET_LANG, etNativeLang.getText().toString());
                                                                                                                            queryUser.findInBackground(new FindCallback<ParseUser>() {
                                                                                                                                @Override
                                                                                                                                public void done(List<ParseUser> users, ParseException e) { //users is the list of all users whose responses this user can grade
                                                                                                                                    if (e == null) {
                                                                                                                                        if (users.size() != 0) {

                                                                                                                                            for (ParseUser u : users) {
                                                                                                                                                for (Response r : responses) {
                                                                                                                                                    //for each response, check if they are from any of the users this user can grade
                                                                                                                                                    //if so, change the grader from DEFAULT to this user

                                                                                                                                                    String id = "";
                                                                                                                                                    try {
                                                                                                                                                        id = r.getResponder().fetchIfNeeded().getObjectId();
                                                                                                                                                    } catch (ParseException ex) {
                                                                                                                                                        ex.printStackTrace();
                                                                                                                                                    }

                                                                                                                                                    if (id.equals(u.getObjectId())) {

                                                                                                                                                        //get the responses that this new user can grade
                                                                                                                                                        r.setGrader(ParseUser.getCurrentUser());

                                                                                                                                                        r.saveInBackground(new SaveCallback() {
                                                                                                                                                            @Override
                                                                                                                                                            public void done(ParseException e) {
                                                                                                                                                                //update grading user's Grading
                                                                                                                                                                grading.addLeftToGrade();
                                                                                                                                                                grading.saveInBackground();
                                                                                                                                                            }
                                                                                                                                                        });

                                                                                                                                                    }
                                                                                                                                                }
                                                                                                                                            }

                                                                                                                                            //once finished, go to main screen
                                                                                                                                            AccessToken.setCurrentAccessToken(null);
                                                                                                                                            if (LoginManager.getInstance() != null) {
                                                                                                                                                LoginManager.getInstance().logOut();
                                                                                                                                            }
                                                                                                                                            goToMainActivity();

                                                                                                                                        } else {
                                                                                                                                            AccessToken.setCurrentAccessToken(null);
                                                                                                                                            if (LoginManager.getInstance() != null) {
                                                                                                                                                LoginManager.getInstance().logOut();
                                                                                                                                            }

                                                                                                                                            goToMainActivity();
                                                                                                                                        }
                                                                                                                                    }

                                                                                                                                }
                                                                                                                            });

                                                                                                                        } else { //if there aren't, finish and go to main activity
                                                                                                                            AccessToken.setCurrentAccessToken(null);
                                                                                                                            if (LoginManager.getInstance() != null) {
                                                                                                                                LoginManager.getInstance().logOut();
                                                                                                                            }

                                                                                                                            goToMainActivity();
                                                                                                                        }
                                                                                                                    }
                                                                                                                }
                                                                                                            });

                                                                                                        }
                                                                                                    }
                                                                                                });

                                                                                            }
                                                                                        }
                                                                                    });
                                                                                }
                                                                            }
                                                                        });
                                                                    }
                                                                }
                                                            });
                                                        }
                                                    }
                                                });

                                            } else {
                                                System.out.println(e);
                                                Toast.makeText(NewAccountActivity.this, getString(R.string.new_acc_error), Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    });
                }
            }
        });

        //FB permissions
        callbackManager = CallbackManager.Factory.create();
        btnLogin = (LoginButton) findViewById(R.id.btnLogin);
        btnLogin.setReadPermissions(Arrays.asList("user_location", "user_birthday"));
        btnLogin.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                AccessToken accessToken = loginResult.getAccessToken();
                GraphRequest request = GraphRequest.newMeRequest(
                        accessToken,
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                //try to get location and birthday info
                                try {
                                    location = object.getJSONObject("location").getString("name");
                                } catch (JSONException e) {
                                    location = ""; //its ok if they don't have the info on the FB account
                                }

                                try {
                                    birthday = object.getString("birthday");
                                } catch (JSONException e) {
                                    birthday = "";  //its ok if they don't have the info on the FB account
                                }
                            }
                        });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,location,link,birthday");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException exception) {
            }
        });
    }

    //to main activity
    private void goToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    //returning from FB login
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    public boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}