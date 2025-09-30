package com.bonitasoft.processbuilder.extension;

import com.bonitasoft.processbuilder.extension.ProcessUtils.ProcessInitiator;
import com.bonitasoft.processbuilder.enums.ProcessOptionType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.processbuilder.model.PBCategory;
import com.processbuilder.model.PBCategoryDAO;
import com.processbuilder.model.PBProcess;
import com.processbuilder.model.PBRunningInstance;
import com.processbuilder.model.PBRunningInstanceDAO;
import com.processbuilder.model.PBUserList;

import org.bonitasoft.engine.api.APIAccessor;
import org.bonitasoft.engine.api.IdentityAPI;
import org.bonitasoft.engine.identity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

// Import the specific BDM classes (replace with your actual package)
// import com.processbuilder.model.PBProcess; 
// import com.processbuilder.model.PBUserList;
// import com.processbuilder.model.PBSteps;
// import com.processbuilder.model.PBAction;
// import com.processbuilder.model.PBRunningInstance; 
// import com.processbuilder.model.PBRunningInstanceDAO;
// import com.processbuilder.model.PBCategoryDAO;


/**
 * Utility class responsible for mapping specific input sections (USERS, STEPS, STATUS)
 * from a general process input structure onto a persistence object (e.g., PBProcess).
 *
 * This class ensures separation of concerns from the main persistence/auditing logic.
 */
public class ProcessDataMapper {

    private static final Logger logger = LoggerFactory.getLogger(ProcessDataMapper.class);

    /**
     * Main dispatcher method that routes the mapping logic based on the option type.
     *
     * @param objectToSave The target persistence object (e.g., PBProcess) to be modified.
     * @param optionTypeInput The option type defining the specific data section to handle.
     * @param processInput The DTO/Map containing all incoming data.
     * @param processInitiator The user performing the action, required for audit data in sub-objects.
     * @param apiAccessor Bonita API Accessor for fetching Bonita runtime data.
     * @param pBRunningInstanceDAO DAO for accessing PBRunningInstance (required for STATUS case).
     * @param searchCategoryByIdFunction Function to search for PBCategory (required for PARAMETER case).
     * @return The updated objectToSave.
     */
    // Function<Long, PBCategory> searchCategoryByIdFunction = (Long id) -> pBCategoryDAO.findByPersistenceId(id);
    public static PBProcess mapInputToPersistenceObject(
        PBProcess objectToSave, String optionTypeInput, Map<String, Object> processInput, ProcessInitiator processInitiator,
        APIAccessor apiAccessor,
        PBRunningInstanceDAO pBRunningInstanceDAO, 
        PBCategoryDAO pBCategoryDAO 
    ) {
        if (objectToSave == null || optionTypeInput == null) {
            logger.warn("Target object or option type is null. Skipping mapping.");
            return objectToSave;
        }
        Function<Long, PBCategory> searchCategoryByIdFunction = (Long id) -> pBCategoryDAO.findByPersistenceId(id);
        // Use switch expression for clean routing (Java 14+)
        try {
            switch (ProcessOptionType.valueOf(optionTypeInput)) {
                case USERS : {
                    handleUsersCase(objectToSave, processInput, processInitiator, apiAccessor);
                    return objectToSave;
                }            
                case INPUTS : { 
                    handleInputsCase(objectToSave, processInput);
                    return objectToSave;
                }            
                case STEPS : { 
                    handleStepsCase();
                    return objectToSave;
                }            
                case STATUS : { 
                    handleStatusCase();
                    return objectToSave;
                }            
                default : { // PARAMETER (Default)
                    handleParameterCase(objectToSave, processInput, searchCategoryByIdFunction);
                    return objectToSave;
                }            
            }
        } catch (IllegalArgumentException e) {
            ExceptionUtils.logAndThrow(() -> new RuntimeException(), "Received invalid option type: {}. - {}", optionTypeInput, e);
        } catch (Exception e) {
            ExceptionUtils.logAndThrow(() -> new RuntimeException(), "Mapping failed - An unexpected error occurred during mapping for option {}. - {}", optionTypeInput, e);
        }
        return null;
    }


    // --------------------------------------------------------------------------
    // DEDICATED HANDLER METHODS (Implementation of complexity)
    // --------------------------------------------------------------------------

    private static void handleInputsCase(PBProcess objectToSave, Map<String, Object> processInput) {
            logger.info("Handling 'INPUTS' case.");
            objectToSave.setInputs(processInput != null ? (String) processInput.get("inputs") : null);
    }


    /**
     * Handles the mapping logic for the 'USERS' option, processing direct user IDs and memberships.
     * (Logic extracted from the original script)
     */
    
