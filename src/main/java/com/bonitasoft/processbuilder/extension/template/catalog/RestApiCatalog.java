package com.bonitasoft.processbuilder.extension.template.catalog;

import com.bonitasoft.processbuilder.extension.template.RestApiTemplate;
import com.bonitasoft.processbuilder.extension.template.auth.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Catalog of predefined REST API templates.
 * <p>
 * Provides factory methods for:
 * <ul>
 *   <li>Common APIs (OpenWeatherMap, NASA, GitHub, etc.)</li>
 *   <li>Generic templates for each authentication type</li>
 *   <li>Bonita BPM remote instance connectivity</li>
 * </ul>
 * </p>
 *
 * <p><b>Usage in Groovy script:</b></p>
 * <pre>{@code
 * import com.bonitasoft.processbuilder.extension.template.catalog.RestApiCatalog
 * import com.fasterxml.jackson.databind.ObjectMapper
 *
 * def mapper = new ObjectMapper()
 *
 * // Get template and convert to JSON string for configValue
 * def template = RestApiCatalog.openWeatherMap("your-api-key")
 * def configValue = template.toJsonString(mapper)
 * }</pre>
 *
 * @author Process Builder Team
 * @since 2025-02-06
 */
public final class RestApiCatalog {

    private RestApiCatalog() {}

    // ========================================================================
    // TEMPLATE TYPE ENUM - Registry of all available templates
    // ========================================================================

    /**
     * Enum representing all available REST API template types in the catalog.
     * <p>
     * This enum enables dynamic discovery of all templates without hardcoding.
     * When a new template is added to the catalog, add a corresponding enum constant here.
     * </p>
     */
    public enum TemplateType {
        /** JSONPlaceholder - Free fake REST API for testing */
        JSON_PLACEHOLDER("JSONPlaceholder", "Free fake REST API for testing - No authentication required", false, AuthType.NONE),
        /** REST Countries - Country information API */
        REST_COUNTRIES("RESTCountries", "Free public API for country information", false, AuthType.NONE),
        /** OpenWeatherMap - Weather data API */
        OPEN_WEATHER_MAP("OpenWeatherMap", "Real weather data API - Requires API key from openweathermap.org", true, AuthType.API_KEY),
        /** NASA APOD - Astronomy Picture of the Day */
        NASA_APOD("NASA_APOD", "NASA Astronomy Picture of the Day - Use DEMO_KEY for testing or get free key at api.nasa.gov", true, AuthType.API_KEY),
        /** HTTPBin - Basic Auth test service */
        HTTP_BIN_BASIC_AUTH("HTTPBin_BasicAuth", "HTTPBin test service for Basic Authentication testing", false, AuthType.BASIC),
        /** Bonita Remote - Connect to another Bonita instance */
        BONITA_REMOTE("Bonita_Remote", "Connect to a remote Bonita BPM instance via REST API", true, AuthType.BASIC),
        /** GitHub - GitHub REST API */
        GITHUB("GitHub", "GitHub REST API - Requires Personal Access Token from github.com/settings/tokens", true, AuthType.BEARER);

        private final String displayName;
        private final String description;
        private final boolean requiresCredentials;
        private final AuthType authType;

        TemplateType(String displayName, String description, boolean requiresCredentials, AuthType authType) {
            this.displayName = displayName;
            this.description = description;
            this.requiresCredentials = requiresCredentials;
            this.authType = authType;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getDescription() {
            return description;
        }

        public boolean requiresCredentials() {
            return requiresCredentials;
        }

        public AuthType getAuthType() {
            return authType;
        }
    }

    /**
     * Enum representing authentication types used by templates.
     */
    public enum AuthType {
        NONE("none"),
        BASIC("basic"),
        BEARER("bearer"),
        API_KEY("api_key"),
        OAUTH2_CLIENT("oauth2_client_credentials"),
        OAUTH2_PASSWORD("oauth2_password");

        private final String value;

        AuthType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    // ========================================================================
    // DYNAMIC DISCOVERY METHODS
    // ========================================================================

    /**
     * Returns all available template type names.
     *
     * @return Unmodifiable list of all template names
     */
    public static List<String> getAllTemplateNames() {
        return Arrays.stream(TemplateType.values())
                .map(TemplateType::getDisplayName)
                .collect(Collectors.toUnmodifiableList());
    }

