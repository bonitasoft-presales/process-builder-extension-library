package com.bonitasoft.processbuilder.extension;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link Utils} utility class.
 * <p>
 * This class ensures that the utility class cannot be instantiated and
 * that the logging utility methods work correctly across various scenarios.
 * </p>
 */
class UtilsTest {

    // -------------------------------------------------------------------------
    // Utility Class Instantiation Test
    // -------------------------------------------------------------------------

    /**
     * Tests that the private constructor throws an UnsupportedOperationException when accessed via reflection,
     * confirming that the utility class cannot be instantiated.
     */
    @Test
    @DisplayName("Should throw UnsupportedOperationException on instantiation attempt")
    void shouldThrowExceptionOnInstantiation() throws Exception {
        // Given the Utils class with private constructor
        // When attempting to get and invoke the constructor via reflection
        Constructor<Utils> constructor = Utils.class.getDeclaredConstructor();

        assertTrue(Modifier.isPrivate(constructor.getModifiers()),
            "Constructor must be private to enforce utility class pattern.");

        constructor.setAccessible(true);

        // Then an InvocationTargetException wrapping UnsupportedOperationException should be thrown
        InvocationTargetException thrown = assertThrows(InvocationTargetException.class,
            constructor::newInstance,
            "The constructor call must throw an exception.");

        // Verify the cause of the exception
        assertEquals(UnsupportedOperationException.class, thrown.getCause().getClass(),
            "The exception cause should be UnsupportedOperationException.");

        final String expectedMessage = "Utils class and cannot be instantiated";
        assertTrue(thrown.getCause().getMessage().contains(expectedMessage.substring(0, 10)),
            "The exception message should contain 'Utils class'.");
    }

    // -------------------------------------------------------------------------
    // logElapsedTime Tests
    // -------------------------------------------------------------------------

    /**
     * Tests that logElapsedTime correctly executes without throwing exceptions.
     */
    @Test
    @DisplayName("logElapsedTime should execute without throwing exceptions")
    void logElapsedTime_should_execute_successfully() {
        // Given a start time and a task name
        long startTime = System.currentTimeMillis() - 5000; // 5 seconds ago
        String taskName = "TestTask";

        // When calling logElapsedTime
        // Then no exception should be thrown
        assertDoesNotThrow(() -> Utils.logElapsedTime(startTime, taskName));
    }

    /**
     * Tests that logElapsedTime works with current time (zero elapsed time).
     */
    @Test
    @DisplayName("logElapsedTime should handle zero elapsed time")
    void logElapsedTime_should_handle_zero_elapsed_time() {
        // Given a start time equal to current time
        long startTime = System.currentTimeMillis();
        String taskName = "InstantTask";

        // When calling logElapsedTime
        // Then no exception should be thrown
        assertDoesNotThrow(() -> Utils.logElapsedTime(startTime, taskName));
    }

    /**
     * Tests that logElapsedTime handles empty task name.
     */
    @Test
    @DisplayName("logElapsedTime should handle empty task name")
    void logElapsedTime_should_handle_empty_task_name() {
        // Given a start time and an empty task name
        long startTime = System.currentTimeMillis() - 1000;
        String taskName = "";

        // When calling logElapsedTime
        // Then no exception should be thrown
        assertDoesNotThrow(() -> Utils.logElapsedTime(startTime, taskName));
    }

    /**
     * Tests that logElapsedTime handles null task name.
     */
    @Test
    @DisplayName("logElapsedTime should handle null task name")
    void logElapsedTime_should_handle_null_task_name() {
        // Given a start time and a null task name
        long startTime = System.currentTimeMillis() - 1000;
        String taskName = null;

        // When calling logElapsedTime
        // Then no exception should be thrown
        assertDoesNotThrow(() -> Utils.logElapsedTime(startTime, taskName));
    }

    /**
     * Tests that logElapsedTime correctly handles long elapsed times (multiple minutes).
     */
    @Test
    @DisplayName("logElapsedTime should handle long elapsed times")
    void logElapsedTime_should_handle_long_elapsed_time() {
        // Given a start time representing 2 minutes, 30 seconds, and 250 milliseconds ago
        long elapsedMillis = (2 * 60 * 1000) + (30 * 1000) + 250; // 2m 30s 250ms
        long startTime = System.currentTimeMillis() - elapsedMillis;
        String taskName = "LongTask";

        // When calling logElapsedTime
        // Then no exception should be thrown
        assertDoesNotThrow(() -> Utils.logElapsedTime(startTime, taskName));
    }

