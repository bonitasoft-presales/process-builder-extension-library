package com.bonitasoft.processbuilder.extension;

import com.bonitasoft.processbuilder.records.InvolvedUsersData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

/**
 * Utility class responsible for parsing JSON configuration related to 
 * involved users, actors, and assignment logic within a Bonita process extension.
 * This class provides a static method to extract, validate, and structure data 
 * from a JSON string into an immutable {@code InvolvedUsersData} record in a single pass.
 */
public class InvolvedUsersParser {

    /**
     * Helper method to extract a required text field from a JsonNode.
     * @param node The parent JsonNode to search within.
     * @param fieldName The name of the required field.
     * @return The String value of the field.
     * @throws IllegalArgumentException if the field is missing, null, or not a valid text value.
     */
    public static String extractRequiredTextField(JsonNode node, String fieldName) {
        return Optional.ofNullable(node.get(fieldName))
                .filter(n -> !n.isNull()) // Ensure it's not JSON 'null'
                .filter(JsonNode::isTextual) // Ensure it is a String
                .map(JsonNode::asText)
                // Validate content: must not be empty or whitespace (new check added here)
                .filter(s -> !s.trim().isEmpty()) 
                .orElseThrow(() -> new IllegalArgumentException(
                        "Required field '" + fieldName + "' is missing, null, empty, or not a valid text value in the JSON configuration."
                ));
    }

    /**
     * Helper method to extract a field that MUST BE PRESENT, but can have a null or empty value.
     *
     * @param node The parent JsonNode to search within.
     * @param fieldName The name of the required, but nullable/empty, field.
     * @return The String value of the field, or null if the value is JSON null, or an empty string if the content is empty.
     * @throws IllegalArgumentException if the field is completely missing from the JSON object.
     */
    public static String extractNullableTextField(JsonNode node, String fieldName) {
        JsonNode fieldNode = Optional.ofNullable(node.get(fieldName))
            .orElseThrow(() -> new IllegalArgumentException(
                "Required field '" + fieldName + "' is MISSING from the JSON configuration, even though its content can be null or empty."
            ));

        if (fieldNode.isNull()) {
            return null;
        }

        if (fieldNode.isTextual()) {
            return fieldNode.asText();
        }
        
        throw new IllegalArgumentException(
            "Required field '" + fieldName + "' is present but is not a valid text value (found type: " + fieldNode.getNodeType() + ").");
    }

    /**
     * Parses the 'involvedUsers' JSON string, expecting the string to contain actor configuration
     * (stepManager, stepUser, memberShips).
     *
     * This method combines validation and data extraction into a single, optimized process.
     *
     * @param jsonString The JSON string to parse. This string is expected to be the object 
     * containing required fields like 'stepManager', 'stepUser', and 'memberShips'.
     * @return An {@code InvolvedUsersData} object containing the parsed actor references.
     * @throws IllegalArgumentException if the JSON string is null, empty, or cannot be parsed, 
     * OR if any required field is missing or invalid (including empty strings for text fields).
     */
    public static InvolvedUsersData parseInvolvedUsersJson(final String jsonString) {
        if (jsonString == null || jsonString.isEmpty()) {
            throw new IllegalArgumentException("Input JSON string cannot be null or empty.");
        }

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode involvedUsersNode = objectMapper.readTree(jsonString);
            
            // 1. Extract REQUIRED stepManagerRef (Checks for null, non-textual, and empty/whitespace)
            String stepManagerRef = extractNullableTextField(involvedUsersNode, "stepManager");

            // 2. Extract REQUIRED stepUserRef (Checks for null, non-textual, and empty/whitespace)
            String stepUserRef = extractNullableTextField(involvedUsersNode, "stepUser");

            // 3. Extract REQUIRED Memberships List (Checks array structure and content validity)
            List<String> memberships = Optional.ofNullable(involvedUsersNode.get("memberShips"))
                    .filter(JsonNode::isArray)
                    .map(jsonArray ->
                            StreamSupport.stream(jsonArray.spliterator(), false)
                                    // Validate each element: must be textual (string)
                                    .filter(JsonNode::isTextual)
                                    .map(JsonNode::asText)
                                    // Validate each element content: must not be empty/whitespace
                                    .filter(s -> !s.trim().isEmpty())
                                    .toList() // Java 16+ equivalent of collect(Collectors.toList())
                    )
                    // If 'memberShips' is missing or not an array, throw an error
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Required field 'memberShips' is missing or not a valid array in the JSON configuration."
                    ));

            // Return the Record (Assignment and validation completed in one pass)
            return new InvolvedUsersData(stepManagerRef, stepUserRef, memberships);

        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Failed to parse JSON string. Invalid JSON format.", e);
        } catch (IllegalArgumentException e) {
             // Re-throw specific errors (like missing/invalid fields) caught from helper methods
             throw e; 
        } catch (Exception e) {
            // Catch unexpected runtime errors and wrap them
            throw new IllegalArgumentException("An unexpected error occurred during JSON processing: " + e.getMessage(), e);
        }
    }
}