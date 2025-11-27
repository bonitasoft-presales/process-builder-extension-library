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
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonNodeUtilsTest.class);
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
            boolean result = JsonNodeUtils.evaluateCondition("value", null, "expected", LOGGER);
            assertFalse(result);
        }

        @Test
        @DisplayName("Should return false for null operator with null logger")
        void should_return_false_for_null_operator_with_null_logger() {
            boolean result = JsonNodeUtils.evaluateCondition("value", null, "expected", null);
            assertFalse(result);
        }

        @Test
        @DisplayName("Should return false for unknown operator")
        void should_return_false_for_unknown_operator() {
            boolean result = JsonNodeUtils.evaluateCondition("value", "unknown", "expected", LOGGER);
            assertFalse(result);
        }

        @Test
        @DisplayName("Should return false for unknown operator with null logger")
        void should_return_false_for_unknown_operator_with_null_logger() {
            boolean result = JsonNodeUtils.evaluateCondition("value", "unknown", "expected", null);
            assertFalse(result);
        }

        // Equals tests
        @Nested
        @DisplayName("Equals Operator Tests")
        class EqualsOperatorTests {

            @Test
            @DisplayName("equals should return true for equal strings")
            void equals_should_return_true_for_equal_strings() {
                assertTrue(JsonNodeUtils.evaluateCondition("test", "equals", "test", LOGGER));
            }

            @Test
            @DisplayName("== should return true for equal strings")
            void equals_symbol_should_return_true_for_equal_strings() {
                assertTrue(JsonNodeUtils.evaluateCondition("test", "==", "test", LOGGER));
            }

            @Test
            @DisplayName("equals should return false for different strings")
            void equals_should_return_false_for_different_strings() {
                assertFalse(JsonNodeUtils.evaluateCondition("test", "equals", "other", LOGGER));
            }

            @Test
            @DisplayName("equals should return true for both null values")
            void equals_should_return_true_for_both_null() {
                assertTrue(JsonNodeUtils.evaluateCondition(null, "equals", null, LOGGER));
            }

            @Test
            @DisplayName("equals should return false when only current is null")
            void equals_should_return_false_when_current_is_null() {
                assertFalse(JsonNodeUtils.evaluateCondition(null, "equals", "test", LOGGER));
            }

            @Test
            @DisplayName("equals should return false when only expected is null")
            void equals_should_return_false_when_expected_is_null() {
                assertFalse(JsonNodeUtils.evaluateCondition("test", "equals", null, LOGGER));
            }

            @Test
            @DisplayName("EQUALS (uppercase) should work case-insensitively")
            void equals_should_work_case_insensitively() {
                assertTrue(JsonNodeUtils.evaluateCondition("test", "EQUALS", "test", LOGGER));
            }
        }

        // Not Equals tests
        @Nested
        @DisplayName("Not Equals Operator Tests")
        class NotEqualsOperatorTests {

            @Test
            @DisplayName("notequals should return true for different strings")
            void notequals_should_return_true_for_different_strings() {
                assertTrue(JsonNodeUtils.evaluateCondition("test", "notequals", "other", LOGGER));
            }

            @Test
            @DisplayName("!= should return true for different strings")
            void notequals_symbol_should_return_true_for_different_strings() {
                assertTrue(JsonNodeUtils.evaluateCondition("test", "!=", "other", LOGGER));
            }

            @Test
            @DisplayName("notequals should return false for equal strings")
            void notequals_should_return_false_for_equal_strings() {
                assertFalse(JsonNodeUtils.evaluateCondition("test", "notequals", "test", LOGGER));
            }

            @Test
            @DisplayName("notequals should return false for both null values")
            void notequals_should_return_false_for_both_null() {
                assertFalse(JsonNodeUtils.evaluateCondition(null, "notequals", null, LOGGER));
            }

            @Test
            @DisplayName("notequals should return true when only current is null")
            void notequals_should_return_true_when_current_is_null() {
                assertTrue(JsonNodeUtils.evaluateCondition(null, "notequals", "test", LOGGER));
            }
        }

        // Contains tests
        @Nested
        @DisplayName("Contains Operator Tests")
        class ContainsOperatorTests {

            @Test
            @DisplayName("contains should return true when substring exists")
            void contains_should_return_true_when_substring_exists() {
                assertTrue(JsonNodeUtils.evaluateCondition("hello world", "contains", "world", LOGGER));
            }

            @Test
            @DisplayName("contains should return false when substring not found")
            void contains_should_return_false_when_substring_not_found() {
                assertFalse(JsonNodeUtils.evaluateCondition("hello world", "contains", "xyz", LOGGER));
            }

            @Test
            @DisplayName("contains should return false when current value is null")
            void contains_should_return_false_when_current_is_null() {
                assertFalse(JsonNodeUtils.evaluateCondition(null, "contains", "test", LOGGER));
            }

            @Test
            @DisplayName("contains should return true when expected is null (empty string)")
            void contains_should_return_true_when_expected_is_null() {
                assertTrue(JsonNodeUtils.evaluateCondition("test", "contains", null, LOGGER));
            }

            @Test
            @DisplayName("contains should work with numbers converted to strings")
            void contains_should_work_with_numbers() {
                assertTrue(JsonNodeUtils.evaluateCondition(12345, "contains", "234", LOGGER));
            }
        }

        // Greater Than tests
        @Nested
        @DisplayName("Greater Than Operator Tests")
        class GreaterThanOperatorTests {

            @Test
            @DisplayName("greaterthan should return true when current > expected")
            void greaterthan_should_return_true_when_current_greater() {
                assertTrue(JsonNodeUtils.evaluateCondition(10, "greaterthan", 5, LOGGER));
            }

            @Test
            @DisplayName("> should return true when current > expected")
            void greaterthan_symbol_should_return_true_when_current_greater() {
                assertTrue(JsonNodeUtils.evaluateCondition(10, ">", 5, LOGGER));
            }

            @Test
            @DisplayName("greaterthan should return false when current < expected")
            void greaterthan_should_return_false_when_current_less() {
                assertFalse(JsonNodeUtils.evaluateCondition(5, "greaterthan", 10, LOGGER));
            }

            @Test
            @DisplayName("greaterthan should return false when current == expected")
            void greaterthan_should_return_false_when_equal() {
                assertFalse(JsonNodeUtils.evaluateCondition(5, "greaterthan", 5, LOGGER));
            }

            @Test
            @DisplayName("greaterthan should return false for non-comparable values")
            void greaterthan_should_return_false_for_non_comparable() {
                assertFalse(JsonNodeUtils.evaluateCondition(new Object(), "greaterthan", new Object(), LOGGER));
            }
        }

        // Less Than tests
        @Nested
        @DisplayName("Less Than Operator Tests")
        class LessThanOperatorTests {

            @Test
            @DisplayName("lessthan should return true when current < expected")
            void lessthan_should_return_true_when_current_less() {
                assertTrue(JsonNodeUtils.evaluateCondition(5, "lessthan", 10, LOGGER));
            }

            @Test
            @DisplayName("< should return true when current < expected")
            void lessthan_symbol_should_return_true_when_current_less() {
                assertTrue(JsonNodeUtils.evaluateCondition(5, "<", 10, LOGGER));
            }

            @Test
            @DisplayName("lessthan should return false when current > expected")
            void lessthan_should_return_false_when_current_greater() {
                assertFalse(JsonNodeUtils.evaluateCondition(10, "lessthan", 5, LOGGER));
            }

            @Test
            @DisplayName("lessthan should return false when current == expected")
            void lessthan_should_return_false_when_equal() {
                assertFalse(JsonNodeUtils.evaluateCondition(5, "lessthan", 5, LOGGER));
            }

            @Test
            @DisplayName("lessthan should return false for non-comparable values")
            void lessthan_should_return_false_for_non_comparable() {
                assertFalse(JsonNodeUtils.evaluateCondition(new Object(), "lessthan", new Object(), LOGGER));
            }
        }

        // Greater Or Equal tests
        @Nested
        @DisplayName("Greater Or Equal Operator Tests")
        class GreaterOrEqualOperatorTests {

            @Test
            @DisplayName("greaterorequal should return true when current > expected")
            void greaterorequal_should_return_true_when_current_greater() {
                assertTrue(JsonNodeUtils.evaluateCondition(10, "greaterorequal", 5, LOGGER));
            }

            @Test
            @DisplayName(">= should return true when current >= expected")
            void greaterorequal_symbol_should_return_true_when_current_greater_or_equal() {
                assertTrue(JsonNodeUtils.evaluateCondition(10, ">=", 5, LOGGER));
            }

            @Test
            @DisplayName("greaterorequal should return true when current == expected")
            void greaterorequal_should_return_true_when_equal() {
                assertTrue(JsonNodeUtils.evaluateCondition(5, "greaterorequal", 5, LOGGER));
            }

            @Test
            @DisplayName("greaterorequal should return false when current < expected")
            void greaterorequal_should_return_false_when_current_less() {
                assertFalse(JsonNodeUtils.evaluateCondition(3, "greaterorequal", 5, LOGGER));
            }

            @Test
            @DisplayName("greaterorequal should return false for non-comparable values")
            void greaterorequal_should_return_false_for_non_comparable() {
                assertFalse(JsonNodeUtils.evaluateCondition(new Object(), ">=", new Object(), LOGGER));
            }
        }

        // Less Or Equal tests
        @Nested
        @DisplayName("Less Or Equal Operator Tests")
        class LessOrEqualOperatorTests {

            @Test
            @DisplayName("lessorequal should return true when current < expected")
            void lessorequal_should_return_true_when_current_less() {
                assertTrue(JsonNodeUtils.evaluateCondition(5, "lessorequal", 10, LOGGER));
            }

            @Test
            @DisplayName("<= should return true when current <= expected")
            void lessorequal_symbol_should_return_true_when_current_less_or_equal() {
                assertTrue(JsonNodeUtils.evaluateCondition(5, "<=", 10, LOGGER));
            }

            @Test
            @DisplayName("lessorequal should return true when current == expected")
            void lessorequal_should_return_true_when_equal() {
                assertTrue(JsonNodeUtils.evaluateCondition(5, "lessorequal", 5, LOGGER));
            }

            @Test
            @DisplayName("lessorequal should return false when current > expected")
            void lessorequal_should_return_false_when_current_greater() {
                assertFalse(JsonNodeUtils.evaluateCondition(10, "lessorequal", 5, LOGGER));
            }

            @Test
            @DisplayName("lessorequal should return false for non-comparable values")
            void lessorequal_should_return_false_for_non_comparable() {
                assertFalse(JsonNodeUtils.evaluateCondition(new Object(), "<=", new Object(), LOGGER));
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
            assertThatThrownBy(() -> JsonNodeUtils.compareValues(null, "expected", LOGGER))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Actual value cannot be null");
        }

        @Test
        @DisplayName("Should throw NullPointerException when expected is null")
        void should_throw_when_expected_is_null() {
            assertThatThrownBy(() -> JsonNodeUtils.compareValues("actual", null, LOGGER))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Expected value cannot be null");
        }

        @Test
        @DisplayName("Should compare Integer and Double as numbers")
        void should_compare_integer_and_double_as_numbers() {
            int result = JsonNodeUtils.compareValues(10, 5.0, LOGGER);
            assertThat(result).isGreaterThan(0);
        }

        @Test
        @DisplayName("Should compare Long and Integer as numbers")
        void should_compare_long_and_integer_as_numbers() {
            int result = JsonNodeUtils.compareValues(5L, 10, LOGGER);
            assertThat(result).isLessThan(0);
        }

        @Test
        @DisplayName("Should compare Float and Double as numbers")
        void should_compare_float_and_double_as_numbers() {
            int result = JsonNodeUtils.compareValues(5.0f, 5.0, LOGGER);
            assertThat(result).isEqualTo(0);
        }

        @Test
        @DisplayName("Should compare same type directly - strings")
        void should_compare_same_type_strings() {
            int result = JsonNodeUtils.compareValues("apple", "banana", LOGGER);
            assertThat(result).isLessThan(0);
        }

        @Test
        @DisplayName("Should compare same type directly - integers")
        void should_compare_same_type_integers() {
            int result = JsonNodeUtils.compareValues(10, 5, LOGGER);
            assertThat(result).isGreaterThan(0);
        }

        @Test
        @DisplayName("Should compare same type directly - equal values")
        void should_compare_same_type_equal_values() {
            int result = JsonNodeUtils.compareValues("test", "test", LOGGER);
            assertThat(result).isEqualTo(0);
        }

        @Test
        @DisplayName("Should compare different types as strings")
        void should_compare_different_types_as_strings() {
            // String "5" vs Boolean true - should compare as strings
            int result = JsonNodeUtils.compareValues("5", true, LOGGER);
            // "5" vs "true" - string comparison
            assertThat(result).isNotEqualTo(0);
        }

        @Test
        @DisplayName("Should compare with null logger (uses class logger)")
        void should_compare_with_null_logger() {
            int result = JsonNodeUtils.compareValues(10, 5, null);
            assertThat(result).isGreaterThan(0);
        }

        @Test
        @DisplayName("Should handle Double comparison edge cases")
        void should_handle_double_comparison_edge_cases() {
            // Very close double values
            int result = JsonNodeUtils.compareValues(1.0000001, 1.0000002, LOGGER);
            assertThat(result).isLessThan(0);
        }

        @Test
        @DisplayName("Should compare negative numbers correctly")
        void should_compare_negative_numbers() {
            int result = JsonNodeUtils.compareValues(-10, -5, LOGGER);
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

            boolean result = JsonNodeUtils.evaluateCondition(currentValue, ">", expectedValue, LOGGER);
            assertTrue(result);
        }

        @Test
        @DisplayName("Should handle string comparison from JsonNode")
        void should_handle_string_comparison_from_jsonnode() {
            JsonNode currentNode = NODE_FACTORY.textNode("hello world");
            JsonNode expectedNode = NODE_FACTORY.textNode("world");

            Object currentValue = JsonNodeUtils.convertJsonNodeToObject(currentNode);
            Object expectedValue = JsonNodeUtils.convertJsonNodeToObject(expectedNode);

            boolean result = JsonNodeUtils.evaluateCondition(currentValue, "contains", expectedValue, LOGGER);
            assertTrue(result);
        }

        @Test
        @DisplayName("Should handle boolean equality from JsonNode")
        void should_handle_boolean_equality_from_jsonnode() {
            JsonNode currentNode = NODE_FACTORY.booleanNode(true);
            JsonNode expectedNode = NODE_FACTORY.booleanNode(true);

            Object currentValue = JsonNodeUtils.convertJsonNodeToObject(currentNode);
            Object expectedValue = JsonNodeUtils.convertJsonNodeToObject(expectedNode);

            boolean result = JsonNodeUtils.evaluateCondition(currentValue, "==", expectedValue, LOGGER);
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
            boolean result = JsonNodeUtils.evaluateCondition(current, operator, expected, LOGGER);
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
            boolean result = JsonNodeUtils.evaluateCondition(current, operator, expected, LOGGER);
            assertEquals(expectedResult, result);
        }
    }
}