    /**
     * Returns all template types as a map of displayName to description.
     *
     * @return Unmodifiable map with displayName as key and description as value
     */
    public static Map<String, String> getAllTemplatesInfo() {
        Map<String, String> info = Arrays.stream(TemplateType.values())
                .collect(Collectors.toMap(
                        TemplateType::getDisplayName,
                        TemplateType::getDescription,
                        (a, b) -> a,
                        LinkedHashMap::new
                ));
        return Collections.unmodifiableMap(info);
    }

    /**
     * Returns all template types that require user credentials.
     *
     * @return Unmodifiable list of templates requiring credentials
     */
    public static List<TemplateType> getTemplatesRequiringCredentials() {
        return Arrays.stream(TemplateType.values())
                .filter(TemplateType::requiresCredentials)
                .collect(Collectors.toUnmodifiableList());
    }

    /**
     * Returns all template types that are free (no credentials needed).
     *
     * @return Unmodifiable list of free templates
     */
    public static List<TemplateType> getFreeTemplates() {
        return Arrays.stream(TemplateType.values())
                .filter(t -> !t.requiresCredentials())
                .collect(Collectors.toUnmodifiableList());
    }

    /**
     * Returns all template types filtered by authentication type.
     *
     * @param authType The authentication type to filter by
     * @return Unmodifiable list of templates using the specified auth type
     */
    public static List<TemplateType> getTemplatesByAuthType(AuthType authType) {
        if (authType == null) {
            return List.of();
        }
        return Arrays.stream(TemplateType.values())
                .filter(t -> t.getAuthType() == authType)
                .collect(Collectors.toUnmodifiableList());
    }

    /**
     * Returns the total count of available templates.
     *
     * @return Number of templates in the catalog
     */
    public static int getTemplateCount() {
        return TemplateType.values().length;
    }

    // ========================================================================
    // GENERIC TEMPLATES FOR EACH AUTH TYPE
    // ========================================================================

    /**
     * Creates a generic template with NO authentication.
     *
     * @param name        Template name (fullName in PBConfiguration)
     * @param displayName Human-readable name
     * @param baseUrl     Base URL of the API
     * @return Template builder pre-configured with no auth
     */
    public static RestApiTemplate.Builder noAuth(String name, String displayName, String baseUrl) {
        return RestApiTemplate.builder()
                .name(name)
                .displayName(displayName)
                .baseUrl(baseUrl)
                .auth(NoAuthConfig.INSTANCE);
    }

    /**
     * Creates a generic template with BASIC authentication.
     *
     * @param name        Template name
     * @param displayName Human-readable name
     * @param baseUrl     Base URL of the API
     * @param username    Username for Basic auth
     * @param password    Password for Basic auth
     * @return Template builder pre-configured with Basic auth
     */
    public static RestApiTemplate.Builder basicAuth(String name, String displayName, String baseUrl,
                                                     String username, String password) {
        return RestApiTemplate.builder()
                .name(name)
                .displayName(displayName)
                .baseUrl(baseUrl)
                .auth(new BasicAuthConfig(username, password, true));
    }

    /**
     * Creates a generic template with BEARER token authentication.
     *
     * @param name        Template name
     * @param displayName Human-readable name
     * @param baseUrl     Base URL of the API
     * @param token       Bearer token
     * @return Template builder pre-configured with Bearer auth
     */
    public static RestApiTemplate.Builder bearerAuth(String name, String displayName, String baseUrl,
                                                      String token) {
        return RestApiTemplate.builder()
                .name(name)
                .displayName(displayName)
                .baseUrl(baseUrl)
                .auth(BearerAuthConfig.of(token));
    }

    /**
     * Creates a generic template with API KEY in HEADER.
     *
     * @param name        Template name
     * @param displayName Human-readable name
     * @param baseUrl     Base URL of the API
     * @param keyName     Header name for the API key
     * @param keyValue    API key value
     * @return Template builder pre-configured with API Key in header
     */
    public static RestApiTemplate.Builder apiKeyHeader(String name, String displayName, String baseUrl,
                                                        String keyName, String keyValue) {
        return RestApiTemplate.builder()
                .name(name)
                .displayName(displayName)
                .baseUrl(baseUrl)
                .auth(ApiKeyAuthConfig.header(keyName, keyValue));
    }

