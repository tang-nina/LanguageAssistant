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
import com.example.languageassistant.R;
import com.example.languageassistant.models.Grading;
import com.example.languageassistant.models.Response;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.io.IOException;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RespondFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RespondFragment extends Fragment {
    private static final String TAG = "RespondFragment";
    private static final String ARG_PARAM1 = "prompt";
    public static final int MAX_COMMENT_LENGTH = 1000;


    // File path of recorded audio
    private File mFileName ;

    private static final String LOG_TAG = "AudioRecordTest";
    MediaRecorder mediaRecorder;
    MediaPlayer mediaPlayer;

    private OnItemSelectedListener listener;

    // Define the events that the fragment will use to communicate
    public interface OnItemSelectedListener {
        // This can be any number of events to be sent to the activity
        public void onAnswerSubmitted();
        //prob the number or the prompt they responded to?
        // I may not even need to send anything if the home fragment checks the prompts by itself

    }

    private String prompt;
    TextView tvPrompt;
    TextInputEditText tietResponse;
    MaterialButton btnSubmit;


    ImageView ivRecord;
    ImageView ivPlay;
    RelativeLayout rlPlay;
    MaterialButton btnSubmitRecording;

    TextView tvRecordInstructions;
    TextView tvPlayInstructions;

    public RespondFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment RespondFragment.
     */
    public static RespondFragment newInstance(String prompt) {
        RespondFragment fragment = new RespondFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, prompt);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            prompt = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
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
        btnSubmit = view.findViewById(R.id.btnSubmit);

        ivRecord = view.findViewById(R.id.ivStart);
        ivPlay = view.findViewById(R.id.ivPlay);
        rlPlay = view.findViewById(R.id.rlPlay);
        btnSubmitRecording = view.findViewById(R.id.btnSubmitRecording);
        tvPlayInstructions = view.findViewById(R.id.tvPlayInstructions);
        tvRecordInstructions = view.findViewById(R.id.tvRecordInstructions);

        tvPrompt.setText(prompt);
        mFileName = getFileUri("audiorecordtest.3gp");

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               String answer = tietResponse.getText().toString();

                if (answer.length() > MAX_COMMENT_LENGTH){
                    Toast.makeText(view.getContext(), "Could not submit - your answer is too long.", Toast.LENGTH_SHORT).show();
                }else if (answer.length() == 0){
                    Toast.makeText(view.getContext(), "Could not submit - you did not respond yet.", Toast.LENGTH_SHORT).show();
                }else{
                    makeNewWrittenResponse(prompt, answer);
                }
            }
        });

        ivRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Verify that the device has a mic first
                PackageManager pmanager = getActivity().getPackageManager();
                if (pmanager.hasSystemFeature(PackageManager.FEATURE_MICROPHONE)) {
                    // Set the file location for the audio
                    //mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
                    //mFileName += "/audiorecordtest.3gp";
                    // Create the recorder

                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.RECORD_AUDIO}, 0);
                    } else {

                        if(ivRecord.getTag().equals("record_button")){
                            Glide.with(getContext()).load(R.drawable.ic_baseline_stop_24).into(ivRecord);
                            ivRecord.setTag("stop_button");
                            tvRecordInstructions.setText("Press to stop recording.");

                            rlPlay.setVisibility(View.INVISIBLE);
                            mediaRecorder = new MediaRecorder();
                            // Set the audio format and encoder
                            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                            // Setup the output location
                            mediaRecorder.setOutputFile(mFileName.getAbsolutePath());

                            // Start the recording
                            try {
                                mediaRecorder.prepare();
                                mediaRecorder.start();

                            } catch (IOException e) {
                                Log.e(LOG_TAG, "prepare() failed");
                                System.out.println(""+e);    //to display the error
                            }

                        }else{
                            Glide.with(getContext()).load(R.drawable.ic_baseline_fiber_manual_record_24).into(ivRecord);
                            ivRecord.setTag("record_button");
                            tvRecordInstructions.setText("Press to start recording.");

                            mediaRecorder.stop();
                            mediaRecorder.reset();
                            mediaRecorder.release();

                            rlPlay.setVisibility(View.VISIBLE);
                            btnSubmitRecording.setVisibility(View.VISIBLE);

                            mediaPlayer = new MediaPlayer();
                            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                            try {
                                mediaPlayer.setDataSource(mFileName.getAbsolutePath());
                                mediaPlayer.prepare(); // must call prepare first
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                    }

                } else { // no mic on device
                    Toast.makeText(getContext(), "This device doesn't have a mic!", Toast.LENGTH_LONG).show();
                }
            }
        });


        ivPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        tvPlayInstructions.setText("Press to play.");
                    }

                });
                tvPlayInstructions.setText("playing");
                mediaPlayer.start();
            }
        });


        btnSubmitRecording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(mFileName == null){
                    Toast.makeText(view.getContext(), "You did not record anything yet.", Toast.LENGTH_SHORT).show();

                }else{
                    if(mediaRecorder != null){
                        mediaRecorder.release();
                    }

                    if(mediaPlayer !=null){
                        mediaPlayer.release();
                    }
                    makeNewAudioResponse(prompt, mFileName);
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
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d("here", "failed to create directory");
        }

        // Return the file target for the audio based on filename
        return new File(mediaStorageDir.getPath() + File.separator + fileName);
    }

    private void makeNewWrittenResponse(String prompt, String answer){
        Response newResponse = new Response();
        newResponse.setResponder(ParseUser.getCurrentUser());
        newResponse.setWrittenAnswer(answer);
        newResponse.setPrompt(prompt);

       String today = (new Date()).toString();
       String formattedToday = today.substring(4, 11) + today.substring(24);
       newResponse.setDateAnswered(formattedToday);

        //figure out grading user and put that in - get all grading objects, then see which one is the bext
        final ParseUser grader = ParseUser.getCurrentUser();//TEMPORARILY

        newResponse.setGrader(grader);

        newResponse.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                //update grading user's Grading
                Grading grading = (Grading) grader.getParseObject("grading");
                grading.addLeftToGrade();
                grading.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        Toast.makeText(getContext(), "Response submitted!", Toast.LENGTH_SHORT).show();
                        listener.onAnswerSubmitted();
                    }
                });
            }
        });
    }

    private void makeNewAudioResponse(String prompt, File recording){

        Response newResponse = new Response();
        newResponse.setResponder(ParseUser.getCurrentUser());
        newResponse.setRecordedAnswer(recording);
        newResponse.setPrompt(prompt);

        String today = (new Date()).toString();
        String formattedToday = today.substring(4, 11) + today.substring(24);
        newResponse.setDateAnswered(formattedToday);

        //figure out grading user and put that in - get all grading objects, then see which one is the bext
        final ParseUser grader = ParseUser.getCurrentUser();//TEMPORARILY

        newResponse.setGrader(grader);

        newResponse.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                //update grading user's Grading
                Grading grading = (Grading) grader.getParseObject("grading");
                grading.addLeftToGrade();
                grading.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        Toast.makeText(getContext(), "Response submitted!", Toast.LENGTH_SHORT).show();
                        listener.onAnswerSubmitted();
                    }
                });
            }
        });

    }
}