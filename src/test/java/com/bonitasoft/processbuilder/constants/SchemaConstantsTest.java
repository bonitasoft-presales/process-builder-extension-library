package com.bonitasoft.processbuilder.constants;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link SchemaConstants} utility class.
 * <p>
 * This class ensures that the utility class cannot be instantiated and
 * that all public static constants hold their expected values.
 * </p>
 */
class SchemaConstantsTest {

    /**
     * Test case to ensure the private constructor throws an
     * {@link UnsupportedOperationException} when accessed via reflection,
     * confirming that the utility class cannot be instantiated.
     * @throws NoSuchMethodException if the constructor cannot be found (should not happen).
     */
    @Test
    @DisplayName("Should throw UnsupportedOperationException when instantiated")
    void shouldThrowExceptionOnInstantiation() throws NoSuchMethodException {
        // 1. Get the private constructor using reflection
        Constructor<SchemaConstants> constructor = SchemaConstants.class.getDeclaredConstructor();
        // 2. Make the constructor accessible (it's private)
        constructor.setAccessible(true);

        // 3. Assert that calling the constructor throws InvocationTargetException,
        // which wraps the actual UnsupportedOperationException
        InvocationTargetException thrown = assertThrows(InvocationTargetException.class, constructor::newInstance,
                "The constructor call must throw an InvocationTargetException.");

        // 4. Check the cause of the exception
        assertEquals(UnsupportedOperationException.class, thrown.getCause().getClass(),
                "The exception cause should be UnsupportedOperationException to enforce non-instantiation.");
    }

    /**
     * Test case to verify the value of the {@code OPENAPI_RESOURCE_PATH} constant.
     */
    @Test
    @DisplayName("Should verify the value of the OPENAPI_RESOURCE_PATH constant")
    void shouldVerifyOpenApiResourcePath() {
        assertEquals("schemas/openapi.yaml", SchemaConstants.OPENAPI_RESOURCE_PATH, 
                "The OPENAPI_RESOURCE_PATH constant should hold the correct path to the schema file.");
    }

    /**
     * Test case to verify the value of the {@code SCHEMA_COMPONENTS_PREFIX} constant.
     */
    @Test
    @DisplayName("Should verify the value of the SCHEMA_COMPONENTS_PREFIX constant")
    void shouldVerifySchemaComponentsPrefix() {
        assertEquals("#/components/schemas/", SchemaConstants.SCHEMA_COMPONENTS_PREFIX, 
                "The SCHEMA_COMPONENTS_PREFIX constant should hold the correct internal reference prefix.");
    }

    /**
     * Test case to verify the value of the {@code DELETE_BASE_SCHEMA} constant.
     */
    @Test
    @DisplayName("Should verify the value of the DELETE_BASE_SCHEMA constant")
    void shouldVerifyDeleteBaseSchema() {
        assertEquals("ObjectInputBaseSchema", SchemaConstants.DELETE_BASE_SCHEMA, 
                "The DELETE_BASE_SCHEMA constant should hold the correct base schema name for delete operations.");
    }
}