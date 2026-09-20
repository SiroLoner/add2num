package com.caesar.add2num.core;

import java.util.Random;

/**
 * Deterministic generator of digit strings for the test suite.
 *
 * <p>Every generator takes an explicit seed. A failing case can therefore be reproduced exactly by
 * re-running the test, which is the whole point of preferring a seeded generator over
 * {@link Math#random()} in a build that has to be trustworthy.
 */
final class TestNumbers {

    private TestNumbers() {
        // utility class
    }

    /** A string of exactly {@code length} decimal digits, possibly with leading zeros. */
    static String digits(int length, long seed) {
        return digits(length, new Random(seed));
    }

    /** A string of exactly {@code length} decimal digits drawn from the supplied generator. */
    static String digits(int length, Random random) {
        if (length == 0) {
            return "";
        }
        char[] buffer = new char[length];
        for (int i = 0; i < length; i++) {
            buffer[i] = (char) ('0' + random.nextInt(10));
        }
        return new String(buffer);
    }
}
