package com.bonitasoft.processbuilder.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link ProcessNameType} enumeration, verifying
 * constant names and the {@code getAllData} utility method.
 *
 * @author Bonitasoft
 * @since 1.0
 */
class ProcessNameTypeTest {

    private static final int EXPECTED_ENUM_COUNT = 18;
    private static final Map<String, String> EXPECTED_DATA;

    static {
        EXPECTED_DATA = ProcessNameType.getAllData();
    }

    // =========================================================================
    // ENUM COUNT TESTS
    // =========================================================================

    @Test
    @DisplayName("Should contain exactly eighteen process name constants")
    void should_contain_eighteen_constants() {
        assertEquals(EXPECTED_ENUM_COUNT, ProcessNameType.values().length);
        assertEquals(EXPECTED_ENUM_COUNT, EXPECTED_DATA.size());
    }

    // =========================================================================
    // INDIVIDUAL CONSTANT TESTS
    // =========================================================================

    @Test
    @DisplayName("PROCESS_BUILDER_INITIALIZATION constant should have the correct key and description")
    void processBuilderInitialization_should_have_correct_values() {
        assertEquals("ProcessBuilderInitialization", ProcessNameType.PROCESS_BUILDER_INITIALIZATION.getKey());
        assertTrue(ProcessNameType.PROCESS_BUILDER_INITIALIZATION.getDescription().contains("Process Builder"));
    }

    @Test
    @DisplayName("OBJECTS_MANAGEMENT constant should have the correct key and description")
    void objectsManagement_should_have_correct_values() {
        assertEquals("ObjectsManagement", ProcessNameType.OBJECTS_MANAGEMENT.getKey());
        assertTrue(ProcessNameType.OBJECTS_MANAGEMENT.getDescription().contains("business objects"));
    }

    @Test
    @DisplayName("OBJECTS_MANAGEMENT_LIST constant should have the correct key and description")
    void objectsManagementList_should_have_correct_values() {
        assertEquals("ObjectsManagementList", ProcessNameType.OBJECTS_MANAGEMENT_LIST.getKey());
        assertTrue(ProcessNameType.OBJECTS_MANAGEMENT_LIST.getDescription().contains("lists of business objects"));
    }

    @Test
    @DisplayName("EXECUTION_CONNECTOR constant should have the correct key and description")
    void executionConnector_should_have_correct_values() {
        assertEquals("ExecutionConnector", ProcessNameType.EXECUTION_CONNECTOR.getKey());
        assertTrue(ProcessNameType.EXECUTION_CONNECTOR.getDescription().contains("connector execution"));
    }

    @Test
    @DisplayName("FORM constant should have the correct key and description")
    void form_should_have_correct_values() {
        assertEquals("Form", ProcessNameType.FORM.getKey());
        assertTrue(ProcessNameType.FORM.getDescription().startsWith("Process for form configuration"));
    }

    @Test
    @DisplayName("NOTIFICATIONS constant should have the correct key and description")
    void notifications_should_have_correct_values() {
        assertEquals("Notifications", ProcessNameType.NOTIFICATIONS.getKey());
        assertTrue(ProcessNameType.NOTIFICATIONS.getDescription().contains("notifications configuration settings."));
    }

    @Test
    @DisplayName("PROCESS_EXECUTION constant should have the correct key and description")
    void processExecution_should_have_correct_values() {
        assertEquals("ProcessExecution", ProcessNameType.PROCESS_EXECUTION.getKey());
        assertTrue(ProcessNameType.PROCESS_EXECUTION.getDescription().contains("process instances"));
    }

    @Test
    @DisplayName("REDIRECTIONS constant should have the correct key and description")
    void redirections_should_have_correct_values() {
        assertEquals("Redirections", ProcessNameType.REDIRECTIONS.getKey());
        assertTrue(ProcessNameType.REDIRECTIONS.getDescription().contains("redirection configuration"));
    }

    @Test
    @DisplayName("REST_API_CONNECTOR constant should have the correct key and description")
    void restApiConnector_should_have_correct_values() {
        assertEquals("RestAPIConnector", ProcessNameType.REST_API_CONNECTOR.getKey());
        assertTrue(ProcessNameType.REST_API_CONNECTOR.getDescription().contains("REST API connector"));
    }

