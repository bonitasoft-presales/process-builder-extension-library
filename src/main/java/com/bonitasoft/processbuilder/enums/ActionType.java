package com.bonitasoft.processbuilder.enums;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Represents the type of action to be performed on a business object.
 * <p>
 * This enum defines the valid actions (INSERT, UPDATE, DELETE) and provides
 * validation logic for each based on the presence and format of a persistence ID.
 * This approach centralizes business rules, making the code more robust and easier
 * to maintain.
 * </p>
 */
public enum ActionType {
    /**
     * Represents the creation of a new business object.
     * <p>
     * For an INSERT action, a valid persistence ID string must be empty.
     * </p>
     */
    INSERT("Insert", "Represents the creation of a new business object (ID must be empty).") {
        @Override
        public boolean isValid(String persistenceId) {
            return persistenceId.trim().isEmpty();
        }
    },
    
    /**
     * Represents the modification of an existing business object.
     * <p>
     * For an UPDATE action, a valid persistence ID string must be non-empty
     * and contain only digits.
     * </p>
     */
    UPDATE("Update", "Represents the modification of an existing business object (requires a numeric ID).") {
        @Override
        public boolean isValid(String persistenceId) {
            return !persistenceId.trim().isEmpty() && persistenceId.matches("\\d+");
        }
    },
    
    /**
     * Represents the deletion of an existing business object.
     * <p>
     * For a DELETE action, a valid persistence ID string must be non-empty
     * and contain only digits.
     * </p>
     */
    DELETE("Delete", "Represents the deletion of an existing business object (requires a numeric ID).")  {
        @Override
        public boolean isValid(String persistenceId) {
            return !persistenceId.trim().isEmpty() && persistenceId.matches("\\d+");
        }
    };
    
    private final String key;
    private final String description;

    /**
     * Private constructor for the enumeration.
     * @param key The technical key used for mapping (e.g., "Insert").
     * @param description A human-readable description of the type.
     */
    ActionType(String key, String description) {
        this.key = key;
        this.description = description;
    }

    /**
     * Returns the unique string key associated with this criticality type.
     * @return The criticality key (e.g., "High").
     */
    public String getKey() {
        return key;
    }

    /**
     * Returns the business description of this criticality type.
     * @return The description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Validates a persistence ID string against the requirements of the specific action type.
     *
     * @param persistenceId The string representation of the business object's persistence ID.
     * @return {@code true} if the persistence ID is valid for the current action type, otherwise {@code false}.
     */
    public abstract boolean isValid(String persistenceId);
    
    /**
     * Retrieves all process instance states as a read-only Map where the key is the technical key 
     * and the value is the description.
     * @return A map containing all process state data (Key -> Description).
     */
    public static Map<String, String> getAllData() {
        Map<String, String> stateData = 
            Arrays.stream(ActionType.values())
            .collect(Collectors.toMap(
                ActionType::getKey, 
                ActionType::getDescription, 
                (oldValue, newValue) -> oldValue, 
                LinkedHashMap::new 
            ));
        
        return Collections.unmodifiableMap(stateData);
    }
    
    /**
     * Retrieves all technical keys as a read-only List of Strings.
     * @return A list containing all technical keys.
     */
    public static List<String> getAllKeysList() {
        return Arrays.stream(ActionType.values())
            .map(ActionType::getKey)
            .collect(Collectors.toUnmodifiableList());
    }
}