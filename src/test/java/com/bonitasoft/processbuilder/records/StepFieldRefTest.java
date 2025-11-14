package com.bonitasoft.processbuilder.records;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for the {@link StepFieldRef} record.
 * <p>
 * This class validates the parsing logic, record instantiation, and all edge cases
 * for the StepFieldRef record including null handling, empty strings, and invalid formats.
 * </p>
 */
class StepFieldRefTest {

    private static final String VALID_STEP_REF = "step_001";
    private static final String VALID_FIELD_REF = "field_name";
    private static final String VALID_INPUT = VALID_STEP_REF + ":" + VALID_FIELD_REF;

    // =========================================================================
    // SECTION 1: Successful Parsing Tests
    // =========================================================================

    @Test
    @DisplayName("parse should return valid StepFieldRef for correct format")
    void parse_should_return_valid_stepFieldRef_for_correct_format() {
        // When
        StepFieldRef result = StepFieldRef.parse(VALID_INPUT);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.stepRef()).isEqualTo(VALID_STEP_REF);
        assertThat(result.fieldRef()).isEqualTo(VALID_FIELD_REF);
    }

    @Test
    @DisplayName("parse should trim whitespace from stepRef and fieldRef")
    void parse_should_trim_whitespace_from_parts() {
        // Given
        String inputWithSpaces = "  step_002  :  field_value  ";

        // When
        StepFieldRef result = StepFieldRef.parse(inputWithSpaces);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.stepRef()).isEqualTo("step_002");
        assertThat(result.fieldRef()).isEqualTo("field_value");
    }

    @Test
    @DisplayName("parse should handle multiple colons by splitting on first occurrence")
    void parse_should_handle_multiple_colons() {
        // Given
        String inputWithMultipleColons = "step_003:field_with:colon";

        // When
        StepFieldRef result = StepFieldRef.parse(inputWithMultipleColons);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.stepRef()).isEqualTo("step_003");
        assertThat(result.fieldRef()).isEqualTo("field_with:colon");
    }

    @Test
    @DisplayName("parse should handle special characters in stepRef and fieldRef")
    void parse_should_handle_special_characters() {
        // Given
        String inputWithSpecialChars = "step_with-dash:field_with_underscore";

        // When
        StepFieldRef result = StepFieldRef.parse(inputWithSpecialChars);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.stepRef()).isEqualTo("step_with-dash");
        assertThat(result.fieldRef()).isEqualTo("field_with_underscore");
    }

    @Test
    @DisplayName("parse should handle numeric values in stepRef and fieldRef")
    void parse_should_handle_numeric_values() {
        // Given
        String inputWithNumbers = "step_123:field_456";

        // When
        StepFieldRef result = StepFieldRef.parse(inputWithNumbers);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.stepRef()).isEqualTo("step_123");
        assertThat(result.fieldRef()).isEqualTo("field_456");
    }

    // =========================================================================
    // SECTION 2: Invalid Input Tests (null, empty, no colon)
    // =========================================================================

    @Test
    @DisplayName("parse should return null for null input")
    void parse_should_return_null_for_null_input() {
        // When
        StepFieldRef result = StepFieldRef.parse(null);

        // Then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("parse should return null for empty string")
    void parse_should_return_null_for_empty_string() {
        // When
        StepFieldRef result = StepFieldRef.parse("");

        // Then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("parse should return null when input does not contain colon")
    void parse_should_return_null_when_no_colon() {
        // Given
        String inputWithoutColon = "step_004_field_value";

        // When
        StepFieldRef result = StepFieldRef.parse(inputWithoutColon);

        // Then
        assertThat(result).isNull();
    }

    // =========================================================================
    // SECTION 3: Empty Parts After Parsing Tests
    // =========================================================================

    @Test
    @DisplayName("parse should return null when stepRef is empty after trimming")
    void parse_should_return_null_when_stepRef_is_empty_after_trim() {
        // Given
        String inputWithEmptyStepRef = "  :field_value";

        // When
        StepFieldRef result = StepFieldRef.parse(inputWithEmptyStepRef);

        // Then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("parse should return null when fieldRef is empty after trimming")
    void parse_should_return_null_when_fieldRef_is_empty_after_trim() {
        // Given
        String inputWithEmptyFieldRef = "step_005:  ";

        // When
        StepFieldRef result = StepFieldRef.parse(inputWithEmptyFieldRef);

        // Then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("parse should return null when both parts are empty after trimming")
    void parse_should_return_null_when_both_parts_are_empty() {
        // Given
        String inputWithEmptyParts = "  :  ";

        // When
        StepFieldRef result = StepFieldRef.parse(inputWithEmptyParts);

        // Then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("parse should return null when colon is at the beginning")
    void parse_should_return_null_when_colon_at_beginning() {
        // Given: Empty stepRef before colon
        String inputWithColonAtStart = ":field_value";

        // When
        StepFieldRef result = StepFieldRef.parse(inputWithColonAtStart);

        // Then: Should fail because stepRef is empty
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("parse should return null when colon is at the end")
    void parse_should_return_null_when_colon_at_end() {
        // Given: Empty fieldRef after colon
        String inputWithColonAtEnd = "step_value:";

        // When
        StepFieldRef result = StepFieldRef.parse(inputWithColonAtEnd);

        // Then: Should fail because fieldRef is empty
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("parse should return null for single colon only")
    void parse_should_return_null_for_single_colon() {
        // Given
        String inputWithOnlyColon = ":";

        // When
        StepFieldRef result = StepFieldRef.parse(inputWithOnlyColon);

        // Then
        assertThat(result).isNull();
    }

    // =========================================================================
    // SECTION 4: Record Behavior Tests (equals, hashCode, toString)
    // =========================================================================

    @Test
    @DisplayName("Should correctly instantiate StepFieldRef and expose all fields via accessors")
    void should_instantiate_and_expose_fields_correctly() {
        // Given
        StepFieldRef stepFieldRef = new StepFieldRef(VALID_STEP_REF, VALID_FIELD_REF);

        // Then
        assertThat(stepFieldRef).isNotNull();
        assertThat(stepFieldRef.stepRef()).isEqualTo(VALID_STEP_REF);
        assertThat(stepFieldRef.fieldRef()).isEqualTo(VALID_FIELD_REF);
    }

    @Test
    @DisplayName("Should correctly implement equals and hashCode")
    void should_implement_equals_and_hashCode_correctly() {
        // Given
        StepFieldRef ref1 = new StepFieldRef(VALID_STEP_REF, VALID_FIELD_REF);
        StepFieldRef ref2 = new StepFieldRef(VALID_STEP_REF, VALID_FIELD_REF);
        StepFieldRef differentRef = new StepFieldRef("different_step", VALID_FIELD_REF);

        // Then
        assertThat(ref1).isEqualTo(ref2).hasSameHashCodeAs(ref2);
        assertThat(ref1).isNotEqualTo(differentRef);
    }

    @Test
    @DisplayName("Should generate a useful toString representation")
    void should_generate_toString() {
        // Given
        StepFieldRef stepFieldRef = new StepFieldRef(VALID_STEP_REF, VALID_FIELD_REF);

        // When
        String result = stepFieldRef.toString();

        // Then
        assertThat(result)
            .contains("StepFieldRef[")
            .contains("stepRef=" + VALID_STEP_REF)
            .contains("fieldRef=" + VALID_FIELD_REF)
            .endsWith("]");
    }

    @Test
    @DisplayName("Should allow null values for stepRef and fieldRef")
    void should_allow_null_values() {
        // Given & When
        StepFieldRef stepFieldRef = new StepFieldRef(null, null);

        // Then
        assertThat(stepFieldRef).isNotNull();
        assertThat(stepFieldRef.stepRef()).isNull();
        assertThat(stepFieldRef.fieldRef()).isNull();
    }

    @Test
    @DisplayName("Should handle two identical StepFieldRef instances as equal")
    void should_handle_identical_instances_as_equal() {
        // Given
        StepFieldRef ref1 = StepFieldRef.parse("step_abc:field_xyz");
        StepFieldRef ref2 = StepFieldRef.parse("step_abc:field_xyz");

        // Then
        assertThat(ref1).isEqualTo(ref2);
        assertThat(ref1.hashCode()).isEqualTo(ref2.hashCode());
    }

    @Test
    @DisplayName("Should handle different StepFieldRef instances as not equal")
    void should_handle_different_instances_as_not_equal() {
        // Given
        StepFieldRef ref1 = StepFieldRef.parse("step_abc:field_xyz");
        StepFieldRef ref2 = StepFieldRef.parse("step_def:field_xyz");
        StepFieldRef ref3 = StepFieldRef.parse("step_abc:field_uvw");

        // Then
        assertThat(ref1).isNotEqualTo(ref2);
        assertThat(ref1).isNotEqualTo(ref3);
        assertThat(ref2).isNotEqualTo(ref3);
    }

    // =========================================================================
    // SECTION 5: Edge Cases and Boundary Tests
    // =========================================================================

    @Test
    @DisplayName("parse should handle very long input strings")
    void parse_should_handle_long_strings() {
        // Given
        String longStepRef = "step_" + "x".repeat(1000);
        String longFieldRef = "field_" + "y".repeat(1000);
        String longInput = longStepRef + ":" + longFieldRef;

        // When
        StepFieldRef result = StepFieldRef.parse(longInput);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.stepRef()).isEqualTo(longStepRef);
        assertThat(result.fieldRef()).isEqualTo(longFieldRef);
    }

    @Test
    @DisplayName("parse should handle minimal valid input")
    void parse_should_handle_minimal_valid_input() {
        // Given: Minimal valid format - single character each
        String minimalInput = "a:b";

        // When
        StepFieldRef result = StepFieldRef.parse(minimalInput);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.stepRef()).isEqualTo("a");
        assertThat(result.fieldRef()).isEqualTo("b");
    }

    @Test
    @DisplayName("parse should preserve case sensitivity")
    void parse_should_preserve_case_sensitivity() {
        // Given
        String mixedCaseInput = "Step_ABC:Field_XYZ";

        // When
        StepFieldRef result = StepFieldRef.parse(mixedCaseInput);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.stepRef()).isEqualTo("Step_ABC");
        assertThat(result.fieldRef()).isEqualTo("Field_XYZ");
    }

    @Test
    @DisplayName("parse should handle input with only whitespace before colon")
    void parse_should_handle_whitespace_before_colon() {
        // Given
        String inputWithWhitespace = "   :field";

        // When
        StepFieldRef result = StepFieldRef.parse(inputWithWhitespace);

        // Then: Should fail because stepRef is empty after trimming
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("parse should handle input with only whitespace after colon")
    void parse_should_handle_whitespace_after_colon() {
        // Given
        String inputWithWhitespace = "step:   ";

        // When
        StepFieldRef result = StepFieldRef.parse(inputWithWhitespace);

        // Then: Should fail because fieldRef is empty after trimming
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("parse should handle consecutive parsing calls independently")
    void parse_should_handle_consecutive_calls_independently() {
        // Given
        String input1 = "step_1:field_1";
        String input2 = "step_2:field_2";

        // When
        StepFieldRef result1 = StepFieldRef.parse(input1);
        StepFieldRef result2 = StepFieldRef.parse(input2);

        // Then
        assertThat(result1).isNotNull();
        assertThat(result2).isNotNull();
        assertThat(result1).isNotEqualTo(result2);
        assertThat(result1.stepRef()).isEqualTo("step_1");
        assertThat(result2.stepRef()).isEqualTo("step_2");
    }
}
