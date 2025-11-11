package com.bonitasoft.processbuilder.records;

/**
 * A record representing a generic user in the system.
 * This is the central source of user data, preventing field duplication.
 *
 * @param id The unique identifier of the user.
 * @param userName The username for login.
 * @param fullName The full name (first name + last name) of the user.
 * @param firstName The first name of the user.
 * @param lastName The last name of the user.
 * @param email The email address of the user.
 */
public record UserRecord(
    Long id,
    String userName,
    String fullName,
    String firstName,
    String lastName,
    String email
) {
    /**
     *
     * @param id The unique identifier of the user.
     * @param userName The username for login.
     * @param fullName The full name (first name + last name) of the user.
     * @param firstName The first name of the user.
     * @param lastName The last name of the user.
     * @param email The email address of the user.
     */
    public UserRecord {
    }
}
