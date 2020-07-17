package com.example.languageassistant.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.languageassistant.EndlessRecyclerViewScrollListener;
import com.example.languageassistant.R;
import com.example.languageassistant.adapters.GradedAdapter;
import com.example.languageassistant.models.Response;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GradedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GradedFragment extends Fragment {
    private static final String TAG = "GradedFragment";
    Date lastPostTime;

    RecyclerView rvGraded;
    List<Response> responses;
    GradedAdapter adapter;
    LinearLayoutManager llm;
    TextView tvNothingMessage;

    private SwipeRefreshLayout swipeContainer;
    private EndlessRecyclerViewScrollListener scrollListener;

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
    public void onViewCreated(@NonNull View view, @NonNull Bundle savedInstanceState) {
        rvGraded = view.findViewById(R.id.rvGraded);
        tvNothingMessage= view.findViewById(R.id.tvNothingMessage);
        responses = new ArrayList<Response>();
        adapter = new GradedAdapter(view.getContext(), responses);
        llm = new LinearLayoutManager(view.getContext());
        rvGraded.setAdapter(adapter);
        rvGraded.setLayoutManager(llm);

        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                getGradedResponses(true);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        scrollListener = new EndlessRecyclerViewScrollListener(llm) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                loadNextDataFromApi(page);
            }
        };
        // Adds the scroll listener to RecyclerView
        rvGraded.addOnScrollListener(scrollListener);

        getGradedResponses(false);
    }

    // Append the next page of data into the adapter
    // This method probably sends out a network request and appends new data items to your adapter.
    public void loadNextDataFromApi(int offset) {
        ParseQuery<Response> query = ParseQuery.getQuery(Response.class);
        query.whereEqualTo("respondingUser", ParseUser.getCurrentUser());
        query.addDescendingOrder(Response.KEY_CREATED);
        query.setLimit(20);
        if(lastPostTime != null){
            query.whereLessThan(Response.KEY_CREATED, lastPostTime);
            tvNothingMessage.setVisibility(View.GONE);
        }
        query.findInBackground(new FindCallback<Response>() {
            public void done(List<Response> objects, ParseException e) {
                if(e==null) {
                    int size = objects.size();
                    if(size != 0){
                        lastPostTime = objects.get(size-1).getTimestamp();
                        adapter.addAll(objects);
                    }
                }else{
                    Log.e(TAG, "done: ", e);
                }
            }
        });
    }


    private void getGradedResponses(final boolean onRefresh) {

        ParseQuery<Response> query = ParseQuery.getQuery(Response.class);
        query.whereEqualTo("respondingUser", ParseUser.getCurrentUser());
        query.addDescendingOrder(Response.KEY_CREATED);
        query.setLimit(20);
        query.findInBackground(new FindCallback<Response>() {
            public void done(List<Response> objects, ParseException e) {
                if(e==null) {
                    if(objects.size()!=0){
                        tvNothingMessage.setVisibility(View.GONE);
                        lastPostTime = objects.get(objects.size() - 1).getTimestamp();
                        if (onRefresh) {
                            adapter.clear();
                            adapter.addAll(objects);
                            swipeContainer.setRefreshing(false);
                        } else {
                            responses.addAll(objects);
                            adapter.notifyDataSetChanged();
                        }
                    }else{
                        tvNothingMessage.setVisibility(View.VISIBLE);
                        swipeContainer.setRefreshing(false);
                    }

                }else{
                    Log.e(TAG, "done: ", e);
                }
            }
        });
    }
}