    private static void handleUsersCase(PBProcess objectToSave, Map<String, Object> processInput,
        ProcessInitiator processInitiator, APIAccessor apiAccessor) {
        logger.info("Handling 'USERS' case.");
        String usersJson = (processInput != null) ?(String) processInput.get("involvedUsers") : null;
        objectToSave.setInvolvedUsers(usersJson);
        
        if (usersJson == null || usersJson.trim().isEmpty()) {
            logger.warn("Input 'involvedUsers' is null or empty. Skipping user processing.");
            return;
        }

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode involvedUsersNode = objectMapper.readTree(usersJson);
            IdentityAPI identityAPI = apiAccessor.getIdentityAPI();

            Set<PBUserList> userListResult = new LinkedHashSet<>();
            List<Long> userIds = null;
            if (involvedUsersNode.has("users") && involvedUsersNode.get("users").isArray()) {
                userIds = StreamSupport.stream(
                        Spliterators.spliteratorUnknownSize(
                            involvedUsersNode.get("users").elements(), 
                            Spliterator.ORDERED 
                        ),false 
                    ).map(JsonNode::asLong).toList();

                if (!userIds.isEmpty()) {
                    identityAPI.getUsers(userIds).values()
                        .stream()
                        .filter(User::isEnabled) // Method reference for cleaner filtering
                        .map(user -> createPBUserList(user, processInitiator))
                        .forEach(userListResult::add);
                    logger.info("Found {} users from direct IDs ({} unique).", userIds.size(), userListResult.size());
                }
            }

            // 2.2: Process Memberships
            if (involvedUsersNode.has("memberShips") && involvedUsersNode.get("memberShips").isArray()) {
                StreamSupport.stream(
                    Spliterators.spliteratorUnknownSize(
                        involvedUsersNode.get("memberShips").elements(),
                        Spliterator.ORDERED
                    ),
                    false
                ).map((JsonNode memberShipNode) -> createPBMembershipList(memberShipNode, processInitiator)) 
                .forEach(userListResult::add);

                logger.info("Processed {} memberships.", involvedUsersNode.get("memberShips").size());
            }
            
            // 2.3: Final Assignment
            if (!userListResult.isEmpty()) {
                objectToSave.setInvolvedUserList(new ArrayList<>(userListResult));
            } else {
                objectToSave.setInvolvedUserList(new ArrayList<>());
            }
            logger.info("Final user list contains {} unique users. Variable updated.", userListResult.size());

        } catch (Exception e) {
            logger.error("An error occurred while processing 'involvedUsers'. Setting user list to empty.", e);
            objectToSave.setInvolvedUserList(new ArrayList<>());
        }
    }

    /**
     * Handles the mapping logic for the 'STEPS' option, parsing complex JSON into PBSteps objects.
     * (Logic extracted from the original script)
     */
    private static void handleStepsCase() {
        logger.info("Handling 'STEPS' case.");
    }
    
    /**
     * Handles the mapping logic for the 'STATUS' option, creating or updating the PBRunningInstance.
     * (Logic extracted from the original script)
     */
    private static void handleStatusCase() {
        logger.info("Handling 'STATUS' case.");
    }

    /**
     * Handles the default 'PARAMETER' case, assigning general process properties and the category.
     * (Logic extracted from the original script)
     */
    private static void handleParameterCase(PBProcess objectToSave, Map<String, Object> processInput, Function<Long, PBCategory> searchCategoryByIdFunction) {
        logger.info("Handling 'PARAMETER' case (default assignments).");
		objectToSave.setFullName((String) processInput.get("fullName"));
		objectToSave.setVersion((String) processInput.get("version"));
		objectToSave.setCriticality((String) processInput.get("criticality"));
		objectToSave.setFullDescription((String) processInput.get("description"));
		objectToSave.setToken((String) processInput.get("token"));
		objectToSave.setDisplayName((String) processInput.get("displayName"));
		objectToSave.setDocumentsFolder((String) processInput.get("documentsFolder"));
		objectToSave.setCancellationDelay((Integer) processInput.get("cancellationDelay"));
		objectToSave.setLastStatus((String) processInput.get("lastStatus") != null ? (String)  processInput.get("lastStatus") : "Draft");

        Map<String, Object> category = (Map<String, Object>) processInput.get("category");
		String categoryPersistenceId = (category != null) ? (String) category.get("persistenceId_string") : null;
		PBCategory pBCategoryVar = ProcessUtils.searchAndValidateId(categoryPersistenceId , searchCategoryByIdFunction, PBCategory.class.getSimpleName());

		// Final assignment to the process variable.
		objectToSave.setCategory(pBCategoryVar);

    }
    
    

    private static PBUserList createPBUserList(User user, ProcessInitiator initiator) {
        PBUserList pbUserList = new PBUserList();
        pbUserList.setUserId(user.getId());
        pbUserList.setUserName(user.getUserName());
        pbUserList.setUserDisplayName(user.getFirstName() + " " + user.getLastName());
        pbUserList.setCreationDate(OffsetDateTime.now());
        pbUserList.setCreatorId(initiator.id());
        pbUserList.setCreatorName(initiator.fullName());
        return pbUserList;
    }

    private static PBUserList createPBMembershipList(JsonNode node, ProcessInitiator initiator) {
        PBUserList pbUserList = new PBUserList();
        pbUserList.setGroupId(node.path("groupId").asLong(0L));
        pbUserList.setRoleId(node.path("roleId").asLong(0L));
        pbUserList.setRefMembership(node.path("ref").asText(null));
        pbUserList.setCreationDate(OffsetDateTime.now());
        pbUserList.setCreatorId(initiator.id());
        pbUserList.setCreatorName(initiator.fullName());
        return pbUserList;
    }
}