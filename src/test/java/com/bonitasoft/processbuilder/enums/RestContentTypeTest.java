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
 * Unit tests for the {@link RestContentType} enumeration.
 *
 * @author Bonitasoft
 * @since 1.0
 */
class RestContentTypeTest {

    // =========================================================================
    // ENUM VALUES TESTS
    // =========================================================================

    @Test
    void values_shouldContainAllExpectedConstants() {
        RestContentType[] values = RestContentType.values();
        assertEquals(8, values.length);
        assertNotNull(RestContentType.JSON);
        assertNotNull(RestContentType.XML);
        assertNotNull(RestContentType.TEXT_PLAIN);
        assertNotNull(RestContentType.TEXT_HTML);
        assertNotNull(RestContentType.FORM_URLENCODED);
        assertNotNull(RestContentType.MULTIPART_FORM_DATA);
        assertNotNull(RestContentType.BINARY);
        assertNotNull(RestContentType.PDF);
    }

    // =========================================================================
    // MIME TYPE TESTS
    // =========================================================================

    @Test
    void getMimeType_shouldReturnCorrectMimeType() {
        assertEquals("application/json", RestContentType.JSON.getMimeType());
        assertEquals("application/xml", RestContentType.XML.getMimeType());
        assertEquals("text/plain", RestContentType.TEXT_PLAIN.getMimeType());
        assertEquals("text/html", RestContentType.TEXT_HTML.getMimeType());
        assertEquals("application/x-www-form-urlencoded", RestContentType.FORM_URLENCODED.getMimeType());
        assertEquals("multipart/form-data", RestContentType.MULTIPART_FORM_DATA.getMimeType());
        assertEquals("application/octet-stream", RestContentType.BINARY.getMimeType());
        assertEquals("application/pdf", RestContentType.PDF.getMimeType());
    }

    @Test
    void getKey_shouldReturnSameAsMimeType() {
        for (RestContentType type : RestContentType.values()) {
            assertEquals(type.getMimeType(), type.getKey());
        }
    }

    // =========================================================================
    // TEXT-BASED TESTS
    // =========================================================================

    @Test
    void isTextBased_shouldReturnTrueForTextTypes() {
        assertTrue(RestContentType.JSON.isTextBased());
        assertTrue(RestContentType.XML.isTextBased());
        assertTrue(RestContentType.TEXT_PLAIN.isTextBased());
        assertTrue(RestContentType.TEXT_HTML.isTextBased());
    }

    @Test
    void isTextBased_shouldReturnFalseForBinaryTypes() {
        assertFalse(RestContentType.FORM_URLENCODED.isTextBased());
        assertFalse(RestContentType.MULTIPART_FORM_DATA.isTextBased());
        assertFalse(RestContentType.BINARY.isTextBased());
        assertFalse(RestContentType.PDF.isTextBased());
    }

    // =========================================================================
    // JSON/XML TESTS
    // =========================================================================

    @Test
    void isJson_shouldReturnTrueOnlyForJson() {
        assertTrue(RestContentType.JSON.isJson());
        assertFalse(RestContentType.XML.isJson());
        assertFalse(RestContentType.TEXT_PLAIN.isJson());
    }

    @Test
    void isXml_shouldReturnTrueOnlyForXml() {
        assertTrue(RestContentType.XML.isXml());
        assertFalse(RestContentType.JSON.isXml());
        assertFalse(RestContentType.TEXT_PLAIN.isXml());
    }

    // =========================================================================
    // IS VALID TESTS
    // =========================================================================

    @ParameterizedTest
    @ValueSource(strings = {"application/json", "APPLICATION/JSON", "Application/Json",
                           "application/xml", "text/plain", "text/html"})
    void isValid_shouldReturnTrueForValidMimeTypes(String input) {
        assertTrue(RestContentType.isValid(input));
    }

    @ParameterizedTest
    @ValueSource(strings = {"application/json; charset=utf-8", "application/xml; charset=ISO-8859-1",
                           "text/plain; charset=utf-8"})
    void isValid_shouldReturnTrueForMimeTypesWithCharset(String input) {
        assertTrue(RestContentType.isValid(input));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "invalid", "application/unknown", "text/css"})
    void isValid_shouldReturnFalseForInvalidMimeTypes(String input) {
        assertFalse(RestContentType.isValid(input));
    }

    // =========================================================================
    // FROM MIME TYPE TESTS
    // =========================================================================

    @Test
    void fromMimeType_shouldReturnCorrectTypeForValidMimeType() {
        assertEquals(Optional.of(RestContentType.JSON), RestContentType.fromMimeType("application/json"));
        assertEquals(Optional.of(RestContentType.XML), RestContentType.fromMimeType("application/xml"));
        assertEquals(Optional.of(RestContentType.TEXT_PLAIN), RestContentType.fromMimeType("text/plain"));
    }

    @Test
    void fromMimeType_shouldHandleMimeTypeWithCharset() {
        assertEquals(Optional.of(RestContentType.JSON),
                RestContentType.fromMimeType("application/json; charset=utf-8"));
        assertEquals(Optional.of(RestContentType.XML),
                RestContentType.fromMimeType("application/xml; charset=ISO-8859-1"));
    }

    @Test
    void fromMimeType_shouldBeCaseInsensitive() {
        assertEquals(Optional.of(RestContentType.JSON), RestContentType.fromMimeType("APPLICATION/JSON"));
        assertEquals(Optional.of(RestContentType.XML), RestContentType.fromMimeType("Application/Xml"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "invalid", "text/css"})
    void fromMimeType_shouldReturnEmptyForInvalidMimeType(String mimeType) {
        assertTrue(RestContentType.fromMimeType(mimeType).isEmpty());
    }

    // =========================================================================
    // COLLECTION METHODS TESTS
    // =========================================================================

    @Test
    void getAllData_shouldReturnMapWithAllConstants() {
        Map<String, String> data = RestContentType.getAllData();
        assertEquals(8, data.size());
        assertTrue(data.containsKey("application/json"));
        assertTrue(data.containsKey("application/xml"));
    }

    @Test
    void getAllData_shouldReturnImmutableMap() {
        Map<String, String> data = RestContentType.getAllData();
        assertThrows(UnsupportedOperationException.class, () -> data.put("new", "value"));
    }

    @Test
    void getAllKeysList_shouldReturnListWithAllMimeTypes() {
        List<String> keys = RestContentType.getAllKeysList();
        assertEquals(8, keys.size());
        assertTrue(keys.contains("application/json"));
        assertTrue(keys.contains("application/xml"));
    }

    @Test
    void getAllKeysList_shouldReturnImmutableList() {
        List<String> keys = RestContentType.getAllKeysList();
        assertThrows(UnsupportedOperationException.class, () -> keys.add("new"));
    }
}
