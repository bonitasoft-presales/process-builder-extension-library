package com.bonitasoft.processbuilder.validation;

import com.bonitasoft.processbuilder.constants.SchemaConstants;
import com.bonitasoft.processbuilder.extension.PBStringUtils;
import com.bonitasoft.processbuilder.records.LoadedSchema;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class serving as the entry point for JSON Schema validation.
 * It prepares the input and delegates the schema loading and validation to SchemaResolver.
 * * This class uses SLF4J for logging all validation results and errors.
 */
public class JsonSchemaValidator {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonSchemaValidator.class);
    
    // ObjectMapper is configured statically for thread safety.
    private static final ObjectMapper MAPPER = new ObjectMapper();
   
    /**
     * Private constructor to prevent instantiation of this utility class.
     * All methods in this class are static and should be called directly on the class itself.
     */
    private JsonSchemaValidator() {
        throw new UnsupportedOperationException("This is a "+this.getClass().getSimpleName()+" class and cannot be instantiated.");
    }


    /**
     * Validates an input object against the schema corresponding to the given option type name.
     * This method handles input serialization and delegates the core validation logic.
     * * @param optionType The name of the schema to validate against (e.g., "Category", "Steps").
     * @param jsonInput The JSON content to validate (can be String, Map, or any serializable object).
     * @return {@code true} if validation is successful, {@code false} otherwise.
     */
    public static boolean isJsonValidForType(String optionType, Object jsonInput) 
    {
        // 1. Basic Input Safety Checks
        if (optionType == null || jsonInput == null ) {
             LOGGER.warn("INPUT_ERROR: Validation skipped. OptionType or JSON input object is null.");
             return false;
        }

        String targetSchemaName = PBStringUtils.normalizeTitleCase(optionType);
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
            // EXCEPTION MANAGEMENT: Error during input object -> string conversion
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