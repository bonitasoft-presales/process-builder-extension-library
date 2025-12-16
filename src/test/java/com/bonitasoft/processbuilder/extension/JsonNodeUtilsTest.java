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

    // --- JSON Test Structure ---
    private JsonNode createTestStructure() {
        ObjectNode root = NODE_FACTORY.objectNode();
        ObjectNode recipients = NODE_FACTORY.objectNode();
        recipients.put("type", "step_users");
        recipients.put("stepId", "step_123");
        
        ObjectNode nested = NODE_FACTORY.objectNode();
        nested.put("deepValue", 999);
        recipients.set("nestedObject", nested);

        root.set("recipients", recipients);
        root.put("subject", "Test Subject");
        root.put("count", 10);
        root.putNull("nullField");

        ArrayNode dataArray = NODE_FACTORY.arrayNode();
        dataArray.add("a");
        dataArray.add("b");
        root.set("dataArray", dataArray);

        return root;
    }
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
    // convertJsonNodeToObject Tests (Existing tests are kept)
    // -------------------------------------------------------------------------
    
    // ... (Existing convertJsonNodeToObject Tests) ...

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
    // getValueByPath Tests
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("getValueByPath Tests")
    class GetValueByPathTests {

        private final JsonNode root = createTestStructure();

        @Test
        @DisplayName("Should return null for null root node")
        void should_return_null_for_null_root() {
            assertNull(JsonNodeUtils.getValueByPath((JsonNode) null, "subject"));
        }

        @Test
        @DisplayName("Should return null for null path")
        void should_return_null_for_null_path() {
            assertNull(JsonNodeUtils.getValueByPath(root, null));
        }
        
        @Test
        @DisplayName("Should return null for blank path")
        void should_return_null_for_blank_path() {
            assertNull(JsonNodeUtils.getValueByPath(root, "  "));
        }

        @Test
        @DisplayName("Should return JsonNode for top-level String field")
        void should_return_node_for_top_level_string() {
            JsonNode result = JsonNodeUtils.getValueByPath(root, "subject");
            assertNotNull(result);
            assertTrue(result.isTextual());
            assertEquals("Test Subject", result.asText());
        }

        @Test
        @DisplayName("Should return JsonNode for top-level Number field")
        void should_return_node_for_top_level_number() {
            JsonNode result = JsonNodeUtils.getValueByPath(root, "count");
            assertNotNull(result);
            assertTrue(result.isInt());
            assertEquals(10, result.asInt());
        }

        @Test
        @DisplayName("Should return JsonNode for top-level Array field")
        void should_return_node_for_top_level_array() {
            JsonNode result = JsonNodeUtils.getValueByPath(root, "dataArray");
            assertNotNull(result);
            assertTrue(result.isArray());
        }

        @Test
        @DisplayName("Should return JsonNode for nested field (2 levels)")
        void should_return_node_for_nested_field_2_levels() {
            JsonNode result = JsonNodeUtils.getValueByPath(root, "recipients.type");
            assertNotNull(result);
            assertTrue(result.isTextual());
            assertEquals("step_users", result.asText());
        }

        @Test
        @DisplayName("Should return JsonNode for deep nested field (3 levels)")
        void should_return_node_for_deep_nested_field() {
            JsonNode result = JsonNodeUtils.getValueByPath(root, "recipients.nestedObject.deepValue");
            assertNotNull(result);
            assertTrue(result.isInt());
            assertEquals(999, result.asInt());
        }

        @Test
        @DisplayName("Should return null if field does not exist")
        void should_return_null_if_field_does_not_exist() {
            assertNull(JsonNodeUtils.getValueByPath(root, "nonExistent"));
        }

        @Test
        @DisplayName("Should return null if intermediate node does not exist")
        void should_return_null_if_intermediate_node_does_not_exist() {
            assertNull(JsonNodeUtils.getValueByPath(root, "recipients.invalidField.stepId"));
        }

        @Test
        @DisplayName("Should return null if final field does not exist")
        void should_return_null_if_final_field_does_not_exist() {
            assertNull(JsonNodeUtils.getValueByPath(root, "recipients.invalidField"));
        }

        @Test
        @DisplayName("Should return null if intermediate node is not an object")
        void should_return_null_if_intermediate_node_not_object() {
            // 'subject' is a String, not an object, so the path fails at the second step
            assertNull(JsonNodeUtils.getValueByPath(root, "subject.nextLevel"));
        }

        @Test
        @DisplayName("Should return null if field is JSON null")
        void should_return_null_if_field_is_json_null() {
            JsonNode result = JsonNodeUtils.getValueByPath(root, "nullField");
            assertNull(result); // NullNode is correctly converted to null
        }
        
        @Test
        @DisplayName("Should handle complex object at the end of the path")
        void should_handle_complex_object_at_end() {
            JsonNode result = JsonNodeUtils.getValueByPath(root, "recipients");
            assertNotNull(result);
            assertTrue(result.isObject());
        }
        
        @Test
        @DisplayName("Should handle path ending on an array")
        void should_handle_path_ending_on_array() {
            JsonNode result = JsonNodeUtils.getValueByPath(root, "dataArray");
            assertNotNull(result);
            assertTrue(result.isArray());
        }
    }

    // -------------------------------------------------------------------------
    // convertStringToJsonNode Tests
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("convertStringToJsonNode Tests")
    class ConvertStringToJsonNodeTests {

        @Test
        @DisplayName("Should return null for null input")
        void should_return_null_for_null_input() {
            assertNull(JsonNodeUtils.convertStringToJsonNode(null));
        }

        @Test
        @DisplayName("Should return null for empty string")
        void should_return_null_for_empty_string() {
            assertNull(JsonNodeUtils.convertStringToJsonNode(""));
        }

        @Test
        @DisplayName("Should return null for blank string")
        void should_return_null_for_blank_string() {
            assertNull(JsonNodeUtils.convertStringToJsonNode("   "));
        }

        @Test
        @DisplayName("Should return null for invalid JSON")
        void should_return_null_for_invalid_json() {
            assertNull(JsonNodeUtils.convertStringToJsonNode("not valid json"));
        }

        @Test
        @DisplayName("Should return null for malformed JSON")
        void should_return_null_for_malformed_json() {
            assertNull(JsonNodeUtils.convertStringToJsonNode("{name: missing quotes}"));
        }

        @Test
        @DisplayName("Should parse valid JSON object")
        void should_parse_valid_json_object() {
            String json = "{\"name\": \"John\", \"age\": 30}";
            JsonNode result = JsonNodeUtils.convertStringToJsonNode(json);

            assertNotNull(result);
            assertTrue(result.isObject());
            assertEquals("John", result.get("name").asText());
            assertEquals(30, result.get("age").asInt());
        }

        @Test
        @DisplayName("Should parse valid JSON array")
        void should_parse_valid_json_array() {
            String json = "[\"a\", \"b\", \"c\"]";
            JsonNode result = JsonNodeUtils.convertStringToJsonNode(json);

            assertNotNull(result);
            assertTrue(result.isArray());
            assertEquals(3, result.size());
        }

        @Test
        @DisplayName("Should parse nested JSON structure")
        void should_parse_nested_json_structure() {
            String json = "{\"user\": {\"address\": {\"city\": \"Madrid\"}}}";
            JsonNode result = JsonNodeUtils.convertStringToJsonNode(json);

            assertNotNull(result);
            assertTrue(result.isObject());
            assertEquals("Madrid", result.get("user").get("address").get("city").asText());
        }

        @Test
        @DisplayName("Should parse JSON with different value types")
        void should_parse_json_with_different_value_types() {
            String json = "{\"string\": \"text\", \"number\": 42, \"boolean\": true, \"nullValue\": null}";
            JsonNode result = JsonNodeUtils.convertStringToJsonNode(json);

            assertNotNull(result);
            assertTrue(result.get("string").isTextual());
            assertTrue(result.get("number").isInt());
            assertTrue(result.get("boolean").isBoolean());
            assertTrue(result.get("nullValue").isNull());
        }
    }

    // -------------------------------------------------------------------------
    // getValueByPath (String overload) Tests
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("getValueByPath (String overload) Tests")
    class GetValueByPathFromStringTests {

        private static final String VALID_JSON = "{\"name\": \"John\", \"address\": {\"city\": \"Madrid\", \"zip\": 28001}}";

        @Test
        @DisplayName("Should return null for null JSON string")
        void should_return_null_for_null_json_string() {
            assertNull(JsonNodeUtils.getValueByPath((String) null, "name"));
        }

        @Test
        @DisplayName("Should return null for empty JSON string")
        void should_return_null_for_empty_json_string() {
            assertNull(JsonNodeUtils.getValueByPath("", "name"));
        }

        @Test
        @DisplayName("Should return null for invalid JSON string")
        void should_return_null_for_invalid_json_string() {
            assertNull(JsonNodeUtils.getValueByPath("not valid json", "name"));
        }

        @Test
        @DisplayName("Should return null for null path")
        void should_return_null_for_null_path() {
            assertNull(JsonNodeUtils.getValueByPath(VALID_JSON, null));
        }

        @Test
        @DisplayName("Should return null for blank path")
        void should_return_null_for_blank_path() {
            assertNull(JsonNodeUtils.getValueByPath(VALID_JSON, "  "));
        }

        @Test
        @DisplayName("Should return value for top-level field")
        void should_return_value_for_top_level_field() {
            JsonNode result = JsonNodeUtils.getValueByPath(VALID_JSON, "name");

            assertNotNull(result);
            assertEquals("John", result.asText());
        }

        @Test
        @DisplayName("Should return value for nested field")
        void should_return_value_for_nested_field() {
            JsonNode result = JsonNodeUtils.getValueByPath(VALID_JSON, "address.city");

            assertNotNull(result);
            assertEquals("Madrid", result.asText());
        }

        @Test
        @DisplayName("Should return number value for nested numeric field")
        void should_return_number_for_nested_numeric_field() {
            JsonNode result = JsonNodeUtils.getValueByPath(VALID_JSON, "address.zip");

            assertNotNull(result);
            assertTrue(result.isInt());
            assertEquals(28001, result.asInt());
        }

        @Test
        @DisplayName("Should return null for non-existent field")
        void should_return_null_for_non_existent_field() {
            assertNull(JsonNodeUtils.getValueByPath(VALID_JSON, "nonExistent"));
        }

        @Test
        @DisplayName("Should return null for non-existent nested path")
        void should_return_null_for_non_existent_nested_path() {
            assertNull(JsonNodeUtils.getValueByPath(VALID_JSON, "address.country"));
        }

        @Test
        @DisplayName("Should return object node for object path")
        void should_return_object_node_for_object_path() {
            JsonNode result = JsonNodeUtils.getValueByPath(VALID_JSON, "address");

            assertNotNull(result);
            assertTrue(result.isObject());
            assertTrue(result.has("city"));
            assertTrue(result.has("zip"));
        }
    }

    // -------------------------------------------------------------------------
    // evaluateCondition Tests (Existing tests are kept)
    // -------------------------------------------------------------------------

    // ... (Existing EvaluateCondition Tests) ...

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
    // compareValues Tests (Existing tests are kept)
    // -------------------------------------------------------------------------

    // ... (Existing CompareValues Tests) ...

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
    // evaluateAllConditions Tests (Existing tests are kept)
    // -------------------------------------------------------------------------

    // ... (Existing EvaluateAllConditions Tests) ...

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

    // -------------------------------------------------------------------------
    // getRedirectionName Tests (Existing tests are kept)
    // -------------------------------------------------------------------------

    // ... (Existing GetRedirectionName Tests) ...

    @Nested
    @DisplayName("getRedirectionName Tests")
    class GetRedirectionNameTests {

        @Test
        @DisplayName("Should return Unknown when redirection is null")
        void should_return_unknown_when_null() {
            String result = JsonNodeUtils.getRedirectionName(null);
            assertEquals(JsonNodeUtils.DEFAULT_REDIRECTION_NAME, result);
        }

        @Test
        @DisplayName("Should return name from new structure (parameters.name)")
        void should_return_name_from_new_structure() {
            ObjectNode redirection = NODE_FACTORY.objectNode();
            ObjectNode parameters = NODE_FACTORY.objectNode();
            parameters.put("name", "NewRedirection");
            redirection.set("parameters", parameters);

            String result = JsonNodeUtils.getRedirectionName(redirection);
            assertEquals("NewRedirection", result);
        }

        @Test
        @DisplayName("Should return name from old structure (name directly)")
        void should_return_name_from_old_structure() {
            ObjectNode redirection = NODE_FACTORY.objectNode();
            redirection.put("name", "OldRedirection");

            String result = JsonNodeUtils.getRedirectionName(redirection);
            assertEquals("OldRedirection", result);
        }

        @Test
        @DisplayName("Should prioritize new structure over old structure")
        void should_prioritize_new_structure() {
            ObjectNode redirection = NODE_FACTORY.objectNode();
            ObjectNode parameters = NODE_FACTORY.objectNode();
            parameters.put("name", "NewName");
            redirection.set("parameters", parameters);
            redirection.put("name", "OldName");

            String result = JsonNodeUtils.getRedirectionName(redirection);
            assertEquals("NewName", result);
        }

        @Test
        @DisplayName("Should return Unknown when no name found")
        void should_return_unknown_when_no_name() {
            ObjectNode redirection = NODE_FACTORY.objectNode();
            redirection.put("otherField", "value");

            String result = JsonNodeUtils.getRedirectionName(redirection);
            assertEquals(JsonNodeUtils.DEFAULT_REDIRECTION_NAME, result);
        }

        @Test
        @DisplayName("Should return Unknown when parameters exists but has no name")
        void should_return_unknown_when_parameters_without_name() {
            ObjectNode redirection = NODE_FACTORY.objectNode();
            ObjectNode parameters = NODE_FACTORY.objectNode();
            parameters.put("otherField", "value");
            redirection.set("parameters", parameters);

            String result = JsonNodeUtils.getRedirectionName(redirection);
            assertEquals(JsonNodeUtils.DEFAULT_REDIRECTION_NAME, result);
        }

        @Test
        @DisplayName("Should handle empty name in new structure")
        void should_handle_empty_name_new_structure() {
            ObjectNode redirection = NODE_FACTORY.objectNode();
            ObjectNode parameters = NODE_FACTORY.objectNode();
            parameters.put("name", "");
            redirection.set("parameters", parameters);

            String result = JsonNodeUtils.getRedirectionName(redirection);
            assertEquals("", result);
        }

        @Test
        @DisplayName("Should handle empty name in old structure")
        void should_handle_empty_name_old_structure() {
            ObjectNode redirection = NODE_FACTORY.objectNode();
            redirection.put("name", "");

            String result = JsonNodeUtils.getRedirectionName(redirection);
            assertEquals("", result);
        }
    }

    // -------------------------------------------------------------------------
    // getTargetStep Tests (Existing tests are kept)
    // -------------------------------------------------------------------------

    // ... (Existing GetTargetStep Tests) ...

    @Nested
    @DisplayName("getTargetStep Tests")
    class GetTargetStepTests {

        @Test
        @DisplayName("Should return null when redirection is null")
        void should_return_null_when_null() {
            String result = JsonNodeUtils.getTargetStep(null);
            assertNull(result);
        }

        @Test
        @DisplayName("Should return targetStep from new structure (parameters.targetStep)")
        void should_return_targetStep_from_new_structure() {
            ObjectNode redirection = NODE_FACTORY.objectNode();
            ObjectNode parameters = NODE_FACTORY.objectNode();
            parameters.put("targetStep", "Step_Review");
            redirection.set("parameters", parameters);

            String result = JsonNodeUtils.getTargetStep(redirection);
            assertEquals("Step_Review", result);
        }

        @Test
        @DisplayName("Should return targetStep from old structure (targetStep directly)")
        void should_return_targetStep_from_old_structure() {
            ObjectNode redirection = NODE_FACTORY.objectNode();
            redirection.put("targetStep", "Step_Approval");

            String result = JsonNodeUtils.getTargetStep(redirection);
            assertEquals("Step_Approval", result);
        }

        @Test
        @DisplayName("Should prioritize new structure over old structure")
        void should_prioritize_new_structure() {
            ObjectNode redirection = NODE_FACTORY.objectNode();
            ObjectNode parameters = NODE_FACTORY.objectNode();
            parameters.put("targetStep", "NewStep");
            redirection.set("parameters", parameters);
            redirection.put("targetStep", "OldStep");

            String result = JsonNodeUtils.getTargetStep(redirection);
            assertEquals("NewStep", result);
        }

        @Test
        @DisplayName("Should return null when no targetStep found")
        void should_return_null_when_no_targetStep() {
            ObjectNode redirection = NODE_FACTORY.objectNode();
            redirection.put("otherField", "value");

            String result = JsonNodeUtils.getTargetStep(redirection);
            assertNull(result);
        }

        @Test
        @DisplayName("Should return null when parameters exists but has no targetStep")
        void should_return_null_when_parameters_without_targetStep() {
            ObjectNode redirection = NODE_FACTORY.objectNode();
            ObjectNode parameters = NODE_FACTORY.objectNode();
            parameters.put("otherField", "value");
            redirection.set("parameters", parameters);

            String result = JsonNodeUtils.getTargetStep(redirection);
            assertNull(result);
        }

        @Test
        @DisplayName("Should handle empty targetStep in new structure")
        void should_handle_empty_targetStep_new_structure() {
            ObjectNode redirection = NODE_FACTORY.objectNode();
            ObjectNode parameters = NODE_FACTORY.objectNode();
            parameters.put("targetStep", "");
            redirection.set("parameters", parameters);

            String result = JsonNodeUtils.getTargetStep(redirection);
            assertEquals("", result);
        }

        @Test
        @DisplayName("Should handle empty targetStep in old structure")
        void should_handle_empty_targetStep_old_structure() {
            ObjectNode redirection = NODE_FACTORY.objectNode();
            redirection.put("targetStep", "");

            String result = JsonNodeUtils.getTargetStep(redirection);
            assertEquals("", result);
        }
    }
}