package com.bonitasoft.processbuilder.extension;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * Unit tests for the {@link ProcessInstance} class.
 * <p>
 * This class ensures that process instance validation works correctly for
 * caseId and id parameters, including handling of non-numeric values,
 * overflow conditions, and decimal values.
 * </p>
 */
class ProcessInstanceTest {

    private ProcessInstance processInstance;

    @BeforeEach
    void setUp() {
        processInstance = new ProcessInstance();
    }

    // -------------------------------------------------------------------------
    // Tests for getByCaseId method
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("should accept valid caseId as long")
    void should_accept_valid_caseId() {
        // Given a valid caseId
        String caseId = "12345";

        // When validating and retrieving by caseId
        // Then no exception should be thrown
        assertDoesNotThrow(() -> processInstance.getByCaseId(caseId));
    }

    @Test
    @DisplayName("should throw ValidationException when caseId is not numeric")
    void should_throw_ValidationException_when_caseId_is_not_numeric() {
        // Given a non-numeric caseId
        String caseId = "abc123";

        // When validating caseId
        // Then ValidationException should be thrown with message containing "long"
        assertThatThrownBy(() -> processInstance.getByCaseId(caseId))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("long");
    }

    // -------------------------------------------------------------------------
    // Tests for getById method
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("should accept valid id as long")
    void should_accept_valid_id() {
        // Given a valid id
        String id = "67890";

        // When validating and retrieving by id
        // Then no exception should be thrown
        assertDoesNotThrow(() -> processInstance.getById(id));
    }

    @Test
    @DisplayName("should throw ValidationException when id is not numeric")
    void should_throw_ValidationException_when_id_is_not_numeric() {
        // Given a non-numeric id
        String id = "xyz789";

        // When validating id
        // Then ValidationException should be thrown with message containing "long"
        assertThatThrownBy(() -> processInstance.getById(id))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("long");
    }

    @Test
    @DisplayName("should handle id overflow")
    void should_handle_id_overflow() {
        // Given an id value that exceeds Long.MAX_VALUE
        String id = "9223372036854775808"; // Long.MAX_VALUE + 1

        // When validating id
        // Then ValidationException should be thrown with message containing "long"
        assertThatThrownBy(() -> processInstance.getById(id))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("long");
    }

    @Test
    @DisplayName("should throw ValidationException when id is decimal")
    void should_throw_ValidationException_when_id_is_decimal() {
        // Given a decimal id value
        String id = "123.45";

        // When validating id
        // Then ValidationException should be thrown with message containing "long"
        assertThatThrownBy(() -> processInstance.getById(id))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("long");
    }

    @Test
    @DisplayName("should throw ValidationException when caseId is null")
    void should_throw_ValidationException_when_caseId_is_null() {
        // Given a null caseId
        String caseId = null;

        // When validating caseId
        // Then ValidationException should be thrown
        assertThatThrownBy(() -> processInstance.getByCaseId(caseId))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    @DisplayName("should throw ValidationException when id is null")
    void should_throw_ValidationException_when_id_is_null() {
        // Given a null id
        String id = null;

        // When validating id
        // Then ValidationException should be thrown
        assertThatThrownBy(() -> processInstance.getById(id))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    @DisplayName("should throw ValidationException when caseId is empty")
    void should_throw_ValidationException_when_caseId_is_empty() {
        // Given an empty caseId
        String caseId = "";

        // When validating caseId
        // Then ValidationException should be thrown
        assertThatThrownBy(() -> processInstance.getByCaseId(caseId))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    @DisplayName("should throw ValidationException when id is empty")
    void should_throw_ValidationException_when_id_is_empty() {
        // Given an empty id
        String id = "";

        // When validating id
        // Then ValidationException should be thrown
        assertThatThrownBy(() -> processInstance.getById(id))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    @DisplayName("should accept maximum valid long value")
    void should_accept_maximum_valid_long_value() {
        // Given Long.MAX_VALUE as string
        String id = String.valueOf(Long.MAX_VALUE);

        // When validating id
        // Then no exception should be thrown
        assertDoesNotThrow(() -> processInstance.getById(id));
    }

    @Test
    @DisplayName("should accept negative long values")
    void should_accept_negative_long_values() {
        // Given a negative long value
        String id = "-12345";

        // When validating id
        // Then no exception should be thrown
        assertDoesNotThrow(() -> processInstance.getById(id));
    }
}
