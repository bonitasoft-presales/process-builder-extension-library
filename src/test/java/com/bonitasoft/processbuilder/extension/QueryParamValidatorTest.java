package com.bonitasoft.processbuilder.extension;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the {@link QueryParamValidator} utility class.
 * <p>
 * This class ensures that query parameter validation works correctly across
 * various scenarios, including valid values, null values, empty strings,
 * and invalid formats.
 * </p>
 */
class QueryParamValidatorTest {

    // -------------------------------------------------------------------------
    // Utility Class Instantiation Test
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("should have private constructor to prevent instantiation")
    void shouldHavePrivateConstructor() throws Exception {
        // Given the QueryParamValidator class
        // When attempting to get the constructor
        Constructor<QueryParamValidator> constructor = QueryParamValidator.class.getDeclaredConstructor();

        // Then the constructor should be private
        assertTrue(Modifier.isPrivate(constructor.getModifiers()),
                "Constructor must be private to enforce utility class pattern.");
    }

    @Test
    @DisplayName("should throw UnsupportedOperationException when trying to instantiate")
    void shouldThrowExceptionWhenInstantiating() throws Exception {
        // Given the private constructor
        Constructor<QueryParamValidator> constructor = QueryParamValidator.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        // When attempting to instantiate
        // Then UnsupportedOperationException should be thrown
        assertThatThrownBy(constructor::newInstance)
                .hasCauseInstanceOf(UnsupportedOperationException.class);
    }

    // -------------------------------------------------------------------------
    // Tests for validateMandatoryLong method
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("validateMandatoryLong should accept valid long value")
    void validateMandatoryLong_should_accept_valid_long() {
        // Given a valid long value as string
        String paramValue = "123456789";

        // When validating
        // Then no exception should be thrown
        assertDoesNotThrow(() -> QueryParamValidator.validateMandatoryLong("testParam", paramValue));
    }

    @Test
    @DisplayName("validateMandatoryLong should accept Long.MAX_VALUE")
    void validateMandatoryLong_should_accept_max_long() {
        // Given Long.MAX_VALUE as string
        String paramValue = String.valueOf(Long.MAX_VALUE);

        // When validating
        // Then no exception should be thrown
        assertDoesNotThrow(() -> QueryParamValidator.validateMandatoryLong("testParam", paramValue));
    }

    @Test
    @DisplayName("validateMandatoryLong should accept Long.MIN_VALUE")
    void validateMandatoryLong_should_accept_min_long() {
        // Given Long.MIN_VALUE as string
        String paramValue = String.valueOf(Long.MIN_VALUE);

        // When validating
        // Then no exception should be thrown
        assertDoesNotThrow(() -> QueryParamValidator.validateMandatoryLong("testParam", paramValue));
    }

    @Test
    @DisplayName("validateMandatoryLong should accept negative long values")
    void validateMandatoryLong_should_accept_negative_long() {
        // Given a negative long value
        String paramValue = "-12345";

        // When validating
        // Then no exception should be thrown
        assertDoesNotThrow(() -> QueryParamValidator.validateMandatoryLong("testParam", paramValue));
    }

    @Test
    @DisplayName("validateMandatoryLong should throw exception for null value")
    void validateMandatoryLong_should_throw_exception_for_null() {
        // Given a null value
        String paramValue = null;

        // When validating
        // Then ValidationException should be thrown
        assertThatThrownBy(() -> QueryParamValidator.validateMandatoryLong("testParam", paramValue))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("mandatory");
    }

    @Test
    @DisplayName("validateMandatoryLong should throw exception for empty string")
    void validateMandatoryLong_should_throw_exception_for_empty() {
        // Given an empty string
        String paramValue = "";

        // When validating
        // Then ValidationException should be thrown
        assertThatThrownBy(() -> QueryParamValidator.validateMandatoryLong("testParam", paramValue))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("mandatory");
    }

    @Test
    @DisplayName("validateMandatoryLong should throw exception for whitespace only")
    void validateMandatoryLong_should_throw_exception_for_whitespace() {
        // Given a whitespace-only string
        String paramValue = "   ";

        // When validating
        // Then ValidationException should be thrown
        assertThatThrownBy(() -> QueryParamValidator.validateMandatoryLong("testParam", paramValue))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("mandatory");
    }

    @Test
    @DisplayName("validateMandatoryLong should throw exception for non-numeric value")
    void validateMandatoryLong_should_throw_exception_for_non_numeric() {
        // Given a non-numeric value
        String paramValue = "abc123";

        // When validating
        // Then ValidationException should be thrown with message containing "long"
        assertThatThrownBy(() -> QueryParamValidator.validateMandatoryLong("testParam", paramValue))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("long");
    }

    @Test
    @DisplayName("validateMandatoryLong should throw exception for decimal value")
    void validateMandatoryLong_should_throw_exception_for_decimal() {
        // Given a decimal value
        String paramValue = "123.45";

        // When validating
        // Then ValidationException should be thrown with message containing "long"
        assertThatThrownBy(() -> QueryParamValidator.validateMandatoryLong("testParam", paramValue))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("long");
    }

