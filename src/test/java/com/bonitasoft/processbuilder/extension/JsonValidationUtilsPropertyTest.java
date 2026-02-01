package com.bonitasoft.processbuilder.extension;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import org.bonitasoft.engine.connector.ConnectorValidationException;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * Property-based tests for {@link JsonValidationUtils} utility class.
 * Tests invariants that must hold for any valid input.
 *
 * @author Bonitasoft
 * @since 1.0
 */
@Label("JsonValidationUtils Property-Based Tests")
class JsonValidationUtilsPropertyTest {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    // =========================================================================
    // validateField Properties
    // =========================================================================

    @Property(tries = 100)
    @Label("validateField should accept valid textual fields")
    void validateField_shouldAcceptValidTextualFields(
            @ForAll @AlphaChars @StringLength(min = 1, max = 30) String fieldName,
            @ForAll @AlphaChars @StringLength(min = 1, max = 100) String fieldValue) {

        ObjectNode parentNode = OBJECT_MAPPER.createObjectNode();
        parentNode.put(fieldName, fieldValue);

        assertThatCode(() -> JsonValidationUtils.validateField(
                parentNode, fieldName, JsonNode::isTextual, fieldName))
                .doesNotThrowAnyException();
    }

    @Property(tries = 100)
    @Label("validateField should accept valid numeric fields")
    void validateField_shouldAcceptValidNumericFields(
            @ForAll @AlphaChars @StringLength(min = 1, max = 30) String fieldName,
            @ForAll @IntRange(min = -10000, max = 10000) int fieldValue) {

        ObjectNode parentNode = OBJECT_MAPPER.createObjectNode();
        parentNode.put(fieldName, fieldValue);

        assertThatCode(() -> JsonValidationUtils.validateField(
                parentNode, fieldName, JsonNode::isNumber, fieldName))
                .doesNotThrowAnyException();
    }

    @Property(tries = 100)
    @Label("validateField should accept valid boolean fields")
    void validateField_shouldAcceptValidBooleanFields(
            @ForAll @AlphaChars @StringLength(min = 1, max = 30) String fieldName,
            @ForAll boolean fieldValue) {

        ObjectNode parentNode = OBJECT_MAPPER.createObjectNode();
        parentNode.put(fieldName, fieldValue);

        assertThatCode(() -> JsonValidationUtils.validateField(
                parentNode, fieldName, JsonNode::isBoolean, fieldName))
                .doesNotThrowAnyException();
    }

    @Property(tries = 100)
    @Label("validateField should throw for missing field")
    void validateField_shouldThrowForMissingField(
            @ForAll @AlphaChars @StringLength(min = 1, max = 20) String existingField,
            @ForAll @AlphaChars @StringLength(min = 1, max = 20) String missingField) {

        Assume.that(!existingField.equals(missingField));

        ObjectNode parentNode = OBJECT_MAPPER.createObjectNode();
        parentNode.put(existingField, "value");

        assertThatThrownBy(() -> JsonValidationUtils.validateField(
                parentNode, missingField, JsonNode::isTextual, missingField))
                .isInstanceOf(ConnectorValidationException.class)
                .hasMessageContaining(missingField)
                .hasMessageContaining("missing");
    }

    @Property(tries = 100)
    @Label("validateField should throw when type check fails")
    void validateField_shouldThrowWhenTypeCheckFails(
            @ForAll @AlphaChars @StringLength(min = 1, max = 30) String fieldName,
            @ForAll @AlphaChars @StringLength(min = 1, max = 50) String textValue) {

        ObjectNode parentNode = OBJECT_MAPPER.createObjectNode();
        parentNode.put(fieldName, textValue);

        // Field has text value, but we expect number
        assertThatThrownBy(() -> JsonValidationUtils.validateField(
                parentNode, fieldName, JsonNode::isNumber, fieldName))
                .isInstanceOf(ConnectorValidationException.class)
                .hasMessageContaining(fieldName)
                .hasMessageContaining("invalid type");
    }

    @Property(tries = 50)
    @Label("validateField should throw when field is null node")
    void validateField_shouldThrowWhenFieldIsNullNode(
            @ForAll @AlphaChars @StringLength(min = 1, max = 30) String fieldName) {

        ObjectNode parentNode = OBJECT_MAPPER.createObjectNode();
        parentNode.putNull(fieldName);

        // Null node won't pass type check for textual
        assertThatThrownBy(() -> JsonValidationUtils.validateField(
                parentNode, fieldName, JsonNode::isTextual, fieldName))
                .isInstanceOf(ConnectorValidationException.class)
                .hasMessageContaining(fieldName);
    }

