package com.bonitasoft.processbuilder.extension;

import org.bonitasoft.engine.connector.ConnectorValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link InputValidationUtils} utility class.
 * <p>
 * This class ensures that the utility class cannot be instantiated and
 * that all input validation methods work correctly across various scenarios,
 * including positive numbers, null values, and type mismatches.
 * </p>
 */
class InputValidationUtilsTest {

    // -------------------------------------------------------------------------
    // Utility Class Instantiation Test
    // -------------------------------------------------------------------------

    /**
     * Tests that the utility class has a private constructor and cannot be instantiated.
     */
    @Test
    @DisplayName("Should have private constructor to prevent instantiation")
    void shouldHavePrivateConstructor() throws Exception {
        // Given the InputValidationUtils class
        // When attempting to get the constructor
        Constructor<InputValidationUtils> constructor = InputValidationUtils.class.getDeclaredConstructor();

        // Then the constructor should be private
        assertTrue(Modifier.isPrivate(constructor.getModifiers()),
            "Constructor must be private to enforce utility class pattern.");
    }

    // -------------------------------------------------------------------------
    // checkPositiveIntegerInput Tests
    // -------------------------------------------------------------------------

    /**
     * Tests that checkPositiveIntegerInput accepts a valid positive Integer.
     */
    @Test
    @DisplayName("checkPositiveIntegerInput should accept valid positive Integer")
    void checkPositiveIntegerInput_should_accept_valid_positive_integer() {
        // Given a valid positive Integer
        Supplier<Object> inputGetter = () -> 42;

        // When validating the input
        // Then no exception should be thrown
        assertDoesNotThrow(() -> InputValidationUtils.checkPositiveIntegerInput("testParam", inputGetter));
    }

    /**
     * Tests that checkPositiveIntegerInput throws exception for null Integer.
     */
    @Test
    @DisplayName("checkPositiveIntegerInput should throw exception for null Integer")
    void checkPositiveIntegerInput_should_throw_exception_for_null() {
        // Given a null Integer
        Supplier<Object> inputGetter = () -> null;

        // When validating the input
        // Then ConnectorValidationException should be thrown
        ConnectorValidationException exception = assertThrows(ConnectorValidationException.class,
            () -> InputValidationUtils.checkPositiveIntegerInput("testParam", inputGetter));

        // Verify the exception message
        assertTrue(exception.getMessage().contains("testParam"));
        assertTrue(exception.getMessage().contains("positive"));
        assertTrue(exception.getMessage().contains("integer"));
    }

    /**
     * Tests that checkPositiveIntegerInput throws exception for zero Integer.
     */
    @Test
    @DisplayName("checkPositiveIntegerInput should throw exception for zero")
    void checkPositiveIntegerInput_should_throw_exception_for_zero() {
        // Given a zero Integer
        Supplier<Object> inputGetter = () -> 0;

        // When validating the input
        // Then ConnectorValidationException should be thrown
        ConnectorValidationException exception = assertThrows(ConnectorValidationException.class,
            () -> InputValidationUtils.checkPositiveIntegerInput("testParam", inputGetter));

        // Verify the exception message
        assertTrue(exception.getMessage().contains("testParam"));
        assertTrue(exception.getMessage().contains("positive"));
    }

    /**
     * Tests that checkPositiveIntegerInput throws exception for negative Integer.
     */
    @Test
    @DisplayName("checkPositiveIntegerInput should throw exception for negative Integer")
    void checkPositiveIntegerInput_should_throw_exception_for_negative() {
        // Given a negative Integer
        Supplier<Object> inputGetter = () -> -10;

        // When validating the input
        // Then ConnectorValidationException should be thrown
        ConnectorValidationException exception = assertThrows(ConnectorValidationException.class,
            () -> InputValidationUtils.checkPositiveIntegerInput("testParam", inputGetter));

        // Verify the exception message
        assertTrue(exception.getMessage().contains("testParam"));
        assertTrue(exception.getMessage().contains("positive"));
    }

