package br.edu.ifsuldeminas.mch.codefacil;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import br.edu.ifsuldeminas.mch.codefacil.notification.NotificationHelper;
import br.edu.ifsuldeminas.mch.codefacil.utils.AppPreferences;

/**
 * SettingsActivity:
 * Permite ao usuário configurar o modo escuro e as preferências de notificação.
 * Utiliza SharedPreferences para persistir as configurações.
 */
public class SettingsActivity extends AppCompatActivity {

    private Switch switchDarkMode;
    private Switch switchNotifications;
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

        // Configura a AppBar (Toolbar)
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Botão de voltar
            getSupportActionBar().setTitle("Configurações");
        }

        switchDarkMode = findViewById(R.id.switchDarkMode);
        switchNotifications = findViewById(R.id.switchNotifications);

        // Carrega as preferências salvas
        switchDarkMode.setChecked(appPreferences.isDarkModeEnabled());
        switchNotifications.setChecked(appPreferences.areNotificationsEnabled());

        // Listener para o switch do modo escuro
        switchDarkMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                appPreferences.setDarkMode(isChecked);
                // Recria a atividade para aplicar o novo tema imediatamente
                recreate();
            }
        });

        // Listener para o switch de notificações
        switchNotifications.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                appPreferences.setNotificationsEnabled(isChecked);
                if (isChecked) {
                    NotificationHelper.scheduleDailyNotification(SettingsActivity.this);
                    Toast.makeText(SettingsActivity.this, "Notificações diárias ativadas!", Toast.LENGTH_SHORT).show();
                } else {
                    NotificationHelper.cancelDailyNotification(SettingsActivity.this);
                    Toast.makeText(SettingsActivity.this, "Notificações diárias desativadas!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed(); // Lida com o botão de voltar na AppBar
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
