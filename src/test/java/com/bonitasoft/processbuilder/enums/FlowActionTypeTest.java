package com.bonitasoft.processbuilder.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link FlowActionType} enumeration, verifying 
 * constant values and the {@code getAllActionData} utility method.
 */
class FlowActionTypeTest {

    private static final Map<String, String> EXPECTED_DATA;

    static {
        EXPECTED_DATA = FlowActionType.getAllActionData();
    }

    @Test
    @DisplayName("Should contain exactly three flow action constants")
    void should_contain_three_constants() {
        assertEquals(3, FlowActionType.values().length);
        assertEquals(3, EXPECTED_DATA.size());
    }

    @Test
    @DisplayName("FORM constant should have the correct key and description")
    void form_should_have_correct_values() {
        assertEquals("form", FlowActionType.FORM.getKey());
        assertTrue(FlowActionType.FORM.getDescription().startsWith("Form Action:"));
    }

    @Test
    @DisplayName("REDIRECTIONS constant should have the correct key and description")
    void redirections_should_have_correct_values() {
        assertEquals("redirections", FlowActionType.REDIRECTIONS.getKey());
        assertTrue(FlowActionType.REDIRECTIONS.getDescription().contains("user is sent after completing the task"));
    }

    @Test
    @DisplayName("getAllActionData should return an unmodifiable map with all required keys")
    void getAllActionData_should_return_correct_map() {
        assertTrue(EXPECTED_DATA.containsKey("form"));
        assertTrue(EXPECTED_DATA.containsKey("notifications"));
        assertTrue(EXPECTED_DATA.containsKey("redirections"));

        assertThrows(UnsupportedOperationException.class, () -> EXPECTED_DATA.put("test", "Test"));
    }
}