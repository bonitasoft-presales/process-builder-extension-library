package com.bonitasoft.processbuilder.validation;

import com.bonitasoft.processbuilder.constants.SchemaConstants;
import com.bonitasoft.processbuilder.enums.ActionType;
import com.bonitasoft.processbuilder.extension.PBStringUtils;
import com.bonitasoft.processbuilder.records.LoadedSchema;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class serving as the entry point for JSON Schema validation.
 * It prepares the input, determines the target schema, and delegates the 
 * schema loading and validation to {@link SchemaResolver}.
 * <p>
 * This class uses SLF4J for logging all validation results and errors and is non-instantiable.
 * </p>
 * @author [Your Name or Company Name]
 * @since 1.0
 */
public final class JsonSchemaValidator {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonSchemaValidator.class);
    
    /**
     * The Jackson {@link ObjectMapper} is configured statically for thread safety 
     * and used for converting input objects to their JSON string representation.
     */
    private static final ObjectMapper MAPPER = new ObjectMapper();
   
    /**
     * Private constructor to prevent instantiation of this utility class.
     * All methods in this class are static and should be called directly on the class itself.
     * @throws UnsupportedOperationException always, to enforce the utility pattern.
     */
    private JsonSchemaValidator() {
        throw new UnsupportedOperationException("This is a "+this.getClass().getSimpleName()+" class and cannot be instantiated.");
    }


    /**
     * Validates an input object against the schema corresponding to the given option type name.
     * This method handles input serialization, determines the final schema name (based on action type), 
     * and delegates the core validation logic.
     * * @param actionType The action being performed (e.g., "DELETE", "INSERT", "UPDATE").
     * @param optionType The name of the schema to validate against (e.g., "Category", "Steps").
     * @param jsonInput The JSON content to validate (can be String, Map, or any serializable object).
     * @return {@code true} if validation is successful, {@code false} otherwise (due to null inputs, 
     * serialization failure, or schema validation/loading errors).
     */
    public static boolean isJsonValidForType(String actionType, String optionType, Object jsonInput) 
    {


        // 1. Basic Input Safety Checks
        if (optionType == null || jsonInput == null ) {
             LOGGER.warn("INPUT_ERROR: Validation skipped. OptionType or JSON input object is null.");
             return false;
        }

        // Determine target schema name: normalize optionType, or use base schema for DELETE
        String targetSchemaName = "";
        if (ActionType.DELETE.name().equalsIgnoreCase(actionType)) {
             targetSchemaName = SchemaConstants.DELETE_BASE_SCHEMA;
        } else {
             targetSchemaName = PBStringUtils.normalizeTitleCase(optionType);
        }
        
        String jsonStringForValidation;
        
        try {
            // 2. Input Serialization (Handle String vs. Object input)
            if (jsonInput instanceof String) {
                jsonStringForValidation = (String) jsonInput;
            } else {
                jsonStringForValidation = MAPPER.writeValueAsString(jsonInput);
            }
            
            if (jsonStringForValidation.trim().isEmpty()) {
                LOGGER.warn("INPUT_WARNING: JSON input is empty after serialization for type: {}", targetSchemaName);
                return false;
            }

        } catch (Exception e) {
            // EXCEPTION MANAGEMENT: Error during input object -> string conversion (e.g., circular reference)
            LOGGER.error("VALIDATION_ERROR: Failed to serialize input object to JSON string for type {}.", targetSchemaName, e);
            return false;
        }

        LOGGER.info("INFO: Starting validation for schema: {}", targetSchemaName);
        
        // 3. Load Schema and Validate
        try {
            // Delegate schema loading, which includes OpenAPI parsing and full resolution
            LoadedSchema loadedData = SchemaResolver.getValidatorSchema(
                SchemaConstants.OPENAPI_RESOURCE_PATH, 
                targetSchemaName, 
                jsonStringForValidation
            );

            LOGGER.info("INFO: Schema '{}' successfully loaded and merged.", targetSchemaName);
            
            // Delegate the final validation step
            return SchemaResolver.isJsonValid(loadedData);
            
        } catch (RuntimeException e){
            // EXCEPTION MANAGEMENT: Catches all RuntimeExceptions thrown by SchemaResolver 
            // (e.g., File not found, Parsing error, Schema definition failure)
            LOGGER.error("VALIDATION_ERROR: Failed during schema resolution or loading for type {}.", targetSchemaName, e);
            return false;
        }
    }
}