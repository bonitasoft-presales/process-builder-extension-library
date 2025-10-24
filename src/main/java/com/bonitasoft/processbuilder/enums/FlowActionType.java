package com.bonitasoft.processbuilder.enums;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Defines the specific action types available for configuration within a process step,
 * such as defining a form, setting up notifications, or establishing redirections.
 *
 * @author Bonitasoft (Adapted)
 * @since 1.0
 */
public enum FlowActionType {

    /**
     * Action related to defining the interface or data capture form for a step.
     */
    FORM("form", "Form Action: Configures the form or UI presented to the user at a specific step."),

    /**
     * Action related to setting up alerts, emails, or system notifications.
     */
    NOTIFICATIONS("notifications", "Notifications Action: Manages configuration for sending alerts or communications."),

    /**
     * Action related to defining post-task navigation and flow redirection.
     */
    REDIRECTIONS("redirections", "Redirections Action: Defines where the user is sent after completing the task.");

    private final String key;
    private final String description;

    FlowActionType(String key, String description) {
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
     * Retrieves all flow action types as a read-only Map where the key is the technical key
     * and the value is the description.
     *
     * @return A map containing all flow action type data.
     */
    public static Map<String, String> getAllActionData() {
        Map<String, String> actionData = 
            java.util.Arrays.stream(FlowActionType.values())
            .collect(Collectors.toMap(
                FlowActionType::getKey,        
                FlowActionType::getDescription,  
                (oldValue, newValue) -> oldValue, 
                LinkedHashMap::new 
            ));
            
        // La inmutabilidad es clave, pero ya se hace el retorno inmutable.
        return Collections.unmodifiableMap(actionData);
    }
}