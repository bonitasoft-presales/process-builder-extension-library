package com.bonitasoft.processbuilder.extension;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.*;
import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

import static org.assertj.core.api.Assertions.*;

/**
 * Property-based tests for {@link JsonNodeUtils} utility class.
 * Tests invariants that must hold for any valid input.
 *
 * @author Bonitasoft
 * @since 1.0
 */
@Label("JsonNodeUtils Property-Based Tests")
class JsonNodeUtilsPropertyTest {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    // =========================================================================
    // UTILITY CLASS INVARIANT PROPERTIES
    // =========================================================================

    @Property(tries = 100)
    @Label("Utility class should not be instantiable")
    void utilityClassShouldNotBeInstantiable() {
        assertThatThrownBy(() -> {
            var constructor = JsonNodeUtils.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            constructor.newInstance();
        }).hasCauseInstanceOf(UnsupportedOperationException.class);
    }

    // =========================================================================
    // OPERATOR CONSTANTS PROPERTIES
    // =========================================================================

    @Property(tries = 100)
    @Label("Operator constants should be non-null and non-empty")
    void operatorConstantsShouldBeNonNull() {
        assertThat(JsonNodeUtils.OP_EQUALS).isNotNull().isNotBlank();
        assertThat(JsonNodeUtils.OP_EQUALS_SYMBOL).isNotNull().isNotBlank();
        assertThat(JsonNodeUtils.OP_NOT_EQUALS).isNotNull().isNotBlank();
        assertThat(JsonNodeUtils.OP_NOT_EQUALS_SYMBOL).isNotNull().isNotBlank();
        assertThat(JsonNodeUtils.OP_CONTAINS).isNotNull().isNotBlank();
        assertThat(JsonNodeUtils.OP_GREATER_THAN).isNotNull().isNotBlank();
        assertThat(JsonNodeUtils.OP_GREATER_THAN_SYMBOL).isNotNull().isNotBlank();
        assertThat(JsonNodeUtils.OP_LESS_THAN).isNotNull().isNotBlank();
        assertThat(JsonNodeUtils.OP_LESS_THAN_SYMBOL).isNotNull().isNotBlank();
        assertThat(JsonNodeUtils.OP_GREATER_OR_EQUAL).isNotNull().isNotBlank();
        assertThat(JsonNodeUtils.OP_GREATER_OR_EQUAL_SYMBOL).isNotNull().isNotBlank();
        assertThat(JsonNodeUtils.OP_LESS_OR_EQUAL).isNotNull().isNotBlank();
        assertThat(JsonNodeUtils.OP_LESS_OR_EQUAL_SYMBOL).isNotNull().isNotBlank();
    }

    // =========================================================================
    // convertJsonNodeToObject() PROPERTIES
    // =========================================================================

    @Property(tries = 100)
    @Label("convertJsonNodeToObject should return null for null input")
    void convertJsonNodeToObjectShouldReturnNullForNull() {
        assertThat(JsonNodeUtils.convertJsonNodeToObject(null)).isNull();
    }

    @Property(tries = 100)
    @Label("convertJsonNodeToObject should return null for NullNode")
    void convertJsonNodeToObjectShouldReturnNullForNullNode() {
        assertThat(JsonNodeUtils.convertJsonNodeToObject(NullNode.getInstance())).isNull();
    }

    @Property(tries = 200)
    @Label("convertJsonNodeToObject should return String for TextNode")
    void convertJsonNodeToObjectShouldReturnStringForTextNode(
            @ForAll @StringLength(min = 0, max = 100) String value) {
        TextNode node = new TextNode(value);
        Object result = JsonNodeUtils.convertJsonNodeToObject(node);
        assertThat(result).isInstanceOf(String.class).isEqualTo(value);
    }

    @Property(tries = 100)
    @Label("convertJsonNodeToObject should return Boolean for BooleanNode")
    void convertJsonNodeToObjectShouldReturnBooleanForBooleanNode(
            @ForAll Boolean value) {
        BooleanNode node = BooleanNode.valueOf(value);
        Object result = JsonNodeUtils.convertJsonNodeToObject(node);
        assertThat(result).isInstanceOf(Boolean.class).isEqualTo(value);
    }

    @Property(tries = 200)
    @Label("convertJsonNodeToObject should return Integer for IntNode")
    void convertJsonNodeToObjectShouldReturnIntegerForIntNode(
            @ForAll @IntRange(min = Integer.MIN_VALUE / 2, max = Integer.MAX_VALUE / 2) Integer value) {
        IntNode node = new IntNode(value);
        Object result = JsonNodeUtils.convertJsonNodeToObject(node);
        assertThat(result).isInstanceOf(Integer.class).isEqualTo(value);
    }

