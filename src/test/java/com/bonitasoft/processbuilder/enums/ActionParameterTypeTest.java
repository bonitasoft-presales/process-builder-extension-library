package com.bonitasoft.processbuilder.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link ActionParameterType} enumeration.
 * <p>
 * This class ensures that all defined constants are present and that
 * utility methods, such as {@code isValid}, {@code getAllData}, and
 * {@code getAllKeysList}, function correctly across various inputs.
 * </p>
 */
class ActionParameterTypeTest {

    // -------------------------------------------------------------------------
    // Constant Count Test
    // -------------------------------------------------------------------------

    /**
     * Tests that the enum contains exactly fourteen constants.
     */
    @Test
    @DisplayName("Should contain exactly fourteen action parameter constants")
    void should_contain_fourteen_constants() {
        assertEquals(14, ActionParameterType.values().length);
    }

    // -------------------------------------------------------------------------
    // Constant Existence Tests
    // -------------------------------------------------------------------------

    /**
     * Tests the existence and name of the NAME constant.
     */
    @Test
    @DisplayName("NAME constant should be defined")
    void should_define_NAME_constant() {
        assertEquals("NAME", ActionParameterType.NAME.name());
    }

    /**
     * Tests the existence and name of the TARGET_STEP constant.
     */
    @Test
    @DisplayName("TARGET_STEP constant should be defined")
    void should_define_TARGET_STEP_constant() {
        assertEquals("TARGET_STEP", ActionParameterType.TARGET_STEP.name());
    }

    /**
     * Tests the existence and name of the RECIPIENTS constant.
     */
    @Test
    @DisplayName("RECIPIENTS constant should be defined")
    void should_define_RECIPIENTS_constant() {
        assertEquals("RECIPIENTS", ActionParameterType.RECIPIENTS.name());
    }

    /**
     * Tests the existence and name of the RECIPIENTS_TYPE constant.
     */
    @Test
    @DisplayName("RECIPIENTS_TYPE constant should be defined")
    void should_define_RECIPIENTS_TYPE_constant() {
        assertEquals("RECIPIENTS_TYPE", ActionParameterType.RECIPIENTS_TYPE.name());
    }

    /**
     * Tests the existence and name of the RECIPIENTS_STEP_ID constant.
     */
    @Test
    @DisplayName("RECIPIENTS_STEP_ID constant should be defined")
    void should_define_RECIPIENTS_STEP_ID_constant() {
        assertEquals("RECIPIENTS_STEP_ID", ActionParameterType.RECIPIENTS_STEP_ID.name());
    }

    /**
     * Tests the existence and name of the MESSAGE constant.
     */
    @Test
    @DisplayName("MESSAGE constant should be defined")
    void should_define_MESSAGE_constant() {
        assertEquals("MESSAGE", ActionParameterType.MESSAGE.name());
    }

    /**
     * Tests the existence and name of the SUBJECT constant.
     */
    @Test
    @DisplayName("SUBJECT constant should be defined")
    void should_define_SUBJECT_constant() {
        assertEquals("SUBJECT", ActionParameterType.SUBJECT.name());
    }

    /**
     * Tests the existence and name of the RECIPIENTS_SPECIFIC_EMAILS constant.
     */
    @Test
    @DisplayName("RECIPIENTS_SPECIFIC_EMAILS constant should be defined")
    void should_define_RECIPIENTS_SPECIFIC_EMAILS_constant() {
        assertEquals("RECIPIENTS_SPECIFIC_EMAILS", ActionParameterType.RECIPIENTS_SPECIFIC_EMAILS.name());
    }

    /**
     * Tests the existence and name of the RECIPIENTS_USER_IDS constant.
     */
    @Test
    @DisplayName("RECIPIENTS_USER_IDS constant should be defined")
    void should_define_RECIPIENTS_USER_IDS_constant() {
        assertEquals("RECIPIENTS_USER_IDS", ActionParameterType.RECIPIENTS_USER_IDS.name());
    }

    /**
     * Tests the existence and name of the RECIPIENTS_MEMBERSHIP_IDS constant.
     */
    @Test
    @DisplayName("RECIPIENTS_MEMBERSHIP_IDS constant should be defined")
    void should_define_RECIPIENTS_MEMBERSHIP_IDS_constant() {
        assertEquals("RECIPIENTS_MEMBERSHIP_IDS", ActionParameterType.RECIPIENTS_MEMBERSHIP_IDS.name());
    }

