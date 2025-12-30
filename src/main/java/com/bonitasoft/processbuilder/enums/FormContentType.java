package com.bonitasoft.processbuilder.enums;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Defines the valid content types for the {@code formContentType} field in {@code PBFormActionContent}.
 * <p>
 * This enumeration provides a type-safe way to reference the different content types
 * that can be associated with form actions, such as notifications, delays, alerts, and messages.
 * </p>
 *
 * @author Bonitasoft
 * @since 1.0
 */
public enum FormContentType {

    /**
     * Content type for notification-related form actions.
     * <p>
     * Used when the form action involves sending notifications to users or groups.
     * </p>
     */
    NOTIFICATIONS("notifications", "Content type for notification-related form actions."),

    /**
     * Content type for delay-related form actions.
     * <p>
     * Used when the form action involves scheduling or delaying an operation.
     * </p>
     */
    DELAY("delay", "Content type for delay or scheduled form actions."),

    /**
     * Content type for alert-related form actions.
     * <p>
     * Used when the form action involves displaying alerts or warnings to users.
     * </p>
     */
    ALERT("alert", "Content type for alert or warning form actions."),

    /**
     * Content type for message-related form actions.
     * <p>
     * Used when the form action involves sending or displaying messages.
     * </p>
     */
    MESSAGE("message", "Content type for message-related form actions.");

    private final String key;
    private final String description;

    /**
     * Private constructor for the enumeration.
     *
     * @param key         The technical key used for JSON mapping and serialization.
     * @param description A human-readable description of the content type.
     */
    FormContentType(String key, String description) {
        this.key = key;
        this.description = description;
    }

    /**
     * Returns the unique string key associated with this content type.
     *
     * @return The content type key (e.g., "notifications", "delay").
     */
    public String getKey() {
        return key;
    }

    /**
     * Returns the description of this content type.
     *
     * @return The human-readable description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Checks if a given string corresponds to a valid enum constant, ignoring case and leading/trailing spaces.
     *
     * @param input The string to validate.
     * @return {@code true} if the string is a valid enum constant name, {@code false} otherwise.
     */
    public static boolean isValid(String input) {
        // Separate null and empty checks for mutation testing coverage
        if (input == null) {
            return false;
        }
        if (input.trim().isEmpty()) {
            return false;
        }
        try {
            FormContentType.valueOf(input.trim().toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Retrieves all form content types as a read-only Map where the key is the technical key
     * and the value is the description.
     *
     * @return An unmodifiable map containing all content type data (Key -&gt; Description).
     */
    public static Map<String, String> getAllData() {
        Map<String, String> data = new LinkedHashMap<>();
        for (FormContentType type : values()) {
            data.put(type.getKey(), type.getDescription());
        }
        return Collections.unmodifiableMap(data);
    }

    /**
     * Retrieves all technical keys as a read-only List of Strings.
     *
     * @return An unmodifiable list containing all content type keys.
     */
    public static List<String> getAllKeysList() {
        return Arrays.stream(values())
            .map(FormContentType::getKey)
            .collect(Collectors.toUnmodifiableList());
    }
}
