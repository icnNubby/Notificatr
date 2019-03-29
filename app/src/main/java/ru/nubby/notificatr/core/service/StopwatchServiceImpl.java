package ru.nubby.notificatr.core.service;

import org.threeten.bp.Duration;
import org.threeten.bp.LocalDateTime;

import ru.nubby.notificatr.core.model.Stopwatch;

import static ru.nubby.notificatr.core.model.StopwachState.PAUSED;
import static ru.nubby.notificatr.core.model.StopwachState.STARTED;

public class StopwatchServiceImpl implements StopwatchService {
    @Override
    public Stopwatch create() {
        return new Stopwatch(PAUSED, LocalDateTime.now(), Duration.ZERO);
    }

    @Override
    public Stopwatch start(Stopwatch stopwatch, LocalDateTime startedAt) {
        switch (stopwatch.getStopwachState()) {
            case STARTED: return stopwatch;
            case PAUSED: return new Stopwatch(STARTED, LocalDateTime.now(), stopwatch.getOffset());
            default: return stopwatch;
        }
    }

    @Override
    public Stopwatch pause(Stopwatch stopwatch, LocalDateTime pausedAt) {
        switch (stopwatch.getStopwachState()) {
            case PAUSED: return stopwatch;
            case STARTED: return new Stopwatch(PAUSED, LocalDateTime.now(),
                    newOffset(stopwatch.getOffset(), stopwatch.getStartedAt(), pausedAt));
            default: return stopwatch;
        }
    }

    @Override
    public Stopwatch reset(Stopwatch stopwatch) {
        return create();
    }

    @Override
    public Duration timeElapsed(Stopwatch stopwatch, LocalDateTime now) {
        switch (stopwatch.getStopwachState()) {
            case PAUSED: return stopwatch.getOffset();
            case STARTED: return stopwatch.getOffset()
                    .plus(Duration.between(stopwatch.getStartedAt(), LocalDateTime.now()));
            default:return stopwatch.getOffset();
        }
    }

    private Duration newOffset(Duration existingOffset,LocalDateTime startedAt, LocalDateTime pausedAt) {
        return existingOffset.plus(Duration.between(startedAt, pausedAt));
    }
}
