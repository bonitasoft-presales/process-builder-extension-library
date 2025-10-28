package com.bonitasoft.processbuilder.extension;

import com.bonitasoft.processbuilder.records.InvolvedUsersData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link InvolvedUsersParser} class.
 * <p>
 * These tests ensure the {@code parseInvolvedUsersJson} static method correctly validates 
 * and extracts all mandatory fields (stepManager, stepUser, memberShips) and handles 
 * various error conditions, including missing or invalid field types, achieving maximum coverage.
 * </p>
 */
class InvolvedUsersParserTest {

    // JSON constant fields used for building test data
    private static final String MANAGER_REF = "manager_group_ref";
    private static final String USER_REF = "assigned_user_ref";
    private static final List<String> MEMBERSHIPS = List.of("role_admin", "group_finance");
    private static final String MEMBERSHIPS_JSON = String.format("[\"%s\", \"%s\"]", MEMBERSHIPS.get(0), MEMBERSHIPS.get(1));

    // JSON representing a complete, valid configuration
    private static final String VALID_FULL_JSON = String.format("""
            {
                "stepManager": "%s",
                "stepUser": "%s",
                "memberShips": %s,
                "input": null,
                "initiator": null,
                "users": []
            }
            """, MANAGER_REF, USER_REF, MEMBERSHIPS_JSON);

    // No need for setUp method or parser field as the method is static.

    // ----------------------------------------------------------------------------------
    //                         TESTS FOR VALID INPUTS
    // ----------------------------------------------------------------------------------

    @Test
    @DisplayName("Should successfully parse JSON when all three fields (manager, user, memberships) are present and valid")
    void testParseInvolvedUsersJson_FullValidData() {
        // ACT
        InvolvedUsersData result = InvolvedUsersParser.parseInvolvedUsersJson(VALID_FULL_JSON);

        // ASSERT
        assertNotNull(result, "The resulting data object should not be null.");
        assertEquals(MANAGER_REF, result.stepManagerRef(), "stepManagerRef must match the value from JSON.");
        assertEquals(USER_REF, result.stepUserRef(), "stepUserRef must match the value from JSON.");
        assertEquals(MEMBERSHIPS, result.memberships(), "Memberships list must be correctly parsed and matched.");
    }
    
    @Test
    @DisplayName("Should correctly handle an empty 'memberShips' array")
    void testParseInvolvedUsersJson_EmptyMembershipsArray() {
        String json = String.format("""
            {
                "stepManager": "%s",
                "stepUser": "%s",
                "memberShips": [],
                "input": null,
                "initiator": null,
                "users": []
            }
            """, MANAGER_REF, USER_REF);

        // ACT
        InvolvedUsersData result = InvolvedUsersParser.parseInvolvedUsersJson(json);

        // ASSERT
        assertTrue(result.memberships().isEmpty(), "Memberships list must be an empty list.");
        assertEquals(MANAGER_REF, result.stepManagerRef());
        assertEquals(USER_REF, result.stepUserRef());
    }

    // ----------------------------------------------------------------------------------
    //                         TESTS FOR MANDATORY FIELD FAILURES
    // ----------------------------------------------------------------------------------

