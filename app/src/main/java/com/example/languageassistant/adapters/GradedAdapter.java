package com.example.languageassistant.adapters;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.languageassistant.R;
import com.example.languageassistant.models.Response;
import com.google.android.material.card.MaterialCardView;

import java.io.IOException;
import java.util.List;

/**
 * An adapter for a recycler view that displays graded responses.
 */
public class GradedAdapter extends RecyclerView.Adapter<GradedAdapter.ViewHolder> {
    Context context;
    List<Response> responses;

    public GradedAdapter(Context context, List<Response> responses) {
        this.context = context;
        this.responses = responses;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_graded, parent, false);
        return new GradedAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
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
     * A view holder for each graded response.
     */
    class ViewHolder extends RecyclerView.ViewHolder{

        boolean flag; //false if feedback not showing, true if feedback showing

        TextView tvPrompt;
        TextView tvDate;
        TextView tvResponse;
        TextView tvNoGrade;
        TextView tvScore;
        TextView tvFeedback;
        MaterialCardView mcvContainer;

        //audio related UI elements
        ImageView ivPlay;
        TextView tvPlaying;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);

            tvPrompt = itemView.findViewById(R.id.tvPrompt);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvResponse = itemView.findViewById(R.id.tvResponse);
            tvNoGrade = itemView.findViewById(R.id.tvNoGrade);
            tvScore = itemView.findViewById(R.id.tvScore);
            tvFeedback = itemView.findViewById(R.id.tvFeedback);
            mcvContainer = itemView.findViewById(R.id.mcvContainer);

            ivPlay = itemView.findViewById(R.id.ivPlay);
            tvPlaying = itemView.findViewById(R.id.tvPlaying);

            flag = false; //default feedback not showing
        }

        public void bind(final Response response) {

            tvNoGrade.setVisibility(View.GONE);
            tvScore.setVisibility(View.GONE);
            tvFeedback.setVisibility(View.GONE);

            mcvContainer.setOnTouchListener(new OnDoubleTapListener(itemView.getContext()) {
                @Override
                public void onDoubleTap(MotionEvent e) {


                    if(flag == false){
                        if(response.getGraded() == false){
                            tvNoGrade.setVisibility(View.VISIBLE);
                        }else{
                            tvScore.setVisibility(View.VISIBLE);
                            tvFeedback.setVisibility(View.VISIBLE);
                            tvScore.setText(context.getString(R.string.score_label) + " " + response.getGrade());

                            if(response.getComments().equals("")){
                                tvFeedback.setText(context.getString(R.string.no_add_comments));
                            }else{
                                tvFeedback.setText(context.getString(R.string.add_comments) + " " + response.getComments());
                            }
                        }
                        flag=true;
                    }else{
                        tvNoGrade.setVisibility(View.GONE);
                        tvScore.setVisibility(View.GONE);
                        tvFeedback.setVisibility(View.GONE);

                        flag = false;
                    }

                }
            });

//            mcvContainer.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    if(flag == false){
//                        if(response.getGraded() == false){
//                            tvNoGrade.setVisibility(View.VISIBLE);
//                        }else{
//                            tvScore.setVisibility(View.VISIBLE);
//                            tvFeedback.setVisibility(View.VISIBLE);
//                            tvScore.setText(context.getString(R.string.score_label) + " " + response.getGrade());
//
//                            if(response.getComments().equals("")){
//                                tvFeedback.setText(context.getString(R.string.no_add_comments));
//                            }else{
//                                tvFeedback.setText(context.getString(R.string.add_comments) + " " + response.getComments());
//                            }
//                        }
//                        flag=true;
//                    }else{
//                        tvNoGrade.setVisibility(View.GONE);
//                        tvScore.setVisibility(View.GONE);
//                        tvFeedback.setVisibility(View.GONE);
//
//                        flag = false;
//                    }
//
//                }
//            });

            tvPrompt.setText(response.getPrompt());
            tvDate.setText(Response.getRelativeTimeAgo(response.getTimestamp().toString()));

            if(response.getRecordedAnswer()!= null){//if the response was recorded
                final MediaPlayer mediaPlayer = new MediaPlayer();
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                String url = response.getRecordedAnswer().getUrl();
                try {
                    mediaPlayer.setDataSource(url);
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

                tvResponse.setText(context.getString(R.string.play_recording) + " ");
                ivPlay.setVisibility(View.VISIBLE);
                ivPlay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!mediaPlayer.isPlaying()){
                            mediaPlayer.start();
                            tvPlaying.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }else{ //if the response was written
                ivPlay.setVisibility(View.GONE);
                tvPlaying.setVisibility(View.GONE);
                tvResponse.setText(response.getWrittenAnswer());
            }

        }

    }


}