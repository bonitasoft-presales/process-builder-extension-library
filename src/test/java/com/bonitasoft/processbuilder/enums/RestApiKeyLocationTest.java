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
 * Unit tests for the {@link RestApiKeyLocation} enumeration.
 *
 * @author Bonitasoft
 * @since 1.0
 */
class RestApiKeyLocationTest {

    // =========================================================================
    // ENUM VALUES TESTS
    // =========================================================================

    @Test
    void values_shouldContainAllExpectedConstants() {
        RestApiKeyLocation[] values = RestApiKeyLocation.values();
        assertEquals(2, values.length);
        assertNotNull(RestApiKeyLocation.HEADER);
        assertNotNull(RestApiKeyLocation.QUERY_PARAM);
    }

    // =========================================================================
    // KEY AND DESCRIPTION TESTS
    // =========================================================================

    @Test
    void getKey_shouldReturnCorrectKeyForEachLocation() {
        assertEquals("header", RestApiKeyLocation.HEADER.getKey());
        assertEquals("queryParam", RestApiKeyLocation.QUERY_PARAM.getKey());
    }

    @Test
    void getDescription_shouldReturnNonEmptyDescription() {
        for (RestApiKeyLocation location : RestApiKeyLocation.values()) {
            assertNotNull(location.getDescription());
            assertFalse(location.getDescription().isBlank());
        }
    }

    // =========================================================================
    // IS VALID TESTS
    // =========================================================================

    @ParameterizedTest
    @ValueSource(strings = {"HEADER", "header", "Header", "QUERY_PARAM", "query_param", "queryParam"})
    void isValid_shouldReturnTrueForValidInputs(String input) {
        assertTrue(RestApiKeyLocation.isValid(input));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "invalid", "body", "cookie"})
    void isValid_shouldReturnFalseForInvalidInputs(String input) {
        assertFalse(RestApiKeyLocation.isValid(input));
    }

    // =========================================================================
    // FROM KEY TESTS
    // =========================================================================

    @Test
    void fromKey_shouldReturnCorrectLocationForValidKey() {
        assertEquals(Optional.of(RestApiKeyLocation.HEADER), RestApiKeyLocation.fromKey("header"));
        assertEquals(Optional.of(RestApiKeyLocation.QUERY_PARAM), RestApiKeyLocation.fromKey("queryParam"));
    }

    @Test
    void fromKey_shouldBeCaseInsensitive() {
        assertEquals(Optional.of(RestApiKeyLocation.HEADER), RestApiKeyLocation.fromKey("HEADER"));
        assertEquals(Optional.of(RestApiKeyLocation.QUERY_PARAM), RestApiKeyLocation.fromKey("QUERYPARAM"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "invalid", "body"})
    void fromKey_shouldReturnEmptyForInvalidKey(String key) {
        assertTrue(RestApiKeyLocation.fromKey(key).isEmpty());
    }

    // =========================================================================
    // COLLECTION METHODS TESTS
    // =========================================================================

    @Test
    void getAllData_shouldReturnMapWithAllConstants() {
        Map<String, String> data = RestApiKeyLocation.getAllData();
        assertEquals(2, data.size());
        assertTrue(data.containsKey("header"));
        assertTrue(data.containsKey("queryParam"));
    }

    @Test
    void getAllData_shouldReturnImmutableMap() {
        Map<String, String> data = RestApiKeyLocation.getAllData();
        assertThrows(UnsupportedOperationException.class, () -> data.put("new", "value"));
    }

    @Test
    void getAllKeysList_shouldReturnListWithAllKeys() {
        List<String> keys = RestApiKeyLocation.getAllKeysList();
        assertEquals(2, keys.size());
        assertTrue(keys.contains("header"));
        assertTrue(keys.contains("queryParam"));
    }

    @Test
    void getAllKeysList_shouldReturnImmutableList() {
        List<String> keys = RestApiKeyLocation.getAllKeysList();
        assertThrows(UnsupportedOperationException.class, () -> keys.add("new"));
    }
}
