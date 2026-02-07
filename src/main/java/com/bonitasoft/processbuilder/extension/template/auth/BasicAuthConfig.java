package com.bonitasoft.processbuilder.extension.template.auth;

import com.bonitasoft.processbuilder.extension.PasswordCrypto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Objects;

/**
 * Configuration for HTTP Basic Authentication.
 *
 * @author Process Builder Team
 * @since 2025-02-06
 */
public record BasicAuthConfig(
        String username,
        String password,
        boolean preemptive
) implements AuthConfig {

    public BasicAuthConfig(String username, String password) {
        this(username, password, true);
    }

    public BasicAuthConfig {
        Objects.requireNonNull(username, "Username cannot be null");
        Objects.requireNonNull(password, "Password cannot be null");
    }

    @Override
    public String getAuthType() {
        return "basic";
    }

    @Override
    public JsonNode toJson(ObjectMapper mapper) {
        ObjectNode node = mapper.createObjectNode();
        node.put("authType", getAuthType());
        node.put("username", username);
        node.put("password", password);
        node.put("preemptive", preemptive);
        return node;
    }

    @Override
    public JsonNode toJsonEncrypted(ObjectMapper mapper) {
        ObjectNode node = mapper.createObjectNode();
        node.put("authType", getAuthType());
        node.put("username", username);
        String encryptedPassword = PasswordCrypto.isMasterPasswordConfigured()
                ? PasswordCrypto.encryptIfNeeded(password)
                : password;
        node.put("password", encryptedPassword);
        node.put("preemptive", preemptive);
        return node;
    }

    public static BasicAuthConfig fromJson(JsonNode node) {
        return new BasicAuthConfig(
                AuthConfig.getText(node, "username", ""),
                AuthConfig.getText(node, "password", ""),
                AuthConfig.getBoolean(node, "preemptive", true)
        );
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String username = "";
        private String password = "";
        private boolean preemptive = true;

        public Builder username(String username) { this.username = username; return this; }
        public Builder password(String password) { this.password = password; return this; }
        public Builder preemptive(boolean preemptive) { this.preemptive = preemptive; return this; }
        public BasicAuthConfig build() { return new BasicAuthConfig(username, password, preemptive); }
    }
}