    @Test
    @DisplayName("REST_APIS constant should have the correct key and description")
    void restApis_should_have_correct_values() {
        assertEquals("RestApis", ProcessNameType.REST_APIS.getKey());
        assertTrue(ProcessNameType.REST_APIS.getDescription().contains("REST API"));
    }

    @Test
    @DisplayName("SMTP_CONNECTOR constant should have the correct key and description")
    void smtpConnector_should_have_correct_values() {
        assertEquals("SmtpConnector", ProcessNameType.SMTP_CONNECTOR.getKey());
        assertTrue(ProcessNameType.SMTP_CONNECTOR.getDescription().contains("SMTP"));
    }

    @Test
    @DisplayName("PROCESS constant should have the correct key and description")
    void process_should_have_correct_values() {
        assertEquals("Process", ProcessNameType.PROCESS.getKey());
        assertTrue(ProcessNameType.PROCESS.getDescription().contains("generic process"));
    }

    @Test
    @DisplayName("SERVICE_CONNECTOR_ACTION_RUNNER constant should have the correct key and description")
    void serviceConnectorActionRunner_should_have_correct_values() {
        assertEquals("ServiceConnectorActionRunner", ProcessNameType.SERVICE_CONNECTOR_ACTION_RUNNER.getKey());
        assertTrue(ProcessNameType.SERVICE_CONNECTOR_ACTION_RUNNER.getDescription().contains("service connector actions"));
    }

    @Test
    @DisplayName("MASTER_PROCESS_LIFECYCLE_MANAGER constant should have the correct key and description")
    void masterProcessLifecycleManager_should_have_correct_values() {
        assertEquals("MasterProcessLifecycleManager", ProcessNameType.MASTER_PROCESS_LIFECYCLE_MANAGER.getKey());
        assertTrue(ProcessNameType.MASTER_PROCESS_LIFECYCLE_MANAGER.getDescription().contains("periodic restart"));
    }

    @Test
    @DisplayName("MASTER_PROCESS_EXECUTION_ORCHESTRATOR constant should have the correct key and description")
    void masterProcessExecutionOrchestrator_should_have_correct_values() {
        assertEquals("MasterProcessExecutionOrchestrator", ProcessNameType.MASTER_PROCESS_EXECUTION_ORCHESTRATOR.getKey());
        assertTrue(ProcessNameType.MASTER_PROCESS_EXECUTION_ORCHESTRATOR.getDescription().contains("master process executions"));
    }

    @Test
    @DisplayName("ASYNCHRONOUS_TIMER_SYNCHRONIZATION_JOB constant should have the correct key and description")
    void asynchronousTimerSynchronizationJob_should_have_correct_values() {
        assertEquals("AsynchronousTimerSynchronizationJob", ProcessNameType.ASYNCHRONOUS_TIMER_SYNCHRONIZATION_JOB.getKey());
        assertTrue(ProcessNameType.ASYNCHRONOUS_TIMER_SYNCHRONIZATION_JOB.getDescription().contains("timer event triggers"));
    }

    @Test
    @DisplayName("TECHNICAL_CASE_CANCELLATION constant should have the correct key and description")
    void technicalCaseCancellation_should_have_correct_values() {
        assertEquals("TechnicalCaseCancellation", ProcessNameType.TECHNICAL_CASE_CANCELLATION.getKey());
        assertTrue(ProcessNameType.TECHNICAL_CASE_CANCELLATION.getDescription().contains("BPA process instance"));
    }

    @Test
    @DisplayName("TEST_REST_API_CONFIGURATION constant should have the correct key and description")
    void testRestApiConfiguration_should_have_correct_values() {
        assertEquals("TestRestAPIConfiguration", ProcessNameType.TEST_REST_API_CONFIGURATION.getKey());
        assertTrue(ProcessNameType.TEST_REST_API_CONFIGURATION.getDescription().contains("REST API connector test"));
    }

    // =========================================================================
    // getAllData TESTS
    // =========================================================================

