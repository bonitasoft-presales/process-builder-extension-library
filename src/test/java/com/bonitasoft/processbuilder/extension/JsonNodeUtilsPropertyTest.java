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
        assertThat(JsonNodeUtils.OP_NOT_CONTAINS).isNotNull().isNotBlank();
        assertThat(JsonNodeUtils.OP_GREATER_THAN).isNotNull().isNotBlank();
        assertThat(JsonNodeUtils.OP_GREATER_THAN_SYMBOL).isNotNull().isNotBlank();
        assertThat(JsonNodeUtils.OP_LESS_THAN).isNotNull().isNotBlank();
        assertThat(JsonNodeUtils.OP_LESS_THAN_SYMBOL).isNotNull().isNotBlank();
        assertThat(JsonNodeUtils.OP_GREATER_OR_EQUAL).isNotNull().isNotBlank();
        assertThat(JsonNodeUtils.OP_GREATER_OR_EQUAL_SYMBOL).isNotNull().isNotBlank();
        assertThat(JsonNodeUtils.OP_LESS_OR_EQUAL).isNotNull().isNotBlank();
        assertThat(JsonNodeUtils.OP_LESS_OR_EQUAL_SYMBOL).isNotNull().isNotBlank();
        assertThat(JsonNodeUtils.OP_IS_EMPTY).isNotNull().isNotBlank();
        assertThat(JsonNodeUtils.OP_IS_NOT_EMPTY).isNotNull().isNotBlank();
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
    @Label("evaluateCondition not_equals should be opposite of equals")
    void evaluateConditionNotEqualsShouldBeOppositeOfEquals(
            @ForAll @StringLength(min = 1, max = 50) String value1,
            @ForAll @StringLength(min = 1, max = 50) String value2) {
        boolean equalsResult = JsonNodeUtils.evaluateCondition(value1, "equals", value2);
        boolean notEqualsResult = JsonNodeUtils.evaluateCondition(value1, "not_equals", value2);
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
    @Label("evaluateCondition not_contains should be opposite of contains")
    void evaluateConditionNotContainsShouldBeOppositeOfContains(
            @ForAll @StringLength(min = 5, max = 30) @AlphaChars String fullString,
            @ForAll @StringLength(min = 1, max = 5) @AlphaChars String searchString) {
        boolean containsResult = JsonNodeUtils.evaluateCondition(fullString, "contains", searchString);
        boolean notContainsResult = JsonNodeUtils.evaluateCondition(fullString, "not_contains", searchString);
        assertThat(containsResult).isNotEqualTo(notContainsResult);
    }

    @Property(tries = 100)
    @Label("evaluateCondition not_contains should return true for null current value")
    void evaluateConditionNotContainsShouldReturnTrueForNullCurrent() {
        assertThat(JsonNodeUtils.evaluateCondition(null, "not_contains", "test")).isTrue();
    }

    @Property(tries = 200)
    @Label("evaluateCondition not_contains should return true when substring not found")
    void evaluateConditionNotContainsShouldReturnTrueWhenNotFound(
            @ForAll @StringLength(min = 5, max = 20) @AlphaChars String haystack) {
        // "zzzzz" is unlikely to be in a purely alpha string of limited length
        assertThat(JsonNodeUtils.evaluateCondition(haystack, "not_contains", "12345")).isTrue();
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

    // =========================================================================
    // is_empty / is_not_empty PROPERTIES
    // =========================================================================

    @Property(tries = 100)
    @Label("is_empty should return true for null")
    void isEmptyShouldReturnTrueForNull() {
        assertThat(JsonNodeUtils.evaluateCondition(null, "is_empty", null)).isTrue();
    }

    @Property(tries = 100)
    @Label("is_empty should return true for empty string")
    void isEmptyShouldReturnTrueForEmptyString() {
        assertThat(JsonNodeUtils.evaluateCondition("", "is_empty", null)).isTrue();
    }

    @Property(tries = 200)
    @Label("is_empty should return true for whitespace-only strings")
    void isEmptyShouldReturnTrueForWhitespaceStrings(
            @ForAll @IntRange(min = 1, max = 20) int spaces) {
        String whitespace = " ".repeat(spaces);
        assertThat(JsonNodeUtils.evaluateCondition(whitespace, "is_empty", null)).isTrue();
    }

    @Property(tries = 200)
    @Label("is_empty should return false for non-blank strings")
    void isEmptyShouldReturnFalseForNonBlankStrings(
            @ForAll @StringLength(min = 1, max = 50) @AlphaChars String value) {
        assertThat(JsonNodeUtils.evaluateCondition(value, "is_empty", null)).isFalse();
    }

    @Property(tries = 200)
    @Label("is_not_empty should return true for non-blank strings")
    void isNotEmptyShouldReturnTrueForNonBlankStrings(
            @ForAll @StringLength(min = 1, max = 50) @AlphaChars String value) {
        assertThat(JsonNodeUtils.evaluateCondition(value, "is_not_empty", null)).isTrue();
    }

    @Property(tries = 100)
    @Label("is_not_empty should return false for null")
    void isNotEmptyShouldReturnFalseForNull() {
        assertThat(JsonNodeUtils.evaluateCondition(null, "is_not_empty", null)).isFalse();
    }

    @Property(tries = 200)
    @Label("is_empty and is_not_empty should be opposites")
    void isEmptyAndIsNotEmptyShouldBeOpposites(
            @ForAll @StringLength(min = 0, max = 50) String value) {
        boolean isEmpty = JsonNodeUtils.evaluateCondition(value, "is_empty", null);
        boolean isNotEmpty = JsonNodeUtils.evaluateCondition(value, "is_not_empty", null);
        assertThat(isEmpty).isNotEqualTo(isNotEmpty);
    }

    @Property(tries = 200)
    @Label("is_empty should return false for numbers")
    void isEmptyShouldReturnFalseForNumbers(
            @ForAll @IntRange(min = -1000, max = 1000) Integer value) {
        assertThat(JsonNodeUtils.evaluateCondition(value, "is_empty", null)).isFalse();
    }

    @Property(tries = 200)
    @Label("is_not_empty should return true for numbers")
    void isNotEmptyShouldReturnTrueForNumbers(
            @ForAll @IntRange(min = -1000, max = 1000) Integer value) {
        assertThat(JsonNodeUtils.evaluateCondition(value, "is_not_empty", null)).isTrue();
    }

    @Property(tries = 100)
    @Label("is_empty should be case-insensitive")
    void isEmptyShouldBeCaseInsensitive() {
        assertThat(JsonNodeUtils.evaluateCondition(null, "IS_EMPTY", null)).isTrue();
        assertThat(JsonNodeUtils.evaluateCondition(null, "Is_Empty", null)).isTrue();
        assertThat(JsonNodeUtils.evaluateCondition(null, "is_empty", null)).isTrue();
    }

    @Property(tries = 100)
    @Label("is_not_empty should be case-insensitive")
    void isNotEmptyShouldBeCaseInsensitive() {
        assertThat(JsonNodeUtils.evaluateCondition("value", "IS_NOT_EMPTY", null)).isTrue();
        assertThat(JsonNodeUtils.evaluateCondition("value", "Is_Not_Empty", null)).isTrue();
        assertThat(JsonNodeUtils.evaluateCondition("value", "is_not_empty", null)).isTrue();
    }
}
