package com.fhfelipefh.sandstorm.util;

public final class NumberFormat {

    private NumberFormat() {}

    public static String compact(long value) {
        if (value >= 1_000_000) {
            double m = value / 1_000_000.0;
            return (m == (long) m) ? (long) m + "M" : String.format("%.1fM", m);
        } else if (value >= 1_000) {
            double k = value / 1_000.0;
            return (k == (long) k) ? (long) k + "K" : String.format("%.1fK", k);
        }
        return String.valueOf(value);
    }
}
