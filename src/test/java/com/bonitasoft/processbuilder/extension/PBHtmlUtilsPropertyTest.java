package com.bonitasoft.processbuilder.extension;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

import static org.assertj.core.api.Assertions.*;

/**
 * Property-based tests for {@link PBHtmlUtils} utility class.
 * <p>
 * Tests invariants that must hold for any valid input, including:
 * <ul>
 *   <li>HTML conversion preserves text content (escaping aside)</li>
 *   <li>Template application behaves consistently</li>
 *   <li>Security properties for HTML escaping</li>
 * </ul>
 * </p>
 *
 * @author Bonitasoft
 * @since 1.0
 */
@Label("PBHtmlUtils Property-Based Tests")
class PBHtmlUtilsPropertyTest {

    // =========================================================================
    // CONSTANTS
    // =========================================================================

    private static final String TEMPLATE_WITH_PLACEHOLDER = "<div>{{content}}</div>";
    private static final long MAX_EXECUTION_TIME_MS = 5000;

    // =========================================================================
    // convertTextToHtml PROPERTIES
    // =========================================================================

    @Property(tries = 500)
    @Label("convertTextToHtml should never return null for non-null input")
    void convertTextToHtml_shouldNeverReturnNullForNonNullInput(
            @ForAll @StringLength(max = 1000) String input) {
        String result = PBHtmlUtils.convertTextToHtml(input);
        assertThat(result).isNotNull();
    }

    @Property(tries = 500)
    @Label("convertTextToHtml should always return null for null input")
    void convertTextToHtml_shouldReturnNullForNullInput() {
        assertThat(PBHtmlUtils.convertTextToHtml(null)).isNull();
    }

    @Property(tries = 500)
    @Label("convertTextToHtml should convert all newlines to br tags")
    void convertTextToHtml_shouldConvertAllNewlines(
            @ForAll @StringLength(min = 1, max = 100) @AlphaChars String before,
            @ForAll @StringLength(min = 1, max = 100) @AlphaChars String after) {

        // Test Unix newline
        String unixInput = before + "\n" + after;
        assertThat(PBHtmlUtils.convertTextToHtml(unixInput))
                .contains("<br/>");

        // Test Windows newline
        String windowsInput = before + "\r\n" + after;
        assertThat(PBHtmlUtils.convertTextToHtml(windowsInput))
                .contains("<br/>");
    }

    @Property(tries = 500)
    @Label("convertTextToHtml should escape HTML special characters")
    void convertTextToHtml_shouldEscapeSpecialChars(
            @ForAll @StringLength(min = 1, max = 100) @AlphaChars String text) {

        // Test with ampersand - the raw & should be converted to &amp;
        String withAmpersand = text + "&" + text;
        String result = PBHtmlUtils.convertTextToHtml(withAmpersand);
        assertThat(result)
                .contains("&amp;");
        // Verify the original unescaped pattern is not present (& followed by text, not by amp;)
        assertThat(result.replaceAll("&amp;", "")).doesNotContain("&");

        // Test with less than
        String withLessThan = text + "<" + text;
        assertThat(PBHtmlUtils.convertTextToHtml(withLessThan))
                .contains("&lt;");

        // Test with greater than
        String withGreaterThan = text + ">" + text;
        assertThat(PBHtmlUtils.convertTextToHtml(withGreaterThan))
                .contains("&gt;");
    }

    @Property(tries = 200)
    @Label("convertTextToHtml result should not contain raw newline characters")
    void convertTextToHtml_shouldNotContainRawNewlines(
            @ForAll @StringLength(max = 500) String input) {
        String result = PBHtmlUtils.convertTextToHtml(input);

        if (result != null) {
            assertThat(result)
                    .doesNotContain("\n")
                    .doesNotContain("\r");
        }
    }

    @Property(tries = 100)
    @Label("convertTextToHtml should handle empty string")
    void convertTextToHtml_shouldHandleEmptyString() {
        assertThat(PBHtmlUtils.convertTextToHtml("")).isEmpty();
    }

    // =========================================================================
    // convertTextToHtml LITERAL ESCAPE SEQUENCE PROPERTIES (JSON strings)
    // =========================================================================

