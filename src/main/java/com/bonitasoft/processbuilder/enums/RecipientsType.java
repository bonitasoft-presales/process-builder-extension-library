package com.bonitasoft.processbuilder.enums;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Defines the valid recipient types for notification actions in process workflows.
 * This enumeration specifies how recipients are determined for email or notification actions.
 *
 * <p>Each type represents a different strategy for resolving the target recipients:</p>
 * <ul>
 *   <li>{@link #MEMBERSHIP} - Recipients based on organization membership</li>
 *   <li>{@link #USERS} - Specific user accounts</li>
 *   <li>{@link #STEP_USERS} - Users who executed a specific step</li>
 *   <li>{@link #STEP_MANAGERS} - Managers of users who executed a specific step</li>
 *   <li>{@link #SPECIFIC} - Explicitly specified email addresses</li>
 * </ul>
 *
 * @author Bonitasoft
 * @since 1.0
 */
public enum RecipientsType {

    /**
     * Recipients determined by organizational membership (group, role, or membership).
     */
    MEMBERSHIP("membership", "Recipients determined by organizational membership (group, role, or combined membership criteria)."),

    /**
     * Recipients specified as a list of user identifiers.
     */
    USERS("users", "Recipients specified as a list of user identifiers from the system."),

    /**
     * Recipients resolved from users who executed a specific process step.
     */
    STEP_USERS("step_users", "Recipients resolved dynamically from users who executed or completed a referenced process step."),

    /**
     * Recipients resolved as the managers of users who executed a specific step.
     */
    STEP_MANAGERS("step_managers", "Recipients resolved as the managers of users who executed a referenced process step."),

    /**
     * Recipients specified as explicit email addresses.
     */
    SPECIFIC("specific", "Recipients specified as explicit email addresses, independent of system users.");

    private final String key;
    private final String description;

    /**
     * Private constructor for the enumeration.
     *
     * @param key         The technical key used for JSON mapping.
     * @param description A human-readable description of the recipient type.
     */
    RecipientsType(String key, String description) {
        this.key = key;
        this.description = description;
    }

    /**
     * Returns the unique string key associated with this recipient type.
     *
     * @return The recipient type key (e.g., "step_users", "membership").
     */
    public String getKey() {
        return key;
    }

    /**
     * Returns the description of this recipient type.
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
        if (input == null || input.trim().isEmpty()) {
            return false;
        }
        try {
            RecipientsType.valueOf(input.trim().toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Finds a RecipientsType by its key value.
     *
     * @param key The key to search for (e.g., "step_users").
     * @return The matching RecipientsType, or {@code null} if not found.
     */
    public static RecipientsType fromKey(String key) {
        if (key == null || key.trim().isEmpty()) {
            return null;
        }
        String trimmedKey = key.trim();
        return Arrays.stream(values())
            .filter(type -> type.getKey().equalsIgnoreCase(trimmedKey))
            .findFirst()
            .orElse(null);
    }

    /**
     * Retrieves all recipient types as a read-only Map where the key is the technical key
     * and the value is the description.
     *
     * @return An unmodifiable map containing all recipient type data (Key -> Description).
     */
    public static Map<String, String> getAllData() {
        Map<String, String> data =
            Arrays.stream(values())
                .collect(Collectors.toMap(
                    RecipientsType::getKey,
                    RecipientsType::getDescription,
                    (oldValue, newValue) -> oldValue,
                    LinkedHashMap::new
                ));

        return Collections.unmodifiableMap(data);
    }

    /**
     * Retrieves all technical keys as a read-only List of Strings.
     *
     * @return An unmodifiable list containing all recipient type keys.
     */
    public static List<String> getAllKeysList() {
        return Arrays.stream(values())
            .map(RecipientsType::getKey)
            .collect(Collectors.toUnmodifiableList());
    }
}
