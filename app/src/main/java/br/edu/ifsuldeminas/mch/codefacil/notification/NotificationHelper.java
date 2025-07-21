package br.edu.ifsuldeminas.mch.codefacil.notification;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import br.edu.ifsuldeminas.mch.codefacil.MainActivity;
import br.edu.ifsuldeminas.mch.codefacil.R;

import java.util.Calendar;

/**
 * Classe utilitária para gerenciar canais de notificação e agendamento de alarmes.
 */
public class NotificationHelper {

    public static final String CHANNEL_ID = "codefacil_channel";
    private static final String CHANNEL_NAME = "CodeFácil Notificações";
    private static final String CHANNEL_DESCRIPTION = "Notificações diárias de desafios de lógica";

    /**
     * Cria o canal de notificação para o aplicativo.
     * Deve ser chamado ao iniciar o aplicativo.
     * @param context O contexto da aplicação.
     */
    public static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription(CHANNEL_DESCRIPTION);

            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
                Log.d("NotificationHelper", "Canal de notificação criado.");
            }
        }
    }

    /**
     * Agenda uma notificação diária às 19:15.
     * @param context O contexto da aplicação.
     */
    public static void scheduleDailyNotification(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Define o horário para 19:15
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 19); // 19h
        calendar.set(Calendar.MINUTE, 15);      // 15 minutos
        calendar.set(Calendar.SECOND, 0);

        // Se o horário já passou para hoje, agende para amanhã
        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        if (alarmManager != null) {
            // Agendamento repetitivo diário
            alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP, // Acorda o dispositivo se estiver dormindo
                    calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY, // Repete a cada dia
                    pendingIntent
            );
            Log.d("NotificationHelper", "Notificação diária agendada para: " + calendar.getTime().toString());
        }
    }

    /**
     * Cancela a notificação diária agendada.
     * @param context O contexto da aplicação.
     */
    public static void cancelDailyNotification(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
            Log.d("NotificationHelper", "Notificação diária cancelada.");
        }
    }
}