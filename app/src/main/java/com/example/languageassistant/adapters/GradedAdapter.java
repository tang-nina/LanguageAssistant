package com.example.languageassistant.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.languageassistant.R;
import com.example.languageassistant.models.Response;
import com.google.android.material.card.MaterialCardView;

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

        Response response;
        boolean flag;

        TextView tvPrompt;
        TextView tvDate;
        TextView tvResponse;
        TextView tvNoGrade;
        TextView tvScore;
        TextView tvFeedback;
        MaterialCardView mcvContainer;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);

            tvPrompt = itemView.findViewById(R.id.tvPrompt);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvResponse = itemView.findViewById(R.id.tvResponse);
            tvNoGrade = itemView.findViewById(R.id.tvNoGrade);
            tvScore = itemView.findViewById(R.id.tvScore);
            tvFeedback = itemView.findViewById(R.id.tvFeedback);
            mcvContainer = itemView.findViewById(R.id.mcvContainer);

            mcvContainer.setOnTouchListener(new OnDoubleTapListener(itemView.getContext()) {
                @Override
                public void onDoubleTap(MotionEvent e) {
                    Toast.makeText(itemView.getContext(), "Double Tap", Toast.LENGTH_SHORT).show();
                }
            });

            flag = false;

        }

        public void bind(Response r) {
            response = r;
            tvNoGrade.setVisibility(View.GONE);
            tvScore.setVisibility(View.GONE);
            tvFeedback.setVisibility(View.GONE);

            tvPrompt.setText(response.getPrompt());
            //check if written or audio, but for now just assuming all is written
            tvResponse.setText(response.getWrittenAnswer());
            tvDate.setText(Response.getRelativeTimeAgo(response.getTimestamp().toString()));


        }

        public boolean onDoubleTap(MotionEvent motionEvent) {

            if(response.getGraded() == false){
                tvNoGrade.setVisibility(View.VISIBLE);
            }else if(response.getGraded() == true){
                tvScore.setVisibility(View.VISIBLE);
                tvFeedback.setVisibility(View.VISIBLE);
                tvScore.setText(response.getGrade());
                tvFeedback.setText(response.getComments());
            }else{
                System.out.println("SOMETHING WENT WRONG, CHECK DEFAULT VAL FOR GRADED");
            }

            return true;
        }


    }


}