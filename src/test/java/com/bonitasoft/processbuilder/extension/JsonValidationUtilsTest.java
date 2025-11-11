package com.bonitasoft.processbuilder.extension;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.bonitasoft.engine.connector.ConnectorValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link JsonValidationUtils} utility class.
 * <p>
 * This class ensures that all JSON structure validation methods work correctly
 * across various scenarios, including field validation and membership array validation.
 * </p>
 */
class JsonValidationUtilsTest {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    // -------------------------------------------------------------------------
    // Utility Class Instantiation Test
    // -------------------------------------------------------------------------

    /**
     * Tests that the utility class has a private constructor and cannot be instantiated.
     */
    @Test
    @DisplayName("Should have private constructor to prevent instantiation")
    void shouldHavePrivateConstructor() throws Exception {
        // Given the JsonValidationUtils class
        // When attempting to get the constructor
        Constructor<JsonValidationUtils> constructor = JsonValidationUtils.class.getDeclaredConstructor();

        // Then the constructor should be private
        assertTrue(Modifier.isPrivate(constructor.getModifiers()),
            "Constructor must be private to enforce utility class pattern.");
    }

    // -------------------------------------------------------------------------
    // validateField Tests
    // -------------------------------------------------------------------------

    /**
     * Tests that validateField accepts a valid textual field.
     */
    @Test
    @DisplayName("validateField should accept valid textual field")
    void validateField_should_accept_valid_textual_field() {
        // Given a JSON object with a valid text field
        ObjectNode parentNode = OBJECT_MAPPER.createObjectNode();
        parentNode.put("name", "Test Name");

        // When validating the field
        // Then no exception should be thrown
        assertDoesNotThrow(() -> JsonValidationUtils.validateField(
            parentNode, "name", JsonNode::isTextual, "name"));
    }

    /**
     * Tests that validateField throws exception for missing field.
     */
    @Test
    @DisplayName("validateField should throw exception for missing field")
    void validateField_should_throw_exception_for_missing_field() {
        // Given a JSON object without the required field
        ObjectNode parentNode = OBJECT_MAPPER.createObjectNode();
        parentNode.put("other", "value");

        // When validating a missing field
        // Then ConnectorValidationException should be thrown
        ConnectorValidationException exception = assertThrows(ConnectorValidationException.class,
            () -> JsonValidationUtils.validateField(
                parentNode, "name", JsonNode::isTextual, "name"));

        // Verify the exception message
        assertTrue(exception.getMessage().contains("name"));
        assertTrue(exception.getMessage().contains("missing"));
    }

    /**
     * Tests that validateField throws exception for field with invalid type.
     */
    @Test
    @DisplayName("validateField should throw exception for invalid field type")
    void validateField_should_throw_exception_for_invalid_type() {
        // Given a JSON object with a field of wrong type (number instead of text)
        ObjectNode parentNode = OBJECT_MAPPER.createObjectNode();
        parentNode.put("name", 12345);

        // When validating the field expecting text
        // Then ConnectorValidationException should be thrown
        ConnectorValidationException exception = assertThrows(ConnectorValidationException.class,
            () -> JsonValidationUtils.validateField(
                parentNode, "name", JsonNode::isTextual, "name"));

        // Verify the exception message
        assertTrue(exception.getMessage().contains("name"));
        assertTrue(exception.getMessage().contains("invalid type"));
    }

    /**
     * Tests that validateField accepts a valid numeric field.
     */
    @Test
    @DisplayName("validateField should accept valid numeric field")
    void validateField_should_accept_valid_numeric_field() {
        // Given a JSON object with a valid numeric field
        ObjectNode parentNode = OBJECT_MAPPER.createObjectNode();
        parentNode.put("count", 42);

        // When validating the field as numeric
        // Then no exception should be thrown
        assertDoesNotThrow(() -> JsonValidationUtils.validateField(
            parentNode, "count", JsonNode::isNumber, "count"));
    }

    /**
     * Tests that validateField accepts a valid boolean field.
     */
    @Test
    @DisplayName("validateField should accept valid boolean field")
    void validateField_should_accept_valid_boolean_field() {
        // Given a JSON object with a valid boolean field
        ObjectNode parentNode = OBJECT_MAPPER.createObjectNode();
        parentNode.put("active", true);

        // When validating the field as boolean
        // Then no exception should be thrown
        assertDoesNotThrow(() -> JsonValidationUtils.validateField(
            parentNode, "active", JsonNode::isBoolean, "active"));
    }

