package com.example.languageassistant.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    private OnItemSelectedListener listener;


    // Define the events that the fragment will use to communicate
    public interface OnItemSelectedListener {
        // This can be any number of events to be sent to the activity
        public void onPromptSelected(String string);
    }

    LinearLayout rlPrompts;
    MaterialCardView mcvContainer1;
    MaterialCardView mcvContainer2;
    MaterialCardView mcvContainer3;
    TextView tvPrompt1;
    TextView tvPrompt2;
    TextView tvPrompt3;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
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
        final String[] prompts = new String[3];
        final boolean[] flags = new boolean[3];


        rlPrompts = view.findViewById(R.id.rlPrompts);
        mcvContainer1 = view.findViewById(R.id.mcvContainer1);
        mcvContainer2 = view.findViewById(R.id.mcvContainer2);
        mcvContainer3 = view.findViewById(R.id.mcvContainer3);
        tvPrompt1 = view.findViewById(R.id.tvPrompt1);
        tvPrompt2 = view.findViewById(R.id.tvPrompt2);
        tvPrompt3 = view.findViewById(R.id.tvPrompt3);

        Calendar calendar = Calendar.getInstance();
        int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
        //System.out.println(dayOfYear);

        ParseQuery<Prompt> query = ParseQuery.getQuery(Prompt.class);
        query.setLimit(3);
        query.whereEqualTo("dayOfTheYear", dayOfYear);
        query.findInBackground(new FindCallback<Prompt>() {
            public void done(List<Prompt> objects, ParseException e) {
                if(e==null){
                    System.out.println("successful");
                    try {
                        prompts[0] = objects.get(0).getPrompt();
                        prompts[1] = objects.get(1).getPrompt();
                        prompts[2] = objects.get(2).getPrompt();
                        tvPrompt1.setText(prompts[0]);
                        tvPrompt2.setText(prompts[1]);
                        tvPrompt3.setText(prompts[2]);

                        //check if user has already responded to them
                        ParseQuery<Response> queryResponded = ParseQuery.getQuery(Response.class);
                        String today = (new Date()).toString();
                        String formattedToday = today.substring(4, 11) + today.substring(24);
                        System.out.println(formattedToday);

                        queryResponded.whereEqualTo(Response.KEY_DATE, formattedToday);
                        queryResponded.findInBackground(new FindCallback<Response>() {
                            public void done(List<Response> objects, ParseException e) {
                                if(e==null){
                                    if(objects.size() != 0 && objects.size() <= 3){
                                        for(Response r: objects){

                                            String curPrompt = r.getPrompt();

                                            for(int i=0; i<prompts.length; i++){
                                                if(curPrompt.equals(prompts[i])){
                                                    flags[i] = true;
                                                }
                                            }
                                        }
                                    }else if (objects.size() > 3){
                                        //something is wrong
                                        Toast.makeText(getContext(), "Something is wrong", Toast.LENGTH_SHORT).show();
                                    }

                                    mcvContainer1.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            if(flags[0] == true){
                                                //pop up asking if they really meant to give a zero
                                                dialogAlreadyResponded();
                                            }else{
                                                //else send to new fragment
                                                listener.onPromptSelected(prompts[0]);
                                            }
                                        }
                                    });

                                    mcvContainer2.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            if(flags[1] == true){
                                                //if already responded, open "you already responsded"
                                                dialogAlreadyResponded();
                                            }else{
                                                //else send to new fragment
                                                listener.onPromptSelected(prompts[1]);
                                            }
                                        }
                                    });

                                    mcvContainer3.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            if(flags[2] == true){
                                                //if already responded, open "you already responsded"
                                                dialogAlreadyResponded();
                                            }else{
                                                //else send to new fragment
                                                listener.onPromptSelected(prompts[2]);
                                            }
                                        }
                                    });
                                }else{
                                    //somethings wrong
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

    private void dialogAlreadyResponded(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(true);
        builder.setTitle("You have already responded to this prompt.");
        builder.setMessage("You can check the graded section to see your previous responses.");

        builder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}