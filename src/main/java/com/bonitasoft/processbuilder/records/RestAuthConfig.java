package com.bonitasoft.processbuilder.records;

import com.bonitasoft.processbuilder.enums.RestApiKeyLocation;
import com.bonitasoft.processbuilder.enums.RestAuthenticationType;
import com.bonitasoft.processbuilder.enums.RestOAuth2ClientAuthMethod;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;

import java.util.Base64;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

/**
 * Sealed interface representing authentication configuration for REST services.
 * <p>
 * This interface defines the contract for all authentication configuration types.
 * Each implementation corresponds to a specific {@link RestAuthenticationType}.
 * </p>
 *
 * @author Bonitasoft
 * @since 1.0
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "authType"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = RestAuthConfig.NoAuth.class, name = "none"),
        @JsonSubTypes.Type(value = RestAuthConfig.BasicAuth.class, name = "basic"),
        @JsonSubTypes.Type(value = RestAuthConfig.BearerAuth.class, name = "bearer"),
        @JsonSubTypes.Type(value = RestAuthConfig.ApiKeyAuth.class, name = "apiKey"),
        @JsonSubTypes.Type(value = RestAuthConfig.OAuth2ClientCredentials.class, name = "oauth2ClientCredentials"),
        @JsonSubTypes.Type(value = RestAuthConfig.OAuth2Password.class, name = "oauth2Password"),
        @JsonSubTypes.Type(value = RestAuthConfig.CustomAuth.class, name = "custom")
})
public sealed interface RestAuthConfig permits
        RestAuthConfig.NoAuth,
        RestAuthConfig.BasicAuth,
        RestAuthConfig.BearerAuth,
        RestAuthConfig.ApiKeyAuth,
        RestAuthConfig.OAuth2ClientCredentials,
        RestAuthConfig.OAuth2Password,
        RestAuthConfig.CustomAuth {

    /**
     * Gets the authentication type for this configuration.
     *
     * @return The authentication type
     */
    RestAuthenticationType getAuthType();

    /**
     * Generates the HTTP headers required for this authentication.
     *
     * @return Map of header name to header value
     */
    Map<String, String> getAuthHeaders();

    /**
     * Gets any query parameters required for this authentication.
     *
     * @return Map of parameter name to parameter value
     */
    default Map<String, String> getAuthQueryParams() {
        return Collections.emptyMap();
    }

    // ========================================================================
    // Factory Methods
    // ========================================================================

    /**
     * Creates an empty/no authentication configuration.
     *
     * @return NoAuth instance
     */
    static NoAuth none() {
        return new NoAuth();
    }

    /**
     * Creates a basic authentication configuration.
     *
     * @param username The username
     * @param password The password
     * @return BasicAuth instance
     */
    static BasicAuth basic(String username, String password) {
        return new BasicAuth(username, password, true);
    }

    /**
     * Creates a bearer token authentication configuration.
     *
     * @param token The bearer token
     * @return BearerAuth instance
     */
    static BearerAuth bearer(String token) {
        return new BearerAuth(token);
    }

    /**
     * Creates an API key authentication configuration.
     *
     * @param keyName  The name of the API key header/parameter
     * @param keyValue The API key value
     * @param location Where to send the key (HEADER or QUERY_PARAM)
     * @return ApiKeyAuth instance
     */
    static ApiKeyAuth apiKey(String keyName, String keyValue, RestApiKeyLocation location) {
        return new ApiKeyAuth(keyName, keyValue, location);
    }

    /**
     * Creates an OAuth2 Client Credentials configuration.
     *
     * @param tokenUrl     The token endpoint URL
     * @param clientId     The client ID
     * @param clientSecret The client secret
     * @return OAuth2ClientCredentials instance
     */
    static OAuth2ClientCredentials oauth2ClientCredentials(String tokenUrl, String clientId, String clientSecret) {
        return new OAuth2ClientCredentials(tokenUrl, clientId, clientSecret, null, null, RestOAuth2ClientAuthMethod.BODY);
    }

    /**
     * Parses authentication configuration from JSON.
     *
     * @param authNode The JSON node containing auth configuration
     * @param logger   Optional logger for warnings
     * @return The parsed RestAuthConfig
     */
    static RestAuthConfig fromJson(JsonNode authNode, Logger logger) {
        if (authNode == null || authNode.isNull() || authNode.isEmpty()) {
            return none();
        }

        String authType = getTextValue(authNode, "authType", "none");

        return switch (authType.toLowerCase()) {
            case "basic" -> new BasicAuth(
                    getTextValue(authNode, "username", ""),
                    getTextValue(authNode, "password", ""),
                    getBooleanValue(authNode, "preemptive", true)
            );
            case "bearer" -> new BearerAuth(
                    getTextValue(authNode, "token", "")
            );
            case "apikey", "api_key" -> new ApiKeyAuth(
                    getTextValue(authNode, "keyName", "X-API-Key"),
                    getTextValue(authNode, "keyValue", ""),
                    RestApiKeyLocation.fromKey(getTextValue(authNode, "location", "header"))
                            .orElse(RestApiKeyLocation.HEADER)
            );
            case "oauth2clientcredentials", "oauth2_client_credentials" -> new OAuth2ClientCredentials(
                    getTextValue(authNode, "tokenUrl", ""),
                    getTextValue(authNode, "clientId", ""),
                    getTextValue(authNode, "clientSecret", ""),
                    getTextValue(authNode, "scope", null),
                    getTextValue(authNode, "audience", null),
                    RestOAuth2ClientAuthMethod.fromKey(getTextValue(authNode, "clientAuthMethod", "body"))
                            .orElse(RestOAuth2ClientAuthMethod.BODY)
            );
            case "oauth2password", "oauth2_password" -> new OAuth2Password(
                    getTextValue(authNode, "tokenUrl", ""),
                    getTextValue(authNode, "clientId", ""),
                    getTextValue(authNode, "clientSecret", null),
                    getTextValue(authNode, "username", ""),
                    getTextValue(authNode, "password", ""),
                    getTextValue(authNode, "scope", null)
            );
            case "custom" -> {
                Map<String, String> headers = Collections.emptyMap();
                JsonNode headersNode = authNode.get("headers");
                if (headersNode != null && headersNode.isObject()) {
                    try {
                        ObjectMapper mapper = new ObjectMapper();
                        headers = mapper.convertValue(headersNode,
                                mapper.getTypeFactory().constructMapType(Map.class, String.class, String.class));
                    } catch (Exception e) {
                        if (logger != null) {
                            logger.warn("Failed to parse custom auth headers: {}", e.getMessage());
                        }
                    }
                }
                yield new CustomAuth(headers);
            }
            default -> none();
        };
    }

    private static String getTextValue(JsonNode node, String field, String defaultValue) {
        JsonNode fieldNode = node.get(field);
        if (fieldNode == null || fieldNode.isNull()) {
            return defaultValue;
        }
        String value = fieldNode.asText();
        return (value == null || value.isBlank()) ? defaultValue : value.trim();
    }

    private static boolean getBooleanValue(JsonNode node, String field, boolean defaultValue) {
        JsonNode fieldNode = node.get(field);
        if (fieldNode == null || fieldNode.isNull()) {
            return defaultValue;
        }
        return fieldNode.asBoolean(defaultValue);
    }

    // ========================================================================
    // Record Implementations
    // ========================================================================

    /**
     * No authentication configuration.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    record NoAuth() implements RestAuthConfig {
        @Override
        public RestAuthenticationType getAuthType() {
            return RestAuthenticationType.NONE;
        }

        @Override
        public Map<String, String> getAuthHeaders() {
            return Collections.emptyMap();
        }
    }

    /**
     * HTTP Basic Authentication configuration.
     *
     * @param username   The username
     * @param password   The password
     * @param preemptive Whether to send credentials without waiting for 401
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    record BasicAuth(String username, String password, boolean preemptive) implements RestAuthConfig {

        public BasicAuth {
            username = username != null ? username : "";
            password = password != null ? password : "";
        }

        @Override
        public RestAuthenticationType getAuthType() {
            return RestAuthenticationType.BASIC;
        }

        @Override
        public Map<String, String> getAuthHeaders() {
            String credentials = username + ":" + password;
            String encoded = Base64.getEncoder().encodeToString(credentials.getBytes());
            return Map.of("Authorization", "Basic " + encoded);
        }
    }

    /**
     * Bearer token authentication configuration.
     *
     * @param token The bearer token (JWT, OAuth2 access token, etc.)
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    record BearerAuth(String token) implements RestAuthConfig {

        public BearerAuth {
            token = token != null ? token : "";
        }

        @Override
        public RestAuthenticationType getAuthType() {
            return RestAuthenticationType.BEARER;
        }

        @Override
        public Map<String, String> getAuthHeaders() {
            return Map.of("Authorization", "Bearer " + token);
        }
    }

    /**
     * API Key authentication configuration.
     *
     * @param keyName  The name of the header or query parameter
     * @param keyValue The API key value
     * @param location Where to send the key (HEADER or QUERY_PARAM)
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    record ApiKeyAuth(String keyName, String keyValue, RestApiKeyLocation location) implements RestAuthConfig {

        public ApiKeyAuth {
            keyName = keyName != null && !keyName.isBlank() ? keyName : "X-API-Key";
            keyValue = keyValue != null ? keyValue : "";
            location = location != null ? location : RestApiKeyLocation.HEADER;
        }

        @Override
        public RestAuthenticationType getAuthType() {
            return RestAuthenticationType.API_KEY;
        }

        @Override
        public Map<String, String> getAuthHeaders() {
            if (location == RestApiKeyLocation.HEADER) {
                return Map.of(keyName, keyValue);
            }
            return Collections.emptyMap();
        }

        @Override
        public Map<String, String> getAuthQueryParams() {
            if (location == RestApiKeyLocation.QUERY_PARAM) {
                return Map.of(keyName, keyValue);
            }
            return Collections.emptyMap();
        }
    }

    /**
     * OAuth 2.0 Client Credentials authentication configuration.
     *
     * @param tokenUrl         The token endpoint URL
     * @param clientId         The client ID
     * @param clientSecret     The client secret
     * @param scope            Optional scope(s) to request
     * @param audience         Optional audience (for Auth0, etc.)
     * @param clientAuthMethod How to send client credentials (BODY or HEADER)
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    record OAuth2ClientCredentials(
            String tokenUrl,
            String clientId,
            String clientSecret,
            String scope,
            String audience,
            RestOAuth2ClientAuthMethod clientAuthMethod
    ) implements RestAuthConfig {

        public OAuth2ClientCredentials {
            tokenUrl = tokenUrl != null ? tokenUrl : "";
            clientId = clientId != null ? clientId : "";
            clientSecret = clientSecret != null ? clientSecret : "";
            clientAuthMethod = clientAuthMethod != null ? clientAuthMethod : RestOAuth2ClientAuthMethod.BODY;
        }

        @Override
        public RestAuthenticationType getAuthType() {
            return RestAuthenticationType.OAUTH2_CLIENT_CREDENTIALS;
        }

        @Override
        public Map<String, String> getAuthHeaders() {
            // Headers will be set after token exchange by the executor
            return Collections.emptyMap();
        }

        /**
         * Gets the headers for the token request (not the API request).
         *
         * @return Headers for token endpoint
         */
        public Map<String, String> getTokenRequestHeaders() {
            if (clientAuthMethod == RestOAuth2ClientAuthMethod.HEADER) {
                String credentials = clientId + ":" + clientSecret;
                String encoded = Base64.getEncoder().encodeToString(credentials.getBytes());
                return Map.of(
                        "Authorization", "Basic " + encoded,
                        "Content-Type", "application/x-www-form-urlencoded"
                );
            }
            return Map.of("Content-Type", "application/x-www-form-urlencoded");
        }

        /**
         * Gets the body parameters for the token request.
         *
         * @return Form parameters for token endpoint
         */
        public String getTokenRequestBody() {
            StringBuilder body = new StringBuilder("grant_type=client_credentials");

            if (clientAuthMethod == RestOAuth2ClientAuthMethod.BODY) {
                body.append("&client_id=").append(urlEncode(clientId));
                body.append("&client_secret=").append(urlEncode(clientSecret));
            }

            if (scope != null && !scope.isBlank()) {
                body.append("&scope=").append(urlEncode(scope));
            }

            if (audience != null && !audience.isBlank()) {
                body.append("&audience=").append(urlEncode(audience));
            }

            return body.toString();
        }

        private static String urlEncode(String value) {
            try {
                return java.net.URLEncoder.encode(value, java.nio.charset.StandardCharsets.UTF_8);
            } catch (Exception e) {
                return value;
            }
        }
    }

    /**
     * OAuth 2.0 Resource Owner Password authentication configuration.
     *
     * @param tokenUrl     The token endpoint URL
     * @param clientId     The client ID
     * @param clientSecret The client secret (optional for public clients)
     * @param username     The resource owner username
     * @param password     The resource owner password
     * @param scope        Optional scope(s) to request
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    record OAuth2Password(
            String tokenUrl,
            String clientId,
            String clientSecret,
            String username,
            String password,
            String scope
    ) implements RestAuthConfig {

        public OAuth2Password {
            tokenUrl = tokenUrl != null ? tokenUrl : "";
            clientId = clientId != null ? clientId : "";
            username = username != null ? username : "";
            password = password != null ? password : "";
        }

        @Override
        public RestAuthenticationType getAuthType() {
            return RestAuthenticationType.OAUTH2_PASSWORD;
        }

        @Override
        public Map<String, String> getAuthHeaders() {
            return Collections.emptyMap();
        }

        /**
         * Gets the body parameters for the token request.
         *
         * @return Form parameters for token endpoint
         */
        public String getTokenRequestBody() {
            StringBuilder body = new StringBuilder("grant_type=password");
            body.append("&username=").append(urlEncode(username));
            body.append("&password=").append(urlEncode(password));
            body.append("&client_id=").append(urlEncode(clientId));

            if (clientSecret != null && !clientSecret.isBlank()) {
                body.append("&client_secret=").append(urlEncode(clientSecret));
            }

            if (scope != null && !scope.isBlank()) {
                body.append("&scope=").append(urlEncode(scope));
            }

            return body.toString();
        }

        private static String urlEncode(String value) {
            try {
                return java.net.URLEncoder.encode(value, java.nio.charset.StandardCharsets.UTF_8);
            } catch (Exception e) {
                return value;
            }
        }
    }

    /**
     * Custom authentication with user-defined headers.
     *
     * @param headers Custom headers to add to requests
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    record CustomAuth(Map<String, String> headers) implements RestAuthConfig {

        public CustomAuth {
            headers = headers != null ? Map.copyOf(headers) : Collections.emptyMap();
        }

        @Override
        public RestAuthenticationType getAuthType() {
            return RestAuthenticationType.CUSTOM;
        }

        @Override
        public Map<String, String> getAuthHeaders() {
            return headers;
        }
    }
}
