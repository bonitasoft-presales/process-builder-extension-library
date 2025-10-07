package com.bonitasoft.processbuilder.validation;

import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import com.bonitasoft.processbuilder.exceptions.ValidationException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Set;

/**
 * Class to handle generic JSON Schema loading and validation using the Networknt library.
 * This class provides a static method for validation, allowing use without explicit instantiation.
 */
public class JsonSchemaValidator {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonSchemaValidator.class);
    
    // ObjectMapper is made private and final for thread safety, but the static method 
    // must instantiate its own or receive one. We will instantiate it locally.
    private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * Loads a JSON schema from the resources folder and validates the input JSON string against it.
     * * @param schemaPath The full path to the JSON Schema file within the classpath (e.g., "/schemas/steps.json").
     * @param jsonInput The raw JSON string to validate.
     * @return {@code true} if validation is successful, {@code false} if the JSON content fails validation.
     * @throws RuntimeException if a critical application error occurs (e.g., schema file not found or IO error).
     * @throws ValidationException if the JSON content fails validation (details are encapsulated here).
     */
    public static boolean isJsonValid(String schemaPath, String jsonInput) throws ValidationException {
        
        // final ObjectMapper localMapper = new ObjectMapper(); 
        
        // 1. Load the Schema (Networknt API) - Handled as a critical application task.
        JsonSchema schema;
        try (InputStream is = JsonSchemaValidator.class.getResourceAsStream(schemaPath)) { // Use Class.getResourceAsStream()
            if (is == null) {
                String errorMsg = String.format("JSON Schema file not found at path: %s", schemaPath);
                LOGGER.error(errorMsg);
                throw new RuntimeException(errorMsg);
            }
            
            JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V202012);
            schema = factory.getSchema(is);
            
        } catch (RuntimeException e) {
            // Rethrow critical application errors (e.g., IO, schema loading failure)
            throw e; 
        } catch (Exception e) {
            LOGGER.error("Failed to read or load schema file: {}", schemaPath, e);
            throw new RuntimeException("Failed to read or load schema file.", e);
        }

        // 2. Validate the Input (Handling Business Validation Errors)
        try {
            JsonNode jsonNode = mapper.readTree(jsonInput); 
            
            Set<ValidationMessage> validationMessages = schema.validate(jsonNode);
            
            if (!validationMessages.isEmpty()) {
                String errorDetails = validationMessages.stream()
                    .map(ValidationMessage::getMessage)
                    .collect(java.util.stream.Collectors.joining("; "));
                    
                // Throw ValidationException to carry error details
                throw new ValidationException(errorDetails); 
            }
            
            // SUCCESS
            LOGGER.debug("JSON successfully validated against schema: {}", schemaPath);
            return true;
            
        } catch (ValidationException e) {
            // Catch ValidationException and rethrow to allow the caller to get details
            LOGGER.warn("JSON validation failed: {}", e.getMessage());
            throw e; 
        } catch (Exception e) {
            // Catch parsing errors (e.g., malformed JSON input)
            LOGGER.error("Failed to parse the input JSON string. Input: {}", jsonInput, e);
            throw new RuntimeException("Failed to parse the input JSON string. Ensure it is valid JSON.", e);
        }
    }

    /**
     * Validates a JSON string against the specific schema associated with the provided ProcessOptionType.
     * @param optionType The string instance (e.g., STEPS, etc.).
     * @param jsonInput The JSON content to validate, expected as a Map/Object from the process context.
     * @return {@code true} if validation is successful and the schema check passes, {@code false} otherwise.
     */
    public static boolean isJsonValidForType(String optionType, Object jsonInput) 
    {
        final String SCHEMA_BASE_PATH = "/schemas/";
        String jsonStringForValidation = null;

        // This check is redundant if the calling code already handles null/empty/non-valid input string,
        // but it's kept here as a defensive measure against null enum instance if called incorrectly.
        if (optionType == null || jsonInput == null ) {
             LOGGER.warn("Validation skipped. OptionType or JSON input object is null.");
             return false;
        }
        
        try {
            jsonStringForValidation = mapper.writeValueAsString(jsonInput);
            
            if (jsonStringForValidation.trim().isEmpty()) {
                LOGGER.warn("Validation skipped. JSON input is empty after serialization.");
                return false;
            }

        } catch (Exception e) {
            LOGGER.error("CRITICAL error: Failed to serialize input Map to JSON string for validation.", e);
            return false;
        }

        // Convert enum name to lowercase to form the file path (e.g., "steps.json").
        String schemaFileName = optionType.toLowerCase() + ".json";
        String fullSchemaPath = SCHEMA_BASE_PATH + schemaFileName;

        LOGGER.info("Starting JSON validation for Type '{}' using schema: {}", optionType, fullSchemaPath);
        
        try {
            JsonSchemaValidator.isJsonValid(fullSchemaPath, jsonStringForValidation);
            
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