    @Property(tries = 200)
    @Label("convertJsonNodeToObject should return Long for LongNode")
    void convertJsonNodeToObjectShouldReturnLongForLongNode(
            @ForAll @LongRange(min = Long.MIN_VALUE / 2, max = Long.MAX_VALUE / 2) Long value) {
        LongNode node = new LongNode(value);
        Object result = JsonNodeUtils.convertJsonNodeToObject(node);
        assertThat(result).isInstanceOf(Long.class).isEqualTo(value);
    }

    @Property(tries = 200)
    @Label("convertJsonNodeToObject should return Double for DoubleNode")
    void convertJsonNodeToObjectShouldReturnDoubleForDoubleNode(
            @ForAll @DoubleRange(min = -1000000.0, max = 1000000.0) Double value) {
        DoubleNode node = new DoubleNode(value);
        Object result = JsonNodeUtils.convertJsonNodeToObject(node);
        assertThat(result).isInstanceOf(Double.class).isEqualTo(value);
    }

    // =========================================================================
    // getValueByPath() PROPERTIES
    // =========================================================================

    @Property(tries = 100)
    @Label("getValueByPath should return null for null rootNode")
    void getValueByPathShouldReturnNullForNullRootNode() {
        assertThat(JsonNodeUtils.getValueByPath((JsonNode) null, "path")).isNull();
    }

    @Property(tries = 100)
    @Label("getValueByPath should return null for null path")
    void getValueByPathShouldReturnNullForNullPath() {
        ObjectNode node = OBJECT_MAPPER.createObjectNode();
        assertThat(JsonNodeUtils.getValueByPath(node, null)).isNull();
    }

    @Property(tries = 100)
    @Label("getValueByPath should return null for blank path")
    void getValueByPathShouldReturnNullForBlankPath() {
        ObjectNode node = OBJECT_MAPPER.createObjectNode();
        assertThat(JsonNodeUtils.getValueByPath(node, "")).isNull();
        assertThat(JsonNodeUtils.getValueByPath(node, "   ")).isNull();
    }

    @Property(tries = 200)
    @Label("getValueByPath should navigate single-level paths")
    void getValueByPathShouldNavigateSingleLevelPaths(
            @ForAll @StringLength(min = 1, max = 20) @AlphaChars String fieldName,
            @ForAll @StringLength(min = 1, max = 50) @AlphaChars String value) {
        ObjectNode node = OBJECT_MAPPER.createObjectNode();
        node.put(fieldName, value);

        JsonNode result = JsonNodeUtils.getValueByPath(node, fieldName);
        assertThat(result).isNotNull();
        assertThat(result.asText()).isEqualTo(value);
    }

    // =========================================================================
    // convertStringToJsonNode() PROPERTIES
    // =========================================================================

    @Property(tries = 100)
    @Label("convertStringToJsonNode should return null for null input")
    void convertStringToJsonNodeShouldReturnNullForNull() {
        assertThat(JsonNodeUtils.convertStringToJsonNode(null)).isNull();
    }

    @Property(tries = 100)
    @Label("convertStringToJsonNode should return null for blank input")
    void convertStringToJsonNodeShouldReturnNullForBlank() {
        assertThat(JsonNodeUtils.convertStringToJsonNode("")).isNull();
        assertThat(JsonNodeUtils.convertStringToJsonNode("   ")).isNull();
    }

    @Property(tries = 200)
    @Label("convertStringToJsonNode should return null for invalid JSON")
    void convertStringToJsonNodeShouldReturnNullForInvalidJson(
            @ForAll @StringLength(min = 1, max = 20) @AlphaChars String invalidJson) {
        // AlphaChars alone won't form valid JSON
        assertThat(JsonNodeUtils.convertStringToJsonNode(invalidJson)).isNull();
    }

    // =========================================================================
    // evaluateCondition() PROPERTIES
    // =========================================================================

    @Property(tries = 200)
    @Label("evaluateCondition equals should be symmetric")
    void evaluateConditionEqualsShouldBeSymmetric(
            @ForAll @StringLength(min = 1, max = 50) String value) {
        boolean result1 = JsonNodeUtils.evaluateCondition(value, "equals", value);
        boolean result2 = JsonNodeUtils.evaluateCondition(value, "==", value);
        assertThat(result1).isTrue();
        assertThat(result2).isTrue();
    }

