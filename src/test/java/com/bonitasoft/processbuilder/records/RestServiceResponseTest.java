package com.bonitasoft.processbuilder.records;

import com.bonitasoft.processbuilder.enums.RestContentType;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link RestServiceResponse} record.
 *
 * @author Bonitasoft
 * @since 1.0
 */
class RestServiceResponseTest {

    private static final String TEST_URL = "https://api.example.com/test";

    // =========================================================================
    // Factory Method Tests
    // =========================================================================

    @Test
    void success_shouldCreateSuccessfulResponse() {
        RestServiceResponse response = RestServiceResponse.success(
                200,
                Map.of("Content-Type", "application/json"),
                "{\"result\":\"ok\"}",
                RestContentType.JSON,
                150L,
                TEST_URL
        );

        assertEquals(200, response.statusCode());
        assertEquals("{\"result\":\"ok\"}", response.body());
        assertEquals(RestContentType.JSON, response.contentType());
        assertEquals(150L, response.executionTimeMs());
        assertNull(response.errorMessage());
        assertEquals(TEST_URL, response.url());
    }

    @Test
    void error_shouldCreateErrorResponse() {
        RestServiceResponse response = RestServiceResponse.error(
                "Connection timeout", 5000L, TEST_URL);

        assertEquals(-1, response.statusCode());
        assertNull(response.body());
        assertNull(response.contentType());
        assertEquals(5000L, response.executionTimeMs());
        assertEquals("Connection timeout", response.errorMessage());
        assertEquals(TEST_URL, response.url());
    }

    @Test
    void fromException_shouldCreateErrorFromException() {
        Exception exception = new RuntimeException("Network error");
        RestServiceResponse response = RestServiceResponse.fromException(exception, 1000L, TEST_URL);

        assertEquals(-1, response.statusCode());
        assertEquals("Network error", response.errorMessage());
    }

    @Test
    void fromException_withNullMessage_shouldUseClassName() {
        Exception exception = new RuntimeException();
        RestServiceResponse response = RestServiceResponse.fromException(exception, 1000L, TEST_URL);

        assertEquals("RuntimeException", response.errorMessage());
    }

    // =========================================================================
    // Status Check Tests
    // =========================================================================

    @ParameterizedTest
    @ValueSource(ints = {200, 201, 204, 299})
    void isSuccessful_with2xxStatus_shouldReturnTrue(int statusCode) {
        RestServiceResponse response = RestServiceResponse.success(
                statusCode, Map.of(), null, null, 0L, TEST_URL);

        assertTrue(response.isSuccessful());
    }

    @ParameterizedTest
    @ValueSource(ints = {100, 300, 400, 500, -1})
    void isSuccessful_withNon2xxStatus_shouldReturnFalse(int statusCode) {
        RestServiceResponse response = new RestServiceResponse(
                statusCode, Map.of(), null, null, 0L, null, TEST_URL);

        assertFalse(response.isSuccessful());
    }

    @ParameterizedTest
    @ValueSource(ints = {400, 401, 403, 404, 422, 499})
    void isClientError_with4xxStatus_shouldReturnTrue(int statusCode) {
        RestServiceResponse response = new RestServiceResponse(
                statusCode, Map.of(), null, null, 0L, null, TEST_URL);

        assertTrue(response.isClientError());
    }

    @ParameterizedTest
    @ValueSource(ints = {200, 300, 500, -1})
    void isClientError_withNon4xxStatus_shouldReturnFalse(int statusCode) {
        RestServiceResponse response = new RestServiceResponse(
                statusCode, Map.of(), null, null, 0L, null, TEST_URL);

        assertFalse(response.isClientError());
    }

    @ParameterizedTest
    @ValueSource(ints = {500, 502, 503, 504, 599})
    void isServerError_with5xxStatus_shouldReturnTrue(int statusCode) {
        RestServiceResponse response = new RestServiceResponse(
                statusCode, Map.of(), null, null, 0L, null, TEST_URL);

        assertTrue(response.isServerError());
    }

    @ParameterizedTest
    @ValueSource(ints = {200, 400, -1})
    void isServerError_withNon5xxStatus_shouldReturnFalse(int statusCode) {
        RestServiceResponse response = new RestServiceResponse(
                statusCode, Map.of(), null, null, 0L, null, TEST_URL);

        assertFalse(response.isServerError());
    }

