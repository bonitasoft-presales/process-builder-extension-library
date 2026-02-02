package com.bonitasoft.processbuilder.enums;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Defines the supported HTTP methods for REST service calls.
 *
 * @author Bonitasoft
 * @since 1.0
 */
public enum RestHttpMethod {

    /**
     * HTTP GET method for retrieving resources.
     */
    GET("GET", "Retrieve a resource", false),

    /**
     * HTTP POST method for creating resources.
     */
    POST("POST", "Create a new resource", true),

    /**
     * HTTP PUT method for replacing resources.
     */
    PUT("PUT", "Replace an existing resource", true),

    /**
     * HTTP PATCH method for partial updates.
     */
    PATCH("PATCH", "Partially update a resource", true),

    /**
     * HTTP DELETE method for removing resources.
     */
    DELETE("DELETE", "Delete a resource", false),

    /**
     * HTTP HEAD method for retrieving headers only.
     */
    HEAD("HEAD", "Retrieve headers only", false),

    /**
     * HTTP OPTIONS method for retrieving allowed methods.
     */
    OPTIONS("OPTIONS", "Retrieve allowed methods", false);

    private final String key;
    private final String description;
    private final boolean supportsBody;

    /**
     * Private constructor for the enumeration.
     *
     * @param key          The HTTP method name
     * @param description  A human-readable description
     * @param supportsBody Whether this method typically includes a request body
     */
    RestHttpMethod(String key, String description, boolean supportsBody) {
        this.key = key;
        this.description = description;
        this.supportsBody = supportsBody;
    }

    /**
     * Gets the HTTP method name.
     *
     * @return The method name (e.g., "GET", "POST")
     */
    public String getKey() {
        return key;
    }

    /**
     * Gets a brief description of the HTTP method.
     *
     * @return The description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Checks if this HTTP method typically supports a request body.
     *
     * @return {@code true} if the method supports a body
     */
    public boolean supportsBody() {
        return supportsBody;
    }

    /**
     * Checks if a given string corresponds to a valid HTTP method.
     *
     * @param input The string to validate (case-insensitive)
     * @return {@code true} if the string is a valid HTTP method
     */
    public static boolean isValid(String input) {
        if (input == null || input.trim().isEmpty()) {
            return false;
        }
        try {
            RestHttpMethod.valueOf(input.trim().toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Finds an HTTP method by its key (case-insensitive).
     *
     * @param key The key to search for
     * @return Optional containing the matching method, or empty if not found
     */
    public static Optional<RestHttpMethod> fromKey(String key) {
        if (key == null || key.trim().isEmpty()) {
            return Optional.empty();
        }
        try {
            return Optional.of(RestHttpMethod.valueOf(key.trim().toUpperCase()));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    /**
     * Retrieves all HTTP methods as a read-only Map.
     *
     * @return A map containing all method data (Key -> Description)
     */
    public static Map<String, String> getAllData() {
        Map<String, String> data = Arrays.stream(values())
                .collect(Collectors.toMap(
                        RestHttpMethod::getKey,
                        RestHttpMethod::getDescription,
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new
                ));
        return Collections.unmodifiableMap(data);
    }

    /**
     * Retrieves all method keys as a read-only List.
     *
     * @return A list containing all HTTP method names
     */
    public static List<String> getAllKeysList() {
        return Arrays.stream(values())
                .map(RestHttpMethod::getKey)
                .collect(Collectors.toUnmodifiableList());
    }
}
