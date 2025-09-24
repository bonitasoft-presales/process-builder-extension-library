package com.bonitasoft.processbuilder.enums;

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
    INSERT {
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
    UPDATE {
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
    DELETE {
        @Override
        public boolean isValid(String persistenceId) {
            return !persistenceId.trim().isEmpty() && persistenceId.matches("\\d+");
        }
    };
    
    /**
     * Validates a persistence ID string against the requirements of the specific action type.
     *
     * @param persistenceId The string representation of the business object's persistence ID.
     * @return {@code true} if the persistence ID is valid for the current action type, otherwise {@code false}.
     */
    public abstract boolean isValid(String persistenceId);
}