    /**
     * Creates a generic template with API KEY in QUERY parameter.
     *
     * @param name        Template name
     * @param displayName Human-readable name
     * @param baseUrl     Base URL of the API
     * @param keyName     Query parameter name for the API key
     * @param keyValue    API key value
     * @return Template builder pre-configured with API Key in query
     */
    public static RestApiTemplate.Builder apiKeyQuery(String name, String displayName, String baseUrl,
                                                       String keyName, String keyValue) {
        return RestApiTemplate.builder()
                .name(name)
                .displayName(displayName)
                .baseUrl(baseUrl)
                .auth(ApiKeyAuthConfig.queryParam(keyName, keyValue));
    }

    /**
     * Creates a generic template with OAuth2 Client Credentials.
     *
     * @param name         Template name
     * @param displayName  Human-readable name
     * @param baseUrl      Base URL of the API
     * @param tokenUrl     OAuth2 token endpoint
     * @param clientId     OAuth2 client ID
     * @param clientSecret OAuth2 client secret
     * @param scope        OAuth2 scope (can be null)
     * @return Template builder pre-configured with OAuth2 Client Credentials
     */
    public static RestApiTemplate.Builder oauth2ClientCredentials(String name, String displayName, String baseUrl,
                                                                   String tokenUrl, String clientId,
                                                                   String clientSecret, String scope) {
        return RestApiTemplate.builder()
                .name(name)
                .displayName(displayName)
                .baseUrl(baseUrl)
                .auth(new OAuth2ClientConfig(tokenUrl, clientId, clientSecret, scope));
    }

    /**
     * Creates a generic template with OAuth2 Password Grant.
     *
     * @param name         Template name
     * @param displayName  Human-readable name
     * @param baseUrl      Base URL of the API
     * @param tokenUrl     OAuth2 token endpoint
     * @param clientId     OAuth2 client ID
     * @param username     Resource owner username
     * @param password     Resource owner password
     * @return Template builder pre-configured with OAuth2 Password Grant
     */
    public static RestApiTemplate.Builder oauth2Password(String name, String displayName, String baseUrl,
                                                          String tokenUrl, String clientId,
                                                          String username, String password) {
        return RestApiTemplate.builder()
                .name(name)
                .displayName(displayName)
                .baseUrl(baseUrl)
                .auth(new OAuth2PasswordConfig(tokenUrl, clientId, username, password));
    }

    // ========================================================================
    // PREDEFINED TEMPLATES - PUBLIC APIS
    // ========================================================================

    /**
     * JSONPlaceholder - Free fake REST API for testing.
     */
    public static RestApiTemplate jsonPlaceholder() {
        return RestApiTemplate.builder()
                .name("TEST_JSONPlaceholder_API")
                .displayName("JSONPlaceholder Public API (Test)")
                .description("FREE public REST API for testing - No authentication required")
                .baseUrl("https://jsonplaceholder.typicode.com")
                .timeoutMs(10000)
                .auth(NoAuthConfig.INSTANCE)
                .addMethod("GetPosts", "GET", "/posts")
                .addMethod("GetPost", "GET", "/posts/1")
                .addMethod("CreatePost", "POST", "/posts")
                .addMethod("GetUsers", "GET", "/users")
                .addMethod("GetUser", "GET", "/users/1")
                .build();
    }

    /**
     * REST Countries API - Country information.
     */
    public static RestApiTemplate restCountries() {
        return RestApiTemplate.builder()
                .name("TEST_REST_Countries_API")
                .displayName("REST Countries API (Test)")
                .description("FREE public API for country information")
                .baseUrl("https://restcountries.com/v3.1")
                .timeoutMs(10000)
                .auth(NoAuthConfig.INSTANCE)
                .addMethod("GetAllCountries", "GET", "/all")
                .addMethod("GetCountryByName", "GET", "/name/spain")
                .addMethod("GetCountryByCode", "GET", "/alpha/ES")
                .build();
    }

    // ========================================================================
    // PREDEFINED TEMPLATES - API KEY AUTHENTICATION
    // ========================================================================

