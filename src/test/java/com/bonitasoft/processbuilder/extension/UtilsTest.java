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
}
