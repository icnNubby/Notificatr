package ru.nubby.notificatr.app.utils;

import org.threeten.bp.Duration;

public final class PrintUtil {
    private PrintUtil() {}

    public static String durationToString(Duration duration) {
        long hours = duration.toHours();
        long minutes = duration.toMinutes() - hours * 60;
        long seconds = duration.getSeconds() - hours * 60 - minutes * 60;
        String minutesString = minutes >= 10? String.valueOf(minutes) : "0" + minutes;
        String secondsString = seconds >= 10? String.valueOf(seconds) : "0" + seconds;
        return hours > 0?
                hours + ":" + minutesString + ":" + secondsString :
                minutesString + ":" + secondsString;
    }
}
