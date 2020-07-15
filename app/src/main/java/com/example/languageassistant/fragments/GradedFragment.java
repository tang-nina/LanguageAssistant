package com.example.languageassistant.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.languageassistant.R;
import com.example.languageassistant.adapters.GradedAdapter;
import com.example.languageassistant.models.Response;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GradedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GradedFragment extends Fragment{
    RecyclerView rvGraded;
    List<Response> responses;
    GradedAdapter adapter;
    LinearLayoutManager llm;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public GradedFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GradedFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GradedFragment newInstance(String param1, String param2) {
        GradedFragment fragment = new GradedFragment();
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
        return inflater.inflate(R.layout.fragment_graded, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @NonNull Bundle savedInstanceState){
        rvGraded = view.findViewById(R.id.rvGraded);
        responses = new ArrayList<Response>();
        adapter = new GradedAdapter(view.getContext(), responses);
        llm = new LinearLayoutManager(view.getContext());
        rvGraded.setAdapter(adapter);
        rvGraded.setLayoutManager(llm);

        ParseQuery<Response> query = ParseQuery.getQuery(Response.class);
        query.whereEqualTo("respondingUser", ParseUser.getCurrentUser());
        query.addDescendingOrder(Response.KEY_CREATED);
        query.findInBackground(new FindCallback<Response>() {
            public void done(List<Response> objects, ParseException e) {
                responses.addAll(objects);
                adapter.notifyDataSetChanged();
            }
        });
    }

}