package com.bonitasoft.processbuilder.extension;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

import static org.assertj.core.api.Assertions.*;

/**
 * Property-based tests for {@link PBStringUtils} utility class.
 * Tests invariants that must hold for any valid input.
 *
 * @author Bonitasoft
 * @since 1.0
 */
@Label("PBStringUtils Property-Based Tests")
class PBStringUtilsPropertyTest {

    // =========================================================================
    // UTILITY CLASS INVARIANT PROPERTIES
    // =========================================================================

    @Property(tries = 100)
    @Label("Utility class should not be instantiable")
    void utilityClassShouldNotBeInstantiable() {
        assertThatThrownBy(() -> {
            var constructor = PBStringUtils.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            constructor.newInstance();
        }).hasCauseInstanceOf(UnsupportedOperationException.class);
    }

    // =========================================================================
    // normalizeTitleCase() PROPERTIES
    // =========================================================================

    @Property(tries = 1000)
    @Label("normalizeTitleCase should return null for null input")
    void normalizeTitleCaseShouldReturnNullForNull() {
        assertThat(PBStringUtils.normalizeTitleCase(null)).isNull();
    }

    @Property(tries = 1000)
    @Label("normalizeTitleCase should return empty for empty input")
    void normalizeTitleCaseShouldReturnEmptyForEmpty() {
        assertThat(PBStringUtils.normalizeTitleCase("")).isEmpty();
    }

    @Property(tries = 1000)
    @Label("normalizeTitleCase result should start with uppercase")
    void normalizeTitleCaseShouldStartWithUppercase(
            @ForAll @StringLength(min = 1, max = 50) @AlphaChars String input) {
        String result = PBStringUtils.normalizeTitleCase(input);
        assertThat(Character.isUpperCase(result.charAt(0))).isTrue();
    }

    @Property(tries = 1000)
    @Label("normalizeTitleCase rest of string should be lowercase")
    void normalizeTitleCaseRestShouldBeLowercase(
            @ForAll @StringLength(min = 2, max = 50) @AlphaChars String input) {
        String result = PBStringUtils.normalizeTitleCase(input);
        String restOfString = result.substring(1);
        assertThat(restOfString).isEqualTo(restOfString.toLowerCase());
    }

    @Property(tries = 1000)
    @Label("normalizeTitleCase should preserve string length")
    void normalizeTitleCaseShouldPreserveLength(
            @ForAll @StringLength(min = 0, max = 100) @AlphaChars String input) {
        String result = PBStringUtils.normalizeTitleCase(input);
        if (input != null) {
            assertThat(result.length()).isEqualTo(input.length());
        }
    }

    @Property(tries = 500)
    @Label("normalizeTitleCase should be idempotent")
    void normalizeTitleCaseShouldBeIdempotent(
            @ForAll @StringLength(min = 1, max = 50) @AlphaChars String input) {
        String firstPass = PBStringUtils.normalizeTitleCase(input);
        String secondPass = PBStringUtils.normalizeTitleCase(firstPass);
        assertThat(firstPass).isEqualTo(secondPass);
    }

    // =========================================================================
    // toLowerSnakeCase() PROPERTIES
    // =========================================================================

    @Property(tries = 1000)
    @Label("toLowerSnakeCase should return null for null input")
    void toLowerSnakeCaseShouldReturnNullForNull() {
        assertThat(PBStringUtils.toLowerSnakeCase(null)).isNull();
    }

    @Property(tries = 1000)
    @Label("toLowerSnakeCase result should be all lowercase")
    void toLowerSnakeCaseShouldBeAllLowercase(
            @ForAll @StringLength(min = 1, max = 50) @AlphaChars String input) {
        String result = PBStringUtils.toLowerSnakeCase(input);
        assertThat(result).isEqualTo(result.toLowerCase());
    }

    @Property(tries = 1000)
    @Label("toLowerSnakeCase should replace spaces with underscores")
    void toLowerSnakeCaseShouldReplaceSpacesWithUnderscores(
            @ForAll @StringLength(min = 1, max = 20) @AlphaChars String word1,
            @ForAll @StringLength(min = 1, max = 20) @AlphaChars String word2) {
        String input = word1 + " " + word2;
        String result = PBStringUtils.toLowerSnakeCase(input);
        assertThat(result).doesNotContain(" ");
        assertThat(result).contains("_");
    }

    @Property(tries = 500)
    @Label("toLowerSnakeCase should be idempotent for lowercase inputs")
    void toLowerSnakeCaseShouldBeIdempotent(
            @ForAll @StringLength(min = 1, max = 50) String input) {
        // Only test with inputs that don't have spaces (already processed)
        String firstPass = PBStringUtils.toLowerSnakeCase(input);
        String secondPass = PBStringUtils.toLowerSnakeCase(firstPass);
        assertThat(secondPass).isEqualTo(firstPass);
    }

