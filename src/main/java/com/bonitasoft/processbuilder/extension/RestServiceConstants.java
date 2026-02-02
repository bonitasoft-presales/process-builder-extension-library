package com.bonitasoft.processbuilder.extension;

import java.util.Collections;
import java.util.Map;

/**
 * Constants for REST service execution.
 * <p>
 * This class contains common constants used when making REST API calls,
 * including default headers, timeouts, and JSON field names.
 * </p>
 *
 * @author Bonitasoft
 * @since 1.0
 */
public final class RestServiceConstants {

    private RestServiceConstants() {
        throw new UnsupportedOperationException("Constants class cannot be instantiated");
    }

    // ========================================================================
    // Timeouts
    // ========================================================================

    /**
     * Default connection timeout in milliseconds (10 seconds).
     */
    public static final int DEFAULT_CONNECT_TIMEOUT_MS = 10_000;

    /**
     * Default read timeout in milliseconds (30 seconds).
     */
    public static final int DEFAULT_READ_TIMEOUT_MS = 30_000;

    /**
     * Maximum timeout allowed in milliseconds (5 minutes).
     */
    public static final int MAX_TIMEOUT_MS = 300_000;

    /**
     * Minimum timeout allowed in milliseconds (1 second).
     */
    public static final int MIN_TIMEOUT_MS = 1_000;

    // ========================================================================
    // Common Headers
    // ========================================================================

    /**
     * Accept header name.
     */
    public static final String HEADER_ACCEPT = "Accept";

    /**
     * Content-Type header name.
     */
    public static final String HEADER_CONTENT_TYPE = "Content-Type";

    /**
     * Authorization header name.
     */
    public static final String HEADER_AUTHORIZATION = "Authorization";

    /**
     * User-Agent header name.
     */
    public static final String HEADER_USER_AGENT = "User-Agent";

    /**
     * X-Request-ID header name for request tracing.
     */
    public static final String HEADER_REQUEST_ID = "X-Request-ID";

    /**
     * X-Correlation-ID header name for distributed tracing.
     */
    public static final String HEADER_CORRELATION_ID = "X-Correlation-ID";

    /**
     * Default User-Agent value.
     */
    public static final String DEFAULT_USER_AGENT = "Bonita-ProcessBuilder/1.0";

    // ========================================================================
    // Content Types
    // ========================================================================

    /**
     * JSON content type.
     */
    public static final String CONTENT_TYPE_JSON = "application/json";

    /**
     * JSON content type with UTF-8 charset.
     */
    public static final String CONTENT_TYPE_JSON_UTF8 = "application/json; charset=utf-8";

    /**
     * XML content type.
     */
    public static final String CONTENT_TYPE_XML = "application/xml";

    /**
     * Form URL encoded content type.
     */
    public static final String CONTENT_TYPE_FORM = "application/x-www-form-urlencoded";

    /**
     * Plain text content type.
     */
    public static final String CONTENT_TYPE_TEXT = "text/plain";

    // ========================================================================
    // OAuth2 Constants
    // ========================================================================

    /**
     * OAuth2 grant type for client credentials.
     */
    public static final String OAUTH2_GRANT_CLIENT_CREDENTIALS = "client_credentials";

    /**
     * OAuth2 grant type for password.
     */
    public static final String OAUTH2_GRANT_PASSWORD = "password";

    /**
     * OAuth2 grant type for refresh token.
     */
    public static final String OAUTH2_GRANT_REFRESH_TOKEN = "refresh_token";

    /**
     * OAuth2 access token field name in response.
     */
    public static final String OAUTH2_ACCESS_TOKEN = "access_token";

    /**
     * OAuth2 token type field name in response.
     */
    public static final String OAUTH2_TOKEN_TYPE = "token_type";

    /**
     * OAuth2 expires in field name in response (seconds).
     */
    public static final String OAUTH2_EXPIRES_IN = "expires_in";

    /**
     * OAuth2 refresh token field name in response.
     */
    public static final String OAUTH2_REFRESH_TOKEN = "refresh_token";

    /**
     * OAuth2 scope field name in response.
     */
    public static final String OAUTH2_SCOPE = "scope";

    // ========================================================================
    // JSON Field Names for Request/Response
    // ========================================================================

    /**
     * JSON field name for URL.
     */
    public static final String JSON_URL = "url";

    /**
     * JSON field name for HTTP method.
     */
    public static final String JSON_METHOD = "method";

    /**
     * JSON field name for headers.
     */
    public static final String JSON_HEADERS = "headers";

    /**
     * JSON field name for query parameters.
     */
    public static final String JSON_QUERY_PARAMS = "queryParams";

