package com.bonitasoft.processbuilder.extension;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class providing HTML manipulation methods for email content processing.
 * <p>
 * This class provides methods to:
 * </p>
 * <ul>
 *   <li>Convert plain text to HTML-safe format (handling newlines, special characters)</li>
 *   <li>Apply email templates by replacing content placeholders</li>
 * </ul>
 * <p>
 * This class is designed to be used from Groovy scripts in Bonita processes,
 * where the template and DAO operations are handled externally.
 * </p>
 *
 * @author Bonitasoft
 * @since 1.0
 */
public final class PBHtmlUtils {

    /**
     * Logger for this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(PBHtmlUtils.class);

    /**
     * The placeholder pattern for content substitution in email templates.
     * Matches {{content}} with optional whitespace inside the braces.
     */
    private static final Pattern CONTENT_PLACEHOLDER_PATTERN = Pattern.compile("\\{\\{\\s*content\\s*\\}\\}");

    /**
     * Default placeholder value when template is missing the content placeholder.
     */
    private static final String DEFAULT_PLACEHOLDER = "{{content}}";

    /**
     * Private constructor to prevent instantiation of this utility class.
     *
     * @throws UnsupportedOperationException always, to enforce the utility pattern.
     */
    private PBHtmlUtils() {
        throw new UnsupportedOperationException(
                "This is a " + this.getClass().getSimpleName() + " class and cannot be instantiated.");
    }

    /**
     * Converts plain text content to HTML-safe format.
     * <p>
     * This method performs the following transformations:
     * </p>
     * <ul>
     *   <li>Escapes HTML special characters ({@code &}, {@code <}, {@code >}, {@code "}, {@code '})</li>
     *   <li>Converts Windows line breaks ({@code \r\n}) to {@code <br/>}</li>
     *   <li>Converts Unix line breaks ({@code \n}) to {@code <br/>}</li>
     *   <li>Converts carriage returns ({@code \r}) to {@code <br/>}</li>
     *   <li>Converts tabs ({@code \t}) to four non-breaking spaces ({@code &nbsp;})</li>
     *   <li>Converts multiple consecutive spaces to non-breaking spaces</li>
     * </ul>
     *
     * @param text The plain text to convert to HTML format.
     * @return The HTML-formatted text, or null if input is null, or empty string if input is empty.
     */
    public static String convertTextToHtml(String text) {
        if (text == null) {
            return null;
        }

        if (text.isEmpty()) {
            return text;
        }

        String result = text;

        // Step 1: Escape HTML special characters (must be done first)
        result = escapeHtmlSpecialChars(result);

        // Step 2: Convert Windows line breaks (\r\n) first, then Unix (\n), then standalone (\r)
        result = result.replace("\r\n", "<br/>");
        result = result.replace("\n", "<br/>");
        result = result.replace("\r", "<br/>");

        // Step 3: Convert tabs to four non-breaking spaces
        result = result.replace("\t", "&nbsp;&nbsp;&nbsp;&nbsp;");

        // Step 4: Convert multiple consecutive spaces to preserve formatting
        result = preserveMultipleSpaces(result);

        LOGGER.debug("Converted text to HTML. Input length: {}, Output length: {}", text.length(), result.length());

        return result;
    }

    /**
     * Applies an email template by replacing the {{content}} placeholder with the provided content.
     * <p>
     * The method expects the template to contain a {@code {{content}}} placeholder
     * (with optional whitespace inside the braces). The content is inserted at this location.
     * </p>
     * <p>
     * <strong>Important:</strong> This method assumes the content has already been converted
     * to HTML format using {@link #convertTextToHtml(String)} if it was plain text.
     * </p>
     *
     * @param template The HTML email template containing the {{content}} placeholder.
     * @param content The HTML content to insert into the template.
     * @return The complete email HTML with the content inserted, or:
     *         <ul>
     *           <li>The original content if template is null or empty</li>
     *           <li>The template unchanged if content is null</li>
     *           <li>The template with placeholder replaced by empty string if content is empty</li>
     *         </ul>
     */
    public static String applyEmailTemplate(String template, String content) {
        // If template is null or empty, return the content as-is
        if (template == null || template.isEmpty()) {
            LOGGER.warn("Email template is null or empty. Returning content without template wrapper.");
            return content;
        }

        // If content is null, return the template unchanged
        if (content == null) {
            LOGGER.warn("Content is null. Returning template with placeholder unchanged.");
            return template;
        }

        // Check if template contains the placeholder
        Matcher matcher = CONTENT_PLACEHOLDER_PATTERN.matcher(template);
        if (!matcher.find()) {
            LOGGER.warn("Template does not contain {{{{content}}}} placeholder. "
                    + "Appending content at the end of template.");
            return template + content;
        }

        // Replace the placeholder with the content
        String result = matcher.replaceFirst(Matcher.quoteReplacement(content));

        LOGGER.debug("Applied email template. Template length: {}, Content length: {}, Result length: {}",
                template.length(), content.length(), result.length());

        return result;
    }

