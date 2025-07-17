package br.edu.ifsuldeminas.mch.codefacil.notification;

import androidx.core.app.NotificationCompat;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import br.edu.ifsuldeminas.mch.codefacil.MainActivity;
import br.edu.ifsuldeminas.mch.codefacil.R;
import br.edu.ifsuldeminas.mch.codefacil.utils.AppPreferences;

/**
 * BroadcastReceiver para lidar com o alarme de notificação diária e reinício do dispositivo.
 */
public class AlarmReceiver extends BroadcastReceiver {

    private static final int NOTIFICATION_ID = 1;

    @Override
    public void onReceive(Context context, Intent intent) {
        // Verifica se as notificações estão ativadas nas preferências
        AppPreferences appPreferences = new AppPreferences(context);
        if (!appPreferences.areNotificationsEnabled()) {
            Log.d("AlarmReceiver", "Notificações desativadas, não exibindo notificação.");
            return;
        }

        String action = intent.getAction();
        if (action != null && action.equals(Intent.ACTION_BOOT_COMPLETED)) {
            // Reagenda a notificação se o dispositivo for reiniciado
            Log.d("AlarmReceiver", "Dispositivo reiniciado. Reagendando notificação diária.");
            NotificationHelper.scheduleDailyNotification(context);
        } else {
            // Exibe a notificação diária
            Log.d("AlarmReceiver", "Alarme recebido. Exibindo notificação diária.");
            showNotification(context);
        }
    }

    /**
     * Exibe a notificação diária.
     * @param context O contexto da aplicação.
     */
    private void showNotification(Context context) {
        Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NotificationHelper.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification) // Certifique-se de ter um ícone ic_notification.xml em drawable
                .setContentTitle("CodeFácil")
                .setContentText("Seu novo desafio de lógica está disponível! Toque para continuar sua evolução no CodeFácil.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true); // A notificação desaparece ao ser tocada

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        }
    }
}
