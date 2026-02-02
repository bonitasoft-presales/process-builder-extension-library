package com.bonitasoft.processbuilder.extension;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link RestServiceConstants} class.
 *
 * @author Bonitasoft
 * @since 1.0
 */
class RestServiceConstantsTest {

    // =========================================================================
    // Private Constructor Test
    // =========================================================================

    @Test
    void constructor_shouldThrowException() throws Exception {
        Constructor<RestServiceConstants> constructor = RestServiceConstants.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        InvocationTargetException exception = assertThrows(InvocationTargetException.class, constructor::newInstance);
        assertInstanceOf(UnsupportedOperationException.class, exception.getCause());
    }

    // =========================================================================
    // Timeout Constants Tests
    // =========================================================================

    @Test
    void timeoutConstants_shouldHaveValidValues() {
        assertTrue(RestServiceConstants.MIN_TIMEOUT_MS > 0);
        assertTrue(RestServiceConstants.DEFAULT_CONNECT_TIMEOUT_MS >= RestServiceConstants.MIN_TIMEOUT_MS);
        assertTrue(RestServiceConstants.DEFAULT_READ_TIMEOUT_MS >= RestServiceConstants.MIN_TIMEOUT_MS);
        assertTrue(RestServiceConstants.MAX_TIMEOUT_MS >= RestServiceConstants.DEFAULT_READ_TIMEOUT_MS);
    }

    @Test
    void timeoutConstants_shouldHaveExpectedValues() {
        assertEquals(1_000, RestServiceConstants.MIN_TIMEOUT_MS);
        assertEquals(10_000, RestServiceConstants.DEFAULT_CONNECT_TIMEOUT_MS);
        assertEquals(30_000, RestServiceConstants.DEFAULT_READ_TIMEOUT_MS);
        assertEquals(300_000, RestServiceConstants.MAX_TIMEOUT_MS);
    }

    // =========================================================================
    // Header Constants Tests
    // =========================================================================

    @Test
    void headerConstants_shouldHaveCorrectValues() {
        assertEquals("Accept", RestServiceConstants.HEADER_ACCEPT);
        assertEquals("Content-Type", RestServiceConstants.HEADER_CONTENT_TYPE);
        assertEquals("Authorization", RestServiceConstants.HEADER_AUTHORIZATION);
        assertEquals("User-Agent", RestServiceConstants.HEADER_USER_AGENT);
        assertEquals("X-Request-ID", RestServiceConstants.HEADER_REQUEST_ID);
        assertEquals("X-Correlation-ID", RestServiceConstants.HEADER_CORRELATION_ID);
    }

    @Test
    void defaultUserAgent_shouldBeNonEmpty() {
        assertNotNull(RestServiceConstants.DEFAULT_USER_AGENT);
        assertFalse(RestServiceConstants.DEFAULT_USER_AGENT.isBlank());
        assertTrue(RestServiceConstants.DEFAULT_USER_AGENT.contains("Bonita"));
    }

    // =========================================================================
    // Content Type Constants Tests
    // =========================================================================

    @Test
    void contentTypeConstants_shouldHaveCorrectValues() {
        assertEquals("application/json", RestServiceConstants.CONTENT_TYPE_JSON);
        assertEquals("application/json; charset=utf-8", RestServiceConstants.CONTENT_TYPE_JSON_UTF8);
        assertEquals("application/xml", RestServiceConstants.CONTENT_TYPE_XML);
        assertEquals("application/x-www-form-urlencoded", RestServiceConstants.CONTENT_TYPE_FORM);
        assertEquals("text/plain", RestServiceConstants.CONTENT_TYPE_TEXT);
    }

    // =========================================================================
    // OAuth2 Constants Tests
    // =========================================================================

    @Test
    void oauth2GrantTypes_shouldHaveCorrectValues() {
        assertEquals("client_credentials", RestServiceConstants.OAUTH2_GRANT_CLIENT_CREDENTIALS);
        assertEquals("password", RestServiceConstants.OAUTH2_GRANT_PASSWORD);
        assertEquals("refresh_token", RestServiceConstants.OAUTH2_GRANT_REFRESH_TOKEN);
    }

    @Test
    void oauth2TokenFields_shouldHaveCorrectValues() {
        assertEquals("access_token", RestServiceConstants.OAUTH2_ACCESS_TOKEN);
        assertEquals("token_type", RestServiceConstants.OAUTH2_TOKEN_TYPE);
        assertEquals("expires_in", RestServiceConstants.OAUTH2_EXPIRES_IN);
        assertEquals("refresh_token", RestServiceConstants.OAUTH2_REFRESH_TOKEN);
        assertEquals("scope", RestServiceConstants.OAUTH2_SCOPE);
    }

    // =========================================================================
    // JSON Field Name Constants Tests
    // =========================================================================

