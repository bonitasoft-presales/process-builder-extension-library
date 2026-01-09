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

    // -------------------------------------------------------------------------
    // resolveTemplateVariables Tests - NEW FORMAT (without refStep prefix)
    // -------------------------------------------------------------------------

    /**
     * Test case for {@code resolveTemplateVariables} with simple variable without prefix.
     * Format: {{dataName}} - refStep will be null
     */
    @Test
    @DisplayName("resolveTemplateVariables should resolve simple variable without prefix")
    void resolveTemplateVariables_should_resolve_simple_variable_without_prefix() {
        String template = "Click here: {{task_link}}";
        String result = PBStringUtils.resolveTemplateVariables(template, (refStep, dataName) -> {
            assertNull(refStep, "refStep should be null for simple variable");
            assertEquals("task_link", dataName);
            return "https://example.com/task/123";
        });
        assertEquals("Click here: https://example.com/task/123", result);
    }

    /**
     * Test case for {@code resolveTemplateVariables} with multiple simple variables.
     */
    @Test
    @DisplayName("resolveTemplateVariables should resolve multiple simple variables without prefix")
    void resolveTemplateVariables_should_resolve_multiple_simple_variables() {
        String template = "Hello {{recipient_firstname}} {{recipient_lastname}}!";
        String result = PBStringUtils.resolveTemplateVariables(template, (refStep, dataName) -> {
            assertNull(refStep, "refStep should be null for simple variables");
            if ("recipient_firstname".equals(dataName)) {
                return "John";
            }
            if ("recipient_lastname".equals(dataName)) {
                return "Doe";
            }
            return null;
        });
        assertEquals("Hello John Doe!", result);
    }

    /**
     * Test case for {@code resolveTemplateVariables} with mixed format (with and without prefix).
     */
    @Test
    @DisplayName("resolveTemplateVariables should handle mixed format variables")
    void resolveTemplateVariables_should_handle_mixed_format_variables() {
        String template = "User {{step_123:step_user_name}} - Email: {{recipient_email}} - Link: {{task_link}}";
        String result = PBStringUtils.resolveTemplateVariables(template, (refStep, dataName) -> {
            if ("step_123".equals(refStep) && "step_user_name".equals(dataName)) {
                return "Alice";
            }
            if (refStep == null && "recipient_email".equals(dataName)) {
                return "alice@example.com";
            }
            if (refStep == null && "task_link".equals(dataName)) {
                return "https://example.com/task";
            }
            return null;
        });
        assertEquals("User Alice - Email: alice@example.com - Link: https://example.com/task", result);
    }

    /**
     * Test case for {@code resolveTemplateVariables} with variable containing numbers and underscores.
     */
    @Test
    @DisplayName("resolveTemplateVariables should handle variables with numbers and underscores")
    void resolveTemplateVariables_should_handle_complex_variable_names() {
        String template = "Step: {{step_456:data_field_1}}, Status: {{step_status}}";
        String result = PBStringUtils.resolveTemplateVariables(template, (refStep, dataName) -> {
            if ("step_456".equals(refStep) && "data_field_1".equals(dataName)) {
                return "Approved";
            }
            if (refStep == null && "step_status".equals(dataName)) {
                return "Completed";
            }
            return null;
        });
        assertEquals("Step: Approved, Status: Completed", result);
    }

    /**
     * Test case for {@code resolveTemplateVariables} when simple variable resolver returns null.
     */
    @Test
    @DisplayName("resolveTemplateVariables should use default when simple variable returns null")
    void resolveTemplateVariables_should_use_default_for_simple_variable_null() {
        String template = "Link: {{unknown_variable}}";
        String result = PBStringUtils.resolveTemplateVariables(template, (refStep, dataName) -> null);
        assertEquals("Link: VAR_NOT_RESOLVED", result);
    }

    /**
     * Test case for all DataResolverType values as simple variables.
     */
    @Test
    @DisplayName("resolveTemplateVariables should resolve all DataResolverType simple variables")
    void resolveTemplateVariables_should_resolve_all_data_resolver_types() {
        String template = "{{recipient_firstname}} {{recipient_lastname}} ({{recipient_email}}) - Task: {{task_link}} - By: {{step_user_name}} - Status: {{step_status}}";
        String result = PBStringUtils.resolveTemplateVariables(template, (refStep, dataName) -> {
            assertNull(refStep, "All should be simple variables without prefix");
            return switch (dataName) {
                case "recipient_firstname" -> "Jane";
                case "recipient_lastname" -> "Smith";
                case "recipient_email" -> "jane@test.com";
                case "task_link" -> "http://task/1";
                case "step_user_name" -> "Admin";
                case "step_status" -> "Done";
                default -> null;
            };
        });
        assertEquals("Jane Smith (jane@test.com) - Task: http://task/1 - By: Admin - Status: Done", result);
    }

    /**
     * Test case for variable with special characters in replacement value.
     */
    @Test
    @DisplayName("resolveTemplateVariables should handle special characters in replacement")
    void resolveTemplateVariables_should_handle_special_chars_in_replacement() {
        String template = "Link: {{task_link}}";
        String result = PBStringUtils.resolveTemplateVariables(template, (refStep, dataName) ->
            "https://example.com/task?id=123&type=email"
        );
        assertEquals("Link: https://example.com/task?id=123&type=email", result);
    }

    /**
     * Test case for variable with dollar sign in replacement (regex special char).
     */
    @Test
    @DisplayName("resolveTemplateVariables should handle dollar sign in replacement")
    void resolveTemplateVariables_should_handle_dollar_sign_in_replacement() {
        String template = "Price: {{price}}";
        String result = PBStringUtils.resolveTemplateVariables(template, (refStep, dataName) -> "$100.00");
        assertEquals("Price: $100.00", result);
    }
}