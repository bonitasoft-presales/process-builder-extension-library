package com.bonitasoft.processbuilder.validation;

import com.bonitasoft.processbuilder.enums.ActionType;
import com.bonitasoft.processbuilder.enums.ObjectsManagementOptionType;
import com.bonitasoft.processbuilder.enums.ProcessOptionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link ProcessOptionValidator} utility class.
 * This class ensures full coverage for both overloaded isMatchingActionAndOption methods
 * and the private constructor.
 */
class ProcessOptionValidatorTest {

    // Common expected enums for testing
    private static final ActionType EXPECTED_ACTION = ActionType.UPDATE;
    private static final ObjectsManagementOptionType EXPECTED_OBJ_OPTION = ObjectsManagementOptionType.CATEGORY;
    private static final ProcessOptionType EXPECTED_PROCESS_OPTION = ProcessOptionType.STEPS;

    // -------------------------------------------------------------------------
    // Constructor Test (Non-instantiable)
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Constructor should throw UnsupportedOperationException")
    void constructor_should_throw_unsupported_operation_exception() throws Exception {
        // 1. Retrieve the Constructor object for the class.
        Constructor<ProcessOptionValidator> constructor = ProcessOptionValidator.class.getDeclaredConstructor();

        // 2. Ensure the constructor is PRIVATE.
        assertTrue(Modifier.isPrivate(constructor.getModifiers()));

        // 3. Force Accessibility & Invoke the constructor
        constructor.setAccessible(true);
        InvocationTargetException thrownException = assertThrows(InvocationTargetException.class, constructor::newInstance);

        // 4. Verify the actual cause is the expected exception.
        assertTrue(thrownException.getCause() instanceof UnsupportedOperationException);
    }

    // -------------------------------------------------------------------------
    // Overload 1: ObjectsManagementOptionType Tests
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("isMatchingActionAndOption(Obj) should return true for exact match")
    void isMatchingActionAndOption_Obj_should_return_true_for_exact_match() {
        assertTrue(ProcessOptionValidator.isMatchingActionAndOption(
                EXPECTED_ACTION.name(),
                EXPECTED_OBJ_OPTION.name(),
                EXPECTED_ACTION,
                EXPECTED_OBJ_OPTION
        ));
    }

    @Test
    @DisplayName("isMatchingActionAndOption(Obj) should return true for case-insensitive match")
    void isMatchingActionAndOption_Obj_should_return_true_for_case_insensitive_match() {
        assertTrue(ProcessOptionValidator.isMatchingActionAndOption(
                EXPECTED_ACTION.name().toLowerCase(),
                EXPECTED_OBJ_OPTION.name().toLowerCase(),
                EXPECTED_ACTION,
                EXPECTED_OBJ_OPTION
        ));
    }

    @Test
    @DisplayName("isMatchingActionAndOption(Obj) should return false if ActionType mismatches")
    void isMatchingActionAndOption_Obj_should_return_false_on_action_mismatch() {
        assertFalse(ProcessOptionValidator.isMatchingActionAndOption(
                ActionType.DELETE.name(), // Mismatch
                EXPECTED_OBJ_OPTION.name(),
                EXPECTED_ACTION,
                EXPECTED_OBJ_OPTION
        ));
    }

    @Test
    @DisplayName("isMatchingActionAndOption(Obj) should return false if OptionType mismatches")
    void isMatchingActionAndOption_Obj_should_return_false_on_option_mismatch() {
        assertFalse(ProcessOptionValidator.isMatchingActionAndOption(
                EXPECTED_ACTION.name(),
                ObjectsManagementOptionType.CONFIGURATION.name(), // Mismatch
                EXPECTED_ACTION,
                EXPECTED_OBJ_OPTION
        ));
    }

    @Test
    @DisplayName("isMatchingActionAndOption(Obj) should return false on null/empty input strings")
    void isMatchingActionAndOption_Obj_should_return_false_on_null_empty_input() {
        // Null actionType
        assertFalse(ProcessOptionValidator.isMatchingActionAndOption(null, EXPECTED_OBJ_OPTION.name(), EXPECTED_ACTION, EXPECTED_OBJ_OPTION));
        // Empty optionType
        assertFalse(ProcessOptionValidator.isMatchingActionAndOption(EXPECTED_ACTION.name(), "", EXPECTED_ACTION, EXPECTED_OBJ_OPTION));
        // Empty actionType
        assertFalse(ProcessOptionValidator.isMatchingActionAndOption("", EXPECTED_OBJ_OPTION.name(), EXPECTED_ACTION, EXPECTED_OBJ_OPTION));
        // Null optionType
        assertFalse(ProcessOptionValidator.isMatchingActionAndOption(EXPECTED_ACTION.name(), null, EXPECTED_ACTION, EXPECTED_OBJ_OPTION));
    }
    
