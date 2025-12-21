package com.bonitasoft.processbuilder.enums;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Enumeration of BDM (Business Data Model) object types used in the Process Builder extension.
 * This enum provides a centralized and type-safe way to reference BDM object names across the application.
 * <p>
 * Each enum constant represents a BDM entity and stores its string key and a description, which are used for:
 * - Logging purposes
 * - Generic search operations
 * - Error messages
 * - Database queries
 * </p>
 * <p>
 * Usage example:</p>
 * <pre>
 * {@code
 * // Using the enum in ProcessUtils.searchBDM()
 * PBProcess process = ProcessUtils.searchBDM(
 *     persistenceIdInput,
 *     searchFunction,
 *     BDMObjectType.PB_PROCESS.getKey()
 * );
 * 
 * // Getting the string representation and description
 * String objectTypeKey = BDMObjectType.PB_PROCESS_INSTANCE.getKey();
 * String objectTypeDescription = BDMObjectType.PB_PROCESS_INSTANCE.getDescription();
 * }
 * </pre>
 * 
 *
 * @author Bonitasoft
 * @since 1.0
 */
public enum BDMObjectType {

    /**
     * Represents a PBDataProcessInstance BDM object.
     * Used when searching or validating data process instances that contain business data.
     */
    PB_DATA_PROCESS_INSTANCE("PBDataProcessInstance", "Data Process Instance object containing business data information"),

    /**
     * Represents a PBProcessInstance BDM object.
     * Used when searching or validating process instances and their execution context.
     */
    PB_PROCESS_INSTANCE("PBProcessInstance", "Process Instance object representing an executed process with its execution state"),

    /**
     * Represents a PBStepProcessInstance BDM object.
     * Used when searching or validating step-level process instances.
     */
    PB_STEP_PROCESS_INSTANCE("PBStepProcessInstance", "Step Process Instance object representing individual steps within a process execution"),

    /**
     * Represents a PBAction BDM object.
     * Used when searching or validating process actions and their configurations.
     */
    PB_ACTION("PBAction", "Action object defining process operations and their configurations"),

    /**
     * Represents a PBActionContent BDM object.
     * Used when searching or validating the content and details associated with process actions.
     */
    PB_ACTION_CONTENT("PBActionContent", "Action Content object containing detailed information and parameters for process actions"),

    /**
     * Represents a PBCategory BDM object.
     * Used when searching or validating process categories for organization and classification.
     */
    PB_CATEGORY("PBCategory", "Category object used for organizing and classifying processes"),

    /**
     * Represents a PBEntityType BDM object.
     * Used when searching or validating entity type definitions and metadata.
     */
    PB_ENTITY_TYPE("PBEntityType", "Entity Type object defining structure and properties of business entities"),

    /**
     * Represents a PBFiles BDM object.
     * Used when searching or validating file storage and document management data.
     */
    PB_FILES("PBFiles", "Files object managing document storage and file attachment information"),

    /**
     * Represents a PBGenericEntry BDM object.
     * Used when searching or validating generic entry data structures.
     */
    PB_GENERIC_ENTRY("PBGenericEntry", "Generic Entry object providing flexible data storage for various entry types"),

    /**
     * Represents a PBProcess BDM object.
     * Used when searching or validating process definitions and configurations.
     */
    PB_PROCESS("PBProcess", "Process object containing process definitions and workflow configurations"),

    /**
     * Represents a PBRunningInstance BDM object.
     * Used when searching or validating currently active process instances.
     */
    PB_RUNNING_INSTANCE("PBRunningInstance", "Running Instance object representing currently executing process instances"),

    /**
     * Represents a PBSmtp BDM object.
     * Used when searching or validating SMTP email configuration data.
     */
    PB_SMTP("PBSmtp", "SMTP object storing email server configuration and communication settings"),

    /**
     * Represents a PBSteps BDM object.
     * Used when searching or validating process step definitions and sequences.
     */
    PB_STEPS("PBSteps", "Steps object defining the sequence and configuration of process workflow steps"),

    /**
     * Represents a PBUserList BDM object.
     * Used when searching or validating user list data and assignments.
     */
    PB_USER_LIST("PBUserList", "User List object managing user assignments and access control lists");

    /**
     * The technical key of this BDM object type (e.g., "PBAction", "PBProcess").
     */
    private final String key;

    /**
     * A human-readable description of this BDM object type.
     */
    private final String description;

    /**
     * Constructs a BDMObjectType enum constant with the specified key and description.
     *
     * @param key The technical key used for mapping and logging (e.g., "PBAction").
     * @param description A human-readable description of the BDM object type.
     */
    BDMObjectType(String key, String description) {
        this.key = key;
        this.description = description;
    }

    /**
     * Returns the unique string key associated with this BDM object type.
     *
     * @return The technical key (e.g., "PBAction").
     */
    public String getKey() {
        return key;
    }

    /**
     * Returns the business description of this BDM object type.
     *
     * @return The human-readable description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns a string representation of this enum constant.
     * Useful for debugging and logging purposes.
     *
     * @return A string containing the enum name, key, and description.
     */
    @Override
    public String toString() {
        return this.name() + " [key=" + this.key + ", description=" + this.description + "]";
    }

    /**
     * Retrieves all BDM object types as a read-only Map where the key is the technical key 
     * and the value is the description.
     *
     * @return A map containing all BDM object type data (Key -> Description).
     */
    public static Map<String, String> getAllData() {
        Map<String, String> objectTypeData = 
            Arrays.stream(values())
            .collect(Collectors.toMap(
                BDMObjectType::getKey, 
                BDMObjectType::getDescription, 
                (oldValue, newValue) -> oldValue, 
                LinkedHashMap::new 
            ));
        
        return Collections.unmodifiableMap(objectTypeData);
    }

    /**
     * Retrieves all technical keys as a read-only List of Strings.
     *
     * @return A list containing all technical keys.
     */
    public static List<String> getAllKeysList() {
        return Arrays.stream(values())
            .map(BDMObjectType::getKey)
            .collect(Collectors.toUnmodifiableList());
    }

    /**
     * Retrieves a BDMObjectType enum constant by its technical key.
     * This method is useful for deserializing or converting string keys back to enum constants.
     * <p>
     * Usage example:</p>
     * <pre>
     * {@code
     * BDMObjectType type = BDMObjectType.fromKey("PBProcess");
     * // Returns BDMObjectType.PB_PROCESS
     * }
     * </pre>
     * 
     *
     * @param key The technical key of the BDM object type (e.g., "PBProcess").
     * @return The corresponding BDMObjectType enum constant.
     * @throws IllegalArgumentException if no enum constant with the specified key is found.
     */
    public static BDMObjectType fromKey(String key) {
        for (BDMObjectType type : values()) {
            if (type.getKey().equalsIgnoreCase(key)) {
                return type;
            }
        }
        throw new IllegalArgumentException(
            String.format("No BDMObjectType found for key: '%s'. Valid keys are: %s",
                key,
                getAllKeysList())
        );
    }
}