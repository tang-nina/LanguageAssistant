package com.example.languageassistant.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.languageassistant.R;
import com.example.languageassistant.models.Response;

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

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvPrompt;
        TextView tvDate;
        TextView tvResponse;
        TextView tvNoGrade;
        TextView tvScore;
        TextView tvFeedback;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvPrompt = itemView.findViewById(R.id.tvPrompt);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvResponse = itemView.findViewById(R.id.tvResponse);
            tvNoGrade = itemView.findViewById(R.id.tvNoGrade);
            tvScore = itemView.findViewById(R.id.tvScore);
            tvFeedback = itemView.findViewById(R.id.tvFeedback);

        }

        public void bind(Response response) {
            tvPrompt.setText(response.getPrompt().getPrompt());
            //check if written or audio, but for now just assuming all is written
            tvResponse.setText(response.getWrittenAnswer());
            tvDate.setText(Response.getRelativeTimeAgo(response.getTimestamp().toString()));
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
        }

    }


}