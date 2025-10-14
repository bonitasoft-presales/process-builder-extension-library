package com.bonitasoft.processbuilder.extension;

/**
 * Utility class for constructing composite keys representing a specific
 * Group and Role membership combination.
 * This key is used primarily for efficient querying in the Business Data Model (BDM).
 */
public class MembershipUtils {

    /**
     * Constructs a composite membership key (Group ID$Role ID) if both IDs are present.
     * <p>
     * The key is formed by concatenating the {@code groupId} and {@code roleId} using
     * a dollar sign ($) as a separator. If either ID is null, an empty string is returned.
     * This avoids creating partial or invalid keys in the BDM.
     * </p>
     *
     * @param groupId The ID of the group (can be null).
     * @param roleId The ID of the role (can be null).
     * @return The concatenated membership key (e.g., "101$201"), or an empty string ("") if either ID is null.
     */
    public static String buildMembershipKey(Long groupId, Long roleId) {
        // Use String.valueOf() for safe conversion of Long to String
        if (groupId != null && roleId != null) {
            return String.valueOf(groupId) + "$" + String.valueOf(roleId);
        } else {
            return null;
        }
    }
}
