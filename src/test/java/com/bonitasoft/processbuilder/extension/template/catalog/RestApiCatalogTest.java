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
    private static final int EXPECTED_TEMPLATE_COUNT = 20;

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

        @Test
        @DisplayName("slack should create valid template with bearer token")
        void slack_should_create_valid_template() {
            RestApiTemplate template = RestApiCatalog.slack("xoxb-token");

            assertThat(template).isNotNull();
            assertThat(template.name()).isEqualTo("Slack_API");
            assertThat(template.baseUrl()).isEqualTo("https://slack.com/api");
            assertThat(template.auth()).isInstanceOf(BearerAuthConfig.class);
        }

        @Test
        @DisplayName("notion should create valid template with bearer token")
        void notion_should_create_valid_template() {
            RestApiTemplate template = RestApiCatalog.notion("secret_token");

            assertThat(template).isNotNull();
            assertThat(template.name()).isEqualTo("Notion_API");
            assertThat(template.baseUrl()).isEqualTo("https://api.notion.com/v1");
            assertThat(template.auth()).isInstanceOf(BearerAuthConfig.class);
            assertThat(template.headers()).containsKey("Notion-Version");
        }

        @Test
        @DisplayName("hubSpot should create valid template with API key")
        void hubSpot_should_create_valid_template() {
            RestApiTemplate template = RestApiCatalog.hubSpot("api-key-123");

            assertThat(template).isNotNull();
            assertThat(template.name()).isEqualTo("HubSpot_API");
            assertThat(template.baseUrl()).isEqualTo("https://api.hubapi.com");
            assertThat(template.auth()).isInstanceOf(ApiKeyAuthConfig.class);
        }

        @Test
        @DisplayName("sendGrid should create valid template with bearer token")
        void sendGrid_should_create_valid_template() {
            RestApiTemplate template = RestApiCatalog.sendGrid("SG.api-key");

            assertThat(template).isNotNull();
            assertThat(template.name()).isEqualTo("SendGrid_API");
            assertThat(template.baseUrl()).isEqualTo("https://api.sendgrid.com/v3");
            assertThat(template.auth()).isInstanceOf(BearerAuthConfig.class);
        }

        @Test
        @DisplayName("jira should create valid template with basic auth")
        void jira_should_create_valid_template() {
            RestApiTemplate template = RestApiCatalog.jira("company.atlassian.net", "user@email.com", "api-token");

            assertThat(template).isNotNull();
            assertThat(template.name()).isEqualTo("Jira_API");
            assertThat(template.baseUrl()).isEqualTo("https://company.atlassian.net/rest/api/3");
            assertThat(template.auth()).isInstanceOf(BasicAuthConfig.class);
            BasicAuthConfig auth = (BasicAuthConfig) template.auth();
            assertThat(auth.username()).isEqualTo("user@email.com");
        }

        @Test
        @DisplayName("zendesk should create valid template with basic auth")
        void zendesk_should_create_valid_template() {
            RestApiTemplate template = RestApiCatalog.zendesk("company", "agent@email.com", "api-token");

            assertThat(template).isNotNull();
            assertThat(template.name()).isEqualTo("Zendesk_API");
            assertThat(template.baseUrl()).isEqualTo("https://company.zendesk.com/api/v2");
            assertThat(template.auth()).isInstanceOf(BasicAuthConfig.class);
        }

        @Test
        @DisplayName("sapBusinessOne should create valid template with basic auth")
        void sapBusinessOne_should_create_valid_template() {
            RestApiTemplate template = RestApiCatalog.sapBusinessOne(
                    "https://sap-server:50000/b1s/v1", "manager", "password");

            assertThat(template).isNotNull();
            assertThat(template.name()).isEqualTo("SAP_BusinessOne_API");
            assertThat(template.auth()).isInstanceOf(BasicAuthConfig.class);
            assertThat(template.verifySsl()).isFalse();
        }

        @Test
        @DisplayName("salesforce should create valid template with OAuth2")
        void salesforce_should_create_valid_template() {
            RestApiTemplate template = RestApiCatalog.salesforce(
                    "https://myorg.my.salesforce.com", "client-id", "client-secret");

            assertThat(template).isNotNull();
            assertThat(template.name()).isEqualTo("Salesforce_API");
            assertThat(template.baseUrl()).contains("salesforce.com");
            assertThat(template.auth()).isInstanceOf(OAuth2ClientConfig.class);
            OAuth2ClientConfig auth = (OAuth2ClientConfig) template.auth();
            assertThat(auth.clientId()).isEqualTo("client-id");
        }

        @Test
        @DisplayName("sapS4Hana should create valid template with OAuth2")
        void sapS4Hana_should_create_valid_template() {
            RestApiTemplate template = RestApiCatalog.sapS4Hana(
                    "https://sap-api.example.com", "https://auth.sap.com/token", "client-id", "client-secret");

            assertThat(template).isNotNull();
            assertThat(template.name()).isEqualTo("SAP_S4HANA_API");
            assertThat(template.auth()).isInstanceOf(OAuth2ClientConfig.class);
        }

        @Test
        @DisplayName("dynamics365 should create valid template with OAuth2")
        void dynamics365_should_create_valid_template() {
            RestApiTemplate template = RestApiCatalog.dynamics365(
                    "https://myorg.crm.dynamics.com", "tenant-id", "client-id", "client-secret");

            assertThat(template).isNotNull();
            assertThat(template.name()).isEqualTo("Dynamics365_API");
            assertThat(template.baseUrl()).contains("dynamics.com");
            assertThat(template.auth()).isInstanceOf(OAuth2ClientConfig.class);
            OAuth2ClientConfig auth = (OAuth2ClientConfig) template.auth();
            assertThat(auth.tokenUrl()).contains("login.microsoftonline.com");
        }

        @Test
        @DisplayName("serviceNow should create valid template with OAuth2")
        void serviceNow_should_create_valid_template() {
            RestApiTemplate template = RestApiCatalog.serviceNow(
                    "https://instance.service-now.com", "client-id", "client-secret");

            assertThat(template).isNotNull();
            assertThat(template.name()).isEqualTo("ServiceNow_API");
            assertThat(template.baseUrl()).contains("service-now.com");
            assertThat(template.auth()).isInstanceOf(OAuth2ClientConfig.class);
        }

        @Test
        @DisplayName("workday should create valid template with OAuth2")
        void workday_should_create_valid_template() {
            RestApiTemplate template = RestApiCatalog.workday(
                    "tenant123", "https://auth.workday.com/token", "client-id", "client-secret");

            assertThat(template).isNotNull();
            assertThat(template.name()).isEqualTo("Workday_API");
            assertThat(template.baseUrl()).contains("workday.com");
            assertThat(template.auth()).isInstanceOf(OAuth2ClientConfig.class);
        }

        @Test
        @DisplayName("docuSign should create valid template with OAuth2 Password")
        void docuSign_should_create_valid_template() {
            RestApiTemplate template = RestApiCatalog.docuSign(
                    "account-id", "https://auth.docusign.com/token", "client-id", "user-id", "private-key");

            assertThat(template).isNotNull();
            assertThat(template.name()).isEqualTo("DocuSign_API");
            assertThat(template.baseUrl()).contains("docusign.net");
            assertThat(template.auth()).isInstanceOf(OAuth2PasswordConfig.class);
        }
    }

    // =========================================================================
    // TEMPLATE DEFINITIONS TESTS
    // =========================================================================

    @Nested
    @DisplayName("Template Definitions Tests")
    class TemplateDefinitionsTests {

        private static final int EXPECTED_TEMPLATE_DEFINITION_COUNT = 31;

        @Test
        @DisplayName("getAllTemplateDefinitions should return expected count")
        void getAllTemplateDefinitions_should_return_expected_count() {
            List<RestApiTemplate> definitions = RestApiCatalog.getAllTemplateDefinitions();

            assertThat(definitions).hasSize(EXPECTED_TEMPLATE_DEFINITION_COUNT);
        }

        @Test
        @DisplayName("getAllTemplateDefinitions should return unmodifiable list")
        void getAllTemplateDefinitions_should_return_unmodifiable_list() {
            List<RestApiTemplate> definitions = RestApiCatalog.getAllTemplateDefinitions();

            assertThatThrownBy(() -> definitions.add(RestApiCatalog.jsonPlaceholder()))
                    .isInstanceOf(UnsupportedOperationException.class);
        }

        @Test
        @DisplayName("all template definitions should have isTemplate=true")
        void all_template_definitions_should_have_isTemplate_true() {
            for (RestApiTemplate template : RestApiCatalog.getAllTemplateDefinitions()) {
                assertThat(template.isTemplate())
                        .as("Template %s should have isTemplate=true", template.name())
                        .isTrue();
            }
        }

        @Test
        @DisplayName("all template definitions should have templateVersion")
        void all_template_definitions_should_have_templateVersion() {
            for (RestApiTemplate template : RestApiCatalog.getAllTemplateDefinitions()) {
                assertThat(template.templateVersion())
                        .as("Template %s should have templateVersion", template.name())
                        .isNotNull()
                        .isNotBlank();
            }
        }

        @Test
        @DisplayName("all template definitions should have unique names")
        void all_template_definitions_should_have_unique_names() {
            List<String> names = RestApiCatalog.getAllTemplateDefinitions().stream()
                    .map(RestApiTemplate::name)
                    .toList();

            assertThat(names).doesNotHaveDuplicates();
        }

        @Test
        @DisplayName("all template definitions should have methods")
        void all_template_definitions_should_have_methods() {
            for (RestApiTemplate template : RestApiCatalog.getAllTemplateDefinitions()) {
                assertThat(template.methods())
                        .as("Template %s should have at least one method", template.name())
                        .isNotEmpty();
            }
        }

        @Test
        @DisplayName("all template definitions should serialize to valid JSON")
        void all_template_definitions_should_serialize_to_valid_json() {
            for (RestApiTemplate template : RestApiCatalog.getAllTemplateDefinitions()) {
                String json = template.toJsonString(MAPPER);
                assertThat(json)
                        .as("Template %s should serialize to valid JSON", template.name())
                        .isNotNull()
                        .isNotBlank()
                        .startsWith("{")
                        .endsWith("}");
            }
        }

        @Test
        @DisplayName("all template definitions JSON should include isTemplate field")
        void all_template_definitions_json_should_include_isTemplate_field() throws Exception {
            for (RestApiTemplate template : RestApiCatalog.getAllTemplateDefinitions()) {
                String json = template.toJsonString(MAPPER);
                assertThat(json)
                        .as("Template %s JSON should include isTemplate", template.name())
                        .contains("\"isTemplate\":true");
            }
        }
    }

    // =========================================================================
    // GENERIC AUTH TYPE TEMPLATES TESTS
    // =========================================================================

    @Nested
    @DisplayName("Generic Auth Type Template Tests")
    class GenericAuthTypeTemplateTests {

        @Test
        @DisplayName("templateNoAuth should create valid template")
        void templateNoAuth_should_create_valid_template() {
            RestApiTemplate template = RestApiCatalog.templateNoAuth();

            assertThat(template).isNotNull();
            assertThat(template.name()).isEqualTo("TEMPLATE_REST_API_NO_AUTH");
            assertThat(template.isTemplate()).isTrue();
            assertThat(template.auth()).isInstanceOf(NoAuthConfig.class);
            assertThat(template.requiredFields()).contains("baseUrl");
            assertThat(template.baseUrl()).isEqualTo("{{baseUrl}}");
        }

        @Test
        @DisplayName("templateBasicAuth should create valid template")
        void templateBasicAuth_should_create_valid_template() {
            RestApiTemplate template = RestApiCatalog.templateBasicAuth();

            assertThat(template).isNotNull();
            assertThat(template.name()).isEqualTo("TEMPLATE_REST_API_BASIC_AUTH");
            assertThat(template.isTemplate()).isTrue();
            assertThat(template.auth()).isInstanceOf(BasicAuthConfig.class);
            assertThat(template.requiredFields()).contains("baseUrl", "username", "password");
        }

        @Test
        @DisplayName("templateBearerToken should create valid template")
        void templateBearerToken_should_create_valid_template() {
            RestApiTemplate template = RestApiCatalog.templateBearerToken();

            assertThat(template).isNotNull();
            assertThat(template.name()).isEqualTo("TEMPLATE_REST_API_BEARER_TOKEN");
            assertThat(template.isTemplate()).isTrue();
            assertThat(template.auth()).isInstanceOf(BearerAuthConfig.class);
            assertThat(template.requiredFields()).contains("baseUrl", "token");
        }

        @Test
        @DisplayName("templateApiKey should create valid template")
        void templateApiKey_should_create_valid_template() {
            RestApiTemplate template = RestApiCatalog.templateApiKey();

            assertThat(template).isNotNull();
            assertThat(template.name()).isEqualTo("TEMPLATE_REST_API_KEY");
            assertThat(template.isTemplate()).isTrue();
            assertThat(template.auth()).isInstanceOf(ApiKeyAuthConfig.class);
            assertThat(template.requiredFields()).contains("baseUrl", "keyName", "keyValue");
        }

        @Test
        @DisplayName("templateOAuth2ClientCredentials should create valid template")
        void templateOAuth2ClientCredentials_should_create_valid_template() {
            RestApiTemplate template = RestApiCatalog.templateOAuth2ClientCredentials();

            assertThat(template).isNotNull();
            assertThat(template.name()).isEqualTo("TEMPLATE_OAUTH2_CLIENT_CREDENTIALS");
            assertThat(template.isTemplate()).isTrue();
            assertThat(template.auth()).isInstanceOf(OAuth2ClientConfig.class);
            assertThat(template.requiredFields()).contains("baseUrl", "tokenUrl", "clientId", "clientSecret");
        }

        @Test
        @DisplayName("templateOAuth2Password should create valid template")
        void templateOAuth2Password_should_create_valid_template() {
            RestApiTemplate template = RestApiCatalog.templateOAuth2Password();

            assertThat(template).isNotNull();
            assertThat(template.name()).isEqualTo("TEMPLATE_OAUTH2_PASSWORD");
            assertThat(template.isTemplate()).isTrue();
            assertThat(template.auth()).isInstanceOf(OAuth2PasswordConfig.class);
            assertThat(template.requiredFields()).contains("baseUrl", "tokenUrl", "clientId", "username", "password");
        }

        @Test
        @DisplayName("templateCustomHeaders should create valid template")
        void templateCustomHeaders_should_create_valid_template() {
            RestApiTemplate template = RestApiCatalog.templateCustomHeaders();

            assertThat(template).isNotNull();
            assertThat(template.name()).isEqualTo("TEMPLATE_REST_API_CUSTOM_HEADERS");
            assertThat(template.isTemplate()).isTrue();
            assertThat(template.auth()).isInstanceOf(NoAuthConfig.class);
            assertThat(template.requiredFields()).contains("baseUrl", "authValue");
            assertThat(template.headers()).containsKey("X-Custom-Auth");
            assertThat(template.headers()).containsKey("X-API-Version");
        }
    }

    // =========================================================================
    // SERVICE-SPECIFIC TEMPLATES TESTS
    // =========================================================================

    @Nested
    @DisplayName("Service-Specific Template Tests")
    class ServiceSpecificTemplateTests {

        @Test
        @DisplayName("templateJsonPlaceholder should create valid template")
        void templateJsonPlaceholder_should_create_valid_template() {
            RestApiTemplate template = RestApiCatalog.templateJsonPlaceholder();

            assertThat(template).isNotNull();
            assertThat(template.name()).isEqualTo("TEMPLATE_JSONPlaceholder");
            assertThat(template.isTemplate()).isTrue();
            assertThat(template.baseUrl()).isEqualTo("https://jsonplaceholder.typicode.com");
            assertThat(template.methods()).hasSizeGreaterThan(5);
        }

        @Test
        @DisplayName("templateRestCountries should create valid template")
        void templateRestCountries_should_create_valid_template() {
            RestApiTemplate template = RestApiCatalog.templateRestCountries();

            assertThat(template).isNotNull();
            assertThat(template.name()).isEqualTo("TEMPLATE_RESTCountries");
            assertThat(template.isTemplate()).isTrue();
            assertThat(template.baseUrl()).contains("restcountries.com");
        }

        @Test
        @DisplayName("templateOpenWeatherMap should create valid template")
        void templateOpenWeatherMap_should_create_valid_template() {
            RestApiTemplate template = RestApiCatalog.templateOpenWeatherMap();

            assertThat(template).isNotNull();
            assertThat(template.name()).isEqualTo("TEMPLATE_OpenWeatherMap");
            assertThat(template.isTemplate()).isTrue();
            assertThat(template.auth()).isInstanceOf(ApiKeyAuthConfig.class);
            assertThat(template.requiredFields()).contains("keyValue");
        }

        @Test
        @DisplayName("templateExchangeRate should create valid template")
        void templateExchangeRate_should_create_valid_template() {
            RestApiTemplate template = RestApiCatalog.templateExchangeRate();

            assertThat(template).isNotNull();
            assertThat(template.name()).isEqualTo("TEMPLATE_ExchangeRate");
            assertThat(template.isTemplate()).isTrue();
            assertThat(template.baseUrl()).contains("exchangerate-api.com");
        }

        @Test
        @DisplayName("templateIPGeolocation should create valid template")
        void templateIPGeolocation_should_create_valid_template() {
            RestApiTemplate template = RestApiCatalog.templateIPGeolocation();

            assertThat(template).isNotNull();
            assertThat(template.name()).isEqualTo("TEMPLATE_IPGeolocation");
            assertThat(template.isTemplate()).isTrue();
            assertThat(template.baseUrl()).contains("ip-api.com");
        }

        @Test
        @DisplayName("templateCompaniesHouseUK should create valid template")
        void templateCompaniesHouseUK_should_create_valid_template() {
            RestApiTemplate template = RestApiCatalog.templateCompaniesHouseUK();

            assertThat(template).isNotNull();
            assertThat(template.name()).isEqualTo("TEMPLATE_CompaniesHouseUK");
            assertThat(template.isTemplate()).isTrue();
            assertThat(template.auth()).isInstanceOf(BasicAuthConfig.class);
            assertThat(template.requiredFields()).contains("username");
        }

        @Test
        @DisplayName("templateVIESVat should create valid template")
        void templateVIESVat_should_create_valid_template() {
            RestApiTemplate template = RestApiCatalog.templateVIESVat();

            assertThat(template).isNotNull();
            assertThat(template.name()).isEqualTo("TEMPLATE_VIES_VAT");
            assertThat(template.isTemplate()).isTrue();
            assertThat(template.baseUrl()).contains("ec.europa.eu");
        }

        @Test
        @DisplayName("templateClearbit should create valid template")
        void templateClearbit_should_create_valid_template() {
            RestApiTemplate template = RestApiCatalog.templateClearbit();

            assertThat(template).isNotNull();
            assertThat(template.name()).isEqualTo("TEMPLATE_Clearbit");
            assertThat(template.isTemplate()).isTrue();
            assertThat(template.auth()).isInstanceOf(BearerAuthConfig.class);
            assertThat(template.requiredFields()).contains("token");
        }

        @Test
        @DisplayName("templateAbstractEmailValidation should create valid template")
        void templateAbstractEmailValidation_should_create_valid_template() {
            RestApiTemplate template = RestApiCatalog.templateAbstractEmailValidation();

            assertThat(template).isNotNull();
            assertThat(template.name()).isEqualTo("TEMPLATE_AbstractEmailValidation");
            assertThat(template.isTemplate()).isTrue();
            assertThat(template.auth()).isInstanceOf(ApiKeyAuthConfig.class);
        }

        @Test
        @DisplayName("templateNumVerify should create valid template")
        void templateNumVerify_should_create_valid_template() {
            RestApiTemplate template = RestApiCatalog.templateNumVerify();

            assertThat(template).isNotNull();
            assertThat(template.name()).isEqualTo("TEMPLATE_NumVerify");
            assertThat(template.isTemplate()).isTrue();
            assertThat(template.auth()).isInstanceOf(ApiKeyAuthConfig.class);
        }

        @Test
        @DisplayName("templateAlphaVantage should create valid template")
        void templateAlphaVantage_should_create_valid_template() {
            RestApiTemplate template = RestApiCatalog.templateAlphaVantage();

            assertThat(template).isNotNull();
            assertThat(template.name()).isEqualTo("TEMPLATE_AlphaVantage");
            assertThat(template.isTemplate()).isTrue();
            assertThat(template.auth()).isInstanceOf(ApiKeyAuthConfig.class);
            assertThat(template.methods()).hasSizeGreaterThan(3);
        }

        @Test
        @DisplayName("templateSapS4Hana should create valid template")
        void templateSapS4Hana_should_create_valid_template() {
            RestApiTemplate template = RestApiCatalog.templateSapS4Hana();

            assertThat(template).isNotNull();
            assertThat(template.name()).isEqualTo("TEMPLATE_SAP_S4HANA");
            assertThat(template.isTemplate()).isTrue();
            assertThat(template.auth()).isInstanceOf(BasicAuthConfig.class);
            assertThat(template.requiredFields()).contains("sapHost", "sapClient", "username", "password");
            assertThat(template.headers()).containsKey("sap-client");
        }

        @Test
        @DisplayName("templateHubSpot should create valid template")
        void templateHubSpot_should_create_valid_template() {
            RestApiTemplate template = RestApiCatalog.templateHubSpot();

            assertThat(template).isNotNull();
            assertThat(template.name()).isEqualTo("TEMPLATE_HubSpot");
            assertThat(template.isTemplate()).isTrue();
            assertThat(template.auth()).isInstanceOf(BearerAuthConfig.class);
            assertThat(template.requiredFields()).contains("token");
        }

        @Test
        @DisplayName("templateMicrosoftGraph should create valid template")
        void templateMicrosoftGraph_should_create_valid_template() {
            RestApiTemplate template = RestApiCatalog.templateMicrosoftGraph();

            assertThat(template).isNotNull();
            assertThat(template.name()).isEqualTo("TEMPLATE_Microsoft_Graph");
            assertThat(template.isTemplate()).isTrue();
            assertThat(template.auth()).isInstanceOf(OAuth2ClientConfig.class);
            assertThat(template.requiredFields()).contains("tenantId", "clientId", "clientSecret");
        }

        @Test
        @DisplayName("templateServiceNow should create valid template")
        void templateServiceNow_should_create_valid_template() {
            RestApiTemplate template = RestApiCatalog.templateServiceNow();

            assertThat(template).isNotNull();
            assertThat(template.name()).isEqualTo("TEMPLATE_ServiceNow");
            assertThat(template.isTemplate()).isTrue();
            assertThat(template.auth()).isInstanceOf(BasicAuthConfig.class);
            assertThat(template.requiredFields()).contains("instanceName", "username", "password");
        }

        @Test
        @DisplayName("templateDocuSign should create valid template")
        void templateDocuSign_should_create_valid_template() {
            RestApiTemplate template = RestApiCatalog.templateDocuSign();

            assertThat(template).isNotNull();
            assertThat(template.name()).isEqualTo("TEMPLATE_DocuSign");
            assertThat(template.isTemplate()).isTrue();
            assertThat(template.auth()).isInstanceOf(OAuth2ClientConfig.class);
            assertThat(template.requiredFields()).contains("environment", "accountId", "clientId", "clientSecret");
        }

        @Test
        @DisplayName("templateStripe should create valid template")
        void templateStripe_should_create_valid_template() {
            RestApiTemplate template = RestApiCatalog.templateStripe();

            assertThat(template).isNotNull();
            assertThat(template.name()).isEqualTo("TEMPLATE_Stripe");
            assertThat(template.isTemplate()).isTrue();
            assertThat(template.auth()).isInstanceOf(BearerAuthConfig.class);
            assertThat(template.requiredFields()).contains("token");
            assertThat(template.headers()).containsEntry("Content-Type", "application/x-www-form-urlencoded");
        }

        @Test
        @DisplayName("templateSlack should create valid template")
        void templateSlack_should_create_valid_template() {
            RestApiTemplate template = RestApiCatalog.templateSlack();

            assertThat(template).isNotNull();
            assertThat(template.name()).isEqualTo("TEMPLATE_Slack");
            assertThat(template.isTemplate()).isTrue();
            assertThat(template.auth()).isInstanceOf(BearerAuthConfig.class);
            assertThat(template.baseUrl()).isEqualTo("https://slack.com/api");
        }

        @Test
        @DisplayName("templateGoogleDrive should create valid template")
        void templateGoogleDrive_should_create_valid_template() {
            RestApiTemplate template = RestApiCatalog.templateGoogleDrive();

            assertThat(template).isNotNull();
            assertThat(template.name()).isEqualTo("TEMPLATE_Google_Drive");
            assertThat(template.isTemplate()).isTrue();
            assertThat(template.auth()).isInstanceOf(OAuth2ClientConfig.class);
            assertThat(template.requiredFields()).contains("clientId", "clientSecret");
        }

        @Test
        @DisplayName("templateJira should create valid template")
        void templateJira_should_create_valid_template() {
            RestApiTemplate template = RestApiCatalog.templateJira();

            assertThat(template).isNotNull();
            assertThat(template.name()).isEqualTo("TEMPLATE_Jira");
            assertThat(template.isTemplate()).isTrue();
            assertThat(template.auth()).isInstanceOf(BasicAuthConfig.class);
            assertThat(template.requiredFields()).contains("domain", "username", "password");
        }

        @Test
        @DisplayName("templateOneDrive should create valid template")
        void templateOneDrive_should_create_valid_template() {
            RestApiTemplate template = RestApiCatalog.templateOneDrive();

            assertThat(template).isNotNull();
            assertThat(template.name()).isEqualTo("TEMPLATE_Microsoft_OneDrive");
            assertThat(template.isTemplate()).isTrue();
            assertThat(template.auth()).isInstanceOf(OAuth2ClientConfig.class);
            assertThat(template.requiredFields()).contains("tenantId", "clientId", "clientSecret");
        }

        @Test
        @DisplayName("templateSalesforce should create valid template")
        void templateSalesforce_should_create_valid_template() {
            RestApiTemplate template = RestApiCatalog.templateSalesforce();

            assertThat(template).isNotNull();
            assertThat(template.name()).isEqualTo("TEMPLATE_Salesforce");
            assertThat(template.isTemplate()).isTrue();
            assertThat(template.auth()).isInstanceOf(OAuth2ClientConfig.class);
            assertThat(template.requiredFields()).contains("instanceUrl", "consumerKey", "consumerSecret");
        }

        @Test
        @DisplayName("templateAlfresco should create valid template")
        void templateAlfresco_should_create_valid_template() {
            RestApiTemplate template = RestApiCatalog.templateAlfresco();

            assertThat(template).isNotNull();
            assertThat(template.name()).isEqualTo("TEMPLATE_Alfresco");
            assertThat(template.isTemplate()).isTrue();
            assertThat(template.auth()).isInstanceOf(BasicAuthConfig.class);
            assertThat(template.requiredFields()).contains("alfrescoHost", "username", "password");
        }

        @Test
        @DisplayName("templateBonitaUniversal should create valid template with 30 methods")
        void templateBonitaUniversal_should_create_valid_template() {
            RestApiTemplate template = RestApiCatalog.templateBonitaUniversal();

            assertThat(template).isNotNull();
            assertThat(template.name()).isEqualTo("TEMPLATE_Bonita_Universal");
            assertThat(template.isTemplate()).isTrue();
            assertThat(template.auth()).isInstanceOf(BasicAuthConfig.class);
            assertThat(template.requiredFields()).contains("bonitaUrl", "username", "password");
            assertThat(template.methods()).hasSize(30);
            assertThat(template.verifySsl()).isFalse();
        }
    }

    // =========================================================================
    // INTEGRATION TESTS - ALL TEMPLATES VALID JSON
    // =========================================================================

    @Nested
    @DisplayName("Integration Tests")
    class IntegrationTests {

        private RestApiTemplate[] getAllPredefinedTemplates() {
            return new RestApiTemplate[] {
                    // Public APIs (No auth)
                    RestApiCatalog.jsonPlaceholder(),
                    RestApiCatalog.restCountries(),
                    // Basic Auth
                    RestApiCatalog.httpBinBasicAuth(),
                    RestApiCatalog.bonitaRemote("http://localhost:8080", "admin", "bpm"),
                    RestApiCatalog.jira("company.atlassian.net", "user@email.com", "token"),
                    RestApiCatalog.zendesk("company", "agent@email.com", "token"),
                    RestApiCatalog.sapBusinessOne("https://sap:50000/b1s/v1", "user", "pass"),
                    // API Key
                    RestApiCatalog.openWeatherMap("test-key"),
                    RestApiCatalog.nasaApod("DEMO_KEY"),
                    RestApiCatalog.hubSpot("api-key"),
                    // Bearer
                    RestApiCatalog.gitHub("test-token"),
                    RestApiCatalog.slack("xoxb-token"),
                    RestApiCatalog.notion("secret_token"),
                    RestApiCatalog.sendGrid("SG.api-key"),
                    // OAuth2 Client Credentials
                    RestApiCatalog.salesforce("https://org.salesforce.com", "client", "secret"),
                    RestApiCatalog.sapS4Hana("https://api.sap.com", "https://auth.sap.com/token", "client", "secret"),
                    RestApiCatalog.dynamics365("https://org.crm.dynamics.com", "tenant", "client", "secret"),
                    RestApiCatalog.serviceNow("https://instance.service-now.com", "client", "secret"),
                    RestApiCatalog.workday("tenant", "https://auth.workday.com/token", "client", "secret"),
                    // OAuth2 Password
                    RestApiCatalog.docuSign("account", "https://auth.docusign.com/token", "client", "user", "key")
            };
        }

        @Test
        @DisplayName("all predefined templates should serialize to valid JSON")
        void all_predefined_templates_should_serialize_to_valid_json() {
            for (RestApiTemplate template : getAllPredefinedTemplates()) {
                String json = template.toJsonString(MAPPER);
                assertThat(json)
                        .as("Template %s should serialize to valid JSON", template.name())
                        .isNotNull()
                        .isNotBlank()
                        .startsWith("{")
                        .endsWith("}");
            }
        }

        @Test
        @DisplayName("all predefined templates should have unique names")
        void all_predefined_templates_should_have_unique_names() {
            List<String> names = java.util.Arrays.stream(getAllPredefinedTemplates())
                    .map(RestApiTemplate::name)
                    .toList();

            assertThat(names).doesNotHaveDuplicates();
        }

        @Test
        @DisplayName("predefined template count should match TemplateType count")
        void predefined_template_count_should_match_templateType_count() {
            assertThat(getAllPredefinedTemplates()).hasSize(EXPECTED_TEMPLATE_COUNT);
        }

        @Test
        @DisplayName("all templates should have at least one method")
        void all_templates_should_have_at_least_one_method() {
            for (RestApiTemplate template : getAllPredefinedTemplates()) {
                assertThat(template.methods())
                        .as("Template %s should have at least one method", template.name())
                        .isNotEmpty();
            }
        }

        @Test
        @DisplayName("all templates should have valid timeout")
        void all_templates_should_have_valid_timeout() {
            for (RestApiTemplate template : getAllPredefinedTemplates()) {
                assertThat(template.timeoutMs())
                        .as("Template %s should have positive timeout", template.name())
                        .isPositive();
            }
        }

        @Test
        @DisplayName("all templates should have non-blank baseUrl")
        void all_templates_should_have_non_blank_baseUrl() {
            for (RestApiTemplate template : getAllPredefinedTemplates()) {
                assertThat(template.baseUrl())
                        .as("Template %s should have non-blank baseUrl", template.name())
                        .isNotBlank();
            }
        }
    }
}
