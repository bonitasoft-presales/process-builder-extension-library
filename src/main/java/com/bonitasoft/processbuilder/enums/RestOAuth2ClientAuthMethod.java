package com.bonitasoft.processbuilder.enums;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Defines how OAuth2 client credentials are sent to the token endpoint.
 *
 * @author Bonitasoft
 * @since 1.0
 */
public enum RestOAuth2ClientAuthMethod {

    /**
     * Client credentials sent in the request body as form parameters.
     */
    BODY("body", "Credentials sent in request body (client_id, client_secret as form params)"),

    /**
     * Client credentials sent as HTTP Basic Auth header.
     */
    HEADER("header", "Credentials sent as Basic Auth header");

    private final String key;
    private final String description;

    /**
     * Private constructor for the enumeration.
     *
     * @param key         The technical key
     * @param description A human-readable description
     */
    RestOAuth2ClientAuthMethod(String key, String description) {
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
     * Checks if a given string corresponds to a valid method.
     *
     * @param input The string to validate (case-insensitive)
     * @return {@code true} if the string is valid
     */
    public static boolean isValid(String input) {
        if (input == null || input.trim().isEmpty()) {
            return false;
        }
        try {
            RestOAuth2ClientAuthMethod.valueOf(input.trim().toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return fromKey(input).isPresent();
        }
    }

    /**
     * Finds a method by its key (case-insensitive).
     *
     * @param key The key to search for
     * @return Optional containing the matching method, or empty if not found
     */
    public static Optional<RestOAuth2ClientAuthMethod> fromKey(String key) {
        if (key == null || key.trim().isEmpty()) {
            return Optional.empty();
        }
        String normalizedKey = key.trim().toLowerCase();
        return Arrays.stream(values())
                .filter(method -> method.getKey().equalsIgnoreCase(normalizedKey))
                .findFirst();
    }

    /**
     * Retrieves all methods as a read-only Map.
     *
     * @return A map containing all method data (Key -> Description)
     */
    public static Map<String, String> getAllData() {
        Map<String, String> data = Arrays.stream(values())
                .collect(Collectors.toMap(
                        RestOAuth2ClientAuthMethod::getKey,
                        RestOAuth2ClientAuthMethod::getDescription,
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new
                ));
        return Collections.unmodifiableMap(data);
    }

    /**
     * Retrieves all keys as a read-only List.
     *
     * @return A list containing all method keys
     */
    public static List<String> getAllKeysList() {
        return Arrays.stream(values())
                .map(RestOAuth2ClientAuthMethod::getKey)
                .collect(Collectors.toUnmodifiableList());
    }
}