    /**
     * Tests that logElapsedTime handles time less than 1 second.
     */
    @Test
    @DisplayName("logElapsedTime should handle time less than 1 second")
    void logElapsedTime_should_handle_less_than_one_second() {
        // Given a start time less than 1 second ago
        long startTime = System.currentTimeMillis() - 500; // 500 milliseconds ago
        String taskName = "QuickTask";

        // When calling logElapsedTime
        // Then no exception should be thrown
        assertDoesNotThrow(() -> Utils.logElapsedTime(startTime, taskName));
    }

    /**
     * Tests that logElapsedTime handles exactly 1 minute.
     */
    @Test
    @DisplayName("logElapsedTime should handle exactly 1 minute")
    void logElapsedTime_should_handle_exactly_one_minute() {
        // Given a start time exactly 1 minute ago
        long startTime = System.currentTimeMillis() - 60000; // 60,000 milliseconds = 1 minute
        String taskName = "OneMinuteTask";

        // When calling logElapsedTime
        // Then no exception should be thrown
        assertDoesNotThrow(() -> Utils.logElapsedTime(startTime, taskName));
    }

    /**
     * Tests that logElapsedTime handles 59 seconds (boundary test).
     */
    @Test
    @DisplayName("logElapsedTime should handle 59 seconds")
    void logElapsedTime_should_handle_fifty_nine_seconds() {
        // Given a start time 59 seconds and 999 milliseconds ago (just before 1 minute)
        long startTime = System.currentTimeMillis() - 59999; // 59s 999ms
        String taskName = "AlmostOneMinute";

        // When calling logElapsedTime
        // Then no exception should be thrown
        assertDoesNotThrow(() -> Utils.logElapsedTime(startTime, taskName));
    }

    /**
     * Tests that logElapsedTime handles very long task names.
     */
    @Test
    @DisplayName("logElapsedTime should handle very long task names")
    void logElapsedTime_should_handle_long_task_name() {
        // Given a start time and a very long task name
        long startTime = System.currentTimeMillis() - 1000;
        String taskName = "This is a very long task name that contains many words and characters to test the logging functionality";

        // When calling logElapsedTime
        // Then no exception should be thrown
        assertDoesNotThrow(() -> Utils.logElapsedTime(startTime, taskName));
    }

    /**
     * Tests that logElapsedTime handles task names with special characters.
     */
    @Test
    @DisplayName("logElapsedTime should handle task names with special characters")
    void logElapsedTime_should_handle_special_characters() {
        // Given a start time and a task name with special characters
        long startTime = System.currentTimeMillis() - 1000;
        String taskName = "Task-Name_With.Special@Characters#123!";

        // When calling logElapsedTime
        // Then no exception should be thrown
        assertDoesNotThrow(() -> Utils.logElapsedTime(startTime, taskName));
    }

    /**
     * Tests that logElapsedTime handles negative start time (future time).
     */
    @Test
    @DisplayName("logElapsedTime should handle future start time")
    void logElapsedTime_should_handle_future_start_time() {
        // Given a start time in the future
        long startTime = System.currentTimeMillis() + 10000; // 10 seconds in the future
        String taskName = "FutureTask";

        // When calling logElapsedTime
        // Then no exception should be thrown (even though the result might be negative)
        assertDoesNotThrow(() -> Utils.logElapsedTime(startTime, taskName));
    }

    // -------------------------------------------------------------------------
    // calculateElapsedTime(long startTime) Tests
    // -------------------------------------------------------------------------

    /**
     * Tests that calculateElapsedTime returns a positive value for past start time.
     */
    @Test
    @DisplayName("calculateElapsedTime should return positive value for past start time")
    void calculateElapsedTime_should_return_positive_for_past_start() {
        // Given a start time in the past
        long startTime = System.currentTimeMillis() - 5000;

        // When calculating elapsed time
        long elapsed = Utils.calculateElapsedTime(startTime);

        // Then the result should be at least 5000ms (allowing some tolerance)
        assertTrue(elapsed >= 4900, "Elapsed time should be at least ~5000ms");
    }

    /**
     * Tests that calculateElapsedTime returns zero or near-zero for current time.
     */
    @Test
    @DisplayName("calculateElapsedTime should return near-zero for current start time")
    void calculateElapsedTime_should_return_near_zero_for_current_time() {
        // Given a start time equal to now
        long startTime = System.currentTimeMillis();

        // When calculating elapsed time
        long elapsed = Utils.calculateElapsedTime(startTime);

        // Then the result should be very small (less than 100ms)
        assertTrue(elapsed >= 0 && elapsed < 100, "Elapsed time should be near zero");
    }

