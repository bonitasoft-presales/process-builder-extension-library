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
 * This class includes a static method to extract and structure data necessary for user
 * involvement and task assignment from a JSON string.
 */
public class InvolvedUsersParser {

    /**
     * Helper method to extract a required text field from a JsonNode.
     * * @param node The parent JsonNode to search within.
     * @param fieldName The name of the required field.
     * @return The String value of the field.
     * @throws IllegalArgumentException if the field is missing, null, or not a text value.
     */
    private static String extractRequiredTextField(JsonNode node, String fieldName) {
        return Optional.ofNullable(node.get(fieldName))
                .filter(n -> !n.isNull()) // Ensure it's not JSON 'null'
                .filter(JsonNode::isTextual) // Ensure it is a String
                .map(JsonNode::asText)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Required field '" + fieldName + "' is missing, null, or not a valid text value in the JSON configuration."
                ));
    }

    /**
     * Parses the 'involvedUsers' JSON string, expecting the string to contain actor configuration
     * directly (e.g., stepManager, stepUser, memberShips).
     *
     * This method extracts references for the step manager, step user, and memberships,
     * packaging them into an immutable data record.
     *
     * @param jsonString The JSON string to parse. This string is expected to be the object 
     * containing required fields like 'stepManager', 'stepUser', and 'memberShips'.
     * @return An {@code InvolvedUsersData} object containing the parsed actor references.
     * @throws IllegalArgumentException if the JSON string is null, empty, or cannot be parsed, 
     * OR if any required field is missing or invalid.
     */
    public static InvolvedUsersData parseInvolvedUsersJson(final String jsonString) {
        if (jsonString == null || jsonString.isEmpty()) {
            throw new IllegalArgumentException("Input JSON string cannot be null or empty.");
        }

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode involvedUsersNode = objectMapper.readTree(jsonString);
            
            // 1. Extract REQUIRED stepManagerRef
            String stepManagerRef = extractRequiredTextField(involvedUsersNode, "stepManager");

            // 2. Extract REQUIRED stepUserRef
            String stepUserRef = extractRequiredTextField(involvedUsersNode, "stepUser");

            // 3. Extract REQUIRED Memberships List
            List<String> memberships = Optional.ofNullable(involvedUsersNode.get("memberShips"))
                    .filter(JsonNode::isArray)
                    .map(jsonArray ->
                            StreamSupport.stream(jsonArray.spliterator(), false)
                                    .filter(JsonNode::isTextual)
                                    .map(JsonNode::asText)
                                    .toList()
                    )
                    // If 'memberShips' is missing or not an array, throw an error
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Required field 'memberShips' is missing or not a valid array in the JSON configuration."
                    ));

            // Return the Record
            return new InvolvedUsersData(stepManagerRef, stepUserRef, memberships);

        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Failed to parse JSON string. Invalid JSON format.", e);
        } catch (IllegalArgumentException e) {
             // Re-throw specific errors (like missing fields) caught from helper methods
             throw e; 
        } catch (Exception e) {
            throw new IllegalArgumentException("An unexpected error occurred during JSON processing.", e);
        }
    }
}