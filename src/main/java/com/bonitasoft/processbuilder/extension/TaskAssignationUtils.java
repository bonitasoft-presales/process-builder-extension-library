package com.bonitasoft.processbuilder.extension;

import com.bonitasoft.processbuilder.records.StepFieldRef;
import com.bonitasoft.processbuilder.records.UsersConfigRecord;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bonitasoft.engine.api.IdentityAPI;
import org.slf4j.Logger;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

/**
 * Utility class for task assignation operations.
 * <p>
 * Provides optimized methods to parse user configuration JSON and collect
 * candidate user IDs for task assignation filters in Bonita processes.
 * </p>
 * <p>
 * This class is designed to work without direct BDM dependencies. All BDM
 * access is performed through functional interfaces (Suppliers and Functions)
 * that are provided by the calling Groovy script.
 * </p>
 * <p>
 * Example usage in Groovy script:
 * </p>
 * <pre>{@code
 * UsersConfigRecord config = TaskAssignationUtils.parseUsersConfig(pbAction.content, logger)
 * Set<Long> userIds = TaskAssignationUtils.collectAllUserIds(
 *     config,
 *     { stepRef -> getMostRecentStepInstance(stepRef, processInstanceId, dao, logger) },
 *     { stepInstance -> IdentityUtils.getUserIdFromObject(stepInstance, "getUserId") },
 *     { membershipRefs -> pBUserListDAO.findByProcessIdAndRefMemberships(processId, membershipRefs, 0, Integer.MAX_VALUE) },
 *     identityAPI,
 *     logger
 * )
 * }</pre>
 *
 * @author Bonitasoft
 * @since 1.0
 */
public final class TaskAssignationUtils {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private TaskAssignationUtils() {
        throw new UnsupportedOperationException("This is a " + this.getClass().getSimpleName()
                + " class and cannot be instantiated.");
    }

    // ========================================================================
    // JSON Parsing Methods
    // ========================================================================

    /**
     * Parses JSON content and extracts the user configuration for task assignation.
     * <p>
     * Expected JSON structure:
     * </p>
     * <pre>{@code
     * {
     *   "users": {
     *     "stepUser": "step_xxx",
     *     "stepManager": "step_yyy",
     *     "memberShips": ["membership_1", "membership_2"],
     *     "membersShipsInput": "step_zzz:field_www"
     *   }
     * }
     * }</pre>
     *
     * @param jsonContent The JSON content string to parse (nullable)
     * @param logger      Logger for reporting (nullable)
     * @return UsersConfigRecord with parsed values, or empty config if parsing fails
     */
    public static UsersConfigRecord parseUsersConfig(String jsonContent, Logger logger) {
        if (jsonContent == null || jsonContent.isBlank()) {
            logWarn(logger, "JSON content is null or blank, returning empty users config");
            return UsersConfigRecord.empty();
        }

        try {
            JsonNode rootNode = OBJECT_MAPPER.readTree(jsonContent);
            if (rootNode == null) {
                logWarn(logger, "Failed to parse JSON content, returning empty users config");
                return UsersConfigRecord.empty();
            }

            JsonNode usersNode = rootNode.get(UsersConfigRecord.USERS_KEY);
            return UsersConfigRecord.fromUsersNode(usersNode, logger);

        } catch (JsonProcessingException e) {
            logWarn(logger, "JSON parsing error: {}", e.getMessage());
            return UsersConfigRecord.empty();
        }
    }

