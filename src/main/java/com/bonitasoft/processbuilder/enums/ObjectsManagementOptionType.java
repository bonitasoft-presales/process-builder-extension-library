package com.bonitasoft.processbuilder.enums;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.bonitasoft.processbuilder.validation.JsonSchemaValidator;

/**
 * Defines the valid option types for the object management process.
 * <p>
 * This enumeration helps to ensure type safety and code readability by
 * restricting the object type to a predefined set of values, specifically
 * for system configuration objects managed by a process.
 * </p>
 * @author Bonitasoft
 * @since 1.0
 */
public enum ObjectsManagementOptionType {
    /**
     * Represents a category object used for classification or grouping.
     */
    CATEGORY("Category", "Represents a classification or grouping category."),
    
    /**
     * Represents a system configuration object containing key-value settings.
     */
    CONFIGURATION("Configuration", "Represents a system configuration object containing application settings and parameters."),

    /**
     * Represents a **generic entry** or record, typically originating from a master data table.
     * This object holds the actual data values (e.g., records from a "lookup" table).
     */
    GENERIC_ENTRY("GenericEntry", "Represents a single master data record or lookup table entry."),


    /**
     * Represents the **entity type** or classification identifier for a record.
     * This field is embedded within a GENERIC_ENTRY object to specify the kind
     * of master data or lookup object the entry represents, allowing a single table
     * structure (GENERIC_ENTRY) to contain multiple logical object types.
     */
    ENTITY_TYPE("EntityType", "Defines the classification ID for a master data record."),

    /**
     * Represents an AI provider configuration for integrating external AI/LLM services.
     * This object holds connection details, model selection, and credential information
     * for AI providers such as OpenAI, Anthropic, or Azure OpenAI.
     */
    AI_PROVIDER("AiProvider", "Represents an AI provider configuration for external AI/LLM service integration.");

    private final String key;
    private final String description;

    /**
     * Private constructor for the enumeration.
     * @param key The technical key used for mapping.
     * @param description A human-readable description of the type.
     */
    ObjectsManagementOptionType(String key, String description) {
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
     * @param optionTypeInput The string to validate (e.g., "category", "  SMTP ").
     * @return {@code true} if the string is a valid enum constant, {@code false} otherwise.
     */
    public static boolean isValid(String optionTypeInput) {
        try {
            if (optionTypeInput == null || optionTypeInput.trim().isEmpty()) {
                return false;
            }
            // Attempts to find the enum constant by name, ignoring case
            ObjectsManagementOptionType.valueOf(optionTypeInput.trim().toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            // The string did not match any constant
            return false;
        }
    }

    /**
     * Validates a JSON input against the specific schema associated with the provided object management type.
     * <p>
     * This method delegates the validation call to the centralized {@link JsonSchemaValidator} utility.
     * </p>
     * @param actionType The action being performed (e.g., "DELETE", "INSERT", "UPDATE").
     * @param optionType The string instance of the object type (e.g., "CATEGORY", "SMTP").
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
            Arrays.stream(values())
            .collect(Collectors.toMap(
                ObjectsManagementOptionType::getKey, 
                ObjectsManagementOptionType::getDescription, 
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
            .map(ObjectsManagementOptionType::getKey)
            .collect(Collectors.toUnmodifiableList());
    }
}