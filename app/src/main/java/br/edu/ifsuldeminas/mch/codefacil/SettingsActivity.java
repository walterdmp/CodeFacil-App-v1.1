package br.edu.ifsuldeminas.mch.codefacil;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.switchmaterial.SwitchMaterial;

import br.edu.ifsuldeminas.mch.codefacil.notification.NotificationHelper;
import br.edu.ifsuldeminas.mch.codefacil.utils.AppPreferences;

public class SettingsActivity extends AppCompatActivity {

    // CORREÇÃO: Alterado de 'Switch' para 'SwitchMaterial'
    private SwitchMaterial switchDarkMode;
    private SwitchMaterial switchNotifications;
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
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Configurações");
        }

        switchDarkMode = findViewById(R.id.switchDarkMode);
        switchNotifications = findViewById(R.id.switchNotifications);

        switchDarkMode.setChecked(appPreferences.isDarkModeEnabled());
        switchNotifications.setChecked(appPreferences.areNotificationsEnabled());

        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            appPreferences.setDarkMode(isChecked);
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });

        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            appPreferences.setNotificationsEnabled(isChecked);
            if (isChecked) {
                NotificationHelper.scheduleDailyNotification(SettingsActivity.this);
                Toast.makeText(SettingsActivity.this, "Notificações diárias ativadas!", Toast.LENGTH_SHORT).show();
            } else {
                NotificationHelper.cancelDailyNotification(SettingsActivity.this);
                Toast.makeText(SettingsActivity.this, "Notificações diárias desativadas!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Usa o comportamento padrão de voltar, que irá chamar o onResume da MainActivity
            super.onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}