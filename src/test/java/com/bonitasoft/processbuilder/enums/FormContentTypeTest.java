package com.bonitasoft.processbuilder.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link FormContentType} enumeration.
 * <p>
 * This class ensures that all defined constants are present and that
 * utility methods, such as {@code isValid}, {@code getAllData}, and
 * {@code getAllKeysList}, function correctly across various inputs.
 * </p>
 *
 * @author Bonitasoft
 * @since 1.0
 */
class FormContentTypeTest {

    // -------------------------------------------------------------------------
    // Constant Count Test
    // -------------------------------------------------------------------------

    /**
     * Tests that the enum contains exactly four constants.
     */
    @Test
    @DisplayName("Should contain exactly four form content type constants")
    void should_contain_four_constants() {
        assertEquals(4, FormContentType.values().length);
    }

    // -------------------------------------------------------------------------
    // Constant Existence Tests
    // -------------------------------------------------------------------------

    /**
     * Tests the existence and name of the NOTIFICATIONS constant.
     */
    @Test
    @DisplayName("NOTIFICATIONS constant should be defined")
    void should_define_NOTIFICATIONS_constant() {
        assertEquals("NOTIFICATIONS", FormContentType.NOTIFICATIONS.name());
    }

    /**
     * Tests the existence and name of the DELAY constant.
     */
    @Test
    @DisplayName("DELAY constant should be defined")
    void should_define_DELAY_constant() {
        assertEquals("DELAY", FormContentType.DELAY.name());
    }

    /**
     * Tests the existence and name of the ALERT constant.
     */
    @Test
    @DisplayName("ALERT constant should be defined")
    void should_define_ALERT_constant() {
        assertEquals("ALERT", FormContentType.ALERT.name());
    }

    /**
     * Tests the existence and name of the MESSAGE constant.
     */
    @Test
    @DisplayName("MESSAGE constant should be defined")
    void should_define_MESSAGE_constant() {
        assertEquals("MESSAGE", FormContentType.MESSAGE.name());
    }

    // -------------------------------------------------------------------------
    // Key and Description Tests
    // -------------------------------------------------------------------------

    /**
     * Tests the key and description of the NOTIFICATIONS constant.
     */
    @Test
    @DisplayName("NOTIFICATIONS should have correct key and description")
    void notifications_should_have_correct_key_and_description() {
        assertEquals("notifications", FormContentType.NOTIFICATIONS.getKey());
        assertTrue(FormContentType.NOTIFICATIONS.getDescription().contains("notification"));
    }

    /**
     * Tests the key and description of the DELAY constant.
     */
    @Test
    @DisplayName("DELAY should have correct key and description")
    void delay_should_have_correct_key_and_description() {
        assertEquals("delay", FormContentType.DELAY.getKey());
        assertTrue(FormContentType.DELAY.getDescription().contains("delay"));
    }

    /**
     * Tests the key and description of the ALERT constant.
     */
    @Test
    @DisplayName("ALERT should have correct key and description")
    void alert_should_have_correct_key_and_description() {
        assertEquals("alert", FormContentType.ALERT.getKey());
        assertTrue(FormContentType.ALERT.getDescription().contains("alert"));
    }

    /**
     * Tests the key and description of the MESSAGE constant.
     */
    @Test
    @DisplayName("MESSAGE should have correct key and description")
    void message_should_have_correct_key_and_description() {
        assertEquals("message", FormContentType.MESSAGE.getKey());
        assertTrue(FormContentType.MESSAGE.getDescription().contains("message"));
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
        assertTrue(FormContentType.isValid("NOTIFICATIONS"));
        assertTrue(FormContentType.isValid("DELAY"));
        assertTrue(FormContentType.isValid("ALERT"));
        assertTrue(FormContentType.isValid("MESSAGE"));
    }

    /**
     * Tests the {@code isValid} method with a valid constant name (lowercase).
     */
    @Test
    @DisplayName("isValid should return true for a valid lowercase name")
    void isValid_should_return_true_for_valid_lowercase() {
        assertTrue(FormContentType.isValid("notifications"));
        assertTrue(FormContentType.isValid("delay"));
        assertTrue(FormContentType.isValid("alert"));
        assertTrue(FormContentType.isValid("message"));
    }

