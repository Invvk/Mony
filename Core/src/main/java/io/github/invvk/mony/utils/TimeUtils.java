package io.github.invvk.mony.utils;

import org.apache.commons.lang.StringUtils;

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


}
