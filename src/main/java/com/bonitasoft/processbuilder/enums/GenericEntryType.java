package com.bonitasoft.processbuilder.enums;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Defines the valid types used to classify generic configuration entries 
 * (master data or lookup records) within the system.
 * <p>
 * This enumeration ensures type safety and restricts the classification 
 * of generic entries to a predefined set of metadata keys, crucial for 
 * maintaining consistency in system configuration objects managed by a process.
 * </p>
 *
 * @author Bonitasoft
 * @since 1.0
 */
public enum GenericEntryType {
    /**
     * Represents the classification key for **Process Storage** definitions.
     * This type is used to identify and retrieve master data records that 
     * define where process documents and files should be persisted (e.g., local server, BDM database, with retention or deletion).
     */
    PROCESS_STORAGE("ProcessStorage", "Defines the storage location and retention policy for process documents."),

    /**
     * Represents the classification key for **Process Criticality** definitions.
     * This type is used to identify and retrieve master data records that
     * define the business impact or priority level of a process (e.g., Low, Medium, High).
     */
    CRITICALITY("Criticality", "Defines the business priority level (e.g., High, Medium, Low) of the process."),

    /**
     * Represents the classification key for **Documents Folder** definitions.
     * This type is used to identify and retrieve master data records that
     * define the directory paths where process documents should be stored.
     * Multiple folder configurations can be defined and selected per PBProcess.
     */
    DOCUMENTS_FOLDER("DocumentsFolder", "Defines the directory paths for storing process documents.");

    private final String key;
    private final String description;

    /**
     * Private constructor for the enumeration.
     * @param key The technical key used for mapping.
     * @param description A human-readable description of the type.
     */
    GenericEntryType(String key, String description) {
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
            GenericEntryType.valueOf(input.trim().toUpperCase());
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
                GenericEntryType::getKey, 
                GenericEntryType::getDescription, 
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
            .map(GenericEntryType::getKey)
            .collect(Collectors.toUnmodifiableList());
    }
}