    /**
     * Tests that validateField accepts a valid array field.
     */
    @Test
    @DisplayName("validateField should accept valid array field")
    void validateField_should_accept_valid_array_field() {
        // Given a JSON object with a valid array field
        ObjectNode parentNode = OBJECT_MAPPER.createObjectNode();
        ArrayNode arrayNode = OBJECT_MAPPER.createArrayNode();
        arrayNode.add("item1").add("item2");
        parentNode.set("items", arrayNode);

        // When validating the field as array
        // Then no exception should be thrown
        assertDoesNotThrow(() -> JsonValidationUtils.validateField(
            parentNode, "items", JsonNode::isArray, "items"));
    }

    /**
     * Tests that validateField throws exception when field is null.
     */
    @Test
    @DisplayName("validateField should throw exception when field is null")
    void validateField_should_throw_exception_when_field_is_null() {
        // Given a JSON object with a null field value
        ObjectNode parentNode = OBJECT_MAPPER.createObjectNode();
        parentNode.set("name", null);

        // When validating the field
        // Then ConnectorValidationException should be thrown
        ConnectorValidationException exception = assertThrows(ConnectorValidationException.class,
            () -> JsonValidationUtils.validateField(
                parentNode, "name", JsonNode::isTextual, "name"));

        // Verify the exception message
        assertTrue(exception.getMessage().contains("name"));
    }

    // -------------------------------------------------------------------------
    // validateMemberships Tests
    // -------------------------------------------------------------------------

    /**
     * Tests that validateMemberships accepts a valid array of membership strings.
     */
    @Test
    @DisplayName("validateMemberships should accept valid membership array")
    void validateMemberships_should_accept_valid_membership_array() {
        // Given a valid array of membership strings
        ArrayNode memberShipsNode = OBJECT_MAPPER.createArrayNode();
        memberShipsNode.add("/group1/role1");
        memberShipsNode.add("/group2/role2");

        // When validating memberships
        // Then no exception should be thrown
        assertDoesNotThrow(() -> JsonValidationUtils.validateMemberships(memberShipsNode));
    }

    /**
     * Tests that validateMemberships throws exception for null node.
     */
    @Test
    @DisplayName("validateMemberships should throw exception for null node")
    void validateMemberships_should_throw_exception_for_null() {
        // Given a null node
        JsonNode memberShipsNode = null;

        // When validating memberships
        // Then ConnectorValidationException should be thrown
        ConnectorValidationException exception = assertThrows(ConnectorValidationException.class,
            () -> JsonValidationUtils.validateMemberships(memberShipsNode));

        // Verify the exception message
        assertTrue(exception.getMessage().contains("memberShips"));
        assertTrue(exception.getMessage().contains("array"));
    }

    /**
     * Tests that validateMemberships throws exception for non-array node.
     */
    @Test
    @DisplayName("validateMemberships should throw exception for non-array node")
    void validateMemberships_should_throw_exception_for_non_array() {
        // Given a non-array node (text node)
        JsonNode memberShipsNode = OBJECT_MAPPER.getNodeFactory().textNode("not an array");

        // When validating memberships
        // Then ConnectorValidationException should be thrown
        ConnectorValidationException exception = assertThrows(ConnectorValidationException.class,
            () -> JsonValidationUtils.validateMemberships(memberShipsNode));

        // Verify the exception message
        assertTrue(exception.getMessage().contains("memberShips"));
        assertTrue(exception.getMessage().contains("array"));
    }

    /**
     * Tests that validateMemberships throws exception for array with non-string elements.
     */
    @Test
    @DisplayName("validateMemberships should throw exception for array with non-string elements")
    void validateMemberships_should_throw_exception_for_non_string_elements() {
        // Given an array with a numeric element
        ArrayNode memberShipsNode = OBJECT_MAPPER.createArrayNode();
        memberShipsNode.add("/group1/role1");
        memberShipsNode.add(12345);  // Invalid: numeric element

        // When validating memberships
        // Then ConnectorValidationException should be thrown
        ConnectorValidationException exception = assertThrows(ConnectorValidationException.class,
            () -> JsonValidationUtils.validateMemberships(memberShipsNode));

        // Verify the exception message
        assertTrue(exception.getMessage().contains("string"));
        assertTrue(exception.getMessage().contains("membership reference"));
    }