    /**
     * Tests the existence and name of the ENABLED constant.
     */
    @Test
    @DisplayName("ENABLED constant should be defined")
    void should_define_ENABLED_constant() {
        assertEquals("ENABLED", ActionParameterType.ENABLED.name());
    }

    /**
     * Tests the existence and name of the OBJECT constant.
     */
    @Test
    @DisplayName("OBJECT constant should be defined")
    void should_define_OBJECT_constant() {
        assertEquals("OBJECT", ActionParameterType.OBJECT.name());
    }

    /**
     * Tests the existence and name of the DAYS constant.
     */
    @Test
    @DisplayName("DAYS constant should be defined")
    void should_define_DAYS_constant() {
        assertEquals("DAYS", ActionParameterType.DAYS.name());
    }

    /**
     * Tests the existence and name of the HOURS constant.
     */
    @Test
    @DisplayName("HOURS constant should be defined")
    void should_define_HOURS_constant() {
        assertEquals("HOURS", ActionParameterType.HOURS.name());
    }

    // -------------------------------------------------------------------------
    // Key and Description Tests
    // -------------------------------------------------------------------------

    /**
     * Tests the key and description of the NAME constant.
     */
    @Test
    @DisplayName("NAME should have correct key and description")
    void name_should_have_correct_key_and_description() {
        assertEquals("name", ActionParameterType.NAME.getKey());
        assertTrue(ActionParameterType.NAME.getDescription().contains("name"));
    }

    /**
     * Tests the key and description of the TARGET_STEP constant.
     */
    @Test
    @DisplayName("TARGET_STEP should have correct key and description")
    void targetStep_should_have_correct_key_and_description() {
        assertEquals("targetStep", ActionParameterType.TARGET_STEP.getKey());
        assertTrue(ActionParameterType.TARGET_STEP.getDescription().contains("target step"));
    }

    /**
     * Tests the key and description of the RECIPIENTS constant.
     */
    @Test
    @DisplayName("RECIPIENTS should have correct key and description")
    void recipients_should_have_correct_key_and_description() {
        assertEquals("recipients", ActionParameterType.RECIPIENTS.getKey());
        assertTrue(ActionParameterType.RECIPIENTS.getDescription().contains("recipients"));
    }

    /**
     * Tests the key and description of the RECIPIENTS_TYPE constant.
     */
    @Test
    @DisplayName("RECIPIENTS_TYPE should have correct key and description")
    void recipientsType_should_have_correct_key_and_description() {
        assertEquals("recipients.type", ActionParameterType.RECIPIENTS_TYPE.getKey());
        assertTrue(ActionParameterType.RECIPIENTS_TYPE.getDescription().contains("type"));
    }

    /**
     * Tests the key and description of the RECIPIENTS_STEP_ID constant.
     */
    @Test
    @DisplayName("RECIPIENTS_STEP_ID should have correct key and description")
    void recipientsStepId_should_have_correct_key_and_description() {
        assertEquals("recipients.stepId", ActionParameterType.RECIPIENTS_STEP_ID.getKey());
        assertTrue(ActionParameterType.RECIPIENTS_STEP_ID.getDescription().contains("step"));
    }

    /**
     * Tests the key and description of the MESSAGE constant.
     */
    @Test
    @DisplayName("MESSAGE should have correct key and description")
    void message_should_have_correct_key_and_description() {
        assertEquals("message", ActionParameterType.MESSAGE.getKey());
        assertTrue(ActionParameterType.MESSAGE.getDescription().contains("message"));
    }

    /**
     * Tests the key and description of the SUBJECT constant.
     */
    @Test
    @DisplayName("SUBJECT should have correct key and description")
    void subject_should_have_correct_key_and_description() {
        assertEquals("subject", ActionParameterType.SUBJECT.getKey());
        assertTrue(ActionParameterType.SUBJECT.getDescription().contains("subject"));
    }