    /**
     * Parses JSON content and returns the "users" node directly.
     * <p>
     * This method is useful when you need more control over the JSON parsing
     * or want to access other nodes in the JSON.
     * </p>
     *
     * @param jsonContent The JSON content string to parse (nullable)
     * @param logger      Logger for reporting (nullable)
     * @return Optional containing the users JsonNode, or empty if not found
     */
    public static Optional<JsonNode> parseUsersNode(String jsonContent, Logger logger) {
        if (jsonContent == null || jsonContent.isBlank()) {
            return Optional.empty();
        }

        try {
            JsonNode rootNode = OBJECT_MAPPER.readTree(jsonContent);
            if (rootNode == null) {
                return Optional.empty();
            }

            JsonNode usersNode = rootNode.get(UsersConfigRecord.USERS_KEY);
            if (usersNode == null || usersNode.isNull()) {
                logWarn(logger, "Key '{}' not found in JSON", UsersConfigRecord.USERS_KEY);
                return Optional.empty();
            }

            return Optional.of(usersNode);

        } catch (JsonProcessingException e) {
            logWarn(logger, "JSON parsing error: {}", e.getMessage());
            return Optional.empty();
        }
    }

    // ========================================================================
    // User ID Collection Methods
    // ========================================================================

    /**
     * Collects all candidate user IDs based on the user configuration.
     * <p>
     * This method processes all user sources defined in the configuration:
     * </p>
     * <ol>
     *   <li>stepUser - User who executed a specific step</li>
     *   <li>stepManager - Manager of the user who executed a specific step</li>
     *   <li>memberShips - Users from static membership references</li>
     *   <li>membersShipsInput - Users from dynamically retrieved membership</li>
     * </ol>
     * <p>
     * The method uses functional interfaces to access BDM data, ensuring no
     * direct BDM dependencies in this library.
     * </p>
     *
     * @param <T>                Type of the step instance object
     * @param <M>                Type of the membership list object
     * @param config             The parsed user configuration
     * @param stepInstanceFinder Function that finds a step instance by reference
     * @param userIdExtractor    Function that extracts user ID from a step instance
     * @param membershipFinder   Function that finds membership objects by reference array
     * @param identityAPI        Bonita Identity API for user lookups
     * @param logger             Logger for reporting (nullable)
     * @return Set of unique candidate user IDs (never null, may be empty)
     */
    public static <T, M> Set<Long> collectAllUserIds(
            UsersConfigRecord config,
            Function<String, T> stepInstanceFinder,
            Function<T, Long> userIdExtractor,
            Function<String[], List<M>> membershipFinder,
            IdentityAPI identityAPI,
            Logger logger) {

        if (config == null || !config.hasAnySource()) {
            logDebug(logger, "No user sources defined in configuration");
            return Collections.emptySet();
        }

        Set<Long> userIds = new HashSet<>();

        // Process stepUser
        processStepUser(config, stepInstanceFinder, userIdExtractor, logger)
                .ifPresent(userId -> {
                    userIds.add(userId);
                    logInfo(logger, "Added stepUser ID: {}", userId);
                });

        // Process stepManager
        processStepManager(config, stepInstanceFinder, userIdExtractor, identityAPI, logger)
                .ifPresent(managerId -> {
                    userIds.add(managerId);
                    logInfo(logger, "Added stepManager ID: {}", managerId);
                });

        // Process static memberShips
        if (config.hasMemberShips()) {
            Set<Long> membershipUsers = processMemberships(
                    config.memberShips(), membershipFinder, identityAPI, logger);
            userIds.addAll(membershipUsers);
            logInfo(logger, "Added {} users from static memberShips", membershipUsers.size());
        }

        // Process dynamic membersShipsInput
        processDynamicMembership(config, stepInstanceFinder, userIdExtractor, membershipFinder,
                identityAPI, logger, userIds);

        logInfo(logger, "Total unique candidate user IDs collected: {}", userIds.size());
        return userIds;
    }

    /**
     * Processes the stepUser configuration and returns the user ID.
     *
     * @param <T>                Type of the step instance object
     * @param config             The parsed user configuration
     * @param stepInstanceFinder Function that finds a step instance by reference
     * @param userIdExtractor    Function that extracts user ID from a step instance
     * @param logger             Logger for reporting (nullable)
     * @return Optional containing the step user ID, or empty if not found
     */
    public static <T> Optional<Long> processStepUser(
            UsersConfigRecord config,
            Function<String, T> stepInstanceFinder,
            Function<T, Long> userIdExtractor,
            Logger logger) {

        if (config == null || !config.hasStepUser()) {
            return Optional.empty();
        }

        String stepRef = config.stepUser();
        logDebug(logger, "Processing stepUser reference: {}", stepRef);

        return findStepInstanceAndExtractUserId(stepRef, stepInstanceFinder, userIdExtractor, logger);
    }

