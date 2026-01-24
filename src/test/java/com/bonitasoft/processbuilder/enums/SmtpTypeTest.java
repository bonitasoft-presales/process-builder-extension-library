package com.bonitasoft.processbuilder.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for the {@link SmtpType} enumeration.
 * <p>
 * This class ensures that all defined constants are present and that
 * utility methods, such as {@code isValid}, {@code getAllData}, and
 * {@code getAllKeysList}, function correctly across various inputs.
 * </p>
 */
@DisplayName("SmtpType Unit Tests")
class SmtpTypeTest {

    // =========================================================================
    // CONSTANTS
    // =========================================================================

    private static final int EXPECTED_CONSTANT_COUNT = 9;

    // =========================================================================
    // CONSTANT COUNT TESTS
    // =========================================================================

    @Nested
    @DisplayName("Constant Count Tests")
    class ConstantCountTests {

        @Test
        @DisplayName("should contain exactly nine SMTP constants")
        void should_contain_nine_constants() {
            assertThat(SmtpType.values()).hasSize(EXPECTED_CONSTANT_COUNT);
        }
    }

    // =========================================================================
    // CONSTANT EXISTENCE TESTS
    // =========================================================================

    @Nested
    @DisplayName("Constant Existence Tests")
    class ConstantExistenceTests {

        @Test
        @DisplayName("SMTP_HOST constant should be defined")
        void should_define_SMTP_HOST_constant() {
            assertThat(SmtpType.SMTP_HOST.name()).isEqualTo("SMTP_HOST");
        }

        @Test
        @DisplayName("SMTP_PORT constant should be defined")
        void should_define_SMTP_PORT_constant() {
            assertThat(SmtpType.SMTP_PORT.name()).isEqualTo("SMTP_PORT");
        }

        @Test
        @DisplayName("FROM constant should be defined")
        void should_define_FROM_constant() {
            assertThat(SmtpType.FROM.name()).isEqualTo("FROM");
        }

        @Test
        @DisplayName("SSL constant should be defined")
        void should_define_SSL_constant() {
            assertThat(SmtpType.SSL.name()).isEqualTo("SSL");
        }

        @Test
        @DisplayName("STARTTLS constant should be defined")
        void should_define_STARTTLS_constant() {
            assertThat(SmtpType.STARTTLS.name()).isEqualTo("STARTTLS");
        }

        @Test
        @DisplayName("TRUST_CERTIFICATE constant should be defined")
        void should_define_TRUST_CERTIFICATE_constant() {
            assertThat(SmtpType.TRUST_CERTIFICATE.name()).isEqualTo("TRUST_CERTIFICATE");
        }

        @Test
        @DisplayName("USERNAME constant should be defined")
        void should_define_USERNAME_constant() {
            assertThat(SmtpType.USERNAME.name()).isEqualTo("USERNAME");
        }

        @Test
        @DisplayName("PASSWORD constant should be defined")
        void should_define_PASSWORD_constant() {
            assertThat(SmtpType.PASSWORD.name()).isEqualTo("PASSWORD");
        }

        @Test
        @DisplayName("EMAILTEMPLATE constant should be defined")
        void should_define_EMAILTEMPLATE_constant() {
            assertThat(SmtpType.EMAILTEMPLATE.name()).isEqualTo("EMAILTEMPLATE");
        }
    }

    // =========================================================================
    // KEY AND DESCRIPTION TESTS
    // =========================================================================

    @Nested
    @DisplayName("Key and Description Tests")
    class KeyAndDescriptionTests {

        @Test
        @DisplayName("SMTP_HOST should have correct key and description")
        void smtpHost_should_have_correct_key_and_description() {
            assertThat(SmtpType.SMTP_HOST.getKey()).isEqualTo("SmtpHost");
            assertThat(SmtpType.SMTP_HOST.getDescription()).contains("hostname");
        }

        @Test
        @DisplayName("SMTP_PORT should have correct key and description")
        void smtpPort_should_have_correct_key_and_description() {
            assertThat(SmtpType.SMTP_PORT.getKey()).isEqualTo("SmtpPort");
            assertThat(SmtpType.SMTP_PORT.getDescription()).contains("port number");
        }

