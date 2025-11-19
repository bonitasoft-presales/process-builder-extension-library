package com.bonitasoft.processbuilder.extension;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for the {@link ValidationException} class.
 * <p>
 * This class ensures that ValidationException properly handles messages and causes.
 * </p>
 */
class ValidationExceptionTest {

    @Test
    @DisplayName("should create exception with message")
    void should_create_exception_with_message() {
        // Given a message
        String message = "Validation failed";

        // When creating exception
        ValidationException exception = new ValidationException(message);

        // Then the message should be set correctly
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getCause()).isNull();
    }

    @Test
    @DisplayName("should create exception with message and cause")
    void should_create_exception_with_message_and_cause() {
        // Given a message and cause
        String message = "Validation failed";
        Throwable cause = new IllegalArgumentException("Invalid argument");

        // When creating exception
        ValidationException exception = new ValidationException(message, cause);

        // Then the message and cause should be set correctly
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getCause()).isEqualTo(cause);
    }

    @Test
    @DisplayName("should be throwable")
    void should_be_throwable() {
        // Given a ValidationException
        ValidationException exception = new ValidationException("Test");

        // When checking instance
        // Then it should be a Throwable and Exception
        assertThat(exception).isInstanceOf(Throwable.class);
        assertThat(exception).isInstanceOf(Exception.class);
    }

    @Test
    @DisplayName("should preserve stack trace")
    void should_preserve_stack_trace() {
        // Given an exception thrown from a method
        ValidationException exception = createException();

        // When examining stack trace
        // Then it should contain the method name
        assertThat(exception.getStackTrace()).isNotEmpty();
        assertThat(exception.getStackTrace()[0].getMethodName()).isEqualTo("createException");
    }

    private ValidationException createException() {
        return new ValidationException("Stack trace test");
    }

    @Test
    @DisplayName("should handle null message")
    void should_handle_null_message() {
        // Given a null message
        String message = null;

        // When creating exception
        ValidationException exception = new ValidationException(message);

        // Then the exception should be created with null message
        assertThat(exception.getMessage()).isNull();
    }

    @Test
    @DisplayName("should handle null cause")
    void should_handle_null_cause() {
        // Given a null cause
        Throwable cause = null;

        // When creating exception with message and null cause
        ValidationException exception = new ValidationException("Test message", cause);

        // Then the exception should be created with null cause
        assertThat(exception.getCause()).isNull();
    }
}
