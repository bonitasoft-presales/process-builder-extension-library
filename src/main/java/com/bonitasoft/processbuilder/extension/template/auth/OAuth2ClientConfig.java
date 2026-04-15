package com.bonitasoft.processbuilder.extension.template.auth;

import com.bonitasoft.processbuilder.extension.PasswordCrypto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Objects;

/**
 * Configuration for OAuth2 Client Credentials flow.
 *
 * @param tokenUrl          URL of the OAuth2 token endpoint
 * @param clientId          OAuth2 client identifier
 * @param clientSecret      OAuth2 client secret
 * @param scope             Optional OAuth2 scope
 * @param audience          Optional audience parameter (e.g. Auth0)
 * @param clientAuthMethod  How to transmit client credentials (body or Authorization header)
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

    /**
     * Defines how OAuth2 client credentials are transmitted to the authorization server.
     */
    public enum ClientAuthMethod {
        /** Client credentials sent in the request body (application/x-www-form-urlencoded). */
        BODY("body"),
        /** Client credentials sent in the Authorization: Basic header. */
        HEADER("header");

        private final String value;

        ClientAuthMethod(String value) {
            this.value = value;
        }

        /**
         * Returns the serialized string value of this method.
         *
         * @return the lowercase string representation (e.g. "body" or "header")
         */
        public String getValue() {
            return value;
        }

        /**
         * Parses a string value into a {@link ClientAuthMethod}.
         *
         * @param value the string value to parse (case-insensitive)
         * @return {@link #HEADER} when value equals "header" (any case); {@link #BODY} otherwise
         */
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

    /**
     * Convenience constructor with only the required fields.
     * Defaults scope/audience to null and client auth method to {@link ClientAuthMethod#BODY}.
     *
     * @param tokenUrl     OAuth2 token endpoint URL
     * @param clientId     OAuth2 client identifier
     * @param clientSecret OAuth2 client secret
     */
    public OAuth2ClientConfig(String tokenUrl, String clientId, String clientSecret) {
        this(tokenUrl, clientId, clientSecret, null, null, ClientAuthMethod.BODY);
    }

    /**
     * Convenience constructor including the scope.
     * Defaults audience to null and client auth method to {@link ClientAuthMethod#BODY}.
     *
     * @param tokenUrl     OAuth2 token endpoint URL
     * @param clientId     OAuth2 client identifier
     * @param clientSecret OAuth2 client secret
     * @param scope        OAuth2 scope
     */
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

    /**
     * Parses a JSON node into an {@link OAuth2ClientConfig}.
     * Missing string fields default to an empty string or {@code null} as appropriate.
     *
     * @param node the JSON node to parse
     * @return a new {@link OAuth2ClientConfig} populated from the JSON
     */
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

    /**
     * Creates a new builder instance.
     *
     * @return a new {@link Builder}
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder for constructing {@link OAuth2ClientConfig} instances.
     */
    public static class Builder {
        private String tokenUrl = "";
        private String clientId = "";
        private String clientSecret = "";
        private String scope = null;
        private String audience = null;
        private ClientAuthMethod clientAuthMethod = ClientAuthMethod.BODY;

        /**
         * Sets the OAuth2 token endpoint URL.
         * @param tokenUrl token endpoint URL
         * @return this builder
         */
        public Builder tokenUrl(String tokenUrl) { this.tokenUrl = tokenUrl; return this; }

        /**
         * Sets the OAuth2 client identifier.
         * @param clientId client identifier
         * @return this builder
         */
        public Builder clientId(String clientId) { this.clientId = clientId; return this; }

        /**
         * Sets the OAuth2 client secret.
         * @param clientSecret client secret
         * @return this builder
         */
        public Builder clientSecret(String clientSecret) { this.clientSecret = clientSecret; return this; }

        /**
         * Sets the OAuth2 scope.
         * @param scope optional scope string
         * @return this builder
         */
        public Builder scope(String scope) { this.scope = scope; return this; }

        /**
         * Sets the optional audience parameter.
         * @param audience optional audience
         * @return this builder
         */
        public Builder audience(String audience) { this.audience = audience; return this; }

        /**
         * Sets how client credentials are transmitted.
         * @param method authentication method
         * @return this builder
         */
        public Builder clientAuthMethod(ClientAuthMethod method) { this.clientAuthMethod = method; return this; }

        /**
         * Builds the {@link OAuth2ClientConfig}.
         * @return the constructed config
         */
        public OAuth2ClientConfig build() { return new OAuth2ClientConfig(tokenUrl, clientId, clientSecret, scope, audience, clientAuthMethod); }
    }
}
