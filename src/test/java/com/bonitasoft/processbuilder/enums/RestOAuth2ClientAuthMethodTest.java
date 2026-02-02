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
 * Unit tests for the {@link RestOAuth2ClientAuthMethod} enumeration.
 *
 * @author Bonitasoft
 * @since 1.0
 */
class RestOAuth2ClientAuthMethodTest {

    // =========================================================================
    // ENUM VALUES TESTS
    // =========================================================================

    @Test
    void values_shouldContainAllExpectedConstants() {
        RestOAuth2ClientAuthMethod[] values = RestOAuth2ClientAuthMethod.values();
        assertEquals(2, values.length);
        assertNotNull(RestOAuth2ClientAuthMethod.BODY);
        assertNotNull(RestOAuth2ClientAuthMethod.HEADER);
    }

    // =========================================================================
    // KEY AND DESCRIPTION TESTS
    // =========================================================================

    @Test
    void getKey_shouldReturnCorrectKeyForEachMethod() {
        assertEquals("body", RestOAuth2ClientAuthMethod.BODY.getKey());
        assertEquals("header", RestOAuth2ClientAuthMethod.HEADER.getKey());
    }

    @Test
    void getDescription_shouldReturnNonEmptyDescription() {
        for (RestOAuth2ClientAuthMethod method : RestOAuth2ClientAuthMethod.values()) {
            assertNotNull(method.getDescription());
            assertFalse(method.getDescription().isBlank());
        }
    }

    // =========================================================================
    // IS VALID TESTS
    // =========================================================================

    @ParameterizedTest
    @ValueSource(strings = {"BODY", "body", "Body", "HEADER", "header", "Header"})
    void isValid_shouldReturnTrueForValidInputs(String input) {
        assertTrue(RestOAuth2ClientAuthMethod.isValid(input));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "invalid", "query", "cookie"})
    void isValid_shouldReturnFalseForInvalidInputs(String input) {
        assertFalse(RestOAuth2ClientAuthMethod.isValid(input));
    }

    // =========================================================================
    // FROM KEY TESTS
    // =========================================================================

    @Test
    void fromKey_shouldReturnCorrectMethodForValidKey() {
        assertEquals(Optional.of(RestOAuth2ClientAuthMethod.BODY), RestOAuth2ClientAuthMethod.fromKey("body"));
        assertEquals(Optional.of(RestOAuth2ClientAuthMethod.HEADER), RestOAuth2ClientAuthMethod.fromKey("header"));
    }

    @Test
    void fromKey_shouldBeCaseInsensitive() {
        assertEquals(Optional.of(RestOAuth2ClientAuthMethod.BODY), RestOAuth2ClientAuthMethod.fromKey("BODY"));
        assertEquals(Optional.of(RestOAuth2ClientAuthMethod.HEADER), RestOAuth2ClientAuthMethod.fromKey("HEADER"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "invalid", "query"})
    void fromKey_shouldReturnEmptyForInvalidKey(String key) {
        assertTrue(RestOAuth2ClientAuthMethod.fromKey(key).isEmpty());
    }

    // =========================================================================
    // COLLECTION METHODS TESTS
    // =========================================================================

    @Test
    void getAllData_shouldReturnMapWithAllConstants() {
        Map<String, String> data = RestOAuth2ClientAuthMethod.getAllData();
        assertEquals(2, data.size());
        assertTrue(data.containsKey("body"));
        assertTrue(data.containsKey("header"));
    }

    @Test
    void getAllData_shouldReturnImmutableMap() {
        Map<String, String> data = RestOAuth2ClientAuthMethod.getAllData();
        assertThrows(UnsupportedOperationException.class, () -> data.put("new", "value"));
    }

    @Test
    void getAllKeysList_shouldReturnListWithAllKeys() {
        List<String> keys = RestOAuth2ClientAuthMethod.getAllKeysList();
        assertEquals(2, keys.size());
        assertTrue(keys.contains("body"));
        assertTrue(keys.contains("header"));
    }

    @Test
    void getAllKeysList_shouldReturnImmutableList() {
        List<String> keys = RestOAuth2ClientAuthMethod.getAllKeysList();
        assertThrows(UnsupportedOperationException.class, () -> keys.add("new"));
    }
}