    /**
     * Processes the stepManager configuration and returns the manager's user ID.
     *
     * @param <T>                Type of the step instance object
     * @param config             The parsed user configuration
     * @param stepInstanceFinder Function that finds a step instance by reference
     * @param userIdExtractor    Function that extracts user ID from a step instance
     * @param identityAPI        Bonita Identity API for manager lookup
     * @param logger             Logger for reporting (nullable)
     * @return Optional containing the manager user ID, or empty if not found
     */
    public static <T> Optional<Long> processStepManager(
            UsersConfigRecord config,
            Function<String, T> stepInstanceFinder,
            Function<T, Long> userIdExtractor,
            IdentityAPI identityAPI,
            Logger logger) {

        if (config == null || !config.hasStepManager()) {
            return Optional.empty();
        }

        String stepRef = config.stepManager();
        logDebug(logger, "Processing stepManager reference: {}", stepRef);

        Optional<Long> stepUserIdOpt = findStepInstanceAndExtractUserId(
                stepRef, stepInstanceFinder, userIdExtractor, logger);

        if (stepUserIdOpt.isEmpty()) {
            logWarn(logger, "No user found for stepManager reference: {}", stepRef);
            return Optional.empty();
        }

        Long stepUserId = stepUserIdOpt.get();
        Long managerId = IdentityUtils.getUserManager(stepUserId, identityAPI);

        if (managerId == null) {
            logWarn(logger, "No manager found for user ID: {}", stepUserId);
            return Optional.empty();
        }

        return Optional.of(managerId);
    }

    /**
     * Processes a list of membership references and returns matching user IDs.
     *
     * @param <M>              Type of the membership list object
     * @param membershipRefs   List of membership reference strings
     * @param membershipFinder Function that finds membership objects by reference array
     * @param identityAPI      Bonita Identity API for user lookups
     * @param logger           Logger for reporting (nullable)
     * @return Set of user IDs from the memberships (never null)
     */
    public static <M> Set<Long> processMemberships(
            List<String> membershipRefs,
            Function<String[], List<M>> membershipFinder,
            IdentityAPI identityAPI,
            Logger logger) {

        if (membershipRefs == null || membershipRefs.isEmpty()) {
            return Collections.emptySet();
        }

        try {
            String[] refArray = membershipRefs.toArray(new String[0]);
            logDebug(logger, "Processing {} membership references", refArray.length);

            List<M> membershipList = membershipFinder.apply(refArray);

            if (membershipList == null || membershipList.isEmpty()) {
                logDebug(logger, "No membership objects found for references");
                return Collections.emptySet();
            }

            logDebug(logger, "Found {} membership objects", membershipList.size());

            // Use IdentityUtils to get users by memberships (uses reflection)
            return IdentityUtils.getUsersByMemberships((List<?>) membershipList, identityAPI);

        } catch (Exception e) {
            logWarn(logger, "Error processing memberships: {}", e.getMessage());
            return Collections.emptySet();
        }
    }

    /**
     * Processes a single membership reference and returns matching user IDs.
     * <p>
     * This is a convenience method for processing a single membership reference
     * instead of a list.
     * </p>
     *
     * @param <M>              Type of the membership list object
     * @param membershipRef    Single membership reference string
     * @param membershipFinder Function that finds membership objects by reference array
     * @param identityAPI      Bonita Identity API for user lookups
     * @param logger           Logger for reporting (nullable)
     * @return Set of user IDs from the membership (never null)
     */
    public static <M> Set<Long> processSingleMembership(
            String membershipRef,
            Function<String[], List<M>> membershipFinder,
            IdentityAPI identityAPI,
            Logger logger) {

        if (membershipRef == null || membershipRef.isBlank()) {
            return Collections.emptySet();
        }

        return processMemberships(List.of(membershipRef.trim()), membershipFinder, identityAPI, logger);
    }

