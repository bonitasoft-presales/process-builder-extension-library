package com.bonitasoft.processbuilder.extension;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

import static org.assertj.core.api.Assertions.*;

/**
 * Property-based tests for {@link ProcessInstance} class.
 * These tests verify the actual return values to kill mutations.
 *
 * @author Bonitasoft
 * @since 1.0
 */
@Label("ProcessInstance Property-Based Tests")
class ProcessInstancePropertyTest {

    private final ProcessInstance processInstance = new ProcessInstance();

    // =========================================================================
    // getByCaseId PROPERTIES
    // =========================================================================

    @Property(tries = 500)
    @Label("getByCaseId should return the exact parsed long value")
    void getByCaseIdShouldReturnExactParsedValue(
            @ForAll @LongRange(min = Long.MIN_VALUE + 1, max = Long.MAX_VALUE) long value) throws ValidationException {

        String caseIdParam = String.valueOf(value);

        long result = processInstance.getByCaseId(caseIdParam);

        // This kills mutations that return different values
        assertThat(result).isEqualTo(value);
        assertThat(result).isEqualTo(Long.parseLong(caseIdParam));
    }

    @Property(tries = 300)
    @Label("getByCaseId should handle positive values correctly")
    void getByCaseIdShouldHandlePositiveValues(
            @ForAll @LongRange(min = 1, max = Long.MAX_VALUE) long value) throws ValidationException {

        String caseIdParam = String.valueOf(value);

        long result = processInstance.getByCaseId(caseIdParam);

        assertThat(result).isPositive();
        assertThat(result).isEqualTo(value);
    }

    @Property(tries = 300)
    @Label("getByCaseId should handle negative values correctly")
    void getByCaseIdShouldHandleNegativeValues(
            @ForAll @LongRange(min = Long.MIN_VALUE + 1, max = -1) long value) throws ValidationException {

        String caseIdParam = String.valueOf(value);

        long result = processInstance.getByCaseId(caseIdParam);

        assertThat(result).isNegative();
        assertThat(result).isEqualTo(value);
    }

    @Property(tries = 100)
    @Label("getByCaseId should handle zero correctly")
    void getByCaseIdShouldHandleZero() throws ValidationException {
        long result = processInstance.getByCaseId("0");

        assertThat(result).isZero();
    }

    @Property(tries = 300)
    @Label("getByCaseId should throw ValidationException for non-numeric strings")
    void getByCaseIdShouldThrowForNonNumeric(
            @ForAll @StringLength(min = 1, max = 20) @AlphaChars String invalidParam) {

        assertThatThrownBy(() -> processInstance.getByCaseId(invalidParam))
                .isInstanceOf(ValidationException.class);
    }

    @Property(tries = 100)
    @Label("getByCaseId should throw ValidationException for null")
    void getByCaseIdShouldThrowForNull() {
        assertThatThrownBy(() -> processInstance.getByCaseId(null))
                .isInstanceOf(ValidationException.class);
    }

    @Property(tries = 100)
    @Label("getByCaseId should throw ValidationException for empty string")
    void getByCaseIdShouldThrowForEmpty() {
        assertThatThrownBy(() -> processInstance.getByCaseId(""))
                .isInstanceOf(ValidationException.class);
    }

    // =========================================================================
    // getById PROPERTIES
    // =========================================================================

    @Property(tries = 500)
    @Label("getById should return the exact parsed long value")
    void getByIdShouldReturnExactParsedValue(
            @ForAll @LongRange(min = Long.MIN_VALUE + 1, max = Long.MAX_VALUE) long value) throws ValidationException {

        String idParam = String.valueOf(value);

        long result = processInstance.getById(idParam);

        // This kills mutations that return different values
        assertThat(result).isEqualTo(value);
        assertThat(result).isEqualTo(Long.parseLong(idParam));
    }

    @Property(tries = 300)
    @Label("getById should handle positive values correctly")
    void getByIdShouldHandlePositiveValues(
            @ForAll @LongRange(min = 1, max = Long.MAX_VALUE) long value) throws ValidationException {

        String idParam = String.valueOf(value);

        long result = processInstance.getById(idParam);

        assertThat(result).isPositive();
        assertThat(result).isEqualTo(value);
    }

    @Property(tries = 300)
    @Label("getById should handle negative values correctly")
    void getByIdShouldHandleNegativeValues(
            @ForAll @LongRange(min = Long.MIN_VALUE + 1, max = -1) long value) throws ValidationException {

        String idParam = String.valueOf(value);

        long result = processInstance.getById(idParam);

        assertThat(result).isNegative();
        assertThat(result).isEqualTo(value);
    }

    @Property(tries = 100)
    @Label("getById should handle zero correctly")
    void getByIdShouldHandleZero() throws ValidationException {
        long result = processInstance.getById("0");

        assertThat(result).isZero();
    }

    @Property(tries = 300)
    @Label("getById should throw ValidationException for non-numeric strings")
    void getByIdShouldThrowForNonNumeric(
            @ForAll @StringLength(min = 1, max = 20) @AlphaChars String invalidParam) {

        assertThatThrownBy(() -> processInstance.getById(invalidParam))
                .isInstanceOf(ValidationException.class);
    }

    @Property(tries = 100)
    @Label("getById should throw ValidationException for null")
    void getByIdShouldThrowForNull() {
        assertThatThrownBy(() -> processInstance.getById(null))
                .isInstanceOf(ValidationException.class);
    }

    @Property(tries = 100)
    @Label("getById should throw ValidationException for empty string")
    void getByIdShouldThrowForEmpty() {
        assertThatThrownBy(() -> processInstance.getById(""))
                .isInstanceOf(ValidationException.class);
    }

    // =========================================================================
    // EDGE CASES PROPERTIES
    // =========================================================================

    @Property(tries = 100)
    @Label("getByCaseId and getById should return same value for same input")
    void bothMethodsShouldReturnSameValueForSameInput(
            @ForAll @LongRange(min = 0, max = Long.MAX_VALUE) long value) throws ValidationException {

        String param = String.valueOf(value);

        long caseIdResult = processInstance.getByCaseId(param);
        long idResult = processInstance.getById(param);

        assertThat(caseIdResult).isEqualTo(idResult);
        assertThat(caseIdResult).isEqualTo(value);
    }

    @Property(tries = 200)
    @Label("Methods should handle Long.MAX_VALUE correctly")
    void shouldHandleMaxLongValue() throws ValidationException {
        String maxValue = String.valueOf(Long.MAX_VALUE);

        long caseIdResult = processInstance.getByCaseId(maxValue);
        long idResult = processInstance.getById(maxValue);

        assertThat(caseIdResult).isEqualTo(Long.MAX_VALUE);
        assertThat(idResult).isEqualTo(Long.MAX_VALUE);
    }

    @Property(tries = 200)
    @Label("Methods should handle Long.MIN_VALUE correctly")
    void shouldHandleMinLongValue() throws ValidationException {
        String minValue = String.valueOf(Long.MIN_VALUE);

        long caseIdResult = processInstance.getByCaseId(minValue);
        long idResult = processInstance.getById(minValue);

        assertThat(caseIdResult).isEqualTo(Long.MIN_VALUE);
        assertThat(idResult).isEqualTo(Long.MIN_VALUE);
    }
}
