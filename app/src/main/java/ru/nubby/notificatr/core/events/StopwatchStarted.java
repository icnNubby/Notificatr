package ru.nubby.notificatr.core.events;

import ru.nubby.notificatr.core.model.Stopwatch;

public final class StopwatchStarted implements StopwatchEvent {
    private final Stopwatch mStopwatch;

    public StopwatchStarted(Stopwatch stopwatch) {
        mStopwatch = stopwatch;
    }

    public Stopwatch getStopwatch() {
        return mStopwatch;
    }
}
