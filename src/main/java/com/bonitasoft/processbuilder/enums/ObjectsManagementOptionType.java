package com.bonitasoft.processbuilder.enums;

import com.bonitasoft.processbuilder.validation.JsonSchemaValidator;

/**
 * Defines the valid option types for the object management process.
 * <p>
 * This enumeration helps to ensure type safety and code readability by
 * restricting the object type to a predefined set of values, specifically
 * for system configuration objects managed by a process.
 * </p>
 * * @author [Your Name or Company Name]
 * @since 1.0
 */
public enum ObjectsManagementOptionType {
    /**
     * Represents a category object used for classification or grouping.
     */
    CATEGORY, 

    /**
     * Represents an SMTP (Simple Mail Transfer Protocol) configuration object.
     */
    SMTP;

    /**
     * Checks if a given string corresponds to a valid enum constant, ignoring case and leading/trailing spaces.
     *
     * @param optionTypeInput The string to validate (e.g., "category", "  SMTP ").
     * @return {@code true} if the string is a valid enum constant, {@code false} otherwise.
     */
    public static boolean isValid(String optionTypeInput) {
        try {
            if (optionTypeInput == null || optionTypeInput.trim().isEmpty()) {
                return false;
            }
            // Attempts to find the enum constant by name, ignoring case
            ObjectsManagementOptionType.valueOf(optionTypeInput.trim().toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            // The string did not match any constant
            return false;
        }
    }

    /**
     * Validates a JSON input against the specific schema associated with the provided object management type.
     * <p>
     * This method delegates the validation call to the centralized {@link JsonSchemaValidator} utility.
     * </p>
     * * @param actionType The action being performed (e.g., "DELETE", "INSERT", "UPDATE").
     * @param optionType The string instance of the object type (e.g., "CATEGORY", "SMTP").
     * @param jsonInput The JSON content to validate, expected as a Map/Object from the process context.
     * @return {@code true} if validation is successful and the schema check passes, {@code false} otherwise.
     */
    public static boolean isJsonValidForType(String actionType, String optionType, Object jsonInput) 
    {
        return JsonSchemaValidator.isJsonValidForType(actionType, optionType, jsonInput);
    }
}