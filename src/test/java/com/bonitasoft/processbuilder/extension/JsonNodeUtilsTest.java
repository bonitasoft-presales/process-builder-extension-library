package com.bonitasoft.processbuilder.extension;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link JsonNodeUtils}.
 * <p>
 * Tests cover all public methods including edge cases, boundary conditions,
 * and error handling to achieve 100% code coverage.
 * </p>
 */
class JsonNodeUtilsTest {

    private static final JsonNodeFactory NODE_FACTORY = JsonNodeFactory.instance;

    // -------------------------------------------------------------------------
    // Constructor Tests
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Private constructor should throw UnsupportedOperationException")
        void constructor_should_prevent_instantiation() throws Exception {
            Constructor<JsonNodeUtils> constructor = JsonNodeUtils.class.getDeclaredConstructor();
            constructor.setAccessible(true);

            InvocationTargetException exception = assertThrows(
                InvocationTargetException.class,
                constructor::newInstance
            );

            assertThat(exception.getCause())
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessageContaining("cannot be instantiated");
        }
    }

    // -------------------------------------------------------------------------
    // Operator Constants Tests
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("Operator Constants Tests")
    class OperatorConstantsTests {

        @Test
        @DisplayName("Should have correct operator constants")
        void should_have_correct_operator_constants() {
            assertEquals("equals", JsonNodeUtils.OP_EQUALS);
            assertEquals("==", JsonNodeUtils.OP_EQUALS_SYMBOL);
            assertEquals("notequals", JsonNodeUtils.OP_NOT_EQUALS);
            assertEquals("!=", JsonNodeUtils.OP_NOT_EQUALS_SYMBOL);
            assertEquals("contains", JsonNodeUtils.OP_CONTAINS);
            assertEquals("greaterthan", JsonNodeUtils.OP_GREATER_THAN);
            assertEquals(">", JsonNodeUtils.OP_GREATER_THAN_SYMBOL);
            assertEquals("lessthan", JsonNodeUtils.OP_LESS_THAN);
            assertEquals("<", JsonNodeUtils.OP_LESS_THAN_SYMBOL);
            assertEquals("greaterorequal", JsonNodeUtils.OP_GREATER_OR_EQUAL);
            assertEquals(">=", JsonNodeUtils.OP_GREATER_OR_EQUAL_SYMBOL);
            assertEquals("lessorequal", JsonNodeUtils.OP_LESS_OR_EQUAL);
            assertEquals("<=", JsonNodeUtils.OP_LESS_OR_EQUAL_SYMBOL);
        }
    }

    // -------------------------------------------------------------------------
    // convertJsonNodeToObject Tests
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("convertJsonNodeToObject Tests")
    class ConvertJsonNodeToObjectTests {

        @Test
        @DisplayName("Should return null for null node")
        void should_return_null_for_null_node() {
            assertNull(JsonNodeUtils.convertJsonNodeToObject(null));
        }

        @Test
        @DisplayName("Should return null for NullNode")
        void should_return_null_for_null_value_node() {
            JsonNode nullNode = NODE_FACTORY.nullNode();
            assertNull(JsonNodeUtils.convertJsonNodeToObject(nullNode));
        }

        @Test
        @DisplayName("Should return String for textual node")
        void should_return_string_for_textual_node() {
            JsonNode textNode = NODE_FACTORY.textNode("hello world");
            Object result = JsonNodeUtils.convertJsonNodeToObject(textNode);

            assertThat(result)
                .isInstanceOf(String.class)
                .isEqualTo("hello world");
        }

        @Test
        @DisplayName("Should return Boolean for boolean node (true)")
        void should_return_boolean_true_for_boolean_node() {
            JsonNode boolNode = NODE_FACTORY.booleanNode(true);
            Object result = JsonNodeUtils.convertJsonNodeToObject(boolNode);

            assertThat(result)
                .isInstanceOf(Boolean.class)
                .isEqualTo(true);
        }

        @Test
        @DisplayName("Should return Boolean for boolean node (false)")
        void should_return_boolean_false_for_boolean_node() {
            JsonNode boolNode = NODE_FACTORY.booleanNode(false);
            Object result = JsonNodeUtils.convertJsonNodeToObject(boolNode);

            assertThat(result)
                .isInstanceOf(Boolean.class)
                .isEqualTo(false);
        }

        @Test
        @DisplayName("Should return Integer for int node")
        void should_return_integer_for_int_node() {
            JsonNode intNode = NODE_FACTORY.numberNode(42);
            Object result = JsonNodeUtils.convertJsonNodeToObject(intNode);

            assertThat(result)
                .isInstanceOf(Integer.class)
                .isEqualTo(42);
        }

        @Test
        @DisplayName("Should return Long for long node")
        void should_return_long_for_long_node() {
            long longValue = 9_223_372_036_854_775_807L;
            JsonNode longNode = NODE_FACTORY.numberNode(longValue);
            Object result = JsonNodeUtils.convertJsonNodeToObject(longNode);

            assertThat(result)
                .isInstanceOf(Long.class)
                .isEqualTo(longValue);
        }

        @Test
        @DisplayName("Should return Double for double node")
        void should_return_double_for_double_node() {
            JsonNode doubleNode = NODE_FACTORY.numberNode(3.14159);
            Object result = JsonNodeUtils.convertJsonNodeToObject(doubleNode);

            assertThat(result)
                .isInstanceOf(Double.class)
                .isEqualTo(3.14159);
        }

        @Test
        @DisplayName("Should return Double for float node")
        void should_return_double_for_float_node() {
            JsonNode floatNode = NODE_FACTORY.numberNode(2.5f);
            Object result = JsonNodeUtils.convertJsonNodeToObject(floatNode);

            assertThat(result)
                .isInstanceOf(Double.class)
                .isEqualTo(2.5);
        }

        @Test
        @DisplayName("Should return JSON string for array node")
        void should_return_json_string_for_array_node() {
            ArrayNode arrayNode = NODE_FACTORY.arrayNode();
            arrayNode.add(1);
            arrayNode.add(2);
            arrayNode.add(3);

            Object result = JsonNodeUtils.convertJsonNodeToObject(arrayNode);

            assertThat(result)
                .isInstanceOf(String.class)
                .isEqualTo("[1,2,3]");
        }

        @Test
        @DisplayName("Should return JSON string for object node")
        void should_return_json_string_for_object_node() {
            ObjectNode objectNode = NODE_FACTORY.objectNode();
            objectNode.put("name", "test");
            objectNode.put("value", 123);

            Object result = JsonNodeUtils.convertJsonNodeToObject(objectNode);

            assertThat(result)
                .isInstanceOf(String.class)
                .isEqualTo("{\"name\":\"test\",\"value\":123}");
        }

        @Test
        @DisplayName("Should return text representation for other node types")
        void should_return_text_for_other_node_types() throws Exception {
            // Use a BigInteger node which is a number but not int/long/double/float
            ObjectMapper mapper = new ObjectMapper();
            JsonNode bigIntNode = mapper.readTree("123456789012345678901234567890");

            Object result = JsonNodeUtils.convertJsonNodeToObject(bigIntNode);

            assertThat(result).isNotNull();
        }
    }

    // -------------------------------------------------------------------------
    // evaluateCondition Tests
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("evaluateCondition Tests")
    class EvaluateConditionTests {

        @Test
        @DisplayName("Should return false for null operator")
        void should_return_false_for_null_operator() {
            boolean result = JsonNodeUtils.evaluateCondition("value", null, "expected");
            assertFalse(result);
        }

        @Test
        @DisplayName("Should return false for null operator with null logger")
        void should_return_false_for_null_operator_with_null_logger() {
            boolean result = JsonNodeUtils.evaluateCondition("value", null, "expected");
            assertFalse(result);
        }

        @Test
        @DisplayName("Should return false for unknown operator")
        void should_return_false_for_unknown_operator() {
            boolean result = JsonNodeUtils.evaluateCondition("value", "unknown", "expected");
            assertFalse(result);
        }

        @Test
        @DisplayName("Should return false for unknown operator with null logger")
        void should_return_false_for_unknown_operator_with_null_logger() {
            boolean result = JsonNodeUtils.evaluateCondition("value", "unknown", "expected");
            assertFalse(result);
        }

        // Equals tests
        @Nested
        @DisplayName("Equals Operator Tests")
        class EqualsOperatorTests {

            @Test
            @DisplayName("equals should return true for equal strings")
            void equals_should_return_true_for_equal_strings() {
                assertTrue(JsonNodeUtils.evaluateCondition("test", "equals", "test"));
            }

            @Test
            @DisplayName("== should return true for equal strings")
            void equals_symbol_should_return_true_for_equal_strings() {
                assertTrue(JsonNodeUtils.evaluateCondition("test", "==", "test"));
            }

            @Test
            @DisplayName("equals should return false for different strings")
            void equals_should_return_false_for_different_strings() {
                assertFalse(JsonNodeUtils.evaluateCondition("test", "equals", "other"));
            }

            @Test
            @DisplayName("equals should return true for both null values")
            void equals_should_return_true_for_both_null() {
                assertTrue(JsonNodeUtils.evaluateCondition(null, "equals", null));
            }

            @Test
            @DisplayName("equals should return false when only current is null")
            void equals_should_return_false_when_current_is_null() {
                assertFalse(JsonNodeUtils.evaluateCondition(null, "equals", "test"));
            }

            @Test
            @DisplayName("equals should return false when only expected is null")
            void equals_should_return_false_when_expected_is_null() {
                assertFalse(JsonNodeUtils.evaluateCondition("test", "equals", null));
            }

            @Test
            @DisplayName("EQUALS (uppercase) should work case-insensitively")
            void equals_should_work_case_insensitively() {
                assertTrue(JsonNodeUtils.evaluateCondition("test", "EQUALS", "test"));
            }
        }

        // Not Equals tests
        @Nested
        @DisplayName("Not Equals Operator Tests")
        class NotEqualsOperatorTests {

            @Test
            @DisplayName("notequals should return true for different strings")
            void notequals_should_return_true_for_different_strings() {
                assertTrue(JsonNodeUtils.evaluateCondition("test", "notequals", "other"));
            }

            @Test
            @DisplayName("!= should return true for different strings")
            void notequals_symbol_should_return_true_for_different_strings() {
                assertTrue(JsonNodeUtils.evaluateCondition("test", "!=", "other"));
            }

            @Test
            @DisplayName("notequals should return false for equal strings")
            void notequals_should_return_false_for_equal_strings() {
                assertFalse(JsonNodeUtils.evaluateCondition("test", "notequals", "test"));
            }

            @Test
            @DisplayName("notequals should return false for both null values")
            void notequals_should_return_false_for_both_null() {
                assertFalse(JsonNodeUtils.evaluateCondition(null, "notequals", null));
            }

            @Test
            @DisplayName("notequals should return true when only current is null")
            void notequals_should_return_true_when_current_is_null() {
                assertTrue(JsonNodeUtils.evaluateCondition(null, "notequals", "test"));
            }
        }

        // Contains tests
        @Nested
        @DisplayName("Contains Operator Tests")
        class ContainsOperatorTests {

            @Test
            @DisplayName("contains should return true when substring exists")
            void contains_should_return_true_when_substring_exists() {
                assertTrue(JsonNodeUtils.evaluateCondition("hello world", "contains", "world"));
            }

            @Test
            @DisplayName("contains should return false when substring not found")
            void contains_should_return_false_when_substring_not_found() {
                assertFalse(JsonNodeUtils.evaluateCondition("hello world", "contains", "xyz"));
            }

            @Test
            @DisplayName("contains should return false when current value is null")
            void contains_should_return_false_when_current_is_null() {
                assertFalse(JsonNodeUtils.evaluateCondition(null, "contains", "test"));
            }

            @Test
            @DisplayName("contains should return true when expected is null (empty string)")
            void contains_should_return_true_when_expected_is_null() {
                assertTrue(JsonNodeUtils.evaluateCondition("test", "contains", null));
            }

            @Test
            @DisplayName("contains should work with numbers converted to strings")
            void contains_should_work_with_numbers() {
                assertTrue(JsonNodeUtils.evaluateCondition(12345, "contains", "234"));
            }
        }

        // Greater Than tests
        @Nested
        @DisplayName("Greater Than Operator Tests")
        class GreaterThanOperatorTests {

            @Test
            @DisplayName("greaterthan should return true when current > expected")
            void greaterthan_should_return_true_when_current_greater() {
                assertTrue(JsonNodeUtils.evaluateCondition(10, "greaterthan", 5));
            }

            @Test
            @DisplayName("> should return true when current > expected")
            void greaterthan_symbol_should_return_true_when_current_greater() {
                assertTrue(JsonNodeUtils.evaluateCondition(10, ">", 5));
            }

            @Test
            @DisplayName("greaterthan should return false when current < expected")
            void greaterthan_should_return_false_when_current_less() {
                assertFalse(JsonNodeUtils.evaluateCondition(5, "greaterthan", 10));
            }

            @Test
            @DisplayName("greaterthan should return false when current == expected")
            void greaterthan_should_return_false_when_equal() {
                assertFalse(JsonNodeUtils.evaluateCondition(5, "greaterthan", 5));
            }

            @Test
            @DisplayName("greaterthan should return false for non-comparable values")
            void greaterthan_should_return_false_for_non_comparable() {
                assertFalse(JsonNodeUtils.evaluateCondition(new Object(), "greaterthan", new Object()));
            }
        }

        // Less Than tests
        @Nested
        @DisplayName("Less Than Operator Tests")
        class LessThanOperatorTests {

            @Test
            @DisplayName("lessthan should return true when current < expected")
            void lessthan_should_return_true_when_current_less() {
                assertTrue(JsonNodeUtils.evaluateCondition(5, "lessthan", 10));
            }

            @Test
            @DisplayName("< should return true when current < expected")
            void lessthan_symbol_should_return_true_when_current_less() {
                assertTrue(JsonNodeUtils.evaluateCondition(5, "<", 10));
            }

            @Test
            @DisplayName("lessthan should return false when current > expected")
            void lessthan_should_return_false_when_current_greater() {
                assertFalse(JsonNodeUtils.evaluateCondition(10, "lessthan", 5));
            }

            @Test
            @DisplayName("lessthan should return false when current == expected")
            void lessthan_should_return_false_when_equal() {
                assertFalse(JsonNodeUtils.evaluateCondition(5, "lessthan", 5));
            }

            @Test
            @DisplayName("lessthan should return false for non-comparable values")
            void lessthan_should_return_false_for_non_comparable() {
                assertFalse(JsonNodeUtils.evaluateCondition(new Object(), "lessthan", new Object()));
            }
        }

        // Greater Or Equal tests
        @Nested
        @DisplayName("Greater Or Equal Operator Tests")
        class GreaterOrEqualOperatorTests {

            @Test
            @DisplayName("greaterorequal should return true when current > expected")
            void greaterorequal_should_return_true_when_current_greater() {
                assertTrue(JsonNodeUtils.evaluateCondition(10, "greaterorequal", 5));
            }

            @Test
            @DisplayName(">= should return true when current >= expected")
            void greaterorequal_symbol_should_return_true_when_current_greater_or_equal() {
                assertTrue(JsonNodeUtils.evaluateCondition(10, ">=", 5));
            }

            @Test
            @DisplayName("greaterorequal should return true when current == expected")
            void greaterorequal_should_return_true_when_equal() {
                assertTrue(JsonNodeUtils.evaluateCondition(5, "greaterorequal", 5));
            }

            @Test
            @DisplayName("greaterorequal should return false when current < expected")
            void greaterorequal_should_return_false_when_current_less() {
                assertFalse(JsonNodeUtils.evaluateCondition(3, "greaterorequal", 5));
            }

            @Test
            @DisplayName("greaterorequal should return false for non-comparable values")
            void greaterorequal_should_return_false_for_non_comparable() {
                assertFalse(JsonNodeUtils.evaluateCondition(new Object(), ">=", new Object()));
            }
        }

        // Less Or Equal tests
        @Nested
        @DisplayName("Less Or Equal Operator Tests")
        class LessOrEqualOperatorTests {

            @Test
            @DisplayName("lessorequal should return true when current < expected")
            void lessorequal_should_return_true_when_current_less() {
                assertTrue(JsonNodeUtils.evaluateCondition(5, "lessorequal", 10));
            }

            @Test
            @DisplayName("<= should return true when current <= expected")
            void lessorequal_symbol_should_return_true_when_current_less_or_equal() {
                assertTrue(JsonNodeUtils.evaluateCondition(5, "<=", 10));
            }

            @Test
            @DisplayName("lessorequal should return true when current == expected")
            void lessorequal_should_return_true_when_equal() {
                assertTrue(JsonNodeUtils.evaluateCondition(5, "lessorequal", 5));
            }

            @Test
            @DisplayName("lessorequal should return false when current > expected")
            void lessorequal_should_return_false_when_current_greater() {
                assertFalse(JsonNodeUtils.evaluateCondition(10, "lessorequal", 5));
            }

            @Test
            @DisplayName("lessorequal should return false for non-comparable values")
            void lessorequal_should_return_false_for_non_comparable() {
                assertFalse(JsonNodeUtils.evaluateCondition(new Object(), "<=", new Object()));
            }
        }
    }

    // -------------------------------------------------------------------------
    // compareValues Tests
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("compareValues Tests")
    class CompareValuesTests {

        @Test
        @DisplayName("Should throw NullPointerException when actual is null")
        void should_throw_when_actual_is_null() {
            assertThatThrownBy(() -> JsonNodeUtils.compareValues(null, "expected"))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Actual value cannot be null");
        }

        @Test
        @DisplayName("Should throw NullPointerException when expected is null")
        void should_throw_when_expected_is_null() {
            assertThatThrownBy(() -> JsonNodeUtils.compareValues("actual", null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Expected value cannot be null");
        }

        @Test
        @DisplayName("Should compare Integer and Double as numbers")
        void should_compare_integer_and_double_as_numbers() {
            int result = JsonNodeUtils.compareValues(10, 5.0);
            assertThat(result).isGreaterThan(0);
        }

        @Test
        @DisplayName("Should compare Long and Integer as numbers")
        void should_compare_long_and_integer_as_numbers() {
            int result = JsonNodeUtils.compareValues(5L, 10);
            assertThat(result).isLessThan(0);
        }

        @Test
        @DisplayName("Should compare Float and Double as numbers")
        void should_compare_float_and_double_as_numbers() {
            int result = JsonNodeUtils.compareValues(5.0f, 5.0);
            assertThat(result).isEqualTo(0);
        }

        @Test
        @DisplayName("Should compare same type directly - strings")
        void should_compare_same_type_strings() {
            int result = JsonNodeUtils.compareValues("apple", "banana");
            assertThat(result).isLessThan(0);
        }

        @Test
        @DisplayName("Should compare same type directly - integers")
        void should_compare_same_type_integers() {
            int result = JsonNodeUtils.compareValues(10, 5);
            assertThat(result).isGreaterThan(0);
        }

        @Test
        @DisplayName("Should compare same type directly - equal values")
        void should_compare_same_type_equal_values() {
            int result = JsonNodeUtils.compareValues("test", "test");
            assertThat(result).isEqualTo(0);
        }

        @Test
        @DisplayName("Should compare different types as strings")
        void should_compare_different_types_as_strings() {
            // String "5" vs Boolean true - should compare as strings
            int result = JsonNodeUtils.compareValues("5", true);
            // "5" vs "true" - string comparison
            assertThat(result).isNotEqualTo(0);
        }

        @Test
        @DisplayName("Should compare with null logger (uses class logger)")
        void should_compare_with_null_logger() {
            int result = JsonNodeUtils.compareValues(10, 5);
            assertThat(result).isGreaterThan(0);
        }

        @Test
        @DisplayName("Should handle Double comparison edge cases")
        void should_handle_double_comparison_edge_cases() {
            // Very close double values
            int result = JsonNodeUtils.compareValues(1.0000001, 1.0000002);
            assertThat(result).isLessThan(0);
        }

        @Test
        @DisplayName("Should compare negative numbers correctly")
        void should_compare_negative_numbers() {
            int result = JsonNodeUtils.compareValues(-10, -5);
            assertThat(result).isLessThan(0);
        }
    }

    // -------------------------------------------------------------------------
    // Integration / Combined Tests
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("Integration Tests")
    class IntegrationTests {

        @Test
        @DisplayName("Should convert JsonNode and evaluate condition")
        void should_convert_and_evaluate() {
            JsonNode currentNode = NODE_FACTORY.numberNode(100);
            JsonNode expectedNode = NODE_FACTORY.numberNode(50);

            Object currentValue = JsonNodeUtils.convertJsonNodeToObject(currentNode);
            Object expectedValue = JsonNodeUtils.convertJsonNodeToObject(expectedNode);

            boolean result = JsonNodeUtils.evaluateCondition(currentValue, ">", expectedValue);
            assertTrue(result);
        }

        @Test
        @DisplayName("Should handle string comparison from JsonNode")
        void should_handle_string_comparison_from_jsonnode() {
            JsonNode currentNode = NODE_FACTORY.textNode("hello world");
            JsonNode expectedNode = NODE_FACTORY.textNode("world");

            Object currentValue = JsonNodeUtils.convertJsonNodeToObject(currentNode);
            Object expectedValue = JsonNodeUtils.convertJsonNodeToObject(expectedNode);

            boolean result = JsonNodeUtils.evaluateCondition(currentValue, "contains", expectedValue);
            assertTrue(result);
        }

        @Test
        @DisplayName("Should handle boolean equality from JsonNode")
        void should_handle_boolean_equality_from_jsonnode() {
            JsonNode currentNode = NODE_FACTORY.booleanNode(true);
            JsonNode expectedNode = NODE_FACTORY.booleanNode(true);

            Object currentValue = JsonNodeUtils.convertJsonNodeToObject(currentNode);
            Object expectedValue = JsonNodeUtils.convertJsonNodeToObject(expectedNode);

            boolean result = JsonNodeUtils.evaluateCondition(currentValue, "==", expectedValue);
            assertTrue(result);
        }
    }

    // -------------------------------------------------------------------------
    // Parameterized Tests for Operators
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("Parameterized Operator Tests")
    class ParameterizedOperatorTests {

        static Stream<Arguments> equalityOperators() {
            return Stream.of(
                Arguments.of("equals", "test", "test", true),
                Arguments.of("==", "test", "test", true),
                Arguments.of("EQUALS", "test", "test", true),
                Arguments.of("Equals", "test", "test", true),
                Arguments.of("equals", "test", "other", false),
                Arguments.of("==", 100, 100, true),
                Arguments.of("equals", 3.14, 3.14, true)
            );
        }

        @ParameterizedTest
        @MethodSource("equalityOperators")
        @DisplayName("Equality operators should work correctly")
        void equality_operators_should_work(String operator, Object current, Object expected, boolean expectedResult) {
            boolean result = JsonNodeUtils.evaluateCondition(current, operator, expected);
            assertEquals(expectedResult, result);
        }

        static Stream<Arguments> comparisonOperators() {
            return Stream.of(
                Arguments.of(">", 10, 5, true),
                Arguments.of(">", 5, 10, false),
                Arguments.of("<", 5, 10, true),
                Arguments.of("<", 10, 5, false),
                Arguments.of(">=", 10, 10, true),
                Arguments.of(">=", 10, 5, true),
                Arguments.of(">=", 5, 10, false),
                Arguments.of("<=", 10, 10, true),
                Arguments.of("<=", 5, 10, true),
                Arguments.of("<=", 10, 5, false)
            );
        }

        @ParameterizedTest
        @MethodSource("comparisonOperators")
        @DisplayName("Comparison operators should work correctly")
        void comparison_operators_should_work(String operator, Object current, Object expected, boolean expectedResult) {
            boolean result = JsonNodeUtils.evaluateCondition(current, operator, expected);
            assertEquals(expectedResult, result);
        }
    }

    // -------------------------------------------------------------------------
    // evaluateAllConditions Tests
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("evaluateAllConditions Tests")
    class EvaluateAllConditionsTests {

        /**
         * Helper to create a condition object node.
         */
        private ObjectNode createCondition(String stepRef, String fieldRef,
                                           String operator, Object value) {
            ObjectNode condition = NODE_FACTORY.objectNode();
            if (stepRef != null) {
                condition.put("stepRef", stepRef);
            }
            if (fieldRef != null) {
                condition.put("fieldRef", fieldRef);
            }
            if (operator != null) {
                condition.put("operator", operator);
            }
            if (value != null) {
                if (value instanceof String) {
                    condition.put("value", (String) value);
                } else if (value instanceof Integer) {
                    condition.put("value", (Integer) value);
                } else if (value instanceof Boolean) {
                    condition.put("value", (Boolean) value);
                } else if (value instanceof Double) {
                    condition.put("value", (Double) value);
                }
            }
            return condition;
        }

        /**
         * Simple resolver that returns fixed values based on fieldRef.
         */
        private BiFunction<String, String, String> createSimpleResolver() {
            return (fieldRef, stepRef) -> {
                if ("status".equals(fieldRef)) {
                    return "APPROVED";
                }
                if ("amount".equals(fieldRef)) {
                    return "100";
                }
                if ("name".equals(fieldRef)) {
                    return "John";
                }
                return null;
            };
        }

        // --- Null and Empty Conditions Tests ---

        @Test
        @DisplayName("Should return true when conditionsNode is null")
        void should_return_true_when_conditions_null() {
            BiFunction<String, String, String> resolver = (f, s) -> "value";
            boolean result = JsonNodeUtils.evaluateAllConditions(null, resolver);
            assertTrue(result);
        }

        @Test
        @DisplayName("Should return true when conditionsNode is empty array")
        void should_return_true_when_conditions_empty() {
            ArrayNode emptyArray = NODE_FACTORY.arrayNode();
            BiFunction<String, String, String> resolver = (f, s) -> "value";
            boolean result = JsonNodeUtils.evaluateAllConditions(emptyArray, resolver);
            assertTrue(result);
        }

        @Test
        @DisplayName("Should return true when conditionsNode is not an array")
        void should_return_true_when_conditions_not_array() {
            ObjectNode notAnArray = NODE_FACTORY.objectNode();
            notAnArray.put("key", "value");
            BiFunction<String, String, String> resolver = (f, s) -> "value";
            boolean result = JsonNodeUtils.evaluateAllConditions(notAnArray, resolver);
            assertTrue(result);
        }

        @Test
        @DisplayName("Should return false when dataValueResolver is null")
        void should_return_false_when_resolver_null() {
            ArrayNode conditions = NODE_FACTORY.arrayNode();
            conditions.add(createCondition("step1", "field1", "equals", "value"));
            boolean result = JsonNodeUtils.evaluateAllConditions(conditions, null);
            assertFalse(result);
        }

        // --- Single Condition Tests ---

        @Test
        @DisplayName("Should return true when single condition matches with equals")
        void should_return_true_single_condition_equals_match() {
            ArrayNode conditions = NODE_FACTORY.arrayNode();
            conditions.add(createCondition("step1", "status", "equals", "APPROVED"));

            boolean result = JsonNodeUtils.evaluateAllConditions(conditions, createSimpleResolver());
            assertTrue(result);
        }

        @Test
        @DisplayName("Should return false when single condition does not match")
        void should_return_false_single_condition_no_match() {
            ArrayNode conditions = NODE_FACTORY.arrayNode();
            conditions.add(createCondition("step1", "status", "equals", "REJECTED"));

            boolean result = JsonNodeUtils.evaluateAllConditions(conditions, createSimpleResolver());
            assertFalse(result);
        }

        @Test
        @DisplayName("Should return true with contains operator")
        void should_return_true_with_contains_operator() {
            ArrayNode conditions = NODE_FACTORY.arrayNode();
            conditions.add(createCondition("step1", "status", "contains", "APPROV"));

            boolean result = JsonNodeUtils.evaluateAllConditions(conditions, createSimpleResolver());
            assertTrue(result);
        }

        @Test
        @DisplayName("Should return true with notequals operator")
        void should_return_true_with_notequals_operator() {
            ArrayNode conditions = NODE_FACTORY.arrayNode();
            conditions.add(createCondition("step1", "status", "notequals", "REJECTED"));

            boolean result = JsonNodeUtils.evaluateAllConditions(conditions, createSimpleResolver());
            assertTrue(result);
        }

        // --- Multiple Conditions Tests ---

        @Test
        @DisplayName("Should return true when all conditions match")
        void should_return_true_all_conditions_match() {
            ArrayNode conditions = NODE_FACTORY.arrayNode();
            conditions.add(createCondition("step1", "status", "equals", "APPROVED"));
            conditions.add(createCondition("step1", "name", "equals", "John"));

            boolean result = JsonNodeUtils.evaluateAllConditions(conditions, createSimpleResolver());
            assertTrue(result);
        }

        @Test
        @DisplayName("Should return false when one condition fails (short-circuit)")
        void should_return_false_one_condition_fails() {
            ArrayNode conditions = NODE_FACTORY.arrayNode();
            conditions.add(createCondition("step1", "status", "equals", "APPROVED"));
            conditions.add(createCondition("step1", "name", "equals", "Jane")); // This will fail

            boolean result = JsonNodeUtils.evaluateAllConditions(conditions, createSimpleResolver());
            assertFalse(result);
        }

        @Test
        @DisplayName("Should return false when first condition fails (short-circuit)")
        void should_return_false_first_condition_fails() {
            ArrayNode conditions = NODE_FACTORY.arrayNode();
            conditions.add(createCondition("step1", "status", "equals", "REJECTED")); // Fails first
            conditions.add(createCondition("step1", "name", "equals", "John"));

            boolean result = JsonNodeUtils.evaluateAllConditions(conditions, createSimpleResolver());
            assertFalse(result);
        }

        // --- Validation Error Tests ---

        @Test
        @DisplayName("Should return false when operator is null")
        void should_return_false_operator_null() {
            ArrayNode conditions = NODE_FACTORY.arrayNode();
            conditions.add(createCondition("step1", "status", null, "APPROVED"));

            boolean result = JsonNodeUtils.evaluateAllConditions(conditions, createSimpleResolver());
            assertFalse(result);
        }

        @Test
        @DisplayName("Should return false when operator is blank")
        void should_return_false_operator_blank() {
            ArrayNode conditions = NODE_FACTORY.arrayNode();
            conditions.add(createCondition("step1", "status", "   ", "APPROVED"));

            boolean result = JsonNodeUtils.evaluateAllConditions(conditions, createSimpleResolver());
            assertFalse(result);
        }

        @Test
        @DisplayName("Should return false when fieldRef is null")
        void should_return_false_fieldRef_null() {
            ArrayNode conditions = NODE_FACTORY.arrayNode();
            conditions.add(createCondition("step1", null, "equals", "APPROVED"));

            boolean result = JsonNodeUtils.evaluateAllConditions(conditions, createSimpleResolver());
            assertFalse(result);
        }

        @Test
        @DisplayName("Should return false when fieldRef is blank")
        void should_return_false_fieldRef_blank() {
            ArrayNode conditions = NODE_FACTORY.arrayNode();
            conditions.add(createCondition("step1", "  ", "equals", "APPROVED"));

            boolean result = JsonNodeUtils.evaluateAllConditions(conditions, createSimpleResolver());
            assertFalse(result);
        }

        @Test
        @DisplayName("Should return false when stepRef is null")
        void should_return_false_stepRef_null() {
            ArrayNode conditions = NODE_FACTORY.arrayNode();
            conditions.add(createCondition(null, "status", "equals", "APPROVED"));

            boolean result = JsonNodeUtils.evaluateAllConditions(conditions, createSimpleResolver());
            assertFalse(result);
        }

        @Test
        @DisplayName("Should return false when stepRef is blank")
        void should_return_false_stepRef_blank() {
            ArrayNode conditions = NODE_FACTORY.arrayNode();
            conditions.add(createCondition("  ", "status", "equals", "APPROVED"));

            boolean result = JsonNodeUtils.evaluateAllConditions(conditions, createSimpleResolver());
            assertFalse(result);
        }

        // --- Data Not Found Tests ---

        @Test
        @DisplayName("Should return false when resolver returns null (no data found)")
        void should_return_false_resolver_returns_null() {
            ArrayNode conditions = NODE_FACTORY.arrayNode();
            conditions.add(createCondition("step1", "unknown_field", "equals", "value"));

            boolean result = JsonNodeUtils.evaluateAllConditions(conditions, createSimpleResolver());
            assertFalse(result);
        }

        // --- Resolver Exception Tests ---

        @Test
        @DisplayName("Should return false when resolver throws exception")
        void should_return_false_resolver_throws_exception() {
            ArrayNode conditions = NODE_FACTORY.arrayNode();
            conditions.add(createCondition("step1", "status", "equals", "APPROVED"));

            BiFunction<String, String, String> failingResolver = (f, s) -> {
                throw new RuntimeException("Database connection failed");
            };

            boolean result = JsonNodeUtils.evaluateAllConditions(conditions, failingResolver);
            assertFalse(result);
        }

        // --- Different Value Types Tests ---

        @Test
        @DisplayName("Should handle integer comparison")
        void should_handle_integer_comparison() {
            ArrayNode conditions = NODE_FACTORY.arrayNode();
            conditions.add(createCondition("step1", "amount", "equals", "100"));

            boolean result = JsonNodeUtils.evaluateAllConditions(conditions, createSimpleResolver());
            assertTrue(result);
        }

        @Test
        @DisplayName("Should handle boolean expected value converted to string comparison")
        void should_handle_boolean_expected_value() {
            ArrayNode conditions = NODE_FACTORY.arrayNode();
            ObjectNode condition = createCondition("step1", "status", "equals", null);
            condition.put("value", true);
            conditions.add(condition);

            // When expected is Boolean true, it's converted to Boolean object
            // The resolver returns "true" string, which doesn't equal Boolean.TRUE
            BiFunction<String, String, String> resolver = (f, s) -> "true";
            boolean result = JsonNodeUtils.evaluateAllConditions(conditions, resolver);
            // String "true" != Boolean.TRUE, so this returns false
            assertFalse(result);
        }

        @Test
        @DisplayName("Should handle null expected value")
        void should_handle_null_expected_value() {
            ArrayNode conditions = NODE_FACTORY.arrayNode();
            ObjectNode condition = NODE_FACTORY.objectNode();
            condition.put("stepRef", "step1");
            condition.put("fieldRef", "status");
            condition.put("operator", "equals");
            condition.putNull("value");
            conditions.add(condition);

            BiFunction<String, String, String> resolver = (f, s) -> "someValue";
            boolean result = JsonNodeUtils.evaluateAllConditions(conditions, resolver);
            assertFalse(result); // "someValue" != null
        }

        // --- Basic Functionality Tests ---

        @Test
        @DisplayName("Should work with valid resolver and matching condition")
        void should_work_with_valid_resolver() {
            ArrayNode conditions = NODE_FACTORY.arrayNode();
            conditions.add(createCondition("step1", "status", "equals", "APPROVED"));

            boolean result = JsonNodeUtils.evaluateAllConditions(conditions, createSimpleResolver());
            assertTrue(result);
        }

        // --- Comparison Operators Tests ---

        @Test
        @DisplayName("Should handle greaterthan operator with string comparison")
        void should_handle_greaterthan_operator() {
            ArrayNode conditions = NODE_FACTORY.arrayNode();
            // Note: String comparison is lexicographic, so "100" < "50" because '1' < '5'
            // Use "050" so that "100" > "050" lexicographically
            conditions.add(createCondition("step1", "amount", "greaterthan", "050"));

            boolean result = JsonNodeUtils.evaluateAllConditions(conditions, createSimpleResolver());
            assertTrue(result);
        }

        @Test
        @DisplayName("Should handle lessthan operator")
        void should_handle_lessthan_operator() {
            ArrayNode conditions = NODE_FACTORY.arrayNode();
            conditions.add(createCondition("step1", "amount", "lessthan", "200"));

            boolean result = JsonNodeUtils.evaluateAllConditions(conditions, createSimpleResolver());
            assertTrue(result);
        }

        @Test
        @DisplayName("Should handle greaterorequal operator")
        void should_handle_greaterorequal_operator() {
            ArrayNode conditions = NODE_FACTORY.arrayNode();
            conditions.add(createCondition("step1", "amount", "greaterorequal", "100"));

            boolean result = JsonNodeUtils.evaluateAllConditions(conditions, createSimpleResolver());
            assertTrue(result);
        }

        @Test
        @DisplayName("Should handle lessorequal operator")
        void should_handle_lessorequal_operator() {
            ArrayNode conditions = NODE_FACTORY.arrayNode();
            conditions.add(createCondition("step1", "amount", "lessorequal", "100"));

            boolean result = JsonNodeUtils.evaluateAllConditions(conditions, createSimpleResolver());
            assertTrue(result);
        }

        // --- Symbol Operators Tests ---

        @Test
        @DisplayName("Should handle == operator symbol")
        void should_handle_equals_symbol() {
            ArrayNode conditions = NODE_FACTORY.arrayNode();
            conditions.add(createCondition("step1", "status", "==", "APPROVED"));

            boolean result = JsonNodeUtils.evaluateAllConditions(conditions, createSimpleResolver());
            assertTrue(result);
        }

        @Test
        @DisplayName("Should handle != operator symbol")
        void should_handle_notequals_symbol() {
            ArrayNode conditions = NODE_FACTORY.arrayNode();
            conditions.add(createCondition("step1", "status", "!=", "REJECTED"));

            boolean result = JsonNodeUtils.evaluateAllConditions(conditions, createSimpleResolver());
            assertTrue(result);
        }

        // --- Complex Scenarios Tests ---

        @Test
        @DisplayName("Should handle multiple conditions with different operators")
        void should_handle_multiple_conditions_different_operators() {
            ArrayNode conditions = NODE_FACTORY.arrayNode();
            conditions.add(createCondition("step1", "status", "equals", "APPROVED"));
            // Note: String comparison is lexicographic, "100" > "050" because '1' > '0'
            conditions.add(createCondition("step1", "amount", "greaterthan", "050"));
            conditions.add(createCondition("step1", "name", "contains", "oh"));

            boolean result = JsonNodeUtils.evaluateAllConditions(conditions, createSimpleResolver());
            assertTrue(result);
        }

        @Test
        @DisplayName("Should use correct stepRef when resolving data")
        void should_use_correct_stepRef() {
            ArrayNode conditions = NODE_FACTORY.arrayNode();
            conditions.add(createCondition("specific_step", "status", "equals", "STEP_VALUE"));

            BiFunction<String, String, String> stepAwareResolver = (fieldRef, stepRef) -> {
                if ("specific_step".equals(stepRef) && "status".equals(fieldRef)) {
                    return "STEP_VALUE";
                }
                return null;
            };

            boolean result = JsonNodeUtils.evaluateAllConditions(conditions, stepAwareResolver);
            assertTrue(result);
        }
    }
}
