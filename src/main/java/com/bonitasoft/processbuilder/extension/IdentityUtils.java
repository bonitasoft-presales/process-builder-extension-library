package com.bonitasoft.processbuilder.extension;

import org.bonitasoft.engine.api.IdentityAPI;
import org.bonitasoft.engine.identity.User;
import org.bonitasoft.engine.identity.UserSearchDescriptor;
import org.bonitasoft.engine.search.SearchOptionsBuilder;
import org.bonitasoft.engine.search.SearchResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Utility class for common operations with Bonita Identity API.
 * <p>
 * Provides methods to retrieve user information, managers, and users by memberships
 * without depending on BDM objects, ensuring functional independence and portability.
 * </p>
 *
 * @author Bonitasoft
 * @since 1.0
 */
public final class IdentityUtils {

    /**
     * A logger for this class, used to record log messages and provide debugging information.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(IdentityUtils.class);

    /**
     * Private constructor to prevent instantiation of this utility class.
     *
     * @throws UnsupportedOperationException always, to enforce the utility pattern.
     */
    private IdentityUtils() {
        throw new UnsupportedOperationException("This is a " + this.getClass().getSimpleName() + " class and cannot be instantiated.");
    }

    /**
     * Gets the manager ID of a given user.
     * <p>
     * This method retrieves the user from Bonita Identity API and returns the manager's user ID
     * if one is assigned. Returns {@code null} if the user is not found, has no manager assigned,
     * or if an error occurs during the retrieval.
     * </p>
     *
     * @param userId The ID of the user whose manager is to be retrieved
     * @param identityAPI The Bonita Identity API instance
     * @return The manager's user ID, or {@code null} if not found or on error
     */
    public static Long getUserManager(Long userId, IdentityAPI identityAPI) {
        try {
            LOGGER.debug("Searching for manager of user ID: {}", userId);

            User user = identityAPI.getUser(userId);

            if (user == null) {
                LOGGER.warn("User not found for ID: {}", userId);
                return null;
            }

            Long managerId = user.getManagerUserId();

            if (managerId == null || managerId <= 0) {
                LOGGER.debug("User ID {} has no manager assigned", userId);
                return null;
            }

            LOGGER.debug("Found manager ID {} for user ID {}", managerId, userId);
            return managerId;

        } catch (Exception e) {
            LOGGER.error("Error getting manager for user ID {}: {}", userId, e.getMessage(), e);
            return null;
        }
    }

