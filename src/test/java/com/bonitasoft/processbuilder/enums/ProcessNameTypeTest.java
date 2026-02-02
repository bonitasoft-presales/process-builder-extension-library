package com.bonitasoft.processbuilder.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link ProcessNameType} enumeration, verifying 
 * constant names and the {@code getAllData} utility method.
 */
class ProcessNameTypeTest {

    private static final Map<String, String> EXPECTED_DATA;

    static {
        EXPECTED_DATA = ProcessNameType.getAllData();
    }

    @Test
    @DisplayName("Should contain exactly four process name constants")
    void should_contain_four_constants() {
        assertEquals(4, ProcessNameType.values().length);
        assertEquals(4, EXPECTED_DATA.size());
    }

    @Test
    @DisplayName("FORM constant should have the correct key and description")
    void form_should_have_correct_values() {
        assertEquals("Form", ProcessNameType.FORM.getKey());
        assertTrue(ProcessNameType.FORM.getDescription().startsWith("Process for form configuration"));
    }

    @Test
    @DisplayName("NOTIFICATIONS constant should have the correct key and description")
    void notifications_should_have_correct_values() {
        assertEquals("Notifications", ProcessNameType.NOTIFICATIONS.getKey());
        assertTrue(ProcessNameType.NOTIFICATIONS.getDescription().contains("notifications configuration settings."));
    }

    @Test
    @DisplayName("REST_APIS constant should have the correct key and description")
    void restApis_should_have_correct_values() {
        assertEquals("RestApis", ProcessNameType.REST_APIS.getKey());
        assertTrue(ProcessNameType.REST_APIS.getDescription().contains("REST API"));
    }

    @Test
    @DisplayName("getAllProcessData should return an unmodifiable map with all required keys")
    void getAllProcessData_should_return_correct_map() {
        assertTrue(EXPECTED_DATA.containsKey("Form"));
        assertTrue(EXPECTED_DATA.containsKey("Notifications"));
        assertTrue(EXPECTED_DATA.containsKey("Redirections"));
        assertTrue(EXPECTED_DATA.containsKey("RestApis"));

        assertThrows(UnsupportedOperationException.class, () -> EXPECTED_DATA.put("TestProcess", "Test"));
    }

    @Test
    void getAllData_shouldReturnCorrectMap() {
        Map<String, String> data = ProcessNameType.getAllData();
        assertEquals(4, data.size());
        assertTrue(data.containsKey("Form"));
        assertTrue(data.containsKey("RestApis"));
        assertThrows(UnsupportedOperationException.class, () -> data.clear());
    }

    @Test
    void getAllKeysList_shouldReturnCorrectList() {
        List<String> keys = ProcessNameType.getAllKeysList();
        assertEquals(4, keys.size());
        assertTrue(keys.contains("Notifications"));
        assertTrue(keys.contains("RestApis"));
        assertThrows(UnsupportedOperationException.class, () -> keys.add("NEW"));
    }
}