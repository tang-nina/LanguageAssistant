package com.example.languageassistant.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.languageassistant.EndlessRecyclerViewScrollListener;
import com.example.languageassistant.R;
import com.example.languageassistant.adapters.GradingAdapter;
import com.example.languageassistant.models.Response;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A fragment for displaying responses that still need to be graded
 */

public class GradingFragment extends Fragment {
    private static final String TAG = "GradingFragment";
    private static final int NUMBER_TO_LOAD = 20;

    RecyclerView rvGrading;
    List<Response> responses;
    GradingAdapter adapter;
    LinearLayoutManager llm;
    TextView tvNothingMessage;

    private SwipeRefreshLayout swipeContainer;
    private EndlessRecyclerViewScrollListener scrollListener;
    Date lastPostTime;

    public GradingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_grading, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @NonNull Bundle savedInstanceState){
        rvGrading = view.findViewById(R.id.rvGrading);
        tvNothingMessage = view.findViewById(R.id.tvNothingMessage);

        responses = new ArrayList<Response>();
        adapter = new GradingAdapter(view.getContext(), responses, this);
        llm = new LinearLayoutManager(view.getContext());
        rvGrading.setAdapter(adapter);
        rvGrading.setLayoutManager(llm);
        rvGrading.setItemAnimator(new DefaultItemAnimator());

        // Setup refresh listener
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getGradingResponses(true);
            }
        });
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        // set up endless scroll
        scrollListener = new EndlessRecyclerViewScrollListener(llm) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadNextDataFromApi();
            }
        };
        rvGrading.addOnScrollListener(scrollListener);

        //load data
        getGradingResponses(false);
    }

    //for loading older data in endless scroll
    private void loadNextDataFromApi() {
        ParseQuery<Response> query = ParseQuery.getQuery(Response.class);
        query.whereEqualTo(Response.KEY_GRADER, ParseUser.getCurrentUser());
        query.whereEqualTo(Response.KEY_GRADED, false);
        query.setLimit(NUMBER_TO_LOAD);
        query.addDescendingOrder(Response.KEY_CREATED);
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

    private void getGradingResponses(final boolean onRefresh){
        ParseQuery<Response> query = ParseQuery.getQuery(Response.class);
        query.whereEqualTo(Response.KEY_GRADER, ParseUser.getCurrentUser());
        query.whereEqualTo(Response.KEY_GRADED, false);
        query.setLimit(NUMBER_TO_LOAD);
        query.addDescendingOrder(Response.KEY_CREATED);
        query.findInBackground(new FindCallback<Response>() {
            public void done(List<Response> objects, ParseException e) {
                if(e == null){
                    if(objects.size()!=0){
                        tvNothingMessage.setVisibility(View.GONE);
                        lastPostTime = objects.get(objects.size() - 1).getTimestamp();
                        if(onRefresh){
                            adapter.clear();
                            adapter.addAll(objects);
                            swipeContainer.setRefreshing(false);
                        }else{
                            responses.addAll(objects);
                            adapter.notifyDataSetChanged();
                        }
                    }else{
                        //if no data to display, show placeholder message
                        tvNothingMessage.setVisibility(View.VISIBLE);
                        swipeContainer.setRefreshing(false);
                    }
                }else{
                    Log.e(TAG, "done: ",e );
                }
            }
        });
    }

    //method to ensure placeholder message is correctly shown/change it to correct visibility
    public void setTvNothingMessage(){
        if(responses.size()==0){
            tvNothingMessage.setVisibility(View.VISIBLE);
        }else{
            tvNothingMessage.setVisibility(View.GONE);
        }
    }
}