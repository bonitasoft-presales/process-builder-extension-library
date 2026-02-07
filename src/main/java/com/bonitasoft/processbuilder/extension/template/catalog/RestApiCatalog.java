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
        // =====================================================================
        // PUBLIC/TEST APIs (No credentials or free tier)
        // =====================================================================
        /** JSONPlaceholder - Free fake REST API for testing */
        JSON_PLACEHOLDER("JSONPlaceholder", "Free fake REST API for testing - No authentication required", false, AuthType.NONE),
        /** REST Countries - Country information API */
        REST_COUNTRIES("RESTCountries", "Free public API for country information", false, AuthType.NONE),
        /** HTTPBin - Basic Auth test service */
        HTTP_BIN_BASIC_AUTH("HTTPBin_BasicAuth", "HTTPBin test service for Basic Authentication testing", false, AuthType.BASIC),

        // =====================================================================
        // API KEY Authentication
        // =====================================================================
        /** OpenWeatherMap - Weather data API */
        OPEN_WEATHER_MAP("OpenWeatherMap", "Real weather data API - Requires API key from openweathermap.org", true, AuthType.API_KEY),
        /** NASA APOD - Astronomy Picture of the Day */
        NASA_APOD("NASA_APOD", "NASA Astronomy Picture of the Day - Use DEMO_KEY for testing or get free key at api.nasa.gov", true, AuthType.API_KEY),
        /** HubSpot - CRM and Marketing Platform */
        HUBSPOT("HubSpot", "HubSpot CRM API - Requires API key from app.hubspot.com/api-key", true, AuthType.API_KEY),
        /** SendGrid - Email delivery service */
        SENDGRID("SendGrid", "SendGrid Email API - Requires API key from app.sendgrid.com/settings/api_keys", true, AuthType.API_KEY),

        // =====================================================================
        // BASIC Authentication
        // =====================================================================
        /** Bonita Remote - Connect to another Bonita instance */
        BONITA_REMOTE("Bonita_Remote", "Connect to a remote Bonita BPM instance via REST API", true, AuthType.BASIC),
        /** Jira - Atlassian Jira issue tracking */
        JIRA("Jira", "Atlassian Jira REST API - Use email + API token from id.atlassian.com/manage-profile/security/api-tokens", true, AuthType.BASIC),
        /** Zendesk - Customer service platform */
        ZENDESK("Zendesk", "Zendesk Support API - Use email/token authentication", true, AuthType.BASIC),

        // =====================================================================
        // BEARER Token Authentication
        // =====================================================================
        /** GitHub - GitHub REST API */
        GITHUB("GitHub", "GitHub REST API - Requires Personal Access Token from github.com/settings/tokens", true, AuthType.BEARER),
        /** Slack - Team communication platform */
        SLACK("Slack", "Slack Web API - Requires Bot Token from api.slack.com/apps", true, AuthType.BEARER),
        /** Notion - Workspace and documentation platform */
        NOTION("Notion", "Notion API - Requires Integration Token from notion.so/my-integrations", true, AuthType.BEARER),

        // =====================================================================
        // OAuth2 Client Credentials (Enterprise/SI)
        // =====================================================================
        /** Salesforce - CRM Platform */
        SALESFORCE("Salesforce", "Salesforce REST API - Requires Connected App OAuth2 credentials", true, AuthType.OAUTH2_CLIENT),
        /** SAP S/4HANA - ERP System */
        SAP_S4HANA("SAP_S4HANA", "SAP S/4HANA OData API - Requires OAuth2 client credentials from SAP BTP", true, AuthType.OAUTH2_CLIENT),
        /** SAP Business One - SMB ERP */
        SAP_BUSINESS_ONE("SAP_BusinessOne", "SAP Business One Service Layer API - Requires session-based authentication", true, AuthType.BASIC),
        /** Microsoft Dynamics 365 - ERP/CRM */
        DYNAMICS_365("Dynamics365", "Microsoft Dynamics 365 Web API - Requires Azure AD OAuth2 credentials", true, AuthType.OAUTH2_CLIENT),
        /** ServiceNow - IT Service Management */
        SERVICENOW("ServiceNow", "ServiceNow REST API - Supports Basic Auth or OAuth2", true, AuthType.OAUTH2_CLIENT),
        /** Workday - HCM Platform */
        WORKDAY("Workday", "Workday REST API - Requires OAuth2 client credentials", true, AuthType.OAUTH2_CLIENT),

        // =====================================================================
        // OAuth2 Password Grant
        // =====================================================================
        /** DocuSign - Electronic signatures */
        DOCUSIGN("DocuSign", "DocuSign eSignature API - Requires OAuth2 credentials from admin.docusign.com", true, AuthType.OAUTH2_PASSWORD);

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

    /**
     * Slack Web API - Team communication.
     *
     * @param botToken Slack Bot Token (xoxb-...)
     */
    public static RestApiTemplate slack(String botToken) {
        return RestApiTemplate.builder()
                .name("Slack_API")
                .displayName("Slack Web API")
                .description("Slack messaging API - Create app at api.slack.com/apps")
                .baseUrl("https://slack.com/api")
                .timeoutMs(15000)
                .auth(BearerAuthConfig.of(botToken))
                .addMethod("PostMessage", "POST", "/chat.postMessage")
                .addMethod("ListChannels", "GET", "/conversations.list")
                .addMethod("GetUserInfo", "GET", "/users.info")
                .build();
    }

    /**
     * Notion API - Workspace platform.
     *
     * @param integrationToken Notion Integration Token
     */
    public static RestApiTemplate notion(String integrationToken) {
        return RestApiTemplate.builder()
                .name("Notion_API")
                .displayName("Notion API")
                .description("Notion workspace API - Create integration at notion.so/my-integrations")
                .baseUrl("https://api.notion.com/v1")
                .timeoutMs(15000)
                .header("Notion-Version", "2022-06-28")
                .auth(BearerAuthConfig.of(integrationToken))
                .addMethod("ListDatabases", "POST", "/search")
                .addMethod("GetPage", "GET", "/pages/{page_id}")
                .addMethod("QueryDatabase", "POST", "/databases/{database_id}/query")
                .build();
    }

    // ========================================================================
    // PREDEFINED TEMPLATES - API KEY (Enterprise)
    // ========================================================================

    /**
     * HubSpot CRM API.
     *
     * @param apiKey HubSpot API key
     */
    public static RestApiTemplate hubSpot(String apiKey) {
        return RestApiTemplate.builder()
                .name("HubSpot_API")
                .displayName("HubSpot CRM API")
                .description("HubSpot CRM and Marketing API")
                .baseUrl("https://api.hubapi.com")
                .timeoutMs(20000)
                .auth(ApiKeyAuthConfig.header("Authorization", "Bearer " + apiKey))
                .addMethod("GetContacts", "GET", "/crm/v3/objects/contacts")
                .addMethod("GetCompanies", "GET", "/crm/v3/objects/companies")
                .addMethod("GetDeals", "GET", "/crm/v3/objects/deals")
                .addMethod("CreateContact", "POST", "/crm/v3/objects/contacts")
                .build();
    }

    /**
     * SendGrid Email API.
     *
     * @param apiKey SendGrid API key
     */
    public static RestApiTemplate sendGrid(String apiKey) {
        return RestApiTemplate.builder()
                .name("SendGrid_API")
                .displayName("SendGrid Email API")
                .description("SendGrid email delivery service")
                .baseUrl("https://api.sendgrid.com/v3")
                .timeoutMs(15000)
                .auth(BearerAuthConfig.of(apiKey))
                .addMethod("SendEmail", "POST", "/mail/send")
                .addMethod("GetStats", "GET", "/stats")
                .build();
    }

    // ========================================================================
    // PREDEFINED TEMPLATES - BASIC AUTH (Enterprise)
    // ========================================================================

    /**
     * Jira REST API - Atlassian issue tracking.
     *
     * @param domain   Jira domain (e.g., "yourcompany.atlassian.net")
     * @param email    User email
     * @param apiToken API token from id.atlassian.com
     */
    public static RestApiTemplate jira(String domain, String email, String apiToken) {
        return RestApiTemplate.builder()
                .name("Jira_API")
                .displayName("Jira REST API")
                .description("Atlassian Jira issue tracking and project management")
                .baseUrl("https://" + domain + "/rest/api/3")
                .timeoutMs(20000)
                .auth(new BasicAuthConfig(email, apiToken, true))
                .addMethod("GetProjects", "GET", "/project")
                .addMethod("SearchIssues", "GET", "/search", Map.of("jql", "project=DEMO"))
                .addMethod("GetIssue", "GET", "/issue/{issueKey}")
                .addMethod("CreateIssue", "POST", "/issue")
                .build();
    }

    /**
     * Zendesk Support API.
     *
     * @param subdomain Zendesk subdomain (e.g., "yourcompany")
     * @param email     Agent email
     * @param apiToken  API token from Admin > Channels > API
     */
    public static RestApiTemplate zendesk(String subdomain, String email, String apiToken) {
        return RestApiTemplate.builder()
                .name("Zendesk_API")
                .displayName("Zendesk Support API")
                .description("Zendesk customer service platform")
                .baseUrl("https://" + subdomain + ".zendesk.com/api/v2")
                .timeoutMs(20000)
                .auth(new BasicAuthConfig(email + "/token", apiToken, true))
                .addMethod("GetTickets", "GET", "/tickets.json")
                .addMethod("GetUsers", "GET", "/users.json")
                .addMethod("CreateTicket", "POST", "/tickets.json")
                .build();
    }

    /**
     * SAP Business One Service Layer.
     *
     * @param baseUrl  SAP B1 Service Layer URL (e.g., "https://server:50000/b1s/v1")
     * @param username SAP B1 username
     * @param password SAP B1 password
     */
    public static RestApiTemplate sapBusinessOne(String baseUrl, String username, String password) {
        return RestApiTemplate.builder()
                .name("SAP_BusinessOne_API")
                .displayName("SAP Business One Service Layer")
                .description("SAP Business One REST API for SMB ERP")
                .baseUrl(baseUrl)
                .timeoutMs(30000)
                .verifySsl(false)
                .auth(new BasicAuthConfig(username, password, true))
                .addMethod("Login", "POST", "/Login")
                .addMethod("GetBusinessPartners", "GET", "/BusinessPartners")
                .addMethod("GetItems", "GET", "/Items")
                .addMethod("GetOrders", "GET", "/Orders")
                .build();
    }

    // ========================================================================
    // PREDEFINED TEMPLATES - OAuth2 (Enterprise SI)
    // ========================================================================

    /**
     * Salesforce REST API.
     *
     * @param instanceUrl  Salesforce instance URL (e.g., "https://yourorg.my.salesforce.com")
     * @param clientId     Connected App Consumer Key
     * @param clientSecret Connected App Consumer Secret
     */
    public static RestApiTemplate salesforce(String instanceUrl, String clientId, String clientSecret) {
        return RestApiTemplate.builder()
                .name("Salesforce_API")
                .displayName("Salesforce REST API")
                .description("Salesforce CRM platform API")
                .baseUrl(instanceUrl + "/services/data/v58.0")
                .timeoutMs(30000)
                .auth(new OAuth2ClientConfig(
                        instanceUrl + "/services/oauth2/token",
                        clientId, clientSecret, null))
                .addMethod("GetAccount", "GET", "/sobjects/Account/{id}")
                .addMethod("QuerySOQL", "GET", "/query", Map.of("q", "SELECT Id, Name FROM Account LIMIT 10"))
                .addMethod("CreateLead", "POST", "/sobjects/Lead")
                .addMethod("GetOpportunities", "GET", "/sobjects/Opportunity")
                .build();
    }

    /**
     * SAP S/4HANA OData API.
     *
     * @param baseUrl      SAP S/4HANA API endpoint
     * @param tokenUrl     OAuth2 token URL from SAP BTP
     * @param clientId     OAuth2 client ID
     * @param clientSecret OAuth2 client secret
     */
    public static RestApiTemplate sapS4Hana(String baseUrl, String tokenUrl, String clientId, String clientSecret) {
        return RestApiTemplate.builder()
                .name("SAP_S4HANA_API")
                .displayName("SAP S/4HANA OData API")
                .description("SAP S/4HANA Cloud ERP API via OAuth2")
                .baseUrl(baseUrl)
                .timeoutMs(30000)
                .auth(new OAuth2ClientConfig(tokenUrl, clientId, clientSecret, null))
                .addMethod("GetBusinessPartners", "GET", "/sap/opu/odata/sap/API_BUSINESS_PARTNER/A_BusinessPartner")
                .addMethod("GetSalesOrders", "GET", "/sap/opu/odata/sap/API_SALES_ORDER_SRV/A_SalesOrder")
                .addMethod("GetProducts", "GET", "/sap/opu/odata/sap/API_PRODUCT_SRV/A_Product")
                .build();
    }

    /**
     * Microsoft Dynamics 365 Web API.
     *
     * @param orgUrl       Dynamics 365 organization URL (e.g., "https://yourorg.crm.dynamics.com")
     * @param tenantId     Azure AD tenant ID
     * @param clientId     Azure AD application (client) ID
     * @param clientSecret Azure AD client secret
     */
    public static RestApiTemplate dynamics365(String orgUrl, String tenantId, String clientId, String clientSecret) {
        return RestApiTemplate.builder()
                .name("Dynamics365_API")
                .displayName("Microsoft Dynamics 365 Web API")
                .description("Dynamics 365 CRM/ERP platform API")
                .baseUrl(orgUrl + "/api/data/v9.2")
                .timeoutMs(30000)
                .auth(new OAuth2ClientConfig(
                        "https://login.microsoftonline.com/" + tenantId + "/oauth2/v2.0/token",
                        clientId, clientSecret, orgUrl + "/.default"))
                .addMethod("GetAccounts", "GET", "/accounts")
                .addMethod("GetContacts", "GET", "/contacts")
                .addMethod("GetOpportunities", "GET", "/opportunities")
                .addMethod("WhoAmI", "GET", "/WhoAmI")
                .build();
    }

    /**
     * ServiceNow REST API.
     *
     * @param instanceUrl  ServiceNow instance URL (e.g., "https://yourinstance.service-now.com")
     * @param clientId     OAuth2 client ID
     * @param clientSecret OAuth2 client secret
     */
    public static RestApiTemplate serviceNow(String instanceUrl, String clientId, String clientSecret) {
        return RestApiTemplate.builder()
                .name("ServiceNow_API")
                .displayName("ServiceNow REST API")
                .description("ServiceNow IT Service Management platform")
                .baseUrl(instanceUrl + "/api/now")
                .timeoutMs(30000)
                .auth(new OAuth2ClientConfig(
                        instanceUrl + "/oauth_token.do",
                        clientId, clientSecret, null))
                .addMethod("GetIncidents", "GET", "/table/incident")
                .addMethod("GetUsers", "GET", "/table/sys_user")
                .addMethod("CreateIncident", "POST", "/table/incident")
                .addMethod("GetChangeRequests", "GET", "/table/change_request")
                .build();
    }

    /**
     * Workday REST API.
     *
     * @param tenant       Workday tenant name
     * @param tokenUrl     OAuth2 token endpoint
     * @param clientId     OAuth2 client ID
     * @param clientSecret OAuth2 client secret
     */
    public static RestApiTemplate workday(String tenant, String tokenUrl, String clientId, String clientSecret) {
        return RestApiTemplate.builder()
                .name("Workday_API")
                .displayName("Workday REST API")
                .description("Workday HCM platform API")
                .baseUrl("https://wd2-impl-services1.workday.com/ccx/api/v1/" + tenant)
                .timeoutMs(30000)
                .auth(new OAuth2ClientConfig(tokenUrl, clientId, clientSecret, null))
                .addMethod("GetWorkers", "GET", "/workers")
                .addMethod("GetOrganizations", "GET", "/organizations")
                .build();
    }

    /**
     * DocuSign eSignature API.
     *
     * @param accountId    DocuSign account ID
     * @param tokenUrl     OAuth2 token URL
     * @param clientId     Integration Key (client ID)
     * @param userId       User ID (for JWT)
     * @param privateKey   RSA private key
     */
    public static RestApiTemplate docuSign(String accountId, String tokenUrl, String clientId,
                                            String userId, String privateKey) {
        return RestApiTemplate.builder()
                .name("DocuSign_API")
                .displayName("DocuSign eSignature API")
                .description("DocuSign electronic signature platform")
                .baseUrl("https://demo.docusign.net/restapi/v2.1/accounts/" + accountId)
                .timeoutMs(30000)
                .auth(new OAuth2PasswordConfig(tokenUrl, clientId, userId, privateKey))
                .addMethod("GetEnvelopes", "GET", "/envelopes")
                .addMethod("CreateEnvelope", "POST", "/envelopes")
                .addMethod("GetTemplates", "GET", "/templates")
                .build();
    }
}
