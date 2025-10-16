package com.bonitasoft.processbuilder.validation;

import com.bonitasoft.processbuilder.enums.ActionType;
import com.bonitasoft.processbuilder.enums.ObjectsManagementOptionType;
import com.bonitasoft.processbuilder.enums.ProcessOptionType;

/**
 * Utility class providing validation methods for checking if action and option 
 * type strings match predefined enum constants.
 * <p>
 * This class primarily handles case-insensitive comparisons against the names
 * of various application enums to ensure clean conditional branching in the process logic.
 * </p>
 * @author Bonitasoft
 * @since 1.0
 */
public final class ProcessOptionValidator {

    /**
     * Private constructor to prevent instantiation of this utility class.
     * @throws UnsupportedOperationException always, to enforce the utility pattern.
     */
    private ProcessOptionValidator() {
        throw new UnsupportedOperationException("This is a "+this.getClass().getSimpleName()+" class and cannot be instantiated.");
    }

    /**
     * Checks if the actual action and option type strings match the provided expected 
     * {@link ActionType} and {@link ObjectsManagementOptionType} enum constants.
     *
     * @param actionType The action string received from the process context (e.g., "DELETE").
     * @param optionType The object option string received from the process context (e.g., "CATEGORY").
     * @param expectedActionType     The expected ActionType enum constant for comparison.
     * @param expectedOptionType     The expected ObjectsManagementOptionType enum constant for comparison.
     * @return {@code true} if both actual strings match their respective expected enum names (case-insensitive), {@code false} otherwise.
     */
    public static boolean isMatchingActionAndOption(
            String actionType, 
            String optionType, 
            ActionType expectedActionType, 
            ObjectsManagementOptionType expectedOptionType) {

        // 1. Initial null/empty string check for early exit.
        if (actionType == null || actionType.trim().isEmpty() ||
            optionType == null || optionType.trim().isEmpty() ||
            expectedActionType == null || expectedOptionType == null) {
            return false;
        }

        // 2. Perform the case-insensitive comparison against the enum's constant name.
        boolean actionMatches = actionType.equalsIgnoreCase(expectedActionType.name());

        boolean optionMatches = optionType.equalsIgnoreCase(expectedOptionType.name());

        // 3. Return true only if both comparisons are true.
        return actionMatches && optionMatches;
    }

    /**
     * Checks if the actual action and option type strings match the provided expected 
     * {@link ActionType} and {@link ProcessOptionType} enum constants.
     *
     * @param actionType The action string received from the process context (e.g., "UPDATE").
     * @param optionType The object option string received from the process context (e.g., "STEPS").
     * @param expectedActionType     The expected ActionType enum constant for comparison.
     * @param expectedProcessOptionType The expected ProcessOptionType enum constant for comparison.
     * @return {@code true} if both actual strings match their respective expected enum names (case-insensitive), {@code false} otherwise.
     */
    public static boolean isMatchingActionAndOption(
            String actionType, 
            String optionType, 
            ActionType expectedActionType, 
            ProcessOptionType expectedProcessOptionType) {
        
        // Initial null/empty string check for early exit.
        if (actionType == null || actionType.trim().isEmpty() ||
            optionType == null || optionType.trim().isEmpty() ||
            expectedActionType == null || expectedProcessOptionType == null) {
            return false;
        }

        // 1. Check ActionType match (same as the original method)
        boolean actionMatches = actionType.equalsIgnoreCase(expectedActionType.name());

        // 2. Check OptionType match against the ProcessOptionType enum
        boolean optionMatches = optionType.equalsIgnoreCase(expectedProcessOptionType.name());

        // 3. Return the combined result.
        return actionMatches && optionMatches;
    }

}