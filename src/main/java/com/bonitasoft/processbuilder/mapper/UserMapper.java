package com.bonitasoft.processbuilder.mapper;

import java.util.List;
import java.util.stream.Collectors;
import org.bonitasoft.engine.identity.User;

/**
 * Utility class for converting and mapping collections of Bonita User entities
 * to various data structures such as identifier lists.
 * <p>
 * This mapper provides type-safe conversion methods that follow the Stream API
 * patterns for efficient and readable data transformations. It is designed to be
 * extensible by adding more mapping methods as needed.
 * </p>
 * <p>
 * This class is marked as {@code final} and contains only static utility methods,
 * following the utility class pattern. It should not be instantiated or subclassed.
 * </p>
 *
 * @author Bonitasoft
 * @since 1.0
 * @see org.bonitasoft.engine.identity.User
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
        // Separated null and empty checks to ensure mutation testing
        // can properly verify each condition is necessary
        if (users == null) {
            return List.of(); // Handles null input safely
        }
        if (users.isEmpty()) {
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