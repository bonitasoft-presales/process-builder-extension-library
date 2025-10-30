package com.bonitasoft.processbuilder.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link CriticalityType} enumeration, verifying 
 * constant values and the {@code getAllCriticalityData} utility method.
 */
class CriticalityTypeTest {

    private static final Map<String, String> EXPECTED_DATA;

    static {
        EXPECTED_DATA = CriticalityType.getAllData();
    }

    @Test
    @DisplayName("Should contain exactly four criticality constants")
    void should_contain_four_constants() {
        assertEquals(4, CriticalityType.values().length);
        assertEquals(4, EXPECTED_DATA.size());
    }

    @Test
    @DisplayName("HIGH constant should have the correct key and description")
    void high_should_have_correct_values() {
        assertEquals("High", CriticalityType.HIGH.getKey());
        assertTrue(CriticalityType.HIGH.getDescription().contains("severe business impact"));
    }

    @Test
    @DisplayName("NONE constant should have the correct key and description")
    void none_should_have_correct_values() {
        assertEquals("None", CriticalityType.NONE.getKey());
        assertTrue(CriticalityType.NONE.getDescription().contains("non-critical process with no business impact on failure."));
    }

    @Test
    @DisplayName("getAllCriticalityData should return an unmodifiable map with all required keys")
    void getAllCriticalityData_should_return_correct_map() {
        assertTrue(EXPECTED_DATA.containsKey("High"));
        assertTrue(EXPECTED_DATA.containsKey("Moderate"));
        assertTrue(EXPECTED_DATA.containsKey("Low"));
        assertTrue(EXPECTED_DATA.containsKey("None"));

        assertThrows(UnsupportedOperationException.class, () -> EXPECTED_DATA.put("Test", "Test"));
    }

    @Test
    void getAllData_shouldReturnCorrectMap() {
        Map<String, String> data = CriticalityType.getAllData();
        assertEquals(4, data.size());
        assertTrue(data.containsKey("High"));
        assertThrows(UnsupportedOperationException.class, () -> data.clear());
    }

    @Test
    void getAllKeysList_shouldReturnCorrectList() {
        List<String> keys = CriticalityType.getAllKeysList();
        assertEquals(4, keys.size());
        assertTrue(keys.contains("Low"));
        assertThrows(UnsupportedOperationException.class, () -> keys.add("NEW"));
    }
}