package com.bonitasoft.processbuilder.mapper;

import java.util.List;
import java.util.stream.Collectors;
import org.bonitasoft.engine.identity.User; // Importación específica de Bonita

/**
 * Utility class (Mapper) dedicated to converting and mapping lists of User entities
 * to common primitive or identifier lists. This class is designed to be extensible 
 * by adding more list mapping methods over time.
 * * It is marked as final as it contains only static utility methods and should not be instantiated 
 * or subclassed.
 */
public final class UserMapper {

    /**
     * Private constructor to prevent instantiation of the utility class.
     */
    private UserMapper() {
        // Utility class: no instantiation
    }

    /**
     * Converts a list of Bonita User objects into a list containing only their Long identifiers.
     * This method utilizes the Java Stream API for efficient and readable conversion.
     * @param users The list of User objects (org.bonitasoft.engine.identity.User) to process.
     * @return A new List of Longs, where each Long is the ID of a user from the input list.
     * Returns an empty list if the input list is null or empty.
     */
    public static List<Long> toLongIds(List<User> users) {
        if (users == null || users.isEmpty()) {
            return List.of(); // Returns an empty immutable list (Java 9+)
        }

        return users.stream()
                // Map each User object to its Long ID using the standard Bonita getId() method
                .map(User::getId)
                // Collect the resulting IDs into a List<Long>
                .collect(Collectors.toList());
    }

    // --- Extension Point ---
    /*
     * Future mapping methods (e.g., toStringNames, toUserDTOList, etc.) can be added here.
     */
}