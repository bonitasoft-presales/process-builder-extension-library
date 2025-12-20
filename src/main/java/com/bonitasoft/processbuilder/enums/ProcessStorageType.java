package com.bonitasoft.processbuilder.enums;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Defines the valid storage types for process-related files, along with their business descriptions.
 * This enumeration is typically used as the source for the 'Process Storage' master data.
 *
 * @author Bonitasoft
 * @since 1.0
 */
public enum ProcessStorageType {

    /**
     * Files are stored on the local application server where the process engine is deployed and are retained indefinitely.
     */
    LOCAL("Local", "Files are stored on the local application server where the process engine is deployed and are retained indefinitely."),

    /**
     * Files are stored on the local application server and are deleted upon process completion.
     */
    LOCAL_AND_DELETE("Local and delete", "Files are stored on the local application server and are deleted upon process completion."),

    /**
     * Files are stored within the Bonita BDM database and are retained indefinitely.
     */
    BONITA("Bonita", "Files are stored within the Bonita BDM database and are retained indefinitely."),

    /**
     * Files are stored within the Bonita BDM database and are deleted upon process completion.
     */
    BONITA_AND_DELETE("Bonita and delete", "Files are stored within the Bonita BDM database and are deleted upon process completion.");

    private final String key;
    private final String description;

    /**
     * Private constructor for the enumeration.
     * @param key The technical key used for mapping.
     * @param description A human-readable description of the type.
     */
    ProcessStorageType(String key, String description) {
        this.key = key;
        this.description = description;
    }

    /**
     * Returns the unique string key associated with this storage type.
     * @return The storage type key (e.g., "Local").
     */
    public String getKey() {
        return key;
    }

    /**
     * Returns the business description of this storage type.
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
            ProcessStorageType.valueOf(input.trim().toUpperCase());
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
                ProcessStorageType::getKey, 
                ProcessStorageType::getDescription, 
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
            .map(ProcessStorageType::getKey)
            .collect(Collectors.toUnmodifiableList());
    }
}