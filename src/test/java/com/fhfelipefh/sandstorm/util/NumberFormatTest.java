package com.fhfelipefh.sandstorm.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NumberFormatTest {

    @Test
    void shouldFormatZeroAndSmallNumbers() {
        assertEquals("0", NumberFormat.compact(0));
        assertEquals("500", NumberFormat.compact(500));
        assertEquals("999", NumberFormat.compact(999));
    }

    @Test
    void shouldFormatThousandsWithK() {
        assertEquals("1K", NumberFormat.compact(1000));
        assertEquals("1K", NumberFormat.compact(1040));
        assertEquals("1.1K", NumberFormat.compact(1060));
        assertEquals("1.5K", NumberFormat.compact(1500));
        assertEquals("10K", NumberFormat.compact(10000));
        assertEquals("1000K", NumberFormat.compact(999999));
    }

    @Test
    void shouldFormatMillionsWithM() {
        assertEquals("1M", NumberFormat.compact(1000000));
        assertEquals("1.5M", NumberFormat.compact(1500000));
        assertEquals("10M", NumberFormat.compact(10000000));
        assertEquals("999M", NumberFormat.compact(999000000));
    }

    @Test
    void shouldFormatBillionsWithB() {
        assertEquals("1B", NumberFormat.compact(1000000000L));
        assertEquals("2.5B", NumberFormat.compact(2500000000L));
        assertEquals("50B", NumberFormat.compact(50000000000L));
    }

    @Test
    void shouldFormatTrillionsWithT() {
        assertEquals("1T", NumberFormat.compact(1000000000000L));
        assertEquals("5.2T", NumberFormat.compact(5200000000000L));
    }

    @Test
    void shouldFormatNegativeNumbersSymmetrically() {
        assertEquals("-1", NumberFormat.compact(-1));
        assertEquals("-500", NumberFormat.compact(-500));
        assertEquals("-999", NumberFormat.compact(-999));
        assertEquals("-1K", NumberFormat.compact(-1000));
        assertEquals("-1K", NumberFormat.compact(-1040));
        assertEquals("-1.5K", NumberFormat.compact(-1500));
        assertEquals("-1.5M", NumberFormat.compact(-1500000));
        assertEquals("-2.5B", NumberFormat.compact(-2500000000L));
        assertEquals("-1T", NumberFormat.compact(-1000000000000L));
    }

    @Test
    void shouldHandleExtremeLongValues() {
        assertEquals("-9223372T", NumberFormat.compact(Long.MIN_VALUE));
        assertEquals("9223372T", NumberFormat.compact(Long.MAX_VALUE));
    }

    @Test
    void shouldFormatExactWithGroupingCommas() {
        assertEquals("0", NumberFormat.formatExact(0));
        assertEquals("500", NumberFormat.formatExact(500));
        assertEquals("1,000", NumberFormat.formatExact(1000));
        assertEquals("1,500,000", NumberFormat.formatExact(1500000));
        assertEquals("-1,500", NumberFormat.formatExact(-1500));
        assertEquals("-9,223,372,036,854,775,808", NumberFormat.formatExact(Long.MIN_VALUE));
    }
}
