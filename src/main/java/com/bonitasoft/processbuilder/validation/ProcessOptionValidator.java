package com.bonitasoft.processbuilder.validation;

import com.bonitasoft.processbuilder.enums.ActionType;
import com.bonitasoft.processbuilder.enums.ObjectsManagementOptionType;
import com.bonitasoft.processbuilder.enums.ProcessOptionType;

public final class ProcessOptionValidator {

    // Prevent instantiation for a utility class
    private ProcessOptionValidator() {
        throw new UnsupportedOperationException("This is a "+this.getClass().getSimpleName()+" class and cannot be instantiated.");
    }

    /**
     * Checks if the actual action and option type strings match the provided expected enum constants.
     *
     * @param actualActionTypeString The action string received from the process context (e.g., "DELETE").
     * @param actualOptionTypeString The object option string received from the process context (e.g., "CATEGORY").
     * @param expectedActionType     The expected ActionType enum constant for comparison.
     * @param expectedOptionType     The expected ObjectsManagementOptionType enum constant for comparison.
     * @return {@code true} if both actual strings match their respective expected enum names (case-insensitive), {@code false} otherwise.
     */
    public static boolean isMatchingActionAndOption(
            String actualActionTypeString, 
            String actualOptionTypeString, 
            ActionType expectedActionType, 
            ObjectsManagementOptionType expectedOptionType) {

        // 1. Initial null/empty string check for early exit.
        if (actualActionTypeString == null || actualActionTypeString.trim().isEmpty() ||
            actualOptionTypeString == null || actualOptionTypeString.trim().isEmpty() ||
            expectedActionType == null || expectedOptionType == null) {
            return false;
        }

        // 2. Perform the case-insensitive comparison against the enum's constant name.
        boolean actionMatches = actualActionTypeString.equalsIgnoreCase(expectedActionType.name());

        boolean optionMatches = actualOptionTypeString.equalsIgnoreCase(expectedOptionType.name());

        // 3. Return true only if both comparisons are true.
        return actionMatches && optionMatches;
    }

    /**
     * Checks if the actual action and option type strings match the provided expected enum constants.
     *
     * @param actualActionTypeString The action string received from the process context (e.g., "DELETE").
     * @param actualOptionTypeString The object option string received from the process context (e.g., "STEP").
     * @param expectedActionType     The expected ActionType enum constant for comparison.
     * @param expectedProcessOptionType The expected ProcessOptionType enum constant for comparison.
     * @return {@code true} if both actual strings match their respective expected enum names (case-insensitive), {@code false} otherwise.
     */
    public static boolean isMatchingActionAndOption(
            String actualActionTypeString, 
            String actualOptionTypeString, 
            ActionType expectedActionType, 
            ProcessOptionType expectedProcessOptionType) {
        
        // Initial null/empty string check for early exit.
        if (actualActionTypeString == null || actualActionTypeString.trim().isEmpty() ||
            actualOptionTypeString == null || actualOptionTypeString.trim().isEmpty() ||
            expectedActionType == null || expectedProcessOptionType == null) {
            return false;
        }

        // 1. Check ActionType match (same as the original method)
        boolean actionMatches = actualActionTypeString.equalsIgnoreCase(expectedActionType.name());

        // 2. Check OptionType match against the ProcessOptionType enum
        boolean optionMatches = actualOptionTypeString.equalsIgnoreCase(expectedProcessOptionType.name());

        // 3. Return the combined result.
        return actionMatches && optionMatches;
    }

}