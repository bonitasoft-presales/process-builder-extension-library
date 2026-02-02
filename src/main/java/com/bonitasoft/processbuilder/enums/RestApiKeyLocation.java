package com.bonitasoft.processbuilder.enums;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Defines where an API key should be placed in REST requests.
 *
 * @author Bonitasoft
 * @since 1.0
 */
public enum RestApiKeyLocation {

    /**
     * API key is sent as an HTTP header.
     */
    HEADER("header", "API key sent as HTTP header"),

    /**
     * API key is sent as a query parameter in the URL.
     */
    QUERY_PARAM("queryParam", "API key sent as URL query parameter");

    private final String key;
    private final String description;

    /**
     * Private constructor for the enumeration.
     *
     * @param key         The technical key
     * @param description A human-readable description
     */
    RestApiKeyLocation(String key, String description) {
        this.key = key;
        this.description = description;
    }

    /**
     * Gets the technical key.
     *
     * @return The key
     */
    public String getKey() {
        return key;
    }

    /**
     * Gets the description.
     *
     * @return The description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Checks if a given string corresponds to a valid location.
     *
     * @param input The string to validate (case-insensitive)
     * @return {@code true} if the string is valid
     */
    public static boolean isValid(String input) {
        if (input == null || input.trim().isEmpty()) {
            return false;
        }
        try {
            RestApiKeyLocation.valueOf(input.trim().toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return fromKey(input).isPresent();
        }
    }

    /**
     * Finds a location by its key (case-insensitive).
     *
     * @param key The key to search for
     * @return Optional containing the matching location, or empty if not found
     */
    public static Optional<RestApiKeyLocation> fromKey(String key) {
        if (key == null || key.trim().isEmpty()) {
            return Optional.empty();
        }
        String normalizedKey = key.trim().toLowerCase();
        return Arrays.stream(values())
                .filter(loc -> loc.getKey().equalsIgnoreCase(normalizedKey))
                .findFirst();
    }

    /**
     * Retrieves all locations as a read-only Map.
     *
     * @return A map containing all location data (Key -> Description)
     */
    public static Map<String, String> getAllData() {
        Map<String, String> data = Arrays.stream(values())
                .collect(Collectors.toMap(
                        RestApiKeyLocation::getKey,
                        RestApiKeyLocation::getDescription,
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new
                ));
        return Collections.unmodifiableMap(data);
    }

    /**
     * Retrieves all keys as a read-only List.
     *
     * @return A list containing all location keys
     */
    public static List<String> getAllKeysList() {
        return Arrays.stream(values())
                .map(RestApiKeyLocation::getKey)
                .collect(Collectors.toUnmodifiableList());
    }
}
