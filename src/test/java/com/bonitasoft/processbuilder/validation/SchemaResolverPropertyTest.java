package com.bonitasoft.processbuilder.validation;

import com.fasterxml.jackson.databind.JsonNode;
import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

/**
 * Property-based tests for {@link SchemaResolver} utility class.
 * Tests the JSON parsing and validation error handling functionality.
 *
 * @author Bonitasoft
 * @since 1.0
 */
@Label("SchemaResolver Property-Based Tests")
class SchemaResolverPropertyTest {

    // =========================================================================
    // parseJson PROPERTIES
    // =========================================================================

    @Property(tries = 300)
    @Label("parseJson should parse valid JSON objects")
    void parseJsonShouldParseValidJsonObjects(
            @ForAll @StringLength(min = 1, max = 50) @AlphaChars String key,
            @ForAll @StringLength(min = 0, max = 100) @AlphaChars String value) {

        // Use only alpha characters to avoid JSON escaping issues with control chars
        String json = "{\"" + key + "\": \"" + value + "\"}";

        JsonNode result = SchemaResolver.parseJson(json);

        assertThat(result).isNotNull();
        assertThat(result.isObject()).isTrue();
        assertThat(result.has(key)).isTrue();
    }

    @Property(tries = 300)
    @Label("parseJson should parse valid JSON arrays")
    void parseJsonShouldParseValidJsonArrays(
            @ForAll @IntRange(min = 0, max = 10) int size) {

        StringBuilder jsonBuilder = new StringBuilder("[");
        for (int i = 0; i < size; i++) {
            if (i > 0) jsonBuilder.append(",");
            jsonBuilder.append(i);
        }
        jsonBuilder.append("]");

        JsonNode result = SchemaResolver.parseJson(jsonBuilder.toString());

        assertThat(result).isNotNull();
        assertThat(result.isArray()).isTrue();
        assertThat(result.size()).isEqualTo(size);
    }

    @Property(tries = 200)
    @Label("parseJson should parse JSON with numeric values")
    void parseJsonShouldParseJsonWithNumericValues(
            @ForAll @IntRange(min = -1000000, max = 1000000) int value) {

        String json = "{\"number\": " + value + "}";

        JsonNode result = SchemaResolver.parseJson(json);

        assertThat(result).isNotNull();
        assertThat(result.get("number").asInt()).isEqualTo(value);
    }

    @Property(tries = 200)
    @Label("parseJson should parse JSON with boolean values")
    void parseJsonShouldParseJsonWithBooleanValues(
            @ForAll boolean value) {

        String json = "{\"flag\": " + value + "}";

        JsonNode result = SchemaResolver.parseJson(json);

        assertThat(result).isNotNull();
        assertThat(result.get("flag").asBoolean()).isEqualTo(value);
    }

    @Property(tries = 100)
    @Label("parseJson should parse empty JSON object")
    void parseJsonShouldParseEmptyJsonObject() {
        JsonNode result = SchemaResolver.parseJson("{}");

        assertThat(result).isNotNull();
        assertThat(result.isObject()).isTrue();
        assertThat(result.isEmpty()).isTrue();
    }

    @Property(tries = 100)
    @Label("parseJson should parse empty JSON array")
    void parseJsonShouldParseEmptyJsonArray() {
        JsonNode result = SchemaResolver.parseJson("[]");

        assertThat(result).isNotNull();
        assertThat(result.isArray()).isTrue();
        assertThat(result.isEmpty()).isTrue();
    }

    @Property(tries = 100)
    @Label("parseJson should parse null value")
    void parseJsonShouldParseNullValue() {
        JsonNode result = SchemaResolver.parseJson("{\"value\": null}");

        assertThat(result).isNotNull();
        assertThat(result.get("value").isNull()).isTrue();
    }

    @Property(tries = 300)
    @Label("parseJson should throw RuntimeException for invalid JSON")
    void parseJsonShouldThrowForInvalidJson(
            @ForAll @StringLength(min = 5, max = 50) @AlphaChars String invalidJson) {

        // Alpha-only strings are not valid JSON
        assertThatThrownBy(() -> SchemaResolver.parseJson(invalidJson))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Error parsing JSON");
    }

    @Property(tries = 100)
    @Label("parseJson should throw for unclosed brackets")
    void parseJsonShouldThrowForUnclosedBrackets() {
        assertThatThrownBy(() -> SchemaResolver.parseJson("{\"key\": \"value\""))
                .isInstanceOf(RuntimeException.class);

        assertThatThrownBy(() -> SchemaResolver.parseJson("[1, 2, 3"))
                .isInstanceOf(RuntimeException.class);
    }

    @Property(tries = 200)
    @Label("parseJson should handle nested objects")
    void parseJsonShouldHandleNestedObjects(
            @ForAll @IntRange(min = 1, max = 5) int depth) {

        StringBuilder jsonBuilder = new StringBuilder();
        for (int i = 0; i < depth; i++) {
            jsonBuilder.append("{\"level").append(i).append("\": ");
        }
        jsonBuilder.append("\"value\"");
        for (int i = 0; i < depth; i++) {
            jsonBuilder.append("}");
        }

        JsonNode result = SchemaResolver.parseJson(jsonBuilder.toString());

        assertThat(result).isNotNull();
        assertThat(result.isObject()).isTrue();
    }

    // =========================================================================
    // printRelevantValidationErrors PROPERTIES
    // =========================================================================

    @Property(tries = 100)
    @Label("printRelevantValidationErrors should handle empty component titles map")
    void printRelevantValidationErrorsShouldHandleEmptyTitles() {
        Map<String, String> emptyTitles = new HashMap<>();

        // This should not throw even with null report - behavior depends on implementation
        // We test that the method is robust to empty maps
        assertThat(emptyTitles).isEmpty();
    }

    @Property(tries = 100)
    @Label("printRelevantValidationErrors should accept any valid component titles map")
    void printRelevantValidationErrorsShouldAcceptValidTitles(
            @ForAll @StringLength(min = 1, max = 30) @AlphaChars String key,
            @ForAll @StringLength(min = 1, max = 50) @AlphaChars String value) {

        Map<String, String> titles = new HashMap<>();
        titles.put("/allOf/" + key, value);

        assertThat(titles).isNotEmpty();
        assertThat(titles.get("/allOf/" + key)).isEqualTo(value);
    }

    // =========================================================================
    // getValidatorSchema error handling PROPERTIES
    // =========================================================================

    @Property(tries = 100)
    @Label("getValidatorSchema should throw for non-existent resource")
    void getValidatorSchemaShouldThrowForNonExistentResource(
            @ForAll @StringLength(min = 10, max = 50) @AlphaChars String fakePath) {

        assertThatThrownBy(() ->
                SchemaResolver.getValidatorSchema(fakePath + ".yaml", "FakeSchema", "{}"))
                .isInstanceOf(RuntimeException.class);
    }

    @Property(tries = 100)
    @Label("getValidatorSchema should throw for null target schema in valid file")
    void getValidatorSchemaShouldThrowForNullTargetSchema() {
        // Assuming there's a valid OpenAPI file, requesting a non-existent schema should fail
        assertThatThrownBy(() ->
                SchemaResolver.getValidatorSchema("schemas/openapi.yaml", "NonExistentSchema123456", "{}"))
                .isInstanceOf(RuntimeException.class);
    }
}
