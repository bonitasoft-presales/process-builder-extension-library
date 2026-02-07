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
 * Unit tests for {@link OAuth2PasswordConfig}.
 *
 * @author Bonitasoft
 * @since 1.0
 */
@DisplayName("OAuth2PasswordConfig Unit Tests")
class OAuth2PasswordConfigTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final String TEST_TOKEN_URL = "https://auth.example.com/oauth/token";
    private static final String TEST_CLIENT_ID = "client-123";
    private static final String TEST_CLIENT_SECRET = "secret-456";
    private static final String TEST_USERNAME = "user@example.com";
    private static final String TEST_PASSWORD = "password123";
    private static final String TEST_SCOPE = "openid profile";

    // =========================================================================
    // CONSTRUCTOR TESTS
    // =========================================================================

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("should create instance with all parameters")
        void should_create_instance_with_all_parameters() {
            OAuth2PasswordConfig config = new OAuth2PasswordConfig(
                    TEST_TOKEN_URL, TEST_CLIENT_ID, TEST_CLIENT_SECRET,
                    TEST_USERNAME, TEST_PASSWORD, TEST_SCOPE);

            assertThat(config.tokenUrl()).isEqualTo(TEST_TOKEN_URL);
            assertThat(config.clientId()).isEqualTo(TEST_CLIENT_ID);
            assertThat(config.clientSecret()).isEqualTo(TEST_CLIENT_SECRET);
            assertThat(config.username()).isEqualTo(TEST_USERNAME);
            assertThat(config.password()).isEqualTo(TEST_PASSWORD);
            assertThat(config.scope()).isEqualTo(TEST_SCOPE);
        }

        @Test
        @DisplayName("should create instance with four parameters")
        void should_create_instance_with_four_parameters() {
            OAuth2PasswordConfig config = new OAuth2PasswordConfig(
                    TEST_TOKEN_URL, TEST_CLIENT_ID, TEST_USERNAME, TEST_PASSWORD);

            assertThat(config.tokenUrl()).isEqualTo(TEST_TOKEN_URL);
            assertThat(config.clientId()).isEqualTo(TEST_CLIENT_ID);
            assertThat(config.clientSecret()).isNull();
            assertThat(config.username()).isEqualTo(TEST_USERNAME);
            assertThat(config.password()).isEqualTo(TEST_PASSWORD);
            assertThat(config.scope()).isNull();
        }

        @Test
        @DisplayName("should throw NPE for null tokenUrl")
        void should_throw_npe_for_null_tokenUrl() {
            assertThatThrownBy(() -> new OAuth2PasswordConfig(
                    null, TEST_CLIENT_ID, TEST_USERNAME, TEST_PASSWORD))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("Token URL");
        }

        @Test
        @DisplayName("should throw NPE for null clientId")
        void should_throw_npe_for_null_clientId() {
            assertThatThrownBy(() -> new OAuth2PasswordConfig(
                    TEST_TOKEN_URL, null, TEST_USERNAME, TEST_PASSWORD))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("Client ID");
        }

        @Test
        @DisplayName("should throw NPE for null username")
        void should_throw_npe_for_null_username() {
            assertThatThrownBy(() -> new OAuth2PasswordConfig(
                    TEST_TOKEN_URL, TEST_CLIENT_ID, null, TEST_PASSWORD))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("Username");
        }

        @Test
        @DisplayName("should throw NPE for null password")
        void should_throw_npe_for_null_password() {
            assertThatThrownBy(() -> new OAuth2PasswordConfig(
                    TEST_TOKEN_URL, TEST_CLIENT_ID, TEST_USERNAME, null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("Password");
        }

        @Test
        @DisplayName("should allow null clientSecret")
        void should_allow_null_clientSecret() {
            OAuth2PasswordConfig config = new OAuth2PasswordConfig(
                    TEST_TOKEN_URL, TEST_CLIENT_ID, null,
                    TEST_USERNAME, TEST_PASSWORD, TEST_SCOPE);

            assertThat(config.clientSecret()).isNull();
        }

        @Test
        @DisplayName("should allow null scope")
        void should_allow_null_scope() {
            OAuth2PasswordConfig config = new OAuth2PasswordConfig(
                    TEST_TOKEN_URL, TEST_CLIENT_ID, TEST_CLIENT_SECRET,
                    TEST_USERNAME, TEST_PASSWORD, null);

            assertThat(config.scope()).isNull();
        }
    }

    // =========================================================================
    // getAuthType() TESTS
    // =========================================================================

    @Test
    @DisplayName("getAuthType should return 'oauth2_password'")
    void getAuthType_should_return_oauth2_password() {
        OAuth2PasswordConfig config = new OAuth2PasswordConfig(
                TEST_TOKEN_URL, TEST_CLIENT_ID, TEST_USERNAME, TEST_PASSWORD);

        assertThat(config.getAuthType()).isEqualTo("oauth2_password");
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
            OAuth2PasswordConfig config = new OAuth2PasswordConfig(
                    TEST_TOKEN_URL, TEST_CLIENT_ID, TEST_USERNAME, TEST_PASSWORD);
            JsonNode json = config.toJson(MAPPER);

            assertThat(json.has("authType")).isTrue();
            assertThat(json.has("tokenUrl")).isTrue();
            assertThat(json.has("clientId")).isTrue();
            assertThat(json.has("username")).isTrue();
            assertThat(json.has("password")).isTrue();
        }

        @Test
        @DisplayName("toJson should include clientSecret when not blank")
        void toJson_should_include_clientSecret_when_not_blank() {
            OAuth2PasswordConfig config = new OAuth2PasswordConfig(
                    TEST_TOKEN_URL, TEST_CLIENT_ID, TEST_CLIENT_SECRET,
                    TEST_USERNAME, TEST_PASSWORD, null);
            JsonNode json = config.toJson(MAPPER);

            assertThat(json.has("clientSecret")).isTrue();
            assertThat(json.get("clientSecret").asText()).isEqualTo(TEST_CLIENT_SECRET);
        }

        @Test
        @DisplayName("toJson should exclude clientSecret when null")
        void toJson_should_exclude_clientSecret_when_null() {
            OAuth2PasswordConfig config = new OAuth2PasswordConfig(
                    TEST_TOKEN_URL, TEST_CLIENT_ID, TEST_USERNAME, TEST_PASSWORD);
            JsonNode json = config.toJson(MAPPER);

            assertThat(json.has("clientSecret")).isFalse();
        }

        @Test
        @DisplayName("toJson should include scope when not blank")
        void toJson_should_include_scope_when_not_blank() {
            OAuth2PasswordConfig config = new OAuth2PasswordConfig(
                    TEST_TOKEN_URL, TEST_CLIENT_ID, TEST_CLIENT_SECRET,
                    TEST_USERNAME, TEST_PASSWORD, TEST_SCOPE);
            JsonNode json = config.toJson(MAPPER);

            assertThat(json.has("scope")).isTrue();
            assertThat(json.get("scope").asText()).isEqualTo(TEST_SCOPE);
        }

        @Test
        @DisplayName("toJson should exclude scope when null")
        void toJson_should_exclude_scope_when_null() {
            OAuth2PasswordConfig config = new OAuth2PasswordConfig(
                    TEST_TOKEN_URL, TEST_CLIENT_ID, TEST_USERNAME, TEST_PASSWORD);
            JsonNode json = config.toJson(MAPPER);

            assertThat(json.has("scope")).isFalse();
        }

        @Test
        @DisplayName("toJson should have correct values")
        void toJson_should_have_correct_values() {
            OAuth2PasswordConfig config = new OAuth2PasswordConfig(
                    TEST_TOKEN_URL, TEST_CLIENT_ID, TEST_CLIENT_SECRET,
                    TEST_USERNAME, TEST_PASSWORD, TEST_SCOPE);
            JsonNode json = config.toJson(MAPPER);

            assertThat(json.get("authType").asText()).isEqualTo("oauth2_password");
            assertThat(json.get("tokenUrl").asText()).isEqualTo(TEST_TOKEN_URL);
            assertThat(json.get("clientId").asText()).isEqualTo(TEST_CLIENT_ID);
            assertThat(json.get("clientSecret").asText()).isEqualTo(TEST_CLIENT_SECRET);
            assertThat(json.get("username").asText()).isEqualTo(TEST_USERNAME);
            assertThat(json.get("password").asText()).isEqualTo(TEST_PASSWORD);
            assertThat(json.get("scope").asText()).isEqualTo(TEST_SCOPE);
        }
    }

    // =========================================================================
    // toJsonEncrypted() TESTS
    // =========================================================================

    @Test
    @DisplayName("toJsonEncrypted should include all required fields")
    void toJsonEncrypted_should_include_all_required_fields() {
        OAuth2PasswordConfig config = new OAuth2PasswordConfig(
                TEST_TOKEN_URL, TEST_CLIENT_ID, TEST_USERNAME, TEST_PASSWORD);
        JsonNode json = config.toJsonEncrypted(MAPPER);

        assertThat(json.has("authType")).isTrue();
        assertThat(json.has("tokenUrl")).isTrue();
        assertThat(json.has("clientId")).isTrue();
        assertThat(json.has("username")).isTrue();
        assertThat(json.has("password")).isTrue();
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
            json.put("authType", "oauth2_password");
            json.put("tokenUrl", TEST_TOKEN_URL);
            json.put("clientId", TEST_CLIENT_ID);
            json.put("clientSecret", TEST_CLIENT_SECRET);
            json.put("username", TEST_USERNAME);
            json.put("password", TEST_PASSWORD);
            json.put("scope", TEST_SCOPE);

            OAuth2PasswordConfig config = OAuth2PasswordConfig.fromJson(json);

            assertThat(config.tokenUrl()).isEqualTo(TEST_TOKEN_URL);
            assertThat(config.clientId()).isEqualTo(TEST_CLIENT_ID);
            assertThat(config.clientSecret()).isEqualTo(TEST_CLIENT_SECRET);
            assertThat(config.username()).isEqualTo(TEST_USERNAME);
            assertThat(config.password()).isEqualTo(TEST_PASSWORD);
            assertThat(config.scope()).isEqualTo(TEST_SCOPE);
        }

        @Test
        @DisplayName("fromJson should use defaults for missing fields")
        void fromJson_should_use_defaults_for_missing_fields() {
            ObjectNode json = MAPPER.createObjectNode();
            json.put("authType", "oauth2_password");

            OAuth2PasswordConfig config = OAuth2PasswordConfig.fromJson(json);

            assertThat(config.tokenUrl()).isEmpty();
            assertThat(config.clientId()).isEmpty();
            assertThat(config.clientSecret()).isNull();
            assertThat(config.username()).isEmpty();
            assertThat(config.password()).isEmpty();
            assertThat(config.scope()).isNull();
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
            OAuth2PasswordConfig config = OAuth2PasswordConfig.builder()
                    .tokenUrl(TEST_TOKEN_URL)
                    .clientId(TEST_CLIENT_ID)
                    .clientSecret(TEST_CLIENT_SECRET)
                    .username(TEST_USERNAME)
                    .password(TEST_PASSWORD)
                    .scope(TEST_SCOPE)
                    .build();

            assertThat(config.tokenUrl()).isEqualTo(TEST_TOKEN_URL);
            assertThat(config.clientId()).isEqualTo(TEST_CLIENT_ID);
            assertThat(config.clientSecret()).isEqualTo(TEST_CLIENT_SECRET);
            assertThat(config.username()).isEqualTo(TEST_USERNAME);
            assertThat(config.password()).isEqualTo(TEST_PASSWORD);
            assertThat(config.scope()).isEqualTo(TEST_SCOPE);
        }

        @Test
        @DisplayName("builder should use defaults")
        void builder_should_use_defaults() {
            OAuth2PasswordConfig config = OAuth2PasswordConfig.builder().build();

            assertThat(config.tokenUrl()).isEmpty();
            assertThat(config.clientId()).isEmpty();
            assertThat(config.clientSecret()).isNull();
            assertThat(config.username()).isEmpty();
            assertThat(config.password()).isEmpty();
            assertThat(config.scope()).isNull();
        }

        @Test
        @DisplayName("builder methods should return builder for chaining")
        void builder_methods_should_return_builder_for_chaining() {
            OAuth2PasswordConfig.Builder builder = OAuth2PasswordConfig.builder();

            assertThat(builder.tokenUrl(TEST_TOKEN_URL)).isSameAs(builder);
            assertThat(builder.clientId(TEST_CLIENT_ID)).isSameAs(builder);
            assertThat(builder.clientSecret(TEST_CLIENT_SECRET)).isSameAs(builder);
            assertThat(builder.username(TEST_USERNAME)).isSameAs(builder);
            assertThat(builder.password(TEST_PASSWORD)).isSameAs(builder);
            assertThat(builder.scope(TEST_SCOPE)).isSameAs(builder);
        }
    }

    // =========================================================================
    // RECORD EQUALITY TESTS
    // =========================================================================

    @Test
    @DisplayName("equals should return true for identical values")
    void equals_should_return_true_for_identical_values() {
        OAuth2PasswordConfig config1 = new OAuth2PasswordConfig(
                TEST_TOKEN_URL, TEST_CLIENT_ID, TEST_USERNAME, TEST_PASSWORD);
        OAuth2PasswordConfig config2 = new OAuth2PasswordConfig(
                TEST_TOKEN_URL, TEST_CLIENT_ID, TEST_USERNAME, TEST_PASSWORD);

        assertThat(config1).isEqualTo(config2);
    }

    @Test
    @DisplayName("hashCode should be consistent with equals")
    void hashCode_should_be_consistent_with_equals() {
        OAuth2PasswordConfig config1 = new OAuth2PasswordConfig(
                TEST_TOKEN_URL, TEST_CLIENT_ID, TEST_USERNAME, TEST_PASSWORD);
        OAuth2PasswordConfig config2 = new OAuth2PasswordConfig(
                TEST_TOKEN_URL, TEST_CLIENT_ID, TEST_USERNAME, TEST_PASSWORD);

        assertThat(config1.hashCode()).isEqualTo(config2.hashCode());
    }

    // =========================================================================
    // AuthConfig INTERFACE COMPLIANCE
    // =========================================================================

    @Test
    @DisplayName("should implement AuthConfig interface")
    void should_implement_authConfig_interface() {
        OAuth2PasswordConfig config = new OAuth2PasswordConfig(
                TEST_TOKEN_URL, TEST_CLIENT_ID, TEST_USERNAME, TEST_PASSWORD);

        assertThat(config).isInstanceOf(AuthConfig.class);
    }
}
