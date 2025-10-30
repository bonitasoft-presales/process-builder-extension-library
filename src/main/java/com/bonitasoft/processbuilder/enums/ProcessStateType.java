package com.bonitasoft.processbuilder.enums;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Defines the possible lifecycle states for a Process Definition (PBProcess).
 * Includes main execution states and an optional health indicator.
 */
public enum ProcessStateType {

    /** The process definition is currently being edited and is not available for execution. */
    DRAFT("Draft", "Definition is under construction."),

    /** The process definition is active and ready to be instantiated. */
    RUNNING("Running", "Process is enabled and available for new instances."),

    /** The process definition has been disabled and cannot be instantiated. */
    STOPPED("Stopped", "Process is temporarily disabled or paused."),

    /** The process definition is marked as historical and is no longer used. */
    ARCHIVED("Archived", "Definition is obsolete and moved to historical storage."),

    /** The optional indicator state: the process definition is fully operational and healthy. */
    HEALTHY("Healthy", "The process definition is fully operational and running without known issues."),

    /** The optional indicator state: the process definition has known operational issues (e.g., failed instances). */
    IN_ERROR("In Error", "The process definition is running but experiencing configuration or runtime errors.");

    private final String key;
    private final String description;

    /**
     * Private constructor for the enumeration.
     * @param key The technical key used for mapping (e.g., in the database or REST APIs).
     * @param description A human-readable description of the state.
     */
    ProcessStateType(String key, String description) {
        this.key = key;
        this.description = description;
    }

    /**
     * Gets the technical key of the state.
     * @return The technical key (String).
     */
    public String getKey() {
        return key;
    }

    /**
     * Gets the human-readable description of the state.
     * @return The description (String).
     */
    public String getDescription() {
        return description;
    }

    /**
     * Checks if a given string corresponds to a valid enum constant, ignoring case and leading/trailing spaces.
     *
     * @param input The string to validate (e.g., " draft ", "Running").
     * @return {@code true} if the string is a valid enum constant, {@code false} otherwise.
     */
    public static boolean isValid(String input) {
        if (input == null || input.trim().isEmpty()) {
            return false;
        }
        try {
            // Attempts to find the enum constant by name, ignoring case
            // The valueOf method is case-sensitive, so we convert the input to uppercase.
            ProcessStateType.valueOf(input.trim().toUpperCase().replace(" ", "_")); // Handles IN_ERROR separation
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
    
    /**
     * Retrieves all process states as a read-only Map where the key is the technical key 
     * and the value is the description.
     *
     * @return A map containing all process state data (Key -> Description).
     */
    public static Map<String, String> getAllData() {
        Map<String, String> stateData = 
            Arrays.stream(ProcessStateType.values())
            .collect(Collectors.toMap(
                ProcessStateType::getKey, 
                ProcessStateType::getDescription, 
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
        return Arrays.stream(ProcessStateType.values())
            .map(ProcessStateType::getKey)
            .collect(Collectors.toUnmodifiableList());
    }
}
