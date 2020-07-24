package com.example.languageassistant.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.example.languageassistant.LoginActivity;
import com.example.languageassistant.R;
import com.example.languageassistant.models.ConvoBuddy;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.material.button.MaterialButton;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.app.Activity.RESULT_OK;


/**
 * A fragment for displaying the profile of a user.
 */
public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 50;
    private static final String KEY_NAME = "name";
    private static final String KEY_NATIVE_LANG = "nativeLanguage";
    private static final String KEY_TARGET_LANG = "targetLanguage";
    private static final String KEY_CONVO = "convoBuddy";
    private static final String KEY_PROFILE_PIC = "profilePic";

    private CallbackManager callbackManager;


    public String photoFileName = "photo.jpg";
    private File photoFile;

    ParseUser user;

    MaterialButton btnLogout;
    ImageView ivProfilePic;
    TextView tvName;
    TextView tvUsername;
    ImageView ivCamera;
    TextView tvNativeLang;
    TextView tvTargetLang;
    MaterialButton btnLinkFb;
    TextView tvConvoBuddy;
    ImageView ivTarget;
    LoginButton btnLogin;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @NonNull Bundle savedInstanceState){
        //FB permissions
        callbackManager = CallbackManager.Factory.create();
        btnLogin = (LoginButton) view.findViewById(R.id.btnLogin);
        btnLogin.setReadPermissions(Arrays.asList("user_location"));
        // If you are using in a fragment, call loginButton.setFragment(this);
        btnLogin.setFragment(this);
        btnLogin.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                // update location with API call

                //update FB partner

                ConvoBuddy convo = (ConvoBuddy) user.getParseObject("convo");

                if(convo.getBuddy() == null){
                    //                Link FB account with no partner
//        Same procedure as someone who just made an account.

                }else{

                    //        Link FB account when you already have a partner
//        You recalculate your partner from among those who do not have partners and your prep partner.
//        If the best person is not your prev partner, you go with the new person.
//        Then you rev partner also gets his recalculated among those who do not have partners.
//        “Coming soon” is prob not a possibility bc you would just get re-recommended your prep partner.

                }
            }

            @Override
            public void onCancel() {
                //Toast.makeText(getContext(), "login cancel", Toast.LENGTH_SHORT).show();
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                Toast.makeText(getContext(), "login error", Toast.LENGTH_SHORT).show();
                // App code
            }
        });

        user = ParseUser.getCurrentUser();

        ivProfilePic = view.findViewById(R.id.ivProfilePic);
        tvName = view.findViewById(R.id.tvName);
        tvUsername = view.findViewById(R.id.tvUsername);
        ivCamera = view.findViewById(R.id.ivCamera);
        tvNativeLang = view.findViewById(R.id.tvNativeLang);
        tvTargetLang = view.findViewById(R.id.tvTargetLang);
        tvNativeLang = view.findViewById(R.id.tvNativeLang);
        tvConvoBuddy = view.findViewById(R.id.tvConvoBuddy);
        btnLogout = view.findViewById(R.id.btnLogout);
        ivTarget = view.findViewById(R.id.ivTarget);

        //log out
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseUser.logOut();
                Intent intent = new Intent(view.getContext(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        tvName.setText(user.getString(KEY_NAME));
        tvUsername.setText(getContext().getString(R.string.username) + " " + user.getUsername());
        tvNativeLang.setText(getContext().getString(R.string.native_lang) + " " + user.getString(KEY_NATIVE_LANG));
        tvTargetLang.setText(getContext().getString(R.string.target_lang) + " " + user.getString(KEY_TARGET_LANG));

        if(user.get(KEY_CONVO) == null){
            tvConvoBuddy.setText(getContext().getString(R.string.coming_soon));
        }else{
            //unfinished
        }


        ConvoBuddy curConvo= (ConvoBuddy) user.getParseObject("convo");
        //System.out.println(user.getParseObject("convo"));

        //this will run every time the profile section is open, not just when teh account is first created
        if(curConvo.getBuddy() == null){
            ParseQuery<ParseUser> query = new ParseQuery<ParseUser>(ParseUser.class);
            query.whereEqualTo(KEY_NATIVE_LANG, user.getString(KEY_TARGET_LANG)); //speaks correct language

            query.findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> objects, ParseException e) {
                    //check their convo objects and add users without a partner
                    ArrayList<ConvoBuddy> noPartner = new ArrayList<>();
                    for(ParseUser candidate:objects){
                        ConvoBuddy convo= (ConvoBuddy) candidate.getParseObject("convo");
                        if(convo.getBuddy() == null){
                            noPartner.add(convo);
                        }
                    }

                    if(noPartner.size()==0){
                        tvConvoBuddy.setText(getContext().getString(R.string.coming_soon));
                    }else{
                        if(user.getString("location").length() == 0){
                            // If no FB account: rank the other users by match of target and native lang

                        }else{
                            //If user A has added a FB account: rank the other users by match of target/native lang as well as location, based on a formula.

                        }
                        // Update convo buddy of both A and the other user
                    }

                }
            });

        }else{
            //nothing, keep your partner
        }


//
//
//                User A changes target language:
//        Follow “makes new account” procedure for user A and his new language. For the prev partner of user A, do the “makes new account” procedure for his target lang too.
//
//


        //load default pic
        Glide.with(getContext()).load(user.getParseFile(KEY_PROFILE_PIC).getUrl()).fitCenter().circleCrop().into(ivProfilePic);

        ivCamera.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                launchCamera(); //take a photo
            }
        });

        ivTarget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FragmentManager fm = getActivity().getSupportFragmentManager();
                EditLangFragment editNameDialogFragment = EditLangFragment.newInstance(user.getString(KEY_TARGET_LANG));
                editNameDialogFragment.show(fm, "fragment_edit_name");

            }
        });
    }

    public void launchCamera(){
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference for future access
        photoFile = getPhotoFileUri(photoFileName);

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(getContext(), "com.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    // Returns the File for a photo stored on disk given the fileName
    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        return new File(mediaStorageDir.getPath() + File.separator + fileName);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode != CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE){
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // by this point we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                // RESIZE BITMAP, see section below
                // Load the taken image into a preview
                //ivProfilePic.setImageBitmap(takenImage);
                Glide.with(getContext()).load(photoFile).fitCenter().circleCrop().into(ivProfilePic);
                sendPost(user, photoFile);
            } else { // Result was a failure
                Toast.makeText(getContext(), getContext().getString(R.string.pic_error), Toast.LENGTH_SHORT).show();
            }
        }
    }

    //saves profile picture to Parse user
    private void sendPost(ParseUser user, File photo){
        user.put(KEY_PROFILE_PIC, new ParseFile(photo));
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null){
                    Log.e(TAG, "done: error while saving", e);
                    Toast.makeText(getContext(), getContext().getString(R.string.save_error), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
