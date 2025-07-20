package br.edu.ifsuldeminas.mch.codefacil;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import br.edu.ifsuldeminas.mch.codefacil.database.AppDatabase;
import br.edu.ifsuldeminas.mch.codefacil.database.dao.ChallengeDao;
import br.edu.ifsuldeminas.mch.codefacil.database.dao.UserProgressDao;
import br.edu.ifsuldeminas.mch.codefacil.model.Challenge;
import br.edu.ifsuldeminas.mch.codefacil.model.UserProgress;
import br.edu.ifsuldeminas.mch.codefacil.utils.AppPreferences;

public class StatisticsActivity extends AppCompatActivity {

    private TextView tvTotalSolved, tvTotalCorrect, tvTotalWrong;
    private TextView tvBasicStats, tvIntermediateStats, tvAdvancedStats;
    private ProgressBar progressBasic, progressIntermediate, progressAdvanced;
    private UserProgressDao userProgressDao;
    private ChallengeDao challengeDao;
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
        setContentView(R.layout.activity_statistics);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.title_activity_statistics);
        }

        initializeViews();

        AppDatabase db = AppDatabase.getDatabase(this);
        userProgressDao = db.userProgressDao();
        challengeDao = db.challengeDao();

        loadStatistics();
    }

    private void initializeViews() {
        tvTotalSolved = findViewById(R.id.tvTotalSolved);
        tvTotalCorrect = findViewById(R.id.tvTotalCorrect);
        tvTotalWrong = findViewById(R.id.tvTotalWrong);
        tvBasicStats = findViewById(R.id.tvBasicStats);
        tvIntermediateStats = findViewById(R.id.tvIntermediateStats);
        tvAdvancedStats = findViewById(R.id.tvAdvancedStats);
        progressBasic = findViewById(R.id.progressBasic);
        progressIntermediate = findViewById(R.id.progressIntermediate);
        progressAdvanced = findViewById(R.id.progressAdvanced);
    }

    private void loadStatistics() {
        challengeDao.getAll().observe(this, allChallenges -> {
            if (allChallenges == null) return;
            userProgressDao.getAllProgress().observe(this, allProgress -> {
                if (allProgress == null) return;

                long totalCorrect = allProgress.stream().filter(UserProgress::isCorrect).count();
                long totalCompleted = allProgress.stream().filter(UserProgress::isCompleted).count();
                long totalWrong = totalCompleted - totalCorrect;

                tvTotalSolved.setText(String.valueOf(totalCompleted));
                tvTotalCorrect.setText(String.valueOf(totalCorrect));
                tvTotalWrong.setText(String.valueOf(totalWrong));

                updateLevelStatistics(allChallenges, allProgress, getString(R.string.level_basic), tvBasicStats, progressBasic);
                updateLevelStatistics(allChallenges, allProgress, getString(R.string.level_intermediate), tvIntermediateStats, progressIntermediate);
                updateLevelStatistics(allChallenges, allProgress, getString(R.string.level_advanced), tvAdvancedStats, progressAdvanced);
            });
        });
    }

    private void updateLevelStatistics(List<Challenge> allChallenges, List<UserProgress> allProgress, String level, TextView textView, ProgressBar progressBar) {
        long totalChallengesInLevel = allChallenges.stream().filter(c -> c.getLevel().equalsIgnoreCase(level)).count();
        if (totalChallengesInLevel == 0) {
            textView.setText(getString(R.string.stats_no_challenges));
            progressBar.setProgress(0);
            return;
        }

        Map<Long, Challenge> challengeMap = allChallenges.stream().collect(Collectors.toMap(Challenge::getId, c -> c));
        long completedInLevel = allProgress.stream()
                .filter(p -> p.isCompleted() && challengeMap.containsKey(p.getChallengeId()) && challengeMap.get(p.getChallengeId()).getLevel().equalsIgnoreCase(level))
                .count();

        int percentage = (int) (( (double) completedInLevel / totalChallengesInLevel) * 100);
        progressBar.setProgress(percentage);
        textView.setText(String.format(Locale.getDefault(), "%d/%d (%d%%)", completedInLevel, totalChallengesInLevel, percentage));
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