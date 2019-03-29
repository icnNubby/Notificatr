package ru.nubby.notificatr.app.utils;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;

import org.threeten.bp.LocalDateTime;

import java.util.Calendar;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import ru.nubby.notificatr.R;
import ru.nubby.notificatr.app.NotificatrApp;
import ru.nubby.notificatr.app.StopwatchAndroidService;
import ru.nubby.notificatr.app.broadcastreceivers.AlarmBootReceiver;
import ru.nubby.notificatr.app.broadcastreceivers.AlarmReceiver;
import ru.nubby.notificatr.app.prefsutils.TimePreference;
import ru.nubby.notificatr.app.store.DefaultPreferences;
import ru.nubby.notificatr.app.ui.preferences.MainActivity;
import ru.nubby.notificatr.app.ui.stopwatch.StopwatchActivity;
import ru.nubby.notificatr.core.model.Stopwatch;
import ru.nubby.notificatr.core.service.StopwatchService;

import static android.content.Context.ALARM_SERVICE;

public class NotificationHelper {

    private static final String TAG = NotificationHelper.class.getSimpleName();

    public static int ALARM_TYPE_RTC = 100;
    public static int NOTIFICATION_ID = 111;
    private static AlarmManager alarmManagerRTC;
    private static PendingIntent alarmIntentRTC;



    /**
     * This is the real time /wall clock time
     *
     * @param context
     */
    public static void scheduleRepeatingRTCNotification(Context context) {

        Calendar nextTimeCalendar = getNextWakeUpTime(context);


        Intent intent = new Intent(context, AlarmReceiver.class);
        alarmIntentRTC = PendingIntent.getBroadcast(context, ALARM_TYPE_RTC, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManagerRTC = (AlarmManager) context.getSystemService(ALARM_SERVICE);

        if (nextTimeCalendar != null) {
            alarmManagerRTC.setExact(AlarmManager.RTC_WAKEUP,
                    nextTimeCalendar.getTimeInMillis(), alarmIntentRTC);
        }
    }

    public static void cancelAlarmRTC() {
        if (alarmManagerRTC != null) {
            alarmManagerRTC.cancel(alarmIntentRTC);
        }
    }


    public static NotificationManager getNotificationManager(Context context) {
        return (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    /**
     * Enable boot receiver to persist alarms set for notifications across device reboots
     */
    public static void enableBootReceiver(Context context) {
        ComponentName receiver = new ComponentName(context, AlarmBootReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    /**
     * Disable boot receiver when user cancels/opt-out from notifications
     */
    public static void disableBootReceiver(Context context) {
        ComponentName receiver = new ComponentName(context, AlarmBootReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }

    @Nullable
    private static Calendar getNextWakeUpTime(Context context) {
        DefaultPreferences mDefaultPreferences = new DefaultPreferences(context);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        int hourStart = TimePreference.parseHour(mDefaultPreferences.getStartTime());
        int minStart = TimePreference.parseMinute(mDefaultPreferences.getStartTime());

        int hourEnd = TimePreference.parseHour(mDefaultPreferences.getFinishTime());
        int minEnd = TimePreference.parseMinute(mDefaultPreferences.getFinishTime());

        if (hourStart == hourEnd && minStart == minEnd && minStart != 0) {
            return null;
        }

        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);

        if ((hourStart <= currentHour && hourEnd > currentHour) ||
                ((hourEnd < hourStart) && (hourStart <= currentHour || currentHour < hourEnd)))  {
            calendar.add(Calendar.HOUR_OF_DAY, 1);
            calendar.set(Calendar.MINUTE, 0);
        } else {
            if (minStart > 0) {
                calendar.add(Calendar.DAY_OF_YEAR, 1);
                calendar.set(Calendar.HOUR_OF_DAY, hourStart + 1);
                calendar.set(Calendar.MINUTE, 0);
            } else {
                calendar.add(Calendar.DAY_OF_YEAR, 1);
                calendar.set(Calendar.HOUR_OF_DAY, hourStart);
                calendar.set(Calendar.MINUTE, 0);
            }
        }


        Log.d(TAG,"Next fire value = " + calendar.toString());
        return calendar;
    }

    public static Notification buildAlarmNotification(Context context) {
        DefaultPreferences mDefaultPreferences = new DefaultPreferences(context);

        Intent intentToRepeat = new Intent(context, MainActivity.class);
        intentToRepeat.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent =
                PendingIntent.getActivity(context, ALARM_TYPE_RTC,
                        intentToRepeat, PendingIntent.FLAG_UPDATE_CURRENT);

        Uri soundUri = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.quite_impressed);

        return new NotificationCompat.Builder(context,
                NotificatrApp.NOTIFICATION_CHANNEL_ALARM_ID)
                .setContentIntent(pendingIntent)
                .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
                .setContentTitle(mDefaultPreferences.getText())
                .setContentInfo(mDefaultPreferences.getText())
                .setAutoCancel(true)
                .setSound(soundUri)
                .setVibrate(new long[]{500,1000})
                .setLights(0xFF0000FF,100,3000)
                .build();

    }
    public static Notification buildStopwatchNotification(Context context,
                                                          String timeElapsed) {

        Uri soundUri = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.quite_impressed);

        DefaultPreferences mDefaultPreferences = new DefaultPreferences(context);
        Intent intent = new Intent(context, StopwatchActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        return new NotificationCompat.Builder(context,
                NotificatrApp.NOTIFICATION_CHANNEL_STOPWATCH_ID)
                .setContentTitle(mDefaultPreferences.getText())
                .setContentText(timeElapsed)
                .setContentIntent(pendingIntent)
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setSound(soundUri)
                .setVibrate(new long[]{500,1000})
                .setLights(0xFF0000FF,100,3000)
                .setAutoCancel(true)
                .setOnlyAlertOnce(true)
                .build();
    }
}
