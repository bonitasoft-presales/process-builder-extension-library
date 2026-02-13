package com.bonitasoft.processbuilder.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Comprehensive unit tests for {@link CompressionUtils}.
 * <p>
 * Tests cover:
 * <ul>
 *   <li>Compression and decompression of various string sizes</li>
 *   <li>Round-trip verification (compress -> decompress = original)</li>
 *   <li>Edge cases (null, empty strings)</li>
 *   <li>Error handling (invalid Base64, corrupted GZIP)</li>
 *   <li>Compression ratio calculations</li>
 *   <li>JSON-specific scenarios</li>
 * </ul>
 * </p>
 *
 * @author Process-Builder Development Team
 * @version 1.0
 * @since 2026-02-12
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("CompressionUtils - Unit Tests")
class CompressionUtilsTest {

    // Test data constants
    private static final String SHORT_STRING = "Hello, World!";
    private static final String JSON_SIMPLE = "{\"name\":\"John\",\"age\":30}";
    private static final String JSON_NESTED = "{\"user\":{\"name\":\"John\",\"address\":{\"city\":\"NYC\",\"zip\":\"10001\"}},\"orders\":[1,2,3]}";
    private static final String LARGE_JSON = "{\"data\":\"" + "x".repeat(10000) + "\"}";
    private static final String SPECIAL_CHARS = "Special: \n\t\r\u00f1\u00e9\u00fc";
    private static final String UNICODE_STRING = "\u4e2d\u6587\u6d4b\u8bd5 \u0639\u0631\u0628\u064a \u0420\u0443\u0441\u0441\u043a\u0438\u0439";

    // =========================================================================
    // Utility Class Pattern Tests
    // =========================================================================

    @Test
    @DisplayName("should_throw_exception_when_attempting_to_instantiate")
    void should_throw_exception_when_attempting_to_instantiate() throws Exception {
        Constructor<CompressionUtils> constructor = CompressionUtils.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        assertThatThrownBy(constructor::newInstance)
                .isInstanceOf(InvocationTargetException.class)
                .hasCauseInstanceOf(UnsupportedOperationException.class);
    }

    // =========================================================================
    // Compression Tests - Basic Functionality
    // =========================================================================

    @Test
    @DisplayName("should_compress_short_string_successfully")
    void should_compress_short_string_successfully() throws IOException {
        // When
        String compressed = CompressionUtils.compress(SHORT_STRING);

        // Then
        assertThat(compressed).isNotNull();
        assertThat(compressed).isNotEmpty();
        assertThat(compressed).isNotEqualTo(SHORT_STRING);
        assertThat(compressed).isBase64();
    }

    @Test
    @DisplayName("should_compress_simple_json_successfully")
    void should_compress_simple_json_successfully() throws IOException {
        // When
        String compressed = CompressionUtils.compress(JSON_SIMPLE);

        // Then
        assertThat(compressed).isNotNull();
        assertThat(compressed).isNotEmpty();
        assertThat(compressed).isBase64();
    }

    @Test
    @DisplayName("should_compress_nested_json_successfully")
    void should_compress_nested_json_successfully() throws IOException {
        // When
        String compressed = CompressionUtils.compress(JSON_NESTED);

        // Then
        assertThat(compressed).isNotNull();
        assertThat(compressed).isNotEmpty();
        assertThat(compressed).isBase64();
    }

    @Test
    @DisplayName("should_compress_large_json_to_smaller_size")
    void should_compress_large_json_to_smaller_size() throws IOException {
        // When
        String compressed = CompressionUtils.compress(LARGE_JSON);

        // Then
        assertThat(compressed).isNotNull();
        assertThat(compressed.length()).isLessThan(LARGE_JSON.length());
    }

    @Test
    @DisplayName("should_compress_empty_string_to_empty_string")
    void should_compress_empty_string_to_empty_string() throws IOException {
        // When
        String compressed = CompressionUtils.compress("");

        // Then
        assertThat(compressed).isEmpty();
    }

