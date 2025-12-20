package com.bonitasoft.processbuilder.enums;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Defines the valid parameter keys for ActionContent.parameters configuration.
 * This enumeration provides a type-safe way to reference action parameters
 * used in process step actions, such as notifications, redirections, and assignments.
 *
 * @author Bonitasoft
 * @since 1.0
 */
public enum ActionParameterType {

    /**
     * The name identifier for the action parameter.
     */
    NAME("name", "The name identifier for the action or notification."),

    /**
     * The target step reference for redirection or flow actions.
     */
    TARGET_STEP("targetStep", "The reference identifier of the target step for redirection or flow control actions."),

    /**
     * The recipients configuration object for notification actions.
     */
    RECIPIENTS("recipients", "The recipients configuration object containing notification target details."),

    /**
     * The type of recipients selection (e.g., static, dynamic, step-based).
     */
    RECIPIENTS_TYPE("recipients.type", "The type of recipients selection strategy (e.g., 'static', 'dynamic', 'fromStep')."),

    /**
     * The step identifier used to resolve dynamic recipients.
     */
    RECIPIENTS_STEP_ID("recipients.stepId", "The step reference identifier used to dynamically resolve recipients from a previous step."),

    /**
     * The message body content for notification actions.
     */
    MESSAGE("message", "The message body content for email or notification actions. Supports template variables."),

    /**
     * The subject line for email notification actions.
     */
    SUBJECT("subject", "The subject line for email notifications. Supports template variables."),

    /**
     * The list of specific email addresses for notification recipients.
     */
    RECIPIENTS_SPECIFIC_EMAILS("recipients.specificEmails", "The list of specific email addresses for direct notification delivery."),

    /**
     * The list of user identifiers for notification recipients.
     */
    RECIPIENTS_USER_IDS("recipients.userIds", "The list of user identifiers to resolve as notification recipients."),

    /**
     * The list of membership identifiers for notification recipients.
     */
    RECIPIENTS_MEMBERSHIP_IDS("recipients.membershipIds",
            "The list of membership identifiers (group/role combinations) to resolve as notification recipients.");

    private final String key;
    private final String description;

    /**
     * Private constructor for the enumeration.
     *
     * @param key         The technical key used for JSON mapping.
     * @param description A human-readable description of the parameter.
     */
    ActionParameterType(String key, String description) {
        this.key = key;
        this.description = description;
    }

    /**
     * Returns the unique string key associated with this action parameter.
     *
     * @return The parameter key (e.g., "targetStep", "recipients.type").
     */
    public String getKey() {
        return key;
    }

    /**
     * Returns the description of this action parameter.
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
            ActionParameterType.valueOf(input.trim().toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Retrieves all action parameters as a read-only Map where the key is the technical key
     * and the value is the description.
     *
     * @return An unmodifiable map containing all parameter data (Key -> Description).
     */
    public static Map<String, String> getAllData() {
        Map<String, String> data =
            Arrays.stream(values())
                .collect(Collectors.toMap(
                    ActionParameterType::getKey,
                    ActionParameterType::getDescription,
                    (oldValue, newValue) -> oldValue,
                    LinkedHashMap::new
                ));

        return Collections.unmodifiableMap(data);
    }

    /**
     * Retrieves all technical keys as a read-only List of Strings.
     *
     * @return An unmodifiable list containing all parameter keys.
     */
    public static List<String> getAllKeysList() {
        return Arrays.stream(values())
            .map(ActionParameterType::getKey)
            .collect(Collectors.toUnmodifiableList());
    }
}
