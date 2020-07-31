package com.example.languageassistant.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.languageassistant.Keys;
import com.example.languageassistant.R;
import com.example.languageassistant.models.Grading;
import com.example.languageassistant.models.GradingComparator;
import com.example.languageassistant.models.GradingScore;
import com.example.languageassistant.models.Response;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * A fragment for responding to prompts.
 */
public class RespondFragment extends Fragment {
    private static final String TAG = "RespondFragment";
    private static final String LOG_TAG = "AudioRecordTest";
    private static final String RECORD_TAG = "record_button";
    private static final String STOP_TAG = "stop_button";
    private static final String PROMPT_TAG = "prompt";
    private static final int MAX_COMMENT_LENGTH = 1000;

    // File path of recorded audio
    private File mFileName;
    MediaRecorder mediaRecorder;
    MediaPlayer mediaPlayer;

    // Interface for interacting with main activity
    public interface OnItemSelectedListener {
        public void onAnswerSubmitted();
    }

    private OnItemSelectedListener listener;
    private String prompt;
    TextView tvPrompt;
    TextInputLayout tilResponse;
    TextInputEditText tietResponse;
    TextView tvWriteHeader;
    MaterialButton btnSubmit;
    MaterialButton btnWrite;
    MaterialButton btnRecord;

    //audio related
    ImageView ivRecord;
    ImageView ivPlay;
    RelativeLayout rlRecording;
    RelativeLayout rlPlay;
    MaterialButton btnSubmitRecording;
    TextView tvRecordInstructions;
    TextView tvPlayInstructions;

    public RespondFragment() {
        // Required empty public constructor
    }

    //factory method for creating a new instance of this fragment
    public static RespondFragment newInstance(String prompt) {
        RespondFragment fragment = new RespondFragment();
        Bundle args = new Bundle();
        args.putSerializable(PROMPT_TAG, prompt);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            prompt = getArguments().getString(PROMPT_TAG);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_respond, container, false);
    }

    // Store the listener (activity) that will have events fired once the fragment is attached
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnItemSelectedListener) {
            listener = (OnItemSelectedListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement MyListFragment.OnItemSelectedListener");
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @NonNull Bundle savedInstanceState) {
        tvPrompt = view.findViewById(R.id.tvPrompt);
        tietResponse = view.findViewById(R.id.tietResponse);
        tilResponse = view.findViewById(R.id.tilResponse);
        btnSubmit = view.findViewById(R.id.btnSubmit);
        btnWrite = view.findViewById(R.id.btnWrite);
        btnRecord = view.findViewById(R.id.btnRespond);
        tvWriteHeader = view.findViewById(R.id.tvWriteHeader);

        ivRecord = view.findViewById(R.id.ivStart);
        ivPlay = view.findViewById(R.id.ivPlay);
        rlPlay = view.findViewById(R.id.rlPlay);
        rlRecording = view.findViewById(R.id.rlRecording);
        btnSubmitRecording = view.findViewById(R.id.btnSubmitRecording);
        tvPlayInstructions = view.findViewById(R.id.tvPlayInstructions);
        tvRecordInstructions = view.findViewById(R.id.tvRecordInstructions);

        tvPrompt.setText(prompt);
        mFileName = getFileUri("audiorecordtest.3gp");

        //default open writing option
       btnRecord.setBackgroundColor(getResources().getColor(R.color.background2));
//        btnWrite.setBackgroundColor(getResources().getColor(R.color.colorAccent));
//        btnRecord.setBackgroundColor(getResources().getColor(R.color.light_pink));
        rlRecording.setVisibility(View.GONE);

        btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnRecord.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                btnWrite.setBackgroundColor(getResources().getColor(R.color.background2));

//                btnRecord.setBackgroundColor(getResources().getColor(R.color.colorAccent));
//                btnWrite.setBackgroundColor(getResources().getColor(R.color.light_pink));

                //make recording visible
                rlRecording.setVisibility(View.VISIBLE);

                //make writing invisible
                tilResponse.setVisibility(View.GONE);
                tvWriteHeader.setVisibility(View.GONE);
                btnSubmit.setVisibility(View.GONE);
            }
        });

        btnWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnRecord.setBackgroundColor(getResources().getColor(R.color.background2));
                btnWrite.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

