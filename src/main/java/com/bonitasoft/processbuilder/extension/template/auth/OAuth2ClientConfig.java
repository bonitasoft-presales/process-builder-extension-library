package com.bonitasoft.processbuilder.extension.template.auth;

import com.bonitasoft.processbuilder.extension.PasswordCrypto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Objects;

/**
 * Configuration for OAuth2 Client Credentials flow.
 *
 * @author Process Builder Team
 * @since 2025-02-06
 */
public record OAuth2ClientConfig(
        String tokenUrl,
        String clientId,
        String clientSecret,
        String scope,
        String audience,
        ClientAuthMethod clientAuthMethod
) implements AuthConfig {

    public enum ClientAuthMethod {
        BODY("body"),
        HEADER("header");

        private final String value;

        ClientAuthMethod(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static ClientAuthMethod fromString(String value) {
            if (value != null && value.equalsIgnoreCase("header")) {
                return HEADER;
            }
            return BODY;
        }
    }

    public OAuth2ClientConfig {
        Objects.requireNonNull(tokenUrl, "Token URL cannot be null");
        Objects.requireNonNull(clientId, "Client ID cannot be null");
        Objects.requireNonNull(clientSecret, "Client Secret cannot be null");
        if (clientAuthMethod == null) {
            clientAuthMethod = ClientAuthMethod.BODY;
        }
    }

    public OAuth2ClientConfig(String tokenUrl, String clientId, String clientSecret) {
        this(tokenUrl, clientId, clientSecret, null, null, ClientAuthMethod.BODY);
    }

    public OAuth2ClientConfig(String tokenUrl, String clientId, String clientSecret, String scope) {
        this(tokenUrl, clientId, clientSecret, scope, null, ClientAuthMethod.BODY);
    }

    @Override
    public String getAuthType() {
        return "oauth2_client_credentials";
    }

    @Override
    public JsonNode toJson(ObjectMapper mapper) {
        ObjectNode node = mapper.createObjectNode();
        node.put("authType", getAuthType());
        node.put("tokenUrl", tokenUrl);
        node.put("clientId", clientId);
        node.put("clientSecret", clientSecret);
        if (scope != null && !scope.isBlank()) node.put("scope", scope);
        if (audience != null && !audience.isBlank()) node.put("audience", audience);
        node.put("clientAuthMethod", clientAuthMethod.getValue());
        return node;
    }

    @Override
    public JsonNode toJsonEncrypted(ObjectMapper mapper) {
        ObjectNode node = mapper.createObjectNode();
        node.put("authType", getAuthType());
        node.put("tokenUrl", tokenUrl);
        node.put("clientId", clientId);
        String encryptedSecret = PasswordCrypto.isMasterPasswordConfigured()
                ? PasswordCrypto.encryptIfNeeded(clientSecret)
                : clientSecret;
        node.put("clientSecret", encryptedSecret);
        if (scope != null && !scope.isBlank()) node.put("scope", scope);
        if (audience != null && !audience.isBlank()) node.put("audience", audience);
        node.put("clientAuthMethod", clientAuthMethod.getValue());
        return node;
    }

    public static OAuth2ClientConfig fromJson(JsonNode node) {
        return new OAuth2ClientConfig(
                AuthConfig.getText(node, "tokenUrl", ""),
                AuthConfig.getText(node, "clientId", ""),
                AuthConfig.getText(node, "clientSecret", ""),
                AuthConfig.getText(node, "scope", null),
                AuthConfig.getText(node, "audience", null),
                ClientAuthMethod.fromString(AuthConfig.getText(node, "clientAuthMethod", "body"))
        );
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String tokenUrl = "";
        private String clientId = "";
        private String clientSecret = "";
        private String scope = null;
        private String audience = null;
        private ClientAuthMethod clientAuthMethod = ClientAuthMethod.BODY;

        public Builder tokenUrl(String tokenUrl) { this.tokenUrl = tokenUrl; return this; }
        public Builder clientId(String clientId) { this.clientId = clientId; return this; }
        public Builder clientSecret(String clientSecret) { this.clientSecret = clientSecret; return this; }
        public Builder scope(String scope) { this.scope = scope; return this; }
        public Builder audience(String audience) { this.audience = audience; return this; }
        public Builder clientAuthMethod(ClientAuthMethod method) { this.clientAuthMethod = method; return this; }
        public OAuth2ClientConfig build() { return new OAuth2ClientConfig(tokenUrl, clientId, clientSecret, scope, audience, clientAuthMethod); }
    }
}
