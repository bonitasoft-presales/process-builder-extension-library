package com.bonitasoft.processbuilder.execution;

import com.bonitasoft.processbuilder.extension.PasswordCrypto;
import com.bonitasoft.processbuilder.records.RestAuthConfig;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Pipeline for normalizing and decrypting authentication configuration.
 * <p>
 * Extracted from ExecuteRestService to be shared between REST Extension and custom connector.
 * Handles backward-compatible field name normalization and AES/GCM decryption.
 * </p>
 */
public final class AuthPipeline {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthPipeline.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private AuthPipeline() {}

    /**
     * Full pipeline: normalize → decrypt → parse.
     *
     * @param authNode Raw auth JSON from PBConfiguration
     * @return Parsed RestAuthConfig ready for HTTP execution
     */
    public static RestAuthConfig resolve(JsonNode authNode) {
        if (authNode == null || authNode.isNull() || !authNode.isObject()) {
            return RestAuthConfig.none();
        }
        JsonNode normalized = normalizeAuthConfig(authNode);
        JsonNode decrypted = decryptSensitiveFields(normalized);
        RestAuthConfig config = RestAuthConfig.fromJson(decrypted, LOGGER);
        return config != null ? config : RestAuthConfig.none();
    }

    /**
     * Normalizes auth configuration JSON for backward compatibility.
     * Handles field name variations: "type"→"authType", "apiKeyName"→"keyName", etc.
     */
    public static JsonNode normalizeAuthConfig(JsonNode authNode) {
        if (authNode == null || authNode.isNull() || !authNode.isObject()) {
            return authNode;
        }

        ObjectNode normalized = MAPPER.createObjectNode();

        String type = null;
        if (authNode.has("type")) {
            type = authNode.get("type").asText();
        } else if (authNode.has("authType")) {
            type = authNode.get("authType").asText();
        }

        if (type != null) {
            normalized.put("authType", type.toLowerCase());
        }

        if (type != null && type.equalsIgnoreCase("API_KEY")) {
            if (authNode.has("apiKeyName")) {
                normalized.put("keyName", authNode.get("apiKeyName").asText());
            } else if (authNode.has("keyName")) {
                normalized.put("keyName", authNode.get("keyName").asText());
            }

            if (authNode.has("apiKeyValue")) {
                normalized.put("keyValue", authNode.get("apiKeyValue").asText());
            } else if (authNode.has("keyValue")) {
                normalized.put("keyValue", authNode.get("keyValue").asText());
            }

            String location = null;
            if (authNode.has("apiKeyLocation")) {
                location = authNode.get("apiKeyLocation").asText();
            } else if (authNode.has("location")) {
                location = authNode.get("location").asText();
            }
            if (location != null) {
                normalized.put("location", normalizeApiKeyLocation(location));
            }
        } else {
            authNode.fields().forEachRemaining(field -> {
                String key = field.getKey();
                if (!normalized.has(key) && !"type".equals(key) && !"authType".equals(key)) {
                    normalized.set(key, field.getValue());
                }
            });
        }

        LOGGER.debug("Normalized auth config: {} -> {}", authNode, normalized);
        return normalized;
    }

    /**
     * Decrypts sensitive fields in auth configuration using PasswordCrypto (AES/GCM).
     */
    public static JsonNode decryptSensitiveFields(JsonNode authNode) {
        if (authNode == null || authNode.isNull() || !authNode.isObject()) {
            return authNode;
        }

        if (!PasswordCrypto.isMasterPasswordConfigured()) {
            LOGGER.debug("Master password not configured, skipping decryption");
            return authNode;
        }

        ObjectNode decrypted = authNode.deepCopy();
        String type = decrypted.has("authType") ? decrypted.get("authType").asText().toLowerCase() : "";

        switch (type) {
            case "basic" -> decryptField(decrypted, "password");
            case "bearer" -> decryptField(decrypted, "token");
            case "api_key" -> decryptField(decrypted, "keyValue");
            case "oauth2_client_credentials" -> decryptField(decrypted, "clientSecret");
            case "oauth2_password" -> {
                decryptField(decrypted, "password");
                decryptField(decrypted, "clientSecret");
            }
            case "oauth2_jwt_bearer", "oauth2jwtbearer" -> decryptField(decrypted, "privateKey");
            default -> { /* No sensitive fields */ }
        }

        return decrypted;
    }

    private static void decryptField(ObjectNode node, String fieldName) {
        if (node.has(fieldName) && !node.get(fieldName).isNull()) {
            String encrypted = node.get(fieldName).asText();
            if (encrypted != null && !encrypted.isEmpty()) {
                try {
                    String decryptedValue = PasswordCrypto.decryptIfNeeded(encrypted);
                    node.put(fieldName, decryptedValue);
                    LOGGER.debug("Decrypted field '{}' successfully", fieldName);
                } catch (Exception e) {
                    LOGGER.warn("Failed to decrypt field '{}', using original value: {}", fieldName, e.getMessage());
                }
            }
        }
    }

    private static String normalizeApiKeyLocation(String location) {
        if (location == null) return "header";
        String upper = location.toUpperCase();
        if (upper.equals("QUERY") || upper.equals("QUERY_PARAM") || upper.equals("QUERYPARAM")) {
            return "queryParam";
        } else if (upper.equals("HEADER")) {
            return "header";
        }
        return location.toLowerCase();
    }
}
