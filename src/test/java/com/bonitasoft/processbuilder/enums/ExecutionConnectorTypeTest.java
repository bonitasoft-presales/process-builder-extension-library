package com.bonitasoft.processbuilder.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link ExecutionConnectorType} enumeration.
 * <p>
 * This class ensures that all defined constants are present and that
 * utility methods, such as {@code isValid}, {@code getAllData}, and
 * {@code getAllKeysList}, function correctly across various inputs.
 * </p>
 */
class ExecutionConnectorTypeTest {

    // -------------------------------------------------------------------------
    // Constant Count Test
    // -------------------------------------------------------------------------

    /**
     * Tests that the enum contains exactly two constants.
     */
    @Test
    @DisplayName("Should contain exactly two ExecutionConnector constants")
    void should_contain_two_constants() {
        assertEquals(2, ExecutionConnectorType.values().length);
    }

    // -------------------------------------------------------------------------
    // Constant Existence Tests
    // -------------------------------------------------------------------------

    /**
     * Tests the existence and name of the MAX_RETRIES constant.
     */
    @Test
    @DisplayName("MAX_RETRIES constant should be defined")
    void should_define_MAX_RETRIES_constant() {
        assertEquals("MAX_RETRIES", ExecutionConnectorType.MAX_RETRIES.name());
    }

    /**
     * Tests the existence and name of the WAIT_TIME_SECONDS constant.
     */
    @Test
    @DisplayName("WAIT_TIME_SECONDS constant should be defined")
    void should_define_WAIT_TIME_SECONDS_constant() {
        assertEquals("WAIT_TIME_SECONDS", ExecutionConnectorType.WAIT_TIME_SECONDS.name());
    }

    // -------------------------------------------------------------------------
    // Key and Description Tests
    // -------------------------------------------------------------------------

    /**
     * Tests the key and description of the MAX_RETRIES constant.
     */
    @Test
    @DisplayName("MAX_RETRIES should have correct key and description")
    void maxRetries_should_have_correct_key_and_description() {
        assertEquals("maxRetries", ExecutionConnectorType.MAX_RETRIES.getKey());
        assertTrue(ExecutionConnectorType.MAX_RETRIES.getDescription().contains("Maximum number of retries"));
    }

    /**
     * Tests the key and description of the WAIT_TIME_SECONDS constant.
     */
    @Test
    @DisplayName("WAIT_TIME_SECONDS should have correct key and description")
    void waitTimeSeconds_should_have_correct_key_and_description() {
        assertEquals("waitTimeSeconds", ExecutionConnectorType.WAIT_TIME_SECONDS.getKey());
        assertTrue(ExecutionConnectorType.WAIT_TIME_SECONDS.getDescription().contains("Waiting time in seconds"));
    }

    // -------------------------------------------------------------------------
    // isValid Method Tests
    // -------------------------------------------------------------------------

    /**
     * Tests the {@code isValid} method with a valid constant name (uppercase).
     */
    @Test
    @DisplayName("isValid should return true for a valid uppercase name")
    void isValid_should_return_true_for_valid_uppercase() {
        assertTrue(ExecutionConnectorType.isValid("MAX_RETRIES"));
        assertTrue(ExecutionConnectorType.isValid("WAIT_TIME_SECONDS"));
    }

    /**
     * Tests the {@code isValid} method with a valid constant name (lowercase).
     */
    @Test
    @DisplayName("isValid should return true for a valid lowercase name")
    void isValid_should_return_true_for_valid_lowercase() {
        assertTrue(ExecutionConnectorType.isValid("max_retries"));
        assertTrue(ExecutionConnectorType.isValid("wait_time_seconds"));
    }

    /**
     * Tests the {@code isValid} method with a mixed-case constant name.
     */
    @Test
    @DisplayName("isValid should return true for a mixed-case name")
    void isValid_should_return_true_for_mixed_case() {
        assertTrue(ExecutionConnectorType.isValid("Max_Retries"));
        assertTrue(ExecutionConnectorType.isValid("Wait_Time_Seconds"));
    }

