package com.bonitasoft.processbuilder.enums;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

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

    ProcessStorageType(String key, String description) {
        this.key = key;
        this.description = description;
    }

    public String getKey() {
        return key;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Retrieves all storage types as a read-only Map where the key is the storage key 
     * (e.g., "Local") and the value is the description.
     * * @return A map containing all storage type data.
     */
    public static Map<String, String> getAllStorageData() {
        Map<String, String> storageData = new LinkedHashMap<>();
        for (ProcessStorageType type : values()) {
            storageData.put(type.getKey(), type.getDescription());
        }
        return Collections.unmodifiableMap(storageData);
    }
}