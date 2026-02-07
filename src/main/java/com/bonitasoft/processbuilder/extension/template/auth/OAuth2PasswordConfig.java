package com.bonitasoft.processbuilder.extension.template.auth;

import com.bonitasoft.processbuilder.extension.PasswordCrypto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Objects;

/**
 * Configuration for OAuth2 Resource Owner Password Credentials flow.
 *
 * @author Process Builder Team
 * @since 2025-02-06
 */
public record OAuth2PasswordConfig(
        String tokenUrl,
        String clientId,
        String clientSecret,
        String username,
        String password,
        String scope
) implements AuthConfig {

    public OAuth2PasswordConfig {
        Objects.requireNonNull(tokenUrl, "Token URL cannot be null");
        Objects.requireNonNull(clientId, "Client ID cannot be null");
        Objects.requireNonNull(username, "Username cannot be null");
        Objects.requireNonNull(password, "Password cannot be null");
    }

    public OAuth2PasswordConfig(String tokenUrl, String clientId, String username, String password) {
        this(tokenUrl, clientId, null, username, password, null);
    }

    @Override
    public String getAuthType() {
        return "oauth2_password";
    }

    @Override
    public JsonNode toJson(ObjectMapper mapper) {
        ObjectNode node = mapper.createObjectNode();
        node.put("authType", getAuthType());
        node.put("tokenUrl", tokenUrl);
        node.put("clientId", clientId);
        if (clientSecret != null && !clientSecret.isBlank()) node.put("clientSecret", clientSecret);
        node.put("username", username);
        node.put("password", password);
        if (scope != null && !scope.isBlank()) node.put("scope", scope);
        return node;
    }

    @Override
    public JsonNode toJsonEncrypted(ObjectMapper mapper) {
        ObjectNode node = mapper.createObjectNode();
        node.put("authType", getAuthType());
        node.put("tokenUrl", tokenUrl);
        node.put("clientId", clientId);

        boolean canEncrypt = PasswordCrypto.isMasterPasswordConfigured();

        if (clientSecret != null && !clientSecret.isBlank()) {
            String encryptedSecret = canEncrypt ? PasswordCrypto.encryptIfNeeded(clientSecret) : clientSecret;
            node.put("clientSecret", encryptedSecret);
        }
        node.put("username", username);
        String encryptedPassword = canEncrypt ? PasswordCrypto.encryptIfNeeded(password) : password;
        node.put("password", encryptedPassword);
        if (scope != null && !scope.isBlank()) node.put("scope", scope);
        return node;
    }

    public static OAuth2PasswordConfig fromJson(JsonNode node) {
        return new OAuth2PasswordConfig(
                AuthConfig.getText(node, "tokenUrl", ""),
                AuthConfig.getText(node, "clientId", ""),
                AuthConfig.getText(node, "clientSecret", null),
                AuthConfig.getText(node, "username", ""),
                AuthConfig.getText(node, "password", ""),
                AuthConfig.getText(node, "scope", null)
        );
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String tokenUrl = "";
        private String clientId = "";
        private String clientSecret = null;
        private String username = "";
        private String password = "";
        private String scope = null;

        public Builder tokenUrl(String tokenUrl) { this.tokenUrl = tokenUrl; return this; }
        public Builder clientId(String clientId) { this.clientId = clientId; return this; }
        public Builder clientSecret(String clientSecret) { this.clientSecret = clientSecret; return this; }
        public Builder username(String username) { this.username = username; return this; }
        public Builder password(String password) { this.password = password; return this; }
        public Builder scope(String scope) { this.scope = scope; return this; }
        public OAuth2PasswordConfig build() { return new OAuth2PasswordConfig(tokenUrl, clientId, clientSecret, username, password, scope); }
    }
}
