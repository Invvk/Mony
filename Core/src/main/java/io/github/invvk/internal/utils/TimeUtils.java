package io.github.invvk.internal.utils;

import org.apache.commons.lang.StringUtils;

import java.util.concurrent.TimeUnit;

public class TimeUtils {

    private TimeUtils() {
        // This is an utils class, so there is no need to initial it.
    }

    public static long getTimeInMilli(String input) {
        return 1000L * getTimeInSeconds(input);
    }

    public static long getTimeInSeconds(String input) {
        long timeInSeconds = 0;
        final StringBuilder s = new StringBuilder();

        for (final char c: input.toCharArray()) {
            s.append(c);

            if (StringUtils.isNumeric(s.toString()))
                continue;

            // remove the last char added because it is not numeric
            s.setLength(s.length() - 1);

            // if length is less than or equals 0 then there is something wrong with the format.
            if (s.length() <= 0)
                continue;

            long num = Long.parseLong(s.toString());
            switch (c) {
                case 's':
                case 'S':
                    timeInSeconds += num;
                    break;
                case 'm':
                case 'M':
                    timeInSeconds += num * 60;
                    break;
                case 'H':
                case 'h':
                    timeInSeconds += num * 60 * 60;
                    break;
                case 'd':
                case 'D':
                    timeInSeconds += num * 60 * 60 * 24;
                    break;
                // represents months
                case 'o':
                case 'O':
                    timeInSeconds += num * 60 * 60 * 24 * 30;
                    break;
                case 'y':
                case 'Y':
                    timeInSeconds += Long.parseLong(s.toString()) * 60 * 60 * 24 * 365;
            }

            // reset the string builder;
            s.setLength(0);
        }
        return timeInSeconds;
    }

    public static String translateTime(long timeMilli) {
        if (timeMilli == 0)
            return "none";

        if (timeMilli == -1)
            return "permanent";

        final TimeUnit unit = TimeUnit.MILLISECONDS;

        final long seconds = unit.toSeconds(timeMilli) % 60;
        final long minutes = unit.toMinutes(timeMilli) % 60;
        final long hours = unit.toHours(timeMilli) % 24;
        final long days = unit.toDays(timeMilli);

        StringBuilder builder = new StringBuilder();

        if (days > 0)
            builder.append(days).append(" day(s)").append(" ");
        if (hours > 0)
            builder.append(hours).append(" hour(s)").append(" ");
        if (minutes > 0)
            builder.append(minutes).append(" minute(s)").append(" ");

        builder.append(seconds).append(" second(s)").append(" ");

        return builder.toString();
    }


}
