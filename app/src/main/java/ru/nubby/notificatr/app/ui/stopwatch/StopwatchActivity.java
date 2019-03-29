package ru.nubby.notificatr.app.ui.stopwatch;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import ru.nubby.notificatr.R;
import ru.nubby.notificatr.app.NotificatrApp;
import ru.nubby.notificatr.app.StopwatchAndroidService;
import ru.nubby.notificatr.app.utils.NotificationHelper;

import static ru.nubby.notificatr.app.utils.NotificationHelper.NOTIFICATION_ID;

public class StopwatchActivity extends AppCompatActivity implements StopwatchContract.View {

    private static final long UPDATE_DELAY = 100;
    private StopwatchContract.Presenter mPresenter;

    private TextView mStopwatchText;
    private Button mStartButton;
    private Button mResetButton;
    private Button mPauseButton;

    private View mStartResetContainer;
    private View mPauseContainer;
    private Timer mTimer;

    private StopwatchAndroidService mStopwatchAndroidService;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stopwatch);
        mStartButton = findViewById(R.id.stopwatch_start);
        mPauseButton = findViewById(R.id.stopwatch_pause);
        mResetButton = findViewById(R.id.stopwatch_reset);
        mStopwatchText = findViewById(R.id.stopwatch_time);
        mStartResetContainer = findViewById(R.id.stopwatch_start_reset_container);
        mPauseContainer = findViewById(R.id.stopwatch_pause_container);

        mTimer = new Timer();

        mStartButton.setOnClickListener(v -> mPresenter.onStopwatchStartClicked());
        mPauseButton.setOnClickListener(v -> mPresenter.onStopwatchPauseClicked());
        mResetButton.setOnClickListener(v -> mPresenter.onStopwatchResetClicked());
        mStopwatchAndroidService = NotificatrApp.getInstance().getStopwatchAndroidService();


        setPresenter(new StopwatchPresenter(this,
                EventBus.getDefault(),
                mStopwatchAndroidService,
                mStopwatchAndroidService));
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.onResume();
        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> {
                    mStopwatchText.setText(mPresenter.getStopwatchTimeElapsed());

                });
            }
        }, 0, UPDATE_DELAY);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPresenter.onPause();
        mTimer.cancel();
    }

    @Override
    public void showStartResetButtons() {
        mStartResetContainer.setVisibility(View.VISIBLE);
        mPauseContainer.setVisibility(View.GONE);
    }

    @Override
    public void showPauseButton() {
        mPauseContainer.setVisibility(View.VISIBLE);
        mStartResetContainer.setVisibility(View.GONE);
    }

    @Override
    public void setPresenter(StopwatchContract.Presenter presenter) {
        mPresenter = presenter;
    }

}
