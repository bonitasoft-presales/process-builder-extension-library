package com.bonitasoft.processbuilder.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link RecipientsType} enumeration.
 * <p>
 * This class ensures that all defined constants are present and that
 * utility methods, such as {@code isValid}, {@code fromKey}, {@code getAllData}, and
 * {@code getAllKeysList}, function correctly across various inputs.
 * </p>
 */
class RecipientsTypeTest {

    // -------------------------------------------------------------------------
    // Constant Count Test
    // -------------------------------------------------------------------------

    /**
     * Tests that the enum contains exactly five constants.
     */
    @Test
    @DisplayName("Should contain exactly five recipient type constants")
    void should_contain_five_constants() {
        assertEquals(5, RecipientsType.values().length);
    }

    // -------------------------------------------------------------------------
    // Constant Existence Tests
    // -------------------------------------------------------------------------

    /**
     * Tests the existence and name of the MEMBERSHIP constant.
     */
    @Test
    @DisplayName("MEMBERSHIP constant should be defined")
    void should_define_MEMBERSHIP_constant() {
        assertEquals("MEMBERSHIP", RecipientsType.MEMBERSHIP.name());
    }

    /**
     * Tests the existence and name of the USERS constant.
     */
    @Test
    @DisplayName("USERS constant should be defined")
    void should_define_USERS_constant() {
        assertEquals("USERS", RecipientsType.USERS.name());
    }

    /**
     * Tests the existence and name of the STEP_USERS constant.
     */
    @Test
    @DisplayName("STEP_USERS constant should be defined")
    void should_define_STEP_USERS_constant() {
        assertEquals("STEP_USERS", RecipientsType.STEP_USERS.name());
    }

    /**
     * Tests the existence and name of the STEP_MANAGERS constant.
     */
    @Test
    @DisplayName("STEP_MANAGERS constant should be defined")
    void should_define_STEP_MANAGERS_constant() {
        assertEquals("STEP_MANAGERS", RecipientsType.STEP_MANAGERS.name());
    }

    /**
     * Tests the existence and name of the SPECIFIC constant.
     */
    @Test
    @DisplayName("SPECIFIC constant should be defined")
    void should_define_SPECIFIC_constant() {
        assertEquals("SPECIFIC", RecipientsType.SPECIFIC.name());
    }

    // -------------------------------------------------------------------------
    // Key and Description Tests
    // -------------------------------------------------------------------------

    /**
     * Tests the key and description of the MEMBERSHIP constant.
     */
    @Test
    @DisplayName("MEMBERSHIP should have correct key and description")
    void membership_should_have_correct_key_and_description() {
        assertEquals("membership", RecipientsType.MEMBERSHIP.getKey());
        assertTrue(RecipientsType.MEMBERSHIP.getDescription().contains("membership"));
    }

    /**
     * Tests the key and description of the USERS constant.
     */
    @Test
    @DisplayName("USERS should have correct key and description")
    void users_should_have_correct_key_and_description() {
        assertEquals("users", RecipientsType.USERS.getKey());
        assertTrue(RecipientsType.USERS.getDescription().contains("user"));
    }

    /**
     * Tests the key and description of the STEP_USERS constant.
     */
    @Test
    @DisplayName("STEP_USERS should have correct key and description")
    void stepUsers_should_have_correct_key_and_description() {
        assertEquals("step_users", RecipientsType.STEP_USERS.getKey());
        assertTrue(RecipientsType.STEP_USERS.getDescription().contains("step"));
    }

    /**
     * Tests the key and description of the STEP_MANAGERS constant.
     */
    @Test
    @DisplayName("STEP_MANAGERS should have correct key and description")
    void stepManagers_should_have_correct_key_and_description() {
        assertEquals("step_managers", RecipientsType.STEP_MANAGERS.getKey());
        assertTrue(RecipientsType.STEP_MANAGERS.getDescription().contains("managers"));
    }

