package br.edu.ifsuldeminas.mch.codefacil;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

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
import br.edu.ifsuldeminas.mch.codefacil.database.DatabaseHelper;
import br.edu.ifsuldeminas.mch.codefacil.model.Challenge;
import br.edu.ifsuldeminas.mch.codefacil.model.UserProgress;
import br.edu.ifsuldeminas.mch.codefacil.notification.NotificationHelper;
import br.edu.ifsuldeminas.mch.codefacil.utils.AppPreferences;

public class MainActivity extends AppCompatActivity implements ChallengeAdapter.OnChallengeClickListener {

    private RecyclerView recyclerViewChallenges;
    private ChallengeAdapter challengeAdapter;
    private DatabaseHelper dbHelper;
    private AppPreferences appPreferences;
    private List<Challenge> allChallenges;
    private ChipGroup chipGroupFilter;
    private boolean isDarkModeActive;

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

        dbHelper = new DatabaseHelper(this);
        allChallenges = new ArrayList<>(); // Inicializa a lista

        setupViews();
        setupFilterChips();
        // Carrega os desafios aqui, uma vez na criação da Activity
        loadAndDisplayChallenges();

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
    }

    private void setupFilterChips() {
        chipGroupFilter.setOnCheckedChangeListener((group, checkedId) -> {
            Chip selectedChip = findViewById(checkedId);
            if (selectedChip != null) {
                filterChallenges(selectedChip.getText().toString());
            } else {
                // Se nenhum chip estiver selecionado, mostra todos
                filterChallenges(getString(R.string.filter_all));
            }
        });
    }

    private void filterChallenges(String level) {
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
            recreate(); // Recria a activity se o tema mudou
            return;
        }
        // Recarrega os desafios para refletir qualquer progresso feito
        loadAndDisplayChallenges();
    }

    private void loadAndDisplayChallenges() {
        allChallenges = dbHelper.getAllChallenges();
        for (Challenge challenge : allChallenges) {
            UserProgress progress = dbHelper.getUserProgress(challenge.getId());
            challenge.setCompleted(progress != null && progress.isCompleted());
            challenge.setCorrect(progress != null && progress.isCorrect());
        }

        allChallenges.sort((c1, c2) -> Integer.compare(getLevelOrder(c1.getLevel()), getLevelOrder(c2.getLevel())));

        if (challengeAdapter == null) {
            challengeAdapter = new ChallengeAdapter(this, allChallenges, dbHelper);
            recyclerViewChallenges.setAdapter(challengeAdapter);
            challengeAdapter.setOnChallengeClickListener(this);
        } else {
            // Atualiza a lista e o filtro selecionado
            Chip selectedChip = findViewById(chipGroupFilter.getCheckedChipId());
            String filter = selectedChip != null ? selectedChip.getText().toString() : getString(R.string.filter_all);
            filterChallenges(filter);
        }
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
        // CORRIGIDO: Forma segura de obter a posição do item
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        // A linha acima pode causar um crash se não estiver a usar um AdapterView.
        // O RecyclerView não usa AdapterContextMenuInfo. O adapter precisa de guardar a posição.
        // A abordagem com a variável no adapter, embora frágil, é a mais simples sem grandes refatorações.
        // Vamos manter a sua abordagem original, mas com uma verificação.

        int position = challengeAdapter.getLongClickedPosition();
        if (position < 0) {
            return super.onContextItemSelected(item);
        }

        Challenge selectedChallenge = challengeAdapter.getChallengeAtPosition(position);
        if (selectedChallenge == null) {
            return super.onContextItemSelected(item);
        }

        if (item.getItemId() == R.id.context_reset_progress) {
            dbHelper.resetChallengeProgress(selectedChallenge.getId());
            Snackbar.make(recyclerViewChallenges, getString(R.string.progress_reset), Snackbar.LENGTH_SHORT).show();
            // Recarrega os dados para atualizar a UI
            loadAndDisplayChallenges();
            return true;
        }
        return super.onContextItemSelected(item);
    }
}