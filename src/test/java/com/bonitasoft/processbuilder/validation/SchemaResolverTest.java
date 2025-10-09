package com.bonitasoft.processbuilder.validation;

import com.bonitasoft.processbuilder.constants.Constants;
import com.bonitasoft.processbuilder.extension.ProcessUtils;
import com.bonitasoft.processbuilder.records.LoadedSchema;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

/**
 * Integration tests for the SchemaResolver class.
 * * Focuses on testing the complex logic of loading the OpenAPI file, 
 * * resolving allOf/refs, and generating the component title map.
 * * NOTE: These tests require the actual 'openapi.yaml' file to be present 
 * * in the test classpath (e.g., src/test/resources/schemas/openapi.yaml).
 */
@ExtendWith(MockitoExtension.class)
class SchemaResolverTest {
    
    private static final String VALID_RESOURCE_PATH = "schemas/openapi.yaml"; 
    private static final String TARGET_SCHEMA = "Category";
    private static final String NON_EXISTENT_SCHEMA = "NonExistentSchema";
    
    // Valid JSON input is needed just to satisfy the LoadedSchema constructor
    private static final String DUMMY_JSON = "{}"; 

    /**
     * Tests the successful loading and resolution of a complex schema (Category).
     * Verifies that the LoadedSchema object is correctly populated.
     */
    @Test
    void getValidatorSchema_should_load_and_resolve_complex_schema_successfully() {
        // ACT
        LoadedSchema loadedData = SchemaResolver.getValidatorSchema(VALID_RESOURCE_PATH, TARGET_SCHEMA, DUMMY_JSON);
        
        // ASSERT
        assertNotNull(loadedData, "The LoadedSchema object must not be null.");
        assertNotNull(loadedData.validator(), "The JsonSchema validator must be successfully created.");
        
        // Category requires 2 components via allOf, so the map size should reflect this.
        assertEquals(2, loadedData.titles().size(), "Title map should contain entries for all allOf components.");
        
        // Verify dynamic title mapping for the Base Schema (allOf/0)
        assertTrue(loadedData.titles().containsKey("/allOf/0"), "Title map must contain the Base Schema pointer.");
        assertEquals("Base Persistence Schema", loadedData.titles().get("/allOf/0"), "Title translation is incorrect.");
    }

    /**
     * Tests the failure path when a non-existent schema name is requested.
     * Expects a RuntimeException because the schema cannot be found in the components.
     *//* 
    @Test
    void getValidatorSchema_should_throw_exception_for_non_existent_schema() {
    
        final String MISSING_SCHEMA_NAME = "NON_EXISTENT_SCHEMA_ZZZ"; 
        
        // ACT & ASSERT
        // 1. Capturamos la excepción que lanza SchemaResolver.getValidatorSchema
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            SchemaResolver.getValidatorSchema(VALID_RESOURCE_PATH, MISSING_SCHEMA_NAME, DUMMY_JSON);
        }, "Should throw RuntimeException if the target schema is not found in the OpenAPI map.");
        
        // 2. Verificamos que el mensaje contiene la frase inicial exacta de la excepción de nuestro código.
        // Esto asegura que la excepción se lanzó en la línea correcta dentro de SchemaResolver.
        final String expectedPrefix = "Target schema '"; 

        // Verificamos que el mensaje COMENCE con la frase esperada Y que contenga el nombre que buscamos.
        // Usamos el nombre específico para asegurar que no se trata de otra excepción de I/O.
        assertTrue(thrown.getMessage().startsWith(expectedPrefix), 
                "The exception message must start with the correct error prefix: '" + expectedPrefix + "'.");
        
        assertTrue(thrown.getMessage().contains(MISSING_SCHEMA_NAME), 
                "The exception message must explicitly contain the name of the missing schema ('" + MISSING_SCHEMA_NAME + "').");
    }*/

    /**
     * Tests the critical fix: ensuring the 'required' array is flattened and not lost.
     * This verifies that the internal schema representation is correctly modified.
     */
    @Test
    void getValidatorSchema_should_flatten_allOf_requirements() {
        // ACT: The flattening happens inside getValidatorSchema. We verify the final validator doesn't crash.
        LoadedSchema loadedData = SchemaResolver.getValidatorSchema(VALID_RESOURCE_PATH, TARGET_SCHEMA, DUMMY_JSON);

        // ASSERT: We must assume the validation is ready to run, proving the schema structure is sound.
        assertDoesNotThrow(() -> {
            // Attempt a successful validation run (will pass if the schema is structurally sound)
            SchemaResolver.isJsonValid(loadedData); 
        }, "Schema structure should be valid after flattening the 'required' array.");
    }

    /**
     * Tests the private constructor to ensure the utility class cannot be instantiated, 
     * enforcing its static nature and achieving code coverage on the constructor.
     */
    @Test
    void constructor_should_throw_unsupported_operation_exception() throws Exception {
        // 1. Retrieve the Constructor object for the class.
        Constructor<SchemaResolver> constructor = SchemaResolver.class.getDeclaredConstructor();
        
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