    @Test
    @DisplayName("isMatchingActionAndOption(Obj) should return false on null expected enums")
    void isMatchingActionAndOption_Obj_should_return_false_on_null_expected_enums() {
        // Null expectedActionType (NEW/FIXED TEST)
        assertFalse(ProcessOptionValidator.isMatchingActionAndOption(EXPECTED_ACTION.name(), EXPECTED_OBJ_OPTION.name(), null, EXPECTED_OBJ_OPTION), 
            "Should fail if expectedActionType is null.");
            
        // Null expectedOptionType (NEW/FIXED TEST)
        assertFalse(ProcessOptionValidator.isMatchingActionAndOption(EXPECTED_ACTION.name(), EXPECTED_OBJ_OPTION.name(), EXPECTED_ACTION, (ObjectsManagementOptionType) null),
            "Should fail if expectedOptionType is null.");
    }


    // -------------------------------------------------------------------------
    // Overload 2: ProcessOptionType Tests
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("isMatchingActionAndOption(Process) should return true for exact match")
    void isMatchingActionAndOption_Process_should_return_true_for_exact_match() {
        assertTrue(ProcessOptionValidator.isMatchingActionAndOption(
                EXPECTED_ACTION.name(),
                EXPECTED_PROCESS_OPTION.name(),
                EXPECTED_ACTION,
                EXPECTED_PROCESS_OPTION
        ));
    }

    @Test
    @DisplayName("isMatchingActionAndOption(Process) should return true for case-insensitive match")
    void isMatchingActionAndOption_Process_should_return_true_for_case_insensitive_match() {
        assertTrue(ProcessOptionValidator.isMatchingActionAndOption(
                EXPECTED_ACTION.name().toLowerCase(),
                EXPECTED_PROCESS_OPTION.name().toLowerCase(),
                EXPECTED_ACTION,
                EXPECTED_PROCESS_OPTION
        ));
    }

    @Test
    @DisplayName("isMatchingActionAndOption(Process) should return false if ActionType mismatches")
    void isMatchingActionAndOption_Process_should_return_false_on_action_mismatch() {
        assertFalse(ProcessOptionValidator.isMatchingActionAndOption(
                ActionType.DELETE.name(), // Mismatch
                EXPECTED_PROCESS_OPTION.name(),
                EXPECTED_ACTION,
                EXPECTED_PROCESS_OPTION
        ));
    }

    @Test
    @DisplayName("isMatchingActionAndOption(Process) should return false if OptionType mismatches")
    void isMatchingActionAndOption_Process_should_return_false_on_option_mismatch() {
        assertFalse(ProcessOptionValidator.isMatchingActionAndOption(
                EXPECTED_ACTION.name(),
                ProcessOptionType.USERS.name(), // Mismatch
                EXPECTED_ACTION,
                EXPECTED_PROCESS_OPTION
        ));
    }

    @Test
    @DisplayName("isMatchingActionAndOption(Process) should return false on null/empty input strings")
    void isMatchingActionAndOption_Process_should_return_false_on_null_empty_input() {
        // Empty actionType
        assertFalse(ProcessOptionValidator.isMatchingActionAndOption("", EXPECTED_PROCESS_OPTION.name(), EXPECTED_ACTION, EXPECTED_PROCESS_OPTION));
        // Null optionType
        assertFalse(ProcessOptionValidator.isMatchingActionAndOption(EXPECTED_ACTION.name(), null, EXPECTED_ACTION, EXPECTED_PROCESS_OPTION));
        // Null actionType
        assertFalse(ProcessOptionValidator.isMatchingActionAndOption(null, EXPECTED_PROCESS_OPTION.name(), EXPECTED_ACTION, EXPECTED_PROCESS_OPTION));
        // Empty optionType
        assertFalse(ProcessOptionValidator.isMatchingActionAndOption(EXPECTED_ACTION.name(), "", EXPECTED_ACTION, EXPECTED_PROCESS_OPTION));
    }
    
    @Test
    @DisplayName("isMatchingActionAndOption(Process) should return false on null expected enums")
    void isMatchingActionAndOption_Process_should_return_false_on_null_expected_enums() {
        // Null expectedActionType (NEW/FIXED TEST)
        assertFalse(ProcessOptionValidator.isMatchingActionAndOption(EXPECTED_ACTION.name(), EXPECTED_PROCESS_OPTION.name(), null, EXPECTED_PROCESS_OPTION),
            "Should fail if expectedActionType is null.");

        // Null expectedProcessOptionType (NEW/FIXED TEST)
        assertFalse(ProcessOptionValidator.isMatchingActionAndOption(EXPECTED_ACTION.name(), EXPECTED_PROCESS_OPTION.name(), EXPECTED_ACTION, (ProcessOptionType) null),
            "Should fail if expectedProcessOptionType is null.");
    }

