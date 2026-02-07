package com.bonitasoft.processbuilder.extension.template.auth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for {@link ApiKeyAuthConfig}.
 *
 * @author Bonitasoft
 * @since 1.0
 */
@DisplayName("ApiKeyAuthConfig Unit Tests")
class ApiKeyAuthConfigTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final String TEST_KEY_NAME = "X-API-Key";
    private static final String TEST_KEY_VALUE = "abc123xyz";

    // =========================================================================
    // CONSTRUCTOR TESTS
    // =========================================================================

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("should create instance with all parameters")
        void should_create_instance_with_all_parameters() {
            ApiKeyAuthConfig config = new ApiKeyAuthConfig(TEST_KEY_NAME, TEST_KEY_VALUE, ApiKeyAuthConfig.Location.HEADER);

            assertThat(config.keyName()).isEqualTo(TEST_KEY_NAME);
            assertThat(config.keyValue()).isEqualTo(TEST_KEY_VALUE);
            assertThat(config.location()).isEqualTo(ApiKeyAuthConfig.Location.HEADER);
        }

        @Test
        @DisplayName("should create instance with two parameters (defaults to HEADER)")
        void should_create_instance_with_two_parameters() {
            ApiKeyAuthConfig config = new ApiKeyAuthConfig(TEST_KEY_NAME, TEST_KEY_VALUE);

            assertThat(config.location()).isEqualTo(ApiKeyAuthConfig.Location.HEADER);
        }

        @Test
        @DisplayName("should throw NPE for null keyName")
        void should_throw_npe_for_null_keyName() {
            assertThatThrownBy(() -> new ApiKeyAuthConfig(null, TEST_KEY_VALUE, ApiKeyAuthConfig.Location.HEADER))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("Key name");
        }

        @Test
        @DisplayName("should throw NPE for null keyValue")
        void should_throw_npe_for_null_keyValue() {
            assertThatThrownBy(() -> new ApiKeyAuthConfig(TEST_KEY_NAME, null, ApiKeyAuthConfig.Location.HEADER))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("Key value");
        }

        @Test
        @DisplayName("should throw NPE for null location")
        void should_throw_npe_for_null_location() {
            assertThatThrownBy(() -> new ApiKeyAuthConfig(TEST_KEY_NAME, TEST_KEY_VALUE, null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("Location");
        }
    }

    // =========================================================================
    // FACTORY METHOD TESTS
    // =========================================================================

    @Test
    @DisplayName("header() should create instance with HEADER location")
    void header_should_create_instance_with_header_location() {
        ApiKeyAuthConfig config = ApiKeyAuthConfig.header(TEST_KEY_NAME, TEST_KEY_VALUE);

        assertThat(config.location()).isEqualTo(ApiKeyAuthConfig.Location.HEADER);
        assertThat(config.keyName()).isEqualTo(TEST_KEY_NAME);
        assertThat(config.keyValue()).isEqualTo(TEST_KEY_VALUE);
    }

    @Test
    @DisplayName("queryParam() should create instance with QUERY location")
    void queryParam_should_create_instance_with_query_location() {
        ApiKeyAuthConfig config = ApiKeyAuthConfig.queryParam(TEST_KEY_NAME, TEST_KEY_VALUE);

        assertThat(config.location()).isEqualTo(ApiKeyAuthConfig.Location.QUERY);
        assertThat(config.keyName()).isEqualTo(TEST_KEY_NAME);
        assertThat(config.keyValue()).isEqualTo(TEST_KEY_VALUE);
    }

    // =========================================================================
    // Location ENUM TESTS
    // =========================================================================

    @Nested
    @DisplayName("Location Enum Tests")
    class LocationEnumTests {

        @Test
        @DisplayName("HEADER should have value 'header'")
        void header_should_have_value_header() {
            assertThat(ApiKeyAuthConfig.Location.HEADER.getValue()).isEqualTo("header");
        }

        @Test
        @DisplayName("QUERY should have value 'queryParam'")
        void query_should_have_value_queryParam() {
            assertThat(ApiKeyAuthConfig.Location.QUERY.getValue()).isEqualTo("queryParam");
        }

        @Test
        @DisplayName("fromString should return HEADER for null")
        void fromString_should_return_header_for_null() {
            assertThat(ApiKeyAuthConfig.Location.fromString(null)).isEqualTo(ApiKeyAuthConfig.Location.HEADER);
        }

        @Test
        @DisplayName("fromString should return HEADER for blank")
        void fromString_should_return_header_for_blank() {
            assertThat(ApiKeyAuthConfig.Location.fromString("   ")).isEqualTo(ApiKeyAuthConfig.Location.HEADER);
        }

        @ParameterizedTest
        @ValueSource(strings = {"QUERY", "query", "QUERYPARAM", "queryParam", "QUERY_PARAM", "query_param"})
        @DisplayName("fromString should return QUERY for query-related values")
        void fromString_should_return_query_for_query_values(String value) {
            assertThat(ApiKeyAuthConfig.Location.fromString(value)).isEqualTo(ApiKeyAuthConfig.Location.QUERY);
        }

        @ParameterizedTest
        @ValueSource(strings = {"HEADER", "header", "Header", "unknown", "other"})
        @DisplayName("fromString should return HEADER for non-query values")
        void fromString_should_return_header_for_non_query_values(String value) {
            assertThat(ApiKeyAuthConfig.Location.fromString(value)).isEqualTo(ApiKeyAuthConfig.Location.HEADER);
        }
    }

    // =========================================================================
    // getAuthType() TESTS
    // =========================================================================

    @Test
    @DisplayName("getAuthType should return 'api_key'")
    void getAuthType_should_return_api_key() {
        ApiKeyAuthConfig config = new ApiKeyAuthConfig(TEST_KEY_NAME, TEST_KEY_VALUE);

        assertThat(config.getAuthType()).isEqualTo("api_key");
    }

    // =========================================================================
    // toJson() TESTS
    // =========================================================================

    @Nested
    @DisplayName("toJson() Tests")
    class ToJsonTests {

        @Test
        @DisplayName("toJson should include all fields")
        void toJson_should_include_all_fields() {
            ApiKeyAuthConfig config = new ApiKeyAuthConfig(TEST_KEY_NAME, TEST_KEY_VALUE, ApiKeyAuthConfig.Location.QUERY);
            JsonNode json = config.toJson(MAPPER);

            assertThat(json.has("authType")).isTrue();
            assertThat(json.has("keyName")).isTrue();
            assertThat(json.has("keyValue")).isTrue();
            assertThat(json.has("location")).isTrue();
        }

        @Test
        @DisplayName("toJson should have correct values")
        void toJson_should_have_correct_values() {
            ApiKeyAuthConfig config = new ApiKeyAuthConfig(TEST_KEY_NAME, TEST_KEY_VALUE, ApiKeyAuthConfig.Location.QUERY);
            JsonNode json = config.toJson(MAPPER);

            assertThat(json.get("authType").asText()).isEqualTo("api_key");
            assertThat(json.get("keyName").asText()).isEqualTo(TEST_KEY_NAME);
            assertThat(json.get("keyValue").asText()).isEqualTo(TEST_KEY_VALUE);
            assertThat(json.get("location").asText()).isEqualTo("queryParam");
        }

        @Test
        @DisplayName("toJson should have exactly 4 fields")
        void toJson_should_have_exactly_four_fields() {
            ApiKeyAuthConfig config = new ApiKeyAuthConfig(TEST_KEY_NAME, TEST_KEY_VALUE);
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
        ApiKeyAuthConfig config = new ApiKeyAuthConfig(TEST_KEY_NAME, TEST_KEY_VALUE);
        JsonNode json = config.toJsonEncrypted(MAPPER);

        assertThat(json.has("authType")).isTrue();
        assertThat(json.has("keyName")).isTrue();
        assertThat(json.has("keyValue")).isTrue();
        assertThat(json.has("location")).isTrue();
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
            json.put("authType", "api_key");
            json.put("keyName", TEST_KEY_NAME);
            json.put("keyValue", TEST_KEY_VALUE);
            json.put("location", "queryParam");

            ApiKeyAuthConfig config = ApiKeyAuthConfig.fromJson(json);

            assertThat(config.keyName()).isEqualTo(TEST_KEY_NAME);
            assertThat(config.keyValue()).isEqualTo(TEST_KEY_VALUE);
            assertThat(config.location()).isEqualTo(ApiKeyAuthConfig.Location.QUERY);
        }

        @Test
        @DisplayName("fromJson should use defaults for missing fields")
        void fromJson_should_use_defaults_for_missing_fields() {
            ObjectNode json = MAPPER.createObjectNode();
            json.put("authType", "api_key");

            ApiKeyAuthConfig config = ApiKeyAuthConfig.fromJson(json);

            assertThat(config.keyName()).isEqualTo("X-API-Key");
            assertThat(config.keyValue()).isEmpty();
            assertThat(config.location()).isEqualTo(ApiKeyAuthConfig.Location.HEADER);
        }

        @Test
        @DisplayName("fromJson should support legacy apiKeyName field")
        void fromJson_should_support_legacy_apiKeyName_field() {
            ObjectNode json = MAPPER.createObjectNode();
            json.put("authType", "api_key");
            json.put("apiKeyName", "Custom-Key");

            ApiKeyAuthConfig config = ApiKeyAuthConfig.fromJson(json);

            assertThat(config.keyName()).isEqualTo("Custom-Key");
        }

        @Test
        @DisplayName("fromJson should support legacy apiKeyValue field")
        void fromJson_should_support_legacy_apiKeyValue_field() {
            ObjectNode json = MAPPER.createObjectNode();
            json.put("authType", "api_key");
            json.put("apiKeyValue", "legacy-value");

            ApiKeyAuthConfig config = ApiKeyAuthConfig.fromJson(json);

            assertThat(config.keyValue()).isEqualTo("legacy-value");
        }

        @Test
        @DisplayName("fromJson should support legacy apiKeyLocation field")
        void fromJson_should_support_legacy_apiKeyLocation_field() {
            ObjectNode json = MAPPER.createObjectNode();
            json.put("authType", "api_key");
            json.put("keyName", TEST_KEY_NAME);
            json.put("keyValue", TEST_KEY_VALUE);
            json.put("apiKeyLocation", "query");

            ApiKeyAuthConfig config = ApiKeyAuthConfig.fromJson(json);

            assertThat(config.location()).isEqualTo(ApiKeyAuthConfig.Location.QUERY);
        }
    }

    // =========================================================================
    // RECORD EQUALITY TESTS
    // =========================================================================

    @Test
    @DisplayName("equals should return true for identical values")
    void equals_should_return_true_for_identical_values() {
        ApiKeyAuthConfig config1 = new ApiKeyAuthConfig(TEST_KEY_NAME, TEST_KEY_VALUE, ApiKeyAuthConfig.Location.HEADER);
        ApiKeyAuthConfig config2 = new ApiKeyAuthConfig(TEST_KEY_NAME, TEST_KEY_VALUE, ApiKeyAuthConfig.Location.HEADER);

        assertThat(config1).isEqualTo(config2);
    }

    @Test
    @DisplayName("equals should return false for different locations")
    void equals_should_return_false_for_different_locations() {
        ApiKeyAuthConfig config1 = new ApiKeyAuthConfig(TEST_KEY_NAME, TEST_KEY_VALUE, ApiKeyAuthConfig.Location.HEADER);
        ApiKeyAuthConfig config2 = new ApiKeyAuthConfig(TEST_KEY_NAME, TEST_KEY_VALUE, ApiKeyAuthConfig.Location.QUERY);

        assertThat(config1).isNotEqualTo(config2);
    }

    // =========================================================================
    // AuthConfig INTERFACE COMPLIANCE
    // =========================================================================

    @Test
    @DisplayName("should implement AuthConfig interface")
    void should_implement_authConfig_interface() {
        ApiKeyAuthConfig config = new ApiKeyAuthConfig(TEST_KEY_NAME, TEST_KEY_VALUE);

        assertThat(config).isInstanceOf(AuthConfig.class);
    }
}
