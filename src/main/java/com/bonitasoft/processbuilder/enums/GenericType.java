package com.bonitasoft.processbuilder.enums;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Defines the valid generic configuration types for system-wide settings.
 * <p>
 * This enumeration provides a type-safe way to reference generic configuration
 * parameters that are stored in PBConfiguration with type "Generic". These are
 * typically system-level settings that don't fit into other specific categories.
 * </p>
 *
 * @author Bonitasoft
 * @since 1.0
 */
public enum GenericType {

    /**
     * Language configuration for the application.
     * <p>
     * Stores the default language code (e.g., "en", "es", "fr") used for
     * internationalization and localization throughout the application.
     * </p>
     */
    LANG("lang", "Application language setting for internationalization (e.g., en, es, fr)."),

    /**
     * Host URL configuration for the application.
     * <p>
     * Stores the base URL of the application host, used for generating
     * absolute links in notifications, emails, and other external communications.
     * </p>
     */
    HOST("host", "Base URL of the application host for generating absolute links in notifications and emails.");

    private final String key;
    private final String description;

    /**
     * Private constructor for the enumeration.
     *
     * @param key         The technical key used for JSON mapping and database storage.
     * @param description A human-readable description of the configuration type.
     */
    GenericType(String key, String description) {
        this.key = key;
        this.description = description;
    }

    /**
     * Returns the unique string key associated with this generic type.
     *
     * @return The configuration key (e.g., "lang", "host").
     */
    public String getKey() {
        return key;
    }

    /**
     * Returns the description of this generic type.
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
        if (input == null) {
            return false;
        }
        if (input.trim().isEmpty()) {
            return false;
        }
        try {
            GenericType.valueOf(input.trim().toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Retrieves all generic types as a read-only Map where the key is the technical key
     * and the value is the description.
     *
     * @return An unmodifiable map containing all generic type data (Key -&gt; Description).
     */
    public static Map<String, String> getAllData() {
        Map<String, String> data = new LinkedHashMap<>();
        for (GenericType type : values()) {
            data.put(type.getKey(), type.getDescription());
        }
        return Collections.unmodifiableMap(data);
    }

    /**
     * Retrieves all technical keys as a read-only List of Strings.
     *
     * @return An unmodifiable list containing all generic type keys.
     */
    public static List<String> getAllKeysList() {
        return List.of(
            LANG.getKey(),
            HOST.getKey()
        );
    }
}
