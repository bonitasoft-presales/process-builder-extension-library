package com.bonitasoft.processbuilder.records;

import com.bonitasoft.processbuilder.enums.RestApiKeyLocation;
import com.bonitasoft.processbuilder.enums.RestAuthenticationType;
import com.bonitasoft.processbuilder.enums.RestOAuth2ClientAuthMethod;
import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

import java.util.Base64;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

/**
 * Property-based tests for {@link RestAuthConfig} sealed interface and implementations.
 * Tests invariants that must hold for any valid input.
 *
 * @author Bonitasoft
 * @since 1.0
 */
@Label("RestAuthConfig Property-Based Tests")
class RestAuthConfigPropertyTest {

    // =========================================================================
    // NO AUTH PROPERTIES
    // =========================================================================

    @Property(tries = 100)
    @Label("NoAuth should always return NONE auth type")
    void noAuthShouldReturnNoneAuthType() {
        RestAuthConfig.NoAuth noAuth = RestAuthConfig.none();
        assertThat(noAuth.getAuthType()).isEqualTo(RestAuthenticationType.NONE);
    }

    @Property(tries = 100)
    @Label("NoAuth should always return empty headers")
    void noAuthShouldReturnEmptyHeaders() {
        RestAuthConfig.NoAuth noAuth = RestAuthConfig.none();
        assertThat(noAuth.getAuthHeaders()).isEmpty();
        assertThat(noAuth.getAuthQueryParams()).isEmpty();
    }

    // =========================================================================
    // BASIC AUTH PROPERTIES
    // =========================================================================

    @Property(tries = 500)
    @Label("BasicAuth should encode credentials correctly")
    void basicAuthShouldEncodeCredentialsCorrectly(
            @ForAll @StringLength(min = 1, max = 50) @AlphaChars String username,
            @ForAll @StringLength(min = 1, max = 50) @AlphaChars String password) {
        RestAuthConfig.BasicAuth basicAuth = RestAuthConfig.basic(username, password);

        Map<String, String> headers = basicAuth.getAuthHeaders();
        assertThat(headers).hasSize(1);
        assertThat(headers).containsKey("Authorization");

        String expectedEncoded = Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
        assertThat(headers.get("Authorization")).isEqualTo("Basic " + expectedEncoded);
    }

    @Property(tries = 100)
    @Label("BasicAuth should handle null username gracefully")
    void basicAuthShouldHandleNullUsername() {
        RestAuthConfig.BasicAuth basicAuth = new RestAuthConfig.BasicAuth(null, "pass", true);
        assertThat(basicAuth.username()).isEmpty();
        assertThatCode(basicAuth::getAuthHeaders).doesNotThrowAnyException();
    }

    @Property(tries = 100)
    @Label("BasicAuth should handle null password gracefully")
    void basicAuthShouldHandleNullPassword() {
        RestAuthConfig.BasicAuth basicAuth = new RestAuthConfig.BasicAuth("user", null, true);
        assertThat(basicAuth.password()).isEmpty();
        assertThatCode(basicAuth::getAuthHeaders).doesNotThrowAnyException();
    }

    @Property(tries = 100)
    @Label("BasicAuth should return BASIC auth type")
    void basicAuthShouldReturnBasicAuthType() {
        RestAuthConfig.BasicAuth basicAuth = RestAuthConfig.basic("user", "pass");
        assertThat(basicAuth.getAuthType()).isEqualTo(RestAuthenticationType.BASIC);
    }

    // =========================================================================
    // BEARER AUTH PROPERTIES
    // =========================================================================

    @Property(tries = 500)
    @Label("BearerAuth should format token correctly")
    void bearerAuthShouldFormatTokenCorrectly(
            @ForAll @StringLength(min = 1, max = 200) String token) {
        RestAuthConfig.BearerAuth bearerAuth = RestAuthConfig.bearer(token);

        Map<String, String> headers = bearerAuth.getAuthHeaders();
        assertThat(headers).hasSize(1);
        assertThat(headers.get("Authorization")).isEqualTo("Bearer " + token);
    }

