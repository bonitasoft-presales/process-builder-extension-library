package com.bonitasoft.processbuilder.records;

import com.bonitasoft.processbuilder.enums.RestContentType;
import com.bonitasoft.processbuilder.enums.RestHttpMethod;
import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

import java.util.Map;

import static org.assertj.core.api.Assertions.*;

/**
 * Property-based tests for {@link RestServiceRequest} record.
 * Tests invariants that must hold for any valid input.
 *
 * @author Bonitasoft
 * @since 1.0
 */
@Label("RestServiceRequest Property-Based Tests")
class RestServiceRequestPropertyTest {

    // =========================================================================
    // CONSTRUCTOR VALIDATION PROPERTIES
    // =========================================================================

    @Property(tries = 100)
    @Label("Constructor should reject null URL")
    void constructorShouldRejectNullUrl() {
        assertThatThrownBy(() -> new RestServiceRequest(
                null, null, null, null, null, null, null, 0, true, true))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Property(tries = 100)
    @Label("Constructor should reject empty URL")
    void constructorShouldRejectEmptyUrl() {
        assertThatThrownBy(() -> new RestServiceRequest(
                "", null, null, null, null, null, null, 0, true, true))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Property(tries = 100)
    @Label("Constructor should reject blank URL")
    void constructorShouldRejectBlankUrl() {
        assertThatThrownBy(() -> new RestServiceRequest(
                "   ", null, null, null, null, null, null, 0, true, true))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Property(tries = 500)
    @Label("Constructor should trim URL")
    void constructorShouldTrimUrl(@ForAll @From("urls") String baseUrl) {
        String paddedUrl = "  " + baseUrl + "  ";
        RestServiceRequest request = new RestServiceRequest(
                paddedUrl, null, null, null, null, null, null, 0, true, true);
        assertThat(request.url()).isEqualTo(baseUrl);
    }

    // =========================================================================
    // DEFAULT VALUES PROPERTIES
    // =========================================================================

    @Property(tries = 500)
    @Label("Constructor should default method to GET")
    void constructorShouldDefaultMethodToGet(@ForAll @From("urls") String url) {
        RestServiceRequest request = new RestServiceRequest(
                url, null, null, null, null, null, null, 0, true, true);
        assertThat(request.method()).isEqualTo(RestHttpMethod.GET);
    }

    @Property(tries = 500)
    @Label("Constructor should default contentType to JSON")
    void constructorShouldDefaultContentTypeToJson(@ForAll @From("urls") String url) {
        RestServiceRequest request = new RestServiceRequest(
                url, null, null, null, null, null, null, 0, true, true);
        assertThat(request.contentType()).isEqualTo(RestContentType.JSON);
    }

    @Property(tries = 500)
    @Label("Constructor should default auth to NoAuth")
    void constructorShouldDefaultAuthToNoAuth(@ForAll @From("urls") String url) {
        RestServiceRequest request = new RestServiceRequest(
                url, null, null, null, null, null, null, 0, true, true);
        assertThat(request.auth()).isInstanceOf(RestAuthConfig.NoAuth.class);
    }

    @Property(tries = 500)
    @Label("Constructor should default timeout to DEFAULT_TIMEOUT_MS")
    void constructorShouldDefaultTimeoutToDefault(@ForAll @From("urls") String url) {
        RestServiceRequest request = new RestServiceRequest(
                url, null, null, null, null, null, null, 0, true, true);
        assertThat(request.timeoutMs()).isEqualTo(RestServiceRequest.DEFAULT_TIMEOUT_MS);
    }

    @Property(tries = 500)
    @Label("Constructor should default headers to empty map")
    void constructorShouldDefaultHeadersToEmptyMap(@ForAll @From("urls") String url) {
        RestServiceRequest request = new RestServiceRequest(
                url, null, null, null, null, null, null, 0, true, true);
        assertThat(request.headers()).isEmpty();
    }

    @Property(tries = 500)
    @Label("Constructor should default queryParams to empty map")
    void constructorShouldDefaultQueryParamsToEmptyMap(@ForAll @From("urls") String url) {
        RestServiceRequest request = new RestServiceRequest(
                url, null, null, null, null, null, null, 0, true, true);
        assertThat(request.queryParams()).isEmpty();
    }

    // =========================================================================
    // BUILDER PROPERTIES
    // =========================================================================

    @Property(tries = 500)
    @Label("Builder should create request with specified URL")
    void builderShouldCreateRequestWithUrl(@ForAll @From("urls") String url) {
        RestServiceRequest request = RestServiceRequest.builder(url).build();
        assertThat(request.url()).isEqualTo(url);
    }

    @Property(tries = 200)
    @Label("Builder should set method correctly")
    void builderShouldSetMethodCorrectly(
            @ForAll @From("urls") String url,
            @ForAll @From("httpMethods") RestHttpMethod method) {
        RestServiceRequest request = RestServiceRequest.builder(url).method(method).build();
        assertThat(request.method()).isEqualTo(method);
    }

    @Property(tries = 500)
    @Label("Builder header() should accumulate headers")
    void builderHeaderShouldAccumulateHeaders(
            @ForAll @From("urls") String url,
            @ForAll @StringLength(min = 1, max = 20) @AlphaChars String key1,
            @ForAll @StringLength(min = 1, max = 20) String value1,
            @ForAll @StringLength(min = 1, max = 20) @AlphaChars String key2,
            @ForAll @StringLength(min = 1, max = 20) String value2) {
        Assume.that(!key1.equals(key2)); // Ensure different keys

        RestServiceRequest request = RestServiceRequest.builder(url)
                .header(key1, value1)
                .header(key2, value2)
                .build();

        assertThat(request.headers()).hasSize(2);
        assertThat(request.headers().get(key1)).isEqualTo(value1);
        assertThat(request.headers().get(key2)).isEqualTo(value2);
    }

    @Property(tries = 500)
    @Label("Builder queryParam() should accumulate params")
    void builderQueryParamShouldAccumulateParams(
            @ForAll @From("urls") String url,
            @ForAll @StringLength(min = 1, max = 20) @AlphaChars String key1,
            @ForAll @StringLength(min = 1, max = 20) String value1,
            @ForAll @StringLength(min = 1, max = 20) @AlphaChars String key2,
            @ForAll @StringLength(min = 1, max = 20) String value2) {
        Assume.that(!key1.equals(key2)); // Ensure different keys

        RestServiceRequest request = RestServiceRequest.builder(url)
                .queryParam(key1, value1)
                .queryParam(key2, value2)
                .build();

        assertThat(request.queryParams()).hasSize(2);
    }

    @Property(tries = 200)
    @Label("Builder timeout() should set positive timeout")
    void builderTimeoutShouldSetPositiveTimeout(
            @ForAll @From("urls") String url,
            @ForAll @IntRange(min = 1, max = 300000) int timeout) {
        RestServiceRequest request = RestServiceRequest.builder(url)
                .timeout(timeout)
                .build();
        assertThat(request.timeoutMs()).isEqualTo(timeout);
    }

    @Property(tries = 100)
    @Label("Builder verifySsl() should set flag correctly")
    void builderVerifySslShouldSetFlagCorrectly(
            @ForAll @From("urls") String url,
            @ForAll boolean verify) {
        RestServiceRequest request = RestServiceRequest.builder(url)
                .verifySsl(verify)
                .build();
        assertThat(request.verifySsl()).isEqualTo(verify);
    }

    @Property(tries = 100)
    @Label("Builder followRedirects() should set flag correctly")
    void builderFollowRedirectsShouldSetFlagCorrectly(
            @ForAll @From("urls") String url,
            @ForAll boolean follow) {
        RestServiceRequest request = RestServiceRequest.builder(url)
                .followRedirects(follow)
                .build();
        assertThat(request.followRedirects()).isEqualTo(follow);
    }

    // =========================================================================
    // FACTORY METHOD PROPERTIES
    // =========================================================================

    @Property(tries = 500)
    @Label("Factory get() should create GET request")
    void factoryGetShouldCreateGetRequest(@ForAll @From("urls") String url) {
        RestServiceRequest request = RestServiceRequest.get(url);
        assertThat(request.url()).isEqualTo(url);
        assertThat(request.method()).isEqualTo(RestHttpMethod.GET);
    }

    @Property(tries = 500)
    @Label("Factory postJson() should create POST request with JSON body")
    void factoryPostJsonShouldCreatePostRequest(
            @ForAll @From("urls") String url,
            @ForAll @StringLength(min = 1, max = 100) String body) {
        RestServiceRequest request = RestServiceRequest.postJson(url, body);
        assertThat(request.url()).isEqualTo(url);
        assertThat(request.method()).isEqualTo(RestHttpMethod.POST);
        assertThat(request.body()).isEqualTo(body);
        assertThat(request.contentType()).isEqualTo(RestContentType.JSON);
    }

    // =========================================================================
    // UTILITY METHOD PROPERTIES
    // =========================================================================

    @Property(tries = 500)
    @Label("buildFullUrl() should return original URL when no query params")
    void buildFullUrlShouldReturnOriginalWhenNoParams(@ForAll @From("urlsWithoutQuery") String url) {
        RestServiceRequest request = RestServiceRequest.get(url);
        assertThat(request.buildFullUrl()).isEqualTo(url);
    }

    @Property(tries = 500)
    @Label("buildFullUrl() should append query params correctly")
    void buildFullUrlShouldAppendQueryParams(
            @ForAll @From("urlsWithoutQuery") String url,
            @ForAll @StringLength(min = 1, max = 10) @AlphaChars String key,
            @ForAll @StringLength(min = 1, max = 10) @AlphaChars String value) {
        RestServiceRequest request = RestServiceRequest.builder(url)
                .queryParam(key, value)
                .build();

        String fullUrl = request.buildFullUrl();
        assertThat(fullUrl).startsWith(url);
        assertThat(fullUrl).contains("?");
        assertThat(fullUrl).contains(key + "=" + value);
    }

    @Property(tries = 500)
    @Label("buildAllHeaders() should include Content-Type when body is present")
    void buildAllHeadersShouldIncludeContentTypeWithBody(@ForAll @From("urls") String url) {
        RestServiceRequest request = RestServiceRequest.builder(url)
                .post()
                .body("{}")
                .contentType(RestContentType.JSON)
                .build();

        Map<String, String> headers = request.buildAllHeaders();
        assertThat(headers).containsKey("Content-Type");
        assertThat(headers.get("Content-Type")).isEqualTo("application/json");
    }

    @Property(tries = 500)
    @Label("buildAllHeaders() should not include Content-Type when body is absent")
    void buildAllHeadersShouldNotIncludeContentTypeWithoutBody(@ForAll @From("urls") String url) {
        RestServiceRequest request = RestServiceRequest.get(url);

        Map<String, String> headers = request.buildAllHeaders();
        assertThat(headers).doesNotContainKey("Content-Type");
    }

    @Property(tries = 500)
    @Label("hasBody() should return true when body is non-empty")
    void hasBodyShouldReturnTrueWhenBodyNonEmpty(
            @ForAll @From("urls") String url,
            @ForAll @StringLength(min = 1, max = 100) String body) {
        RestServiceRequest request = RestServiceRequest.builder(url)
                .body(body)
                .build();
        assertThat(request.hasBody()).isTrue();
    }

    @Property(tries = 500)
    @Label("hasBody() should return false when body is null or empty")
    void hasBodyShouldReturnFalseWhenBodyEmpty(@ForAll @From("urls") String url) {
        RestServiceRequest request1 = RestServiceRequest.get(url);
        RestServiceRequest request2 = RestServiceRequest.builder(url).body("").build();

        assertThat(request1.hasBody()).isFalse();
        assertThat(request2.hasBody()).isFalse();
    }

    // =========================================================================
    // ARBITRARIES (DATA GENERATORS)
    // =========================================================================

    @Provide
    Arbitrary<String> urls() {
        return Arbitraries.of(
                "https://api.example.com/test",
                "http://localhost:8080/api",
                "https://test.com/v1/users",
                "http://192.168.1.1:3000/data",
                "https://api.example.com/test?existing=true"
        );
    }

    @Provide
    Arbitrary<String> urlsWithoutQuery() {
        return Arbitraries.of(
                "https://api.example.com/test",
                "http://localhost:8080/api",
                "https://test.com/v1/users",
                "http://192.168.1.1:3000/data"
        );
    }

    @Provide
    Arbitrary<RestHttpMethod> httpMethods() {
        return Arbitraries.of(RestHttpMethod.values());
    }
}
