package com.bonitasoft.processbuilder.enums;

import com.bonitasoft.processbuilder.exceptions.ValidationException;
import com.bonitasoft.processbuilder.validation.JsonSchemaValidator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Defines the valid option types for a process.
 * <p>
 * This enumeration is used to categorize different components within a process,
 * such as parameters, users, inputs, steps, and status. It improves code clarity
 * and reduces the risk of errors from using hard-coded strings.
 * </p>
 */
public enum ProcessOptionType {
    /**
     * Represents a process parameter.
     */
    PARAMETER,
    
    /**
     * Represents a user within a process.
     */
    USERS,
    
    /**
     * Represents the inputs of a process.
     */
    INPUTS,
    
    /**
     * Represents a step in the process workflow.
     */
    STEPS,
    
    /**
     * Represents the status of the process.
     */
    STATUS;

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessOptionType.class);


    /**
     * Checks if a given string corresponds to a valid enum constant.
     *
     * @param optionTypeInput The string to validate.
     * @return {@code true} if the string is a valid enum constant, {@code false} otherwise.
     */
    public static boolean isValid(String optionTypeInput) {
        try {
            if (optionTypeInput == null || optionTypeInput.trim().isEmpty()) {
                return false;
            }
            ProcessOptionType.valueOf(optionTypeInput.trim().toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Validates a JSON string against the specific schema associated with the provided ProcessOptionType.
     * * @param optionType The ProcessOptionType enum instance (e.g., STEPS).
     * @param jsonInput The raw JSON content to validate.
     * @return {@code true} if validation is successful and the schema check passes, {@code false} otherwise.
     */
    public static boolean isJsonValidForType(ProcessOptionType optionType, String jsonInput) 
    {
        final String SCHEMA_BASE_PATH = "/schemas/";
        
        // This check is redundant if the calling code already handles null/empty/non-valid input string,
        // but it's kept here as a defensive measure against null enum instance if called incorrectly.
        if (optionType == null || jsonInput == null || jsonInput.trim().isEmpty()) {
             LOGGER.warn("Validation skipped. OptionType is null or JSON input is empty.");
             return false;
        }
        
        // Convert enum name to lowercase to form the file path (e.g., "steps.json").
        String schemaFileName = optionType.name().toLowerCase() + ".json";
        String fullSchemaPath = SCHEMA_BASE_PATH + schemaFileName;

        LOGGER.info("Starting JSON validation for Type '{}' using schema: {}", optionType, fullSchemaPath);
        
        try {
            JsonSchemaValidator.isJsonValid(fullSchemaPath, jsonInput);
            
            LOGGER.info("Schema validation successful for Type: {}", optionType);
            return true;
            
        } catch (ValidationException e) {
            // Catches validation failures (structural errors in JSON based on schema rules)
            LOGGER.warn("Schema validation failed for Type {}. Details: {}", optionType, e.getMessage());
            // The method should return false in case of validation failure
            return false; 
        } catch (RuntimeException e) {
            // Catches critical errors (e.g., Schema file not found, critical IO/Parsing failure)
            LOGGER.error("CRITICAL application error during schema processing for Type {}.", optionType, e);
            // The method should return false in case of critical application errors
            return false;
        } 
    }
}