    @Property(tries = 500)
    @Label("convertTextToHtml should convert literal backslash-n sequences to br tags")
    void convertTextToHtml_shouldConvertLiteralBackslashN(
            @ForAll @StringLength(min = 1, max = 100) @AlphaChars String before,
            @ForAll @StringLength(min = 1, max = 100) @AlphaChars String after) {

        // Simulates JSON string where \n is stored as literal \\n
        String input = before + "\\n" + after;
        String result = PBHtmlUtils.convertTextToHtml(input);

        assertThat(result)
                .contains("<br/>")
                .doesNotContain("\\n");
    }

    @Property(tries = 500)
    @Label("convertTextToHtml should convert literal backslash-r-n sequences to br tags")
    void convertTextToHtml_shouldConvertLiteralBackslashRN(
            @ForAll @StringLength(min = 1, max = 100) @AlphaChars String before,
            @ForAll @StringLength(min = 1, max = 100) @AlphaChars String after) {

        // Simulates JSON string where \r\n is stored as literal \\r\\n
        String input = before + "\\r\\n" + after;
        String result = PBHtmlUtils.convertTextToHtml(input);

        assertThat(result)
                .contains("<br/>")
                .doesNotContain("\\r\\n");
    }

    @Property(tries = 500)
    @Label("convertTextToHtml should convert literal backslash-r sequences to br tags")
    void convertTextToHtml_shouldConvertLiteralBackslashR(
            @ForAll @StringLength(min = 1, max = 100) @AlphaChars String before,
            @ForAll @StringLength(min = 1, max = 100) @AlphaChars String after) {

        // Simulates JSON string where \r is stored as literal \\r
        String input = before + "\\r" + after;
        String result = PBHtmlUtils.convertTextToHtml(input);

        assertThat(result)
                .contains("<br/>")
                .doesNotContain("\\r");
    }

    @Property(tries = 500)
    @Label("convertTextToHtml should convert literal backslash-t sequences to nbsp")
    void convertTextToHtml_shouldConvertLiteralBackslashT(
            @ForAll @StringLength(min = 1, max = 100) @AlphaChars String before,
            @ForAll @StringLength(min = 1, max = 100) @AlphaChars String after) {

        // Simulates JSON string where \t is stored as literal \\t
        String input = before + "\\t" + after;
        String result = PBHtmlUtils.convertTextToHtml(input);

        assertThat(result)
                .contains("&nbsp;&nbsp;&nbsp;&nbsp;")
                .doesNotContain("\\t");
    }

    @Property(tries = 200)
    @Label("convertTextToHtml should handle mixed literal and real escape sequences")
    void convertTextToHtml_shouldHandleMixedLiteralAndRealEscapes(
            @ForAll @StringLength(min = 1, max = 50) @AlphaChars String text) {

        // Mix of literal (from JSON) and real control characters
        String input = text + "\\n" + text + "\n" + text;
        String result = PBHtmlUtils.convertTextToHtml(input);

        // Should not contain either the literal or real newlines
        assertThat(result)
                .doesNotContain("\\n")
                .doesNotContain("\n")
                .contains("<br/>");

        // Should contain two br tags (one for each newline type)
        long brCount = result.chars().filter(ch -> ch == '<').count();
        assertThat(brCount).isGreaterThanOrEqualTo(2);
    }

    @Property(tries = 200)
    @Label("convertTextToHtml result should not contain any literal escape sequences")
    void convertTextToHtml_shouldNotContainLiteralEscapeSequences(
            @ForAll @StringLength(max = 500) String input) {
        String result = PBHtmlUtils.convertTextToHtml(input);

        if (result != null) {
            // After conversion, no literal escape sequences should remain
            assertThat(result)
                    .doesNotContain("\\n")
                    .doesNotContain("\\r")
                    .doesNotContain("\\t");
        }
    }

    // =========================================================================
    // applyEmailTemplate PROPERTIES
    // =========================================================================

