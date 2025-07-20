package br.edu.ifsuldeminas.mch.codefacil.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.edu.ifsuldeminas.mch.codefacil.R;
import br.edu.ifsuldeminas.mch.codefacil.model.Challenge;

public class ChallengeAdapter extends RecyclerView.Adapter<ChallengeAdapter.ChallengeViewHolder> {

    private Context context;
    private List<Challenge> challenges;
    private OnChallengeClickListener listener;
    private int longClickedPosition;

    public interface OnChallengeClickListener {
        void onChallengeClick(Challenge challenge);
    }

    public void setOnChallengeClickListener(OnChallengeClickListener listener) {
        this.listener = listener;
    }

    public ChallengeAdapter(Context context, List<Challenge> challenges) {
        this.context = context;
        this.challenges = challenges;
    }

    public int getLongClickedPosition() {
        return longClickedPosition;
    }

    public void updateChallenges(List<Challenge> newChallenges) {
        this.challenges = newChallenges;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ChallengeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_challenge, parent, false);
        return new ChallengeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChallengeViewHolder holder, int position) {
        Challenge challenge = challenges.get(position);
        holder.tvChallengeTitle.setText(challenge.getTitle());
        holder.tvChallengeLevel.setText(context.getString(R.string.level_prefix, challenge.getLevel()));

        // A lógica agora usa os campos booleanos do próprio objeto Challenge,
        // que foram preenchidos na MainActivity.
        if (challenge.isCompleted()) {
            if (challenge.isCorrect()) {
                holder.statusIndicator.setBackgroundColor(ContextCompat.getColor(context, R.color.status_correct));
            } else {
                holder.statusIndicator.setBackgroundColor(ContextCompat.getColor(context, R.color.status_wrong));
            }
        } else {
            holder.statusIndicator.setBackgroundColor(ContextCompat.getColor(context, R.color.status_pending));
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onChallengeClick(challenge);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            this.longClickedPosition = holder.getAdapterPosition();
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return challenges.size();
    }

    public Challenge getChallengeAtPosition(int position) {
        if (position >= 0 && position < challenges.size()) {
            return challenges.get(position);
        }
        return null;
    }

    public static class ChallengeViewHolder extends RecyclerView.ViewHolder {
        TextView tvChallengeTitle;
        TextView tvChallengeLevel;
        View statusIndicator;
        CardView cardView;

        public ChallengeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvChallengeTitle = itemView.findViewById(R.id.tvChallengeTitle);
            tvChallengeLevel = itemView.findViewById(R.id.tvChallengeLevel);
            statusIndicator = itemView.findViewById(R.id.statusIndicator);
            cardView = itemView.findViewById(R.id.cardViewChallenge);
        }
    }
}