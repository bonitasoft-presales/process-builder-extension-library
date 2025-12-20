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

    /**
     * Returns the display name of the user.
     * Prefers fullName if available, otherwise constructs from firstName + lastName,
     * and falls back to username if neither is available.
     *
     * @return The display name or empty string if no name data is available
     */
    public String displayName() {
        if (fullName != null && !fullName.isBlank()) {
            return fullName;
        }
        StringBuilder sb = new StringBuilder();
        if (firstName != null && !firstName.isBlank()) {
            sb.append(firstName);
        }
        if (lastName != null && !lastName.isBlank()) {
            if (sb.length() > 0) {
                sb.append(" ");
            }
            sb.append(lastName);
        }
        if (sb.length() == 0 && userName != null) {
            return userName;
        }
        return sb.toString();
    }
}
