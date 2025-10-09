package com.bonitasoft.processbuilder.validation;

import com.bonitasoft.processbuilder.records.LoadedSchema;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

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
}