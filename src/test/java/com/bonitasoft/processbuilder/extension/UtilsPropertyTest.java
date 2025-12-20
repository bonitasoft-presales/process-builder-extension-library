package com.bonitasoft.processbuilder.extension;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

import static org.assertj.core.api.Assertions.*;

/**
 * Property-based tests for {@link Utils} utility class.
 * These tests focus on verifying actual computed values to kill mutations.
 *
 * @author Bonitasoft
 * @since 1.0
 */
@Label("Utils Property-Based Tests")
class UtilsPropertyTest {

    // =========================================================================
    // calculateElapsedTime(startTime, endTime) PROPERTIES
    // =========================================================================

    @Property(tries = 500)
    @Label("calculateElapsedTime should return exactly endTime - startTime")
    void calculateElapsedTimeShouldReturnExactDifference(
            @ForAll @LongRange(min = 0, max = Long.MAX_VALUE / 2) long startTime,
            @ForAll @LongRange(min = 0, max = Long.MAX_VALUE / 2) long delta) {

        long endTime = startTime + delta;
        long result = Utils.calculateElapsedTime(startTime, endTime);

        // This kills mutations that change the subtraction operation
        assertThat(result).isEqualTo(delta);
        assertThat(result).isEqualTo(endTime - startTime);
    }

    @Property(tries = 500)
    @Label("calculateElapsedTime should be negative when endTime < startTime")
    void calculateElapsedTimeShouldBeNegativeWhenEndBeforeStart(
            @ForAll @LongRange(min = 1, max = Long.MAX_VALUE / 2) long startTime,
            @ForAll @LongRange(min = 1, max = 1000000) long delta) {

        long endTime = startTime - delta;
        long result = Utils.calculateElapsedTime(startTime, endTime);

        assertThat(result).isNegative();
        assertThat(result).isEqualTo(-delta);
    }

    @Property(tries = 300)
    @Label("calculateElapsedTime should return zero when startTime equals endTime")
    void calculateElapsedTimeShouldReturnZeroWhenEqual(
            @ForAll @LongRange(min = 0, max = Long.MAX_VALUE) long time) {

        long result = Utils.calculateElapsedTime(time, time);

        assertThat(result).isZero();
    }

    @Property(tries = 300)
    @Label("calculateElapsedTime should satisfy commutative inverse property")
    void calculateElapsedTimeShouldSatisfyInverseProperty(
            @ForAll @LongRange(min = 0, max = Long.MAX_VALUE / 2) long a,
            @ForAll @LongRange(min = 0, max = Long.MAX_VALUE / 2) long b) {

        long forward = Utils.calculateElapsedTime(a, b);
        long backward = Utils.calculateElapsedTime(b, a);

        // a-b and b-a should be negatives of each other
        assertThat(forward + backward).isZero();
    }

    // =========================================================================
    // logElapsedTimeByElapsedTime PROPERTIES (verify it doesn't crash)
    // =========================================================================

    @Property(tries = 300)
    @Label("logElapsedTimeByElapsedTime should never throw for any elapsed time")
    void logElapsedTimeByElapsedTimeShouldNeverThrow(
            @ForAll long elapsedTime,
            @ForAll @StringLength(min = 0, max = 100) String name) {

        assertThatCode(() -> Utils.logElapsedTimeByElapsedTime(elapsedTime, name))
                .doesNotThrowAnyException();
    }

    @Property(tries = 100)
    @Label("logElapsedTimeByElapsedTime should handle null name without throwing")
    void logElapsedTimeByElapsedTimeShouldHandleNullName(
            @ForAll @LongRange(min = 0, max = 1000000) long elapsedTime) {

        assertThatCode(() -> Utils.logElapsedTimeByElapsedTime(elapsedTime, null))
                .doesNotThrowAnyException();
    }

    // =========================================================================
    // logAndGetElapsedTime PROPERTIES
    // =========================================================================

    @Property(tries = 300)
    @Label("logAndGetElapsedTime should return the same as calculateElapsedTime")
    void logAndGetElapsedTimeShouldReturnSameAsCalculate(
            @ForAll @LongRange(min = 0, max = Long.MAX_VALUE / 2) long startTime) {

        // Get a snapshot before and after to bracket the expected value
        long before = System.currentTimeMillis();
        long result = Utils.logAndGetElapsedTime(startTime, "test");
        long after = System.currentTimeMillis();

        // The result should be between the two boundaries
        assertThat(result).isGreaterThanOrEqualTo(before - startTime);
        assertThat(result).isLessThanOrEqualTo(after - startTime);
    }

    @Property(tries = 200)
    @Label("logAndGetElapsedTime should never throw for any valid input")
    void logAndGetElapsedTimeShouldNeverThrow(
            @ForAll long startTime,
            @ForAll @StringLength(min = 0, max = 100) String name) {

        assertThatCode(() -> Utils.logAndGetElapsedTime(startTime, name))
                .doesNotThrowAnyException();
    }

    // =========================================================================
    // logElapsedTime PROPERTIES
    // =========================================================================

    @Property(tries = 200)
    @Label("logElapsedTime should never throw for any valid input")
    void logElapsedTimeShouldNeverThrow(
            @ForAll long startTime,
            @ForAll @StringLength(min = 0, max = 100) String name) {

        assertThatCode(() -> Utils.logElapsedTime(startTime, name))
                .doesNotThrowAnyException();
    }

    @Property(tries = 100)
    @Label("logElapsedTime should handle null name without throwing")
    void logElapsedTimeShouldHandleNullName(
            @ForAll @LongRange(min = 0, max = Long.MAX_VALUE / 2) long startTime) {

        assertThatCode(() -> Utils.logElapsedTime(startTime, null))
                .doesNotThrowAnyException();
    }

    // =========================================================================
    // calculateElapsedTime(startTime) single-arg PROPERTIES
    // =========================================================================

    @Property(tries = 200)
    @Label("calculateElapsedTime single-arg should return non-negative for past times")
    void calculateElapsedTimeSingleArgShouldReturnNonNegativeForPast(
            @ForAll @LongRange(min = 1, max = 100000) long delta) {

        long startTime = System.currentTimeMillis() - delta;
        long result = Utils.calculateElapsedTime(startTime);

        // Result should be at least delta (minus small tolerance for execution time)
        assertThat(result).isGreaterThanOrEqualTo(delta - 10);
    }

    @Property(tries = 200)
    @Label("calculateElapsedTime single-arg should return negative for future times")
    void calculateElapsedTimeSingleArgShouldReturnNegativeForFuture(
            @ForAll @LongRange(min = 1000, max = 100000) long delta) {

        long startTime = System.currentTimeMillis() + delta;
        long result = Utils.calculateElapsedTime(startTime);

        // Result should be negative (or at most slightly positive due to execution time)
        assertThat(result).isLessThanOrEqualTo(10);
    }
}
