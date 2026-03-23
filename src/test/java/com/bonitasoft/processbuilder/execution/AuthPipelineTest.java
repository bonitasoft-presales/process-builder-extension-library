package com.bonitasoft.processbuilder.execution;

import com.bonitasoft.processbuilder.enums.RestAuthenticationType;
import com.bonitasoft.processbuilder.records.RestAuthConfig;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AuthPipelineTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    void should_return_none_when_null() {
        RestAuthConfig config = AuthPipeline.resolve(null);
        assertThat(config.getAuthType()).isEqualTo(RestAuthenticationType.NONE);
    }

    @Test
    void should_normalize_type_to_authType() {
        ObjectNode auth = MAPPER.createObjectNode();
        auth.put("type", "BASIC");
        auth.put("username", "admin");
        auth.put("password", "secret");

        JsonNode normalized = AuthPipeline.normalizeAuthConfig(auth);
        assertThat(normalized.get("authType").asText()).isEqualTo("basic");
        assertThat(normalized.get("username").asText()).isEqualTo("admin");
    }

    @Test
    void should_normalize_apikey_field_names() {
        ObjectNode auth = MAPPER.createObjectNode();
        auth.put("type", "API_KEY");
        auth.put("apiKeyName", "X-Api-Key");
        auth.put("apiKeyValue", "secret123");
        auth.put("apiKeyLocation", "HEADER");

        JsonNode normalized = AuthPipeline.normalizeAuthConfig(auth);
        assertThat(normalized.get("authType").asText()).isEqualTo("api_key");
        assertThat(normalized.get("keyName").asText()).isEqualTo("X-Api-Key");
        assertThat(normalized.get("keyValue").asText()).isEqualTo("secret123");
        assertThat(normalized.get("location").asText()).isEqualTo("header");
    }

    @Test
    void should_normalize_query_param_location() {
        ObjectNode auth = MAPPER.createObjectNode();
        auth.put("type", "API_KEY");
        auth.put("apiKeyName", "key");
        auth.put("apiKeyValue", "val");
        auth.put("apiKeyLocation", "QUERY_PARAM");

        JsonNode normalized = AuthPipeline.normalizeAuthConfig(auth);
        assertThat(normalized.get("location").asText()).isEqualTo("queryParam");
    }

    @Test
    void should_resolve_basic_auth_without_encryption() {
        ObjectNode auth = MAPPER.createObjectNode();
        auth.put("authType", "basic");
        auth.put("username", "user");
        auth.put("password", "pass");

        RestAuthConfig config = AuthPipeline.resolve(auth);
        assertThat(config.getAuthType()).isEqualTo(RestAuthenticationType.BASIC);
        assertThat(config).isInstanceOf(RestAuthConfig.BasicAuth.class);
        assertThat(((RestAuthConfig.BasicAuth) config).username()).isEqualTo("user");
    }

    @Test
    void should_resolve_bearer_auth() {
        ObjectNode auth = MAPPER.createObjectNode();
        auth.put("authType", "bearer");
        auth.put("token", "jwt-token-123");

        RestAuthConfig config = AuthPipeline.resolve(auth);
        assertThat(config.getAuthType()).isEqualTo(RestAuthenticationType.BEARER);
        assertThat(((RestAuthConfig.BearerAuth) config).token()).isEqualTo("jwt-token-123");
    }

    @Test
    void should_resolve_none_auth() {
        ObjectNode auth = MAPPER.createObjectNode();
        auth.put("authType", "none");

        RestAuthConfig config = AuthPipeline.resolve(auth);
        assertThat(config.getAuthType()).isEqualTo(RestAuthenticationType.NONE);
    }

    @Test
    void should_skip_decryption_when_master_password_not_configured() {
        ObjectNode auth = MAPPER.createObjectNode();
        auth.put("authType", "basic");
        auth.put("username", "admin");
        auth.put("password", "plaintext_pass");

        // Without MASTER_BONITA_PWD env var, decryption is skipped
        JsonNode decrypted = AuthPipeline.decryptSensitiveFields(auth);
        assertThat(decrypted.get("password").asText()).isEqualTo("plaintext_pass");
    }

    @Test
    void should_keep_original_auth_type_field() {
        ObjectNode auth = MAPPER.createObjectNode();
        auth.put("authType", "basic");
        auth.put("username", "user");
        auth.put("password", "pass");

        JsonNode normalized = AuthPipeline.normalizeAuthConfig(auth);
        assertThat(normalized.get("authType").asText()).isEqualTo("basic");
    }
}