    // -------------------------------------------------------------------------
    // Additional Tests for Mutation Coverage
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("isMatchingActionAndOption(Obj) should return false for whitespace-only actionType")
    void isMatchingActionAndOption_Obj_should_return_false_for_whitespace_action() {
        assertFalse(ProcessOptionValidator.isMatchingActionAndOption(
                "   ",  // Whitespace only - tests trim().isEmpty()
                EXPECTED_OBJ_OPTION.name(),
                EXPECTED_ACTION,
                EXPECTED_OBJ_OPTION
        ), "Whitespace-only actionType should return false");
    }

    @Test
    @DisplayName("isMatchingActionAndOption(Obj) should return false for whitespace-only optionType")
    void isMatchingActionAndOption_Obj_should_return_false_for_whitespace_option() {
        assertFalse(ProcessOptionValidator.isMatchingActionAndOption(
                EXPECTED_ACTION.name(),
                "   ",  // Whitespace only - tests trim().isEmpty()
                EXPECTED_ACTION,
                EXPECTED_OBJ_OPTION
        ), "Whitespace-only optionType should return false");
    }

    @Test
    @DisplayName("isMatchingActionAndOption(Process) should return false for whitespace-only actionType")
    void isMatchingActionAndOption_Process_should_return_false_for_whitespace_action() {
        assertFalse(ProcessOptionValidator.isMatchingActionAndOption(
                "   ",  // Whitespace only
                EXPECTED_PROCESS_OPTION.name(),
                EXPECTED_ACTION,
                EXPECTED_PROCESS_OPTION
        ), "Whitespace-only actionType should return false");
    }

    @Test
    @DisplayName("isMatchingActionAndOption(Process) should return false for whitespace-only optionType")
    void isMatchingActionAndOption_Process_should_return_false_for_whitespace_option() {
        assertFalse(ProcessOptionValidator.isMatchingActionAndOption(
                EXPECTED_ACTION.name(),
                "\t\n",  // Whitespace only (tabs and newlines)
                EXPECTED_ACTION,
                EXPECTED_PROCESS_OPTION
        ), "Whitespace-only optionType should return false");
    }

    @Test
    @DisplayName("isMatchingActionAndOption(Obj) should handle mixed case correctly")
    void isMatchingActionAndOption_Obj_should_handle_mixed_case() {
        // Mixed case should match
        assertTrue(ProcessOptionValidator.isMatchingActionAndOption(
                "UpDaTe",  // Mixed case
                "CaTeGoRy",  // Mixed case
                ActionType.UPDATE,
                ObjectsManagementOptionType.CATEGORY
        ), "Mixed case should match");
    }

    @Test
    @DisplayName("isMatchingActionAndOption(Process) should handle mixed case correctly")
    void isMatchingActionAndOption_Process_should_handle_mixed_case() {
        // Mixed case should match
        assertTrue(ProcessOptionValidator.isMatchingActionAndOption(
                "uPdAtE",  // Mixed case
                "sTePs",  // Mixed case
                ActionType.UPDATE,
                ProcessOptionType.STEPS
        ), "Mixed case should match");
    }

    @Test
    @DisplayName("isMatchingActionAndOption(Obj) should return false when both mismatch")
    void isMatchingActionAndOption_Obj_should_return_false_when_both_mismatch() {
        assertFalse(ProcessOptionValidator.isMatchingActionAndOption(
                ActionType.DELETE.name(),  // Mismatch
                ObjectsManagementOptionType.CONFIGURATION.name(),  // Mismatch
                EXPECTED_ACTION,
                EXPECTED_OBJ_OPTION
        ), "Both mismatching should return false");
    }

    @Test
    @DisplayName("isMatchingActionAndOption(Process) should return false when both mismatch")
    void isMatchingActionAndOption_Process_should_return_false_when_both_mismatch() {
        assertFalse(ProcessOptionValidator.isMatchingActionAndOption(
                ActionType.DELETE.name(),  // Mismatch
                ProcessOptionType.USERS.name(),  // Mismatch
                EXPECTED_ACTION,
                EXPECTED_PROCESS_OPTION
        ), "Both mismatching should return false");
    }

    @Test
    @DisplayName("isMatchingActionAndOption(Obj) should verify exact true return")
    void isMatchingActionAndOption_Obj_exact_true() {
        boolean result = ProcessOptionValidator.isMatchingActionAndOption(
                ActionType.UPDATE.name(),
                ObjectsManagementOptionType.CATEGORY.name(),
                ActionType.UPDATE,
                ObjectsManagementOptionType.CATEGORY
        );
        assertTrue(result, "Should return exactly true");
        assertEquals(true, result, "Result must be true");
    }