    /**
     * Tests that calculateElapsedTime returns negative for future start time.
     */
    @Test
    @DisplayName("calculateElapsedTime should return negative for future start time")
    void calculateElapsedTime_should_return_negative_for_future_start() {
        // Given a start time in the future
        long startTime = System.currentTimeMillis() + 5000;

        // When calculating elapsed time
        long elapsed = Utils.calculateElapsedTime(startTime);

        // Then the result should be negative
        assertTrue(elapsed < 0, "Elapsed time should be negative for future start time");
    }

    // -------------------------------------------------------------------------
    // calculateElapsedTime(long startTime, long endTime) Tests
    // -------------------------------------------------------------------------

    /**
     * Tests that calculateElapsedTime with two parameters returns correct difference.
     */
    @Test
    @DisplayName("calculateElapsedTime with two params should return correct difference")
    void calculateElapsedTime_two_params_should_return_correct_difference() {
        // Given fixed start and end times
        long startTime = 1000L;
        long endTime = 6000L;

        // When calculating elapsed time
        long elapsed = Utils.calculateElapsedTime(startTime, endTime);

        // Then the result should be exactly 5000ms
        assertEquals(5000L, elapsed);
    }

    /**
     * Tests that calculateElapsedTime with two parameters handles zero difference.
     */
    @Test
    @DisplayName("calculateElapsedTime with two params should handle zero difference")
    void calculateElapsedTime_two_params_should_handle_zero_difference() {
        // Given same start and end times
        long time = 5000L;

        // When calculating elapsed time
        long elapsed = Utils.calculateElapsedTime(time, time);

        // Then the result should be zero
        assertEquals(0L, elapsed);
    }

    /**
     * Tests that calculateElapsedTime with two parameters handles negative difference.
     */
    @Test
    @DisplayName("calculateElapsedTime with two params should handle negative difference")
    void calculateElapsedTime_two_params_should_handle_negative_difference() {
        // Given end time before start time
        long startTime = 6000L;
        long endTime = 1000L;

        // When calculating elapsed time
        long elapsed = Utils.calculateElapsedTime(startTime, endTime);

        // Then the result should be negative
        assertEquals(-5000L, elapsed);
    }

    /**
     * Tests that calculateElapsedTime with two parameters handles large values.
     */
    @Test
    @DisplayName("calculateElapsedTime with two params should handle large values")
    void calculateElapsedTime_two_params_should_handle_large_values() {
        // Given large timestamp values
        long startTime = System.currentTimeMillis();
        long endTime = startTime + 3600000L; // 1 hour later

        // When calculating elapsed time
        long elapsed = Utils.calculateElapsedTime(startTime, endTime);

        // Then the result should be exactly 1 hour in milliseconds
        assertEquals(3600000L, elapsed);
    }

    // -------------------------------------------------------------------------
    // logElapsedTimeByElapsedTime Tests
    // -------------------------------------------------------------------------

    /**
     * Tests that logElapsedTimeByElapsedTime executes without throwing exceptions.
     */
    @Test
    @DisplayName("logElapsedTimeByElapsedTime should execute without throwing exceptions")
    void logElapsedTimeByElapsedTime_should_execute_successfully() {
        // Given a pre-calculated elapsed time
        long elapsedTime = 125250L; // 2m 5s 250ms

        // When calling logElapsedTimeByElapsedTime
        // Then no exception should be thrown
        assertDoesNotThrow(() -> Utils.logElapsedTimeByElapsedTime(elapsedTime, "TestOperation"));
    }

    /**
     * Tests that logElapsedTimeByElapsedTime handles zero elapsed time.
     */
    @Test
    @DisplayName("logElapsedTimeByElapsedTime should handle zero elapsed time")
    void logElapsedTimeByElapsedTime_should_handle_zero() {
        assertDoesNotThrow(() -> Utils.logElapsedTimeByElapsedTime(0L, "InstantOperation"));
    }

    /**
     * Tests that logElapsedTimeByElapsedTime handles negative elapsed time.
     */
    @Test
    @DisplayName("logElapsedTimeByElapsedTime should handle negative elapsed time")
    void logElapsedTimeByElapsedTime_should_handle_negative() {
        assertDoesNotThrow(() -> Utils.logElapsedTimeByElapsedTime(-5000L, "NegativeTimeOperation"));
    }

    /**
     * Tests that logElapsedTimeByElapsedTime handles null task name.
     */
    @Test
    @DisplayName("logElapsedTimeByElapsedTime should handle null task name")
    void logElapsedTimeByElapsedTime_should_handle_null_name() {
        assertDoesNotThrow(() -> Utils.logElapsedTimeByElapsedTime(1000L, null));
    }

