package com.bonitasoft.processbuilder.enums;

import com.bonitasoft.processbuilder.validation.JsonSchemaValidator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.MockedStatic;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Map;

/**
 * Unit tests for the {@link ObjectsManagementOptionType} enumeration.
 * <p>
 * This class validates enum constants, the {@code isValid} logic, and ensures
 * proper delegation to the mocked {@code JsonSchemaValidator} for schema validation.
 * </p>
 */
class ObjectsManagementOptionTypeTest {

    private MockedStatic<JsonSchemaValidator> mockedJsonValidator;

    /**
     * Set up Mockito before each test to mock the static JsonSchemaValidator class.
     * This is necessary to test the delegation logic in isJsonValidForType()
     * without relying on the actual validator implementation.
     */
    @BeforeEach
    void setup() {
        mockedJsonValidator = mockStatic(JsonSchemaValidator.class);
    }

    /**
     * Tear down Mockito after each test to close the mock.
     */
    @AfterEach
    void tearDown() {
        mockedJsonValidator.close();
    }

    // -------------------------------------------------------------------------
    // ENUM CONSTANTS & VALUEOF TESTS
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should contain exactly two constants (CATEGORY, SMTP, ...)")
    void enum_should_have_only_two_constant() {
        // Given & When: Get array of enum values
        ObjectsManagementOptionType[] types = ObjectsManagementOptionType.values();

        // Then the enum should have only two constant and their names must be correct
        assertEquals(4, types.length, "The enum should contain exactly 2 constants.");
        assertEquals("CATEGORY", ObjectsManagementOptionType.CATEGORY.name());
        assertEquals("SMTP", ObjectsManagementOptionType.SMTP.name());
        assertEquals("GENERIC_ENTRY", ObjectsManagementOptionType.GENERIC_ENTRY.name());
        assertEquals("ENTITY_TYPE", ObjectsManagementOptionType.ENTITY_TYPE.name());
    }

