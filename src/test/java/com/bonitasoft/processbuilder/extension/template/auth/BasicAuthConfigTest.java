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
 * Unit tests for {@link BasicAuthConfig}.
 *
 * @author Bonitasoft
 * @since 1.0
 */
@DisplayName("BasicAuthConfig Unit Tests")
class BasicAuthConfigTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final String TEST_USERNAME = "testUser";
    private static final String TEST_PASSWORD = "testPass123";

    // =========================================================================
    // CONSTRUCTOR TESTS
    // =========================================================================

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("should create instance with all parameters")
        void should_create_instance_with_all_parameters() {
            BasicAuthConfig config = new BasicAuthConfig(TEST_USERNAME, TEST_PASSWORD, true);

            assertThat(config.username()).isEqualTo(TEST_USERNAME);
            assertThat(config.password()).isEqualTo(TEST_PASSWORD);
            assertThat(config.preemptive()).isTrue();
        }

        @Test
        @DisplayName("should create instance with two parameters (preemptive defaults to true)")
        void should_create_instance_with_two_parameters() {
            BasicAuthConfig config = new BasicAuthConfig(TEST_USERNAME, TEST_PASSWORD);

            assertThat(config.username()).isEqualTo(TEST_USERNAME);
            assertThat(config.password()).isEqualTo(TEST_PASSWORD);
            assertThat(config.preemptive()).isTrue();
        }

        @Test
        @DisplayName("should allow preemptive to be false")
        void should_allow_preemptive_to_be_false() {
            BasicAuthConfig config = new BasicAuthConfig(TEST_USERNAME, TEST_PASSWORD, false);

            assertThat(config.preemptive()).isFalse();
        }

        @Test
        @DisplayName("should throw NPE for null username")
        void should_throw_npe_for_null_username() {
            assertThatThrownBy(() -> new BasicAuthConfig(null, TEST_PASSWORD, true))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("Username");
        }

        @Test
        @DisplayName("should throw NPE for null password")
        void should_throw_npe_for_null_password() {
            assertThatThrownBy(() -> new BasicAuthConfig(TEST_USERNAME, null, true))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("Password");
        }

        @Test
        @DisplayName("should allow empty username")
        void should_allow_empty_username() {
            BasicAuthConfig config = new BasicAuthConfig("", TEST_PASSWORD, true);

            assertThat(config.username()).isEmpty();
        }

        @Test
        @DisplayName("should allow empty password")
        void should_allow_empty_password() {
            BasicAuthConfig config = new BasicAuthConfig(TEST_USERNAME, "", true);

            assertThat(config.password()).isEmpty();
        }
    }

    // =========================================================================
    // getAuthType() TESTS
    // =========================================================================

    @Test
    @DisplayName("getAuthType should return 'basic'")
    void getAuthType_should_return_basic() {
        BasicAuthConfig config = new BasicAuthConfig(TEST_USERNAME, TEST_PASSWORD);

        assertThat(config.getAuthType()).isEqualTo("basic");
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
            BasicAuthConfig config = new BasicAuthConfig(TEST_USERNAME, TEST_PASSWORD, true);
            JsonNode json = config.toJson(MAPPER);

            assertThat(json).isNotNull();
            assertThat(json.isObject()).isTrue();
        }

        @Test
        @DisplayName("toJson should include all fields")
        void toJson_should_include_all_fields() {
            BasicAuthConfig config = new BasicAuthConfig(TEST_USERNAME, TEST_PASSWORD, true);
            JsonNode json = config.toJson(MAPPER);

            assertThat(json.has("authType")).isTrue();
            assertThat(json.has("username")).isTrue();
            assertThat(json.has("password")).isTrue();
            assertThat(json.has("preemptive")).isTrue();
        }

        @Test
        @DisplayName("toJson should have correct field values")
        void toJson_should_have_correct_field_values() {
            BasicAuthConfig config = new BasicAuthConfig(TEST_USERNAME, TEST_PASSWORD, false);
            JsonNode json = config.toJson(MAPPER);

            assertThat(json.get("authType").asText()).isEqualTo("basic");
            assertThat(json.get("username").asText()).isEqualTo(TEST_USERNAME);
            assertThat(json.get("password").asText()).isEqualTo(TEST_PASSWORD);
            assertThat(json.get("preemptive").asBoolean()).isFalse();
        }

        @Test
        @DisplayName("toJson should have exactly 4 fields")
        void toJson_should_have_exactly_four_fields() {
            BasicAuthConfig config = new BasicAuthConfig(TEST_USERNAME, TEST_PASSWORD, true);
            JsonNode json = config.toJson(MAPPER);

            assertThat(json.size()).isEqualTo(4);
        }
    }

    // =========================================================================
    // toJsonEncrypted() TESTS
    // =========================================================================

    @Test
    @DisplayName("toJsonEncrypted should include all fields")
    void toJsonEncrypted_should_include_all_fields() {
        BasicAuthConfig config = new BasicAuthConfig(TEST_USERNAME, TEST_PASSWORD, true);
        JsonNode json = config.toJsonEncrypted(MAPPER);

        assertThat(json.has("authType")).isTrue();
        assertThat(json.has("username")).isTrue();
        assertThat(json.has("password")).isTrue();
        assertThat(json.has("preemptive")).isTrue();
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
            json.put("authType", "basic");
            json.put("username", TEST_USERNAME);
            json.put("password", TEST_PASSWORD);
            json.put("preemptive", true);

            BasicAuthConfig config = BasicAuthConfig.fromJson(json);

            assertThat(config.username()).isEqualTo(TEST_USERNAME);
            assertThat(config.password()).isEqualTo(TEST_PASSWORD);
            assertThat(config.preemptive()).isTrue();
        }

        @Test
        @DisplayName("fromJson should use defaults for missing fields")
        void fromJson_should_use_defaults_for_missing_fields() {
            ObjectNode json = MAPPER.createObjectNode();
            json.put("authType", "basic");

            BasicAuthConfig config = BasicAuthConfig.fromJson(json);

            assertThat(config.username()).isEmpty();
            assertThat(config.password()).isEmpty();
            assertThat(config.preemptive()).isTrue();
        }

        @Test
        @DisplayName("fromJson should handle preemptive false")
        void fromJson_should_handle_preemptive_false() {
            ObjectNode json = MAPPER.createObjectNode();
            json.put("username", TEST_USERNAME);
            json.put("password", TEST_PASSWORD);
            json.put("preemptive", false);

            BasicAuthConfig config = BasicAuthConfig.fromJson(json);

            assertThat(config.preemptive()).isFalse();
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
            BasicAuthConfig config = BasicAuthConfig.builder()
                    .username(TEST_USERNAME)
                    .password(TEST_PASSWORD)
                    .preemptive(false)
                    .build();

            assertThat(config.username()).isEqualTo(TEST_USERNAME);
            assertThat(config.password()).isEqualTo(TEST_PASSWORD);
            assertThat(config.preemptive()).isFalse();
        }

        @Test
        @DisplayName("builder should use defaults")
        void builder_should_use_defaults() {
            BasicAuthConfig config = BasicAuthConfig.builder().build();

            assertThat(config.username()).isEmpty();
            assertThat(config.password()).isEmpty();
            assertThat(config.preemptive()).isTrue();
        }

        @Test
        @DisplayName("builder methods should return builder for chaining")
        void builder_methods_should_return_builder_for_chaining() {
            BasicAuthConfig.Builder builder = BasicAuthConfig.builder();

            assertThat(builder.username(TEST_USERNAME)).isSameAs(builder);
            assertThat(builder.password(TEST_PASSWORD)).isSameAs(builder);
            assertThat(builder.preemptive(true)).isSameAs(builder);
        }
    }

    // =========================================================================
    // RECORD EQUALITY TESTS
    // =========================================================================

    @Test
    @DisplayName("equals should return true for identical values")
    void equals_should_return_true_for_identical_values() {
        BasicAuthConfig config1 = new BasicAuthConfig(TEST_USERNAME, TEST_PASSWORD, true);
        BasicAuthConfig config2 = new BasicAuthConfig(TEST_USERNAME, TEST_PASSWORD, true);

        assertThat(config1).isEqualTo(config2);
    }

    @Test
    @DisplayName("equals should return false for different values")
    void equals_should_return_false_for_different_values() {
        BasicAuthConfig config1 = new BasicAuthConfig(TEST_USERNAME, TEST_PASSWORD, true);
        BasicAuthConfig config2 = new BasicAuthConfig("other", TEST_PASSWORD, true);

        assertThat(config1).isNotEqualTo(config2);
    }

    @Test
    @DisplayName("hashCode should be consistent with equals")
    void hashCode_should_be_consistent_with_equals() {
        BasicAuthConfig config1 = new BasicAuthConfig(TEST_USERNAME, TEST_PASSWORD, true);
        BasicAuthConfig config2 = new BasicAuthConfig(TEST_USERNAME, TEST_PASSWORD, true);

        assertThat(config1.hashCode()).isEqualTo(config2.hashCode());
    }

    // =========================================================================
    // AuthConfig INTERFACE COMPLIANCE
    // =========================================================================

    @Test
    @DisplayName("should implement AuthConfig interface")
    void should_implement_authConfig_interface() {
        BasicAuthConfig config = new BasicAuthConfig(TEST_USERNAME, TEST_PASSWORD);

        assertThat(config).isInstanceOf(AuthConfig.class);
    }
}
