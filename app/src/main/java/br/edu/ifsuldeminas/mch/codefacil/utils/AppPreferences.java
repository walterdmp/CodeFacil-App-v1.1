package br.edu.ifsuldeminas.mch.codefacil.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

/**
 * Classe utilitária para gerenciar as preferências do aplicativo usando SharedPreferences.
 */
public class AppPreferences {

    private static final String PREF_NAME = "CodeFacilPrefs";
    private static final String KEY_DARK_MODE = "darkMode";
    private static final String KEY_NOTIFICATIONS_ENABLED = "notificationsEnabled";
    private static final String KEY_LAST_ACCESSED_CHALLENGE_ID = "lastAccessedChallengeId";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Context context; // Contexto para aceder aos recursos do sistema

    public AppPreferences(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    /**
     * Define o estado do modo escuro.
     * @param isEnabled true para modo escuro ativado, false caso contrário.
     */
    public void setDarkMode(boolean isEnabled) {
        editor.putBoolean(KEY_DARK_MODE, isEnabled);
        editor.apply();
    }

    /**
     * Retorna o estado atual do modo escuro.
     * Se o utilizador nunca definiu uma preferência, o tema do sistema é utilizado como padrão.
     * @return true se o modo escuro estiver ativado, false caso contrário.
     */
    public boolean isDarkModeEnabled() {
        // Verifica se o utilizador já fez uma escolha de tema.
        if (!sharedPreferences.contains(KEY_DARK_MODE)) {
            // Se não houver preferência salva, usa o tema do sistema.
            int nightModeFlags = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
            return nightModeFlags == Configuration.UI_MODE_NIGHT_YES;
        }
        // Se já houver uma preferência, retorna o valor salvo.
        return sharedPreferences.getBoolean(KEY_DARK_MODE, false);
    }

    /**
     * Define se as notificações estão ativadas.
     * @param isEnabled true para notificações ativadas, false caso contrário.
     */
    public void setNotificationsEnabled(boolean isEnabled) {
        editor.putBoolean(KEY_NOTIFICATIONS_ENABLED, isEnabled);
        editor.apply();
    }

    /**
     * Retorna se as notificações estão ativadas.
     * @return true se as notificações estiverem ativadas, false caso contrário (padrão é true).
     */
    public boolean areNotificationsEnabled() {
        return sharedPreferences.getBoolean(KEY_NOTIFICATIONS_ENABLED, true); // Padrão é true conforme doc
    }

    /**
     * Define o ID do último desafio acessado.
     * @param challengeId O ID do desafio.
     */
    public void setLastAccessedChallengeId(long challengeId) {
        editor.putLong(KEY_LAST_ACCESSED_CHALLENGE_ID, challengeId);
        editor.apply();
    }

    /**
     * Retorna o ID do último desafio acessado.
     * @return O ID do último desafio acessado, ou -1 se nenhum foi acessado.
     */
    public long getLastAccessedChallengeId() {
        return sharedPreferences.getLong(KEY_LAST_ACCESSED_CHALLENGE_ID, -1);
    }
}