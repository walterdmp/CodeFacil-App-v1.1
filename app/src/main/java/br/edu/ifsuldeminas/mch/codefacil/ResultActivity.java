package br.edu.ifsuldeminas.mch.codefacil;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import br.edu.ifsuldeminas.mch.codefacil.database.DatabaseHelper;
import br.edu.ifsuldeminas.mch.codefacil.model.Challenge;
import br.edu.ifsuldeminas.mch.codefacil.utils.AppPreferences;

public class ResultActivity extends AppCompatActivity {

    private Challenge challenge;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppPreferences appPreferences = new AppPreferences(this);
        if (appPreferences.isDarkModeEnabled()) {
            setTheme(R.style.Theme_CodeFacil_Dark);
        } else {
            setTheme(R.style.Theme_CodeFacil);
        }
        setContentView(R.layout.activity_result);

        dbHelper = new DatabaseHelper(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.title_activity_result);
        }

        TextView tvResult = findViewById(R.id.tvResult);
        TextView tvExplanation = findViewById(R.id.tvExplanation);
        Button btnBackToDashboard = findViewById(R.id.btnBackToDashboard);
        Button btnNextChallenge = findViewById(R.id.btnNextChallenge);
        Button btnShareConquest = findViewById(R.id.btnShareConquest);

        challenge = (Challenge) getIntent().getSerializableExtra("challenge");
        boolean isCorrect = getIntent().getBooleanExtra("isCorrect", false);
        String explanation = getIntent().getStringExtra("explanation");

        if (isCorrect) {
            tvResult.setText(R.string.correct_answer);
            tvResult.setTextColor(ContextCompat.getColor(this, R.color.status_correct));
        } else {
            tvResult.setText(R.string.wrong_answer);
            tvResult.setTextColor(ContextCompat.getColor(this, R.color.status_wrong));
        }

        tvExplanation.setText(explanation);

        btnBackToDashboard.setOnClickListener(v -> {
            Intent intent = new Intent(ResultActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        btnNextChallenge.setOnClickListener(v -> openNextChallenge());

        btnShareConquest.setOnClickListener(v -> shareConquest(isCorrect));
    }

    private void openNextChallenge() {
        Challenge nextChallenge = dbHelper.getNextChallenge(challenge.getId());
        if (nextChallenge != null) {
            Intent intent = new Intent(ResultActivity.this, ChallengeActivity.class);
            intent.putExtra("challenge", nextChallenge);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, R.string.all_challenges_completed, Toast.LENGTH_SHORT).show();
        }
    }

    private void shareConquest(boolean isCorrect) {
        String shareMessage;
        if (isCorrect) {
            shareMessage = getString(R.string.share_message_correct, challenge.getTitle());
        } else {
            shareMessage = getString(R.string.share_message_wrong, challenge.getTitle());
        }

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_subject));
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);

        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_via)));
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