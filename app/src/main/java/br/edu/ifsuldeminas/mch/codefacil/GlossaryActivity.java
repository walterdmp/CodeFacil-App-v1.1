package br.edu.ifsuldeminas.mch.codefacil;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import br.edu.ifsuldeminas.mch.codefacil.adapter.GlossaryAdapter;
import br.edu.ifsuldeminas.mch.codefacil.model.GlossaryTerm;
import br.edu.ifsuldeminas.mch.codefacil.utils.AppPreferences; // Importar AppPreferences

public class GlossaryActivity extends AppCompatActivity {

    private static final String TAG = "GlossaryActivity";
    private RecyclerView recyclerViewGlossary;
    private GlossaryAdapter adapter;
    private List<GlossaryTerm> glossaryTermList = new ArrayList<>();
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private AppPreferences appPreferences; // Adicionar AppPreferences

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // LÓGICA DO TEMA ADICIONADA AQUI
        appPreferences = new AppPreferences(this);
        if (appPreferences.isDarkModeEnabled()) {
            setTheme(R.style.Theme_CodeFacil_Dark);
        } else {
            setTheme(R.style.Theme_CodeFacil);
        }
        // FIM DA LÓGICA DO TEMA

        setContentView(R.layout.activity_glossary);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        recyclerViewGlossary = findViewById(R.id.recyclerViewGlossary);
        recyclerViewGlossary.setLayoutManager(new LinearLayoutManager(this));
        adapter = new GlossaryAdapter(this, glossaryTermList);
        recyclerViewGlossary.setAdapter(adapter);

        adapter.setOnGlossaryTermClickListener(new GlossaryAdapter.OnGlossaryTermClickListener() {
            @Override
            public void onGlossaryTermClick(GlossaryTerm term) {
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser != null && term.getUserId().equals(currentUser.getUid())) {
                    Intent intent = new Intent(GlossaryActivity.this, GlossaryAddEditActivity.class);
                    intent.putExtra("glossary_term", term);
                    startActivity(intent);
                } else {
                    Toast.makeText(GlossaryActivity.this, "Você só pode editar os termos que criou.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onGlossaryTermLongClick(GlossaryTerm term) {
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser != null && term.getUserId().equals(currentUser.getUid())) {
                    new AlertDialog.Builder(GlossaryActivity.this)
                            .setTitle(R.string.delete_term)
                            .setMessage(R.string.delete_term_confirmation)
                            .setPositiveButton(R.string.delete, (dialog, which) -> deleteTerm(term.getId()))
                            .setNegativeButton(R.string.cancel, null)
                            .show();
                } else {
                    Toast.makeText(GlossaryActivity.this, "Você só pode apagar os termos que criou.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        FloatingActionButton fab = findViewById(R.id.fabAddTerm);
        fab.setOnClickListener(view -> {
            startActivity(new Intent(GlossaryActivity.this, GlossaryAddEditActivity.class));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadGlossaryTerms();
    }

    private void loadGlossaryTerms() {
        db.collection("glossary")
                .orderBy("term", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        glossaryTermList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            GlossaryTerm term = document.toObject(GlossaryTerm.class);
                            term.setId(document.getId());
                            glossaryTermList.add(term);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.w(TAG, "Erro ao carregar documentos.", task.getException());
                        Toast.makeText(GlossaryActivity.this, "Erro ao carregar os termos.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void deleteTerm(String termId) {
        db.collection("glossary").document(termId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(GlossaryActivity.this, "Termo deletado com sucesso!", Toast.LENGTH_SHORT).show();
                    loadGlossaryTerms();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(GlossaryActivity.this, "Erro ao deletar o termo.", Toast.LENGTH_SHORT).show();
                    Log.w(TAG, "Erro ao deletar documento", e);
                });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}