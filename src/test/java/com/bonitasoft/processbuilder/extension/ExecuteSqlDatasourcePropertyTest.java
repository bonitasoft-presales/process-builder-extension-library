package com.bonitasoft.processbuilder.extension;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

import static org.assertj.core.api.Assertions.*;

/**
 * Property-based tests for {@link ExecuteSqlDatasource} class.
 * Tests invariants that must hold for any valid input.
 *
 * @author Bonitasoft
 * @since 1.0
 */
@Label("ExecuteSqlDatasource Property-Based Tests")
class ExecuteSqlDatasourcePropertyTest {

    private final ExecuteSqlDatasource datasource = new ExecuteSqlDatasource();

    // =========================================================================
    // Validation Properties
    // =========================================================================

    @Property(tries = 100)
    @Label("validateInputParameters should accept valid numerical strings")
    void validateInputParameters_shouldAcceptValidNumericalStrings(
            @ForAll @IntRange(min = 0, max = 10000) int page,
            @ForAll @IntRange(min = 1, max = 1000) int count) {

        String p = String.valueOf(page);
        String c = String.valueOf(count);

        assertThatCode(() -> datasource.validateInputParameters(p, c))
                .doesNotThrowAnyException();
    }

    @Property(tries = 100)
    @Label("validateInputParameters should accept negative numbers")
    void validateInputParameters_shouldAcceptNegativeNumbers(
            @ForAll @IntRange(min = -10000, max = -1) int page,
            @ForAll @IntRange(min = -1000, max = -1) int count) {

        String p = String.valueOf(page);
        String c = String.valueOf(count);

        assertThatCode(() -> datasource.validateInputParameters(p, c))
                .doesNotThrowAnyException();
    }

    @Property(tries = 100)
    @Label("validateInputParameters should accept null parameters")
    void validateInputParameters_shouldAcceptNullParameters(
            @ForAll boolean pIsNull,
            @ForAll boolean cIsNull) {

        String p = pIsNull ? null : "1";
        String c = cIsNull ? null : "10";

        assertThatCode(() -> datasource.validateInputParameters(p, c))
                .doesNotThrowAnyException();
    }

    @Property(tries = 100)
    @Label("validateInputParameters should accept empty or blank strings")
    void validateInputParameters_shouldAcceptEmptyOrBlankStrings(
            @ForAll("emptyOrBlankStrings") String p,
            @ForAll("emptyOrBlankStrings") String c) {

        assertThatCode(() -> datasource.validateInputParameters(p, c))
                .doesNotThrowAnyException();
    }

    @Provide
    Arbitrary<String> emptyOrBlankStrings() {
        return Arbitraries.of("", "   ", "\t", "\n", "  \t\n  ");
    }

    @Property(tries = 100)
    @Label("validateInputParameters should reject non-numerical p parameter")
    void validateInputParameters_shouldRejectNonNumericalP(
            @ForAll @AlphaChars @StringLength(min = 1, max = 10) String invalidP) {

        assertThatThrownBy(() -> datasource.validateInputParameters(invalidP, "10"))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("numerical value");
    }

    @Property(tries = 100)
    @Label("validateInputParameters should reject non-numerical c parameter")
    void validateInputParameters_shouldRejectNonNumericalC(
            @ForAll @AlphaChars @StringLength(min = 1, max = 10) String invalidC) {

        assertThatThrownBy(() -> datasource.validateInputParameters("1", invalidC))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("numerical value");
    }

    @Property(tries = 100)
    @Label("validateInputParameters should reject decimal numbers")
    void validateInputParameters_shouldRejectDecimalNumbers(
            @ForAll @IntRange(min = 0, max = 100) int intPart,
            @ForAll @IntRange(min = 1, max = 99) int decimalPart) {

        String decimalP = intPart + "." + decimalPart;
        String decimalC = intPart + "." + decimalPart;

        // Either p or c as decimal should throw
        assertThatThrownBy(() -> datasource.validateInputParameters(decimalP, "10"))
                .isInstanceOf(ValidationException.class);

        assertThatThrownBy(() -> datasource.validateInputParameters("1", decimalC))
                .isInstanceOf(ValidationException.class);
    }

    // =========================================================================
    // Execute Method Properties
    // =========================================================================

    @Property(tries = 100)
    @Label("execute should return result with valid parameters")
    void execute_shouldReturnResultWithValidParameters(
            @ForAll @IntRange(min = 0, max = 100) int page,
            @ForAll @IntRange(min = 1, max = 100) int count) throws ValidationException {

        String p = String.valueOf(page);
        String c = String.valueOf(count);

        Object result = datasource.execute(p, c);

        assertThat(result).isNotNull();
        assertThat(result.toString()).contains("page=" + page);
        assertThat(result.toString()).contains("count=" + count);
    }

