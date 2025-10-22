package com.bonitasoft.processbuilder.extension;

import java.util.Set;

import com.bonitasoft.processbuilder.enums.ProcessStorageType;

/**
 * Utility methods for handling process storage logic based purely on storage name strings.
 *
 * This implementation relies directly on the ProcessStorageType enum for comparison logic,
 * ensuring consistency with the master data keys.
 *
 * @author Bonitasoft
 * @since 1.0
 */
public class StorageUtils {
    
    private static final Set<String> BONITA_STORAGE_KEYS = Set.of(
        PBStringUtils.toUpperSnakeCase(ProcessStorageType.BONITA.getKey()), 
        PBStringUtils.toUpperSnakeCase(ProcessStorageType.BONITA_AND_DELETE.getKey())
    );

    private static final Set<String> LOCAL_STORAGE_KEYS = Set.of(
        PBStringUtils.toUpperSnakeCase(ProcessStorageType.LOCAL.getKey()),
        PBStringUtils.toUpperSnakeCase(ProcessStorageType.LOCAL_AND_DELETE.getKey())
    );

    private StorageUtils() {
        // Private constructor to prevent instantiation of a utility class
    }

    /**
     * Determines whether the process documents are configured for storage in the Bonita BDM database.
     * This is true if the storage type key is "Bonita" or "Bonita and delete".
     *
     * @param storageKey The storage key (e.g., "Bonita", "Local and delete").
     * @return {@code true} if the storage is defined to use the Bonita DB, {@code false} otherwise.
     */
    public static boolean isBonitaStorage(String storageKey) {
        if (storageKey == null) {
            return false;
        }
        return BONITA_STORAGE_KEYS.contains(PBStringUtils.toUpperSnakeCase(storageKey));
    }
    
    /**
     * Determines whether the process documents are configured for storage on the local application server.
     * This is true if the storage type key is "Local" or "Local and delete".
     *
     * @param storageKey The storage key (e.g., "Bonita", "Local and delete").
     * @return {@code true} if the storage is defined to use the local server, {@code false} otherwise.
     */
    public static boolean isLocalStorage(String storageKey) {
        if (storageKey == null) {
            return false;
        }
        return LOCAL_STORAGE_KEYS.contains(PBStringUtils.toUpperSnakeCase(storageKey));
    }
}
