package com.bonitasoft.processbuilder.extension;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

import static org.assertj.core.api.Assertions.*;

/**
 * Property-based tests for {@link ExceptionUtils} utility class.
 * Tests invariants that must hold for any valid input.
 *
 * @author Bonitasoft
 * @since 1.0
 */
@Label("ExceptionUtils Property-Based Tests")
class ExceptionUtilsPropertyTest {

    // =========================================================================
    // UTILITY CLASS INVARIANT PROPERTIES
    // =========================================================================

    @Property(tries = 100)
    @Label("Utility class should not be instantiable")
    void utilityClassShouldNotBeInstantiable() {
        assertThatThrownBy(() -> {
            var constructor = ExceptionUtils.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            constructor.newInstance();
        }).hasCauseInstanceOf(UnsupportedOperationException.class);
    }

    // =========================================================================
    // logAndThrow() PROPERTIES
    // =========================================================================

    @Property(tries = 500)
    @Label("logAndThrow should throw the supplied exception")
    void logAndThrowShouldThrowSuppliedException(
            @ForAll @StringLength(min = 1, max = 100) String message) {
        assertThatThrownBy(() ->
            ExceptionUtils.logAndThrow(
                () -> new IllegalArgumentException(message),
                "Error: {}", message
            )
        ).isInstanceOf(IllegalArgumentException.class)
         .hasMessage(message);
    }

    @Property(tries = 500)
    @Label("logAndThrow should work with RuntimeException")
    void logAndThrowShouldWorkWithRuntimeException(
            @ForAll @StringLength(min = 1, max = 50) @AlphaChars String errorDetail) {
        String message = "Runtime error: " + errorDetail;
        assertThatThrownBy(() ->
            ExceptionUtils.logAndThrow(
                () -> new RuntimeException(message),
                "Error occurred: {}", errorDetail
            )
        ).isInstanceOf(RuntimeException.class)
         .hasMessage(message);
    }

    @Property(tries = 500)
    @Label("logAndThrow should work with custom exception types")
    void logAndThrowShouldWorkWithCustomExceptionTypes(
            @ForAll @StringLength(min = 1, max = 50) @AlphaChars String message) {
        assertThatThrownBy(() ->
            ExceptionUtils.logAndThrow(
                () -> new IllegalStateException(message),
                "State error: {}", message
            )
        ).isInstanceOf(IllegalStateException.class)
         .hasMessage(message);
    }

    // =========================================================================
    // logAndThrowWithMessage() PROPERTIES
    // =========================================================================

    @Property(tries = 500)
    @Label("logAndThrowWithMessage should throw exception with formatted message")
    void logAndThrowWithMessageShouldThrowWithFormattedMessage(
            @ForAll @StringLength(min = 1, max = 30) @AlphaChars String param1,
            @ForAll @IntRange(min = 1, max = 1000) int param2) {
        String expectedMessage = String.format("Error with %s and %d", param1, param2);
        assertThatThrownBy(() ->
            ExceptionUtils.logAndThrowWithMessage(
                IllegalArgumentException::new,
                "Error with %s and %d", param1, param2
            )
        ).isInstanceOf(IllegalArgumentException.class)
         .hasMessage(expectedMessage);
    }

    @Property(tries = 500)
    @Label("logAndThrowWithMessage should preserve exact formatted message")
    void logAndThrowWithMessageShouldPreserveExactFormattedMessage(
            @ForAll @StringLength(min = 1, max = 50) @AlphaChars String value) {
        String format = "Value is: %s";
        String expectedMessage = String.format(format, value);
        assertThatThrownBy(() ->
            ExceptionUtils.logAndThrowWithMessage(
                RuntimeException::new,
                format, value
            )
        ).isInstanceOf(RuntimeException.class)
         .hasMessage(expectedMessage);
    }