    /**
     * Tests that validateMemberships throws exception for array with empty string elements.
     */
    @Test
    @DisplayName("validateMemberships should throw exception for array with empty strings")
    void validateMemberships_should_throw_exception_for_empty_strings() {
        // Given an array with an empty string element
        ArrayNode memberShipsNode = OBJECT_MAPPER.createArrayNode();
        memberShipsNode.add("/group1/role1");
        memberShipsNode.add("");  // Invalid: empty string

        // When validating memberships
        // Then ConnectorValidationException should be thrown
        ConnectorValidationException exception = assertThrows(ConnectorValidationException.class,
            () -> JsonValidationUtils.validateMemberships(memberShipsNode));

        // Verify the exception message
        assertTrue(exception.getMessage().contains("empty"));
    }

    /**
     * Tests that validateMemberships throws exception for array with blank string elements.
     */
    @Test
    @DisplayName("validateMemberships should throw exception for array with blank strings")
    void validateMemberships_should_throw_exception_for_blank_strings() {
        // Given an array with a blank string element (only spaces)
        ArrayNode memberShipsNode = OBJECT_MAPPER.createArrayNode();
        memberShipsNode.add("/group1/role1");
        memberShipsNode.add("   ");  // Invalid: blank string

        // When validating memberships
        // Then ConnectorValidationException should be thrown
        ConnectorValidationException exception = assertThrows(ConnectorValidationException.class,
            () -> JsonValidationUtils.validateMemberships(memberShipsNode));

        // Verify the exception message
        assertTrue(exception.getMessage().contains("empty"));
    }

    /**
     * Tests that validateMemberships accepts an empty array.
     */
    @Test
    @DisplayName("validateMemberships should accept empty array")
    void validateMemberships_should_accept_empty_array() {
        // Given an empty array
        ArrayNode memberShipsNode = OBJECT_MAPPER.createArrayNode();

        // When validating memberships
        // Then no exception should be thrown (empty array is valid)
        assertDoesNotThrow(() -> JsonValidationUtils.validateMemberships(memberShipsNode));
    }

    /**
     * Tests that validateMemberships accepts array with single membership.
     */
    @Test
    @DisplayName("validateMemberships should accept array with single membership")
    void validateMemberships_should_accept_single_membership() {
        // Given an array with a single membership
        ArrayNode memberShipsNode = OBJECT_MAPPER.createArrayNode();
        memberShipsNode.add("/group/role");

        // When validating memberships
        // Then no exception should be thrown
        assertDoesNotThrow(() -> JsonValidationUtils.validateMemberships(memberShipsNode));
    }

    /**
     * Tests that validateMemberships throws exception for array with object elements.
     */
    @Test
    @DisplayName("validateMemberships should throw exception for array with object elements")
    void validateMemberships_should_throw_exception_for_object_elements() {
        // Given an array with an object element
        ArrayNode memberShipsNode = OBJECT_MAPPER.createArrayNode();
        memberShipsNode.add("/group1/role1");
        ObjectNode objectNode = OBJECT_MAPPER.createObjectNode();
        objectNode.put("group", "test");
        memberShipsNode.add(objectNode);  // Invalid: object element

        // When validating memberships
        // Then ConnectorValidationException should be thrown
        ConnectorValidationException exception = assertThrows(ConnectorValidationException.class,
            () -> JsonValidationUtils.validateMemberships(memberShipsNode));

        // Verify the exception message
        assertTrue(exception.getMessage().contains("string"));
    }

    /**
     * Tests that validateMemberships throws exception for array with boolean elements.
     */
    @Test
    @DisplayName("validateMemberships should throw exception for array with boolean elements")
    void validateMemberships_should_throw_exception_for_boolean_elements() {
        // Given an array with a boolean element
        ArrayNode memberShipsNode = OBJECT_MAPPER.createArrayNode();
        memberShipsNode.add("/group1/role1");
        memberShipsNode.add(true);  // Invalid: boolean element

        // When validating memberships
        // Then ConnectorValidationException should be thrown
        ConnectorValidationException exception = assertThrows(ConnectorValidationException.class,
            () -> JsonValidationUtils.validateMemberships(memberShipsNode));

        // Verify the exception message
        assertTrue(exception.getMessage().contains("string"));
    }
}
