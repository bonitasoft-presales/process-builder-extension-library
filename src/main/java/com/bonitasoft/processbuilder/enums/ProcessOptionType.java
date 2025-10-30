package com.bonitasoft.processbuilder.enums;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.bonitasoft.processbuilder.validation.JsonSchemaValidator;

/**
 * Defines the valid option types for a process.
 * <p>
 * This enumeration is used to categorize different components within a process,
 * such as parameters, users, inputs, steps, and status. It improves code clarity
 * and reduces the risk of errors from using hard-coded strings.
 * </p>
 *
 * @author Bonitasoft
 * @since 1.0
 */
public enum ProcessOptionType {
    /**
     * Represents a process parameter or configuration setting.
     */
    PARAMETER("Parameter", "Represents a configurable parameter or setting within the process definition."),
    
    /**
     * Represents a user or a list of users involved in a process instance.
     */
    USERS("Users", "Represents a user or set of users associated with the process."),
    
    /**
     * Represents the initial data inputs required to start a process instance.
     */
    INPUTS("Inputs", "Represents the initial data inputs required for process instantiation."),
    
    /**
     * Represents a step or stage in the process workflow, typically linked to a task or action.
     */
    STEPS("Steps", "Represents a step or stage within the process workflow."),
    
    /**
     * Represents the status or state of the process execution lifecycle.
     */
    STATUS("Status", "Represents the current status or state of the process instance.");

    private final String key;
    private final String description;

    /**
     * Private constructor for the enumeration.
     * @param key The technical key used for mapping.
     * @param description A human-readable description of the type.
     */
    ProcessOptionType(String key, String description) {
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
     *
     * @param optionTypeInput The string to validate (e.g., "users", "  STEPS ").
     * @return {@code true} if the string, after trimming and converting to uppercase, matches a valid enum constant, 
     * {@code false} otherwise (including for null, empty, or blank strings).
     */
    public static boolean isValid(String optionTypeInput) {
        try {
            if (optionTypeInput == null || optionTypeInput.trim().isEmpty()) {
                return false;
            }
            // Attempts to find the enum constant by name
            ProcessOptionType.valueOf(optionTypeInput.trim().toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            // The string did not match any constant
            return false;
        }
    }

    /**
     * Validates a JSON input against the specific schema associated with the provided ProcessOptionType.
     * <p>
     * This method delegates the validation to the centralized {@link JsonSchemaValidator}.
     * </p>
     * @param actionType The action being performed (e.g., "DELETE", "INSERT", "UPDATE").
     * @param optionType The string name of the ProcessOptionType (e.g., "STEPS", "USERS").
     * @param jsonInput The JSON content to validate, expected as a Map/Object from the process context.
     * @return {@code true} if validation is successful and the schema check passes, {@code false} otherwise.
     */
    public static boolean isJsonValidForType(String actionType, String optionType, Object jsonInput) 
    {
        return JsonSchemaValidator.isJsonValidForType(actionType, optionType, jsonInput);
    }
    
    /**
     * Retrieves all process instance states as a read-only Map where the key is the technical key 
     * and the value is the description.
     * @return A map containing all process state data (Key -> Description).
     */
    public static Map<String, String> getAllData() {
        Map<String, String> stateData = 
            Arrays.stream(ProcessOptionType.values())
            .collect(Collectors.toMap(
                ProcessOptionType::getKey, 
                ProcessOptionType::getDescription, 
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
        return Arrays.stream(ProcessOptionType.values())
            .map(ProcessOptionType::getKey)
            .collect(Collectors.toUnmodifiableList());
    }
}