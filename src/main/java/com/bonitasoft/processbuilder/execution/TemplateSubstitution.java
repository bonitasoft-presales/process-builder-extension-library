package com.bonitasoft.processbuilder.execution;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Substitutes {{param}} placeholders in template strings with actual values.
 * <p>
 * Example: "https://api.example.com/{{version}}/users" with params {"version": "v2"}
 * becomes "https://api.example.com/v2/users"
 * </p>
 */
public final class TemplateSubstitution {

    private static final Pattern TEMPLATE_PATTERN = Pattern.compile("\\{\\{([^}]+)\\}\\}");

    private TemplateSubstitution() {}

    /**
     * Substitutes all {{param}} occurrences in template with values from params map.
     * Unresolved placeholders are kept as-is.
     *
     * @param template The template string
     * @param params   Parameter values (may contain String or Object values)
     * @return The resolved string
     */
    public static String substitute(String template, Map<String, ?> params) {
        if (template == null || template.isEmpty() || params == null || params.isEmpty()) {
            return template;
        }

        Matcher matcher = TEMPLATE_PATTERN.matcher(template);
        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            String paramName = matcher.group(1).trim();
            Object value = params.get(paramName);
            String replacement = value != null ? String.valueOf(value) : matcher.group(0);
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(result);

        return result.toString();
    }

    /**
     * Builds a final URL by joining baseUrl and path with proper slash handling.
     */
    public static String buildFinalUrl(String baseUrl, String path) {
        if (path == null || path.isEmpty()) {
            return baseUrl;
        }
        String cleanBase = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        String cleanPath = path.startsWith("/") ? path.substring(1) : path;
        return cleanBase + "/" + cleanPath;
    }
}
