package io.github.invvk.mony.internal.utils;

import org.bukkit.ChatColor;

import java.util.regex.Pattern;

public class Utils {

    private Utils() {
        // This is an utils class, so there is no need to initial it.
    }

    private static final Pattern specialChar;

    static {
        specialChar = Pattern.compile("[^A-Za-z0-9]");
    }

    public static boolean containsSpecialChar(String input) {
        return specialChar.matcher(input).find();
    }

    public static String color(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

}
