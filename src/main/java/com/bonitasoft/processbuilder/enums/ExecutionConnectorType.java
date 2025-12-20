package com.bonitasoft.processbuilder.enums;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Defines the valid configuration attributes for the ExecutionConnector process.
 * This enumeration contains parameters for controlling retry behavior and timing
 * in connector execution scenarios.
 *
 * @author Bonitasoft
 * @since 1.0
 */
public enum ExecutionConnectorType {

    /**
     * The maximum number of retry attempts allowed for the connector execution.
     */
    MAX_RETRIES("maxRetries", "Maximum number of retries. Upper limit for the retry count."),

    /**
     * The waiting time in seconds before each retry attempt.
     */
    WAIT_TIME_SECONDS("waitTimeSeconds", "Waiting time in seconds. Duration of the pause before each retry attempt.");

    private final String key;
    private final String description;

    /**
     * Private constructor for the enumeration.
     * @param key The technical key used for mapping.
     * @param description A human-readable description of the attribute.
     */
    ExecutionConnectorType(String key, String description) {
        this.key = key;
        this.description = description;
    }

    /**
     * Returns the unique string key associated with this attribute.
     * @return The attribute key (e.g., "maxRetries").
     */
    public String getKey() {
        return key;
    }

    /**
     * Returns the description of this configuration attribute.
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
            ExecutionConnectorType.valueOf(input.trim().toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Retrieves all ExecutionConnector configuration attributes as a read-only Map where the key is the technical key
     * and the value is the description.
     * @return A map containing all attribute data (Key -> Description).
     */
    public static Map<String, String> getAllData() {
        Map<String, String> data =
            Arrays.stream(values())
            .collect(Collectors.toMap(
                ExecutionConnectorType::getKey,
                ExecutionConnectorType::getDescription,
                (oldValue, newValue) -> oldValue,
                LinkedHashMap::new
            ));

        return Collections.unmodifiableMap(data);
    }

    /**
     * Retrieves all technical keys as a read-only List of Strings.
     * @return A list containing all attribute keys.
     */
    public static List<String> getAllKeysList() {
        return Arrays.stream(values())
            .map(ExecutionConnectorType::getKey)
            .collect(Collectors.toUnmodifiableList());
    }
}
