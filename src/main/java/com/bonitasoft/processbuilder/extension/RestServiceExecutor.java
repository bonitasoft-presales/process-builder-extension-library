package com.bonitasoft.processbuilder.extension;

import com.bonitasoft.processbuilder.enums.RestAuthenticationType;
import com.bonitasoft.processbuilder.enums.RestContentType;
import com.bonitasoft.processbuilder.records.RestAuthConfig;
import com.bonitasoft.processbuilder.records.RestServiceRequest;
import com.bonitasoft.processbuilder.records.RestServiceResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Utility class for executing REST service calls.
 * <p>
 * This class provides methods to execute HTTP requests with various authentication
 * methods, handling OAuth2 token exchange, retries, and response parsing.
 * </p>
 * <p>
 * Example usage:
 * </p>
 * <pre>{@code
 * // Simple GET request
 * RestServiceResponse response = RestServiceExecutor.execute(
 *     RestServiceRequest.get("https://api.example.com/data")
 * );
 *
 * // POST with JSON body and Basic Auth
 * RestServiceResponse response = RestServiceExecutor.execute(
 *     RestServiceRequest.builder("https://api.example.com/data")
 *         .post()
 *         .jsonBody(Map.of("key", "value"))
 *         .basicAuth("user", "password")
 *         .build()
 * );
 *
 * // Execute from JSON configuration
 * RestServiceResponse response = RestServiceExecutor.executeFromJson(jsonString);
 * }</pre>
 *
 * @author Bonitasoft
 * @since 1.0
 */
