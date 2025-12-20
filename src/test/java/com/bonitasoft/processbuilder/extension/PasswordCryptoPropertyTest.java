package com.bonitasoft.processbuilder.extension;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

import static org.assertj.core.api.Assertions.*;

/**
 * Property-based tests for {@link PasswordCrypto} utility class.
 * Tests the isEncrypted heuristic and safe methods behavior.
 *
 * @author Bonitasoft
 * @since 1.0
 */
@Label("PasswordCrypto Property-Based Tests")
class PasswordCryptoPropertyTest {

    // =========================================================================
    // IS ENCRYPTED PROPERTIES
    // =========================================================================

    @Property(tries = 500)
    @Label("isEncrypted should return false for null input")
    void isEncryptedShouldReturnFalseForNull() {
        assertThat(PasswordCrypto.isEncrypted(null)).isFalse();
    }

    @Property(tries = 500)
    @Label("isEncrypted should return false for empty string")
    void isEncryptedShouldReturnFalseForEmpty() {
        assertThat(PasswordCrypto.isEncrypted("")).isFalse();
    }

    @Property(tries = 500)
    @Label("isEncrypted should return false for blank strings")
    void isEncryptedShouldReturnFalseForBlank(
            @ForAll @IntRange(min = 1, max = 100) int spaces) {
        String blankString = " ".repeat(spaces);
        assertThat(PasswordCrypto.isEncrypted(blankString)).isFalse();
    }

    @Property(tries = 500)
    @Label("isEncrypted should return false for strings shorter than minimum length")
    void isEncryptedShouldReturnFalseForShortStrings(
            @ForAll @StringLength(min = 1, max = 59) @From("base64Chars") String shortString) {
        assertThat(PasswordCrypto.isEncrypted(shortString)).isFalse();
    }

    @Property(tries = 500)
    @Label("isEncrypted should return false for non-Base64 strings")
    void isEncryptedShouldReturnFalseForNonBase64(
            @ForAll @StringLength(min = 60, max = 200) String text) {
        // Filter to ensure we have non-base64 characters
        Assume.that(text.matches(".*[^A-Za-z0-9+/=].*"));
        assertThat(PasswordCrypto.isEncrypted(text)).isFalse();
    }

    @Property(tries = 500)
    @Label("isEncrypted should return true for valid Base64 strings of sufficient length")
    void isEncryptedShouldReturnTrueForValidBase64(
            @ForAll @StringLength(min = 60, max = 200) @From("base64Chars") String base64String) {
        // Append padding if needed to make it valid-looking Base64
        String paddedString = ensureValidBase64Padding(base64String);
        assertThat(PasswordCrypto.isEncrypted(paddedString)).isTrue();
    }

    @Property(tries = 300)
    @Label("isEncrypted should never throw exception")
    void isEncryptedShouldNeverThrow(@ForAll String anyString) {
        assertThatCode(() -> PasswordCrypto.isEncrypted(anyString)).doesNotThrowAnyException();
    }

    // =========================================================================
    // ENCRYPT IF NEEDED PROPERTIES
    // =========================================================================

    @Property(tries = 300)
    @Label("encryptIfNeeded should return null for null input")
    void encryptIfNeededShouldReturnNullForNull() {
        assertThat(PasswordCrypto.encryptIfNeeded(null)).isNull();
    }

    @Property(tries = 300)
    @Label("encryptIfNeeded should return empty for empty input")
    void encryptIfNeededShouldReturnEmptyForEmpty() {
        assertThat(PasswordCrypto.encryptIfNeeded("")).isEmpty();
    }

    @Property(tries = 300)
    @Label("encryptIfNeeded should return blank input unchanged")
    void encryptIfNeededShouldReturnBlankUnchanged(
            @ForAll @IntRange(min = 1, max = 50) int spaces) {
        String blankString = " ".repeat(spaces);
        assertThat(PasswordCrypto.encryptIfNeeded(blankString)).isEqualTo(blankString);
    }

