package br.edu.ifsuldeminas.mch.codefacil.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import br.edu.ifsuldeminas.mch.codefacil.R;
import br.edu.ifsuldeminas.mch.codefacil.model.GlossaryTerm;

public class GlossaryAdapter extends RecyclerView.Adapter<GlossaryAdapter.GlossaryViewHolder> {

    private Context context;
    private List<GlossaryTerm> glossaryTerms;
    private OnGlossaryTermClickListener listener;

    public interface OnGlossaryTermClickListener {
        void onGlossaryTermClick(GlossaryTerm term);
        void onGlossaryTermLongClick(GlossaryTerm term);
    }

    public void setOnGlossaryTermClickListener(OnGlossaryTermClickListener listener) {
        this.listener = listener;
    }

    public GlossaryAdapter(Context context, List<GlossaryTerm> glossaryTerms) {
        this.context = context;
        this.glossaryTerms = glossaryTerms;
    }

    @NonNull
    @Override
    public GlossaryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_glossary, parent, false);
        return new GlossaryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GlossaryViewHolder holder, int position) {
        GlossaryTerm term = glossaryTerms.get(position);
        holder.tvGlossaryTerm.setText(term.getTerm());
        // LIGANDO A DEFINIÇÃO
        holder.tvGlossaryDefinition.setText(term.getDefinition());
        holder.tvGlossaryLanguage.setText(context.getString(R.string.language_prefix, term.getLanguage()));
        holder.tvGlossaryLevel.setText(context.getString(R.string.level_prefix, term.getLevel()));

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onGlossaryTermClick(term);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) {
                listener.onGlossaryTermLongClick(term);
                return true;
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return glossaryTerms.size();
    }

    public static class GlossaryViewHolder extends RecyclerView.ViewHolder {
        // ADICIONANDO A REFERÊNCIA
        TextView tvGlossaryTerm, tvGlossaryDefinition, tvGlossaryLanguage, tvGlossaryLevel;

        public GlossaryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvGlossaryTerm = itemView.findViewById(R.id.tvGlossaryTerm);
            // INICIALIZANDO A REFERÊNCIA
            tvGlossaryDefinition = itemView.findViewById(R.id.tvGlossaryDefinition);
            tvGlossaryLanguage = itemView.findViewById(R.id.tvGlossaryLanguage);
            tvGlossaryLevel = itemView.findViewById(R.id.tvGlossaryLevel);
        }
    }
}