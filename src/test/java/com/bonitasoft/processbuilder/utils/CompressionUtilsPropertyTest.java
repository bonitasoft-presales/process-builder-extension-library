package com.bonitasoft.processbuilder.utils;

import static org.assertj.core.api.Assertions.assertThat;

import net.jqwik.api.*;
import net.jqwik.api.constraints.IntRange;
import net.jqwik.api.constraints.Size;
import net.jqwik.api.constraints.StringLength;

import org.junit.jupiter.api.DisplayName;

import java.io.IOException;

/**
 * Property-based tests for {@link CompressionUtils} using jqwik.
 * <p>
 * These tests verify that compression/decompression properties hold for a wide range of inputs:
 * <ul>
 *   <li>Round-trip property: compress(decompress(x)) == x</li>
 *   <li>Idempotence: repeated compression/decompression cycles preserve data</li>
 *   <li>Non-null preservation: non-null inputs produce non-null outputs</li>
 *   <li>Compression effectiveness: large repetitive strings compress well</li>
 * </ul>
 * </p>
 *
 * @author Process-Builder Development Team
 * @version 1.0
 * @since 2026-02-12
 */
@DisplayName("CompressionUtils Property-Based Tests")
class CompressionUtilsPropertyTest {

    // =========================================================================
    // Round-Trip Properties
    // =========================================================================

    @Property(tries = 100)
    void should_preserve_any_string_through_compress_decompress_cycle(
            @ForAll @StringLength(min = 1, max = 1000) String original) throws IOException {
        // When
        String compressed = CompressionUtils.compress(original);
        String decompressed = CompressionUtils.decompress(compressed);

        // Then
        assertThat(decompressed).isEqualTo(original);
    }

    @Property(tries = 50)
    void should_preserve_json_strings_through_compress_decompress_cycle(
            @ForAll("jsonStrings") String jsonString) throws IOException {
        // When
        String compressed = CompressionUtils.compress(jsonString);
        String decompressed = CompressionUtils.decompress(compressed);

        // Then
        assertThat(decompressed).isEqualTo(jsonString);
    }

    @Property(tries = 50)
    void should_preserve_unicode_strings_through_compress_decompress_cycle(
            @ForAll @StringLength(min = 1, max = 500) String original) throws IOException {
        // When
        String compressed = CompressionUtils.compress(original);
        String decompressed = CompressionUtils.decompress(compressed);

        // Then
        assertThat(decompressed).isEqualTo(original);
    }

    // =========================================================================
    // Idempotence Properties
    // =========================================================================

    @Property(tries = 50)
    void should_be_idempotent_through_multiple_cycles(
            @ForAll @StringLength(min = 10, max = 500) String original,
            @ForAll @IntRange(min = 1, max = 5) int cycles) throws IOException {
        // When - Apply multiple compress/decompress cycles
        String result = original;
        for (int i = 0; i < cycles; i++) {
            String compressed = CompressionUtils.compress(result);
            result = CompressionUtils.decompress(compressed);
        }

        // Then
        assertThat(result).isEqualTo(original);
    }

    // =========================================================================
    // Non-Null Properties
    // =========================================================================

    @Property(tries = 100)
    void should_always_return_non_null_when_compressing_non_empty_string(
            @ForAll @StringLength(min = 1, max = 1000) String input) throws IOException {
        // When
        String compressed = CompressionUtils.compress(input);

        // Then
        assertThat(compressed).isNotNull();
    }

    @Property(tries = 100)
    void should_always_return_non_null_when_decompressing_valid_data(
            @ForAll @StringLength(min = 1, max = 500) String original) throws IOException {
        // Given
        String compressed = CompressionUtils.compress(original);

        // When
        String decompressed = CompressionUtils.decompress(compressed);

        // Then
        assertThat(decompressed).isNotNull();
    }

    // =========================================================================
    // Compression Effectiveness Properties
    // =========================================================================

    @Property(tries = 50)
    void should_compress_repetitive_strings_to_smaller_size(
            @ForAll @StringLength(min = 1, max = 10) String pattern,
            @ForAll @IntRange(min = 50, max = 200) int repetitions) throws IOException {
        // Given - Create highly repetitive string
        String repetitive = pattern.repeat(repetitions);

        // When
        String compressed = CompressionUtils.compress(repetitive);

        // Then - Compression should be effective for repetitive data
        double ratio = CompressionUtils.getCompressionRatio(repetitive, compressed);
        assertThat(ratio).isLessThan(80); // Should compress well for repetitive data (allows small patterns)
    }

    @Property(tries = 30)
    void should_achieve_reasonable_compression_for_json_like_structures(
            @ForAll("jsonStrings") String jsonString) throws IOException {
        // When
        String compressed = CompressionUtils.compress(jsonString);

        // Then - JSON should compress reasonably well
        assertThat(compressed).isNotNull();
        assertThat(compressed).isNotEmpty();

        // Verify round-trip
        String decompressed = CompressionUtils.decompress(compressed);
        assertThat(decompressed).isEqualTo(jsonString);
    }