    @Property(tries = 100)
    @Label("BearerAuth should handle null token gracefully")
    void bearerAuthShouldHandleNullToken() {
        RestAuthConfig.BearerAuth bearerAuth = new RestAuthConfig.BearerAuth(null);
        assertThat(bearerAuth.token()).isEmpty();
        assertThatCode(bearerAuth::getAuthHeaders).doesNotThrowAnyException();
    }

    @Property(tries = 100)
    @Label("BearerAuth should return BEARER auth type")
    void bearerAuthShouldReturnBearerAuthType() {
        RestAuthConfig.BearerAuth bearerAuth = RestAuthConfig.bearer("token");
        assertThat(bearerAuth.getAuthType()).isEqualTo(RestAuthenticationType.BEARER);
    }

    // =========================================================================
    // API KEY AUTH PROPERTIES
    // =========================================================================

    @Property(tries = 500)
    @Label("ApiKeyAuth with HEADER location should add header")
    void apiKeyAuthWithHeaderLocationShouldAddHeader(
            @ForAll @StringLength(min = 1, max = 50) @AlphaChars String keyName,
            @ForAll @StringLength(min = 1, max = 100) String keyValue) {
        RestAuthConfig.ApiKeyAuth apiKeyAuth = RestAuthConfig.apiKey(keyName, keyValue, RestApiKeyLocation.HEADER);

        Map<String, String> headers = apiKeyAuth.getAuthHeaders();
        assertThat(headers).hasSize(1);
        assertThat(headers.get(keyName)).isEqualTo(keyValue);
        assertThat(apiKeyAuth.getAuthQueryParams()).isEmpty();
    }

    @Property(tries = 500)
    @Label("ApiKeyAuth with QUERY_PARAM location should add query param")
    void apiKeyAuthWithQueryLocationShouldAddQueryParam(
            @ForAll @StringLength(min = 1, max = 50) @AlphaChars String keyName,
            @ForAll @StringLength(min = 1, max = 100) String keyValue) {
        RestAuthConfig.ApiKeyAuth apiKeyAuth = RestAuthConfig.apiKey(keyName, keyValue, RestApiKeyLocation.QUERY_PARAM);

        Map<String, String> queryParams = apiKeyAuth.getAuthQueryParams();
        assertThat(queryParams).hasSize(1);
        assertThat(queryParams.get(keyName)).isEqualTo(keyValue);
        assertThat(apiKeyAuth.getAuthHeaders()).isEmpty();
    }

    @Property(tries = 100)
    @Label("ApiKeyAuth should default to HEADER location")
    void apiKeyAuthShouldDefaultToHeaderLocation() {
        RestAuthConfig.ApiKeyAuth apiKeyAuth = new RestAuthConfig.ApiKeyAuth("key", "value", null);
        assertThat(apiKeyAuth.location()).isEqualTo(RestApiKeyLocation.HEADER);
    }

    @Property(tries = 100)
    @Label("ApiKeyAuth should default key name to X-API-Key")
    void apiKeyAuthShouldDefaultKeyName() {
        RestAuthConfig.ApiKeyAuth apiKeyAuth = new RestAuthConfig.ApiKeyAuth(null, "value", RestApiKeyLocation.HEADER);
        assertThat(apiKeyAuth.keyName()).isEqualTo("X-API-Key");
    }

    @Property(tries = 100)
    @Label("ApiKeyAuth should return API_KEY auth type")
    void apiKeyAuthShouldReturnApiKeyAuthType() {
        RestAuthConfig.ApiKeyAuth apiKeyAuth = RestAuthConfig.apiKey("key", "value", RestApiKeyLocation.HEADER);
        assertThat(apiKeyAuth.getAuthType()).isEqualTo(RestAuthenticationType.API_KEY);
    }

