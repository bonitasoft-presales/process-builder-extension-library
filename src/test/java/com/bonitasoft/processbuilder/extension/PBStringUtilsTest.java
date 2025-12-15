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
 * that all string manipulation methods, especially {@code normalizeTitleCase}, {@code toLowerSnakeCase}
 * and {@code toUpperSnakeCase}, function correctly across various input scenarios.
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
    // toLowerSnakeCase Tests (New for full coverage)
    // -------------------------------------------------------------------------

    /**
     * Test case for {@code toLowerSnakeCase} with a null input.
     */
    @Test
    @DisplayName("toLowerSnakeCase should return null for null input")
    void toLowerSnakeCase_should_return_null_for_null() {
        assertNull(PBStringUtils.toLowerSnakeCase(null));
    }

    /**
     * Test case for {@code toLowerSnakeCase} with standard mixed-case, multi-word input.
     */
    @Test
    @DisplayName("toLowerSnakeCase should convert mixed-case space-separated string to snake_case")
    void toLowerSnakeCase_should_handle_mixed_case_input() {
        assertEquals("bonita_and_delete", PBStringUtils.toLowerSnakeCase("Bonita and delete"));
    }

    /**
     * Test case for {@code toLowerSnakeCase} with all uppercase input.
     */
    @Test
    @DisplayName("toLowerSnakeCase should convert fully uppercase input to snake_case")
    void toLowerSnakeCase_should_handle_uppercase_input() {
        // "PROCESS NAME" -> "process_name"
        assertEquals("process_name", PBStringUtils.toLowerSnakeCase("PROCESS NAME"));
    }

    /**
     * Test case for {@code toLowerSnakeCase} with single word input (should only lowercase).
     */
    @Test
    @DisplayName("toLowerSnakeCase should handle single word input by only lowercasing")
    void toLowerSnakeCase_should_handle_single_word() {
        // "Category" -> "category"
        assertEquals("category", PBStringUtils.toLowerSnakeCase("Category"));
    }
    
    /**
     * Test case for {@code toLowerSnakeCase} with empty string input.
     */
    @Test
    @DisplayName("toLowerSnakeCase should return empty string for empty input")
    void toLowerSnakeCase_should_return_empty_string() {
        assertEquals("", PBStringUtils.toLowerSnakeCase(""));
    }

     // -------------------------------------------------------------------------
    // toUpperSnakeCase Tests (New for full coverage)
    // -------------------------------------------------------------------------

    /**
     * Test case for {@code toUpperSnakeCase} with a null input.
     */
    @Test
    @DisplayName("toUpperSnakeCase should return null for null input")
    void toUpperSnakeCase_should_return_null_for_null() {
        assertNull(PBStringUtils.toUpperSnakeCase(null));
    }

    /**
     * Test case for {@code toUpperSnakeCase} with standard mixed-case, multi-word input.
     */
    @Test
    @DisplayName("toUpperSnakeCase should convert mixed-case space-separated string to snake_case")
    void toUpperSnakeCase_should_handle_mixed_case_input() {
        assertEquals("BONITA_AND_DELETE", PBStringUtils.toUpperSnakeCase("Bonita and delete"));
    }

    /**
     * Test case for {@code toUpperSnakeCase} with all uppercase input.
     */
    @Test
    @DisplayName("toUpperSnakeCase should convert fully uppercase input to snake_case")
    void toUpperSnakeCase_should_handle_uppercase_input() {
        // "PROCESS NAME" -> "PROCESS NAME"
        assertEquals("PROCESS_NAME", PBStringUtils.toUpperSnakeCase("PROCESS NAME"));
    }

    /**
     * Test case for {@code toUpperSnakeCase} with single word input (should only lowercase).
     */
    @Test
    @DisplayName("toUpperSnakeCase should handle single word input by only lowercasing")
    void toUpperSnakeCase_should_handle_single_word() {
        // "Category" -> "CATEGORY"
        assertEquals("CATEGORY", PBStringUtils.toUpperSnakeCase("Category"));
    }
    
    /**
     * Test case for {@code toUpperSnakeCase} with empty string input.
     */
    @Test
    @DisplayName("toUpperSnakeCase should return empty string for empty input")
    void toUpperSnakeCase_should_return_empty_string() {
        assertEquals("", PBStringUtils.toUpperSnakeCase(""));
    }

    // -------------------------------------------------------------------------
    // resolveTemplateVariables Tests
    // -------------------------------------------------------------------------

    /**
     * Test case for {@code resolveTemplateVariables} with null template input.
     */
    @Test
    @DisplayName("resolveTemplateVariables should return null for null template")
    void resolveTemplateVariables_should_return_null_for_null_template() {
        assertNull(PBStringUtils.resolveTemplateVariables(null, (refStep, dataName) -> "value"));
    }

    /**
     * Test case for {@code resolveTemplateVariables} with empty template input.
     */
    @Test
    @DisplayName("resolveTemplateVariables should return empty string for empty template")
    void resolveTemplateVariables_should_return_empty_for_empty_template() {
        assertEquals("", PBStringUtils.resolveTemplateVariables("", (refStep, dataName) -> "value"));
    }

    /**
     * Test case for {@code resolveTemplateVariables} with null resolver function.
     */
    @Test
    @DisplayName("resolveTemplateVariables should return original template when resolver is null")
    void resolveTemplateVariables_should_return_template_when_resolver_is_null() {
        String template = "Hello {{step1:name}}";
        assertEquals(template, PBStringUtils.resolveTemplateVariables(template, null));
    }

    /**
     * Test case for {@code resolveTemplateVariables} with template containing no variables.
     */
    @Test
    @DisplayName("resolveTemplateVariables should return unchanged template when no variables present")
    void resolveTemplateVariables_should_return_unchanged_when_no_variables() {
        String template = "Hello World, this is a plain template.";
        String result = PBStringUtils.resolveTemplateVariables(template, (refStep, dataName) -> "value");
        assertEquals(template, result);
    }

    /**
     * Test case for {@code resolveTemplateVariables} with a single variable successfully resolved.
     */
    @Test
    @DisplayName("resolveTemplateVariables should resolve single variable successfully")
    void resolveTemplateVariables_should_resolve_single_variable() {
        String template = "Hello {{step1:userName}}, welcome!";
        String result = PBStringUtils.resolveTemplateVariables(template, (refStep, dataName) -> {
            if ("step1".equals(refStep) && "userName".equals(dataName)) {
                return "John";
            }
            return null;
        });
        assertEquals("Hello John, welcome!", result);
    }

    /**
     * Test case for {@code resolveTemplateVariables} with multiple variables successfully resolved.
     */
    @Test
    @DisplayName("resolveTemplateVariables should resolve multiple variables successfully")
    void resolveTemplateVariables_should_resolve_multiple_variables() {
        String template = "User: {{step1:firstName}} {{step1:lastName}}, Role: {{step2:role}}";
        String result = PBStringUtils.resolveTemplateVariables(template, (refStep, dataName) -> {
            if ("step1".equals(refStep) && "firstName".equals(dataName)) {
                return "Jane";
            }
            if ("step1".equals(refStep) && "lastName".equals(dataName)) {
                return "Doe";
            }
            if ("step2".equals(refStep) && "role".equals(dataName)) {
                return "Admin";
            }
            return null;
        });
        assertEquals("User: Jane Doe, Role: Admin", result);
    }

    /**
     * Test case for {@code resolveTemplateVariables} when resolver returns null for a variable.
     */
    @Test
    @DisplayName("resolveTemplateVariables should use default value when resolver returns null")
    void resolveTemplateVariables_should_use_default_when_resolver_returns_null() {
        String template = "Value: {{step1:unknownData}}";
        String result = PBStringUtils.resolveTemplateVariables(template, (refStep, dataName) -> null);
        assertEquals("Value: VAR_NOT_RESOLVED", result);
    }

    /**
     * Test case for {@code resolveTemplateVariables} when resolver throws an exception.
     */
    @Test
    @DisplayName("resolveTemplateVariables should use default value when resolver throws exception")
    void resolveTemplateVariables_should_handle_resolver_exception() {
        String template = "Value: {{step1:errorData}}";
        String result = PBStringUtils.resolveTemplateVariables(template, (refStep, dataName) -> {
            throw new RuntimeException("Simulated resolver failure");
        });
        assertEquals("Value: VAR_NOT_RESOLVED", result);
    }

    /**
     * Test case for {@code resolveTemplateVariables} with mixed resolved and unresolved variables.
     */
    @Test
    @DisplayName("resolveTemplateVariables should handle mixed resolved and unresolved variables")
    void resolveTemplateVariables_should_handle_mixed_variables() {
        String template = "Hello {{step1:name}}, your ID is {{step2:unknown}}";
        String result = PBStringUtils.resolveTemplateVariables(template, (refStep, dataName) -> {
            if ("step1".equals(refStep) && "name".equals(dataName)) {
                return "Alice";
            }
            return null;
        });
        assertEquals("Hello Alice, your ID is VAR_NOT_RESOLVED", result);
    }
}