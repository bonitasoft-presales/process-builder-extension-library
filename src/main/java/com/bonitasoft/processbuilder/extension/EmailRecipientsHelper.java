package com.bonitasoft.processbuilder.extension;

import com.bonitasoft.processbuilder.enums.ActionParameterType;
import com.fasterxml.jackson.databind.JsonNode;

import org.bonitasoft.engine.api.IdentityAPI;
import org.bonitasoft.engine.identity.ContactData;
import org.bonitasoft.engine.identity.User;
import org.bonitasoft.engine.identity.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Utility class for retrieving email addresses from Bonita Identity API.
 * <p>
 * This class provides thread-safe and stateless methods for:
 * </p>
 * <ul>
 *   <li>Retrieving email addresses by user ID or manager ID</li>
 *   <li>Extracting recipient information from JSON action parameters</li>
 *   <li>Processing user IDs from step execution results (DAO-independent)</li>
 *   <li>Processing membership-based user lookups (DAO-independent)</li>
 *   <li>Validating and filtering email addresses</li>
 * </ul>
 *
 * <p><b>Design Philosophy:</b> All methods receive their dependencies as parameters,
 * making them easily testable and independent of specific DAO implementations.
 * Methods that process step or membership data accept generic types with extractor
 * functions, allowing scripts to pass BDM objects without compile-time dependencies.</p>
 *
 * <p><b>Usage Example (Groovy Script):</b></p>
 * <pre>{@code
 * // Extract userId from step results
 * def steps = pBStepProcessInstanceDAO.findLastByRefStepAndRootProcessInstanceId(
 *     rootProcessInstanceId, stepIdParam, 0, 1)
 * Long userId = EmailRecipientsHelper.extractUserIdFromFirstStep(steps, { it.userId })
 *
 * // Get email for user
 * Optional<String> email = EmailRecipientsHelper.getEmailByUserId(identityAPI, userId)
 * }</pre>
 *
 * @author Bonitasoft
 * @since 1.0
 * @see ActionParameterType
 * @see IdentityUtils
 */