    /**
     * Tests the {@code isValid} method with a non-existent name.
     */
    @Test
    @DisplayName("isValid should return false for an invalid name")
    void isValid_should_return_false_for_invalid_name() {
        assertFalse(ExecutionConnectorType.isValid("NON_EXISTENT_TYPE"));
        assertFalse(ExecutionConnectorType.isValid("INVALID"));
    }

    /**
     * Tests the {@code isValid} method with a null input.
     */
    @Test
    @DisplayName("isValid should return false for null input")
    void isValid_should_return_false_for_null() {
        assertFalse(ExecutionConnectorType.isValid(null));
    }

    /**
     * Tests the {@code isValid} method with an empty string input.
     */
    @Test
    @DisplayName("isValid should return false for an empty string input")
    void isValid_should_return_false_for_empty_string() {
        assertFalse(ExecutionConnectorType.isValid(""));
    }

    /**
     * Tests the {@code isValid} method with whitespace-padded input.
     */
    @Test
    @DisplayName("isValid should return true for whitespace-padded valid name")
    void isValid_should_return_true_for_whitespace_padded_name() {
        assertTrue(ExecutionConnectorType.isValid("  MAX_RETRIES  "));
        assertTrue(ExecutionConnectorType.isValid("\tWAIT_TIME_SECONDS\t"));
    }

    /**
     * Tests the {@code isValid} method with whitespace-only input.
     */
    @Test
    @DisplayName("isValid should return false for whitespace-only input")
    void isValid_should_return_false_for_whitespace_only() {
        assertFalse(ExecutionConnectorType.isValid("   "));
        assertFalse(ExecutionConnectorType.isValid("\t"));
    }

    // -------------------------------------------------------------------------
    // getAllData Method Tests
    // -------------------------------------------------------------------------

    /**
     * Tests that getAllData returns a map with all two constants.
     */
    @Test
    @DisplayName("getAllData should return map with all two constants")
    void getAllData_shouldReturnCorrectMap() {
        Map<String, String> data = ExecutionConnectorType.getAllData();
        assertEquals(2, data.size());
        assertTrue(data.containsKey("maxRetries"));
        assertTrue(data.containsKey("waitTimeSeconds"));
    }

    /**
     * Tests that getAllData returns an unmodifiable map.
     */
    @Test
    @DisplayName("getAllData should return an unmodifiable map")
    void getAllData_shouldReturnUnmodifiableMap() {
        Map<String, String> data = ExecutionConnectorType.getAllData();
        assertThrows(UnsupportedOperationException.class, () -> data.clear());
        assertThrows(UnsupportedOperationException.class, () -> data.put("Test", "Value"));
    }

    // -------------------------------------------------------------------------
    // getAllKeysList Method Tests
    // -------------------------------------------------------------------------

    /**
     * Tests that getAllKeysList returns a list with all two keys.
     */
    @Test
    @DisplayName("getAllKeysList should return list with all two keys")
    void getAllKeysList_shouldReturnCorrectList() {
        List<String> keys = ExecutionConnectorType.getAllKeysList();
        assertEquals(2, keys.size());
        assertTrue(keys.contains("maxRetries"));
        assertTrue(keys.contains("waitTimeSeconds"));
    }

    /**
     * Tests that getAllKeysList returns an unmodifiable list.
     */
    @Test
    @DisplayName("getAllKeysList should return an unmodifiable list")
    void getAllKeysList_shouldReturnUnmodifiableList() {
        List<String> keys = ExecutionConnectorType.getAllKeysList();
        assertThrows(UnsupportedOperationException.class, () -> keys.add("NEW"));
        assertThrows(UnsupportedOperationException.class, () -> keys.remove(0));
    }
}
