package com.bonitasoft.processbuilder.enums;

import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for ProcessStateType to ensure full coverage of utility methods.
 */
class ProcessStateTypeTest {

    // --- Enum Fields Test ---

    @Test
    void getAttributes_shouldReturnCorrectValues() {
        ProcessStateType state = ProcessStateType.RUNNING;
        assertEquals("Running", state.getKey());
        assertTrue(state.getDescription().contains("enabled and available"));
    }
    
    @Test
    void inError_shouldHaveCorrectKey() {
        ProcessStateType state = ProcessStateType.IN_ERROR;
        assertEquals("In Error", state.getKey());
    }

    // --- isValid() Tests ---

    @Test
    void isValid_shouldReturnTrueForValidNamesCaseInsensitive() {
        assertTrue(ProcessStateType.isValid("Draft"));
        assertTrue(ProcessStateType.isValid("running"));
        assertTrue(ProcessStateType.isValid("STOPPED"));
        assertTrue(ProcessStateType.isValid("aRcHiveD"));
    }

    @Test
    void isValid_shouldHandleSpacesAndReturnTrue() {
        assertTrue(ProcessStateType.isValid("  Draft  "));
        // Special case for IN_ERROR since the enum name is IN_ERROR
        assertTrue(ProcessStateType.isValid("in error")); 
        assertTrue(ProcessStateType.isValid("IN ERROR"));
    }

    @Test
    void isValid_shouldReturnFalseForInvalidNames() {
        assertFalse(ProcessStateType.isValid("InvalidState"));
        assertFalse(ProcessStateType.isValid("DRAFTED"));
    }

    @Test
    void isValid_shouldReturnFalseForNullOrEmptyInput() {
        assertFalse(ProcessStateType.isValid(null));
        assertFalse(ProcessStateType.isValid(""));
        assertFalse(ProcessStateType.isValid("  "));
    }

    // --- getAllStatesData() Test ---

    @Test
    void getAllStatesData_shouldReturnCorrectMap() {
        Map<String, String> data = ProcessStateType.getAllStatesData();
        assertEquals(6, data.size());
        assertTrue(data.containsKey("Running"));
        assertTrue(data.get("Draft").contains("under construction"));
        
        // Ensure the map is read-only (unmodifiable map)
        assertThrows(UnsupportedOperationException.class, () -> data.put("NEW", "Test"));
    }

    // --- getAllKeysList() Test ---

    @Test
    void getAllKeysList_shouldReturnCorrectList() {
        List<String> keys = ProcessStateType.getAllKeysList();
        assertEquals(6, keys.size());
        assertTrue(keys.contains("Stopped"));
        // Ensure the list is read-only
        assertThrows(UnsupportedOperationException.class, () -> keys.add("NEW"));
    }
}
