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
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Loads a JSON schema from the resources folder and validates the input JSON string against it.
     * * @param schemaPath The full path to the JSON Schema file within the classpath (e.g., "/schemas/steps.json").
     * @param jsonInput The raw JSON string to validate.
     * @return {@code true} if validation is successful, {@code false} if the JSON content fails validation.
     * @throws RuntimeException if a critical application error occurs (e.g., schema file not found or IO error).
     * @throws ValidationException if the JSON content fails validation (details are encapsulated here).
     */
    public static boolean isJsonValid(String schemaPath, String jsonInput) throws ValidationException {
        
        // INSTANTIATION FOR STATIC CONTEXT:
        // A new instance is created to access non-static members like getResourceAsStream() 
        // and the ObjectMapper instance (if it were a field).
        // Since getResourceAsStream is common, we use the class loader directly.
        final ObjectMapper localMapper = new ObjectMapper(); 
        
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
            JsonNode jsonNode = localMapper.readTree(jsonInput); // Use local ObjectMapper
            
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
}