    @Property(tries = 300)
    @Label("encryptIfNeeded should return already encrypted text unchanged")
    void encryptIfNeededShouldReturnEncryptedUnchanged(
            @ForAll @StringLength(min = 60, max = 200) @From("base64Chars") String base64String) {
        String paddedString = ensureValidBase64Padding(base64String);
        // If isEncrypted returns true, encryptIfNeeded should return the same value
        if (PasswordCrypto.isEncrypted(paddedString)) {
            assertThat(PasswordCrypto.encryptIfNeeded(paddedString)).isEqualTo(paddedString);
        }
    }

    // =========================================================================
    // DECRYPT IF NEEDED PROPERTIES
    // =========================================================================

    @Property(tries = 300)
    @Label("decryptIfNeeded should return null for null input")
    void decryptIfNeededShouldReturnNullForNull() {
        assertThat(PasswordCrypto.decryptIfNeeded(null)).isNull();
    }

    @Property(tries = 300)
    @Label("decryptIfNeeded should return empty for empty input")
    void decryptIfNeededShouldReturnEmptyForEmpty() {
        assertThat(PasswordCrypto.decryptIfNeeded("")).isEmpty();
    }

    @Property(tries = 300)
    @Label("decryptIfNeeded should return blank input unchanged")
    void decryptIfNeededShouldReturnBlankUnchanged(
            @ForAll @IntRange(min = 1, max = 50) int spaces) {
        String blankString = " ".repeat(spaces);
        assertThat(PasswordCrypto.decryptIfNeeded(blankString)).isEqualTo(blankString);
    }

    @Property(tries = 300)
    @Label("decryptIfNeeded should return non-encrypted text unchanged")
    void decryptIfNeededShouldReturnNonEncryptedUnchanged(
            @ForAll @StringLength(min = 1, max = 59) @AlphaChars String shortText) {
        // Short text should not be considered encrypted
        assertThat(PasswordCrypto.decryptIfNeeded(shortText)).isEqualTo(shortText);
    }

    @Property(tries = 300)
    @Label("decryptIfNeeded should never throw exception")
    void decryptIfNeededShouldNeverThrow(@ForAll String anyString) {
        assertThatCode(() -> PasswordCrypto.decryptIfNeeded(anyString)).doesNotThrowAnyException();
    }

    // =========================================================================
    // IS MASTER PASSWORD CONFIGURED PROPERTIES
    // =========================================================================

    @Property(tries = 100)
    @Label("isMasterPasswordConfigured should never throw exception")
    void isMasterPasswordConfiguredShouldNeverThrow() {
        assertThatCode(() -> PasswordCrypto.isMasterPasswordConfigured()).doesNotThrowAnyException();
    }

    // =========================================================================
    // INVARIANT PROPERTIES
    // =========================================================================

    @Property(tries = 300)
    @Label("Short strings should never be considered encrypted")
    void shortStringsShouldNeverBeEncrypted(
            @ForAll @StringLength(min = 0, max = 59) String shortString) {
        assertThat(PasswordCrypto.isEncrypted(shortString)).isFalse();
    }

    @Property(tries = 300)
    @Label("Strings with special characters should not be considered encrypted")
    void stringsWithSpecialCharsShouldNotBeEncrypted(
            @ForAll @StringLength(min = 60, max = 200) String text) {
        // If contains characters not in Base64 alphabet, should not be encrypted
        if (text.matches(".*[^A-Za-z0-9+/=].*")) {
            assertThat(PasswordCrypto.isEncrypted(text)).isFalse();
        }
    }

    // =========================================================================
    // ARBITRARIES (DATA GENERATORS)
    // =========================================================================

    @Provide
    Arbitrary<String> base64Chars() {
        return Arbitraries.strings()
                .withChars("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/")
                .ofMinLength(1)
                .ofMaxLength(200);
    }

    /**
     * Ensures the string ends with valid Base64 padding.
     */
    private String ensureValidBase64Padding(String input) {
        // Remove any existing padding
        String noPadding = input.replaceAll("=+$", "");
        // Calculate required padding
        int remainder = noPadding.length() % 4;
        if (remainder == 0) {
            return noPadding;
        }
        int paddingNeeded = 4 - remainder;
        return noPadding + "=".repeat(paddingNeeded);
    }
}
