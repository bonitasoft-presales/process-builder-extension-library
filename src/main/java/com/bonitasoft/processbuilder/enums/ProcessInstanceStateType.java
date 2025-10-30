package com.bonitasoft.processbuilder.enums;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Defines the possible lifecycle states for a Process Instance (PBProcessInstance).
 * Includes execution, termination, and future placeholder states.
 */
public enum ProcessInstanceStateType {
    
    /** The instance is currently active and processing steps. */
    RUNNING("Running", "The process instance is currently active."),
    
    /** The instance has successfully completed its final step. */
    COMPLETED("Completed", "The process instance finished successfully."),
    
    /** The instance was manually terminated before completion. */
    CANCELED("Canceled", "The process instance was manually terminated."),
    
    /** The instance failed due to a severe system or business error. */
    FAILED("Failed", "The process instance encountered a non-recoverable error."),
    
    /** Future state: The instance has been manually suspended and is awaiting restart. */
    PAUSED("Paused", "The process instance has been manually suspended."),
    
    /** Future state: The instance is moved to historical records for dashboard performance. */
    ARCHIVED("Archived", "The process instance is marked as historical and hidden from active views.");

    private final String key;
    private final String description;

    /**
     * Private constructor for the enumeration.
     * @param key The technical key used for mapping, persistence, or REST API representation. 
     * This should typically match the enum constant name (e.g., "RUNNING").
     * @param description A human-readable description of the process instance state.
     */
    ProcessInstanceStateType(String key, String description) {
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
     * @param input The string to validate.
     * @return {@code true} if the string is a valid enum constant, {@code false} otherwise.
     */
    public static boolean isValid(String input) {
        if (input == null || input.trim().isEmpty()) {
            return false;
        }
        try {
            ProcessInstanceStateType.valueOf(input.trim().toUpperCase());
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
    public static Map<String, String> getAllStatesData() {
        Map<String, String> stateData = 
            Arrays.stream(ProcessInstanceStateType.values())
            .collect(Collectors.toMap(
                ProcessInstanceStateType::getKey, 
                ProcessInstanceStateType::getDescription, 
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
        return Arrays.stream(ProcessInstanceStateType.values())
            .map(ProcessInstanceStateType::getKey)
            .collect(Collectors.toUnmodifiableList());
    }
}