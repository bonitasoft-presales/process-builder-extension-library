package com.bonitasoft.processbuilder.extension;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Utility class for secure password encryption and decryption.
 * <p>
 * Uses the environment variable {@code MASTER_BONITA_PWD} as the master password
 * to derive encryption keys using PBKDF2. This approach eliminates the need to
 * store encryption keys in the database.
 * </p>
 *
 * <p><b>Usage:</b></p>
 * <pre>{@code
 * // Encrypt a password before storing in database
 * String encrypted = PasswordCrypto.encrypt("myPassword123");
 *
 * // Decrypt when needed
 * String decrypted = PasswordCrypto.decrypt(encrypted);
 *
 * // Safe methods that check if already encrypted/decrypted
 * String safeEncrypted = PasswordCrypto.encryptIfNeeded(text);
 * String safeDecrypted = PasswordCrypto.decryptIfNeeded(text);
 * }</pre>
 *
 * <p><b>Configuration:</b></p>
 * <p>Set the environment variable before starting the server:</p>
 * <pre>{@code
 * export MASTER_BONITA_PWD="YourSecureMasterPassword123!"
 * }</pre>
 *
 * @author Bonitasoft
 * @since 1.0
 */
public final class PasswordCrypto {

    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final String KEY_ALGORITHM = "AES";
    private static final String KEY_DERIVATION_ALGORITHM = "PBKDF2WithHmacSHA256";

    private static final int KEY_LENGTH_BITS = 256;
    private static final int GCM_IV_LENGTH_BYTES = 12;
    private static final int GCM_TAG_LENGTH_BITS = 128;
    private static final int SALT_LENGTH_BYTES = 16;
    private static final int PBKDF2_ITERATIONS = 310_000;

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    /**
     * Environment variable name for the master password.
     */
    public static final String ENV_VAR_NAME = "MASTER_BONITA_PWD";

    /**
     * Minimum length for encrypted Base64 output (salt + iv + tag + minimal data).
     */
    private static final int MIN_ENCRYPTED_LENGTH = 60;

    private PasswordCrypto() {
        throw new UnsupportedOperationException(
            "This is a " + this.getClass().getSimpleName() + " class and cannot be instantiated."
        );
    }

    /**
     * Encrypts the given text using the master password from environment variable.
     *
     * @param plainText the text to encrypt (must not be null)
     * @return the encrypted text as Base64 string
     * @throws IllegalArgumentException if plainText is null
     * @throws CryptoException if master password is not configured or encryption fails
     */
    public static String encrypt(String plainText) {
        if (plainText == null) {
            throw new IllegalArgumentException("Plain text cannot be null");
        }
        return encryptWithPassword(plainText, getMasterPassword());
    }

    /**
     * Encrypts the given text using the provided master password.
     * <p>
     * Package-private for testing purposes.
     * </p>
     *
     * @param plainText the text to encrypt (must not be null)
     * @param masterPassword the master password to use for encryption
     * @return the encrypted text as Base64 string
     * @throws IllegalArgumentException if plainText is null
     * @throws CryptoException if encryption fails
     */
    static String encryptWithPassword(String plainText, String masterPassword) {
        if (plainText == null) {
            throw new IllegalArgumentException("Plain text cannot be null");
        }

        try {
            byte[] salt = generateRandomBytes(SALT_LENGTH_BYTES);
            byte[] iv = generateRandomBytes(GCM_IV_LENGTH_BYTES);

            var key = deriveKey(masterPassword, salt);
            var cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv));

            byte[] cipherText = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

            ByteBuffer buffer = ByteBuffer.allocate(salt.length + iv.length + cipherText.length);
            buffer.put(salt);
            buffer.put(iv);
            buffer.put(cipherText);

