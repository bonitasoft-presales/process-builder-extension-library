package com.bonitasoft.processbuilder.extension;

import com.bonitasoft.processbuilder.enums.DataResolverType;

import org.bonitasoft.engine.api.IdentityAPI;
import org.bonitasoft.engine.identity.ContactData;
import org.bonitasoft.engine.identity.User;
import org.bonitasoft.engine.identity.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Utility class for resolving template variables in notification messages.
 * <p>
 * This class provides methods to resolve standard template variables like recipient information
 * (firstname, lastname, email) using the Bonita Identity API. It is designed to work with
 * {@link PBStringUtils#resolveTemplateVariables(String, BiFunction)} method.
 * </p>
 *
 * <p>The resolver supports two variable formats:</p>
 * <ul>
 *   <li>{@code {{dataName}}} - Simple variable without prefix (refStep will be null)</li>
 *   <li>{@code {{refStep:dataName}}} - Variable with step reference prefix</li>
 * </ul>
 *
 * <p><b>Usage Example (Groovy Script):</b></p>
 * <pre>{@code
 * // Create the base resolver for standard variables
 * BiFunction<String, String, String> resolver = TemplateDataResolver.createResolver(
 *     identityAPI,
 *     recipientUserId,
 *     hostUrl,
 *     humanTaskId,
 *     // Custom resolver for BDM-specific data
 *     { refStep, dataName ->
 *         // Your custom BDM lookup logic here
 *         return myCustomValue
 *     }
 * );
 *
 * // Resolve template variables
 * String result = PBStringUtils.resolveTemplateVariables(template, resolver);
 * }</pre>
 *
 * @author Bonitasoft
 * @since 1.0
 * @see PBStringUtils#resolveTemplateVariables(String, BiFunction)
 * @see DataResolverType
 */
public final class TemplateDataResolver {

    private static final Logger LOGGER = LoggerFactory.getLogger(TemplateDataResolver.class);

    private TemplateDataResolver() {
        throw new UnsupportedOperationException("This is a " + this.getClass().getSimpleName() + " class and cannot be instantiated.");
    }

    // ═══════════════════════════════════════════════════════════════════
    // USER INFORMATION METHODS
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Gets the first name of a user.
     *
     * @param identityAPI the Bonita Identity API
     * @param userId the user ID
     * @return Optional containing the first name if found
     */
    public static Optional<String> getUserFirstName(IdentityAPI identityAPI, Long userId) {
        return getUser(identityAPI, userId).map(User::getFirstName);
    }

    /**
     * Gets the last name of a user.
     *
     * @param identityAPI the Bonita Identity API
     * @param userId the user ID
     * @return Optional containing the last name if found
     */
    public static Optional<String> getUserLastName(IdentityAPI identityAPI, Long userId) {
        return getUser(identityAPI, userId).map(User::getLastName);
    }

    /**
     * Gets the email of a user.
     *
     * @param identityAPI the Bonita Identity API
     * @param userId the user ID
     * @return Optional containing the email if found
     */
    public static Optional<String> getUserEmail(IdentityAPI identityAPI, Long userId) {
        if (identityAPI == null || !isValidUserId(userId)) {
            return Optional.empty();
        }

        try {
            ContactData contactData = identityAPI.getUserContactData(userId, false);
            if (contactData != null && contactData.getEmail() != null && !contactData.getEmail().isBlank()) {
                return Optional.of(contactData.getEmail());
            }
        } catch (UserNotFoundException e) {
            LOGGER.warn("User not found for email lookup: userId={}", userId);
        } catch (Exception e) {
            LOGGER.error("Error retrieving user email for userId={}: {}", userId, e.getMessage(), e);
        }
        return Optional.empty();
    }

    /**
     * Gets the full name (first + last) of a user.
     *
     * @param identityAPI the Bonita Identity API
     * @param userId the user ID
     * @return Optional containing the full name if found
     */
    public static Optional<String> getUserFullName(IdentityAPI identityAPI, Long userId) {
        return getUser(identityAPI, userId).map(user -> {
            String firstName = user.getFirstName();
            String lastName = user.getLastName();
            if (firstName != null && lastName != null) {
                return firstName + " " + lastName;
            } else if (firstName != null) {
                return firstName;
            } else if (lastName != null) {
                return lastName;
            }
            return user.getUserName();
        });
    }

    // ═══════════════════════════════════════════════════════════════════
    // LINK GENERATION METHODS
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Generates a task link HTML anchor tag.
     *
     * @param hostUrl the base host URL (e.g., "https://bonita.example.com")
     * @param taskId the human task ID
     * @return the HTML link string
     */
    public static String generateTaskLink(String hostUrl, Long taskId) {
        if (hostUrl == null || hostUrl.isBlank()) {
            LOGGER.warn("Host URL is null or blank for task link generation");
            return "#" + taskId;
        }
        if (taskId == null || taskId <= 0) {
            LOGGER.warn("Invalid taskId for link generation: {}", taskId);
            return "#invalid-task";
        }

        String cleanHost = hostUrl.endsWith("/") ? hostUrl.substring(0, hostUrl.length() - 1) : hostUrl;
        String url = cleanHost + "/app/process-builder?taskId=" + taskId;
        return "<a href=\"" + url + "\">#" + taskId + "</a>";
    }

    /**
     * Generates a plain task URL (without HTML anchor).
     *
     * @param hostUrl the base host URL
     * @param taskId the human task ID
     * @return the URL string
     */
    public static String generateTaskUrl(String hostUrl, Long taskId) {
        if (hostUrl == null || hostUrl.isBlank() || taskId == null || taskId <= 0) {
            return null;
        }

        String cleanHost = hostUrl.endsWith("/") ? hostUrl.substring(0, hostUrl.length() - 1) : hostUrl;
        return cleanHost + "/app/process-builder?taskId=" + taskId;
    }

    // ═══════════════════════════════════════════════════════════════════
    // RESOLVER FACTORY METHODS
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Creates a complete BiFunction resolver for template variables.
     * <p>
     * This method creates a resolver that handles standard variables
     * ({@link DataResolverType}) and delegates unknown variables to a custom fallback resolver.
     * </p>
     *
     * @param identityAPI the Bonita Identity API for user lookups
     * @param recipientUserId the user ID of the recipient (for recipient_* variables)
     * @param hostUrl the base host URL (for task_link variable)
     * @param humanTaskId the human task ID (for task_link variable)
     * @param customResolver optional custom resolver for BDM-specific or step-based variables.
     *                       Called when standard variables don't match. May be null.
     * @return BiFunction resolver for use with {@link PBStringUtils#resolveTemplateVariables}
     */
    public static BiFunction<String, String, String> createResolver(
            IdentityAPI identityAPI,
            Long recipientUserId,
            String hostUrl,
            Long humanTaskId,
            BiFunction<String, String, String> customResolver) {

        Objects.requireNonNull(identityAPI, "IdentityAPI cannot be null");

        return (refStep, dataName) -> {
            LOGGER.debug("Resolving variable: refStep={}, dataName={}", refStep, dataName);

            // Handle standard recipient variables (no refStep required)
            if (dataName != null) {
                String standardResult = resolveStandardVariable(
                        identityAPI, recipientUserId, hostUrl, humanTaskId, dataName);
                if (standardResult != null) {
                    LOGGER.debug("Resolved standard variable {}={}", dataName, standardResult);
                    return standardResult;
                }
            }

            // Delegate to custom resolver for BDM-specific or step-based lookups
            if (customResolver != null) {
                String customResult = customResolver.apply(refStep, dataName);
                if (customResult != null) {
                    LOGGER.debug("Resolved via custom resolver: {}:{}={}", refStep, dataName, customResult);
                    return customResult;
                }
            }

            LOGGER.warn("Variable not resolved: refStep={}, dataName={}", refStep, dataName);
            return null;
        };
    }

    /**
     * Creates a simple resolver for recipient-only variables.
     * <p>
     * Use this when you only need to resolve recipient_firstname, recipient_lastname,
     * and recipient_email variables without any custom BDM lookups.
     * </p>
     *
     * @param identityAPI the Bonita Identity API
     * @param recipientUserId the user ID of the recipient
     * @return BiFunction resolver for recipient variables only
     */
    public static BiFunction<String, String, String> createRecipientResolver(
            IdentityAPI identityAPI,
            Long recipientUserId) {

        return createResolver(identityAPI, recipientUserId, null, null, null);
    }

    /**
     * Creates a resolver with task link support.
     *
     * @param identityAPI the Bonita Identity API
     * @param recipientUserId the user ID of the recipient
     * @param hostUrl the base host URL
     * @param humanTaskId the human task ID
     * @return BiFunction resolver for recipient and task link variables
     */
    public static BiFunction<String, String, String> createResolverWithTaskLink(
            IdentityAPI identityAPI,
            Long recipientUserId,
            String hostUrl,
            Long humanTaskId) {

        return createResolver(identityAPI, recipientUserId, hostUrl, humanTaskId, null);
    }

    // ═══════════════════════════════════════════════════════════════════
    // STEP-BASED DATA HELPER
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Creates a step-based data extractor function.
     * <p>
     * This helper creates a function that can be used as part of the custom resolver
     * to handle step_user_name and step_status variables from BDM step data.
     * </p>
     *
     * <p><b>Usage Example (Groovy Script):</b></p>
     * <pre>{@code
     * // Define step data lookup
     * Function<String, Object> stepLookup = { refStep ->
     *     def steps = pBStepProcessInstanceDAO.findLastByRefStepAndRootProcessInstanceId(
     *         rootProcessInstanceId, refStep, 0, 1)
     *     return steps?.isEmpty() ? null : steps.get(0)
     * }
     *
     * // Create step data resolver
     * BiFunction<String, String, String> stepResolver = TemplateDataResolver.createStepDataResolver(
     *     stepLookup,
     *     { step -> step.getUsername() },
     *     { step -> step.getStepStatus() }
     * );
     * }</pre>
     *
     * @param <T> the type of step object returned by the lookup
     * @param stepLookup function that takes refStep and returns the step object (or null)
     * @param usernameExtractor function to extract username from step object
     * @param statusExtractor function to extract status from step object
     * @return BiFunction that resolves step_user_name and step_status variables
     */
    public static <T> BiFunction<String, String, String> createStepDataResolver(
            Function<String, T> stepLookup,
            Function<T, String> usernameExtractor,
            Function<T, String> statusExtractor) {

        Objects.requireNonNull(stepLookup, "stepLookup cannot be null");
        Objects.requireNonNull(usernameExtractor, "usernameExtractor cannot be null");
        Objects.requireNonNull(statusExtractor, "statusExtractor cannot be null");

        return (refStep, dataName) -> {
            if (refStep == null) {
                LOGGER.debug("No refStep provided for step data lookup");
                return null;
            }

            if (!DataResolverType.STEP_USER_NAME.getKey().equals(dataName) &&
                !DataResolverType.STEP_STATUS.getKey().equals(dataName)) {
                return null;
            }

            T stepData = stepLookup.apply(refStep);
            if (stepData == null) {
                LOGGER.warn("No step data found for refStep={}", refStep);
                return null;
            }

            if (DataResolverType.STEP_USER_NAME.getKey().equals(dataName)) {
                return usernameExtractor.apply(stepData);
            } else {
                return statusExtractor.apply(stepData);
            }
        };
    }

    // ═══════════════════════════════════════════════════════════════════
    // PRIVATE HELPER METHODS
    // ═══════════════════════════════════════════════════════════════════

    private static Optional<User> getUser(IdentityAPI identityAPI, Long userId) {
        if (identityAPI == null || !isValidUserId(userId)) {
            return Optional.empty();
        }

        try {
            return Optional.of(identityAPI.getUser(userId));
        } catch (UserNotFoundException e) {
            LOGGER.warn("User not found: userId={}", userId);
        } catch (Exception e) {
            LOGGER.error("Error retrieving user userId={}: {}", userId, e.getMessage(), e);
        }
        return Optional.empty();
    }

    private static boolean isValidUserId(Long userId) {
        return userId != null && userId > 0;
    }

    private static String resolveStandardVariable(
            IdentityAPI identityAPI,
            Long recipientUserId,
            String hostUrl,
            Long humanTaskId,
            String dataName) {

        // Check if it's a known DataResolverType
        DataResolverType type = DataResolverType.fromKey(dataName);
        if (type == null) {
            return null;
        }

        return switch (type) {
            case RECIPIENT_FIRSTNAME -> getUserFirstName(identityAPI, recipientUserId).orElse(null);
            case RECIPIENT_LASTNAME -> getUserLastName(identityAPI, recipientUserId).orElse(null);
            case RECIPIENT_EMAIL -> getUserEmail(identityAPI, recipientUserId).orElse(null);
            case TASK_LINK -> generateTaskLink(hostUrl, humanTaskId);
            // STEP_USER_NAME and STEP_STATUS require BDM lookup, handled by custom resolver
            case STEP_USER_NAME, STEP_STATUS -> null;
        };
    }
}
