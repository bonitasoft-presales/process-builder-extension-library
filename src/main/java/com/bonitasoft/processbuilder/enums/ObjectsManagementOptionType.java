package com.bonitasoft.processbuilder.enums;

import com.bonitasoft.processbuilder.validation.JsonSchemaValidator;

/**
 * Defines the valid option types for the object management process.
 * <p>
 * This enumeration helps to ensure type safety and code readability by
 * restricting the object type to a predefined set of values.
 * </p>
 */
public enum ObjectsManagementOptionType {
    /**
     * Represents a category object.
     */
    CATEGORY, 

    /**
     * Represents a smtp object.
     */
    SMTP;

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
            ObjectsManagementOptionType.valueOf(optionTypeInput.trim().toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Validates a JSON string against the specific schema associated with the provided ProcessOptionType.
     * @param actionType The action being performed (e.g., "DELETE", "INSERT").
     * @param optionType The string instance (e.g., STEPS, etc.).
     * @param jsonInput The JSON content to validate, expected as a Map/Object from the process context.
     * @return {@code true} if validation is successful and the schema check passes, {@code false} otherwise.
     */
    public static boolean isJsonValidForType(String actionType, String optionType, Object jsonInput) 
    {
        return JsonSchemaValidator.isJsonValidForType(actionType, optionType, jsonInput);
    }
}

