package com.bonitasoft.processbuilder.records;

import com.bonitasoft.processbuilder.enums.RestApiKeyLocation;
import com.bonitasoft.processbuilder.enums.RestContentType;
import com.bonitasoft.processbuilder.enums.RestHttpMethod;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link RestServiceRequest} record.
 *
 * @author Bonitasoft
 * @since 1.0
 */
class RestServiceRequestTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final String TEST_URL = "https://api.example.com/test";

    // =========================================================================
    // Constructor Tests
    // =========================================================================

    @Test
    void constructor_withValidUrl_shouldCreateRequest() {
        RestServiceRequest request = new RestServiceRequest(
                TEST_URL, null, null, null, null, null, null, 0, true, true);

        assertEquals(TEST_URL, request.url());
        assertEquals(RestHttpMethod.GET, request.method());
        assertEquals(RestContentType.JSON, request.contentType());
        assertEquals(RestServiceRequest.DEFAULT_TIMEOUT_MS, request.timeoutMs());
        assertTrue(request.headers().isEmpty());
        assertTrue(request.queryParams().isEmpty());
        assertInstanceOf(RestAuthConfig.NoAuth.class, request.auth());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t", "\n"})
    void constructor_withInvalidUrl_shouldThrowException(String url) {
        assertThrows(IllegalArgumentException.class, () ->
                new RestServiceRequest(url, null, null, null, null, null, null, 0, true, true));
    }

    @Test
    void constructor_shouldTrimUrl() {
        RestServiceRequest request = new RestServiceRequest(
                "  " + TEST_URL + "  ", null, null, null, null, null, null, 0, true, true);

        assertEquals(TEST_URL, request.url());
    }

    // =========================================================================
    // Builder Tests
    // =========================================================================

    @Test
    void builder_shouldCreateRequestWithDefaults() {
        RestServiceRequest request = RestServiceRequest.builder(TEST_URL).build();

        assertEquals(TEST_URL, request.url());
        assertEquals(RestHttpMethod.GET, request.method());
        assertEquals(RestContentType.JSON, request.contentType());
        assertTrue(request.followRedirects());
        assertTrue(request.verifySsl());
    }

    @Test
    void builder_get_shouldSetGetMethod() {
        RestServiceRequest request = RestServiceRequest.builder(TEST_URL).get().build();
        assertEquals(RestHttpMethod.GET, request.method());
    }

    @Test
    void builder_post_shouldSetPostMethod() {
        RestServiceRequest request = RestServiceRequest.builder(TEST_URL).post().build();
        assertEquals(RestHttpMethod.POST, request.method());
    }

    @Test
    void builder_put_shouldSetPutMethod() {
        RestServiceRequest request = RestServiceRequest.builder(TEST_URL).put().build();
        assertEquals(RestHttpMethod.PUT, request.method());
    }

    @Test
    void builder_patch_shouldSetPatchMethod() {
        RestServiceRequest request = RestServiceRequest.builder(TEST_URL).patch().build();
        assertEquals(RestHttpMethod.PATCH, request.method());
    }

    @Test
    void builder_delete_shouldSetDeleteMethod() {
        RestServiceRequest request = RestServiceRequest.builder(TEST_URL).delete().build();
        assertEquals(RestHttpMethod.DELETE, request.method());
    }

    @Test
    void builder_header_shouldAddHeader() {
        RestServiceRequest request = RestServiceRequest.builder(TEST_URL)
                .header("X-Custom", "value")
                .build();

        assertEquals("value", request.headers().get("X-Custom"));
    }

    @Test
    void builder_headers_shouldAddMultipleHeaders() {
        RestServiceRequest request = RestServiceRequest.builder(TEST_URL)
                .headers(Map.of("X-One", "1", "X-Two", "2"))
                .build();

        assertEquals(2, request.headers().size());
    }

    @Test
    void builder_queryParam_shouldAddQueryParam() {
        RestServiceRequest request = RestServiceRequest.builder(TEST_URL)
                .queryParam("page", "1")
                .build();

        assertEquals("1", request.queryParams().get("page"));
    }

    @Test
    void builder_body_shouldSetBody() {
        String body = "{\"key\":\"value\"}";
        RestServiceRequest request = RestServiceRequest.builder(TEST_URL)
                .body(body)
                .build();

        assertEquals(body, request.body());
    }

    @Test
    void builder_jsonBody_shouldSerializeObject() {
        Map<String, String> data = Map.of("key", "value");
        RestServiceRequest request = RestServiceRequest.builder(TEST_URL)
                .jsonBody(data)
                .build();

        assertTrue(request.body().contains("\"key\""));
        assertTrue(request.body().contains("\"value\""));
        assertEquals(RestContentType.JSON, request.contentType());
    }

    @Test
    void builder_basicAuth_shouldSetBasicAuth() {
        RestServiceRequest request = RestServiceRequest.builder(TEST_URL)
                .basicAuth("user", "pass")
                .build();

        assertInstanceOf(RestAuthConfig.BasicAuth.class, request.auth());
    }

    @Test
    void builder_bearerAuth_shouldSetBearerAuth() {
        RestServiceRequest request = RestServiceRequest.builder(TEST_URL)
                .bearerAuth("token")
                .build();

        assertInstanceOf(RestAuthConfig.BearerAuth.class, request.auth());
    }

    @Test
    void builder_apiKeyAuth_shouldSetApiKeyAuth() {
        RestServiceRequest request = RestServiceRequest.builder(TEST_URL)
                .apiKeyAuth("X-API-Key", "secret")
                .build();

        assertInstanceOf(RestAuthConfig.ApiKeyAuth.class, request.auth());
    }

    @Test
    void builder_timeout_shouldSetTimeout() {
        RestServiceRequest request = RestServiceRequest.builder(TEST_URL)
                .timeout(60000)
                .build();

        assertEquals(60000, request.timeoutMs());
    }

    @Test
    void builder_followRedirects_shouldSetFollowRedirects() {
        RestServiceRequest request = RestServiceRequest.builder(TEST_URL)
                .followRedirects(false)
                .build();

        assertFalse(request.followRedirects());
    }

    @Test
    void builder_verifySsl_shouldSetVerifySsl() {
        RestServiceRequest request = RestServiceRequest.builder(TEST_URL)
                .verifySsl(false)
                .build();

        assertFalse(request.verifySsl());
    }

    // =========================================================================
    // Factory Method Tests
    // =========================================================================

    @Test
    void get_shouldCreateGetRequest() {
        RestServiceRequest request = RestServiceRequest.get(TEST_URL);

        assertEquals(TEST_URL, request.url());
        assertEquals(RestHttpMethod.GET, request.method());
    }

    @Test
    void postJson_shouldCreatePostRequest() {
        String body = "{\"key\":\"value\"}";
        RestServiceRequest request = RestServiceRequest.postJson(TEST_URL, body);

        assertEquals(TEST_URL, request.url());
        assertEquals(RestHttpMethod.POST, request.method());
        assertEquals(body, request.body());
        assertEquals(RestContentType.JSON, request.contentType());
    }

    // =========================================================================
    // fromJson Tests
    // =========================================================================

    @Test
    void fromJson_minimalRequest_shouldParse() throws Exception {
        String json = """
                {
                    "url": "https://api.example.com/test"
                }
                """;
        JsonNode node = MAPPER.readTree(json);

        RestServiceRequest request = RestServiceRequest.fromJson(node, null);

        assertEquals("https://api.example.com/test", request.url());
        assertEquals(RestHttpMethod.GET, request.method());
    }

    @Test
    void fromJson_fullRequest_shouldParse() throws Exception {
        String json = """
                {
                    "url": "https://api.example.com/test",
                    "method": "POST",
                    "headers": {
                        "X-Custom": "value"
                    },
                    "queryParams": {
                        "page": "1"
                    },
                    "body": {"data": "test"},
                    "contentType": "application/json",
                    "timeoutMs": 60000,
                    "followRedirects": false,
                    "verifySsl": false,
                    "auth": {
                        "authType": "basic",
                        "username": "user",
                        "password": "pass"
                    }
                }
                """;
        JsonNode node = MAPPER.readTree(json);

        RestServiceRequest request = RestServiceRequest.fromJson(node, null);

        assertEquals("https://api.example.com/test", request.url());
        assertEquals(RestHttpMethod.POST, request.method());
        assertEquals("value", request.headers().get("X-Custom"));
        assertEquals("1", request.queryParams().get("page"));
        assertNotNull(request.body());
        assertEquals(RestContentType.JSON, request.contentType());
        assertEquals(60000, request.timeoutMs());
        assertFalse(request.followRedirects());
        assertFalse(request.verifySsl());
        assertInstanceOf(RestAuthConfig.BasicAuth.class, request.auth());
    }

    @Test
    void fromJson_nullNode_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () ->
                RestServiceRequest.fromJson(null, null));
    }

    @Test
    void fromJson_missingUrl_shouldThrowException() throws Exception {
        String json = """
                {
                    "method": "GET"
                }
                """;
        JsonNode node = MAPPER.readTree(json);

        assertThrows(IllegalArgumentException.class, () ->
                RestServiceRequest.fromJson(node, null));
    }

    // =========================================================================
    // Utility Method Tests
    // =========================================================================

    @Test
    void buildFullUrl_noQueryParams_shouldReturnOriginalUrl() {
        RestServiceRequest request = RestServiceRequest.get(TEST_URL);
        assertEquals(TEST_URL, request.buildFullUrl());
    }

    @Test
    void buildFullUrl_withQueryParams_shouldAppendParams() {
        RestServiceRequest request = RestServiceRequest.builder(TEST_URL)
                .queryParam("page", "1")
                .queryParam("size", "10")
                .build();

        String fullUrl = request.buildFullUrl();

        assertTrue(fullUrl.startsWith(TEST_URL + "?"));
        assertTrue(fullUrl.contains("page=1"));
        assertTrue(fullUrl.contains("size=10"));
    }

    @Test
    void buildFullUrl_withExistingQueryString_shouldAppendWithAmpersand() {
        RestServiceRequest request = RestServiceRequest.builder(TEST_URL + "?existing=true")
                .queryParam("page", "1")
                .build();

        String fullUrl = request.buildFullUrl();

        assertTrue(fullUrl.contains("existing=true"));
        assertTrue(fullUrl.contains("&page=1"));
    }

    @Test
    void buildFullUrl_withApiKeyQueryParam_shouldIncludeApiKey() {
        RestServiceRequest request = RestServiceRequest.builder(TEST_URL)
                .auth(RestAuthConfig.apiKey("api_key", "secret", RestApiKeyLocation.QUERY_PARAM))
                .build();

        String fullUrl = request.buildFullUrl();

        assertTrue(fullUrl.contains("api_key=secret"));
    }

    @Test
    void buildAllHeaders_shouldIncludeContentTypeAndAuth() {
        RestServiceRequest request = RestServiceRequest.builder(TEST_URL)
                .post()
                .body("{}")
                .bearerAuth("token")
                .header("X-Custom", "value")
                .build();

        Map<String, String> allHeaders = request.buildAllHeaders();

        assertEquals("application/json", allHeaders.get("Content-Type"));
        assertEquals("Bearer token", allHeaders.get("Authorization"));
        assertEquals("value", allHeaders.get("X-Custom"));
    }

    @Test
    void buildAllHeaders_noBody_shouldNotIncludeContentType() {
        RestServiceRequest request = RestServiceRequest.get(TEST_URL);

        Map<String, String> allHeaders = request.buildAllHeaders();

        assertNull(allHeaders.get("Content-Type"));
    }

    @Test
    void hasBody_withBody_shouldReturnTrue() {
        RestServiceRequest request = RestServiceRequest.postJson(TEST_URL, "{}");
        assertTrue(request.hasBody());
    }

    @Test
    void hasBody_withoutBody_shouldReturnFalse() {
        RestServiceRequest request = RestServiceRequest.get(TEST_URL);
        assertFalse(request.hasBody());
    }

    @Test
    void hasBody_withEmptyBody_shouldReturnFalse() {
        RestServiceRequest request = RestServiceRequest.builder(TEST_URL)
                .body("")
                .build();
        assertFalse(request.hasBody());
    }
}