        @Test
        @DisplayName("FROM should have correct key and description")
        void from_should_have_correct_key_and_description() {
            assertThat(SmtpType.FROM.getKey()).isEqualTo("From");
            assertThat(SmtpType.FROM.getDescription()).contains("sender email");
        }

        @Test
        @DisplayName("SSL should have correct key and description")
        void ssl_should_have_correct_key_and_description() {
            assertThat(SmtpType.SSL.getKey()).isEqualTo("Ssl");
            assertThat(SmtpType.SSL.getDescription()).contains("SSL");
        }

        @Test
        @DisplayName("STARTTLS should have correct key and description")
        void starttls_should_have_correct_key_and_description() {
            assertThat(SmtpType.STARTTLS.getKey()).isEqualTo("StartTls");
            assertThat(SmtpType.STARTTLS.getDescription()).contains("STARTTLS");
        }

        @Test
        @DisplayName("TRUST_CERTIFICATE should have correct key and description")
        void trustCertificate_should_have_correct_key_and_description() {
            assertThat(SmtpType.TRUST_CERTIFICATE.getKey()).isEqualTo("TrustCertificate");
            assertThat(SmtpType.TRUST_CERTIFICATE.getDescription()).contains("certificate");
        }

        @Test
        @DisplayName("USERNAME should have correct key and description")
        void username_should_have_correct_key_and_description() {
            assertThat(SmtpType.USERNAME.getKey()).isEqualTo("Username");
            assertThat(SmtpType.USERNAME.getDescription()).contains("username");
        }

        @Test
        @DisplayName("PASSWORD should have correct key and description")
        void password_should_have_correct_key_and_description() {
            assertThat(SmtpType.PASSWORD.getKey()).isEqualTo("Password");
            assertThat(SmtpType.PASSWORD.getDescription()).contains("password");
        }

        @Test
        @DisplayName("EMAILTEMPLATE should have correct key and description")
        void emailTemplate_should_have_correct_key_and_description() {
            assertThat(SmtpType.EMAILTEMPLATE.getKey()).isEqualTo("EmailTemplate");
            assertThat(SmtpType.EMAILTEMPLATE.getDescription()).contains("template");
            assertThat(SmtpType.EMAILTEMPLATE.getDescription()).contains("{{content}}");
        }
    }

    // =========================================================================
    // isValid METHOD TESTS
    // =========================================================================

    @Nested
    @DisplayName("isValid Method Tests")
    class IsValidMethodTests {

        @Test
        @DisplayName("should return true for valid uppercase name")
        void isValid_should_return_true_for_valid_uppercase() {
            assertThat(SmtpType.isValid("SMTP_HOST")).isTrue();
            assertThat(SmtpType.isValid("SSL")).isTrue();
            assertThat(SmtpType.isValid("EMAILTEMPLATE")).isTrue();
        }

        @Test
        @DisplayName("should return true for valid lowercase name")
        void isValid_should_return_true_for_valid_lowercase() {
            assertThat(SmtpType.isValid("smtp_port")).isTrue();
            assertThat(SmtpType.isValid("starttls")).isTrue();
            assertThat(SmtpType.isValid("emailtemplate")).isTrue();
        }

        @Test
        @DisplayName("should return true for mixed-case name")
        void isValid_should_return_true_for_mixed_case() {
            assertThat(SmtpType.isValid("Trust_Certificate")).isTrue();
            assertThat(SmtpType.isValid("Username")).isTrue();
            assertThat(SmtpType.isValid("EmailTemplate")).isTrue();
        }

        @Test
        @DisplayName("should return false for invalid name")
        void isValid_should_return_false_for_invalid_name() {
            assertThat(SmtpType.isValid("NON_EXISTENT_TYPE")).isFalse();
            assertThat(SmtpType.isValid("INVALID")).isFalse();
        }

        @Test
        @DisplayName("should return false for null input")
        void isValid_should_return_false_for_null() {
            assertThat(SmtpType.isValid(null)).isFalse();
        }

