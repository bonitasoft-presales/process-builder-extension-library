package com.bonitasoft.processbuilder.extension.template.auth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link NoAuthConfig}.
 *
 * @author Bonitasoft
 * @since 1.0
 */
@DisplayName("NoAuthConfig Unit Tests")
class NoAuthConfigTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    // =========================================================================
    // SINGLETON TESTS
    // =========================================================================

    @Test
    @DisplayName("INSTANCE should be a singleton")
    void instance_should_be_singleton() {
        NoAuthConfig instance1 = NoAuthConfig.INSTANCE;
        NoAuthConfig instance2 = NoAuthConfig.INSTANCE;

        assertThat(instance1).isSameAs(instance2);
    }

    @Test
    @DisplayName("INSTANCE should not be null")
    void instance_should_not_be_null() {
        assertThat(NoAuthConfig.INSTANCE).isNotNull();
    }

    // =========================================================================
    // getAuthType() TESTS
    // =========================================================================

    @Test
    @DisplayName("getAuthType should return 'none'")
    void getAuthType_should_return_none() {
        assertThat(NoAuthConfig.INSTANCE.getAuthType()).isEqualTo("none");
    }

    @Test
    @DisplayName("getAuthType should never return null")
    void getAuthType_should_never_return_null() {
        assertThat(NoAuthConfig.INSTANCE.getAuthType()).isNotNull();
    }

    @Test
    @DisplayName("getAuthType should never return blank")
    void getAuthType_should_never_return_blank() {
        assertThat(NoAuthConfig.INSTANCE.getAuthType()).isNotBlank();
    }

    // =========================================================================
    // toJson() TESTS
    // =========================================================================

    @Test
    @DisplayName("toJson should return valid JSON node")
    void toJson_should_return_valid_json_node() {
        JsonNode json = NoAuthConfig.INSTANCE.toJson(MAPPER);

        assertThat(json).isNotNull();
        assertThat(json.isObject()).isTrue();
    }

    @Test
    @DisplayName("toJson should include authType field")
    void toJson_should_include_authType_field() {
        JsonNode json = NoAuthConfig.INSTANCE.toJson(MAPPER);

        assertThat(json.has("authType")).isTrue();
        assertThat(json.get("authType").asText()).isEqualTo("none");
    }

    @Test
    @DisplayName("toJson should only contain authType field")
    void toJson_should_only_contain_authType_field() {
        JsonNode json = NoAuthConfig.INSTANCE.toJson(MAPPER);

        assertThat(json.size()).isEqualTo(1);
        assertThat(json.fieldNames()).toIterable().containsExactly("authType");
    }

    // =========================================================================
    // toJsonEncrypted() TESTS
    // =========================================================================

    @Test
    @DisplayName("toJsonEncrypted should return same as toJson")
    void toJsonEncrypted_should_return_same_as_toJson() {
        JsonNode json = NoAuthConfig.INSTANCE.toJson(MAPPER);
        JsonNode jsonEncrypted = NoAuthConfig.INSTANCE.toJsonEncrypted(MAPPER);

        assertThat(jsonEncrypted).isEqualTo(json);
    }

    @Test
    @DisplayName("toJsonEncrypted should include authType field")
    void toJsonEncrypted_should_include_authType_field() {
        JsonNode json = NoAuthConfig.INSTANCE.toJsonEncrypted(MAPPER);

        assertThat(json.has("authType")).isTrue();
        assertThat(json.get("authType").asText()).isEqualTo("none");
    }

    // =========================================================================
    // toString() TESTS
    // =========================================================================

    @Test
    @DisplayName("toString should return expected format")
    void toString_should_return_expected_format() {
        assertThat(NoAuthConfig.INSTANCE.toString()).isEqualTo("NoAuthConfig[]");
    }

    @Test
    @DisplayName("toString should not be null")
    void toString_should_not_be_null() {
        assertThat(NoAuthConfig.INSTANCE.toString()).isNotNull();
    }

    @Test
    @DisplayName("toString should contain class name")
    void toString_should_contain_class_name() {
        assertThat(NoAuthConfig.INSTANCE.toString()).contains("NoAuthConfig");
    }

    // =========================================================================
    // AuthConfig INTERFACE COMPLIANCE
    // =========================================================================

    @Test
    @DisplayName("should implement AuthConfig interface")
    void should_implement_authConfig_interface() {
        assertThat(NoAuthConfig.INSTANCE).isInstanceOf(AuthConfig.class);
    }
}
