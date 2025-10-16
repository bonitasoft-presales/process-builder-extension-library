package com.bonitasoft.processbuilder.enums;

import com.bonitasoft.processbuilder.validation.JsonSchemaValidator;

/**
 * Defines the valid option types for a process.
 * <p>
 * This enumeration is used to categorize different components within a process,
 * such as parameters, users, inputs, steps, and status. It improves code clarity
 * and reduces the risk of errors from using hard-coded strings.
 * </p>
 *
 * @author Bonitasoft
 * @since 1.0
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

    /**
     * Checks if a given string corresponds to a valid enum constant, ignoring case and leading/trailing spaces.
     *
     * @param optionTypeInput The string to validate (e.g., "users", "  STEPS ").
     * @return {@code true} if the string, after trimming and converting to uppercase, matches a valid enum constant, 
     * {@code false} otherwise (including for null, empty, or blank strings).
     */
    public static boolean isValid(String optionTypeInput) {
        try {
            if (optionTypeInput == null || optionTypeInput.trim().isEmpty()) {
                return false;
            }
            // Attempts to find the enum constant by name
            ProcessOptionType.valueOf(optionTypeInput.trim().toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            // The string did not match any constant
            return false;
        }
    }

    /**
     * Validates a JSON input against the specific schema associated with the provided ProcessOptionType.
     * <p>
     * This method delegates the validation to the centralized {@link JsonSchemaValidator}.
     * </p>
     * @param actionType The action being performed (e.g., "DELETE", "INSERT", "UPDATE").
     * @param optionType The string name of the ProcessOptionType (e.g., "STEPS", "USERS").
     * @param jsonInput The JSON content to validate, expected as a Map/Object from the process context.
     * @return {@code true} if validation is successful and the schema check passes, {@code false} otherwise.
     */
    public static boolean isJsonValidForType(String actionType, String optionType, Object jsonInput) 
    {
        return JsonSchemaValidator.isJsonValidForType(actionType, optionType, jsonInput);
    }
}