    @Property(tries = 50)
    @Label("validateField error message should contain the error message field name")
    void validateField_errorMessageShouldContainFieldName(
            @ForAll @AlphaChars @StringLength(min = 1, max = 20) String fieldName,
            @ForAll @AlphaChars @StringLength(min = 1, max = 20) String errorFieldName) {

        ObjectNode parentNode = OBJECT_MAPPER.createObjectNode();

        assertThatThrownBy(() -> JsonValidationUtils.validateField(
                parentNode, fieldName, JsonNode::isTextual, errorFieldName))
                .isInstanceOf(ConnectorValidationException.class)
                .hasMessageContaining(errorFieldName);
    }

    // =========================================================================
    // validateMemberships Properties
    // =========================================================================

    @Property(tries = 100)
    @Label("validateMemberships should accept valid string arrays")
    void validateMemberships_shouldAcceptValidStringArrays(
            @ForAll @Size(min = 1, max = 10) List<@AlphaChars @StringLength(min = 1, max = 30) String> memberships) {

        ArrayNode memberShipsNode = OBJECT_MAPPER.createArrayNode();
        for (String membership : memberships) {
            memberShipsNode.add(membership);
        }

        assertThatCode(() -> JsonValidationUtils.validateMemberships(memberShipsNode))
                .doesNotThrowAnyException();
    }

    @Property(tries = 50)
    @Label("validateMemberships should accept empty arrays")
    void validateMemberships_shouldAcceptEmptyArrays() {
        ArrayNode memberShipsNode = OBJECT_MAPPER.createArrayNode();

        assertThatCode(() -> JsonValidationUtils.validateMemberships(memberShipsNode))
                .doesNotThrowAnyException();
    }

    @Property(tries = 50)
    @Label("validateMemberships should throw for null node")
    void validateMemberships_shouldThrowForNullNode() {
        assertThatThrownBy(() -> JsonValidationUtils.validateMemberships(null))
                .isInstanceOf(ConnectorValidationException.class)
                .hasMessageContaining("memberShips")
                .hasMessageContaining("array");
    }

    @Property(tries = 50)
    @Label("validateMemberships should throw for non-array node")
    void validateMemberships_shouldThrowForNonArrayNode(
            @ForAll @AlphaChars @StringLength(min = 1, max = 30) String textValue) {

        JsonNode textNode = JsonNodeFactory.instance.textNode(textValue);

        assertThatThrownBy(() -> JsonValidationUtils.validateMemberships(textNode))
                .isInstanceOf(ConnectorValidationException.class)
                .hasMessageContaining("memberShips")
                .hasMessageContaining("array");
    }

    @Property(tries = 100)
    @Label("validateMemberships should throw for arrays containing non-string elements")
    void validateMemberships_shouldThrowForNonStringElements(
            @ForAll @AlphaChars @StringLength(min = 1, max = 20) String validMembership,
            @ForAll @IntRange(min = 0, max = 1000) int numericValue) {

        ArrayNode memberShipsNode = OBJECT_MAPPER.createArrayNode();
        memberShipsNode.add(validMembership);
        memberShipsNode.add(numericValue); // Invalid: numeric

        assertThatThrownBy(() -> JsonValidationUtils.validateMemberships(memberShipsNode))
                .isInstanceOf(ConnectorValidationException.class)
                .hasMessageContaining("string")
                .hasMessageContaining("membership reference");
    }

    @Property(tries = 50)
    @Label("validateMemberships should throw for arrays containing boolean elements")
    void validateMemberships_shouldThrowForBooleanElements(
            @ForAll @AlphaChars @StringLength(min = 1, max = 20) String validMembership,
            @ForAll boolean boolValue) {

        ArrayNode memberShipsNode = OBJECT_MAPPER.createArrayNode();
        memberShipsNode.add(validMembership);
        memberShipsNode.add(boolValue); // Invalid: boolean

        assertThatThrownBy(() -> JsonValidationUtils.validateMemberships(memberShipsNode))
                .isInstanceOf(ConnectorValidationException.class)
                .hasMessageContaining("string");
    }

    @Property(tries = 50)
    @Label("validateMemberships should throw for arrays containing empty strings")
    void validateMemberships_shouldThrowForEmptyStrings(
            @ForAll @AlphaChars @StringLength(min = 1, max = 20) String validMembership) {

        ArrayNode memberShipsNode = OBJECT_MAPPER.createArrayNode();
        memberShipsNode.add(validMembership);
        memberShipsNode.add(""); // Invalid: empty string

        assertThatThrownBy(() -> JsonValidationUtils.validateMemberships(memberShipsNode))
                .isInstanceOf(ConnectorValidationException.class)
                .hasMessageContaining("empty");
    }

