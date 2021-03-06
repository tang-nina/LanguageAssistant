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
import com.example.languageassistant.Keys;
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
 * A fragment for displaying the graded elements.
 */
public class GradedFragment extends Fragment {
    private static final String TAG = "GradedFragment";
    private static final int NUMBER_TO_LOAD = 20; //number of responses to load with each call

    RecyclerView rvGraded;
    List<Response> responses;
    GradedAdapter adapter;
    LinearLayoutManager llm;

    private SwipeRefreshLayout swipeContainer;
    private EndlessRecyclerViewScrollListener scrollListener;
    Date lastPostTime;
    TextView tvNothingMessage;

    public GradedFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_graded, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @NonNull Bundle savedInstanceState) {
        rvGraded = view.findViewById(R.id.rvGraded);
        tvNothingMessage= view.findViewById(R.id.tvNothingMessage);

        //set up recycler view
        responses = new ArrayList<Response>();
        adapter = new GradedAdapter(view.getContext(), responses);
        llm = new LinearLayoutManager(view.getContext());
        rvGraded.setAdapter(adapter);
        rvGraded.setLayoutManager(llm);

        // Setup refresh listener which triggers new data loading
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getGradedResponses(true);
            }
        });
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        // Setup endless scroll listener
        scrollListener = new EndlessRecyclerViewScrollListener(llm) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadNextDataFromApi();
            }
        };
        rvGraded.addOnScrollListener(scrollListener);

        //load data
        getGradedResponses(false);
    }

    //load older data for endless scroll
    public void loadNextDataFromApi() {
        ParseQuery<Response> query = ParseQuery.getQuery(Response.class);
        query.whereEqualTo(Keys.KEY_RESPONDER, ParseUser.getCurrentUser());
        query.addDescendingOrder(Keys.KEY_CREATED);
        query.setLimit(NUMBER_TO_LOAD);
        if(lastPostTime != null){
            query.whereLessThan(Keys.KEY_CREATED, lastPostTime);
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

    //load most current responses
    private void getGradedResponses(final boolean onRefresh) {
        ParseQuery<Response> query = ParseQuery.getQuery(Response.class);
        query.whereEqualTo(Keys.KEY_RESPONDER, ParseUser.getCurrentUser());
        query.addDescendingOrder(Keys.KEY_CREATED);
        query.setLimit(NUMBER_TO_LOAD);
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
                        //if there is nothing to display, make placeholder visible
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