    @Property(tries = 200)
    @Label("evaluateCondition notequals should be opposite of equals")
    void evaluateConditionNotEqualsShouldBeOppositeOfEquals(
            @ForAll @StringLength(min = 1, max = 50) String value1,
            @ForAll @StringLength(min = 1, max = 50) String value2) {
        boolean equalsResult = JsonNodeUtils.evaluateCondition(value1, "equals", value2);
        boolean notEqualsResult = JsonNodeUtils.evaluateCondition(value1, "notequals", value2);
        assertThat(equalsResult).isNotEqualTo(notEqualsResult);
    }

    @Property(tries = 100)
    @Label("evaluateCondition should return false for null operator")
    void evaluateConditionShouldReturnFalseForNullOperator(
            @ForAll @StringLength(min = 1, max = 50) String value) {
        boolean result = JsonNodeUtils.evaluateCondition(value, null, value);
        assertThat(result).isFalse();
    }

    @Property(tries = 200)
    @Label("evaluateCondition contains should work correctly")
    void evaluateConditionContainsShouldWorkCorrectly(
            @ForAll @StringLength(min = 5, max = 20) @AlphaChars String fullString,
            @ForAll @IntRange(min = 0, max = 2) int startOffset,
            @ForAll @IntRange(min = 2, max = 4) int length) {
        if (startOffset + length <= fullString.length()) {
            String substring = fullString.substring(startOffset, startOffset + length);
            boolean result = JsonNodeUtils.evaluateCondition(fullString, "contains", substring);
            assertThat(result).isTrue();
        }
    }

    @Property(tries = 200)
    @Label("evaluateCondition greater than should be consistent")
    void evaluateConditionGreaterThanShouldBeConsistent(
            @ForAll @IntRange(min = 0, max = 1000) Integer smaller,
            @ForAll @IntRange(min = 1, max = 1000) Integer offset) {
        Integer larger = smaller + offset;
        assertThat(JsonNodeUtils.evaluateCondition(larger, ">", smaller)).isTrue();
        assertThat(JsonNodeUtils.evaluateCondition(smaller, ">", larger)).isFalse();
    }

    @Property(tries = 200)
    @Label("evaluateCondition less than should be consistent")
    void evaluateConditionLessThanShouldBeConsistent(
            @ForAll @IntRange(min = 0, max = 1000) Integer smaller,
            @ForAll @IntRange(min = 1, max = 1000) Integer offset) {
        Integer larger = smaller + offset;
        assertThat(JsonNodeUtils.evaluateCondition(smaller, "<", larger)).isTrue();
        assertThat(JsonNodeUtils.evaluateCondition(larger, "<", smaller)).isFalse();
    }

    // =========================================================================
    // compareValues() PROPERTIES
    // =========================================================================

    @Property(tries = 200)
    @Label("compareValues should be reflexive")
    void compareValuesShouldBeReflexive(
            @ForAll @IntRange(min = -1000, max = 1000) Integer value) {
        int result = JsonNodeUtils.compareValues(value, value);
        assertThat(result).isEqualTo(0);
    }

    @Property(tries = 200)
    @Label("compareValues should be antisymmetric")
    void compareValuesShouldBeAntisymmetric(
            @ForAll @IntRange(min = -1000, max = 1000) Integer value1,
            @ForAll @IntRange(min = -1000, max = 1000) Integer value2) {
        int result1 = JsonNodeUtils.compareValues(value1, value2);
        int result2 = JsonNodeUtils.compareValues(value2, value1);
        assertThat(Integer.signum(result1)).isEqualTo(-Integer.signum(result2));
    }

    @Property(tries = 200)
    @Label("compareValues should handle different numeric types")
    void compareValuesShouldHandleDifferentNumericTypes(
            @ForAll @IntRange(min = 0, max = 1000) Integer intValue) {
        Long longValue = intValue.longValue();
        Double doubleValue = intValue.doubleValue();

        // All should compare as equal
        assertThat(JsonNodeUtils.compareValues(intValue, longValue)).isEqualTo(0);
        assertThat(JsonNodeUtils.compareValues(intValue, doubleValue)).isEqualTo(0);
        assertThat(JsonNodeUtils.compareValues(longValue, doubleValue)).isEqualTo(0);
    }

    // =========================================================================
    // getRedirectionName() / getTargetStep() PROPERTIES
    // =========================================================================

    @Property(tries = 100)
    @Label("getRedirectionName should return default for null")
    void getRedirectionNameShouldReturnDefaultForNull() {
        assertThat(JsonNodeUtils.getRedirectionName(null))
            .isEqualTo(JsonNodeUtils.DEFAULT_REDIRECTION_NAME);
    }

    @Property(tries = 100)
    @Label("getTargetStep should return null for null")
    void getTargetStepShouldReturnNullForNull() {
        assertThat(JsonNodeUtils.getTargetStep(null)).isNull();
    }
}