    /**
     * JSON field name for request/response body.
     */
    public static final String JSON_BODY = "body";

    /**
     * JSON field name for content type.
     */
    public static final String JSON_CONTENT_TYPE = "contentType";

    /**
     * JSON field name for authentication configuration.
     */
    public static final String JSON_AUTH = "auth";

    /**
     * JSON field name for authentication type.
     */
    public static final String JSON_AUTH_TYPE = "authType";

    /**
     * JSON field name for timeout.
     */
    public static final String JSON_TIMEOUT_MS = "timeoutMs";

    /**
     * JSON field name for follow redirects flag.
     */
    public static final String JSON_FOLLOW_REDIRECTS = "followRedirects";

    /**
     * JSON field name for verify SSL flag.
     */
    public static final String JSON_VERIFY_SSL = "verifySsl";

    /**
     * JSON field name for status code.
     */
    public static final String JSON_STATUS_CODE = "statusCode";

    /**
     * JSON field name for error message.
     */
    public static final String JSON_ERROR_MESSAGE = "errorMessage";

    /**
     * JSON field name for execution time.
     */
    public static final String JSON_EXECUTION_TIME_MS = "executionTimeMs";

    // ========================================================================
    // Authentication Field Names
    // ========================================================================

    /**
     * JSON field name for username.
     */
    public static final String JSON_USERNAME = "username";

    /**
     * JSON field name for password.
     */
    public static final String JSON_PASSWORD = "password";

    /**
     * JSON field name for token.
     */
    public static final String JSON_TOKEN = "token";

    /**
     * JSON field name for API key name.
     */
    public static final String JSON_KEY_NAME = "keyName";

    /**
     * JSON field name for API key value.
     */
    public static final String JSON_KEY_VALUE = "keyValue";

    /**
     * JSON field name for API key location.
     */
    public static final String JSON_LOCATION = "location";

    /**
     * JSON field name for OAuth2 token URL.
     */
    public static final String JSON_TOKEN_URL = "tokenUrl";

    /**
     * JSON field name for OAuth2 client ID.
     */
    public static final String JSON_CLIENT_ID = "clientId";

    /**
     * JSON field name for OAuth2 client secret.
     */
    public static final String JSON_CLIENT_SECRET = "clientSecret";

    /**
     * JSON field name for OAuth2 scope.
     */
    public static final String JSON_SCOPE = "scope";

    /**
     * JSON field name for OAuth2 audience.
     */
    public static final String JSON_AUDIENCE = "audience";

    // ========================================================================
    // HTTP Status Codes
    // ========================================================================

    /**
     * HTTP 200 OK.
     */
    public static final int HTTP_OK = 200;

    /**
     * HTTP 201 Created.
     */
    public static final int HTTP_CREATED = 201;

    /**
     * HTTP 204 No Content.
     */
    public static final int HTTP_NO_CONTENT = 204;

    /**
     * HTTP 400 Bad Request.
     */
    public static final int HTTP_BAD_REQUEST = 400;

    /**
     * HTTP 401 Unauthorized.
     */
    public static final int HTTP_UNAUTHORIZED = 401;

    /**
     * HTTP 403 Forbidden.
     */
    public static final int HTTP_FORBIDDEN = 403;

    /**
     * HTTP 404 Not Found.
     */
    public static final int HTTP_NOT_FOUND = 404;

    /**
     * HTTP 500 Internal Server Error.
     */
    public static final int HTTP_INTERNAL_ERROR = 500;

    /**
     * HTTP 502 Bad Gateway.
     */
    public static final int HTTP_BAD_GATEWAY = 502;

    /**
     * HTTP 503 Service Unavailable.
     */
    public static final int HTTP_SERVICE_UNAVAILABLE = 503;

    // ========================================================================
    // Default Headers Map
    // ========================================================================

    /**
     * Default headers for JSON requests.
     */
    public static final Map<String, String> DEFAULT_JSON_HEADERS = Collections.unmodifiableMap(
            Map.of(
                    HEADER_ACCEPT, CONTENT_TYPE_JSON,
                    HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON_UTF8,
                    HEADER_USER_AGENT, DEFAULT_USER_AGENT
            )
    );

    /**
     * Default headers for XML requests.
     */
    public static final Map<String, String> DEFAULT_XML_HEADERS = Collections.unmodifiableMap(
            Map.of(
                    HEADER_ACCEPT, CONTENT_TYPE_XML,
                    HEADER_CONTENT_TYPE, CONTENT_TYPE_XML,
                    HEADER_USER_AGENT, DEFAULT_USER_AGENT
            )
    );
}
