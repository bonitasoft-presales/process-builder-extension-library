package com.bonitasoft.processbuilder.extension;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

import java.util.Map;

import static org.assertj.core.api.Assertions.*;

/**
 * Property-based tests for {@link RestServiceConstants} utility class.
 * These tests verify invariants and relationships between constants.
 *
 * @author Bonitasoft
 * @since 1.0
 */
@Label("RestServiceConstants Property-Based Tests")
class RestServiceConstantsPropertyTest {

    // =========================================================================
    // TIMEOUT CONSTANTS INVARIANTS
    // =========================================================================

    @Property(tries = 100)
    @Label("MIN_TIMEOUT_MS should be positive and less than defaults")
    void minTimeoutShouldBePositiveAndLessThanDefaults() {
        assertThat(RestServiceConstants.MIN_TIMEOUT_MS)
                .isPositive()
                .isLessThan(RestServiceConstants.DEFAULT_CONNECT_TIMEOUT_MS)
                .isLessThan(RestServiceConstants.DEFAULT_READ_TIMEOUT_MS)
                .isLessThan(RestServiceConstants.MAX_TIMEOUT_MS);
    }

    @Property(tries = 100)
    @Label("MAX_TIMEOUT_MS should be greater than all other timeouts")
    void maxTimeoutShouldBeGreaterThanAllOthers() {
        assertThat(RestServiceConstants.MAX_TIMEOUT_MS)
                .isGreaterThan(RestServiceConstants.MIN_TIMEOUT_MS)
                .isGreaterThan(RestServiceConstants.DEFAULT_CONNECT_TIMEOUT_MS)
                .isGreaterThan(RestServiceConstants.DEFAULT_READ_TIMEOUT_MS);
    }

    @Property(tries = 100)
    @Label("Timeout constants should maintain valid hierarchy")
    void timeoutConstantsShouldMaintainValidHierarchy() {
        assertThat(RestServiceConstants.MIN_TIMEOUT_MS)
                .isLessThanOrEqualTo(RestServiceConstants.DEFAULT_CONNECT_TIMEOUT_MS);
        assertThat(RestServiceConstants.DEFAULT_CONNECT_TIMEOUT_MS)
                .isLessThanOrEqualTo(RestServiceConstants.DEFAULT_READ_TIMEOUT_MS);
        assertThat(RestServiceConstants.DEFAULT_READ_TIMEOUT_MS)
                .isLessThanOrEqualTo(RestServiceConstants.MAX_TIMEOUT_MS);
    }

    @Property(tries = 100)
    @Label("Timeout range should allow meaningful values")
    void timeoutRangeShouldAllowMeaningfulValues() {
        int range = RestServiceConstants.MAX_TIMEOUT_MS - RestServiceConstants.MIN_TIMEOUT_MS;
        assertThat(range).isGreaterThan(0);
        // Should allow at least 1 minute of range
        assertThat(range).isGreaterThan(60_000);
    }

    // =========================================================================
    // HEADER CONSTANTS INVARIANTS
    // =========================================================================

    @Property(tries = 100)
    @Label("All header constants should be non-null and non-blank")
    void allHeaderConstantsShouldBeNonNullAndNonBlank() {
        assertThat(RestServiceConstants.HEADER_ACCEPT).isNotNull().isNotBlank();
        assertThat(RestServiceConstants.HEADER_CONTENT_TYPE).isNotNull().isNotBlank();
        assertThat(RestServiceConstants.HEADER_AUTHORIZATION).isNotNull().isNotBlank();
        assertThat(RestServiceConstants.HEADER_USER_AGENT).isNotNull().isNotBlank();
        assertThat(RestServiceConstants.HEADER_REQUEST_ID).isNotNull().isNotBlank();
        assertThat(RestServiceConstants.HEADER_CORRELATION_ID).isNotNull().isNotBlank();
    }

    @Property(tries = 100)
    @Label("Header constants should follow HTTP header naming conventions")
    void headerConstantsShouldFollowHttpNamingConventions() {
        // HTTP headers typically use Title-Case with hyphens
        assertThat(RestServiceConstants.HEADER_ACCEPT).matches("^[A-Z][a-z]*(-[A-Z][a-z]*)*$|^[A-Z][a-z]*$");
        assertThat(RestServiceConstants.HEADER_CONTENT_TYPE).matches("^[A-Z][a-z]*(-[A-Z][a-z]*)*$");
        assertThat(RestServiceConstants.HEADER_AUTHORIZATION).matches("^[A-Z][a-z]*$");
        assertThat(RestServiceConstants.HEADER_USER_AGENT).matches("^[A-Z][a-z]*(-[A-Z][a-z]*)*$");
    }

