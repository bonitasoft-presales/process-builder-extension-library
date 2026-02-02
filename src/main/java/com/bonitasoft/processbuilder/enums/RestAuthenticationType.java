package com.bonitasoft.processbuilder.enums;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Defines the supported authentication types for REST service calls.
 * <p>
 * This enumeration provides all authentication methods that can be used
 * when executing REST API calls from Bonita processes or REST API extensions.
 * </p>
 *
 * @author Bonitasoft
 * @since 1.0
 */
public enum RestAuthenticationType {

    /**
     * No authentication required. Used for public APIs.
     */
    NONE("none", "No authentication required"),

    /**
     * HTTP Basic Authentication using username and password.
     * Credentials are sent as Base64 encoded header: Authorization: Basic base64(user:pass)
     */
    BASIC("basic", "HTTP Basic Authentication with username and password"),

    /**
     * Bearer token authentication (JWT, OAuth2 access tokens).
     * Token is sent as header: Authorization: Bearer {token}
     */
    BEARER("bearer", "Bearer token authentication (JWT, OAuth2)"),

    /**
     * API Key authentication. Key can be sent in header or query parameter.
     */
    API_KEY("apiKey", "API Key authentication in header or query parameter"),

    /**
     * OAuth 2.0 Client Credentials grant type.
     * Used for server-to-server authentication without user context.
     */
    OAUTH2_CLIENT_CREDENTIALS("oauth2ClientCredentials", "OAuth 2.0 Client Credentials grant"),

    /**
     * OAuth 2.0 Resource Owner Password grant type.
     * Used when username/password are exchanged for tokens (legacy, not recommended).
     */
    OAUTH2_PASSWORD("oauth2Password", "OAuth 2.0 Resource Owner Password grant"),

    /**
     * Digest Authentication.
     * More secure than Basic as password is never sent in clear text.
     */
    DIGEST("digest", "HTTP Digest Authentication"),

    /**
     * NTLM Authentication for Windows-based services.
     */
    NTLM("ntlm", "Windows NTLM Authentication"),

    /**
     * Client Certificate Authentication (mTLS).
     * Uses X.509 certificates for mutual TLS authentication.
     */
    CERTIFICATE("certificate", "Client Certificate Authentication (mTLS)"),

    /**
     * Custom authentication with user-defined headers.
     */
    CUSTOM("custom", "Custom authentication with user-defined headers");

    private final String key;
    private final String description;

    /**
     * Private constructor for the enumeration.
     *
     * @param key         The technical key used for JSON mapping
     * @param description A human-readable description of the authentication type
     */
    RestAuthenticationType(String key, String description) {
        this.key = key;
        this.description = description;
    }

    /**
     * Gets the technical key of the authentication type.
     *
     * @return The technical key (lowercase)
     */
    public String getKey() {
        return key;
    }

    /**
     * Gets a brief description of the authentication type.
     *
     * @return The description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Checks if a given string corresponds to a valid enum constant.
     *
     * @param input The string to validate (case-insensitive)
     * @return {@code true} if the string is a valid enum constant
     */
    public static boolean isValid(String input) {
        if (input == null || input.trim().isEmpty()) {
            return false;
        }
        try {
            RestAuthenticationType.valueOf(input.trim().toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            // Try to match by key
            return fromKey(input).isPresent();
        }
    }

    /**
     * Finds an authentication type by its key (case-insensitive).
     *
     * @param key The key to search for
     * @return Optional containing the matching type, or empty if not found
     */
    public static Optional<RestAuthenticationType> fromKey(String key) {
        if (key == null || key.trim().isEmpty()) {
            return Optional.empty();
        }
        String normalizedKey = key.trim().toLowerCase();
        return Arrays.stream(values())
                .filter(type -> type.getKey().equalsIgnoreCase(normalizedKey))
                .findFirst();
    }

    /**
     * Retrieves all authentication types as a read-only Map.
     *
     * @return A map containing all authentication data (Key -> Description)
     */
    public static Map<String, String> getAllData() {
        Map<String, String> data = Arrays.stream(values())
                .collect(Collectors.toMap(
                        RestAuthenticationType::getKey,
                        RestAuthenticationType::getDescription,
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new
                ));
        return Collections.unmodifiableMap(data);
    }

    /**
     * Retrieves all technical keys as a read-only List.
     *
     * @return A list containing all authentication type keys
     */
    public static List<String> getAllKeysList() {
        return Arrays.stream(values())
                .map(RestAuthenticationType::getKey)
                .collect(Collectors.toUnmodifiableList());
    }

    /**
     * Checks if this authentication type requires credentials (username/password).
     *
     * @return {@code true} if credentials are required
     */
    public boolean requiresCredentials() {
        return this == BASIC || this == DIGEST || this == NTLM || this == OAUTH2_PASSWORD;
    }

    /**
     * Checks if this authentication type requires OAuth2 token exchange.
     *
     * @return {@code true} if OAuth2 token exchange is required
     */
    public boolean requiresOAuth2TokenExchange() {
        return this == OAUTH2_CLIENT_CREDENTIALS || this == OAUTH2_PASSWORD;
    }

    /**
     * Checks if this authentication type uses a static token.
     *
     * @return {@code true} if a static token is used
     */
    public boolean usesStaticToken() {
        return this == BEARER || this == API_KEY;
    }
}
