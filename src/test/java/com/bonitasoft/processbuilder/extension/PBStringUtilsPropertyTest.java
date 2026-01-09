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
    @Label("resolveTemplateVariables should replace valid variable patterns with prefix")
    void resolveTemplateVariablesShouldReplaceValidPatternsWithPrefix(
            @ForAll @StringLength(min = 1, max = 10) @AlphaChars String refStep,
            @ForAll @StringLength(min = 1, max = 10) @AlphaChars String dataName,
            @ForAll @StringLength(min = 1, max = 20) @AlphaChars String replacement) {
        String template = "Hello {{" + refStep + ":" + dataName + "}} world";
        String result = PBStringUtils.resolveTemplateVariables(template, (r, d) -> replacement);
        assertThat(result).isEqualTo("Hello " + replacement + " world");
    }

    // =========================================================================
    // resolveTemplateVariables() - NEW FORMAT (without refStep prefix)
    // =========================================================================

    @Property(tries = 500)
    @Label("resolveTemplateVariables should replace simple variable patterns without prefix")
    void resolveTemplateVariablesShouldReplaceSimplePatterns(
            @ForAll @StringLength(min = 1, max = 20) @AlphaChars String dataName,
            @ForAll @StringLength(min = 1, max = 20) @AlphaChars String replacement) {
        String template = "Value: {{" + dataName + "}}";
        String result = PBStringUtils.resolveTemplateVariables(template, (r, d) -> {
            assertThat(r).isNull();
            assertThat(d).isEqualTo(dataName);
            return replacement;
        });
        assertThat(result).isEqualTo("Value: " + replacement);
    }

    @Property(tries = 500)
    @Label("resolveTemplateVariables should handle mixed format patterns")
    void resolveTemplateVariablesShouldHandleMixedPatterns(
            @ForAll @StringLength(min = 1, max = 10) @AlphaChars String refStep,
            @ForAll @StringLength(min = 1, max = 10) @AlphaChars String dataName1,
            @ForAll @StringLength(min = 1, max = 10) @AlphaChars String dataName2) {
        String template = "{{" + refStep + ":" + dataName1 + "}} and {{" + dataName2 + "}}";
        String result = PBStringUtils.resolveTemplateVariables(template, (r, d) -> {
            if (r != null) {
                return "prefixed";
            }
            return "simple";
        });
        assertThat(result).isEqualTo("prefixed and simple");
    }

    @Property(tries = 500)
    @Label("resolveTemplateVariables should capture refStep correctly when prefix present")
    void resolveTemplateVariablesShouldCaptureRefStepCorrectly(
            @ForAll @StringLength(min = 1, max = 15) @AlphaChars String refStep,
            @ForAll @StringLength(min = 1, max = 15) @AlphaChars String dataName) {
        String template = "{{" + refStep + ":" + dataName + "}}";
        PBStringUtils.resolveTemplateVariables(template, (r, d) -> {
            assertThat(r).isNotNull().isEqualTo(refStep);
            assertThat(d).isEqualTo(dataName);
            return "resolved";
        });
    }

    @Property(tries = 500)
    @Label("resolveTemplateVariables should have null refStep when no prefix")
    void resolveTemplateVariablesShouldHaveNullRefStepWithoutPrefix(
            @ForAll @StringLength(min = 1, max = 20) @AlphaChars String dataName) {
        String template = "{{" + dataName + "}}";
        PBStringUtils.resolveTemplateVariables(template, (r, d) -> {
            assertThat(r).isNull();
            assertThat(d).isEqualTo(dataName);
            return "resolved";
        });
    }

    @Property(tries = 300)
    @Label("resolveTemplateVariables should handle variable names with underscores")
    void resolveTemplateVariablesShouldHandleUnderscoreNames(
            @ForAll @StringLength(min = 1, max = 10) @AlphaChars String part1,
            @ForAll @StringLength(min = 1, max = 10) @AlphaChars String part2) {
        String dataName = part1.toLowerCase() + "_" + part2.toLowerCase();
        String template = "{{" + dataName + "}}";
        String result = PBStringUtils.resolveTemplateVariables(template, (r, d) -> {
            assertThat(r).isNull();
            assertThat(d).isEqualTo(dataName);
            return "value";
        });
        assertThat(result).isEqualTo("value");
    }

    @Property(tries = 300)
    @Label("resolveTemplateVariables result should not contain unresolved pattern markers")
    void resolveTemplateVariablesResultShouldNotContainPatternMarkers(
            @ForAll @StringLength(min = 1, max = 10) @AlphaChars String dataName,
            @ForAll @StringLength(min = 1, max = 15) @AlphaChars String replacement) {
        // Ensure replacement doesn't contain pattern markers
        Assume.that(!replacement.contains("{{") && !replacement.contains("}}"));

        String template = "Before {{" + dataName + "}} After";
        String result = PBStringUtils.resolveTemplateVariables(template, (r, d) -> replacement);
        assertThat(result).doesNotContain("{{").doesNotContain("}}");
    }

    @Property(tries = 300)
    @Label("resolveTemplateVariables should handle multiple simple variables")
    void resolveTemplateVariablesShouldHandleMultipleSimpleVariables(
            @ForAll @StringLength(min = 1, max = 8) @AlphaChars String var1,
            @ForAll @StringLength(min = 1, max = 8) @AlphaChars String var2,
            @ForAll @StringLength(min = 1, max = 8) @AlphaChars String var3) {
        // Ensure unique variable names
        Assume.that(!var1.equals(var2) && !var2.equals(var3) && !var1.equals(var3));

        String template = "{{" + var1 + "}} {{" + var2 + "}} {{" + var3 + "}}";
        String result = PBStringUtils.resolveTemplateVariables(template, (r, d) -> d.toUpperCase());
        assertThat(result).isEqualTo(var1.toUpperCase() + " " + var2.toUpperCase() + " " + var3.toUpperCase());
    }
}
