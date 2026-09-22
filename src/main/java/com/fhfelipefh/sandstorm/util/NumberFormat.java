package com.fhfelipefh.sandstorm.util;

import java.util.Locale;

public final class NumberFormat {

    private NumberFormat() {}

    public static String compact(long value) {
        if (value == Long.MIN_VALUE) {
            return "-" + formatMagnitude(9_223_372_036_854_775_808.0, 1_000_000_000_000.0, "T");
        }
        if (value < 0) {
            return "-" + compact(-value);
        }
        if (value >= 1_000_000_000_000L) {
            return formatMagnitude((double) value, 1_000_000_000_000.0, "T");
        } else if (value >= 1_000_000_000L) {
            return formatMagnitude((double) value, 1_000_000_000.0, "B");
        } else if (value >= 1_000_000L) {
            return formatMagnitude((double) value, 1_000_000.0, "M");
        } else if (value >= 1_000L) {
            return formatMagnitude((double) value, 1_000.0, "K");
        }
        return String.valueOf(value);
    }

    public static String formatExact(long value) {
        return String.format(Locale.ROOT, "%,d", value);
    }

    private static String formatMagnitude(double value, double divisor, String suffix) {
        double divided = value / divisor;
        String formatted = String.format(Locale.ROOT, "%.1f", divided);
        if (formatted.endsWith(".0")) {
            formatted = formatted.substring(0, formatted.length() - 2);
        }
        return formatted + suffix;
    }
}
