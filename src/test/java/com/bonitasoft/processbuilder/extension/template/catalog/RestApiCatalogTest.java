package com.bonitasoft.processbuilder.extension.template.catalog;

import com.bonitasoft.processbuilder.extension.template.RestApiTemplate;
import com.bonitasoft.processbuilder.extension.template.auth.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for {@link RestApiCatalog}.
 *
 * @author Bonitasoft
 * @since 1.0
 */
@DisplayName("RestApiCatalog Unit Tests")
class RestApiCatalogTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final int EXPECTED_TEMPLATE_COUNT = 7;

    // =========================================================================
    // UTILITY CLASS STRUCTURE TESTS
    // =========================================================================

    @Test
    @DisplayName("should have private constructor")
    void should_have_private_constructor() throws Exception {
        Constructor<RestApiCatalog> constructor = RestApiCatalog.class.getDeclaredConstructor();
        assertThat(Modifier.isPrivate(constructor.getModifiers())).isTrue();
    }

    @Test
    @DisplayName("class should be final")
    void class_should_be_final() {
        assertThat(Modifier.isFinal(RestApiCatalog.class.getModifiers())).isTrue();
    }

    // =========================================================================
    // TemplateType ENUM TESTS
    // =========================================================================

    @Nested
    @DisplayName("TemplateType Enum Tests")
    class TemplateTypeEnumTests {

        @Test
        @DisplayName("should have expected number of template types")
        void should_have_expected_template_count() {
            assertThat(RestApiCatalog.TemplateType.values()).hasSize(EXPECTED_TEMPLATE_COUNT);
        }

        @Test
        @DisplayName("each template type should have non-null displayName")
        void each_template_type_should_have_non_null_displayName() {
            for (RestApiCatalog.TemplateType type : RestApiCatalog.TemplateType.values()) {
                assertThat(type.getDisplayName()).isNotNull().isNotBlank();
            }
        }

        @Test
        @DisplayName("each template type should have non-null description")
        void each_template_type_should_have_non_null_description() {
            for (RestApiCatalog.TemplateType type : RestApiCatalog.TemplateType.values()) {
                assertThat(type.getDescription()).isNotNull().isNotBlank();
            }
        }

        @Test
        @DisplayName("each template type should have non-null authType")
        void each_template_type_should_have_non_null_authType() {
            for (RestApiCatalog.TemplateType type : RestApiCatalog.TemplateType.values()) {
                assertThat(type.getAuthType()).isNotNull();
            }
        }

        @Test
        @DisplayName("JSON_PLACEHOLDER should not require credentials")
        void json_placeholder_should_not_require_credentials() {
            assertThat(RestApiCatalog.TemplateType.JSON_PLACEHOLDER.requiresCredentials()).isFalse();
        }

        @Test
        @DisplayName("GITHUB should require credentials")
        void github_should_require_credentials() {
            assertThat(RestApiCatalog.TemplateType.GITHUB.requiresCredentials()).isTrue();
        }
    }

    // =========================================================================
    // AuthType ENUM TESTS
    // =========================================================================

    @Nested
    @DisplayName("AuthType Enum Tests")
    class AuthTypeEnumTests {

        @Test
        @DisplayName("should have 6 auth types")
        void should_have_six_auth_types() {
            assertThat(RestApiCatalog.AuthType.values()).hasSize(6);
        }

        @Test
        @DisplayName("each auth type should have non-null value")
        void each_auth_type_should_have_non_null_value() {
            for (RestApiCatalog.AuthType type : RestApiCatalog.AuthType.values()) {
                assertThat(type.getValue()).isNotNull().isNotBlank();
            }
        }

        @Test
        @DisplayName("NONE should have value 'none'")
        void none_should_have_value_none() {
            assertThat(RestApiCatalog.AuthType.NONE.getValue()).isEqualTo("none");
        }

        @Test
        @DisplayName("BASIC should have value 'basic'")
        void basic_should_have_value_basic() {
            assertThat(RestApiCatalog.AuthType.BASIC.getValue()).isEqualTo("basic");
        }

        @Test
        @DisplayName("BEARER should have value 'bearer'")
        void bearer_should_have_value_bearer() {
            assertThat(RestApiCatalog.AuthType.BEARER.getValue()).isEqualTo("bearer");
        }

        @Test
        @DisplayName("API_KEY should have value 'api_key'")
        void api_key_should_have_value_api_key() {
            assertThat(RestApiCatalog.AuthType.API_KEY.getValue()).isEqualTo("api_key");
        }
    }

    // =========================================================================
    // DYNAMIC DISCOVERY METHOD TESTS
    // =========================================================================

    @Nested
    @DisplayName("Dynamic Discovery Method Tests")
    class DynamicDiscoveryTests {

        @Test
        @DisplayName("getAllTemplateNames should return expected count")
        void getAllTemplateNames_should_return_expected_count() {
            List<String> names = RestApiCatalog.getAllTemplateNames();

            assertThat(names).hasSize(EXPECTED_TEMPLATE_COUNT);
        }

        @Test
        @DisplayName("getAllTemplateNames should return unmodifiable list")
        void getAllTemplateNames_should_return_unmodifiable_list() {
            List<String> names = RestApiCatalog.getAllTemplateNames();

            assertThatThrownBy(() -> names.add("NEW"))
                    .isInstanceOf(UnsupportedOperationException.class);
        }

        @Test
        @DisplayName("getAllTemplateNames should contain JSONPlaceholder")
        void getAllTemplateNames_should_contain_jsonPlaceholder() {
            List<String> names = RestApiCatalog.getAllTemplateNames();

            assertThat(names).contains("JSONPlaceholder");
        }

        @Test
        @DisplayName("getAllTemplatesInfo should return expected count")
        void getAllTemplatesInfo_should_return_expected_count() {
            Map<String, String> info = RestApiCatalog.getAllTemplatesInfo();

            assertThat(info).hasSize(EXPECTED_TEMPLATE_COUNT);
        }

        @Test
        @DisplayName("getAllTemplatesInfo should return unmodifiable map")
        void getAllTemplatesInfo_should_return_unmodifiable_map() {
            Map<String, String> info = RestApiCatalog.getAllTemplatesInfo();

            assertThatThrownBy(() -> info.put("NEW", "description"))
                    .isInstanceOf(UnsupportedOperationException.class);
        }

        @Test
        @DisplayName("getAllTemplatesInfo should map displayName to description")
        void getAllTemplatesInfo_should_map_displayName_to_description() {
            Map<String, String> info = RestApiCatalog.getAllTemplatesInfo();

            assertThat(info.get("JSONPlaceholder")).contains("REST API");
        }

        @Test
        @DisplayName("getTemplatesRequiringCredentials should not be empty")
        void getTemplatesRequiringCredentials_should_not_be_empty() {
            List<RestApiCatalog.TemplateType> templates = RestApiCatalog.getTemplatesRequiringCredentials();

            assertThat(templates).isNotEmpty();
            assertThat(templates).allMatch(RestApiCatalog.TemplateType::requiresCredentials);
        }

        @Test
        @DisplayName("getTemplatesRequiringCredentials should return unmodifiable list")
        void getTemplatesRequiringCredentials_should_return_unmodifiable_list() {
            List<RestApiCatalog.TemplateType> templates = RestApiCatalog.getTemplatesRequiringCredentials();

            assertThatThrownBy(() -> templates.add(RestApiCatalog.TemplateType.JSON_PLACEHOLDER))
                    .isInstanceOf(UnsupportedOperationException.class);
        }

        @Test
        @DisplayName("getFreeTemplates should not be empty")
        void getFreeTemplates_should_not_be_empty() {
            List<RestApiCatalog.TemplateType> templates = RestApiCatalog.getFreeTemplates();

            assertThat(templates).isNotEmpty();
            assertThat(templates).noneMatch(RestApiCatalog.TemplateType::requiresCredentials);
        }

        @Test
        @DisplayName("getFreeTemplates should return unmodifiable list")
        void getFreeTemplates_should_return_unmodifiable_list() {
            List<RestApiCatalog.TemplateType> templates = RestApiCatalog.getFreeTemplates();

            assertThatThrownBy(() -> templates.add(RestApiCatalog.TemplateType.GITHUB))
                    .isInstanceOf(UnsupportedOperationException.class);
        }

        @Test
        @DisplayName("getTemplatesByAuthType should filter correctly for NONE")
        void getTemplatesByAuthType_should_filter_for_none() {
            List<RestApiCatalog.TemplateType> templates =
                    RestApiCatalog.getTemplatesByAuthType(RestApiCatalog.AuthType.NONE);

            assertThat(templates).isNotEmpty();
            assertThat(templates).allMatch(t -> t.getAuthType() == RestApiCatalog.AuthType.NONE);
        }

        @Test
        @DisplayName("getTemplatesByAuthType should return empty for null")
        void getTemplatesByAuthType_should_return_empty_for_null() {
            List<RestApiCatalog.TemplateType> templates = RestApiCatalog.getTemplatesByAuthType(null);

            assertThat(templates).isEmpty();
        }

        @Test
        @DisplayName("getTemplatesByAuthType should return unmodifiable list")
        void getTemplatesByAuthType_should_return_unmodifiable_list() {
            List<RestApiCatalog.TemplateType> templates =
                    RestApiCatalog.getTemplatesByAuthType(RestApiCatalog.AuthType.BASIC);

            assertThatThrownBy(() -> templates.add(RestApiCatalog.TemplateType.GITHUB))
                    .isInstanceOf(UnsupportedOperationException.class);
        }

        @Test
        @DisplayName("getTemplateCount should return expected count")
        void getTemplateCount_should_return_expected_count() {
            assertThat(RestApiCatalog.getTemplateCount()).isEqualTo(EXPECTED_TEMPLATE_COUNT);
        }

        @Test
        @DisplayName("free + credentials templates should equal total count")
        void free_plus_credentials_should_equal_total() {
            int freeCount = RestApiCatalog.getFreeTemplates().size();
            int credentialsCount = RestApiCatalog.getTemplatesRequiringCredentials().size();

            assertThat(freeCount + credentialsCount).isEqualTo(EXPECTED_TEMPLATE_COUNT);
        }
    }

    // =========================================================================
    // GENERIC TEMPLATE BUILDER TESTS
    // =========================================================================

    @Nested
    @DisplayName("Generic Template Builder Tests")
    class GenericTemplateBuilderTests {

        @Test
        @DisplayName("noAuth should create builder with NoAuthConfig")
        void noAuth_should_create_builder_with_noAuthConfig() {
            RestApiTemplate.Builder builder = RestApiCatalog.noAuth("test", "Test", "https://api.test.com");
            RestApiTemplate template = builder.build();

            assertThat(template.auth()).isInstanceOf(NoAuthConfig.class);
            assertThat(template.name()).isEqualTo("test");
            assertThat(template.baseUrl()).isEqualTo("https://api.test.com");
        }

        @Test
        @DisplayName("basicAuth should create builder with BasicAuthConfig")
        void basicAuth_should_create_builder_with_basicAuthConfig() {
            RestApiTemplate.Builder builder = RestApiCatalog.basicAuth(
                    "test", "Test", "https://api.test.com", "user", "pass");
            RestApiTemplate template = builder.build();

            assertThat(template.auth()).isInstanceOf(BasicAuthConfig.class);
            BasicAuthConfig auth = (BasicAuthConfig) template.auth();
            assertThat(auth.username()).isEqualTo("user");
            assertThat(auth.password()).isEqualTo("pass");
        }

        @Test
        @DisplayName("bearerAuth should create builder with BearerAuthConfig")
        void bearerAuth_should_create_builder_with_bearerAuthConfig() {
            RestApiTemplate.Builder builder = RestApiCatalog.bearerAuth(
                    "test", "Test", "https://api.test.com", "my-token");
            RestApiTemplate template = builder.build();

            assertThat(template.auth()).isInstanceOf(BearerAuthConfig.class);
            assertThat(((BearerAuthConfig) template.auth()).token()).isEqualTo("my-token");
        }

        @Test
        @DisplayName("apiKeyHeader should create builder with ApiKeyAuthConfig in HEADER")
        void apiKeyHeader_should_create_builder_with_header_location() {
            RestApiTemplate.Builder builder = RestApiCatalog.apiKeyHeader(
                    "test", "Test", "https://api.test.com", "X-API-Key", "abc123");
            RestApiTemplate template = builder.build();

            assertThat(template.auth()).isInstanceOf(ApiKeyAuthConfig.class);
            ApiKeyAuthConfig auth = (ApiKeyAuthConfig) template.auth();
            assertThat(auth.location()).isEqualTo(ApiKeyAuthConfig.Location.HEADER);
        }

        @Test
        @DisplayName("apiKeyQuery should create builder with ApiKeyAuthConfig in QUERY")
        void apiKeyQuery_should_create_builder_with_query_location() {
            RestApiTemplate.Builder builder = RestApiCatalog.apiKeyQuery(
                    "test", "Test", "https://api.test.com", "api_key", "abc123");
            RestApiTemplate template = builder.build();

            assertThat(template.auth()).isInstanceOf(ApiKeyAuthConfig.class);
            ApiKeyAuthConfig auth = (ApiKeyAuthConfig) template.auth();
            assertThat(auth.location()).isEqualTo(ApiKeyAuthConfig.Location.QUERY);
        }

        @Test
        @DisplayName("oauth2ClientCredentials should create builder with OAuth2ClientConfig")
        void oauth2ClientCredentials_should_create_builder_with_oauth2ClientConfig() {
            RestApiTemplate.Builder builder = RestApiCatalog.oauth2ClientCredentials(
                    "test", "Test", "https://api.test.com",
                    "https://auth.test.com/token", "client-id", "client-secret", "read write");
            RestApiTemplate template = builder.build();

            assertThat(template.auth()).isInstanceOf(OAuth2ClientConfig.class);
            OAuth2ClientConfig auth = (OAuth2ClientConfig) template.auth();
            assertThat(auth.clientId()).isEqualTo("client-id");
            assertThat(auth.scope()).isEqualTo("read write");
        }

        @Test
        @DisplayName("oauth2Password should create builder with OAuth2PasswordConfig")
        void oauth2Password_should_create_builder_with_oauth2PasswordConfig() {
            RestApiTemplate.Builder builder = RestApiCatalog.oauth2Password(
                    "test", "Test", "https://api.test.com",
                    "https://auth.test.com/token", "client-id", "user", "pass");
            RestApiTemplate template = builder.build();

            assertThat(template.auth()).isInstanceOf(OAuth2PasswordConfig.class);
            OAuth2PasswordConfig auth = (OAuth2PasswordConfig) template.auth();
            assertThat(auth.username()).isEqualTo("user");
        }
    }

    // =========================================================================
    // PREDEFINED TEMPLATE TESTS
    // =========================================================================

    @Nested
    @DisplayName("Predefined Template Tests")
    class PredefinedTemplateTests {

        @Test
        @DisplayName("jsonPlaceholder should create valid template")
        void jsonPlaceholder_should_create_valid_template() {
            RestApiTemplate template = RestApiCatalog.jsonPlaceholder();

            assertThat(template).isNotNull();
            assertThat(template.name()).isEqualTo("TEST_JSONPlaceholder_API");
            assertThat(template.baseUrl()).isEqualTo("https://jsonplaceholder.typicode.com");
            assertThat(template.auth()).isInstanceOf(NoAuthConfig.class);
            assertThat(template.methods()).isNotEmpty();
        }

        @Test
        @DisplayName("restCountries should create valid template")
        void restCountries_should_create_valid_template() {
            RestApiTemplate template = RestApiCatalog.restCountries();

            assertThat(template).isNotNull();
            assertThat(template.name()).isEqualTo("TEST_REST_Countries_API");
            assertThat(template.baseUrl()).isEqualTo("https://restcountries.com/v3.1");
            assertThat(template.auth()).isInstanceOf(NoAuthConfig.class);
        }

        @Test
        @DisplayName("openWeatherMap should create valid template with API key")
        void openWeatherMap_should_create_valid_template() {
            RestApiTemplate template = RestApiCatalog.openWeatherMap("my-api-key");

            assertThat(template).isNotNull();
            assertThat(template.auth()).isInstanceOf(ApiKeyAuthConfig.class);
            assertThat(((ApiKeyAuthConfig) template.auth()).keyValue()).isEqualTo("my-api-key");
        }

        @Test
        @DisplayName("nasaApod should create valid template with API key")
        void nasaApod_should_create_valid_template() {
            RestApiTemplate template = RestApiCatalog.nasaApod("DEMO_KEY");

            assertThat(template).isNotNull();
            assertThat(template.auth()).isInstanceOf(ApiKeyAuthConfig.class);
        }

        @Test
        @DisplayName("httpBinBasicAuth should create valid template")
        void httpBinBasicAuth_should_create_valid_template() {
            RestApiTemplate template = RestApiCatalog.httpBinBasicAuth();

            assertThat(template).isNotNull();
            assertThat(template.auth()).isInstanceOf(BasicAuthConfig.class);
        }

        @Test
        @DisplayName("bonitaRemote should create valid template")
        void bonitaRemote_should_create_valid_template() {
            RestApiTemplate template = RestApiCatalog.bonitaRemote(
                    "http://localhost:8080", "admin", "bpm");

            assertThat(template).isNotNull();
            assertThat(template.baseUrl()).isEqualTo("http://localhost:8080");
            assertThat(template.auth()).isInstanceOf(BasicAuthConfig.class);
            assertThat(template.verifySsl()).isFalse();
        }

        @Test
        @DisplayName("gitHub should create valid template with bearer token")
        void gitHub_should_create_valid_template() {
            RestApiTemplate template = RestApiCatalog.gitHub("ghp_token123");

            assertThat(template).isNotNull();
            assertThat(template.baseUrl()).isEqualTo("https://api.github.com");
            assertThat(template.auth()).isInstanceOf(BearerAuthConfig.class);
            assertThat(((BearerAuthConfig) template.auth()).token()).isEqualTo("ghp_token123");
        }
    }

    // =========================================================================
    // INTEGRATION TESTS - ALL TEMPLATES VALID JSON
    // =========================================================================

    @Nested
    @DisplayName("Integration Tests")
    class IntegrationTests {

        @Test
        @DisplayName("all predefined templates should serialize to valid JSON")
        void all_predefined_templates_should_serialize_to_valid_json() {
            RestApiTemplate[] templates = {
                    RestApiCatalog.jsonPlaceholder(),
                    RestApiCatalog.restCountries(),
                    RestApiCatalog.openWeatherMap("test-key"),
                    RestApiCatalog.nasaApod("DEMO_KEY"),
                    RestApiCatalog.httpBinBasicAuth(),
                    RestApiCatalog.bonitaRemote("http://localhost:8080", "admin", "bpm"),
                    RestApiCatalog.gitHub("test-token")
            };

            for (RestApiTemplate template : templates) {
                String json = template.toJsonString(MAPPER);
                assertThat(json).isNotNull().isNotBlank();
                assertThat(json).startsWith("{");
                assertThat(json).endsWith("}");
            }
        }

        @Test
        @DisplayName("all predefined templates should have unique names")
        void all_predefined_templates_should_have_unique_names() {
            RestApiTemplate[] templates = {
                    RestApiCatalog.jsonPlaceholder(),
                    RestApiCatalog.restCountries(),
                    RestApiCatalog.openWeatherMap("test-key"),
                    RestApiCatalog.nasaApod("DEMO_KEY"),
                    RestApiCatalog.httpBinBasicAuth(),
                    RestApiCatalog.bonitaRemote("http://localhost:8080", "admin", "bpm"),
                    RestApiCatalog.gitHub("test-token")
            };

            List<String> names = java.util.Arrays.stream(templates)
                    .map(RestApiTemplate::name)
                    .toList();

            assertThat(names).doesNotHaveDuplicates();
        }
    }
}
