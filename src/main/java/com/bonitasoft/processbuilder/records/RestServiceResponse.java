package com.bonitasoft.processbuilder.records;

import com.bonitasoft.processbuilder.enums.RestContentType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

/**
 * Represents a REST service response.
 * <p>
 * This record contains all the information from a REST API response,
 * including the status code, headers, body, and execution metadata.
 * </p>
 *
 * @param statusCode     The HTTP status code
 * @param headers        Response headers
 * @param body           The response body as a string
 * @param contentType    The content type of the response
 * @param executionTimeMs Time taken to execute the request in milliseconds
 * @param errorMessage   Error message if the request failed (null if successful)
 * @param url            The URL that was called
 * @author Bonitasoft
 * @since 1.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record RestServiceResponse(
        int statusCode,
        Map<String, String> headers,
        String body,
        RestContentType contentType,
        long executionTimeMs,
        String errorMessage,
        String url
) {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * Compact constructor with defaults.
     */
    public RestServiceResponse {
        headers = headers != null ? Map.copyOf(headers) : Collections.emptyMap();
    }

    // ========================================================================
    // Factory Methods
    // ========================================================================

    /**
     * Creates a successful response.
     *
     * @param statusCode      The HTTP status code
     * @param headers         Response headers
     * @param body            Response body
     * @param contentType     Content type of the response
     * @param executionTimeMs Execution time
     * @param url             The URL called
     * @return A successful response
     */
    public static RestServiceResponse success(
            int statusCode,
            Map<String, String> headers,
            String body,
            RestContentType contentType,
            long executionTimeMs,
            String url) {
        return new RestServiceResponse(statusCode, headers, body, contentType, executionTimeMs, null, url);
    }

    /**
     * Creates an error response.
     *
     * @param errorMessage    The error message
     * @param executionTimeMs Execution time
     * @param url             The URL that was called
     * @return An error response with status code -1
     */
    public static RestServiceResponse error(String errorMessage, long executionTimeMs, String url) {
        return new RestServiceResponse(-1, Collections.emptyMap(), null, null, executionTimeMs, errorMessage, url);
    }

    /**
     * Creates an error response from an exception.
     *
     * @param exception       The exception that occurred
     * @param executionTimeMs Execution time
     * @param url             The URL that was called
     * @return An error response
     */
    public static RestServiceResponse fromException(Exception exception, long executionTimeMs, String url) {
        String message = exception.getMessage();
        if (message == null || message.isBlank()) {
            message = exception.getClass().getSimpleName();
        }
        return error(message, executionTimeMs, url);
    }

    // ========================================================================
    // Status Check Methods
    // ========================================================================

    /**
     * Checks if the response indicates success (2xx status code).
     *
     * @return true if status code is between 200 and 299
     */
    public boolean isSuccessful() {
        return statusCode >= 200 && statusCode < 300;
    }

    /**
     * Checks if the response indicates a client error (4xx status code).
     *
     * @return true if status code is between 400 and 499
     */
    public boolean isClientError() {
        return statusCode >= 400 && statusCode < 500;
    }

    /**
     * Checks if the response indicates a server error (5xx status code).
     *
     * @return true if status code is between 500 and 599
     */
    public boolean isServerError() {
        return statusCode >= 500 && statusCode < 600;
    }

    /**
     * Checks if there was an error (network error or non-success HTTP status).
     *
     * @return true if there was an error
     */
    public boolean isError() {
        return errorMessage != null || statusCode < 0 || statusCode >= 400;
    }

    /**
     * Checks if the response was a redirect (3xx status code).
     *
     * @return true if status code is between 300 and 399
     */
    public boolean isRedirect() {
        return statusCode >= 300 && statusCode < 400;
    }

    // ========================================================================
    // Body Parsing Methods
    // ========================================================================

    /**
     * Parses the response body as JSON.
     *
     * @return Optional containing the JsonNode, or empty if parsing fails
     */
    public Optional<JsonNode> bodyAsJson() {
        if (body == null || body.isBlank()) {
            return Optional.empty();
        }
        try {
            return Optional.of(OBJECT_MAPPER.readTree(body));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * Parses the response body as a specific type.
     *
     * @param <T>   The type to parse to
     * @param clazz The class of the type
     * @return Optional containing the parsed object, or empty if parsing fails
     */
    public <T> Optional<T> bodyAs(Class<T> clazz) {
        if (body == null || body.isBlank()) {
            return Optional.empty();
        }
        try {
            return Optional.of(OBJECT_MAPPER.readValue(body, clazz));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * Gets a specific field from the JSON body.
     *
     * @param fieldName The field name to extract
     * @return Optional containing the field value as string, or empty if not found
     */
    public Optional<String> getJsonField(String fieldName) {
        return bodyAsJson()
                .map(json -> json.get(fieldName))
                .filter(node -> !node.isNull())
                .map(JsonNode::asText);
    }

    /**
     * Checks if the response body contains JSON content.
     *
     * @return true if the content type is JSON and body is not empty
     */
    public boolean hasJsonBody() {
        return contentType != null && contentType.isJson() && body != null && !body.isBlank();
    }

    // ========================================================================
    // Header Methods
    // ========================================================================

    /**
     * Gets a specific header value (case-insensitive).
     *
     * @param headerName The header name
     * @return Optional containing the header value, or empty if not found
     */
    public Optional<String> getHeader(String headerName) {
        if (headerName == null) {
            return Optional.empty();
        }
        return headers.entrySet().stream()
                .filter(e -> e.getKey().equalsIgnoreCase(headerName))
                .map(Map.Entry::getValue)
                .findFirst();
    }

    /**
     * Gets the Location header (for redirects).
     *
     * @return Optional containing the Location header value
     */
    public Optional<String> getLocation() {
        return getHeader("Location");
    }

    // ========================================================================
    // Utility Methods
    // ========================================================================

    /**
     * Returns a summary of the response for logging.
     *
     * @return A summary string
     */
    public String toSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("HTTP ").append(statusCode);

        if (errorMessage != null) {
            summary.append(" ERROR: ").append(errorMessage);
        }

        summary.append(" (").append(executionTimeMs).append("ms)");

        if (body != null) {
            summary.append(" Body: ").append(body.length()).append(" chars");
        }

        return summary.toString();
    }

    /**
     * Creates a copy of this response with a different body.
     *
     * @param newBody The new body
     * @return A new response with the updated body
     */
    public RestServiceResponse withBody(String newBody) {
        return new RestServiceResponse(statusCode, headers, newBody, contentType, executionTimeMs, errorMessage, url);
    }

    /**
     * Creates a copy of this response with a different error message.
     *
     * @param newErrorMessage The new error message
     * @return A new response with the updated error message
     */
    public RestServiceResponse withError(String newErrorMessage) {
        return new RestServiceResponse(statusCode, headers, body, contentType, executionTimeMs, newErrorMessage, url);
    }
}