    /**
     * Tests that checkPositiveIntegerInput throws exception for non-Integer type.
     */
    @Test
    @DisplayName("checkPositiveIntegerInput should throw exception for non-Integer type")
    void checkPositiveIntegerInput_should_throw_exception_for_wrong_type() {
        // Given a String instead of Integer
        Supplier<Object> inputGetter = () -> "not an integer";

        // When validating the input
        // Then ConnectorValidationException should be thrown
        ConnectorValidationException exception = assertThrows(ConnectorValidationException.class,
            () -> InputValidationUtils.checkPositiveIntegerInput("testParam", inputGetter));

        // Verify the exception message mentions the parameter must be an Integer
        assertTrue(exception.getMessage().contains("testParam"));
        assertTrue(exception.getMessage().contains("Integer"));
    }

    /**
     * Tests that checkPositiveIntegerInput throws exception for Long instead of Integer.
     */
    @Test
    @DisplayName("checkPositiveIntegerInput should throw exception for Long type")
    void checkPositiveIntegerInput_should_throw_exception_for_long_type() {
        // Given a Long instead of Integer
        Supplier<Object> inputGetter = () -> 42L;

        // When validating the input
        // Then ConnectorValidationException should be thrown
        ConnectorValidationException exception = assertThrows(ConnectorValidationException.class,
            () -> InputValidationUtils.checkPositiveIntegerInput("testParam", inputGetter));

        // Verify the exception message
        assertTrue(exception.getMessage().contains("testParam"));
        assertTrue(exception.getMessage().contains("Integer"));
    }

    // -------------------------------------------------------------------------
    // checkPositiveLongInput Tests
    // -------------------------------------------------------------------------

    /**
     * Tests that checkPositiveLongInput accepts a valid positive Long.
     */
    @Test
    @DisplayName("checkPositiveLongInput should accept valid positive Long")
    void checkPositiveLongInput_should_accept_valid_positive_long() {
        // Given a valid positive Long
        Supplier<Object> inputGetter = () -> 1000L;

        // When validating the input
        // Then no exception should be thrown
        assertDoesNotThrow(() -> InputValidationUtils.checkPositiveLongInput("testParam", inputGetter));
    }

    /**
     * Tests that checkPositiveLongInput throws exception for null Long.
     */
    @Test
    @DisplayName("checkPositiveLongInput should throw exception for null Long")
    void checkPositiveLongInput_should_throw_exception_for_null() {
        // Given a null Long
        Supplier<Object> inputGetter = () -> null;

        // When validating the input
        // Then ConnectorValidationException should be thrown
        ConnectorValidationException exception = assertThrows(ConnectorValidationException.class,
            () -> InputValidationUtils.checkPositiveLongInput("testParam", inputGetter));

        // Verify the exception message
        assertTrue(exception.getMessage().contains("testParam"));
        assertTrue(exception.getMessage().contains("positive"));
        assertTrue(exception.getMessage().contains("long"));
    }

    /**
     * Tests that checkPositiveLongInput throws exception for zero Long.
     */
    @Test
    @DisplayName("checkPositiveLongInput should throw exception for zero")
    void checkPositiveLongInput_should_throw_exception_for_zero() {
        // Given a zero Long
        Supplier<Object> inputGetter = () -> 0L;

        // When validating the input
        // Then ConnectorValidationException should be thrown
        ConnectorValidationException exception = assertThrows(ConnectorValidationException.class,
            () -> InputValidationUtils.checkPositiveLongInput("testParam", inputGetter));

        // Verify the exception message
        assertTrue(exception.getMessage().contains("testParam"));
        assertTrue(exception.getMessage().contains("positive"));
    }