//                btnRecord.setBackgroundColor(getResources().getColor(R.color.light_pink));
//                btnWrite.setBackgroundColor(getResources().getColor(R.color.colorAccent));

                //make recording invisible
                rlRecording.setVisibility(View.GONE);
                rlPlay.setVisibility(View.GONE);
                btnSubmitRecording.setVisibility(View.GONE);

                //make writing visible
                tilResponse.setVisibility(View.VISIBLE);
                tvWriteHeader.setVisibility(View.VISIBLE);
                btnSubmit.setVisibility(View.VISIBLE);
            }
        });

        //submit the written response
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String answer = tietResponse.getText().toString();

                if (answer.length() > MAX_COMMENT_LENGTH) { //too long
                    Toast.makeText(view.getContext(), view.getContext().getString(R.string.answer_long), Toast.LENGTH_SHORT).show();
                } else if (answer.length() == 0) { //no response
                    Toast.makeText(view.getContext(), view.getContext().getString(R.string.no_response), Toast.LENGTH_SHORT).show();
                } else {
                    makeNewResponse(prompt, answer, null, true);
                }
            }
        });

        //record an answer
        ivRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Verify that the device has a mic first
                PackageManager pmanager = getActivity().getPackageManager();
                if (pmanager.hasSystemFeature(PackageManager.FEATURE_MICROPHONE)) {
                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                        //ask for audio permission if none
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.RECORD_AUDIO}, 0);
                    } else {
                        if (ivRecord.getTag().equals(RECORD_TAG)) {
                            Glide.with(getContext()).load(R.drawable.ic_baseline_stop_24).into(ivRecord);
                            ivRecord.setTag(STOP_TAG);
                            tvRecordInstructions.setText(getContext().getString(R.string.stop_recording));

                            rlPlay.setVisibility(View.GONE);
                            btnSubmitRecording.setVisibility(View.GONE);
                            mediaRecorder = new MediaRecorder();
                            // Set the audio format and encoder
                            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                            // Setup the output location
                            mediaRecorder.setOutputFile(mFileName.getAbsolutePath());

                            // Start the recording
                            try {
                                mediaRecorder.setMaxDuration(60000); //one minute limit
                                mediaRecorder.prepare();
                                mediaRecorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {
                                    @Override
                                    public void onInfo(MediaRecorder mr, int what, int extra) {
                                        //stop recording if one minute has passed
                                        if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
                                            Toast.makeText(getContext(), "One minute reached. Recording has stopped.", Toast.LENGTH_SHORT).show();

                                            Glide.with(getContext()).load(R.drawable.ic_baseline_fiber_manual_record_24).into(ivRecord);
                                            ivRecord.setTag(RECORD_TAG);
                                            tvRecordInstructions.setText(getContext().getString(R.string.start_recording));

                                            //stop recording
                                            mediaRecorder.stop();
                                            mediaRecorder.reset();
                                            mediaRecorder.release();

                                            rlPlay.setVisibility(View.VISIBLE);
                                            btnSubmitRecording.setVisibility(View.VISIBLE);

                                            //prepare audio player
                                            mediaPlayer = new MediaPlayer();
                                            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                                            try {
                                                mediaPlayer.setDataSource(mFileName.getAbsolutePath());
                                                mediaPlayer.prepare();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                });
                                mediaRecorder.start();

                            } catch (IOException e) {
                                Log.e(LOG_TAG, "prepare() failed");
                            }

                        } else {
                            Glide.with(getContext()).load(R.drawable.ic_baseline_fiber_manual_record_24).into(ivRecord);
                            ivRecord.setTag(RECORD_TAG);
                            tvRecordInstructions.setText(getContext().getString(R.string.start_recording));

                            //stop recording
                            mediaRecorder.stop();
                            mediaRecorder.reset();
                            mediaRecorder.release();

                            rlPlay.setVisibility(View.VISIBLE);
                            btnSubmitRecording.setVisibility(View.VISIBLE);

                            //prepare audio player
                            mediaPlayer = new MediaPlayer();
                            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                            try {
                                mediaPlayer.setDataSource(mFileName.getAbsolutePath());
                                mediaPlayer.prepare();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                } else { // no mic on device
                    Toast.makeText(getContext(), getContext().getString(R.string.no_mic), Toast.LENGTH_LONG).show();
                }
            }
        });


        //play previous recording
        ivPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        tvPlayInstructions.setText(getContext().getString(R.string.press_play));
                    }
                });
                tvPlayInstructions.setText(getContext().getString(R.string.playing));
                mediaPlayer.start();
            }
        });

        //submit audio response
        btnSubmitRecording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mFileName == null) {
                    Toast.makeText(view.getContext(), getContext().getString(R.string.no_recording), Toast.LENGTH_SHORT).show();
                } else {
                    if (mediaRecorder != null) {
                        mediaRecorder.release();
                    }
                    if (mediaPlayer != null) {
                        mediaPlayer.release();
                    }
                    makeNewResponse(prompt, null, mFileName, false);
                }
            }
        });

    }

    // Returns the File for a audio stored on disk given the fileName
    public File getFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_MUSIC), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d("here", "failed to create directory");
        }

        // Return the file target for the audio based on filename
        return new File(mediaStorageDir.getPath() + File.separator + fileName);
    }

    //creates new Response parse + algorithm to decide who grades it
    private void makeNewResponse(final String prompt, final String answer, final File recording, final boolean written) {
        final ParseUser curUser = ParseUser.getCurrentUser();

        ParseQuery<ParseUser> query = ParseQuery.getQuery(ParseUser.class);
        query.whereEqualTo(Keys.KEY_NATIVE_LANG, curUser.getString(Keys.KEY_TARGET_LANG)); //get all users who can grade in the targetlanguage
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    ArrayList<GradingScore> scores = new ArrayList<GradingScore>();

                    for (int i = 0; i < objects.size(); i++) {
                        Grading grading = (Grading) objects.get(i).getParseObject(Keys.KEY_GRADING);
                        scores.add(new GradingScore(grading, objects.get(i)));
                    }

                    if (objects.size() == 0) { //if none of these users exist
                        ParseQuery<ParseUser> query = new ParseQuery<ParseUser>(ParseUser.class);
                        query.whereEqualTo(Keys.KEY_ID, Keys.KEY_DEFAULT_ID);
                        query.findInBackground(new FindCallback<ParseUser>() {
                            @Override
                            public void done(List<ParseUser> objects, ParseException e) {
                                if (e == null) {
                                    //make new response with grader as the DEFAULT user in the database
                                    Response newResponse = new Response();
                                    newResponse.setResponder(curUser);
                                    newResponse.setPrompt(prompt);
                                    if (written) {
                                        newResponse.setWrittenAnswer(answer);
                                    } else {
                                        newResponse.setRecordedAnswer(recording);
                                    }

                                    String today = (new Date()).toString();
                                    String formattedToday = today.substring(4, 11) + today.substring(24);
                                    newResponse.setDateAnswered(formattedToday);
                                    newResponse.setGrader(objects.get(0));

                                    newResponse.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if (e == null) {
                                                Toast.makeText(getContext(), getContext().getString(R.string.response_submitted), Toast.LENGTH_SHORT).show();
                                                listener.onAnswerSubmitted();
                                            } else {
                                                Toast.makeText(getContext(), getContext().getString(R.string.try_again), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                } else {
                                    Toast.makeText(getContext(), getContext().getString(R.string.try_again), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    } else {
                        //pick best grader
                        Collections.sort(scores, new GradingComparator());
                        final ParseUser grader = scores.get(0).getUser();

                        //make new response
                        Response newResponse = new Response();
                        newResponse.setResponder(curUser);
                        newResponse.setPrompt(prompt);
                        if (written) {
                            newResponse.setWrittenAnswer(answer);
                        } else {
                            newResponse.setRecordedAnswer(recording);
                        }

                        String today = (new Date()).toString();
                        String formattedToday = today.substring(4, 11) + today.substring(24);
                        newResponse.setDateAnswered(formattedToday);
                        newResponse.setGrader(grader);

                        newResponse.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    //update grading user's Grading object
                                    Grading grading = (Grading) grader.getParseObject(Keys.KEY_GRADING);
                                    grading.addLeftToGrade();
                                    grading.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if (e == null) {
                                                Toast.makeText(getContext(), getContext().getString(R.string.response_submitted), Toast.LENGTH_SHORT).show();
                                                listener.onAnswerSubmitted();
                                            } else {
                                                Toast.makeText(getContext(), getContext().getString(R.string.try_again), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                } else {
                                    Toast.makeText(getContext(), getContext().getString(R.string.try_again), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                } else {
                    Toast.makeText(getContext(), getContext().getString(R.string.try_again), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}