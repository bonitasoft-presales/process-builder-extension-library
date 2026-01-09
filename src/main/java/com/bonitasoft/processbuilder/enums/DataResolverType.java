package com.bonitasoft.processbuilder.enums;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Defines the valid data resolver types used to resolve dynamic placeholders in templates.
 * <p>
 * This enumeration provides a type-safe way to reference the different types of data
 * that can be resolved dynamically in notifications, emails, and other templated content.
 * Each type corresponds to a specific data source such as recipient information,
 * task links, or step status.
 * </p>
 *
 * @author Bonitasoft
 * @since 1.0
 */
public enum DataResolverType {

    /**
     * Resolves to the first name of the recipient user.
     * <p>
     * Retrieved from the Bonita Identity API using the user ID of the pending human task.
     * </p>
     */
    RECIPIENT_FIRSTNAME("recipient_firstname",
            "Resolves to the first name of the task recipient from the Identity API."),

    /**
     * Resolves to the last name of the recipient user.
     * <p>
     * Retrieved from the Bonita Identity API using the user ID of the pending human task.
     * </p>
     */
    RECIPIENT_LASTNAME("recipient_lastname",
            "Resolves to the last name of the task recipient from the Identity API."),

    /**
     * Resolves to the email address of the recipient user.
     * <p>
     * Retrieved from the Bonita Identity API contact data using the user ID of the pending human task.
     * </p>
     */
    RECIPIENT_EMAIL("recipient_email",
            "Resolves to the email address of the task recipient from the Identity API contact data."),

    /**
     * Resolves to a clickable HTML link to the task.
     * <p>
     * Generates an HTML anchor tag with the task URL based on the configured host
     * and the human task ID.
     * </p>
     */
    TASK_LINK("task_link",
            "Resolves to an HTML anchor link to the task using the configured host URL and task ID."),

    /**
     * Resolves to the username assigned to a specific step.
     * <p>
     * Retrieved from the PBStepProcessInstance data based on the root process instance
     * and reference step.
     * </p>
     */
    STEP_USER_NAME("step_user_name",
            "Resolves to the username assigned to the referenced step from process instance data."),

    /**
     * Resolves to the current status of a specific step.
     * <p>
     * Retrieved from the PBStepProcessInstance data based on the root process instance
     * and reference step.
     * </p>
     */
    STEP_STATUS("step_status",
            "Resolves to the current status of the referenced step from process instance data.");

    private final String key;
    private final String description;

    /**
     * Private constructor for the enumeration.
     *
     * @param key         The technical key used for template placeholder matching.
     * @param description A human-readable description of the resolver type.
     */
    DataResolverType(String key, String description) {
        this.key = key;
        this.description = description;
    }

    /**
     * Returns the unique string key associated with this resolver type.
     * <p>
     * This key is used to match placeholders in templates (e.g., "recipient_firstname").
     * </p>
     *
     * @return The resolver key.
     */
    public String getKey() {
        return key;
    }

    /**
     * Returns the description of this resolver type.
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
            DataResolverType.valueOf(input.trim().toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Checks if a given key corresponds to a valid resolver type.
     * <p>
     * Unlike {@link #isValid(String)}, this method matches against the technical key
     * (e.g., "recipient_firstname") rather than the enum constant name.
     * </p>
     *
     * @param key The key to validate.
     * @return {@code true} if the key matches a resolver type, {@code false} otherwise.
     */
    public static boolean isValidKey(String key) {
        if (key == null) {
            return false;
        }
        String trimmedKey = key.trim();
        if (trimmedKey.isEmpty()) {
            return false;
        }
        for (DataResolverType type : values()) {
            if (type.getKey().equals(trimmedKey)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Finds a DataResolverType by its key.
     *
     * @param key The technical key to search for.
     * @return The matching DataResolverType, or {@code null} if not found.
     */
    public static DataResolverType fromKey(String key) {
        if (key == null) {
            return null;
        }
        String trimmedKey = key.trim();
        for (DataResolverType type : values()) {
            if (type.getKey().equals(trimmedKey)) {
                return type;
            }
        }
        return null;
    }

    /**
     * Retrieves all resolver types as a read-only Map where the key is the technical key
     * and the value is the description.
     *
     * @return An unmodifiable map containing all resolver type data (Key -&gt; Description).
     */
    public static Map<String, String> getAllData() {
        Map<String, String> data = new LinkedHashMap<>();
        for (DataResolverType type : values()) {
            data.put(type.getKey(), type.getDescription());
        }
        return Collections.unmodifiableMap(data);
    }

    /**
     * Retrieves all technical keys as a read-only List of Strings.
     *
     * @return An unmodifiable list containing all resolver type keys.
     */
    public static List<String> getAllKeysList() {
        return List.of(
            RECIPIENT_FIRSTNAME.getKey(),
            RECIPIENT_LASTNAME.getKey(),
            RECIPIENT_EMAIL.getKey(),
            TASK_LINK.getKey(),
            STEP_USER_NAME.getKey(),
            STEP_STATUS.getKey()
        );
    }
}