    /**
     * OpenWeatherMap API - Weather data.
     *
     * @param apiKey Your OpenWeatherMap API key (get free at openweathermap.org)
     */
    public static RestApiTemplate openWeatherMap(String apiKey) {
        return RestApiTemplate.builder()
                .name("TEST_OpenWeatherMap_API")
                .displayName("OpenWeatherMap API (Test - Requires API Key)")
                .description("Real weather data API. Sign up at openweathermap.org for free API key.")
                .baseUrl("https://api.openweathermap.org/data/2.5")
                .timeoutMs(15000)
                .auth(ApiKeyAuthConfig.queryParam("appid", apiKey))
                .addMethod("GetCurrentWeather", "GET", "/weather", Map.of("q", "Madrid,ES", "units", "metric"))
                .addMethod("GetForecast", "GET", "/forecast", Map.of("q", "Madrid,ES", "units", "metric"))
                .build();
    }

    /**
     * NASA APOD API - Astronomy Picture of the Day.
     *
     * @param apiKey Your NASA API key (use "DEMO_KEY" for testing)
     */
    public static RestApiTemplate nasaApod(String apiKey) {
        return RestApiTemplate.builder()
                .name("TEST_NASA_APOD")
                .displayName("NASA APOD API (Test - Requires API Key)")
                .description("NASA Astronomy Picture of the Day - Get free API key at api.nasa.gov")
                .baseUrl("https://api.nasa.gov")
                .timeoutMs(15000)
                .auth(ApiKeyAuthConfig.queryParam("api_key", apiKey))
                .addMethod("GetAPOD", "GET", "/planetary/apod")
                .build();
    }

    // ========================================================================
    // PREDEFINED TEMPLATES - BASIC AUTHENTICATION
    // ========================================================================

    /**
     * HTTPBin Basic Auth test service.
     */
    public static RestApiTemplate httpBinBasicAuth() {
        return RestApiTemplate.builder()
                .name("TEST_HTTPBin_BasicAuth")
                .displayName("HTTPBin Basic Auth (Test)")
                .description("HTTPBin test service for testing Basic Authentication")
                .baseUrl("https://httpbin.org")
                .timeoutMs(10000)
                .auth(new BasicAuthConfig("user", "pass", true))
                .addMethod("TestBasicAuth", "GET", "/basic-auth/user/pass")
                .addMethod("GetIP", "GET", "/ip")
                .addMethod("GetHeaders", "GET", "/headers")
                .build();
    }

    /**
     * Bonita BPM Remote Instance - Connect to another Bonita server.
     *
     * @param baseUrl  Bonita server URL (e.g., "http://localhost:8080")
     * @param username Bonita username
     * @param password Bonita password
     */
    public static RestApiTemplate bonitaRemote(String baseUrl, String username, String password) {
        return RestApiTemplate.builder()
                .name("TEST_Bonita_Remote")
                .displayName("Bonita BPM Remote Instance")
                .description("Connect to a remote Bonita BPM instance via REST API")
                .baseUrl(baseUrl)
                .timeoutMs(30000)
                .verifySsl(false)
                .auth(new BasicAuthConfig(username, password, true))
                .addMethod("GetCurrentUser", "GET", "/bonita/API/system/session/unusedId")
                .addMethod("ListUsers", "GET", "/bonita/API/identity/user", Map.of("p", "0", "c", "20"))
                .addMethod("ListProcesses", "GET", "/bonita/API/bpm/process", Map.of("p", "0", "c", "20"))
                .addMethod("ListCases", "GET", "/bonita/API/bpm/case", Map.of("p", "0", "c", "20"))
                .addMethod("ListTasks", "GET", "/bonita/API/bpm/humanTask", Map.of("p", "0", "c", "20"))
                .build();
    }

    // ========================================================================
    // PREDEFINED TEMPLATES - BEARER TOKEN
    // ========================================================================

    /**
     * GitHub API - Requires Personal Access Token.
     *
     * @param token GitHub Personal Access Token
     */
    public static RestApiTemplate gitHub(String token) {
        return RestApiTemplate.builder()
                .name("TEST_GitHub_API")
                .displayName("GitHub API (Test - Requires Token)")
                .description("GitHub REST API - Create token at github.com/settings/tokens")
                .baseUrl("https://api.github.com")
                .timeoutMs(15000)
                .auth(BearerAuthConfig.of(token))
                .addMethod("GetUser", "GET", "/user")
                .addMethod("GetRepos", "GET", "/user/repos", Map.of("per_page", "10", "sort", "updated"))
                .addMethod("GetPublicRepo", "GET", "/repos/anthropics/claude-code")
                .build();
    }
}
