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
import com.example.languageassistant.Keys;
import com.example.languageassistant.LoginActivity;
import com.example.languageassistant.R;
import com.example.languageassistant.models.ConvoBuddy;
import com.example.languageassistant.models.ConvoPartnerScore;
import com.example.languageassistant.models.ConvoPartnerScoreComparator;
import com.example.languageassistant.models.Email;
import com.google.android.material.button.MaterialButton;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * A fragment for displaying the profile of a user.
 */
public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 50;

    public String photoFileName = "photo.jpg";
    private File photoFile;

    ParseUser user; //current user

    MaterialButton btnLogout;
    ImageView ivProfilePic;
    TextView tvName;
    TextView tvUsername;
    ImageView ivCamera;
    TextView tvNativeLang;
    TextView tvTargetLang;
    TextView tvConvoBuddy;
    ImageView ivTarget;

    //FB sdk info
    TextView tvBday;
    TextView tvLoc;
    ImageView ivLoc;

    //flag for if this fragment is being reloaded from a previous fragmen
    boolean reloaded = false;

    public ProfileFragment() {
        // Required empty public constructor
    }

    //factory method for this fragment
    public static ProfileFragment newInstance(boolean prev) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putSerializable("FLAG", prev);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            reloaded = getArguments().getBoolean("FLAG");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @NonNull Bundle savedInstanceState) {
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

        tvBday = view.findViewById(R.id.tvBday);
        tvLoc = view.findViewById(R.id.tvLoc);
        ivLoc = view.findViewById(R.id.ivLoc);

        //log out
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseUser.logOut();

                //back to login page
                Intent intent = new Intent(view.getContext(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        tvName.setText(user.getString(Keys.KEY_NAME));
        tvUsername.setText(getContext().getString(R.string.username) + " " + user.getUsername());
        tvNativeLang.setText(getContext().getString(R.string.native_lang) + " " + user.getString(Keys.KEY_NATIVE_LANG));
        tvTargetLang.setText(getContext().getString(R.string.target_lang) + " " + user.getString(Keys.KEY_TARGET_LANG));

        String birthday = user.getString(Keys.KEY_BDAY);
        if (birthday == null || birthday.length() == 0) {
            tvBday.setText(getContext().getString(R.string.bday_na));
        } else {
            tvBday.setText(getContext().getString(R.string.bday) + " " + birthday);
        }

        String location = user.getString(Keys.KEY_LOC);
        if (location == null || location.length() == 0) {
            location = "";
            tvLoc.setText(getContext().getString(R.string.loc_na));
        } else {
            tvLoc.setText(getContext().getString(R.string.loc) + " " + location);
        }

        //convo object of current user
        ConvoBuddy curConvo = (ConvoBuddy) user.getParseObject(Keys.KEY_CONVO);

        if (reloaded == true) { //this is being loaded after updating a language
            ParseUser prevPartner = curConvo.getBuddy();
            String prevId = ""; //id of previous partner
            if (prevPartner != null) {
                prevId = prevPartner.getObjectId();
            }

            convoBuddyNoPartner(user, true); //assign a new buddy using new language

            //if they had a previous buddy in the old language that was not themselves, we must reassign the new buddy too
            if (!(prevPartner == null) && !(prevId.equals(user.getObjectId()))) {
                convoBuddyNoPartner(prevPartner, false); //assign prev buddy a new partner
            }
        } else { //this is being normally loaded
            if (curConvo.getBuddy() == null) { //if no partner
                convoBuddyNoPartner(user, true); //try to assign one
            } else { //keep your partner
                try {
                    Email email = (Email) curConvo.getBuddy().fetchIfNeeded().getParseObject("emailObject"); //email of partner
                    //set name and email of partner on screen
                    tvConvoBuddy.setText(curConvo.getBuddy().fetchIfNeeded().getString(Keys.KEY_NAME) + " (" + email.getEmail() + ")");
                } catch (ParseException e1) {
                    e1.printStackTrace();
                }
            }
        }

        //load profile pic
        Glide.with(getContext()).load(user.getParseFile(Keys.KEY_PROFILE_PIC).getUrl()).fitCenter().circleCrop().into(ivProfilePic);

        ivCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchCamera(); //take a photo
            }
        });

        //change target language
        ivTarget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                EditLangFragment editNameDialogFragment = EditLangFragment.newInstance(user.getString(Keys.KEY_TARGET_LANG));
                editNameDialogFragment.show(fm, "fragment_edit_lang");
            }
        });

        //change current location
        final String finalLocation = location;
        ivLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getActivity().getSupportFragmentManager();

                EditLocFragment editLocFragment = null;
                if (finalLocation.equals("")) {
                    editLocFragment = EditLocFragment.newInstance("", "");
                } else {
                    //format: city, region
                    int commaIdx = finalLocation.indexOf(',');
                    String city = finalLocation.substring(0, commaIdx);
                    String region = finalLocation.substring(commaIdx + 2);
                    editLocFragment = EditLocFragment.newInstance(city, region);
                }
                editLocFragment.show(fm, "fragment_edit_loc");
            }
        });

    }

    // for matching convo buddies if there was no previous partner of concern
    // userToPartner is the user we want to partner up with someone; flag is true if this user is the current user
    // Note: if the user has the same native lang and target lang, they could get assigned to themselves
    private void convoBuddyNoPartner(final ParseUser userToPartner, final boolean isCurUser) {

        ParseQuery<ParseUser> query = new ParseQuery<ParseUser>(ParseUser.class);
        query.addAscendingOrder(Keys.KEY_CREATED); //oldest user at top
        query.whereEqualTo(Keys.KEY_NATIVE_LANG, userToPartner.getString(Keys.KEY_TARGET_LANG)); //speaks correct language
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {

                    ArrayList<ParseUser> candidates = new ArrayList<>();
                    for (ParseUser candidate : objects) {
                        //if the languages match both ways and they don't already have a partner
                        //add to candidates list
                        if (candidate.getString(Keys.KEY_TARGET_LANG).equals(userToPartner.getString(Keys.KEY_NATIVE_LANG))) {
                            ConvoBuddy convo = (ConvoBuddy) candidate.getParseObject(Keys.KEY_CONVO);
                            if (convo.getBuddy() == null) {
                                candidates.add(candidate);
                            }
                        }
                    }

                    if (candidates.size() == 0) { //if no available ppl who speak the correct lang + are unpartnered
                        ConvoBuddy convo = (ConvoBuddy) userToPartner.getParseObject(Keys.KEY_CONVO);
                        convo.removeBuddy();

                        convo.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    if (isCurUser) {
                                        tvConvoBuddy.setText(getContext().getString(R.string.coming_soon));
                                    }
                                } else {
                                    Log.e(TAG, "error", e);
                                }
                            }
                        });

                    } else { //if people available to be partners
                        String location = userToPartner.getString(Keys.KEY_LOC);
                        String birthday = userToPartner.getString(Keys.KEY_BDAY);

                        ParseUser partner = null;

                        if ((location == null || location.length() == 0) && (birthday == null || birthday.length() == 0)) { //no FB account info at all
                            //take the oldest partner that matches both ways in lang needs
                            partner = candidates.get(0);

                        } else if ((location == null || location.length() == 0) && !(birthday == null || birthday.length() == 0)) { //if we only have bday info
                            ArrayList<ConvoPartnerScore> scores = new ArrayList<ConvoPartnerScore>();

                            for (ParseUser candidate : candidates) {
                                int score = 0;

                                int userYr = Integer.parseInt(birthday.substring(6));
                                String candidateBday = candidate.getString(Keys.KEY_BDAY);

                                if (candidateBday != null && candidateBday.length() != 0) {
                                    int candidateYr = Integer.parseInt(candidateBday.substring(6));

                                    if (Math.abs(candidateYr - userYr) <= 5) {
                                        score++;
                                    }
                                }
                                scores.add(new ConvoPartnerScore(score, candidate));
                            }

                            Collections.sort(scores, new ConvoPartnerScoreComparator());
                            partner = scores.get(0).getUser(); //take highest scoring user

                        } else if (!(location == null || location.length() == 0) && (birthday == null || birthday.length() == 0)) { //only location info
                            ArrayList<ConvoPartnerScore> scores = new ArrayList<ConvoPartnerScore>();

                            for (ParseUser candidate : candidates) {
                                int score = 0;

                                String locationRegion = location.substring(location.indexOf(",") + 2);
                                String candidateLoc = candidate.getString(Keys.KEY_LOC);

                                if (candidateLoc != null && candidateLoc.length() != 0) {
                                    String candidateRegion = candidateLoc.substring(candidateLoc.indexOf(",") + 2);
                                    if (candidateRegion.equals(locationRegion)) {
                                        score++;
                                    }
                                }
                                scores.add(new ConvoPartnerScore(score, candidate));
                            }

                            Collections.sort(scores, new ConvoPartnerScoreComparator());
                            partner = scores.get(0).getUser(); //take highest scoring user

                        } else { //if we have both
                            ArrayList<ConvoPartnerScore> scores = new ArrayList<ConvoPartnerScore>();

                            for (ParseUser candidate : candidates) {
                                int score = 0;

                                int userYr = Integer.parseInt(birthday.substring(6));
                                String candidateBday = candidate.getString(Keys.KEY_BDAY);

                                if (candidateBday != null && candidateBday.length() != 0) {
                                    int candidateYr = Integer.parseInt(candidateBday.substring(6));

                                    if (Math.abs(candidateYr - userYr) <= 5) {
                                        score++;
                                    }
                                }

                                String locationRegion = location.substring(location.indexOf(",") + 2);
                                String candidateLoc = candidate.getString(Keys.KEY_LOC);

                                if (candidateLoc != null && candidateLoc.length() != 0) {
                                    String candidateRegion = candidateLoc.substring(candidateLoc.indexOf(",") + 2);
                                    if (candidateRegion.equals(locationRegion)) {
                                        score++;
                                    }
                                }

                                scores.add(new ConvoPartnerScore(score, candidate));
                            }

                            Collections.sort(scores, new ConvoPartnerScoreComparator());
                            partner = scores.get(0).getUser(); //take highest scoring user
                        }

                        // Update convo buddy of userToPartner and the chosen partner
                        ConvoBuddy convoPartner = (ConvoBuddy) partner.getParseObject(Keys.KEY_CONVO);
                        convoPartner.putBuddy(userToPartner);
                        convoPartner.saveInBackground();

                        ConvoBuddy convoUser = (ConvoBuddy) userToPartner.getParseObject(Keys.KEY_CONVO);
                        convoUser.putBuddy(partner);
                        final ParseUser finalPartner = partner;
                        convoUser.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null && isCurUser) {
                                    Email email = (Email) finalPartner.getParseObject(Keys.KEY_EMAIL_OB); //email of partner
                                    tvConvoBuddy.setText(finalPartner.getString(Keys.KEY_NAME) + " (" + email.getEmail() + ")"); //set text
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    public void launchCamera() {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference for future access
        photoFile = getPhotoFileUri(photoFileName);

        // wrap File object into a content provider
        // required for API >= 24
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
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
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
    private void sendPost(ParseUser user, File photo) {
        user.put(Keys.KEY_PROFILE_PIC, new ParseFile(photo));
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "done: error while saving", e);
                    Toast.makeText(getContext(), getContext().getString(R.string.save_error), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