    @Property(tries = 100)
    @Label("Custom X- headers should start with X-")
    void customHeadersShouldStartWithX() {
        assertThat(RestServiceConstants.HEADER_REQUEST_ID).startsWith("X-");
        assertThat(RestServiceConstants.HEADER_CORRELATION_ID).startsWith("X-");
    }

    @Property(tries = 100)
    @Label("DEFAULT_USER_AGENT should contain identifying information")
    void defaultUserAgentShouldContainIdentifyingInfo() {
        assertThat(RestServiceConstants.DEFAULT_USER_AGENT)
                .isNotBlank()
                .contains("Bonita")
                .contains("/");
    }

    // =========================================================================
    // CONTENT TYPE CONSTANTS INVARIANTS
    // =========================================================================

    @Property(tries = 100)
    @Label("All content type constants should be non-null and non-blank")
    void allContentTypeConstantsShouldBeNonNullAndNonBlank() {
        assertThat(RestServiceConstants.CONTENT_TYPE_JSON).isNotNull().isNotBlank();
        assertThat(RestServiceConstants.CONTENT_TYPE_JSON_UTF8).isNotNull().isNotBlank();
        assertThat(RestServiceConstants.CONTENT_TYPE_XML).isNotNull().isNotBlank();
        assertThat(RestServiceConstants.CONTENT_TYPE_FORM).isNotNull().isNotBlank();
        assertThat(RestServiceConstants.CONTENT_TYPE_TEXT).isNotNull().isNotBlank();
    }

    @Property(tries = 100)
    @Label("JSON content types should follow MIME type format")
    void jsonContentTypesShouldFollowMimeFormat() {
        assertThat(RestServiceConstants.CONTENT_TYPE_JSON).startsWith("application/json");
        assertThat(RestServiceConstants.CONTENT_TYPE_JSON_UTF8).startsWith("application/json");
        assertThat(RestServiceConstants.CONTENT_TYPE_JSON_UTF8).contains("charset=utf-8");
    }

    @Property(tries = 100)
    @Label("Content types should follow type/subtype format")
    void contentTypesShouldFollowTypeSubtypeFormat() {
        assertThat(RestServiceConstants.CONTENT_TYPE_JSON).matches("^\\w+/[\\w+.-]+.*$");
        assertThat(RestServiceConstants.CONTENT_TYPE_XML).matches("^\\w+/[\\w+.-]+.*$");
        assertThat(RestServiceConstants.CONTENT_TYPE_FORM).matches("^\\w+/[\\w+.-]+.*$");
        assertThat(RestServiceConstants.CONTENT_TYPE_TEXT).matches("^\\w+/[\\w+.-]+.*$");
    }

    // =========================================================================
    // OAUTH2 CONSTANTS INVARIANTS
    // =========================================================================

    @Property(tries = 100)
    @Label("OAuth2 grant type constants should be non-null and lowercase with underscores")
    void oauth2GrantTypesShouldBeLowercaseWithUnderscores() {
        assertThat(RestServiceConstants.OAUTH2_GRANT_CLIENT_CREDENTIALS)
                .isNotBlank()
                .matches("^[a-z_]+$");
        assertThat(RestServiceConstants.OAUTH2_GRANT_PASSWORD)
                .isNotBlank()
                .matches("^[a-z_]+$");
        assertThat(RestServiceConstants.OAUTH2_GRANT_REFRESH_TOKEN)
                .isNotBlank()
                .matches("^[a-z_]+$");
    }

    @Property(tries = 100)
    @Label("OAuth2 token field constants should be non-null and lowercase with underscores")
    void oauth2TokenFieldsShouldBeLowercaseWithUnderscores() {
        assertThat(RestServiceConstants.OAUTH2_ACCESS_TOKEN).isNotBlank().matches("^[a-z_]+$");
        assertThat(RestServiceConstants.OAUTH2_TOKEN_TYPE).isNotBlank().matches("^[a-z_]+$");
        assertThat(RestServiceConstants.OAUTH2_EXPIRES_IN).isNotBlank().matches("^[a-z_]+$");
        assertThat(RestServiceConstants.OAUTH2_REFRESH_TOKEN).isNotBlank().matches("^[a-z_]+$");
        assertThat(RestServiceConstants.OAUTH2_SCOPE).isNotBlank().matches("^[a-z_]+$");
    }