    @Test
    @DisplayName("isMatchingActionAndOption(Obj) should verify exact false return when action only matches")
    void isMatchingActionAndOption_Obj_exact_false_action_only() {
        boolean result = ProcessOptionValidator.isMatchingActionAndOption(
                ActionType.UPDATE.name(),  // Matches
                ObjectsManagementOptionType.CONFIGURATION.name(),  // Does NOT match
                ActionType.UPDATE,
                ObjectsManagementOptionType.CATEGORY
        );
        assertFalse(result, "Should return exactly false when only action matches");
        assertEquals(false, result);
    }

    @Test
    @DisplayName("isMatchingActionAndOption(Obj) should verify exact false return when option only matches")
    void isMatchingActionAndOption_Obj_exact_false_option_only() {
        boolean result = ProcessOptionValidator.isMatchingActionAndOption(
                ActionType.DELETE.name(),  // Does NOT match
                ObjectsManagementOptionType.CATEGORY.name(),  // Matches
                ActionType.UPDATE,
                ObjectsManagementOptionType.CATEGORY
        );
        assertFalse(result, "Should return exactly false when only option matches");
        assertEquals(false, result);
    }

    @Test
    @DisplayName("isMatchingActionAndOption(Process) should verify exact true return")
    void isMatchingActionAndOption_Process_exact_true() {
        boolean result = ProcessOptionValidator.isMatchingActionAndOption(
                ActionType.UPDATE.name(),
                ProcessOptionType.STEPS.name(),
                ActionType.UPDATE,
                ProcessOptionType.STEPS
        );
        assertTrue(result, "Should return exactly true");
        assertEquals(true, result);
    }

    @Test
    @DisplayName("isMatchingActionAndOption(Process) should verify exact false return when action only matches")
    void isMatchingActionAndOption_Process_exact_false_action_only() {
        boolean result = ProcessOptionValidator.isMatchingActionAndOption(
                ActionType.UPDATE.name(),  // Matches
                ProcessOptionType.USERS.name(),  // Does NOT match
                ActionType.UPDATE,
                ProcessOptionType.STEPS
        );
        assertFalse(result, "Should return exactly false when only action matches");
        assertEquals(false, result);
    }

    @Test
    @DisplayName("isMatchingActionAndOption(Process) should verify exact false return when option only matches")
    void isMatchingActionAndOption_Process_exact_false_option_only() {
        boolean result = ProcessOptionValidator.isMatchingActionAndOption(
                ActionType.DELETE.name(),  // Does NOT match
                ProcessOptionType.STEPS.name(),  // Matches
                ActionType.UPDATE,
                ProcessOptionType.STEPS
        );
        assertFalse(result, "Should return exactly false when only option matches");
        assertEquals(false, result);
    }

    @Test
    @DisplayName("isMatchingActionAndOption(Obj) should handle all enum values for ActionType")
    void isMatchingActionAndOption_Obj_all_action_types() {
        for (ActionType action : ActionType.values()) {
            boolean result = ProcessOptionValidator.isMatchingActionAndOption(
                    action.name(),
                    EXPECTED_OBJ_OPTION.name(),
                    action,
                    EXPECTED_OBJ_OPTION
            );
            assertTrue(result, "Should match for ActionType: " + action.name());
        }
    }

    @Test
    @DisplayName("isMatchingActionAndOption(Obj) should handle all enum values for ObjectsManagementOptionType")
    void isMatchingActionAndOption_Obj_all_option_types() {
        for (ObjectsManagementOptionType option : ObjectsManagementOptionType.values()) {
            boolean result = ProcessOptionValidator.isMatchingActionAndOption(
                    EXPECTED_ACTION.name(),
                    option.name(),
                    EXPECTED_ACTION,
                    option
            );
            assertTrue(result, "Should match for ObjectsManagementOptionType: " + option.name());
        }
    }

    @Test
    @DisplayName("isMatchingActionAndOption(Process) should handle all enum values for ProcessOptionType")
    void isMatchingActionAndOption_Process_all_option_types() {
        for (ProcessOptionType option : ProcessOptionType.values()) {
            boolean result = ProcessOptionValidator.isMatchingActionAndOption(
                    EXPECTED_ACTION.name(),
                    option.name(),
                    EXPECTED_ACTION,
                    option
            );
            assertTrue(result, "Should match for ProcessOptionType: " + option.name());
        }
    }

    @Test
    @DisplayName("isMatchingActionAndOption should not match different enum names")
    void isMatchingActionAndOption_should_not_match_different_names() {
        // Even if strings are provided, they must match expected enums
        assertFalse(ProcessOptionValidator.isMatchingActionAndOption(
                "RANDOM_ACTION",
                "RANDOM_OPTION",
                ActionType.UPDATE,
                ObjectsManagementOptionType.CATEGORY
        ), "Random strings should not match specific enums");
    }
}