package com.bonitasoft.processbuilder.extension;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bonitasoft.processbuilder.enums.ActionType;
import com.bonitasoft.processbuilder.records.UserRecord;

import java.util.function.Function;

import org.bonitasoft.engine.api.APIAccessor;
import org.bonitasoft.engine.api.IdentityAPI;
import org.bonitasoft.engine.api.ProcessAPI;
import org.bonitasoft.engine.bpm.flownode.ActivityInstanceNotFoundException;
import org.bonitasoft.engine.bpm.flownode.HumanTaskInstance;
import org.bonitasoft.engine.bpm.process.ProcessInstance;
import org.bonitasoft.engine.exception.RetrieveException;
import org.bonitasoft.engine.identity.ContactData;
import org.bonitasoft.engine.identity.User;
import org.bonitasoft.engine.identity.UserNotFoundException;
import org.bonitasoft.engine.session.InvalidSessionException;


/**
 * Utility class providing common process and BDM (Business Data Model) operations.
 * This includes retrieving the process initiator and utility methods for BDM validation
 * and deletion handling, specifically tailored for Bonita API interaction.
 * <p>
 * This class is non-instantiable and all methods are static.
 * </p>
 * @author Bonitasoft
 * @since 1.0
 */
public final class ProcessUtils {

    /**
     * A logger for this class, used to record log messages and provide debugging information.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessUtils.class);

    /**
     * Private constructor to prevent instantiation of this utility class.
     * All methods in this class are static and should be called directly on the class itself.
     * @throws UnsupportedOperationException always, to enforce the utility pattern.
     */
    private ProcessUtils() {
        throw new UnsupportedOperationException("This is a "+this.getClass().getSimpleName()+" class and cannot be instantiated.");
    }

    /**
     * Retrieves the user who started a specific process instance.
     * This method accesses the Bonita process and identity APIs to find the initiator's details.
     * If the initiator is not found, or an unexpected error occurs, a default 'unknown_user' is returned.
     *
     * @param apiAccessor An instance of {@link APIAccessor} to get the Bonita APIs.
     * @param processInstanceId The unique identifier of the process instance.
     * @return A {@link UserRecord} record containing the initiator's ID, username, and full name, etc.
     */
    public static UserRecord getProcessInitiator(APIAccessor apiAccessor, long processInstanceId) {
        try {
            LOGGER.info("Attempting to retrieve the user who started the process instance ID: {}", processInstanceId);
            ProcessAPI processAPI = apiAccessor.getProcessAPI();
            IdentityAPI identityAPI = apiAccessor.getIdentityAPI();

            ProcessInstance processInstance = processAPI.getProcessInstance(processInstanceId);
            long startedByUserId = processInstance.getStartedBy();
            User processInitiator = identityAPI.getUser(startedByUserId);

            String firstName = processInitiator.getFirstName();
            String lastName = processInitiator.getLastName();
            String creationFullName = firstName + " " + lastName;
            String creationUserName = processInitiator.getUserName();

            String email = null;
            try {
                ContactData startedByUserContactData = identityAPI.getUserContactData(startedByUserId, false);
                if (startedByUserContactData != null) {
                    email = startedByUserContactData.getEmail();
                }
            } catch (Exception e) {
                LOGGER.warn("Could not retrieve contact data for user ID {}: {}", startedByUserId, e.getMessage());
                email = null;
            }
            LOGGER.debug("Successfully retrieved initiator user: {}", creationFullName);
            return new UserRecord(startedByUserId, creationUserName, creationFullName, firstName, lastName, email);

        } catch (UserNotFoundException e) {
            LOGGER.warn("The user who started process instance ID {} was not found. Using 'unknown_user'.", processInstanceId, e);
            return new UserRecord(null, "unknown_user", "unknown_user", "unknown_user", "unknown_user", "unknown_user");

        } catch (Exception e) {
            LOGGER.error("An unexpected error occurred while retrieving the process initiator for process instance ID {}: {}"
                , processInstanceId, e.getMessage(), e);
            return new UserRecord(null, "unknown_user", "unknown_user", "unknown_user", "unknown_user", "unknown_user");
        }
    }

