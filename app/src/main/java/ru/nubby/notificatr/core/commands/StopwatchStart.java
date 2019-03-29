package ru.nubby.notificatr.core.commands;


import java.util.Date;

import ru.nubby.notificatr.core.model.Stopwatch;

public final class StopwatchStart implements StopwatchCommand {
    private final Stopwatch mStopwatch;
    private final Date mStartedAt;

    public StopwatchStart(Stopwatch stopwatch, Date startedAt) {
        mStopwatch = stopwatch;
        mStartedAt = startedAt;
    }

    public Date getStartedAt() {
        return mStartedAt;
    }

    public Stopwatch getStopwatch() {
        return mStopwatch;
    }
}