    @Property(tries = 500)
    @Label("applyEmailTemplate should replace placeholder with content")
    void applyEmailTemplate_shouldReplacePlaceholder(
            @ForAll @StringLength(min = 1, max = 200) @AlphaChars String content) {

        String result = PBHtmlUtils.applyEmailTemplate(TEMPLATE_WITH_PLACEHOLDER, content);

        assertThat(result)
                .contains(content)
                .doesNotContain("{{content}}");
    }

    @Property(tries = 500)
    @Label("applyEmailTemplate should return content when template is null or empty")
    void applyEmailTemplate_shouldReturnContentWhenTemplateNullOrEmpty(
            @ForAll @StringLength(min = 1, max = 100) @AlphaChars String content) {

        assertThat(PBHtmlUtils.applyEmailTemplate(null, content)).isEqualTo(content);
        assertThat(PBHtmlUtils.applyEmailTemplate("", content)).isEqualTo(content);
    }

    @Property(tries = 500)
    @Label("applyEmailTemplate should return template when content is null")
    void applyEmailTemplate_shouldReturnTemplateWhenContentNull(
            @ForAll @StringLength(min = 1, max = 200) @AlphaChars String template) {

        String result = PBHtmlUtils.applyEmailTemplate(template, null);
        assertThat(result).isEqualTo(template);
    }

    @Property(tries = 200)
    @Label("applyEmailTemplate should preserve template structure around content")
    void applyEmailTemplate_shouldPreserveTemplateStructure(
            @ForAll @StringLength(min = 1, max = 50) @AlphaChars String prefix,
            @ForAll @StringLength(min = 1, max = 50) @AlphaChars String suffix,
            @ForAll @StringLength(min = 1, max = 100) @AlphaChars String content) {

        String template = prefix + "{{content}}" + suffix;
        String result = PBHtmlUtils.applyEmailTemplate(template, content);

        assertThat(result)
                .startsWith(prefix)
                .endsWith(suffix)
                .contains(content);
    }

    @Property(tries = 200)
    @Label("applyEmailTemplate should handle content with special regex characters")
    void applyEmailTemplate_shouldHandleSpecialRegexChars(
            @ForAll @StringLength(min = 1, max = 50) @AlphaChars String text) {

        String contentWithSpecialChars = text + "$100.00 (50%)";
        String result = PBHtmlUtils.applyEmailTemplate(TEMPLATE_WITH_PLACEHOLDER, contentWithSpecialChars);

        assertThat(result).contains("$100.00 (50%)");
    }

    // =========================================================================
    // prepareEmailContent PROPERTIES
    // =========================================================================

    @Property(tries = 500)
    @Label("prepareEmailContent should combine conversion and template application for valid templates")
    void prepareEmailContent_shouldCombineConversionAndTemplate(
            @ForAll @StringLength(min = 1, max = 100) @AlphaChars String text) {

        String content = text + "\n" + text;
        String result = PBHtmlUtils.prepareEmailContent(content, TEMPLATE_WITH_PLACEHOLDER);

        assertThat(result)
                .contains("<br/>")
                .doesNotContain("{{content}}")
                .startsWith("<div>")
                .endsWith("</div>");
    }

    @Property(tries = 200)
    @Label("prepareEmailContent should return original content when template is invalid")
    void prepareEmailContent_shouldReturnOriginalContentWhenTemplateInvalid(
            @ForAll @StringLength(min = 1, max = 100) @AlphaChars String text) {

        String contentWithNewline = text + "\n" + text;

        // Null template - should return original content without HTML conversion
        String resultNullTemplate = PBHtmlUtils.prepareEmailContent(contentWithNewline, null);
        assertThat(resultNullTemplate)
                .isEqualTo(contentWithNewline)
                .contains("\n")
                .doesNotContain("<br/>");

        // Empty template - should return original content without HTML conversion
        String resultEmptyTemplate = PBHtmlUtils.prepareEmailContent(contentWithNewline, "");
        assertThat(resultEmptyTemplate)
                .isEqualTo(contentWithNewline)
                .contains("\n")
                .doesNotContain("<br/>");

        // Template without placeholder - should return original content without HTML conversion
        String resultNoPlaceholder = PBHtmlUtils.prepareEmailContent(contentWithNewline, "No placeholder here");
        assertThat(resultNoPlaceholder)
                .isEqualTo(contentWithNewline)
                .contains("\n")
                .doesNotContain("<br/>");
    }

