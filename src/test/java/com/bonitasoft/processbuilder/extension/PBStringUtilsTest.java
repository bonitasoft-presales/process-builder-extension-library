package com.bonitasoft.processbuilder.extension;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link PBStringUtils} utility class.
 * <p>
 * This class ensures that the utility class cannot be instantiated and
 * that all string manipulation methods, especially {@code normalizeTitleCase}
 * and {@code convertToSnakeCase}, function correctly across various input scenarios.
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
    void shouldThrowExceptionOnInstantiation() throws Exception { 
        // 1. Get the private constructor using reflection
        Constructor<PBStringUtils> constructor = PBStringUtils.class.getDeclaredConstructor();
        
        assertTrue(Modifier.isPrivate(constructor.getModifiers()), "Constructor must be private.");
        
        // 2. Make the constructor accessible (it's private)
        constructor.setAccessible(true);

        // 3. Assert that calling the constructor throws InvocationTargetException,
        // which wraps the actual UnsupportedOperationException
        InvocationTargetException thrown = assertThrows(InvocationTargetException.class, constructor::newInstance,
                "The constructor call must throw an exception.");

        // 4. Check the cause of the exception
        assertEquals(UnsupportedOperationException.class, thrown.getCause().getClass(),
                "The exception cause should be UnsupportedOperationException to enforce the utility pattern.");
        
        final String expectedMessage = "This is a PBStringUtils class and cannot be instantiated.";
        assertTrue(thrown.getCause().getMessage().contains(expectedMessage.substring(10, 29)), 
                "The exception message should contain 'PBStringUtils class and cannot be instantiated.'");
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
    @DisplayName("normalizeTitleCase should handle single lowercase character input (Covers length == 1)")
    void normalizeTitleCase_should_handle_single_lowercase_char() {
        assertEquals("A", PBStringUtils.normalizeTitleCase("a"));
    }

    @Test
    @DisplayName("normalizeTitleCase should handle single uppercase character input (Covers length == 1)")
    void normalizeTitleCase_should_handle_single_uppercase_char() {
        assertEquals("A", PBStringUtils.normalizeTitleCase("A"));
    }

    @Test
    @DisplayName("normalizeTitleCase should handle multi-word inputs (only first letter capitalized)")
    void normalizeTitleCase_should_handle_multi_word_input() {
        assertEquals("Vacation request", PBStringUtils.normalizeTitleCase("VACATION REQUEST"));
    }
    
    // -------------------------------------------------------------------------
    // convertToSnakeCase Tests (New for full coverage)
    // -------------------------------------------------------------------------

    /**
     * Test case for {@code convertToSnakeCase} with a null input.
     */
    @Test
    @DisplayName("convertToSnakeCase should return null for null input")
    void convertToSnakeCase_should_return_null_for_null() {
        assertNull(PBStringUtils.convertToSnakeCase(null));
    }

    /**
     * Test case for {@code convertToSnakeCase} with standard mixed-case, multi-word input.
     */
    @Test
    @DisplayName("convertToSnakeCase should convert mixed-case space-separated string to snake_case")
    void convertToSnakeCase_should_handle_mixed_case_input() {
        assertEquals("bonita_and_delete", PBStringUtils.convertToSnakeCase("Bonita and delete"));
    }

    /**
     * Test case for {@code convertToSnakeCase} with all uppercase input.
     */
    @Test
    @DisplayName("convertToSnakeCase should convert fully uppercase input to snake_case")
    void convertToSnakeCase_should_handle_uppercase_input() {
        // "PROCESS NAME" -> "process_name"
        assertEquals("process_name", PBStringUtils.convertToSnakeCase("PROCESS NAME"));
    }

    /**
     * Test case for {@code convertToSnakeCase} with single word input (should only lowercase).
     */
    @Test
    @DisplayName("convertToSnakeCase should handle single word input by only lowercasing")
    void convertToSnakeCase_should_handle_single_word() {
        // "Category" -> "category"
        assertEquals("category", PBStringUtils.convertToSnakeCase("Category"));
    }
    
    /**
     * Test case for {@code convertToSnakeCase} with empty string input.
     */
    @Test
    @DisplayName("convertToSnakeCase should return empty string for empty input")
    void convertToSnakeCase_should_return_empty_string() {
        assertEquals("", PBStringUtils.convertToSnakeCase(""));
    }
}