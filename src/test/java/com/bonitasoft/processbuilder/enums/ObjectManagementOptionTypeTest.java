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
}