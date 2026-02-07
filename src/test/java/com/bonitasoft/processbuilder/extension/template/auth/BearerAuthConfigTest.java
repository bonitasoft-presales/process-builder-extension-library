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
 * Unit tests for {@link BearerAuthConfig}.
 *
 * @author Bonitasoft
 * @since 1.0
 */
@DisplayName("BearerAuthConfig Unit Tests")
class BearerAuthConfigTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final String TEST_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test";

    // =========================================================================
    // CONSTRUCTOR TESTS
    // =========================================================================

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("should create instance with valid token")
        void should_create_instance_with_valid_token() {
            BearerAuthConfig config = new BearerAuthConfig(TEST_TOKEN);

            assertThat(config.token()).isEqualTo(TEST_TOKEN);
        }

        @Test
        @DisplayName("should throw NPE for null token")
        void should_throw_npe_for_null_token() {
            assertThatThrownBy(() -> new BearerAuthConfig(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("Token");
        }

        @Test
        @DisplayName("should allow empty token")
        void should_allow_empty_token() {
            BearerAuthConfig config = new BearerAuthConfig("");

            assertThat(config.token()).isEmpty();
        }
    }

    // =========================================================================
    // FACTORY METHOD TESTS
    // =========================================================================

    @Test
    @DisplayName("of() should create instance with token")
    void of_should_create_instance_with_token() {
        BearerAuthConfig config = BearerAuthConfig.of(TEST_TOKEN);

        assertThat(config.token()).isEqualTo(TEST_TOKEN);
    }

    @Test
    @DisplayName("of() should throw NPE for null token")
    void of_should_throw_npe_for_null_token() {
        assertThatThrownBy(() -> BearerAuthConfig.of(null))
                .isInstanceOf(NullPointerException.class);
    }

    // =========================================================================
    // getAuthType() TESTS
    // =========================================================================

    @Test
    @DisplayName("getAuthType should return 'bearer'")
    void getAuthType_should_return_bearer() {
        BearerAuthConfig config = new BearerAuthConfig(TEST_TOKEN);

        assertThat(config.getAuthType()).isEqualTo("bearer");
    }

    @Test
    @DisplayName("getAuthType should not be null or blank")
    void getAuthType_should_not_be_null_or_blank() {
        BearerAuthConfig config = new BearerAuthConfig(TEST_TOKEN);

        assertThat(config.getAuthType()).isNotNull().isNotBlank();
    }

    // =========================================================================
    // toJson() TESTS
    // =========================================================================

    @Nested
    @DisplayName("toJson() Tests")
    class ToJsonTests {

        @Test
        @DisplayName("toJson should return valid JSON node")
        void toJson_should_return_valid_json_node() {
            BearerAuthConfig config = new BearerAuthConfig(TEST_TOKEN);
            JsonNode json = config.toJson(MAPPER);

            assertThat(json).isNotNull();
            assertThat(json.isObject()).isTrue();
        }

        @Test
        @DisplayName("toJson should include authType and token")
        void toJson_should_include_authType_and_token() {
            BearerAuthConfig config = new BearerAuthConfig(TEST_TOKEN);
            JsonNode json = config.toJson(MAPPER);

            assertThat(json.has("authType")).isTrue();
            assertThat(json.has("token")).isTrue();
        }

        @Test
        @DisplayName("toJson should have correct values")
        void toJson_should_have_correct_values() {
            BearerAuthConfig config = new BearerAuthConfig(TEST_TOKEN);
            JsonNode json = config.toJson(MAPPER);

            assertThat(json.get("authType").asText()).isEqualTo("bearer");
            assertThat(json.get("token").asText()).isEqualTo(TEST_TOKEN);
        }

        @Test
        @DisplayName("toJson should have exactly 2 fields")
        void toJson_should_have_exactly_two_fields() {
            BearerAuthConfig config = new BearerAuthConfig(TEST_TOKEN);
            JsonNode json = config.toJson(MAPPER);

            assertThat(json.size()).isEqualTo(2);
        }
    }

    // =========================================================================
    // toJsonEncrypted() TESTS
    // =========================================================================

    @Test
    @DisplayName("toJsonEncrypted should include authType and token")
    void toJsonEncrypted_should_include_authType_and_token() {
        BearerAuthConfig config = new BearerAuthConfig(TEST_TOKEN);
        JsonNode json = config.toJsonEncrypted(MAPPER);

        assertThat(json.has("authType")).isTrue();
        assertThat(json.has("token")).isTrue();
        assertThat(json.get("authType").asText()).isEqualTo("bearer");
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
            json.put("authType", "bearer");
            json.put("token", TEST_TOKEN);

            BearerAuthConfig config = BearerAuthConfig.fromJson(json);

            assertThat(config.token()).isEqualTo(TEST_TOKEN);
        }

        @Test
        @DisplayName("fromJson should use empty string for missing token")
        void fromJson_should_use_empty_for_missing_token() {
            ObjectNode json = MAPPER.createObjectNode();
            json.put("authType", "bearer");

            BearerAuthConfig config = BearerAuthConfig.fromJson(json);

            assertThat(config.token()).isEmpty();
        }
    }

    // =========================================================================
    // RECORD EQUALITY TESTS
    // =========================================================================

    @Test
    @DisplayName("equals should return true for identical tokens")
    void equals_should_return_true_for_identical_tokens() {
        BearerAuthConfig config1 = new BearerAuthConfig(TEST_TOKEN);
        BearerAuthConfig config2 = new BearerAuthConfig(TEST_TOKEN);

        assertThat(config1).isEqualTo(config2);
    }

    @Test
    @DisplayName("equals should return false for different tokens")
    void equals_should_return_false_for_different_tokens() {
        BearerAuthConfig config1 = new BearerAuthConfig(TEST_TOKEN);
        BearerAuthConfig config2 = new BearerAuthConfig("other-token");

        assertThat(config1).isNotEqualTo(config2);
    }

    @Test
    @DisplayName("hashCode should be consistent with equals")
    void hashCode_should_be_consistent_with_equals() {
        BearerAuthConfig config1 = new BearerAuthConfig(TEST_TOKEN);
        BearerAuthConfig config2 = new BearerAuthConfig(TEST_TOKEN);

        assertThat(config1.hashCode()).isEqualTo(config2.hashCode());
    }

    // =========================================================================
    // AuthConfig INTERFACE COMPLIANCE
    // =========================================================================

    @Test
    @DisplayName("should implement AuthConfig interface")
    void should_implement_authConfig_interface() {
        BearerAuthConfig config = new BearerAuthConfig(TEST_TOKEN);

        assertThat(config).isInstanceOf(AuthConfig.class);
    }
}
