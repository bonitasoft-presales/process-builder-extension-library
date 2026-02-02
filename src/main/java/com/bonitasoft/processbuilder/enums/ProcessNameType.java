package com.bonitasoft.processbuilder.enums;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Defines the names of the configurable process definitions.
 *
 * @author Bonitasoft
 * @since 1.0
 */

public enum ProcessNameType {

    /**
     * Process definition for Form configuration.
     */
    FORM("Form", "Process for form configuration settings."),

    /**
     * Process definition for Notifications configuration.
     */
    NOTIFICATIONS("Notifications", "Process for notifications configuration settings."),

    /**
     * Process definition for Redirections configuration.
     */
    REDIRECTIONS("Redirections", "Process for redirection configuration settings."),

    /**
     * Process definition for REST APIs configuration and execution.
     */
    REST_APIS("RestApis", "Process for executing configured REST API services.");

    private final String key;
    private final String description;

    /**
     * Private constructor for the enumeration.
     * @param key The technical key used for mapping.
     * @param description A human-readable description of the type.
     */
    ProcessNameType(String key, String description) {
        this.key = key;
        this.description = description;
    }

    /**
     * Gets the human-readable, capitalized key of the process.
     *
     * @return The process key.
     */
    public String getKey() {
        return key;
    }

    /**
     * Gets a brief description of the process purpose.
     *
     * @return The process description.
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
            ProcessNameType.valueOf(input.trim().toUpperCase());
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
            Arrays.stream(values())
            .collect(Collectors.toMap(
                ProcessNameType::getKey, 
                ProcessNameType::getDescription, 
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
        return Arrays.stream(values())
            .map(ProcessNameType::getKey)
            .collect(Collectors.toUnmodifiableList());
    }
}