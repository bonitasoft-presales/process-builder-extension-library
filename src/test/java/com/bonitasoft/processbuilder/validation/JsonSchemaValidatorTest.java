package com.bonitasoft.processbuilder.validation;

import com.bonitasoft.processbuilder.constants.SchemaConstants;
import com.bonitasoft.processbuilder.enums.ActionType;
import com.bonitasoft.processbuilder.extension.PBStringUtils;
import com.bonitasoft.processbuilder.records.LoadedSchema;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.MockedStatic;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

/**
 * Unit tests for the {@link JsonSchemaValidator} utility class.
 * This class uses Mockito's static mocking capabilities to isolate dependencies 
 * on {@code PBStringUtils} and {@code SchemaResolver}, ensuring full coverage 
 * of the validation method's logic and exception handling. The test uses 
 * dynamic mocks for the {@code LoadedSchema} record.
 */
class JsonSchemaValidatorTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    
    // MockedStatic variables for static dependencies
    private MockedStatic<SchemaResolver> mockedSchemaResolver;
    private MockedStatic<PBStringUtils> mockedStringUtils;

    // A dynamically created mock of the LoadedSchema record will be used instead of an inner class.
    private LoadedSchema mockedLoadedSchema;


    /**
     * Setup static mocks before each test.
     */
    @BeforeEach
    void setUp() {
        // Initialize static mocks
        mockedSchemaResolver = mockStatic(SchemaResolver.class);
        mockedStringUtils = mockStatic(PBStringUtils.class);
        
        // Create the dynamic mock instance for the LoadedSchema record
        mockedLoadedSchema = mock(LoadedSchema.class);

        // Stub PBStringUtils to return a predictable Title Case version
        mockedStringUtils.when(() -> PBStringUtils.normalizeTitleCase(anyString()))
                         .thenAnswer(invocation -> invocation.getArgument(0).toString().toUpperCase());

        // Stub SchemaResolver to return the mock LoadedSchema instance by default (Happy Path)
        mockedSchemaResolver.when(() -> SchemaResolver.getValidatorSchema(anyString(), anyString(), anyString()))
                            .thenReturn(mockedLoadedSchema);
        // Stub the validation method to return success by default
        mockedSchemaResolver.when(() -> SchemaResolver.isJsonValid(eq(mockedLoadedSchema)))
                            .thenReturn(true);
    }

    /**
     * Close static mocks after each test.
     */
    @AfterEach
    void tearDown() {
        mockedSchemaResolver.close();
        mockedStringUtils.close();
    }

    // -------------------------------------------------------------------------
    // Constructor Test (Non-instantiable)
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Constructor should throw UnsupportedOperationException")
    void constructor_should_throw_unsupported_operation_exception() throws Exception {
        // 1. Retrieve the Constructor object for the class.
        Constructor<JsonSchemaValidator> constructor = JsonSchemaValidator.class.getDeclaredConstructor();
        
        // 2. VERIFICATION: Ensure the constructor is PRIVATE.
        assertTrue(Modifier.isPrivate(constructor.getModifiers()));
        
        // 3. FORCE ACCESSIBILITY & Invoke the constructor 
        constructor.setAccessible(true);
        InvocationTargetException thrownException = assertThrows(InvocationTargetException.class, constructor::newInstance);
        
        // 4. Verify the actual cause is the expected exception.
        assertTrue(thrownException.getCause() instanceof UnsupportedOperationException);
    }

    // -------------------------------------------------------------------------
    // Happy Path and Basic Validation Tests
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should return true for valid string input (String instanceof path)")
    void isJsonValidForType_should_return_true_for_valid_string_input() {
        final String actionType = "INSERT";
        final String optionType = "CATEGORY";
        final String validJsonString = "{\"name\": \"Test\"}";

        boolean result = JsonSchemaValidator.isJsonValidForType(actionType, optionType, validJsonString);
        
        assertTrue(result, "Expected validation to succeed for valid String input.");
        // Verify SchemaResolver was called
        mockedSchemaResolver.verify(() -> SchemaResolver.getValidatorSchema(anyString(), anyString(), eq(validJsonString)), times(1));
    }

    @Test
    @DisplayName("Should return true for valid Object input (Object serialization path)")
    void isJsonValidForType_should_return_true_for_valid_object_input() {
        final String actionType = "UPDATE";
        final String optionType = "CATEGORY";
        final Object validJsonObject = MAPPER.createObjectNode().put("name", "Test");
        
        boolean result = JsonSchemaValidator.isJsonValidForType(actionType, optionType, validJsonObject);
        
        assertTrue(result, "Expected validation to succeed for valid Object input.");
        // Verify SchemaResolver was called with a JSON string
        mockedSchemaResolver.verify(() -> SchemaResolver.getValidatorSchema(anyString(), anyString(), anyString()), times(1));
    }
    
    // -------------------------------------------------------------------------
    // Schema Name Determination Tests (ActionType and Normalization)
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should use DELETE_BASE_SCHEMA for DELETE action")
    void isJsonValidForType_should_use_delete_base_schema_on_delete_action() {
        final String actionType = ActionType.DELETE.name(); // "DELETE"
        final String optionType = "CATEGORY";
        final Object validJsonObject = MAPPER.createObjectNode().put("id", 1);
        
        JsonSchemaValidator.isJsonValidForType(actionType, optionType, validJsonObject);

        // Verify that the target schema name is the constant DELETE_BASE_SCHEMA
        mockedSchemaResolver.verify(() -> SchemaResolver.getValidatorSchema(
                anyString(), 
                eq(SchemaConstants.DELETE_BASE_SCHEMA), 
                anyString()), 
            times(1));
        
        // Ensure PBStringUtils was NOT called when action is DELETE
        mockedStringUtils.verify(() -> PBStringUtils.normalizeTitleCase(anyString()), never());
    }

    @Test
    @DisplayName("Should use Title Case name for INSERT/UPDATE action")
    void isJsonValidForType_should_use_title_case_name_on_non_delete_action() {
        final String actionType = ActionType.INSERT.name(); // "INSERT"
        final String optionType = "category";
        final Object validJsonObject = MAPPER.createObjectNode().put("name", "Test");
        
        JsonSchemaValidator.isJsonValidForType(actionType, optionType, validJsonObject);

        // Verify that PBStringUtils was called
        mockedStringUtils.verify(() -> PBStringUtils.normalizeTitleCase(eq(optionType)), times(1));

        // Verify that the result of PBStringUtils (which is stubbed to be uppercase) was used as the target name
        mockedSchemaResolver.verify(() -> SchemaResolver.getValidatorSchema(
                anyString(), 
                eq(optionType.toUpperCase()), 
                anyString()), 
            times(1));
    }

    // -------------------------------------------------------------------------
    // Input Safety Checks and Serialization Failure Tests
    // -------------------------------------------------------------------------
    
    @Test
    @DisplayName("Should return false for null optionType (Input Safety)")
    void isJsonValidForType_should_return_false_for_null_option_type() {
        assertFalse(JsonSchemaValidator.isJsonValidForType("UPDATE", null, MAPPER.createObjectNode()));
    }

    @Test
    @DisplayName("Should return false for null jsonInput (Input Safety)")
    void isJsonValidForType_should_return_false_for_null_json_input() {
        assertFalse(JsonSchemaValidator.isJsonValidForType("UPDATE", "CATEGORY", null));
    }
    
    @Test
    @DisplayName("Should return false for empty/blank JSON string after serialization")
    void isJsonValidForType_should_return_false_for_empty_json_string() {
        // Test 1: Empty String input
        assertFalse(JsonSchemaValidator.isJsonValidForType("INSERT", "CATEGORY", ""));
        
        // Test 2: Blank String input
        assertFalse(JsonSchemaValidator.isJsonValidForType("INSERT", "CATEGORY", " "));
    }

    @Test
    @DisplayName("Should return false on serialization exception (Exception catch branch)")
    void isJsonValidForType_should_return_false_on_serialization_exception() {
        // Given an object that cannot be serialized (e.g., a class with a circular reference)
        class NonSerializableObject {
            NonSerializableObject self = this;
        }
        
        assertFalse(JsonSchemaValidator.isJsonValidForType("INSERT", "CATEGORY", new NonSerializableObject()));
    }

    // -------------------------------------------------------------------------
    // Schema Resolution and Validation Failure Tests
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should return false when SchemaResolver.isJsonValid returns false")
    void isJsonValidForType_should_return_false_on_validation_failure() {
        // Stub SchemaResolver.isJsonValid to return FALSE for validation result
        mockedSchemaResolver.when(() -> SchemaResolver.isJsonValid(any(LoadedSchema.class)))
                            .thenReturn(false);

        final String validJsonString = "{\"name\": \"Test\"}";
        boolean result = JsonSchemaValidator.isJsonValidForType("UPDATE", "CATEGORY", validJsonString);
        
        assertFalse(result, "Expected false because the mocked validation result is false.");
    }
    
    @Test
    @DisplayName("Should return false on RuntimeException during schema loading (SchemaResolver failure)")
    void isJsonValidForType_should_return_false_on_schema_resolution_runtime_exception() {
        // Stub SchemaResolver.getValidatorSchema to throw a RuntimeException (e.g., file not found)
        mockedSchemaResolver.when(() -> SchemaResolver.getValidatorSchema(anyString(), anyString(), anyString()))
                            .thenThrow(new RuntimeException("Schema file not found"));

        final String validJsonString = "{\"name\": \"Test\"}";
        boolean result = JsonSchemaValidator.isJsonValidForType("UPDATE", "CATEGORY", validJsonString);
        
        assertFalse(result, "Expected false because the mocked schema loading failed with a RuntimeException.");
    }
}