    // ========================================================================
    // Dynamic Membership Methods
    // ========================================================================

    /**
     * Extracts a membership ID from a step's JSON input field.
     * <p>
     * This method parses the step's jsonInput and extracts the value of the
     * specified field. The step and field are specified in the stepFieldRef
     * parameter in format "step_xxx:field_yyy".
     * </p>
     *
     * @param <T>                Type of the step instance object
     * @param stepFieldRefString The step:field reference in format "step_xxx:field_yyy"
     * @param stepInstanceFinder Function that finds a step instance by reference
     * @param jsonInputExtractor Function that extracts the jsonInput string from a step instance
     * @param logger             Logger for reporting (nullable)
     * @return Optional containing the membership ID, or empty if not found
     */
    public static <T> Optional<String> extractMembershipFromStepInput(
            String stepFieldRefString,
            Function<String, T> stepInstanceFinder,
            Function<T, String> jsonInputExtractor,
            Logger logger) {

        if (stepFieldRefString == null || stepFieldRefString.isBlank()) {
            return Optional.empty();
        }

        // Parse the step:field reference
        StepFieldRef stepFieldRef = StepFieldRef.parse(stepFieldRefString);
        if (stepFieldRef == null) {
            logWarn(logger, "Invalid stepFieldRef format: {}", stepFieldRefString);
            return Optional.empty();
        }

        logDebug(logger, "Extracting membership from step '{}' field '{}'",
                stepFieldRef.stepRef(), stepFieldRef.fieldRef());

        // Find the step instance
        T stepInstance = stepInstanceFinder.apply(stepFieldRef.stepRef());
        if (stepInstance == null) {
            logWarn(logger, "No step instance found for reference: {}", stepFieldRef.stepRef());
            return Optional.empty();
        }

        // Extract the jsonInput from the step instance
        String jsonInput = jsonInputExtractor.apply(stepInstance);
        if (jsonInput == null || jsonInput.isBlank()) {
            logWarn(logger, "jsonInput is empty for step '{}'", stepFieldRef.stepRef());
            return Optional.empty();
        }

        // Parse jsonInput and extract the field value
        return extractFieldFromJson(jsonInput, stepFieldRef.fieldRef(), logger);
    }

    /**
     * Extracts a field value from a JSON string.
     *
     * @param jsonString JSON string to parse
     * @param fieldName  Field name to extract
     * @param logger     Logger for reporting (nullable)
     * @return Optional containing the field value, or empty if not found
     */
    public static Optional<String> extractFieldFromJson(String jsonString, String fieldName, Logger logger) {
        if (jsonString == null || jsonString.isBlank()) {
            return Optional.empty();
        }

        try {
            JsonNode rootNode = OBJECT_MAPPER.readTree(jsonString);
            if (rootNode == null) {
                return Optional.empty();
            }

            JsonNode fieldNode = rootNode.get(fieldName);
            if (fieldNode == null || fieldNode.isNull()) {
                logWarn(logger, "Field '{}' not found in JSON", fieldName);
                return Optional.empty();
            }

            String value = fieldNode.asText();
            if (value == null || value.isBlank()) {
                logDebug(logger, "Field '{}' is blank", fieldName);
                return Optional.empty();
            }

            return Optional.of(value.trim());

        } catch (JsonProcessingException e) {
            logWarn(logger, "Error parsing JSON to extract field '{}': {}", fieldName, e.getMessage());
            return Optional.empty();
        }
    }

    // ========================================================================
    // Convenience Methods for Complete Flow
    // ========================================================================