    @Test
    @DisplayName("validateMandatoryLong should throw exception for overflow value")
    void validateMandatoryLong_should_throw_exception_for_overflow() {
        // Given a value that exceeds Long.MAX_VALUE
        String paramValue = "9223372036854775808"; // Long.MAX_VALUE + 1

        // When validating
        // Then ValidationException should be thrown with message containing "long"
        assertThatThrownBy(() -> QueryParamValidator.validateMandatoryLong("testParam", paramValue))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("long");
    }

    // -------------------------------------------------------------------------
    // Tests for validateNumerical method
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("validateNumerical should accept valid integer value")
    void validateNumerical_should_accept_valid_integer() {
        // Given a valid integer value as string
        String paramValue = "42";

        // When validating
        // Then no exception should be thrown
        assertDoesNotThrow(() -> QueryParamValidator.validateNumerical("testParam", paramValue));
    }

    @Test
    @DisplayName("validateNumerical should accept Integer.MAX_VALUE")
    void validateNumerical_should_accept_max_integer() {
        // Given Integer.MAX_VALUE as string
        String paramValue = String.valueOf(Integer.MAX_VALUE);

        // When validating
        // Then no exception should be thrown
        assertDoesNotThrow(() -> QueryParamValidator.validateNumerical("testParam", paramValue));
    }

    @Test
    @DisplayName("validateNumerical should accept Integer.MIN_VALUE")
    void validateNumerical_should_accept_min_integer() {
        // Given Integer.MIN_VALUE as string
        String paramValue = String.valueOf(Integer.MIN_VALUE);

        // When validating
        // Then no exception should be thrown
        assertDoesNotThrow(() -> QueryParamValidator.validateNumerical("testParam", paramValue));
    }

    @Test
    @DisplayName("validateNumerical should accept negative integer values")
    void validateNumerical_should_accept_negative_integer() {
        // Given a negative integer value
        String paramValue = "-100";

        // When validating
        // Then no exception should be thrown
        assertDoesNotThrow(() -> QueryParamValidator.validateNumerical("testParam", paramValue));
    }

    @Test
    @DisplayName("validateNumerical should accept zero")
    void validateNumerical_should_accept_zero() {
        // Given zero as string
        String paramValue = "0";

        // When validating
        // Then no exception should be thrown
        assertDoesNotThrow(() -> QueryParamValidator.validateNumerical("testParam", paramValue));
    }

    @Test
    @DisplayName("validateNumerical should throw exception for null value")
    void validateNumerical_should_throw_exception_for_null() {
        // Given a null value
        String paramValue = null;

        // When validating
        // Then ValidationException should be thrown
        assertThatThrownBy(() -> QueryParamValidator.validateNumerical("testParam", paramValue))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("mandatory");
    }

    @Test
    @DisplayName("validateNumerical should throw exception for empty string")
    void validateNumerical_should_throw_exception_for_empty() {
        // Given an empty string
        String paramValue = "";

        // When validating
        // Then ValidationException should be thrown
        assertThatThrownBy(() -> QueryParamValidator.validateNumerical("testParam", paramValue))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("mandatory");
    }

    @Test
    @DisplayName("validateNumerical should throw exception for whitespace only")
    void validateNumerical_should_throw_exception_for_whitespace() {
        // Given a whitespace-only string
        String paramValue = "   ";

        // When validating
        // Then ValidationException should be thrown
        assertThatThrownBy(() -> QueryParamValidator.validateNumerical("testParam", paramValue))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("mandatory");
    }

    @Test
    @DisplayName("validateNumerical should throw exception for non-numeric value")
    void validateNumerical_should_throw_exception_for_non_numeric() {
        // Given a non-numeric value
        String paramValue = "abc";

        // When validating
        // Then ValidationException should be thrown with message containing "numerical value"
        assertThatThrownBy(() -> QueryParamValidator.validateNumerical("testParam", paramValue))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("numerical value");
    }

    @Test
    @DisplayName("validateNumerical should throw exception for decimal value")
    void validateNumerical_should_throw_exception_for_decimal() {
        // Given a decimal value
        String paramValue = "10.5";

        // When validating
        // Then ValidationException should be thrown with message containing "numerical value"
        assertThatThrownBy(() -> QueryParamValidator.validateNumerical("testParam", paramValue))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("numerical value");
    }

    @Test
    @DisplayName("validateNumerical should throw exception for overflow value")
    void validateNumerical_should_throw_exception_for_overflow() {
        // Given a value that exceeds Integer.MAX_VALUE
        String paramValue = "2147483648"; // Integer.MAX_VALUE + 1

        // When validating
        // Then ValidationException should be thrown with message containing "numerical value"
        assertThatThrownBy(() -> QueryParamValidator.validateNumerical("testParam", paramValue))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("numerical value");
    }

    @Test
    @DisplayName("validateNumerical should throw exception for mixed alphanumeric")
    void validateNumerical_should_throw_exception_for_mixed_alphanumeric() {
        // Given a mixed alphanumeric value
        String paramValue = "123abc";

        // When validating
        // Then ValidationException should be thrown
        assertThatThrownBy(() -> QueryParamValidator.validateNumerical("testParam", paramValue))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("numerical value");
    }
}
