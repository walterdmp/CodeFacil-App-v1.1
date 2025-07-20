package br.edu.ifsuldeminas.mch.codefacil;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import br.edu.ifsuldeminas.mch.codefacil.model.GlossaryTerm;
import br.edu.ifsuldeminas.mch.codefacil.utils.AppPreferences; // Importar AppPreferences

public class GlossaryAddEditActivity extends AppCompatActivity {

    private static final String TAG = "GlossaryAddEdit";
    private EditText editTextTerm, editTextDefinition, editTextLanguage;
    private ChipGroup chipGroupLevel;
    private Button buttonSave;
    private FirebaseFirestore db;
    private GlossaryTerm currentTerm;
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

        setContentView(R.layout.activity_glossary_add_edit);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        editTextTerm = findViewById(R.id.editTextTerm);
        editTextDefinition = findViewById(R.id.editTextDefinition);
        editTextLanguage = findViewById(R.id.editTextLanguage);
        chipGroupLevel = findViewById(R.id.chipGroupLevel);
        buttonSave = findViewById(R.id.buttonSave);

        currentTerm = (GlossaryTerm) getIntent().getSerializableExtra("glossary_term");

        if (currentTerm != null) {
            getSupportActionBar().setTitle(R.string.edit_term);
            editTextTerm.setText(currentTerm.getTerm());
            editTextDefinition.setText(currentTerm.getDefinition());
            editTextLanguage.setText(currentTerm.getLanguage());
            selectChip(currentTerm.getLevel());
        } else {
            getSupportActionBar().setTitle(R.string.add_term);
        }

        buttonSave.setOnClickListener(v -> saveTerm());
    }

    private void saveTerm() {
        String term = editTextTerm.getText().toString().trim();
        String definition = editTextDefinition.getText().toString().trim();
        String language = editTextLanguage.getText().toString().trim();
        String level = getSelectedChipText();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Utilizador não autenticado.", Toast.LENGTH_SHORT).show();
            return;
        }
        String userId = currentUser.getUid();

        if (TextUtils.isEmpty(term) || TextUtils.isEmpty(definition) || TextUtils.isEmpty(language) || TextUtils.isEmpty(level)) {
            Toast.makeText(this, "Por favor, preencha todos os campos.", Toast.LENGTH_SHORT).show();
            return;
        }

        GlossaryTerm newTerm = new GlossaryTerm(term, definition, language, level, userId);

        if (currentTerm != null) {
            if (!currentTerm.getUserId().equals(userId)) {
                Toast.makeText(this, "Você não tem permissão para editar este termo.", Toast.LENGTH_SHORT).show();
                return;
            }
            db.collection("glossary").document(currentTerm.getId())
                    .set(newTerm)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(GlossaryAddEditActivity.this, "Termo atualizado com sucesso!", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(GlossaryAddEditActivity.this, "Erro ao atualizar o termo.", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Erro ao atualizar documento", e);
                    });
        } else {
            db.collection("glossary")
                    .add(newTerm)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(GlossaryAddEditActivity.this, "Termo adicionado com sucesso!", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(GlossaryAddEditActivity.this, "Erro ao adicionar o termo.", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Erro ao adicionar documento", e);
                    });
        }
    }

    private String getSelectedChipText() {
        int selectedId = chipGroupLevel.getCheckedChipId();
        if (selectedId != View.NO_ID) {
            Chip selectedChip = findViewById(selectedId);
            return selectedChip.getText().toString();
        }
        return "";
    }

    private void selectChip(String level) {
        for (int i = 0; i < chipGroupLevel.getChildCount(); i++) {
            Chip chip = (Chip) chipGroupLevel.getChildAt(i);
            if (chip.getText().toString().equalsIgnoreCase(level)) {
                chip.setChecked(true);
                break;
            }
        }
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