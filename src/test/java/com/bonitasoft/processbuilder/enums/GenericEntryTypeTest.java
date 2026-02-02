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

    private static final int EXPECTED_CONSTANT_COUNT = 4;

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
     * Tests the existence and name of the DOCUMENTS_FOLDER constant.
     */
    @Test
    @DisplayName("DOCUMENTS_FOLDER constant should be defined")
    void should_define_DOCUMENTS_FOLDER_constant() {
        assertEquals("DOCUMENTS_FOLDER", GenericEntryType.DOCUMENTS_FOLDER.name());
    }

    /**
     * Tests the existence and name of the REST_APIS constant.
     */
    @Test
    @DisplayName("REST_APIS constant should be defined")
    void should_define_REST_APIS_constant() {
        assertEquals("REST_APIS", GenericEntryType.REST_APIS.name());
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
     * Tests the key and description of the DOCUMENTS_FOLDER constant.
     */
    @Test
    @DisplayName("DOCUMENTS_FOLDER should have correct key and description")
    void documentsFolder_should_have_correct_key_and_description() {
        assertEquals("DocumentsFolder", GenericEntryType.DOCUMENTS_FOLDER.getKey());
        assertTrue(GenericEntryType.DOCUMENTS_FOLDER.getDescription().contains("directory paths"));
    }

    /**
     * Tests the key and description of the REST_APIS constant.
     */
    @Test
    @DisplayName("REST_APIS should have correct key and description")
    void restApis_should_have_correct_key_and_description() {
        assertEquals("RestApis", GenericEntryType.REST_APIS.getKey());
        assertTrue(GenericEntryType.REST_APIS.getDescription().contains("REST API"));
    }

    // -------------------------------------------------------------------------
    // Utility Method Tests
    // -------------------------------------------------------------------------

    /**
     * Tests the {@code isValid} method with a valid constant name (uppercase).
     */
    @Test
    @DisplayName("isValid should return true for a valid uppercase name")
    void isValid_should_return_true_for_valid_uppercase() {
        assertTrue(GenericEntryType.isValid("PROCESS_STORAGE"));
        assertTrue(GenericEntryType.isValid("CRITICALITY"));
        assertTrue(GenericEntryType.isValid("DOCUMENTS_FOLDER"));
        assertTrue(GenericEntryType.isValid("REST_APIS"));
    }

    /**
     * Tests the {@code isValid} method with a valid constant name (lowercase).
     */
    @Test
    @DisplayName("isValid should return true for a valid lowercase name")
    void isValid_should_return_true_for_valid_lowercase() {
        assertTrue(GenericEntryType.isValid("process_storage"));
        assertTrue(GenericEntryType.isValid("criticality"));
        assertTrue(GenericEntryType.isValid("documents_folder"));
        assertTrue(GenericEntryType.isValid("rest_apis"));
    }

    /**
     * Tests the {@code isValid} method with a mixed-case constant name.
     */
    @Test
    @DisplayName("isValid should return true for a mixed-case name")
    void isValid_should_return_true_for_mixed_case() {
        assertTrue(GenericEntryType.isValid("Process_Storage"));
        assertTrue(GenericEntryType.isValid("Criticality"));
        assertTrue(GenericEntryType.isValid("Documents_Folder"));
        assertTrue(GenericEntryType.isValid("Rest_Apis"));
    }

    /**
     * Tests the {@code isValid} method with a non-existent name.
     */
    @Test
    @DisplayName("isValid should return false for an invalid name")
    void isValid_should_return_false_for_invalid_name() {
        assertFalse(GenericEntryType.isValid("NON_EXISTENT_TYPE"));
        assertFalse(GenericEntryType.isValid("INVALID"));
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
     * Tests that getAllData returns a map with all constants.
     */
    @Test
    @DisplayName("getAllData should return map with all constants")
    void getAllData_shouldReturnCorrectMap() {
        Map<String, String> data = GenericEntryType.getAllData();
        assertEquals(EXPECTED_CONSTANT_COUNT, data.size());
        assertTrue(data.containsKey("ProcessStorage"));
        assertTrue(data.containsKey("Criticality"));
        assertTrue(data.containsKey("DocumentsFolder"));
        assertTrue(data.containsKey("RestApis"));
        assertThrows(UnsupportedOperationException.class, () -> data.clear());
    }

    /**
     * Tests that getAllKeysList returns a list with all keys.
     */
    @Test
    @DisplayName("getAllKeysList should return list with all keys")
    void getAllKeysList_shouldReturnCorrectList() {
        List<String> keys = GenericEntryType.getAllKeysList();
        assertEquals(EXPECTED_CONSTANT_COUNT, keys.size());
        assertTrue(keys.contains("ProcessStorage"));
        assertTrue(keys.contains("Criticality"));
        assertTrue(keys.contains("DocumentsFolder"));
        assertTrue(keys.contains("RestApis"));
        assertThrows(UnsupportedOperationException.class, () -> keys.add("NEW"));
    }

    /**
     * Tests that the enum contains exactly two constants.
     */
    @Test
    @DisplayName("Should contain exactly two constants")
    void should_contain_expected_constants() {
        assertEquals(EXPECTED_CONSTANT_COUNT, GenericEntryType.values().length);
    }

    /**
     * Tests the isValid method with whitespace-padded input.
     */
    @Test
    @DisplayName("isValid should return true for whitespace-padded valid name")
    void isValid_should_return_true_for_whitespace_padded_name() {
        assertTrue(GenericEntryType.isValid("  PROCESS_STORAGE  "));
        assertTrue(GenericEntryType.isValid("\tCRITICALITY\t"));
        assertTrue(GenericEntryType.isValid("  DOCUMENTS_FOLDER  "));
        assertTrue(GenericEntryType.isValid("  REST_APIS  "));
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
