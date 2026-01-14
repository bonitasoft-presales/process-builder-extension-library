package com.bonitasoft.processbuilder.extension;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for the {@link PBHtmlUtils} utility class.
 * <p>
 * This class tests HTML conversion and email template application methods
 * for proper handling of various input scenarios including null, empty,
 * and special character inputs.
 * </p>
 *
 * @author Bonitasoft
 * @since 1.0
 */
@DisplayName("PBHtmlUtils Unit Tests")
class PBHtmlUtilsTest {

    // =========================================================================
    // CONSTANTS
    // =========================================================================

    private static final String SIMPLE_TEMPLATE = "<html><body>{{content}}</body></html>";
    private static final String TEMPLATE_WITH_SPACES = "<html><body>{{ content }}</body></html>";
    private static final String TEMPLATE_WITHOUT_PLACEHOLDER = "<html><body>No placeholder here</body></html>";

    // =========================================================================
    // CONSTRUCTOR TESTS
    // =========================================================================

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("should have private constructor")
        void constructor_should_be_private() throws Exception {
            Constructor<PBHtmlUtils> constructor = PBHtmlUtils.class.getDeclaredConstructor();
            assertThat(Modifier.isPrivate(constructor.getModifiers())).isTrue();
        }

        @Test
        @DisplayName("should throw UnsupportedOperationException when instantiated via reflection")
        void constructor_should_throw_exception_when_instantiated() throws Exception {
            Constructor<PBHtmlUtils> constructor = PBHtmlUtils.class.getDeclaredConstructor();
            constructor.setAccessible(true);

            assertThatThrownBy(constructor::newInstance)
                    .isInstanceOf(InvocationTargetException.class)
                    .hasCauseInstanceOf(UnsupportedOperationException.class);
        }
    }

    // =========================================================================
    // convertTextToHtml TESTS
    // =========================================================================

    @Nested
    @DisplayName("convertTextToHtml Method Tests")
    class ConvertTextToHtmlTests {

        @Test
        @DisplayName("should return null for null input")
        void should_return_null_for_null_input() {
            assertThat(PBHtmlUtils.convertTextToHtml(null)).isNull();
        }

        @Test
        @DisplayName("should return empty string for empty input")
        void should_return_empty_for_empty_input() {
            assertThat(PBHtmlUtils.convertTextToHtml("")).isEmpty();
        }

        @Test
        @DisplayName("should convert Unix newlines to br tags")
        void should_convert_unix_newlines_to_br() {
            String input = "Line 1\nLine 2\nLine 3";
            String result = PBHtmlUtils.convertTextToHtml(input);

            assertThat(result).isEqualTo("Line 1<br/>Line 2<br/>Line 3");
        }

        @Test
        @DisplayName("should convert Windows newlines to br tags")
        void should_convert_windows_newlines_to_br() {
            String input = "Line 1\r\nLine 2\r\nLine 3";
            String result = PBHtmlUtils.convertTextToHtml(input);

            assertThat(result).isEqualTo("Line 1<br/>Line 2<br/>Line 3");
        }

        @Test
        @DisplayName("should convert carriage returns to br tags")
        void should_convert_carriage_returns_to_br() {
            String input = "Line 1\rLine 2\rLine 3";
            String result = PBHtmlUtils.convertTextToHtml(input);

            assertThat(result).isEqualTo("Line 1<br/>Line 2<br/>Line 3");
        }

        @Test
        @DisplayName("should convert tabs to non-breaking spaces")
        void should_convert_tabs_to_nbsp() {
            String input = "Column1\tColumn2";
            String result = PBHtmlUtils.convertTextToHtml(input);

            assertThat(result).isEqualTo("Column1&nbsp;&nbsp;&nbsp;&nbsp;Column2");
        }

        @Test
        @DisplayName("should preserve multiple consecutive spaces")
        void should_preserve_multiple_spaces() {
            String input = "Word1  Word2   Word3";
            String result = PBHtmlUtils.convertTextToHtml(input);

            assertThat(result).isEqualTo("Word1 &nbsp;Word2 &nbsp;&nbsp;Word3");
        }

        @Test
        @DisplayName("should escape ampersand")
        void should_escape_ampersand() {
            String input = "A & B";
            String result = PBHtmlUtils.convertTextToHtml(input);

            assertThat(result).contains("&amp;");
        }

        @Test
        @DisplayName("should escape less than sign")
        void should_escape_less_than() {
            String input = "A < B";
            String result = PBHtmlUtils.convertTextToHtml(input);

            assertThat(result).contains("&lt;");
        }

        @Test
        @DisplayName("should escape greater than sign")
        void should_escape_greater_than() {
            String input = "A > B";
            String result = PBHtmlUtils.convertTextToHtml(input);

            assertThat(result).contains("&gt;");
        }

        @Test
        @DisplayName("should escape double quotes")
        void should_escape_double_quotes() {
            String input = "Say \"Hello\"";
            String result = PBHtmlUtils.convertTextToHtml(input);

            assertThat(result).contains("&quot;");
        }

        @Test
        @DisplayName("should escape single quotes")
        void should_escape_single_quotes() {
            String input = "It's fine";
            String result = PBHtmlUtils.convertTextToHtml(input);

            assertThat(result).contains("&#39;");
        }

        @Test
        @DisplayName("should handle all transformations together")
        void should_handle_all_transformations() {
            String input = "Hello & World\nSecond line with \"quotes\" and <tags>";
            String result = PBHtmlUtils.convertTextToHtml(input);

            assertThat(result)
                    .contains("&amp;")
                    .contains("<br/>")
                    .contains("&quot;")
                    .contains("&lt;")
                    .contains("&gt;");
        }

        @Test
        @DisplayName("should handle mixed newline types")
        void should_handle_mixed_newlines() {
            String input = "Line 1\r\nLine 2\nLine 3\rLine 4";
            String result = PBHtmlUtils.convertTextToHtml(input);

            assertThat(result).isEqualTo("Line 1<br/>Line 2<br/>Line 3<br/>Line 4");
        }
    }

    // =========================================================================
    // applyEmailTemplate TESTS
    // =========================================================================

    @Nested
    @DisplayName("applyEmailTemplate Method Tests")
    class ApplyEmailTemplateTests {

        @Test
        @DisplayName("should return content when template is null")
        void should_return_content_when_template_null() {
            String content = "Test content";

            String result = PBHtmlUtils.applyEmailTemplate(null, content);

            assertThat(result).isEqualTo(content);
        }

        @Test
        @DisplayName("should return content when template is empty")
        void should_return_content_when_template_empty() {
            String content = "Test content";

            String result = PBHtmlUtils.applyEmailTemplate("", content);

            assertThat(result).isEqualTo(content);
        }

        @Test
        @DisplayName("should return template unchanged when content is null")
        void should_return_template_when_content_null() {
            String result = PBHtmlUtils.applyEmailTemplate(SIMPLE_TEMPLATE, null);

            assertThat(result).isEqualTo(SIMPLE_TEMPLATE);
        }

        @Test
        @DisplayName("should replace placeholder with empty string when content is empty")
        void should_replace_placeholder_with_empty_content() {
            String result = PBHtmlUtils.applyEmailTemplate(SIMPLE_TEMPLATE, "");

            assertThat(result).isEqualTo("<html><body></body></html>");
        }

        @Test
        @DisplayName("should replace placeholder with content")
        void should_replace_placeholder_with_content() {
            String content = "<p>Hello World</p>";

            String result = PBHtmlUtils.applyEmailTemplate(SIMPLE_TEMPLATE, content);

            assertThat(result).isEqualTo("<html><body><p>Hello World</p></body></html>");
        }

        @Test
        @DisplayName("should handle template with spaces in placeholder")
        void should_handle_spaces_in_placeholder() {
            String content = "Test";

            String result = PBHtmlUtils.applyEmailTemplate(TEMPLATE_WITH_SPACES, content);

            assertThat(result).isEqualTo("<html><body>Test</body></html>");
        }

        @Test
        @DisplayName("should append content when template has no placeholder")
        void should_append_content_when_no_placeholder() {
            String content = "<p>Content</p>";

            String result = PBHtmlUtils.applyEmailTemplate(TEMPLATE_WITHOUT_PLACEHOLDER, content);

            assertThat(result).isEqualTo(TEMPLATE_WITHOUT_PLACEHOLDER + content);
        }

        @Test
        @DisplayName("should handle realistic email template")
        void should_handle_realistic_template() {
            String template = "<p style=\"text-align: center;\">"
                    + "<img src=\"https://example.com/logo.png\">"
                    + "</p><p>{{content}}</p>";
            String content = "Dear User,<br/>Your request has been processed.";

            String result = PBHtmlUtils.applyEmailTemplate(template, content);

            assertThat(result)
                    .contains("Dear User,<br/>Your request has been processed.")
                    .contains("<img src=\"https://example.com/logo.png\">")
                    .doesNotContain("{{content}}");
        }

        @Test
        @DisplayName("should handle content with special regex characters")
        void should_handle_content_with_special_regex_chars() {
            String content = "Price: $100.00 (50% off)";

            String result = PBHtmlUtils.applyEmailTemplate(SIMPLE_TEMPLATE, content);

            assertThat(result).isEqualTo("<html><body>Price: $100.00 (50% off)</body></html>");
        }

        @Test
        @DisplayName("should only replace first occurrence of placeholder")
        void should_replace_first_placeholder_only() {
            String template = "{{content}} and {{content}}";
            String content = "TEST";

            String result = PBHtmlUtils.applyEmailTemplate(template, content);

            assertThat(result).isEqualTo("TEST and {{content}}");
        }
    }

    // =========================================================================
    // prepareEmailContent TESTS
    // =========================================================================

    @Nested
    @DisplayName("prepareEmailContent Method Tests")
    class PrepareEmailContentTests {

        @Test
        @DisplayName("should convert text and apply template when template is valid")
        void should_convert_text_and_apply_template() {
            String textContent = "Line 1\nLine 2";

            String result = PBHtmlUtils.prepareEmailContent(textContent, SIMPLE_TEMPLATE);

            assertThat(result)
                    .contains("<br/>")
                    .startsWith("<html><body>")
                    .endsWith("</body></html>");
        }

        @Test
        @DisplayName("should return original content when template is null")
        void should_return_original_content_when_template_null() {
            String textContent = "Test content\nWith newline";

            String result = PBHtmlUtils.prepareEmailContent(textContent, null);

            assertThat(result).isEqualTo("Test content\nWith newline");
        }

        @Test
        @DisplayName("should return original content when template is empty")
        void should_return_original_content_when_template_empty() {
            String textContent = "Test content\nWith newline";

            String result = PBHtmlUtils.prepareEmailContent(textContent, "");

            assertThat(result).isEqualTo("Test content\nWith newline");
        }

        @Test
        @DisplayName("should return original content when template has no placeholder")
        void should_return_original_content_when_template_no_placeholder() {
            String textContent = "Test content\nWith newline";

            String result = PBHtmlUtils.prepareEmailContent(textContent, TEMPLATE_WITHOUT_PLACEHOLDER);

            assertThat(result).isEqualTo("Test content\nWith newline");
        }

        @Test
        @DisplayName("should return null when both content and template are null")
        void should_return_null_when_both_null() {
            String result = PBHtmlUtils.prepareEmailContent(null, null);

            assertThat(result).isNull();
        }

        @Test
        @DisplayName("should return null content when template is invalid and content is null")
        void should_return_null_when_template_invalid_and_content_null() {
            String result = PBHtmlUtils.prepareEmailContent(null, "");

            assertThat(result).isNull();
        }

        @Test
        @DisplayName("should handle null text content with valid template")
        void should_handle_null_text_content_with_valid_template() {
            String result = PBHtmlUtils.prepareEmailContent(null, SIMPLE_TEMPLATE);

            assertThat(result).isEqualTo(SIMPLE_TEMPLATE);
        }

        @Test
        @DisplayName("should escape HTML and convert newlines before applying valid template")
        void should_escape_and_convert_before_applying() {
            String textContent = "Hello <World>\nNew line with & ampersand";

            String result = PBHtmlUtils.prepareEmailContent(textContent, SIMPLE_TEMPLATE);

            assertThat(result)
                    .contains("&lt;World&gt;")
                    .contains("<br/>")
                    .contains("&amp;")
                    .doesNotContain("{{content}}");
        }

        @Test
        @DisplayName("should not convert HTML when template has no placeholder")
        void should_not_convert_html_when_template_invalid() {
            String textContent = "Hello <World>\nNew line";

            String result = PBHtmlUtils.prepareEmailContent(textContent, TEMPLATE_WITHOUT_PLACEHOLDER);

            // Should return original content without HTML conversion
            assertThat(result)
                    .contains("<World>")
                    .contains("\n")
                    .doesNotContain("<br/>")
                    .doesNotContain("&lt;");
        }

        @Test
        @DisplayName("should handle template with spaces in placeholder")
        void should_handle_template_with_spaces_in_placeholder() {
            String textContent = "Test\nContent";

            String result = PBHtmlUtils.prepareEmailContent(textContent, TEMPLATE_WITH_SPACES);

            assertThat(result)
                    .contains("<br/>")
                    .startsWith("<html><body>")
                    .endsWith("</body></html>");
        }
    }

    // =========================================================================
    // isValidTemplate TESTS (Package-private)
    // =========================================================================

    @Nested
    @DisplayName("isValidTemplate Method Tests")
    class IsValidTemplateTests {

        @Test
        @DisplayName("should return false for null template")
        void should_return_false_for_null() {
            assertThat(PBHtmlUtils.isValidTemplate(null)).isFalse();
        }

        @Test
        @DisplayName("should return false for empty template")
        void should_return_false_for_empty() {
            assertThat(PBHtmlUtils.isValidTemplate("")).isFalse();
        }

        @Test
        @DisplayName("should return false for template without placeholder")
        void should_return_false_for_template_without_placeholder() {
            assertThat(PBHtmlUtils.isValidTemplate(TEMPLATE_WITHOUT_PLACEHOLDER)).isFalse();
        }

        @Test
        @DisplayName("should return true for template with placeholder")
        void should_return_true_for_template_with_placeholder() {
            assertThat(PBHtmlUtils.isValidTemplate(SIMPLE_TEMPLATE)).isTrue();
        }

        @Test
        @DisplayName("should return true for template with spaced placeholder")
        void should_return_true_for_template_with_spaced_placeholder() {
            assertThat(PBHtmlUtils.isValidTemplate(TEMPLATE_WITH_SPACES)).isTrue();
        }

        @Test
        @DisplayName("should return true for minimal template with placeholder only")
        void should_return_true_for_minimal_template() {
            assertThat(PBHtmlUtils.isValidTemplate("{{content}}")).isTrue();
        }

        @Test
        @DisplayName("should return false for template with partial placeholder")
        void should_return_false_for_partial_placeholder() {
            assertThat(PBHtmlUtils.isValidTemplate("{{content")).isFalse();
            assertThat(PBHtmlUtils.isValidTemplate("content}}")).isFalse();
            assertThat(PBHtmlUtils.isValidTemplate("{content}")).isFalse();
        }

        @Test
        @DisplayName("should return false for whitespace-only template")
        void should_return_false_for_whitespace_only() {
            assertThat(PBHtmlUtils.isValidTemplate("   ")).isFalse();
            assertThat(PBHtmlUtils.isValidTemplate("\t\n")).isFalse();
        }
    }

    // =========================================================================
    // escapeHtmlSpecialChars TESTS (Package-private)
    // =========================================================================

    @Nested
    @DisplayName("escapeHtmlSpecialChars Method Tests")
    class EscapeHtmlSpecialCharsTests {

        @Test
        @DisplayName("should return null for null input")
        void should_return_null_for_null() {
            assertThat(PBHtmlUtils.escapeHtmlSpecialChars(null)).isNull();
        }

        @Test
        @DisplayName("should return empty for empty input")
        void should_return_empty_for_empty() {
            assertThat(PBHtmlUtils.escapeHtmlSpecialChars("")).isEmpty();
        }

        @Test
        @DisplayName("should escape all special characters")
        void should_escape_all_special_chars() {
            String input = "&<>\"'";

            String result = PBHtmlUtils.escapeHtmlSpecialChars(input);

            assertThat(result).isEqualTo("&amp;&lt;&gt;&quot;&#39;");
        }

        @Test
        @DisplayName("should not modify text without special characters")
        void should_not_modify_normal_text() {
            String input = "Hello World 123";

            String result = PBHtmlUtils.escapeHtmlSpecialChars(input);

            assertThat(result).isEqualTo(input);
        }
    }

    // =========================================================================
    // preserveMultipleSpaces TESTS (Package-private)
    // =========================================================================

    @Nested
    @DisplayName("preserveMultipleSpaces Method Tests")
    class PreserveMultipleSpacesTests {

        @Test
        @DisplayName("should return null for null input")
        void should_return_null_for_null() {
            assertThat(PBHtmlUtils.preserveMultipleSpaces(null)).isNull();
        }

        @Test
        @DisplayName("should return empty for empty input")
        void should_return_empty_for_empty() {
            assertThat(PBHtmlUtils.preserveMultipleSpaces("")).isEmpty();
        }

        @Test
        @DisplayName("should not modify single spaces")
        void should_not_modify_single_spaces() {
            String input = "A B C D";

            String result = PBHtmlUtils.preserveMultipleSpaces(input);

            assertThat(result).isEqualTo("A B C D");
        }

        @Test
        @DisplayName("should convert consecutive spaces to alternating nbsp")
        void should_convert_consecutive_spaces() {
            String input = "A  B   C";

            String result = PBHtmlUtils.preserveMultipleSpaces(input);

            assertThat(result).isEqualTo("A &nbsp;B &nbsp;&nbsp;C");
        }

        @Test
        @DisplayName("should handle text without spaces")
        void should_handle_text_without_spaces() {
            String input = "NoSpacesHere";

            String result = PBHtmlUtils.preserveMultipleSpaces(input);

            assertThat(result).isEqualTo(input);
        }

        @Test
        @DisplayName("should handle leading spaces")
        void should_handle_leading_spaces() {
            String input = "  Leading";

            String result = PBHtmlUtils.preserveMultipleSpaces(input);

            assertThat(result).isEqualTo(" &nbsp;Leading");
        }

        @Test
        @DisplayName("should handle trailing spaces")
        void should_handle_trailing_spaces() {
            String input = "Trailing  ";

            String result = PBHtmlUtils.preserveMultipleSpaces(input);

            assertThat(result).isEqualTo("Trailing &nbsp;");
        }
    }
}