    /**
     * Tests the key and description of the SPECIFIC constant.
     */
    @Test
    @DisplayName("SPECIFIC should have correct key and description")
    void specific_should_have_correct_key_and_description() {
        assertEquals("specific", RecipientsType.SPECIFIC.getKey());
        assertTrue(RecipientsType.SPECIFIC.getDescription().contains("email"));
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
        assertTrue(RecipientsType.isValid("MEMBERSHIP"));
        assertTrue(RecipientsType.isValid("USERS"));
        assertTrue(RecipientsType.isValid("STEP_USERS"));
    }

    /**
     * Tests the {@code isValid} method with a valid constant name (lowercase).
     */
    @Test
    @DisplayName("isValid should return true for a valid lowercase name")
    void isValid_should_return_true_for_valid_lowercase() {
        assertTrue(RecipientsType.isValid("step_managers"));
        assertTrue(RecipientsType.isValid("specific"));
    }

    /**
     * Tests the {@code isValid} method with a mixed-case constant name.
     */
    @Test
    @DisplayName("isValid should return true for a mixed-case name")
    void isValid_should_return_true_for_mixed_case() {
        assertTrue(RecipientsType.isValid("Step_Users"));
        assertTrue(RecipientsType.isValid("Membership"));
    }

    /**
     * Tests the {@code isValid} method with a non-existent name.
     */
    @Test
    @DisplayName("isValid should return false for an invalid name")
    void isValid_should_return_false_for_invalid_name() {
        assertFalse(RecipientsType.isValid("NON_EXISTENT_TYPE"));
        assertFalse(RecipientsType.isValid("INVALID"));
    }

    /**
     * Tests the {@code isValid} method with a null input.
     */
    @Test
    @DisplayName("isValid should return false for null input")
    void isValid_should_return_false_for_null() {
        assertFalse(RecipientsType.isValid(null));
    }

    /**
     * Tests the {@code isValid} method with an empty string input.
     */
    @Test
    @DisplayName("isValid should return false for an empty string input")
    void isValid_should_return_false_for_empty_string() {
        assertFalse(RecipientsType.isValid(""));
    }

    /**
     * Tests the {@code isValid} method with whitespace-padded input.
     */
    @Test
    @DisplayName("isValid should return true for whitespace-padded valid name")
    void isValid_should_return_true_for_whitespace_padded_name() {
        assertTrue(RecipientsType.isValid("  MEMBERSHIP  "));
        assertTrue(RecipientsType.isValid("\tUSERS\t"));
    }

    /**
     * Tests the {@code isValid} method with whitespace-only input.
     */
    @Test
    @DisplayName("isValid should return false for whitespace-only input")
    void isValid_should_return_false_for_whitespace_only() {
        assertFalse(RecipientsType.isValid("   "));
        assertFalse(RecipientsType.isValid("\t"));
    }

    // -------------------------------------------------------------------------
    // fromKey Method Tests
    // -------------------------------------------------------------------------

    /**
     * Tests the {@code fromKey} method with valid keys.
     */
    @Test
    @DisplayName("fromKey should return correct type for valid key")
    void fromKey_should_return_correct_type_for_valid_key() {
        assertEquals(RecipientsType.MEMBERSHIP, RecipientsType.fromKey("membership"));
        assertEquals(RecipientsType.USERS, RecipientsType.fromKey("users"));
        assertEquals(RecipientsType.STEP_USERS, RecipientsType.fromKey("step_users"));
        assertEquals(RecipientsType.STEP_MANAGERS, RecipientsType.fromKey("step_managers"));
        assertEquals(RecipientsType.SPECIFIC, RecipientsType.fromKey("specific"));
    }

    /**
     * Tests the {@code fromKey} method with case-insensitive keys.
     */
    @Test
    @DisplayName("fromKey should be case-insensitive")
    void fromKey_should_be_case_insensitive() {
        assertEquals(RecipientsType.STEP_USERS, RecipientsType.fromKey("STEP_USERS"));
        assertEquals(RecipientsType.STEP_USERS, RecipientsType.fromKey("Step_Users"));
    }