    /**
     * Tests the {@code isValid} method with a mixed-case constant name.
     */
    @Test
    @DisplayName("isValid should return true for a mixed-case name")
    void isValid_should_return_true_for_mixed_case() {
        assertTrue(FormContentType.isValid("Notifications"));
        assertTrue(FormContentType.isValid("DeLaY"));
        assertTrue(FormContentType.isValid("aLeRt"));
        assertTrue(FormContentType.isValid("Message"));
    }

    /**
     * Tests the {@code isValid} method with a non-existent name.
     */
    @Test
    @DisplayName("isValid should return false for an invalid name")
    void isValid_should_return_false_for_invalid_name() {
        assertFalse(FormContentType.isValid("NON_EXISTENT_TYPE"));
        assertFalse(FormContentType.isValid("INVALID"));
        assertFalse(FormContentType.isValid("EMAIL"));
    }

    /**
     * Tests the {@code isValid} method with a null input.
     */
    @Test
    @DisplayName("isValid should return false for null input")
    void isValid_should_return_false_for_null() {
        assertFalse(FormContentType.isValid(null));
    }

    /**
     * Tests the {@code isValid} method with an empty string input.
     */
    @Test
    @DisplayName("isValid should return false for an empty string input")
    void isValid_should_return_false_for_empty_string() {
        assertFalse(FormContentType.isValid(""));
    }

    /**
     * Tests the {@code isValid} method with whitespace-padded input.
     */
    @Test
    @DisplayName("isValid should return true for whitespace-padded valid name")
    void isValid_should_return_true_for_whitespace_padded_name() {
        assertTrue(FormContentType.isValid("  NOTIFICATIONS  "));
        assertTrue(FormContentType.isValid("\tDELAY\t"));
    }

    /**
     * Tests the {@code isValid} method with whitespace-only input.
     */
    @Test
    @DisplayName("isValid should return false for whitespace-only input")
    void isValid_should_return_false_for_whitespace_only() {
        assertFalse(FormContentType.isValid("   "));
        assertFalse(FormContentType.isValid("\t"));
    }

    // -------------------------------------------------------------------------
    // getAllData Method Tests
    // -------------------------------------------------------------------------

    /**
     * Tests that getAllData returns a map with all four constants.
     */
    @Test
    @DisplayName("getAllData should return map with all four constants")
    void getAllData_shouldReturnCorrectMap() {
        Map<String, String> data = FormContentType.getAllData();
        assertEquals(4, data.size());
        assertTrue(data.containsKey("notifications"));
        assertTrue(data.containsKey("delay"));
        assertTrue(data.containsKey("alert"));
        assertTrue(data.containsKey("message"));
    }

    /**
     * Tests that getAllData returns an unmodifiable map.
     */
    @Test
    @DisplayName("getAllData should return an unmodifiable map")
    void getAllData_shouldReturnUnmodifiableMap() {
        Map<String, String> data = FormContentType.getAllData();
        assertThrows(UnsupportedOperationException.class, () -> data.clear());
        assertThrows(UnsupportedOperationException.class, () -> data.put("Test", "Value"));
    }

    /**
     * Tests that getAllData values match the descriptions.
     */
    @Test
    @DisplayName("getAllData values should match enum descriptions")
    void getAllData_valuesShouldMatchDescriptions() {
        Map<String, String> data = FormContentType.getAllData();
        assertEquals(FormContentType.NOTIFICATIONS.getDescription(), data.get("notifications"));
        assertEquals(FormContentType.DELAY.getDescription(), data.get("delay"));
        assertEquals(FormContentType.ALERT.getDescription(), data.get("alert"));
        assertEquals(FormContentType.MESSAGE.getDescription(), data.get("message"));
    }

    // -------------------------------------------------------------------------
    // getAllKeysList Method Tests
    // -------------------------------------------------------------------------

