package com.bonitasoft.processbuilder.extension;

import com.bonitasoft.processbuilder.records.InvolvedUsersData;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;

/**
 * Property-based tests for {@link InvolvedUsersParser} class.
 * Tests invariants that must hold for any valid input.
 *
 * @author Bonitasoft
 * @since 1.0
 */
@Label("InvolvedUsersParser Property-Based Tests")
class InvolvedUsersParserPropertyTest {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    // =========================================================================
    // parseInvolvedUsersJson Properties
    // =========================================================================

    @Property(tries = 100)
    @Label("parseInvolvedUsersJson should parse valid JSON with all required fields")
    void parseInvolvedUsersJson_shouldParseValidJson(
            @ForAll @AlphaChars @StringLength(min = 1, max = 30) String stepManager,
            @ForAll @AlphaChars @StringLength(min = 1, max = 30) String stepUser,
            @ForAll @Size(min = 0, max = 5) List<@AlphaChars @StringLength(min = 1, max = 20) String> memberships) {

        String membershipsJson = memberships.stream()
                .map(m -> "\"" + m + "\"")
                .collect(Collectors.joining(", ", "[", "]"));

        String json = String.format("""
                {
                    "stepManager": "%s",
                    "stepUser": "%s",
                    "memberShips": %s
                }
                """, stepManager, stepUser, membershipsJson);

        InvolvedUsersData result = InvolvedUsersParser.parseInvolvedUsersJson(json);

        assertThat(result.stepManagerRef()).isEqualTo(stepManager);
        assertThat(result.stepUserRef()).isEqualTo(stepUser);
        assertThat(result.memberships()).containsExactlyElementsOf(memberships);
    }

    @Property(tries = 50)
    @Label("parseInvolvedUsersJson should allow null stepManager")
    void parseInvolvedUsersJson_shouldAllowNullStepManager(
            @ForAll @AlphaChars @StringLength(min = 1, max = 30) String stepUser,
            @ForAll @Size(min = 0, max = 3) List<@AlphaChars @StringLength(min = 1, max = 20) String> memberships) {

        String membershipsJson = memberships.stream()
                .map(m -> "\"" + m + "\"")
                .collect(Collectors.joining(", ", "[", "]"));

        String json = String.format("""
                {
                    "stepManager": null,
                    "stepUser": "%s",
                    "memberShips": %s
                }
                """, stepUser, membershipsJson);

        InvolvedUsersData result = InvolvedUsersParser.parseInvolvedUsersJson(json);

        assertThat(result.stepManagerRef()).isNull();
        assertThat(result.stepUserRef()).isEqualTo(stepUser);
    }

    @Property(tries = 50)
    @Label("parseInvolvedUsersJson should allow null stepUser")
    void parseInvolvedUsersJson_shouldAllowNullStepUser(
            @ForAll @AlphaChars @StringLength(min = 1, max = 30) String stepManager,
            @ForAll @Size(min = 0, max = 3) List<@AlphaChars @StringLength(min = 1, max = 20) String> memberships) {

        String membershipsJson = memberships.stream()
                .map(m -> "\"" + m + "\"")
                .collect(Collectors.joining(", ", "[", "]"));

        String json = String.format("""
                {
                    "stepManager": "%s",
                    "stepUser": null,
                    "memberShips": %s
                }
                """, stepManager, membershipsJson);

        InvolvedUsersData result = InvolvedUsersParser.parseInvolvedUsersJson(json);

        assertThat(result.stepManagerRef()).isEqualTo(stepManager);
        assertThat(result.stepUserRef()).isNull();
    }

