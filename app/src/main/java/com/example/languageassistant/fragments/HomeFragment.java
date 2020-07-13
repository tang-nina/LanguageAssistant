package com.example.languageassistant.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.languageassistant.R;
import com.example.languageassistant.models.Prompt;
import com.google.android.material.card.MaterialCardView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.Calendar;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    RelativeLayout rlPrompts;
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
    // TODO: Rename and change types and number of parameters
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


    @Override
    public void onViewCreated(@NonNull View view, @NonNull Bundle savedInstanceState){
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
                    tvPrompt1.setText(objects.get(0).getPrompt());
                    tvPrompt2.setText(objects.get(1).getPrompt());
                    tvPrompt3.setText(objects.get(2).getPrompt());
                }
            }
        });

        //check if user has already responded to them


    }
}