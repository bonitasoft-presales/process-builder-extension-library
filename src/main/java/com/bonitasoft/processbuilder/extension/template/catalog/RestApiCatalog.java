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
 *
 * <p>Provides factory methods for:</p>
 * <ul>
 *   <li>Common APIs (OpenWeatherMap, NASA, GitHub, etc.)</li>
 *   <li>Generic templates for each authentication type</li>
 *   <li>Bonita BPM remote instance connectivity</li>
 * </ul>
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

    // ========================================================================
    // TEMPLATE DEFINITIONS - Templates with placeholders for UI configuration
    // ========================================================================

    /**
     * Returns all template definitions as a list.
     * <p>
     * These templates include placeholder values ({{baseUrl}}, {{username}}, etc.)
     * that users must fill in when creating actual API configurations.
     * </p>
     *
     * @return List of all template definitions
     */
    public static List<RestApiTemplate> getAllTemplateDefinitions() {
        return List.of(
                // Generic auth type templates
                templateNoAuth(),
                templateBasicAuth(),
                templateBearerToken(),
                templateApiKey(),
                templateOAuth2ClientCredentials(),
                templateOAuth2Password(),
                templateCustomHeaders(),
                // Service-specific templates
                templateJsonPlaceholder(),
                templateRestCountries(),
                templateOpenWeatherMap(),
                templateExchangeRate(),
                templateIPGeolocation(),
                templateCompaniesHouseUK(),
                templateVIESVat(),
                templateClearbit(),
                templateAbstractEmailValidation(),
                templateNumVerify(),
                templateAlphaVantage(),
                templateSapS4Hana(),
                templateHubSpot(),
                templateMicrosoftGraph(),
                templateServiceNow(),
                templateDocuSign(),
                templateStripe(),
                templateSlack(),
                templateGoogleDrive(),
                templateJira(),
                templateOneDrive(),
                templateSalesforce(),
                templateAlfresco(),
                templateBonitaUniversal()
        );
    }

    // ========================================================================
    // GENERIC AUTH TYPE TEMPLATES (with placeholders)
    // ========================================================================

    /**
     * Template: REST API - No Authentication.
     * Generic template for public APIs without authentication.
     */
    public static RestApiTemplate templateNoAuth() {
        return RestApiTemplate.builder()
                .name("TEMPLATE_REST_API_NO_AUTH")
                .displayName("REST API - No Authentication")
                .description("Public REST APIs without authentication")
                .baseUrl("{{baseUrl}}")
                .timeoutMs(30000)
                .asTemplate()
                .requiredFields("baseUrl")
                .auth(NoAuthConfig.INSTANCE)
                .addMethod("GetResource", "Get Resource", "Retrieve resource without auth", "GET", "{{resourcePath}}", null, null)
                .addMethod("CreateResource", "Create Resource", "Create resource without auth", "POST", "{{resourcePath}}", null, "{{requestBody}}")
                .build();
    }

    /**
     * Template: REST API - Basic Authentication.
     * Generic template for APIs using HTTP Basic Auth.
     */
    public static RestApiTemplate templateBasicAuth() {
        return RestApiTemplate.builder()
                .name("TEMPLATE_REST_API_BASIC_AUTH")
                .displayName("REST API - Basic Authentication")
                .description("REST APIs using HTTP Basic Auth")
                .baseUrl("{{baseUrl}}")
                .timeoutMs(30000)
                .asTemplate()
                .requiredFields("baseUrl", "username", "password")
                .auth(new BasicAuthConfig("{{username}}", "{{password}}", true))
                .addMethod("GetResource", "Get Resource", "Retrieve resource with Basic Auth", "GET", "{{resourcePath}}", null, null)
                .addMethod("CreateResource", "Create Resource", "Create resource with Basic Auth", "POST", "{{resourcePath}}", null, "{{requestBody}}")
                .build();
    }

    /**
     * Template: REST API - Bearer Token.
     * Generic template for APIs using Bearer token authentication.
     */
    public static RestApiTemplate templateBearerToken() {
        return RestApiTemplate.builder()
                .name("TEMPLATE_REST_API_BEARER_TOKEN")
                .displayName("REST API - Bearer Token")
                .description("REST APIs using Bearer Token (JWT)")
                .baseUrl("{{baseUrl}}")
                .timeoutMs(30000)
                .asTemplate()
                .requiredFields("baseUrl", "token")
                .auth(BearerAuthConfig.of("{{token}}"))
                .addMethod("GetResource", "Get Resource", "Retrieve resource with Bearer Token", "GET", "{{resourcePath}}", null, null)
                .addMethod("CreateResource", "Create Resource", "Create resource with Bearer Token", "POST", "{{resourcePath}}", null, "{{requestBody}}")
                .build();
    }

    /**
     * Template: REST API - API Key.
     * Generic template for APIs using API Key authentication.
     */
    public static RestApiTemplate templateApiKey() {
        return RestApiTemplate.builder()
                .name("TEMPLATE_REST_API_KEY")
                .displayName("REST API - API Key")
                .description("REST APIs using API Key authentication")
                .baseUrl("{{baseUrl}}")
                .timeoutMs(30000)
                .asTemplate()
                .requiredFields("baseUrl", "keyName", "keyValue")
                .auth(ApiKeyAuthConfig.header("{{keyName}}", "{{keyValue}}"))
                .addMethod("GetResource", "Get Resource", "Retrieve resource with API Key", "GET", "{{resourcePath}}", null, null)
                .addMethod("CreateResource", "Create Resource", "Create resource with API Key", "POST", "{{resourcePath}}", null, "{{requestBody}}")
                .build();
    }

    /**
     * Template: REST API - OAuth2 Client Credentials.
     * Generic template for APIs using OAuth2 Client Credentials flow (M2M).
     */
    public static RestApiTemplate templateOAuth2ClientCredentials() {
        return RestApiTemplate.builder()
                .name("TEMPLATE_OAUTH2_CLIENT_CREDENTIALS")
                .displayName("REST API - OAuth2 Client Credentials")
                .description("REST APIs using OAuth2 Client Credentials (M2M)")
                .baseUrl("{{baseUrl}}")
                .timeoutMs(30000)
                .asTemplate()
                .requiredFields("baseUrl", "tokenUrl", "clientId", "clientSecret")
                .auth(new OAuth2ClientConfig("{{tokenUrl}}", "{{clientId}}", "{{clientSecret}}", null))
                .addMethod("GetResource", "Get Resource", "Retrieve resource with OAuth2", "GET", "{{resourcePath}}", null, null)
                .addMethod("CreateResource", "Create Resource", "Create resource with OAuth2", "POST", "{{resourcePath}}", null, "{{requestBody}}")
                .build();
    }

    /**
     * Template: REST API - OAuth2 Password Grant.
     * Generic template for APIs using OAuth2 Password Grant flow.
     */
    public static RestApiTemplate templateOAuth2Password() {
        return RestApiTemplate.builder()
                .name("TEMPLATE_OAUTH2_PASSWORD")
                .displayName("REST API - OAuth2 Password Grant")
                .description("REST APIs using OAuth2 Password flow")
                .baseUrl("{{baseUrl}}")
                .timeoutMs(30000)
                .asTemplate()
                .requiredFields("baseUrl", "tokenUrl", "clientId", "username", "password")
                .auth(new OAuth2PasswordConfig("{{tokenUrl}}", "{{clientId}}", "{{username}}", "{{password}}"))
                .addMethod("GetResource", "Get Resource", "Retrieve resource with OAuth2 Password", "GET", "{{resourcePath}}", null, null)
                .addMethod("CreateResource", "Create Resource", "Create resource with OAuth2 Password", "POST", "{{resourcePath}}", null, "{{requestBody}}")
                .build();
    }

    /**
     * Template: REST API - Custom Headers.
     * Generic template for APIs using custom authentication headers.
     */
    public static RestApiTemplate templateCustomHeaders() {
        return RestApiTemplate.builder()
                .name("TEMPLATE_REST_API_CUSTOM_HEADERS")
                .displayName("REST API - Custom Headers")
                .description("REST APIs using custom authentication headers")
                .baseUrl("{{baseUrl}}")
                .timeoutMs(30000)
                .asTemplate()
                .requiredFields("baseUrl", "authValue")
                .header("X-Custom-Auth", "{{authValue}}")
                .header("X-API-Version", "{{apiVersion}}")
                .auth(NoAuthConfig.INSTANCE)
                .addMethod("GetResource", "Get Resource", "Retrieve with custom headers", "GET", "{{resourcePath}}", null, null)
                .addMethod("CreateResource", "Create Resource", "Create with custom headers", "POST", "{{resourcePath}}", null, "{{requestBody}}")
                .build();
    }

    // ========================================================================
    // SERVICE-SPECIFIC TEMPLATES (with real URLs but placeholder credentials)
    // ========================================================================

    /**
     * Template: JSONPlaceholder - Free test API.
     */
    public static RestApiTemplate templateJsonPlaceholder() {
        return RestApiTemplate.builder()
                .name("TEMPLATE_JSONPlaceholder")
                .displayName("JSONPlaceholder (Test API)")
                .description("Free test API - No authentication required")
                .baseUrl("https://jsonplaceholder.typicode.com")
                .timeoutMs(10000)
                .asTemplate()
                .auth(NoAuthConfig.INSTANCE)
                .addMethod("GetPosts", "Get All Posts", "Retrieve all posts", "GET", "/posts", null, null)
                .addMethod("GetPost", "Get Post", "Retrieve post by ID", "GET", "/posts/{{postId}}", null, null)
                .addMethod("CreatePost", "Create Post", "Create a new post", "POST", "/posts", null, "{\"title\":\"{{title}}\",\"body\":\"{{body}}\",\"userId\":{{userId}}}")
                .addMethod("UpdatePost", "Update Post", "Update existing post", "PUT", "/posts/{{postId}}", null, "{\"id\":{{postId}},\"title\":\"{{title}}\",\"body\":\"{{body}}\",\"userId\":{{userId}}}")
                .addMethod("PatchPost", "Patch Post", "Partially update post", "PATCH", "/posts/{{postId}}", null, "{\"title\":\"{{title}}\"}")
                .addMethod("DeletePost", "Delete Post", "Delete a post", "DELETE", "/posts/{{postId}}", null, null)
                .addMethod("GetUsers", "Get Users", "Retrieve all users", "GET", "/users", null, null)
                .addMethod("GetUser", "Get User", "Retrieve user by ID", "GET", "/users/{{userId}}", null, null)
                .addMethod("GetComments", "Get Comments", "Retrieve all comments", "GET", "/comments", null, null)
                .addMethod("GetPostComments", "Post Comments", "Comments for a post", "GET", "/posts/{{postId}}/comments", null, null)
                .addMethod("GetTodos", "Get Todos", "Retrieve all todos", "GET", "/todos", null, null)
                .addMethod("GetAlbums", "Get Albums", "Retrieve all albums", "GET", "/albums", null, null)
                .addMethod("GetPhotos", "Get Photos", "Retrieve all photos", "GET", "/photos", null, null)
                .build();
    }

    /**
     * Template: REST Countries - Free countries API.
     */
    public static RestApiTemplate templateRestCountries() {
        return RestApiTemplate.builder()
                .name("TEMPLATE_RESTCountries")
                .displayName("REST Countries API")
                .description("Free public API for country information - No authentication required")
                .baseUrl("https://restcountries.com/v3.1")
                .timeoutMs(15000)
                .asTemplate()
                .auth(NoAuthConfig.INSTANCE)
                .addMethod("GetAllCountries", "All Countries", "Get all countries", "GET", "/all", null, null)
                .addMethod("GetCountryByName", "By Name", "Search country by name", "GET", "/name/{{countryName}}", null, null)
                .addMethod("GetCountryByCode", "By Code", "Get country by alpha code", "GET", "/alpha/{{countryCode}}", null, null)
                .addMethod("GetCountriesByRegion", "By Region", "Get countries by region", "GET", "/region/{{region}}", null, null)
                .addMethod("GetCountriesByCurrency", "By Currency", "Get countries by currency", "GET", "/currency/{{currency}}", null, null)
                .addMethod("GetCountriesByLanguage", "By Language", "Get countries by language", "GET", "/lang/{{language}}", null, null)
                .addMethod("GetCountriesByCapital", "By Capital", "Get countries by capital", "GET", "/capital/{{capital}}", null, null)
                .build();
    }

    /**
     * Template: OpenWeatherMap - Weather data API.
     */
    public static RestApiTemplate templateOpenWeatherMap() {
        return RestApiTemplate.builder()
                .name("TEMPLATE_OpenWeatherMap")
                .displayName("OpenWeatherMap API")
                .description("Real weather data - Requires free API key from openweathermap.org")
                .baseUrl("https://api.openweathermap.org/data/2.5")
                .timeoutMs(15000)
                .asTemplate()
                .requiredFields("keyValue")
                .auth(ApiKeyAuthConfig.queryParam("appid", "{{keyValue}}"))
                .addMethod("GetCurrentWeather", "Current Weather", "Weather by city", "GET", "/weather", Map.of("q", "{{city}}", "units", "{{units}}"), null)
                .addMethod("GetWeatherByCoords", "Weather by Coords", "Weather by lat/lon", "GET", "/weather", Map.of("lat", "{{latitude}}", "lon", "{{longitude}}", "units", "{{units}}"), null)
                .addMethod("Get5DayForecast", "5-Day Forecast", "5-day forecast by city", "GET", "/forecast", Map.of("q", "{{city}}", "units", "{{units}}"), null)
                .addMethod("GetAirPollution", "Air Pollution", "Air quality data", "GET", "/air_pollution", Map.of("lat", "{{latitude}}", "lon", "{{longitude}}"), null)
                .build();
    }

    /**
     * Template: ExchangeRate-API - Free currency rates.
     */
    public static RestApiTemplate templateExchangeRate() {
        return RestApiTemplate.builder()
                .name("TEMPLATE_ExchangeRate")
                .displayName("ExchangeRate-API")
                .description("Free currency exchange rates - No authentication required")
                .baseUrl("https://api.exchangerate-api.com/v4")
                .timeoutMs(10000)
                .asTemplate()
                .auth(NoAuthConfig.INSTANCE)
                .addMethod("GetLatestRates", "Latest Rates", "Exchange rates for base currency", "GET", "/latest/{{baseCurrency}}", null, null)
                .addMethod("GetEURRates", "EUR Rates", "EUR exchange rates", "GET", "/latest/EUR", null, null)
                .addMethod("GetUSDRates", "USD Rates", "USD exchange rates", "GET", "/latest/USD", null, null)
                .addMethod("GetGBPRates", "GBP Rates", "GBP exchange rates", "GET", "/latest/GBP", null, null)
                .build();
    }

    /**
     * Template: IP-API - Free IP geolocation.
     */
    public static RestApiTemplate templateIPGeolocation() {
        return RestApiTemplate.builder()
                .name("TEMPLATE_IPGeolocation")
                .displayName("IP-API Geolocation")
                .description("Free IP geolocation - No authentication required")
                .baseUrl("http://ip-api.com")
                .timeoutMs(10000)
                .asTemplate()
                .auth(NoAuthConfig.INSTANCE)
                .addMethod("GetIPLocation", "IP Location", "Geolocation for IP address", "GET", "/json/{{ipAddress}}", null, null)
                .addMethod("GetMyLocation", "My Location", "Geolocation for requesting IP", "GET", "/json", null, null)
                .addMethod("BatchLookup", "Batch Lookup", "Geolocation for multiple IPs", "POST", "/batch", null, "[\"{{ip1}}\",\"{{ip2}}\",\"{{ip3}}\"]")
                .build();
    }

    /**
     * Template: Companies House UK - UK company registry.
     */
    public static RestApiTemplate templateCompaniesHouseUK() {
        return RestApiTemplate.builder()
                .name("TEMPLATE_CompaniesHouseUK")
                .displayName("Companies House UK")
                .description("UK company registry - Requires API key (use as username with Basic Auth)")
                .baseUrl("https://api.company-information.service.gov.uk")
                .timeoutMs(15000)
                .asTemplate()
                .requiredFields("username")
                .auth(new BasicAuthConfig("{{username}}", "", true))
                .addMethod("SearchCompanies", "Search", "Search companies by name", "GET", "/search/companies", Map.of("q", "{{query}}", "items_per_page", "{{limit}}"), null)
                .addMethod("GetCompanyProfile", "Profile", "Get company by number", "GET", "/company/{{companyNumber}}", null, null)
                .addMethod("GetCompanyOfficers", "Officers", "Get company officers", "GET", "/company/{{companyNumber}}/officers", null, null)
                .addMethod("GetFilingHistory", "Filing History", "Get filing history", "GET", "/company/{{companyNumber}}/filing-history", null, null)
                .addMethod("GetCompanyInsolvency", "Insolvency", "Get insolvency information", "GET", "/company/{{companyNumber}}/insolvency", null, null)
                .addMethod("GetCompanyCharges", "Charges", "Get charges/mortgages", "GET", "/company/{{companyNumber}}/charges", null, null)
                .build();
    }

    /**
     * Template: VIES VAT Validation - EU VAT validation.
     */
    public static RestApiTemplate templateVIESVat() {
        return RestApiTemplate.builder()
                .name("TEMPLATE_VIES_VAT")
                .displayName("VIES VAT Validation")
                .description("EU VAT number validation - No authentication required")
                .baseUrl("https://ec.europa.eu/taxation_customs/vies/rest-api")
                .timeoutMs(20000)
                .asTemplate()
                .auth(NoAuthConfig.INSTANCE)
                .addMethod("CheckVATNumber", "Check VAT (GET)", "Validate VAT number via GET", "GET", "/check-vat-number", Map.of("countryCode", "{{countryCode}}", "vatNumber", "{{vatNumber}}"), null)
                .addMethod("CheckVATNumberPost", "Check VAT (POST)", "Validate VAT number via POST", "POST", "/check-vat-number", null, "{\"countryCode\":\"{{countryCode}}\",\"vatNumber\":\"{{vatNumber}}\"}")
                .build();
    }

    /**
     * Template: Clearbit - Data enrichment API.
     */
    public static RestApiTemplate templateClearbit() {
        return RestApiTemplate.builder()
                .name("TEMPLATE_Clearbit")
                .displayName("Clearbit Enrichment")
                .description("Data enrichment - Requires API key (Bearer Token)")
                .baseUrl("https://person.clearbit.com/v2")
                .timeoutMs(15000)
                .asTemplate()
                .requiredFields("token")
                .auth(BearerAuthConfig.of("{{token}}"))
                .addMethod("EnrichPerson", "Enrich Person", "Enrich person by email", "GET", "/people/find", Map.of("email", "{{email}}"), null)
                .addMethod("EnrichCompany", "Enrich Company", "Enrich company by domain", "GET", "https://company.clearbit.com/v2/companies/find", Map.of("domain", "{{domain}}"), null)
                .addMethod("CombinedEnrichment", "Combined", "Enrich person + company", "GET", "https://person.clearbit.com/v2/combined/find", Map.of("email", "{{email}}"), null)
                .build();
    }

    /**
     * Template: Abstract Email Validation.
     */
    public static RestApiTemplate templateAbstractEmailValidation() {
        return RestApiTemplate.builder()
                .name("TEMPLATE_AbstractEmailValidation")
                .displayName("Abstract Email Validation")
                .description("Email validation - Requires API key")
                .baseUrl("https://emailvalidation.abstractapi.com/v1")
                .timeoutMs(10000)
                .asTemplate()
                .requiredFields("keyValue")
                .auth(ApiKeyAuthConfig.queryParam("api_key", "{{keyValue}}"))
                .addMethod("ValidateEmail", "Validate Email", "Validate an email address", "GET", "/", Map.of("email", "{{email}}"), null)
                .build();
    }

    /**
     * Template: NumVerify - Phone validation.
     */
    public static RestApiTemplate templateNumVerify() {
        return RestApiTemplate.builder()
                .name("TEMPLATE_NumVerify")
                .displayName("NumVerify Phone Validation")
                .description("Phone number validation - Requires API key")
                .baseUrl("http://apilayer.net/api")
                .timeoutMs(10000)
                .asTemplate()
                .requiredFields("keyValue")
                .auth(ApiKeyAuthConfig.queryParam("access_key", "{{keyValue}}"))
                .addMethod("ValidatePhone", "Validate Phone", "Validate a phone number", "GET", "/validate", Map.of("number", "{{phoneNumber}}", "country_code", "{{countryCode}}", "format", "1"), null)
                .build();
    }

    /**
     * Template: Alpha Vantage - Financial data.
     */
    public static RestApiTemplate templateAlphaVantage() {
        return RestApiTemplate.builder()
                .name("TEMPLATE_AlphaVantage")
                .displayName("Alpha Vantage Finance")
                .description("Stock market data - Requires free API key")
                .baseUrl("https://www.alphavantage.co")
                .timeoutMs(15000)
                .asTemplate()
                .requiredFields("keyValue")
                .auth(ApiKeyAuthConfig.queryParam("apikey", "{{keyValue}}"))
                .addMethod("GetStockQuote", "Stock Quote", "Real-time stock quote", "GET", "/query", Map.of("function", "GLOBAL_QUOTE", "symbol", "{{symbol}}"), null)
                .addMethod("GetDailyTimeSeries", "Daily Series", "Daily stock data", "GET", "/query", Map.of("function", "TIME_SERIES_DAILY", "symbol", "{{symbol}}"), null)
                .addMethod("GetWeeklyTimeSeries", "Weekly Series", "Weekly stock data", "GET", "/query", Map.of("function", "TIME_SERIES_WEEKLY", "symbol", "{{symbol}}"), null)
                .addMethod("SearchSymbol", "Search Symbol", "Search stock symbols", "GET", "/query", Map.of("function", "SYMBOL_SEARCH", "keywords", "{{keywords}}"), null)
                .addMethod("GetExchangeRate", "Exchange Rate", "Currency exchange rate", "GET", "/query", Map.of("function", "CURRENCY_EXCHANGE_RATE", "from_currency", "{{fromCurrency}}", "to_currency", "{{toCurrency}}"), null)
                .build();
    }

    /**
     * Template: SAP S/4HANA OData API.
     */
    public static RestApiTemplate templateSapS4Hana() {
        return RestApiTemplate.builder()
                .name("TEMPLATE_SAP_S4HANA")
                .displayName("SAP S/4HANA OData")
                .description("SAP S/4HANA OData API - Requires Basic Auth")
                .baseUrl("https://{{sapHost}}/sap/opu/odata/sap")
                .timeoutMs(60000)
                .asTemplate()
                .requiredFields("sapHost", "sapClient", "username", "password")
                .header("sap-client", "{{sapClient}}")
                .auth(new BasicAuthConfig("{{username}}", "{{password}}", true))
                .addMethod("GetBusinessPartners", "Business Partners", "List business partners", "GET", "/API_BUSINESS_PARTNER/A_BusinessPartner", Map.of("$top", "{{limit}}", "$format", "json"), null)
                .addMethod("GetBusinessPartner", "Business Partner", "Get specific partner", "GET", "/API_BUSINESS_PARTNER/A_BusinessPartner('{{partnerId}}')", Map.of("$format", "json"), null)
                .addMethod("GetSalesOrders", "Sales Orders", "List sales orders", "GET", "/API_SALES_ORDER_SRV/A_SalesOrder", Map.of("$top", "{{limit}}", "$format", "json"), null)
                .addMethod("GetSalesOrder", "Sales Order", "Get specific order", "GET", "/API_SALES_ORDER_SRV/A_SalesOrder('{{salesOrderId}}')", Map.of("$format", "json"), null)
                .addMethod("CreateSalesOrder", "Create SO", "Create sales order", "POST", "/API_SALES_ORDER_SRV/A_SalesOrder", null, "{{salesOrderPayload}}")
                .addMethod("GetPurchaseOrders", "Purchase Orders", "List purchase orders", "GET", "/API_PURCHASEORDER_PROCESS_SRV/A_PurchaseOrder", Map.of("$top", "{{limit}}", "$format", "json"), null)
                .addMethod("GetMaterials", "Materials", "List materials", "GET", "/API_PRODUCT_SRV/A_Product", Map.of("$top", "{{limit}}", "$format", "json"), null)
                .addMethod("GetCostCenters", "Cost Centers", "List cost centers", "GET", "/API_COSTCENTER_SRV/A_CostCenter", Map.of("$top", "{{limit}}", "$format", "json"), null)
                .build();
    }

    /**
     * Template: HubSpot CRM.
     */
    public static RestApiTemplate templateHubSpot() {
        return RestApiTemplate.builder()
                .name("TEMPLATE_HubSpot")
                .displayName("HubSpot CRM")
                .description("HubSpot CRM API - Requires Bearer Token")
                .baseUrl("https://api.hubapi.com")
                .timeoutMs(30000)
                .asTemplate()
                .requiredFields("token")
                .auth(BearerAuthConfig.of("{{token}}"))
                .addMethod("GetContacts", "Contacts", "List contacts", "GET", "/crm/v3/objects/contacts", Map.of("limit", "{{limit}}"), null)
                .addMethod("GetContact", "Contact", "Get contact by ID", "GET", "/crm/v3/objects/contacts/{{contactId}}", null, null)
                .addMethod("CreateContact", "Create Contact", "Create new contact", "POST", "/crm/v3/objects/contacts", null, "{\"properties\":{\"email\":\"{{email}}\",\"firstname\":\"{{firstName}}\",\"lastname\":\"{{lastName}}\"}}")
                .addMethod("UpdateContact", "Update Contact", "Update contact", "PATCH", "/crm/v3/objects/contacts/{{contactId}}", null, "{\"properties\":{{properties}}}")
                .addMethod("GetCompanies", "Companies", "List companies", "GET", "/crm/v3/objects/companies", Map.of("limit", "{{limit}}"), null)
                .addMethod("CreateCompany", "Create Company", "Create company", "POST", "/crm/v3/objects/companies", null, "{\"properties\":{\"name\":\"{{companyName}}\",\"domain\":\"{{domain}}\"}}")
                .addMethod("GetDeals", "Deals", "List deals", "GET", "/crm/v3/objects/deals", Map.of("limit", "{{limit}}"), null)
                .addMethod("CreateDeal", "Create Deal", "Create deal", "POST", "/crm/v3/objects/deals", null, "{\"properties\":{\"dealname\":\"{{dealName}}\",\"amount\":\"{{amount}}\",\"pipeline\":\"{{pipeline}}\"}}")
                .addMethod("GetOwners", "Owners", "List owners", "GET", "/crm/v3/owners", null, null)
                .build();
    }

    /**
     * Template: Microsoft Graph API.
     */
    public static RestApiTemplate templateMicrosoftGraph() {
        return RestApiTemplate.builder()
                .name("TEMPLATE_Microsoft_Graph")
                .displayName("Microsoft Graph API")
                .description("Microsoft 365 API (Mail, Calendar, Teams) - Requires OAuth2")
                .baseUrl("https://graph.microsoft.com/v1.0")
                .timeoutMs(30000)
                .asTemplate()
                .requiredFields("tenantId", "clientId", "clientSecret")
                .auth(new OAuth2ClientConfig(
                        "https://login.microsoftonline.com/{{tenantId}}/oauth2/v2.0/token",
                        "{{clientId}}", "{{clientSecret}}", "https://graph.microsoft.com/.default"))
                .addMethod("GetMe", "Current User", "Get current user", "GET", "/me", null, null)
                .addMethod("GetUsers", "Users", "List users", "GET", "/users", Map.of("$top", "{{limit}}"), null)
                .addMethod("GetUser", "User", "Get user by ID", "GET", "/users/{{userId}}", null, null)
                .addMethod("GetMyMail", "My Mail", "Get mail messages", "GET", "/me/messages", Map.of("$top", "{{limit}}"), null)
                .addMethod("SendMail", "Send Mail", "Send email", "POST", "/me/sendMail", null, "{\"message\":{\"subject\":\"{{subject}}\",\"body\":{\"contentType\":\"Text\",\"content\":\"{{body}}\"},\"toRecipients\":[{\"emailAddress\":{\"address\":\"{{toEmail}}\"}}]}}")
                .addMethod("GetMyCalendar", "Calendar", "Get calendar events", "GET", "/me/events", Map.of("$top", "{{limit}}"), null)
                .addMethod("CreateEvent", "Create Event", "Create calendar event", "POST", "/me/events", null, "{\"subject\":\"{{subject}}\",\"start\":{\"dateTime\":\"{{startDateTime}}\",\"timeZone\":\"{{timeZone}}\"},\"end\":{\"dateTime\":\"{{endDateTime}}\",\"timeZone\":\"{{timeZone}}\"}}")
                .addMethod("GetGroups", "Groups", "List groups", "GET", "/groups", Map.of("$top", "{{limit}}"), null)
                .addMethod("GetTeams", "Teams", "Get joined teams", "GET", "/me/joinedTeams", null, null)
                .build();
    }

    /**
     * Template: ServiceNow ITSM.
     */
    public static RestApiTemplate templateServiceNow() {
        return RestApiTemplate.builder()
                .name("TEMPLATE_ServiceNow")
                .displayName("ServiceNow ITSM")
                .description("ServiceNow Table API - Requires Basic Auth")
                .baseUrl("https://{{instanceName}}.service-now.com/api/now")
                .timeoutMs(30000)
                .asTemplate()
                .requiredFields("instanceName", "username", "password")
                .auth(new BasicAuthConfig("{{username}}", "{{password}}", true))
                .addMethod("GetIncidents", "Incidents", "List incidents", "GET", "/table/incident", Map.of("sysparm_limit", "{{limit}}"), null)
                .addMethod("GetIncident", "Incident", "Get incident by sys_id", "GET", "/table/incident/{{sysId}}", null, null)
                .addMethod("CreateIncident", "Create Incident", "Create incident", "POST", "/table/incident", null, "{\"short_description\":\"{{shortDescription}}\",\"description\":\"{{description}}\",\"urgency\":\"{{urgency}}\",\"impact\":\"{{impact}}\"}")
                .addMethod("UpdateIncident", "Update Incident", "Update incident", "PATCH", "/table/incident/{{sysId}}", null, "{{updatePayload}}")
                .addMethod("GetChanges", "Changes", "List change requests", "GET", "/table/change_request", Map.of("sysparm_limit", "{{limit}}"), null)
                .addMethod("GetProblems", "Problems", "List problems", "GET", "/table/problem", Map.of("sysparm_limit", "{{limit}}"), null)
                .addMethod("GetUsers", "Users", "List users", "GET", "/table/sys_user", Map.of("sysparm_limit", "{{limit}}"), null)
                .addMethod("GetCMDBItems", "CMDB Items", "List configuration items", "GET", "/table/cmdb_ci", Map.of("sysparm_limit", "{{limit}}"), null)
                .build();
    }

    /**
     * Template: DocuSign eSignature.
     */
    public static RestApiTemplate templateDocuSign() {
        return RestApiTemplate.builder()
                .name("TEMPLATE_DocuSign")
                .displayName("DocuSign eSignature")
                .description("Electronic signatures - Requires OAuth2")
                .baseUrl("https://{{environment}}.docusign.net/restapi/v2.1")
                .timeoutMs(60000)
                .asTemplate()
                .requiredFields("environment", "accountId", "clientId", "clientSecret")
                .auth(new OAuth2ClientConfig(
                        "https://account-d.docusign.com/oauth/token",
                        "{{clientId}}", "{{clientSecret}}", "signature"))
                .addMethod("GetUserInfo", "User Info", "Get current user", "GET", "/accounts/{{accountId}}/users", null, null)
                .addMethod("GetEnvelopes", "Envelopes", "List envelopes", "GET", "/accounts/{{accountId}}/envelopes", Map.of("from_date", "{{fromDate}}"), null)
                .addMethod("GetEnvelope", "Envelope", "Get envelope by ID", "GET", "/accounts/{{accountId}}/envelopes/{{envelopeId}}", null, null)
                .addMethod("CreateEnvelope", "Create Envelope", "Create and send envelope", "POST", "/accounts/{{accountId}}/envelopes", null, "{{envelopeDefinition}}")
                .addMethod("GetEnvelopeDocuments", "Envelope Docs", "List envelope documents", "GET", "/accounts/{{accountId}}/envelopes/{{envelopeId}}/documents", null, null)
                .addMethod("GetTemplates", "Templates", "List templates", "GET", "/accounts/{{accountId}}/templates", null, null)
                .addMethod("VoidEnvelope", "Void Envelope", "Void an envelope", "PUT", "/accounts/{{accountId}}/envelopes/{{envelopeId}}", null, "{\"status\":\"voided\",\"voidedReason\":\"{{reason}}\"}")
                .build();
    }

    /**
     * Template: Stripe Payments.
     */
    public static RestApiTemplate templateStripe() {
        return RestApiTemplate.builder()
                .name("TEMPLATE_Stripe")
                .displayName("Stripe Payments")
                .description("Payment processing - Requires API key (Bearer Token)")
                .baseUrl("https://api.stripe.com/v1")
                .timeoutMs(30000)
                .asTemplate()
                .requiredFields("token")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .auth(BearerAuthConfig.of("{{token}}"))
                .addMethod("GetCustomers", "Customers", "List customers", "GET", "/customers", Map.of("limit", "{{limit}}"), null)
                .addMethod("GetCustomer", "Customer", "Get customer by ID", "GET", "/customers/{{customerId}}", null, null)
                .addMethod("CreateCustomer", "Create Customer", "Create customer", "POST", "/customers", null, "email={{email}}&name={{name}}")
                .addMethod("GetPaymentIntents", "Payment Intents", "List payment intents", "GET", "/payment_intents", Map.of("limit", "{{limit}}"), null)
                .addMethod("CreatePaymentIntent", "Create Intent", "Create payment intent", "POST", "/payment_intents", null, "amount={{amount}}&currency={{currency}}&customer={{customerId}}")
                .addMethod("GetCharges", "Charges", "List charges", "GET", "/charges", Map.of("limit", "{{limit}}"), null)
                .addMethod("GetInvoices", "Invoices", "List invoices", "GET", "/invoices", Map.of("limit", "{{limit}}"), null)
                .addMethod("GetSubscriptions", "Subscriptions", "List subscriptions", "GET", "/subscriptions", Map.of("limit", "{{limit}}"), null)
                .addMethod("GetBalance", "Balance", "Get account balance", "GET", "/balance", null, null)
                .build();
    }

    /**
     * Template: Slack Web API.
     */
    public static RestApiTemplate templateSlack() {
        return RestApiTemplate.builder()
                .name("TEMPLATE_Slack")
                .displayName("Slack Workspace")
                .description("Slack Web API - Requires Bot Token")
                .baseUrl("https://slack.com/api")
                .timeoutMs(15000)
                .asTemplate()
                .requiredFields("token")
                .auth(BearerAuthConfig.of("{{token}}"))
                .addMethod("PostMessage", "Post Message", "Send message to channel", "POST", "/chat.postMessage", null, "{\"channel\":\"{{channel}}\",\"text\":\"{{message}}\"}")
                .addMethod("GetChannels", "Channels", "List channels", "GET", "/conversations.list", Map.of("limit", "{{limit}}"), null)
                .addMethod("GetChannelHistory", "History", "Get channel messages", "GET", "/conversations.history", Map.of("channel", "{{channel}}", "limit", "{{limit}}"), null)
                .addMethod("GetUsers", "Users", "List users", "GET", "/users.list", Map.of("limit", "{{limit}}"), null)
                .addMethod("GetUserInfo", "User Info", "Get user info", "GET", "/users.info", Map.of("user", "{{userId}}"), null)
                .addMethod("CreateChannel", "Create Channel", "Create a channel", "POST", "/conversations.create", null, "{\"name\":\"{{channelName}}\",\"is_private\":{{isPrivate}}}")
                .addMethod("UploadFile", "Upload File", "Upload a file", "POST", "/files.upload", null, "{\"channels\":\"{{channel}}\",\"content\":\"{{content}}\",\"filename\":\"{{filename}}\"}")
                .build();
    }

    /**
     * Template: Google Drive API.
     */
    public static RestApiTemplate templateGoogleDrive() {
        return RestApiTemplate.builder()
                .name("TEMPLATE_Google_Drive")
                .displayName("Google Drive API")
                .description("Google Drive storage - Requires OAuth2")
                .baseUrl("https://www.googleapis.com/drive/v3")
                .timeoutMs(30000)
                .asTemplate()
                .requiredFields("clientId", "clientSecret")
                .auth(new OAuth2ClientConfig(
                        "https://oauth2.googleapis.com/token",
                        "{{clientId}}", "{{clientSecret}}", "https://www.googleapis.com/auth/drive"))
                .addMethod("ListFiles", "List Files", "List files", "GET", "/files", Map.of("pageSize", "{{limit}}", "fields", "files(id,name,mimeType,createdTime,modifiedTime)"), null)
                .addMethod("GetFile", "Get File", "Get file metadata", "GET", "/files/{{fileId}}", Map.of("fields", "*"), null)
                .addMethod("DownloadFile", "Download", "Download file content", "GET", "/files/{{fileId}}", Map.of("alt", "media"), null)
                .addMethod("CreateFolder", "Create Folder", "Create a folder", "POST", "/files", null, "{\"name\":\"{{folderName}}\",\"mimeType\":\"application/vnd.google-apps.folder\",\"parents\":[\"{{parentId}}\"]}")
                .addMethod("DeleteFile", "Delete", "Delete a file", "DELETE", "/files/{{fileId}}", null, null)
                .addMethod("SearchFiles", "Search", "Search files", "GET", "/files", Map.of("q", "{{query}}", "pageSize", "{{limit}}"), null)
                .build();
    }

    /**
     * Template: Jira Cloud.
     */
    public static RestApiTemplate templateJira() {
        return RestApiTemplate.builder()
                .name("TEMPLATE_Jira")
                .displayName("Jira Cloud")
                .description("Jira issue tracking - Requires Basic Auth (email + API token)")
                .baseUrl("https://{{domain}}.atlassian.net/rest/api/3")
                .timeoutMs(30000)
                .asTemplate()
                .requiredFields("domain", "username", "password")
                .auth(new BasicAuthConfig("{{username}}", "{{password}}", true))
                .addMethod("GetProjects", "Projects", "List projects", "GET", "/project", null, null)
                .addMethod("GetProject", "Project", "Get project by key", "GET", "/project/{{projectKey}}", null, null)
                .addMethod("SearchIssues", "Search Issues", "Search with JQL", "GET", "/search", Map.of("jql", "{{jql}}", "maxResults", "{{limit}}"), null)
                .addMethod("GetIssue", "Issue", "Get issue by key", "GET", "/issue/{{issueKey}}", null, null)
                .addMethod("CreateIssue", "Create Issue", "Create an issue", "POST", "/issue", null, "{\"fields\":{\"project\":{\"key\":\"{{projectKey}}\"},\"summary\":\"{{summary}}\",\"description\":{\"type\":\"doc\",\"version\":1,\"content\":[{\"type\":\"paragraph\",\"content\":[{\"type\":\"text\",\"text\":\"{{description}}\"}]}]},\"issuetype\":{\"name\":\"{{issueType}}\"}}}")
                .addMethod("UpdateIssue", "Update Issue", "Update an issue", "PUT", "/issue/{{issueKey}}", null, "{\"fields\":{{fields}}}")
                .addMethod("AddComment", "Add Comment", "Add issue comment", "POST", "/issue/{{issueKey}}/comment", null, "{\"body\":{\"type\":\"doc\",\"version\":1,\"content\":[{\"type\":\"paragraph\",\"content\":[{\"type\":\"text\",\"text\":\"{{comment}}\"}]}]}}")
                .addMethod("TransitionIssue", "Transition", "Change issue status", "POST", "/issue/{{issueKey}}/transitions", null, "{\"transition\":{\"id\":\"{{transitionId}}\"}}")
                .addMethod("GetTransitions", "Transitions", "Get available transitions", "GET", "/issue/{{issueKey}}/transitions", null, null)
                .build();
    }

    /**
     * Template: Microsoft OneDrive.
     */
    public static RestApiTemplate templateOneDrive() {
        return RestApiTemplate.builder()
                .name("TEMPLATE_Microsoft_OneDrive")
                .displayName("Microsoft OneDrive")
                .description("OneDrive cloud storage - Requires OAuth2")
                .baseUrl("https://graph.microsoft.com/v1.0")
                .timeoutMs(30000)
                .asTemplate()
                .requiredFields("tenantId", "clientId", "clientSecret")
                .auth(new OAuth2ClientConfig(
                        "https://login.microsoftonline.com/{{tenantId}}/oauth2/v2.0/token",
                        "{{clientId}}", "{{clientSecret}}", "https://graph.microsoft.com/.default"))
                .addMethod("ListFiles", "List Files", "List root files", "GET", "/me/drive/root/children", null, null)
                .addMethod("GetFile", "Get File", "Get file metadata", "GET", "/me/drive/items/{{fileId}}", null, null)
                .addMethod("DownloadFile", "Download", "Download file content", "GET", "/me/drive/items/{{fileId}}/content", null, null)
                .addMethod("UploadFile", "Upload", "Upload a file", "PUT", "/me/drive/root:/{{fileName}}:/content", null, null)
                .addMethod("CreateFolder", "Create Folder", "Create a folder", "POST", "/me/drive/root/children", null, "{\"name\":\"{{folderName}}\",\"folder\":{},\"@microsoft.graph.conflictBehavior\":\"rename\"}")
                .addMethod("DeleteFile", "Delete", "Delete a file", "DELETE", "/me/drive/items/{{fileId}}", null, null)
                .addMethod("SearchFiles", "Search", "Search files", "GET", "/me/drive/root/search(q='{{query}}')", null, null)
                .build();
    }

    /**
     * Template: Salesforce CRM.
     */
    public static RestApiTemplate templateSalesforce() {
        return RestApiTemplate.builder()
                .name("TEMPLATE_Salesforce")
                .displayName("Salesforce CRM")
                .description("Salesforce REST API - Requires OAuth2")
                .baseUrl("https://{{instanceUrl}}.salesforce.com/services/data/v59.0")
                .timeoutMs(30000)
                .asTemplate()
                .requiredFields("instanceUrl", "consumerKey", "consumerSecret")
                .auth(new OAuth2ClientConfig(
                        "https://{{instanceUrl}}.salesforce.com/services/oauth2/token",
                        "{{consumerKey}}", "{{consumerSecret}}", null))
                .addMethod("GetAccounts", "Accounts", "Query accounts", "GET", "/query", Map.of("q", "SELECT Id,Name,Industry FROM Account LIMIT 100"), null)
                .addMethod("GetAccount", "Account", "Get account by ID", "GET", "/sobjects/Account/{{accountId}}", null, null)
                .addMethod("CreateAccount", "Create Account", "Create an account", "POST", "/sobjects/Account", null, "{\"Name\":\"{{accountName}}\",\"Industry\":\"{{industry}}\"}")
                .addMethod("GetContacts", "Contacts", "Query contacts", "GET", "/query", Map.of("q", "SELECT Id,FirstName,LastName,Email FROM Contact LIMIT 100"), null)
                .addMethod("CreateContact", "Create Contact", "Create a contact", "POST", "/sobjects/Contact", null, "{\"FirstName\":\"{{firstName}}\",\"LastName\":\"{{lastName}}\",\"Email\":\"{{email}}\"}")
                .build();
    }

    /**
     * Template: Alfresco Content Services.
     */
    public static RestApiTemplate templateAlfresco() {
        return RestApiTemplate.builder()
                .name("TEMPLATE_Alfresco")
                .displayName("Alfresco Content Services")
                .description("Alfresco ECM - Requires Basic Auth")
                .baseUrl("https://{{alfrescoHost}}/alfresco/api/-default-/public/alfresco/versions/1")
                .timeoutMs(30000)
                .asTemplate()
                .requiredFields("alfrescoHost", "username", "password")
                .auth(new BasicAuthConfig("{{username}}", "{{password}}", true))
                .addMethod("GetNodes", "List Nodes", "List node children", "GET", "/nodes/{{nodeId}}/children", null, null)
                .addMethod("GetNode", "Get Node", "Get node metadata", "GET", "/nodes/{{nodeId}}", null, null)
                .addMethod("CreateFolder", "Create Folder", "Create a folder", "POST", "/nodes/{{parentNodeId}}/children", null, "{\"name\":\"{{folderName}}\",\"nodeType\":\"cm:folder\"}")
                .addMethod("DownloadDocument", "Download", "Download document content", "GET", "/nodes/{{nodeId}}/content", null, null)
                .addMethod("DeleteNode", "Delete", "Delete a node", "DELETE", "/nodes/{{nodeId}}", null, null)
                .build();
    }

    /**
     * Template: Bonita BPM Universal (30 methods).
     */
    public static RestApiTemplate templateBonitaUniversal() {
        return RestApiTemplate.builder()
                .name("TEMPLATE_Bonita_Universal")
                .displayName("Bonita BPM Platform")
                .description("Universal Bonita template - 30 methods for complete BPM operations")
                .baseUrl("{{bonitaUrl}}")
                .timeoutMs(30000)
                .verifySsl(false)
                .asTemplate()
                .requiredFields("bonitaUrl", "username", "password")
                .auth(new BasicAuthConfig("{{username}}", "{{password}}", true))
                // Session & System
                .addMethod("GetSession", "Session", "Get session info", "GET", "/bonita/API/system/session/unusedId", null, null)
                .addMethod("GetServerInfo", "Server Info", "Get server info", "GET", "/bonita/API/system/tenant/1", null, null)
                .addMethod("GetI18n", "I18n", "Get translations", "GET", "/bonita/API/system/i18ntranslation", Map.of("p", "0", "c", "100"), null)
                // Identity
                .addMethod("GetUsers", "List Users", "List users", "GET", "/bonita/API/identity/user", Map.of("p", "{{page}}", "c", "{{count}}"), null)
                .addMethod("GetUser", "Get User", "Get user by ID", "GET", "/bonita/API/identity/user/{{userId}}", null, null)
                .addMethod("SearchUsers", "Search Users", "Search users by term", "GET", "/bonita/API/identity/user", Map.of("p", "0", "c", "20", "s", "{{searchTerm}}"), null)
                .addMethod("CreateUser", "Create User", "Create a user", "POST", "/bonita/API/identity/user", null, "{\"userName\":\"{{userName}}\",\"password\":\"{{password}}\",\"password_confirm\":\"{{password}}\",\"firstname\":\"{{firstName}}\",\"lastname\":\"{{lastName}}\",\"enabled\":\"true\"}")
                .addMethod("UpdateUser", "Update User", "Update a user", "PUT", "/bonita/API/identity/user/{{userId}}", null, "{{userPayload}}")
                .addMethod("GetGroups", "List Groups", "List groups", "GET", "/bonita/API/identity/group", Map.of("p", "{{page}}", "c", "{{count}}"), null)
                .addMethod("GetGroup", "Get Group", "Get group by ID", "GET", "/bonita/API/identity/group/{{groupId}}", null, null)
                .addMethod("GetRoles", "List Roles", "List roles", "GET", "/bonita/API/identity/role", Map.of("p", "{{page}}", "c", "{{count}}"), null)
                .addMethod("GetMemberships", "Memberships", "Get user memberships", "GET", "/bonita/API/identity/membership", Map.of("p", "0", "c", "20", "f", "user_id={{userId}}"), null)
                // Processes
                .addMethod("GetProcesses", "List Processes", "List deployed processes", "GET", "/bonita/API/bpm/process", Map.of("p", "{{page}}", "c", "{{count}}"), null)
                .addMethod("GetProcess", "Get Process", "Get process by ID", "GET", "/bonita/API/bpm/process/{{processId}}", null, null)
                .addMethod("SearchProcesses", "Search Processes", "Search by name", "GET", "/bonita/API/bpm/process", Map.of("p", "0", "c", "20", "s", "{{searchTerm}}"), null)
                .addMethod("StartProcess", "Start Process", "Instantiate process", "POST", "/bonita/API/bpm/process/{{processId}}/instantiation", null, "{{contractInputs}}")
                // Cases
                .addMethod("GetCases", "List Cases", "List active cases", "GET", "/bonita/API/bpm/case", Map.of("p", "{{page}}", "c", "{{count}}"), null)
                .addMethod("GetCase", "Get Case", "Get case by ID", "GET", "/bonita/API/bpm/case/{{caseId}}", null, null)
                .addMethod("GetCaseContext", "Case Context", "Get BDM context", "GET", "/bonita/API/bpm/case/{{caseId}}/context", null, null)
                .addMethod("DeleteCase", "Delete Case", "Delete a case", "DELETE", "/bonita/API/bpm/case/{{caseId}}", null, null)
                .addMethod("GetArchivedCases", "Archived Cases", "List archived cases", "GET", "/bonita/API/bpm/archivedCase", Map.of("p", "{{page}}", "c", "{{count}}"), null)
                // Tasks
                .addMethod("GetHumanTasks", "Human Tasks", "List human tasks", "GET", "/bonita/API/bpm/humanTask", Map.of("p", "{{page}}", "c", "{{count}}"), null)
                .addMethod("GetHumanTask", "Get Task", "Get task by ID", "GET", "/bonita/API/bpm/humanTask/{{taskId}}", null, null)
                .addMethod("GetTaskContext", "Task Context", "Get task context", "GET", "/bonita/API/bpm/userTask/{{taskId}}/context", null, null)
                .addMethod("AssignTask", "Assign Task", "Assign task to user", "PUT", "/bonita/API/bpm/humanTask/{{taskId}}", null, "{\"assigned_id\":\"{{userId}}\"}")
                .addMethod("ExecuteTask", "Execute Task", "Complete task", "POST", "/bonita/API/bpm/userTask/{{taskId}}/execution", null, "{{contractInputs}}")
                .addMethod("GetPendingTasks", "Pending Tasks", "User's pending tasks", "GET", "/bonita/API/bpm/humanTask", Map.of("p", "0", "c", "20", "f", "state=ready&assigned_id={{userId}}"), null)
                // Activities
                .addMethod("GetActivities", "Activities", "Case activities", "GET", "/bonita/API/bpm/activity", Map.of("p", "0", "c", "100", "f", "caseId={{caseId}}"), null)
                .addMethod("GetFlowNodes", "Flow Nodes", "Case flow nodes", "GET", "/bonita/API/bpm/flowNode", Map.of("p", "0", "c", "100", "f", "caseId={{caseId}}"), null)
                .addMethod("GetArchivedActivities", "Archived Activities", "Completed activities", "GET", "/bonita/API/bpm/archivedActivity", Map.of("p", "0", "c", "100", "f", "caseId={{caseId}}"), null)
                .build();
    }
}
