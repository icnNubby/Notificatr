package ru.nubby.notificatr.core.commands;

import java.util.Date;

import ru.nubby.notificatr.core.model.Stopwatch;

public final class StopwatchPause implements StopwatchCommand {

    private final Stopwatch mStopwatch;
    private final Date mPausedAt;

    public StopwatchPause(Stopwatch stopwatch, Date pausedAt) {
        mStopwatch = stopwatch;
        mPausedAt = pausedAt;
    }

    public Date getPausedAt() {
        return mPausedAt;
    }

    public Stopwatch getStopwatch() {
        return mStopwatch;
    }

}
