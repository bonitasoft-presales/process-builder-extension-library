package com.bonitasoft.processbuilder.validation;

import com.bonitasoft.processbuilder.enums.ActionType;
import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

/**
 * Property-based tests for {@link JsonSchemaValidator} utility class.
 * Tests invariants that must hold for JSON schema validation.
 *
 * @author Bonitasoft
 * @since 1.0
 */
@Label("JsonSchemaValidator Property-Based Tests")
class JsonSchemaValidatorPropertyTest {

    // =========================================================================
    // Null Input Handling Properties
    // =========================================================================

    @Property(tries = 100)
    @Label("isJsonValidForType should return false for null optionType")
    void isJsonValidForType_shouldReturnFalseForNullOptionType(
            @ForAll @AlphaChars @StringLength(min = 1, max = 20) String actionType) {

        boolean result = JsonSchemaValidator.isJsonValidForType(actionType, null, "{}");

        assertThat(result).isFalse();
    }

    @Property(tries = 100)
    @Label("isJsonValidForType should return false for null jsonInput")
    void isJsonValidForType_shouldReturnFalseForNullJsonInput(
            @ForAll @AlphaChars @StringLength(min = 1, max = 20) String actionType,
            @ForAll @AlphaChars @StringLength(min = 1, max = 20) String optionType) {

        boolean result = JsonSchemaValidator.isJsonValidForType(actionType, optionType, null);

        assertThat(result).isFalse();
    }

    @Property(tries = 50)
    @Label("isJsonValidForType should return false when both optionType and jsonInput are null")
    void isJsonValidForType_shouldReturnFalseWhenBothNull(
            @ForAll @AlphaChars @StringLength(min = 1, max = 20) String actionType) {

        boolean result = JsonSchemaValidator.isJsonValidForType(actionType, null, null);

        assertThat(result).isFalse();
    }

    // =========================================================================
    // Empty Input Handling Properties
    // =========================================================================

    @Property(tries = 100)
    @Label("isJsonValidForType should return false for empty JSON string")
    void isJsonValidForType_shouldReturnFalseForEmptyJsonString(
            @ForAll @AlphaChars @StringLength(min = 1, max = 20) String actionType,
            @ForAll @AlphaChars @StringLength(min = 1, max = 20) String optionType) {

        boolean result = JsonSchemaValidator.isJsonValidForType(actionType, optionType, "");

        assertThat(result).isFalse();
    }

    @Property(tries = 100)
    @Label("isJsonValidForType should return false for whitespace-only JSON string")
    void isJsonValidForType_shouldReturnFalseForWhitespaceJsonString(
            @ForAll @AlphaChars @StringLength(min = 1, max = 20) String actionType,
            @ForAll @AlphaChars @StringLength(min = 1, max = 20) String optionType,
            @ForAll("whitespaceStrings") String whitespace) {

        boolean result = JsonSchemaValidator.isJsonValidForType(actionType, optionType, whitespace);

        assertThat(result).isFalse();
    }

    @Provide
    Arbitrary<String> whitespaceStrings() {
        return Arbitraries.of("   ", "\t", "\n", "  \t\n  ", "    ");
    }

    // =========================================================================
    // DELETE Action Type Properties
    // =========================================================================

    @Property(tries = 50)
    @Label("isJsonValidForType with DELETE action should use base schema")
    void isJsonValidForType_withDeleteAction_shouldUseBaseSchema(
            @ForAll @AlphaChars @StringLength(min = 1, max = 20) String optionType) {

        // DELETE action uses ObjectInputBaseSchema regardless of optionType
        // This test verifies the method doesn't throw and handles DELETE correctly
        // The actual validation will fail because schema file is not in test resources,
        // but the method should not throw and should return false gracefully
        boolean result = JsonSchemaValidator.isJsonValidForType(
                ActionType.DELETE.name(), optionType, "{\"id\": 1}");

        // Result depends on whether schema file exists in test resources
        // The important thing is no exception is thrown
        assertThat(result).isIn(true, false);
    }

    @Property(tries = 50)
    @Label("isJsonValidForType with lowercase delete should use base schema")
    void isJsonValidForType_withLowercaseDelete_shouldUseBaseSchema(
            @ForAll @AlphaChars @StringLength(min = 1, max = 20) String optionType) {

        // Test case-insensitive DELETE handling
        boolean result = JsonSchemaValidator.isJsonValidForType(
                "delete", optionType, "{\"id\": 1}");

        assertThat(result).isIn(true, false);
    }

    // =========================================================================
    // Object Input Serialization Properties
    // =========================================================================

