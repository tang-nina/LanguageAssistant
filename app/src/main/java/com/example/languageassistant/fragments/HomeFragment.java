package com.example.languageassistant.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.languageassistant.R;
import com.example.languageassistant.models.Prompt;
import com.example.languageassistant.models.Response;
import com.google.android.material.card.MaterialCardView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * A fragment to display the daily prompts.
 */
public class HomeFragment extends Fragment {

    //interface for communication with host activity
    public interface OnItemSelectedListener {
        public void onPromptSelected(String string);
    }

    public static final String KEY_DAY_OF_YEAR = "dayOfTheYear";
    private OnItemSelectedListener listener;

    LinearLayout rlPrompts;
    MaterialCardView mcvContainer1;
    MaterialCardView mcvContainer2;
    MaterialCardView mcvContainer3;
    TextView tvPrompt1;
    TextView tvPrompt2;
    TextView tvPrompt3;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
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
    public void onViewCreated(@NonNull View view, @NonNull Bundle savedInstanceState){
        final String[] prompts = new String[3]; //storing the three prompts
        final boolean[] flags = new boolean[3]; //storing if each prompt is still open for response

        rlPrompts = view.findViewById(R.id.rlPrompts);
        mcvContainer1 = view.findViewById(R.id.mcvContainer1);
        mcvContainer2 = view.findViewById(R.id.mcvContainer2);
        mcvContainer3 = view.findViewById(R.id.mcvContainer3);
        tvPrompt1 = view.findViewById(R.id.tvPrompt1);
        tvPrompt2 = view.findViewById(R.id.tvPrompt2);
        tvPrompt3 = view.findViewById(R.id.tvPrompt3);

        Calendar calendar = Calendar.getInstance();
        int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);

        ParseQuery<Prompt> query = ParseQuery.getQuery(Prompt.class);
        query.setLimit(3);
        query.whereEqualTo(KEY_DAY_OF_YEAR, dayOfYear);
        query.findInBackground(new FindCallback<Prompt>() {
            public void done(List<Prompt> objects, ParseException e) {
                if(e==null){
                    try {
                        //save into storage array
                        prompts[0] = objects.get(0).getPrompt();
                        prompts[1] = objects.get(1).getPrompt();
                        prompts[2] = objects.get(2).getPrompt();

                        //load onto screen
                        tvPrompt1.setText(prompts[0]);
                        tvPrompt2.setText(prompts[1]);
                        tvPrompt3.setText(prompts[2]);

                        //check if user has already responded to them
                        //get the responses from today
                        ParseQuery<Response> queryResponded = ParseQuery.getQuery(Response.class);
                        String today = (new Date()).toString();
                        String formattedToday = today.substring(4, 11) + today.substring(24);
                        queryResponded.whereEqualTo(Response.KEY_DATE, formattedToday);
                        queryResponded.findInBackground(new FindCallback<Response>() {
                            public void done(List<Response> objects, ParseException e) {
                                if(e==null){
                                    if(objects.size() != 0 && objects.size() <= 3){ //if user has responded

                                        //for each response
                                        for(Response r: objects){
                                            String curPrompt = r.getPrompt();
                                            //check which prompt it was responding to
                                            for(int i=0; i<prompts.length; i++){
                                                if(curPrompt.equals(prompts[i])){
                                                    flags[i] = true; //change the corresponding flag
                                                }
                                            }
                                        }
                                    }

                                    mcvContainer1.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            if(flags[0] == true){
                                                //pop up saying they already responded
                                                dialogAlreadyResponded();
                                            }else{
                                                //send to respond fragment
                                                listener.onPromptSelected(prompts[0]);
                                            }
                                        }
                                    });

                                    mcvContainer2.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            if(flags[1] == true){
                                                //pop up saying they already responded
                                                dialogAlreadyResponded();
                                            }else{
                                                //send to respond fragment
                                                listener.onPromptSelected(prompts[1]);
                                            }
                                        }
                                    });

                                    mcvContainer3.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            if(flags[2] == true){
                                                //pop up saying they already responded
                                                dialogAlreadyResponded();
                                            }else{
                                                //send to respond fragment
                                                listener.onPromptSelected(prompts[2]);
                                            }
                                        }
                                    });
                                }else{
                                    //say something's wrong, please restart app
                                }
                            }
                        });
                    }catch(IndexOutOfBoundsException exception){
                        //display a screen that says please restart app or something like that
                    }
                }
            }
        });
    }

    //pop up dialog for prompts that already have a response
    private void dialogAlreadyResponded(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(true);
        builder.setTitle(getContext().getString(R.string.respond_title));
        builder.setMessage(getContext().getString(R.string.respond_msg));
        builder.setPositiveButton(getContext().getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}