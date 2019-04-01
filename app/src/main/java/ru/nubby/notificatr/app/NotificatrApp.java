package ru.nubby.notificatr.app;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;

import com.jakewharton.threetenabp.AndroidThreeTen;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;
import ru.nubby.notificatr.R;
import ru.nubby.notificatr.app.store.DefaultPreferences;
import ru.nubby.notificatr.app.utils.NotificationHelper;

public class NotificatrApp extends Application implements LifecycleObserver {

    private static final String TAG = NotificatrApp.class.getSimpleName();
    private static NotificatrApp sNotificatrApp;

    public static final String NOTIFICATION_CHANNEL_STOPWATCH_ID = "stopwatch_channel";
    public static final String NOTIFICATION_CHANNEL_ALARM_ID = "alarm_channel";

    private StopwatchAndroidService mStopwatchAndroidService;


    public static NotificatrApp getInstance() {
        return sNotificatrApp;
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            StopwatchAndroidService.LocalBinder binder = ((StopwatchAndroidService.LocalBinder) service);
            mStopwatchAndroidService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mStopwatchAndroidService = null;
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        AndroidThreeTen.init(this);
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
        initNotificationChannels();
        sNotificatrApp = this;
        bindService(new Intent(getApplicationContext(), StopwatchAndroidService.class),
                mServiceConnection
                , BIND_AUTO_CREATE);

    }

    public StopwatchAndroidService getStopwatchAndroidService() {
        return mStopwatchAndroidService;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    protected void appBackgrounded() {
        NotificationHelper.disableBootReceiver(this);
        NotificationHelper.cancelAlarmRTC();
        DefaultPreferences defaultPreferences = new DefaultPreferences(this);
        if (defaultPreferences.getIsOn()) {
            NotificationHelper.enableBootReceiver(this);
            NotificationHelper.scheduleRepeatingRTCNotification(this);
        }
    }

    private void initNotificationChannels() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return;
        }
        NotificationManager manager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannel mChannelStopwatch = new NotificationChannel(
                NOTIFICATION_CHANNEL_STOPWATCH_ID,
                getResources().getString(R.string.stopwatch_notification_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT);

        NotificationChannel mChannelAlarm = new NotificationChannel(
                NOTIFICATION_CHANNEL_ALARM_ID,
                getResources().getString(R.string.alarm_notification_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT);

        manager.createNotificationChannel(mChannelStopwatch);
        manager.createNotificationChannel(mChannelAlarm);
    }
}
