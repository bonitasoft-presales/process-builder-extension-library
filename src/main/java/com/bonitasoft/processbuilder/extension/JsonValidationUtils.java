package com.bonitasoft.processbuilder.extension;

import com.fasterxml.jackson.databind.JsonNode;
import org.bonitasoft.engine.connector.ConnectorValidationException;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.StreamSupport;

/**
 * Utility class for generic JSON structure validation checks, decoupled from the main parsing logic.
 */
public class JsonValidationUtils {

    /**
     * Private constructor to prevent instantiation of this utility class.
     *
     * @throws UnsupportedOperationException always, to enforce the utility pattern.
     */
    private JsonValidationUtils() {
        throw new UnsupportedOperationException("This is a " + this.getClass().getSimpleName() + " class and cannot be instantiated.");
    }

    /**
     * A utility method to validate the existence and type of a specific field in a JSON node.
     * @param parentNode The parent JSON node to search in.
     * @param fieldName The name of the field to validate.
     * @param typeCheck A predicate to check the required type of the field (e.g., JsonNode::isTextual).
     * @param errorMessageField The name of the field to use in the error message.
     * @throws ConnectorValidationException if the field is missing or has an invalid type.
     */
    public static void validateField(final JsonNode parentNode, final String fieldName, final Predicate<JsonNode> typeCheck, final String errorMessageField) throws ConnectorValidationException {
        JsonNode fieldNode = parentNode.get(fieldName);

        Optional.ofNullable(fieldNode)
            .filter(typeCheck)
            .orElseThrow(() -> new ConnectorValidationException(
                String.format("Mandatory field '%s' is missing or has an invalid type.", errorMessageField)));
    }

    /**
     * Validates that the 'memberShips' node is a JSON array and that all elements 
     * within the array are non-empty strings (membership references).
     * @param memberShipsNode The JSON node containing the list of membership references.
     * @throws ConnectorValidationException if the node is not an array or if any element is not a valid string reference.
     */
    public static void validateMemberships(final JsonNode memberShipsNode) throws ConnectorValidationException {
        
        // 1. Validate that the main node is an Array.
        if (memberShipsNode == null || !memberShipsNode.isArray()) {
             // We check for null explicitly to provide a more specific message if needed, but array check usually suffices
             throw new ConnectorValidationException("The 'memberShips' configuration must be a JSON array.");
        }

        try {
            StreamSupport.stream(memberShipsNode.spliterator(), false)
                .forEach(referenceNode -> {
                    
                    // 2. Validate that each element is a string (textual node).
                    if (!referenceNode.isTextual()) {
                        throw new RuntimeException("Each element in the 'memberShips' array must be a string (membership reference).");
                    }
                    
                    // 3. Validate that the string is not empty.
                    if (referenceNode.asText().trim().isEmpty()) {
                        throw new RuntimeException("Membership references in the array cannot be empty strings.");
                    }
                });
        } catch (RuntimeException e) {
            // 4. Catch the exception thrown in the stream and re-throw it as a ConnectorValidationException.
            // This is necessary because lambdas cannot throw checked exceptions.
            throw new ConnectorValidationException(e.getMessage());
        }
    }
}