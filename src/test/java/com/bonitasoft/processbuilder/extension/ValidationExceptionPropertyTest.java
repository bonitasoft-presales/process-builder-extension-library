package com.bonitasoft.processbuilder.extension;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

import static org.assertj.core.api.Assertions.*;

/**
 * Property-based tests for {@link ValidationException} class.
 * Tests exception behavior invariants.
 *
 * @author Bonitasoft
 * @since 1.0
 */
@Label("ValidationException Property-Based Tests")
class ValidationExceptionPropertyTest {

    // =========================================================================
    // MESSAGE CONSTRUCTOR PROPERTIES
    // =========================================================================

    @Property(tries = 300)
    @Label("Constructor with message should preserve the message")
    void constructorWithMessageShouldPreserveMessage(
            @ForAll @StringLength(min = 1, max = 200) String message) {

        ValidationException exception = new ValidationException(message);

        assertThat(exception.getMessage()).isEqualTo(message);
    }

    @Property(tries = 300)
    @Label("Constructor with message should have null cause")
    void constructorWithMessageShouldHaveNullCause(
            @ForAll @StringLength(min = 1, max = 200) String message) {

        ValidationException exception = new ValidationException(message);

        assertThat(exception.getCause()).isNull();
    }

    @Property(tries = 100)
    @Label("Constructor should accept null message")
    void constructorShouldAcceptNullMessage() {
        ValidationException exception = new ValidationException(null);

        assertThat(exception.getMessage()).isNull();
    }

    // =========================================================================
    // MESSAGE AND CAUSE CONSTRUCTOR PROPERTIES
    // =========================================================================

    @Property(tries = 300)
    @Label("Constructor with message and cause should preserve both")
    void constructorWithMessageAndCauseShouldPreserveBoth(
            @ForAll @StringLength(min = 1, max = 200) String message,
            @ForAll @StringLength(min = 1, max = 100) String causeMessage) {

        Throwable cause = new RuntimeException(causeMessage);
        ValidationException exception = new ValidationException(message, cause);

        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getCause()).isEqualTo(cause);
    }

    @Property(tries = 300)
    @Label("Constructor with cause should preserve the cause message")
    void constructorWithCauseShouldPreserveCauseMessage(
            @ForAll @StringLength(min = 1, max = 200) String message,
            @ForAll @StringLength(min = 1, max = 100) String causeMessage) {

        Throwable cause = new RuntimeException(causeMessage);
        ValidationException exception = new ValidationException(message, cause);

        assertThat(exception.getCause().getMessage()).isEqualTo(causeMessage);
    }

    @Property(tries = 100)
    @Label("Constructor should accept null cause")
    void constructorShouldAcceptNullCause(
            @ForAll @StringLength(min = 1, max = 200) String message) {

        ValidationException exception = new ValidationException(message, null);

        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getCause()).isNull();
    }

    // =========================================================================
    // EXCEPTION HIERARCHY PROPERTIES
    // =========================================================================

    @Property(tries = 300)
    @Label("ValidationException should be an instance of Exception")
    void shouldBeInstanceOfException(
            @ForAll @StringLength(min = 1, max = 200) String message) {

        ValidationException exception = new ValidationException(message);

        assertThat(exception).isInstanceOf(Exception.class);
    }

    @Property(tries = 300)
    @Label("ValidationException should be throwable")
    void shouldBeThrowable(
            @ForAll @StringLength(min = 1, max = 200) String message) {

        ValidationException exception = new ValidationException(message);

        assertThatThrownBy(() -> {
            throw exception;
        }).isInstanceOf(ValidationException.class)
          .hasMessage(message);
    }

    // =========================================================================
    // TOSTRING PROPERTIES
    // =========================================================================

    @Property(tries = 300)
    @Label("toString should never return null")
    void toStringShouldNeverReturnNull(
            @ForAll @StringLength(min = 1, max = 200) String message) {

        ValidationException exception = new ValidationException(message);

        assertThat(exception.toString()).isNotNull();
    }

    @Property(tries = 300)
    @Label("toString should contain the message")
    void toStringShouldContainMessage(
            @ForAll @StringLength(min = 1, max = 200) @AlphaChars String message) {

        ValidationException exception = new ValidationException(message);

        assertThat(exception.toString()).contains(message);
    }

    @Property(tries = 300)
    @Label("toString should contain the class name")
    void toStringShouldContainClassName(
            @ForAll @StringLength(min = 1, max = 200) String message) {

        ValidationException exception = new ValidationException(message);

        assertThat(exception.toString()).contains("ValidationException");
    }

    // =========================================================================
    // STACK TRACE PROPERTIES
    // =========================================================================

    @Property(tries = 100)
    @Label("getStackTrace should never return null")
    void getStackTraceShouldNeverReturnNull(
            @ForAll @StringLength(min = 1, max = 200) String message) {

        ValidationException exception = new ValidationException(message);

        assertThat(exception.getStackTrace()).isNotNull();
    }

    @Property(tries = 100)
    @Label("getStackTrace should have at least one element")
    void getStackTraceShouldHaveElements(
            @ForAll @StringLength(min = 1, max = 200) String message) {

        ValidationException exception = new ValidationException(message);

        assertThat(exception.getStackTrace()).isNotEmpty();
    }
}