    @Test
    @DisplayName("getAllProcessData should return an unmodifiable map with all required keys")
    void getAllProcessData_should_return_correct_map() {
        assertTrue(EXPECTED_DATA.containsKey("ProcessBuilderInitialization"));
        assertTrue(EXPECTED_DATA.containsKey("ObjectsManagement"));
        assertTrue(EXPECTED_DATA.containsKey("ObjectsManagementList"));
        assertTrue(EXPECTED_DATA.containsKey("ExecutionConnector"));
        assertTrue(EXPECTED_DATA.containsKey("Form"));
        assertTrue(EXPECTED_DATA.containsKey("Notifications"));
        assertTrue(EXPECTED_DATA.containsKey("ProcessExecution"));
        assertTrue(EXPECTED_DATA.containsKey("Redirections"));
        assertTrue(EXPECTED_DATA.containsKey("RestAPIConnector"));
        assertTrue(EXPECTED_DATA.containsKey("RestApis"));
        assertTrue(EXPECTED_DATA.containsKey("SmtpConnector"));
        assertTrue(EXPECTED_DATA.containsKey("Process"));
        assertTrue(EXPECTED_DATA.containsKey("ServiceConnectorActionRunner"));
        assertTrue(EXPECTED_DATA.containsKey("MasterProcessLifecycleManager"));
        assertTrue(EXPECTED_DATA.containsKey("MasterProcessExecutionOrchestrator"));
        assertTrue(EXPECTED_DATA.containsKey("AsynchronousTimerSynchronizationJob"));
        assertTrue(EXPECTED_DATA.containsKey("TechnicalCaseCancellation"));
        assertTrue(EXPECTED_DATA.containsKey("TestRestAPIConfiguration"));

        assertThrows(UnsupportedOperationException.class, () -> EXPECTED_DATA.put("TestProcess", "Test"));
    }

    @Test
    @DisplayName("getAllData should return correct map size and be immutable")
    void getAllData_shouldReturnCorrectMap() {
        Map<String, String> data = ProcessNameType.getAllData();
        assertEquals(EXPECTED_ENUM_COUNT, data.size());
        assertTrue(data.containsKey("Form"));
        assertTrue(data.containsKey("RestApis"));
        assertTrue(data.containsKey("ProcessBuilderInitialization"));
        assertTrue(data.containsKey("SmtpConnector"));
        assertTrue(data.containsKey("ServiceConnectorActionRunner"));
        assertTrue(data.containsKey("MasterProcessLifecycleManager"));
        assertTrue(data.containsKey("MasterProcessExecutionOrchestrator"));
        assertTrue(data.containsKey("AsynchronousTimerSynchronizationJob"));
        assertTrue(data.containsKey("TechnicalCaseCancellation"));
        assertTrue(data.containsKey("TestRestAPIConfiguration"));
        assertThrows(UnsupportedOperationException.class, () -> data.clear());
    }

    // =========================================================================
    // getAllKeysList TESTS
    // =========================================================================

    @Test
    @DisplayName("getAllKeysList should return correct list size and be immutable")
    void getAllKeysList_shouldReturnCorrectList() {
        List<String> keys = ProcessNameType.getAllKeysList();
        assertEquals(EXPECTED_ENUM_COUNT, keys.size());
        assertTrue(keys.contains("Notifications"));
        assertTrue(keys.contains("RestApis"));
        assertTrue(keys.contains("ObjectsManagement"));
        assertTrue(keys.contains("Process"));
        assertTrue(keys.contains("ServiceConnectorActionRunner"));
        assertTrue(keys.contains("MasterProcessLifecycleManager"));
        assertTrue(keys.contains("MasterProcessExecutionOrchestrator"));
        assertTrue(keys.contains("AsynchronousTimerSynchronizationJob"));
        assertTrue(keys.contains("TechnicalCaseCancellation"));
        assertTrue(keys.contains("TestRestAPIConfiguration"));
        assertThrows(UnsupportedOperationException.class, () -> keys.add("NEW"));
    }

    @Test
    @DisplayName("getAllKeysList should contain all process keys in enum declaration order")
    void getAllKeysList_shouldContainAllKeysInOrder() {
        List<String> keys = ProcessNameType.getAllKeysList();

        assertEquals("ProcessBuilderInitialization", keys.get(0));
        assertEquals("ObjectsManagement", keys.get(1));
        assertEquals("ObjectsManagementList", keys.get(2));
        assertEquals("ExecutionConnector", keys.get(3));
        assertEquals("Form", keys.get(4));
        assertEquals("Notifications", keys.get(5));
        assertEquals("ProcessExecution", keys.get(6));
        assertEquals("Redirections", keys.get(7));
        assertEquals("RestAPIConnector", keys.get(8));
        assertEquals("RestApis", keys.get(9));
        assertEquals("SmtpConnector", keys.get(10));
        assertEquals("Process", keys.get(11));
        assertEquals("ServiceConnectorActionRunner", keys.get(12));
        assertEquals("MasterProcessLifecycleManager", keys.get(13));
        assertEquals("MasterProcessExecutionOrchestrator", keys.get(14));
        assertEquals("AsynchronousTimerSynchronizationJob", keys.get(15));
        assertEquals("TechnicalCaseCancellation", keys.get(16));
        assertEquals("TestRestAPIConfiguration", keys.get(17));
    }

