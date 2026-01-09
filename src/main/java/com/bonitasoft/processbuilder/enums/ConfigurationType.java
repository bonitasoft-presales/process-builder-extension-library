package com.bonitasoft.processbuilder.enums;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Defines the valid types used to classify generic configuration entries 
 * (master data or lookup records) within the system.
 * <p>
 * This enumeration ensures type safety and restricts the classification 
 * of generic entries to a predefined set of metadata keys, crucial for 
 * maintaining consistency in system configuration objects managed by a process.
 * </p>
 *
 * @author Bonitasoft
 * @since 1.0
 */
public enum ConfigurationType {
    
    /**
     * Represents the classification key for **SMTP Configuration** definitions.
     * This type is used to identify and retrieve master data records that 
     * define the email server settings required for sending notifications 
     * (e.g., host, port, SSL/TLS, authentication credentials).
     */
    SMTP("Smtp",
            "Defines the SMTP server configuration settings for email notifications, "
                    + "including host address, port number, SSL/TLS encryption, and authentication parameters."),

    /**
     * Represents the classification key for **Process Execution Connector** definitions.
     * This type is used to identify and retrieve master data records that
     * define the connector settings for process execution operations.
     */
    PROC_EXECUTION_CONNECTOR("ProcExecutionConnector", "Defines the process execution connector configuration for automated workflow integrations."),

    /**
     * Represents the classification key for **Theme Configuration** definitions.
     * This type is used to identify and retrieve master data records that
     * define the visual appearance and branding of the application
     * (e.g., colors, logos, fonts, styles).
     */
    THEME("Theme",
            "Defines the visual theme configuration for the application, including primary and secondary colors, "
                    + "logo, typography, and other branding elements."),

    /**
     * Represents the classification key for **Generic Configuration** definitions.
     * <p>
     * This type is used to identify and retrieve master data records that define
     * system-wide generic settings such as language preferences and application host URL.
     * Generic configurations are stored in PBConfiguration and referenced by {@link GenericType}.
     * </p>
     */
    GENERIC("Generic",
            "Defines generic system-wide configuration settings such as language preferences (lang) "
                    + "and application host URL (host) for notifications and external links.");

    private final String key;
    private final String description;

    /**
     * Private constructor for the enumeration.
     * @param key The technical key used for mapping.
     * @param description A human-readable description of the type.
     */
    ConfigurationType(String key, String description) {
        this.key = key;
        this.description = description;
    }

    /**
     * Gets the technical key of the flow action type, typically used for internal logic and data mapping.
     *
     * @return The technical key (lowercase).
     */
    public String getKey() {
        return key;
    }

    /**
     * Gets a brief business description of the action type.
     *
     * @return The description for the user interface or documentation.
     */
    public String getDescription() {
        return description;
    }


    /**
     * Checks if a given string corresponds to a valid enum constant, ignoring case and leading/trailing spaces.
     *
     * @param input The string to validate.
     * @return {@code true} if the string is a valid enum constant, {@code false} otherwise.
     */
    public static boolean isValid(String input) {
        if (input == null) {
            return false;
        }
        if (input.trim().isEmpty()) {
            return false;
        }
        try {
            ConfigurationType.valueOf(input.trim().toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Retrieves all configuration types as a read-only Map where the key is the technical key
     * and the value is the description.
     *
     * @return An unmodifiable map containing all configuration type data (Key -&gt; Description).
     */
    public static Map<String, String> getAllData() {
        Map<String, String> data = new LinkedHashMap<>();
        for (ConfigurationType type : values()) {
            data.put(type.getKey(), type.getDescription());
        }
        return Collections.unmodifiableMap(data);
    }
    
    /**
     * Retrieves all technical keys as a read-only List of Strings.
     * @return A list containing all technical keys.
     */
    public static List<String> getAllKeysList() {
        return Arrays.stream(values())
            .map(ConfigurationType::getKey)
            .collect(Collectors.toUnmodifiableList());
    }
}