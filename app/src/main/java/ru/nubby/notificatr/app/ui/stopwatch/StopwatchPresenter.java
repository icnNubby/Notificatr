package ru.nubby.notificatr.app.ui.stopwatch;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.threeten.bp.LocalDateTime;

import ru.nubby.notificatr.app.utils.PrintUtil;
import ru.nubby.notificatr.core.events.StopwatchPaused;
import ru.nubby.notificatr.core.events.StopwatchStarted;
import ru.nubby.notificatr.core.events.StopwatchWasReset;
import ru.nubby.notificatr.core.manager.StopwatchManager;
import ru.nubby.notificatr.core.model.Stopwatch;
import ru.nubby.notificatr.core.service.StopwatchService;

public class StopwatchPresenter implements StopwatchContract.Presenter {
    private final StopwatchContract.View mView;
    private final EventBus mEventBus;
    private final StopwatchManager mStopwatchManager;
    private final StopwatchService mStopwatchService;

    private Stopwatch mStopwatch;

    public StopwatchPresenter(StopwatchContract.View view,
                              EventBus eventBus,
                              StopwatchManager stopwatchManager,
                              StopwatchService stopwatchService) {
        mView = view;
        mEventBus = eventBus;
        mStopwatchManager = stopwatchManager;
        mStopwatchService = stopwatchService;
        mStopwatch = stopwatchManager.getStopwatch();
    }

    @Override
    public void onStopwatchStartClicked() {
        mStopwatchService.start(mStopwatch, LocalDateTime.now());
    }

    @Override
    public void onStopwatchPauseClicked() {
        mStopwatchService.pause(mStopwatch, LocalDateTime.now());
    }

    @Override
    public void onStopwatchResetClicked() {
        mStopwatchService.reset(mStopwatch);
    }

    @Override
    public String getStopwatchTimeElapsed() {
        return PrintUtil.durationToString(
                mStopwatchService.timeElapsed(mStopwatch, LocalDateTime.now()));
    }

    @Override
    public void onResume() {
        mEventBus.register(this);
        mStopwatch = mStopwatchManager.getStopwatch();
        switch (mStopwatch.getStopwachState()) {
            case PAUSED: {
                mView.showStartResetButtons();
                break;
            }
            case STARTED: {
                mView.showPauseButton();
                break;
            }
        }
    }

    @Override
    public void onPause() {
        mEventBus.unregister(this);
    }
    @Subscribe
    public void onStopwatchStarted(StopwatchStarted event) {
        this.mStopwatch = event.getStopwatch();
        mView.showPauseButton();
    }
    @Subscribe
    public void onStopwatchPaused(StopwatchPaused event) {
        this.mStopwatch = event.getStopwatch();
        mView.showStartResetButtons();
    }
    @Subscribe
    public void onStopwatchReset(StopwatchWasReset event) {
        this.mStopwatch = event.getStopwatch();
        mView.showStartResetButtons();
    }

}
