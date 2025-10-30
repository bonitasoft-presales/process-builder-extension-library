package com.bonitasoft.processbuilder.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link ProcessStorageType} enumeration, verifying 
 * constant values and the {@code getAllData} utility method.
 */
class ProcessStorageTypeTest {

    private static final Map<String, String> EXPECTED_DATA;

    static {
        EXPECTED_DATA = ProcessStorageType.getAllData();
    }

    @Test
    @DisplayName("Should contain exactly four storage constants")
    void should_contain_four_constants() {
        assertEquals(4, ProcessStorageType.values().length);
        assertEquals(4, EXPECTED_DATA.size());
    }

    @Test
    @DisplayName("LOCAL constant should have the correct key and description")
    void local_should_have_correct_values() {
        assertEquals("Local", ProcessStorageType.LOCAL.getKey());
        assertTrue(ProcessStorageType.LOCAL.getDescription().startsWith("Files are stored on the local application server where the process engine is deployed"));
    }

    @Test
    @DisplayName("BONITA_AND_DELETE constant should have the correct key and description")
    void bonita_and_delete_should_have_correct_values() {
        assertEquals("Bonita and delete", ProcessStorageType.BONITA_AND_DELETE.getKey());
        assertTrue(ProcessStorageType.BONITA_AND_DELETE.getDescription().contains("BDM database and are deleted upon process completion."));
    }

    @Test
    @DisplayName("getAllStorageData should return an unmodifiable map with all required keys")
    void getAllData_should_return_correct_map() {
        assertTrue(EXPECTED_DATA.containsKey("Local"));
        assertTrue(EXPECTED_DATA.containsKey("Local and delete"));
        assertTrue(EXPECTED_DATA.containsKey("Bonita"));
        assertTrue(EXPECTED_DATA.containsKey("Bonita and delete"));

        assertThrows(UnsupportedOperationException.class, () -> EXPECTED_DATA.put("Test", "Test"));
    }

    @Test
    void getAllData_shouldReturnCorrectMap() {
        Map<String, String> data = ProcessStorageType.getAllData();
        assertEquals(4, data.size());
        assertTrue(data.containsKey("Local"));
        assertThrows(UnsupportedOperationException.class, () -> data.clear());
    }

    @Test
    void getAllKeysList_shouldReturnCorrectList() {
        List<String> keys = ProcessStorageType.getAllKeysList();
        assertEquals(4, keys.size());
        assertTrue(keys.contains("Bonita"));
        assertThrows(UnsupportedOperationException.class, () -> keys.add("NEW"));
    }
}