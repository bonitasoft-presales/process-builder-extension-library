package com.bonitasoft.processbuilder.constants;

/**
 * Utility class containing constant values related to OpenAPI/JSON Schema configuration 
 * and resource paths within the application.
 * <p>
 * These constants centralize configuration data used for schema resolution and validation logic.
 * This class is non-instantiable.
 * </p>
 *
 * @author Bonitasoft
 * @since 1.0
 */
public final class SchemaConstants {
    
    /**
     * Private constructor to prevent instantiation of this utility class.
     * @throws UnsupportedOperationException always, to enforce the utility pattern.
     */
    private SchemaConstants() {
        throw new UnsupportedOperationException("This is a " + this.getClass().getSimpleName() + " class and cannot be instantiated.");
    }

    // -------------------------------------------------------------------------
    // SCHEMA PATHS AND REFERENCES
    // -------------------------------------------------------------------------

    /**
     * The path to the main resource file containing all OpenAPI/JSON Schema definitions.
     * <p>This file is the single source of truth for all schema component definitions.</p>
     */
    public static final String OPENAPI_RESOURCE_PATH = "schemas/openapi.yaml";

    /**
     * The prefix used for internal references to components within the schema file.
     * <p>Typically used for resolving $ref pointers within the OpenAPI document, pointing 
     * to components located in the {@code #/components/schemas/} section.</p>
     */
    public static final String SCHEMA_COMPONENTS_PREFIX = "#/components/schemas/";

    // -------------------------------------------------------------------------
    // BUSINESS LOGIC SCHEMAS
    // -------------------------------------------------------------------------

    /**
     * The name of the base schema used for DELETE operations.
     * <p>This schema is typically highly restrictive, often requiring only an ID 
     * field for basic object input validation before deletion.</p>
     */
    public static final String DELETE_BASE_SCHEMA = "ObjectInputBaseSchema";
}