package com.example.languageassistant.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.languageassistant.R;
import com.example.languageassistant.fragments.GradingFragment;
import com.example.languageassistant.models.Grading;
import com.example.languageassistant.models.Response;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.IOException;
import java.util.List;

/**
 * An adapter for a recycler view tha displays responses that still need to be graded.
 */
public class GradingAdapter extends RecyclerView.Adapter<com.example.languageassistant.adapters.GradingAdapter.ViewHolder> {
    Context context;
    List<Response> responses;
    GradingFragment gradingFragment;

    public GradingAdapter(Context context, List<Response> responses, GradingFragment gf) {
        this.context = context;
        this.responses = responses;
        gradingFragment = gf;
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

    // Clean all elements of the recycler
    public void clear() {
        responses.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Response> list) {
        responses.addAll(list);
        notifyDataSetChanged();
    }

    /**
     * A view holder for each grading response.
     */
    class ViewHolder extends RecyclerView.ViewHolder {
        public static final int MAX_COMMENT_LENGTH = 500;
        public static final String KEY_GRADING = "grading";

        MaterialCardView mcvContainer;
        TextView tvPrompt;
        TextView tvDate;
        TextView tvResponse;
        RatingBar rbRating;
        TextInputEditText tietComments;
        MaterialButton btnSubmit;

        //audio related elements
        TextView tvPlaying;
        ImageView ivPlayAudio;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);

            mcvContainer = itemView.findViewById(R.id.mcvContainer);
            tvPrompt = itemView.findViewById(R.id.tvPrompt);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvResponse = itemView.findViewById(R.id.tvResponse);
            rbRating = itemView.findViewById(R.id.rbRating);
            tietComments = itemView.findViewById(R.id.tietComments);
            btnSubmit = itemView.findViewById(R.id.btnSubmit);

            ivPlayAudio = itemView.findViewById(R.id.ivPlayAudio);
            tvPlaying = itemView.findViewById(R.id.tvPlaying);
        }

        public void bind(final Response response) {
            if (response.getRecordedAnswer() != null) { //audio response
                ivPlayAudio.setVisibility(View.VISIBLE);

                tvPrompt.setText(response.getPrompt());
                tvResponse.setText(context.getString(R.string.play_recording) + " ");
                tvDate.setText(Response.getRelativeTimeAgo(response.getTimestamp().toString()));

                final MediaPlayer mediaPlayer = new MediaPlayer();
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                String url = response.getRecordedAnswer().getUrl();
                try {
                    mediaPlayer.setDataSource(url); //setting data source
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        tvPlaying.setVisibility(View.GONE);
                    }
                });

                ivPlayAudio.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!mediaPlayer.isPlaying()){
                            mediaPlayer.start();
                            tvPlaying.setVisibility(View.VISIBLE);
                        }
                    }
                });

                btnSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final float rating = rbRating.getRating();
                        final String comment = tietComments.getText().toString();

                        if (comment.length() > MAX_COMMENT_LENGTH) { //comments are too long
                            Toast.makeText(context, context.getString(R.string.comments_long), Toast.LENGTH_SHORT).show();
                        } else if (rating == 0.0) { //pop up asking if they really meant to give a zero
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setCancelable(true);
                            builder.setMessage(context.getString(R.string.confirm_question));
                            builder.setPositiveButton(context.getString(R.string.confirm),
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            //they did intend to give a 0
                                            mediaPlayer.release();
                                            submitFeedback(response, rating, comment, getAdapterPosition());
                                        }
                                    });
                            builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //nothing is submitted if they did not intend to give a 0
                                }
                            });

                            AlertDialog dialog = builder.create();
                            dialog.show();

                        } else { //everything is ok for a submission
                            mediaPlayer.release();
                            submitFeedback(response, rating, comment, getAdapterPosition());
                        }
                    }
                });

            } else { //written response
                ivPlayAudio.setVisibility(View.GONE);
                tvPrompt.setText(response.getPrompt());
                tvResponse.setText(response.getWrittenAnswer());
                tvDate.setText(Response.getRelativeTimeAgo(response.getTimestamp().toString()));

                btnSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final float rating = rbRating.getRating();
                        final String comment = tietComments.getText().toString();

                        if (comment.length() > MAX_COMMENT_LENGTH) { //comment is too long
                            Toast.makeText(context, context.getString(R.string.comments_long), Toast.LENGTH_SHORT).show();
                        } else if (rating == 0.0) { //pop up asking if they really meant to give a zero
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setCancelable(true);
                            builder.setMessage(context.getString(R.string.confirm_question));
                            builder.setPositiveButton(context.getString(R.string.confirm),
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            //score of 0 was intended
                                            submitFeedback(response, rating, comment, getAdapterPosition());
                                        }
                                    });
                            builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //nothing happens if 0 was not intended
                                }
                            });

                            AlertDialog dialog = builder.create();
                            dialog.show();
                        } else { //submit if everything is good
                            submitFeedback(response, rating, comment, getAdapterPosition());
                        }
                    }
                });

            }
        }

        //submits the feedback by adding the grading info to the parse response object
        private void submitFeedback(Response response, float rating, String comment, final int position) {
            response.setGrade((int) rating);
            response.setComments(comment);
            response.setGraded(true);
            response.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Grading grading = (Grading) ParseUser.getCurrentUser().getParseObject(KEY_GRADING);
                        grading.addGraded();
                        grading.subtractLeftToGrade();
                        grading.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    Toast.makeText(context, context.getString(R.string.feedback_saved), Toast.LENGTH_SHORT).show();

                                    rbRating.setRating(0);
                                    tietComments.getText().clear();

                                    //clear the response from the recycler view right away.
                                    responses.remove(position);
                                    notifyDataSetChanged();
                                    gradingFragment.setTvNothingMessage();

                                } else {
                                    Toast.makeText(context, context.getString(R.string.save_again), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    } else {
                        Toast.makeText(context, context.getString(R.string.save_again), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
