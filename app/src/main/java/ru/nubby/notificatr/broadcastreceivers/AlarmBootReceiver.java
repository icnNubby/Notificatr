package ru.nubby.notificatr.broadcastreceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import ru.nubby.notificatr.DefaultPreferences;
import ru.nubby.notificatr.NotificationHelper;

public class AlarmBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        DefaultPreferences defaultPreferences = new DefaultPreferences(context);
        String action = intent.getAction();
        if (action != null) {
            if (action.equals("android.intent.action.BOOT_COMPLETED") ||
                    action.equals("android.intent.action.QUICKBOOT_POWERON") ||
                    action.equals("com.htc.intent.action.QUICKBOOT_POWERON") ||
                    action.equals("android.intent.action.REBOOT")) {

                if (defaultPreferences.getIsOn()) {
                    NotificationHelper.scheduleRepeatingRTCNotification(context);
                } else {
                    NotificationHelper.disableBootReceiver(context);
                    NotificationHelper.cancelAlarmRTC();
                }
            }
        }
    }
}