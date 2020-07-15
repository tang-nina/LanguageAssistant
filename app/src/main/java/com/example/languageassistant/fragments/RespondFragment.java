package com.example.languageassistant.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.languageassistant.R;
import com.example.languageassistant.models.Grading;
import com.example.languageassistant.models.Response;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RespondFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RespondFragment extends Fragment {
    private static final String ARG_PARAM1 = "prompt";
    public static final int MAX_COMMENT_LENGTH = 1000;

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

        tvPrompt.setText(prompt);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               String answer = tietResponse.getText().toString();

                if (answer.length() > MAX_COMMENT_LENGTH){
                    Toast.makeText(view.getContext(), "Could not submit - your answer is too long.", Toast.LENGTH_SHORT).show();
                }else if (answer.length() == 0){
                    Toast.makeText(view.getContext(), "Could not submit - you did not respond yet.", Toast.LENGTH_SHORT).show();
                }else{
                    makeNewResponse(prompt, answer);
                }
            }
        });

    }

    private void makeNewResponse(String prompt, String answer){
        Response newResponse = new Response();
        newResponse.put(Response.KEY_RESPONDER, ParseUser.getCurrentUser());
        newResponse.put(Response.KEY_ANSWER_WRITTEN, answer);
        newResponse.put(Response.KEY_PROMPT, prompt);

        //figure out grading user and put that in - get all grading objects, then see which one is the bext
        final ParseUser grader = ParseUser.getCurrentUser();//TEMPORARILY

        newResponse.put(Response.KEY_GRADER, grader);

        newResponse.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                //update grading user's Grading
                Grading grading = (Grading) grader.getParseObject("grading");
                grading.addLeftToGrade();
                grading.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        listener.onAnswerSubmitted();
                    }
                });
            }
        });
    }
}