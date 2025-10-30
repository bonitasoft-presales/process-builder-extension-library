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
 * Unit tests for the {@link ProcessOptionType} enumeration.
 * <p>
 * This class validates enum constants, the {@code isValid} logic, and ensures
 * proper delegation to the {@code JsonSchemaValidator} for schema validation.
 * </p>
 */
class ProcessOptionTypeTest {

    private MockedStatic<JsonSchemaValidator> mockedJsonValidator;

    /**
     * Set up Mockito before each test to mock the static JsonSchemaValidator class.
     */
    @BeforeEach
    void setup() {
        // Mocking static methods requires wrapping the class in MockedStatic
        mockedJsonValidator = mockStatic(JsonSchemaValidator.class);
    }

    /**
     * Tear down Mockito after each test to close the mock.
     */
    @AfterEach
    void tearDown() {
        // Must close the mock after use to avoid impacting other tests
        mockedJsonValidator.close();
    }

    // -------------------------------------------------------------------------
    // ENUM CONSTANTS & VALUEOF TESTS (Copied and retained from original)
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should contain exactly 5 expected enum constants")
    void enum_should_contain_all_expected_constants() {
        ProcessOptionType[] types = ProcessOptionType.values();
        assertEquals(5, types.length);
        
        // Ensure all required constants exist
        assertTrue(containsEnum(types, "PARAMETER"));
        assertTrue(containsEnum(types, "USERS"));
        assertTrue(containsEnum(types, "INPUTS"));
        assertTrue(containsEnum(types, "STEPS"));
        assertTrue(containsEnum(types, "STATUS"));
    }

    @Test
    @DisplayName("Should successfully return the correct constant for a valid name")
    void valueOf_should_return_correct_enum_constant() {
        String usersName = "USERS";
        ProcessOptionType enumConstant = ProcessOptionType.valueOf(usersName);
        assertEquals(ProcessOptionType.USERS, enumConstant);
    }
    
    @Test
    @DisplayName("Should throw IllegalArgumentException for an invalid constant name")
    void valueOf_should_throw_exception_for_invalid_constant() {
        String invalidName = "INVALID_TYPE";
        assertThrows(IllegalArgumentException.class, () -> {
            ProcessOptionType.valueOf(invalidName);
        });
    }

    /**
     * Helper method to check if an array of enums contains a specific constant name.
     */
    private boolean containsEnum(ProcessOptionType[] enums, String name) {
        for (ProcessOptionType e : enums) {
            if (e.name().equals(name)) {
                return true;
            }
        }
        return false;
    }

    @Test
    @DisplayName("isValid() should correctly validate various string inputs")
    void isValid_should_correctly_validate_input() {
        // Success cases (valid, case-insensitive, trimmed)
        assertTrue(ProcessOptionType.isValid("USERS"));
        assertTrue(ProcessOptionType.isValid("users"));
        assertTrue(ProcessOptionType.isValid("  USERS  "));
        assertTrue(ProcessOptionType.isValid("parameter"));
        assertTrue(ProcessOptionType.isValid("STATUS"));


        // Failure cases (invalid, null, empty, blank)
        assertFalse(ProcessOptionType.isValid("INVALID_TYPE"));
        assertFalse(ProcessOptionType.isValid(null));
        assertFalse(ProcessOptionType.isValid(""));
        assertFalse(ProcessOptionType.isValid(" "));
        assertFalse(ProcessOptionType.isValid("\t")); // Test with tab
    }

    @Test
    @DisplayName("isJsonValidForType should return TRUE when JsonSchemaValidator returns TRUE")
    void isJsonValidForType_should_delegate_and_return_true() {
        // Given parameters
        String action = "INSERT";
        String option = "INPUTS";
        Object json = new Object(); // Mock JSON object

        // When JsonSchemaValidator is called, it should return TRUE
        mockedJsonValidator.when(() -> JsonSchemaValidator.isJsonValidForType(action, option, json))
                           .thenReturn(true);

        // Then the delegation method should also return TRUE
        assertTrue(ProcessOptionType.isJsonValidForType(action, option, json), 
                   "The method should return TRUE, delegating the result from the validator.");

        // Verify the static method was called exactly once with the correct parameters
        mockedJsonValidator.verify(() -> JsonSchemaValidator.isJsonValidForType(action, option, json), times(1));
    }

    @Test
    @DisplayName("isJsonValidForType should return FALSE when JsonSchemaValidator returns FALSE")
    void isJsonValidForType_should_delegate_and_return_false() {
        // Given parameters
        String action = "DELETE";
        String option = "STATUS";
        Object json = new Object();

        // When JsonSchemaValidator is called, it should return FALSE
        mockedJsonValidator.when(() -> JsonSchemaValidator.isJsonValidForType(action, option, json))
                           .thenReturn(false);

        // Then the delegation method should also return FALSE
        assertFalse(ProcessOptionType.isJsonValidForType(action, option, json),
                    "The method should return FALSE, delegating the result from the validator.");

        // Verify the static method was called exactly once with the correct parameters
        mockedJsonValidator.verify(() -> JsonSchemaValidator.isJsonValidForType(action, option, json), times(1));
    }

    @Test
    void getAllData_shouldReturnCorrectMap() {
        Map<String, String> data = ProcessOptionType.getAllData();
        assertEquals(5, data.size());
        assertTrue(data.containsKey("Users"));
        assertThrows(UnsupportedOperationException.class, () -> data.clear());
    }

    @Test
    void getAllKeysList_shouldReturnCorrectList() {
        List<String> keys = ProcessOptionType.getAllKeysList();
        assertEquals(5, keys.size());
        assertTrue(keys.contains("Parameter"));
        assertThrows(UnsupportedOperationException.class, () -> keys.add("NEW"));
    }
}