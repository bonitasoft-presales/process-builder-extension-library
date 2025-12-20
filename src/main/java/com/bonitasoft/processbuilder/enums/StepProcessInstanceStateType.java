package com.bonitasoft.processbuilder.enums;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Defines the possible lifecycle states for a Process Step Instance (PBStepProcessInstance).
 */
public enum StepProcessInstanceStateType {
    
    /** The step is currently being executed or is waiting for execution in the engine. */
    RUNNING("Running", "The step is currently active."),
    
    /** The step is a user task awaiting assignment or completion by an actor. */
    PENDING("Pending", "The step is a user task awaiting action."),
    
    /** The step failed due to a system error (e.g., connector failure). */
    FAILED("Failed", "The step execution encountered an error."),
    
    /** The human task has been successfully completed by the user (e.g., the form 
     * was submitted). The step's subsequent actions are now being processed.
     */
    COMPLETED("Completed", "The human task (e.g., form submission) was successfully completed."),

    /** The step execution finished successfully, meaning all associated actions 
     * (connectors, notifications, redirections) have been successfully processed.
     */
    ENDED("Ended", "The step finished successfully after all associated actions were executed."),
    
    /** The step was bypassed or manually terminated. */
    CANCELED("Canceled", "The step was manually bypassed or terminated.");

    private final String key;
    private final String description;

    /**
     * Private constructor for the enumeration.
     * @param key The technical key used for mapping (e.g., in the database or REST APIs).
     * @param description A human-readable description of the state.
     */
    StepProcessInstanceStateType(String key, String description) {
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
            StepProcessInstanceStateType.valueOf(input.trim().toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
    
    /**
     * Retrieves all step instance states as a read-only Map where the key is the technical key 
     * and the value is the description.
     * @return A map containing all process state data (Key -> Description).
     */
    public static Map<String, String> getAllData() {
        Map<String, String> stateData = 
            Arrays.stream(values())
            .collect(Collectors.toMap(
                StepProcessInstanceStateType::getKey, 
                StepProcessInstanceStateType::getDescription, 
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
            .map(StepProcessInstanceStateType::getKey)
            .collect(Collectors.toUnmodifiableList());
    }
}