    // =========================================================================
    // JSON FIELD NAME CONSTANTS INVARIANTS
    // =========================================================================

    @Property(tries = 100)
    @Label("JSON field name constants should be non-null, non-blank, and camelCase")
    void jsonFieldNamesShouldBeCamelCase() {
        assertThat(RestServiceConstants.JSON_URL).isNotBlank().matches("^[a-z][a-zA-Z]*$");
        assertThat(RestServiceConstants.JSON_METHOD).isNotBlank().matches("^[a-z][a-zA-Z]*$");
        assertThat(RestServiceConstants.JSON_HEADERS).isNotBlank().matches("^[a-z][a-zA-Z]*$");
        assertThat(RestServiceConstants.JSON_QUERY_PARAMS).isNotBlank().matches("^[a-z][a-zA-Z]*$");
        assertThat(RestServiceConstants.JSON_BODY).isNotBlank().matches("^[a-z][a-zA-Z]*$");
        assertThat(RestServiceConstants.JSON_CONTENT_TYPE).isNotBlank().matches("^[a-z][a-zA-Z]*$");
        assertThat(RestServiceConstants.JSON_AUTH).isNotBlank().matches("^[a-z][a-zA-Z]*$");
        assertThat(RestServiceConstants.JSON_AUTH_TYPE).isNotBlank().matches("^[a-z][a-zA-Z]*$");
        assertThat(RestServiceConstants.JSON_TIMEOUT_MS).isNotBlank().matches("^[a-z][a-zA-Z]*$");
        assertThat(RestServiceConstants.JSON_FOLLOW_REDIRECTS).isNotBlank().matches("^[a-z][a-zA-Z]*$");
        assertThat(RestServiceConstants.JSON_VERIFY_SSL).isNotBlank().matches("^[a-z][a-zA-Z]*$");
        assertThat(RestServiceConstants.JSON_STATUS_CODE).isNotBlank().matches("^[a-z][a-zA-Z]*$");
        assertThat(RestServiceConstants.JSON_ERROR_MESSAGE).isNotBlank().matches("^[a-z][a-zA-Z]*$");
        assertThat(RestServiceConstants.JSON_EXECUTION_TIME_MS).isNotBlank().matches("^[a-z][a-zA-Z]*$");
    }

    @Property(tries = 100)
    @Label("Auth JSON field names should be non-null, non-blank, and camelCase")
    void authJsonFieldNamesShouldBeCamelCase() {
        assertThat(RestServiceConstants.JSON_USERNAME).isNotBlank().matches("^[a-z][a-zA-Z]*$");
        assertThat(RestServiceConstants.JSON_PASSWORD).isNotBlank().matches("^[a-z][a-zA-Z]*$");
        assertThat(RestServiceConstants.JSON_TOKEN).isNotBlank().matches("^[a-z][a-zA-Z]*$");
        assertThat(RestServiceConstants.JSON_KEY_NAME).isNotBlank().matches("^[a-z][a-zA-Z]*$");
        assertThat(RestServiceConstants.JSON_KEY_VALUE).isNotBlank().matches("^[a-z][a-zA-Z]*$");
        assertThat(RestServiceConstants.JSON_LOCATION).isNotBlank().matches("^[a-z][a-zA-Z]*$");
        assertThat(RestServiceConstants.JSON_TOKEN_URL).isNotBlank().matches("^[a-z][a-zA-Z]*$");
        assertThat(RestServiceConstants.JSON_CLIENT_ID).isNotBlank().matches("^[a-z][a-zA-Z]*$");
        assertThat(RestServiceConstants.JSON_CLIENT_SECRET).isNotBlank().matches("^[a-z][a-zA-Z]*$");
        assertThat(RestServiceConstants.JSON_SCOPE).isNotBlank().matches("^[a-z][a-zA-Z]*$");
        assertThat(RestServiceConstants.JSON_AUDIENCE).isNotBlank().matches("^[a-z][a-zA-Z]*$");
    }

