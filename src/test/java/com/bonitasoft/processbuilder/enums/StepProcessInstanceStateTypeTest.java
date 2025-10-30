package com.bonitasoft.processbuilder.enums;

import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for StepProcessInstanceStateType to ensure full coverage of utility methods.
 */
class StepProcessInstanceStateTypeTest {

    @Test
    void getAttributes_shouldReturnCorrectValues() {
        StepProcessInstanceStateType state = StepProcessInstanceStateType.PENDING;
        assertEquals("Pending", state.getKey());
        assertTrue(state.getDescription().contains("user task awaiting action"));
    }
    
    @Test
    void isValid_shouldReturnTrueForValidNamesCaseInsensitive() {
        assertTrue(StepProcessInstanceStateType.isValid("running"));
        assertTrue(StepProcessInstanceStateType.isValid("COMPLETED"));
        assertTrue(StepProcessInstanceStateType.isValid("pending"));
    }

    @Test
    void isValid_shouldReturnFalseForInvalidNames() {
        assertFalse(StepProcessInstanceStateType.isValid("Archived")); // Archived is in Process instance, not Step
    }

    @Test
    void getAllStatesData_shouldReturnCorrectMap() {
        Map<String, String> data = StepProcessInstanceStateType.getAllData();
        assertEquals(5, data.size());
        assertTrue(data.containsKey("Running"));
        assertThrows(UnsupportedOperationException.class, () -> data.remove("Failed"));
    }

    @Test
    void getAllKeysList_shouldReturnCorrectList() {
        List<String> keys = StepProcessInstanceStateType.getAllKeysList();
        assertEquals(5, keys.size());
        assertTrue(keys.contains("Canceled"));
        assertThrows(UnsupportedOperationException.class, () -> keys.set(0, "START"));
    }
}