package com.bonitasoft.processbuilder.enums;


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
    PROCESS_STORAGE, 

    /**
     * Represents the classification key for **Process Criticality** definitions.
     * This type is used to identify and retrieve master data records that 
     * define the business impact or priority level of a process (e.g., Low, Medium, High).
     */
    CRITICALITY;

    // Optional: Add a utility method to validate existence if needed
    /**
     * Checks if the given string value matches any defined enum constant name.
     * * @param name The string name to check.
     * @return true if the name matches a constant, false otherwise.
     */
    public static boolean isValid(String name) {
        if (name == null) {
            return false;
        }
        for (GenericEntryType type : GenericEntryType.values()) {
            if (type.name().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }
}