    /**
     * Complete flow: parses JSON and collects all user IDs.
     * <p>
     * This is a convenience method that combines JSON parsing and user ID collection
     * in a single call.
     * </p>
     *
     * @param <T>                Type of the step instance object
     * @param <M>                Type of the membership list object
     * @param jsonContent        The JSON content string containing user configuration
     * @param stepInstanceFinder Function that finds a step instance by reference
     * @param userIdExtractor    Function that extracts user ID from a step instance
     * @param membershipFinder   Function that finds membership objects by reference array
     * @param identityAPI        Bonita Identity API for user lookups
     * @param logger             Logger for reporting (nullable)
     * @return Set of unique candidate user IDs (never null)
     */
    public static <T, M> Set<Long> parseAndCollectUserIds(
            String jsonContent,
            Function<String, T> stepInstanceFinder,
            Function<T, Long> userIdExtractor,
            Function<String[], List<M>> membershipFinder,
            IdentityAPI identityAPI,
            Logger logger) {

        UsersConfigRecord config = parseUsersConfig(jsonContent, logger);
        return collectAllUserIds(config, stepInstanceFinder, userIdExtractor, membershipFinder,
                identityAPI, logger);
    }

    /**
     * Complete flow with dynamic membership extraction.
     * <p>
     * This version also handles the membersShipsInput field which requires
     * extracting a membership reference from a step's input JSON.
     * </p>
     *
     * @param <T>                Type of the step instance object
     * @param <M>                Type of the membership list object
     * @param jsonContent        The JSON content string containing user configuration
     * @param stepInstanceFinder Function that finds a step instance by reference
     * @param userIdExtractor    Function that extracts user ID from a step instance
     * @param jsonInputExtractor Function that extracts jsonInput from a step instance
     * @param membershipFinder   Function that finds membership objects by reference array
     * @param identityAPI        Bonita Identity API for user lookups
     * @param logger             Logger for reporting (nullable)
     * @return Set of unique candidate user IDs (never null)
     */
    public static <T, M> Set<Long> parseAndCollectUserIdsWithDynamicMembership(
            String jsonContent,
            Function<String, T> stepInstanceFinder,
            Function<T, Long> userIdExtractor,
            Function<T, String> jsonInputExtractor,
            Function<String[], List<M>> membershipFinder,
            IdentityAPI identityAPI,
            Logger logger) {

        UsersConfigRecord config = parseUsersConfig(jsonContent, logger);

        if (config == null || !config.hasAnySource()) {
            return Collections.emptySet();
        }

        Set<Long> userIds = new HashSet<>();

        // Process stepUser
        processStepUser(config, stepInstanceFinder, userIdExtractor, logger)
                .ifPresent(userIds::add);

        // Process stepManager
        processStepManager(config, stepInstanceFinder, userIdExtractor, identityAPI, logger)
                .ifPresent(userIds::add);

        // Process static memberShips
        if (config.hasMemberShips()) {
            userIds.addAll(processMemberships(config.memberShips(), membershipFinder, identityAPI, logger));
        }

        // Process dynamic membersShipsInput
        if (config.hasMembersShipsInput()) {
            extractMembershipFromStepInput(config.membersShipsInput(), stepInstanceFinder,
                    jsonInputExtractor, logger)
                    .ifPresent(membershipId -> {
                        logInfo(logger, "Extracted dynamic membership ID: {}", membershipId);
                        Set<Long> dynamicUsers = processSingleMembership(
                                membershipId, membershipFinder, identityAPI, logger);
                        userIds.addAll(dynamicUsers);
                        logInfo(logger, "Added {} users from dynamic membership", dynamicUsers.size());
                    });
        }

        logInfo(logger, "Total unique candidate user IDs: {}", userIds.size());
        return userIds;
    }

    // ========================================================================
    // Private Helper Methods
    // ========================================================================

