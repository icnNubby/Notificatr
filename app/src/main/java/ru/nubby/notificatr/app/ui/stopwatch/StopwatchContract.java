package ru.nubby.notificatr.app.ui.stopwatch;

import ru.nubby.notificatr.app.ui.base.MvpPresenter;
import ru.nubby.notificatr.app.ui.base.MvpView;

public interface StopwatchContract {
    interface Presenter extends MvpPresenter {
        void onStopwatchStartClicked();
        void onStopwatchPauseClicked();
        void onStopwatchResetClicked();
        String getStopwatchTimeElapsed();

        void onResume();
        void onPause();
    }

    interface View extends MvpView<Presenter> {
        void showStartResetButtons();
        void showPauseButton();
    }
}
