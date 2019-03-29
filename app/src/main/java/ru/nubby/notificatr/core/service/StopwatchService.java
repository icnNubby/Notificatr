package ru.nubby.notificatr.core.service;

import org.threeten.bp.Duration;
import org.threeten.bp.LocalDateTime;

import ru.nubby.notificatr.core.model.Stopwatch;

public interface StopwatchService {
    Stopwatch create();
    Stopwatch start(Stopwatch stopwatch, LocalDateTime startedAt);
    Stopwatch pause(Stopwatch stopwatch, LocalDateTime pausedAt);
    Stopwatch reset(Stopwatch stopwatch);
    Duration timeElapsed(Stopwatch stopwatch, LocalDateTime now);
}
