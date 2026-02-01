package com.bonitasoft.processbuilder.records;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * A record representing the user configuration for task assignation.
 * <p>
 * This record is parsed from a JSON structure with the following format:
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
 * <p>
 * All fields are nullable, allowing flexible configuration for different
 * task assignation scenarios.
 * </p>
 *
 * @param stepUser          Reference to a step whose executor becomes a candidate (nullable)
 * @param stepManager       Reference to a step whose executor's manager becomes a candidate (nullable)
 * @param memberShips       Static list of membership references defined in BDM (nullable)
 * @param membersShipsInput Dynamic membership reference in format "step_xxx:field_yyy" (nullable)
 * @author Bonitasoft
 * @since 1.0
 */
public record UsersConfigRecord(
        String stepUser,
        String stepManager,
        List<String> memberShips,
        String membersShipsInput
) {

    /** JSON key for the users node. */
    public static final String USERS_KEY = "users";
    /** JSON key for stepUser. */
    public static final String STEP_USER_KEY = "stepUser";
    /** JSON key for stepManager. */
    public static final String STEP_MANAGER_KEY = "stepManager";
    /** JSON key for memberShips. */
    public static final String MEMBERSHIPS_KEY = "memberShips";
    /** JSON key for membersShipsInput. */
    public static final String MEMBERSHIPS_INPUT_KEY = "membersShipsInput";

    /**
     * Compact constructor ensuring memberShips is immutable.
     *
     * @param stepUser          The step user reference
     * @param stepManager       The step manager reference
     * @param memberShips       The list of membership references
     * @param membersShipsInput The dynamic membership input reference
     */
    public UsersConfigRecord {
        memberShips = memberShips == null ? Collections.emptyList() : List.copyOf(memberShips);
    }

    /**
     * Creates an empty UsersConfigRecord with all null/empty values.
     *
     * @return An empty UsersConfigRecord instance
     */
    public static UsersConfigRecord empty() {
        return new UsersConfigRecord(null, null, Collections.emptyList(), null);
    }

    /**
     * Parses a "users" JsonNode and creates a UsersConfigRecord.
     * <p>
     * This method extracts all user configuration fields from the provided JsonNode.
     * Missing or null fields result in null/empty values in the record.
     * </p>
     *
     * @param usersNode The JsonNode containing the users configuration (nullable)
     * @param logger    Logger for debug messages (nullable)
     * @return A UsersConfigRecord with parsed values, or empty record if usersNode is null
     */
    public static UsersConfigRecord fromUsersNode(JsonNode usersNode, Logger logger) {
        if (usersNode == null || usersNode.isNull()) {
            logDebug(logger, "Users node is null, returning empty config");
            return empty();
        }

        String stepUser = extractTextValue(usersNode, STEP_USER_KEY);
        String stepManager = extractTextValue(usersNode, STEP_MANAGER_KEY);
        List<String> memberShips = extractStringList(usersNode, MEMBERSHIPS_KEY);
        String membersShipsInput = extractTextValue(usersNode, MEMBERSHIPS_INPUT_KEY);

        logDebug(logger, "Parsed UsersConfigRecord: stepUser={}, stepManager={}, memberShips={}, membersShipsInput={}",
                stepUser, stepManager, memberShips.size(), membersShipsInput);

        return new UsersConfigRecord(stepUser, stepManager, memberShips, membersShipsInput);
    }

    /**
     * Checks if this configuration has any step user reference.
     *
     * @return true if stepUser is not null and not blank
     */
    public boolean hasStepUser() {
        return stepUser != null && !stepUser.isBlank();
    }

    /**
     * Checks if this configuration has any step manager reference.
     *
     * @return true if stepManager is not null and not blank
     */
    public boolean hasStepManager() {
        return stepManager != null && !stepManager.isBlank();
    }

    /**
     * Checks if this configuration has any static memberships.
     *
     * @return true if memberShips is not empty
     */
    public boolean hasMemberShips() {
        return memberShips != null && !memberShips.isEmpty();
    }

    /**
     * Checks if this configuration has a dynamic membership input reference.
     *
     * @return true if membersShipsInput is not null and not blank
     */
    public boolean hasMembersShipsInput() {
        return membersShipsInput != null && !membersShipsInput.isBlank();
    }

    /**
     * Checks if this configuration has any user source defined.
     *
     * @return true if any of stepUser, stepManager, memberShips, or membersShipsInput is defined
     */
    public boolean hasAnySource() {
        return hasStepUser() || hasStepManager() || hasMemberShips() || hasMembersShipsInput();
    }

    /**
     * Gets the stepUser as an Optional.
     *
     * @return Optional containing the stepUser, or empty if not defined
     */
    public Optional<String> getStepUserOptional() {
        return hasStepUser() ? Optional.of(stepUser) : Optional.empty();
    }

    /**
     * Gets the stepManager as an Optional.
     *
     * @return Optional containing the stepManager, or empty if not defined
     */
    public Optional<String> getStepManagerOptional() {
        return hasStepManager() ? Optional.of(stepManager) : Optional.empty();
    }

    /**
     * Gets the membersShipsInput as an Optional.
     *
     * @return Optional containing the membersShipsInput, or empty if not defined
     */
    public Optional<String> getMembersShipsInputOptional() {
        return hasMembersShipsInput() ? Optional.of(membersShipsInput) : Optional.empty();
    }

    /**
     * Parses the membersShipsInput into a StepFieldRef.
     *
     * @return Optional containing the parsed StepFieldRef, or empty if not defined or invalid format
     */
    public Optional<StepFieldRef> parseMembersShipsInput() {
        if (!hasMembersShipsInput()) {
            return Optional.empty();
        }
        return Optional.ofNullable(StepFieldRef.parse(membersShipsInput));
    }

    // ========================================================================
    // Private Helper Methods
    // ========================================================================

    private static String extractTextValue(JsonNode parentNode, String key) {
        JsonNode node = parentNode.get(key);
        if (node == null || node.isNull()) {
            return null;
        }
        String text = node.asText();
        return (text == null || text.isBlank()) ? null : text.trim();
    }

    private static List<String> extractStringList(JsonNode parentNode, String key) {
        JsonNode arrayNode = parentNode.get(key);
        if (arrayNode == null || !arrayNode.isArray() || arrayNode.isEmpty()) {
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

    private static void logDebug(Logger logger, String message, Object... args) {
        if (logger != null && logger.isDebugEnabled()) {
            logger.debug(message, args);
        }
    }
}
