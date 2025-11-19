package com.bonitasoft.processbuilder.extension;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * Unit tests for the {@link ExecuteSqlDatasource} class.
 * <p>
 * This class ensures that SQL datasource parameter validation works correctly,
 * particularly for pagination parameters 'p' (page) and 'c' (count).
 * </p>
 */
class ExecuteSqlDatasourceTest {

    private ExecuteSqlDatasource datasource;

    @BeforeEach
    void setUp() {
        datasource = new ExecuteSqlDatasource();
    }

    // -------------------------------------------------------------------------
    // Tests for validateInputParameters method
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("should accept valid numerical parameters")
    void validateInputParameters_should_accept_valid_parameters() {
        // Given valid numerical parameters
        String p = "1";
        String c = "10";

        // When validating parameters
        // Then no exception should be thrown
        assertDoesNotThrow(() -> datasource.validateInputParameters(p, c));
    }

    @Test
    @DisplayName("should throw exception if c is not numerical")
    void validateInputParameters_should_throw_exception_if_c_is_not_numerical() {
        // Given a non-numerical c parameter
        String p = "1";
        String c = "abc";

        // When validating parameters
        // Then ValidationException should be thrown with message containing "numerical value"
        assertThatThrownBy(() -> datasource.validateInputParameters(p, c))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("numerical value");
    }

    @Test
    @DisplayName("should throw exception if p is not numerical")
    void validateInputParameters_should_throw_exception_if_p_is_not_numerical() {
        // Given a non-numerical p parameter
        String p = "xyz";
        String c = "10";

        // When validating parameters
        // Then ValidationException should be thrown with message containing "numerical value"
        assertThatThrownBy(() -> datasource.validateInputParameters(p, c))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("numerical value");
    }

    @Test
    @DisplayName("should accept null parameters")
    void validateInputParameters_should_accept_null_parameters() {
        // Given null parameters
        String p = null;
        String c = null;

        // When validating parameters
        // Then no exception should be thrown (nulls are allowed)
        assertDoesNotThrow(() -> datasource.validateInputParameters(p, c));
    }

    @Test
    @DisplayName("should accept empty string parameters")
    void validateInputParameters_should_accept_empty_parameters() {
        // Given empty string parameters
        String p = "";
        String c = "";

        // When validating parameters
        // Then no exception should be thrown (empty strings are allowed)
        assertDoesNotThrow(() -> datasource.validateInputParameters(p, c));
    }

    @Test
    @DisplayName("should throw exception if c is decimal")
    void validateInputParameters_should_throw_exception_if_c_is_decimal() {
        // Given a decimal c parameter
        String p = "1";
        String c = "10.5";

        // When validating parameters
        // Then ValidationException should be thrown
        assertThatThrownBy(() -> datasource.validateInputParameters(p, c))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("numerical value");
    }

    @Test
    @DisplayName("should throw exception if p is decimal")
    void validateInputParameters_should_throw_exception_if_p_is_decimal() {
        // Given a decimal p parameter
        String p = "1.5";
        String c = "10";

        // When validating parameters
        // Then ValidationException should be thrown
        assertThatThrownBy(() -> datasource.validateInputParameters(p, c))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("numerical value");
    }

    @Test
    @DisplayName("should accept zero values")
    void validateInputParameters_should_accept_zero_values() {
        // Given zero values
        String p = "0";
        String c = "0";

        // When validating parameters
        // Then no exception should be thrown
        assertDoesNotThrow(() -> datasource.validateInputParameters(p, c));
    }

    @Test
    @DisplayName("should accept negative values")
    void validateInputParameters_should_accept_negative_values() {
        // Given negative values
        String p = "-1";
        String c = "-10";

        // When validating parameters
        // Then no exception should be thrown (validation only checks if numerical)
        assertDoesNotThrow(() -> datasource.validateInputParameters(p, c));
    }

    // -------------------------------------------------------------------------
    // Tests for execute method
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("should execute with valid parameters")
    void execute_should_work_with_valid_parameters() {
        // Given valid parameters
        String p = "2";
        String c = "20";

        // When executing
        // Then no exception should be thrown and result should be returned
        assertDoesNotThrow(() -> {
            Object result = datasource.execute(p, c);
            assertThat(result).isNotNull();
        });
    }

    @Test
    @DisplayName("should throw exception when executing with invalid c parameter")
    void execute_should_throw_exception_with_invalid_c() {
        // Given an invalid c parameter
        String p = "1";
        String c = "invalid";

        // When executing
        // Then ValidationException should be thrown
        assertThatThrownBy(() -> datasource.execute(p, c))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("numerical value");
    }

    @Test
    @DisplayName("should throw exception when executing with invalid p parameter")
    void execute_should_throw_exception_with_invalid_p() {
        // Given an invalid p parameter
        String p = "invalid";
        String c = "10";

        // When executing
        // Then ValidationException should be thrown
        assertThatThrownBy(() -> datasource.execute(p, c))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("numerical value");
    }

    @Test
    @DisplayName("should execute with null parameters using defaults")
    void execute_should_work_with_null_parameters() {
        // Given null parameters
        String p = null;
        String c = null;

        // When executing
        // Then no exception should be thrown
        assertDoesNotThrow(() -> {
            Object result = datasource.execute(p, c);
            assertThat(result).isNotNull();
        });
    }
}