    // =========================================================================
    // OAUTH2 CLIENT CREDENTIALS PROPERTIES
    // =========================================================================

    @Property(tries = 200)
    @Label("OAuth2ClientCredentials should build correct token request body with BODY method")
    void oauth2ClientCredentialsShouldBuildCorrectBodyForBodyMethod(
            @ForAll @StringLength(min = 1, max = 50) @AlphaChars String clientId,
            @ForAll @StringLength(min = 1, max = 50) @AlphaChars String clientSecret) {
        RestAuthConfig.OAuth2ClientCredentials oauth2 = new RestAuthConfig.OAuth2ClientCredentials(
                "https://auth.example.com/token", clientId, clientSecret, null, null, RestOAuth2ClientAuthMethod.BODY);

        String body = oauth2.getTokenRequestBody();
        assertThat(body).contains("grant_type=client_credentials");
        assertThat(body).contains("client_id=" + clientId);
        assertThat(body).contains("client_secret=" + clientSecret);
    }

    @Property(tries = 200)
    @Label("OAuth2ClientCredentials should include scope and audience when provided")
    void oauth2ClientCredentialsShouldIncludeScopeAndAudience() {
        RestAuthConfig.OAuth2ClientCredentials oauth2 = new RestAuthConfig.OAuth2ClientCredentials(
                "https://auth.example.com/token", "client", "secret", "read write", "https://api", RestOAuth2ClientAuthMethod.BODY);

        String body = oauth2.getTokenRequestBody();
        assertThat(body).contains("scope=read");
        assertThat(body).contains("audience=https");
    }

    @Property(tries = 100)
    @Label("OAuth2ClientCredentials should return OAUTH2_CLIENT_CREDENTIALS auth type")
    void oauth2ClientCredentialsShouldReturnCorrectAuthType() {
        RestAuthConfig.OAuth2ClientCredentials oauth2 = RestAuthConfig.oauth2ClientCredentials(
                "https://auth.example.com/token", "client", "secret");
        assertThat(oauth2.getAuthType()).isEqualTo(RestAuthenticationType.OAUTH2_CLIENT_CREDENTIALS);
    }

    @Property(tries = 100)
    @Label("OAuth2ClientCredentials with HEADER method should generate Basic auth for token request")
    void oauth2ClientCredentialsWithHeaderMethodShouldGenerateBasicAuth() {
        RestAuthConfig.OAuth2ClientCredentials oauth2 = new RestAuthConfig.OAuth2ClientCredentials(
                "https://auth.example.com/token", "client", "secret", null, null, RestOAuth2ClientAuthMethod.HEADER);

        Map<String, String> headers = oauth2.getTokenRequestHeaders();
        assertThat(headers).containsKey("Authorization");
        assertThat(headers.get("Authorization")).startsWith("Basic ");
        assertThat(headers.get("Content-Type")).isEqualTo("application/x-www-form-urlencoded");
    }

    // =========================================================================
    // OAUTH2 PASSWORD PROPERTIES
    // =========================================================================

    @Property(tries = 200)
    @Label("OAuth2Password should build correct token request body")
    void oauth2PasswordShouldBuildCorrectBody(
            @ForAll @StringLength(min = 1, max = 30) @AlphaChars String username,
            @ForAll @StringLength(min = 1, max = 30) @AlphaChars String password) {
        RestAuthConfig.OAuth2Password oauth2 = new RestAuthConfig.OAuth2Password(
                "https://auth.example.com/token", "client", "secret", username, password, null);

        String body = oauth2.getTokenRequestBody();
        assertThat(body).contains("grant_type=password");
        assertThat(body).contains("username=" + username);
        assertThat(body).contains("password=" + password);
        assertThat(body).contains("client_id=client");
    }