        @Test
        @DisplayName("should return false for empty string input")
        void isValid_should_return_false_for_empty_string() {
            assertThat(SmtpType.isValid("")).isFalse();
        }

        @Test
        @DisplayName("should return true for whitespace-padded valid name")
        void isValid_should_return_true_for_whitespace_padded_name() {
            assertThat(SmtpType.isValid("  SMTP_HOST  ")).isTrue();
            assertThat(SmtpType.isValid("\tSSL\t")).isTrue();
            assertThat(SmtpType.isValid("  EMAILTEMPLATE  ")).isTrue();
        }

        @Test
        @DisplayName("should return false for whitespace-only input")
        void isValid_should_return_false_for_whitespace_only() {
            assertThat(SmtpType.isValid("   ")).isFalse();
            assertThat(SmtpType.isValid("\t")).isFalse();
        }
    }

    // =========================================================================
    // getAllData METHOD TESTS
    // =========================================================================

    @Nested
    @DisplayName("getAllData Method Tests")
    class GetAllDataMethodTests {

        @Test
        @DisplayName("should return map with all nine constants")
        void getAllData_shouldReturnCorrectMap() {
            Map<String, String> data = SmtpType.getAllData();

            assertThat(data).hasSize(EXPECTED_CONSTANT_COUNT);
            assertThat(data).containsKey("SmtpHost");
            assertThat(data).containsKey("SmtpPort");
            assertThat(data).containsKey("From");
            assertThat(data).containsKey("Ssl");
            assertThat(data).containsKey("StartTls");
            assertThat(data).containsKey("TrustCertificate");
            assertThat(data).containsKey("Username");
            assertThat(data).containsKey("Password");
            assertThat(data).containsKey("EmailTemplate");
        }

        @Test
        @DisplayName("should return an unmodifiable map")
        void getAllData_shouldReturnUnmodifiableMap() {
            Map<String, String> data = SmtpType.getAllData();

            assertThatThrownBy(data::clear)
                    .isInstanceOf(UnsupportedOperationException.class);
            assertThatThrownBy(() -> data.put("Test", "Value"))
                    .isInstanceOf(UnsupportedOperationException.class);
        }

        @Test
        @DisplayName("should map keys to correct descriptions")
        void getAllData_shouldMapKeysToCorrectDescriptions() {
            Map<String, String> data = SmtpType.getAllData();

            for (SmtpType type : SmtpType.values()) {
                assertThat(data.get(type.getKey())).isEqualTo(type.getDescription());
            }
        }
    }

    // =========================================================================
    // getAllKeysList METHOD TESTS
    // =========================================================================

    @Nested
    @DisplayName("getAllKeysList Method Tests")
    class GetAllKeysListMethodTests {

        @Test
        @DisplayName("should return list with all nine keys")
        void getAllKeysList_shouldReturnCorrectList() {
            List<String> keys = SmtpType.getAllKeysList();

            assertThat(keys).hasSize(EXPECTED_CONSTANT_COUNT);
            assertThat(keys).contains("SmtpHost", "SmtpPort", "From", "Ssl",
                    "StartTls", "TrustCertificate", "Username", "Password", "EmailTemplate");
        }

        @Test
        @DisplayName("should return an unmodifiable list")
        void getAllKeysList_shouldReturnUnmodifiableList() {
            List<String> keys = SmtpType.getAllKeysList();

            assertThatThrownBy(() -> keys.add("NEW"))
                    .isInstanceOf(UnsupportedOperationException.class);
            assertThatThrownBy(() -> keys.remove(0))
                    .isInstanceOf(UnsupportedOperationException.class);
        }

        @Test
        @DisplayName("should preserve enum declaration order")
        void getAllKeysList_shouldPreserveOrder() {
            List<String> keys = SmtpType.getAllKeysList();

            int index = 0;
            for (SmtpType type : SmtpType.values()) {
                assertThat(keys.get(index)).isEqualTo(type.getKey());
                index++;
            }
        }
    }
}
