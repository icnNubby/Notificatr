package ru.nubby.notificatr.core.events;

import ru.nubby.notificatr.core.model.Stopwatch;

public final class StopwatchPaused implements StopwatchEvent {
    private final Stopwatch mStopwatch;

    public StopwatchPaused(Stopwatch stopwatch) {
        mStopwatch = stopwatch;
    }

    public Stopwatch getStopwatch() {
        return mStopwatch;
    }
}
