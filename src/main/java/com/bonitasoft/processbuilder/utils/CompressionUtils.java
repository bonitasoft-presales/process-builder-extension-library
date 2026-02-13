package com.bonitasoft.processbuilder.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Utility class for compressing and decompressing strings using GZIP compression and Base64 encoding.
 * <p>
 * This class provides methods to:
 * <ul>
 *   <li>Compress strings (typically JSON) to reduce size for transmission</li>
 *   <li>Decompress previously compressed strings back to original form</li>
 * </ul>
 * <p>
 * The compression process:
 * <ol>
 *   <li>Converts string to UTF-8 bytes</li>
 *   <li>Compresses using GZIP</li>
 *   <li>Encodes compressed bytes to Base64 for safe string transmission</li>
 * </ol>
 * <p>
 * Typical compression ratios for JSON data: 70-90% size reduction.
 * </p>
 *
 * @author Process-Builder Development Team
 * @version 1.0
 * @since 2026-02-12
 */
public final class CompressionUtils {

    private static final int BUFFER_SIZE = 1024;

    /**
     * Private constructor to prevent instantiation.
     * This is a utility class with only static methods.
     */
    private CompressionUtils() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Compresses a string using GZIP compression and encodes it as Base64.
     * <p>
     * This method is particularly useful for compressing JSON strings or other text data
     * before sending them over the network or storing them in a database.
     * </p>
     * <p>
     * Example:
     * <pre>
     * String json = "{\"data\": \"large content...\"}";
     * String compressed = CompressionUtils.compress(json);
     * // compressed is now a Base64-encoded GZIP string, typically 70-90% smaller
     * </pre>
     *
     * @param input The string to compress (must not be null)
     * @return Base64-encoded GZIP-compressed string
     * @throws IllegalArgumentException if input is null
     * @throws IOException if compression fails
     */
    public static String compress(String input) throws IOException {
        if (input == null) {
            throw new IllegalArgumentException("Input string cannot be null");
        }

        if (input.isEmpty()) {
            return ""; // Empty string compresses to empty string
        }

        byte[] inputBytes = input.getBytes(StandardCharsets.UTF_8);

        try (ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
             GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteOutputStream)) {

            gzipOutputStream.write(inputBytes);
            gzipOutputStream.finish();

            byte[] compressedBytes = byteOutputStream.toByteArray();
            return Base64.getEncoder().encodeToString(compressedBytes);
        }
    }

    /**
     * Decompresses a Base64-encoded GZIP-compressed string back to its original form.
     * <p>
     * This method reverses the compression performed by {@link #compress(String)}.
     * The input must be a valid Base64-encoded GZIP string, otherwise decompression will fail.
     * </p>
     * <p>
     * Example:
     * <pre>
     * String compressed = "H4sIAAAAAAAA/..."; // Base64 GZIP string
     * String original = CompressionUtils.decompress(compressed);
     * // original is now the decompressed string
     * </pre>
     *
     * @param compressedBase64 The Base64-encoded GZIP-compressed string (must not be null)
     * @return The decompressed original string
     * @throws IllegalArgumentException if compressedBase64 is null or has invalid Base64 format
     * @throws IOException if decompression fails (corrupted data, invalid GZIP format)
     */
    public static String decompress(String compressedBase64) throws IOException {
        if (compressedBase64 == null) {
            throw new IllegalArgumentException("Compressed input cannot be null");
        }

        if (compressedBase64.isEmpty()) {
            return ""; // Empty string decompresses to empty string
        }

        try {
            byte[] compressedBytes = Base64.getDecoder().decode(compressedBase64);

            try (ByteArrayInputStream byteInputStream = new ByteArrayInputStream(compressedBytes);
                 GZIPInputStream gzipInputStream = new GZIPInputStream(byteInputStream);
                 ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

                byte[] buffer = new byte[BUFFER_SIZE];
                int len;
                while ((len = gzipInputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, len);
                }

                return outputStream.toString(StandardCharsets.UTF_8.name());
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid Base64 format: " + e.getMessage(), e);
        }
    }

    /**
     * Calculates the compression ratio as a percentage.
     * <p>
     * This is a utility method for monitoring and logging compression effectiveness.
     * </p>
     * <p>
     * Example:
     * <pre>
     * String original = "{\"data\": \"...\"}"; // 1000 bytes
     * String compressed = CompressionUtils.compress(original);
     * double ratio = CompressionUtils.getCompressionRatio(original, compressed);
     * // ratio might be 15.5 (meaning compressed is 15.5% of original size)
     * </pre>
     *
     * @param originalString The original uncompressed string
     * @param compressedBase64 The compressed Base64 string
     * @return Compression ratio as percentage (0-100), where lower is better
     * @throws IllegalArgumentException if either parameter is null
     */
    public static double getCompressionRatio(String originalString, String compressedBase64) {
        if (originalString == null || compressedBase64 == null) {
            throw new IllegalArgumentException("Parameters cannot be null");
        }

        if (originalString.isEmpty() || compressedBase64.isEmpty()) {
            return 0.0;
        }

        int originalSize = originalString.getBytes(StandardCharsets.UTF_8).length;
        int compressedSize = compressedBase64.getBytes(StandardCharsets.UTF_8).length;

        return (100.0 * compressedSize) / originalSize;
    }
}