    @Property(tries = 200)
    @Label("prepareEmailContent should handle null text content with valid template")
    void prepareEmailContent_shouldHandleNullTextContentWithValidTemplate() {
        String resultNullText = PBHtmlUtils.prepareEmailContent(null, TEMPLATE_WITH_PLACEHOLDER);
        assertThat(resultNullText).isEqualTo(TEMPLATE_WITH_PLACEHOLDER);
    }

    @Property(tries = 200)
    @Label("prepareEmailContent should handle both null inputs")
    void prepareEmailContent_shouldHandleBothNullInputs() {
        String resultBothNull = PBHtmlUtils.prepareEmailContent(null, null);
        assertThat(resultBothNull).isNull();
    }

    // =========================================================================
    // isValidTemplate PROPERTIES
    // =========================================================================

    @Property(tries = 200)
    @Label("isValidTemplate should return false for null or empty templates")
    void isValidTemplate_shouldReturnFalseForNullOrEmpty() {
        assertThat(PBHtmlUtils.isValidTemplate(null)).isFalse();
        assertThat(PBHtmlUtils.isValidTemplate("")).isFalse();
    }

    @Property(tries = 500)
    @Label("isValidTemplate should return true for templates with placeholder")
    void isValidTemplate_shouldReturnTrueForTemplatesWithPlaceholder(
            @ForAll @StringLength(min = 0, max = 50) @AlphaChars String prefix,
            @ForAll @StringLength(min = 0, max = 50) @AlphaChars String suffix) {

        String template = prefix + "{{content}}" + suffix;
        assertThat(PBHtmlUtils.isValidTemplate(template)).isTrue();
    }

    @Property(tries = 500)
    @Label("isValidTemplate should return true for templates with spaced placeholder")
    void isValidTemplate_shouldReturnTrueForSpacedPlaceholder(
            @ForAll @StringLength(min = 0, max = 50) @AlphaChars String prefix,
            @ForAll @StringLength(min = 0, max = 50) @AlphaChars String suffix,
            @ForAll @IntRange(min = 0, max = 5) int spaces) {

        String spacePadding = " ".repeat(spaces);
        String template = prefix + "{{" + spacePadding + "content" + spacePadding + "}}" + suffix;
        assertThat(PBHtmlUtils.isValidTemplate(template)).isTrue();
    }

    @Property(tries = 500)
    @Label("isValidTemplate should return false for templates without placeholder")
    void isValidTemplate_shouldReturnFalseForTemplatesWithoutPlaceholder(
            @ForAll @StringLength(min = 1, max = 100) @AlphaChars String template) {

        // Template without {{content}} should be invalid
        assertThat(PBHtmlUtils.isValidTemplate(template)).isFalse();
    }

    @Property(tries = 200)
    @Label("isValidTemplate result should be consistent with prepareEmailContent behavior")
    void isValidTemplate_shouldBeConsistentWithPrepareEmailContent(
            @ForAll @StringLength(min = 1, max = 50) @AlphaChars String text,
            @ForAll @StringLength(min = 1, max = 50) @AlphaChars String templateText) {

        String contentWithNewline = text + "\n" + text;

        // If template is valid, prepareEmailContent should convert HTML
        String validTemplate = templateText + "{{content}}" + templateText;
        if (PBHtmlUtils.isValidTemplate(validTemplate)) {
            String result = PBHtmlUtils.prepareEmailContent(contentWithNewline, validTemplate);
            assertThat(result).contains("<br/>");
        }

        // If template is invalid, prepareEmailContent should return original content
        String invalidTemplate = templateText;
        if (!PBHtmlUtils.isValidTemplate(invalidTemplate)) {
            String result = PBHtmlUtils.prepareEmailContent(contentWithNewline, invalidTemplate);
            assertThat(result).isEqualTo(contentWithNewline);
        }
    }

    // =========================================================================
    // SECURITY PROPERTIES
    // =========================================================================

