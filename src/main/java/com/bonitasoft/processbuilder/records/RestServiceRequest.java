package com.bonitasoft.processbuilder.records;

import com.bonitasoft.processbuilder.enums.RestContentType;
import com.bonitasoft.processbuilder.enums.RestHttpMethod;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a REST service request configuration.
 * <p>
 * This record contains all the information needed to execute a REST API call,
 * including the URL, HTTP method, headers, body, authentication, and timeout settings.
 * </p>
 *
 * @param url             The full URL to call (required)
 * @param method          The HTTP method (GET, POST, PUT, etc.)
 * @param headers         Additional headers to include in the request
 * @param queryParams     Query parameters to append to the URL
 * @param body            The request body (for POST, PUT, PATCH)
 * @param contentType     The content type of the request body
 * @param auth            Authentication configuration
 * @param timeoutMs       Connection and read timeout in milliseconds
 * @param followRedirects Whether to follow HTTP redirects
 * @param verifySsl       Whether to verify SSL certificates
 * @author Bonitasoft
 * @since 1.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record RestServiceRequest(
        String url,
        RestHttpMethod method,
        Map<String, String> headers,
        Map<String, String> queryParams,
        String body,
        RestContentType contentType,
        RestAuthConfig auth,
        int timeoutMs,
        boolean followRedirects,
        boolean verifySsl
) {

    /**
     * Default timeout in milliseconds (30 seconds).
     */
    public static final int DEFAULT_TIMEOUT_MS = 30000;

    /**
     * Compact constructor with validation and defaults.
     */
    public RestServiceRequest {
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("URL cannot be null or blank");
        }
        url = url.trim();
        method = method != null ? method : RestHttpMethod.GET;
        headers = headers != null ? Map.copyOf(headers) : Collections.emptyMap();
        queryParams = queryParams != null ? Map.copyOf(queryParams) : Collections.emptyMap();
        contentType = contentType != null ? contentType : RestContentType.JSON;
        auth = auth != null ? auth : RestAuthConfig.none();
        timeoutMs = timeoutMs > 0 ? timeoutMs : DEFAULT_TIMEOUT_MS;
    }

    // ========================================================================
    // Builder Pattern
    // ========================================================================

    /**
     * Creates a new builder for RestServiceRequest.
     *
     * @param url The URL to call
     * @return A new builder instance
     */
    public static Builder builder(String url) {
        return new Builder(url);
    }

    /**
     * Builder class for constructing RestServiceRequest instances.
     */
    public static class Builder {
        private final String url;
        private RestHttpMethod method = RestHttpMethod.GET;
        private Map<String, String> headers = new HashMap<>();
        private Map<String, String> queryParams = new HashMap<>();
        private String body;
        private RestContentType contentType = RestContentType.JSON;
        private RestAuthConfig auth = RestAuthConfig.none();
        private int timeoutMs = DEFAULT_TIMEOUT_MS;
        private boolean followRedirects = true;
        private boolean verifySsl = true;

        private Builder(String url) {
            this.url = url;
        }

        public Builder method(RestHttpMethod method) {
            this.method = method;
            return this;
        }

        public Builder get() {
            return method(RestHttpMethod.GET);
        }

        public Builder post() {
            return method(RestHttpMethod.POST);
        }

        public Builder put() {
            return method(RestHttpMethod.PUT);
        }

        public Builder patch() {
            return method(RestHttpMethod.PATCH);
        }

        public Builder delete() {
            return method(RestHttpMethod.DELETE);
        }

        public Builder header(String name, String value) {
            this.headers.put(name, value);
            return this;
        }

        public Builder headers(Map<String, String> headers) {
            this.headers.putAll(headers);
            return this;
        }

        public Builder queryParam(String name, String value) {
            this.queryParams.put(name, value);
            return this;
        }

        public Builder queryParams(Map<String, String> params) {
            this.queryParams.putAll(params);
            return this;
        }

        public Builder body(String body) {
            this.body = body;
            return this;
        }

        public Builder jsonBody(Object obj) {
            try {
                this.body = new ObjectMapper().writeValueAsString(obj);
                this.contentType = RestContentType.JSON;
            } catch (Exception e) {
                throw new IllegalArgumentException("Failed to serialize object to JSON", e);
            }
            return this;
        }

        public Builder contentType(RestContentType contentType) {
            this.contentType = contentType;
            return this;
        }

        public Builder auth(RestAuthConfig auth) {
            this.auth = auth;
            return this;
        }

        public Builder basicAuth(String username, String password) {
            this.auth = RestAuthConfig.basic(username, password);
            return this;
        }

        public Builder bearerAuth(String token) {
            this.auth = RestAuthConfig.bearer(token);
            return this;
        }

        public Builder apiKeyAuth(String keyName, String keyValue) {
            this.auth = RestAuthConfig.apiKey(keyName, keyValue,
                    com.bonitasoft.processbuilder.enums.RestApiKeyLocation.HEADER);
            return this;
        }

        public Builder timeout(int timeoutMs) {
            this.timeoutMs = timeoutMs;
            return this;
        }

        public Builder followRedirects(boolean follow) {
            this.followRedirects = follow;
            return this;
        }

        public Builder verifySsl(boolean verify) {
            this.verifySsl = verify;
            return this;
        }

        public RestServiceRequest build() {
            return new RestServiceRequest(
                    url, method, headers, queryParams, body,
                    contentType, auth, timeoutMs, followRedirects, verifySsl
            );
        }
    }

    // ========================================================================
    // Factory Methods
    // ========================================================================

    /**
     * Creates a simple GET request.
     *
     * @param url The URL to call
     * @return A GET request
     */
    public static RestServiceRequest get(String url) {
        return builder(url).get().build();
    }

    /**
     * Creates a POST request with JSON body.
     *
     * @param url  The URL to call
     * @param body The JSON body string
     * @return A POST request
     */
    public static RestServiceRequest postJson(String url, String body) {
        return builder(url).post().body(body).contentType(RestContentType.JSON).build();
    }

    /**
     * Parses a RestServiceRequest from JSON.
     *
     * @param requestNode The JSON node containing the request configuration
     * @param logger      Optional logger for warnings
     * @return The parsed RestServiceRequest
     */
    public static RestServiceRequest fromJson(JsonNode requestNode, Logger logger) {
        if (requestNode == null || requestNode.isNull()) {
            throw new IllegalArgumentException("Request JSON node cannot be null");
        }

        String url = getRequiredText(requestNode, "url");

        Builder builder = builder(url);

        // Parse method
        String methodStr = getTextValue(requestNode, "method", "GET");
        RestHttpMethod.fromKey(methodStr).ifPresent(builder::method);

        // Parse headers
        JsonNode headersNode = requestNode.get("headers");
        if (headersNode != null && headersNode.isObject()) {
            headersNode.fields().forEachRemaining(entry ->
                    builder.header(entry.getKey(), entry.getValue().asText()));
        }

        // Parse query params
        JsonNode paramsNode = requestNode.get("queryParams");
        if (paramsNode != null && paramsNode.isObject()) {
            paramsNode.fields().forEachRemaining(entry ->
                    builder.queryParam(entry.getKey(), entry.getValue().asText()));
        }

        // Parse body
        JsonNode bodyNode = requestNode.get("body");
        if (bodyNode != null && !bodyNode.isNull()) {
            if (bodyNode.isObject() || bodyNode.isArray()) {
                builder.body(bodyNode.toString());
            } else {
                builder.body(bodyNode.asText());
            }
        }

        // Parse content type
        String contentTypeStr = getTextValue(requestNode, "contentType", "application/json");
        RestContentType.fromMimeType(contentTypeStr).ifPresent(builder::contentType);

        // Parse auth
        JsonNode authNode = requestNode.get("auth");
        if (authNode != null && !authNode.isNull()) {
            builder.auth(RestAuthConfig.fromJson(authNode, logger));
        }

        // Parse timeout
        JsonNode timeoutNode = requestNode.get("timeoutMs");
        if (timeoutNode != null && timeoutNode.isNumber()) {
            builder.timeout(timeoutNode.asInt(DEFAULT_TIMEOUT_MS));
        }

        // Parse follow redirects
        JsonNode redirectsNode = requestNode.get("followRedirects");
        if (redirectsNode != null && redirectsNode.isBoolean()) {
            builder.followRedirects(redirectsNode.asBoolean(true));
        }

        // Parse verify SSL
        JsonNode sslNode = requestNode.get("verifySsl");
        if (sslNode != null && sslNode.isBoolean()) {
            builder.verifySsl(sslNode.asBoolean(true));
        }

        return builder.build();
    }

    private static String getRequiredText(JsonNode node, String field) {
        JsonNode fieldNode = node.get(field);
        if (fieldNode == null || fieldNode.isNull() || fieldNode.asText().isBlank()) {
            throw new IllegalArgumentException("Required field '" + field + "' is missing or blank");
        }
        return fieldNode.asText().trim();
    }

    private static String getTextValue(JsonNode node, String field, String defaultValue) {
        JsonNode fieldNode = node.get(field);
        if (fieldNode == null || fieldNode.isNull()) {
            return defaultValue;
        }
        String value = fieldNode.asText();
        return (value == null || value.isBlank()) ? defaultValue : value.trim();
    }

    // ========================================================================
    // Utility Methods
    // ========================================================================

    /**
     * Builds the full URL including query parameters.
     *
     * @return The full URL with query parameters
     */
    public String buildFullUrl() {
        if (queryParams.isEmpty() && auth.getAuthQueryParams().isEmpty()) {
            return url;
        }

        StringBuilder fullUrl = new StringBuilder(url);
        boolean hasQueryString = url.contains("?");

        // Add auth query params first
        for (Map.Entry<String, String> param : auth.getAuthQueryParams().entrySet()) {
            fullUrl.append(hasQueryString ? "&" : "?");
            fullUrl.append(urlEncode(param.getKey())).append("=").append(urlEncode(param.getValue()));
            hasQueryString = true;
        }

        // Add regular query params
        for (Map.Entry<String, String> param : queryParams.entrySet()) {
            fullUrl.append(hasQueryString ? "&" : "?");
            fullUrl.append(urlEncode(param.getKey())).append("=").append(urlEncode(param.getValue()));
            hasQueryString = true;
        }

        return fullUrl.toString();
    }

    /**
     * Builds all headers including auth headers and content type.
     *
     * @return Combined headers map
     */
    public Map<String, String> buildAllHeaders() {
        Map<String, String> allHeaders = new HashMap<>();

        // Add content type if there's a body
        if (body != null && !body.isEmpty()) {
            allHeaders.put("Content-Type", contentType.getMimeType());
        }

        // Add auth headers
        allHeaders.putAll(auth.getAuthHeaders());

        // Add custom headers (can override defaults)
        allHeaders.putAll(headers);

        return allHeaders;
    }

    /**
     * Checks if this request has a body.
     *
     * @return true if the request has a non-empty body
     */
    public boolean hasBody() {
        return body != null && !body.isEmpty();
    }

    private static String urlEncode(String value) {
        try {
            return java.net.URLEncoder.encode(value, java.nio.charset.StandardCharsets.UTF_8);
        } catch (Exception e) {
            return value;
        }
    }
}