    /**
     * Tests that checkPositiveLongInput throws exception for negative Long.
     */
    @Test
    @DisplayName("checkPositiveLongInput should throw exception for negative Long")
    void checkPositiveLongInput_should_throw_exception_for_negative() {
        // Given a negative Long
        Supplier<Object> inputGetter = () -> -100L;

        // When validating the input
        // Then ConnectorValidationException should be thrown
        ConnectorValidationException exception = assertThrows(ConnectorValidationException.class,
            () -> InputValidationUtils.checkPositiveLongInput("testParam", inputGetter));

        // Verify the exception message
        assertTrue(exception.getMessage().contains("testParam"));
        assertTrue(exception.getMessage().contains("positive"));
    }

    /**
     * Tests that checkPositiveLongInput throws exception for non-Long type.
     */
    @Test
    @DisplayName("checkPositiveLongInput should throw exception for non-Long type")
    void checkPositiveLongInput_should_throw_exception_for_wrong_type() {
        // Given a String instead of Long
        Supplier<Object> inputGetter = () -> "not a long";

        // When validating the input
        // Then ConnectorValidationException should be thrown
        ConnectorValidationException exception = assertThrows(ConnectorValidationException.class,
            () -> InputValidationUtils.checkPositiveLongInput("testParam", inputGetter));

        // Verify the exception message mentions the parameter must be a Long
        assertTrue(exception.getMessage().contains("testParam"));
        assertTrue(exception.getMessage().contains("Long"));
    }

    /**
     * Tests that checkPositiveLongInput throws exception for Integer instead of Long.
     */
    @Test
    @DisplayName("checkPositiveLongInput should throw exception for Integer type")
    void checkPositiveLongInput_should_throw_exception_for_integer_type() {
        // Given an Integer instead of Long
        Supplier<Object> inputGetter = () -> 42;

        // When validating the input
        // Then ConnectorValidationException should be thrown
        ConnectorValidationException exception = assertThrows(ConnectorValidationException.class,
            () -> InputValidationUtils.checkPositiveLongInput("testParam", inputGetter));

        // Verify the exception message
        assertTrue(exception.getMessage().contains("testParam"));
        assertTrue(exception.getMessage().contains("Long"));
    }

    /**
     * Tests that checkPositiveLongInput accepts very large positive Long values.
     */
    @Test
    @DisplayName("checkPositiveLongInput should accept very large positive Long")
    void checkPositiveLongInput_should_accept_large_positive_long() {
        // Given a very large positive Long
        Supplier<Object> inputGetter = () -> Long.MAX_VALUE;

        // When validating the input
        // Then no exception should be thrown
        assertDoesNotThrow(() -> InputValidationUtils.checkPositiveLongInput("testParam", inputGetter));
    }

    /**
     * Tests that checkPositiveIntegerInput accepts maximum Integer value.
     */
    @Test
    @DisplayName("checkPositiveIntegerInput should accept maximum Integer value")
    void checkPositiveIntegerInput_should_accept_max_integer() {
        // Given the maximum Integer value
        Supplier<Object> inputGetter = () -> Integer.MAX_VALUE;

        // When validating the input
        // Then no exception should be thrown
        assertDoesNotThrow(() -> InputValidationUtils.checkPositiveIntegerInput("testParam", inputGetter));
    }

    /**
     * Tests that checkPositiveIntegerInput accepts value of 1 (edge case).
     */
    @Test
    @DisplayName("checkPositiveIntegerInput should accept value of 1")
    void checkPositiveIntegerInput_should_accept_one() {
        // Given an Integer value of 1
        Supplier<Object> inputGetter = () -> 1;

        // When validating the input
        // Then no exception should be thrown
        assertDoesNotThrow(() -> InputValidationUtils.checkPositiveIntegerInput("testParam", inputGetter));
    }

    /**
     * Tests that checkPositiveLongInput accepts value of 1 (edge case).
     */
    @Test
    @DisplayName("checkPositiveLongInput should accept value of 1")
    void checkPositiveLongInput_should_accept_one() {
        // Given a Long value of 1
        Supplier<Object> inputGetter = () -> 1L;

        // When validating the input
        // Then no exception should be thrown
        assertDoesNotThrow(() -> InputValidationUtils.checkPositiveLongInput("testParam", inputGetter));
    }
}
