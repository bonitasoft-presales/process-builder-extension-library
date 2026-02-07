package com.bonitasoft.processbuilder.extension.template.auth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link AuthConfig} sealed interface and its static methods.
 *
 * @author Bonitasoft
 * @since 1.0
 */
@DisplayName("AuthConfig Unit Tests")
class AuthConfigTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    // =========================================================================
    // fromJson() TESTS
    // =========================================================================

    @Nested
    @DisplayName("fromJson() Tests")
    class FromJsonTests {

        @Test
        @DisplayName("fromJson should return NoAuthConfig for null node")
        void fromJson_should_return_noAuth_for_null_node() {
            AuthConfig config = AuthConfig.fromJson(null, MAPPER);

            assertThat(config).isInstanceOf(NoAuthConfig.class);
        }

        @Test
        @DisplayName("fromJson should return NoAuthConfig for null value node")
        void fromJson_should_return_noAuth_for_null_value_node() {
            JsonNode nullNode = MAPPER.nullNode();

            AuthConfig config = AuthConfig.fromJson(nullNode, MAPPER);

            assertThat(config).isInstanceOf(NoAuthConfig.class);
        }

        @Test
        @DisplayName("fromJson should return NoAuthConfig for non-object node")
        void fromJson_should_return_noAuth_for_non_object_node() {
            JsonNode arrayNode = MAPPER.createArrayNode();

            AuthConfig config = AuthConfig.fromJson(arrayNode, MAPPER);

            assertThat(config).isInstanceOf(NoAuthConfig.class);
        }

        @Test
        @DisplayName("fromJson should return NoAuthConfig for missing authType")
        void fromJson_should_return_noAuth_for_missing_authType() {
            ObjectNode json = MAPPER.createObjectNode();
            json.put("username", "test");

            AuthConfig config = AuthConfig.fromJson(json, MAPPER);

            assertThat(config).isInstanceOf(NoAuthConfig.class);
        }

        @Test
        @DisplayName("fromJson should return NoAuthConfig for blank authType")
        void fromJson_should_return_noAuth_for_blank_authType() {
            ObjectNode json = MAPPER.createObjectNode();
            json.put("authType", "   ");

            AuthConfig config = AuthConfig.fromJson(json, MAPPER);

            assertThat(config).isInstanceOf(NoAuthConfig.class);
        }

        @Test
        @DisplayName("fromJson should parse basic auth")
        void fromJson_should_parse_basic_auth() {
            ObjectNode json = MAPPER.createObjectNode();
            json.put("authType", "basic");
            json.put("username", "user");
            json.put("password", "pass");

            AuthConfig config = AuthConfig.fromJson(json, MAPPER);

            assertThat(config).isInstanceOf(BasicAuthConfig.class);
            assertThat(((BasicAuthConfig) config).username()).isEqualTo("user");
        }

        @Test
        @DisplayName("fromJson should parse bearer auth")
        void fromJson_should_parse_bearer_auth() {
            ObjectNode json = MAPPER.createObjectNode();
            json.put("authType", "bearer");
            json.put("token", "my-token");

            AuthConfig config = AuthConfig.fromJson(json, MAPPER);

            assertThat(config).isInstanceOf(BearerAuthConfig.class);
            assertThat(((BearerAuthConfig) config).token()).isEqualTo("my-token");
        }

        @ParameterizedTest
        @ValueSource(strings = {"api_key", "apikey", "API_KEY", "ApiKey"})
        @DisplayName("fromJson should parse api_key auth (case insensitive)")
        void fromJson_should_parse_api_key_auth(String authType) {
            ObjectNode json = MAPPER.createObjectNode();
            json.put("authType", authType);
            json.put("keyName", "X-API-Key");
            json.put("keyValue", "abc123");

            AuthConfig config = AuthConfig.fromJson(json, MAPPER);

            assertThat(config).isInstanceOf(ApiKeyAuthConfig.class);
        }

        @ParameterizedTest
        @ValueSource(strings = {"oauth2_client_credentials", "oauth2clientcredentials", "OAUTH2_CLIENT_CREDENTIALS"})
        @DisplayName("fromJson should parse oauth2_client_credentials auth (case insensitive)")
        void fromJson_should_parse_oauth2_client_credentials_auth(String authType) {
            ObjectNode json = MAPPER.createObjectNode();
            json.put("authType", authType);
            json.put("tokenUrl", "https://auth.example.com/token");
            json.put("clientId", "client");
            json.put("clientSecret", "secret");

            AuthConfig config = AuthConfig.fromJson(json, MAPPER);

            assertThat(config).isInstanceOf(OAuth2ClientConfig.class);
        }

        @ParameterizedTest
        @ValueSource(strings = {"oauth2_password", "oauth2password", "OAUTH2_PASSWORD"})
        @DisplayName("fromJson should parse oauth2_password auth (case insensitive)")
        void fromJson_should_parse_oauth2_password_auth(String authType) {
            ObjectNode json = MAPPER.createObjectNode();
            json.put("authType", authType);
            json.put("tokenUrl", "https://auth.example.com/token");
            json.put("clientId", "client");
            json.put("username", "user");
            json.put("password", "pass");

            AuthConfig config = AuthConfig.fromJson(json, MAPPER);

            assertThat(config).isInstanceOf(OAuth2PasswordConfig.class);
        }

        @Test
        @DisplayName("fromJson should return NoAuthConfig for unknown authType")
        void fromJson_should_return_noAuth_for_unknown_authType() {
            ObjectNode json = MAPPER.createObjectNode();
            json.put("authType", "unknown_type");

            AuthConfig config = AuthConfig.fromJson(json, MAPPER);

            assertThat(config).isInstanceOf(NoAuthConfig.class);
        }

        @Test
        @DisplayName("fromJson should support 'type' field as alias for 'authType'")
        void fromJson_should_support_type_field_alias() {
            ObjectNode json = MAPPER.createObjectNode();
            json.put("type", "basic");
            json.put("username", "user");
            json.put("password", "pass");

            AuthConfig config = AuthConfig.fromJson(json, MAPPER);

            assertThat(config).isInstanceOf(BasicAuthConfig.class);
        }
    }

    // =========================================================================
    // getText() TESTS
    // =========================================================================

    @Nested
    @DisplayName("getText() Helper Tests")
    class GetTextTests {

        @Test
        @DisplayName("getText should return value for existing field")
        void getText_should_return_value_for_existing_field() {
            ObjectNode json = MAPPER.createObjectNode();
            json.put("field", "value");

            String result = AuthConfig.getText(json, "field", "default");

            assertThat(result).isEqualTo("value");
        }

        @Test
        @DisplayName("getText should return default for null node")
        void getText_should_return_default_for_null_node() {
            String result = AuthConfig.getText(null, "field", "default");

            assertThat(result).isEqualTo("default");
        }

        @Test
        @DisplayName("getText should return default for missing field")
        void getText_should_return_default_for_missing_field() {
            ObjectNode json = MAPPER.createObjectNode();

            String result = AuthConfig.getText(json, "missing", "default");

            assertThat(result).isEqualTo("default");
        }

        @Test
        @DisplayName("getText should return default for null field value")
        void getText_should_return_default_for_null_field_value() {
            ObjectNode json = MAPPER.createObjectNode();
            json.putNull("field");

            String result = AuthConfig.getText(json, "field", "default");

            assertThat(result).isEqualTo("default");
        }

        @Test
        @DisplayName("getText should return default for blank field value")
        void getText_should_return_default_for_blank_field_value() {
            ObjectNode json = MAPPER.createObjectNode();
            json.put("field", "   ");

            String result = AuthConfig.getText(json, "field", "default");

            assertThat(result).isEqualTo("default");
        }

        @Test
        @DisplayName("getText should return default for empty field value")
        void getText_should_return_default_for_empty_field_value() {
            ObjectNode json = MAPPER.createObjectNode();
            json.put("field", "");

            String result = AuthConfig.getText(json, "field", "default");

            assertThat(result).isEqualTo("default");
        }
    }

    // =========================================================================
    // getBoolean() TESTS
    // =========================================================================

    @Nested
    @DisplayName("getBoolean() Helper Tests")
    class GetBooleanTests {

        @Test
        @DisplayName("getBoolean should return value for existing field")
        void getBoolean_should_return_value_for_existing_field() {
            ObjectNode json = MAPPER.createObjectNode();
            json.put("flag", true);

            boolean result = AuthConfig.getBoolean(json, "flag", false);

            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("getBoolean should return false when field is false")
        void getBoolean_should_return_false_when_field_is_false() {
            ObjectNode json = MAPPER.createObjectNode();
            json.put("flag", false);

            boolean result = AuthConfig.getBoolean(json, "flag", true);

            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("getBoolean should return default for null node")
        void getBoolean_should_return_default_for_null_node() {
            boolean result = AuthConfig.getBoolean(null, "flag", true);

            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("getBoolean should return default for missing field")
        void getBoolean_should_return_default_for_missing_field() {
            ObjectNode json = MAPPER.createObjectNode();

            boolean result = AuthConfig.getBoolean(json, "missing", true);

            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("getBoolean should return default for null field value")
        void getBoolean_should_return_default_for_null_field_value() {
            ObjectNode json = MAPPER.createObjectNode();
            json.putNull("flag");

            boolean result = AuthConfig.getBoolean(json, "flag", true);

            assertThat(result).isTrue();
        }
    }

    // =========================================================================
    // SEALED INTERFACE TESTS
    // =========================================================================

    @Test
    @DisplayName("AuthConfig should be a sealed interface")
    void authConfig_should_be_sealed_interface() {
        assertThat(AuthConfig.class.isSealed()).isTrue();
    }

    @Test
    @DisplayName("AuthConfig should have exactly 6 permitted subclasses")
    void authConfig_should_have_six_permitted_subclasses() {
        Class<?>[] permitted = AuthConfig.class.getPermittedSubclasses();

        assertThat(permitted).hasSize(6);
        assertThat(permitted).containsExactlyInAnyOrder(
                NoAuthConfig.class,
                BasicAuthConfig.class,
                BearerAuthConfig.class,
                ApiKeyAuthConfig.class,
                OAuth2ClientConfig.class,
                OAuth2PasswordConfig.class
        );
    }
}