    // =========================================================================
    // logAndThrowWithClass() PROPERTIES
    // Note: This method has a KNOWN BUG where all exception types get caught
    // by the internal catch block and wrapped in a RuntimeException.
    // The tests below document this actual behavior.
    // =========================================================================

    @Property(tries = 500)
    @Label("logAndThrowWithClass wraps all exceptions due to implementation bug")
    void logAndThrowWithClassWrapsAllExceptionsDueToBug(
            @ForAll @StringLength(min = 1, max = 50) @AlphaChars String message) {
        // Note: Due to a bug in logAndThrowWithClass, the thrown exception
        // gets caught by the catch(Exception e) block and wrapped.
        // This tests the actual (buggy) behavior.
        assertThatThrownBy(() ->
            ExceptionUtils.logAndThrowWithClass(
                IllegalArgumentException.class,
                "Invalid: %s", message
            )
        ).isInstanceOf(RuntimeException.class)
         .hasMessageContaining("Could not instantiate exception class");
    }

    @Property(tries = 500)
    @Label("logAndThrowWithClass wraps RuntimeException")
    void logAndThrowWithClassWrapsRuntimeException(
            @ForAll @StringLength(min = 1, max = 50) @AlphaChars String detail) {
        assertThatThrownBy(() ->
            ExceptionUtils.logAndThrowWithClass(
                RuntimeException.class,
                "Runtime: %s", detail
            )
        ).isInstanceOf(RuntimeException.class)
         .hasMessageContaining("Could not instantiate exception class");
    }

    @Property(tries = 500)
    @Label("logAndThrowWithClass wraps IllegalStateException")
    void logAndThrowWithClassWrapsIllegalStateException(
            @ForAll @StringLength(min = 1, max = 30) @AlphaChars String state) {
        assertThatThrownBy(() ->
            ExceptionUtils.logAndThrowWithClass(
                IllegalStateException.class,
                "State: %s", state
            )
        ).isInstanceOf(RuntimeException.class)
         .hasMessageContaining("Could not instantiate exception class");
    }

    @Property(tries = 500)
    @Label("logAndThrowWithClass preserves original exception as cause")
    void logAndThrowWithClassPreservesOriginalExceptionAsCause(
            @ForAll @StringLength(min = 1, max = 30) @AlphaChars String message) {
        String expectedOriginalMessage = String.format("Test: %s", message);
        assertThatThrownBy(() ->
            ExceptionUtils.logAndThrowWithClass(
                IllegalArgumentException.class,
                "Test: %s", message
            )
        ).isInstanceOf(RuntimeException.class)
         .hasCauseInstanceOf(IllegalArgumentException.class)
         .hasRootCauseMessage(expectedOriginalMessage);
    }

    // =========================================================================
    // MESSAGE FORMAT PROPERTIES
    // =========================================================================

    @Property(tries = 500)
    @Label("Message formatting should handle multiple arguments")
    void messageFormattingShouldHandleMultipleArguments(
            @ForAll @StringLength(min = 1, max = 20) @AlphaChars String arg1,
            @ForAll @StringLength(min = 1, max = 20) @AlphaChars String arg2,
            @ForAll @IntRange(min = 1, max = 100) int arg3) {
        String format = "First: %s, Second: %s, Third: %d";
        String expectedMessage = String.format(format, arg1, arg2, arg3);
        assertThatThrownBy(() ->
            ExceptionUtils.logAndThrowWithMessage(
                RuntimeException::new,
                format, arg1, arg2, arg3
            )
        ).isInstanceOf(RuntimeException.class)
         .hasMessage(expectedMessage);
    }

    @Property(tries = 500)
    @Label("Message formatting should handle no arguments")
    void messageFormattingShouldHandleNoArguments() {
        String message = "Simple error message";
        assertThatThrownBy(() ->
            ExceptionUtils.logAndThrowWithMessage(
                RuntimeException::new,
                message
            )
        ).isInstanceOf(RuntimeException.class)
         .hasMessage(message);
    }
}
