package com.bonitasoft.processbuilder.execution;

import com.bonitasoft.processbuilder.enums.RestAuthenticationType;
import com.bonitasoft.processbuilder.enums.RestContentType;
import com.bonitasoft.processbuilder.records.RestAuthConfig;
import com.bonitasoft.processbuilder.records.RestServiceRequest;
import com.bonitasoft.processbuilder.records.RestServiceResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * HTTP execution engine with connection pooling, OAuth2 token caching, and Bonita session management.
 * <p>
 * Extracted from RestServiceExecutor to be shared between REST Extension and custom connector.
 * This is now an instantiable class (not static) to allow proper lifecycle management,
 * but uses shared static clients and caches for connection pooling.
 * </p>
 */
public final class HttpExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpExecutor.class);

    private static final int DEFAULT_TIMEOUT_MS = 30_000;
    private static final int MAX_TIMEOUT_MS = 300_000;

    private static final Pattern BONITA_API_PATTERN = Pattern.compile(
            "^(https?://[^/]+)/bonita/API/.*", Pattern.CASE_INSENSITIVE);
    private static final String BONITA_LOGIN_PATH = "/bonita/loginservice";
    private static final String JSESSIONID_COOKIE = "JSESSIONID";

    // Shared HttpClient instances for connection pooling
    private static final HttpClient SECURE_CLIENT;
    private static final HttpClient INSECURE_CLIENT;

    // OAuth2 token cache (cacheKey -> CachedToken)
    private static final ConcurrentHashMap<String, CachedToken> TOKEN_CACHE = new ConcurrentHashMap<>();

    // Bonita session cache (baseUrl:user -> CachedSession)
    private static final ConcurrentHashMap<String, CachedSession> BONITA_SESSION_CACHE = new ConcurrentHashMap<>();

    static {
        SECURE_CLIENT = HttpClient.newBuilder()
                .connectTimeout(Duration.ofMillis(DEFAULT_TIMEOUT_MS))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();

        HttpClient insecure;
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{new TrustAllManager()}, new java.security.SecureRandom());
            insecure = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofMillis(DEFAULT_TIMEOUT_MS))
                    .followRedirects(HttpClient.Redirect.NORMAL)
                    .sslContext(sslContext)
                    .build();
        } catch (Exception e) {
            insecure = SECURE_CLIENT;
        }
        INSECURE_CLIENT = insecure;
    }

    public HttpExecutor() {}

    /**
     * Executes a REST service request.
     *
     * @param request The REST service request configuration
     * @return The REST service response
     */
    public RestServiceResponse execute(RestServiceRequest request) {
        long startTime = System.currentTimeMillis();
        String requestUrl = request.buildFullUrl();

        try {
            LOGGER.info("Executing REST request: {} {}", request.method(), requestUrl);

            HttpClient client = request.verifySsl() ? SECURE_CLIENT : INSECURE_CLIENT;

            HttpRequest.Builder httpRequestBuilder = HttpRequest.newBuilder()
                    .uri(URI.create(requestUrl))
                    .timeout(Duration.ofMillis(
                            Math.min(request.timeoutMs() > 0 ? request.timeoutMs() : DEFAULT_TIMEOUT_MS, MAX_TIMEOUT_MS)));

            // Apply headers
            Map<String, String> allHeaders = request.buildAllHeaders();
            allHeaders.forEach(httpRequestBuilder::header);

            // Handle Bonita session-based authentication
            if (request.auth() != null && isBasicAuth(request.auth()) && isBonitaApiUrl(requestUrl)) {
                LOGGER.debug("Detected Bonita API URL, using session-based authentication");
                String sessionCookie = getBonitaSession(requestUrl, request.auth(), request.verifySsl());
                if (sessionCookie != null) {
                    httpRequestBuilder.header("Cookie", JSESSIONID_COOKIE + "=" + sessionCookie);
                }
            }

            // Handle OAuth2 authentication
            if (request.auth() != null && isOAuth2Auth(request.auth())) {
                String token = getOAuth2Token(request.auth());
                if (token != null) {
                    httpRequestBuilder.header("Authorization", "Bearer " + token);
                }
            }

            // Set HTTP method and body
            HttpRequest.BodyPublisher bodyPublisher = request.hasBody()
                    ? HttpRequest.BodyPublishers.ofString(request.body())
                    : HttpRequest.BodyPublishers.noBody();

            switch (request.method()) {
                case GET -> httpRequestBuilder.GET();
                case POST -> httpRequestBuilder.POST(bodyPublisher);
                case PUT -> httpRequestBuilder.PUT(bodyPublisher);
                case DELETE -> httpRequestBuilder.DELETE();
                case PATCH -> httpRequestBuilder.method("PATCH", bodyPublisher);
                case HEAD -> httpRequestBuilder.method("HEAD", HttpRequest.BodyPublishers.noBody());
                case OPTIONS -> httpRequestBuilder.method("OPTIONS", HttpRequest.BodyPublishers.noBody());
                default -> httpRequestBuilder.method(request.method().name(), bodyPublisher);
            }

            HttpResponse<String> response = client.send(
                    httpRequestBuilder.build(),
                    HttpResponse.BodyHandlers.ofString());

            long executionTime = System.currentTimeMillis() - startTime;

            Map<String, String> responseHeaders = new HashMap<>();
            response.headers().map().forEach((key, values) -> {
                if (!values.isEmpty()) {
                    responseHeaders.put(key, values.get(0));
                }
            });

            RestContentType contentType = determineContentType(
                    response.headers().firstValue("Content-Type").orElse("application/json"));

            LOGGER.info("REST request completed: {} {} -> {} in {}ms",
                    request.method(), requestUrl, response.statusCode(), executionTime);

            return RestServiceResponse.success(
                    response.statusCode(), responseHeaders, response.body(),
                    contentType, executionTime, requestUrl);

        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            LOGGER.error("REST request failed: {} {} - {}", request.method(), requestUrl, e.getMessage(), e);
            return RestServiceResponse.fromException(e, executionTime, requestUrl);
        }
    }

    // ========================================================================
    // Authentication helpers
    // ========================================================================

    private boolean isOAuth2Auth(RestAuthConfig auth) {
        RestAuthenticationType type = auth.getAuthType();
        return type == RestAuthenticationType.OAUTH2_CLIENT_CREDENTIALS
                || type == RestAuthenticationType.OAUTH2_PASSWORD;
    }

    private boolean isBasicAuth(RestAuthConfig auth) {
        return auth.getAuthType() == RestAuthenticationType.BASIC;
    }

    private boolean isBonitaApiUrl(String url) {
        return BONITA_API_PATTERN.matcher(url).matches();
    }

    private String extractBonitaBaseUrl(String url) {
        Matcher matcher = BONITA_API_PATTERN.matcher(url);
        return matcher.matches() ? matcher.group(1) : null;
    }

    private String getBonitaSession(String requestUrl, RestAuthConfig auth, boolean verifySsl) {
        String baseUrl = extractBonitaBaseUrl(requestUrl);
        if (baseUrl == null) return null;

        String username = "";
        if (auth instanceof RestAuthConfig.BasicAuth basicAuth) {
            username = basicAuth.username();
        }
        String cacheKey = baseUrl + ":" + username;

        CachedSession cached = BONITA_SESSION_CACHE.get(cacheKey);
        if (cached != null && !cached.isExpired()) {
            LOGGER.debug("Using cached Bonita session for {}", baseUrl);
            return cached.value;
        }

        try {
            String sessionId = loginToBonita(baseUrl, auth, verifySsl);
            if (sessionId != null) {
                BONITA_SESSION_CACHE.put(cacheKey,
                        new CachedSession(sessionId, System.currentTimeMillis() + 25 * 60 * 1000));
                LOGGER.info("Bonita session obtained and cached for {}", baseUrl);
            }
            return sessionId;
        } catch (Exception e) {
            LOGGER.error("Failed to login to Bonita at {}: {}", baseUrl, e.getMessage(), e);
            return null;
        }
    }

    private String loginToBonita(String baseUrl, RestAuthConfig auth, boolean verifySsl) throws Exception {
        if (!(auth instanceof RestAuthConfig.BasicAuth basicAuth)) {
            LOGGER.error("Bonita login requires Basic auth config");
            return null;
        }

        String loginUrl = baseUrl + BONITA_LOGIN_PATH;
        LOGGER.info("Logging in to Bonita at: {}", loginUrl);

        String loginBody = "username=" + encode(basicAuth.username())
                + "&password=" + encode(basicAuth.password())
                + "&redirect=false";

        HttpClient client = verifySsl ? SECURE_CLIENT : INSECURE_CLIENT;

        HttpRequest loginRequest = HttpRequest.newBuilder()
                .uri(URI.create(loginUrl))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(loginBody))
                .timeout(Duration.ofSeconds(30))
                .build();

        HttpResponse<String> response = client.send(loginRequest, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200 || response.statusCode() == 204) {
            List<String> cookies = response.headers().allValues("Set-Cookie");
            for (String cookie : cookies) {
                if (cookie.startsWith(JSESSIONID_COOKIE + "=")) {
                    return cookie.split(";")[0].substring(JSESSIONID_COOKIE.length() + 1);
                }
            }
            LOGGER.warn("Bonita login returned {} but no JSESSIONID cookie found", response.statusCode());
        } else {
            LOGGER.error("Bonita login failed with status {}: {}", response.statusCode(), response.body());
        }
        return null;
    }

    private String getOAuth2Token(RestAuthConfig auth) {
        if (auth instanceof RestAuthConfig.OAuth2ClientCredentials oauth2) {
            String cacheKey = "cc:" + oauth2.clientId() + ":" + oauth2.tokenUrl();
            CachedToken cached = TOKEN_CACHE.get(cacheKey);
            if (cached != null && !cached.isExpired()) {
                LOGGER.debug("Using cached OAuth2 token for {}", oauth2.clientId());
                return cached.value;
            }
            try {
                String token = requestOAuth2ClientCredentialsToken(oauth2);
                if (token != null) {
                    TOKEN_CACHE.put(cacheKey,
                            new CachedToken(token, System.currentTimeMillis() + 55 * 60 * 1000));
                }
                return token;
            } catch (Exception e) {
                LOGGER.error("Failed to get OAuth2 token: {}", e.getMessage(), e);
                return null;
            }
        }
        return null;
    }

    private String requestOAuth2ClientCredentialsToken(RestAuthConfig.OAuth2ClientCredentials config) throws Exception {
        LOGGER.info("Requesting OAuth2 Client Credentials token from: {}", config.tokenUrl());

        String body = config.getTokenRequestBody();
        Map<String, String> tokenHeaders = config.getTokenRequestHeaders();

        HttpRequest.Builder reqBuilder = HttpRequest.newBuilder()
                .uri(URI.create(config.tokenUrl()))
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .timeout(Duration.ofSeconds(30));

        tokenHeaders.forEach(reqBuilder::header);

        HttpResponse<String> response = SECURE_CLIENT.send(reqBuilder.build(), HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            String responseBody = response.body();
            // Simple JSON parsing to avoid Jackson dependency in the critical path
            int tokenStart = responseBody.indexOf("\"access_token\"");
            if (tokenStart >= 0) {
                int valueStart = responseBody.indexOf(":", tokenStart) + 1;
                int valueEnd = responseBody.indexOf(",", valueStart);
                if (valueEnd < 0) valueEnd = responseBody.indexOf("}", valueStart);
                String tokenValue = responseBody.substring(valueStart, valueEnd).trim();
                if (tokenValue.startsWith("\"")) {
                    tokenValue = tokenValue.substring(1, tokenValue.length() - 1);
                }
                LOGGER.info("OAuth2 token obtained successfully");
                return tokenValue;
            }
        }

        LOGGER.error("Failed to get OAuth2 token. Status: {}, Response: {}", response.statusCode(), response.body());
        return null;
    }

    // ========================================================================
    // Utility
    // ========================================================================

    private static RestContentType determineContentType(String contentTypeHeader) {
        if (contentTypeHeader == null) return RestContentType.JSON;
        String lower = contentTypeHeader.toLowerCase();
        if (lower.contains("json")) return RestContentType.JSON;
        if (lower.contains("xml")) return RestContentType.XML;
        if (lower.contains("form-urlencoded")) return RestContentType.FORM_URLENCODED;
        if (lower.contains("text/plain")) return RestContentType.TEXT_PLAIN;
        if (lower.contains("text/html")) return RestContentType.TEXT_HTML;
        return RestContentType.JSON;
    }

    private static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    public static void clearTokenCache() { TOKEN_CACHE.clear(); }
    public static void clearSessionCache() { BONITA_SESSION_CACHE.clear(); }

    // ========================================================================
    // Cache records
    // ========================================================================

    private record CachedToken(String value, long expiresAt) {
        boolean isExpired() { return System.currentTimeMillis() >= expiresAt; }
    }

    private record CachedSession(String value, long expiresAt) {
        boolean isExpired() { return System.currentTimeMillis() >= expiresAt; }
    }

    private static class TrustAllManager implements X509TrustManager {
        @Override public void checkClientTrusted(X509Certificate[] chain, String authType) {}
        @Override public void checkServerTrusted(X509Certificate[] chain, String authType) {}
        @Override public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
    }
}