    @Test
    @DisplayName("Should successfully return the correct constant for a valid name")
    void valueOf_should_return_correct_enum_constant() {
        String categoryName = "CATEGORY";
        ObjectsManagementOptionType enumConstant = ObjectsManagementOptionType.valueOf(categoryName);
        assertEquals(ObjectsManagementOptionType.CATEGORY, enumConstant);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException for an invalid constant name")
    void valueOf_should_throw_exception_for_invalid_constant() {
        String invalidName = "INVALID_TYPE";
        assertThrows(IllegalArgumentException.class, () -> {
            ObjectsManagementOptionType.valueOf(invalidName);
        });
    }

    // -------------------------------------------------------------------------
    // ISVALID LOGIC TESTS
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("isValid() should correctly validate various string inputs")
    void isValid_should_correctly_validate_input() {
        // Success cases (valid, case-insensitive, trimmed)
        assertTrue(ObjectsManagementOptionType.isValid("CATEGORY"));
        assertTrue(ObjectsManagementOptionType.isValid("smtp"));
        assertTrue(ObjectsManagementOptionType.isValid("  CATEGORY  "));
        assertTrue(ObjectsManagementOptionType.isValid("CaTeGoRy"));
        assertTrue(ObjectsManagementOptionType.isValid("sMtP"));
        assertTrue(ObjectsManagementOptionType.isValid("GENERIC_eNtRY"));
        assertTrue(ObjectsManagementOptionType.isValid("ENTiTY_TYPE  "));
        assertTrue(ObjectsManagementOptionType.isValid(" ENTITy_TYPE "));

        // Failure cases (invalid, null, empty, blank)
        assertFalse(ObjectsManagementOptionType.isValid("INVALID_TYPE"));
        assertFalse(ObjectsManagementOptionType.isValid(null));
        assertFalse(ObjectsManagementOptionType.isValid(""));
        assertFalse(ObjectsManagementOptionType.isValid(" "));
        assertFalse(ObjectsManagementOptionType.isValid("\t")); // Test with tab
    }

    // -------------------------------------------------------------------------
    // ISJSONVALIDFORTYPE DELEGATION TESTS (NEW COVERAGE)
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("isJsonValidForType should return TRUE when JsonSchemaValidator returns TRUE")
    void isJsonValidForType_should_delegate_and_return_true() {
        // Given mock parameters
        String action = "INSERT";
        String option = "CATEGORY";
        Object json = new Object(); 

        // When the static validator is called, mock it to return TRUE
        mockedJsonValidator.when(() -> JsonSchemaValidator.isJsonValidForType(action, option, json))
                           .thenReturn(true);

        // Then the delegation method should also return TRUE
        assertTrue(ObjectsManagementOptionType.isJsonValidForType(action, option, json), 
                   "The method should return TRUE, delegating the successful result from the validator.");

        // Verify the static method was called exactly once with the correct parameters
        mockedJsonValidator.verify(() -> JsonSchemaValidator.isJsonValidForType(action, option, json), times(1));
    }

    @Test
    @DisplayName("isJsonValidForType should return FALSE when JsonSchemaValidator returns FALSE")
    void isJsonValidForType_should_delegate_and_return_false() {
        // Given mock parameters
        String action = "DELETE";
        String option = "SMTP";
        Object json = new Object();

        // When the static validator is called, mock it to return FALSE
        mockedJsonValidator.when(() -> JsonSchemaValidator.isJsonValidForType(action, option, json))
                           .thenReturn(false);

        // Then the delegation method should also return FALSE
        assertFalse(ObjectsManagementOptionType.isJsonValidForType(action, option, json),
                    "The method should return FALSE, delegating the failed result from the validator.");

        // Verify the static method was called exactly once with the correct parameters
        mockedJsonValidator.verify(() -> JsonSchemaValidator.isJsonValidForType(action, option, json), times(1));
    }

    @Test
    void getAllData_shouldReturnCorrectMap() {
        Map<String, String> data = ObjectsManagementOptionType.getAllData();
        assertEquals(4, data.size());
        assertTrue(data.containsKey("Category"));
        assertThrows(UnsupportedOperationException.class, () -> data.clear());
    }

    @Test
    void getAllKeysList_shouldReturnCorrectList() {
        List<String> keys = ObjectsManagementOptionType.getAllKeysList();
        assertEquals(4, keys.size());
        assertTrue(keys.contains("SMTP"));
        assertThrows(UnsupportedOperationException.class, () -> keys.add("NEW"));
    }

    /**
     * Tests that the {@code getKey} method returns the correct technical key for each enum constant.
     */
    @Test
    void getKey_should_return_correct_technical_key() {
        // Given the enum constants
        // When getting the keys
        // Then they should match the expected values
        assertEquals("Category", ObjectsManagementOptionType.CATEGORY.getKey());
        assertEquals("SMTP", ObjectsManagementOptionType.SMTP.getKey());
        assertEquals("GenericEntry", ObjectsManagementOptionType.GENERIC_ENTRY.getKey());
        assertEquals("EntityType", ObjectsManagementOptionType.ENTITY_TYPE.getKey());
    }

    /**
     * Tests that the {@code getDescription} method returns the correct description for each enum constant.
     */
    @Test
    void getDescription_should_return_correct_description() {
        // Given the enum constants
        // When getting the descriptions
        // Then they should match the expected values
        assertEquals("Represents a classification or grouping category.",
            ObjectsManagementOptionType.CATEGORY.getDescription());
        assertEquals("Represents an SMTP configuration object for email services.",
            ObjectsManagementOptionType.SMTP.getDescription());
        assertEquals("Represents a single master data record or lookup table entry.",
            ObjectsManagementOptionType.GENERIC_ENTRY.getDescription());
        assertEquals("Defines the classification ID for a master data record.",
            ObjectsManagementOptionType.ENTITY_TYPE.getDescription());
    }

    /**
     * Tests that the {@code isValid} method returns true for valid enum constant names.
     */
    @Test
    void isValid_should_return_true_for_valid_enum_names() {
        // Given valid enum constant names
        String categoryUpper = "CATEGORY";
        String categoryLower = "category";
        String categoryMixed = "Category";
        String smtpWithSpaces = "  SMTP  ";
        String genericEntry = "GENERIC_ENTRY";
        String entityType = "ENTITY_TYPE";

        // When validating these names
        // Then all should return true
        assertTrue(ObjectsManagementOptionType.isValid(categoryUpper));
        assertTrue(ObjectsManagementOptionType.isValid(categoryLower));
        assertTrue(ObjectsManagementOptionType.isValid(categoryMixed));
        assertTrue(ObjectsManagementOptionType.isValid(smtpWithSpaces));
        assertTrue(ObjectsManagementOptionType.isValid(genericEntry));
        assertTrue(ObjectsManagementOptionType.isValid(entityType));
    }

    /**
     * Tests that the {@code isValid} method returns false for invalid enum constant names.
     */
    @Test
    void isValid_should_return_false_for_invalid_enum_names() {
        // Given invalid enum constant names
        String invalidName = "INVALID_TYPE";
        String emptyString = "";
        String blankString = "   ";
        String nullString = null;

        // When validating these names
        // Then all should return false
        assertFalse(ObjectsManagementOptionType.isValid(invalidName));
        assertFalse(ObjectsManagementOptionType.isValid(emptyString));
        assertFalse(ObjectsManagementOptionType.isValid(blankString));
        assertFalse(ObjectsManagementOptionType.isValid(nullString));
    }

    /**
     * Tests that the {@code getAllData} method returns a correct and unmodifiable map.
     */
    @Test
    void getAllData_should_return_correct_and_unmodifiable_map() {
        // Given the getAllData method
        // When retrieving all data
        Map<String, String> data = ObjectsManagementOptionType.getAllData();

        // Then it should contain all enum constants
        assertNotNull(data);
        assertEquals(4, data.size());

        // Verify all keys and descriptions are present
        assertTrue(data.containsKey("Category"));
        assertTrue(data.containsKey("SMTP"));
        assertTrue(data.containsKey("GenericEntry"));
        assertTrue(data.containsKey("EntityType"));

        assertEquals("Represents a classification or grouping category.", data.get("Category"));
        assertEquals("Represents an SMTP configuration object for email services.", data.get("SMTP"));
        assertEquals("Represents a single master data record or lookup table entry.", data.get("GenericEntry"));
        assertEquals("Defines the classification ID for a master data record.", data.get("EntityType"));

        // Verify the map is unmodifiable
        assertThrows(UnsupportedOperationException.class, () -> data.put("NEW_KEY", "New Value"));
        assertThrows(UnsupportedOperationException.class, () -> data.clear());
    }

    /**
     * Tests that the {@code getAllKeysList} method returns a correct and unmodifiable list.
     */
    @Test
    void getAllKeysList_should_return_correct_and_unmodifiable_list() {
        // Given the getAllKeysList method
        // When retrieving all keys
        List<String> keys = ObjectsManagementOptionType.getAllKeysList();

        // Then it should contain all enum constant keys
        assertNotNull(keys);
        assertEquals(4, keys.size());

        // Verify all keys are present
        assertTrue(keys.contains("Category"));
        assertTrue(keys.contains("SMTP"));
        assertTrue(keys.contains("GenericEntry"));
        assertTrue(keys.contains("EntityType"));

        // Verify the list is unmodifiable
        assertThrows(UnsupportedOperationException.class, () -> keys.add("NEW_KEY"));
        assertThrows(UnsupportedOperationException.class, () -> keys.clear());
    }

    /**
     * Tests that all enum constants can be retrieved using values().
     */
    @Test
    void values_should_return_all_enum_constants() {
        // Given the enum
        // When calling values()
        ObjectsManagementOptionType[] values = ObjectsManagementOptionType.values();

        // Then it should return all 4 constants
        assertNotNull(values);
        assertEquals(4, values.length);

        // Verify the order and presence of all constants
        assertEquals(ObjectsManagementOptionType.CATEGORY, values[0]);
        assertEquals(ObjectsManagementOptionType.SMTP, values[1]);
        assertEquals(ObjectsManagementOptionType.GENERIC_ENTRY, values[2]);
        assertEquals(ObjectsManagementOptionType.ENTITY_TYPE, values[3]);
    }

    /**
     * Tests that valueOf correctly retrieves enum constants by name.
     */
    @Test
    void valueOf_should_retrieve_enum_constant_by_name() {
        // Given valid enum constant names
        // When calling valueOf
        // Then it should return the correct enum constant
        assertEquals(ObjectsManagementOptionType.CATEGORY,
            ObjectsManagementOptionType.valueOf("CATEGORY"));
        assertEquals(ObjectsManagementOptionType.SMTP,
            ObjectsManagementOptionType.valueOf("SMTP"));
        assertEquals(ObjectsManagementOptionType.GENERIC_ENTRY,
            ObjectsManagementOptionType.valueOf("GENERIC_ENTRY"));
        assertEquals(ObjectsManagementOptionType.ENTITY_TYPE,
            ObjectsManagementOptionType.valueOf("ENTITY_TYPE"));
    }

    /**
     * Tests that valueOf throws IllegalArgumentException for invalid enum constant names.
     */
    @Test
    void valueOf_should_throw_exception_for_invalid_name() {
        // Given an invalid enum constant name
        String invalidName = "INVALID_TYPE";

        // When calling valueOf with invalid name
        // Then it should throw IllegalArgumentException
        assertThrows(IllegalArgumentException.class,
            () -> ObjectsManagementOptionType.valueOf(invalidName));
    }

    /**
     * Tests that valueOf throws NullPointerException for null input.
     */
    @Test
    void valueOf_should_throw_exception_for_null_name() {
        // Given a null enum constant name
        // When calling valueOf with null
        // Then it should throw NullPointerException
        assertThrows(NullPointerException.class,
            () -> ObjectsManagementOptionType.valueOf(null));
    }
}