    @Test
    void jsonFieldNames_shouldHaveCorrectValues() {
        assertEquals("url", RestServiceConstants.JSON_URL);
        assertEquals("method", RestServiceConstants.JSON_METHOD);
        assertEquals("headers", RestServiceConstants.JSON_HEADERS);
        assertEquals("queryParams", RestServiceConstants.JSON_QUERY_PARAMS);
        assertEquals("body", RestServiceConstants.JSON_BODY);
        assertEquals("contentType", RestServiceConstants.JSON_CONTENT_TYPE);
        assertEquals("auth", RestServiceConstants.JSON_AUTH);
        assertEquals("authType", RestServiceConstants.JSON_AUTH_TYPE);
        assertEquals("timeoutMs", RestServiceConstants.JSON_TIMEOUT_MS);
        assertEquals("followRedirects", RestServiceConstants.JSON_FOLLOW_REDIRECTS);
        assertEquals("verifySsl", RestServiceConstants.JSON_VERIFY_SSL);
        assertEquals("statusCode", RestServiceConstants.JSON_STATUS_CODE);
        assertEquals("errorMessage", RestServiceConstants.JSON_ERROR_MESSAGE);
        assertEquals("executionTimeMs", RestServiceConstants.JSON_EXECUTION_TIME_MS);
    }

    @Test
    void authFieldNames_shouldHaveCorrectValues() {
        assertEquals("username", RestServiceConstants.JSON_USERNAME);
        assertEquals("password", RestServiceConstants.JSON_PASSWORD);
        assertEquals("token", RestServiceConstants.JSON_TOKEN);
        assertEquals("keyName", RestServiceConstants.JSON_KEY_NAME);
        assertEquals("keyValue", RestServiceConstants.JSON_KEY_VALUE);
        assertEquals("location", RestServiceConstants.JSON_LOCATION);
        assertEquals("tokenUrl", RestServiceConstants.JSON_TOKEN_URL);
        assertEquals("clientId", RestServiceConstants.JSON_CLIENT_ID);
        assertEquals("clientSecret", RestServiceConstants.JSON_CLIENT_SECRET);
        assertEquals("scope", RestServiceConstants.JSON_SCOPE);
        assertEquals("audience", RestServiceConstants.JSON_AUDIENCE);
    }

    // =========================================================================
    // HTTP Status Codes Tests
    // =========================================================================

    @Test
    void httpStatusCodes_shouldHaveCorrectValues() {
        assertEquals(200, RestServiceConstants.HTTP_OK);
        assertEquals(201, RestServiceConstants.HTTP_CREATED);
        assertEquals(204, RestServiceConstants.HTTP_NO_CONTENT);
        assertEquals(400, RestServiceConstants.HTTP_BAD_REQUEST);
        assertEquals(401, RestServiceConstants.HTTP_UNAUTHORIZED);
        assertEquals(403, RestServiceConstants.HTTP_FORBIDDEN);
        assertEquals(404, RestServiceConstants.HTTP_NOT_FOUND);
        assertEquals(500, RestServiceConstants.HTTP_INTERNAL_ERROR);
        assertEquals(502, RestServiceConstants.HTTP_BAD_GATEWAY);
        assertEquals(503, RestServiceConstants.HTTP_SERVICE_UNAVAILABLE);
    }

    // =========================================================================
    // Default Headers Maps Tests
    // =========================================================================

    @Test
    void defaultJsonHeaders_shouldContainExpectedHeaders() {
        Map<String, String> headers = RestServiceConstants.DEFAULT_JSON_HEADERS;

        assertEquals(3, headers.size());
        assertEquals(RestServiceConstants.CONTENT_TYPE_JSON, headers.get(RestServiceConstants.HEADER_ACCEPT));
        assertEquals(RestServiceConstants.CONTENT_TYPE_JSON_UTF8, headers.get(RestServiceConstants.HEADER_CONTENT_TYPE));
        assertEquals(RestServiceConstants.DEFAULT_USER_AGENT, headers.get(RestServiceConstants.HEADER_USER_AGENT));
    }

    @Test
    void defaultJsonHeaders_shouldBeImmutable() {
        Map<String, String> headers = RestServiceConstants.DEFAULT_JSON_HEADERS;
        assertThrows(UnsupportedOperationException.class, () -> headers.put("New", "Value"));
    }

    @Test
    void defaultXmlHeaders_shouldContainExpectedHeaders() {
        Map<String, String> headers = RestServiceConstants.DEFAULT_XML_HEADERS;

        assertEquals(3, headers.size());
        assertEquals(RestServiceConstants.CONTENT_TYPE_XML, headers.get(RestServiceConstants.HEADER_ACCEPT));
        assertEquals(RestServiceConstants.CONTENT_TYPE_XML, headers.get(RestServiceConstants.HEADER_CONTENT_TYPE));
        assertEquals(RestServiceConstants.DEFAULT_USER_AGENT, headers.get(RestServiceConstants.HEADER_USER_AGENT));
    }

    @Test
    void defaultXmlHeaders_shouldBeImmutable() {
        Map<String, String> headers = RestServiceConstants.DEFAULT_XML_HEADERS;
        assertThrows(UnsupportedOperationException.class, () -> headers.put("New", "Value"));
    }
}
