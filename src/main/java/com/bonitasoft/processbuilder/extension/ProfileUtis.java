package com.bonitasoft.processbuilder.extension;
import org.bonitasoft.engine.api.APIAccessor;
import org.bonitasoft.engine.api.IdentityAPI;
import org.bonitasoft.engine.api.ProfileAPI;
import org.bonitasoft.engine.identity.MemberType;
import org.bonitasoft.engine.identity.User;
import org.bonitasoft.engine.identity.UserCriterion;
import org.bonitasoft.engine.identity.UserSearchDescriptor;
import org.bonitasoft.engine.profile.Profile;
import org.bonitasoft.engine.profile.ProfileMember;
import org.bonitasoft.engine.profile.ProfileMemberSearchDescriptor;
import org.bonitasoft.engine.profile.ProfileSearchDescriptor;
import org.bonitasoft.engine.search.SearchOptionsBuilder;
import org.bonitasoft.engine.search.SearchResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Utility class for retrieving user information based on Bonita profiles.
 * Provides static methods to collect all user IDs associated with a profile through
 * direct membership, roles, groups, and role-group combinations (memberships).
 *
 * @author Bonitasoft
 * @since 1.0
 */
public final class ProfileUtis {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProfileUtis.class);
    private static final int MAX_RESULTS = Integer.MAX_VALUE;

    /**
     * Private constructor to prevent instantiation.
     */
    private ProfileUtis() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Retrieves all unique user IDs associated with a given profile name.
     * <p>
     * This method collects users from all membership types:
     * <ul>
     *   <li><b>USER</b>: Users directly assigned to the profile</li>
     *   <li><b>ROLE</b>: Users belonging to roles assigned to the profile</li>
     *   <li><b>GROUP</b>: Users belonging to groups assigned to the profile</li>
     *   <li><b>MEMBERSHIP</b>: Users with specific role-group combinations assigned to the profile</li>
     * </ul>
     *
     * @param apiAccessor The Bonita API accessor to obtain ProfileAPI and IdentityAPI.
     * @param profileName The name of the profile to search for (e.g., "Administrator", "User").
     * @return A list of unique user IDs associated with the profile, or an empty list if
     *         the profile is not found or an error occurs.
     * @throws IllegalArgumentException if apiAccessor is null or profileName is null/empty.
     */
    public static List<Long> getUserIdsInProfile(APIAccessor apiAccessor, String profileName) {
        validateInputs(apiAccessor, profileName);

        long startTime = System.currentTimeMillis();
        LOGGER.info("Starting user ID retrieval for profile: '{}'", profileName);

        try {
            ProfileAPI profileAPI = apiAccessor.getProfileAPI();
            IdentityAPI identityAPI = apiAccessor.getIdentityAPI();

            Optional<Profile> profileOpt = findProfileByName(profileAPI, profileName);
            if (profileOpt.isEmpty()) {
                LOGGER.warn("Profile '{}' not found. Returning empty list.", profileName);
                return List.of();
            }

            long profileId = profileOpt.get().getId();

            Set<Long> userIds = Stream.of(
                    collectDirectUsers(profileAPI, profileId),
                    collectUsersFromRoles(profileAPI, identityAPI, profileId),
                    collectUsersFromGroups(profileAPI, identityAPI, profileId),
                    collectUsersFromMemberships(profileAPI, identityAPI, profileId)
                )
                .flatMap(Set::stream)
                .collect(Collectors.toSet());

            LOGGER.info("Successfully retrieved {} unique user IDs for profile '{}' in {} ms",
                    userIds.size(), profileName, System.currentTimeMillis() - startTime);

            return new ArrayList<>(userIds);

        } catch (Exception e) {
            LOGGER.error("Unexpected error retrieving user IDs for profile '{}': {}",
                    profileName, e.getMessage(), e);
            return List.of();
        }
    }

    /**
     * Validates input parameters.
     *
     * @param apiAccessor The API accessor to validate.
     * @param profileName The profile name to validate.
     * @throws IllegalArgumentException if any parameter is invalid.
     */
    private static void validateInputs(APIAccessor apiAccessor, String profileName) {
        if (apiAccessor == null) {
            throw new IllegalArgumentException("APIAccessor cannot be null");
        }
        if (profileName == null || profileName.isBlank()) {
            throw new IllegalArgumentException("Profile name cannot be null or blank");
        }
    }

    /**
     * Finds a profile by its name.
     *
     * @param profileAPI  The Profile API instance.
     * @param profileName The name of the profile to find.
     * @return An Optional containing the Profile if found, or empty if not found.
     */
    private static Optional<Profile> findProfileByName(ProfileAPI profileAPI, String profileName) {
        try {
            SearchOptionsBuilder searchBuilder = new SearchOptionsBuilder(0, 1);
            searchBuilder.filter(ProfileSearchDescriptor.NAME, profileName);

            SearchResult<Profile> searchResult = profileAPI.searchProfiles(searchBuilder.done());

            return searchResult.getResult().stream().findFirst();
        } catch (Exception e) {
            LOGGER.error("Error searching for profile '{}': {}", profileName, e.getMessage(), e);
            return Optional.empty();
        }
    }

    /**
     * Collects user IDs from direct user memberships in the profile.
     *
     * @param profileAPI The Profile API instance.
     * @param profileId  The profile ID to search.
     * @return A set of user IDs directly assigned to the profile.
     */
    private static Set<Long> collectDirectUsers(ProfileAPI profileAPI, long profileId) {
        try {
            List<ProfileMember> members = searchProfileMembers(profileAPI, profileId, MemberType.USER);

            Set<Long> userIds = members.stream()
                    .mapToLong(ProfileMember::getUserId)
                    .filter(id -> id > 0)
                    .boxed()
                    .collect(Collectors.toSet());

            LOGGER.debug("Collected {} direct users from profile ID {}", userIds.size(), profileId);
            return userIds;

        } catch (Exception e) {
            LOGGER.error("Error collecting direct users for profile ID {}: {}", profileId, e.getMessage(), e);
            return Set.of();
        }
    }

    /**
     * Collects user IDs from role memberships in the profile.
     *
     * @param profileAPI  The Profile API instance.
     * @param identityAPI The Identity API instance.
     * @param profileId   The profile ID to search.
     * @return A set of user IDs belonging to roles assigned to the profile.
     */
    private static Set<Long> collectUsersFromRoles(ProfileAPI profileAPI, IdentityAPI identityAPI, long profileId) {
        try {
            List<ProfileMember> members = searchProfileMembers(profileAPI, profileId, MemberType.ROLE);

            Set<Long> userIds = members.stream()
                    .mapToLong(ProfileMember::getRoleId)
                    .filter(roleId -> roleId > 0)
                    .boxed()
                    .flatMap(roleId -> getActiveUsersInRole(identityAPI, roleId).stream())
                    .map(User::getId)
                    .collect(Collectors.toSet());

            LOGGER.debug("Collected {} users from {} role memberships for profile ID {}",
                    userIds.size(), members.size(), profileId);
            return userIds;

        } catch (Exception e) {
            LOGGER.error("Error collecting users from roles for profile ID {}: {}", profileId, e.getMessage(), e);
            return Set.of();
        }
    }

    /**
     * Collects user IDs from group memberships in the profile.
     *
     * @param profileAPI  The Profile API instance.
     * @param identityAPI The Identity API instance.
     * @param profileId   The profile ID to search.
     * @return A set of user IDs belonging to groups assigned to the profile.
     */
    private static Set<Long> collectUsersFromGroups(ProfileAPI profileAPI, IdentityAPI identityAPI, long profileId) {
        try {
            List<ProfileMember> members = searchProfileMembers(profileAPI, profileId, MemberType.GROUP);

            Set<Long> userIds = members.stream()
                    .mapToLong(ProfileMember::getGroupId)
                    .filter(groupId -> groupId > 0)
                    .boxed()
                    .flatMap(groupId -> getActiveUsersInGroup(identityAPI, groupId).stream())
                    .map(User::getId)
                    .collect(Collectors.toSet());

            LOGGER.debug("Collected {} users from {} group memberships for profile ID {}",
                    userIds.size(), members.size(), profileId);
            return userIds;

        } catch (Exception e) {
            LOGGER.error("Error collecting users from groups for profile ID {}: {}", profileId, e.getMessage(), e);
            return Set.of();
        }
    }

    /**
     * Collects user IDs from role-group (membership) combinations in the profile.
     *
     * @param profileAPI  The Profile API instance.
     * @param identityAPI The Identity API instance.
     * @param profileId   The profile ID to search.
     * @return A set of user IDs matching the role-group combinations assigned to the profile.
     */
    private static Set<Long> collectUsersFromMemberships(ProfileAPI profileAPI, IdentityAPI identityAPI, long profileId) {
        try {
            List<ProfileMember> members = searchProfileMembers(profileAPI, profileId, MemberType.MEMBERSHIP);

            Set<Long> userIds = members.stream()
                    .filter(member -> member.getGroupId() > 0 && member.getRoleId() > 0)
                    .flatMap(member -> searchUsersByGroupAndRole(identityAPI, member.getGroupId(), member.getRoleId()).stream())
                    .map(User::getId)
                    .collect(Collectors.toSet());

            LOGGER.debug("Collected {} users from {} membership combinations for profile ID {}",
                    userIds.size(), members.size(), profileId);
            return userIds;

        } catch (Exception e) {
            LOGGER.error("Error collecting users from memberships for profile ID {}: {}", profileId, e.getMessage(), e);
            return Set.of();
        }
    }

    /**
     * Searches for profile members of a specific type.
     *
     * @param profileAPI The Profile API instance.
     * @param profileId  The profile ID to search.
     * @param memberType The type of membership to search for.
     * @return A list of ProfileMember objects matching the criteria.
     */
    private static List<ProfileMember> searchProfileMembers(ProfileAPI profileAPI, long profileId, MemberType memberType) {
        try {
            SearchOptionsBuilder searchBuilder = new SearchOptionsBuilder(0, MAX_RESULTS);
            searchBuilder.filter(ProfileMemberSearchDescriptor.PROFILE_ID, profileId);

            SearchResult<ProfileMember> result = profileAPI.searchProfileMembers(
                    memberType.name(), searchBuilder.done());

            return result.getResult();

        } catch (Exception e) {
            LOGGER.error("Error searching profile members of type {} for profile ID {}: {}",
                    memberType, profileId, e.getMessage(), e);
            return List.of();
        }
    }

    /**
     * Gets active users in a specific role.
     *
     * @param identityAPI The Identity API instance.
     * @param roleId      The role ID to search.
     * @return A list of active users in the role.
     */
    private static List<User> getActiveUsersInRole(IdentityAPI identityAPI, long roleId) {
        try {
            return identityAPI.getActiveUsersInRole(roleId, 0, MAX_RESULTS, UserCriterion.USER_NAME_ASC);
        } catch (Exception e) {
            LOGGER.error("Error getting active users in role {}: {}", roleId, e.getMessage(), e);
            return List.of();
        }
    }

    /**
     * Gets active users in a specific group.
     *
     * @param identityAPI The Identity API instance.
     * @param groupId     The group ID to search.
     * @return A list of active users in the group.
     */
    private static List<User> getActiveUsersInGroup(IdentityAPI identityAPI, long groupId) {
        try {
            return identityAPI.getActiveUsersInGroup(groupId, 0, MAX_RESULTS, UserCriterion.USER_NAME_ASC);
        } catch (Exception e) {
            LOGGER.error("Error getting active users in group {}: {}", groupId, e.getMessage(), e);
            return List.of();
        }
    }

    /**
     * Searches for users that belong to a specific group and role combination.
     *
     * @param identityAPI The Identity API instance.
     * @param groupId     The group ID to filter by.
     * @param roleId      The role ID to filter by.
     * @return A list of users matching the criteria.
     */
    private static List<User> searchUsersByGroupAndRole(IdentityAPI identityAPI, long groupId, long roleId) {
        try {
            SearchOptionsBuilder searchBuilder = new SearchOptionsBuilder(0, MAX_RESULTS);
            searchBuilder.filter(UserSearchDescriptor.GROUP_ID, groupId);
            searchBuilder.filter(UserSearchDescriptor.ROLE_ID, roleId);

            SearchResult<User> result = identityAPI.searchUsers(searchBuilder.done());
            return result.getResult();

        } catch (Exception e) {
            LOGGER.error("Error searching users by group {} and role {}: {}", groupId, roleId, e.getMessage(), e);
            return List.of();
        }
    }

}