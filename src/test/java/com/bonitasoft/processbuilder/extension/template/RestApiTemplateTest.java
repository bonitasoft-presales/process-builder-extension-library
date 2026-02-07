package com.bonitasoft.processbuilder.extension.template;

import com.bonitasoft.processbuilder.extension.template.auth.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for {@link RestApiTemplate} and its nested {@link RestApiTemplate.Method} record.
 *
 * @author Bonitasoft
 * @since 1.0
 */
@DisplayName("RestApiTemplate Unit Tests")
class RestApiTemplateTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final String TEST_NAME = "TestAPI";
    private static final String TEST_DISPLAY_NAME = "Test API";
    private static final String TEST_DESCRIPTION = "A test API";
    private static final String TEST_BASE_URL = "https://api.test.com";

    // =========================================================================
    // CONSTRUCTOR TESTS
    // =========================================================================

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("should create instance with required parameters")
        void should_create_instance_with_required_parameters() {
            RestApiTemplate template = new RestApiTemplate(
                    TEST_NAME, TEST_DISPLAY_NAME, TEST_DESCRIPTION, TEST_BASE_URL,
                    30000, true, NoAuthConfig.INSTANCE, null, null);

            assertThat(template.name()).isEqualTo(TEST_NAME);
            assertThat(template.displayName()).isEqualTo(TEST_DISPLAY_NAME);
            assertThat(template.baseUrl()).isEqualTo(TEST_BASE_URL);
        }

        @Test
        @DisplayName("should throw NPE for null name")
        void should_throw_npe_for_null_name() {
            assertThatThrownBy(() -> new RestApiTemplate(
                    null, TEST_DISPLAY_NAME, TEST_DESCRIPTION, TEST_BASE_URL,
                    30000, true, NoAuthConfig.INSTANCE, null, null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("Name");
        }

        @Test
        @DisplayName("should throw NPE for null baseUrl")
        void should_throw_npe_for_null_baseUrl() {
            assertThatThrownBy(() -> new RestApiTemplate(
                    TEST_NAME, TEST_DISPLAY_NAME, TEST_DESCRIPTION, null,
                    30000, true, NoAuthConfig.INSTANCE, null, null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("Base URL");
        }

        @Test
        @DisplayName("should default displayName to name when null")
        void should_default_displayName_to_name_when_null() {
            RestApiTemplate template = new RestApiTemplate(
                    TEST_NAME, null, TEST_DESCRIPTION, TEST_BASE_URL,
                    30000, true, NoAuthConfig.INSTANCE, null, null);

            assertThat(template.displayName()).isEqualTo(TEST_NAME);
        }

        @Test
        @DisplayName("should default displayName to name when blank")
        void should_default_displayName_to_name_when_blank() {
            RestApiTemplate template = new RestApiTemplate(
                    TEST_NAME, "   ", TEST_DESCRIPTION, TEST_BASE_URL,
                    30000, true, NoAuthConfig.INSTANCE, null, null);

            assertThat(template.displayName()).isEqualTo(TEST_NAME);
        }

        @Test
        @DisplayName("should default timeoutMs to 30000 when zero or negative")
        void should_default_timeoutMs_when_invalid() {
            RestApiTemplate template = new RestApiTemplate(
                    TEST_NAME, TEST_DISPLAY_NAME, TEST_DESCRIPTION, TEST_BASE_URL,
                    0, true, NoAuthConfig.INSTANCE, null, null);

            assertThat(template.timeoutMs()).isEqualTo(30000);
        }

        @Test
        @DisplayName("should default auth to NoAuthConfig when null")
        void should_default_auth_to_noAuth_when_null() {
            RestApiTemplate template = new RestApiTemplate(
                    TEST_NAME, TEST_DISPLAY_NAME, TEST_DESCRIPTION, TEST_BASE_URL,
                    30000, true, null, null, null);

            assertThat(template.auth()).isEqualTo(NoAuthConfig.INSTANCE);
        }

        @Test
        @DisplayName("should default headers when null")
        void should_default_headers_when_null() {
            RestApiTemplate template = new RestApiTemplate(
                    TEST_NAME, TEST_DISPLAY_NAME, TEST_DESCRIPTION, TEST_BASE_URL,
                    30000, true, NoAuthConfig.INSTANCE, null, null);

            assertThat(template.headers()).containsKey("Accept");
            assertThat(template.headers()).containsKey("Content-Type");
        }

        @Test
        @DisplayName("should make headers immutable")
        void should_make_headers_immutable() {
            RestApiTemplate template = new RestApiTemplate(
                    TEST_NAME, TEST_DISPLAY_NAME, TEST_DESCRIPTION, TEST_BASE_URL,
                    30000, true, NoAuthConfig.INSTANCE, Map.of("X-Custom", "value"), null);

            assertThatThrownBy(() -> template.headers().put("NEW", "value"))
                    .isInstanceOf(UnsupportedOperationException.class);
        }

        @Test
        @DisplayName("should default methods to empty list when null")
        void should_default_methods_to_empty_list_when_null() {
            RestApiTemplate template = new RestApiTemplate(
                    TEST_NAME, TEST_DISPLAY_NAME, TEST_DESCRIPTION, TEST_BASE_URL,
                    30000, true, NoAuthConfig.INSTANCE, null, null);

            assertThat(template.methods()).isEmpty();
        }

        @Test
        @DisplayName("should make methods immutable")
        void should_make_methods_immutable() {
            RestApiTemplate.Method method = new RestApiTemplate.Method("Get", "Get", "/");
            RestApiTemplate template = new RestApiTemplate(
                    TEST_NAME, TEST_DISPLAY_NAME, TEST_DESCRIPTION, TEST_BASE_URL,
                    30000, true, NoAuthConfig.INSTANCE, null, List.of(method));

            assertThatThrownBy(() -> template.methods().add(method))
                    .isInstanceOf(UnsupportedOperationException.class);
        }
    }

    // =========================================================================
    // BUILDER TESTS
    // =========================================================================

    @Nested
    @DisplayName("Builder Tests")
    class BuilderTests {

        @Test
        @DisplayName("builder should create valid template")
        void builder_should_create_valid_template() {
            RestApiTemplate template = RestApiTemplate.builder()
                    .name(TEST_NAME)
                    .displayName(TEST_DISPLAY_NAME)
                    .description(TEST_DESCRIPTION)
                    .baseUrl(TEST_BASE_URL)
                    .timeoutMs(15000)
                    .verifySsl(false)
                    .auth(new BasicAuthConfig("user", "pass"))
                    .build();

            assertThat(template.name()).isEqualTo(TEST_NAME);
            assertThat(template.timeoutMs()).isEqualTo(15000);
            assertThat(template.verifySsl()).isFalse();
            assertThat(template.auth()).isInstanceOf(BasicAuthConfig.class);
        }

        @Test
        @DisplayName("builder should add methods")
        void builder_should_add_methods() {
            RestApiTemplate template = RestApiTemplate.builder()
                    .name(TEST_NAME)
                    .baseUrl(TEST_BASE_URL)
                    .addMethod("GetUsers", "GET", "/users")
                    .addMethod("GetUser", "GET", "/users/1")
                    .build();

            assertThat(template.methods()).hasSize(2);
        }

        @Test
        @DisplayName("builder should add method with query params")
        void builder_should_add_method_with_query_params() {
            RestApiTemplate template = RestApiTemplate.builder()
                    .name(TEST_NAME)
                    .baseUrl(TEST_BASE_URL)
                    .addMethod("Search", "GET", "/search", Map.of("q", "test", "limit", "10"))
                    .build();

            assertThat(template.methods()).hasSize(1);
            assertThat(template.methods().get(0).queryParams()).containsKey("q");
        }

        @Test
        @DisplayName("builder should add custom headers")
        void builder_should_add_custom_headers() {
            RestApiTemplate template = RestApiTemplate.builder()
                    .name(TEST_NAME)
                    .baseUrl(TEST_BASE_URL)
                    .header("X-Custom", "value")
                    .header("X-Another", "value2")
                    .build();

            assertThat(template.headers()).containsKey("X-Custom");
            assertThat(template.headers()).containsKey("X-Another");
        }

        @Test
        @DisplayName("builder should add headers from map")
        void builder_should_add_headers_from_map() {
            RestApiTemplate template = RestApiTemplate.builder()
                    .name(TEST_NAME)
                    .baseUrl(TEST_BASE_URL)
                    .headers(Map.of("X-First", "1", "X-Second", "2"))
                    .build();

            assertThat(template.headers()).containsKey("X-First");
            assertThat(template.headers()).containsKey("X-Second");
        }

        @Test
        @DisplayName("builder should default headers when none added")
        void builder_should_default_headers_when_none_added() {
            RestApiTemplate template = RestApiTemplate.builder()
                    .name(TEST_NAME)
                    .baseUrl(TEST_BASE_URL)
                    .build();

            assertThat(template.headers()).containsEntry("Accept", "application/json");
            assertThat(template.headers()).containsEntry("Content-Type", "application/json");
        }

        @Test
        @DisplayName("builder methods should return builder for chaining")
        void builder_methods_should_return_builder() {
            RestApiTemplate.Builder builder = RestApiTemplate.builder();

            assertThat(builder.name(TEST_NAME)).isSameAs(builder);
            assertThat(builder.displayName(TEST_DISPLAY_NAME)).isSameAs(builder);
            assertThat(builder.description(TEST_DESCRIPTION)).isSameAs(builder);
            assertThat(builder.baseUrl(TEST_BASE_URL)).isSameAs(builder);
            assertThat(builder.timeoutMs(30000)).isSameAs(builder);
            assertThat(builder.verifySsl(true)).isSameAs(builder);
            assertThat(builder.auth(NoAuthConfig.INSTANCE)).isSameAs(builder);
        }
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
            RestApiTemplate template = RestApiTemplate.builder()
                    .name(TEST_NAME)
                    .baseUrl(TEST_BASE_URL)
                    .build();
            JsonNode json = template.toJson(MAPPER);

            assertThat(json.has("baseUrl")).isTrue();
            assertThat(json.has("timeoutMs")).isTrue();
            assertThat(json.has("verifySsl")).isTrue();
            assertThat(json.has("auth")).isTrue();
        }

        @Test
        @DisplayName("toJson should include headers when not empty")
        void toJson_should_include_headers() {
            RestApiTemplate template = RestApiTemplate.builder()
                    .name(TEST_NAME)
                    .baseUrl(TEST_BASE_URL)
                    .header("X-Test", "value")
                    .build();
            JsonNode json = template.toJson(MAPPER);

            assertThat(json.has("headers")).isTrue();
            assertThat(json.get("headers").has("X-Test")).isTrue();
        }

        @Test
        @DisplayName("toJson should include methods when not empty")
        void toJson_should_include_methods() {
            RestApiTemplate template = RestApiTemplate.builder()
                    .name(TEST_NAME)
                    .baseUrl(TEST_BASE_URL)
                    .addMethod("Get", "GET", "/test")
                    .build();
            JsonNode json = template.toJson(MAPPER);

            assertThat(json.has("methods")).isTrue();
            assertThat(json.get("methods").isArray()).isTrue();
            assertThat(json.get("methods").size()).isEqualTo(1);
        }

        @Test
        @DisplayName("toJson should have correct values")
        void toJson_should_have_correct_values() {
            RestApiTemplate template = RestApiTemplate.builder()
                    .name(TEST_NAME)
                    .baseUrl(TEST_BASE_URL)
                    .timeoutMs(15000)
                    .verifySsl(false)
                    .build();
            JsonNode json = template.toJson(MAPPER);

            assertThat(json.get("baseUrl").asText()).isEqualTo(TEST_BASE_URL);
            assertThat(json.get("timeoutMs").asInt()).isEqualTo(15000);
            assertThat(json.get("verifySsl").asBoolean()).isFalse();
        }
    }

    // =========================================================================
    // toJsonString() TESTS
    // =========================================================================

    @Test
    @DisplayName("toJsonString should return valid JSON string")
    void toJsonString_should_return_valid_json_string() {
        RestApiTemplate template = RestApiTemplate.builder()
                .name(TEST_NAME)
                .baseUrl(TEST_BASE_URL)
                .build();

        String json = template.toJsonString(MAPPER);

        assertThat(json).isNotNull().isNotBlank();
        assertThat(json).startsWith("{");
        assertThat(json).endsWith("}");
        assertThat(json).contains("\"baseUrl\"");
    }

    // =========================================================================
    // toJsonEncrypted() TESTS
    // =========================================================================

    @Test
    @DisplayName("toJsonEncrypted should include auth field")
    void toJsonEncrypted_should_include_auth() {
        RestApiTemplate template = RestApiTemplate.builder()
                .name(TEST_NAME)
                .baseUrl(TEST_BASE_URL)
                .auth(new BasicAuthConfig("user", "pass"))
                .build();

        JsonNode json = template.toJsonEncrypted(MAPPER);

        assertThat(json.has("auth")).isTrue();
    }

    // =========================================================================
    // METHOD RECORD TESTS
    // =========================================================================

    @Nested
    @DisplayName("Method Record Tests")
    class MethodRecordTests {

        @Test
        @DisplayName("should create method with required fields")
        void should_create_method_with_required_fields() {
            RestApiTemplate.Method method = new RestApiTemplate.Method(
                    "GetUsers", "Get Users", "/users");

            assertThat(method.name()).isEqualTo("GetUsers");
            assertThat(method.displayName()).isEqualTo("Get Users");
            assertThat(method.path()).isEqualTo("/users");
            assertThat(method.httpMethod()).isEqualTo("GET");
        }

        @Test
        @DisplayName("should default httpMethod to GET when null")
        void should_default_httpMethod_to_get() {
            RestApiTemplate.Method method = new RestApiTemplate.Method(
                    "Test", "Test", null, null, "/test", null, null, null);

            assertThat(method.httpMethod()).isEqualTo("GET");
        }

        @Test
        @DisplayName("should default httpMethod to GET when blank")
        void should_default_httpMethod_to_get_when_blank() {
            RestApiTemplate.Method method = new RestApiTemplate.Method(
                    "Test", "Test", null, "   ", "/test", null, null, null);

            assertThat(method.httpMethod()).isEqualTo("GET");
        }

        @Test
        @DisplayName("should default path to empty when null")
        void should_default_path_to_empty_when_null() {
            RestApiTemplate.Method method = new RestApiTemplate.Method(
                    "Test", "Test", null, "GET", null, null, null, null);

            assertThat(method.path()).isEmpty();
        }

        @Test
        @DisplayName("should make queryParams immutable")
        void should_make_queryParams_immutable() {
            RestApiTemplate.Method method = new RestApiTemplate.Method(
                    "Test", "Test", null, "GET", "/test",
                    Map.of("key", "value"), null, null);

            assertThatThrownBy(() -> method.queryParams().put("NEW", "value"))
                    .isInstanceOf(UnsupportedOperationException.class);
        }

        @Test
        @DisplayName("should make headers immutable")
        void should_make_headers_immutable() {
            RestApiTemplate.Method method = new RestApiTemplate.Method(
                    "Test", "Test", null, "GET", "/test",
                    null, Map.of("X-Custom", "value"), null);

            assertThatThrownBy(() -> method.headers().put("NEW", "value"))
                    .isInstanceOf(UnsupportedOperationException.class);
        }

        @Test
        @DisplayName("toJson should include required fields")
        void toJson_should_include_required_fields() {
            RestApiTemplate.Method method = new RestApiTemplate.Method(
                    "GetUsers", "Get Users", "/users");
            JsonNode json = method.toJson(MAPPER);

            assertThat(json.has("name")).isTrue();
            assertThat(json.has("displayName")).isTrue();
            assertThat(json.has("httpMethod")).isTrue();
            assertThat(json.has("path")).isTrue();
        }

        @Test
        @DisplayName("toJson should include description when not blank")
        void toJson_should_include_description_when_not_blank() {
            RestApiTemplate.Method method = new RestApiTemplate.Method(
                    "GetUsers", "Get Users", "Fetches all users",
                    "GET", "/users", null, null, null);
            JsonNode json = method.toJson(MAPPER);

            assertThat(json.has("description")).isTrue();
            assertThat(json.get("description").asText()).isEqualTo("Fetches all users");
        }

        @Test
        @DisplayName("toJson should exclude description when blank")
        void toJson_should_exclude_description_when_blank() {
            RestApiTemplate.Method method = new RestApiTemplate.Method(
                    "GetUsers", "Get Users", "/users");
            JsonNode json = method.toJson(MAPPER);

            assertThat(json.has("description")).isFalse();
        }

        @Test
        @DisplayName("toJson should include queryParams when not empty")
        void toJson_should_include_queryParams_when_not_empty() {
            RestApiTemplate.Method method = new RestApiTemplate.Method(
                    "Search", "Search", null, "GET", "/search",
                    Map.of("q", "test"), null, null);
            JsonNode json = method.toJson(MAPPER);

            assertThat(json.has("queryParams")).isTrue();
            assertThat(json.get("queryParams").get("q").asText()).isEqualTo("test");
        }

        @Test
        @DisplayName("toJson should include bodyTemplate when not blank")
        void toJson_should_include_bodyTemplate_when_not_blank() {
            RestApiTemplate.Method method = new RestApiTemplate.Method(
                    "Create", "Create", null, "POST", "/create",
                    null, null, "{\"name\": \"test\"}");
            JsonNode json = method.toJson(MAPPER);

            assertThat(json.has("bodyTemplate")).isTrue();
        }

        @Test
        @DisplayName("toJson should use name as displayName when displayName is null")
        void toJson_should_use_name_as_displayName_fallback() {
            RestApiTemplate.Method method = new RestApiTemplate.Method(
                    "GetData", null, null, "GET", "/data", null, null, null);
            JsonNode json = method.toJson(MAPPER);

            assertThat(json.get("displayName").asText()).isEqualTo("GetData");
        }
    }

    // =========================================================================
    // RECORD EQUALITY TESTS
    // =========================================================================

    @Test
    @DisplayName("equals should return true for identical templates")
    void equals_should_return_true_for_identical_templates() {
        RestApiTemplate template1 = RestApiTemplate.builder()
                .name(TEST_NAME)
                .baseUrl(TEST_BASE_URL)
                .build();
        RestApiTemplate template2 = RestApiTemplate.builder()
                .name(TEST_NAME)
                .baseUrl(TEST_BASE_URL)
                .build();

        assertThat(template1).isEqualTo(template2);
    }

    @Test
    @DisplayName("hashCode should be consistent with equals")
    void hashCode_should_be_consistent_with_equals() {
        RestApiTemplate template1 = RestApiTemplate.builder()
                .name(TEST_NAME)
                .baseUrl(TEST_BASE_URL)
                .build();
        RestApiTemplate template2 = RestApiTemplate.builder()
                .name(TEST_NAME)
                .baseUrl(TEST_BASE_URL)
                .build();

        assertThat(template1.hashCode()).isEqualTo(template2.hashCode());
    }
}
