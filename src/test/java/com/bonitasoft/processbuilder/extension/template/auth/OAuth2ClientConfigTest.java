package com.bonitasoft.processbuilder.extension.template.auth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for {@link OAuth2ClientConfig}.
 *
 * @author Bonitasoft
 * @since 1.0
 */
@DisplayName("OAuth2ClientConfig Unit Tests")
class OAuth2ClientConfigTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final String TEST_TOKEN_URL = "https://auth.example.com/oauth/token";
    private static final String TEST_CLIENT_ID = "client-123";
    private static final String TEST_CLIENT_SECRET = "secret-456";
    private static final String TEST_SCOPE = "read write";
    private static final String TEST_AUDIENCE = "https://api.example.com";

    // =========================================================================
    // CONSTRUCTOR TESTS
    // =========================================================================

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("should create instance with all parameters")
        void should_create_instance_with_all_parameters() {
            OAuth2ClientConfig config = new OAuth2ClientConfig(
                    TEST_TOKEN_URL, TEST_CLIENT_ID, TEST_CLIENT_SECRET,
                    TEST_SCOPE, TEST_AUDIENCE, OAuth2ClientConfig.ClientAuthMethod.HEADER);

            assertThat(config.tokenUrl()).isEqualTo(TEST_TOKEN_URL);
            assertThat(config.clientId()).isEqualTo(TEST_CLIENT_ID);
            assertThat(config.clientSecret()).isEqualTo(TEST_CLIENT_SECRET);
            assertThat(config.scope()).isEqualTo(TEST_SCOPE);
            assertThat(config.audience()).isEqualTo(TEST_AUDIENCE);
            assertThat(config.clientAuthMethod()).isEqualTo(OAuth2ClientConfig.ClientAuthMethod.HEADER);
        }

        @Test
        @DisplayName("should create instance with three parameters")
        void should_create_instance_with_three_parameters() {
            OAuth2ClientConfig config = new OAuth2ClientConfig(TEST_TOKEN_URL, TEST_CLIENT_ID, TEST_CLIENT_SECRET);

            assertThat(config.scope()).isNull();
            assertThat(config.audience()).isNull();
            assertThat(config.clientAuthMethod()).isEqualTo(OAuth2ClientConfig.ClientAuthMethod.BODY);
        }

        @Test
        @DisplayName("should create instance with four parameters")
        void should_create_instance_with_four_parameters() {
            OAuth2ClientConfig config = new OAuth2ClientConfig(TEST_TOKEN_URL, TEST_CLIENT_ID, TEST_CLIENT_SECRET, TEST_SCOPE);

            assertThat(config.scope()).isEqualTo(TEST_SCOPE);
            assertThat(config.audience()).isNull();
            assertThat(config.clientAuthMethod()).isEqualTo(OAuth2ClientConfig.ClientAuthMethod.BODY);
        }

        @Test
        @DisplayName("should default null clientAuthMethod to BODY")
        void should_default_null_clientAuthMethod_to_body() {
            OAuth2ClientConfig config = new OAuth2ClientConfig(
                    TEST_TOKEN_URL, TEST_CLIENT_ID, TEST_CLIENT_SECRET,
                    TEST_SCOPE, TEST_AUDIENCE, null);

            assertThat(config.clientAuthMethod()).isEqualTo(OAuth2ClientConfig.ClientAuthMethod.BODY);
        }

        @Test
        @DisplayName("should throw NPE for null tokenUrl")
        void should_throw_npe_for_null_tokenUrl() {
            assertThatThrownBy(() -> new OAuth2ClientConfig(null, TEST_CLIENT_ID, TEST_CLIENT_SECRET))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("Token URL");
        }

        @Test
        @DisplayName("should throw NPE for null clientId")
        void should_throw_npe_for_null_clientId() {
            assertThatThrownBy(() -> new OAuth2ClientConfig(TEST_TOKEN_URL, null, TEST_CLIENT_SECRET))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("Client ID");
        }

        @Test
        @DisplayName("should throw NPE for null clientSecret")
        void should_throw_npe_for_null_clientSecret() {
            assertThatThrownBy(() -> new OAuth2ClientConfig(TEST_TOKEN_URL, TEST_CLIENT_ID, null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("Client Secret");
        }
    }

    // =========================================================================
    // ClientAuthMethod ENUM TESTS
    // =========================================================================

    @Nested
    @DisplayName("ClientAuthMethod Enum Tests")
    class ClientAuthMethodTests {

        @Test
        @DisplayName("BODY should have value 'body'")
        void body_should_have_value_body() {
            assertThat(OAuth2ClientConfig.ClientAuthMethod.BODY.getValue()).isEqualTo("body");
        }

        @Test
        @DisplayName("HEADER should have value 'header'")
        void header_should_have_value_header() {
            assertThat(OAuth2ClientConfig.ClientAuthMethod.HEADER.getValue()).isEqualTo("header");
        }

        @Test
        @DisplayName("fromString should return HEADER for 'header'")
        void fromString_should_return_header() {
            assertThat(OAuth2ClientConfig.ClientAuthMethod.fromString("header"))
                    .isEqualTo(OAuth2ClientConfig.ClientAuthMethod.HEADER);
        }

        @Test
        @DisplayName("fromString should return HEADER for 'HEADER' (case insensitive)")
        void fromString_should_return_header_case_insensitive() {
            assertThat(OAuth2ClientConfig.ClientAuthMethod.fromString("HEADER"))
                    .isEqualTo(OAuth2ClientConfig.ClientAuthMethod.HEADER);
            assertThat(OAuth2ClientConfig.ClientAuthMethod.fromString("Header"))
                    .isEqualTo(OAuth2ClientConfig.ClientAuthMethod.HEADER);
        }

        @Test
        @DisplayName("fromString should return BODY for other values")
        void fromString_should_return_body_for_other_values() {
            assertThat(OAuth2ClientConfig.ClientAuthMethod.fromString("body"))
                    .isEqualTo(OAuth2ClientConfig.ClientAuthMethod.BODY);
            assertThat(OAuth2ClientConfig.ClientAuthMethod.fromString(null))
                    .isEqualTo(OAuth2ClientConfig.ClientAuthMethod.BODY);
            assertThat(OAuth2ClientConfig.ClientAuthMethod.fromString("unknown"))
                    .isEqualTo(OAuth2ClientConfig.ClientAuthMethod.BODY);
        }
    }

    // =========================================================================
    // getAuthType() TESTS
    // =========================================================================

    @Test
    @DisplayName("getAuthType should return 'oauth2_client_credentials'")
    void getAuthType_should_return_oauth2_client_credentials() {
        OAuth2ClientConfig config = new OAuth2ClientConfig(TEST_TOKEN_URL, TEST_CLIENT_ID, TEST_CLIENT_SECRET);

        assertThat(config.getAuthType()).isEqualTo("oauth2_client_credentials");
    }

    // =========================================================================
    // toJson() TESTS
    // =========================================================================

    @Nested
    @DisplayName("toJson() Tests")
    class ToJsonTests {

        @Test
        @DisplayName("toJson should include required fields")
        void toJson_should_include_required_fields() {
            OAuth2ClientConfig config = new OAuth2ClientConfig(TEST_TOKEN_URL, TEST_CLIENT_ID, TEST_CLIENT_SECRET);
            JsonNode json = config.toJson(MAPPER);

            assertThat(json.has("authType")).isTrue();
            assertThat(json.has("tokenUrl")).isTrue();
            assertThat(json.has("clientId")).isTrue();
            assertThat(json.has("clientSecret")).isTrue();
            assertThat(json.has("clientAuthMethod")).isTrue();
        }

        @Test
        @DisplayName("toJson should include scope when not blank")
        void toJson_should_include_scope_when_not_blank() {
            OAuth2ClientConfig config = new OAuth2ClientConfig(
                    TEST_TOKEN_URL, TEST_CLIENT_ID, TEST_CLIENT_SECRET, TEST_SCOPE);
            JsonNode json = config.toJson(MAPPER);

            assertThat(json.has("scope")).isTrue();
            assertThat(json.get("scope").asText()).isEqualTo(TEST_SCOPE);
        }

        @Test
        @DisplayName("toJson should exclude scope when null")
        void toJson_should_exclude_scope_when_null() {
            OAuth2ClientConfig config = new OAuth2ClientConfig(TEST_TOKEN_URL, TEST_CLIENT_ID, TEST_CLIENT_SECRET);
            JsonNode json = config.toJson(MAPPER);

            assertThat(json.has("scope")).isFalse();
        }

        @Test
        @DisplayName("toJson should include audience when not blank")
        void toJson_should_include_audience_when_not_blank() {
            OAuth2ClientConfig config = new OAuth2ClientConfig(
                    TEST_TOKEN_URL, TEST_CLIENT_ID, TEST_CLIENT_SECRET,
                    TEST_SCOPE, TEST_AUDIENCE, OAuth2ClientConfig.ClientAuthMethod.BODY);
            JsonNode json = config.toJson(MAPPER);

            assertThat(json.has("audience")).isTrue();
            assertThat(json.get("audience").asText()).isEqualTo(TEST_AUDIENCE);
        }

        @Test
        @DisplayName("toJson should have correct values")
        void toJson_should_have_correct_values() {
            OAuth2ClientConfig config = new OAuth2ClientConfig(
                    TEST_TOKEN_URL, TEST_CLIENT_ID, TEST_CLIENT_SECRET,
                    TEST_SCOPE, TEST_AUDIENCE, OAuth2ClientConfig.ClientAuthMethod.HEADER);
            JsonNode json = config.toJson(MAPPER);

            assertThat(json.get("authType").asText()).isEqualTo("oauth2_client_credentials");
            assertThat(json.get("tokenUrl").asText()).isEqualTo(TEST_TOKEN_URL);
            assertThat(json.get("clientId").asText()).isEqualTo(TEST_CLIENT_ID);
            assertThat(json.get("clientSecret").asText()).isEqualTo(TEST_CLIENT_SECRET);
            assertThat(json.get("clientAuthMethod").asText()).isEqualTo("header");
        }

        @Test
        @DisplayName("toJson should exclude scope when blank")
        void toJson_should_exclude_scope_when_blank() {
            OAuth2ClientConfig config = new OAuth2ClientConfig(
                    TEST_TOKEN_URL, TEST_CLIENT_ID, TEST_CLIENT_SECRET,
                    "   ", null, OAuth2ClientConfig.ClientAuthMethod.BODY);
            JsonNode json = config.toJson(MAPPER);

            assertThat(json.has("scope")).isFalse();
        }

        @Test
        @DisplayName("toJson should exclude audience when null")
        void toJson_should_exclude_audience_when_null() {
            OAuth2ClientConfig config = new OAuth2ClientConfig(
                    TEST_TOKEN_URL, TEST_CLIENT_ID, TEST_CLIENT_SECRET);
            JsonNode json = config.toJson(MAPPER);

            assertThat(json.has("audience")).isFalse();
        }

        @Test
        @DisplayName("toJson should exclude audience when blank")
        void toJson_should_exclude_audience_when_blank() {
            OAuth2ClientConfig config = new OAuth2ClientConfig(
                    TEST_TOKEN_URL, TEST_CLIENT_ID, TEST_CLIENT_SECRET,
                    TEST_SCOPE, "   ", OAuth2ClientConfig.ClientAuthMethod.BODY);
            JsonNode json = config.toJson(MAPPER);

            assertThat(json.has("audience")).isFalse();
        }
    }

    // =========================================================================
    // toJsonEncrypted() TESTS
    // =========================================================================

    @Nested
    @DisplayName("toJsonEncrypted() Tests")
    class ToJsonEncryptedTests {

        @Test
        @DisplayName("toJsonEncrypted should include all required fields")
        void toJsonEncrypted_should_include_all_required_fields() {
            OAuth2ClientConfig config = new OAuth2ClientConfig(TEST_TOKEN_URL, TEST_CLIENT_ID, TEST_CLIENT_SECRET);
            JsonNode json = config.toJsonEncrypted(MAPPER);

            assertThat(json.has("authType")).isTrue();
            assertThat(json.has("tokenUrl")).isTrue();
            assertThat(json.has("clientId")).isTrue();
            assertThat(json.has("clientSecret")).isTrue();
        }

        @Test
        @DisplayName("toJsonEncrypted should include scope when not blank")
        void toJsonEncrypted_should_include_scope_when_not_blank() {
            OAuth2ClientConfig config = new OAuth2ClientConfig(
                    TEST_TOKEN_URL, TEST_CLIENT_ID, TEST_CLIENT_SECRET, TEST_SCOPE);
            JsonNode json = config.toJsonEncrypted(MAPPER);

            assertThat(json.has("scope")).isTrue();
        }

        @Test
        @DisplayName("toJsonEncrypted should exclude scope when null")
        void toJsonEncrypted_should_exclude_scope_when_null() {
            OAuth2ClientConfig config = new OAuth2ClientConfig(
                    TEST_TOKEN_URL, TEST_CLIENT_ID, TEST_CLIENT_SECRET);
            JsonNode json = config.toJsonEncrypted(MAPPER);

            assertThat(json.has("scope")).isFalse();
        }

        @Test
        @DisplayName("toJsonEncrypted should exclude scope when blank")
        void toJsonEncrypted_should_exclude_scope_when_blank() {
            OAuth2ClientConfig config = new OAuth2ClientConfig(
                    TEST_TOKEN_URL, TEST_CLIENT_ID, TEST_CLIENT_SECRET,
                    "   ", null, OAuth2ClientConfig.ClientAuthMethod.BODY);
            JsonNode json = config.toJsonEncrypted(MAPPER);

            assertThat(json.has("scope")).isFalse();
        }

        @Test
        @DisplayName("toJsonEncrypted should include audience when not blank")
        void toJsonEncrypted_should_include_audience_when_not_blank() {
            OAuth2ClientConfig config = new OAuth2ClientConfig(
                    TEST_TOKEN_URL, TEST_CLIENT_ID, TEST_CLIENT_SECRET,
                    TEST_SCOPE, TEST_AUDIENCE, OAuth2ClientConfig.ClientAuthMethod.BODY);
            JsonNode json = config.toJsonEncrypted(MAPPER);

            assertThat(json.has("audience")).isTrue();
        }

        @Test
        @DisplayName("toJsonEncrypted should exclude audience when null")
        void toJsonEncrypted_should_exclude_audience_when_null() {
            OAuth2ClientConfig config = new OAuth2ClientConfig(
                    TEST_TOKEN_URL, TEST_CLIENT_ID, TEST_CLIENT_SECRET);
            JsonNode json = config.toJsonEncrypted(MAPPER);

            assertThat(json.has("audience")).isFalse();
        }

        @Test
        @DisplayName("toJsonEncrypted should exclude audience when blank")
        void toJsonEncrypted_should_exclude_audience_when_blank() {
            OAuth2ClientConfig config = new OAuth2ClientConfig(
                    TEST_TOKEN_URL, TEST_CLIENT_ID, TEST_CLIENT_SECRET,
                    TEST_SCOPE, "   ", OAuth2ClientConfig.ClientAuthMethod.BODY);
            JsonNode json = config.toJsonEncrypted(MAPPER);

            assertThat(json.has("audience")).isFalse();
        }
    }

    // =========================================================================
    // fromJson() TESTS
    // =========================================================================

    @Nested
    @DisplayName("fromJson() Tests")
    class FromJsonTests {

        @Test
        @DisplayName("fromJson should parse valid JSON")
        void fromJson_should_parse_valid_json() {
            ObjectNode json = MAPPER.createObjectNode();
            json.put("authType", "oauth2_client_credentials");
            json.put("tokenUrl", TEST_TOKEN_URL);
            json.put("clientId", TEST_CLIENT_ID);
            json.put("clientSecret", TEST_CLIENT_SECRET);
            json.put("scope", TEST_SCOPE);
            json.put("audience", TEST_AUDIENCE);
            json.put("clientAuthMethod", "header");

            OAuth2ClientConfig config = OAuth2ClientConfig.fromJson(json);

            assertThat(config.tokenUrl()).isEqualTo(TEST_TOKEN_URL);
            assertThat(config.clientId()).isEqualTo(TEST_CLIENT_ID);
            assertThat(config.clientSecret()).isEqualTo(TEST_CLIENT_SECRET);
            assertThat(config.scope()).isEqualTo(TEST_SCOPE);
            assertThat(config.audience()).isEqualTo(TEST_AUDIENCE);
            assertThat(config.clientAuthMethod()).isEqualTo(OAuth2ClientConfig.ClientAuthMethod.HEADER);
        }

        @Test
        @DisplayName("fromJson should use defaults for missing fields")
        void fromJson_should_use_defaults_for_missing_fields() {
            ObjectNode json = MAPPER.createObjectNode();
            json.put("authType", "oauth2_client_credentials");

            OAuth2ClientConfig config = OAuth2ClientConfig.fromJson(json);

            assertThat(config.tokenUrl()).isEmpty();
            assertThat(config.clientId()).isEmpty();
            assertThat(config.clientSecret()).isEmpty();
            assertThat(config.scope()).isNull();
            assertThat(config.clientAuthMethod()).isEqualTo(OAuth2ClientConfig.ClientAuthMethod.BODY);
        }
    }

    // =========================================================================
    // BUILDER TESTS
    // =========================================================================

    @Nested
    @DisplayName("Builder Tests")
    class BuilderTests {

        @Test
        @DisplayName("builder should create instance with all fields")
        void builder_should_create_instance_with_all_fields() {
            OAuth2ClientConfig config = OAuth2ClientConfig.builder()
                    .tokenUrl(TEST_TOKEN_URL)
                    .clientId(TEST_CLIENT_ID)
                    .clientSecret(TEST_CLIENT_SECRET)
                    .scope(TEST_SCOPE)
                    .audience(TEST_AUDIENCE)
                    .clientAuthMethod(OAuth2ClientConfig.ClientAuthMethod.HEADER)
                    .build();

            assertThat(config.tokenUrl()).isEqualTo(TEST_TOKEN_URL);
            assertThat(config.clientId()).isEqualTo(TEST_CLIENT_ID);
            assertThat(config.clientSecret()).isEqualTo(TEST_CLIENT_SECRET);
            assertThat(config.scope()).isEqualTo(TEST_SCOPE);
            assertThat(config.audience()).isEqualTo(TEST_AUDIENCE);
            assertThat(config.clientAuthMethod()).isEqualTo(OAuth2ClientConfig.ClientAuthMethod.HEADER);
        }

        @Test
        @DisplayName("builder should use defaults")
        void builder_should_use_defaults() {
            OAuth2ClientConfig config = OAuth2ClientConfig.builder().build();

            assertThat(config.tokenUrl()).isEmpty();
            assertThat(config.clientId()).isEmpty();
            assertThat(config.clientSecret()).isEmpty();
            assertThat(config.scope()).isNull();
            assertThat(config.audience()).isNull();
            assertThat(config.clientAuthMethod()).isEqualTo(OAuth2ClientConfig.ClientAuthMethod.BODY);
        }

        @Test
        @DisplayName("builder methods should return builder for chaining")
        void builder_methods_should_return_builder_for_chaining() {
            OAuth2ClientConfig.Builder builder = OAuth2ClientConfig.builder();

            assertThat(builder.tokenUrl(TEST_TOKEN_URL)).isSameAs(builder);
            assertThat(builder.clientId(TEST_CLIENT_ID)).isSameAs(builder);
            assertThat(builder.clientSecret(TEST_CLIENT_SECRET)).isSameAs(builder);
            assertThat(builder.scope(TEST_SCOPE)).isSameAs(builder);
            assertThat(builder.audience(TEST_AUDIENCE)).isSameAs(builder);
            assertThat(builder.clientAuthMethod(OAuth2ClientConfig.ClientAuthMethod.BODY)).isSameAs(builder);
        }
    }

    // =========================================================================
    // RECORD EQUALITY TESTS
    // =========================================================================

    @Test
    @DisplayName("equals should return true for identical values")
    void equals_should_return_true_for_identical_values() {
        OAuth2ClientConfig config1 = new OAuth2ClientConfig(TEST_TOKEN_URL, TEST_CLIENT_ID, TEST_CLIENT_SECRET);
        OAuth2ClientConfig config2 = new OAuth2ClientConfig(TEST_TOKEN_URL, TEST_CLIENT_ID, TEST_CLIENT_SECRET);

        assertThat(config1).isEqualTo(config2);
    }

    @Test
    @DisplayName("hashCode should be consistent with equals")
    void hashCode_should_be_consistent_with_equals() {
        OAuth2ClientConfig config1 = new OAuth2ClientConfig(TEST_TOKEN_URL, TEST_CLIENT_ID, TEST_CLIENT_SECRET);
        OAuth2ClientConfig config2 = new OAuth2ClientConfig(TEST_TOKEN_URL, TEST_CLIENT_ID, TEST_CLIENT_SECRET);

        assertThat(config1.hashCode()).isEqualTo(config2.hashCode());
    }

    // =========================================================================
    // AuthConfig INTERFACE COMPLIANCE
    // =========================================================================

    @Test
    @DisplayName("should implement AuthConfig interface")
    void should_implement_authConfig_interface() {
        OAuth2ClientConfig config = new OAuth2ClientConfig(TEST_TOKEN_URL, TEST_CLIENT_ID, TEST_CLIENT_SECRET);

        assertThat(config).isInstanceOf(AuthConfig.class);
    }
}
