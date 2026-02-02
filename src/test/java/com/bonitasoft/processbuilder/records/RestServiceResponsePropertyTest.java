package com.bonitasoft.processbuilder.records;

import com.bonitasoft.processbuilder.enums.RestContentType;
import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

/**
 * Property-based tests for {@link RestServiceResponse} record.
 * Tests invariants that must hold for any valid input.
 *
 * @author Bonitasoft
 * @since 1.0
 */
@Label("RestServiceResponse Property-Based Tests")
class RestServiceResponsePropertyTest {

    private static final String TEST_URL = "https://api.example.com/test";

    // =========================================================================
    // CONSTRUCTOR PROPERTIES
    // =========================================================================

    @Property(tries = 100)
    @Label("Constructor should default null headers to empty map")
    void constructorShouldDefaultNullHeadersToEmptyMap() {
        RestServiceResponse response = new RestServiceResponse(
                200, null, null, null, 0L, null, TEST_URL);
        assertThat(response.headers()).isNotNull().isEmpty();
    }

    @Property(tries = 100)
    @Label("Constructor should make headers immutable")
    void constructorShouldMakeHeadersImmutable() {
        RestServiceResponse response = new RestServiceResponse(
                200, Map.of("Key", "Value"), null, null, 0L, null, TEST_URL);
        assertThatThrownBy(() -> response.headers().put("New", "Value"))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    // =========================================================================
    // FACTORY METHOD PROPERTIES
    // =========================================================================

    @Property(tries = 500)
    @Label("success() factory should create response without error message")
    void successFactoryShouldCreateResponseWithoutError(
            @ForAll @IntRange(min = 200, max = 299) int statusCode,
            @ForAll @LongRange(min = 0, max = 10000) long executionTime) {
        RestServiceResponse response = RestServiceResponse.success(
                statusCode, Map.of(), "body", RestContentType.JSON, executionTime, TEST_URL);

        assertThat(response.statusCode()).isEqualTo(statusCode);
        assertThat(response.errorMessage()).isNull();
        assertThat(response.executionTimeMs()).isEqualTo(executionTime);
    }

    @Property(tries = 500)
    @Label("error() factory should create response with status -1")
    void errorFactoryShouldCreateResponseWithNegativeStatus(
            @ForAll @StringLength(min = 1, max = 100) String errorMessage,
            @ForAll @LongRange(min = 0, max = 10000) long executionTime) {
        RestServiceResponse response = RestServiceResponse.error(errorMessage, executionTime, TEST_URL);

        assertThat(response.statusCode()).isEqualTo(-1);
        assertThat(response.errorMessage()).isEqualTo(errorMessage);
        assertThat(response.body()).isNull();
    }

    @Property(tries = 200)
    @Label("fromException() should use exception message when not blank")
    void fromExceptionShouldUseExceptionMessage(
            @ForAll @StringLength(min = 1, max = 100) String message) {
        Assume.that(!message.isBlank()); // Skip blank messages as they fall back to class name

        Exception exception = new RuntimeException(message);
        RestServiceResponse response = RestServiceResponse.fromException(exception, 100L, TEST_URL);

        assertThat(response.errorMessage()).isEqualTo(message);
        assertThat(response.statusCode()).isEqualTo(-1);
    }

    @Property(tries = 100)
    @Label("fromException() should use class name when message is null")
    void fromExceptionShouldUseClassNameWhenMessageNull() {
        Exception exception = new IllegalStateException();
        RestServiceResponse response = RestServiceResponse.fromException(exception, 100L, TEST_URL);

        assertThat(response.errorMessage()).isEqualTo("IllegalStateException");
    }

    // =========================================================================
    // STATUS CHECK PROPERTIES
    // =========================================================================

    @Property(tries = 500)
    @Label("isSuccessful() should return true for 2xx status codes")
    void isSuccessfulShouldReturnTrueFor2xx(@ForAll @IntRange(min = 200, max = 299) int statusCode) {
        RestServiceResponse response = new RestServiceResponse(
                statusCode, Map.of(), null, null, 0L, null, TEST_URL);
        assertThat(response.isSuccessful()).isTrue();
    }

    @Property(tries = 500)
    @Label("isSuccessful() should return false for non-2xx status codes")
    void isSuccessfulShouldReturnFalseForNon2xx(@ForAll @IntRange(min = 300, max = 599) int statusCode) {
        RestServiceResponse response = new RestServiceResponse(
                statusCode, Map.of(), null, null, 0L, null, TEST_URL);
        assertThat(response.isSuccessful()).isFalse();
    }

    @Property(tries = 500)
    @Label("isClientError() should return true for 4xx status codes")
    void isClientErrorShouldReturnTrueFor4xx(@ForAll @IntRange(min = 400, max = 499) int statusCode) {
        RestServiceResponse response = new RestServiceResponse(
                statusCode, Map.of(), null, null, 0L, null, TEST_URL);
        assertThat(response.isClientError()).isTrue();
    }

    @Property(tries = 500)
    @Label("isClientError() should return false for non-4xx status codes")
    void isClientErrorShouldReturnFalseForNon4xx(@ForAll @IntRange(min = 200, max = 399) int statusCode) {
        RestServiceResponse response = new RestServiceResponse(
                statusCode, Map.of(), null, null, 0L, null, TEST_URL);
        assertThat(response.isClientError()).isFalse();
    }

    @Property(tries = 500)
    @Label("isServerError() should return true for 5xx status codes")
    void isServerErrorShouldReturnTrueFor5xx(@ForAll @IntRange(min = 500, max = 599) int statusCode) {
        RestServiceResponse response = new RestServiceResponse(
                statusCode, Map.of(), null, null, 0L, null, TEST_URL);
        assertThat(response.isServerError()).isTrue();
    }

    @Property(tries = 500)
    @Label("isServerError() should return false for non-5xx status codes")
    void isServerErrorShouldReturnFalseForNon5xx(@ForAll @IntRange(min = 200, max = 499) int statusCode) {
        RestServiceResponse response = new RestServiceResponse(
                statusCode, Map.of(), null, null, 0L, null, TEST_URL);
        assertThat(response.isServerError()).isFalse();
    }

    @Property(tries = 500)
    @Label("isRedirect() should return true for 3xx status codes")
    void isRedirectShouldReturnTrueFor3xx(@ForAll @IntRange(min = 300, max = 399) int statusCode) {
        RestServiceResponse response = new RestServiceResponse(
                statusCode, Map.of(), null, null, 0L, null, TEST_URL);
        assertThat(response.isRedirect()).isTrue();
    }

    @Property(tries = 500)
    @Label("isError() should return true when errorMessage is present")
    void isErrorShouldReturnTrueWithErrorMessage(
            @ForAll @StringLength(min = 1, max = 50) String errorMessage) {
        RestServiceResponse response = new RestServiceResponse(
                200, Map.of(), null, null, 0L, errorMessage, TEST_URL);
        assertThat(response.isError()).isTrue();
    }

    @Property(tries = 500)
    @Label("isError() should return true for 4xx and 5xx status codes")
    void isErrorShouldReturnTrueFor4xxAnd5xx(@ForAll @IntRange(min = 400, max = 599) int statusCode) {
        RestServiceResponse response = new RestServiceResponse(
                statusCode, Map.of(), null, null, 0L, null, TEST_URL);
        assertThat(response.isError()).isTrue();
    }

    @Property(tries = 500)
    @Label("isError() should return false for successful responses")
    void isErrorShouldReturnFalseForSuccess(@ForAll @IntRange(min = 200, max = 299) int statusCode) {
        RestServiceResponse response = RestServiceResponse.success(
                statusCode, Map.of(), null, null, 0L, TEST_URL);
        assertThat(response.isError()).isFalse();
    }

    // =========================================================================
    // BODY PARSING PROPERTIES
    // =========================================================================

    @Property(tries = 100)
    @Label("bodyAsJson() should return empty for null body")
    void bodyAsJsonShouldReturnEmptyForNullBody() {
        RestServiceResponse response = RestServiceResponse.success(
                200, Map.of(), null, RestContentType.JSON, 0L, TEST_URL);
        assertThat(response.bodyAsJson()).isEmpty();
    }

    @Property(tries = 100)
    @Label("bodyAsJson() should return empty for blank body")
    void bodyAsJsonShouldReturnEmptyForBlankBody() {
        RestServiceResponse response = RestServiceResponse.success(
                200, Map.of(), "   ", RestContentType.JSON, 0L, TEST_URL);
        assertThat(response.bodyAsJson()).isEmpty();
    }

    @Property(tries = 100)
    @Label("bodyAsJson() should return present for valid JSON")
    void bodyAsJsonShouldReturnPresentForValidJson() {
        RestServiceResponse response = RestServiceResponse.success(
                200, Map.of(), "{\"key\":\"value\"}", RestContentType.JSON, 0L, TEST_URL);
        assertThat(response.bodyAsJson()).isPresent();
    }

    @Property(tries = 100)
    @Label("bodyAsJson() should return empty for invalid JSON")
    void bodyAsJsonShouldReturnEmptyForInvalidJson() {
        RestServiceResponse response = RestServiceResponse.success(
                200, Map.of(), "not json", RestContentType.TEXT_PLAIN, 0L, TEST_URL);
        assertThat(response.bodyAsJson()).isEmpty();
    }

    @Property(tries = 100)
    @Label("hasJsonBody() should return true for JSON content with non-empty body")
    void hasJsonBodyShouldReturnTrueForJsonContent() {
        RestServiceResponse response = RestServiceResponse.success(
                200, Map.of(), "{}", RestContentType.JSON, 0L, TEST_URL);
        assertThat(response.hasJsonBody()).isTrue();
    }

    @Property(tries = 100)
    @Label("hasJsonBody() should return false for non-JSON content")
    void hasJsonBodyShouldReturnFalseForNonJsonContent() {
        RestServiceResponse response = RestServiceResponse.success(
                200, Map.of(), "text", RestContentType.TEXT_PLAIN, 0L, TEST_URL);
        assertThat(response.hasJsonBody()).isFalse();
    }

    // =========================================================================
    // HEADER METHODS PROPERTIES
    // =========================================================================

    @Property(tries = 500)
    @Label("getHeader() should be case insensitive")
    void getHeaderShouldBeCaseInsensitive(
            @ForAll @StringLength(min = 1, max = 20) @AlphaChars String headerName,
            @ForAll @StringLength(min = 1, max = 50) String headerValue) {
        RestServiceResponse response = new RestServiceResponse(
                200, Map.of(headerName, headerValue), null, null, 0L, null, TEST_URL);

        Optional<String> result1 = response.getHeader(headerName.toLowerCase());
        Optional<String> result2 = response.getHeader(headerName.toUpperCase());

        assertThat(result1).isPresent().contains(headerValue);
        assertThat(result2).isPresent().contains(headerValue);
    }

    @Property(tries = 100)
    @Label("getHeader() should return empty for null header name")
    void getHeaderShouldReturnEmptyForNullName() {
        RestServiceResponse response = new RestServiceResponse(
                200, Map.of("Key", "Value"), null, null, 0L, null, TEST_URL);
        assertThat(response.getHeader(null)).isEmpty();
    }

    @Property(tries = 100)
    @Label("getLocation() should return Location header value")
    void getLocationShouldReturnLocationHeader() {
        String redirectUrl = "https://redirect.example.com";
        RestServiceResponse response = new RestServiceResponse(
                302, Map.of("Location", redirectUrl), null, null, 0L, null, TEST_URL);
        assertThat(response.getLocation()).isPresent().contains(redirectUrl);
    }

    // =========================================================================
    // UTILITY METHOD PROPERTIES
    // =========================================================================

    @Property(tries = 500)
    @Label("toSummary() should contain status code")
    void toSummaryShouldContainStatusCode(@ForAll @IntRange(min = 100, max = 599) int statusCode) {
        RestServiceResponse response = new RestServiceResponse(
                statusCode, Map.of(), null, null, 0L, null, TEST_URL);
        assertThat(response.toSummary()).contains("HTTP " + statusCode);
    }

    @Property(tries = 500)
    @Label("toSummary() should contain execution time")
    void toSummaryShouldContainExecutionTime(@ForAll @LongRange(min = 0, max = 10000) long time) {
        RestServiceResponse response = new RestServiceResponse(
                200, Map.of(), null, null, time, null, TEST_URL);
        assertThat(response.toSummary()).contains(time + "ms");
    }

    @Property(tries = 200)
    @Label("toSummary() should contain error message when present")
    void toSummaryShouldContainErrorMessage(
            @ForAll @StringLength(min = 1, max = 50) @AlphaChars String errorMessage) {
        RestServiceResponse response = RestServiceResponse.error(errorMessage, 0L, TEST_URL);
        assertThat(response.toSummary()).contains("ERROR");
        assertThat(response.toSummary()).contains(errorMessage);
    }

    @Property(tries = 500)
    @Label("withBody() should create new response with different body")
    void withBodyShouldCreateNewResponse(
            @ForAll @StringLength(min = 1, max = 50) String originalBody,
            @ForAll @StringLength(min = 1, max = 50) String newBody) {
        Assume.that(!originalBody.equals(newBody));

        RestServiceResponse original = RestServiceResponse.success(
                200, Map.of(), originalBody, RestContentType.JSON, 100L, TEST_URL);
        RestServiceResponse modified = original.withBody(newBody);

        assertThat(original.body()).isEqualTo(originalBody);
        assertThat(modified.body()).isEqualTo(newBody);
        assertThat(modified.statusCode()).isEqualTo(original.statusCode());
        assertThat(modified.executionTimeMs()).isEqualTo(original.executionTimeMs());
    }

    @Property(tries = 500)
    @Label("withError() should create new response with error message")
    void withErrorShouldCreateNewResponse(
            @ForAll @StringLength(min = 1, max = 50) String errorMessage) {
        RestServiceResponse original = RestServiceResponse.success(
                200, Map.of(), "{}", RestContentType.JSON, 100L, TEST_URL);
        RestServiceResponse modified = original.withError(errorMessage);

        assertThat(original.errorMessage()).isNull();
        assertThat(modified.errorMessage()).isEqualTo(errorMessage);
        assertThat(modified.statusCode()).isEqualTo(original.statusCode());
    }
}
