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
                ObjectsManagementOptionType.SMTP.name(), // Mismatch
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
}