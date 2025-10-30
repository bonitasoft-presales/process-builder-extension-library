package com.bonitasoft.processbuilder.enums;

import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for ProcessInstanceStateType to ensure full coverage of utility methods.
 */
class ProcessInstanceStateTypeTest {

    @Test
    void getAttributes_shouldReturnCorrectValues() {
        ProcessInstanceStateType state = ProcessInstanceStateType.COMPLETED;
        assertEquals("Completed", state.getKey());
        assertTrue(state.getDescription().contains("finished successfully"));
    }
    
    @Test
    void isValid_shouldReturnTrueForValidNamesCaseInsensitive() {
        assertTrue(ProcessInstanceStateType.isValid("rUnning"));
        assertTrue(ProcessInstanceStateType.isValid("COMPLETED"));
        assertTrue(ProcessInstanceStateType.isValid("failed"));
        assertTrue(ProcessInstanceStateType.isValid("  Archived  "));
    }

    @Test
    void isValid_shouldReturnFalseForInvalidNames() {
        assertFalse(ProcessInstanceStateType.isValid("InActive"));
        assertFalse(ProcessInstanceStateType.isValid("Pending")); // Pending is in Step instance, not Process instance
    }

    @Test
    void getAllStatesData_shouldReturnCorrectMap() {
        Map<String, String> data = ProcessInstanceStateType.getAllData();
        assertEquals(6, data.size());
        assertTrue(data.containsKey("Canceled"));
        assertThrows(UnsupportedOperationException.class, () -> data.clear());
    }

    @Test
    void getAllKeysList_shouldReturnCorrectList() {
        List<String> keys = ProcessInstanceStateType.getAllKeysList();
        assertEquals(6, keys.size());
        assertTrue(keys.contains("Paused"));
        assertThrows(UnsupportedOperationException.class, () -> keys.add("NEW"));
    }
}
