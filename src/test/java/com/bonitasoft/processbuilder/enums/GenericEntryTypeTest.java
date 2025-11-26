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

    /**
     * Tests the existence and name of the SMTP constant.
     */
    @Test
    @DisplayName("SMTP constant should be defined")
    void should_define_SMTP_constant() {
        assertEquals("SMTP", GenericEntryType.SMTP.name());
    }

    /**
     * Tests the existence and name of the THEME constant.
     */
    @Test
    @DisplayName("THEME constant should be defined")
    void should_define_THEME_constant() {
        assertEquals("THEME", GenericEntryType.THEME.name());
    }

    // -------------------------------------------------------------------------
    // Key and Description Tests
    // -------------------------------------------------------------------------

    /**
     * Tests the key and description of the PROCESS_STORAGE constant.
     */
    @Test
    @DisplayName("PROCESS_STORAGE should have correct key and description")
    void processStorage_should_have_correct_key_and_description() {
        assertEquals("ProcessStorage", GenericEntryType.PROCESS_STORAGE.getKey());
        assertTrue(GenericEntryType.PROCESS_STORAGE.getDescription().contains("storage location"));
    }

    /**
     * Tests the key and description of the CRITICALITY constant.
     */
    @Test
    @DisplayName("CRITICALITY should have correct key and description")
    void criticality_should_have_correct_key_and_description() {
        assertEquals("Criticality", GenericEntryType.CRITICALITY.getKey());
        assertTrue(GenericEntryType.CRITICALITY.getDescription().contains("priority level"));
    }

    /**
     * Tests the key and description of the SMTP constant.
     */
    @Test
    @DisplayName("SMTP should have correct key and description")
    void smtp_should_have_correct_key_and_description() {
        assertEquals("Smtp", GenericEntryType.SMTP.getKey());
        assertTrue(GenericEntryType.SMTP.getDescription().contains("SMTP server"));
    }

    /**
     * Tests the key and description of the THEME constant.
     */
    @Test
    @DisplayName("THEME should have correct key and description")
    void theme_should_have_correct_key_and_description() {
        assertEquals("Theme", GenericEntryType.THEME.getKey());
        assertTrue(GenericEntryType.THEME.getDescription().contains("visual theme"));
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

    /**
     * Tests that getAllData returns a map with all four constants.
     */
    @Test
    @DisplayName("getAllData should return map with all four constants")
    void getAllData_shouldReturnCorrectMap() {
        Map<String, String> data = GenericEntryType.getAllData();
        assertEquals(4, data.size());
        assertTrue(data.containsKey("ProcessStorage"));
        assertTrue(data.containsKey("Criticality"));
        assertTrue(data.containsKey("Smtp"));
        assertTrue(data.containsKey("Theme"));
        assertThrows(UnsupportedOperationException.class, () -> data.clear());
    }

    /**
     * Tests that getAllKeysList returns a list with all four keys.
     */
    @Test
    @DisplayName("getAllKeysList should return list with all four keys")
    void getAllKeysList_shouldReturnCorrectList() {
        List<String> keys = GenericEntryType.getAllKeysList();
        assertEquals(4, keys.size());
        assertTrue(keys.contains("ProcessStorage"));
        assertTrue(keys.contains("Criticality"));
        assertTrue(keys.contains("Smtp"));
        assertTrue(keys.contains("Theme"));
        assertThrows(UnsupportedOperationException.class, () -> keys.add("NEW"));
    }

    /**
     * Tests that the enum contains exactly four constants.
     */
    @Test
    @DisplayName("Should contain exactly four constants")
    void should_contain_four_constants() {
        assertEquals(4, GenericEntryType.values().length);
    }

    /**
     * Tests the isValid method with whitespace-padded input.
     */
    @Test
    @DisplayName("isValid should return true for whitespace-padded valid name")
    void isValid_should_return_true_for_whitespace_padded_name() {
        assertTrue(GenericEntryType.isValid("  SMTP  "));
        assertTrue(GenericEntryType.isValid("\tTHEME\t"));
    }

    /**
     * Tests the isValid method with whitespace-only input.
     */
    @Test
    @DisplayName("isValid should return false for whitespace-only input")
    void isValid_should_return_false_for_whitespace_only() {
        assertFalse(GenericEntryType.isValid("   "));
        assertFalse(GenericEntryType.isValid("\t"));
    }
}