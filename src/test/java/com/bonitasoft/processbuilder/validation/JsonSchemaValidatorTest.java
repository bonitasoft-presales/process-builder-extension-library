package com.bonitasoft.processbuilder.validation;

import com.bonitasoft.processbuilder.constants.Constants;
import com.bonitasoft.processbuilder.extension.ProcessUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

@ExtendWith(MockitoExtension.class)
class JsonSchemaValidatorTest {

    private static final ObjectMapper mapper = new ObjectMapper();   
    

    
    /**
     * Tests the successful validation using isJsonValidForType with a valid optionType and JSON object.
     * This ensures the method serializes the Object to JSON and delegates correctly.
     * Assumes a schema at "/schemas/category.json" (lowercase optionType + ".json").
     * Example valid Object: Map with required fields for CATEGORY schema.
     */
    @Test
    void isJsonValidForType_should_return_true_for_valid_type_and_json_object() {
        final String actionType = "UPDATE";
        // Given a valid optionType and valid JSON object (e.g., for CATEGORY)
        final String optionType = "CATEGORY";
        final Object validJsonObject = mapper.createObjectNode()  // Use ObjectNode for easy building
            .put("persistenceId_string", "1")
            .put("fullName", "Test Category")
            .put("fullDescription", "Valid description")
            .put("enabled", true);

        // When validating
        boolean result = JsonSchemaValidator.isJsonValidForType(actionType, optionType, validJsonObject);
        System.out.println("Validation result for CATEGORY: " + result);  // Debug print
        // Then it should return true (assuming schema validates it)
        assertTrue(result, "Expected true, but got false. Check schema file and references.");
    }


    /**
     * Tests that isJsonValidForType returns false for invalid JSON object against the schema.
     * This verifies that validation failures lead to false return without crashing.
     * Example invalid Object: Missing required fields for CATEGORY.
     */
    @Test
    void isJsonValidForType_should_return_false_for_invalid_json_object() {
        final String actionType = "UPDATE";
        // Given a valid optionType and invalid JSON object (missing required fields)
        final String optionType = "CATEGORY";
        final Object invalidJsonObject = mapper.createObjectNode()
            .put("persistenceId_string", "cat-001");  // Missing fullName, fullDescription, enabled

        // When validating
        boolean result = JsonSchemaValidator.isJsonValidForType(actionType, optionType, invalidJsonObject);

        // Then it should return false due to validation failure
        assertFalse(result);
    }

    /**
     * Tests that isJsonValidForType returns false when optionType or jsonInput is null.
     * This covers defensive checks for null inputs.
     */
    @Test
    void isJsonValidForType_should_return_false_for_null_inputs() {
        // Given null optionType and valid JSON object
        final String actionType = "INSERT";
        final String nullOptionType = null;
        final Object validJsonObject = mapper.createObjectNode().put("name", "test");

        boolean result1 = JsonSchemaValidator.isJsonValidForType(actionType, nullOptionType, validJsonObject);
        assertFalse(result1);

        // Given valid optionType and null JSON object
        boolean result2 = JsonSchemaValidator.isJsonValidForType(actionType, "CATEGORY", null);
        assertFalse(result2);

        // Given both null
        boolean result3 = JsonSchemaValidator.isJsonValidForType(actionType, null, null);
        assertFalse(result3);
    }

    /**
     * Tests that isJsonValidForType returns false for empty JSON object after serialization.
     * This ensures empty inputs are handled gracefully.
     */
    @Test
    void isJsonValidForType_should_return_false_for_empty_json_object() {
        final String actionType = "INSERT";
        // Given a valid optionType and empty JSON object
        final String optionType = "CATEGORY";
        final Object emptyJsonObject = mapper.createObjectNode();  // Empty node: {}

        // When validating
        boolean result = JsonSchemaValidator.isJsonValidForType(actionType, optionType, emptyJsonObject);

        // Then it should return false (empty after serialization)
        assertFalse(result, "Should fail because an empty object often fails required property checks.");

    }

    /**
     * Tests that isJsonValidForType returns false when schema loading fails (e.g., non-existent schema).
     * This verifies critical errors lead to false return.
     * Example: Use an optionType without corresponding schema file.
     */
    @Test
    void isJsonValidForType_should_return_false_for_missing_schema_file() {
        final String actionType = "INSERT";
        // Given an optionType with no corresponding schema (e.g., "INVALIDTYPE.json" won't exist)
        final String optionType = "INVALIDTYPE";
        final Object jsonObject = mapper.createObjectNode().put("name", "test");

        // When validating
        boolean result = JsonSchemaValidator.isJsonValidForType(actionType, optionType, jsonObject);

        // Then it should return false due to critical error (schema not found)
        assertFalse(result);
        assertFalse(result, "Expected false because the schema resolver throws an exception.");
    }

    /**
     * Tests the private constructor to ensure the utility class cannot be instantiated, 
     * enforcing its static nature and achieving code coverage on the constructor.
     */
    @Test
    void constructor_should_throw_unsupported_operation_exception() throws Exception {
        // 1. Retrieve the Constructor object for the class.
        Constructor<JsonSchemaValidator> constructor = JsonSchemaValidator.class.getDeclaredConstructor();
        
        // 2. VERIFICATION: Use getModifiers() to ensure the constructor is PRIVATE.
        // This confirms we are testing the correct, restricted constructor.
        assertTrue(Modifier.isPrivate(constructor.getModifiers()), 
                "The constructor must be declared as private to prevent instantiation.");
        
        // 3. FORCE ACCESSIBILITY: Override the 'private' restriction for testing purposes.
        // This is necessary for the newInstance() method to be invokable.
        constructor.setAccessible(true);
        
        // 4. Invoke the constructor and expect the wrapper exception (InvocationTargetException).
        InvocationTargetException thrownException = assertThrows(InvocationTargetException.class, () -> {
            // The call must be 'newInstance()', which is the reflection invocation method.
            constructor.newInstance();
        }, "Invoking the private constructor should wrap the internal exception in InvocationTargetException.");
        
        // 5. Verify the actual cause is the expected exception (UnsupportedOperationException).
        Throwable actualCause = thrownException.getCause();
        assertTrue(actualCause instanceof UnsupportedOperationException, 
                "The internal exception (cause) must be UnsupportedOperationException.");
                
        final String expectedMessage = "This is a "+this.getClass().getSimpleName().replace(Constants.TEST, "")+" class and cannot be instantiated.";
        assertEquals(expectedMessage, actualCause.getMessage(),
                    "The constructor's message should match the expected text.");
        
        // Optional: Revert the accessibility change after the test
        constructor.setAccessible(false);
    }
}