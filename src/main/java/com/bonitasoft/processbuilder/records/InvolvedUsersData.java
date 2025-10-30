package com.bonitasoft.processbuilder.records;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import java.util.Collections;

/**
 * A record to hold the parsed configuration data for finding candidate users
 * (actors) involved in a process step. Records are a clean way to model immutable data in modern Java.
 *
 * @param stepManagerRef The reference (usually a String ID) pointing to a process step 
 * from which the manager of the step's user will be retrieved.
 * @param stepUserRef The reference (usually a String ID) pointing to a process step 
 * from which the user directly assigned to that step will be retrieved.
 * @param memberships A list of membership references (e.g., group or role names/IDs) 
 * used to find candidate users.
 */
public record InvolvedUsersData(
    String stepManagerRef, 
    String stepUserRef, 
    List<String> memberships
) {
    
    /**
     * The compact constructor for this record. 
     * It creates a defensive copy of the mutable lists to ensure the record's immutability.
     * @param stepManagerRef The reference (usually a String ID) pointing to a process step 
     * from which the manager of the step's user will be retrieved.
     * @param stepUserRef The reference (usually a String ID) pointing to a process step 
     * from which the user directly assigned to that step will be retrieved.
     * @param memberships A list of membership references (e.g., group or role names/IDs) 
     * used to find candidate users.
     */
    public InvolvedUsersData {
        // Defensive Copying: Ensures internal list is mutable only within the record.
        // It prevents external changes to the list passed during construction.
        memberships = new ArrayList<>(memberships);
    }

    /**
     * Constructs an array containing the step references (stepManagerRef and stepUserRef)
     * after filtering out any null or empty strings.
     * * This method leverages the Java Stream API for clean, concise, and optimized filtering.
     *
     * @return A String array containing only the valid (non-null and non-empty) step references.
     */
    public String[] getRefStepsArray() {
        return Stream.of(this.stepManagerRef, this.stepUserRef)
            .filter(Objects::nonNull)
            .filter(ref -> !ref.trim().isEmpty()) 
            .toArray(String[]::new);
    }

    // --- Accessor Methods with Defensive Copying ---

    /**
     * Returns a read-only view (unmodifiable list) of the membership references
     * to prevent external modification of the record's internal state.
     *
     * @return An unmodifiable List containing the membership reference strings.
     */
    public List<String> memberships() {
        return Collections.unmodifiableList(this.memberships);
    }
}