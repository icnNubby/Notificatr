package ru.nubby.notificatr.core.model;

import org.threeten.bp.Duration;
import org.threeten.bp.LocalDateTime;

public final class Stopwatch {

    private StopwachState mStopwachState;
    private LocalDateTime mStartedAt;
    private Duration mOffset;

    public Stopwatch(StopwachState stopwachState, LocalDateTime startedAt, Duration offset) {
        mStopwachState = stopwachState;
        mStartedAt = startedAt;
        mOffset = offset;
    }

    public StopwachState getStopwachState() {
        return mStopwachState;
    }

    public void setStopwachState(StopwachState stopwachState) {
        mStopwachState = stopwachState;
    }

    public LocalDateTime getStartedAt() {
        return mStartedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        mStartedAt = startedAt;
    }

    public Duration getOffset() {
        return mOffset;
    }

    public void setOffset(Duration offset) {
        mOffset = offset;
    }
}