    @Property(tries = 100)
    @Label("OAuth2Password should return OAUTH2_PASSWORD auth type")
    void oauth2PasswordShouldReturnCorrectAuthType() {
        RestAuthConfig.OAuth2Password oauth2 = new RestAuthConfig.OAuth2Password(
                "https://auth.example.com/token", "client", null, "user", "pass", null);
        assertThat(oauth2.getAuthType()).isEqualTo(RestAuthenticationType.OAUTH2_PASSWORD);
    }

    // =========================================================================
    // CUSTOM AUTH PROPERTIES
    // =========================================================================

    @Property(tries = 200)
    @Label("CustomAuth should return provided headers")
    void customAuthShouldReturnProvidedHeaders(
            @ForAll @StringLength(min = 1, max = 30) @AlphaChars String headerName,
            @ForAll @StringLength(min = 1, max = 50) String headerValue) {
        Map<String, String> customHeaders = Map.of(headerName, headerValue);
        RestAuthConfig.CustomAuth customAuth = new RestAuthConfig.CustomAuth(customHeaders);

        assertThat(customAuth.getAuthHeaders()).isEqualTo(customHeaders);
    }

    @Property(tries = 100)
    @Label("CustomAuth should handle null headers gracefully")
    void customAuthShouldHandleNullHeaders() {
        RestAuthConfig.CustomAuth customAuth = new RestAuthConfig.CustomAuth(null);
        assertThat(customAuth.headers()).isEmpty();
        assertThat(customAuth.getAuthHeaders()).isEmpty();
    }

    @Property(tries = 100)
    @Label("CustomAuth should return CUSTOM auth type")
    void customAuthShouldReturnCustomAuthType() {
        RestAuthConfig.CustomAuth customAuth = new RestAuthConfig.CustomAuth(Map.of("X-Custom", "value"));
        assertThat(customAuth.getAuthType()).isEqualTo(RestAuthenticationType.CUSTOM);
    }

    // =========================================================================
    // FACTORY METHOD PROPERTIES
    // =========================================================================

    @Property(tries = 100)
    @Label("Factory method none() should create NoAuth")
    void factoryNoneShouldCreateNoAuth() {
        RestAuthConfig config = RestAuthConfig.none();
        assertThat(config).isInstanceOf(RestAuthConfig.NoAuth.class);
    }

    @Property(tries = 500)
    @Label("Factory method basic() should create BasicAuth")
    void factoryBasicShouldCreateBasicAuth(
            @ForAll @StringLength(min = 1, max = 30) String user,
            @ForAll @StringLength(min = 1, max = 30) String pass) {
        RestAuthConfig config = RestAuthConfig.basic(user, pass);
        assertThat(config).isInstanceOf(RestAuthConfig.BasicAuth.class);
        assertThat(((RestAuthConfig.BasicAuth) config).username()).isEqualTo(user);
    }

    @Property(tries = 500)
    @Label("Factory method bearer() should create BearerAuth")
    void factoryBearerShouldCreateBearerAuth(@ForAll @StringLength(min = 1, max = 100) String token) {
        RestAuthConfig config = RestAuthConfig.bearer(token);
        assertThat(config).isInstanceOf(RestAuthConfig.BearerAuth.class);
        assertThat(((RestAuthConfig.BearerAuth) config).token()).isEqualTo(token);
    }

    @Property(tries = 200)
    @Label("Factory method apiKey() should create ApiKeyAuth")
    void factoryApiKeyShouldCreateApiKeyAuth(
            @ForAll @From("locations") RestApiKeyLocation location) {
        RestAuthConfig config = RestAuthConfig.apiKey("X-Key", "value", location);
        assertThat(config).isInstanceOf(RestAuthConfig.ApiKeyAuth.class);
        assertThat(((RestAuthConfig.ApiKeyAuth) config).location()).isEqualTo(location);
    }

    // =========================================================================
    // ARBITRARIES (DATA GENERATORS)
    // =========================================================================

    @Provide
    Arbitrary<RestApiKeyLocation> locations() {
        return Arbitraries.of(RestApiKeyLocation.values());
    }
}