    /**
     * Tests that getAllKeysList returns a list with all four keys.
     */
    @Test
    @DisplayName("getAllKeysList should return list with all four keys")
    void getAllKeysList_shouldReturnCorrectList() {
        List<String> keys = FormContentType.getAllKeysList();
        assertEquals(4, keys.size());
        assertTrue(keys.contains("notifications"));
        assertTrue(keys.contains("delay"));
        assertTrue(keys.contains("alert"));
        assertTrue(keys.contains("message"));
    }

    /**
     * Tests that getAllKeysList returns an unmodifiable list.
     */
    @Test
    @DisplayName("getAllKeysList should return an unmodifiable list")
    void getAllKeysList_shouldReturnUnmodifiableList() {
        List<String> keys = FormContentType.getAllKeysList();
        assertThrows(UnsupportedOperationException.class, () -> keys.add("NEW"));
        assertThrows(UnsupportedOperationException.class, () -> keys.remove(0));
    }

    /**
     * Tests that getAllKeysList preserves declaration order.
     */
    @Test
    @DisplayName("getAllKeysList should preserve enum declaration order")
    void getAllKeysList_shouldPreserveOrder() {
        List<String> keys = FormContentType.getAllKeysList();
        assertEquals("notifications", keys.get(0));
        assertEquals("delay", keys.get(1));
        assertEquals("alert", keys.get(2));
        assertEquals("message", keys.get(3));
    }

    // -------------------------------------------------------------------------
    // Ordinal Tests
    // -------------------------------------------------------------------------

    /**
     * Tests that ordinal values are correct for all constants.
     */
    @Test
    @DisplayName("Enum ordinals should be in declaration order")
    void ordinals_should_be_in_declaration_order() {
        assertEquals(0, FormContentType.NOTIFICATIONS.ordinal());
        assertEquals(1, FormContentType.DELAY.ordinal());
        assertEquals(2, FormContentType.ALERT.ordinal());
        assertEquals(3, FormContentType.MESSAGE.ordinal());
    }

    // -------------------------------------------------------------------------
    // Additional Tests for Mutation Coverage
    // -------------------------------------------------------------------------

    /**
     * Tests that null input is handled safely and returns false.
     * This specifically tests the null check in isValid method.
     */
    @Test
    @DisplayName("isValid should safely handle null without NullPointerException")
    void isValid_should_safely_handle_null_without_exception() {
        boolean result = assertDoesNotThrow(
                () -> FormContentType.isValid(null),
                "Null input should not cause NullPointerException"
        );

        assertFalse(result, "Result should be false for null input");
    }

    /**
     * Tests that null check is evaluated before isEmpty() call.
     * Verifies short-circuit evaluation of || is critical for null safety.
     */
    @Test
    @DisplayName("isValid should evaluate null check before isEmpty")
    void isValid_null_check_must_precede_isEmpty_call() {
        // Both paths should return false but through different code paths
        boolean nullResult = assertDoesNotThrow(() -> FormContentType.isValid(null));
        boolean emptyResult = assertDoesNotThrow(() -> FormContentType.isValid(""));

        assertFalse(nullResult, "Null input should return false");
        assertFalse(emptyResult, "Empty input should return false");
    }

    /**
     * Tests that each individual constant is valid.
     */
    @Test
    @DisplayName("Each individual enum constant should be valid")
    void each_individual_constant_should_be_valid() {
        assertTrue(FormContentType.isValid("NOTIFICATIONS"), "NOTIFICATIONS should be valid");
        assertTrue(FormContentType.isValid("DELAY"), "DELAY should be valid");
        assertTrue(FormContentType.isValid("ALERT"), "ALERT should be valid");
        assertTrue(FormContentType.isValid("MESSAGE"), "MESSAGE should be valid");
    }

    /**
     * Tests getAllData values are correctly mapped for each constant.
     */
    @Test
    @DisplayName("getAllData should correctly map each key to its description")
    void getAllData_should_map_each_key_to_description() {
        Map<String, String> data = FormContentType.getAllData();

        // Verify each key maps to its description
        for (FormContentType type : FormContentType.values()) {
            assertEquals(type.getDescription(), data.get(type.getKey()),
                    "Key " + type.getKey() + " should map to its description");
        }
    }
}
