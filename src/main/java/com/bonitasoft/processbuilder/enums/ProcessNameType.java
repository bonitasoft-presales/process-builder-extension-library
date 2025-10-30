package com.bonitasoft.processbuilder.enums;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Defines the names of the configurable process definitions.
 *
 * @author Bonitasoft
 * @since 1.0
 */

public enum ProcessNameType {

    /**
     * Process definition for Form configuration.
     */
    FORM("Form", "Process for form configuration settings."),

    /**
     * Process definition for Notifications configuration.
     */
    NOTIFICATIONS("Notifications", "Process for notifications configuration settings."),

    /**
     * Process definition for Redirections configuration.
     */
    REDIRECTIONS("Redirections", "Process for redirection configuration settings.");

    private final String key;
    private final String description;

    ProcessNameType(String key, String description) {
        this.key = key;
        this.description = description;
    }

    /**
     * Gets the human-readable, capitalized key of the process.
     *
     * @return The process key.
     */
    public String getKey() {
        return key;
    }

    /**
     * Gets a brief description of the process purpose.
     *
     * @return The process description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Retrieves all process names as a read-only Map where the key is the process name
     * (e.g., "Form") and the value is its description.
     *
     * @return A map containing all process name data.
     */
    public static Map<String, String> getAllProcessData() {
        Map<String, String> actionData = 
            java.util.Arrays.stream(ProcessNameType.values())
            .collect(Collectors.toMap(
                ProcessNameType::getKey,        
                ProcessNameType::getDescription,  
                (oldValue, newValue) -> oldValue, 
                LinkedHashMap::new 
            ));
            
        // La inmutabilidad es clave, pero ya se hace el retorno inmutable.
        return Collections.unmodifiableMap(actionData);
    }
}