            return Base64.getEncoder().encodeToString(buffer.array());

        } catch (GeneralSecurityException e) {
            throw new CryptoException("Encryption failed", e);
        }
    }

    /**
     * Decrypts the given encrypted text using the master password from environment variable.
     *
     * @param encryptedText the Base64 encrypted text to decrypt
     * @return the decrypted plain text
     * @throws IllegalArgumentException if encryptedText is null or empty
     * @throws CryptoException if master password is not configured or decryption fails
     */
    public static String decrypt(String encryptedText) {
        if (encryptedText == null || encryptedText.isBlank()) {
            throw new IllegalArgumentException("Encrypted text cannot be null or empty");
        }
        return decryptWithPassword(encryptedText, getMasterPassword());
    }

    /**
     * Decrypts the given encrypted text using the provided master password.
     * <p>
     * Package-private for testing purposes.
     * </p>
     *
     * @param encryptedText the Base64 encrypted text to decrypt
     * @param masterPassword the master password to use for decryption
     * @return the decrypted plain text
     * @throws IllegalArgumentException if encryptedText is null or empty
     * @throws CryptoException if decryption fails
     */
    static String decryptWithPassword(String encryptedText, String masterPassword) {
        if (encryptedText == null || encryptedText.isBlank()) {
            throw new IllegalArgumentException("Encrypted text cannot be null or empty");
        }

        try {
            byte[] decoded = Base64.getDecoder().decode(encryptedText);

            int minLength = SALT_LENGTH_BYTES + GCM_IV_LENGTH_BYTES + 1;
            if (decoded.length < minLength) {
                throw new CryptoException("Invalid encrypted data: too short");
            }

            ByteBuffer buffer = ByteBuffer.wrap(decoded);

            byte[] salt = new byte[SALT_LENGTH_BYTES];
            buffer.get(salt);

            byte[] iv = new byte[GCM_IV_LENGTH_BYTES];
            buffer.get(iv);

            byte[] cipherText = new byte[buffer.remaining()];
            buffer.get(cipherText);

            var key = deriveKey(masterPassword, salt);
            var cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv));

            return new String(cipher.doFinal(cipherText), StandardCharsets.UTF_8);

        } catch (IllegalArgumentException e) {
            throw new CryptoException("Invalid Base64 encoded input", e);
        } catch (GeneralSecurityException e) {
            throw new CryptoException("Decryption failed: wrong master password or corrupted data", e);
        }
    }

    /**
     * Encrypts the text only if it does not appear to be already encrypted.
     *
     * @param text the text to encrypt
     * @return the encrypted text, or the original if null/empty/already encrypted
     */
    public static String encryptIfNeeded(String text) {
        if (text == null || text.isBlank()) {
            return text;
        }
        if (isEncrypted(text)) {
            return text;
        }
        return encrypt(text);
    }

    /**
     * Decrypts the text only if it appears to be encrypted.
     *
     * @param text the text to decrypt
     * @return the decrypted text, or the original if null/empty/not encrypted
     */
    public static String decryptIfNeeded(String text) {
        if (text == null || text.isBlank()) {
            return text;
        }
        if (!isEncrypted(text)) {
            return text;
        }
        try {
            return decrypt(text);
        } catch (CryptoException e) {
            return text;
        }
    }

    /**
     * Checks if the master password environment variable is configured.
     *
     * @return true if configured, false otherwise
     */
    public static boolean isMasterPasswordConfigured() {
        String masterPassword = System.getenv(ENV_VAR_NAME);
        return masterPassword != null && !masterPassword.isBlank();
    }

    /**
     * Checks if the given text appears to be encrypted.
     * <p>
     * This is a heuristic check based on Base64 format and minimum length.
     * </p>
     *
     * @param text the text to check
     * @return true if the text appears to be encrypted
     */
    public static boolean isEncrypted(String text) {
        if (text == null || text.isBlank() || text.length() < MIN_ENCRYPTED_LENGTH) {
            return false;
        }
        return text.matches("^[A-Za-z0-9+/]+=*$");
    }

    private static String getMasterPassword() {
        String masterPassword = System.getenv(ENV_VAR_NAME);
        if (masterPassword == null || masterPassword.isBlank()) {
            throw new CryptoException(
                "Master password not configured. Set environment variable: " + ENV_VAR_NAME
            );
        }
        return masterPassword;
    }

    private static SecretKeySpec deriveKey(String password, byte[] salt) throws GeneralSecurityException {
        var spec = new PBEKeySpec(password.toCharArray(), salt, PBKDF2_ITERATIONS, KEY_LENGTH_BITS);
        var factory = SecretKeyFactory.getInstance(KEY_DERIVATION_ALGORITHM);
        byte[] keyBytes = factory.generateSecret(spec).getEncoded();
        return new SecretKeySpec(keyBytes, KEY_ALGORITHM);
    }

    private static byte[] generateRandomBytes(int length) {
        byte[] bytes = new byte[length];
        SECURE_RANDOM.nextBytes(bytes);
        return bytes;
    }

    /**
     * Exception thrown when cryptographic operations fail.
     */
    public static class CryptoException extends RuntimeException {

        private static final long serialVersionUID = 1L;

        /**
         * Constructs a new CryptoException with the specified detail message.
         *
         * @param message the detail message
         */
        public CryptoException(String message) {
            super(message);
        }

        /**
         * Constructs a new CryptoException with the specified detail message and cause.
         *
         * @param message the detail message
         * @param cause the cause of the exception
         */
        public CryptoException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
