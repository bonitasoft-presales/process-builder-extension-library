package com.bonitasoft.processbuilder.records;

/**
 * Record representing a minimal set of user and membership data
 * defined for a **process definition**.
 * <p>
 * This record is used to store the specific users and memberships
 * associated with a process to facilitate efficient passing of
 * actor filter data without including the complete process user
 * list (`PBUserList`) object.
 * </p>
 *
 * @param persistenceId The unique identifier for this user list entry.
 * @param canLaunchProcess A flag indicating if the user/membership can launch the process.
 * @param refMemberShip The reference name of the membership (e.g., a role or group name).
 * @param memberShipKey The unique key identifying the type of membership (e.g., "ROLE", "GROUP", "USER").
 * @param groupId The persistence ID of the associated group, if applicable.
 * @param roleId The persistence ID of the associated role, if applicable.
 * @param userId The persistence ID of the associated user, if applicable.
 * @param pBProcessPersistenceId The persistence ID of the process definition this list belongs to.
 */
public record UserList (
    Long persistenceId,
    Boolean canLaunchProcess,
    String refMemberShip,
    String memberShipKey,
    Long groupId,
    Long roleId,
    Long userId,
    Long pBProcessPersistenceId
) {
    /**
     * Compact constructor to perform validation or canonicalization, if needed.
     * Currently, it serves as the default constructor provided by the record.
     * @param persistenceId The unique identifier for this user list entry.
     * @param canLaunchProcess A flag indicating if the user/membership can launch the process.
     * @param refMemberShip The reference name of the membership (e.g., a role or group name).
     * @param memberShipKey The unique key identifying the type of membership (e.g., "ROLE", "GROUP", "USER").
     * @param groupId The persistence ID of the associated group, if applicable.
     * @param roleId The persistence ID of the associated role, if applicable.
     * @param userId The persistence ID of the associated user, if applicable.
     * @param pBProcessPersistenceId The persistence ID of the process definition this list belongs to.
     */
    public UserList {
        // You can add validation logic here if required, e.g.:
        // if (persistenceId == null) {
        //     throw new IllegalArgumentException("Persistence ID cannot be null.");
        // }
    }
}