    /**
     * Tests the {@code fromKey} method with whitespace-padded keys.
     */
    @Test
    @DisplayName("fromKey should trim whitespace from keys")
    void fromKey_should_trim_whitespace() {
        assertEquals(RecipientsType.MEMBERSHIP, RecipientsType.fromKey("  membership  "));
        assertEquals(RecipientsType.USERS, RecipientsType.fromKey("\tusers\t"));
    }

    /**
     * Tests the {@code fromKey} method with invalid key.
     */
    @Test
    @DisplayName("fromKey should return null for invalid key")
    void fromKey_should_return_null_for_invalid_key() {
        assertNull(RecipientsType.fromKey("invalid_key"));
        assertNull(RecipientsType.fromKey("unknown"));
    }

    /**
     * Tests the {@code fromKey} method with null input.
     */
    @Test
    @DisplayName("fromKey should return null for null input")
    void fromKey_should_return_null_for_null() {
        assertNull(RecipientsType.fromKey(null));
    }

    /**
     * Tests the {@code fromKey} method with empty string input.
     */
    @Test
    @DisplayName("fromKey should return null for empty string input")
    void fromKey_should_return_null_for_empty_string() {
        assertNull(RecipientsType.fromKey(""));
    }

    /**
     * Tests the {@code fromKey} method with whitespace-only input.
     */
    @Test
    @DisplayName("fromKey should return null for whitespace-only input")
    void fromKey_should_return_null_for_whitespace_only() {
        assertNull(RecipientsType.fromKey("   "));
    }

    // -------------------------------------------------------------------------
    // getAllData Method Tests
    // -------------------------------------------------------------------------

    /**
     * Tests that getAllData returns a map with all five constants.
     */
    @Test
    @DisplayName("getAllData should return map with all five constants")
    void getAllData_shouldReturnCorrectMap() {
        Map<String, String> data = RecipientsType.getAllData();
        assertEquals(5, data.size());
        assertTrue(data.containsKey("membership"));
        assertTrue(data.containsKey("users"));
        assertTrue(data.containsKey("step_users"));
        assertTrue(data.containsKey("step_managers"));
        assertTrue(data.containsKey("specific"));
    }

    /**
     * Tests that getAllData returns an unmodifiable map.
     */
    @Test
    @DisplayName("getAllData should return an unmodifiable map")
    void getAllData_shouldReturnUnmodifiableMap() {
        Map<String, String> data = RecipientsType.getAllData();
        assertThrows(UnsupportedOperationException.class, () -> data.clear());
        assertThrows(UnsupportedOperationException.class, () -> data.put("Test", "Value"));
    }

    // -------------------------------------------------------------------------
    // getAllKeysList Method Tests
    // -------------------------------------------------------------------------

    /**
     * Tests that getAllKeysList returns a list with all five keys.
     */
    @Test
    @DisplayName("getAllKeysList should return list with all five keys")
    void getAllKeysList_shouldReturnCorrectList() {
        List<String> keys = RecipientsType.getAllKeysList();
        assertEquals(5, keys.size());
        assertTrue(keys.contains("membership"));
        assertTrue(keys.contains("users"));
        assertTrue(keys.contains("step_users"));
        assertTrue(keys.contains("step_managers"));
        assertTrue(keys.contains("specific"));
    }

    /**
     * Tests that getAllKeysList returns an unmodifiable list.
     */
    @Test
    @DisplayName("getAllKeysList should return an unmodifiable list")
    void getAllKeysList_shouldReturnUnmodifiableList() {
        List<String> keys = RecipientsType.getAllKeysList();
        assertThrows(UnsupportedOperationException.class, () -> keys.add("NEW"));
        assertThrows(UnsupportedOperationException.class, () -> keys.remove(0));
    }
}
