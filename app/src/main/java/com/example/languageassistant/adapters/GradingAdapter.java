package com.example.languageassistant.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.languageassistant.R;
import com.example.languageassistant.models.Grading;
import com.example.languageassistant.models.Response;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

public class GradingAdapter extends RecyclerView.Adapter<com.example.languageassistant.adapters.GradingAdapter.ViewHolder> {
        Context context;
        List<Response> responses;

        public GradingAdapter(Context context, List<Response> responses) {
            this.context = context;
            this.responses = responses;
        }

        @NonNull
        @Override
        public GradingAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_to_grade, parent, false);
            return new GradingAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull GradingAdapter.ViewHolder holder, int position) {
            Response response = responses.get(position);
            holder.bind(response);

        }

        @Override
        public int getItemCount() {
            return responses.size();
        }


        class ViewHolder extends RecyclerView.ViewHolder{

            public static final int MAX_COMMENT_LENGTH = 500;

            MaterialCardView mcvContainer;
            TextView tvPrompt;
            TextView tvDate;
            TextView tvResponse;
            RatingBar rbRating;
            TextInputEditText tietComments;
            MaterialButton btnSubmit;


            public ViewHolder(@NonNull final View itemView) {
                super(itemView);

                mcvContainer = itemView.findViewById(R.id.mcvContainer);
                tvPrompt = itemView.findViewById(R.id.tvPrompt);
                tvDate = itemView.findViewById(R.id.tvDate);
                tvResponse = itemView.findViewById(R.id.tvResponse);
                rbRating = itemView.findViewById(R.id.rbRating);
                tietComments = itemView.findViewById(R.id.tietComments);
                btnSubmit = itemView.findViewById(R.id.btnSubmit);

            }

            private void submitFeedback(Response response, float rating, String comment){

                response.setGrade((int) rating);
                response.setComments(comment);
                response.setGraded(true);
                response.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {

                        if(e == null){

                            Grading grading = (Grading) ParseUser.getCurrentUser().getParseObject("grading");

                            grading.addGraded();
                            grading.subtractLeftToGrade();

//                            //try catch in case. I really don't know why this works sometimes and other times it doesn't.
//                            try {
//                                user.put("totalResponsesGraded", ((int) user.getNumber("totalResponsesGraded")) + 1);
//                                user.put("responsesLeftToGrade", ((int) user.getNumber("responsesLeftToGrade")) - 1);
//                            }catch(Exception exception){
//
//                            }


                            grading.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if(e == null){
                                        Toast.makeText(context, "Your feedback has been saved successfully.", Toast.LENGTH_SHORT).show();
                                        //clearing in case this affects other responses that get attached to the recycler view?
                                        rbRating.setRating(0);
                                        tietComments.getText().clear();

                                        //should clear this entire card from the recycler view right away.

                                    }else{
                                        System.out.println("ISNT WORKING");
                                    }

                                }
                            });


                        }else{
                            System.out.println("HEREEEEEEEEEEEE ggrading adapter");
                        }

                    }
                });

            }


            public void bind(final Response response) {
                tvPrompt.setText(response.getPrompt());
                //check if written or audio, but for now just assuming all is written
                tvResponse.setText(response.getWrittenAnswer());
                tvDate.setText(Response.getRelativeTimeAgo(response.getTimestamp().toString()));

                btnSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //does this need to be an int??? relating to how
                        //graded shows and stores the scores
                        final float rating = rbRating.getRating();
                        final String comment = tietComments.getText().toString();


                        if (comment.length() > MAX_COMMENT_LENGTH){
                            Toast.makeText(context, "Could not submit - your comments are too long.", Toast.LENGTH_SHORT).show();
                        }else if (rating == 0.0){
                            //pop up asking if they really meant to give a zero
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setCancelable(true);
                            //builder.setTitle("Please confirm.");
                            builder.setMessage("Are you sure you want to give a score of 0 to this response?");
                            builder.setPositiveButton("Confirm",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            submitFeedback(response, rating, comment);
                                        }
                                    });
                            builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //nothing happens
                                }
                            });

                            AlertDialog dialog = builder.create();
                            dialog.show();

                        }else{
                            submitFeedback(response, rating, comment);
                        }
                    }
                });

            }

        }


    }
