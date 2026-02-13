package com.bonitasoft.processbuilder.enums;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Defines the names of the configurable process definitions.
 *
 * @author Bonitasoft
 * @since 1.0
 */

public enum ProcessNameType {

    /**
     * Process definition for Process Builder initialization.
     */
    PROCESS_BUILDER_INITIALIZATION("ProcessBuilderInitialization", "Process for initializing and configuring the Process Builder."),

    /**
     * Process definition for Objects Management.
     */
    OBJECTS_MANAGEMENT("ObjectsManagement", "Process for managing business objects and data entities."),

    /**
     * Process definition for Objects Management List operations.
     */
    OBJECTS_MANAGEMENT_LIST("ObjectsManagementList", "Process for managing lists of business objects."),

    /**
     * Process definition for Execution Connector.
     */
    EXECUTION_CONNECTOR("ExecutionConnector", "Process for connector execution and integration."),

    /**
     * Process definition for Form configuration.
     */
    FORM("Form", "Process for form configuration settings."),

    /**
     * Process definition for Notifications configuration.
     */
    NOTIFICATIONS("Notifications", "Process for notifications configuration settings."),

    /**
     * Process definition for Process Execution.
     */
    PROCESS_EXECUTION("ProcessExecution", "Process for executing and managing process instances."),

    /**
     * Process definition for Redirections configuration.
     */
    REDIRECTIONS("Redirections", "Process for redirection configuration settings."),

    /**
     * Process definition for REST API Connector.
     */
    REST_API_CONNECTOR("RestAPIConnector", "Process for REST API connector execution."),

    /**
     * Process definition for REST APIs configuration and execution.
     */
    REST_APIS("RestApis", "Process for executing configured REST API services."),

    /**
     * Process definition for SMTP Connector.
     */
    SMTP_CONNECTOR("SmtpConnector", "Process for SMTP email connector execution."),

    /**
     * Process definition for generic Process operations.
     */
    PROCESS("Process", "Process for generic process operations and management."),

    /**
     * Process definition for Service Connector Action Runner.
     */
    SERVICE_CONNECTOR_ACTION_RUNNER("ServiceConnectorActionRunner", "Process for executing service connector actions such as notifications, REST APIs, and other integrations."),

    /**
     * Process definition for the lifecycle manager that periodically restarts the master orchestrator.
     * Ensures continuous availability by opening a new instance, closing the previous one,
     * and cleaning up completed instances to avoid infinite-running processes.
     */
    MASTER_PROCESS_LIFECYCLE_MANAGER("MasterProcessLifecycleManager", "Process for managing the periodic restart of the master orchestrator, ensuring continuous availability without infinite-running instances."),

    /**
     * Process definition for the master execution orchestrator.
     * Executes master processes without spawning a new instance per execution by using a persistent
     * waiting message pattern that reactivates after each parallel branch execution.
     */
    MASTER_PROCESS_EXECUTION_ORCHESTRATOR("MasterProcessExecutionOrchestrator", "Process for orchestrating master process executions using a persistent message pattern for parallel branch dispatching."),

    /**
     * Process definition for the asynchronous timer synchronization job.
     * Updates timer event triggers for process instances whose cancellation delay has changed,
     * identified by their pbProcessId.
     */
    ASYNCHRONOUS_TIMER_SYNCHRONIZATION_JOB("AsynchronousTimerSynchronizationJob", "Process for asynchronously updating timer event triggers on instances affected by cancellation delay changes."),

    /**
     * Process definition for technical case cancellation.
     * Cancels both the BPA process instance and the corresponding PBProcess entry in the BDM.
     */
    TECHNICAL_CASE_CANCELLATION("TechnicalCaseCancellation", "Process for cancelling a BPA process instance and its associated PBProcess record in the Business Data Model."),

    /**
     * Process definition for testing REST API configurations.
     * Uploads test entries to PBConfiguration related to connector integrations such as REST APIs.
     */
    TEST_REST_API_CONFIGURATION("TestRestAPIConfiguration", "Process for uploading and validating REST API connector test configurations in PBConfiguration.");

    private final String key;
    private final String description;

    /**
     * Private constructor for the enumeration.
     * @param key The technical key used for mapping.
     * @param description A human-readable description of the type.
     */
    ProcessNameType(String key, String description) {
        this.key = key;
        this.description = description;
    }

    /**
     * Gets the human-readable, capitalized key of the process.
     *
     * @return The process key.
     */
    public String getKey() {
        return key;
    }

    /**
     * Gets a brief description of the process purpose.
     *
     * @return The process description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Checks if a given string corresponds to a valid enum constant, ignoring case and leading/trailing spaces.
     * @param input The string to validate.
     * @return {@code true} if the string is a valid enum constant, {@code false} otherwise.
     */
    public static boolean isValid(String input) {
        if (input == null || input.trim().isEmpty()) {
            return false;
        }
        try {
            ProcessNameType.valueOf(input.trim().toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
    
    /**
     * Retrieves all process instance states as a read-only Map where the key is the technical key 
     * and the value is the description.
     * @return A map containing all process state data (Key -> Description).
     */
    public static Map<String, String> getAllData() {
        Map<String, String> stateData = 
            Arrays.stream(values())
            .collect(Collectors.toMap(
                ProcessNameType::getKey, 
                ProcessNameType::getDescription, 
                (oldValue, newValue) -> oldValue, 
                LinkedHashMap::new 
            ));
        
        return Collections.unmodifiableMap(stateData);
    }
    
    /**
     * Retrieves all technical keys as a read-only List of Strings.
     * @return A list containing all technical keys.
     */
    public static List<String> getAllKeysList() {
        return Arrays.stream(values())
            .map(ProcessNameType::getKey)
            .collect(Collectors.toUnmodifiableList());
    }
}