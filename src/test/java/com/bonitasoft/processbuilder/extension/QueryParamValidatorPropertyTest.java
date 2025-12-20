package com.bonitasoft.processbuilder.extension;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

import static org.assertj.core.api.Assertions.*;

/**
 * Property-based tests for {@link QueryParamValidator} utility class.
 * Tests invariants that must hold for any valid input.
 *
 * @author Bonitasoft
 * @since 1.0
 */
@Label("QueryParamValidator Property-Based Tests")
class QueryParamValidatorPropertyTest {

    // =========================================================================
    // UTILITY CLASS INVARIANT PROPERTIES
    // =========================================================================

    @Property(tries = 100)
    @Label("Utility class should not be instantiable")
    void utilityClassShouldNotBeInstantiable() {
        assertThatThrownBy(() -> {
            var constructor = QueryParamValidator.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            constructor.newInstance();
        }).hasCauseInstanceOf(UnsupportedOperationException.class);
    }

    // =========================================================================
    // validateMandatoryLong() PROPERTIES
    // =========================================================================

    @Property(tries = 100)
    @Label("validateMandatoryLong should throw for null value")
    void validateMandatoryLongShouldThrowForNull(
            @ForAll @StringLength(min = 1, max = 20) @AlphaChars String paramName) {
        assertThatThrownBy(() ->
            QueryParamValidator.validateMandatoryLong(paramName, null)
        ).isInstanceOf(ValidationException.class)
         .hasMessageContaining("mandatory");
    }

    @Property(tries = 100)
    @Label("validateMandatoryLong should throw for empty value")
    void validateMandatoryLongShouldThrowForEmpty(
            @ForAll @StringLength(min = 1, max = 20) @AlphaChars String paramName) {
        assertThatThrownBy(() ->
            QueryParamValidator.validateMandatoryLong(paramName, "")
        ).isInstanceOf(ValidationException.class)
         .hasMessageContaining("mandatory");
    }

    @Property(tries = 100)
    @Label("validateMandatoryLong should throw for blank value")
    void validateMandatoryLongShouldThrowForBlank(
            @ForAll @StringLength(min = 1, max = 20) @AlphaChars String paramName) {
        assertThatThrownBy(() ->
            QueryParamValidator.validateMandatoryLong(paramName, "   ")
        ).isInstanceOf(ValidationException.class)
         .hasMessageContaining("mandatory");
    }

    @Property(tries = 200)
    @Label("validateMandatoryLong should accept valid long strings")
    void validateMandatoryLongShouldAcceptValidLongs(
            @ForAll @StringLength(min = 1, max = 20) @AlphaChars String paramName,
            @ForAll @LongRange(min = Long.MIN_VALUE / 2, max = Long.MAX_VALUE / 2) Long validValue) {
        assertThatCode(() ->
            QueryParamValidator.validateMandatoryLong(paramName, String.valueOf(validValue))
        ).doesNotThrowAnyException();
    }

    @Property(tries = 200)
    @Label("validateMandatoryLong should throw for non-numeric strings")
    void validateMandatoryLongShouldThrowForNonNumeric(
            @ForAll @StringLength(min = 1, max = 20) @AlphaChars String paramName,
            @ForAll @StringLength(min = 1, max = 20) @AlphaChars String nonNumericValue) {
        assertThatThrownBy(() ->
            QueryParamValidator.validateMandatoryLong(paramName, nonNumericValue)
        ).isInstanceOf(ValidationException.class)
         .hasMessageContaining("should be a long");
    }

    @Property(tries = 200)
    @Label("validateMandatoryLong exception message should contain parameter name")
    void validateMandatoryLongExceptionShouldContainParamName(
            @ForAll @StringLength(min = 3, max = 20) @AlphaChars String paramName) {
        assertThatThrownBy(() ->
            QueryParamValidator.validateMandatoryLong(paramName, null)
        ).isInstanceOf(ValidationException.class)
         .hasMessageContaining(paramName);
    }

    // =========================================================================
    // validateNumerical() PROPERTIES
    // =========================================================================

    @Property(tries = 100)
    @Label("validateNumerical should throw for null value")
    void validateNumericalShouldThrowForNull(
            @ForAll @StringLength(min = 1, max = 20) @AlphaChars String paramName) {
        assertThatThrownBy(() ->
            QueryParamValidator.validateNumerical(paramName, null)
        ).isInstanceOf(ValidationException.class)
         .hasMessageContaining("mandatory");
    }

    @Property(tries = 100)
    @Label("validateNumerical should throw for empty value")
    void validateNumericalShouldThrowForEmpty(
            @ForAll @StringLength(min = 1, max = 20) @AlphaChars String paramName) {
        assertThatThrownBy(() ->
            QueryParamValidator.validateNumerical(paramName, "")
        ).isInstanceOf(ValidationException.class)
         .hasMessageContaining("mandatory");
    }

    @Property(tries = 200)
    @Label("validateNumerical should accept valid integer strings")
    void validateNumericalShouldAcceptValidIntegers(
            @ForAll @StringLength(min = 1, max = 20) @AlphaChars String paramName,
            @ForAll @IntRange(min = Integer.MIN_VALUE / 2, max = Integer.MAX_VALUE / 2) Integer validValue) {
        assertThatCode(() ->
            QueryParamValidator.validateNumerical(paramName, String.valueOf(validValue))
        ).doesNotThrowAnyException();
    }

    @Property(tries = 200)
    @Label("validateNumerical should throw for non-numeric strings")
    void validateNumericalShouldThrowForNonNumeric(
            @ForAll @StringLength(min = 1, max = 20) @AlphaChars String paramName,
            @ForAll @StringLength(min = 1, max = 20) @AlphaChars String nonNumericValue) {
        assertThatThrownBy(() ->
            QueryParamValidator.validateNumerical(paramName, nonNumericValue)
        ).isInstanceOf(ValidationException.class)
         .hasMessageContaining("should be a numerical value");
    }

    @Property(tries = 200)
    @Label("validateNumerical exception message should contain parameter name")
    void validateNumericalExceptionShouldContainParamName(
            @ForAll @StringLength(min = 3, max = 20) @AlphaChars String paramName) {
        assertThatThrownBy(() ->
            QueryParamValidator.validateNumerical(paramName, null)
        ).isInstanceOf(ValidationException.class)
         .hasMessageContaining(paramName);
    }

    // =========================================================================
    // CONSISTENCY PROPERTIES
    // =========================================================================

    @Property(tries = 200)
    @Label("Both validators should agree on rejecting null values")
    void bothValidatorsShouldRejectNull(
            @ForAll @StringLength(min = 1, max = 20) @AlphaChars String paramName) {
        assertThatThrownBy(() ->
            QueryParamValidator.validateMandatoryLong(paramName, null)
        ).isInstanceOf(ValidationException.class);

        assertThatThrownBy(() ->
            QueryParamValidator.validateNumerical(paramName, null)
        ).isInstanceOf(ValidationException.class);
    }

    @Property(tries = 200)
    @Label("Valid integer should pass both validators")
    void validIntegerShouldPassBothValidators(
            @ForAll @StringLength(min = 1, max = 20) @AlphaChars String paramName,
            @ForAll @IntRange(min = 1, max = Integer.MAX_VALUE / 2) Integer validValue) {
        String valueStr = String.valueOf(validValue);

        assertThatCode(() ->
            QueryParamValidator.validateMandatoryLong(paramName, valueStr)
        ).doesNotThrowAnyException();

        assertThatCode(() ->
            QueryParamValidator.validateNumerical(paramName, valueStr)
        ).doesNotThrowAnyException();
    }
}
