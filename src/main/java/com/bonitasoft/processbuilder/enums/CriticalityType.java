package com.bonitasoft.processbuilder.enums;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Defines the valid criticality levels for a business process, along with their business descriptions.
 * This enumeration is typically used as the source for the 'Criticality' master data.
 *
 * @author Bonitasoft
 * @since 1.0
 */
public enum CriticalityType {

    /**
     * Indicates severe business impact, requiring immediate attention and maximum uptime assurance.
     */
    HIGH("High", "Indicates severe business impact, requiring immediate attention and maximum uptime assurance."),

    /**
     * Indicates significant business impact, requiring standard attention and prompt resolution.
     */
    MODERATE("Moderate", "Indicates significant business impact, requiring standard attention and prompt resolution."),

    /**
     * Indicates minimal business impact, usually involving non-urgent or supportive functions.
     */
    LOW("Low", "Indicates minimal business impact, usually involving non-urgent or supportive functions."),

    /**
     * Indicates a purely informational or non-critical process with no business impact on failure.
     */
    NONE("None", "Indicates a purely informational or non-critical process with no business impact on failure.");

    private final String key;
    private final String description;

    CriticalityType(String key, String description) {
        this.key = key;
        this.description = description;
    }

    public String getKey() {
        return key;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Retrieves all criticality types as a read-only Map where the key is the criticality key 
     * (e.g., "High") and the value is the description.
     * * @return A map containing all criticality type data.
     */
    public static Map<String, String> getAllCriticalityData() {
        Map<String, String> criticalyData = new LinkedHashMap<>();
        for (CriticalityType type : values()) {
            criticalyData.put(type.getKey(), type.getDescription());
        }
        return Collections.unmodifiableMap(criticalyData);
    }
}