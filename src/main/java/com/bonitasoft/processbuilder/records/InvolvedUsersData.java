package com.bonitasoft.processbuilder.records;

import java.util.ArrayList;
import java.util.List;
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
     */
    public InvolvedUsersData {
        // Defensive Copying: Ensures internal list is mutable only within the record.
        // It prevents external changes to the list passed during construction.
        memberships = new ArrayList<>(memberships);
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