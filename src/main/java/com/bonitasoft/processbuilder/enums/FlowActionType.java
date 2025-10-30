package com.bonitasoft.processbuilder.enums;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Defines the specific action types available for configuration within a process step,
 * such as defining a form, setting up notifications, or establishing redirections.
 *
 * @author Bonitasoft (Adapted)
 * @since 1.0
 */
public enum FlowActionType {

    /**
     * Action related to defining the interface or data capture form for a step.
     */
    FORM("form", "Form Action: Configures the form or UI presented to the user at a specific step."),

    /**
     * Action related to setting up alerts, emails, or system notifications.
     */
    NOTIFICATIONS("notifications", "Notifications Action: Manages configuration for sending alerts or communications."),

    /**
     * Action related to defining post-task navigation and flow redirection.
     */
    REDIRECTIONS("redirections", "Redirections Action: Defines where the user is sent after completing the task.");

    private final String key;
    private final String description;

    /**
     * Private constructor for the enumeration.
     * @param key The technical key used for mapping.
     * @param description A human-readable description of the type.
     */
    FlowActionType(String key, String description) {
        this.key = key;
        this.description = description;
    }

    /**
     * Gets the technical key of the flow action type, typically used for internal logic and data mapping.
     *
     * @return The technical key (lowercase).
     */
    public String getKey() {
        return key;
    }

    /**
     * Gets a brief business description of the action type.
     *
     * @return The description for the user interface or documentation.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Checks if a given string corresponds to a valid enum constant, ignoring case and leading/trailing spaces.
     * @param input The string to validate.
     * @return {@code true} if the string is a valid enum constant, {@code false} otherwise.
     */
    public static boolean isValid(String input) {
        if (input == null || input.trim().isEmpty()) {
            return false;
        }
        try {
            FlowActionType.valueOf(input.trim().toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
    
    /**
     * Retrieves all process instance states as a read-only Map where the key is the technical key 
     * and the value is the description.
     * @return A map containing all process state data (Key -> Description).
     */
    public static Map<String, String> getAllData() {
        Map<String, String> stateData = 
            Arrays.stream(FlowActionType.values())
            .collect(Collectors.toMap(
                FlowActionType::getKey, 
                FlowActionType::getDescription, 
                (oldValue, newValue) -> oldValue, 
                LinkedHashMap::new 
            ));
        
        return Collections.unmodifiableMap(stateData);
    }
    
    /**
     * Retrieves all technical keys as a read-only List of Strings.
     * @return A list containing all technical keys.
     */
    public static List<String> getAllKeysList() {
        return Arrays.stream(FlowActionType.values())
            .map(FlowActionType::getKey)
            .collect(Collectors.toUnmodifiableList());
    }
}