    /**
     * Gets all users matching the given memberships (groups/roles).
     * <p>
     * This method accepts a list of objects that contain group and role information.
     * Each object in the list must have accessible {@code getGroupId()} and {@code getRoleId()} methods
     * that return {@code Long} values. The method uses reflection to extract these values,
     * allowing it to work with any object type without requiring a specific class dependency.
     * </p>
     * <p>
     * The method builds a complex search query with OR conditions for each membership combination
     * and returns all enabled users that match at least one of the specified memberships.
     * </p>
     *
     * @param membershipList List of objects containing group and role IDs (must have getGroupId() and getRoleId() methods)
     * @param identityAPI The Bonita Identity API instance
     * @return Set of user IDs matching the memberships, empty set if no users found or on error
     */
    public static Set<Long> getUsersByMemberships(List<?> membershipList, IdentityAPI identityAPI) {
        Set<Long> userIds = new HashSet<>();

        if (membershipList == null || membershipList.isEmpty()) {
            LOGGER.debug("Empty membership list provided, returning empty user set");
            return userIds;
        }

        try {
            SearchOptionsBuilder searchBuilder = new SearchOptionsBuilder(0, Integer.MAX_VALUE);
            searchBuilder.filter(UserSearchDescriptor.ENABLED, true);
            searchBuilder.and();

            // Assuming searchBuilder is initialized and the opening parenthesis is done before this block
            searchBuilder.leftParenthesis();
            Boolean isFirst = true;
            
            // // Flag to track the first iteration for the OR condition
            // final java.util.concurrent.atomic.AtomicBoolean isFirst = new java.util.concurrent.atomic.AtomicBoolean(true);

            // // 1. Process the membership list using a stream.
            // membershipList.stream()
            //     // 2. Explicitly define the mapping function (Function<InputType, OutputType>)
            //     .map(new java.util.function.Function<Object, java.util.Map.Entry<Long, Long>>() {
            //         @Override
            //         public java.util.Map.Entry<Long, Long> apply(Object membershipObj) {
            //             // Note: extractLongValue(Object, String, Logger) is used here.
            //             final Long groupId = extractLongValue(membershipObj, "getGroupId", logger);
            //             final Long roleId = extractLongValue(membershipObj, "getRoleId", logger);

            //             // Return a known, strongly-typed object (Map.Entry<Long, Long>)
            //             return new java.util.AbstractMap.SimpleImmutableEntry<>(groupId, roleId);
            //         }
            //     })
            //     // 3. Filter out entries where both GroupId and RoleId are null.
            //     .filter(entry -> {
            //         if (entry.getKey() == null && entry.getValue() == null) {
            //             logger.warn("Skipping membership object with both null groupId and roleId");
            //             return false;
            //         }
            //         return true;
            //     })
            //     // 4. Process the remaining valid entries and build the search query.
            //     .forEachOrdered(entry -> {
            //         final Long groupId = entry.getKey();
            //         final Long roleId = entry.getValue();

            //         // Add OR condition starting from the second element.
            //         if (!isFirst.getAndSet(false)) {
            //             searchBuilder.or();
            //         }

            //         searchBuilder.leftParenthesis();

            //         // Build the (GROUP_ID = X AND ROLE_ID = Y) or (GROUP_ID = X) or (ROLE_ID = Y) filter.
            //         if (groupId != null) {
            //             searchBuilder.filter(UserSearchDescriptor.GROUP_ID, groupId);
            //         }
            //         if (groupId != null && roleId != null) {
            //             searchBuilder.and();
            //         }
            //         if (roleId != null) {
            //             searchBuilder.filter(UserSearchDescriptor.ROLE_ID, roleId);
            //         }

            //         searchBuilder.rightParenthesis();
            //     });

            // // Close the global OR parenthesis.
            // searchBuilder.rightParenthesis();

            for (final Object membershipObj : membershipList) {
                final Long groupId = extractLongValue(membershipObj, "getGroupId");
                final Long roleId = extractLongValue(membershipObj, "getRoleId");

                // Skip if both groupId and roleId are null
                if (groupId == null && roleId == null) {
                    LOGGER.warn("Skipping membership object with both null groupId and roleId");
                    continue;
                }
                if (!isFirst) {
                    searchBuilder.or();
                }
                searchBuilder.leftParenthesis();
                if (groupId != null && roleId != null) {
                    searchBuilder.filter(UserSearchDescriptor.GROUP_ID, groupId);
                    searchBuilder.and();
                    searchBuilder.filter(UserSearchDescriptor.ROLE_ID, roleId);
                } else if (groupId != null) {
                    searchBuilder.filter(UserSearchDescriptor.GROUP_ID, groupId);
                } else if (roleId != null) {
                    searchBuilder.filter(UserSearchDescriptor.ROLE_ID, roleId);
                }
                searchBuilder.rightParenthesis();
                isFirst = false;
            }
            searchBuilder.rightParenthesis();

            final SearchResult<User> searchResult = identityAPI.searchUsers(searchBuilder.done());
            userIds = searchResult.getResult().stream()
                .map(User::getId)
                .collect(Collectors.toSet());

            LOGGER.debug("Found {} users from {} memberships", userIds.size(), membershipList.size());

        } catch (final Exception e) {
            LOGGER.error("An error occurred during user search by membership: {}", e.getMessage(), e);
        }

        return userIds;
    }

    /**
     * Extracts a Long value from an object using reflection by calling a getter method.
     * <p>
     * This helper method attempts to find and invoke the specified method on the given object.
     * If the method exists and returns a Long value, it is returned. Otherwise, {@code null} is returned.
     * </p>
     *
     * @param obj The object from which to extract the value
     * @param methodName The name of the getter method to invoke (e.g., "getGroupId")
     * @return The Long value returned by the method, or {@code null} if not found or on error
     */
    static Long extractLongValue(Object obj, String methodName) {
        if (obj == null) {
            return null;
        }

        try {
            Method method = obj.getClass().getMethod(methodName);
            Object result = method.invoke(obj);

            if (result instanceof Long) {
                return (Long) result;
            } else if (result == null) {
                return null;
            } else {
                LOGGER.warn("Method {} returned non-Long value: {}", methodName, result.getClass().getName());
                return null;
            }
        } catch (NoSuchMethodException e) {
            LOGGER.warn("Method {} not found on object of type {}", methodName, obj.getClass().getName());
            return null;
        } catch (Exception e) {
            LOGGER.warn("Error invoking method {} on object: {}", methodName, e.getMessage());
            return null;
        }
    }
}