    @Test
    void isError_withErrorMessage_shouldReturnTrue() {
        RestServiceResponse response = RestServiceResponse.error("Error", 0L, TEST_URL);
        assertTrue(response.isError());
    }

    @Test
    void isError_withNegativeStatus_shouldReturnTrue() {
        RestServiceResponse response = new RestServiceResponse(
                -1, Map.of(), null, null, 0L, null, TEST_URL);
        assertTrue(response.isError());
    }

    @Test
    void isError_with4xxOr5xxStatus_shouldReturnTrue() {
        RestServiceResponse response400 = new RestServiceResponse(
                400, Map.of(), null, null, 0L, null, TEST_URL);
        RestServiceResponse response500 = new RestServiceResponse(
                500, Map.of(), null, null, 0L, null, TEST_URL);

        assertTrue(response400.isError());
        assertTrue(response500.isError());
    }

    @Test
    void isError_with2xxStatus_shouldReturnFalse() {
        RestServiceResponse response = RestServiceResponse.success(
                200, Map.of(), null, null, 0L, TEST_URL);
        assertFalse(response.isError());
    }

    @ParameterizedTest
    @ValueSource(ints = {300, 301, 302, 307, 308})
    void isRedirect_with3xxStatus_shouldReturnTrue(int statusCode) {
        RestServiceResponse response = new RestServiceResponse(
                statusCode, Map.of(), null, null, 0L, null, TEST_URL);

        assertTrue(response.isRedirect());
    }

    // =========================================================================
    // Body Parsing Tests
    // =========================================================================

    @Test
    void bodyAsJson_withValidJson_shouldReturnJsonNode() {
        RestServiceResponse response = RestServiceResponse.success(
                200, Map.of(), "{\"key\":\"value\"}", RestContentType.JSON, 0L, TEST_URL);

        Optional<JsonNode> json = response.bodyAsJson();

        assertTrue(json.isPresent());
        assertEquals("value", json.get().get("key").asText());
    }

    @Test
    void bodyAsJson_withInvalidJson_shouldReturnEmpty() {
        RestServiceResponse response = RestServiceResponse.success(
                200, Map.of(), "not json", RestContentType.TEXT_PLAIN, 0L, TEST_URL);

        Optional<JsonNode> json = response.bodyAsJson();

        assertTrue(json.isEmpty());
    }

    @Test
    void bodyAsJson_withNullBody_shouldReturnEmpty() {
        RestServiceResponse response = RestServiceResponse.success(
                204, Map.of(), null, null, 0L, TEST_URL);

        Optional<JsonNode> json = response.bodyAsJson();

        assertTrue(json.isEmpty());
    }

    @Test
    void bodyAs_withValidJson_shouldDeserialize() {
        RestServiceResponse response = RestServiceResponse.success(
                200, Map.of(), "{\"name\":\"test\"}", RestContentType.JSON, 0L, TEST_URL);

        Optional<TestDto> dto = response.bodyAs(TestDto.class);

        assertTrue(dto.isPresent());
        assertEquals("test", dto.get().name);
    }

    @Test
    void getJsonField_withExistingField_shouldReturnValue() {
        RestServiceResponse response = RestServiceResponse.success(
                200, Map.of(), "{\"id\":\"123\",\"name\":\"test\"}", RestContentType.JSON, 0L, TEST_URL);

        Optional<String> id = response.getJsonField("id");
        Optional<String> name = response.getJsonField("name");

        assertTrue(id.isPresent());
        assertEquals("123", id.get());
        assertTrue(name.isPresent());
        assertEquals("test", name.get());
    }

    @Test
    void getJsonField_withMissingField_shouldReturnEmpty() {
        RestServiceResponse response = RestServiceResponse.success(
                200, Map.of(), "{\"name\":\"test\"}", RestContentType.JSON, 0L, TEST_URL);

        Optional<String> missing = response.getJsonField("missing");

        assertTrue(missing.isEmpty());
    }

    @Test
    void hasJsonBody_withJsonContent_shouldReturnTrue() {
        RestServiceResponse response = RestServiceResponse.success(
                200, Map.of(), "{}", RestContentType.JSON, 0L, TEST_URL);

        assertTrue(response.hasJsonBody());
    }