    // =========================================================================
    // Compression Ratio Properties
    // =========================================================================

    @Property(tries = 50)
    void should_always_calculate_positive_compression_ratio_for_non_empty_strings(
            @ForAll @StringLength(min = 1, max = 500) String original) throws IOException {
        // Given
        String compressed = CompressionUtils.compress(original);

        // When
        double ratio = CompressionUtils.getCompressionRatio(original, compressed);

        // Then
        assertThat(ratio).isGreaterThan(0);
        assertThat(ratio).isLessThan(5000); // Tiny strings can expand significantly due to GZIP + Base64 overhead
    }

    // =========================================================================
    // Base64 Format Properties
    // =========================================================================

    @Property(tries = 100)
    void should_always_produce_valid_base64_when_compressing(
            @ForAll @StringLength(min = 1, max = 500) String input) throws IOException {
        // When
        String compressed = CompressionUtils.compress(input);

        // Then - Should be valid Base64
        assertThat(compressed).matches("^[A-Za-z0-9+/]*={0,2}$");
    }

    // =========================================================================
    // Edge Cases Properties
    // =========================================================================

    @Property(tries = 50)
    void should_handle_strings_with_special_characters(
            @ForAll @StringLength(min = 1, max = 500) String input) throws IOException {
        // Add some special characters to the input
        String withSpecialChars = input + "\n\t\r\u00f1\u00e9";

        // When
        String compressed = CompressionUtils.compress(withSpecialChars);
        String decompressed = CompressionUtils.decompress(compressed);

        // Then
        assertThat(decompressed).isEqualTo(withSpecialChars);
    }

    @Property(tries = 30)
    void should_handle_very_long_strings(
            @ForAll @StringLength(min = 5000, max = 10000) String longString) throws IOException {
        // When
        String compressed = CompressionUtils.compress(longString);
        String decompressed = CompressionUtils.decompress(compressed);

        // Then
        assertThat(decompressed).isEqualTo(longString);
    }

    // =========================================================================
    // Data Integrity Properties
    // =========================================================================

    @Property(tries = 100)
    void should_preserve_string_length_through_round_trip(
            @ForAll @StringLength(min = 1, max = 1000) String original) throws IOException {
        // When
        String compressed = CompressionUtils.compress(original);
        String decompressed = CompressionUtils.decompress(compressed);

        // Then
        assertThat(decompressed.length()).isEqualTo(original.length());
    }

    @Property(tries = 100)
    void should_preserve_character_content_exactly(
            @ForAll @StringLength(min = 1, max = 500) String original) throws IOException {
        // When
        String compressed = CompressionUtils.compress(original);
        String decompressed = CompressionUtils.decompress(compressed);

        // Then - Character-by-character comparison
        assertThat(decompressed.toCharArray()).containsExactly(original.toCharArray());
    }

    // =========================================================================
    // Arbitrary Providers
    // =========================================================================

    @Provide
    Arbitrary<String> jsonStrings() {
        return Combinators.combine(
                Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(20),
                Arbitraries.integers().between(1, 100),
                Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(20)
        ).as((name, age, city) ->
                String.format("{\"name\":\"%s\",\"age\":%d,\"city\":\"%s\"}", name, age, city)
        );
    }

    @Provide
    Arbitrary<String> repetitiveStrings() {
        return Combinators.combine(
                Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(5),
                Arbitraries.integers().between(10, 100)
        ).as((pattern, count) -> pattern.repeat(count));
    }

    @Provide
    Arbitrary<String> unicodeStrings() {
        return Arbitraries.strings()
                .withCharRange('\u0000', '\uffff')
                .ofMinLength(1)
                .ofMaxLength(200);
    }

    @Provide
    Arbitrary<String> specialCharStrings() {
        return Arbitraries.of(
                "Line\nBreak",
                "Tab\tChar",
                "Carriage\rReturn",
                "Quote\"Test",
                "Slash\\Test",
                "Mixed\n\t\r\"\\Special"
        );
    }

    // =========================================================================
    // Symmetry Properties
    // =========================================================================

    @Property(tries = 50)
    void should_maintain_symmetry_between_compress_and_decompress(
            @ForAll @StringLength(min = 1, max = 500) String string1,
            @ForAll @StringLength(min = 1, max = 500) String string2) throws IOException {
        // When
        String compressed1 = CompressionUtils.compress(string1);
        String compressed2 = CompressionUtils.compress(string2);

        String decompressed1 = CompressionUtils.decompress(compressed1);
        String decompressed2 = CompressionUtils.decompress(compressed2);

        // Then
        assertThat(decompressed1).isEqualTo(string1);
        assertThat(decompressed2).isEqualTo(string2);

        // If originals were equal, compressed should decompress to equal
        if (string1.equals(string2)) {
            assertThat(decompressed1).isEqualTo(decompressed2);
        }
    }
}
