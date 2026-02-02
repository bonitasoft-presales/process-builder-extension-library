package com.bonitasoft.processbuilder.records;

import com.bonitasoft.processbuilder.enums.RestApiKeyLocation;
import com.bonitasoft.processbuilder.enums.RestAuthenticationType;
import com.bonitasoft.processbuilder.enums.RestOAuth2ClientAuthMethod;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.Base64;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link RestAuthConfig} sealed interface and its implementations.
 *
 * @author Bonitasoft
 * @since 1.0
 */
class RestAuthConfigTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    // =========================================================================
    // NoAuth Tests
    // =========================================================================

    @Test
    void noAuth_shouldReturnCorrectAuthType() {
        RestAuthConfig.NoAuth noAuth = RestAuthConfig.none();
        assertEquals(RestAuthenticationType.NONE, noAuth.getAuthType());
    }

    @Test
    void noAuth_shouldReturnEmptyHeaders() {
        RestAuthConfig.NoAuth noAuth = RestAuthConfig.none();
        assertTrue(noAuth.getAuthHeaders().isEmpty());
    }

    @Test
    void noAuth_shouldReturnEmptyQueryParams() {
        RestAuthConfig.NoAuth noAuth = RestAuthConfig.none();
        assertTrue(noAuth.getAuthQueryParams().isEmpty());
    }

    // =========================================================================
    // BasicAuth Tests
    // =========================================================================

    @Test
    void basicAuth_shouldReturnCorrectAuthType() {
        RestAuthConfig.BasicAuth basicAuth = RestAuthConfig.basic("user", "pass");
        assertEquals(RestAuthenticationType.BASIC, basicAuth.getAuthType());
    }

    @Test
    void basicAuth_shouldGenerateCorrectAuthHeader() {
        RestAuthConfig.BasicAuth basicAuth = RestAuthConfig.basic("user", "pass");
        Map<String, String> headers = basicAuth.getAuthHeaders();

        assertEquals(1, headers.size());
        assertTrue(headers.containsKey("Authorization"));

        String expectedEncoded = Base64.getEncoder().encodeToString("user:pass".getBytes());
        assertEquals("Basic " + expectedEncoded, headers.get("Authorization"));
    }

    @Test
    void basicAuth_shouldHandleNullUsername() {
        RestAuthConfig.BasicAuth basicAuth = new RestAuthConfig.BasicAuth(null, "pass", true);
        assertEquals("", basicAuth.username());
    }

    @Test
    void basicAuth_shouldHandleNullPassword() {
        RestAuthConfig.BasicAuth basicAuth = new RestAuthConfig.BasicAuth("user", null, true);
        assertEquals("", basicAuth.password());
    }

    // =========================================================================
    // BearerAuth Tests
    // =========================================================================

    @Test
    void bearerAuth_shouldReturnCorrectAuthType() {
        RestAuthConfig.BearerAuth bearerAuth = RestAuthConfig.bearer("my-token");
        assertEquals(RestAuthenticationType.BEARER, bearerAuth.getAuthType());
    }

    @Test
    void bearerAuth_shouldGenerateCorrectAuthHeader() {
        RestAuthConfig.BearerAuth bearerAuth = RestAuthConfig.bearer("my-token");
        Map<String, String> headers = bearerAuth.getAuthHeaders();

        assertEquals(1, headers.size());
        assertEquals("Bearer my-token", headers.get("Authorization"));
    }

    @Test
    void bearerAuth_shouldHandleNullToken() {
        RestAuthConfig.BearerAuth bearerAuth = new RestAuthConfig.BearerAuth(null);
        assertEquals("", bearerAuth.token());
        assertEquals("Bearer ", bearerAuth.getAuthHeaders().get("Authorization"));
    }

    // =========================================================================
    // ApiKeyAuth Tests
    // =========================================================================

    @Test
    void apiKeyAuth_shouldReturnCorrectAuthType() {
        RestAuthConfig.ApiKeyAuth apiKeyAuth = RestAuthConfig.apiKey("X-API-Key", "secret", RestApiKeyLocation.HEADER);
        assertEquals(RestAuthenticationType.API_KEY, apiKeyAuth.getAuthType());
    }

    @Test
    void apiKeyAuth_headerLocation_shouldGenerateHeader() {
        RestAuthConfig.ApiKeyAuth apiKeyAuth = RestAuthConfig.apiKey("X-API-Key", "secret", RestApiKeyLocation.HEADER);
        Map<String, String> headers = apiKeyAuth.getAuthHeaders();

        assertEquals(1, headers.size());
        assertEquals("secret", headers.get("X-API-Key"));
        assertTrue(apiKeyAuth.getAuthQueryParams().isEmpty());
    }

    @Test
    void apiKeyAuth_queryLocation_shouldGenerateQueryParam() {
        RestAuthConfig.ApiKeyAuth apiKeyAuth = RestAuthConfig.apiKey("api_key", "secret", RestApiKeyLocation.QUERY_PARAM);
        Map<String, String> queryParams = apiKeyAuth.getAuthQueryParams();

        assertEquals(1, queryParams.size());
        assertEquals("secret", queryParams.get("api_key"));
        assertTrue(apiKeyAuth.getAuthHeaders().isEmpty());
    }

    @Test
    void apiKeyAuth_shouldDefaultToHeaderLocation() {
        RestAuthConfig.ApiKeyAuth apiKeyAuth = new RestAuthConfig.ApiKeyAuth("X-API-Key", "secret", null);
        assertEquals(RestApiKeyLocation.HEADER, apiKeyAuth.location());
    }

    @Test
    void apiKeyAuth_shouldDefaultKeyName() {
        RestAuthConfig.ApiKeyAuth apiKeyAuth = new RestAuthConfig.ApiKeyAuth(null, "secret", RestApiKeyLocation.HEADER);
        assertEquals("X-API-Key", apiKeyAuth.keyName());
    }

    // =========================================================================
    // OAuth2ClientCredentials Tests
    // =========================================================================

    @Test
    void oauth2ClientCredentials_shouldReturnCorrectAuthType() {
        RestAuthConfig.OAuth2ClientCredentials oauth2 = RestAuthConfig.oauth2ClientCredentials(
                "https://auth.example.com/token", "client-id", "client-secret");
        assertEquals(RestAuthenticationType.OAUTH2_CLIENT_CREDENTIALS, oauth2.getAuthType());
    }

    @Test
    void oauth2ClientCredentials_shouldReturnEmptyAuthHeaders() {
        RestAuthConfig.OAuth2ClientCredentials oauth2 = RestAuthConfig.oauth2ClientCredentials(
                "https://auth.example.com/token", "client-id", "client-secret");
        assertTrue(oauth2.getAuthHeaders().isEmpty());
    }

    @Test
    void oauth2ClientCredentials_bodyMethod_shouldGenerateCorrectTokenRequestBody() {
        RestAuthConfig.OAuth2ClientCredentials oauth2 = new RestAuthConfig.OAuth2ClientCredentials(
                "https://auth.example.com/token", "client-id", "client-secret",
                "read write", "https://api.example.com", RestOAuth2ClientAuthMethod.BODY);

        String body = oauth2.getTokenRequestBody();

        assertTrue(body.contains("grant_type=client_credentials"));
        assertTrue(body.contains("client_id=client-id"));
        assertTrue(body.contains("client_secret=client-secret"));
        assertTrue(body.contains("scope=read+write"));
        assertTrue(body.contains("audience=https"));
    }

    @Test
    void oauth2ClientCredentials_headerMethod_shouldGenerateBasicAuthHeader() {
        RestAuthConfig.OAuth2ClientCredentials oauth2 = new RestAuthConfig.OAuth2ClientCredentials(
                "https://auth.example.com/token", "client-id", "client-secret",
                null, null, RestOAuth2ClientAuthMethod.HEADER);

        Map<String, String> headers = oauth2.getTokenRequestHeaders();

        assertTrue(headers.containsKey("Authorization"));
        assertTrue(headers.get("Authorization").startsWith("Basic "));
        assertEquals("application/x-www-form-urlencoded", headers.get("Content-Type"));
    }

    // =========================================================================
    // OAuth2Password Tests
    // =========================================================================

    @Test
    void oauth2Password_shouldReturnCorrectAuthType() {
        RestAuthConfig.OAuth2Password oauth2 = new RestAuthConfig.OAuth2Password(
                "https://auth.example.com/token", "client-id", "client-secret",
                "user", "pass", "read");
        assertEquals(RestAuthenticationType.OAUTH2_PASSWORD, oauth2.getAuthType());
    }

    @Test
    void oauth2Password_shouldGenerateCorrectTokenRequestBody() {
        RestAuthConfig.OAuth2Password oauth2 = new RestAuthConfig.OAuth2Password(
                "https://auth.example.com/token", "client-id", "client-secret",
                "user", "pass", "read write");

        String body = oauth2.getTokenRequestBody();

        assertTrue(body.contains("grant_type=password"));
        assertTrue(body.contains("username=user"));
        assertTrue(body.contains("password=pass"));
        assertTrue(body.contains("client_id=client-id"));
        assertTrue(body.contains("client_secret=client-secret"));
        assertTrue(body.contains("scope=read+write"));
    }

    // =========================================================================
    // CustomAuth Tests
    // =========================================================================

    @Test
    void customAuth_shouldReturnCorrectAuthType() {
        RestAuthConfig.CustomAuth customAuth = new RestAuthConfig.CustomAuth(Map.of("X-Custom", "value"));
        assertEquals(RestAuthenticationType.CUSTOM, customAuth.getAuthType());
    }

    @Test
    void customAuth_shouldReturnProvidedHeaders() {
        Map<String, String> headers = Map.of("X-Custom", "value", "X-Another", "another");
        RestAuthConfig.CustomAuth customAuth = new RestAuthConfig.CustomAuth(headers);

        assertEquals(headers, customAuth.getAuthHeaders());
    }

    @Test
    void customAuth_shouldHandleNullHeaders() {
        RestAuthConfig.CustomAuth customAuth = new RestAuthConfig.CustomAuth(null);
        assertTrue(customAuth.getAuthHeaders().isEmpty());
    }

    // =========================================================================
    // fromJson Tests
    // =========================================================================

    @Test
    void fromJson_nullNode_shouldReturnNoAuth() {
        RestAuthConfig result = RestAuthConfig.fromJson(null, null);
        assertInstanceOf(RestAuthConfig.NoAuth.class, result);
    }

    @Test
    void fromJson_basicAuth_shouldParseCorrectly() throws Exception {
        String json = """
                {
                    "authType": "basic",
                    "username": "testuser",
                    "password": "testpass"
                }
                """;
        JsonNode node = MAPPER.readTree(json);

        RestAuthConfig result = RestAuthConfig.fromJson(node, null);

        assertInstanceOf(RestAuthConfig.BasicAuth.class, result);
        RestAuthConfig.BasicAuth basicAuth = (RestAuthConfig.BasicAuth) result;
        assertEquals("testuser", basicAuth.username());
        assertEquals("testpass", basicAuth.password());
    }

    @Test
    void fromJson_bearerAuth_shouldParseCorrectly() throws Exception {
        String json = """
                {
                    "authType": "bearer",
                    "token": "my-jwt-token"
                }
                """;
        JsonNode node = MAPPER.readTree(json);

        RestAuthConfig result = RestAuthConfig.fromJson(node, null);

        assertInstanceOf(RestAuthConfig.BearerAuth.class, result);
        RestAuthConfig.BearerAuth bearerAuth = (RestAuthConfig.BearerAuth) result;
        assertEquals("my-jwt-token", bearerAuth.token());
    }

    @Test
    void fromJson_apiKeyAuth_shouldParseCorrectly() throws Exception {
        String json = """
                {
                    "authType": "apiKey",
                    "keyName": "X-Custom-Key",
                    "keyValue": "secret-key",
                    "location": "header"
                }
                """;
        JsonNode node = MAPPER.readTree(json);

        RestAuthConfig result = RestAuthConfig.fromJson(node, null);

        assertInstanceOf(RestAuthConfig.ApiKeyAuth.class, result);
        RestAuthConfig.ApiKeyAuth apiKeyAuth = (RestAuthConfig.ApiKeyAuth) result;
        assertEquals("X-Custom-Key", apiKeyAuth.keyName());
        assertEquals("secret-key", apiKeyAuth.keyValue());
        assertEquals(RestApiKeyLocation.HEADER, apiKeyAuth.location());
    }

    @Test
    void fromJson_oauth2ClientCredentials_shouldParseCorrectly() throws Exception {
        String json = """
                {
                    "authType": "oauth2ClientCredentials",
                    "tokenUrl": "https://auth.example.com/token",
                    "clientId": "my-client",
                    "clientSecret": "my-secret",
                    "scope": "read write",
                    "clientAuthMethod": "header"
                }
                """;
        JsonNode node = MAPPER.readTree(json);

        RestAuthConfig result = RestAuthConfig.fromJson(node, null);

        assertInstanceOf(RestAuthConfig.OAuth2ClientCredentials.class, result);
        RestAuthConfig.OAuth2ClientCredentials oauth2 = (RestAuthConfig.OAuth2ClientCredentials) result;
        assertEquals("https://auth.example.com/token", oauth2.tokenUrl());
        assertEquals("my-client", oauth2.clientId());
        assertEquals("my-secret", oauth2.clientSecret());
        assertEquals("read write", oauth2.scope());
        assertEquals(RestOAuth2ClientAuthMethod.HEADER, oauth2.clientAuthMethod());
    }

    @Test
    void fromJson_customAuth_shouldParseHeaders() throws Exception {
        String json = """
                {
                    "authType": "custom",
                    "headers": {
                        "X-Custom": "value1",
                        "X-Another": "value2"
                    }
                }
                """;
        JsonNode node = MAPPER.readTree(json);

        RestAuthConfig result = RestAuthConfig.fromJson(node, null);

        assertInstanceOf(RestAuthConfig.CustomAuth.class, result);
        RestAuthConfig.CustomAuth customAuth = (RestAuthConfig.CustomAuth) result;
        assertEquals(2, customAuth.headers().size());
        assertEquals("value1", customAuth.headers().get("X-Custom"));
    }

    @Test
    void fromJson_unknownAuthType_shouldReturnNoAuth() throws Exception {
        String json = """
                {
                    "authType": "unknown"
                }
                """;
        JsonNode node = MAPPER.readTree(json);

        RestAuthConfig result = RestAuthConfig.fromJson(node, null);

        assertInstanceOf(RestAuthConfig.NoAuth.class, result);
    }
}
