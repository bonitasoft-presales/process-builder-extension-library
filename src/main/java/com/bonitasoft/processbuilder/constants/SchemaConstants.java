package com.bonitasoft.processbuilder.constants;

public class SchemaConstants {
    
    private SchemaConstants() {
        throw new UnsupportedOperationException("This is a "+this.getClass().getSimpleName()+" class and cannot be instantiated.");
    }

    /** Path al archivo principal que contiene todas las definiciones OpenAPI/JSON Schemas. */
    public static final String OPENAPI_RESOURCE_PATH = "schemas/openapi.yaml";

    /** Prefijo usado en las referencias internas de los componentes del esquema. */
    public static final String SCHEMA_COMPONENTS_PREFIX = "#/components/schemas/";

    /**
     * The name of the base schema used for DELETE operations, which typically only requires an ID.
     */
    public static final String DELETE_BASE_SCHEMA = "ObjectInputBaseSchema";
}