    @Test
    void hasJsonBody_withNonJsonContent_shouldReturnFalse() {
        RestServiceResponse response = RestServiceResponse.success(
                200, Map.of(), "text", RestContentType.TEXT_PLAIN, 0L, TEST_URL);

        assertFalse(response.hasJsonBody());
    }

    // =========================================================================
    // Header Tests
    // =========================================================================

    @Test
    void getHeader_withExistingHeader_shouldReturnValue() {
        RestServiceResponse response = RestServiceResponse.success(
                200, Map.of("Content-Type", "application/json", "X-Request-Id", "123"),
                null, null, 0L, TEST_URL);

        Optional<String> contentType = response.getHeader("Content-Type");
        Optional<String> requestId = response.getHeader("X-Request-Id");

        assertTrue(contentType.isPresent());
        assertEquals("application/json", contentType.get());
        assertTrue(requestId.isPresent());
        assertEquals("123", requestId.get());
    }

    @Test
    void getHeader_caseInsensitive_shouldReturnValue() {
        RestServiceResponse response = RestServiceResponse.success(
                200, Map.of("Content-Type", "application/json"),
                null, null, 0L, TEST_URL);

        Optional<String> contentType = response.getHeader("content-type");

        assertTrue(contentType.isPresent());
    }

    @Test
    void getHeader_withMissingHeader_shouldReturnEmpty() {
        RestServiceResponse response = RestServiceResponse.success(
                200, Map.of(), null, null, 0L, TEST_URL);

        Optional<String> missing = response.getHeader("Missing");

        assertTrue(missing.isEmpty());
    }

    @Test
    void getLocation_withLocationHeader_shouldReturnValue() {
        RestServiceResponse response = new RestServiceResponse(
                302, Map.of("Location", "https://redirect.example.com"),
                null, null, 0L, null, TEST_URL);

        Optional<String> location = response.getLocation();

        assertTrue(location.isPresent());
        assertEquals("https://redirect.example.com", location.get());
    }

    // =========================================================================
    // Utility Method Tests
    // =========================================================================

    @Test
    void toSummary_shouldFormatCorrectly() {
        RestServiceResponse response = RestServiceResponse.success(
                200, Map.of(), "{}", RestContentType.JSON, 150L, TEST_URL);

        String summary = response.toSummary();

        assertTrue(summary.contains("HTTP 200"));
        assertTrue(summary.contains("150ms"));
        assertTrue(summary.contains("Body:"));
    }

    @Test
    void toSummary_withError_shouldIncludeErrorMessage() {
        RestServiceResponse response = RestServiceResponse.error("Connection failed", 5000L, TEST_URL);

        String summary = response.toSummary();

        assertTrue(summary.contains("ERROR:"));
        assertTrue(summary.contains("Connection failed"));
    }

    @Test
    void withBody_shouldCreateNewResponseWithNewBody() {
        RestServiceResponse original = RestServiceResponse.success(
                200, Map.of(), "original", RestContentType.JSON, 100L, TEST_URL);

        RestServiceResponse modified = original.withBody("modified");

        assertEquals("original", original.body());
        assertEquals("modified", modified.body());
        assertEquals(original.statusCode(), modified.statusCode());
    }

    @Test
    void withError_shouldCreateNewResponseWithError() {
        RestServiceResponse original = RestServiceResponse.success(
                200, Map.of(), "{}", RestContentType.JSON, 100L, TEST_URL);

        RestServiceResponse modified = original.withError("New error");

        assertNull(original.errorMessage());
        assertEquals("New error", modified.errorMessage());
    }

    // =========================================================================
    // Constructor Tests
    // =========================================================================

    @Test
    void constructor_withNullHeaders_shouldDefaultToEmptyMap() {
        RestServiceResponse response = new RestServiceResponse(
                200, null, null, null, 0L, null, TEST_URL);

        assertNotNull(response.headers());
        assertTrue(response.headers().isEmpty());
    }

    @Test
    void constructor_shouldMakeHeadersImmutable() {
        RestServiceResponse response = new RestServiceResponse(
                200, Map.of("Key", "Value"), null, null, 0L, null, TEST_URL);

        assertThrows(UnsupportedOperationException.class, () ->
                response.headers().put("New", "Value"));
    }

    // =========================================================================
    // Helper Class
    // =========================================================================

    static class TestDto {
        public String name;
    }
}
