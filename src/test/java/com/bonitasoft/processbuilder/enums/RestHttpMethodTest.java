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
 * Unit tests for the {@link RestHttpMethod} enumeration.
 *
 * @author Bonitasoft
 * @since 1.0
 */
class RestHttpMethodTest {

    // =========================================================================
    // ENUM VALUES TESTS
    // =========================================================================

    @Test
    void values_shouldContainAllExpectedConstants() {
        RestHttpMethod[] values = RestHttpMethod.values();
        assertEquals(7, values.length);
        assertNotNull(RestHttpMethod.GET);
        assertNotNull(RestHttpMethod.POST);
        assertNotNull(RestHttpMethod.PUT);
        assertNotNull(RestHttpMethod.PATCH);
        assertNotNull(RestHttpMethod.DELETE);
        assertNotNull(RestHttpMethod.HEAD);
        assertNotNull(RestHttpMethod.OPTIONS);
    }

    // =========================================================================
    // KEY AND DESCRIPTION TESTS
    // =========================================================================

    @Test
    void getKey_shouldReturnCorrectKeyForEachMethod() {
        assertEquals("GET", RestHttpMethod.GET.getKey());
        assertEquals("POST", RestHttpMethod.POST.getKey());
        assertEquals("PUT", RestHttpMethod.PUT.getKey());
        assertEquals("PATCH", RestHttpMethod.PATCH.getKey());
        assertEquals("DELETE", RestHttpMethod.DELETE.getKey());
        assertEquals("HEAD", RestHttpMethod.HEAD.getKey());
        assertEquals("OPTIONS", RestHttpMethod.OPTIONS.getKey());
    }

    @Test
    void getDescription_shouldReturnNonEmptyDescription() {
        for (RestHttpMethod method : RestHttpMethod.values()) {
            assertNotNull(method.getDescription());
            assertFalse(method.getDescription().isBlank());
        }
    }

    // =========================================================================
    // SUPPORTS BODY TESTS
    // =========================================================================

    @Test
    void supportsBody_shouldReturnTrueForBodyMethods() {
        assertTrue(RestHttpMethod.POST.supportsBody());
        assertTrue(RestHttpMethod.PUT.supportsBody());
        assertTrue(RestHttpMethod.PATCH.supportsBody());
    }

    @Test
    void supportsBody_shouldReturnFalseForNonBodyMethods() {
        assertFalse(RestHttpMethod.GET.supportsBody());
        assertFalse(RestHttpMethod.DELETE.supportsBody());
        assertFalse(RestHttpMethod.HEAD.supportsBody());
        assertFalse(RestHttpMethod.OPTIONS.supportsBody());
    }

    // =========================================================================
    // IS VALID TESTS
    // =========================================================================

    @ParameterizedTest
    @ValueSource(strings = {"GET", "get", "Get", "POST", "post", "PUT", "put", "PATCH", "DELETE", "HEAD", "OPTIONS"})
    void isValid_shouldReturnTrueForValidInputs(String input) {
        assertTrue(RestHttpMethod.isValid(input));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "INVALID", "CONNECT", "TRACE"})
    void isValid_shouldReturnFalseForInvalidInputs(String input) {
        assertFalse(RestHttpMethod.isValid(input));
    }

    // =========================================================================
    // FROM KEY TESTS
    // =========================================================================

    @Test
    void fromKey_shouldReturnCorrectMethodForValidKey() {
        assertEquals(Optional.of(RestHttpMethod.GET), RestHttpMethod.fromKey("GET"));
        assertEquals(Optional.of(RestHttpMethod.POST), RestHttpMethod.fromKey("POST"));
        assertEquals(Optional.of(RestHttpMethod.PUT), RestHttpMethod.fromKey("PUT"));
        assertEquals(Optional.of(RestHttpMethod.PATCH), RestHttpMethod.fromKey("PATCH"));
        assertEquals(Optional.of(RestHttpMethod.DELETE), RestHttpMethod.fromKey("DELETE"));
    }

    @Test
    void fromKey_shouldBeCaseInsensitive() {
        assertEquals(Optional.of(RestHttpMethod.GET), RestHttpMethod.fromKey("get"));
        assertEquals(Optional.of(RestHttpMethod.POST), RestHttpMethod.fromKey("post"));
        assertEquals(Optional.of(RestHttpMethod.PUT), RestHttpMethod.fromKey("Put"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "INVALID", "CONNECT"})
    void fromKey_shouldReturnEmptyForInvalidKey(String key) {
        assertTrue(RestHttpMethod.fromKey(key).isEmpty());
    }

    // =========================================================================
    // COLLECTION METHODS TESTS
    // =========================================================================

    @Test
    void getAllData_shouldReturnMapWithAllConstants() {
        Map<String, String> data = RestHttpMethod.getAllData();
        assertEquals(7, data.size());
        assertTrue(data.containsKey("GET"));
        assertTrue(data.containsKey("POST"));
        assertTrue(data.containsKey("PUT"));
    }

    @Test
    void getAllData_shouldReturnImmutableMap() {
        Map<String, String> data = RestHttpMethod.getAllData();
        assertThrows(UnsupportedOperationException.class, () -> data.put("NEW", "value"));
    }

    @Test
    void getAllKeysList_shouldReturnListWithAllKeys() {
        List<String> keys = RestHttpMethod.getAllKeysList();
        assertEquals(7, keys.size());
        assertTrue(keys.contains("GET"));
        assertTrue(keys.contains("POST"));
    }

    @Test
    void getAllKeysList_shouldReturnImmutableList() {
        List<String> keys = RestHttpMethod.getAllKeysList();
        assertThrows(UnsupportedOperationException.class, () -> keys.add("NEW"));
    }
}