public final class EmailRecipientsHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailRecipientsHelper.class);

    private EmailRecipientsHelper() {
        // Utility class - prevent instantiation
    }

    // ═══════════════════════════════════════════════════════════════════
    // EMAIL RETRIEVAL METHODS
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Retrieves email for a single user ID.
     *
     * @param identityAPI the Bonita Identity API
     * @param userId      the user ID (must be positive)
     * @return Optional containing the email if found and valid
     */
    public static Optional<String> getEmailByUserId(IdentityAPI identityAPI, Long userId) {
        Objects.requireNonNull(identityAPI, "IdentityAPI cannot be null");

        if (!isValidUserId(userId)) {
            LOGGER.debug("Invalid userId provided: {}", userId);
            return Optional.empty();
        }

        try {
            ContactData contactData = identityAPI.getUserContactData(userId, false);
            return extractEmail(contactData);
        } catch (UserNotFoundException e) {
            LOGGER.warn("User not found for userId: {}", userId);
        } catch (Exception e) {
            LOGGER.error("Error retrieving contact data for userId: {}", userId, e);
        }
        return Optional.empty();
    }

    /**
     * Retrieves manager's email for a given user ID.
     *
     * @param identityAPI the Bonita Identity API
     * @param userId      the user ID whose manager email is requested
     * @return Optional containing the manager's email if found
     */
    public static Optional<String> getManagerEmailByUserId(IdentityAPI identityAPI, Long userId) {
        Objects.requireNonNull(identityAPI, "IdentityAPI cannot be null");

        if (!isValidUserId(userId)) {
            LOGGER.debug("Invalid userId for manager lookup: {}", userId);
            return Optional.empty();
        }

        try {
            User user = identityAPI.getUser(userId);
            long managerId = user.getManagerUserId();

            if (managerId <= 0) {
                LOGGER.debug("User {} has no manager assigned", userId);
                return Optional.empty();
            }

            return getEmailByUserId(identityAPI, managerId);
        } catch (UserNotFoundException e) {
            LOGGER.warn("User not found for manager lookup, userId: {}", userId);
        } catch (Exception e) {
            LOGGER.error("Error retrieving manager for userId: {}", userId, e);
        }
        return Optional.empty();
    }

    /**
     * Retrieves emails for multiple user IDs in batch.
     *
     * @param identityAPI the Bonita Identity API
     * @param userIds     collection of user IDs
     * @return Set of unique, valid email addresses (preserves insertion order)
     */
    public static Set<String> getEmailsByUserIds(IdentityAPI identityAPI, Collection<Long> userIds) {
        Objects.requireNonNull(identityAPI, "IdentityAPI cannot be null");

        if (userIds == null || userIds.isEmpty()) {
            LOGGER.debug("Empty or null userIds collection provided");
            return Collections.emptySet();
        }

        return userIds.stream()
                .filter(EmailRecipientsHelper::isValidUserId)
                .distinct()
                .map(userId -> getEmailByUserId(identityAPI, userId))
                .flatMap(Optional::stream)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    // ═══════════════════════════════════════════════════════════════════
    // JSON PARAMETER EXTRACTION METHODS
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Extracts user IDs from JSON parameters (RECIPIENTS_USER_IDS).
     *
     * @param parameters the JSON parameters node
     * @return List of valid user IDs
     */
    public static List<Long> extractUserIdsFromParameters(JsonNode parameters) {
        String userIdsParam = ActionParameterType.RECIPIENTS_USER_IDS.getKey();
        JsonNode userIdsNode = JsonNodeUtils.getValueByPath(parameters, userIdsParam);

        if (userIdsNode == null || !userIdsNode.isArray()) {
            LOGGER.debug("No valid userIds array found in parameters");
            return Collections.emptyList();
        }

        List<Long> userIds = StreamSupport.stream(userIdsNode.spliterator(), false)
                .map(JsonNode::asLong)
                .filter(EmailRecipientsHelper::isValidUserId)
                .collect(Collectors.toList());

        LOGGER.info("Extracted {} valid userIds from parameters", userIds.size());
        return userIds;
    }

    /**
     * Extracts membership reference IDs from JSON parameters.
     *
     * @param parameters the JSON parameters node
     * @return Array of membership reference strings
     */
    public static String[] extractMembershipRefs(JsonNode parameters) {
        String membershipParam = ActionParameterType.RECIPIENTS_MEMBERSHIP_IDS.getKey();
        JsonNode membershipNode = JsonNodeUtils.getValueByPath(parameters, membershipParam);

        if (membershipNode == null || !membershipNode.isArray()) {
            LOGGER.debug("No valid membership array found in parameters");
            return new String[0];
        }

        String[] refs = StreamSupport.stream(membershipNode.spliterator(), false)
                .map(JsonNode::asText)
                .filter(text -> text != null && !text.isBlank())
                .toArray(String[]::new);

        LOGGER.debug("Extracted {} membership references", refs.length);
        return refs;
    }

    /**
     * Extracts specific email addresses from JSON parameters.
     *
     * @param parameters the JSON parameters node
     * @return Set of valid email addresses
     */
    public static Set<String> extractSpecificEmails(JsonNode parameters) {
        String specificParam = ActionParameterType.RECIPIENTS_SPECIFIC_EMAILS.getKey();
        JsonNode specificNode = JsonNodeUtils.getValueByPath(parameters, specificParam);

        if (specificNode == null || !specificNode.isArray()) {
            LOGGER.debug("No valid specific emails array found");
            return Collections.emptySet();
        }

        Set<String> emails = StreamSupport.stream(specificNode.spliterator(), false)
                .map(JsonNode::asText)
                .filter(EmailRecipientsHelper::isValidEmail)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        LOGGER.info("Extracted {} specific emails", emails.size());
        return emails;
    }

    /**
     * Gets the step ID parameter key.
     *
     * @return the step ID parameter key
     */
    public static String getStepIdParameterKey() {
        return ActionParameterType.RECIPIENTS_STEP_ID.getKey();
    }

    // ═══════════════════════════════════════════════════════════════════
    // PROCESSING METHODS (for use with extracted user IDs)
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Processes user IDs from parameters and returns their emails.
     *
     * @param identityAPI the Bonita Identity API
     * @param parameters  the JSON parameters node
     * @return Set of email addresses
     */
    public static Set<String> processUsersRecipients(IdentityAPI identityAPI, JsonNode parameters) {
        List<Long> userIds = extractUserIdsFromParameters(parameters);
        Set<String> emails = getEmailsByUserIds(identityAPI, userIds);
        LOGGER.info("USERS - Processed {} emails from {} userIds", emails.size(), userIds.size());
        return emails;
    }

    /**
     * Processes a single user ID and adds email (direct or manager) to the set.
     *
     * @param emails       the set to add the email to
     * @param identityAPI  the Bonita Identity API
     * @param userId       the user ID
     * @param fetchManager if true, fetch manager's email; otherwise fetch user's email
     */
    public static void addEmailForUser(
            Set<String> emails,
            IdentityAPI identityAPI,
            Long userId,
            boolean fetchManager) {

        if (!isValidUserId(userId)) {
            LOGGER.debug("Invalid userId, skipping email lookup");
            return;
        }

        Optional<String> emailOpt = fetchManager
                ? getManagerEmailByUserId(identityAPI, userId)
                : getEmailByUserId(identityAPI, userId);

        emailOpt.ifPresent(email -> {
            emails.add(email);
            LOGGER.debug("Added {} email: {}", fetchManager ? "manager" : "user", email);
        });
    }

    // ═══════════════════════════════════════════════════════════════════
    // UTILITY METHODS
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Filters and validates a collection of email strings.
     *
     * @param emails collection of email strings (may contain nulls/blanks)
     * @return Set of valid, non-blank email addresses
     */
    public static Set<String> filterValidEmails(Collection<String> emails) {
        if (emails == null || emails.isEmpty()) {
            return Collections.emptySet();
        }

        return emails.stream()
                .filter(EmailRecipientsHelper::isValidEmail)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    /**
     * Joins email addresses into a comma-separated string.
     *
     * @param emails collection of emails
     * @return comma-separated string of unique emails
     */
    public static String joinEmails(Collection<String> emails) {
        if (emails == null || emails.isEmpty()) {
            return "";
        }
        return emails.stream()
                .filter(EmailRecipientsHelper::isValidEmail)
                .distinct()
                .collect(Collectors.joining(", "));
    }

    /**
     * Validates if a userId is valid (non-null and positive).
     *
     * @param userId the user ID to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidUserId(Long userId) {
        return userId != null && userId > 0;
    }

    /**
     * Validates if an email string is valid (non-null and non-blank).
     *
     * @param email the email to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidEmail(String email) {
        return email != null && !email.isBlank();
    }

    // ═══════════════════════════════════════════════════════════════════
    // DAO-INDEPENDENT EXTRACTION METHODS
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Extracts the user ID from the first element of a step results list.
     * <p>
     * This method is designed to work with DAO query results without requiring
     * a compile-time dependency on BDM types. It accepts a generic list and a
     * function to extract the user ID from each element.
     * </p>
     *
     * <p><b>Usage Example (Groovy Script):</b></p>
     * <pre>{@code
     * // Query the DAO for step instances
     * def steps = pBStepProcessInstanceDAO.findLastByRefStepAndRootProcessInstanceId(
     *     rootProcessInstanceId,
     *     EmailRecipientsHelper.getStepIdParameterKey(),
     *     0, 1
     * )
     *
     * // Extract userId using a closure as the extractor function
     * Long userId = EmailRecipientsHelper.extractUserIdFromFirstStep(steps, { step -> step.userId })
     *
     * // Alternative syntax with property access
     * Long userId = EmailRecipientsHelper.extractUserIdFromFirstStep(steps, { it.userId })
     * }</pre>
     *
     * @param <T>             the type of elements in the list (e.g., PBStepProcessInstance)
     * @param steps           the list of step process instances from a DAO query (may be null or empty)
     * @param userIdExtractor a function that extracts the user ID from a step object
     * @return the user ID from the first step, or {@code null} if the list is null/empty
     *         or the extractor returns null
     */
    public static <T> Long extractUserIdFromFirstStep(List<T> steps, Function<T, Long> userIdExtractor) {
        if (steps == null || steps.isEmpty()) {
            LOGGER.debug("No step instances provided for userId extraction");
            return null;
        }

        Objects.requireNonNull(userIdExtractor, "userIdExtractor function cannot be null");

        T firstStep = steps.get(0);
        Long userId = userIdExtractor.apply(firstStep);

        LOGGER.debug("Extracted userId from first step: {}", userId);
        return userId;
    }

    /**
     * Extracts user IDs from a collection of membership-based user list objects.
     * <p>
     * This method processes membership query results and extracts unique user IDs
     * using the provided extractor function. It's designed to work with BDM objects
     * like PBUserList without requiring compile-time dependencies.
     * </p>
     *
     * <p><b>Usage Example (Groovy Script):</b></p>
     * <pre>{@code
     * // Get membership references from action parameters
     * String[] refMemberships = EmailRecipientsHelper.extractMembershipRefs(pbActionContent.parameters)
     *
     * // Query user lists from DAO
     * def userLists = pBUserListDAO.findByProcessIdAndRefMemberships(
     *     processId, refMemberships, 0, Integer.MAX_VALUE
     * )
     *
     * // Extract user IDs using a closure
     * Set<Long> userIds = EmailRecipientsHelper.extractUserIdsFromMembershipResults(
     *     userLists,
     *     { userList -> userList.userId }
     * )
     *
     * // Get emails for all extracted user IDs
     * Set<String> emails = EmailRecipientsHelper.getEmailsByUserIds(identityAPI, userIds)
     * }</pre>
     *
     * @param <T>             the type of elements in the collection (e.g., PBUserList)
     * @param userLists       the collection of user list objects from a DAO query (may be null or empty)
     * @param userIdExtractor a function that extracts the user ID from each user list object
     * @return a set of unique, valid user IDs extracted from the user lists;
     *         returns an empty set if the input is null/empty
     */
    public static <T> Set<Long> extractUserIdsFromMembershipResults(
            Collection<T> userLists,
            Function<T, Long> userIdExtractor) {

        if (userLists == null || userLists.isEmpty()) {
            LOGGER.debug("No user lists provided for membership userId extraction");
            return Collections.emptySet();
        }

        Objects.requireNonNull(userIdExtractor, "userIdExtractor function cannot be null");

        Set<Long> userIds = userLists.stream()
                .map(userIdExtractor)
                .filter(EmailRecipientsHelper::isValidUserId)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        LOGGER.debug("Extracted {} unique userIds from {} membership entries", userIds.size(), userLists.size());
        return userIds;
    }

    /**
     * Processes step-based recipients and retrieves their email addresses.
     * <p>
     * This is a convenience method that combines step user ID extraction with email retrieval.
     * It supports both direct user emails and manager emails based on the {@code fetchManager} flag.
     * </p>
     *
     * <p><b>Usage Example (Groovy Script):</b></p>
     * <pre>{@code
     * // For STEP_USERS recipients type
     * def steps = pBStepProcessInstanceDAO.findLastByRefStepAndRootProcessInstanceId(
     *     rootProcessInstanceId, stepIdParam, 0, 1
     * )
     * Set<String> emails = EmailRecipientsHelper.processStepBasedRecipients(
     *     identityAPI, steps, { it.userId }, false  // false = user email
     * )
     *
     * // For STEP_MANAGERS recipients type
     * Set<String> managerEmails = EmailRecipientsHelper.processStepBasedRecipients(
     *     identityAPI, steps, { it.userId }, true  // true = manager email
     * )
     * }</pre>
     *
     * @param <T>             the type of elements in the steps list
     * @param identityAPI     the Bonita Identity API for email lookup
     * @param steps           the list of step process instances from a DAO query
     * @param userIdExtractor a function that extracts the user ID from a step object
     * @param fetchManager    if {@code true}, retrieves the manager's email; otherwise retrieves the user's email
     * @return a set containing the email address (empty if not found or invalid)
     */
    public static <T> Set<String> processStepBasedRecipients(
            IdentityAPI identityAPI,
            List<T> steps,
            Function<T, Long> userIdExtractor,
            boolean fetchManager) {

        Objects.requireNonNull(identityAPI, "IdentityAPI cannot be null");

        Long userId = extractUserIdFromFirstStep(steps, userIdExtractor);

        if (!isValidUserId(userId)) {
            LOGGER.debug("No valid userId extracted from steps for {} lookup",
                    fetchManager ? "manager" : "user");
            return Collections.emptySet();
        }

        Set<String> emails = new LinkedHashSet<>();
        addEmailForUser(emails, identityAPI, userId, fetchManager);

        LOGGER.info("{} - Processed email for userId: {}, found: {}",
                fetchManager ? "STEP_MANAGERS" : "STEP_USERS",
                userId,
                !emails.isEmpty());

        return emails;
    }

    /**
     * Processes membership-based recipients and retrieves their email addresses.
     * <p>
     * This is a convenience method that combines membership user ID extraction with
     * batch email retrieval. It processes all user IDs from the membership results
     * and returns their corresponding email addresses.
     * </p>
     *
     * <p><b>Usage Example (Groovy Script):</b></p>
     * <pre>{@code
     * // Get membership references from parameters
     * String[] refMemberships = EmailRecipientsHelper.extractMembershipRefs(pbActionContent.parameters)
     *
     * // Query user lists
     * def userLists = pBUserListDAO.findByProcessIdAndRefMemberships(
     *     processId, refMemberships, 0, Integer.MAX_VALUE
     * )
     *
     * // Process and get all emails in one call
     * Set<String> emails = EmailRecipientsHelper.processMembershipBasedRecipients(
     *     identityAPI, userLists, { it.userId }
     * )
     * }</pre>
     *
     * @param <T>             the type of elements in the user lists collection
     * @param identityAPI     the Bonita Identity API for email lookup
     * @param userLists       the collection of user list objects from a DAO query
     * @param userIdExtractor a function that extracts the user ID from each user list object
     * @return a set of email addresses for all valid users in the membership results
     */
    public static <T> Set<String> processMembershipBasedRecipients(
            IdentityAPI identityAPI,
            Collection<T> userLists,
            Function<T, Long> userIdExtractor) {

        Objects.requireNonNull(identityAPI, "IdentityAPI cannot be null");

        Set<Long> userIds = extractUserIdsFromMembershipResults(userLists, userIdExtractor);

        if (userIds.isEmpty()) {
            LOGGER.info("MEMBERSHIP - No valid userIds extracted from membership results");
            return Collections.emptySet();
        }

        Set<String> emails = getEmailsByUserIds(identityAPI, userIds);
        LOGGER.info("MEMBERSHIP - Processed {} emails from {} userIds", emails.size(), userIds.size());

        return emails;
    }

    // ═══════════════════════════════════════════════════════════════════
    // PRIVATE HELPER METHODS
    // ═══════════════════════════════════════════════════════════════════

    private static Optional<String> extractEmail(ContactData contactData) {
        if (contactData == null) {
            return Optional.empty();
        }
        String email = contactData.getEmail();
        return isValidEmail(email) ? Optional.of(email) : Optional.empty();
    }
}