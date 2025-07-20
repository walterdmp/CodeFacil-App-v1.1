package br.edu.ifsuldeminas.mch.codefacil;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import br.edu.ifsuldeminas.mch.codefacil.adapter.ChallengeAdapter;
import br.edu.ifsuldeminas.mch.codefacil.database.AppDatabase;
import br.edu.ifsuldeminas.mch.codefacil.database.dao.ChallengeDao;
import br.edu.ifsuldeminas.mch.codefacil.database.dao.UserProgressDao;
import br.edu.ifsuldeminas.mch.codefacil.model.Challenge;
import br.edu.ifsuldeminas.mch.codefacil.model.UserProgress;
import br.edu.ifsuldeminas.mch.codefacil.notification.NotificationHelper;
import br.edu.ifsuldeminas.mch.codefacil.utils.AppPreferences;

public class MainActivity extends AppCompatActivity implements ChallengeAdapter.OnChallengeClickListener {

    private RecyclerView recyclerViewChallenges;
    private ChallengeAdapter challengeAdapter;
    private AppPreferences appPreferences;
    private List<Challenge> allChallenges = new ArrayList<>();
    private ChipGroup chipGroupFilter;
    private boolean isDarkModeActive;

    private ChallengeDao challengeDao;
    private UserProgressDao userProgressDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appPreferences = new AppPreferences(this);

        isDarkModeActive = appPreferences.isDarkModeEnabled();

        if (isDarkModeActive) {
            setTheme(R.style.Theme_CodeFacil_Dark);
        } else {
            setTheme(R.style.Theme_CodeFacil);
        }
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.app_name);
        }

        AppDatabase db = AppDatabase.getDatabase(this);
        challengeDao = db.challengeDao();
        userProgressDao = db.userProgressDao();

        setupViews();
        setupFilterChips();
        loadChallenges();

        NotificationHelper.createNotificationChannel(this);
        if (appPreferences.areNotificationsEnabled()) {
            NotificationHelper.scheduleDailyNotification(this);
        }
    }

    private void setupViews() {
        recyclerViewChallenges = findViewById(R.id.recyclerViewChallenges);
        recyclerViewChallenges.setLayoutManager(new LinearLayoutManager(this));
        registerForContextMenu(recyclerViewChallenges);
        chipGroupFilter = findViewById(R.id.chipGroupFilter);
        FloatingActionButton fabStatistics = findViewById(R.id.fabStatistics);
        fabStatistics.setOnClickListener(view ->
                startActivity(new Intent(MainActivity.this, StatisticsActivity.class))
        );

        Button btnOpenGlossary = findViewById(R.id.btnOpenGlossary);
        btnOpenGlossary.setOnClickListener(view ->
                startActivity(new Intent(MainActivity.this, GlossaryActivity.class))
        );

        Button btnOpenDictionary = findViewById(R.id.btnOpenDictionary);
        btnOpenDictionary.setOnClickListener(view ->
                startActivity(new Intent(MainActivity.this, DictionaryActivity.class))
        );

        // CORREÇÃO 1: Inicializar o adapter com uma lista vazia para nunca ser nulo.
        challengeAdapter = new ChallengeAdapter(this, new ArrayList<>());
        recyclerViewChallenges.setAdapter(challengeAdapter);
        challengeAdapter.setOnChallengeClickListener(this);
    }

    private void setupFilterChips() {
        chipGroupFilter.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == View.NO_ID) {
                if (challengeAdapter != null) { // Verificação de segurança
                    challengeAdapter.updateChallenges(allChallenges);
                }
                return;
            }
            Chip selectedChip = findViewById(checkedId);
            if (selectedChip != null) {
                filterChallenges(selectedChip.getText().toString());
            }
        });
    }

    private void filterChallenges(String level) {
        if (allChallenges == null || challengeAdapter == null) return;

        if (level.equalsIgnoreCase(getString(R.string.filter_all))) {
            challengeAdapter.updateChallenges(allChallenges);
        } else {
            List<Challenge> filteredList = allChallenges.stream()
                    .filter(c -> c.getLevel().equalsIgnoreCase(level))
                    .collect(Collectors.toList());
            challengeAdapter.updateChallenges(filteredList);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isDarkModeActive != appPreferences.isDarkModeEnabled()) {
            recreate();
            return;
        }

        loadChallenges();
        if (chipGroupFilter.getCheckedChipId() == View.NO_ID) {
            chipGroupFilter.check(R.id.chipAll);
        }
    }

    private void loadChallenges() {
        challengeDao.getAll().observe(this, challenges -> {
            if (challenges == null) return;

            userProgressDao.getAllProgress().observe(this, progressList -> {
                if (progressList == null) return;

                for (Challenge challenge : challenges) {
                    UserProgress progress = progressList.stream()
                            .filter(p -> p.getChallengeId() == challenge.getId())
                            .findFirst()
                            .orElse(null);
                    challenge.setCompleted(progress != null && progress.isCompleted());
                    challenge.setCorrect(progress != null && progress.isCorrect());
                }

                challenges.sort((c1, c2) -> Integer.compare(getLevelOrder(c1.getLevel()), getLevelOrder(c2.getLevel())));
                allChallenges = new ArrayList<>(challenges);

                // CORREÇÃO 2: Apenas atualizar a lista de desafios no adapter existente
                // e aplicar o filtro atual.
                Chip selectedChip = findViewById(chipGroupFilter.getCheckedChipId());
                String filter = (selectedChip != null) ? selectedChip.getText().toString() : getString(R.string.filter_all);
                filterChallenges(filter);
            });
        });
    }

    private int getLevelOrder(String level) {
        if (level == null) return 99;
        if (level.equalsIgnoreCase(getString(R.string.level_basic))) return 1;
        if (level.equalsIgnoreCase(getString(R.string.level_intermediate))) return 2;
        if (level.equalsIgnoreCase(getString(R.string.level_advanced))) return 3;
        return 99;
    }

    @Override
    public void onChallengeClick(Challenge challenge) {
        appPreferences.setLastAccessedChallengeId(challenge.getId());
        Intent intent = new Intent(MainActivity.this, ChallengeActivity.class);
        intent.putExtra("challenge", challenge);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_settings) {
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            return true;
        } else if (itemId == R.id.action_logout) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.challenge_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        int position = challengeAdapter.getLongClickedPosition();
        if (position < 0) return super.onContextItemSelected(item);

        Challenge selectedChallenge = challengeAdapter.getChallengeAtPosition(position);
        if (selectedChallenge == null) return super.onContextItemSelected(item);

        if (item.getItemId() == R.id.context_reset_progress) {
            AppDatabase.databaseWriteExecutor.execute(() -> {
                userProgressDao.deleteProgressById(selectedChallenge.getId());
                runOnUiThread(() -> {
                    Snackbar.make(recyclerViewChallenges, getString(R.string.progress_reset), Snackbar.LENGTH_SHORT).show();
                });
            });
            return true;
        }
        return super.onContextItemSelected(item);
    }
}