    @Test
    @DisplayName("should_throw_exception_when_compressing_null")
    void should_throw_exception_when_compressing_null() {
        // When / Then
        assertThatThrownBy(() -> CompressionUtils.compress(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Input string cannot be null");
    }

    @Test
    @DisplayName("should_compress_string_with_special_characters")
    void should_compress_string_with_special_characters() throws IOException {
        // When
        String compressed = CompressionUtils.compress(SPECIAL_CHARS);

        // Then
        assertThat(compressed).isNotNull();
        assertThat(compressed).isNotEmpty();
        assertThat(compressed).isBase64();
    }

    @Test
    @DisplayName("should_compress_unicode_string_successfully")
    void should_compress_unicode_string_successfully() throws IOException {
        // When
        String compressed = CompressionUtils.compress(UNICODE_STRING);

        // Then
        assertThat(compressed).isNotNull();
        assertThat(compressed).isNotEmpty();
        assertThat(compressed).isBase64();
    }

    // =========================================================================
    // Decompression Tests - Basic Functionality
    // =========================================================================

    @Test
    @DisplayName("should_decompress_short_string_successfully")
    void should_decompress_short_string_successfully() throws IOException {
        // Given
        String compressed = CompressionUtils.compress(SHORT_STRING);

        // When
        String decompressed = CompressionUtils.decompress(compressed);

        // Then
        assertThat(decompressed).isEqualTo(SHORT_STRING);
    }

    @Test
    @DisplayName("should_decompress_simple_json_successfully")
    void should_decompress_simple_json_successfully() throws IOException {
        // Given
        String compressed = CompressionUtils.compress(JSON_SIMPLE);

        // When
        String decompressed = CompressionUtils.decompress(compressed);

        // Then
        assertThat(decompressed).isEqualTo(JSON_SIMPLE);
    }

    @Test
    @DisplayName("should_decompress_nested_json_successfully")
    void should_decompress_nested_json_successfully() throws IOException {
        // Given
        String compressed = CompressionUtils.compress(JSON_NESTED);

        // When
        String decompressed = CompressionUtils.decompress(compressed);

        // Then
        assertThat(decompressed).isEqualTo(JSON_NESTED);
    }

    @Test
    @DisplayName("should_decompress_large_json_successfully")
    void should_decompress_large_json_successfully() throws IOException {
        // Given
        String compressed = CompressionUtils.compress(LARGE_JSON);

        // When
        String decompressed = CompressionUtils.decompress(compressed);

        // Then
        assertThat(decompressed).isEqualTo(LARGE_JSON);
    }

    @Test
    @DisplayName("should_decompress_empty_string_to_empty_string")
    void should_decompress_empty_string_to_empty_string() throws IOException {
        // When
        String decompressed = CompressionUtils.decompress("");

        // Then
        assertThat(decompressed).isEmpty();
    }

    @Test
    @DisplayName("should_throw_exception_when_decompressing_null")
    void should_throw_exception_when_decompressing_null() {
        // When / Then
        assertThatThrownBy(() -> CompressionUtils.decompress(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Compressed input cannot be null");
    }

    @Test
    @DisplayName("should_throw_exception_when_decompressing_invalid_base64")
    void should_throw_exception_when_decompressing_invalid_base64() {
        // Given
        String invalidBase64 = "This is not valid Base64!@#$%";

        // When / Then
        assertThatThrownBy(() -> CompressionUtils.decompress(invalidBase64))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid Base64 format");
    }

    @Test
    @DisplayName("should_throw_exception_when_decompressing_valid_base64_but_invalid_gzip")
    void should_throw_exception_when_decompressing_valid_base64_but_invalid_gzip() {
        // Given - Valid Base64 but not GZIP data
        String validBase64NotGzip = "SGVsbG8gV29ybGQh"; // "Hello World!" in Base64

        // When / Then
        assertThatThrownBy(() -> CompressionUtils.decompress(validBase64NotGzip))
                .isInstanceOf(IOException.class)
                .hasMessageContaining("Not in GZIP format");
    }

    @Test
    @DisplayName("should_decompress_string_with_special_characters")
    void should_decompress_string_with_special_characters() throws IOException {
        // Given
        String compressed = CompressionUtils.compress(SPECIAL_CHARS);

        // When
        String decompressed = CompressionUtils.decompress(compressed);

        // Then
        assertThat(decompressed).isEqualTo(SPECIAL_CHARS);
    }

    @Test
    @DisplayName("should_decompress_unicode_string_successfully")
    void should_decompress_unicode_string_successfully() throws IOException {
        // Given
        String compressed = CompressionUtils.compress(UNICODE_STRING);

        // When
        String decompressed = CompressionUtils.decompress(compressed);

        // Then
        assertThat(decompressed).isEqualTo(UNICODE_STRING);
    }

    // =========================================================================
    // Round-Trip Tests
    // =========================================================================

    @Test
    @DisplayName("should_preserve_data_integrity_through_compress_decompress_cycle")
    void should_preserve_data_integrity_through_compress_decompress_cycle() throws IOException {
        // Given
        String[] testStrings = {
                SHORT_STRING,
                JSON_SIMPLE,
                JSON_NESTED,
                SPECIAL_CHARS,
                UNICODE_STRING,
                LARGE_JSON
        };

        // When / Then
        for (String original : testStrings) {
            String compressed = CompressionUtils.compress(original);
            String decompressed = CompressionUtils.decompress(compressed);
            assertThat(decompressed).isEqualTo(original);
        }
    }

    @Test
    @DisplayName("should_handle_multiple_compress_decompress_cycles")
    void should_handle_multiple_compress_decompress_cycles() throws IOException {
        // Given
        String original = JSON_NESTED;

        // When - Multiple cycles
        String result = original;
        for (int i = 0; i < 5; i++) {
            String compressed = CompressionUtils.compress(result);
            result = CompressionUtils.decompress(compressed);
        }

        // Then
        assertThat(result).isEqualTo(original);
    }

    // =========================================================================
    // Compression Ratio Tests
    // =========================================================================

    @Test
    @DisplayName("should_calculate_compression_ratio_for_simple_json")
    void should_calculate_compression_ratio_for_simple_json() throws IOException {
        // Given
        String compressed = CompressionUtils.compress(JSON_SIMPLE);

        // When
        double ratio = CompressionUtils.getCompressionRatio(JSON_SIMPLE, compressed);

        // Then
        assertThat(ratio).isGreaterThan(0);
        assertThat(ratio).isLessThan(300); // Small JSON may expand due to GZIP + Base64 overhead
    }

    @Test
    @DisplayName("should_calculate_compression_ratio_for_large_json_showing_good_compression")
    void should_calculate_compression_ratio_for_large_json_showing_good_compression() throws IOException {
        // Given
        String compressed = CompressionUtils.compress(LARGE_JSON);

        // When
        double ratio = CompressionUtils.getCompressionRatio(LARGE_JSON, compressed);

        // Then
        assertThat(ratio).isLessThan(10); // Should compress to less than 10% for repetitive data
    }

    @Test
    @DisplayName("should_return_zero_ratio_for_empty_strings")
    void should_return_zero_ratio_for_empty_strings() {
        // When
        double ratio = CompressionUtils.getCompressionRatio("", "");

        // Then
        assertThat(ratio).isEqualTo(0.0);
    }

    @Test
    @DisplayName("should_throw_exception_when_calculating_ratio_with_null_original")
    void should_throw_exception_when_calculating_ratio_with_null_original() {
        // When / Then
        assertThatThrownBy(() -> CompressionUtils.getCompressionRatio(null, "compressed"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Parameters cannot be null");
    }

    @Test
    @DisplayName("should_throw_exception_when_calculating_ratio_with_null_compressed")
    void should_throw_exception_when_calculating_ratio_with_null_compressed() {
        // When / Then
        assertThatThrownBy(() -> CompressionUtils.getCompressionRatio("original", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Parameters cannot be null");
    }

    // =========================================================================
    // Performance and Size Tests
    // =========================================================================

    @Test
    @DisplayName("should_achieve_good_compression_for_repetitive_json")
    void should_achieve_good_compression_for_repetitive_json() throws IOException {
        // Given - Highly repetitive JSON (should compress well)
        String repetitiveJson = "{\"field\":\"" + "abc".repeat(1000) + "\"}";

        // When
        String compressed = CompressionUtils.compress(repetitiveJson);

        // Then
        double ratio = CompressionUtils.getCompressionRatio(repetitiveJson, compressed);
        assertThat(ratio).isLessThan(5); // Should achieve >95% compression for repetitive data
    }

    @Test
    @DisplayName("should_handle_very_small_strings_gracefully")
    void should_handle_very_small_strings_gracefully() throws IOException {
        // Given
        String tinyString = "a";

        // When
        String compressed = CompressionUtils.compress(tinyString);
        String decompressed = CompressionUtils.decompress(compressed);

        // Then
        assertThat(decompressed).isEqualTo(tinyString);
        // Note: Very small strings may actually grow when compressed due to GZIP header overhead
    }

    // =========================================================================
    // JSON-Specific Tests
    // =========================================================================

    @Test
    @DisplayName("should_compress_json_with_arrays_successfully")
    void should_compress_json_with_arrays_successfully() throws IOException {
        // Given
        String jsonWithArrays = "{\"items\":[{\"id\":1},{\"id\":2},{\"id\":3},{\"id\":4},{\"id\":5}]}";

        // When
        String compressed = CompressionUtils.compress(jsonWithArrays);
        String decompressed = CompressionUtils.decompress(compressed);

        // Then
        assertThat(decompressed).isEqualTo(jsonWithArrays);
    }

    @Test
    @DisplayName("should_compress_json_with_null_values_successfully")
    void should_compress_json_with_null_values_successfully() throws IOException {
        // Given
        String jsonWithNulls = "{\"name\":\"John\",\"age\":null,\"address\":null}";

        // When
        String compressed = CompressionUtils.compress(jsonWithNulls);
        String decompressed = CompressionUtils.decompress(compressed);

        // Then
        assertThat(decompressed).isEqualTo(jsonWithNulls);
    }

    @Test
    @DisplayName("should_compress_json_with_boolean_values_successfully")
    void should_compress_json_with_boolean_values_successfully() throws IOException {
        // Given
        String jsonWithBooleans = "{\"active\":true,\"verified\":false,\"premium\":true}";

        // When
        String compressed = CompressionUtils.compress(jsonWithBooleans);
        String decompressed = CompressionUtils.decompress(compressed);

        // Then
        assertThat(decompressed).isEqualTo(jsonWithBooleans);
    }

    @Test
    @DisplayName("should_compress_json_with_numbers_successfully")
    void should_compress_json_with_numbers_successfully() throws IOException {
        // Given
        String jsonWithNumbers = "{\"integer\":42,\"decimal\":3.14159,\"negative\":-100,\"scientific\":1.5e10}";

        // When
        String compressed = CompressionUtils.compress(jsonWithNumbers);
        String decompressed = CompressionUtils.decompress(compressed);

        // Then
        assertThat(decompressed).isEqualTo(jsonWithNumbers);
    }
}