    // =========================================================================
    // HTTP STATUS CODE CONSTANTS INVARIANTS
    // =========================================================================

    @Property(tries = 100)
    @Label("HTTP success status codes should be in 2xx range")
    void httpSuccessCodesShouldBeIn2xxRange() {
        assertThat(RestServiceConstants.HTTP_OK).isBetween(200, 299);
        assertThat(RestServiceConstants.HTTP_CREATED).isBetween(200, 299);
        assertThat(RestServiceConstants.HTTP_NO_CONTENT).isBetween(200, 299);
    }

    @Property(tries = 100)
    @Label("HTTP client error status codes should be in 4xx range")
    void httpClientErrorCodesShouldBeIn4xxRange() {
        assertThat(RestServiceConstants.HTTP_BAD_REQUEST).isBetween(400, 499);
        assertThat(RestServiceConstants.HTTP_UNAUTHORIZED).isBetween(400, 499);
        assertThat(RestServiceConstants.HTTP_FORBIDDEN).isBetween(400, 499);
        assertThat(RestServiceConstants.HTTP_NOT_FOUND).isBetween(400, 499);
    }

    @Property(tries = 100)
    @Label("HTTP server error status codes should be in 5xx range")
    void httpServerErrorCodesShouldBeIn5xxRange() {
        assertThat(RestServiceConstants.HTTP_INTERNAL_ERROR).isBetween(500, 599);
        assertThat(RestServiceConstants.HTTP_BAD_GATEWAY).isBetween(500, 599);
        assertThat(RestServiceConstants.HTTP_SERVICE_UNAVAILABLE).isBetween(500, 599);
    }

    @Property(tries = 100)
    @Label("HTTP status codes should be in valid HTTP range")
    void httpStatusCodesShouldBeInValidRange() {
        int[] allStatusCodes = {
                RestServiceConstants.HTTP_OK,
                RestServiceConstants.HTTP_CREATED,
                RestServiceConstants.HTTP_NO_CONTENT,
                RestServiceConstants.HTTP_BAD_REQUEST,
                RestServiceConstants.HTTP_UNAUTHORIZED,
                RestServiceConstants.HTTP_FORBIDDEN,
                RestServiceConstants.HTTP_NOT_FOUND,
                RestServiceConstants.HTTP_INTERNAL_ERROR,
                RestServiceConstants.HTTP_BAD_GATEWAY,
                RestServiceConstants.HTTP_SERVICE_UNAVAILABLE
        };

        for (int statusCode : allStatusCodes) {
            assertThat(statusCode).isBetween(100, 599);
        }
    }

    // =========================================================================
    // DEFAULT HEADERS MAP INVARIANTS
    // =========================================================================

    @Property(tries = 100)
    @Label("DEFAULT_JSON_HEADERS should be non-null and immutable")
    void defaultJsonHeadersShouldBeNonNullAndImmutable() {
        Map<String, String> headers = RestServiceConstants.DEFAULT_JSON_HEADERS;

        assertThat(headers).isNotNull();
        assertThat(headers).isNotEmpty();
        assertThatThrownBy(() -> headers.put("Test", "Value"))
                .isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> headers.remove(RestServiceConstants.HEADER_ACCEPT))
                .isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(headers::clear)
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Property(tries = 100)
    @Label("DEFAULT_JSON_HEADERS should contain required headers")
    void defaultJsonHeadersShouldContainRequiredHeaders() {
        Map<String, String> headers = RestServiceConstants.DEFAULT_JSON_HEADERS;

        assertThat(headers).containsKey(RestServiceConstants.HEADER_ACCEPT);
        assertThat(headers).containsKey(RestServiceConstants.HEADER_CONTENT_TYPE);
        assertThat(headers).containsKey(RestServiceConstants.HEADER_USER_AGENT);

        assertThat(headers.get(RestServiceConstants.HEADER_ACCEPT))
                .isEqualTo(RestServiceConstants.CONTENT_TYPE_JSON);
        assertThat(headers.get(RestServiceConstants.HEADER_CONTENT_TYPE))
                .isEqualTo(RestServiceConstants.CONTENT_TYPE_JSON_UTF8);
        assertThat(headers.get(RestServiceConstants.HEADER_USER_AGENT))
                .isEqualTo(RestServiceConstants.DEFAULT_USER_AGENT);
    }