    /**
     * Tests that logElapsedTimeByElapsedTime handles exactly 1 minute.
     */
    @Test
    @DisplayName("logElapsedTimeByElapsedTime should handle exactly 1 minute")
    void logElapsedTimeByElapsedTime_should_handle_one_minute() {
        assertDoesNotThrow(() -> Utils.logElapsedTimeByElapsedTime(60000L, "OneMinuteOperation"));
    }

    /**
     * Tests that logElapsedTimeByElapsedTime handles multiple minutes.
     */
    @Test
    @DisplayName("logElapsedTimeByElapsedTime should handle multiple minutes")
    void logElapsedTimeByElapsedTime_should_handle_multiple_minutes() {
        // 5 minutes, 30 seconds, 500 milliseconds
        long elapsed = (5 * 60 * 1000) + (30 * 1000) + 500;
        assertDoesNotThrow(() -> Utils.logElapsedTimeByElapsedTime(elapsed, "LongOperation"));
    }

    // -------------------------------------------------------------------------
    // logAndGetElapsedTime Tests
    // -------------------------------------------------------------------------

    /**
     * Tests that logAndGetElapsedTime returns correct elapsed time.
     */
    @Test
    @DisplayName("logAndGetElapsedTime should return correct elapsed time")
    void logAndGetElapsedTime_should_return_correct_value() {
        // Given a start time 5 seconds ago
        long startTime = System.currentTimeMillis() - 5000;

        // When calling logAndGetElapsedTime
        long elapsed = Utils.logAndGetElapsedTime(startTime, "TestTask");

        // Then the result should be at least ~5000ms
        assertTrue(elapsed >= 4900, "Elapsed time should be at least ~5000ms");
    }

    /**
     * Tests that logAndGetElapsedTime handles zero elapsed time.
     */
    @Test
    @DisplayName("logAndGetElapsedTime should handle zero elapsed time")
    void logAndGetElapsedTime_should_handle_zero_elapsed() {
        // Given a start time equal to current time
        long startTime = System.currentTimeMillis();

        // When calling logAndGetElapsedTime
        long elapsed = Utils.logAndGetElapsedTime(startTime, "InstantTask");

        // Then the result should be very small
        assertTrue(elapsed >= 0 && elapsed < 100, "Elapsed time should be near zero");
    }

    /**
     * Tests that logAndGetElapsedTime handles null task name.
     */
    @Test
    @DisplayName("logAndGetElapsedTime should handle null task name")
    void logAndGetElapsedTime_should_handle_null_name() {
        long startTime = System.currentTimeMillis() - 1000;
        assertDoesNotThrow(() -> Utils.logAndGetElapsedTime(startTime, null));
    }

    /**
     * Tests that logAndGetElapsedTime handles empty task name.
     */
    @Test
    @DisplayName("logAndGetElapsedTime should handle empty task name")
    void logAndGetElapsedTime_should_handle_empty_name() {
        long startTime = System.currentTimeMillis() - 1000;
        assertDoesNotThrow(() -> Utils.logAndGetElapsedTime(startTime, ""));
    }

    /**
     * Tests that logAndGetElapsedTime handles future start time.
     */
    @Test
    @DisplayName("logAndGetElapsedTime should handle future start time")
    void logAndGetElapsedTime_should_handle_future_start() {
        // Given a start time in the future
        long startTime = System.currentTimeMillis() + 5000;

        // When calling logAndGetElapsedTime
        long elapsed = Utils.logAndGetElapsedTime(startTime, "FutureTask");

        // Then the result should be negative
        assertTrue(elapsed < 0, "Elapsed time should be negative for future start time");
    }

    /**
     * Tests that logAndGetElapsedTime handles long elapsed times.
     */
    @Test
    @DisplayName("logAndGetElapsedTime should handle long elapsed times")
    void logAndGetElapsedTime_should_handle_long_elapsed() {
        // Given a start time 3 minutes and 45 seconds ago
        long elapsedMs = (3 * 60 * 1000) + (45 * 1000) + 123;
        long startTime = System.currentTimeMillis() - elapsedMs;

        // When calling logAndGetElapsedTime
        long elapsed = Utils.logAndGetElapsedTime(startTime, "LongRunningTask");

        // Then the result should be approximately the expected elapsed time
        assertTrue(elapsed >= elapsedMs - 100 && elapsed <= elapsedMs + 100,
            "Elapsed time should be approximately " + elapsedMs + "ms");
    }
}