    @Property(tries = 50)
    @Label("execute should use defaults for null parameters")
    void execute_shouldUseDefaultsForNullParameters() throws ValidationException {
        Object result = datasource.execute(null, null);

        assertThat(result).isNotNull();
        assertThat(result.toString()).contains("page=0");
        assertThat(result.toString()).contains("count=10");
    }

    @Property(tries = 50)
    @Label("execute should use defaults for empty parameters")
    void execute_shouldUseDefaultsForEmptyParameters() throws ValidationException {
        Object result = datasource.execute("", "");

        assertThat(result).isNotNull();
        assertThat(result.toString()).contains("page=0");
        assertThat(result.toString()).contains("count=10");
    }

    @Property(tries = 100)
    @Label("execute should throw exception for invalid p")
    void execute_shouldThrowExceptionForInvalidP(
            @ForAll @AlphaChars @StringLength(min = 1, max = 10) String invalidP) {

        assertThatThrownBy(() -> datasource.execute(invalidP, "10"))
                .isInstanceOf(ValidationException.class);
    }

    @Property(tries = 100)
    @Label("execute should throw exception for invalid c")
    void execute_shouldThrowExceptionForInvalidC(
            @ForAll @AlphaChars @StringLength(min = 1, max = 10) String invalidC) {

        assertThatThrownBy(() -> datasource.execute("1", invalidC))
                .isInstanceOf(ValidationException.class);
    }

    // =========================================================================
    // Consistency Properties
    // =========================================================================

    @Property(tries = 50)
    @Label("execute result format should be consistent")
    void execute_resultFormatShouldBeConsistent(
            @ForAll @IntRange(min = 0, max = 1000) int page,
            @ForAll @IntRange(min = 1, max = 1000) int count) throws ValidationException {

        Object result = datasource.execute(String.valueOf(page), String.valueOf(count));

        assertThat(result.toString())
                .matches("Executing query with page=\\d+, count=\\d+");
    }

    @Property(tries = 100)
    @Label("validateInputParameters and execute should be consistent")
    void validateAndExecute_shouldBeConsistent(
            @ForAll @IntRange(min = 0, max = 100) int page,
            @ForAll @IntRange(min = 1, max = 100) int count) {

        String p = String.valueOf(page);
        String c = String.valueOf(count);

        // If validation passes, execute should also pass
        assertThatCode(() -> datasource.validateInputParameters(p, c))
                .doesNotThrowAnyException();

        assertThatCode(() -> datasource.execute(p, c))
                .doesNotThrowAnyException();
    }

    // =========================================================================
    // Edge Case Properties
    // =========================================================================

    @Property(tries = 50)
    @Label("validateInputParameters should handle large integer values within range")
    void validateInputParameters_shouldHandleLargeIntegerValues(
            @ForAll @IntRange(min = Integer.MAX_VALUE - 100, max = Integer.MAX_VALUE) int largeValue) {

        String value = String.valueOf(largeValue);

        // Should accept as valid numerical string within Integer range
        assertThatCode(() -> datasource.validateInputParameters(value, "10"))
                .doesNotThrowAnyException();
    }

    @Property(tries = 50)
    @Label("validateInputParameters should reject values exceeding Integer range")
    void validateInputParameters_shouldRejectValuesExceedingIntegerRange(
            @ForAll @LongRange(min = (long) Integer.MAX_VALUE + 1, max = Long.MAX_VALUE) long overflowValue) {

        String value = String.valueOf(overflowValue);

        // Values exceeding Integer.MAX_VALUE should be rejected
        assertThatThrownBy(() -> datasource.validateInputParameters(value, "10"))
                .isInstanceOf(ValidationException.class);
    }

    @Property(tries = 50)
    @Label("validateInputParameters should reject mixed alphanumeric strings")
    void validateInputParameters_shouldRejectMixedAlphanumericStrings(
            @ForAll @IntRange(min = 1, max = 100) int number,
            @ForAll @AlphaChars @StringLength(min = 1, max = 5) String letters) {

        String mixed = number + letters;

        assertThatThrownBy(() -> datasource.validateInputParameters(mixed, "10"))
                .isInstanceOf(ValidationException.class);

        assertThatThrownBy(() -> datasource.validateInputParameters("1", mixed))
                .isInstanceOf(ValidationException.class);
    }
}
