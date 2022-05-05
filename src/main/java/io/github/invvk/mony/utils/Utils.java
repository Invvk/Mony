package io.github.invvk.mony.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Utils {

    public static boolean validateTimeStamp(String stamp, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            sdf.parse(stamp);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    public static boolean validateTimeStamp(String stamp) {
        return validateTimeStamp(stamp, "HH:mm");
    }

}
