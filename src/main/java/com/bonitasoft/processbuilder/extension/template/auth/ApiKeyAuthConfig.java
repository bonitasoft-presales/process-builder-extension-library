package com.bonitasoft.processbuilder.extension.template.auth;

import com.bonitasoft.processbuilder.extension.PasswordCrypto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Objects;

/**
 * Configuration for API Key Authentication.
 *
 * @author Process Builder Team
 * @since 2025-02-06
 */
public record ApiKeyAuthConfig(
        String keyName,
        String keyValue,
        Location location
) implements AuthConfig {

    public enum Location {
        HEADER("header"),
        QUERY("queryParam");

        private final String value;

        Location(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static Location fromString(String value) {
            if (value == null || value.isBlank()) {
                return HEADER;
            }
            String upper = value.toUpperCase();
            if (upper.equals("QUERY") || upper.equals("QUERYPARAM") || upper.equals("QUERY_PARAM")) {
                return QUERY;
            }
            return HEADER;
        }
    }

    public ApiKeyAuthConfig {
        Objects.requireNonNull(keyName, "Key name cannot be null");
        Objects.requireNonNull(keyValue, "Key value cannot be null");
        Objects.requireNonNull(location, "Location cannot be null");
    }

    public ApiKeyAuthConfig(String keyName, String keyValue) {
        this(keyName, keyValue, Location.HEADER);
    }

    @Override
    public String getAuthType() {
        return "api_key";
    }

    @Override
    public JsonNode toJson(ObjectMapper mapper) {
        ObjectNode node = mapper.createObjectNode();
        node.put("authType", getAuthType());
        node.put("keyName", keyName);
        node.put("keyValue", keyValue);
        node.put("location", location.getValue());
        return node;
    }

    @Override
    public JsonNode toJsonEncrypted(ObjectMapper mapper) {
        ObjectNode node = mapper.createObjectNode();
        node.put("authType", getAuthType());
        node.put("keyName", keyName);
        String encryptedKeyValue = PasswordCrypto.isMasterPasswordConfigured()
                ? PasswordCrypto.encryptIfNeeded(keyValue)
                : keyValue;
        node.put("keyValue", encryptedKeyValue);
        node.put("location", location.getValue());
        return node;
    }

    public static ApiKeyAuthConfig fromJson(JsonNode node) {
        String locationStr = AuthConfig.getText(node, "location", "header");
        if (locationStr.equals("header") && node.has("apiKeyLocation")) {
            locationStr = node.get("apiKeyLocation").asText("header");
        }

        String keyName = AuthConfig.getText(node, "keyName", "X-API-Key");
        if (keyName.equals("X-API-Key") && node.has("apiKeyName")) {
            keyName = node.get("apiKeyName").asText("X-API-Key");
        }

        String keyValue = AuthConfig.getText(node, "keyValue", "");
        if (keyValue.isEmpty() && node.has("apiKeyValue")) {
            keyValue = node.get("apiKeyValue").asText("");
        }

        return new ApiKeyAuthConfig(keyName, keyValue, Location.fromString(locationStr));
    }

    public static ApiKeyAuthConfig queryParam(String keyName, String keyValue) {
        return new ApiKeyAuthConfig(keyName, keyValue, Location.QUERY);
    }

    public static ApiKeyAuthConfig header(String keyName, String keyValue) {
        return new ApiKeyAuthConfig(keyName, keyValue, Location.HEADER);
    }
}