    @Property(tries = 50)
    @Label("validateMemberships should throw for arrays containing blank strings")
    void validateMemberships_shouldThrowForBlankStrings(
            @ForAll @AlphaChars @StringLength(min = 1, max = 20) String validMembership,
            @ForAll("blankStrings") String blankValue) {

        ArrayNode memberShipsNode = OBJECT_MAPPER.createArrayNode();
        memberShipsNode.add(validMembership);
        memberShipsNode.add(blankValue); // Invalid: blank string

        assertThatThrownBy(() -> JsonValidationUtils.validateMemberships(memberShipsNode))
                .isInstanceOf(ConnectorValidationException.class)
                .hasMessageContaining("empty");
    }

    @Provide
    Arbitrary<String> blankStrings() {
        return Arbitraries.of("   ", "\t", "\n", "  \t\n  ");
    }

    @Property(tries = 50)
    @Label("validateMemberships should throw for arrays containing object elements")
    void validateMemberships_shouldThrowForObjectElements(
            @ForAll @AlphaChars @StringLength(min = 1, max = 20) String validMembership) {

        ArrayNode memberShipsNode = OBJECT_MAPPER.createArrayNode();
        memberShipsNode.add(validMembership);
        ObjectNode objectNode = OBJECT_MAPPER.createObjectNode();
        objectNode.put("key", "value");
        memberShipsNode.add(objectNode); // Invalid: object

        assertThatThrownBy(() -> JsonValidationUtils.validateMemberships(memberShipsNode))
                .isInstanceOf(ConnectorValidationException.class)
                .hasMessageContaining("string");
    }

    // =========================================================================
    // Consistency Properties
    // =========================================================================

    @Property(tries = 100)
    @Label("validateField should be idempotent for valid fields")
    void validateField_shouldBeIdempotentForValidFields(
            @ForAll @AlphaChars @StringLength(min = 1, max = 30) String fieldName,
            @ForAll @AlphaChars @StringLength(min = 1, max = 50) String fieldValue) {

        ObjectNode parentNode = OBJECT_MAPPER.createObjectNode();
        parentNode.put(fieldName, fieldValue);

        // Multiple calls should all succeed without side effects
        assertThatCode(() -> {
            JsonValidationUtils.validateField(parentNode, fieldName, JsonNode::isTextual, fieldName);
            JsonValidationUtils.validateField(parentNode, fieldName, JsonNode::isTextual, fieldName);
            JsonValidationUtils.validateField(parentNode, fieldName, JsonNode::isTextual, fieldName);
        }).doesNotThrowAnyException();
    }

    @Property(tries = 50)
    @Label("validateMemberships should be idempotent for valid arrays")
    void validateMemberships_shouldBeIdempotentForValidArrays(
            @ForAll @Size(min = 1, max = 5) List<@AlphaChars @StringLength(min = 1, max = 20) String> memberships) {

        ArrayNode memberShipsNode = OBJECT_MAPPER.createArrayNode();
        for (String membership : memberships) {
            memberShipsNode.add(membership);
        }

        // Multiple calls should all succeed without side effects
        assertThatCode(() -> {
            JsonValidationUtils.validateMemberships(memberShipsNode);
            JsonValidationUtils.validateMemberships(memberShipsNode);
            JsonValidationUtils.validateMemberships(memberShipsNode);
        }).doesNotThrowAnyException();
    }

    // =========================================================================
    // Edge Cases
    // =========================================================================

    @Property(tries = 50)
    @Label("validateField should handle special characters in field names")
    void validateField_shouldHandleSpecialFieldNames(
            @ForAll("specialFieldNames") String fieldName) {

        ObjectNode parentNode = OBJECT_MAPPER.createObjectNode();
        parentNode.put(fieldName, "value");

        assertThatCode(() -> JsonValidationUtils.validateField(
                parentNode, fieldName, JsonNode::isTextual, fieldName))
                .doesNotThrowAnyException();
    }

    @Provide
    Arbitrary<String> specialFieldNames() {
        return Arbitraries.of(
                "field_name",
                "field-name",
                "fieldName",
                "field.name",
                "field123",
                "_field",
                "$field"
        );
    }

    @Property(tries = 50)
    @Label("validateMemberships should accept arrays with special membership formats")
    void validateMemberships_shouldAcceptSpecialMembershipFormats(
            @ForAll("membershipFormats") String membership) {

        ArrayNode memberShipsNode = OBJECT_MAPPER.createArrayNode();
        memberShipsNode.add(membership);

        assertThatCode(() -> JsonValidationUtils.validateMemberships(memberShipsNode))
                .doesNotThrowAnyException();
    }

    @Provide
    Arbitrary<String> membershipFormats() {
        return Arbitraries.of(
                "/group/role",
                "group_name/role_name",
                "ADMIN",
                "user.role",
                "group-name/role-name",
                "GROUP123/ROLE456"
        );
    }
}