    // =========================================================================
    // isValid TESTS
    // =========================================================================

    @Test
    @DisplayName("isValid should return true for all valid enum names")
    void isValid_shouldReturnTrueForValidNames() {
        assertTrue(ProcessNameType.isValid("PROCESS_BUILDER_INITIALIZATION"));
        assertTrue(ProcessNameType.isValid("OBJECTS_MANAGEMENT"));
        assertTrue(ProcessNameType.isValid("OBJECTS_MANAGEMENT_LIST"));
        assertTrue(ProcessNameType.isValid("EXECUTION_CONNECTOR"));
        assertTrue(ProcessNameType.isValid("FORM"));
        assertTrue(ProcessNameType.isValid("NOTIFICATIONS"));
        assertTrue(ProcessNameType.isValid("PROCESS_EXECUTION"));
        assertTrue(ProcessNameType.isValid("REDIRECTIONS"));
        assertTrue(ProcessNameType.isValid("REST_API_CONNECTOR"));
        assertTrue(ProcessNameType.isValid("REST_APIS"));
        assertTrue(ProcessNameType.isValid("SMTP_CONNECTOR"));
        assertTrue(ProcessNameType.isValid("PROCESS"));
        assertTrue(ProcessNameType.isValid("SERVICE_CONNECTOR_ACTION_RUNNER"));
        assertTrue(ProcessNameType.isValid("MASTER_PROCESS_LIFECYCLE_MANAGER"));
        assertTrue(ProcessNameType.isValid("MASTER_PROCESS_EXECUTION_ORCHESTRATOR"));
        assertTrue(ProcessNameType.isValid("ASYNCHRONOUS_TIMER_SYNCHRONIZATION_JOB"));
        assertTrue(ProcessNameType.isValid("TECHNICAL_CASE_CANCELLATION"));
        assertTrue(ProcessNameType.isValid("TEST_REST_API_CONFIGURATION"));
    }

    @Test
    @DisplayName("isValid should return true for lowercase enum names")
    void isValid_shouldReturnTrueForLowercaseNames() {
        assertTrue(ProcessNameType.isValid("process_builder_initialization"));
        assertTrue(ProcessNameType.isValid("form"));
        assertTrue(ProcessNameType.isValid("smtp_connector"));
        assertTrue(ProcessNameType.isValid("service_connector_action_runner"));
    }

    @Test
    @DisplayName("isValid should return true for names with surrounding whitespace")
    void isValid_shouldReturnTrueForNamesWithWhitespace() {
        assertTrue(ProcessNameType.isValid("  FORM  "));
        assertTrue(ProcessNameType.isValid("\tPROCESS\t"));
    }

    @Test
    @DisplayName("isValid should return false for null input")
    void isValid_shouldReturnFalseForNull() {
        assertFalse(ProcessNameType.isValid(null));
    }

    @Test
    @DisplayName("isValid should return false for empty string")
    void isValid_shouldReturnFalseForEmptyString() {
        assertFalse(ProcessNameType.isValid(""));
    }

    @Test
    @DisplayName("isValid should return false for whitespace only")
    void isValid_shouldReturnFalseForWhitespaceOnly() {
        assertFalse(ProcessNameType.isValid("   "));
        assertFalse(ProcessNameType.isValid("\t\n"));
    }

    @Test
    @DisplayName("isValid should return false for invalid enum names")
    void isValid_shouldReturnFalseForInvalidNames() {
        assertFalse(ProcessNameType.isValid("INVALID_TYPE"));
        assertFalse(ProcessNameType.isValid("random_value"));
        assertFalse(ProcessNameType.isValid("NOT_A_PROCESS_NAME"));
        assertFalse(ProcessNameType.isValid("SomeInvalidName"));
    }
}
