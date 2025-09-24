package com.bonitasoft.processbuilder.enums;

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
    CATEGORY;

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
}