    /**
     * Retrieves the user who executed a specific human task instance.
     * This method accesses the Bonita Process and Identity APIs to find the executor's details.
     * If the executor is not found, or an unexpected error occurs, a default 'unknown_user' is returned.
     *
     * @param apiAccessor An instance of {@link APIAccessor} to get the Bonita APIs.
     * @param activityInstanceId The unique identifier of the human task instance (activityId).
     * @return A {@link UserRecord} record containing the executor's ID, username, and full name.
     */
    public static UserRecord getTaskExecutor(APIAccessor apiAccessor, long activityInstanceId) {
        try {
            LOGGER.info("Attempting to retrieve the user who executed the task instance ID: {}", activityInstanceId);

            HumanTaskInstance humanTaskInstance = apiAccessor.getProcessAPI().getHumanTaskInstance(activityInstanceId);

            long executedByUserId = humanTaskInstance.getExecutedBy();

            if (executedByUserId <= 0) {
                LOGGER.debug("Task instance ID {} was not executed by a human user (executedBy ID: {}).",
                            activityInstanceId, executedByUserId);
                return new UserRecord(executedByUserId, "system_or_unassigned", "System or Unassigned", "system_or_unassigned", "system_or_unassigned", "system_or_unassigned");
            }

            IdentityAPI identityAPI = apiAccessor.getIdentityAPI();
            User taskExecutor = identityAPI.getUser(executedByUserId);

            String firstName = taskExecutor.getFirstName();
            String lastName = taskExecutor.getLastName();
            String executorFullName = firstName + " " + lastName;
            String executorUserName = taskExecutor.getUserName();

            String email = null;
            try {
                ContactData startedByUserContactData = identityAPI.getUserContactData(executedByUserId, false);
                if (startedByUserContactData != null) {
                    email = startedByUserContactData.getEmail();
                }
            } catch (Exception e) {
                LOGGER.warn("Could not retrieve contact data for user ID {}: {}", executedByUserId, e.getMessage());
                email = null;
            }

            LOGGER.debug("Successfully retrieved executor user: {}", executorFullName);
            return new UserRecord(executedByUserId, executorUserName, executorFullName, firstName, lastName, email);

        } catch (UserNotFoundException e) {
            LOGGER.warn("The user who executed task instance ID {} was not found. Using 'unknown_user'.", activityInstanceId, e);
            return new UserRecord(null, "unknown_user", "Unknown User", "unknown_user", "unknown_user", "unknown_user");

        } catch (InvalidSessionException | ActivityInstanceNotFoundException | RetrieveException e) {
            LOGGER.error("An API error occurred while retrieving the task executor for activity ID {}: {}",
                        activityInstanceId, e.getMessage(), e);
            return new UserRecord(null, "api_error", "API Error", "api_error", "api_error", "api_error");

        } catch (Exception e) {
            LOGGER.error("An unexpected error occurred while retrieving the task executor for activity ID {}: {}",
                        activityInstanceId, e.getMessage(), e);
            return new UserRecord(null, "unexpected_error", "Unexpected Error", "unexpected_error", "unexpected_error", "unexpected_error");
        }
    }

    /**
     * Searches for a BDM object by its persistence ID and validates its existence.
     * This method should be used internally after the persistence ID has been successfully converted to a Long.
     * 
     * @param <T> The generic type of the BDM object (e.g., PBProcess, PBCategory).
     * @param persistenceId The ID (Long) used to search for the object. Must not be null.
     * @param searchFunction The function (e.g., DAO method) to perform the search: (Long ID -> T Object).
     * @param objectType The name of the BDM object class (e.g., "PBProcess" or "PBCategory"). Used for error logging.
     * @return The found BDM object of type T.
     * @throws RuntimeException if no object is found for the given ID (i.e., the search function returns {@code null}).
     */
    private static <T> T searchAndValidate(
            Long persistenceId, 
            Function<Long, T> searchFunction, 
            String objectType) throws RuntimeException {

        // Apply the search function
        T bdmObject = searchFunction.apply(persistenceId);

        if (bdmObject == null) {
            String errorMessage = String.format(
                "No existing %s found for persistenceId: '%s'. Cannot update.", 
                objectType, 
                persistenceId
            );
            LOGGER.error(errorMessage);
            // Uses an external utility to log the error and throw the exception
            throw ExceptionUtils.logAndThrow(() -> new RuntimeException(errorMessage), errorMessage);
        }
        
        return bdmObject;
    }