    // =========================================================================
    // toUpperSnakeCase() PROPERTIES
    // =========================================================================

    @Property(tries = 1000)
    @Label("toUpperSnakeCase should return null for null input")
    void toUpperSnakeCaseShouldReturnNullForNull() {
        assertThat(PBStringUtils.toUpperSnakeCase(null)).isNull();
    }

    @Property(tries = 1000)
    @Label("toUpperSnakeCase result should be all uppercase")
    void toUpperSnakeCaseShouldBeAllUppercase(
            @ForAll @StringLength(min = 1, max = 50) @AlphaChars String input) {
        String result = PBStringUtils.toUpperSnakeCase(input);
        assertThat(result).isEqualTo(result.toUpperCase());
    }

    @Property(tries = 1000)
    @Label("toUpperSnakeCase should replace spaces with underscores")
    void toUpperSnakeCaseShouldReplaceSpacesWithUnderscores(
            @ForAll @StringLength(min = 1, max = 20) @AlphaChars String word1,
            @ForAll @StringLength(min = 1, max = 20) @AlphaChars String word2) {
        String input = word1 + " " + word2;
        String result = PBStringUtils.toUpperSnakeCase(input);
        assertThat(result).doesNotContain(" ");
        assertThat(result).contains("_");
    }

    @Property(tries = 500)
    @Label("toUpperSnakeCase should be idempotent for uppercase inputs")
    void toUpperSnakeCaseShouldBeIdempotent(
            @ForAll @StringLength(min = 1, max = 50) String input) {
        String firstPass = PBStringUtils.toUpperSnakeCase(input);
        String secondPass = PBStringUtils.toUpperSnakeCase(firstPass);
        assertThat(secondPass).isEqualTo(firstPass);
    }

    // =========================================================================
    // RELATIONSHIP PROPERTIES
    // =========================================================================

    @Property(tries = 500)
    @Label("toLowerSnakeCase and toUpperSnakeCase should differ only in case")
    void snakeCaseMethodsShouldDifferOnlyInCase(
            @ForAll @StringLength(min = 1, max = 50) @AlphaChars String input) {
        String lower = PBStringUtils.toLowerSnakeCase(input);
        String upper = PBStringUtils.toUpperSnakeCase(input);
        assertThat(lower.toUpperCase()).isEqualTo(upper);
        assertThat(upper.toLowerCase()).isEqualTo(lower);
    }

    // =========================================================================
    // resolveTemplateVariables() PROPERTIES
    // =========================================================================

    @Property(tries = 500)
    @Label("resolveTemplateVariables should return null for null template")
    void resolveTemplateVariablesShouldReturnNullForNull() {
        assertThat(PBStringUtils.resolveTemplateVariables(null, (s1, s2) -> "value")).isNull();
    }

    @Property(tries = 500)
    @Label("resolveTemplateVariables should return empty for empty template")
    void resolveTemplateVariablesShouldReturnEmptyForEmpty() {
        assertThat(PBStringUtils.resolveTemplateVariables("", (s1, s2) -> "value")).isEmpty();
    }

    @Property(tries = 500)
    @Label("resolveTemplateVariables should return original if resolver is null")
    void resolveTemplateVariablesShouldReturnOriginalIfResolverNull(
            @ForAll @StringLength(min = 1, max = 50) @AlphaChars String template) {
        assertThat(PBStringUtils.resolveTemplateVariables(template, null)).isEqualTo(template);
    }

    @Property(tries = 500)
    @Label("resolveTemplateVariables should preserve non-variable text")
    void resolveTemplateVariablesShouldPreserveNonVariableText(
            @ForAll @StringLength(min = 1, max = 50) @AlphaChars String text) {
        // Text without {{variable}} patterns should remain unchanged
        assertThat(PBStringUtils.resolveTemplateVariables(text, (s1, s2) -> "value")).isEqualTo(text);
    }

    @Property(tries = 500)
    @Label("resolveTemplateVariables should replace valid variable patterns")
    void resolveTemplateVariablesShouldReplaceValidPatterns(
            @ForAll @StringLength(min = 1, max = 10) @AlphaChars String refStep,
            @ForAll @StringLength(min = 1, max = 10) @AlphaChars String dataName,
            @ForAll @StringLength(min = 1, max = 20) @AlphaChars String replacement) {
        String template = "Hello {{" + refStep + ":" + dataName + "}} world";
        String result = PBStringUtils.resolveTemplateVariables(template, (r, d) -> replacement);
        assertThat(result).isEqualTo("Hello " + replacement + " world");
    }
}
