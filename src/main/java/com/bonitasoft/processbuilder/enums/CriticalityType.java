package com.bonitasoft.processbuilder.enums;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Defines the valid criticality levels for a business process, along with their business descriptions.
 * This enumeration is typically used as the source for the 'Criticality' master data.
 *
 * @author Bonitasoft
 * @since 1.0
 */
public enum CriticalityType {

    /**
     * Indicates severe business impact, requiring immediate attention and maximum uptime assurance.
     */
    HIGH("High", "Indicates severe business impact, requiring immediate attention and maximum uptime assurance."),

    /**
     * Indicates significant business impact, requiring standard attention and prompt resolution.
     */
    MODERATE("Moderate", "Indicates significant business impact, requiring standard attention and prompt resolution."),

    /**
     * Indicates minimal business impact, usually involving non-urgent or supportive functions.
     */
    LOW("Low", "Indicates minimal business impact, usually involving non-urgent or supportive functions."),

    /**
     * Indicates a purely informational or non-critical process with no business impact on failure.
     */
    NONE("None", "Indicates a purely informational or non-critical process with no business impact on failure.");

    private final String key;
    private final String description;

    /**
     * Private constructor for the enumeration.
     * @param key The technical key used for mapping.
     * @param description A human-readable description of the type.
     */
    CriticalityType(String key, String description) {
        this.key = key;
        this.description = description;
    }

    /**
     * Returns the unique string key associated with this criticality type.
     * @return The criticality key (e.g., "High").
     */
    public String getKey() {
        return key;
    }

    /**
     * Returns the business description of this criticality type.
     * @return The description.
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
            CriticalityType.valueOf(input.trim().toUpperCase());
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
            Arrays.stream(CriticalityType.values())
            .collect(Collectors.toMap(
                CriticalityType::getKey, 
                CriticalityType::getDescription, 
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
        return Arrays.stream(CriticalityType.values())
            .map(CriticalityType::getKey)
            .collect(Collectors.toUnmodifiableList());
    }

}