    /**
     * Validates that the BDM object exists before performing a DELETE action.
     * If the object is not found (i.e., is {@code null}), a {@code RuntimeException} is thrown.
     * 
     * @param <T> The generic type of the BDM object (e.g., PBProcess, PBCategory).
     * @param bdmObject The BDM object retrieved from the search (may be null).
     * @param persistenceId The ID used for logging.
     * @param objectType The name of the BDM object class.
     * @return The existing BDM object (T).
     * @throws RuntimeException if the object is null (not found for deletion).
     */
    private static <T> T validateForDelete(T bdmObject, Long persistenceId, String objectType) throws RuntimeException {
        
        if (bdmObject == null) {
            String errorMessage = String.format(
                "No existing %s found to delete with persistenceId: '%s'.", 
                objectType, 
                persistenceId
            );
            
            // Uses an external utility to log the error and throw the exception
            throw ExceptionUtils.logAndThrow(() -> new RuntimeException(errorMessage), errorMessage);
        }
        
        LOGGER.info("Finished processing DELETE action for ID: {}", persistenceId);
        return bdmObject;
    }

    /**
     * Searches for a BDM object by its persistence ID, handling the ID conversion from String to Long.
     * Validates that the object exists if the persistence ID is present.
     *
     * @param <T> The generic type of the BDM object (e.g., PBProcess, PBCategory).
     * @param persistenceIdString The persistence ID as a String (can be null or empty).
     * @param searchFunction The function (e.g., DAO method) to perform the search: (Long ID -> T Object).
     * @param objectType The name of the BDM object class (e.g., "PBProcess" or "PBCategory").
     * @return The found BDM object of type T, or {@code null} if the persistence ID is null or empty.
     * @throws RuntimeException if the String cannot be converted to Long, or if the object is not found (and ID was present).
     */
    public static <T> T searchAndValidateId(
            String persistenceIdString, 
            Function<Long, T> searchFunction, 
            String objectType) throws RuntimeException  {

        Long persistenceId = null;

        if (persistenceIdString != null && !persistenceIdString.isEmpty()) {
            try {
                // First: Convert the ID string to Long
                persistenceId = Long.valueOf(persistenceIdString);
            } catch (NumberFormatException e) {
                String errorMessage = String.format(
                    "Invalid format for %s persistence ID: '%s'. Must be a valid number.", 
                    objectType, 
                    persistenceIdString
                );
                LOGGER.error(errorMessage, e);
                throw ExceptionUtils.logAndThrow(() -> new RuntimeException(errorMessage), errorMessage);
            }
            
            // Second: Search and validate the object's existence
            T bdmObject = searchAndValidate(persistenceId, searchFunction, objectType);
            
            return bdmObject;
        }

        // Return null if the input ID string was null or empty
        return null;
    }

    /**
     * Combines the check for a DELETE action with the validation that the BDM object exists.
     * If the {@code actionTypeInput} is "DELETE", it delegates to {@code validateForDelete}.
     * For any other action type (or null), it returns {@code null}, assuming the caller will handle
     * creation or update logic on the {@code bdmObject}.
     *
     * @param <T> The generic type of the BDM object.
     * @param bdmObject The BDM object retrieved from the search (may be null).
     * @param actionTypeInput The action type (e.g., "DELETE", "INSERT", "UPDATE").
     * @param persistenceId The ID used for logging.
     * @param objectType The name of the BDM object class.
     * @return The existing BDM object (T) if the action is DELETE and the object exists; otherwise, returns {@code null}.
     * @throws RuntimeException if the action is DELETE but the object is null (not found).
     */
    public static <T> T validateActionAndDelete(
            T bdmObject, 
            String actionTypeInput, 
            Long persistenceId, 
            String objectType) {
        
        if (actionTypeInput != null && actionTypeInput.equalsIgnoreCase(ActionType.DELETE.name())) {
            // Validate existence before deletion
            return validateForDelete(bdmObject, persistenceId, objectType);
        }
        // Return null if it's not a DELETE action (e.g., CREATE or UPDATE)
        return null;
    }

