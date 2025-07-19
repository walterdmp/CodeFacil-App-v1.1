package br.edu.ifsuldeminas.mch.codefacil;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import br.edu.ifsuldeminas.mch.codefacil.database.AppDatabase;
import br.edu.ifsuldeminas.mch.codefacil.database.dao.UserProgressDao;
import br.edu.ifsuldeminas.mch.codefacil.model.Challenge;
import br.edu.ifsuldeminas.mch.codefacil.model.UserProgress;
import br.edu.ifsuldeminas.mch.codefacil.utils.AppPreferences;

public class ChallengeActivity extends AppCompatActivity {

    private Challenge currentChallenge;
    private TextView tvChallengeTitle;
    private TextView tvChallengeEnunciado;
    private LinearLayout optionsContainer;
    private EditText etAnnotation;
    private Button btnToggleAnnotation;
    private Button btnSaveAnnotation;
    private View annotationContainer;

    private UserProgressDao userProgressDao;
    private AppPreferences appPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appPreferences = new AppPreferences(this);
        if (appPreferences.isDarkModeEnabled()) {
            setTheme(R.style.Theme_CodeFacil_Dark);
        } else {
            setTheme(R.style.Theme_CodeFacil);
        }
        setContentView(R.layout.activity_challenge);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        initializeViews();
        userProgressDao = AppDatabase.getDatabase(this).userProgressDao();

        currentChallenge = (Challenge) getIntent().getSerializableExtra("challenge");

        if (currentChallenge != null) {
            displayChallenge();
            loadAnnotations();
        } else {
            Toast.makeText(this, "Erro: Desafio não encontrado.", Toast.LENGTH_SHORT).show();
            finish();
        }

        setupAnnotationListeners();
    }

    private void initializeViews() {
        tvChallengeTitle = findViewById(R.id.tvChallengeTitle);
        tvChallengeEnunciado = findViewById(R.id.tvChallengeEnunciado);
        optionsContainer = findViewById(R.id.optionsContainer);
        etAnnotation = findViewById(R.id.etAnnotation);
        btnToggleAnnotation = findViewById(R.id.btnToggleAnnotation);
        btnSaveAnnotation = findViewById(R.id.btnSaveAnnotation);
        annotationContainer = findViewById(R.id.annotationContainer);
    }

    private void displayChallenge() {
        tvChallengeTitle.setText(currentChallenge.getTitle());
        tvChallengeEnunciado.setText(currentChallenge.getEnunciado());
        createOptionButtons();
    }

    private void createOptionButtons() {
        optionsContainer.removeAllViews();
        if (currentChallenge.getOptions() == null) return;

        for (String option : currentChallenge.getOptions()) {
            Button optionButton = new Button(this);
            optionButton.setText(option);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 0, 0, 16);
            optionButton.setLayoutParams(params);
            optionButton.setOnClickListener(v -> checkAnswer(option));
            optionsContainer.addView(optionButton);
        }
    }

    private void checkAnswer(String userAnswer) {
        boolean isCorrect = userAnswer.equalsIgnoreCase(currentChallenge.getCorrectAnswer());

        UserProgress userProgress = userProgressDao.getProgressById(currentChallenge.getId());
        if (userProgress == null) {
            userProgress = new UserProgress(currentChallenge.getId(), true, isCorrect, System.currentTimeMillis(), etAnnotation.getText().toString());
        } else {
            userProgress.setCompleted(true);
            userProgress.setCorrect(isCorrect);
            userProgress.setLastAccessedTimestamp(System.currentTimeMillis());
        }
        userProgressDao.saveOrUpdate(userProgress);

        Intent intent = new Intent(ChallengeActivity.this, ResultActivity.class);
        intent.putExtra("challenge", currentChallenge);
        intent.putExtra("isCorrect", isCorrect);
        intent.putExtra("explanation", currentChallenge.getExplanation());
        startActivity(intent);
        finish();
    }

    private void loadAnnotations() {
        UserProgress userProgress = userProgressDao.getProgressById(currentChallenge.getId());
        if (userProgress != null && userProgress.getAnnotation() != null) {
            etAnnotation.setText(userProgress.getAnnotation());
        }
        if (getIntent().getBooleanExtra("showAnnotation", false)) {
            annotationContainer.setVisibility(View.VISIBLE);
            btnToggleAnnotation.setText("Esconder Anotação");
        }
    }

    private void setupAnnotationListeners() {
        btnToggleAnnotation.setOnClickListener(v -> {
            if (annotationContainer.getVisibility() == View.VISIBLE) {
                annotationContainer.setVisibility(View.GONE);
                btnToggleAnnotation.setText("Anotação Pessoal");
            } else {
                annotationContainer.setVisibility(View.VISIBLE);
                btnToggleAnnotation.setText("Esconder Anotação");
            }
        });

        btnSaveAnnotation.setOnClickListener(v -> {
            String annotation = etAnnotation.getText().toString();
            UserProgress userProgress = userProgressDao.getProgressById(currentChallenge.getId());
            if (userProgress == null) {
                userProgress = new UserProgress(currentChallenge.getId(), false, false, System.currentTimeMillis(), annotation);
            } else {
                userProgress.setAnnotation(annotation);
            }
            userProgressDao.saveOrUpdate(userProgress);
            Toast.makeText(this, "Anotação salva!", Toast.LENGTH_SHORT).show();
            annotationContainer.setVisibility(View.GONE);
            btnToggleAnnotation.setText("Anotação Pessoal");
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
