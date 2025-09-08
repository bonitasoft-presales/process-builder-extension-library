package com.bonitasoft.processbuilder.extension;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.bonitasoft.engine.api.APIAccessor;
import org.bonitasoft.engine.api.IdentityAPI;
import org.bonitasoft.engine.api.ProcessAPI;
import org.bonitasoft.engine.bpm.process.ProcessInstance;
import org.bonitasoft.engine.identity.User;
import org.bonitasoft.engine.identity.UserNotFoundException;


/**
 * An actor filter to assign a task to a list of users based on a JSON configuration.
 * The configuration can include the process initiator, a list of user IDs, and/or
 * a list of memberships (group and role IDs) to find candidate users.
 */
public class ProcessUtils {

    /**
     * A logger for this class, used to record log messages and provide debugging information.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessUtils.class);

    /**
     * Private constructor to prevent instantiation of this utility class.
     * All methods in this class are static and should be called directly on the class itself.
     */
    private ProcessUtils() {
        // This constructor is intentionally empty.
    }

    /**
     * Retrieves the user who started a specific process instance.
     * This method accesses the Bonita process and identity APIs to find the initiator's details.
     * If the initiator is not found, or an unexpected error occurs, a default 'unknown_user' is returned.
     *
     * @param apiAccessor An instance of {@link APIAccessor} to get the Bonita APIs.
     * @param processInstanceId The unique identifier of the process instance.
     * @return A {@link ProcessInitiator} record containing the initiator's ID, username, and full name.
     */
    public static ProcessInitiator getProcessInitiator(APIAccessor apiAccessor, long processInstanceId) {
        try {
            LOGGER.info("Attempting to retrieve the user who started the process instance ID: {}", processInstanceId);
            ProcessAPI processAPI = apiAccessor.getProcessAPI();
            IdentityAPI identityAPI = apiAccessor.getIdentityAPI();

            ProcessInstance processInstance = processAPI.getProcessInstance(processInstanceId);
            long startedByUserId = processInstance.getStartedBy();
            User processInitiator = identityAPI.getUser(startedByUserId);

            String creationFullName = processInitiator.getFirstName() + " " + processInitiator.getLastName();
            String creationUserName = processInitiator.getUserName();

            LOGGER.debug("Successfully retrieved initiator user: {}", creationFullName);
            return new ProcessInitiator(startedByUserId, creationUserName, creationFullName);

        } catch (UserNotFoundException e) {
            LOGGER.warn("The user who started process instance ID {} was not found. Using 'unknown_user'.", processInstanceId, e);
            return new ProcessInitiator(null, "unknown_user", "unknown_user");

        } catch (Exception e) {
            LOGGER.error("An unexpected error occurred while retrieving the process initiator for process instance ID {}: {}"
                , processInstanceId, e.getMessage(), e);
            return new ProcessInitiator(null, "unknown_user", "unknown_user");
        }
    }

    /**
     * A record representing the process initiator.
     *
     * @param id The unique identifier of the user who initiated the process.
     * @param userName The username of the process initiator.
     * @param fullName The full name (first name + last name) of the process initiator.
     */
    public record ProcessInitiator(Long id, String userName, String fullName) {}
}