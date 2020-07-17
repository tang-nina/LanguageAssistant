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

import com.bumptech.glide.Glide;
import com.example.languageassistant.LoginActivity;
import com.example.languageassistant.R;
import com.google.android.material.button.MaterialButton;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;

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
        user = ParseUser.getCurrentUser();

        ivProfilePic = view.findViewById(R.id.ivProfilePic);
        tvName = view.findViewById(R.id.tvName);
        tvUsername = view.findViewById(R.id.tvUsername);
        ivCamera = view.findViewById(R.id.ivCamera);
        tvNativeLang = view.findViewById(R.id.tvNativeLang);
        tvTargetLang = view.findViewById(R.id.tvTargetLang);
        tvNativeLang = view.findViewById(R.id.tvNativeLang);
        btnLinkFb = view.findViewById(R.id.btnLinkFb);
        tvConvoBuddy = view.findViewById(R.id.tvConvoBuddy);
        btnLogout = view.findViewById(R.id.btnLogout);

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

        btnLinkFb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //unfinished
            }
        });

        //load default pic
        Glide.with(getContext()).load(user.getParseFile(KEY_PROFILE_PIC).getUrl()).fitCenter().circleCrop().into(ivProfilePic);

        ivCamera.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                launchCamera(); //take a photo
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