    private static <T> Optional<Long> findStepInstanceAndExtractUserId(
            String stepRef,
            Function<String, T> stepInstanceFinder,
            Function<T, Long> userIdExtractor,
            Logger logger) {

        if (stepRef == null || stepRef.isBlank()) {
            return Optional.empty();
        }

        T stepInstance = stepInstanceFinder.apply(stepRef);
        if (stepInstance == null) {
            logWarn(logger, "No step instance found for reference: {}", stepRef);
            return Optional.empty();
        }

        Long userId = userIdExtractor.apply(stepInstance);
        if (userId == null || userId <= 0) {
            logWarn(logger, "No valid user ID found in step instance for reference: {}", stepRef);
            return Optional.empty();
        }

        return Optional.of(userId);
    }

    private static <T, M> void processDynamicMembership(
            UsersConfigRecord config,
            Function<String, T> stepInstanceFinder,
            Function<T, Long> userIdExtractor,
            Function<String[], List<M>> membershipFinder,
            IdentityAPI identityAPI,
            Logger logger,
            Set<Long> userIds) {

        if (!config.hasMembersShipsInput()) {
            return;
        }

        Optional<StepFieldRef> stepFieldRefOpt = config.parseMembersShipsInput();
        if (stepFieldRefOpt.isEmpty()) {
            logWarn(logger, "Could not parse membersShipsInput: {}", config.membersShipsInput());
            return;
        }

        StepFieldRef stepFieldRef = stepFieldRefOpt.get();
        logDebug(logger, "Processing dynamic membership from step '{}' field '{}'",
                stepFieldRef.stepRef(), stepFieldRef.fieldRef());

        // Find the step instance
        T stepInstance = stepInstanceFinder.apply(stepFieldRef.stepRef());
        if (stepInstance == null) {
            logWarn(logger, "No step instance found for dynamic membership reference: {}",
                    stepFieldRef.stepRef());
            return;
        }

        // For dynamic membership, we need to extract jsonInput from the step instance
        // This requires the step instance to have a getJsonInput() method
        String jsonInput = extractJsonInputFromStepInstance(stepInstance, logger);
        if (jsonInput == null || jsonInput.isBlank()) {
            logWarn(logger, "No jsonInput found in step instance for dynamic membership");
            return;
        }

        // Extract the membership ID from jsonInput
        Optional<String> membershipIdOpt = extractFieldFromJson(jsonInput, stepFieldRef.fieldRef(), logger);
        if (membershipIdOpt.isEmpty()) {
            logWarn(logger, "Could not extract membership ID from field '{}'", stepFieldRef.fieldRef());
            return;
        }

        String membershipId = membershipIdOpt.get();
        logInfo(logger, "Extracted dynamic membership ID: {}", membershipId);

        // Process the dynamic membership
        Set<Long> dynamicUsers = processSingleMembership(membershipId, membershipFinder, identityAPI, logger);
        userIds.addAll(dynamicUsers);
        logInfo(logger, "Added {} users from dynamic membersShipsInput", dynamicUsers.size());
    }

    private static <T> String extractJsonInputFromStepInstance(T stepInstance, Logger logger) {
        if (stepInstance == null) {
            return null;
        }

        try {
            java.lang.reflect.Method method = stepInstance.getClass().getMethod("getJsonInput");
            Object result = method.invoke(stepInstance);
            return result != null ? result.toString() : null;
        } catch (NoSuchMethodException e) {
            logDebug(logger, "Step instance does not have getJsonInput() method");
            return null;
        } catch (Exception e) {
            logWarn(logger, "Error extracting jsonInput from step instance: {}", e.getMessage());
            return null;
        }
    }

    // ========================================================================
    // Logging Helpers
    // ========================================================================

    private static void logDebug(Logger logger, String message, Object... args) {
        if (logger != null && logger.isDebugEnabled()) {
            logger.debug(message, args);
        }
    }

    private static void logInfo(Logger logger, String message, Object... args) {
        if (logger != null && logger.isInfoEnabled()) {
            logger.info(message, args);
        }
    }

    private static void logWarn(Logger logger, String message, Object... args) {
        if (logger != null) {
            logger.warn(message, args);
        }
    }
}
