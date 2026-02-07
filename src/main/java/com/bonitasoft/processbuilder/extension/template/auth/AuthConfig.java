package com.bonitasoft.processbuilder.extension.template.auth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Sealed interface for REST API authentication configuration.
 * <p>
 * This interface defines the contract for all authentication types supported
 * by the REST API template system. Each implementation handles a specific
 * authentication mechanism.
 * </p>
 *
 * @author Process Builder Team
 * @since 2025-02-06
 */
public sealed interface AuthConfig
        permits NoAuthConfig, BasicAuthConfig, BearerAuthConfig,
                ApiKeyAuthConfig, OAuth2ClientConfig, OAuth2PasswordConfig {

    /**
     * Gets the authentication type identifier.
     *
     * @return The authentication type string (lowercase)
     */
    String getAuthType();

    /**
     * Converts this configuration to a JSON node for storage.
     *
     * @param mapper The ObjectMapper to use for JSON creation
     * @return JSON representation of this configuration
     */
    JsonNode toJson(ObjectMapper mapper);

    /**
     * Converts this configuration to a JSON node with sensitive fields encrypted.
     *
     * @param mapper The ObjectMapper to use for JSON creation
     * @return JSON representation with encrypted sensitive fields
     */
    JsonNode toJsonEncrypted(ObjectMapper mapper);

    /**
     * Creates an AuthConfig from a JSON node.
     *
     * @param node   The JSON node to parse
     * @param mapper The ObjectMapper to use
     * @return The appropriate AuthConfig implementation
     */
    static AuthConfig fromJson(JsonNode node, ObjectMapper mapper) {
        if (node == null || node.isNull() || !node.isObject()) {
            return NoAuthConfig.INSTANCE;
        }

        String authType = null;
        if (node.has("authType")) {
            authType = node.get("authType").asText();
        } else if (node.has("type")) {
            authType = node.get("type").asText();
        }

        if (authType == null || authType.isBlank()) {
            return NoAuthConfig.INSTANCE;
        }

        return switch (authType.toLowerCase()) {
            case "basic" -> BasicAuthConfig.fromJson(node);
            case "bearer" -> BearerAuthConfig.fromJson(node);
            case "api_key", "apikey" -> ApiKeyAuthConfig.fromJson(node);
            case "oauth2_client_credentials", "oauth2clientcredentials" -> OAuth2ClientConfig.fromJson(node);
            case "oauth2_password", "oauth2password" -> OAuth2PasswordConfig.fromJson(node);
            default -> NoAuthConfig.INSTANCE;
        };
    }

    /**
     * Helper method to get a text value from a JSON node with a default.
     */
    static String getText(JsonNode node, String field, String defaultValue) {
        if (node == null || !node.has(field) || node.get(field).isNull()) {
            return defaultValue;
        }
        String value = node.get(field).asText();
        return (value == null || value.isBlank()) ? defaultValue : value;
    }

    /**
     * Helper method to get a boolean value from a JSON node with a default.
     */
    static boolean getBoolean(JsonNode node, String field, boolean defaultValue) {
        if (node == null || !node.has(field) || node.get(field).isNull()) {
            return defaultValue;
        }
        return node.get(field).asBoolean(defaultValue);
    }
}
