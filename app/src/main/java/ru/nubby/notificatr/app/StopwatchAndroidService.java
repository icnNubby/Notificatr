package ru.nubby.notificatr.app;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.threeten.bp.Duration;
import org.threeten.bp.LocalDateTime;

import java.util.concurrent.atomic.AtomicReference;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import ru.nubby.notificatr.R;
import ru.nubby.notificatr.app.store.DefaultPreferences;
import ru.nubby.notificatr.app.ui.stopwatch.StopwatchActivity;
import ru.nubby.notificatr.app.utils.PrintUtil;
import ru.nubby.notificatr.core.events.StopwatchPaused;
import ru.nubby.notificatr.core.events.StopwatchStarted;
import ru.nubby.notificatr.core.events.StopwatchWasReset;
import ru.nubby.notificatr.core.manager.StopwatchManager;
import ru.nubby.notificatr.core.model.Stopwatch;
import ru.nubby.notificatr.core.service.StopwatchService;
import ru.nubby.notificatr.core.service.StopwatchServiceImpl;

public class StopwatchAndroidService extends Service implements StopwatchService, StopwatchManager {
    private static final String TAG = StopwatchAndroidService.class.getSimpleName();

    private final int NOTIFICATION_ID = 111;
    private final AtomicReference<Stopwatch> mStopwatchAtomicReference = new AtomicReference<>();
    private final StopwatchService mStopwatchService = new StopwatchServiceImpl();
    private DefaultPreferences mDefaultPreferences;
    private final IBinder mBinder = new LocalBinder();
    private final EventBus mEventBus = EventBus.getDefault();
    private Notification mNotification = null;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        mDefaultPreferences = new DefaultPreferences(this);
        mStopwatchAtomicReference.set(mStopwatchService.create());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        return START_STICKY;
    }

    @Override
    public Stopwatch getStopwatch() {
        return mStopwatchAtomicReference.get();
    }

    @Override
    public Stopwatch create() {
        return mStopwatchService.create();
    }

    @Override
    public Stopwatch start(Stopwatch stopwatch, LocalDateTime startedAt) {
        Log.d(TAG, "start");

        Stopwatch newStopwatch = mStopwatchService.start(stopwatch, startedAt);
        mStopwatchAtomicReference.set(newStopwatch);
        mEventBus.post(new StopwatchStarted(newStopwatch));
        mNotification = (mNotification == null) ? createNotification(newStopwatch): mNotification;
        startForeground(NOTIFICATION_ID, mNotification);

        return newStopwatch;
    }

    @Override
    public Stopwatch pause(Stopwatch stopwatch, LocalDateTime pausedAt) {
        Log.d(TAG, "pause");

        Stopwatch newStopwatch = mStopwatchService.pause(stopwatch, pausedAt);
        mStopwatchAtomicReference.set(newStopwatch);
        mEventBus.post(new StopwatchPaused(newStopwatch));

        return newStopwatch;
    }

    @Override
    public Stopwatch reset(Stopwatch stopwatch) {
        Log.d(TAG, "reset");

        Stopwatch newStopwatch = mStopwatchService.reset(stopwatch);
        mStopwatchAtomicReference.set(newStopwatch);
        mEventBus.post(new StopwatchWasReset(newStopwatch));

        stopForeground(true);

        return newStopwatch;
    }

    @Override
    public Duration timeElapsed(Stopwatch stopwatch, LocalDateTime now) {
        return mStopwatchService.timeElapsed(stopwatch, now);
    }

    private Notification createNotification(Stopwatch stopwatch) {
        Intent intent = new Intent(getApplicationContext(), StopwatchActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        String timeElapsedString = PrintUtil.durationToString(
                mStopwatchService.timeElapsed(stopwatch, LocalDateTime.now()));
        return new NotificationCompat.Builder(this, NotificatrApp.NOTIFICATION_CHANNEL_STOPWATCH_ID)
                .setContentTitle(mDefaultPreferences.getText())
                .setContentText(timeElapsedString)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .build();
    }

    public class LocalBinder extends Binder {
        public StopwatchAndroidService getService() {
            return StopwatchAndroidService.this;
        }
    }

}
