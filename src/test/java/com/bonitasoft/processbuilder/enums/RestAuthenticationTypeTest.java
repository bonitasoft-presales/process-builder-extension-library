package com.bonitasoft.processbuilder.enums;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link RestAuthenticationType} enumeration.
 *
 * @author Bonitasoft
 * @since 1.0
 */
class RestAuthenticationTypeTest {

    // =========================================================================
    // ENUM VALUES TESTS
    // =========================================================================

    @Test
    void values_shouldContainAllExpectedConstants() {
        RestAuthenticationType[] values = RestAuthenticationType.values();
        assertEquals(10, values.length);
        assertNotNull(RestAuthenticationType.NONE);
        assertNotNull(RestAuthenticationType.BASIC);
        assertNotNull(RestAuthenticationType.BEARER);
        assertNotNull(RestAuthenticationType.API_KEY);
        assertNotNull(RestAuthenticationType.OAUTH2_CLIENT_CREDENTIALS);
        assertNotNull(RestAuthenticationType.OAUTH2_PASSWORD);
        assertNotNull(RestAuthenticationType.DIGEST);
        assertNotNull(RestAuthenticationType.NTLM);
        assertNotNull(RestAuthenticationType.CERTIFICATE);
        assertNotNull(RestAuthenticationType.CUSTOM);
    }

    // =========================================================================
    // KEY AND DESCRIPTION TESTS
    // =========================================================================

    @Test
    void getKey_shouldReturnCorrectKeyForEachType() {
        assertEquals("none", RestAuthenticationType.NONE.getKey());
        assertEquals("basic", RestAuthenticationType.BASIC.getKey());
        assertEquals("bearer", RestAuthenticationType.BEARER.getKey());
        assertEquals("apiKey", RestAuthenticationType.API_KEY.getKey());
        assertEquals("oauth2ClientCredentials", RestAuthenticationType.OAUTH2_CLIENT_CREDENTIALS.getKey());
        assertEquals("oauth2Password", RestAuthenticationType.OAUTH2_PASSWORD.getKey());
        assertEquals("digest", RestAuthenticationType.DIGEST.getKey());
        assertEquals("ntlm", RestAuthenticationType.NTLM.getKey());
        assertEquals("certificate", RestAuthenticationType.CERTIFICATE.getKey());
        assertEquals("custom", RestAuthenticationType.CUSTOM.getKey());
    }

    @Test
    void getDescription_shouldReturnNonEmptyDescription() {
        for (RestAuthenticationType type : RestAuthenticationType.values()) {
            assertNotNull(type.getDescription());
            assertFalse(type.getDescription().isBlank());
        }
    }

    // =========================================================================
    // IS VALID TESTS
    // =========================================================================

    @ParameterizedTest
    @ValueSource(strings = {"NONE", "none", "None", "BASIC", "basic", "BEARER", "bearer"})
    void isValid_shouldReturnTrueForValidInputs(String input) {
        assertTrue(RestAuthenticationType.isValid(input));
    }