    /**
     * Tests the key and description of the RECIPIENTS_SPECIFIC_EMAILS constant.
     */
    @Test
    @DisplayName("RECIPIENTS_SPECIFIC_EMAILS should have correct key and description")
    void recipientsSpecificEmails_should_have_correct_key_and_description() {
        assertEquals("recipients.specificEmails", ActionParameterType.RECIPIENTS_SPECIFIC_EMAILS.getKey());
        assertTrue(ActionParameterType.RECIPIENTS_SPECIFIC_EMAILS.getDescription().contains("email"));
    }

    /**
     * Tests the key and description of the RECIPIENTS_USER_IDS constant.
     */
    @Test
    @DisplayName("RECIPIENTS_USER_IDS should have correct key and description")
    void recipientsUserIds_should_have_correct_key_and_description() {
        assertEquals("recipients.userIds", ActionParameterType.RECIPIENTS_USER_IDS.getKey());
        assertTrue(ActionParameterType.RECIPIENTS_USER_IDS.getDescription().contains("user"));
    }

    /**
     * Tests the key and description of the RECIPIENTS_MEMBERSHIP_IDS constant.
     */
    @Test
    @DisplayName("RECIPIENTS_MEMBERSHIP_IDS should have correct key and description")
    void recipientsMembershipIds_should_have_correct_key_and_description() {
        assertEquals("recipients.membershipIds", ActionParameterType.RECIPIENTS_MEMBERSHIP_IDS.getKey());
        assertTrue(ActionParameterType.RECIPIENTS_MEMBERSHIP_IDS.getDescription().contains("membership"));
    }

    /**
     * Tests the key and description of the ENABLED constant.
     */
    @Test
    @DisplayName("ENABLED should have correct key and description")
    void enabled_should_have_correct_key_and_description() {
        assertEquals("enabled", ActionParameterType.ENABLED.getKey());
        assertTrue(ActionParameterType.ENABLED.getDescription().contains("enabled"));
    }

    /**
     * Tests the key and description of the OBJECT constant.
     */
    @Test
    @DisplayName("OBJECT should have correct key and description")
    void object_should_have_correct_key_and_description() {
        assertEquals("object", ActionParameterType.OBJECT.getKey());
        assertTrue(ActionParameterType.OBJECT.getDescription().contains("object"));
    }

    /**
     * Tests the key and description of the DAYS constant.
     */
    @Test
    @DisplayName("DAYS should have correct key and description")
    void days_should_have_correct_key_and_description() {
        assertEquals("days", ActionParameterType.DAYS.getKey());
        assertTrue(ActionParameterType.DAYS.getDescription().contains("days"));
    }

    /**
     * Tests the key and description of the HOURS constant.
     */
    @Test
    @DisplayName("HOURS should have correct key and description")
    void hours_should_have_correct_key_and_description() {
        assertEquals("hours", ActionParameterType.HOURS.getKey());
        assertTrue(ActionParameterType.HOURS.getDescription().contains("hours"));
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
        assertTrue(ActionParameterType.isValid("NAME"));
        assertTrue(ActionParameterType.isValid("TARGET_STEP"));
        assertTrue(ActionParameterType.isValid("RECIPIENTS"));
    }

    /**
     * Tests the {@code isValid} method with a valid constant name (lowercase).
     */
    @Test
    @DisplayName("isValid should return true for a valid lowercase name")
    void isValid_should_return_true_for_valid_lowercase() {
        assertTrue(ActionParameterType.isValid("message"));
        assertTrue(ActionParameterType.isValid("subject"));
    }

    /**
     * Tests the {@code isValid} method with a mixed-case constant name.
     */
    @Test
    @DisplayName("isValid should return true for a mixed-case name")
    void isValid_should_return_true_for_mixed_case() {
        assertTrue(ActionParameterType.isValid("Recipients_Type"));
        assertTrue(ActionParameterType.isValid("Target_Step"));
    }

    /**
     * Tests the {@code isValid} method with a non-existent name.
     */
    @Test
    @DisplayName("isValid should return false for an invalid name")
    void isValid_should_return_false_for_invalid_name() {
        assertFalse(ActionParameterType.isValid("NON_EXISTENT_TYPE"));
        assertFalse(ActionParameterType.isValid("INVALID"));
    }

    /**
     * Tests the {@code isValid} method with a null input.
     */
    @Test
    @DisplayName("isValid should return false for null input")
    void isValid_should_return_false_for_null() {
        assertFalse(ActionParameterType.isValid(null));
    }

