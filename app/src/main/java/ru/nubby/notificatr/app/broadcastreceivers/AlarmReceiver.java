package ru.nubby.notificatr.app.broadcastreceivers;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import ru.nubby.notificatr.R;
import ru.nubby.notificatr.app.store.DefaultPreferences;
import ru.nubby.notificatr.app.ui.preferences.MainActivity;
import ru.nubby.notificatr.app.utils.NotificationHelper;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String NOTIFICATION_CHANNEL_ID = "alarm_notificatr_channel_id";

    DefaultPreferences mDefaultPreferences;

    @Override
    public void onReceive(Context context, Intent intent) {
        mDefaultPreferences = new DefaultPreferences(context);
        //Intent для вызова приложения по клику.
        //Мы хотим запустить наше приложение (главную активность) при клике на уведомлении

        NotificationHelper.cancelAlarmRTC();
        if (mDefaultPreferences.getIsOn()) {
            //Создаём уведомление
            Notification repeatedNotification = buildLocalNotification(context).build();
            //Отправляем уведомление
            NotificationHelper.getNotificationManager(context)
                    .notify(NotificationHelper.ALARM_TYPE_RTC, repeatedNotification);
            NotificationHelper.scheduleRepeatingRTCNotification(context);
        } else {
            NotificationHelper.disableBootReceiver(context);
        }
    }

    public NotificationCompat.Builder buildLocalNotification(Context context) {

        Intent intentToRepeat = new Intent(context, MainActivity.class);
        //настроим флаг для перезапуска приложения
        intentToRepeat.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent =
                PendingIntent.getActivity(context, NotificationHelper.ALARM_TYPE_RTC,
                        intentToRepeat, PendingIntent.FLAG_UPDATE_CURRENT);

        Uri soundUri = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.quite_impressed);

        NotificationCompat.Builder notification = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setContentIntent(pendingIntent)
                .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
                .setContentTitle(mDefaultPreferences.getText())
                .setContentInfo(mDefaultPreferences.getText())
                .setAutoCancel(true)
                .setSound(soundUri)
                .setVibrate(new long[]{500,1000})
                .setLights(0xFF0000FF,100,3000);

        return notification;
    }
}