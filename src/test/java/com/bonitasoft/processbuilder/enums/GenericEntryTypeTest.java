package com.bonitasoft.processbuilder.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;

/**
 * Unit tests for the {@link GenericEntryType} enumeration.
 * <p>
 * This class ensures that all defined constants are present and that 
 * utility methods, such as {@code isValid}, function correctly across 
 * various inputs including case sensitivity and null values.
 * </p>
 */
class GenericEntryTypeTest {

    // -------------------------------------------------------------------------
    // Constant Existence Tests
    // -------------------------------------------------------------------------

    /**
     * Tests the existence and name of the PROCESS_STORAGE constant.
     */
    @Test
    @DisplayName("PROCESS_STORAGE constant should be defined")
    void should_define_PROCESS_STORAGE_constant() {
        assertEquals("PROCESS_STORAGE", GenericEntryType.PROCESS_STORAGE.name());
    }

    /**
     * Tests the existence and name of the CRITICALITY constant.
     */
    @Test
    @DisplayName("CRITICALITY constant should be defined")
    void should_define_CRITICALITY_constant() {
        assertEquals("CRITICALITY", GenericEntryType.CRITICALITY.name());
    }

    // -------------------------------------------------------------------------
    // Utility Method Tests (Assuming the isValid method is included)
    // -------------------------------------------------------------------------

    /**
     * Tests the {@code isValid} method with a valid constant name (uppercase).
     */
    @Test
    @DisplayName("isValid should return true for a valid uppercase name")
    void isValid_should_return_true_for_valid_uppercase() {
        assertTrue(GenericEntryType.isValid("PROCESS_STORAGE"));
    }

    /**
     * Tests the {@code isValid} method with a valid constant name (lowercase, checking case insensitivity).
     */
    @Test
    @DisplayName("isValid should return true for a valid lowercase name")
    void isValid_should_return_true_for_valid_lowercase() {
        assertTrue(GenericEntryType.isValid("criticality"));
    }

    /**
     * Tests the {@code isValid} method with a mixed-case constant name.
     */
    @Test
    @DisplayName("isValid should return true for a mixed-case name")
    void isValid_should_return_true_for_mixed_case() {
        assertTrue(GenericEntryType.isValid("Process_Storage"));
    }

    /**
     * Tests the {@code isValid} method with a non-existent name.
     */
    @Test
    @DisplayName("isValid should return false for an invalid name")
    void isValid_should_return_false_for_invalid_name() {
        assertFalse(GenericEntryType.isValid("NON_EXISTENT_TYPE"));
    }

    /**
     * Tests the {@code isValid} method with a null input.
     */
    @Test
    @DisplayName("isValid should return false for null input")
    void isValid_should_return_false_for_null() {
        assertFalse(GenericEntryType.isValid(null));
    }

    /**
     * Tests the {@code isValid} method with an empty string input.
     */
    @Test
    @DisplayName("isValid should return false for an empty string input")
    void isValid_should_return_false_for_empty_string() {
        assertFalse(GenericEntryType.isValid(""));
    }

    @Test
    void getAllData_shouldReturnCorrectMap() {
        Map<String, String> data = GenericEntryType.getAllData();
        assertEquals(2, data.size());
        assertTrue(data.containsKey("ProcessStorage"));
        assertThrows(UnsupportedOperationException.class, () -> data.clear());
    }

    @Test
    void getAllKeysList_shouldReturnCorrectList() {
        List<String> keys = GenericEntryType.getAllKeysList();
        assertEquals(2, keys.size());
        assertTrue(keys.contains("Criticality"));
        assertThrows(UnsupportedOperationException.class, () -> keys.add("NEW"));
    }
}