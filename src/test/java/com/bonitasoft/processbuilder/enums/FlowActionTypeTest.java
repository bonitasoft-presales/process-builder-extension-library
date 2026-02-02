package com.bonitasoft.processbuilder.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link FlowActionType} enumeration, verifying 
 * constant values and the {@code getAllActionData} utility method.
 */
class FlowActionTypeTest {

    private static final Map<String, String> EXPECTED_DATA;

    static {
        EXPECTED_DATA = FlowActionType.getAllData();
    }

    @Test
    @DisplayName("Should contain exactly four flow action constants")
    void should_contain_four_constants() {
        assertEquals(4, FlowActionType.values().length);
        assertEquals(4, EXPECTED_DATA.size());
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
    @DisplayName("REST_APIS constant should have the correct key and description")
    void restApis_should_have_correct_values() {
        assertEquals("restApis", FlowActionType.REST_APIS.getKey());
        assertTrue(FlowActionType.REST_APIS.getDescription().contains("REST"));
    }

    @Test
    @DisplayName("getAllData should return an unmodifiable map with all required keys")
    void getAllData_should_return_correct_map() {
        assertTrue(EXPECTED_DATA.containsKey("form"));
        assertTrue(EXPECTED_DATA.containsKey("notifications"));
        assertTrue(EXPECTED_DATA.containsKey("redirections"));
        assertTrue(EXPECTED_DATA.containsKey("restApis"));

        assertThrows(UnsupportedOperationException.class, () -> EXPECTED_DATA.put("test", "Test"));
    }

    @Test
    void getAllData_shouldReturnCorrectMap() {
        Map<String, String> data = FlowActionType.getAllData();
        assertEquals(4, data.size());
        assertTrue(data.containsKey("notifications"));
        assertTrue(data.containsKey("restApis"));
        assertThrows(UnsupportedOperationException.class, () -> data.clear());
    }

    @Test
    void getAllKeysList_shouldReturnCorrectList() {
        List<String> keys = FlowActionType.getAllKeysList();
        assertEquals(4, keys.size());
        assertTrue(keys.contains("redirections"));
        assertTrue(keys.contains("restApis"));
        assertThrows(UnsupportedOperationException.class, () -> keys.add("NEW"));
    }
}