   /**
     * Searches for a BDM object using a provided search function (closure/lambda).
     * This method is a generic wrapper that accepts a search function and applies it to retrieve the object.
     * It does not perform any validation; use {@code searchAndValidateId} for validated searches.
     * <p>
     * This method is useful when you want to defer the actual search logic to the caller,
     * allowing the same retrieval method to work with different BDM types through their respective DAOs.
     * </p>
     *
     * @param <T> The generic type of the BDM object to be retrieved.
     * @param persistenceId The ID (Long) used to search for the object.
     * @param searchFunction The function that performs the search. Must accept a Long ID and return the BDM object (or null if not found).
     * @param objectType The name of the BDM object class (e.g., "PBProcess"). Used for logging purposes only.
     * @return The BDM object found by the search function, or {@code null} if the search function returns null.
     */
    public static <T> T searchById(
            Long persistenceId, 
            Function<Long, T> searchFunction, 
            String objectType) {
        
        if (persistenceId == null || persistenceId <= 0) {
            LOGGER.warn("Skipping search for {} with invalid persistenceId: {}", objectType, persistenceId);
            return null;
        }

        try {
            LOGGER.debug("Searching for {} with persistenceId: {}", objectType, persistenceId);
            T result = searchFunction.apply(persistenceId);
            
            if (result != null) {
                LOGGER.debug("Successfully retrieved {} with persistenceId: {}", objectType, persistenceId);
            } else {
                LOGGER.debug("No {} found for persistenceId: {}", objectType, persistenceId);
            }
            
            return result;
        } catch (Exception e) {
            LOGGER.error("Error occurred while searching for {} with persistenceId: {}. Message: {}", 
                        objectType, persistenceId, e.getMessage(), e);
            return null;
        }
    }

    /**
     * Searches for a BDM object by its persistence ID string, handling conversion and validation.
     * This is a convenience method that combines ID parsing and object search in a single call.
     * <p>
     * This method is particularly useful for Bonita scripts where the persistence ID comes as a String
     * and you want to retrieve any BDM object type without writing multiple lines of boilerplate code.
     * </p>
     *
     * @param <T> The generic type of the BDM object to be retrieved.
     * @param persistenceIdInput The persistence ID as a String (can be null or empty).
     * @param searchFunction The function that performs the search. Must accept a Long ID and return the BDM object (or null if not found).
     * @param objectType The name of the BDM object class (e.g., "PBProcess" or "PBCategory"). Used for logging purposes.
     * @return The BDM object if found, or {@code null} if the persistence ID is null/empty, invalid format, or object not found.
     */
    public static <T> T searchBDM(
            String persistenceIdInput,
            Function<Long, T> searchFunction,
            String objectType) {

        if (persistenceIdInput == null || persistenceIdInput.trim().isEmpty()) {
            LOGGER.warn("Skipping search: persistenceId is null or empty for object type {}", objectType);
            return null;
        }

        try {
            Long persistenceId = Long.valueOf(persistenceIdInput.trim());
            return searchById(persistenceId, searchFunction, objectType);
        } catch (NumberFormatException e) {
            LOGGER.error("Invalid persistenceId format '{}' for {}. Must be a valid number.", 
                        persistenceIdInput, objectType, e);
            return null;
        } catch (Exception e) {
            LOGGER.error("Error searching for {} with persistenceId: {}", objectType, persistenceIdInput, e);
            return null;
        }
    }
}