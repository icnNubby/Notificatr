package ru.nubby.notificatr.app.broadcastreceivers;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import ru.nubby.notificatr.app.store.DefaultPreferences;
import ru.nubby.notificatr.app.utils.NotificationHelper;

public class AlarmReceiver extends BroadcastReceiver {

    DefaultPreferences mDefaultPreferences;

    @Override
    public void onReceive(Context context, Intent intent) {
        mDefaultPreferences = new DefaultPreferences(context);

        NotificationHelper.cancelAlarmRTC();
        if (mDefaultPreferences.getIsOn()) {
            Notification repeatedNotification = NotificationHelper.buildAlarmNotification(context);
            NotificationHelper.getNotificationManager(context)
                    .notify(NotificationHelper.ALARM_TYPE_RTC, repeatedNotification);
            NotificationHelper.scheduleRepeatingRTCNotification(context);
        } else {
            NotificationHelper.disableBootReceiver(context);
        }
    }


}