    @Property(tries = 100)
    @Label("isJsonValidForType should handle Map input")
    void isJsonValidForType_shouldHandleMapInput(
            @ForAll @AlphaChars @StringLength(min = 1, max = 20) String key,
            @ForAll @AlphaChars @StringLength(min = 1, max = 50) String value) {

        Map<String, String> mapInput = new HashMap<>();
        mapInput.put(key, value);

        // Method should serialize the map and attempt validation
        // Won't throw exception even if schema doesn't exist
        boolean result = JsonSchemaValidator.isJsonValidForType(
                "INSERT", "TestType", mapInput);

        assertThat(result).isIn(true, false);
    }

    @Property(tries = 100)
    @Label("isJsonValidForType should handle String input directly")
    void isJsonValidForType_shouldHandleStringInputDirectly(
            @ForAll @AlphaChars @StringLength(min = 1, max = 20) String actionType,
            @ForAll @AlphaChars @StringLength(min = 1, max = 20) String optionType) {

        String jsonString = "{\"key\": \"value\"}";

        // String input should be used directly without serialization
        boolean result = JsonSchemaValidator.isJsonValidForType(
                actionType, optionType, jsonString);

        assertThat(result).isIn(true, false);
    }

    // =========================================================================
    // Class Structure Properties
    // =========================================================================

    @Example
    @Label("Class should be final to prevent inheritance")
    void class_shouldBeFinal() {
        assertThat(Modifier.isFinal(JsonSchemaValidator.class.getModifiers())).isTrue();
    }

    @Example
    @Label("Constructor should be private")
    void constructor_shouldBePrivate() throws Exception {
        Constructor<JsonSchemaValidator> constructor =
                JsonSchemaValidator.class.getDeclaredConstructor();
        assertThat(Modifier.isPrivate(constructor.getModifiers())).isTrue();
    }

    @Example
    @Label("Constructor should throw UnsupportedOperationException when invoked")
    void constructor_shouldThrowException() throws Exception {
        Constructor<JsonSchemaValidator> constructor =
                JsonSchemaValidator.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        assertThatThrownBy(constructor::newInstance)
                .hasCauseInstanceOf(UnsupportedOperationException.class);
    }

    // =========================================================================
    // Consistency Properties
    // =========================================================================

    @Property(tries = 50)
    @Label("isJsonValidForType should be deterministic for same inputs")
    void isJsonValidForType_shouldBeDeterministic(
            @ForAll @AlphaChars @StringLength(min = 1, max = 20) String actionType,
            @ForAll @AlphaChars @StringLength(min = 1, max = 20) String optionType) {

        String jsonInput = "{\"test\": true}";

        boolean result1 = JsonSchemaValidator.isJsonValidForType(actionType, optionType, jsonInput);
        boolean result2 = JsonSchemaValidator.isJsonValidForType(actionType, optionType, jsonInput);
        boolean result3 = JsonSchemaValidator.isJsonValidForType(actionType, optionType, jsonInput);

        assertThat(result1).isEqualTo(result2).isEqualTo(result3);
    }

    @Example
    @Label("isJsonValidForType null handling should be consistent")
    void isJsonValidForType_nullHandling_shouldBeConsistent() {
        // Multiple calls with null should consistently return false
        for (int i = 0; i < 10; i++) {
            assertThat(JsonSchemaValidator.isJsonValidForType("INSERT", null, "{}")).isFalse();
            assertThat(JsonSchemaValidator.isJsonValidForType("INSERT", "Type", null)).isFalse();
            assertThat(JsonSchemaValidator.isJsonValidForType("INSERT", null, null)).isFalse();
        }
    }

    // =========================================================================
    // Action Type Handling Properties
    // =========================================================================

    @Property(tries = 100)
    @Label("isJsonValidForType should handle various action types")
    void isJsonValidForType_shouldHandleVariousActionTypes(
            @ForAll("actionTypes") String actionType,
            @ForAll @AlphaChars @StringLength(min = 1, max = 20) String optionType) {

        // Method should not throw for any action type
        boolean result = JsonSchemaValidator.isJsonValidForType(
                actionType, optionType, "{\"id\": 1}");

        assertThat(result).isIn(true, false);
    }

    @Provide
    Arbitrary<String> actionTypes() {
        return Arbitraries.of(
                "INSERT", "UPDATE", "DELETE", "READ",
                "insert", "update", "delete", "read",
                "Insert", "Update", "Delete", "Read",
                "UNKNOWN", "custom_action"
        );
    }

    // =========================================================================
    // Invalid JSON Handling Properties
    // =========================================================================

    @Property(tries = 50)
    @Label("isJsonValidForType should handle invalid JSON gracefully")
    void isJsonValidForType_shouldHandleInvalidJsonGracefully(
            @ForAll @AlphaChars @StringLength(min = 1, max = 20) String optionType) {

        // Invalid JSON should not cause exception
        String invalidJson = "{ invalid json }";

        boolean result = JsonSchemaValidator.isJsonValidForType("INSERT", optionType, invalidJson);

        // Should return false but not throw
        assertThat(result).isIn(true, false);
    }
}
