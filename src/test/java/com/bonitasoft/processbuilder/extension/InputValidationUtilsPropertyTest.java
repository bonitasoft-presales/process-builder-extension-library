package com.bonitasoft.processbuilder.extension;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

/**
 * Property-based tests for {@link InputValidationUtils} utility class.
 * Tests invariants that must hold for any valid input.
 *
 * @author Bonitasoft
 * @since 1.0
 */
@Label("InputValidationUtils Property-Based Tests")
class InputValidationUtilsPropertyTest {

    // =========================================================================
    // UTILITY CLASS INVARIANT PROPERTIES
    // =========================================================================

    @Property(tries = 100)
    @Label("Utility class should not be instantiable")
    void utilityClassShouldNotBeInstantiable() {
        assertThatThrownBy(() -> {
            var constructor = InputValidationUtils.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            constructor.newInstance();
        }).hasCauseInstanceOf(UnsupportedOperationException.class);
    }

    // =========================================================================
    // parseStringToPositiveLong() PROPERTIES
    // =========================================================================

    @Property(tries = 100)
    @Label("parseStringToPositiveLong should return empty for null input")
    void parseStringToPositiveLongShouldReturnEmptyForNull() {
        Optional<Long> result = InputValidationUtils.parseStringToPositiveLong(null, "testParam");
        assertThat(result).isEmpty();
    }

    @Property(tries = 100)
    @Label("parseStringToPositiveLong should return empty for empty input")
    void parseStringToPositiveLongShouldReturnEmptyForEmpty() {
        assertThat(InputValidationUtils.parseStringToPositiveLong("", "testParam")).isEmpty();
        assertThat(InputValidationUtils.parseStringToPositiveLong("   ", "testParam")).isEmpty();
    }

    @Property(tries = 100)
    @Label("parseStringToPositiveLong should return empty for 'null' string")
    void parseStringToPositiveLongShouldReturnEmptyForNullString() {
        assertThat(InputValidationUtils.parseStringToPositiveLong("null", "testParam")).isEmpty();
        assertThat(InputValidationUtils.parseStringToPositiveLong("NULL", "testParam")).isEmpty();
        assertThat(InputValidationUtils.parseStringToPositiveLong("Null", "testParam")).isEmpty();
    }

    @Property(tries = 300)
    @Label("parseStringToPositiveLong should return value for positive numbers")
    void parseStringToPositiveLongShouldReturnValueForPositive(
            @ForAll @LongRange(min = 1, max = Long.MAX_VALUE) Long positiveValue) {
        String input = String.valueOf(positiveValue);
        Optional<Long> result = InputValidationUtils.parseStringToPositiveLong(input, "testParam");
        assertThat(result).isPresent().hasValue(positiveValue);
    }

    @Property(tries = 100)
    @Label("parseStringToPositiveLong should return empty for zero")
    void parseStringToPositiveLongShouldReturnEmptyForZero() {
        assertThat(InputValidationUtils.parseStringToPositiveLong("0", "testParam")).isEmpty();
    }

    @Property(tries = 200)
    @Label("parseStringToPositiveLong should return empty for negative numbers")
    void parseStringToPositiveLongShouldReturnEmptyForNegative(
            @ForAll @LongRange(min = Long.MIN_VALUE, max = -1) Long negativeValue) {
        String input = String.valueOf(negativeValue);
        Optional<Long> result = InputValidationUtils.parseStringToPositiveLong(input, "testParam");
        assertThat(result).isEmpty();
    }

    @Property(tries = 200)
    @Label("parseStringToPositiveLong should return empty for non-numeric strings")
    void parseStringToPositiveLongShouldReturnEmptyForNonNumeric(
            @ForAll @StringLength(min = 1, max = 20) @AlphaChars String input) {
        Optional<Long> result = InputValidationUtils.parseStringToPositiveLong(input, "testParam");
        assertThat(result).isEmpty();
    }

    @Property(tries = 200)
    @Label("parseStringToPositiveLong should trim whitespace")
    void parseStringToPositiveLongShouldTrimWhitespace(
            @ForAll @LongRange(min = 1, max = 1000000) Long positiveValue) {
        String input = "  " + positiveValue + "  ";
        Optional<Long> result = InputValidationUtils.parseStringToPositiveLong(input, "testParam");
        assertThat(result).isPresent().hasValue(positiveValue);
    }

    // =========================================================================
    // parseStringToLong() PROPERTIES
    // =========================================================================

    @Property(tries = 100)
    @Label("parseStringToLong should return null for null input")
    void parseStringToLongShouldReturnNullForNull() {
        Long result = InputValidationUtils.parseStringToLong(null, "testParam");
        assertThat(result).isNull();
    }

    @Property(tries = 100)
    @Label("parseStringToLong should return null for empty input")
    void parseStringToLongShouldReturnNullForEmpty() {
        assertThat(InputValidationUtils.parseStringToLong("", "testParam")).isNull();
        assertThat(InputValidationUtils.parseStringToLong("   ", "testParam")).isNull();
    }

    @Property(tries = 100)
    @Label("parseStringToLong should return null for 'null' string")
    void parseStringToLongShouldReturnNullForNullString() {
        assertThat(InputValidationUtils.parseStringToLong("null", "testParam")).isNull();
        assertThat(InputValidationUtils.parseStringToLong("NULL", "testParam")).isNull();
    }

    @Property(tries = 300)
    @Label("parseStringToLong should return value for valid numbers")
    void parseStringToLongShouldReturnValueForValidNumbers(
            @ForAll @LongRange(min = Long.MIN_VALUE / 2, max = Long.MAX_VALUE / 2) Long value) {
        String input = String.valueOf(value);
        Long result = InputValidationUtils.parseStringToLong(input, "testParam");
        assertThat(result).isEqualTo(value);
    }

    @Property(tries = 200)
    @Label("parseStringToLong should return 0L for non-numeric strings")
    void parseStringToLongShouldReturnZeroForNonNumeric(
            @ForAll @StringLength(min = 1, max = 20) @AlphaChars String input) {
        Long result = InputValidationUtils.parseStringToLong(input, "testParam");
        assertThat(result).isEqualTo(0L);
    }

    @Property(tries = 200)
    @Label("parseStringToLong should trim whitespace")
    void parseStringToLongShouldTrimWhitespace(
            @ForAll @LongRange(min = -1000000, max = 1000000) Long value) {
        String input = "  " + value + "  ";
        Long result = InputValidationUtils.parseStringToLong(input, "testParam");
        assertThat(result).isEqualTo(value);
    }

    // =========================================================================
    // CONSISTENCY PROPERTIES
    // =========================================================================

    @Property(tries = 200)
    @Label("parseStringToPositiveLong and parseStringToLong should be consistent for positive numbers")
    void parseMethodsShouldBeConsistentForPositive(
            @ForAll @LongRange(min = 1, max = 1000000) Long positiveValue) {
        String input = String.valueOf(positiveValue);
        Optional<Long> optionalResult = InputValidationUtils.parseStringToPositiveLong(input, "testParam");
        Long directResult = InputValidationUtils.parseStringToLong(input, "testParam");

        assertThat(optionalResult).isPresent();
        assertThat(optionalResult.get()).isEqualTo(directResult);
    }
}
