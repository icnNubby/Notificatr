package ru.nubby.notificatr.core.events;

import ru.nubby.notificatr.core.model.Stopwatch;

public final class StopwatchWasReset implements StopwatchEvent {
    private final Stopwatch mStopwatch;

    public StopwatchWasReset(Stopwatch stopwatch) {
        mStopwatch = stopwatch;
    }

    public Stopwatch getStopwatch() {
        return mStopwatch;
    }
}