    @Property(tries = 100)
    @Label("DEFAULT_XML_HEADERS should be non-null and immutable")
    void defaultXmlHeadersShouldBeNonNullAndImmutable() {
        Map<String, String> headers = RestServiceConstants.DEFAULT_XML_HEADERS;

        assertThat(headers).isNotNull();
        assertThat(headers).isNotEmpty();
        assertThatThrownBy(() -> headers.put("Test", "Value"))
                .isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> headers.remove(RestServiceConstants.HEADER_ACCEPT))
                .isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(headers::clear)
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Property(tries = 100)
    @Label("DEFAULT_XML_HEADERS should contain required headers")
    void defaultXmlHeadersShouldContainRequiredHeaders() {
        Map<String, String> headers = RestServiceConstants.DEFAULT_XML_HEADERS;

        assertThat(headers).containsKey(RestServiceConstants.HEADER_ACCEPT);
        assertThat(headers).containsKey(RestServiceConstants.HEADER_CONTENT_TYPE);
        assertThat(headers).containsKey(RestServiceConstants.HEADER_USER_AGENT);

        assertThat(headers.get(RestServiceConstants.HEADER_ACCEPT))
                .isEqualTo(RestServiceConstants.CONTENT_TYPE_XML);
        assertThat(headers.get(RestServiceConstants.HEADER_CONTENT_TYPE))
                .isEqualTo(RestServiceConstants.CONTENT_TYPE_XML);
        assertThat(headers.get(RestServiceConstants.HEADER_USER_AGENT))
                .isEqualTo(RestServiceConstants.DEFAULT_USER_AGENT);
    }

    @Property(tries = 100)
    @Label("Default header maps should have same size")
    void defaultHeaderMapsShouldHaveSameSize() {
        assertThat(RestServiceConstants.DEFAULT_JSON_HEADERS.size())
                .isEqualTo(RestServiceConstants.DEFAULT_XML_HEADERS.size());
    }

    // =========================================================================
    // CONSTANT UNIQUENESS INVARIANTS
    // =========================================================================

    @Property(tries = 100)
    @Label("All header constants should be unique")
    void allHeaderConstantsShouldBeUnique() {
        String[] headers = {
                RestServiceConstants.HEADER_ACCEPT,
                RestServiceConstants.HEADER_CONTENT_TYPE,
                RestServiceConstants.HEADER_AUTHORIZATION,
                RestServiceConstants.HEADER_USER_AGENT,
                RestServiceConstants.HEADER_REQUEST_ID,
                RestServiceConstants.HEADER_CORRELATION_ID
        };

        assertThat(headers).doesNotHaveDuplicates();
    }

    @Property(tries = 100)
    @Label("All content type constants should be unique")
    void allContentTypeConstantsShouldBeUnique() {
        String[] contentTypes = {
                RestServiceConstants.CONTENT_TYPE_JSON,
                RestServiceConstants.CONTENT_TYPE_JSON_UTF8,
                RestServiceConstants.CONTENT_TYPE_XML,
                RestServiceConstants.CONTENT_TYPE_FORM,
                RestServiceConstants.CONTENT_TYPE_TEXT
        };

        assertThat(contentTypes).doesNotHaveDuplicates();
    }

    @Property(tries = 100)
    @Label("All HTTP status codes should be unique")
    void allHttpStatusCodesShouldBeUnique() {
        int[] statusCodes = {
                RestServiceConstants.HTTP_OK,
                RestServiceConstants.HTTP_CREATED,
                RestServiceConstants.HTTP_NO_CONTENT,
                RestServiceConstants.HTTP_BAD_REQUEST,
                RestServiceConstants.HTTP_UNAUTHORIZED,
                RestServiceConstants.HTTP_FORBIDDEN,
                RestServiceConstants.HTTP_NOT_FOUND,
                RestServiceConstants.HTTP_INTERNAL_ERROR,
                RestServiceConstants.HTTP_BAD_GATEWAY,
                RestServiceConstants.HTTP_SERVICE_UNAVAILABLE
        };

        // Convert to set to check uniqueness
        assertThat(statusCodes).hasSize(10);
        java.util.Set<Integer> uniqueCodes = new java.util.HashSet<>();
        for (int code : statusCodes) {
            uniqueCodes.add(code);
        }
        assertThat(uniqueCodes).hasSize(statusCodes.length);
    }
}
