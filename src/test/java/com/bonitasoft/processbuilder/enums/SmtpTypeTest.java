package com.bonitasoft.processbuilder.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link SmtpType} enumeration.
 * <p>
 * This class ensures that all defined constants are present and that
 * utility methods, such as {@code isValid}, {@code getAllData}, and
 * {@code getAllKeysList}, function correctly across various inputs.
 * </p>
 */
class SmtpTypeTest {

    // -------------------------------------------------------------------------
    // Constant Count Test
    // -------------------------------------------------------------------------

    /**
     * Tests that the enum contains exactly eight constants.
     */
    @Test
    @DisplayName("Should contain exactly eight SMTP constants")
    void should_contain_eight_constants() {
        assertEquals(8, SmtpType.values().length);
    }

    // -------------------------------------------------------------------------
    // Constant Existence Tests
    // -------------------------------------------------------------------------

    /**
     * Tests the existence and name of the SMTP_HOST constant.
     */
    @Test
    @DisplayName("SMTP_HOST constant should be defined")
    void should_define_SMTP_HOST_constant() {
        assertEquals("SMTP_HOST", SmtpType.SMTP_HOST.name());
    }

    /**
     * Tests the existence and name of the SMTP_PORT constant.
     */
    @Test
    @DisplayName("SMTP_PORT constant should be defined")
    void should_define_SMTP_PORT_constant() {
        assertEquals("SMTP_PORT", SmtpType.SMTP_PORT.name());
    }

    /**
     * Tests the existence and name of the FROM constant.
     */
    @Test
    @DisplayName("FROM constant should be defined")
    void should_define_FROM_constant() {
        assertEquals("FROM", SmtpType.FROM.name());
    }

    /**
     * Tests the existence and name of the SSL constant.
     */
    @Test
    @DisplayName("SSL constant should be defined")
    void should_define_SSL_constant() {
        assertEquals("SSL", SmtpType.SSL.name());
    }

    /**
     * Tests the existence and name of the STARTTLS constant.
     */
    @Test
    @DisplayName("STARTTLS constant should be defined")
    void should_define_STARTTLS_constant() {
        assertEquals("STARTTLS", SmtpType.STARTTLS.name());
    }

    /**
     * Tests the existence and name of the TRUST_CERTIFICATE constant.
     */
    @Test
    @DisplayName("TRUST_CERTIFICATE constant should be defined")
    void should_define_TRUST_CERTIFICATE_constant() {
        assertEquals("TRUST_CERTIFICATE", SmtpType.TRUST_CERTIFICATE.name());
    }

    /**
     * Tests the existence and name of the USERNAME constant.
     */
    @Test
    @DisplayName("USERNAME constant should be defined")
    void should_define_USERNAME_constant() {
        assertEquals("USERNAME", SmtpType.USERNAME.name());
    }

    /**
     * Tests the existence and name of the PASSWORD constant.
     */
    @Test
    @DisplayName("PASSWORD constant should be defined")
    void should_define_PASSWORD_constant() {
        assertEquals("PASSWORD", SmtpType.PASSWORD.name());
    }

    // -------------------------------------------------------------------------
    // Key and Description Tests
    // -------------------------------------------------------------------------

    /**
     * Tests the key and description of the SMTP_HOST constant.
     */
    @Test
    @DisplayName("SMTP_HOST should have correct key and description")
    void smtpHost_should_have_correct_key_and_description() {
        assertEquals("SmtpHost", SmtpType.SMTP_HOST.getKey());
        assertTrue(SmtpType.SMTP_HOST.getDescription().contains("hostname"));
    }

    /**
     * Tests the key and description of the SMTP_PORT constant.
     */
    @Test
    @DisplayName("SMTP_PORT should have correct key and description")
    void smtpPort_should_have_correct_key_and_description() {
        assertEquals("SmtpPort", SmtpType.SMTP_PORT.getKey());
        assertTrue(SmtpType.SMTP_PORT.getDescription().contains("port number"));
    }

    /**
     * Tests the key and description of the FROM constant.
     */
    @Test
    @DisplayName("FROM should have correct key and description")
    void from_should_have_correct_key_and_description() {
        assertEquals("From", SmtpType.FROM.getKey());
        assertTrue(SmtpType.FROM.getDescription().contains("sender email"));
    }

    /**
     * Tests the key and description of the SSL constant.
     */
    @Test
    @DisplayName("SSL should have correct key and description")
    void ssl_should_have_correct_key_and_description() {
        assertEquals("Ssl", SmtpType.SSL.getKey());
        assertTrue(SmtpType.SSL.getDescription().contains("SSL"));
    }

    /**
     * Tests the key and description of the STARTTLS constant.
     */
    @Test
    @DisplayName("STARTTLS should have correct key and description")
    void starttls_should_have_correct_key_and_description() {
        assertEquals("StartTls", SmtpType.STARTTLS.getKey());
        assertTrue(SmtpType.STARTTLS.getDescription().contains("STARTTLS"));
    }

    /**
     * Tests the key and description of the TRUST_CERTIFICATE constant.
     */
    @Test
    @DisplayName("TRUST_CERTIFICATE should have correct key and description")
    void trustCertificate_should_have_correct_key_and_description() {
        assertEquals("TrustCertificate", SmtpType.TRUST_CERTIFICATE.getKey());
        assertTrue(SmtpType.TRUST_CERTIFICATE.getDescription().contains("certificate"));
    }

