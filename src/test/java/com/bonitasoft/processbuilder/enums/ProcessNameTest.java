package com.bonitasoft.processbuilder.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link ProcessName} enumeration, verifying 
 * constant names and the {@code getAllProcessData} utility method.
 */
class ProcessNameTest {

    private static final Map<String, String> EXPECTED_DATA;

    static {
        EXPECTED_DATA = ProcessName.getAllProcessData();
    }

    @Test
    @DisplayName("Should contain exactly three process name constants")
    void should_contain_three_constants() {
        assertEquals(3, ProcessName.values().length);
        assertEquals(3, EXPECTED_DATA.size());
    }

    @Test
    @DisplayName("FORM constant should have the correct key and description")
    void form_should_have_correct_values() {
        assertEquals("Form", ProcessName.FORM.getKey());
        assertTrue(ProcessName.FORM.getDescription().startsWith("Process for form configuration"));
    }

    @Test
    @DisplayName("NOTIFICATIONS constant should have the correct key and description")
    void notifications_should_have_correct_values() {
        assertEquals("Notifications", ProcessName.NOTIFICATIONS.getKey());
        assertTrue(ProcessName.NOTIFICATIONS.getDescription().contains("notifications configuration settings."));
    }

    @Test
    @DisplayName("getAllProcessData should return an unmodifiable map with all required keys")
    void getAllProcessData_should_return_correct_map() {
        assertTrue(EXPECTED_DATA.containsKey("Form"));
        assertTrue(EXPECTED_DATA.containsKey("Notifications"));
        assertTrue(EXPECTED_DATA.containsKey("Redirections"));

        assertThrows(UnsupportedOperationException.class, () -> EXPECTED_DATA.put("TestProcess", "Test"));
    }
}