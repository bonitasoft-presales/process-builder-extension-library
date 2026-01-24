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
 * and special character inputs. It also tests XSS protection mechanisms.
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
        @DisplayName("should preserve legitimate anchor tags")
        void should_preserve_anchor_tags() {
            String input = "Click <a href=\"https://example.com\">here</a> for more info.";
            String result = PBHtmlUtils.convertTextToHtml(input);

            assertThat(result).contains("<a href=\"https://example.com\">here</a>");
        }

        @Test
        @DisplayName("should preserve legitimate HTML tags")
        void should_preserve_legitimate_html_tags() {
            String input = "<p>Paragraph</p><strong>Bold</strong><em>Italic</em>";
            String result = PBHtmlUtils.convertTextToHtml(input);

            assertThat(result)
                    .contains("<p>")
                    .contains("</p>")
                    .contains("<strong>")
                    .contains("<em>");
        }

        @Test
        @DisplayName("should handle mixed newline types")
        void should_handle_mixed_newlines() {
            String input = "Line 1\r\nLine 2\nLine 3\rLine 4";
            String result = PBHtmlUtils.convertTextToHtml(input);

            assertThat(result).isEqualTo("Line 1<br/>Line 2<br/>Line 3<br/>Line 4");
        }

        // =====================================================================
        // XSS PROTECTION TESTS
        // =====================================================================

        @Test
        @DisplayName("should remove script tags with content")
        void should_remove_script_tags() {
            String input = "Hello <script>alert('XSS')</script> World";
            String result = PBHtmlUtils.convertTextToHtml(input);

            assertThat(result)
                    .doesNotContain("<script>")
                    .doesNotContain("</script>")
                    .doesNotContain("alert")
                    .contains("Hello")
                    .contains("World");
        }

        @Test
        @DisplayName("should remove script tags case insensitive")
        void should_remove_script_tags_case_insensitive() {
            String input = "Test <SCRIPT>alert('XSS')</SCRIPT> End";
            String result = PBHtmlUtils.convertTextToHtml(input);

            assertThat(result)
                    .doesNotContain("<SCRIPT>")
                    .doesNotContain("</SCRIPT>")
                    .doesNotContain("alert");
        }

        @Test
        @DisplayName("should remove script tags with attributes")
        void should_remove_script_tags_with_attributes() {
            String input = "<script type=\"text/javascript\" src=\"evil.js\">code</script>";
            String result = PBHtmlUtils.convertTextToHtml(input);

            assertThat(result)
                    .doesNotContain("<script")
                    .doesNotContain("</script>")
                    .doesNotContain("evil.js");
        }

        @Test
        @DisplayName("should remove self-closing script tags")
        void should_remove_self_closing_script_tags() {
            String input = "Before <script src=\"evil.js\" /> After";
            String result = PBHtmlUtils.convertTextToHtml(input);

            assertThat(result)
                    .doesNotContain("<script")
                    .doesNotContain("evil.js")
                    .contains("Before")
                    .contains("After");
        }

        @Test
        @DisplayName("should remove onclick event handler")
        void should_remove_onclick_handler() {
            String input = "<button onclick=\"alert('XSS')\">Click</button>";
            String result = PBHtmlUtils.convertTextToHtml(input);

            assertThat(result)
                    .doesNotContain("onclick")
                    .doesNotContain("alert")
                    .contains("<button")
                    .contains(">Click</button>");
        }

        @Test
        @DisplayName("should remove onload event handler")
        void should_remove_onload_handler() {
            String input = "<img src=\"img.jpg\" onload=\"malicious()\">";
            String result = PBHtmlUtils.convertTextToHtml(input);

            assertThat(result)
                    .doesNotContain("onload")
                    .doesNotContain("malicious")
                    .contains("<img src=\"img.jpg\"");
        }

        @Test
        @DisplayName("should remove onerror event handler")
        void should_remove_onerror_handler() {
            String input = "<img src=\"x\" onerror=\"alert('XSS')\">";
            String result = PBHtmlUtils.convertTextToHtml(input);

            assertThat(result)
                    .doesNotContain("onerror")
                    .doesNotContain("alert");
        }

        @Test
        @DisplayName("should remove onmouseover event handler")
        void should_remove_onmouseover_handler() {
            String input = "<div onmouseover=\"evil()\">Hover me</div>";
            String result = PBHtmlUtils.convertTextToHtml(input);

            assertThat(result)
                    .doesNotContain("onmouseover")
                    .doesNotContain("evil");
        }

        @Test
        @DisplayName("should remove multiple event handlers")
        void should_remove_multiple_event_handlers() {
            String input = "<div onclick=\"a()\" onmouseover=\"b()\" onload=\"c()\">Test</div>";
            String result = PBHtmlUtils.convertTextToHtml(input);

            assertThat(result)
                    .doesNotContain("onclick")
                    .doesNotContain("onmouseover")
                    .doesNotContain("onload");
        }

        @Test
        @DisplayName("should remove event handlers case insensitive")
        void should_remove_event_handlers_case_insensitive() {
            String input = "<div ONCLICK=\"alert('XSS')\" OnMouseOver=\"evil()\">Test</div>";
            String result = PBHtmlUtils.convertTextToHtml(input);

            assertThat(result)
                    .doesNotContain("ONCLICK")
                    .doesNotContain("OnMouseOver")
                    .doesNotContain("alert")
                    .doesNotContain("evil");
        }

        @Test
        @DisplayName("should replace javascript protocol in href")
        void should_replace_javascript_protocol() {
            String input = "<a href=\"javascript:alert('XSS')\">Click</a>";
            String result = PBHtmlUtils.convertTextToHtml(input);

            assertThat(result)
                    .doesNotContain("javascript:")
                    .doesNotContain("alert")
                    .contains("href=\"#\"");
        }

        @Test
        @DisplayName("should replace javascript protocol case insensitive")
        void should_replace_javascript_protocol_case_insensitive() {
            String input = "<a href=\"JAVASCRIPT:evil()\">Link</a>";
            String result = PBHtmlUtils.convertTextToHtml(input);

            assertThat(result)
                    .doesNotContain("JAVASCRIPT:")
                    .doesNotContain("evil")
                    .contains("href=\"#\"");
        }

        @Test
        @DisplayName("should handle combined XSS attacks")
        void should_handle_combined_xss_attacks() {
            String input = "<script>alert(1)</script><img onerror=\"alert(2)\" src=\"x\">"
                    + "<a href=\"javascript:alert(3)\">Link</a>";
            String result = PBHtmlUtils.convertTextToHtml(input);

            assertThat(result)
                    .doesNotContain("<script>")
                    .doesNotContain("</script>")
                    .doesNotContain("onerror")
                    .doesNotContain("javascript:")
                    .contains("href=\"#\"");
        }

        @Test
        @DisplayName("should preserve normal href links")
        void should_preserve_normal_href_links() {
            String input = "<a href=\"https://example.com/page?param=1\">Safe Link</a>";
            String result = PBHtmlUtils.convertTextToHtml(input);

            assertThat(result).contains("<a href=\"https://example.com/page?param=1\">Safe Link</a>");
        }

        @Test
        @DisplayName("should handle multiline script tags")
        void should_handle_multiline_script_tags() {
            String input = "Before<script>\n  var x = 1;\n  alert(x);\n</script>After";
            String result = PBHtmlUtils.convertTextToHtml(input);

            assertThat(result)
                    .doesNotContain("<script>")
                    .doesNotContain("</script>")
                    .doesNotContain("var x")
                    .contains("Before")
                    .contains("After");
        }

        // =====================================================================
        // LITERAL ESCAPE SEQUENCE TESTS (JSON strings with escaped characters)
        // =====================================================================

        @Test
        @DisplayName("should convert literal backslash-n to br tags")
        void should_convert_literal_backslash_n_to_br() {
            // Simulates JSON string where \n is stored as literal \\n
            String input = "Line 1\\nLine 2\\nLine 3";
            String result = PBHtmlUtils.convertTextToHtml(input);

            assertThat(result).isEqualTo("Line 1<br/>Line 2<br/>Line 3");
        }

        @Test
        @DisplayName("should convert literal backslash-r-n to br tags")
        void should_convert_literal_backslash_r_n_to_br() {
            // Simulates JSON string where \r\n is stored as literal \\r\\n
            String input = "Line 1\\r\\nLine 2\\r\\nLine 3";
            String result = PBHtmlUtils.convertTextToHtml(input);

            assertThat(result).isEqualTo("Line 1<br/>Line 2<br/>Line 3");
        }

        @Test
        @DisplayName("should convert literal backslash-r to br tags")
        void should_convert_literal_backslash_r_to_br() {
            // Simulates JSON string where \r is stored as literal \\r
            String input = "Line 1\\rLine 2\\rLine 3";
            String result = PBHtmlUtils.convertTextToHtml(input);

            assertThat(result).isEqualTo("Line 1<br/>Line 2<br/>Line 3");
        }

        @Test
        @DisplayName("should convert literal backslash-t to nbsp")
        void should_convert_literal_backslash_t_to_nbsp() {
            // Simulates JSON string where \t is stored as literal \\t
            String input = "Column1\\tColumn2";
            String result = PBHtmlUtils.convertTextToHtml(input);

            assertThat(result).isEqualTo("Column1&nbsp;&nbsp;&nbsp;&nbsp;Column2");
        }

        @Test
        @DisplayName("should handle mixed literal and real escape sequences")
        void should_handle_mixed_literal_and_real_escapes() {
            // Mix of literal (from JSON) and real control characters
            String input = "Line 1\\nLine 2\nLine 3";
            String result = PBHtmlUtils.convertTextToHtml(input);

            assertThat(result).isEqualTo("Line 1<br/>Line 2<br/>Line 3");
        }

        @Test
        @DisplayName("should handle multiple literal tabs")
        void should_handle_multiple_literal_tabs() {
            String input = "Col1\\tCol2\\tCol3";
            String result = PBHtmlUtils.convertTextToHtml(input);

            assertThat(result).isEqualTo("Col1&nbsp;&nbsp;&nbsp;&nbsp;Col2&nbsp;&nbsp;&nbsp;&nbsp;Col3");
        }

        @Test
        @DisplayName("should handle mixed literal and real tabs")
        void should_handle_mixed_literal_and_real_tabs() {
            String input = "Col1\\tCol2\tCol3";
            String result = PBHtmlUtils.convertTextToHtml(input);

            assertThat(result).isEqualTo("Col1&nbsp;&nbsp;&nbsp;&nbsp;Col2&nbsp;&nbsp;&nbsp;&nbsp;Col3");
        }

        @Test
        @DisplayName("should handle complex JSON-like input with all escape types")
        void should_handle_complex_json_like_input() {
            // Simulates content that might come from a JSON field
            String input = "Dear User,\\r\\n\\r\\nYour request has been processed.\\n\\tDetails:\\n\\t\\tID: 123";
            String result = PBHtmlUtils.convertTextToHtml(input);

            assertThat(result)
                    .contains("<br/><br/>")  // Double line breaks
                    .contains("&nbsp;&nbsp;&nbsp;&nbsp;Details")  // Tab before Details
                    .doesNotContain("\\n")
                    .doesNotContain("\\r")
                    .doesNotContain("\\t");
        }
    }

    // =========================================================================
    // sanitizeXss TESTS (Package-private)
    // =========================================================================

    @Nested
    @DisplayName("sanitizeXss Method Tests")
    class SanitizeXssTests {

        @Test
        @DisplayName("should return null for null input")
        void should_return_null_for_null() {
            assertThat(PBHtmlUtils.sanitizeXss(null)).isNull();
        }

        @Test
        @DisplayName("should return empty for empty input")
        void should_return_empty_for_empty() {
            assertThat(PBHtmlUtils.sanitizeXss("")).isEmpty();
        }

        @Test
        @DisplayName("should not modify safe text")
        void should_not_modify_safe_text() {
            String input = "Hello World! This is safe text.";
            String result = PBHtmlUtils.sanitizeXss(input);

            assertThat(result).isEqualTo(input);
        }

        @Test
        @DisplayName("should remove script tags")
        void should_remove_script_tags() {
            String input = "Test<script>alert('XSS')</script>End";
            String result = PBHtmlUtils.sanitizeXss(input);

            assertThat(result).isEqualTo("TestEnd");
        }

        @Test
        @DisplayName("should remove script tags with mixed case")
        void should_remove_script_tags_mixed_case() {
            String input = "Test<ScRiPt>evil()</ScRiPt>End";
            String result = PBHtmlUtils.sanitizeXss(input);

            assertThat(result).isEqualTo("TestEnd");
        }

        @Test
        @DisplayName("should remove orphan script tags")
        void should_remove_orphan_script_tags() {
            String input = "Test<script>End";
            String result = PBHtmlUtils.sanitizeXss(input);

            assertThat(result).isEqualTo("TestEnd");
        }

        @Test
        @DisplayName("should remove event handlers with double quotes")
        void should_remove_event_handlers_double_quotes() {
            String input = "<button onclick=\"evil()\">Click</button>";
            String result = PBHtmlUtils.sanitizeXss(input);

            assertThat(result)
                    .isEqualTo("<button>Click</button>")
                    .doesNotContain("onclick");
        }

        @Test
        @DisplayName("should remove event handlers with single quotes")
        void should_remove_event_handlers_single_quotes() {
            String input = "<button onclick='evil()'>Click</button>";
            String result = PBHtmlUtils.sanitizeXss(input);

            assertThat(result)
                    .doesNotContain("onclick")
                    .contains("<button");
        }

        @Test
        @DisplayName("should remove all on* event handlers")
        void should_remove_all_on_event_handlers() {
            String[] events = {"onclick", "onload", "onerror", "onmouseover", "onmouseout",
                    "onfocus", "onblur", "onsubmit", "onkeydown", "onkeyup"};

            for (String event : events) {
                String input = "<div " + event + "=\"evil()\">Test</div>";
                String result = PBHtmlUtils.sanitizeXss(input);

                assertThat(result)
                        .doesNotContain(event)
                        .as("Should remove " + event);
            }
        }

        @Test
        @DisplayName("should replace javascript protocol with safe href")
        void should_replace_javascript_protocol() {
            String input = "<a href=\"javascript:alert('XSS')\">Link</a>";
            String result = PBHtmlUtils.sanitizeXss(input);

            assertThat(result)
                    .doesNotContain("javascript:")
                    .contains("href=\"#\"");
        }

        @Test
        @DisplayName("should preserve safe anchor tags")
        void should_preserve_safe_anchor_tags() {
            String input = "<a href=\"https://safe.com\">Safe</a>";
            String result = PBHtmlUtils.sanitizeXss(input);

            assertThat(result).isEqualTo(input);
        }

        @Test
        @DisplayName("should preserve safe HTML content")
        void should_preserve_safe_html() {
            String input = "<p><strong>Bold</strong> and <em>italic</em></p>";
            String result = PBHtmlUtils.sanitizeXss(input);

            assertThat(result).isEqualTo(input);
        }

        @Test
        @DisplayName("should handle nested dangerous elements")
        void should_handle_nested_dangerous_elements() {
            String input = "<div onclick=\"a()\"><span onmouseover=\"b()\">Text</span></div>";
            String result = PBHtmlUtils.sanitizeXss(input);

            assertThat(result)
                    .doesNotContain("onclick")
                    .doesNotContain("onmouseover")
                    .contains("<div>")
                    .contains("<span>")
                    .contains("Text");
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
        @DisplayName("should sanitize XSS and convert newlines before applying valid template")
        void should_sanitize_xss_and_convert_before_applying() {
            String textContent = "Hello <script>alert('XSS')</script>\nNew line";

            String result = PBHtmlUtils.prepareEmailContent(textContent, SIMPLE_TEMPLATE);

            assertThat(result)
                    .doesNotContain("<script>")
                    .doesNotContain("</script>")
                    .doesNotContain("alert")
                    .contains("<br/>")
                    .doesNotContain("{{content}}");
        }

        @Test
        @DisplayName("should preserve legitimate links when template is valid")
        void should_preserve_legitimate_links() {
            String textContent = "Visit <a href=\"https://example.com\">our site</a> for more info.";

            String result = PBHtmlUtils.prepareEmailContent(textContent, SIMPLE_TEMPLATE);

            assertThat(result)
                    .contains("<a href=\"https://example.com\">our site</a>")
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
                    .doesNotContain("<br/>");
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
