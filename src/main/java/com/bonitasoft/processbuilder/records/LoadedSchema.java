package com.bonitasoft.processbuilder.records;

import java.util.Collections;
import java.util.Map;

import com.github.fge.jsonschema.main.JsonSchema;

/**
 * Record representing a fully loaded and prepared JSON Schema used for validation.
 * <p>
 * This record holds the executable schema validator, a map of component titles for 
 * error reporting, the target schema's name, and the raw JSON input.
 * </p>
 * @param validator The executable JSON Schema object used for validation.
 * @param titles The map associating internal pointers (e.g., /allOf/0) to user-friendly component names.
 * @param targetSchemaName The name of the schema component being validated (e.g., "Category").
 * @param jsonInput The raw JSON content string being validated.
 *
 * @author [Your Name or Company Name]
 * @since 1.0
 */
public record LoadedSchema (
    JsonSchema validator,
    Map<String, String> titles,
    String targetSchemaName,
    String jsonInput
) {
    /**
     * Compact Canonical Constructor.
     * <p>
     * Implements defensive copying for the mutable {@code titles} map to ensure 
     * the record's immutability upon construction (corrects {@code EI_EXPOSE_REP2} in the constructor).
     * </p>
     */
    public LoadedSchema {
        // Defensive Copy: Creates an internal, immutable copy of the input map.
        // If the map is mutated externally after instantiation, the record's state remains safe.
        titles = Map.copyOf(titles); 
    }

    /**
     * Accessor method for the map of titles.
     * <p>
     * Overrides the default accessor to return an unmodifiable view of the internal 
     * map, preventing external code from mutating the record's internal state 
     * (corrects {@code EI_EXPOSE_REP2} in the getter).
     * </p>
     * @return An unmodifiable view of the component titles map.
     */
    @Override
    public Map<String, String> titles() {
        return Collections.unmodifiableMap(titles);
    }
}