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

    public Response getItemAt(int position){ return responses.get(position);}

    class ViewHolder extends RecyclerView.ViewHolder{

        boolean flag; //false if feedback not showing, true if feedback showing

        TextView tvPrompt;
        TextView tvDate;
        TextView tvResponse;
        TextView tvNoGrade;
        TextView tvScore;
        TextView tvFeedback;
        MaterialCardView mcvContainer;

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

            flag = false;
            //all of the flag stuff as implemented means that
            //if you open feedback, scroll past it and then come back to it, the feedback will
            //be reset to not showing

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
                        }else if(response.getGraded() == true){
                            tvScore.setVisibility(View.VISIBLE);
                            tvFeedback.setVisibility(View.VISIBLE);

                            //should a try catch go around this in case???
                            tvScore.setText("Score: "+ response.getGrade());

                            if(response.getComments().equals("")){
                                tvFeedback.setText("No additional comments.");
                            }else{
                                tvFeedback.setText("Additional Comments: "+response.getComments());
                            }
                        }else{
                            System.out.println("SOMETHING WENT WRONG, CHECK DEFAULT VAL FOR GRADED");
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


            tvPrompt.setText(response.getPrompt());
            tvDate.setText(Response.getRelativeTimeAgo(response.getTimestamp().toString()));


            if(response.getRecordedAnswer()!= null){
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

                tvResponse.setText("Play recording: ");
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
                //Thu Jul 16 10:36:13 EDT 2020
                //    Wed Jul 15 18:14:02 EDT 2020

            }else{
                ivPlay.setVisibility(View.GONE);
                tvPlaying.setVisibility(View.GONE);
                tvResponse.setText(response.getWrittenAnswer());
                //Thu Jul 16 10:36:13 EDT 2020
                //    Wed Jul 15 18:14:02 EDT 2020

            }

        }

    }


}