    @ParameterizedTest
    @ValueSource(strings = {"apiKey", "oauth2ClientCredentials", "oauth2Password"})
    void isValid_shouldReturnTrueForKeyInputs(String input) {
        assertTrue(RestAuthenticationType.isValid(input));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "invalid", "UNKNOWN", "basicAuth", "jwt"})
    void isValid_shouldReturnFalseForInvalidInputs(String input) {
        assertFalse(RestAuthenticationType.isValid(input));
    }

    // =========================================================================
    // FROM KEY TESTS
    // =========================================================================

    @Test
    void fromKey_shouldReturnCorrectTypeForValidKey() {
        assertEquals(Optional.of(RestAuthenticationType.NONE), RestAuthenticationType.fromKey("none"));
        assertEquals(Optional.of(RestAuthenticationType.BASIC), RestAuthenticationType.fromKey("basic"));
        assertEquals(Optional.of(RestAuthenticationType.BEARER), RestAuthenticationType.fromKey("bearer"));
        assertEquals(Optional.of(RestAuthenticationType.API_KEY), RestAuthenticationType.fromKey("apiKey"));
        assertEquals(Optional.of(RestAuthenticationType.OAUTH2_CLIENT_CREDENTIALS),
                RestAuthenticationType.fromKey("oauth2ClientCredentials"));
    }

    @Test
    void fromKey_shouldBeCaseInsensitive() {
        assertEquals(Optional.of(RestAuthenticationType.BASIC), RestAuthenticationType.fromKey("BASIC"));
        assertEquals(Optional.of(RestAuthenticationType.BASIC), RestAuthenticationType.fromKey("Basic"));
        assertEquals(Optional.of(RestAuthenticationType.API_KEY), RestAuthenticationType.fromKey("APIKEY"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "invalid", "unknown"})
    void fromKey_shouldReturnEmptyForInvalidKey(String key) {
        assertTrue(RestAuthenticationType.fromKey(key).isEmpty());
    }

    // =========================================================================
    // REQUIRES CREDENTIALS TESTS
    // =========================================================================

    @Test
    void requiresCredentials_shouldReturnTrueForCredentialTypes() {
        assertTrue(RestAuthenticationType.BASIC.requiresCredentials());
        assertTrue(RestAuthenticationType.DIGEST.requiresCredentials());
        assertTrue(RestAuthenticationType.NTLM.requiresCredentials());
        assertTrue(RestAuthenticationType.OAUTH2_PASSWORD.requiresCredentials());
    }

    @Test
    void requiresCredentials_shouldReturnFalseForNonCredentialTypes() {
        assertFalse(RestAuthenticationType.NONE.requiresCredentials());
        assertFalse(RestAuthenticationType.BEARER.requiresCredentials());
        assertFalse(RestAuthenticationType.API_KEY.requiresCredentials());
        assertFalse(RestAuthenticationType.OAUTH2_CLIENT_CREDENTIALS.requiresCredentials());
        assertFalse(RestAuthenticationType.CERTIFICATE.requiresCredentials());
        assertFalse(RestAuthenticationType.CUSTOM.requiresCredentials());
    }

    // =========================================================================
    // REQUIRES OAUTH2 TOKEN EXCHANGE TESTS
    // =========================================================================

    @Test
    void requiresOAuth2TokenExchange_shouldReturnTrueForOAuth2Types() {
        assertTrue(RestAuthenticationType.OAUTH2_CLIENT_CREDENTIALS.requiresOAuth2TokenExchange());
        assertTrue(RestAuthenticationType.OAUTH2_PASSWORD.requiresOAuth2TokenExchange());
    }

    @Test
    void requiresOAuth2TokenExchange_shouldReturnFalseForNonOAuth2Types() {
        assertFalse(RestAuthenticationType.NONE.requiresOAuth2TokenExchange());
        assertFalse(RestAuthenticationType.BASIC.requiresOAuth2TokenExchange());
        assertFalse(RestAuthenticationType.BEARER.requiresOAuth2TokenExchange());
        assertFalse(RestAuthenticationType.API_KEY.requiresOAuth2TokenExchange());
        assertFalse(RestAuthenticationType.DIGEST.requiresOAuth2TokenExchange());
        assertFalse(RestAuthenticationType.NTLM.requiresOAuth2TokenExchange());
        assertFalse(RestAuthenticationType.CERTIFICATE.requiresOAuth2TokenExchange());
        assertFalse(RestAuthenticationType.CUSTOM.requiresOAuth2TokenExchange());
    }

    // =========================================================================
    // USES STATIC TOKEN TESTS
    // =========================================================================

    @Test
    void usesStaticToken_shouldReturnTrueForTokenTypes() {
        assertTrue(RestAuthenticationType.BEARER.usesStaticToken());
        assertTrue(RestAuthenticationType.API_KEY.usesStaticToken());
    }

    @Test
    void usesStaticToken_shouldReturnFalseForNonTokenTypes() {
        assertFalse(RestAuthenticationType.NONE.usesStaticToken());
        assertFalse(RestAuthenticationType.BASIC.usesStaticToken());
        assertFalse(RestAuthenticationType.OAUTH2_CLIENT_CREDENTIALS.usesStaticToken());
        assertFalse(RestAuthenticationType.OAUTH2_PASSWORD.usesStaticToken());
        assertFalse(RestAuthenticationType.DIGEST.usesStaticToken());
        assertFalse(RestAuthenticationType.NTLM.usesStaticToken());
        assertFalse(RestAuthenticationType.CERTIFICATE.usesStaticToken());
        assertFalse(RestAuthenticationType.CUSTOM.usesStaticToken());
    }

    // =========================================================================
    // COLLECTION METHODS TESTS
    // =========================================================================

    @Test
    void getAllData_shouldReturnMapWithAllConstants() {
        Map<String, String> data = RestAuthenticationType.getAllData();
        assertEquals(10, data.size());
        assertTrue(data.containsKey("none"));
        assertTrue(data.containsKey("basic"));
        assertTrue(data.containsKey("bearer"));
        assertTrue(data.containsKey("apiKey"));
    }

    @Test
    void getAllData_shouldReturnImmutableMap() {
        Map<String, String> data = RestAuthenticationType.getAllData();
        assertThrows(UnsupportedOperationException.class, () -> data.put("new", "value"));
        assertThrows(UnsupportedOperationException.class, data::clear);
    }

    @Test
    void getAllKeysList_shouldReturnListWithAllKeys() {
        List<String> keys = RestAuthenticationType.getAllKeysList();
        assertEquals(10, keys.size());
        assertTrue(keys.contains("none"));
        assertTrue(keys.contains("basic"));
        assertTrue(keys.contains("bearer"));
        assertTrue(keys.contains("apiKey"));
    }

    @Test
    void getAllKeysList_shouldReturnImmutableList() {
        List<String> keys = RestAuthenticationType.getAllKeysList();
        assertThrows(UnsupportedOperationException.class, () -> keys.add("new"));
        assertThrows(UnsupportedOperationException.class, keys::clear);
    }
}