    /**
     * Prepares email content by converting text to HTML and applying the email template.
     * <p>
     * This is a convenience method that combines {@link #convertTextToHtml(String)} and
     * {@link #applyEmailTemplate(String, String)} in a single call.
     * </p>
     * <p>
     * <strong>Important:</strong> If the email template is null, empty, or does not contain
     * the {@code {{content}}} placeholder, this method returns the original text content
     * without any HTML conversion or template application.
     * </p>
     * <p>
     * <strong>Usage from Bonita Groovy script:</strong>
     * </p>
     * <pre>{@code
     * // In your Groovy script:
     * String finalResult = PBStringUtils.resolveTemplateVariables(originalResult, dataResolver)
     *
     * // Get the email template from PBConfiguration
     * PBConfiguration pbConfiguration = pBConfigurationDAO.findByFullNameAndRefEntityTypeName(
     *     SmtpType.EMAIL_TEMPLATE.name(),
     *     ConfigurationType.SMTP.name()
     * )
     * String emailTemplateString = pbConfiguration.getConfigValue()
     *
     * // Apply the template to the content
     * String emailBody = PBHtmlUtils.prepareEmailContent(finalResult, emailTemplateString)
     * }</pre>
     *
     * @param textContent The plain text content to be included in the email.
     * @param emailTemplate The HTML email template containing {{content}} placeholder.
     * @return The complete email HTML ready to be sent, or the original textContent
     *         if the template is invalid (null, empty, or missing placeholder).
     */
    public static String prepareEmailContent(String textContent, String emailTemplate) {
        // If template is null, empty, or doesn't contain the placeholder, return content as-is
        if (!isValidTemplate(emailTemplate)) {
            LOGGER.warn("Email template is invalid (null, empty, or missing {{content}} placeholder). "
                    + "Returning original content without HTML conversion.");
            return textContent;
        }

        // Convert text content to HTML format
        String htmlContent = convertTextToHtml(textContent);

        // Apply the email template
        return applyEmailTemplate(emailTemplate, htmlContent);
    }

    /**
     * Checks if the given template is valid for email content processing.
     * <p>
     * A valid template must be non-null, non-empty, and contain the {@code {{content}}} placeholder.
     * </p>
     *
     * @param template The template to validate.
     * @return {@code true} if the template is valid, {@code false} otherwise.
     */
    static boolean isValidTemplate(String template) {
        if (template == null || template.isEmpty()) {
            return false;
        }
        return CONTENT_PLACEHOLDER_PATTERN.matcher(template).find();
    }

    /**
     * Escapes HTML special characters in the given string.
     * <p>
     * The following characters are escaped:
     * <ul>
     *   <li>{@code &} becomes {@code &amp;}</li>
     *   <li>{@code <} becomes {@code &lt;}</li>
     *   <li>{@code >} becomes {@code &gt;}</li>
     *   <li>{@code "} becomes {@code &quot;}</li>
     *   <li>{@code '} becomes {@code &#39;}</li>
     * </ul>
     * </p>
     *
     * @param text The text to escape.
     * @return The escaped text.
     */
    static String escapeHtmlSpecialChars(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        StringBuilder result = new StringBuilder(text.length() + 16);
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            switch (c) {
                case '&':
                    result.append("&amp;");
                    break;
                case '<':
                    result.append("&lt;");
                    break;
                case '>':
                    result.append("&gt;");
                    break;
                case '"':
                    result.append("&quot;");
                    break;
                case '\'':
                    result.append("&#39;");
                    break;
                default:
                    result.append(c);
            }
        }
        return result.toString();
    }

    /**
     * Preserves multiple consecutive spaces by converting them to non-breaking spaces.
     * <p>
     * In HTML, multiple consecutive spaces are collapsed to a single space.
     * This method alternates between regular spaces and non-breaking spaces
     * to preserve the visual formatting while remaining HTML-compliant.
     * </p>
     *
     * @param text The text with potential multiple spaces.
     * @return The text with multiple spaces preserved for HTML rendering.
     */
    static String preserveMultipleSpaces(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        StringBuilder result = new StringBuilder(text.length() + 16);
        boolean previousWasSpace = false;

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == ' ') {
                if (previousWasSpace) {
                    result.append("&nbsp;");
                } else {
                    result.append(' ');
                    previousWasSpace = true;
                }
            } else {
                result.append(c);
                previousWasSpace = false;
            }
        }
        return result.toString();
    }
}
