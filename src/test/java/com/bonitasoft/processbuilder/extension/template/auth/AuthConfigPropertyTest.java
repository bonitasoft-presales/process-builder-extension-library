package com.bonitasoft.processbuilder.extension.template.auth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

/**
 * Property-based tests for all {@link AuthConfig} implementations.
 *
 * @author Bonitasoft
 * @since 1.0
 */
@Label("AuthConfig Property-Based Tests")
class AuthConfigPropertyTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    // =========================================================================
    // INVARIANT PROPERTIES - ALL AUTH CONFIGS
    // =========================================================================

    @Property(tries = 100)
    @Label("getAuthType should never return null for any AuthConfig")
    void getAuthType_should_never_return_null(@ForAll @From("allAuthConfigs") AuthConfig config) {
        assertThat(config.getAuthType()).isNotNull();
    }

    @Property(tries = 100)
    @Label("getAuthType should never return blank for any AuthConfig")
    void getAuthType_should_never_return_blank(@ForAll @From("allAuthConfigs") AuthConfig config) {
        assertThat(config.getAuthType()).isNotBlank();
    }

    @Property(tries = 100)
    @Label("toJson should never return null for any AuthConfig")
    void toJson_should_never_return_null(@ForAll @From("allAuthConfigs") AuthConfig config) {
        assertThat(config.toJson(MAPPER)).isNotNull();
    }

    @Property(tries = 100)
    @Label("toJson should always return object node for any AuthConfig")
    void toJson_should_always_return_object_node(@ForAll @From("allAuthConfigs") AuthConfig config) {
        assertThat(config.toJson(MAPPER).isObject()).isTrue();
    }

    @Property(tries = 100)
    @Label("toJson should always include authType field")
    void toJson_should_always_include_authType(@ForAll @From("allAuthConfigs") AuthConfig config) {
        JsonNode json = config.toJson(MAPPER);
        assertThat(json.has("authType")).isTrue();
        assertThat(json.get("authType").asText()).isEqualTo(config.getAuthType());
    }

    @Property(tries = 100)
    @Label("toJsonEncrypted should never return null")
    void toJsonEncrypted_should_never_return_null(@ForAll @From("allAuthConfigs") AuthConfig config) {
        assertThat(config.toJsonEncrypted(MAPPER)).isNotNull();
    }

    @Property(tries = 100)
    @Label("toJsonEncrypted should always include authType field")
    void toJsonEncrypted_should_always_include_authType(@ForAll @From("allAuthConfigs") AuthConfig config) {
        JsonNode json = config.toJsonEncrypted(MAPPER);
        assertThat(json.has("authType")).isTrue();
    }

    // =========================================================================
    // NoAuthConfig PROPERTIES
    // =========================================================================

    @Property(tries = 50)
    @Label("NoAuthConfig singleton should always be the same instance")
    void noAuthConfig_should_be_singleton() {
        assertThat(NoAuthConfig.INSTANCE).isSameAs(NoAuthConfig.INSTANCE);
    }

    @Property(tries = 50)
    @Label("NoAuthConfig getAuthType should always return 'none'")
    void noAuthConfig_authType_should_be_none() {
        assertThat(NoAuthConfig.INSTANCE.getAuthType()).isEqualTo("none");
    }

    @Property(tries = 50)
    @Label("NoAuthConfig toJson and toJsonEncrypted should be identical")
    void noAuthConfig_toJson_should_equal_toJsonEncrypted() {
        assertThat(NoAuthConfig.INSTANCE.toJson(MAPPER))
                .isEqualTo(NoAuthConfig.INSTANCE.toJsonEncrypted(MAPPER));
    }

    // =========================================================================
    // BasicAuthConfig PROPERTIES
    // =========================================================================

    @Property(tries = 200)
    @Label("BasicAuthConfig should preserve username and password")
    void basicAuthConfig_should_preserve_credentials(
            @ForAll @StringLength(min = 1, max = 50) @AlphaChars String username,
            @ForAll @StringLength(min = 1, max = 50) @AlphaChars String password) {
        BasicAuthConfig config = new BasicAuthConfig(username, password);

        assertThat(config.username()).isEqualTo(username);
        assertThat(config.password()).isEqualTo(password);
    }

    @Property(tries = 100)
    @Label("BasicAuthConfig toJson should include username and password")
    void basicAuthConfig_toJson_should_include_credentials(
            @ForAll @StringLength(min = 1, max = 50) @AlphaChars String username,
            @ForAll @StringLength(min = 1, max = 50) @AlphaChars String password) {
        BasicAuthConfig config = new BasicAuthConfig(username, password);
        JsonNode json = config.toJson(MAPPER);

        assertThat(json.get("username").asText()).isEqualTo(username);
        assertThat(json.get("password").asText()).isEqualTo(password);
    }

    @Property(tries = 100)
    @Label("BasicAuthConfig equals should be reflexive")
    void basicAuthConfig_equals_should_be_reflexive(
            @ForAll @StringLength(min = 1, max = 30) @AlphaChars String username,
            @ForAll @StringLength(min = 1, max = 30) @AlphaChars String password) {
        BasicAuthConfig config = new BasicAuthConfig(username, password);
        assertThat(config).isEqualTo(config);
    }

    @Property(tries = 100)
    @Label("BasicAuthConfig hashCode should be consistent")
    void basicAuthConfig_hashCode_should_be_consistent(
            @ForAll @StringLength(min = 1, max = 30) @AlphaChars String username,
            @ForAll @StringLength(min = 1, max = 30) @AlphaChars String password) {
        BasicAuthConfig config = new BasicAuthConfig(username, password);
        assertThat(config.hashCode()).isEqualTo(config.hashCode());
    }

    // =========================================================================
    // BearerAuthConfig PROPERTIES
    // =========================================================================

    @Property(tries = 200)
    @Label("BearerAuthConfig should preserve token")
    void bearerAuthConfig_should_preserve_token(
            @ForAll @StringLength(min = 1, max = 100) @AlphaChars String token) {
        BearerAuthConfig config = new BearerAuthConfig(token);

        assertThat(config.token()).isEqualTo(token);
    }

    @Property(tries = 100)
    @Label("BearerAuthConfig.of() should create same as constructor")
    void bearerAuthConfig_of_should_create_same_as_constructor(
            @ForAll @StringLength(min = 1, max = 100) @AlphaChars String token) {
        BearerAuthConfig fromConstructor = new BearerAuthConfig(token);
        BearerAuthConfig fromFactory = BearerAuthConfig.of(token);

        assertThat(fromConstructor).isEqualTo(fromFactory);
    }

    // =========================================================================
    // ApiKeyAuthConfig PROPERTIES
    // =========================================================================

    @Property(tries = 200)
    @Label("ApiKeyAuthConfig should preserve keyName and keyValue")
    void apiKeyAuthConfig_should_preserve_key(
            @ForAll @StringLength(min = 1, max = 30) @AlphaChars String keyName,
            @ForAll @StringLength(min = 1, max = 50) @AlphaChars String keyValue) {
        ApiKeyAuthConfig config = new ApiKeyAuthConfig(keyName, keyValue);

        assertThat(config.keyName()).isEqualTo(keyName);
        assertThat(config.keyValue()).isEqualTo(keyValue);
    }

    @Property(tries = 100)
    @Label("ApiKeyAuthConfig.header() should set HEADER location")
    void apiKeyAuthConfig_header_should_set_header_location(
            @ForAll @StringLength(min = 1, max = 30) @AlphaChars String keyName,
            @ForAll @StringLength(min = 1, max = 50) @AlphaChars String keyValue) {
        ApiKeyAuthConfig config = ApiKeyAuthConfig.header(keyName, keyValue);

        assertThat(config.location()).isEqualTo(ApiKeyAuthConfig.Location.HEADER);
    }

    @Property(tries = 100)
    @Label("ApiKeyAuthConfig.queryParam() should set QUERY location")
    void apiKeyAuthConfig_queryParam_should_set_query_location(
            @ForAll @StringLength(min = 1, max = 30) @AlphaChars String keyName,
            @ForAll @StringLength(min = 1, max = 50) @AlphaChars String keyValue) {
        ApiKeyAuthConfig config = ApiKeyAuthConfig.queryParam(keyName, keyValue);

        assertThat(config.location()).isEqualTo(ApiKeyAuthConfig.Location.QUERY);
    }

    // =========================================================================
    // OAuth2ClientConfig PROPERTIES
    // =========================================================================

    @Property(tries = 100)
    @Label("OAuth2ClientConfig should preserve required fields")
    void oauth2ClientConfig_should_preserve_required_fields(
            @ForAll @StringLength(min = 5, max = 50) @AlphaChars String tokenUrl,
            @ForAll @StringLength(min = 1, max = 30) @AlphaChars String clientId,
            @ForAll @StringLength(min = 1, max = 30) @AlphaChars String clientSecret) {
        OAuth2ClientConfig config = new OAuth2ClientConfig(tokenUrl, clientId, clientSecret);

        assertThat(config.tokenUrl()).isEqualTo(tokenUrl);
        assertThat(config.clientId()).isEqualTo(clientId);
        assertThat(config.clientSecret()).isEqualTo(clientSecret);
    }

    @Property(tries = 100)
    @Label("OAuth2ClientConfig should default clientAuthMethod to BODY")
    void oauth2ClientConfig_should_default_clientAuthMethod_to_body(
            @ForAll @StringLength(min = 5, max = 50) @AlphaChars String tokenUrl,
            @ForAll @StringLength(min = 1, max = 30) @AlphaChars String clientId,
            @ForAll @StringLength(min = 1, max = 30) @AlphaChars String clientSecret) {
        OAuth2ClientConfig config = new OAuth2ClientConfig(tokenUrl, clientId, clientSecret);

        assertThat(config.clientAuthMethod()).isEqualTo(OAuth2ClientConfig.ClientAuthMethod.BODY);
    }

    // =========================================================================
    // OAuth2PasswordConfig PROPERTIES
    // =========================================================================

    @Property(tries = 100)
    @Label("OAuth2PasswordConfig should preserve required fields")
    void oauth2PasswordConfig_should_preserve_required_fields(
            @ForAll @StringLength(min = 5, max = 50) @AlphaChars String tokenUrl,
            @ForAll @StringLength(min = 1, max = 30) @AlphaChars String clientId,
            @ForAll @StringLength(min = 1, max = 30) @AlphaChars String username,
            @ForAll @StringLength(min = 1, max = 30) @AlphaChars String password) {
        OAuth2PasswordConfig config = new OAuth2PasswordConfig(tokenUrl, clientId, username, password);

        assertThat(config.tokenUrl()).isEqualTo(tokenUrl);
        assertThat(config.clientId()).isEqualTo(clientId);
        assertThat(config.username()).isEqualTo(username);
        assertThat(config.password()).isEqualTo(password);
    }

    // =========================================================================
    // JSON ROUND-TRIP PROPERTIES
    // =========================================================================

    @Property(tries = 100)
    @Label("BasicAuthConfig JSON round-trip should preserve data")
    void basicAuthConfig_json_roundtrip(
            @ForAll @StringLength(min = 1, max = 30) @AlphaChars String username,
            @ForAll @StringLength(min = 1, max = 30) @AlphaChars String password,
            @ForAll boolean preemptive) {
        BasicAuthConfig original = new BasicAuthConfig(username, password, preemptive);
        JsonNode json = original.toJson(MAPPER);
        BasicAuthConfig restored = BasicAuthConfig.fromJson(json);

        assertThat(restored).isEqualTo(original);
    }

    @Property(tries = 100)
    @Label("BearerAuthConfig JSON round-trip should preserve data")
    void bearerAuthConfig_json_roundtrip(
            @ForAll @StringLength(min = 1, max = 100) @AlphaChars String token) {
        BearerAuthConfig original = new BearerAuthConfig(token);
        JsonNode json = original.toJson(MAPPER);
        BearerAuthConfig restored = BearerAuthConfig.fromJson(json);

        assertThat(restored).isEqualTo(original);
    }

    @Property(tries = 100)
    @Label("ApiKeyAuthConfig JSON round-trip should preserve data")
    void apiKeyAuthConfig_json_roundtrip(
            @ForAll @StringLength(min = 1, max = 30) @AlphaChars String keyName,
            @ForAll @StringLength(min = 1, max = 50) @AlphaChars String keyValue,
            @ForAll @From("locations") ApiKeyAuthConfig.Location location) {
        ApiKeyAuthConfig original = new ApiKeyAuthConfig(keyName, keyValue, location);
        JsonNode json = original.toJson(MAPPER);
        ApiKeyAuthConfig restored = ApiKeyAuthConfig.fromJson(json);

        assertThat(restored).isEqualTo(original);
    }

    // =========================================================================
    // ARBITRARIES (DATA GENERATORS)
    // =========================================================================

    @Provide
    Arbitrary<AuthConfig> allAuthConfigs() {
        return Arbitraries.oneOf(
                Arbitraries.just(NoAuthConfig.INSTANCE),
                basicAuthConfigs(),
                bearerAuthConfigs(),
                apiKeyAuthConfigs(),
                oauth2ClientConfigs(),
                oauth2PasswordConfigs()
        );
    }

    @Provide
    Arbitrary<BasicAuthConfig> basicAuthConfigs() {
        Arbitrary<String> usernames = Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(30);
        Arbitrary<String> passwords = Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(30);
        Arbitrary<Boolean> preemptives = Arbitraries.of(true, false);

        return Combinators.combine(usernames, passwords, preemptives)
                .as(BasicAuthConfig::new);
    }

    @Provide
    Arbitrary<BearerAuthConfig> bearerAuthConfigs() {
        return Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(100)
                .map(BearerAuthConfig::new);
    }

    @Provide
    Arbitrary<ApiKeyAuthConfig> apiKeyAuthConfigs() {
        Arbitrary<String> keyNames = Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(30);
        Arbitrary<String> keyValues = Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(50);
        Arbitrary<ApiKeyAuthConfig.Location> locations = Arbitraries.of(ApiKeyAuthConfig.Location.values());

        return Combinators.combine(keyNames, keyValues, locations)
                .as(ApiKeyAuthConfig::new);
    }

    @Provide
    Arbitrary<OAuth2ClientConfig> oauth2ClientConfigs() {
        Arbitrary<String> tokenUrls = Arbitraries.strings().alpha().ofMinLength(5).ofMaxLength(50);
        Arbitrary<String> clientIds = Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(30);
        Arbitrary<String> clientSecrets = Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(30);

        return Combinators.combine(tokenUrls, clientIds, clientSecrets)
                .as(OAuth2ClientConfig::new);
    }

    @Provide
    Arbitrary<OAuth2PasswordConfig> oauth2PasswordConfigs() {
        Arbitrary<String> tokenUrls = Arbitraries.strings().alpha().ofMinLength(5).ofMaxLength(50);
        Arbitrary<String> clientIds = Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(30);
        Arbitrary<String> usernames = Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(30);
        Arbitrary<String> passwords = Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(30);

        return Combinators.combine(tokenUrls, clientIds, usernames, passwords)
                .as(OAuth2PasswordConfig::new);
    }

    @Provide
    Arbitrary<ApiKeyAuthConfig.Location> locations() {
        return Arbitraries.of(ApiKeyAuthConfig.Location.values());
    }
}