    /**
     * Tests the key and description of the USERNAME constant.
     */
    @Test
    @DisplayName("USERNAME should have correct key and description")
    void username_should_have_correct_key_and_description() {
        assertEquals("Username", SmtpType.USERNAME.getKey());
        assertTrue(SmtpType.USERNAME.getDescription().contains("username"));
    }

    /**
     * Tests the key and description of the PASSWORD constant.
     */
    @Test
    @DisplayName("PASSWORD should have correct key and description")
    void password_should_have_correct_key_and_description() {
        assertEquals("Password", SmtpType.PASSWORD.getKey());
        assertTrue(SmtpType.PASSWORD.getDescription().contains("password"));
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
        assertTrue(SmtpType.isValid("SMTP_HOST"));
        assertTrue(SmtpType.isValid("SSL"));
    }

    /**
     * Tests the {@code isValid} method with a valid constant name (lowercase).
     */
    @Test
    @DisplayName("isValid should return true for a valid lowercase name")
    void isValid_should_return_true_for_valid_lowercase() {
        assertTrue(SmtpType.isValid("smtp_port"));
        assertTrue(SmtpType.isValid("starttls"));
    }

    /**
     * Tests the {@code isValid} method with a mixed-case constant name.
     */
    @Test
    @DisplayName("isValid should return true for a mixed-case name")
    void isValid_should_return_true_for_mixed_case() {
        assertTrue(SmtpType.isValid("Trust_Certificate"));
        assertTrue(SmtpType.isValid("Username"));
    }

    /**
     * Tests the {@code isValid} method with a non-existent name.
     */
    @Test
    @DisplayName("isValid should return false for an invalid name")
    void isValid_should_return_false_for_invalid_name() {
        assertFalse(SmtpType.isValid("NON_EXISTENT_TYPE"));
        assertFalse(SmtpType.isValid("INVALID"));
    }

    /**
     * Tests the {@code isValid} method with a null input.
     */
    @Test
    @DisplayName("isValid should return false for null input")
    void isValid_should_return_false_for_null() {
        assertFalse(SmtpType.isValid(null));
    }

    /**
     * Tests the {@code isValid} method with an empty string input.
     */
    @Test
    @DisplayName("isValid should return false for an empty string input")
    void isValid_should_return_false_for_empty_string() {
        assertFalse(SmtpType.isValid(""));
    }

    /**
     * Tests the {@code isValid} method with whitespace-padded input.
     */
    @Test
    @DisplayName("isValid should return true for whitespace-padded valid name")
    void isValid_should_return_true_for_whitespace_padded_name() {
        assertTrue(SmtpType.isValid("  SMTP_HOST  "));
        assertTrue(SmtpType.isValid("\tSSL\t"));
    }

    /**
     * Tests the {@code isValid} method with whitespace-only input.
     */
    @Test
    @DisplayName("isValid should return false for whitespace-only input")
    void isValid_should_return_false_for_whitespace_only() {
        assertFalse(SmtpType.isValid("   "));
        assertFalse(SmtpType.isValid("\t"));
    }

    // -------------------------------------------------------------------------
    // getAllData Method Tests
    // -------------------------------------------------------------------------

    /**
     * Tests that getAllData returns a map with all eight constants.
     */
    @Test
    @DisplayName("getAllData should return map with all eight constants")
    void getAllData_shouldReturnCorrectMap() {
        Map<String, String> data = SmtpType.getAllData();
        assertEquals(8, data.size());
        assertTrue(data.containsKey("SmtpHost"));
        assertTrue(data.containsKey("SmtpPort"));
        assertTrue(data.containsKey("From"));
        assertTrue(data.containsKey("Ssl"));
        assertTrue(data.containsKey("StartTls"));
        assertTrue(data.containsKey("TrustCertificate"));
        assertTrue(data.containsKey("Username"));
        assertTrue(data.containsKey("Password"));
    }

    /**
     * Tests that getAllData returns an unmodifiable map.
     */
    @Test
    @DisplayName("getAllData should return an unmodifiable map")
    void getAllData_shouldReturnUnmodifiableMap() {
        Map<String, String> data = SmtpType.getAllData();
        assertThrows(UnsupportedOperationException.class, () -> data.clear());
        assertThrows(UnsupportedOperationException.class, () -> data.put("Test", "Value"));
    }

    // -------------------------------------------------------------------------
    // getAllKeysList Method Tests
    // -------------------------------------------------------------------------

    /**
     * Tests that getAllKeysList returns a list with all eight keys.
     */
    @Test
    @DisplayName("getAllKeysList should return list with all eight keys")
    void getAllKeysList_shouldReturnCorrectList() {
        List<String> keys = SmtpType.getAllKeysList();
        assertEquals(8, keys.size());
        assertTrue(keys.contains("SmtpHost"));
        assertTrue(keys.contains("SmtpPort"));
        assertTrue(keys.contains("From"));
        assertTrue(keys.contains("Ssl"));
        assertTrue(keys.contains("StartTls"));
        assertTrue(keys.contains("TrustCertificate"));
        assertTrue(keys.contains("Username"));
        assertTrue(keys.contains("Password"));
    }

    /**
     * Tests that getAllKeysList returns an unmodifiable list.
     */
    @Test
    @DisplayName("getAllKeysList should return an unmodifiable list")
    void getAllKeysList_shouldReturnUnmodifiableList() {
        List<String> keys = SmtpType.getAllKeysList();
        assertThrows(UnsupportedOperationException.class, () -> keys.add("NEW"));
        assertThrows(UnsupportedOperationException.class, () -> keys.remove(0));
    }
}