public final class RestServiceExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestServiceExecutor.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    // Token cache for OAuth2 (thread-safe)
    private static final ConcurrentHashMap<String, CachedToken> TOKEN_CACHE = new ConcurrentHashMap<>();

    private RestServiceExecutor() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    // ========================================================================
    // Main Execution Methods
    // ========================================================================

    /**
     * Executes a REST service request.
     *
     * @param request The request configuration
     * @return The response from the service
     */
    public static RestServiceResponse execute(RestServiceRequest request) {
        return execute(request, null);
    }

    /**
     * Executes a REST service request with optional logger.
     *
     * @param request The request configuration
     * @param logger  Optional logger for debugging
     * @return The response from the service
     */
    public static RestServiceResponse execute(RestServiceRequest request, Logger logger) {
        if (request == null) {
            return RestServiceResponse.error("Request cannot be null", 0, null);
        }

        Logger log = logger != null ? logger : LOGGER;
        long startTime = System.currentTimeMillis();
        String fullUrl = request.buildFullUrl();

        try {
            log.debug("Executing {} request to: {}", request.method(), fullUrl);

            // Handle OAuth2 token exchange if needed
            RestAuthConfig effectiveAuth = resolveAuthentication(request.auth(), log);

            // Build HTTP client
            HttpClient client = buildHttpClient(request);

            // Build HTTP request
            HttpRequest httpRequest = buildHttpRequest(request, fullUrl, effectiveAuth);

            // Execute request
            HttpResponse<String> httpResponse = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            long executionTime = System.currentTimeMillis() - startTime;

            // Parse response headers
            Map<String, String> responseHeaders = new HashMap<>();
            httpResponse.headers().map().forEach((key, values) -> {
                if (!values.isEmpty()) {
                    responseHeaders.put(key, values.get(0));
                }
            });

            // Determine content type
            RestContentType responseContentType = RestContentType.fromMimeType(
                    responseHeaders.getOrDefault("content-type", "application/json")
            ).orElse(RestContentType.JSON);

            log.debug("Response: {} {} in {}ms", httpResponse.statusCode(), fullUrl, executionTime);

            return RestServiceResponse.success(
                    httpResponse.statusCode(),
                    responseHeaders,
                    httpResponse.body(),
                    responseContentType,
                    executionTime,
                    fullUrl
            );

        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            log.error("Error executing request to {}: {}", fullUrl, e.getMessage(), e);
            return RestServiceResponse.fromException(e, executionTime, fullUrl);
        }
    }

    /**
     * Executes a REST service request from a JSON configuration string.
     *
     * @param jsonConfig The JSON configuration string
     * @return The response from the service
     */
    public static RestServiceResponse executeFromJson(String jsonConfig) {
        return executeFromJson(jsonConfig, null);
    }

    /**
     * Executes a REST service request from a JSON configuration string with logger.
     *
     * @param jsonConfig The JSON configuration string
     * @param logger     Optional logger for debugging
     * @return The response from the service
     */
    public static RestServiceResponse executeFromJson(String jsonConfig, Logger logger) {
        Logger log = logger != null ? logger : LOGGER;
        long startTime = System.currentTimeMillis();

        try {
            if (jsonConfig == null || jsonConfig.isBlank()) {
                return RestServiceResponse.error("JSON configuration cannot be null or blank", 0, null);
            }

            JsonNode rootNode = OBJECT_MAPPER.readTree(jsonConfig);
            RestServiceRequest request = RestServiceRequest.fromJson(rootNode, log);

            return execute(request, log);

        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            log.error("Error parsing JSON configuration: {}", e.getMessage(), e);
            return RestServiceResponse.error("Invalid JSON configuration: " + e.getMessage(), executionTime, null);
        }
    }

    /**
     * Executes a REST service request from a JsonNode configuration.
     *
     * @param configNode The JSON node containing the configuration
     * @param logger     Optional logger for debugging
     * @return The response from the service
     */
    public static RestServiceResponse executeFromJson(JsonNode configNode, Logger logger) {
        Logger log = logger != null ? logger : LOGGER;
        long startTime = System.currentTimeMillis();

        try {
            if (configNode == null || configNode.isNull()) {
                return RestServiceResponse.error("Configuration node cannot be null", 0, null);
            }

            RestServiceRequest request = RestServiceRequest.fromJson(configNode, log);
            return execute(request, log);

        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            log.error("Error parsing configuration: {}", e.getMessage(), e);
            return RestServiceResponse.error("Invalid configuration: " + e.getMessage(), executionTime, null);
        }
    }

    // ========================================================================
    // OAuth2 Token Management
    // ========================================================================

    /**
     * Resolves authentication, performing OAuth2 token exchange if needed.
     *
     * @param authConfig The original auth configuration
     * @param logger     Logger for debugging
     * @return The effective auth configuration (with token if OAuth2)
     */
    private static RestAuthConfig resolveAuthentication(RestAuthConfig authConfig, Logger logger) {
        if (authConfig == null) {
            return RestAuthConfig.none();
        }

        RestAuthenticationType authType = authConfig.getAuthType();

        if (authType == RestAuthenticationType.OAUTH2_CLIENT_CREDENTIALS) {
            return resolveOAuth2ClientCredentials((RestAuthConfig.OAuth2ClientCredentials) authConfig, logger);
        }

        if (authType == RestAuthenticationType.OAUTH2_PASSWORD) {
            return resolveOAuth2Password((RestAuthConfig.OAuth2Password) authConfig, logger);
        }

        return authConfig;
    }

    /**
     * Resolves OAuth2 Client Credentials, obtaining access token.
     */
    private static RestAuthConfig resolveOAuth2ClientCredentials(
            RestAuthConfig.OAuth2ClientCredentials oauth2Config, Logger logger) {

        String cacheKey = "cc:" + oauth2Config.tokenUrl() + ":" + oauth2Config.clientId();

        // Check cache
        CachedToken cached = TOKEN_CACHE.get(cacheKey);
        if (cached != null && !cached.isExpired()) {
            logger.debug("Using cached OAuth2 token for {}", oauth2Config.clientId());
            return RestAuthConfig.bearer(cached.token);
        }

        logger.debug("Obtaining OAuth2 token from {}", oauth2Config.tokenUrl());

        try {
            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(10))
                    .build();

            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                    .uri(URI.create(oauth2Config.tokenUrl()))
                    .timeout(Duration.ofSeconds(30))
                    .POST(HttpRequest.BodyPublishers.ofString(oauth2Config.getTokenRequestBody()));

            // Add headers
            oauth2Config.getTokenRequestHeaders().forEach(requestBuilder::header);

            HttpResponse<String> response = client.send(requestBuilder.build(),
                    HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                JsonNode tokenResponse = OBJECT_MAPPER.readTree(response.body());

                String accessToken = tokenResponse.get(RestServiceConstants.OAUTH2_ACCESS_TOKEN).asText();
                int expiresIn = tokenResponse.has(RestServiceConstants.OAUTH2_EXPIRES_IN)
                        ? tokenResponse.get(RestServiceConstants.OAUTH2_EXPIRES_IN).asInt(3600)
                        : 3600;

                // Cache token (with 60 second buffer)
                TOKEN_CACHE.put(cacheKey, new CachedToken(accessToken, expiresIn - 60));

                logger.debug("OAuth2 token obtained successfully, expires in {} seconds", expiresIn);
                return RestAuthConfig.bearer(accessToken);

            } else {
                logger.error("OAuth2 token request failed: {} - {}", response.statusCode(), response.body());
                throw new RuntimeException("OAuth2 token request failed: " + response.statusCode());
            }

        } catch (Exception e) {
            logger.error("Error obtaining OAuth2 token: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to obtain OAuth2 token: " + e.getMessage(), e);
        }
    }

    /**
     * Resolves OAuth2 Password grant, obtaining access token.
     */
    private static RestAuthConfig resolveOAuth2Password(
            RestAuthConfig.OAuth2Password oauth2Config, Logger logger) {

        String cacheKey = "pwd:" + oauth2Config.tokenUrl() + ":" + oauth2Config.username();

        // Check cache
        CachedToken cached = TOKEN_CACHE.get(cacheKey);
        if (cached != null && !cached.isExpired()) {
            logger.debug("Using cached OAuth2 token for {}", oauth2Config.username());
            return RestAuthConfig.bearer(cached.token);
        }

        logger.debug("Obtaining OAuth2 password token from {}", oauth2Config.tokenUrl());

        try {
            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(10))
                    .build();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(oauth2Config.tokenUrl()))
                    .timeout(Duration.ofSeconds(30))
                    .header("Content-Type", RestServiceConstants.CONTENT_TYPE_FORM)
                    .POST(HttpRequest.BodyPublishers.ofString(oauth2Config.getTokenRequestBody()))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                JsonNode tokenResponse = OBJECT_MAPPER.readTree(response.body());

                String accessToken = tokenResponse.get(RestServiceConstants.OAUTH2_ACCESS_TOKEN).asText();
                int expiresIn = tokenResponse.has(RestServiceConstants.OAUTH2_EXPIRES_IN)
                        ? tokenResponse.get(RestServiceConstants.OAUTH2_EXPIRES_IN).asInt(3600)
                        : 3600;

                TOKEN_CACHE.put(cacheKey, new CachedToken(accessToken, expiresIn - 60));

                logger.debug("OAuth2 password token obtained successfully");
                return RestAuthConfig.bearer(accessToken);

            } else {
                logger.error("OAuth2 password token request failed: {}", response.statusCode());
                throw new RuntimeException("OAuth2 password token request failed: " + response.statusCode());
            }

        } catch (Exception e) {
            logger.error("Error obtaining OAuth2 password token: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to obtain OAuth2 token: " + e.getMessage(), e);
        }
    }

    /**
     * Clears the OAuth2 token cache.
     */
    public static void clearTokenCache() {
        TOKEN_CACHE.clear();
        LOGGER.debug("OAuth2 token cache cleared");
    }

    /**
     * Removes a specific token from the cache.
     *
     * @param tokenUrl The token URL
     * @param clientId The client ID (for client credentials) or username (for password grant)
     */
    public static void invalidateToken(String tokenUrl, String clientId) {
        TOKEN_CACHE.remove("cc:" + tokenUrl + ":" + clientId);
        TOKEN_CACHE.remove("pwd:" + tokenUrl + ":" + clientId);
    }

    // ========================================================================
    // HTTP Client and Request Building
    // ========================================================================

    /**
     * Builds an HttpClient based on request configuration.
     */
    private static HttpClient buildHttpClient(RestServiceRequest request) {
        HttpClient.Builder builder = HttpClient.newBuilder()
                .connectTimeout(Duration.ofMillis(request.timeoutMs()))
                .followRedirects(request.followRedirects()
                        ? HttpClient.Redirect.NORMAL
                        : HttpClient.Redirect.NEVER);

        // Handle SSL verification
        if (!request.verifySsl()) {
            try {
                SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, new TrustManager[]{new TrustAllManager()}, new SecureRandom());
                builder.sslContext(sslContext);
            } catch (Exception e) {
                LOGGER.warn("Failed to configure SSL context: {}", e.getMessage());
            }
        }

        return builder.build();
    }

    /**
     * Builds an HttpRequest from the request configuration.
     */
    private static HttpRequest buildHttpRequest(RestServiceRequest request, String fullUrl,
                                                 RestAuthConfig effectiveAuth) {

        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(fullUrl))
                .timeout(Duration.ofMillis(request.timeoutMs()));

        // Add all headers
        Map<String, String> allHeaders = new HashMap<>();

        // Content-Type for requests with body
        if (request.hasBody()) {
            allHeaders.put(RestServiceConstants.HEADER_CONTENT_TYPE, request.contentType().getMimeType());
        }

        // Add auth headers
        allHeaders.putAll(effectiveAuth.getAuthHeaders());

        // Add custom headers (can override defaults)
        allHeaders.putAll(request.headers());

        // Apply headers
        allHeaders.forEach(builder::header);

        // Set method and body
        HttpRequest.BodyPublisher bodyPublisher = request.hasBody()
                ? HttpRequest.BodyPublishers.ofString(request.body())
                : HttpRequest.BodyPublishers.noBody();

        builder.method(request.method().getKey(), bodyPublisher);

        return builder.build();
    }

    // ========================================================================
    // Utility Methods
    // ========================================================================

    /**
     * Extracts an access token from an OAuth2 token response.
     *
     * @param responseBody The token response body (JSON)
     * @return Optional containing the access token
     */
    public static Optional<String> extractAccessToken(String responseBody) {
        try {
            JsonNode node = OBJECT_MAPPER.readTree(responseBody);
            JsonNode tokenNode = node.get(RestServiceConstants.OAUTH2_ACCESS_TOKEN);
            if (tokenNode != null && !tokenNode.isNull()) {
                return Optional.of(tokenNode.asText());
            }
        } catch (Exception e) {
            LOGGER.warn("Failed to extract access token: {}", e.getMessage());
        }
        return Optional.empty();
    }

    /**
     * Creates a simple JSON request body from key-value pairs.
     *
     * @param keyValues Key-value pairs (must be even number of arguments)
     * @return JSON string
     */
    public static String createJsonBody(String... keyValues) {
        if (keyValues == null || keyValues.length == 0) {
            return "{}";
        }
        if (keyValues.length % 2 != 0) {
            throw new IllegalArgumentException("Key-value pairs must be even");
        }

        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < keyValues.length; i += 2) {
            map.put(keyValues[i], keyValues[i + 1]);
        }

        try {
            return OBJECT_MAPPER.writeValueAsString(map);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create JSON body", e);
        }
    }

    // ========================================================================
    // Helper Classes
    // ========================================================================

    /**
     * Cached OAuth2 token with expiration.
     */
    private record CachedToken(String token, long expiresAtMs) {

        CachedToken(String token, int expiresInSeconds) {
            this(token, System.currentTimeMillis() + (expiresInSeconds * 1000L));
        }

        boolean isExpired() {
            return System.currentTimeMillis() >= expiresAtMs;
        }
    }

    /**
     * TrustManager that accepts all certificates (for testing only).
     */
    private static class TrustAllManager implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) {
            // Trust all
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) {
            // Trust all
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }
}