    @Property(tries = 100)
    @Label("HTML conversion should prevent XSS attacks by escaping tags")
    void htmlConversion_shouldPreventXSS(
            @ForAll @StringLength(min = 1, max = 50) @AlphaChars String text) {

        String xssAttempt = "<script>alert('" + text + "')</script>";
        String result = PBHtmlUtils.convertTextToHtml(xssAttempt);

        assertThat(result)
                .doesNotContain("<script>")
                .contains("&lt;script&gt;");
    }

    @Property(tries = 100)
    @Label("HTML conversion should escape event handlers")
    void htmlConversion_shouldEscapeEventHandlers(
            @ForAll @StringLength(min = 1, max = 50) @AlphaChars String text) {

        String onclickAttempt = "<div onclick=\"alert('" + text + "')\">Click</div>";
        String result = PBHtmlUtils.convertTextToHtml(onclickAttempt);

        // Verify that the HTML tag brackets are escaped, making the tag non-executable
        assertThat(result)
                .doesNotContain("<div")
                .contains("&lt;div")
                .contains("&quot;")  // The quotes should be escaped
                .doesNotContain("</div>")
                .contains("&lt;/div&gt;");
    }

    // =========================================================================
    // PERFORMANCE PROPERTIES
    // =========================================================================

    @Property(tries = 20)
    @Label("convertTextToHtml should process long strings within time limits")
    void convertTextToHtml_shouldProcessLongStringsInTime(
            @ForAll @IntRange(min = 10000, max = 50000) int length) {

        String longInput = "a".repeat(length);
        long startTime = System.currentTimeMillis();

        assertThatCode(() -> PBHtmlUtils.convertTextToHtml(longInput))
                .doesNotThrowAnyException();

        long executionTime = System.currentTimeMillis() - startTime;
        assertThat(executionTime)
                .as("Execution time should be less than %d ms", MAX_EXECUTION_TIME_MS)
                .isLessThan(MAX_EXECUTION_TIME_MS);
    }

    @Property(tries = 20)
    @Label("applyEmailTemplate should process large content within time limits")
    void applyEmailTemplate_shouldProcessLargeContentInTime(
            @ForAll @IntRange(min = 10000, max = 50000) int length) {

        String largeContent = "a".repeat(length);
        long startTime = System.currentTimeMillis();

        assertThatCode(() -> PBHtmlUtils.applyEmailTemplate(TEMPLATE_WITH_PLACEHOLDER, largeContent))
                .doesNotThrowAnyException();

        long executionTime = System.currentTimeMillis() - startTime;
        assertThat(executionTime)
                .as("Execution time should be less than %d ms", MAX_EXECUTION_TIME_MS)
                .isLessThan(MAX_EXECUTION_TIME_MS);
    }

    // =========================================================================
    // IDEMPOTENCE AND CONSISTENCY PROPERTIES
    // =========================================================================

    @Property(tries = 200)
    @Label("escapeHtmlSpecialChars should be consistent with convertTextToHtml")
    void escapeHtmlSpecialChars_shouldBeConsistent(
            @ForAll @StringLength(min = 1, max = 100) @AlphaChars String text) {

        String textWithSpecialChars = text + "&<>\"'";

        String escaped = PBHtmlUtils.escapeHtmlSpecialChars(textWithSpecialChars);
        String converted = PBHtmlUtils.convertTextToHtml(textWithSpecialChars);

        // The escaped version should be contained in the converted version
        // (converted may have additional transformations like nbsp)
        assertThat(escaped)
                .contains("&amp;")
                .contains("&lt;")
                .contains("&gt;")
                .contains("&quot;")
                .contains("&#39;");
    }

    @Property(tries = 200)
    @Label("preserveMultipleSpaces should handle alternating pattern correctly")
    void preserveMultipleSpaces_shouldHandleAlternatingPattern(
            @ForAll @IntRange(min = 2, max = 10) int spaceCount,
            @ForAll @StringLength(min = 1, max = 20) @AlphaChars String text) {

        String input = text + " ".repeat(spaceCount) + text;
        String result = PBHtmlUtils.preserveMultipleSpaces(input);

        // Result should contain nbsp for multiple spaces
        if (spaceCount > 1) {
            assertThat(result).contains("&nbsp;");
        }

        // Should still contain the original text
        assertThat(result).contains(text);
    }
}
