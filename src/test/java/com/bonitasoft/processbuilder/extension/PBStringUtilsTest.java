package com.bonitasoft.processbuilder.extension;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link PBStringUtils} utility class.
 * <p>
 * This class ensures that the utility class cannot be instantiated and
 * that all string manipulation methods, especially {@code normalizeTitleCase},
 * function correctly across various input scenarios.
 * </p>
 */
class PBStringUtilsTest {

    // -------------------------------------------------------------------------
    // Utility Class Instantiation Test
    // -------------------------------------------------------------------------

    /**
     * Test case to ensure the private constructor throws an
     * {@link UnsupportedOperationException} when accessed via reflection,
     * confirming that the utility class cannot be instantiated.
     */
    @Test
    @DisplayName("Should throw UnsupportedOperationException on instantiation attempt")
    void shouldThrowExceptionOnInstantiation() throws NoSuchMethodException {
        // 1. Get the private constructor using reflection
        Constructor<PBStringUtils> constructor = PBStringUtils.class.getDeclaredConstructor();
        // 2. Make the constructor accessible (it's private)
        constructor.setAccessible(true);

        // 3. Assert that calling the constructor throws InvocationTargetException,
        // which wraps the actual UnsupportedOperationException
        InvocationTargetException thrown = assertThrows(InvocationTargetException.class, constructor::newInstance,
                "The constructor call must throw an exception.");

        // 4. Check the cause of the exception
        assertEquals(UnsupportedOperationException.class, thrown.getCause().getClass(),
                "The exception cause should be UnsupportedOperationException to enforce the utility pattern.");
    }

    // -------------------------------------------------------------------------
    // normalizeTitleCase Tests
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("normalizeTitleCase should return null for null input")
    void normalizeTitleCase_should_return_null_for_null() {
        assertNull(PBStringUtils.normalizeTitleCase(null));
    }

    @Test
    @DisplayName("normalizeTitleCase should return empty string for empty input")
    void normalizeTitleCase_should_return_empty_for_empty() {
        assertEquals("", PBStringUtils.normalizeTitleCase(""));
    }
    
    @Test
    @DisplayName("normalizeTitleCase should return Title Case for fully lowercase input")
    void normalizeTitleCase_should_handle_lowercase_input() {
        assertEquals("Category", PBStringUtils.normalizeTitleCase("category"));
    }

    @Test
    @DisplayName("normalizeTitleCase should return Title Case for fully uppercase input")
    void normalizeTitleCase_should_handle_uppercase_input() {
        assertEquals("Category", PBStringUtils.normalizeTitleCase("CATEGORY"));
    }

    @Test
    @DisplayName("normalizeTitleCase should return Title Case for mixed case input")
    void normalizeTitleCase_should_handle_mixed_case_input() {
        assertEquals("Category", PBStringUtils.normalizeTitleCase("CaTeGoRy"));
    }

    @Test
    @DisplayName("normalizeTitleCase should handle single lowercase character input")
    void normalizeTitleCase_should_handle_single_lowercase_char() {
        assertEquals("A", PBStringUtils.normalizeTitleCase("a"));
    }

    @Test
    @DisplayName("normalizeTitleCase should handle single uppercase character input")
    void normalizeTitleCase_should_handle_single_uppercase_char() {
        assertEquals("A", PBStringUtils.normalizeTitleCase("A"));
    }

    @Test
    @DisplayName("normalizeTitleCase should handle multi-word inputs (only first word is Title Cased)")
    void normalizeTitleCase_should_handle_multi_word_input() {
        // Note: This implementation only title cases the first letter of the entire string
        assertEquals("Vacation request", PBStringUtils.normalizeTitleCase("VACATION REQUEST"));
    }
}