package ru.nubby.notificatr.core.commands;

import ru.nubby.notificatr.core.model.Stopwatch;

public final class StopwatchReset implements StopwatchCommand {

    private final Stopwatch mStopwatch;

    public StopwatchReset(Stopwatch stopwatch) {
        mStopwatch = stopwatch;
    }

    public Stopwatch getStopwatch() {
        return mStopwatch;
    }
}