    @Property(tries = 50)
    @Label("parseInvolvedUsersJson should throw for null or empty input")
    void parseInvolvedUsersJson_shouldThrowForNullOrEmptyInput(
            @ForAll("nullOrEmptyStrings") String input) {

        assertThatThrownBy(() -> InvolvedUsersParser.parseInvolvedUsersJson(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot be null or empty");
    }

    @Provide
    Arbitrary<String> nullOrEmptyStrings() {
        return Arbitraries.of(null, "");
    }

    @Property(tries = 50)
    @Label("parseInvolvedUsersJson should throw for missing stepManager field")
    void parseInvolvedUsersJson_shouldThrowForMissingStepManager(
            @ForAll @AlphaChars @StringLength(min = 1, max = 30) String stepUser) {

        String json = String.format("""
                {
                    "stepUser": "%s",
                    "memberShips": []
                }
                """, stepUser);

        assertThatThrownBy(() -> InvolvedUsersParser.parseInvolvedUsersJson(json))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("stepManager")
                .hasMessageContaining("MISSING");
    }

    @Property(tries = 50)
    @Label("parseInvolvedUsersJson should throw for missing stepUser field")
    void parseInvolvedUsersJson_shouldThrowForMissingStepUser(
            @ForAll @AlphaChars @StringLength(min = 1, max = 30) String stepManager) {

        String json = String.format("""
                {
                    "stepManager": "%s",
                    "memberShips": []
                }
                """, stepManager);

        assertThatThrownBy(() -> InvolvedUsersParser.parseInvolvedUsersJson(json))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("stepUser")
                .hasMessageContaining("MISSING");
    }

    @Property(tries = 50)
    @Label("parseInvolvedUsersJson should throw for missing memberShips field")
    void parseInvolvedUsersJson_shouldThrowForMissingMemberShips(
            @ForAll @AlphaChars @StringLength(min = 1, max = 30) String stepManager,
            @ForAll @AlphaChars @StringLength(min = 1, max = 30) String stepUser) {

        String json = String.format("""
                {
                    "stepManager": "%s",
                    "stepUser": "%s"
                }
                """, stepManager, stepUser);

        assertThatThrownBy(() -> InvolvedUsersParser.parseInvolvedUsersJson(json))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("memberShips")
                .hasMessageContaining("missing or not a valid array");
    }

    @Property(tries = 50)
    @Label("parseInvolvedUsersJson should throw for invalid JSON format")
    void parseInvolvedUsersJson_shouldThrowForInvalidJsonFormat(
            @ForAll @AlphaChars @StringLength(min = 5, max = 30) String invalidJson) {

        // Make sure it's definitely not valid JSON
        String brokenJson = "{ broken json " + invalidJson;

        assertThatThrownBy(() -> InvolvedUsersParser.parseInvolvedUsersJson(brokenJson))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid JSON format");
    }

    // =========================================================================
    // extractRequiredTextField Properties
    // =========================================================================

    @Property(tries = 100)
    @Label("extractRequiredTextField should return value for valid text field")
    void extractRequiredTextField_shouldReturnValueForValidField(
            @ForAll @AlphaChars @StringLength(min = 1, max = 50) String fieldName,
            @ForAll @AlphaChars @StringLength(min = 1, max = 100) String fieldValue) throws Exception {

        ObjectNode node = JsonNodeFactory.instance.objectNode();
        node.put(fieldName, fieldValue);

        String result = InvolvedUsersParser.extractRequiredTextField(node, fieldName);

        assertThat(result).isEqualTo(fieldValue);
    }

    @Property(tries = 50)
    @Label("extractRequiredTextField should throw for missing field")
    void extractRequiredTextField_shouldThrowForMissingField(
            @ForAll @AlphaChars @StringLength(min = 1, max = 20) String existingField,
            @ForAll @AlphaChars @StringLength(min = 1, max = 20) String missingField) {

        Assume.that(!existingField.equals(missingField));

        ObjectNode node = JsonNodeFactory.instance.objectNode();
        node.put(existingField, "value");

        assertThatThrownBy(() -> InvolvedUsersParser.extractRequiredTextField(node, missingField))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(missingField)
                .hasMessageContaining("missing");
    }

    @Property(tries = 50)
    @Label("extractRequiredTextField should throw for null value")
    void extractRequiredTextField_shouldThrowForNullValue(
            @ForAll @AlphaChars @StringLength(min = 1, max = 20) String fieldName) {

        ObjectNode node = JsonNodeFactory.instance.objectNode();
        node.putNull(fieldName);

        assertThatThrownBy(() -> InvolvedUsersParser.extractRequiredTextField(node, fieldName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(fieldName);
    }

    @Property(tries = 50)
    @Label("extractRequiredTextField should throw for empty or whitespace value")
    void extractRequiredTextField_shouldThrowForEmptyOrWhitespaceValue(
            @ForAll @AlphaChars @StringLength(min = 1, max = 20) String fieldName,
            @ForAll("emptyOrWhitespaceStrings") String emptyValue) {

        ObjectNode node = JsonNodeFactory.instance.objectNode();
        node.put(fieldName, emptyValue);

        assertThatThrownBy(() -> InvolvedUsersParser.extractRequiredTextField(node, fieldName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(fieldName);
    }

    @Provide
    Arbitrary<String> emptyOrWhitespaceStrings() {
        return Arbitraries.of("", "   ", "\t", "\n", "  \t\n  ");
    }

    // =========================================================================
    // extractNullableTextField Properties
    // =========================================================================

    @Property(tries = 100)
    @Label("extractNullableTextField should return value for valid text field")
    void extractNullableTextField_shouldReturnValueForValidField(
            @ForAll @AlphaChars @StringLength(min = 1, max = 50) String fieldName,
            @ForAll @AlphaChars @StringLength(min = 0, max = 100) String fieldValue) {

        ObjectNode node = JsonNodeFactory.instance.objectNode();
        node.put(fieldName, fieldValue);

        String result = InvolvedUsersParser.extractNullableTextField(node, fieldName);

        assertThat(result).isEqualTo(fieldValue);
    }

    @Property(tries = 50)
    @Label("extractNullableTextField should return null for null value")
    void extractNullableTextField_shouldReturnNullForNullValue(
            @ForAll @AlphaChars @StringLength(min = 1, max = 20) String fieldName) {

        ObjectNode node = JsonNodeFactory.instance.objectNode();
        node.putNull(fieldName);

        String result = InvolvedUsersParser.extractNullableTextField(node, fieldName);

        assertThat(result).isNull();
    }

    @Property(tries = 50)
    @Label("extractNullableTextField should throw for missing field")
    void extractNullableTextField_shouldThrowForMissingField(
            @ForAll @AlphaChars @StringLength(min = 1, max = 20) String existingField,
            @ForAll @AlphaChars @StringLength(min = 1, max = 20) String missingField) {

        Assume.that(!existingField.equals(missingField));

        ObjectNode node = JsonNodeFactory.instance.objectNode();
        node.put(existingField, "value");

        assertThatThrownBy(() -> InvolvedUsersParser.extractNullableTextField(node, missingField))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(missingField)
                .hasMessageContaining("MISSING");
    }

    @Property(tries = 50)
    @Label("extractNullableTextField should throw for non-text value")
    void extractNullableTextField_shouldThrowForNonTextValue(
            @ForAll @AlphaChars @StringLength(min = 1, max = 20) String fieldName,
            @ForAll @IntRange(min = 0, max = 1000) int numericValue) {

        ObjectNode node = JsonNodeFactory.instance.objectNode();
        node.put(fieldName, numericValue);

        assertThatThrownBy(() -> InvolvedUsersParser.extractNullableTextField(node, fieldName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(fieldName)
                .hasMessageContaining("not a valid text value");
    }

    // =========================================================================
    // Membership Filtering Properties
    // =========================================================================

    @Property(tries = 50)
    @Label("parseInvolvedUsersJson should filter out empty membership strings")
    void parseInvolvedUsersJson_shouldFilterOutEmptyMembershipStrings(
            @ForAll @AlphaChars @StringLength(min = 1, max = 20) String validMembership) {

        String json = String.format("""
                {
                    "stepManager": "manager",
                    "stepUser": "user",
                    "memberShips": ["%s", "", "   ", "%s"]
                }
                """, validMembership, validMembership + "2");

        InvolvedUsersData result = InvolvedUsersParser.parseInvolvedUsersJson(json);

        // Only non-empty, non-whitespace memberships should be included
        assertThat(result.memberships()).containsExactly(validMembership, validMembership + "2");
    }

    @Property(tries = 50)
    @Label("parseInvolvedUsersJson should preserve membership order")
    void parseInvolvedUsersJson_shouldPreserveMembershipOrder(
            @ForAll @Size(min = 2, max = 5) List<@AlphaChars @StringLength(min = 1, max = 10) String> memberships) {

        String membershipsJson = memberships.stream()
                .map(m -> "\"" + m + "\"")
                .collect(Collectors.joining(", ", "[", "]"));

        String json = String.format("""
                {
                    "stepManager": "manager",
                    "stepUser": "user",
                    "memberShips": %s
                }
                """, membershipsJson);

        InvolvedUsersData result = InvolvedUsersParser.parseInvolvedUsersJson(json);

        assertThat(result.memberships()).containsExactlyElementsOf(memberships);
    }

    // =========================================================================
    // Edge Cases
    // =========================================================================

    @Property(tries = 50)
    @Label("parseInvolvedUsersJson should handle extra fields in JSON")
    void parseInvolvedUsersJson_shouldHandleExtraFields(
            @ForAll @AlphaChars @StringLength(min = 1, max = 20) String stepManager,
            @ForAll @AlphaChars @StringLength(min = 1, max = 20) String extraField) {

        String json = String.format("""
                {
                    "stepManager": "%s",
                    "stepUser": "user",
                    "memberShips": [],
                    "extraField": "%s",
                    "anotherExtra": 12345
                }
                """, stepManager, extraField);

        InvolvedUsersData result = InvolvedUsersParser.parseInvolvedUsersJson(json);

        assertThat(result.stepManagerRef()).isEqualTo(stepManager);
        assertThat(result.stepUserRef()).isEqualTo("user");
    }

    @Property(tries = 50)
    @Label("parseInvolvedUsersJson should throw when memberShips is not an array")
    void parseInvolvedUsersJson_shouldThrowWhenMemberShipsNotArray(
            @ForAll @AlphaChars @StringLength(min = 1, max = 20) String notAnArray) {

        String json = String.format("""
                {
                    "stepManager": "manager",
                    "stepUser": "user",
                    "memberShips": "%s"
                }
                """, notAnArray);

        assertThatThrownBy(() -> InvolvedUsersParser.parseInvolvedUsersJson(json))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("memberShips")
                .hasMessageContaining("not a valid array");
    }
}
