package com.bonitasoft.processbuilder.extension.template.auth;

import com.bonitasoft.processbuilder.extension.PasswordCrypto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Objects;

/**
 * Configuration for Bearer Token Authentication.
 *
 * @author Process Builder Team
 * @since 2025-02-06
 */
public record BearerAuthConfig(String token) implements AuthConfig {

    public BearerAuthConfig {
        Objects.requireNonNull(token, "Token cannot be null");
    }

    @Override
    public String getAuthType() {
        return "bearer";
    }

    @Override
    public JsonNode toJson(ObjectMapper mapper) {
        ObjectNode node = mapper.createObjectNode();
        node.put("authType", getAuthType());
        node.put("token", token);
        return node;
    }

    @Override
    public JsonNode toJsonEncrypted(ObjectMapper mapper) {
        ObjectNode node = mapper.createObjectNode();
        node.put("authType", getAuthType());
        String encryptedToken = PasswordCrypto.isMasterPasswordConfigured()
                ? PasswordCrypto.encryptIfNeeded(token)
                : token;
        node.put("token", encryptedToken);
        return node;
    }

    public static BearerAuthConfig fromJson(JsonNode node) {
        return new BearerAuthConfig(AuthConfig.getText(node, "token", ""));
    }

    public static BearerAuthConfig of(String token) {
        return new BearerAuthConfig(token);
    }
}
