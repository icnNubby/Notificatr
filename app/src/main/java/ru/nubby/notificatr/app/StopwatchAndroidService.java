package ru.nubby.notificatr.app;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.threeten.bp.Duration;
import org.threeten.bp.LocalDateTime;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicReference;

import androidx.annotation.Nullable;
import ru.nubby.notificatr.app.utils.NotificationHelper;
import ru.nubby.notificatr.app.utils.PrintUtil;
import ru.nubby.notificatr.core.events.StopwatchPaused;
import ru.nubby.notificatr.core.events.StopwatchStarted;
import ru.nubby.notificatr.core.events.StopwatchWasReset;
import ru.nubby.notificatr.core.manager.StopwatchManager;
import ru.nubby.notificatr.core.model.Stopwatch;
import ru.nubby.notificatr.core.service.StopwatchService;
import ru.nubby.notificatr.core.service.StopwatchServiceImpl;

import static ru.nubby.notificatr.app.utils.NotificationHelper.NOTIFICATION_ID;

public class StopwatchAndroidService extends Service implements StopwatchService, StopwatchManager {
    private static final String TAG = StopwatchAndroidService.class.getSimpleName();

    private final AtomicReference<Stopwatch> mStopwatchAtomicReference = new AtomicReference<>();
    private final StopwatchService mStopwatchService = new StopwatchServiceImpl();
    private final IBinder mBinder = new LocalBinder();
    private final EventBus mEventBus = EventBus.getDefault();
    private Notification mNotification = null;
    private Timer mTimer;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        mStopwatchAtomicReference.set(mStopwatchService.create());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        if (mTimer != null) {
            mTimer.cancel();
        }
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
        mNotification = (mNotification == null) ?
                NotificationHelper.buildStopwatchNotification(getApplicationContext(),
                        PrintUtil.durationToString(
                                mStopwatchService.timeElapsed(newStopwatch, LocalDateTime.now()))) :
                mNotification;
        startForeground(NOTIFICATION_ID, mNotification);

        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                NotificationManager manager = (NotificationManager)
                        getSystemService(NOTIFICATION_SERVICE);

                String duration =
                        PrintUtil.durationToString(
                                mStopwatchService.timeElapsed(mStopwatchAtomicReference.get(),
                                        LocalDateTime.now()));

                Notification notification = NotificationHelper.buildStopwatchNotification(
                        getApplicationContext(), duration);

                manager.notify(NOTIFICATION_ID, notification);

            }
        }, 0, 1000);

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

        if (mTimer  != null) {
            mTimer.cancel();
        }

        stopForeground(true);

        return newStopwatch;
    }

    @Override
    public Duration timeElapsed(Stopwatch stopwatch, LocalDateTime now) {
        return mStopwatchService.timeElapsed(stopwatch, now);
    }



    public class LocalBinder extends Binder {
        public StopwatchAndroidService getService() {
            return StopwatchAndroidService.this;
        }
    }

}
