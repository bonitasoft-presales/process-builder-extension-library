package com.bonitasoft.processbuilder.extension;

import org.bonitasoft.engine.api.IdentityAPI;
import org.bonitasoft.engine.identity.User;
import org.bonitasoft.engine.identity.UserSearchDescriptor;
import org.bonitasoft.engine.search.SearchOptionsBuilder;
import org.bonitasoft.engine.search.SearchResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
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
 * @version 2.0.0 - Fixed membership handling for group-only or role-only memberships
 */
public final class IdentityUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(IdentityUtils.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private IdentityUtils() {
        throw new UnsupportedOperationException("This is a " + this.getClass().getSimpleName() + " class and cannot be instantiated.");
    }

    /**
     * Gets the manager ID of a given user.
     *
     * @param userId      The ID of the user whose manager is to be retrieved
     * @param identityAPI The Bonita Identity API instance
     * @return The manager's user ID, or {@code null} if not found or on error
     */
    public static Long getUserManager(Long userId, IdentityAPI identityAPI) {
        if (!isValidId(userId)) {
            LOGGER.warn("Invalid userId provided: {}", userId);
            return null;
        }

        try {
            LOGGER.debug("Searching for manager of user ID: {}", userId);

            User user = identityAPI.getUser(userId);
            if (user == null) {
                LOGGER.warn("User not found for ID: {}", userId);
                return null;
            }

            Long managerId = user.getManagerUserId();

            if (!isValidId(managerId)) {
                LOGGER.debug("User ID {} has no manager assigned (managerId: {})", userId, managerId);
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
     * Each object must have {@code getGroupId()} and {@code getRoleId()} methods.
     * </p>
     * <p>
     * <b>IMPORTANT:</b> This method correctly handles memberships that have:
     * </p>
     * <ul>
     *   <li>Both groupId AND roleId defined</li>
     *   <li>Only groupId defined (roleId is null or 0)</li>
     *   <li>Only roleId defined (groupId is null or 0)</li>
     * </ul>
     *
     * @param membershipList List of objects containing group and role IDs
     * @param identityAPI    The Bonita Identity API instance
     * @return Set of user IDs matching the memberships, empty set if no users found
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

            // Track if we have any valid membership conditions
            boolean hasValidConditions = false;
            boolean isFirstCondition = true;

            // Start the OR group for membership conditions
            searchBuilder.and();
            searchBuilder.leftParenthesis();

            for (final Object membershipObj : membershipList) {
                final Long groupId = extractLongValue(membershipObj, "getGroupId");
                final Long roleId = extractLongValue(membershipObj, "getRoleId");

                // Use helper method that validates > 0, not just != null
                boolean hasValidGroup = isValidId(groupId);
                boolean hasValidRole = isValidId(roleId);

                // Skip if both groupId and roleId are invalid
                if (!hasValidGroup && !hasValidRole) {
                    LOGGER.warn("Skipping membership object with no valid groupId or roleId (groupId={}, roleId={})",
                            groupId, roleId);
                    continue;
                }

                // Add OR condition after the first valid membership
                if (!isFirstCondition) {
                    searchBuilder.or();
                }

                searchBuilder.leftParenthesis();

                // Only include valid IDs in the query
                if (hasValidGroup && hasValidRole) {
                    // Both group and role - require exact match
                    LOGGER.debug("Adding filter: GROUP_ID={} AND ROLE_ID={}", groupId, roleId);
                    searchBuilder.filter(UserSearchDescriptor.GROUP_ID, groupId);
                    searchBuilder.and();
                    searchBuilder.filter(UserSearchDescriptor.ROLE_ID, roleId);
                } else if (hasValidGroup) {
                    // Only group - search users in this group (any role)
                    LOGGER.debug("Adding filter: GROUP_ID={} (no role constraint)", groupId);
                    searchBuilder.filter(UserSearchDescriptor.GROUP_ID, groupId);
                } else {
                    // Only role - search users with this role (any group)
                    LOGGER.debug("Adding filter: ROLE_ID={} (no group constraint)", roleId);
                    searchBuilder.filter(UserSearchDescriptor.ROLE_ID, roleId);
                }

                searchBuilder.rightParenthesis();
                isFirstCondition = false;
                hasValidConditions = true;
            }

            searchBuilder.rightParenthesis();

            // If no valid conditions were added, return empty set
            if (!hasValidConditions) {
                LOGGER.warn("No valid membership conditions found, returning empty user set");
                return userIds;
            }

            final SearchResult<User> searchResult = identityAPI.searchUsers(searchBuilder.done());
            userIds = searchResult.getResult().stream()
                    .map(User::getId)
                    .collect(Collectors.toSet());

            LOGGER.info("Found {} users from {} memberships", userIds.size(), membershipList.size());

        } catch (final Exception e) {
            LOGGER.error("An error occurred during user search by membership: {}", e.getMessage(), e);
        }

        return userIds;
    }

    /**
     * Checks if an ID is valid (not null and greater than 0).
     *
     * @param id The ID to validate
     * @return true if the ID is valid, false otherwise
     */
    private static boolean isValidId(Long id) {
        return id != null && id > 0;
    }

    /**
     * Extracts a Long value from an object using reflection by calling a getter method.
     * <p>
     * This method handles multiple return types: Long, Integer, Number, and String.
     * Returns {@code null} for invalid values (null, 0, negative, or non-parseable).
     * </p>
     *
     * @param obj        The object from which to extract the value
     * @param methodName The name of the getter method to invoke (e.g., "getGroupId")
     * @return The Long value (greater than 0), or {@code null} if not valid
     */
    static Long extractLongValue(Object obj, String methodName) {
        if (obj == null) {
            return null;
        }

        try {
            Method method = obj.getClass().getMethod(methodName);
            Object result = method.invoke(obj);

            if (result == null) {
                return null;
            }

            Long value = null;

            if (result instanceof Long) {
                value = (Long) result;
            } else if (result instanceof Integer) {
                value = ((Integer) result).longValue();
            } else if (result instanceof Number) {
                value = ((Number) result).longValue();
            } else if (result instanceof String) {
                String str = ((String) result).trim();
                if (!str.isEmpty()) {
                    try {
                        value = Long.parseLong(str);
                    } catch (NumberFormatException nfe) {
                        LOGGER.warn("Method {} returned non-parseable String: '{}'", methodName, str);
                        return null;
                    }
                }
            } else {
                LOGGER.warn("Method {} returned unexpected type: {}", methodName, result.getClass().getName());
                return null;
            }

            // Return null if value is 0 or negative (invalid ID)
            if (value == null || value <= 0) {
                LOGGER.trace("Method {} returned invalid ID value: {}", methodName, value);
                return null;
            }

            return value;

        } catch (NoSuchMethodException e) {
            LOGGER.warn("Method {} not found on object of type {}", methodName, obj.getClass().getName());
            return null;
        } catch (Exception e) {
            LOGGER.warn("Error invoking method {} on object: {}", methodName, e.getMessage());
            return null;
        }
    }

    /**
     * Extracts a user ID from a BDM object using reflection.
     * <p>
     * This method attempts to call a getter method on the provided object to extract
     * a user ID. It supports various method names commonly used in BDM objects such as
     * {@code getUserId()}, {@code getStepUser()}, or any custom getter name.
     * </p>
     * <p>
     * The method handles null objects, null return values, and invalid ID values (0 or negative).
     * </p>
     *
     * @param bdmObject The BDM object from which to extract the user ID (can be null)
     * @param methodName The name of the getter method to invoke (e.g., "getUserId", "getStepUser")
     * @return The user ID if valid (greater than 0), or {@code null} if object is null,
     *         method not found, or ID is invalid
     */
    public static Long getUserIdFromObject(Object bdmObject, String methodName) {
        if (bdmObject == null) {
            LOGGER.debug("Cannot extract user ID: BDM object is null");
            return null;
        }

        if (methodName == null || methodName.trim().isEmpty()) {
            LOGGER.warn("Cannot extract user ID: method name is null or empty");
            return null;
        }

        Long userId = extractLongValue(bdmObject, methodName);

        if (userId != null && userId > 0) {
            LOGGER.debug("Extracted user ID {} from {} using method {}",
                    userId, bdmObject.getClass().getSimpleName(), methodName);
            return userId;
        }

        LOGGER.debug("No valid user ID found in {} using method {} (value: {})",
                bdmObject.getClass().getSimpleName(), methodName, userId);
        return null;
    }

    /**
     * Builds a set of candidate user IDs for task assignation based on step user, manager, and memberships.
     * <p>
     * This method implements the common actor filter logic:
     * </p>
     * <ol>
     *   <li>If stepUserId is valid, add it to the candidates</li>
     *   <li>If includeManager is true and stepUserId has a manager, add the manager ID</li>
     *   <li>If membershipList is provided, add all users from those memberships</li>
     * </ol>
     * <p>
     * This method is designed to be used by Groovy scripts to reduce code duplication
     * when implementing actor filters.
     * </p>
     *
     * @param stepUserId The user ID from a previous step (can be null)
     * @param includeManager Whether to include the step user's manager
     * @param membershipList List of membership objects with getGroupId/getRoleId methods (can be null)
     * @param identityAPI The Bonita Identity API instance
     * @return A set of candidate user IDs (never null, may be empty)
     */
    public static Set<Long> buildCandidateUsers(
            Long stepUserId,
            boolean includeManager,
            List<?> membershipList,
            IdentityAPI identityAPI) {

        Set<Long> candidates = new HashSet<>();

        // Add the step user if valid
        if (isValidId(stepUserId)) {
            candidates.add(stepUserId);
            LOGGER.debug("Added step user ID {} to candidates", stepUserId);

            // Add the manager if requested
            if (includeManager) {
                Long managerId = getUserManager(stepUserId, identityAPI);
                if (managerId != null) {
                    candidates.add(managerId);
                    LOGGER.debug("Added manager ID {} to candidates", managerId);
                }
            }
        }

        // Add users from memberships
        if (membershipList != null && !membershipList.isEmpty()) {
            Set<Long> membershipUsers = getUsersByMemberships(membershipList, identityAPI);
            candidates.addAll(membershipUsers);
            LOGGER.debug("Added {} users from memberships to candidates", membershipUsers.size());
        }

        LOGGER.info("Built candidate user set with {} total users", candidates.size());
        return candidates;
    }

    /**
     * Filters assignable users based on candidate user IDs.
     * <p>
     * This method takes a set of candidate user IDs and returns only those that
     * are present in the provided collection of assignable user IDs. This is the
     * final step in actor filter logic where candidates are intersected with
     * users who are actually assignable to the task.
     * </p>
     *
     * @param candidateUserIds Set of candidate user IDs to filter
     * @param assignableUserIds Collection of user IDs that are assignable to the task
     * @return A set containing only the user IDs that are both candidates AND assignable
     */
    public static Set<Long> filterAssignableUsers(
            Set<Long> candidateUserIds,
            Collection<Long> assignableUserIds) {

        if (candidateUserIds == null || candidateUserIds.isEmpty()) {
            LOGGER.debug("No candidate users to filter");
            return Collections.emptySet();
        }

        if (assignableUserIds == null || assignableUserIds.isEmpty()) {
            LOGGER.debug("No assignable users provided, returning empty set");
            return Collections.emptySet();
        }

        Set<Long> assignableSet = new HashSet<>(assignableUserIds);
        Set<Long> filteredUsers = candidateUserIds.stream()
                .filter(assignableSet::contains)
                .collect(Collectors.toSet());

        LOGGER.info("Filtered {} candidate users down to {} assignable users",
                candidateUserIds.size(), filteredUsers.size());

        return filteredUsers;
    }

    /**
     * Convenience method that combines building candidate users and filtering in one call.
     * <p>
     * This method performs the complete actor filter logic:
     * </p>
     * <ol>
     *   <li>Builds the candidate user set from step user, manager, and memberships</li>
     *   <li>Filters the candidates against the assignable users</li>
     * </ol>
     * <p>
     * Example usage in Groovy:
     * </p>
     * <pre>{@code
     * Set<Long> filteredUsers = IdentityUtils.getFilteredAssignableUsers(
     *     stepUserId,
     *     true,  // include manager
     *     membershipList,
     *     assignableUserIds,
     *     identityAPI
     * )
     * return filteredUsers.toList()
     * }</pre>
     *
     * @param stepUserId The user ID from a previous step (can be null)
     * @param includeManager Whether to include the step user's manager
     * @param membershipList List of membership objects with getGroupId/getRoleId methods (can be null)
     * @param assignableUserIds Collection of user IDs that are assignable to the task
     * @param identityAPI The Bonita Identity API instance
     * @return A set of user IDs that are both candidates AND assignable (never null)
     */
    public static Set<Long> getFilteredAssignableUsers(
            Long stepUserId,
            boolean includeManager,
            List<?> membershipList,
            Collection<Long> assignableUserIds,
            IdentityAPI identityAPI) {

        Set<Long> candidates = buildCandidateUsers(stepUserId, includeManager, membershipList, identityAPI);
        return filterAssignableUsers(candidates, assignableUserIds);
    }

    /**
     * Extracts user IDs from a list of BDM user objects.
     * <p>
     * This method iterates over a list of BDM objects and extracts user IDs using
     * the specified getter method. It filters out null and invalid IDs (0 or negative).
     * </p>
     * <p>
     * This is useful for extracting user IDs from BDM objects like PBUserList where
     * each object has a user ID field.
     * </p>
     *
     * @param userObjects List of BDM objects containing user IDs
     * @param userIdMethodName The getter method name to extract user ID (e.g., "getUserId")
     * @return A set of valid user IDs extracted from the objects (never null)
     */
    public static Set<Long> extractUserIdsFromObjects(List<?> userObjects, String userIdMethodName) {
        if (userObjects == null || userObjects.isEmpty()) {
            LOGGER.debug("No user objects provided for extraction");
            return Collections.emptySet();
        }

        if (userIdMethodName == null || userIdMethodName.trim().isEmpty()) {
            LOGGER.warn("Cannot extract user IDs: method name is null or empty");
            return Collections.emptySet();
        }

        Set<Long> userIds = new HashSet<>();

        for (Object userObject : userObjects) {
            Long userId = getUserIdFromObject(userObject, userIdMethodName);
            if (userId != null) {
                userIds.add(userId);
            }
        }

        LOGGER.debug("Extracted {} valid user IDs from {} objects", userIds.size(), userObjects.size());
        return userIds;
    }

    // ========================================================================
    // JSON Parsing Utilities
    // ========================================================================

    /**
     * Parses a JSON string and returns the root JsonNode.
     *
     * @param jsonContent JSON string to parse
     * @param logger      Logger for error reporting (nullable)
     * @return Optional containing the root JsonNode, or empty if parsing fails
     */
    public static Optional<JsonNode> parseJson(String jsonContent, Logger logger) {
        if (jsonContent == null || jsonContent.isBlank()) {
            logWarn(logger, "JSON content is null or blank");
            return Optional.empty();
        }

        try {
            JsonNode rootNode = OBJECT_MAPPER.readTree(jsonContent);
            return Optional.ofNullable(rootNode);
        } catch (JsonProcessingException e) {
            logWarn(logger, "Failed to parse JSON content: {}", e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Parses JSON and extracts a specific node by key.
     *
     * @param jsonContent JSON string to parse
     * @param nodeKey     Key of the node to extract
     * @param logger      Logger for error reporting (nullable)
     * @return Optional containing the extracted JsonNode, or empty if not found
     */
    public static Optional<JsonNode> parseJsonAndGetNode(String jsonContent, String nodeKey, Logger logger) {
        return parseJson(jsonContent, logger)
                .flatMap(root -> getJsonNode(root, nodeKey, logger));
    }

    /**
     * Extracts a child node from a parent JsonNode.
     *
     * @param parentNode Parent node to search in
     * @param nodeKey    Key of the child node
     * @param logger     Logger for error reporting (nullable)
     * @return Optional containing the child JsonNode, or empty if not found
     */
    public static Optional<JsonNode> getJsonNode(JsonNode parentNode, String nodeKey, Logger logger) {
        if (parentNode == null) {
            logWarn(logger, "Parent node is null when searching for key '{}'", nodeKey);
            return Optional.empty();
        }

        JsonNode childNode = parentNode.get(nodeKey);
        if (childNode == null || childNode.isNull()) {
            logWarn(logger, "Key '{}' not found in JSON node", nodeKey);
            return Optional.empty();
        }

        return Optional.of(childNode);
    }

    /**
     * Extracts a non-empty text value from a JsonNode.
     *
     * @param node   JsonNode to extract text from
     * @param logger Logger for debug reporting (nullable)
     * @return Optional containing the trimmed text, or empty if null/blank
     */
    public static Optional<String> getNodeText(JsonNode node, Logger logger) {
        if (node == null || node.isNull()) {
            return Optional.empty();
        }

        String text = node.asText();
        if (text == null || text.isBlank()) {
            logDebug(logger, "Node text is null or blank");
            return Optional.empty();
        }

        return Optional.of(text.trim());
    }

    /**
     * Extracts a list of strings from a JsonNode array.
     *
     * @param arrayNode JsonNode that should be an array
     * @param logger    Logger for error reporting (nullable)
     * @return List of strings (empty if node is not an array or is empty)
     */
    public static List<String> getNodeArrayAsStringList(JsonNode arrayNode, Logger logger) {
        if (arrayNode == null || !arrayNode.isArray() || arrayNode.isEmpty()) {
            logDebug(logger, "Node is null, not an array, or empty");
            return Collections.emptyList();
        }

        List<String> result = new ArrayList<>(arrayNode.size());
        arrayNode.forEach(node -> {
            String text = node.asText();
            if (text != null && !text.isBlank()) {
                result.add(text.trim());
            }
        });

        return result;
    }

    // ========================================================================
    // Membership Utilities with Functional Interface
    // ========================================================================

    /**
     * Gets user IDs from memberships using a supplier for data access.
     * The supplier should return a collection of objects that have group/role information.
     *
     * @param membershipDataSupplier Supplier that provides membership data objects
     * @param groupIdExtractor       Function to extract group ID from membership object
     * @param roleIdExtractor        Function to extract role ID from membership object
     * @param identityAPI            Bonita Identity API
     * @param logger                 Logger for reporting (nullable)
     * @param <T>                    Type of membership data object
     * @return Set of user IDs (empty if none found)
     */
    public static <T> Set<Long> getUsersByMemberships(
            Supplier<List<T>> membershipDataSupplier,
            java.util.function.Function<T, Long> groupIdExtractor,
            java.util.function.Function<T, Long> roleIdExtractor,
            IdentityAPI identityAPI,
            Logger logger) {

        if (membershipDataSupplier == null || identityAPI == null) {
            return Collections.emptySet();
        }

        try {
            List<T> membershipData = membershipDataSupplier.get();
            if (membershipData == null || membershipData.isEmpty()) {
                logDebug(logger, "No membership data provided");
                return Collections.emptySet();
            }

            Set<Long> userIds = new HashSet<>();

            for (T data : membershipData) {
                Long groupId = groupIdExtractor.apply(data);
                Long roleId = roleIdExtractor.apply(data);

                Set<Long> users = getUsersForMembership(groupId, roleId, identityAPI, logger);
                userIds.addAll(users);
            }

            logDebug(logger, "Found {} unique users from {} memberships", userIds.size(), membershipData.size());
            return userIds;

        } catch (Exception e) {
            logWarn(logger, "Error getting users by memberships: {}", e.getMessage());
            return Collections.emptySet();
        }
    }

    /**
     * Gets user IDs for a specific group/role combination.
     * Handles three cases: group+role, group-only, role-only.
     *
     * @param groupId     Group ID (nullable)
     * @param roleId      Role ID (nullable)
     * @param identityAPI Bonita Identity API
     * @param logger      Logger for reporting (nullable)
     * @return Set of user IDs matching the membership criteria
     */
    public static Set<Long> getUsersForMembership(
            Long groupId,
            Long roleId,
            IdentityAPI identityAPI,
            Logger logger) {

        if (identityAPI == null) {
            return Collections.emptySet();
        }

        boolean hasGroup = groupId != null && groupId > 0;
        boolean hasRole = roleId != null && roleId > 0;

        if (!hasGroup && !hasRole) {
            logDebug(logger, "Both groupId and roleId are invalid");
            return Collections.emptySet();
        }

        try {
            SearchOptionsBuilder searchBuilder = new SearchOptionsBuilder(0, Integer.MAX_VALUE);
            searchBuilder.filter(UserSearchDescriptor.ENABLED, true);

            if (hasGroup && hasRole) {
                // Full membership: group + role
                logDebug(logger, "Searching users with groupId={} and roleId={}", groupId, roleId);
                searchBuilder.filter(UserSearchDescriptor.GROUP_ID, groupId);
                searchBuilder.and();
                searchBuilder.filter(UserSearchDescriptor.ROLE_ID, roleId);

            } else if (hasGroup) {
                // Group-only membership
                logDebug(logger, "Searching users in groupId={}", groupId);
                searchBuilder.filter(UserSearchDescriptor.GROUP_ID, groupId);

            } else {
                // Role-only membership
                logDebug(logger, "Searching users with roleId={}", roleId);
                searchBuilder.filter(UserSearchDescriptor.ROLE_ID, roleId);
            }

            SearchResult<User> result = identityAPI.searchUsers(searchBuilder.done());
            return result.getResult().stream()
                    .map(User::getId)
                    .collect(Collectors.toSet());

        } catch (Exception e) {
            logWarn(logger, "Error getting users for membership (group={}, role={}): {}",
                    groupId, roleId, e.getMessage());
            return Collections.emptySet();
        }
    }

    // ========================================================================
    // Step Instance Utilities
    // ========================================================================

    /**
     * Finds the most recent instance from a supplier result.
     * Generic method that works with any step instance type.
     *
     * @param instanceSupplier Supplier that executes the DAO query and returns a list
     * @param typeName         Name of the type for logging purposes
     * @param logger           Logger for reporting (nullable)
     * @param <T>              Type of the step instance
     * @return The first (most recent) instance, or null if none found
     */
    public static <T> T findMostRecentInstance(
            Supplier<List<T>> instanceSupplier,
            String typeName,
            Logger logger) {

        if (instanceSupplier == null) {
            return null;
        }

        try {
            List<T> instances = instanceSupplier.get();

            if (instances == null || instances.isEmpty()) {
                logDebug(logger, "No {} instances found", typeName);
                return null;
            }

            T mostRecent = instances.get(0);
            logDebug(logger, "Found most recent {} instance", typeName);
            return mostRecent;

        } catch (Exception e) {
            logWarn(logger, "Error finding most recent {}: {}", typeName, e.getMessage());
            return null;
        }
    }

    /**
     * Extracts a field value from a JSON string.
     *
     * @param jsonString JSON string to parse
     * @param fieldName  Field name to extract
     * @param logger     Logger for reporting (nullable)
     * @return Optional containing the field value as string, or empty if not found
     */
    public static Optional<String> extractFieldFromJson(String jsonString, String fieldName, Logger logger) {
        return parseJson(jsonString, logger)
                .flatMap(root -> getJsonNode(root, fieldName, logger))
                .flatMap(node -> getNodeText(node, logger));
    }

    // ========================================================================
    // Private Logging Helpers
    // ========================================================================

    private static void logDebug(Logger logger, String message, Object... args) {
        if (logger != null) {
            logger.debug(message, args);
        }
    }

    private static void logWarn(Logger logger, String message, Object... args) {
        if (logger != null) {
            logger.warn(message, args);
        }
    }
}