    /**
     * Tests the {@code isValid} method with an empty string input.
     */
    @Test
    @DisplayName("isValid should return false for an empty string input")
    void isValid_should_return_false_for_empty_string() {
        assertFalse(ActionParameterType.isValid(""));
    }

    /**
     * Tests the {@code isValid} method with whitespace-padded input.
     */
    @Test
    @DisplayName("isValid should return true for whitespace-padded valid name")
    void isValid_should_return_true_for_whitespace_padded_name() {
        assertTrue(ActionParameterType.isValid("  NAME  "));
        assertTrue(ActionParameterType.isValid("\tMESSAGE\t"));
    }

    /**
     * Tests the {@code isValid} method with whitespace-only input.
     */
    @Test
    @DisplayName("isValid should return false for whitespace-only input")
    void isValid_should_return_false_for_whitespace_only() {
        assertFalse(ActionParameterType.isValid("   "));
        assertFalse(ActionParameterType.isValid("\t"));
    }

    // -------------------------------------------------------------------------
    // getAllData Method Tests
    // -------------------------------------------------------------------------

    /**
     * Tests that getAllData returns a map with all fourteen constants.
     */
    @Test
    @DisplayName("getAllData should return map with all fourteen constants")
    void getAllData_shouldReturnCorrectMap() {
        Map<String, String> data = ActionParameterType.getAllData();
        assertEquals(14, data.size());
        assertTrue(data.containsKey("name"));
        assertTrue(data.containsKey("targetStep"));
        assertTrue(data.containsKey("recipients"));
        assertTrue(data.containsKey("recipients.type"));
        assertTrue(data.containsKey("recipients.stepId"));
        assertTrue(data.containsKey("message"));
        assertTrue(data.containsKey("subject"));
        assertTrue(data.containsKey("recipients.specificEmails"));
        assertTrue(data.containsKey("recipients.userIds"));
        assertTrue(data.containsKey("recipients.membershipIds"));
        assertTrue(data.containsKey("enabled"));
        assertTrue(data.containsKey("object"));
        assertTrue(data.containsKey("days"));
        assertTrue(data.containsKey("hours"));
    }

    /**
     * Tests that getAllData returns an unmodifiable map.
     */
    @Test
    @DisplayName("getAllData should return an unmodifiable map")
    void getAllData_shouldReturnUnmodifiableMap() {
        Map<String, String> data = ActionParameterType.getAllData();
        assertThrows(UnsupportedOperationException.class, () -> data.clear());
        assertThrows(UnsupportedOperationException.class, () -> data.put("Test", "Value"));
    }

    // -------------------------------------------------------------------------
    // getAllKeysList Method Tests
    // -------------------------------------------------------------------------

    /**
     * Tests that getAllKeysList returns a list with all fourteen keys.
     */
    @Test
    @DisplayName("getAllKeysList should return list with all fourteen keys")
    void getAllKeysList_shouldReturnCorrectList() {
        List<String> keys = ActionParameterType.getAllKeysList();
        assertEquals(14, keys.size());
        assertTrue(keys.contains("name"));
        assertTrue(keys.contains("targetStep"));
        assertTrue(keys.contains("recipients"));
        assertTrue(keys.contains("recipients.type"));
        assertTrue(keys.contains("recipients.stepId"));
        assertTrue(keys.contains("message"));
        assertTrue(keys.contains("subject"));
        assertTrue(keys.contains("recipients.specificEmails"));
        assertTrue(keys.contains("recipients.userIds"));
        assertTrue(keys.contains("recipients.membershipIds"));
        assertTrue(keys.contains("enabled"));
        assertTrue(keys.contains("object"));
        assertTrue(keys.contains("days"));
        assertTrue(keys.contains("hours"));
    }

    /**
     * Tests that getAllKeysList returns an unmodifiable list.
     */
    @Test
    @DisplayName("getAllKeysList should return an unmodifiable list")
    void getAllKeysList_shouldReturnUnmodifiableList() {
        List<String> keys = ActionParameterType.getAllKeysList();
        assertThrows(UnsupportedOperationException.class, () -> keys.add("NEW"));
        assertThrows(UnsupportedOperationException.class, () -> keys.remove(0));
    }
}
