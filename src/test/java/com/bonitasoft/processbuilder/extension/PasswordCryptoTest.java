package com.bonitasoft.processbuilder.extension;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link PasswordCrypto}.
 * <p>
 * Note: Most tests require the MASTER_BONITA_PWD environment variable to be set.
 * Tests that don't require it are marked accordingly.
 * </p>
 */
class PasswordCryptoTest {

    // -------------------------------------------------------------------------
    // Constructor Tests
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Private constructor should throw UnsupportedOperationException")
        void constructor_should_prevent_instantiation() throws Exception {
            Constructor<PasswordCrypto> constructor = PasswordCrypto.class.getDeclaredConstructor();
            constructor.setAccessible(true);

            InvocationTargetException exception = assertThrows(
                InvocationTargetException.class,
                constructor::newInstance
            );

            assertThat(exception.getCause())
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessageContaining("cannot be instantiated");
        }
    }

    // -------------------------------------------------------------------------
    // Environment Variable Tests
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("Environment Variable Tests")
    class EnvironmentVariableTests {

        @Test
        @DisplayName("ENV_VAR_NAME should be MASTER_BONITA_PWD")
        void should_have_correct_env_var_name() {
            assertEquals("MASTER_BONITA_PWD", PasswordCrypto.ENV_VAR_NAME);
        }

        @Test
        @DisplayName("isMasterPasswordConfigured should return correct value")
        void should_check_master_password_configured() {
            // This test verifies the method executes without throwing and returns a valid boolean
            assertDoesNotThrow(() -> PasswordCrypto.isMasterPasswordConfigured());
        }

        @Test
        @DisplayName("encrypt should throw if master password not configured")
        void encrypt_should_throw_if_not_configured() {
            if (!PasswordCrypto.isMasterPasswordConfigured()) {
                PasswordCrypto.CryptoException exception = assertThrows(
                    PasswordCrypto.CryptoException.class,
                    () -> PasswordCrypto.encrypt("test")
                );
                assertThat(exception.getMessage()).contains("MASTER_BONITA_PWD");
            }
        }

        @Test
        @DisplayName("decrypt should throw if master password not configured")
        void decrypt_should_throw_if_not_configured() {
            if (!PasswordCrypto.isMasterPasswordConfigured()) {
                PasswordCrypto.CryptoException exception = assertThrows(
                    PasswordCrypto.CryptoException.class,
                    () -> PasswordCrypto.decrypt("test")
                );
                assertThat(exception.getMessage()).contains("MASTER_BONITA_PWD");
            }
        }
    }

    // -------------------------------------------------------------------------
    // Input Validation Tests
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("Input Validation Tests")
    class InputValidationTests {

        @Test
        @DisplayName("encrypt should throw for null input")
        void encrypt_should_throw_for_null() {
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> PasswordCrypto.encrypt(null)
            );
            assertThat(exception.getMessage()).contains("cannot be null");
        }

        @Test
        @DisplayName("decrypt should throw for null input")
        void decrypt_should_throw_for_null() {
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> PasswordCrypto.decrypt(null)
            );
            assertThat(exception.getMessage()).contains("cannot be null or empty");
        }

        @Test
        @DisplayName("decrypt should throw for empty input")
        void decrypt_should_throw_for_empty() {
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> PasswordCrypto.decrypt("")
            );
            assertThat(exception.getMessage()).contains("cannot be null or empty");
        }

        @Test
        @DisplayName("decrypt should throw for blank input")
        void decrypt_should_throw_for_blank() {
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> PasswordCrypto.decrypt("   ")
            );
            assertThat(exception.getMessage()).contains("cannot be null or empty");
        }
    }

    // -------------------------------------------------------------------------
    // isEncrypted Tests
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("isEncrypted Tests")
    class IsEncryptedTests {

        @Test
        @DisplayName("Should return false for null")
        void should_return_false_for_null() {
            boolean result = PasswordCrypto.isEncrypted(null);
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("Should return false for empty string")
        void should_return_false_for_empty() {
            boolean result = PasswordCrypto.isEncrypted("");
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("Should return false for blank string")
        void should_return_false_for_blank() {
            boolean result = PasswordCrypto.isEncrypted("   ");
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("Should return false for short text")
        void should_return_false_for_short_text() {
            boolean result = PasswordCrypto.isEncrypted("short");
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("Should return false for text exactly at min length minus one")
        void should_return_false_for_text_at_boundary() {
            // MIN_ENCRYPTED_LENGTH is 60, so 59 chars should fail
            String almostLongEnough = "A".repeat(59);
            boolean result = PasswordCrypto.isEncrypted(almostLongEnough);
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("Should return false for plain text")
        void should_return_false_for_plain_text() {
            boolean result = PasswordCrypto.isEncrypted("This is a plain password");
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("Should return false for text with non-Base64 chars")
        void should_return_false_for_non_base64() {
            String invalid = "This contains invalid characters!!!@@##$$";
            boolean result = PasswordCrypto.isEncrypted(invalid);
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("Should return false for long text with special characters")
        void should_return_false_for_long_non_base64() {
            // Long enough but contains invalid characters
            String longInvalid = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()";
            boolean result = PasswordCrypto.isEncrypted(longInvalid);
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("Should return true for valid Base64 of sufficient length")
        void should_return_true_for_long_base64() {
            // Generate a long enough valid Base64 string (60+ chars)
            String longBase64 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/==";
            boolean result = PasswordCrypto.isEncrypted(longBase64);
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("Should return true for Base64 exactly at minimum length")
        void should_return_true_at_exactly_min_length() {
            // Exactly 60 valid Base64 characters
            String exactlyMinLength = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz01234567";
            boolean result = PasswordCrypto.isEncrypted(exactlyMinLength);
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("Should return true for Base64 with padding")
        void should_return_true_for_base64_with_padding() {
            String withPadding = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz012345==";
            boolean result = PasswordCrypto.isEncrypted(withPadding);
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("Should return true for Base64 with plus and slash")
        void should_return_true_for_base64_with_special_chars() {
            String withSpecial = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdef+/ijklmnopqrstuvwxyz012345==";
            boolean result = PasswordCrypto.isEncrypted(withSpecial);
            assertThat(result).isTrue();
        }
    }

    // -------------------------------------------------------------------------
    // encryptIfNeeded Tests
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("encryptIfNeeded Tests")
    class EncryptIfNeededTests {

        @Test
        @DisplayName("Should return null for null input")
        void should_return_null_for_null() {
            assertNull(PasswordCrypto.encryptIfNeeded(null));
        }

        @Test
        @DisplayName("Should return empty for empty input")
        void should_return_empty_for_empty() {
            assertEquals("", PasswordCrypto.encryptIfNeeded(""));
        }

        @Test
        @DisplayName("Should return blank for blank input")
        void should_return_blank_for_blank() {
            assertEquals("   ", PasswordCrypto.encryptIfNeeded("   "));
        }

        @Test
        @DisplayName("Should return same value if already encrypted")
        void should_return_same_if_encrypted() {
            String fakeEncrypted = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/==";
            assertEquals(fakeEncrypted, PasswordCrypto.encryptIfNeeded(fakeEncrypted));
        }
    }

    // -------------------------------------------------------------------------
    // decryptIfNeeded Tests
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("decryptIfNeeded Tests")
    class DecryptIfNeededTests {

        @Test
        @DisplayName("Should return null for null input")
        void should_return_null_for_null() {
            assertNull(PasswordCrypto.decryptIfNeeded(null));
        }

        @Test
        @DisplayName("Should return empty for empty input")
        void should_return_empty_for_empty() {
            assertEquals("", PasswordCrypto.decryptIfNeeded(""));
        }

        @Test
        @DisplayName("Should return blank for blank input")
        void should_return_blank_for_blank() {
            assertEquals("   ", PasswordCrypto.decryptIfNeeded("   "));
        }

        @Test
        @DisplayName("Should return plain text unchanged")
        void should_return_plain_text_unchanged() {
            String plain = "not encrypted";
            assertEquals(plain, PasswordCrypto.decryptIfNeeded(plain));
        }

        @Test
        @DisplayName("Should return original if decryption fails")
        void should_return_original_on_failure() {
            // Valid Base64 but not properly encrypted
            String fakeEncrypted = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/==";
            String result = PasswordCrypto.decryptIfNeeded(fakeEncrypted);
            assertEquals(fakeEncrypted, result);
        }
    }

    // -------------------------------------------------------------------------
    // CryptoException Tests
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("CryptoException Tests")
    class CryptoExceptionTests {

        @Test
        @DisplayName("Should create exception with message only")
        void should_create_with_message() {
            PasswordCrypto.CryptoException exception =
                new PasswordCrypto.CryptoException("Test message");

            assertEquals("Test message", exception.getMessage());
            assertNull(exception.getCause());
        }

        @Test
        @DisplayName("Should create exception with message and cause")
        void should_create_with_message_and_cause() {
            Throwable cause = new RuntimeException("Root cause");
            PasswordCrypto.CryptoException exception =
                new PasswordCrypto.CryptoException("Test message", cause);

            assertEquals("Test message", exception.getMessage());
            assertEquals(cause, exception.getCause());
        }
    }

    // -------------------------------------------------------------------------
    // Conditional Integration Tests (only run if env var is set)
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("Integration Tests (require MASTER_BONITA_PWD)")
    class IntegrationTests {

        @Test
        @DisplayName("Should encrypt and decrypt successfully")
        void should_encrypt_and_decrypt() {
            if (!PasswordCrypto.isMasterPasswordConfigured()) {
                return; // Skip if not configured
            }

            String original = "mySecretPassword123";
            String encrypted = PasswordCrypto.encrypt(original);
            String decrypted = PasswordCrypto.decrypt(encrypted);

            assertEquals(original, decrypted);
            assertNotEquals(original, encrypted);
        }

        @Test
        @DisplayName("Should handle empty string encryption")
        void should_handle_empty_string() {
            if (!PasswordCrypto.isMasterPasswordConfigured()) {
                return;
            }

            String encrypted = PasswordCrypto.encrypt("");
            String decrypted = PasswordCrypto.decrypt(encrypted);

            assertEquals("", decrypted);
        }

        @Test
        @DisplayName("Should produce different ciphertext each time")
        void should_produce_different_ciphertext() {
            if (!PasswordCrypto.isMasterPasswordConfigured()) {
                return;
            }

            String original = "samePassword";
            String encrypted1 = PasswordCrypto.encrypt(original);
            String encrypted2 = PasswordCrypto.encrypt(original);

            assertNotEquals(encrypted1, encrypted2);
            assertEquals(original, PasswordCrypto.decrypt(encrypted1));
            assertEquals(original, PasswordCrypto.decrypt(encrypted2));
        }

        @Test
        @DisplayName("Should handle special characters")
        void should_handle_special_characters() {
            if (!PasswordCrypto.isMasterPasswordConfigured()) {
                return;
            }

            String original = "P@$$w0rd!#%^&*()_+-=áéíóú日本語🔐";
            String encrypted = PasswordCrypto.encrypt(original);
            String decrypted = PasswordCrypto.decrypt(encrypted);

            assertEquals(original, decrypted);
        }
    }

    // -------------------------------------------------------------------------
    // Decryption Error Tests
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("Decryption Error Tests")
    class DecryptionErrorTests {

        @Test
        @DisplayName("Should throw for invalid Base64")
        void should_throw_for_invalid_base64() {
            if (!PasswordCrypto.isMasterPasswordConfigured()) {
                return;
            }

            PasswordCrypto.CryptoException exception = assertThrows(
                PasswordCrypto.CryptoException.class,
                () -> PasswordCrypto.decrypt("not-valid-base64!!!")
            );
            assertThat(exception.getMessage()).contains("Invalid Base64");
        }

        @Test
        @DisplayName("Should throw for too short data")
        void should_throw_for_short_data() {
            if (!PasswordCrypto.isMasterPasswordConfigured()) {
                return;
            }

            String tooShort = java.util.Base64.getEncoder().encodeToString(new byte[10]);

            PasswordCrypto.CryptoException exception = assertThrows(
                PasswordCrypto.CryptoException.class,
                () -> PasswordCrypto.decrypt(tooShort)
            );
            assertThat(exception.getMessage()).contains("too short");
        }
    }
}