    @Test
    @DisplayName("Should throw error if 'stepManager' field is missing from the JSON")
    void testParseInvolvedUsersJson_MissingStepManager() {
        String json = String.format("""
            {
                "stepUser": "%s",
                "memberShips": %s,
                "input": null, "initiator": null, "users": []
            }
            """, USER_REF, MEMBERSHIPS_JSON);

        // ACT & ASSERT
        IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class, 
            () -> InvolvedUsersParser.parseInvolvedUsersJson(json) 
        );
        assertTrue(thrown.getMessage().contains("Required field 'stepManager' is missing"),
                "Exception message should pinpoint the missing 'stepManager' field.");
    }

    @Test
    @DisplayName("Should throw error if 'stepUser' field is missing from the JSON")
    void testParseInvolvedUsersJson_MissingStepUser() {
        String json = String.format("""
            {
                "stepManager": "%s",
                "memberShips": %s,
                "input": null, "initiator": null, "users": []
            }
            """, MANAGER_REF, MEMBERSHIPS_JSON);

        // ACT & ASSERT
        IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class, 
            () -> InvolvedUsersParser.parseInvolvedUsersJson(json) 
        );
        assertTrue(thrown.getMessage().contains("Required field 'stepUser' is missing"),
                "Exception message should pinpoint the missing 'stepUser' field.");
    }

    @Test
    @DisplayName("Should throw error if 'memberShips' field is missing from the JSON")
    void testParseInvolvedUsersJson_MissingMemberShips() {
        String json = String.format("""
            {
                "stepManager": "%s",
                "stepUser": "%s",
                "input": null, "initiator": null, "users": []
            }
            """, MANAGER_REF, USER_REF);

        // ACT & ASSERT
        IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class, 
            () -> InvolvedUsersParser.parseInvolvedUsersJson(json) 
        );
        assertTrue(thrown.getMessage().contains("Required field 'memberShips' is missing or not a valid array"),
                "Exception message should pinpoint the missing 'memberShips' array.");
    }

    @ParameterizedTest(name = "Invalid type for: {0}")
    @ValueSource(strings = {"stepManager", "stepUser"})
    @DisplayName("Should throw error if mandatory text fields are present but not text (e.g., number)")
    void testParseInvolvedUsersJson_RequiredFieldWrongType(String fieldName) {
        String json = """
            {
                "stepManager": 12345,
                "stepUser": 67890,
                "memberShips": [],
                "input": null, "initiator": null, "users": []
            }
            """;

        // ACT & ASSERT
        IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class, 
            () -> InvolvedUsersParser.parseInvolvedUsersJson(json),
            "Should throw exception for required field with incorrect type: " + fieldName
        );
        
        // Assert on the first failing field ('stepManager') as execution stops there.
        assertTrue(thrown.getMessage().contains("'stepManager' is missing, null, or not a valid text value"),
                    "Exception message should pinpoint 'stepManager' as the field with the wrong type.");
    }
    
    @Test
    @DisplayName("Should throw error if 'memberShips' is present but not an array")
    void testParseInvolvedUsersJson_MembershipsNotArray() {
        String json = String.format("""
            {
                "stepManager": "%s",
                "stepUser": "%s",
                "memberShips": "not_an_array",
                "input": null, "initiator": null
            }
            """, MANAGER_REF, USER_REF);

        // ACT & ASSERT
        IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class, 
            () -> InvolvedUsersParser.parseInvolvedUsersJson(json),
            "Should throw exception if memberShips is not an array."
        );
        assertTrue(thrown.getMessage().contains("Required field 'memberShips' is missing or not a valid array"),
                    "Exception message should pinpoint that memberShips is not a valid array.");
    }

    // ----------------------------------------------------------------------------------
    //                         TESTS FOR GENERAL EXCEPTIONS
    // ----------------------------------------------------------------------------------

    @ParameterizedTest(name = "Input: \"{0}\"")
    @NullAndEmptySource
    @DisplayName("Should throw IllegalArgumentException for null or empty JSON string input")
    void testParseInvolvedUsersJson_NullOrEmptyInput(String input) {
        // ACT & ASSERT
        assertThrows(IllegalArgumentException.class, 
                     () -> InvolvedUsersParser.parseInvolvedUsersJson(input),
                     "Must throw IllegalArgumentException if the input JSON string is null or empty.");
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException for JSON that is structurally invalid (JsonProcessingException)")
    void testParseInvolvedUsersJson_InvalidJsonFormat() {
        // JSON that cannot be parsed by Jackson (e.g., missing quotes, missing comma)
        String invalidJson = "{ \"stepManager\": \"test\" \"invalidKey\": 10";
        
        // ACT & ASSERT
        IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class, 
            () -> InvolvedUsersParser.parseInvolvedUsersJson(invalidJson),
            "Must throw IllegalArgumentException indicating a parsing failure."
        );
        
        assertTrue(thrown.getMessage().contains("Invalid JSON format."), 
                    "Exception message should confirm invalid JSON format.");
        assertNotNull(thrown.getCause(), "The exception should wrap the underlying Jackson exception.");
    }
}